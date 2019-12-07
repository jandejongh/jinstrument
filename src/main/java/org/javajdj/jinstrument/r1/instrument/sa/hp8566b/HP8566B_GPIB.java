package org.javajdj.jinstrument.r1.instrument.sa.hp8566b;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.javajdj.jinstrument.r1.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.r1.instrument.InstrumentCommand;
import org.javajdj.jinstrument.r1.instrument.InstrumentSettings;
import org.javajdj.jinstrument.r1.instrument.sa.AbstractSpectrumAnalyzer;
import org.javajdj.jinstrument.r1.instrument.sa.DefaultSpectrumAnalyzerTrace;
import org.javajdj.jinstrument.r1.instrument.sa.DefaultSpectrumAnalyzerTraceSettings;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerDetector;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerDetector_Simple;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerTrace;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerTraceSettings;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzer;

/**
 *
 */
public class HP8566B_GPIB
extends AbstractSpectrumAnalyzer
implements SpectrumAnalyzer
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP8566B_GPIB.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP8566B_GPIB (final GpibDevice device)
  {
    super ("HP8566B", device, null, null);
    this.out = new PrintWriter (device.getOutputStream (), true);
  }

  private boolean poweredOn = false;
  
  @Override
  public boolean isPoweredOn ()
  {
    return this.poweredOn;
  }

  @Override
  public void setPoweredOn (final boolean poweredOn)
  {
    final GpibDevice device = (GpibDevice) getDevice ();
    try
    {
      device.lockDevice ();
      this.out.println (poweredOn ? "pstate on;" : "pstate off;");
    }
    catch (IOException | InterruptedException e)
    {
      LOG.log (Level.WARNING, "Caught exception!", e);
    }
    finally
    {
      device.unlockDevice ();
    }
    this.poweredOn = poweredOn;
  }
  
  private final PrintWriter out;
  
  private final boolean binaryTraceData = false; // XXX
  
  public final static DefaultSpectrumAnalyzerTraceSettings SAFE_SETTINGS = new DefaultSpectrumAnalyzerTraceSettings
    (1.0, 2.0, 1000.0, true, 1000.0, true, 10.0, true, 800, 0.0, 10.0, SpectrumAnalyzerDetector_Simple.ROSENFELL);
  
  private String readLine () throws IOException
  {
    // XXX THIS METHOD SHOULD REALLY NOT BE NEEDED!!
    String line = "";
    boolean done = false;
    while (! done)
    {
      final int intRead = ((GpibDevice) getDevice ()).getInputStream ().read ();
      done = (intRead == 10);
      if (! done)
        line += Character.toString ((char) intRead);
    }
    return line;
  }
  
  private double binaryTracePairToDouble (final byte ms, final byte ls)
  {
    final short mData = (short) (((ms & 0xff) << 8) | (ls & 0xff));
    return 0.01 * mData;
  }
  
  private synchronized void getTraceSettingsFromInstrument
  (final boolean autoResolutionBandwidth, final boolean autoVideoBandwidth, final boolean autoSweepTime)
  throws IOException
  {
    try
    {
      out.println ("sngls;");
//      out.println ("trdef tra?;");
//      final int traceLength = Integer.parseInt (readLine ());
      final int traceLength = 1001; // XXX
      this.out.println ("cf?;");
      final double centerFrequency_MHz = 1.0E-6 * Double.parseDouble (readLine ());
      this.out.println ("sp?;");
      final double span_MHz = 1.0E-6 * Double.parseDouble (readLine ());
      this.out.println ("rb?;");
      final double resolutionBandwidth_Hz = Double.parseDouble (readLine ());
      this.out.println ("vb?;");
      final double videoBandwidth_Hz = Double.parseDouble (readLine ());
      this.out.println ("st?;");
      final double sweepTime_s = Double.parseDouble (readLine ());
      this.out.println ("rl?;");
      final double referenceLevel_dBm = Double.parseDouble (readLine ());
      this.out.println ("at?;");
      final double attenuation_dB = Double.parseDouble (readLine ());
      this.out.println ("det?;");
      final String detectorString = readLine ().trim ().toLowerCase ();
      final SpectrumAnalyzerDetector detector;
      switch (detectorString)
      {
        case "smp":
          detector = SpectrumAnalyzerDetector_Simple.SAMPLE;
          break;
        case "pos":
          detector = SpectrumAnalyzerDetector_Simple.PEAK_MAX;
          break;
        case "neg":
          detector = SpectrumAnalyzerDetector_Simple.PEAK_MIN;
          break;
        case "nrm":
          detector = SpectrumAnalyzerDetector_Simple.ROSENFELL;
          break;
        default:
          throw new IOException ("Unrecognized detector type from HP-8566B!");
      }
      final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
        (centerFrequency_MHz, span_MHz,
         resolutionBandwidth_Hz, autoResolutionBandwidth,
         videoBandwidth_Hz, autoVideoBandwidth,
         sweepTime_s, autoSweepTime,
         traceLength,
         referenceLevel_dBm,
         attenuation_dB,
         detector);
      setCurrentTraceSettings (newSettings);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException (nfe);
    }
  }
  
  private synchronized void getTraceSettingsFromInstrument ()
  throws IOException
  {
    getTraceSettingsFromInstrument (false, false, false);
  }
  
  private synchronized void setTraceSettingsOnInstrument (final SpectrumAnalyzerTraceSettings settings)
  throws IOException, InterruptedException
  {
    this.out.println ("sngls;");
    if (this.binaryTraceData)
      this.out.println ("tdf b;");
    else
      this.out.println ("tdf p;");
    this.out.println ("trdef tra" + settings.getTraceLength () + ";");
    this.out.println ("cf " + settings.getCenterFrequency_MHz () + " MZ;");
    this.out.println ("sp " + settings.getSpan_MHz () + " MZ;");
    if (settings.isAutoResolutionBandwidth ())
      this.out.println ("cr;");
    else
      this.out.println ("rb " + settings.getResolutionBandwidth_Hz () + " HZ;");
    if (settings.isAutoVideoBandwidth ())
      this.out.println ("cv;");
    else
      this.out.println ("vb " + settings.getVideoBandwidth_Hz () + " HZ;");
    if (settings.isAutoSweepTime ())
      this.out.println ("ct;");
    else
      this.out.println ("st " + settings.getSweepTime_s () + " SC;");
    this.out.println ("rl " + settings.getReferenceLevel_dBm () + " DM;");
    this.out.println ("at " + settings.getAttenuation_dB () + " DB;");
    if (settings.getDetector () != null && (settings.getDetector () instanceof SpectrumAnalyzerDetector_Simple))
      switch ((SpectrumAnalyzerDetector_Simple) settings.getDetector ())
      {
        case UNKNOWN:
          break;
        case SAMPLE:
          this.out.println ("det smp;");
          break;
        case PEAK_MAX:
          this.out.println ("det pos;");
          break;
        case PEAK_MIN:
          this.out.println ("det neg;");
          break;
        case ROSENFELL:
          this.out.println ("det nrm;");
          break;
        default:
          break;
      }
    getTraceSettingsFromInstrument
      (settings.isAutoResolutionBandwidth (), settings.isAutoVideoBandwidth (), settings.isAutoSweepTime ());
  }
  
  @Override
  public synchronized SpectrumAnalyzerTrace getTrace ()
  throws IOException, InterruptedException
  {
    return getTrace (null);
  }
  
  @Override
  public synchronized SpectrumAnalyzerTrace getTrace (final SpectrumAnalyzerTraceSettings settings)
  throws IOException, InterruptedException
  {
    final GpibDevice device = (GpibDevice) getDevice ();
    try
    {
      device.lockDevice ();
      final SpectrumAnalyzerTraceSettings oldSettings = getCurrentTraceSettings ();
      if (settings == null && oldSettings == null)
        getTraceSettingsFromInstrument ();
      else if (settings != null && ! settings.equals (getCurrentTraceSettings ()))
        setTraceSettingsOnInstrument (settings);
      if (getCurrentTraceSettings ().getSweepTime_s () > 60)
      {
        if (JOptionPane.showConfirmDialog (null, "Sweep Time is " + getCurrentTraceSettings ().getSweepTime_s () + ", OK?")
          != JOptionPane.OK_OPTION)
          setTraceSettingsOnInstrument (oldSettings != null ? oldSettings : HP8566B_GPIB.SAFE_SETTINGS);
      }
      final int traceLength = getCurrentTraceSettings ().getTraceLength ();
      if (this.binaryTraceData)
        this.out.println ("tdf b;");
      else
        this.out.println ("tdf p;");
      this.out.println ("ts;");   // Take Sweep.
      this.out.println ("tra?;"); // Return Trace.
      final double samples[];
      if (this.binaryTraceData)
      {
        byte buf[] = new byte[2 * traceLength];
        int bytesRead = 0;
        while (bytesRead < buf.length)
          bytesRead += device.getInputStream ().read (buf, bytesRead, buf.length - bytesRead);
        samples = new double[traceLength];
        int i_buf = 0;
        for (int s = 0; s < samples.length; s++)
          samples[s] = binaryTracePairToDouble (buf[i_buf++], buf[i_buf++]);
      }
      else
      {
        final String traString = readLine ();
        // LOG.log (Level.INFO, "Read TRA String" + traString + ".");
        final String[] samplesString = traString.split (",");
        samples = new double[samplesString.length];
        for (int s = 0; s < samplesString.length; s++)
          samples[s] = Double.parseDouble (samplesString[s]);
      }
//      this.out.println ("stb?;"); // Status Byte.
//      final String stbString = readLine ();
      boolean error = false;
      String errorMessage = null;
      boolean uncal = false;
      boolean uncor = false;
//      try
//      {
//        final int stbInt = Integer.parseInt (stbString);
//        error = ((stbInt & 0x20) != 0);
//        if (error)
//        {
//          this.out.println ("err?");
//          errorMessage = readLine ();
//        }
//        if ((stbInt & 0x02) != 0)
//        {
//          // MESSAGE [UNCAL/UNCOR]
//          this.out.println ("msg?;"); // Error Message.
//          final String msgString = readLine ();
//          final String[] splitString = msgString.trim ().split (",");
//          if (splitString == null || splitString.length < 2)
//            error = true;
//          else
//          {
//            uncal = ! splitString[0].equalsIgnoreCase ("0");
//            uncor = ! splitString[1].equalsIgnoreCase ("0");
//            if (! (uncal || uncor))
//              error = true;
//          }
//        }
//        else
//        {
//          uncal = false;
//          uncor = false;
//        }
//      }
//      catch (NumberFormatException nfe)
//      {
//        error = true;
//      }
      return new DefaultSpectrumAnalyzerTrace (getCurrentTraceSettings ().clone (), samples, error, errorMessage, uncal, uncor);
    }
    catch (CloneNotSupportedException cnse)
    {
      throw new RuntimeException (cnse);
    }
    finally
    {
      device.unlockDevice ();
    }
  }
  
  @Override
  protected InstrumentSettings getSettingsFromInstrumentSync ()
    throws UnsupportedOperationException, IOException, InterruptedException
  {
    throw new UnsupportedOperationException ();
  }
  
  @Override
  protected void requestSettingsFromInstrumentASync ()
    throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  @Override
  protected void processCommand (final InstrumentCommand instrumentCommand)
  {
    throw new UnsupportedOperationException ();
  }
  
}
