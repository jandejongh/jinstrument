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
    super ("HP-8116A", device, null, null, false, true, true, true, false, false, false);
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
  // HP8116A_GPIB_Instrument
  // TRIGGER MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum HP8116ATriggerMode
  {
    M1_NORM,
    M2_TRIG,
    M3_GATE,
    M4_E_WID,
    M5_I_SWP, // Option001
    M6_E_SWP, // Option001
    M7_I_BUR, // Option001
    M8_E_BUR; // Option001
  }
  
  public void setTriggerMode (final HP8116ATriggerMode triggerMode) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_TRIGGER_MODE,
      InstrumentCommand.ICARG_TRIGGER_MODE, triggerMode));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // TRIGGER MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum HP8116ATriggerControl
  {
    T0_TRIGGER_OFF,
    T1_POSITIVE_TRIGGER_SLOPE,
    T2_NEGATIVE_TRIGGER_SLOPE;
  }
  
  public void setTriggerControl (final HP8116ATriggerControl triggerControl) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_TRIGGER_CONTROL,
      InstrumentCommand.ICARG_TRIGGER_CONTROL, triggerControl));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // CONTROL MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum HP8116AControlMode
  {
    CT0_OFF,
    CT1_FM,
    CT2_AM,
    CT3_PWM,
    CT4_VCO;
  }
  
  public void setControlMode (final HP8116AControlMode controlMode) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_CONTROL_MODE,
      InstrumentCommand.ICARG_CONTROL_MODE, controlMode));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // START PHASE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum HP8116AStartPhase
  {
    H0_NORMAL,
    H1_MINUS_90_DEGREES;
  }
  
  public void setStartPhase (final HP8116AStartPhase startPhase) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_START_PHASE,
      InstrumentCommand.ICARG_START_PHASE, startPhase));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // AUTO VERNIER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void setAutoVernier (final boolean autoVernier) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_AUTO_VERNIER,
      InstrumentCommand.ICARG_AUTO_VERNIER, autoVernier));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // OUTPUT LIMITS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void setOutputLimits (final boolean outputLimits) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_OUTPUT_LIMITS,
      InstrumentCommand.ICARG_OUTPUT_LIMITS, outputLimits));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // COMPLEMENTARY OUTPUT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void setComplementaryOutput (final boolean complementaryOutput) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_COMPLEMENTARY_OUTPUT,
      InstrumentCommand.ICARG_COMPLEMENTARY_OUTPUT, complementaryOutput));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // BURST LENGTH
  // [OPTION001]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public void setBurstLength (final int burstLength) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_BURST_LENGTH,
      InstrumentCommand.ICARG_BURST_LENGTH, burstLength));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // REPEAT INTERVAL
  // [OPTION001]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public void setRepeatInterval_s (final double repeatInterval_s) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_REPEAT_INTERVAL,
      InstrumentCommand.ICARG_REPEAT_INTERVAL_S, repeatInterval_s));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // START FREQUENCY
  // STOP FREQUENCY
  // [OPTION001]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public void setStartFrequency_Hz (final double startFrequency_Hz) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_START_FREQUENCY,
      InstrumentCommand.ICARG_START_FREQUENCY_HZ, startFrequency_Hz));
  }
  
  public void setStopFrequency_Hz (final double stopFrequency_Hz) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_STOP_FREQUENCY,
      InstrumentCommand.ICARG_STOP_FREQUENCY_HZ, stopFrequency_Hz));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // SWEEP TIME
  // [OPTION001]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void setSweepTime_s (final double sweepTime_s) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SWEEP_TIME,
      InstrumentCommand.ICARG_SWEEP_TIME_S, sweepTime_s));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // MARKER FREQUENCY
  // [OPTION001]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void setMarkerFrequency_Hz (final double markerFrequency_Hz) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_MARKER_FREQUENCY,
      InstrumentCommand.ICARG_MARKER_FREQUENCY_HZ, markerFrequency_Hz));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // DUTY CYCLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void setDutyCycle_percent (final double dutyCycle_percent) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_DUTY_CYCLE,
      InstrumentCommand.ICARG_DUTY_CYCLE_PERCENT, dutyCycle_percent));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP8116A_GPIB_Instrument
  // PULSE WIDTH
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void setPulseWidth_s (final double pulseWidth_s) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_PULSE_WIDTH,
      InstrumentCommand.ICARG_PULSE_WIDTH_S, pulseWidth_s));
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
            case DC:       writeSync ("W0\r\n"); break;
            case SINE:     writeSync ("W1\r\n"); break;
            case TRIANGLE: writeSync ("W2\r\n"); break;
            case SQUARE:   writeSync ("W3\r\n"); break;
            case PULSE:    writeSync ("W4\r\n"); break;
            default: throw new UnsupportedOperationException ();
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
        case InstrumentCommand.IC_TRIGGER_MODE:
        {
          final HP8116ATriggerMode triggerMode =
            (HP8116ATriggerMode) instrumentCommand.get (InstrumentCommand.ICARG_TRIGGER_MODE);
          switch (triggerMode)
          {
            case M1_NORM:  writeSync ("M1\r\n"); break;
            case M2_TRIG:  writeSync ("M2\r\n"); break;
            case M3_GATE:  writeSync ("M3\r\n"); break;
            case M4_E_WID: writeSync ("M4\r\n"); break;
            case M5_I_SWP: writeSync ("M5\r\n"); break;
            case M6_E_SWP: writeSync ("M6\r\n"); break;
            case M7_I_BUR: writeSync ("M7\r\n"); break;
            case M8_E_BUR: writeSync ("M8\r\n"); break;
            default: throw new RuntimeException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_TRIGGER_CONTROL:
        {
          final HP8116ATriggerControl triggerControl =
            (HP8116ATriggerControl) instrumentCommand.get (InstrumentCommand.ICARG_TRIGGER_CONTROL);
          switch (triggerControl)
          {
            case T0_TRIGGER_OFF:            writeSync ("T0\r\n"); break;
            case T1_POSITIVE_TRIGGER_SLOPE: writeSync ("T1\r\n"); break;
            case T2_NEGATIVE_TRIGGER_SLOPE: writeSync ("T2\r\n"); break;
            default: throw new RuntimeException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_CONTROL_MODE:
        {
          final HP8116AControlMode controlMode =
            (HP8116AControlMode) instrumentCommand.get (InstrumentCommand.ICARG_CONTROL_MODE);
          switch (controlMode)
          {
            case CT0_OFF: writeSync ("CT0\r\n"); break;
            case CT1_FM:  writeSync ("CT1\r\n"); break;
            case CT2_AM:  writeSync ("CT2\r\n"); break;
            case CT3_PWM: writeSync ("CT3\r\n"); break;
            case CT4_VCO: writeSync ("CT4\r\n"); break;
            default: throw new RuntimeException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;          
        }
        case InstrumentCommand.IC_START_PHASE:
        {
          final HP8116AStartPhase startPhase =
            (HP8116AStartPhase) instrumentCommand.get (InstrumentCommand.ICARG_START_PHASE);
          switch (startPhase)
          {
            case H0_NORMAL:           writeSync ("H0\r\n"); break;
            case H1_MINUS_90_DEGREES: writeSync ("H1\r\n"); break;
            default: throw new RuntimeException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;          
        }
        case InstrumentCommand.IC_AUTO_VERNIER:
        {
          final boolean autoVernier = (boolean) instrumentCommand.get (InstrumentCommand.ICARG_AUTO_VERNIER);
          writeSync (autoVernier ? "A1\r\n" : "A0\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_OUTPUT_LIMITS:
        {
          final boolean outputLimits = (boolean) instrumentCommand.get (InstrumentCommand.ICARG_OUTPUT_LIMITS);
          writeSync (outputLimits ? "L1\r\n" : "L0\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_COMPLEMENTARY_OUTPUT:
        {
          final boolean complementaryOutput = (boolean) instrumentCommand.get (InstrumentCommand.ICARG_COMPLEMENTARY_OUTPUT);
          writeSync (complementaryOutput ? "C1\r\n" : "C0\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_BURST_LENGTH:
        {
          final int burstLength = (int) instrumentCommand.get (InstrumentCommand.ICARG_BURST_LENGTH);
          writeSync ("BUR " + burstLength + " #\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_REPEAT_INTERVAL:
        {
          final double repeatInterval_s = (double) instrumentCommand.get (InstrumentCommand.ICARG_REPEAT_INTERVAL_S);          
          final double repeatInterval_ms = 1000 * repeatInterval_s;
          writeSync ("RPT " + repeatInterval_ms + " MS\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_START_FREQUENCY:
        {
          final double startFrequency_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_START_FREQUENCY_HZ);
          writeSync ("STA " + startFrequency_Hz + " HZ\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_STOP_FREQUENCY:
        {
          final double stopFrequency_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_STOP_FREQUENCY_HZ);
          writeSync ("STP " + stopFrequency_Hz + " HZ\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SWEEP_TIME:
        {
          final double sweepTime_s = (double) instrumentCommand.get (InstrumentCommand.ICARG_SWEEP_TIME_S);
          writeSync ("SWT " + sweepTime_s + " S\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_MARKER_FREQUENCY:
        {
          final double markerFrequency_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_MARKER_FREQUENCY_HZ);
          writeSync ("MRK " + markerFrequency_Hz + " HZ\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_DUTY_CYCLE:
        {
          final double dutyCycle_percent = (double) instrumentCommand.get (InstrumentCommand.ICARG_DUTY_CYCLE_PERCENT);          
          writeSync ("DTY " + dutyCycle_percent + " %\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_PULSE_WIDTH:
        {
          final double pulseWidth_s = (double) instrumentCommand.get (InstrumentCommand.ICARG_PULSE_WIDTH_S);          
          final double pulseWidth_ms = 1000 * pulseWidth_s;          
          writeSync ("WID " + pulseWidth_ms + " MS\r\n");
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
