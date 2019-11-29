package org.javajdj.jinstrument.r1.instrument.dso.hp54502a;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.r1.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.r1.instrument.dso.AbstractOscilloscope;
import org.javajdj.jinstrument.r1.instrument.dso.DefaultOscilloscopeDualChannelId_Letters;
import org.javajdj.jinstrument.r1.instrument.dso.DefaultOscilloscopeTrace;
import org.javajdj.jinstrument.r1.instrument.dso.DefaultOscilloscopeTraceSettings;
import org.javajdj.jinstrument.r1.instrument.dso.Oscilloscope;
import org.javajdj.jinstrument.r1.instrument.dso.OscilloscopeTrace;
import org.javajdj.jinstrument.r1.instrument.dso.OscilloscopeTraceSettings;

/**
 *
 */
public class HP54502A_GPIB
extends AbstractOscilloscope<DefaultOscilloscopeDualChannelId_Letters>
implements Oscilloscope<DefaultOscilloscopeDualChannelId_Letters>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP54502A_GPIB.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP54502A_GPIB (final GpibDevice device)
  {
    super (device);
    this.out = new PrintWriter (device.getOutputStream (), true);
  }

  @Override
  public final List<DefaultOscilloscopeDualChannelId_Letters> getChannelIds ()
  {
    return Arrays.asList (DefaultOscilloscopeDualChannelId_Letters.values ());
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
    // XXX
//    final GpibDevice device = (GpibDevice) getDevice ();
//    try
//    {
//      device.lockDevice ();
//      this.out.println (poweredOn ? "pstate on;" : "pstate off;");
//    }
//    catch (IOException | InterruptedException e)
//    {
//      LOG.log (Level.WARNING, "Caught exception!", e);
//    }
//    finally
//    {
//      device.unlockDevice ();
//    }
    this.poweredOn = poweredOn;
  }
  
  private final PrintWriter out;
  
//  private final boolean binaryTraceData = true; // XXX
  
//  public final static DefaultSpectrumAnalyzerTraceSettings SAFE_SETTINGS = new DefaultSpectrumAnalyzerTraceSettings
//    (1.0, 2.0, 1000.0, true, 1000.0, true, 10.0, true, 800, 0.0, 10.0, SpectrumAnalyzerDetector_Simple.ROSENFELL);
  
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
  
  private double binaryTracePairToDouble (final byte ms, final byte ls, final double y_inc, final double y_or, final double y_ref)
  {
    final short mData = (short) (((ms & 0xff) << 8) | (ls & 0xff));
    // return 0.01 * mData;
    // return 0.5e-4 /* yinc */ * (mData /* - 16384 /* yreference */ );
    return (mData - y_ref) * y_inc + y_or;
  }
  
//  private synchronized void getTraceSettingsFromInstrument
//  (final boolean autoResolutionBandwidth, final boolean autoVideoBandwidth, final boolean autoSweepTime)
//  throws IOException
//  {
//    try
//    {
//      out.println ("sngls;");
//      out.println ("trdef tra?;");
//      final int traceLength = Integer.parseInt (readLine ());
//      this.out.println ("cf?;");
//      final double centerFrequency_MHz = 1.0E-6 * Double.parseDouble (readLine ());
//      this.out.println ("sp?;");
//      final double span_MHz = 1.0E-6 * Double.parseDouble (readLine ());
//      this.out.println ("rb?;");
//      final double resolutionBandwidth_Hz = Double.parseDouble (readLine ());
//      this.out.println ("vb?;");
//      final double videoBandwidth_Hz = Double.parseDouble (readLine ());
//      this.out.println ("st?;");
//      final double sweepTime_s = Double.parseDouble (readLine ());
//      this.out.println ("rl?;");
//      final double referenceLevel_dBm = Double.parseDouble (readLine ());
//      this.out.println ("at?;");
//      final double attenuation_dB = Double.parseDouble (readLine ());
//      this.out.println ("det?;");
//      final String detectorString = readLine ().trim ().toLowerCase ();
//      final SpectrumAnalyzerDetector detector;
//      switch (detectorString)
//      {
//        case "gnd":
//          detector = SpectrumAnalyzerDetector_Simple.GROUND;
//          break;
//        case "smp":
//          detector = SpectrumAnalyzerDetector_Simple.SAMPLE;
//          break;
//        case "pos":
//          detector = SpectrumAnalyzerDetector_Simple.PEAK_MAX;
//          break;
//        case "neg":
//          detector = SpectrumAnalyzerDetector_Simple.PEAK_MIN;
//          break;
//        case "nrm":
//          detector = SpectrumAnalyzerDetector_Simple.ROSENFELL;
//          break;
//        default:
//          throw new IOException ("Unrecognized detector type from HP-7000!");
//      }
//      final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
//        (centerFrequency_MHz, span_MHz,
//         resolutionBandwidth_Hz, autoResolutionBandwidth,
//         videoBandwidth_Hz, autoVideoBandwidth,
//         sweepTime_s, autoSweepTime,
//         traceLength,
//         referenceLevel_dBm,
//         attenuation_dB,
//         detector);
//      setCurrentTraceSettings (newSettings);
//    }
//    catch (NumberFormatException nfe)
//    {
//      throw new IOException (nfe);
//    }
//  }
//  
//  private synchronized void getTraceSettingsFromInstrument ()
//  throws IOException
//  {
//    getTraceSettingsFromInstrument (false, false, false);
//  }
//  
//  private synchronized void setTraceSettingsOnInstrument (final SpectrumAnalyzerTraceSettings settings)
//  throws IOException, InterruptedException
//  {
//    this.out.println ("sngls;");
//    if (this.binaryTraceData)
//      this.out.println ("tdf b;");
//    else
//      this.out.println ("tdf p;");
//    this.out.println ("trdef tra" + settings.getTraceLength () + ";");
//    this.out.println ("cf " + settings.getCenterFrequency_MHz () + " MHZ;");
//    this.out.println ("sp " + settings.getSpan_MHz () + " MHZ;");
//    if (settings.isAutoResolutionBandwidth ())
//      this.out.println ("rb auto;");
//    else
//      this.out.println ("rb " + settings.getResolutionBandwidth_Hz () + " HZ;");
//    if (settings.isAutoVideoBandwidth ())
//      this.out.println ("vb auto;");
//    else
//      this.out.println ("vb " + settings.getVideoBandwidth_Hz () + " HZ;");
//    if (settings.isAutoSweepTime ())
//      this.out.println ("st auto;");
//    else
//      this.out.println ("st " + settings.getSweepTime_s () + " S;");
//    this.out.println ("rl " + settings.getReferenceLevel_dBm () + " DBM;");
//    this.out.println ("at " + settings.getAttenuation_dB () + " DB;");
//    if (settings.getDetector () != null && (settings.getDetector () instanceof SpectrumAnalyzerDetector_Simple))
//      switch ((SpectrumAnalyzerDetector_Simple) settings.getDetector ())
//      {
//        case UNKNOWN:
//          break;
//        case GROUND:
//          this.out.println ("det gnd;");
//          break;
//        case SAMPLE:
//          this.out.println ("det smp;");
//          break;
//        case PEAK_MAX:
//          this.out.println ("det pos;");
//          break;
//        case PEAK_MIN:
//          this.out.println ("det neg;");
//          break;
//        case ROSENFELL:
//          this.out.println ("det nrm;");
//          break;
//        default:
//          break;
//      }
//    getTraceSettingsFromInstrument
//      (settings.isAutoResolutionBandwidth (), settings.isAutoVideoBandwidth (), settings.isAutoSweepTime ());
//  }
  
//  @Override
//  public synchronized SpectrumAnalyzerTrace getTrace (final SpectrumAnalyzerTraceSettings settings)
//  throws IOException, InterruptedException
//  {
//    final GpibDevice device = (GpibDevice) getDevice ();
//    try
//    {
//      device.lockDevice ();
//      final SpectrumAnalyzerTraceSettings oldSettings = getCurrentTraceSettings ();
//      if (settings == null && oldSettings == null)
//        getTraceSettingsFromInstrument ();
//      else if (settings != null && ! settings.equals (getCurrentTraceSettings ()))
//        setTraceSettingsOnInstrument (settings);
//      if (getCurrentTraceSettings ().getSweepTime_s () > 60)
//      {
//        if (JOptionPane.showConfirmDialog (null, "Sweep Time is " + getCurrentTraceSettings ().getSweepTime_s () + ", OK?")
//          != JOptionPane.OK_OPTION)
//          setTraceSettingsOnInstrument (oldSettings != null ? oldSettings : HP54502A_GPIB.SAFE_SETTINGS);
//      }
//      final int traceLength = getCurrentTraceSettings ().getTraceLength ();
//      if (this.binaryTraceData)
//        this.out.println ("tdf b;");
//      else
//        this.out.println ("tdf p;");
//      this.out.println ("ts;");   // Take Sweep.
//      this.out.println ("tra?;"); // Return Trace.
//      final double samples[];
//      if (this.binaryTraceData)
//      {
//        byte buf[] = new byte[2 * traceLength];
//        int bytesRead = 0;
//        while (bytesRead < buf.length)
//          bytesRead += device.getInputStream ().read (buf, bytesRead, buf.length - bytesRead);
//        samples = new double[traceLength];
//        int i_buf = 0;
//        for (int s = 0; s < samples.length; s++)
//          samples[s] = binaryTracePairToDouble (buf[i_buf++], buf[i_buf++]);
//      }
//      else
//      {
//        final String traString = readLine ();
//        // LOG.log (Level.INFO, "Read TRA String" + traString + ".");
//        final String[] samplesString = traString.split (",");
//        samples = new double[samplesString.length];
//        for (int s = 0; s < samplesString.length; s++)
//          samples[s] = Double.parseDouble (samplesString[s]);
//      }
//      this.out.println ("stb?;"); // Status Byte.
//      final String stbString = readLine ();
//      boolean error = false;
//      String errorMessage = null;
//      boolean uncal = false;
//      boolean uncor = false;
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
//      return new DefaultSpectrumAnalyzerTrace (getCurrentTraceSettings ().clone (), samples, error, errorMessage, uncal, uncor);
//    }
//    catch (CloneNotSupportedException cnse)
//    {
//      throw new RuntimeException (cnse);
//    }
//    finally
//    {
//      device.unlockDevice ();
//    }
//  }
  
  @Override
  public synchronized OscilloscopeTrace getTrace
  (final DefaultOscilloscopeDualChannelId_Letters channel, final OscilloscopeTraceSettings settings)
  throws IOException, InterruptedException
  {
    if (channel == null /* XXX || settings == null */)
      return null;
    final Map<DefaultOscilloscopeDualChannelId_Letters, OscilloscopeTrace> traceMap
      = getTraceMap (Collections.singletonMap (channel, settings));
    if (traceMap == null)
      return null;
    else
      return traceMap.get (channel);
  }

  @Override
  public synchronized Map<DefaultOscilloscopeDualChannelId_Letters, OscilloscopeTrace> getTraceMap
  (final Map<DefaultOscilloscopeDualChannelId_Letters, OscilloscopeTraceSettings> settings)
  throws IOException, InterruptedException
  {
    if (settings == null)
      return null;
    final Map<DefaultOscilloscopeDualChannelId_Letters, OscilloscopeTrace> traces = new LinkedHashMap<> ();
    final GpibDevice device = (GpibDevice) getDevice ();
    try
    {
      device.lockDevice ();
      final List<String> channelNames = new ArrayList<> ();
      for (Map.Entry<DefaultOscilloscopeDualChannelId_Letters, OscilloscopeTraceSettings> settingEntry : settings.entrySet ())
      {
        final DefaultOscilloscopeDualChannelId_Letters channelId = settingEntry.getKey ();
        switch (channelId)
        {
          case CHANNEL_A:
            // Check/modify settings!
            // XXX
            channelNames.add ("chan1");
            break;
          case CHANNEL_B:
            // Check/modify settings!
            // XXX
            channelNames.add ("chan2");
            break;
          default:
            LOG.log (Level.WARNING, "Illegal channel: {0}!", channelId);
            break;
        }
      }
      if (! channelNames.isEmpty ())
      {
        String digString = ":dig ";
        boolean first = true;
        for (final String channelName : channelNames)
        {
          if (first)
            first = false;
          else
            digString += ",";
          digString += channelName;
        }
        // Acquire data.
        this.out.println (digString);
      }
      final Iterator<DefaultOscilloscopeDualChannelId_Letters> i_channelId = settings.keySet ().iterator ();
      final Iterator<String> i_channelName = channelNames.iterator ();
      while (i_channelId.hasNext ())
      {
        // Select source for HP-IB transfer.
        this.out.println (":wav:sour " + i_channelName.next ());
        final double traceData_V[] = getTraceData_V (device, false);
        traces.put (i_channelId.next (),
                    new DefaultOscilloscopeTrace (new DefaultOscilloscopeTraceSettings (traceData_V.length), traceData_V));        
      }
      return traces;
    }
    finally
    {
      device.unlockDevice ();
    }
  }

  private double[] getTraceData_V (final GpibDevice device, final boolean lockDevice) throws IOException, InterruptedException
  {
    if (device == null)
      throw new IllegalArgumentException ();
    try
    {
      if (lockDevice)
        device.lockDevice ();
      // Get Y-parameters for the trace.
      this.out.println (":wav:yinc?");
      final double y_inc = Double.parseDouble (readLine ());
      this.out.println (":wav:yor?");
      final double y_or = Double.parseDouble (readLine ());
      this.out.println (":wav:yref?");
      final double y_ref = Double.parseDouble (readLine ());
      // Set HP-IB Data Transfer Format (to WORD).
      this.out.println (":wav:form word");
      // Parse the number of points in the trace.
      this.out.println (":wav:poin?");
      final int traceLength = Integer.parseInt (readLine ());
      // XXX
      System.err.println ("Found trace length for oscilloscope: " + traceLength + ".");
      // Read the data; should start with '#8<DD..D>', i.e., 10 bytes.
      int bytesRead = 0;
      final byte preamble[] = new byte[10];
      this.out.println (":wav:data?");
      while (bytesRead < preamble.length)
        bytesRead += device.getInputStream ().read (preamble, bytesRead, preamble.length - bytesRead);
      // XXX Check preamble.
      System.err.println (Arrays.toString (preamble));
      bytesRead = 0;
      byte buf[] = new byte[2 * traceLength];
      while (bytesRead < buf.length)
        bytesRead += device.getInputStream ().read (buf, bytesRead, buf.length - bytesRead);
      readLine ();
      final double samples[] = new double[traceLength];
      int i_buf = 0;
      for (int s = 0; s < samples.length; s++)
        samples[s] = binaryTracePairToDouble (buf[i_buf++], buf[i_buf++], y_inc, y_or, y_ref);
      // Check errors.
      // XXX
      return samples;
    }
    // XXX
    //catch (NumberFormatException nfe)
    //{
    //  
    //}
    finally
    {
      if (lockDevice)
        device.unlockDevice ();
    }
  }
  
}
