/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.javajdj.jinstrument.gpib.sa.hp70000;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.gpib.sa.AbstractGpibSpectrumAnalyzer;
import org.javajdj.jinstrument.SpectrumAnalyzer;
import org.javajdj.jinstrument.SpectrumAnalyzerSettings;

/** Implementation of {@link Instrument} and {@link SpectrumAnalyzer} for the HP-70000 MMS.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP70000_GPIB_Instrument
  extends AbstractGpibSpectrumAnalyzer
  implements SpectrumAnalyzer
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP70000_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP70000_GPIB_Instrument (final GpibDevice device)
  {
    super ("HP-70000", device, null, null, /* XXX */ false, true, true, true);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static InstrumentType INSTRUMENT_TYPE = new InstrumentType ()
  {
    
    @Override
    public final String getInstrumentTypeUrl ()
    {
      return "HP-70000 [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP70000_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP70000_GPIB_Instrument ((GpibDevice) device);
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getInstrumentUrl ()
  {
    return HP70000_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
  }

  @Override
  public String toString ()
  {
    return getInstrumentUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // POWER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public boolean isPoweredOn ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public void setPoweredOn (final boolean poweredOn)
  {
    throw new UnsupportedOperationException ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT STATUS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  protected InstrumentStatus getStatusFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  protected void requestStatusFromInstrumentASync ()
    throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected void requestSettingsFromInstrumentASync () throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  @Override
  public SpectrumAnalyzerSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
  }
 
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  protected final InstrumentReading getReadingFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  protected final void requestReadingFromInstrumentASync () throws IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // PROCESS COMMAND
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  @Override
  protected void processCommand (final InstrumentCommand instrumentCommand)
    throws IOException, InterruptedException, TimeoutException
  {
    if (instrumentCommand == null)
      throw new IllegalArgumentException ();
    if (! instrumentCommand.containsKey (InstrumentCommand.IC_COMMAND_KEY))
      throw new IllegalArgumentException ();
    if (instrumentCommand.get (InstrumentCommand.IC_COMMAND_KEY) == null)
      throw new IllegalArgumentException ();
    if (! (instrumentCommand.get (InstrumentCommand.IC_COMMAND_KEY) instanceof String))
      throw new IllegalArgumentException ();
    final String commandString = (String) instrumentCommand.get (InstrumentCommand.IC_COMMAND_KEY);
    if (commandString.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    final GpibDevice device = (GpibDevice) getDevice ();
    InstrumentSettings newInstrumentSettings = null;
    try
    {
      switch (commandString)
      {
        case InstrumentCommand.IC_NOP_KEY:
          break;
        case InstrumentCommand.IC_GET_SETTINGS_KEY:
        {
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        default:
          throw new UnsupportedOperationException ();
      }
    }
    finally
    {
      // EMPTY
    }
    if (newInstrumentSettings != null)
      settingsReadFromInstrument (newInstrumentSettings);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LEGACY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
//  private final boolean binaryTraceData = true; // XXX
//  
//  public final static DefaultSpectrumAnalyzerTraceSettings SAFE_SETTINGS = new DefaultSpectrumAnalyzerTraceSettings
//    (1.0, 2.0, 1000.0, true, 1000.0, true, 10.0, true, 800, 0.0, 10.0, SpectrumAnalyzerDetector_Simple.ROSENFELL);
//  
//  private String readLine () throws IOException
//  {
//    // XXX THIS METHOD SHOULD REALLY NOT BE NEEDED!!
//    String line = "";
//    boolean done = false;
//    while (! done)
//    {
//      final int intRead = ((GpibDevice) getDevice ()).getInputStream ().read ();
//      done = (intRead == 10);
//      if (! done)
//        line += Character.toString ((char) intRead);
//    }
//    return line;
//  }
//  
//  private double binaryTracePairToDouble (final byte ms, final byte ls)
//  {
//    final short mData = (short) (((ms & 0xff) << 8) | (ls & 0xff));
//    return 0.01 * mData;
//  }
//  
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
//    return null;
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
//          setTraceSettingsOnInstrument (oldSettings != null ? oldSettings : HP70000_GPIB.SAFE_SETTINGS);
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
