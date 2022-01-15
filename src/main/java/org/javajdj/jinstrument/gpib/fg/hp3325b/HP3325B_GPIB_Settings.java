/*
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.gpib.fg.hp3325b;

import java.util.EnumSet;
import java.util.Objects;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultFunctionGeneratorSettings;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.FunctionGeneratorSettings;
import org.javajdj.jinstrument.InstrumentSettings;

/** Implementation of {@link InstrumentSettings} for the HP-3325B.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class HP3325B_GPIB_Settings
  extends DefaultFunctionGeneratorSettings
  implements FunctionGeneratorSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP3325B_GPIB_Settings.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP3325B_GPIB_Settings (
    final FunctionGenerator.Waveform waveform,
    final double frequency_Hz,
    final double amplitude_Vpp,
    final double dcOffset_V,
    final boolean rs232Echo,
    final boolean enhancements,
    final int errorCode,
    final EnumSet<ServiceRequestEnableMaskBit> serviceRequestEnableMask,
    final boolean externalReferenceLocked,
    final boolean useResponseHeader,
    final boolean highVoltageOutput,
    final String id,
    final boolean amplitudeModulation,
    final DataTransferMode dataTransferMode,
    final double markerFrequency_Hz,
    final double modulationSourceAmplitude_Vpp,
    final double modulationSourceFrequency_Hz,
    final ModulationSourceWaveformFunction modulationSourceWaveformFunction,
    final boolean phaseModulation,
    final EnumSet<Option> optionsInstalled,
    final double phase_degrees,
    final byte statusByteRs232,
    final RFOutputMode rfOutputMode,
    final SweepMode sweepMode,
    final double sweepStopFrequency_Hz,
    final double sweepStartFrequency_Hz,
    final double sweepTime_s)
  {
    super (null, true, waveform, frequency_Hz, amplitude_Vpp, dcOffset_V);
    this.rs232Echo = rs232Echo;
    this.enhancements = enhancements;
    this.errorCode = errorCode;
    this.serviceRequestEnableMask = serviceRequestEnableMask;
    this.externalReferenceLocked = externalReferenceLocked;
    this.useResponseHeader = useResponseHeader;
    this.highVoltageOutput = highVoltageOutput;
    this.id = id;
    this.amplitudeModulation = amplitudeModulation;
    this.dataTransferMode = dataTransferMode;
    this.markerFrequency_Hz = markerFrequency_Hz;
    this.modulationSourceAmplitude_Vpp = modulationSourceAmplitude_Vpp;
    this.modulationSourceFrequency_Hz = modulationSourceFrequency_Hz;
    this.modulationSourceWaveformFunction = modulationSourceWaveformFunction;
    this.phaseModulation = phaseModulation;
    this.optionsInstalled = optionsInstalled;
    this.phase_degrees = phase_degrees;
    this.statusByteRs232 = statusByteRs232;
    this.rfOutputMode = rfOutputMode;
    this.sweepMode = sweepMode;
    this.sweepStopFrequency_Hz = sweepStopFrequency_Hz;
    this.sweepStartFrequency_Hz = sweepStartFrequency_Hz;
    this.sweepTime_s = sweepTime_s;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AMPLITUDE CALIBRATION MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum AmpltitudeCalibrationMode
  {
    BEST,
    FAST;
  }
  
  // XXX There seems to be no way to interrogate CALM.
  // Fix at power-on default.
  private final AmpltitudeCalibrationMode ampltitudeCalibrationMode =
    AmpltitudeCalibrationMode.BEST;
  
  public final AmpltitudeCalibrationMode getAmplitudeCalibrationMode ()
  {
    return this.ampltitudeCalibrationMode;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RS-232 ECHO
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean rs232Echo;

  public final boolean isRs232Echo ()
  {
    return this.rs232Echo;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ENHANCEMENTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean enhancements;

  public final boolean isEnhancements ()
  {
    return this.enhancements;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ERROR CODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final int errorCode;
  
  public final int getErrorCode ()
  {
    return this.errorCode;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE REQUEST ENABLE MASK
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum ServiceRequestEnableMaskBit
  {
    
    ERR (1),
    STOP (2),
    START (4),
    FAIL (8);

    private final int value;
    
    private ServiceRequestEnableMaskBit (final int value)
    {
      this.value = value;
    }
    
    public final int getValue ()
    {
      return this.value;
    }
    
  }
  
  private final EnumSet<ServiceRequestEnableMaskBit> serviceRequestEnableMask;

  public final EnumSet<ServiceRequestEnableMaskBit> getServiceRequestEnableMask ()
  {
    return this.serviceRequestEnableMask;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EXTERNAL REFERENCE LOCKED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean externalReferenceLocked;
  
  public boolean isExternalReferenceLocked ()
  {
    return externalReferenceLocked;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // USE RESPONSE HEADER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean useResponseHeader;

  public final boolean isUseResponseHeader ()
  {
    return this.useResponseHeader;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HIGH VOLTAGE OUTPUT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean highVoltageOutput;

  public final boolean isHighVoltageOutput ()
  {
    return this.highVoltageOutput;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ID
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final String id;
  
  public final String getId ()
  {
    return this.id;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AMPLITUDE MODULATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean amplitudeModulation;

  public final boolean isAmplitudeModulation ()
  {
    return this.amplitudeModulation;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DATA TRANSFER MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum DataTransferMode
  {
    Mode1,
    Mode2;
  }
  
  private final DataTransferMode dataTransferMode;

  public final DataTransferMode getDataTransferMode ()
  {
    return this.dataTransferMode;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MARKER FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double markerFrequency_Hz;

  public final double getMarkerFrequency_Hz ()
  {
    return this.markerFrequency_Hz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MODULATION SOURCE AMPLITUDE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double modulationSourceAmplitude_Vpp;

  public final double getModulationSourceAmplitude_Vpp ()
  {
    return this.modulationSourceAmplitude_Vpp;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MODULATION SOURCE FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double modulationSourceFrequency_Hz;

  public final double getModulationSourceFrequency_Hz ()
  {
    return this.modulationSourceFrequency_Hz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MODULATION SOURCE WAVEFORM
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum ModulationSourceWaveformFunction
  {
    Off,
    Sine,
    Square,
    Arbitrary;
  }
  
  private final ModulationSourceWaveformFunction modulationSourceWaveformFunction;

  public final ModulationSourceWaveformFunction getModulationSourceWaveformFunction ()
  {
    return this.modulationSourceWaveformFunction;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PHASE MODULATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean phaseModulation;

  public final boolean isPhaseModulation ()
  {
    return this.phaseModulation;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // OPTIONS INSTALLED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum Option
  {
    Oven,
    HighVoltage;
  }
  
  private final EnumSet<Option> optionsInstalled;

  public final EnumSet<Option> getOptionsInstalled ()
  {
    return this.optionsInstalled;
  }
  
  public final boolean isOptionInstalledOven ()
  {
    return getOptionsInstalled ().contains (Option.Oven);
  }
  
  public final boolean isOptionInstalledHighVoltage ()
  {
    return getOptionsInstalled ().contains (Option.HighVoltage);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PHASE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double phase_degrees;

  public final double getPhase_degrees ()
  {
    return this.phase_degrees;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS BYTE RS-232
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final byte statusByteRs232;

  public final byte getStatusByteRs232 ()
  {
    return this.statusByteRs232;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RF OUTPUT MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum RFOutputMode
  {
    Front,
    Rear;
  }
  
  private final RFOutputMode rfOutputMode;

  public final RFOutputMode getRFOutputMode ()
  {
    return this.rfOutputMode;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWEEP MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum SweepMode
  {
    Linear,
    Logarithmic,
    Discrete;
  }
  
  private final SweepMode sweepMode;

  public final SweepMode getSweepMode ()
  {
    return this.sweepMode;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWEEP STOP FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double sweepStopFrequency_Hz;

  public final double getSweepStopFrequency_Hz ()
  {
    return this.sweepStopFrequency_Hz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWEEP START FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double sweepStartFrequency_Hz;

  public final double getSweepStartFrequency_Hz ()
  {
    return this.sweepStartFrequency_Hz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWEEP TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double sweepTime_s;
      
  public final double getSweepTime_s ()
  {
    return this.sweepTime_s;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    int hash = super.hashCode_NonBytes ();
    hash = 19 * hash + Objects.hashCode (this.ampltitudeCalibrationMode);
    hash = 19 * hash + (this.rs232Echo ? 1 : 0);
    hash = 19 * hash + (this.enhancements ? 1 : 0);
    hash = 19 * hash + this.errorCode;
    hash = 19 * hash + Objects.hashCode (this.serviceRequestEnableMask);
    hash = 19 * hash + (this.externalReferenceLocked ? 1 : 0);
    hash = 19 * hash + (this.useResponseHeader ? 1 : 0);
    hash = 19 * hash + (this.highVoltageOutput ? 1 : 0);
    hash = 19 * hash + Objects.hashCode (this.id);
    hash = 19 * hash + (this.amplitudeModulation ? 1 : 0);
    hash = 19 * hash + Objects.hashCode (this.dataTransferMode);
    hash = 19 * hash + (int) (Double.doubleToLongBits (this.markerFrequency_Hz) ^
      (Double.doubleToLongBits (this.markerFrequency_Hz) >>> 32));
    hash = 19 * hash + (int) (Double.doubleToLongBits (this.modulationSourceAmplitude_Vpp) ^
      (Double.doubleToLongBits (this.modulationSourceAmplitude_Vpp) >>> 32));
    hash = 19 * hash + (int) (Double.doubleToLongBits (this.modulationSourceFrequency_Hz) ^
      (Double.doubleToLongBits (this.modulationSourceFrequency_Hz) >>> 32));
    hash = 19 * hash + Objects.hashCode (this.modulationSourceWaveformFunction);
    hash = 19 * hash + (this.phaseModulation ? 1 : 0);
    hash = 19 * hash + Objects.hashCode (this.optionsInstalled);
    hash = 19 * hash + (int) (Double.doubleToLongBits (this.phase_degrees) ^
      (Double.doubleToLongBits (this.phase_degrees) >>> 32));
    hash = 19 * hash + this.statusByteRs232;
    hash = 19 * hash + Objects.hashCode (this.rfOutputMode);
    hash = 19 * hash + Objects.hashCode (this.sweepMode);
    hash = 19 * hash + (int) (Double.doubleToLongBits (this.sweepStopFrequency_Hz) ^
      (Double.doubleToLongBits (this.sweepStopFrequency_Hz) >>> 32));
    hash = 19 * hash + (int) (Double.doubleToLongBits (this.sweepStartFrequency_Hz) ^
      (Double.doubleToLongBits (this.sweepStartFrequency_Hz) >>> 32));
    hash = 19 * hash + (int) (Double.doubleToLongBits (this.sweepTime_s) ^
      (Double.doubleToLongBits (this.sweepTime_s) >>> 32));
    return hash;
  }

  @Override
  public boolean equals (Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass () != obj.getClass ())
    {
      return false;
    }
    final HP3325B_GPIB_Settings other = (HP3325B_GPIB_Settings) obj;
    if (! super.equals_NonBytes (other))
    {
      return false;
    }
    if (this.rs232Echo != other.rs232Echo)
    {
      return false;
    }
    if (this.enhancements != other.enhancements)
    {
      return false;
    }
    if (this.errorCode != other.errorCode)
    {
      return false;
    }
    if (this.externalReferenceLocked != other.externalReferenceLocked)
    {
      return false;
    }
    if (this.useResponseHeader != other.useResponseHeader)
    {
      return false;
    }
    if (this.highVoltageOutput != other.highVoltageOutput)
    {
      return false;
    }
    if (this.amplitudeModulation != other.amplitudeModulation)
    {
      return false;
    }
    if (Double.doubleToLongBits (this.markerFrequency_Hz) != Double.doubleToLongBits (other.markerFrequency_Hz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.modulationSourceAmplitude_Vpp) != Double.doubleToLongBits (other.modulationSourceAmplitude_Vpp))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.modulationSourceFrequency_Hz) != Double.doubleToLongBits (other.modulationSourceFrequency_Hz))
    {
      return false;
    }
    if (this.phaseModulation != other.phaseModulation)
    {
      return false;
    }
    if (Double.doubleToLongBits (this.phase_degrees) != Double.doubleToLongBits (other.phase_degrees))
    {
      return false;
    }
    if (this.statusByteRs232 != other.statusByteRs232)
    {
      return false;
    }
    if (Double.doubleToLongBits (this.sweepStopFrequency_Hz) != Double.doubleToLongBits (other.sweepStopFrequency_Hz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.sweepStartFrequency_Hz) != Double.doubleToLongBits (other.sweepStartFrequency_Hz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.sweepTime_s) != Double.doubleToLongBits (other.sweepTime_s))
    {
      return false;
    }
    if (!Objects.equals (this.id, other.id))
    {
      return false;
    }
    if (!Objects.equals (this.serviceRequestEnableMask, other.serviceRequestEnableMask))
    {
      return false;
    }
    if (this.dataTransferMode != other.dataTransferMode)
    {
      return false;
    }
    if (this.modulationSourceWaveformFunction != other.modulationSourceWaveformFunction)
    {
      return false;
    }
    if (!Objects.equals (this.optionsInstalled, other.optionsInstalled))
    {
      return false;
    }
    if (this.rfOutputMode != other.rfOutputMode)
    {
      return false;
    }
    return this.sweepMode == other.sweepMode;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
