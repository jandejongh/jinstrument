/*
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>, TNO.
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

import org.javajdj.jinstrument.InstrumentCommand;

/** A command for a {@link HP3325B_GPIB_Instrument} Function Generator.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface HP3325B_InstrumentCommand
  extends InstrumentCommand
{
  
  public final static String IC_HP3325B_AMPLITUDE_CALIBRATION = "commandHp3325bAmplitudeCalibration";

  public final static String IC_HP3325B_ASSIGN_ZERO_PHASE = "commandHp3325bAssignZeroPhase";
    
  public final static String IC_HP3325B_AMPLITUDE_CALIBRATION_MODE = "commandHp3325bAmplitudeCalibrationMode";
  public final static String ICARG_HP3325B_AMPLITUDE_CALIBRATION_MODE = "hp3325bAmplitudeCalibrationMode";
  
  public final static String IC_HP3325B_DISCRETE_SWEEP_TABLE_CLEAR = "commandHp3325bDiscreteSweepTableClear";
  
  public final static String IC_HP3325B_DISPLAY = "commandHp3325bDisplay";
  public final static String ICARG_HP3325B_DISPLAY = "hp3325bDisplay";

  public final static String IC_HP3325B_DISCRETE_SWEEP_STORE = "commandHp3325bDiscreteSweepStore";
  public final static String ICARG_HP3325B_DISCRETE_SWEEP_STORE = "hp3325bDiscreteSweepStore";

  public final static String IC_HP3325B_DISCRETE_SWEEP_RECALL = "commandHp3325bDiscreteSweepRecall";
  public final static String ICARG_HP3325B_DISCRETE_SWEEP_RECALL = "hp3325bDiscreteSweepRecall";
    
  public final static String IC_HP3325B_DISPLAY_STRING = "commandHp3325bDisplayString";
  public final static String ICARG_HP3325B_DISPLAY_STRING = "hp3325bDisplayString";
    
  public final static String IC_HP3325B_RS232_ECHO = "commandHp3325bRs232Echo";
  public final static String ICARG_HP3325B_RS232_ECHO = "hp3325bRs232Echo";
  
  public final static String IC_HP3325B_ENHANCEMENTS_CONTROL = "commandHp3325bEnhancementsControl";
  public final static String ICARG_HP3325B_ENHANCEMENTS_CONTROL = "hp3325bEnhancementsControl";
    
  public final static String IC_HP3325B_ERROR = "commandHp3325bError";
    
  public final static String IC_HP3325B_SERVICE_REQUEST_ENABLE = "commandHp3325bServiceRequestEnable";
  public final static String ICARG_HP3325B_SERVICE_REQUEST_ENABLE = "hp3325bServiceRequestEnable";
    
  public final static String IC_HP3325B_EXTERNAL_REFERENCE_LOCKED = "commandHp3325bExternalReferenceLocked";
  
  public final static String IC_HP3325B_RESPONSE_HEADER_CONTROL = "commandHp3325bResponseHeaderControl";
  public final static String ICARG_HP3325B_RESPONSE_HEADER_CONTROL = "hp3325bResponseHeaderControl";
    
  public final static String IC_HP3325B_HIGH_VOLTAGE_OUTPUT = "commandHp3325bHighVoltageOutput";
  public final static String ICARG_HP3325B_HIGH_VOLTAGE_OUTPUT = "hp3325bHighVoltageOutput";
  
  public final static String IC_HP3325B_IDENTIFICATION = "commandHp3325bIdentification";
    
  public final static String IC_HP3325B_LOCAL = "commandHp3325bLocal";
    
  public final static String IC_HP3325B_AMPLITUDE_MODULATION = "commandHp3325bAmplitudeModulation";
  public final static String ICARG_HP3325B_AMPLITUDE_MODULATION = "hp3325bAmplitudeModulation";
    
  public final static String IC_HP3325B_DATA_TRANSFER_MODE = "commandHp3325bDataTransferMode";
  public final static String ICARG_HP3325B_DATA_TRANSFER_MODE = "hp3325bDataTransferMode";
  
  public final static String IC_HP3325B_MARKER_FREQUENCY = "commandHp3325bMarkerFrequency";
  public final static String ICARG_HP3325B_MARKER_FREQUENCY = "hp3325bMarkerFrequency";
  
  public final static String IC_HP3325B_MODULATION_SOURCE_AMPLITUDE = "commandHp3325bModulationSourceAmplitude";
  public final static String ICARG_HP3325B_MODULATION_SOURCE_AMPLITUDE = "hp3325bModulationSourceAmplitude";
    
  public final static String IC_HP3325B_WRITE_MODULATION_SOURCE_ARBITRARY_WAVEFORM
    = "commandHp3325bWriteModulationSourceArbitraryWaveform";
  public final static String ICARG_HP3325B_WRITE_MODULATION_SOURCE_ARBITRARY_WAVEFORM
    = "hp3325bWriteModulationSourceArbitraryWaveform";
  
  public final static String IC_HP3325B_MODULATION_SOURCE_FREQUENCY = "commandHp3325bModulationSourceFrequency";
  public final static String ICARG_HP3325B_MODULATION_SOURCE_FREQUENCY = "hp3325bModulationSourceFrequency";
  
  public final static String IC_HP3325B_MODULATION_SOURCE_WAVEFORM_FUNCTION = "commandHp3325bModulationSourceWaveformFunction";
  public final static String ICARG_HP3325B_MODULATION_SOURCE_WAVEFORM_FUNCTION = "hp3325bModulationSourceWaveformFunction";
    
  public final static String IC_HP3325B_PHASE_MODULATION = "commandHp3325bPhaseModulation";
  public final static String ICARG_HP3325B_PHASE_MODULATION = "hp3325bPhaseModulation";
    
  public final static String IC_HP3325B_STATUS_BYTE_MASK = "commandHp3325bStatusByteMask";
  public final static String ICARG_HP3325B_STATUS_BYTE_MASK = "hp3325bStatusByteMask";
    
  public final static String IC_HP3325B_OPTION = "commandHp3325bOption";
  
  public final static String IC_HP3325B_PHASE = "commandHp3325bPhase";
  public final static String ICARG_HP3325B_PHASE = "hp3325bPhase";

  public final static String IC_HP3325B_RS232_STATUS_BYTE = "commandHp3325bRs232StatusByte";
  
  public final static String IC_HP3325B_RECALL_STATE = "commandHp3325bRecallState";
  public final static String ICARG_HP3325B_RECALL_STATE = "hp3325bRecallState";
  
  public final static String IC_HP3325B_REAR_FRONT = "commandHp3325bRearFront";
  public final static String ICARG_HP3325B_REAR_FRONT = "hp3325bRearFront";
  
  public final static String IC_HP3325B_REMOTE = "commandHp3325bRemote";
  
  public final static String IC_HP3325B_RESET = "commandHp3325bReset";
  
  public final static String IC_HP3325B_RESET_SINGLE_SWEEP = "commandHp3325bResetSingleSweep";
  
  public final static String IC_HP3325B_START_CONTINUOUS_SWEEP = "commandHp3325bStartContinuousSweep";
  
  public final static String IC_HP3325B_SWEEP_MODE = "commandHp3325bSweepMode";
  public final static String ICARG_HP3325B_SWEEP_MODE = "hp3325bSweepMode";
  
  public final static String IC_HP3325B_SWEEP_STOP_FREQUENCY = "commandHp3325bSweepStopFrequency";
  public final static String ICARG_HP3325B_SWEEP_STOP_FREQUENCY = "hp3325bSweepStopFrequency";
  
  public final static String IC_HP3325B_STORE_STATE = "commandHp3325bStoreState";
  public final static String ICARG_HP3325B_STORE_STATE = "hp3325bStoreState";
  
  public final static String IC_HP3325B_START_SINGLE_SWEEP = "commandHp3325bStartSingleSweep";
  
  public final static String IC_HP3325B_SWEEP_START_FREQUENCY = "commandHp3325bSweepStartFrequency";
  public final static String ICARG_HP3325B_SWEEP_START_FREQUENCY = "hp3325bSweepStartFrequency";
  
  public final static String IC_HP3325B_SWEEP_TIME = "commandHp3325bSweepTime";
  public final static String ICARG_HP3325B_SWEEP_TIME = "hp3325bSweepTime";
  
}
