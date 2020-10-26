/*
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.gpib.sa.hp8566b;

import java.io.IOException;
import java.nio.charset.Charset;
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
import org.javajdj.jinstrument.Unit;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.controller.gpib.GpibControllerCommand;
import org.javajdj.jinstrument.gpib.sa.AbstractGpibSpectrumAnalyzer;
import org.javajdj.jinstrument.DefaultSpectrumAnalyzerSettings;
import org.javajdj.jinstrument.DefaultSpectrumAnalyzerTrace;
import org.javajdj.jinstrument.Resolution;
import org.javajdj.jinstrument.SpectrumAnalyzer;
import org.javajdj.jinstrument.SpectrumAnalyzerSettings;
import org.javajdj.jinstrument.controller.gpib.ReadlineTerminationMode;

/** Implementation of {@link Instrument} and {@link SpectrumAnalyzer} for the HP-8566B.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP8566B_GPIB_Instrument
  extends AbstractGpibSpectrumAnalyzer
  implements SpectrumAnalyzer
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP8566B_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP8566B_GPIB_Instrument (final GpibDevice device)
  {
    super ("HP-8663A", device, null, null, false, /* XXX */ false, true, true, true, false, false);
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
      return "HP-8566B [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP8566B_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP8566B_GPIB_Instrument ((GpibDevice) device);
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
    return HP8566B_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
  }

  @Override
  public String toString ()
  {
    return getInstrumentUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READ LINE TERMINATION MODE
  // READLINE TIMEOUT
  // READ AND WRITE METHODS TO THE DEVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static ReadlineTerminationMode READLINE_TERMINATION_MODE = ReadlineTerminationMode.OPTCR_LF;
  
  private GpibControllerCommand generateGetTraceCommandO3 ()
  {
    final GpibControllerCommand getTraceCommandO3 = getDevice ().generateWriteAndReadlnNCommand (
      "S2;TS;O3;TA;\n".getBytes (Charset.forName ("US-ASCII")),
      HP8566B_GPIB_Instrument.READLINE_TERMINATION_MODE,
      HP8566B_GPIB_Instrument.TRACE_LENGTH);
    return getTraceCommandO3;
  }
  
  private GpibControllerCommand generateGetSettingsCommandOL ()
  {
    final GpibControllerCommand getSettingsCommandOL = getDevice ().generateWriteAndReadNCommand (
      "OL;\n".getBytes (Charset.forName ("US-ASCII")),
      HP8566B_GPIB_Instrument.OL_LENGTH);
    return getSettingsCommandOL;
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
  // INSTRUMENT INITIALIZATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected final void initializeInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
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
  
  private final static int OL_LENGTH = 80;
  
  @Override
  protected void requestSettingsFromInstrumentASync () throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  @Override
  public SpectrumAnalyzerSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final GpibControllerCommand getSettingsCommand = generateGetSettingsCommandOL ();
    ((GpibDevice) getDevice ()).doControllerCommandSync (getSettingsCommand, getGetSettingsTimeout_ms ());
    final byte[] ol = (byte[]) getSettingsCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
    final SpectrumAnalyzerSettings settings = HP8566B_GPIB_Instrument.settingsFromOL (ol);
    // LOG message for debugging the OL output.
    // LOG.log (Level.WARNING, "bytes = {0}", Util.bytesToHex (ol));
    LOG.log (Level.INFO, "Settings received.");
    return settings;
  }
 
  private static SpectrumAnalyzerSettings settingsFromOL (final byte[] ol)
  {
    if (ol == null || ol.length != HP8566B_GPIB_Instrument.OL_LENGTH)
    {
      LOG.log (Level.WARNING, "Null OL object or unexpected number of bytes read for Settings.");
      throw new IllegalArgumentException ();
    }
    if (ol[0] != 0x1f || (ol[79] & 0xff) != 0xa2)
      throw new IllegalArgumentException ();
    //
    final double f_MHz = 0.0
      + 100000.0 * ((ol[3] & 0xf0) >> 4)
      + 10000.0  * (ol[3] & 0x0f)
      + 1000.0   * ((ol[4] & 0xf0) >> 4)
      + 100.0    * (ol[4] & 0x0f)
      + 10.0     * ((ol[5] & 0xf0) >> 4)
      + 1.0      * (ol[5] & 0x0f)
      + 0.1      * ((ol[6] & 0xf0) >> 4)
      + 0.01     * (ol[6] & 0x0f)
      + 0.001    * ((ol[7] & 0xf0) >> 4)
      + 0.0001   * (ol[7] & 0x0f)
      + 0.00001  * ((ol[8] & 0xf0) >> 4)
      + 0.000001 * (ol[8] & 0x0f)
      ;
    final double span_MHz = 0.0
      + 100000.0  * ((ol[11] & 0xf0) >> 4)
      + 10000.0   * (ol[11] & 0x0f)
      + 1000.0    * ((ol[12] & 0xf0) >> 4)
      + 100.0     * (ol[12] & 0x0f)
      + 10.0      * ((ol[13] & 0xf0) >> 4)
      + 1         * (ol[13] & 0x0f)
      + 0.1       * ((ol[14] & 0xf0) >> 4)
      + 0.01      * (ol[14] & 0x0f)
      + 0.001     * ((ol[15] & 0xf0) >> 4)
      + 0.0001    * (ol[15] & 0x0f)
      + 0.00001   * ((ol[16] & 0xf0) >> 4)
      + 0.000001  * (ol[16] & 0x0f)
      ;
    final double resolutionBandwidth_Hz;
    switch ((ol[26] & 0xf0) >> 4)
    {
      case  2: resolutionBandwidth_Hz =      10; break;
      case  3: resolutionBandwidth_Hz =      30; break;
      case  4: resolutionBandwidth_Hz =     100; break;
      case  5: resolutionBandwidth_Hz =     300; break;
      case  6: resolutionBandwidth_Hz =    1000; break;
      case  9: resolutionBandwidth_Hz =    3000; break;
      case 10: resolutionBandwidth_Hz =   10000; break;
      case 11: resolutionBandwidth_Hz =   30000; break;
      case 12: resolutionBandwidth_Hz =  100000; break;
      case 13: resolutionBandwidth_Hz =  300000; break;
      case 14: resolutionBandwidth_Hz = 1000000; break;
      case 15: resolutionBandwidth_Hz = 3000000; break;
      default:
        throw new UnsupportedOperationException ();
    }
    final boolean resolutionBandwidthCoupled = ((ol[18] & 0x40) == 0);
    final double videoBandwidth_Hz;
    switch (ol[26] & 0x0f)
    {
      case  0: videoBandwidth_Hz =       1; break;
      case  1: videoBandwidth_Hz =       3; break;
      case  2: videoBandwidth_Hz =      10; break;
      case  3: videoBandwidth_Hz =      30; break;
      case  4: videoBandwidth_Hz =     100; break;
      case  5: videoBandwidth_Hz =     300; break;
      case  6: videoBandwidth_Hz =    1000; break;
      case  9: videoBandwidth_Hz =    3000; break;
      case 10: videoBandwidth_Hz =   10000; break;
      case 11: videoBandwidth_Hz =   30000; break;
      case 12: videoBandwidth_Hz =  100000; break;
      case 13: videoBandwidth_Hz =  300000; break;
      case 14: videoBandwidth_Hz = 1000000; break;
      case 15: videoBandwidth_Hz = 3000000; break;
      default:
        throw new UnsupportedOperationException ();
    }
    final boolean videoBandwidthCoupled = ((ol[18] & 0x80) == 0);
    final double sweepTime_s = 0.0
      + 100000.0  * ((ol[65] & 0xf0) >> 4)
      + 10000.0   * (ol[65] & 0x0f)
      + 1000.0    * ((ol[66] & 0xf0) >> 4)
      + 100.0     * (ol[66] & 0x0f)
      + 10.0      * ((ol[67] & 0xf0) >> 4)
      + 1         * (ol[67] & 0x0f)
      + 0.1       * ((ol[68] & 0xf0) >> 4)
      + 0.01      * (ol[68] & 0x0f)
      + 0.001     * ((ol[69] & 0xf0) >> 4)
      + 0.0001    * (ol[69] & 0x0f)
      + 0.00001   * ((ol[70] & 0xf0) >> 4)
      + 0.000001  * (ol[70] & 0x0f)
      ;
    final boolean sweepTimeCoupled = ((ol[17] & 0x01) == 0);
    final int a = (ol[23] & 0xf0) >> 4;
    final int b = ol[23] & 0x0f;
    final int c = (ol[24] & 0xf0) >> 4;
    final int d = ol[24] & 0x0f;
    final double referenceLevel_dBm =
      10.0 * (10.0 * a + b - 64) + c + 0.1 * d;
    final double rfAttenuation_dB = (ol[22] & 0x07) * 10.0;
    final boolean rfAttenuationCoupled = ((ol[17] & 0x02) == 0);
    LOG.log (Level.WARNING, ""
      + "f_MHz={0}"
      + ", span_MHz={1}"
      + ", resBW_Hz={2}({3})"
      + ", vidBW={4}({5})"
      + ", st_s={6}({7})"
      + ", rl_dBm={8}"
      + ", att_dB={9}({10}).", new Object[]{
      f_MHz,
      span_MHz,
      resolutionBandwidth_Hz,
      resolutionBandwidthCoupled ? "C" : "U",
      videoBandwidth_Hz,
      videoBandwidthCoupled ? "C" : "U",
      sweepTime_s,
      sweepTimeCoupled ? "C" : "U",
      referenceLevel_dBm,
      rfAttenuation_dB,
      rfAttenuationCoupled ? "C" : "U",
      });
    return new DefaultSpectrumAnalyzerSettings (
      ol,
      Unit.UNIT_dBm,
      f_MHz,
      span_MHz,
      resolutionBandwidth_Hz,
      resolutionBandwidthCoupled,
      videoBandwidth_Hz,
      videoBandwidthCoupled,
      sweepTime_s,
      sweepTimeCoupled,
      referenceLevel_dBm,
      rfAttenuation_dB,
      rfAttenuationCoupled);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final static int TRACE_LENGTH = 1001;
  
  @Override
  protected final InstrumentReading getReadingFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final GpibControllerCommand getTraceCommand = generateGetTraceCommandO3 ();
    final GpibControllerCommand getSettingsCommand = generateGetSettingsCommandOL ();
    ((GpibDevice) getDevice ()).atomicSequenceSync (
      new GpibControllerCommand[] {getTraceCommand, getSettingsCommand},
      getGetReadingTimeout_ms ());
    final byte[][] bytes = (byte[][]) getTraceCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
    final String[] strings = new String[bytes.length];
    for (int s = 0; s < strings.length; s++)
      strings[s] = new String (bytes[s], Charset.forName ("US-ASCII"));
    final byte[] ol = (byte[]) getSettingsCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
    final SpectrumAnalyzerSettings settings = settingsFromOL (ol);
    settingsReadFromInstrument (settings);
    final InstrumentReading reading = HP8566B_GPIB_Instrument.traceFromO3 (strings, settings);
    // LOG.log (Level.WARNING, "Strings = {0}", strings);
    LOG.log (Level.INFO, "Trace received.");
    return reading;
  }

  @Override
  protected final void requestReadingFromInstrumentASync () throws IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  private static InstrumentReading traceFromO3 (final String[] strings, final SpectrumAnalyzerSettings settings)
  {
    if (strings == null || strings.length != HP8566B_GPIB_Instrument.TRACE_LENGTH)
    {
      LOG.log (Level.WARNING, "Null Strings object or unexpected number of Strings read for Trace.");
      throw new IllegalArgumentException ();
    }
    final double[] samples = new double[strings.length];
    for (int s = 0; s < samples.length; s++)
      samples[s] = Double.parseDouble (strings[s]);
    return new DefaultSpectrumAnalyzerTrace (
      settings,
      null,
      samples,
      Unit.UNIT_dBm, // XXX Always correct?
      Resolution.DIGITS_5, // XXX Educated guess: dynamic range > 100 dB, 0.01 dB resolution...
      true,
      null,
      false,
      true,  // XXX ???
      true); // XXX ???
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT HOUSEKEEPING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected final void instrumentHousekeeping ()
    throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractGpibInstrument
  // ON GPIB SERVICE REQUEST FROM INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected void onGpibServiceRequestFromInstrument ()
    throws IOException, InterruptedException, TimeoutException
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
        case InstrumentCommand.IC_RF_FREQUENCY:
        {
          final double centerFrequency_MHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_FREQUENCY_MHZ);
          writeSync ("CF " + centerFrequency_MHz + " MZ;\n");
          break;
        }
        case InstrumentCommand.IC_RF_SPAN:
        {
          final double span_MHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_SPAN_MHZ);
          writeSync ("SP " + span_MHz + " MZ;\n");
          break;
        }
        case InstrumentCommand.IC_RESOLUTION_BANDWIDTH:
        {
          final double resolutionBandwidth_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RESOLUTION_BANDWIDTH_HZ);
          writeSync ("RB " + resolutionBandwidth_Hz + " HZ;\n");
          break;
        }
        case InstrumentCommand.IC_SET_RESOLUTION_BANDWIDTH_COUPLED:
        {
          writeSync ("CR;\n");
          break;
        }
        case InstrumentCommand.IC_VIDEO_BANDWIDTH:
        {
          final double videoBandwidth_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_VIDEO_BANDWIDTH_HZ);
          writeSync ("VB " + videoBandwidth_Hz + " HZ;\n");
          break;
        }
        case InstrumentCommand.IC_SET_VIDEO_BANDWIDTH_COUPLED:
        {
          writeSync ("CV;\n");
          break;
        }
        case InstrumentCommand.IC_SWEEP_TIME:
        {
          final double sweepTime_s = (double) instrumentCommand.get (InstrumentCommand.ICARG_SWEEP_TIME_S);
          writeSync ("ST " + sweepTime_s + " SC;\n");
          break;
        }
        case InstrumentCommand.IC_SET_SWEEP_TIME_COUPLED:
        {
          writeSync ("CT;\n");
          break;
        }
        case InstrumentCommand.IC_REFERENCE_LEVEL:
        {
          final double referenceLevel_dBm = (double) instrumentCommand.get (InstrumentCommand.ICARG_REFERENCE_LEVEL_DBM);
          writeSync ("RL " + referenceLevel_dBm + " DM;\n");
          break;
        }
        case InstrumentCommand.IC_RF_ATTENUATION:
        {
          final double rfAttenuation_dB = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_ATTENUATION_DB);
          writeSync ("AT " + rfAttenuation_dB + " DB;\n");
          break;
        }
        case InstrumentCommand.IC_SET_RF_ATTENUATION_COUPLED:
        {
          writeSync ("CA;\n");
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
