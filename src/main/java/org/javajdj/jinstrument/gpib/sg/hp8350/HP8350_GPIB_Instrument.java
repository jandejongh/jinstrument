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
package org.javajdj.jinstrument.gpib.sg.hp8350;

import java.io.IOException;
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
import org.javajdj.jinstrument.gpib.sg.AbstractGpibSignalGenerator;
import org.javajdj.jinstrument.SignalGenerator;

/** Implementation of {@link Instrument} and {@link SignalGenerator} for the HP-8350.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP8350_GPIB_Instrument
extends AbstractGpibSignalGenerator
implements SignalGenerator
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP8350_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP8350_GPIB_Instrument (final GpibDevice device)
  {
    super ("HP-8350", device, null, null, true, true, true, false);
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
      return "HP-8350 [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP8350_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP8350_GPIB_Instrument ((GpibDevice) device);
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
    return HP8350_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
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
  public InstrumentSettings getSettingsFromInstrumentSync ()
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
  // SignalGenerator
  //
  // XXX SINCE THESE ARE SignalGenerator generic, they could move to AbstractGpibSignalGenerator??
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public void setOutputEnable (final boolean outputEnable)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_OUTPUT_ENABLE,
      InstrumentCommand.ICARG_RF_OUTPUT_ENABLE,
      outputEnable));
  }
  
  @Override
  public void setCenterFrequency_MHz (final double centerFrequency_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_FREQUENCY,
      InstrumentCommand.ICARG_RF_FREQUENCY_MHZ,
      centerFrequency_MHz));
  }
  
  @Override
  public void setSpan_MHz (final double span_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_SPAN,
      InstrumentCommand.ICARG_RF_SPAN_MHZ,
      span_MHz));
  }
  
  @Override
  public void setStartFrequency_MHz (final double startFrequency_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_START_FREQUENCY,
      InstrumentCommand.ICARG_RF_START_FREQUENCY_MHZ,
      startFrequency_MHz));
  }
  
  @Override
  public void setStopFrequency_MHz (final double stopFrequency_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_STOP_FREQUENCY,
      InstrumentCommand.ICARG_RF_STOP_FREQUENCY_MHZ,
      stopFrequency_MHz));
  }
  
  @Override
  public void setRfSweepMode (final RfSweepMode rfSweepMode)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_SWEEP_MODE,
      InstrumentCommand.ICARG_RF_SWEEP_MODE,
      rfSweepMode));
  }
  
  @Override
  public void setAmplitude_dBm (final double amplitude_dBm)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_OUTPUT_LEVEL,
      InstrumentCommand.ICARG_RF_OUTPUT_LEVEL_DBM,
      amplitude_dBm));
  }
  
  @Override
  public void setEnableAm (
    final boolean enableAm,
    final double depth_percent,
    final SignalGenerator.ModulationSource modulationSource)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_AM_CONTROL,
      InstrumentCommand.ICARG_RF_AM_ENABLE, enableAm,
      InstrumentCommand.ICARG_RF_AM_DEPTH_PERCENT, depth_percent,
      InstrumentCommand.ICARG_RF_AM_SOURCE, modulationSource));
  }

  @Override
  public void setEnableFm (
    final boolean enableFm,
    final double fmDeviation_kHz,
    SignalGenerator.ModulationSource modulationSource)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_FM_CONTROL,
      InstrumentCommand.ICARG_RF_FM_ENABLE, enableFm,
      InstrumentCommand.ICARG_RF_FM_DEVIATION_KHZ, fmDeviation_kHz,
      InstrumentCommand.ICARG_RF_FM_SOURCE, modulationSource));
  }
  
  @Override
  public void setEnablePm (
    final boolean enablePm,
    final double pmDeviation_degrees,
    SignalGenerator.ModulationSource modulationSource)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_PM_CONTROL,
      InstrumentCommand.ICARG_RF_PM_ENABLE, enablePm,
      InstrumentCommand.ICARG_RF_PM_DEVIATION_DEGREES, pmDeviation_degrees,
      InstrumentCommand.ICARG_RF_PM_SOURCE, modulationSource));
  }
  
  @Override
  public void setEnablePulseM (
    final boolean enablePulseM,
    SignalGenerator.ModulationSource modulationSource)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_PULSEM_CONTROL,
      InstrumentCommand.ICARG_RF_PULSEM_ENABLE, enablePulseM,
      InstrumentCommand.ICARG_RF_PULSEM_SOURCE, modulationSource));
  }
  
  @Override
  public void setEnableBpsk (
    final boolean enableBpsk,
    SignalGenerator.ModulationSource modulationSource)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_BPSK_CONTROL,
      InstrumentCommand.ICARG_RF_BPSK_ENABLE, enableBpsk,
      InstrumentCommand.ICARG_RF_BPSK_SOURCE, modulationSource));
  }
  
  @Override
  public void setModulationSourceInternalFrequency_kHz (final double modulationSourceInternalFrequency_kHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_INTMODSOURCE_CONTROL,
      InstrumentCommand.ICARG_INTMODSOURCE_FREQUENCY_KHZ, modulationSourceInternalFrequency_kHz));
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
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_OUTPUT_LEVEL:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_FREQUENCY:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_SWEEP_MODE:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_SPAN:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_START_FREQUENCY:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_STOP_FREQUENCY:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_AM_CONTROL:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_FM_CONTROL:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_PM_CONTROL:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_PULSEM_CONTROL:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RF_BPSK_CONTROL:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_INTMODSOURCE_CONTROL:
        {
          throw new UnsupportedOperationException ();
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
