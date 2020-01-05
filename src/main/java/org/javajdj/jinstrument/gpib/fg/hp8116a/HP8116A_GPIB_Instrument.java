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
package org.javajdj.jinstrument.gpib.fg.hp8116a;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.DefaultInstrumentCommand;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.FunctionGeneratorSettings;
import org.javajdj.jinstrument.gpib.fg.AbstractGpibFunctionGenerator;

/** Implementation of {@link Instrument} and {@link FunctionGenerator} for the HP-8116A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP8116A_GPIB_Instrument
  extends AbstractGpibFunctionGenerator
  implements FunctionGenerator
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP8116A_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP8116A_GPIB_Instrument (final GpibDevice device)
  {
    super ("HP-8116A", device, null, null, true, true, true, false);
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
      return "HP-8116A [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP8116A_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP8116A_GPIB_Instrument ((GpibDevice) device);
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
    return HP8116A_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
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
    final byte statusByte = serialPollSync ();
    return new HP8116A_GPIB_Status (statusByte);
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

  private final static String CST_COMMAND_STRING = "CST\r\n";
  
  @Override
  protected void requestSettingsFromInstrumentASync () throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  @Override
  public FunctionGeneratorSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String cstString = writeAndReadlnSync (HP8116A_GPIB_Instrument.CST_COMMAND_STRING);
    return HP8116A_GPIB_Settings.fromCstString (cstString);
  }
 
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  protected final InstrumentReading getReadingFromInstrumentSync () throws IOException, InterruptedException, TimeoutException
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
  // FunctionGenerator
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final boolean supportsOutputEnable ()
  {
    return true;
  }

  @Override
  public void setOutputEnable (final boolean outputEnable)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_OUTPUT_ENABLE,
      InstrumentCommand.ICARG_RF_OUTPUT_ENABLE,
      outputEnable));
  }
  
  private final static Waveform[] SUPPORTED_WAVEFORMS = new Waveform[]
  {
    Waveform.DC,
    Waveform.SINE,
    Waveform.TRIANGLE,
    Waveform.SQUARE,
    Waveform.PULSE
  };
  
  private final static List<Waveform> SUPPORTED_WAVEFORMS_AS_LIST
    = Collections.unmodifiableList (Arrays.asList (HP8116A_GPIB_Instrument.SUPPORTED_WAVEFORMS));
  
  @Override
  public final List<Waveform> getSupportedWaveforms ()
  {
    return HP8116A_GPIB_Instrument.SUPPORTED_WAVEFORMS_AS_LIST;
  }
  
  @Override
  public void setWaveform (final Waveform waveform)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_WAVEFORM,
      InstrumentCommand.ICARG_WAVEFORM, waveform));
  }

  @Override
  public final double getMinFrequency_Hz ()
  {
    return 0.001; // 1 mHz
  }

  @Override
  public final double getMaxFrequency_Hz ()
  {
    return 50e6; // 50 MHz
  }
  
  @Override
  public void setFrequency_Hz (final double frequency_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_LF_FREQUENCY,
      InstrumentCommand.ICARG_LF_FREQUENCY_HZ, frequency_Hz));
  }

  @Override
  public final double getMinAmplitude_Vpp ()
  {
    return 0.010;
  }

  @Override
  public final double getMaxAmplitude_Vpp ()
  {
    return 16;
  }

  @Override
  public final void setAmplitude_Vpp (final double amplitude_Vpp)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_AMPLITUDE,
      InstrumentCommand.ICARG_AMPLITUDE_VPP, amplitude_Vpp));
  }

  @Override
  public final boolean supportsDCOffset ()
  {
    return true;
  }

  @Override
  public final double getMinDCOffset_V ()
  {
    return -8;
  }

  @Override
  public final double getMaxDCOffset_V ()
  {
    return 8;
  }

  @Override
  public final void setDCOffset_V (final double dcOffset_V)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_DC_OFFSET,
      InstrumentCommand.ICARG_DC_OFFSET_V, dcOffset_V));
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
        case InstrumentCommand.IC_RF_OUTPUT_ENABLE:
        {
          final boolean outputEnable = (boolean) instrumentCommand.get (InstrumentCommand.ICARG_RF_OUTPUT_ENABLE);
          writeSync (outputEnable ? "D0\r\n" : "D1\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_WAVEFORM:
        {
          final Waveform waveform = (Waveform) instrumentCommand.get (InstrumentCommand.ICARG_WAVEFORM);
          switch (waveform)
          {
            case DC:
              writeSync ("W0\r\n");
              break;
            case SINE:
              writeSync ("W1\r\n");
              break;
            case TRIANGLE:
              writeSync ("W2\r\n");
              break;
            case SQUARE:
              writeSync ("W3\r\n");
              break;
            case PULSE:
              writeSync ("W4\r\n");
              break;
            default:
              throw new UnsupportedOperationException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_LF_FREQUENCY:
        {
          final double frequency_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_LF_FREQUENCY_HZ);
          writeSync ("FRQ " + frequency_Hz + " HZ\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_AMPLITUDE:
        {
          final double amplitude_Vpp = (double) instrumentCommand.get (InstrumentCommand.ICARG_AMPLITUDE_VPP);
          writeSync ("AMP " + amplitude_Vpp + " V\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_DC_OFFSET:
        {
          final double dcOffset_V = (double) instrumentCommand.get (InstrumentCommand.ICARG_DC_OFFSET_V);
          writeSync ("OFS " + dcOffset_V + " V\r\n");
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
