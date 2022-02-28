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
package org.javajdj.jinstrument.gpib.sa.hp70000;

import org.javajdj.jinstrument.InstrumentCommand;

/** A (specific) command for a {@link HP70000_GPIB_Instrument} Spectrum Analyzer.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface HP70000_InstrumentCommand
  extends InstrumentCommand
{
  
  // ABORT
  public final static String IC_HP70000_ABORT = "commandHp70000Abort";
  
  // XXX
  
  // CONFIG?
  public final static String IC_HP70000_GET_CONFIGURATION_STRING = "commandHp70000GetConfigurationString";
  
  // XXX
  
  // ID?
  public final static String IC_HP70000_GET_ID = "commandHp70000GetId";
  
  // IDN?
  public final static String IC_HP70000_GET_IDENTIFICATION_NUMBER = "commandHp70000GetIdentificationNumber";
  
  // XXX
  
  // IP
  public final static String IC_HP70000_SET_PRESET_STATE = "commandHp70000SetPresetState";
  
  // XXX
  
  // MEASURE?
  public final static String IC_HP70000_GET_MEASURE_MODE = "commandHp70000GetMeasureMode";
  
  // MEASURE
  public final static String IC_HP70000_SET_MEASURE_MODE = "commandHp70000SetMeasureMode";
  public final static String ICARG_HP70000_SET_MEASURE_MODE = "hp70000SetMeasureMode";
  
  // XXX
  
  // XXX SQR
  
  // SRCALC?
  public final static String IC_HP70000_GET_SOURCE_ALC_MODE = "commandHp70000GetSourceAlcMode";
  
  // SRCALC
  public final static String IC_HP70000_SET_SOURCE_ALC_MODE = "commandHp70000SetSourceAlcMode";
  public final static String ICARG_HP70000_SET_SOURCE_ALC_MODE = "hp70000SetSourceAlcMode";
  
  // SRCAM?
  public final static String IC_HP70000_GET_SOURCE_AM_DEPTH = "commandHp70000GetSourceAmDepth";
  
  // SRCAM
  public final static String IC_HP70000_SET_SOURCE_AM_DEPTH = "commandHp70000SetSourceAmDepth";
  public final static String ICARG_HP70000_SET_SOURCE_AM_DEPTH = "hp70000SetSourceAmDepth";
  public final static String IC_HP70000_SET_SOURCE_AM_ACTIVE = "commandHp70000SetSourceAmActive";
  public final static String ICARG_HP70000_SET_SOURCE_AM_ACTIVE = "hp70000SetSourceAmActive";
  
  // SRCAMF?
  public final static String IC_HP70000_GET_SOURCE_AM_FREQUENCY = "commandHp70000GetSourceAmFrequency";
  
  // SRCAMF
  public final static String IC_HP70000_SET_SOURCE_AM_FREQUENCY = "commandHp70000SetSourceAmFrequency";
  public final static String ICARG_HP70000_SET_SOURCE_AM_FREQUENCY = "hp70000SetSourceAmFrequency";
  
  // SRCAT?
  public final static String IC_HP70000_GET_SOURCE_ATTENUATION = "commandHp70000GetSourceAttenuation";
  
  // SRCAT
  public final static String IC_HP70000_SET_SOURCE_ATTENUATION = "commandHp70000SetSourceAttenuation";
  public final static String ICARG_HP70000_SET_SOURCE_ATTENUATION = "hp70000SetSourceAttenuation";
  public final static String IC_HP70000_SET_SOURCE_ATTENUATION_COUPLED = "commandHp70000SetSourceAttenuationCoupled";
  public final static String ICARG_HP70000_SET_SOURCE_ATTENUATION_COUPLED = "hp70000SetSourceAttenuationCoupled";
  
  // SRCBLNK?
  public final static String IC_HP70000_GET_SOURCE_BLANKING = "commandHp70000GetSourceBlanking";
  
  // SRCBLNK
  public final static String IC_HP70000_SET_SOURCE_BLANKING = "commandHp70000SetSourceBlanking";
  public final static String ICARG_HP70000_SET_SOURCE_BLANKING = "hp70000SetSourceBlanking";
  
  // SRCMOD?
  public final static String IC_HP70000_GET_SOURCE_MODULATION_INPUT = "commandHp70000GetSourceModulationInput";
  
  // SRCMOD
  public final static String IC_HP70000_SET_SOURCE_MODULATION_INPUT = "commandHp70000SetSourceModulationInput";
  public final static String ICARG_HP70000_SET_SOURCE_MODULATION_INPUT = "hp70000SetSourceModulationInput";
  
  // SRCOSC?
  public final static String IC_HP70000_GET_SOURCE_OSCILLATOR = "commandHp70000GetSourceOscillator";
  
  // SRCOSC
  public final static String IC_HP70000_SET_SOURCE_OSCILLATOR = "commandHp70000SetSourceOscillator";
  public final static String ICARG_HP70000_SET_SOURCE_OSCILLATOR = "hp70000SetSourceOscillator";
  
  // SRCPOFS?
  public final static String IC_HP70000_GET_SOURCE_POWER_OFFSET = "commandHp70000GetSourcePowerOffset";
  
  // SRCPOFS
  public final static String IC_HP70000_SET_SOURCE_POWER_OFFSET = "commandHp70000SetSourcePowerOffset";
  public final static String ICARG_HP70000_SET_SOURCE_POWER_OFFSET = "hp70000SetSourcePowerOffset";
  
  // SRCPSTP?
  public final static String IC_HP70000_GET_SOURCE_POWER_STEP_SIZE = "commandHp70000GetSourcePowerStepSize";
  
  // SRCPSTP
  public final static String IC_HP70000_SET_SOURCE_POWER_STEP_SIZE = "commandHp70000SetSourcePowerStepSize";
  public final static String ICARG_HP70000_SET_SOURCE_POWER_STEP_SIZE = "hp70000SetSourcePowerStepSize";
  public final static String IC_HP70000_SET_SOURCE_POWER_STEP_SIZE_MODE = "commandHp70000SetSourcePowerStepSizeMode";
  public final static String ICARG_HP70000_SET_SOURCE_POWER_STEP_SIZE_MODE = "hp70000SetSourcePowerStepSizeMode";
  
  // SRCPSWP?
  public final static String IC_HP70000_GET_SOURCE_POWER_SWEEP_RANGE = "commandHp70000GetSourcePowerSweepRange";
  
  // SRCPSWP
  public final static String IC_HP70000_SET_SOURCE_POWER_SWEEP_RANGE = "commandHp70000SetSourcePowerSweepRange";
  public final static String ICARG_HP70000_SET_SOURCE_POWER_SWEEP_RANGE = "hp70000SetSourcePowerSweepRange";
  public final static String IC_HP70000_SET_SOURCE_POWER_SWEEP_ACTIVE = "commandHp70000SetSourcePowerSweepActive";
  public final static String ICARG_HP70000_SET_SOURCE_POWER_SWEEP_ACTIVE = "hp70000SetSourcePowerSweepActive";
  
  // SRCPWR?
  public final static String IC_HP70000_GET_SOURCE_POWER = "commandHp70000GetSourcePower";
  
  // SRCPWR
  public final static String IC_HP70000_SET_SOURCE_POWER = "commandHp70000SetSourcePower";
  public final static String ICARG_HP70000_SET_SOURCE_POWER = "hp70000SetSourcePower";
  public final static String IC_HP70000_SET_SOURCE_ACTIVE = "commandHp70000SetSourceActive";
  public final static String ICARG_HP70000_SET_SOURCE_ACTIVE = "hp70000SetSourceActive";  
  
  // SRCTK?
  public final static String IC_HP70000_GET_SOURCE_TRACKING = "commandHp70000GetSourceTracking";
  
  // SRCTK
  public final static String IC_HP70000_SET_SOURCE_TRACKING = "commandHp70000SetSourceTracking";
  public final static String ICARG_HP70000_SET_SOURCE_TRACKING = "hp70000SetSourceTracking";
  
  // SRCTK;
  public final static String IC_HP70000_SET_SOURCE_TRACKING_AUTO_ONCE = "commandHp70000SetSourceTrackingAutoOnce";
  
  // SRCTKPK;
  public final static String IC_HP70000_SET_SOURCE_PEAK_TRACKING_AUTO = "commandHp70000SetSourcePeakTrackingAuto";
  public final static String ICARG_HP70000_SET_SOURCE_PEAK_TRACKING_AUTO = "hp70000SetSourcePeakTrackingAuto";
  
  // XXX SRQ
  
  // XXX

}
