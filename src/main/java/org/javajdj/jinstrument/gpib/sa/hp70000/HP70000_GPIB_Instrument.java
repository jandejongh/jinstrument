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
package org.javajdj.jinstrument.gpib.sa.hp70000;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultSpectrumAnalyzerTrace;
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
import org.javajdj.jinstrument.Unit;
import org.javajdj.jinstrument.controller.gpib.GpibControllerCommand;

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
    super ("HP-70000", device, null, null, false, true, true, true, false, false, true);
    setStatusCollectorPeriod_s (1.0);
    setSettingsCollectorPeriod_s (1.0);
    setGpibInstrumentServiceRequestCollectorPeriod_s (0.75);
    setPollServiceRequestTimeout_ms (200);
    setSerialPollTimeout_ms (200);
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
  // INSTRUMENT INITIALIZATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected void initializeInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    writeSync ("CONTS\r\n"); // Continuous Sweep.
    writeSync ("RQS 4\r\n"); // Generate GPIB Service Request at End of Sweep.
    writeSync ("TDF B\r\n"); // Trace Data Format (Binary).
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
    final byte statusByte = serialPollSync ();
    final String errorString = writeAndReadlnSync ("ERR?\r\n");
    // XXX Could read the message structure from "MSG\r\n"... It contains UNCAL/UNCOR indications.
    return HP70000_GPIB_Status.fromSerialPollStatusByteAndErrorString (statusByte, errorString);
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
    // XXX TODO: Picking up the state (< 1000 bytes) takes a lot of time with zero benefits (currently).
    final GpibControllerCommand statePreampleCommand = generateWriteAndReadNCommand ("STATE?\r\n", 14); // Fixed size!
    final GpibControllerCommand stateCommand = generateReadNCommand (1065); // N will be overwritten!
    final GpibControllerCommand[] atomicSequenceCommands = new GpibControllerCommand[]
    {
      // REMEMBER TO PICK UP THE VALUE BELOW!
      // NOTE: ONCE WE CAN DECODE THE STATE PROPERLY, WE CAN EXTRACT THE PARAMETERS DIRECTLY :-)...
      generateWriteAndReadlnCommand ("TRDEF TRA,?\r\n"),
      generateWriteAndReadlnCommand ("CF?\r\n"),
      generateWriteAndReadlnCommand ("SP?\r\n"),
      generateWriteAndReadlnCommand ("RB?\r\n"),
      generateWriteAndReadlnCommand ("VB?\r\n"),
      generateWriteAndReadlnCommand ("ST?\r\n"),
      generateWriteAndReadlnCommand ("RL?\r\n"),
      generateWriteAndReadlnCommand ("AT?\r\n"),
      generateWriteAndReadlnCommand ("DET?\r\n"),
      statePreampleCommand,
      generateUserRunnableCommand (() ->
      {
        // The last two bytes of the preamble encode the length of the rest of the state, excluding the terminating 0x0a.
        final byte[] statePreamble = (byte[]) statePreampleCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
        // Sanity check to prevent us from trying a lengthy block in vain.
        if (! new String (statePreamble, Charset.forName ("US-ASCII")).startsWith ("#A"))
          throw new IllegalArgumentException ();
        if (statePreamble[2] != 4)
          throw new IllegalArgumentException ();
        if (! new String (statePreamble, Charset.forName ("US-ASCII")).substring (3).startsWith ("2STATE"))
          throw new IllegalArgumentException ();
        final int length =
          (statePreamble[statePreamble.length - 2] & 0xff) * 256
          + (statePreamble[statePreamble.length - 1] & 0xff);
        stateCommand.put (GpibControllerCommand.CCARG_GPIB_READ_N, length + 1 /* MUST ADD ONE FOR 0x0A! */);
      }),
      stateCommand
    };
    atomicSequenceSync (atomicSequenceCommands);
    final byte[] state = (byte[]) stateCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
    int i = 0;
    final int traceLength = Integer.parseInt (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double centerFrequency_MHz = 1e-6 * Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double span_MHz = 1e-6 * Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double resolutionBandwidth_Hz = Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double videoBandwidth_Hz = Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double sweepTime_s = Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double referenceLevel_dBm = Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double rfAttenuation_dB = Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final String detString = 
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ();
    return new HP70000_GPIB_Settings (
      state,
      Unit.UNIT_dBm, // XXX This needs proper checking; may be in LIN or zero-span...
      centerFrequency_MHz,
      span_MHz,
      resolutionBandwidth_Hz,
      false,
      videoBandwidth_Hz,
      false,
      sweepTime_s,
      false,
      referenceLevel_dBm,
      rfAttenuation_dB,
      false,
      traceLength);
  }
 
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private double binaryTracePairToDouble (final byte ms, final byte ls)
  {
    final short mData = (short) (((ms & 0xff) << 8) | (ls & 0xff));
    return 0.01 * mData;
  }
  
  @Override
  protected final InstrumentReading getReadingFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final HP70000_GPIB_Settings settings = (HP70000_GPIB_Settings) getSettingsFromInstrumentSync ();
    settingsReadFromInstrument (settings);
    final int traceLength = settings.getTraceLength ();
    // In the approach below we initiate the trace and await its completion and transfer.
    // This fails with high sweep-time settings, say a few seconds and higher.
    // TDF B: Two bytes per trace point.
    // final byte[] binaryTraceBytes = writeAndReadNSync ("SNGLS;TDF B;TS;TRA?;\r\n", 2 * traceLength);
    // In the other approach, we directly read Trace A, assuming we are invoked from a Service Request.
    final byte[] binaryTraceBytes = writeAndReadNSync ("TRA?\r\n", 2 * traceLength);
    final double samples[] = new double[traceLength];
    int i_buf = 0;
    for (int s = 0; s < samples.length; s++)
      samples[s] = binaryTracePairToDouble (binaryTraceBytes[i_buf++], binaryTraceBytes[i_buf++]);
    return new DefaultSpectrumAnalyzerTrace (
      settings,
      samples,
      false, // XXX
      null,
      false,
      false);
  }

  @Override
  protected final void requestReadingFromInstrumentASync () throws IOException
  {
    throw new UnsupportedOperationException ();
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
    final InstrumentReading reading = getReadingFromInstrumentSync ();
    if (reading != null)
      readingReadFromInstrument (reading);
    else
      LOG.log (Level.WARNING, "Null Reading on {0}!", this.toString ());
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
          writeSync ("CF " + centerFrequency_MHz + " MZ\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_RF_SPAN:
        {
          final double span_MHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_SPAN_MHZ);
          writeSync ("SP " + span_MHz + " MZ\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_RESOLUTION_BANDWIDTH:
        {
          final double resolutionBandwidth_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RESOLUTION_BANDWIDTH_HZ);
          writeSync ("RB " + resolutionBandwidth_Hz + " HZ\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SET_RESOLUTION_BANDWIDTH_COUPLED:
        {
          writeSync ("RB AUTO\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_VIDEO_BANDWIDTH:
        {
          final double videoBandwidth_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_VIDEO_BANDWIDTH_HZ);
          writeSync ("VB " + videoBandwidth_Hz + " HZ\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SET_VIDEO_BANDWIDTH_COUPLED:
        {
          writeSync ("VB AUTO\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SWEEP_TIME:
        {
          final double sweepTime_s = (double) instrumentCommand.get (InstrumentCommand.ICARG_SWEEP_TIME_S);
          writeSync ("ST " + sweepTime_s + " SC\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SET_SWEEP_TIME_COUPLED:
        {
          writeSync ("ST AUTO\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_REFERENCE_LEVEL:
        {
          final double referenceLevel_dBm = (double) instrumentCommand.get (InstrumentCommand.ICARG_REFERENCE_LEVEL_DBM);
          writeSync ("RL " + referenceLevel_dBm + " DM\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_RF_ATTENUATION:
        {
          final double rfAttenuation_dB = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_ATTENUATION_DB);
          writeSync ("AT " + rfAttenuation_dB + " DB\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SET_RF_ATTENUATION_COUPLED:
        {
          writeSync ("AT AUTO\r\n");
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
