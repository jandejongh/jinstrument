package org.javajdj.jinstrument.r1.instrument.sg.hp8663a;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.r1.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.r1.instrument.sg.AbstractSignalGenerator;
import org.javajdj.jinstrument.r1.instrument.sg.DefaultSignalGeneratorSettings;
import org.javajdj.jinstrument.r1.instrument.sg.SignalGenerator;
import org.javajdj.jinstrument.r1.instrument.sg.SignalGeneratorSettings;

/**
 *
 */
public class HP8663A_GPIB
extends AbstractSignalGenerator
implements SignalGenerator
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP8663A_GPIB.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP8663A_GPIB (final GpibDevice device)
  {
    super ("HP8663A", device, null, null);
    this.out = new PrintWriter (device.getOutputStream (), true);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // OUT PRINTWRITER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final PrintWriter out;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // POWER ON
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public boolean isPoweredOn ()
  {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  @Override
  public void setPoweredOn (boolean poweredOn)
  {
    throw new UnsupportedOperationException ("Not supported yet.");
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READ LINE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
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
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
}
  private static final int L1_LENGTH = 189;
  
  private transient byte[] lastL1 = null;
  
  private SignalGeneratorSettings getSettingsFromInstrumentNoLock (GpibDevice gpibDevice)
    throws IOException, InterruptedException
  {
    final byte[] l1Buffer = new byte[HP8663A_GPIB.L1_LENGTH];
    this.out.println ("L1");
    final int bytesRead = gpibDevice.getInputStream ().read (l1Buffer);
    // LOG.log (Level.WARNING, "number of bytes read = {0}", bytesRead);
    LOG.log (Level.WARNING, "bytes = {0}", bytesToHex (l1Buffer));
    if (this.lastL1 != null)
      for (int i = 0; i < HP8663A_GPIB.L1_LENGTH; i++)
        if (l1Buffer[i] != this.lastL1[i])
          LOG.log (Level.WARNING, "L1 string index {0} changed from {1} to {2}.",
            new Object[]{i, Integer.toHexString (this.lastL1[i] & 0xff), Integer.toHexString (l1Buffer[i] & 0xff)});
    this.lastL1 = l1Buffer;
    return HP8663A_GPIB.settingsFromL1 (l1Buffer);
  }
  
  @Override
  public synchronized SignalGeneratorSettings getSettingsFromInstrument ()
    throws IOException, InterruptedException
  {
    final GpibDevice device = (GpibDevice) getDevice ();
    try
    {
      device.lockDevice ();
//      this.out.println ("FR 500 MZ");
//      Thread.sleep (100L);
//      if (this.bSwitch)
//        this.out.println ("AP -50 DM");
//      else
//        this.out.println ("AP -30 DM");
//      Thread.sleep (100L);
      return getSettingsFromInstrumentNoLock (device);
    }
//    catch (Exception e)
//    {
//      LOG.log (Level.WARNING, "Caught Exception while reading L1: {0}", e.getStackTrace ());
//      return new DefaultSignalGeneratorSettings (100.0 /* XXX DUMMY */, -30.0);            
//    }
    finally
    {
      device.unlockDevice ();
    }
  }

 
  private static SignalGeneratorSettings settingsFromL1 (final byte[] l1)
  {
    if (l1 == null || l1.length != L1_LENGTH)
      throw new IllegalArgumentException ();
    if (l1[0] != 0x40 || l1[1] != 0x41)
      throw new IllegalArgumentException ();
    //
    final double f_MHz = 0.0
      + 1000.0    * (l1[7] & 0x0f)
      + 100.0     * ((l1[6] & 0xf0) >> 4)
      + 10.0      * (l1[6] & 0x0f)
      + 1.0       * ((l1[5] & 0xf0) >> 4)
      + 0.1       * (l1[5] & 0x0f)
      + 0.01      * ((l1[4] & 0xf0) >> 4)
      + 0.001     * (l1[4] & 0x0f)
      + 0.0001    * ((l1[3] & 0xf0) >> 4)
      + 0.00001   * (l1[3] & 0x0f)
      + 0.000001  * ((l1[2] & 0xf0) >> 4)
      + 0.0000001 * (l1[2] & 0x0f)
      ;
    final double S_dBm = (0
      + 100.0 * (l1[17] & 0x0f)
      + 10.0  * ((l1[16] & 0xf0) >> 4)
      + 1.0   * (l1[16] & 0x0f)
      + 0.1   * ((l1[15] & 0xf0) >> 4)
      + 0.01  * (l1[15] & 0x0f)
      ) * (1 - 2 * ((l1[17] & 0x80) >> 7));
    final boolean outputEnable;
    // Guess work below... Candidates are 21 and 81.
    // On 21, the change is from 0xfe (on) to 0xfb (off)...
    // On 81, the change is from 0x02 (on) to 0x0a (off)...
    outputEnable = (l1[21] & 0x04) != 0;
    return new DefaultSignalGeneratorSettings (f_MHz, S_dBm, outputEnable); 
  }
  
  @Override
  public synchronized void setSettingsOnInstrument (final SignalGeneratorSettings signalGeneratorSettings)
  {
    throw new UnsupportedOperationException ("Not supported yet.");
  }
  
  @Override
  public synchronized void setCenterFrequency_MHz (final double centerFrequency_MHz)
    throws IOException, InterruptedException
  {
    final GpibDevice device = (GpibDevice) getDevice ();
    try
    {
      device.lockDevice ();
      this.out.println ("FR " + centerFrequency_MHz + " MZ");
      getSettingsFromInstrumentNoLock (device);
    }
    finally
    {
      device.unlockDevice ();
    }
  }
  
  @Override
  public synchronized void setAmplitude_dBm (final double amplitude_dBm)
    throws IOException, InterruptedException
  {
    final GpibDevice device = (GpibDevice) getDevice ();
    try
    {
      device.lockDevice ();
      this.out.println ("AP " + amplitude_dBm + " DM");
      getSettingsFromInstrumentNoLock (device);
    }
    finally
    {
      device.unlockDevice ();
    }
  }
  
  @Override
  public synchronized void setOutputEnable (final boolean outputEnable)
    throws IOException, InterruptedException
  {
    final GpibDevice device = (GpibDevice) getDevice ();
    try
    {
      device.lockDevice ();
      if (outputEnable)
        this.out.println ("AP -60 DM");
      else
        this.out.println ("AO");
      getSettingsFromInstrumentNoLock (device);
    }
    finally
    {
      device.unlockDevice ();
    }    
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
//  
//  @Override
//  public synchronized SpectrumAnalyzerTrace getTrace ()
//  throws IOException, InterruptedException
//  {
//    return getTrace (null);
//  }
//  
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
//          setTraceSettingsOnInstrument (oldSettings != null ? oldSettings : HP8663A_GPIB.SAFE_SETTINGS);
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

}
