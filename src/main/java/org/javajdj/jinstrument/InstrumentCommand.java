/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>, TNO.
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
package org.javajdj.jinstrument;

import java.util.Map;

/** A command for an {@link Instrument}.
 * 
 * <p>
 * Instrument commands are {@code Map}s from {@link String}s to {@link Object}s.
 * The interface provides {@code String} definitions for the most command commands and
 * their arguments.
 * 
 * @see Instrument#addCommand
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface InstrumentCommand
  extends Map<String, Object>
{
  
  public final static String IC_COMMAND_KEY = "command";
  
  public final static String IC_RETURN_STATUS_KEY = "returnStatus";
  
  public final static String IC_RETURN_VALUE_KEY = "returnValue";
  
  public final static String IC_NOP_KEY = "commandNop";
  
  public final static String IC_PROPE_KEY = "commandProbe";
  
  public final static String IC_GET_SETTINGS_KEY = "commandGetSettings";
  
  public final static String IC_RF_OUTPUT_ENABLE = "commandRfOutputEnable";
  public final static String ICARG_RF_OUTPUT_ENABLE = "rfOutputEnable"; 
  
  public final static String IC_RF_OUTPUT_LEVEL = "commandRfOutputLevel";
  public final static String ICARG_RF_OUTPUT_LEVEL_DBM = "rfOutputLevel_dBm"; 
  
  public final static String IC_RF_FREQUENCY = "commandRfFrequency";
  public final static String ICARG_RF_FREQUENCY_MHZ = "rfFrequency_MHz"; 
  
  public final static String IC_RF_SPAN = "commandRfSpan";
  public final static String ICARG_RF_SPAN_MHZ = "rfSpan_MHz"; 
  
  public final static String IC_RF_START_FREQUENCY = "commandRfStartFrequency";
  public final static String ICARG_RF_START_FREQUENCY_MHZ = "rfStartFrequency_MHz"; 
  
  public final static String IC_RF_STOP_FREQUENCY = "commandRfStopFrequency";
  public final static String ICARG_RF_STOP_FREQUENCY_MHZ = "rfStopFrequency_MHz"; 
  
  public final static String IC_RF_SWEEP_MODE = "commandRfSweepMode";
  public final static String ICARG_RF_SWEEP_MODE = "rfSweepMode"; 
  
  public final static String IC_RF_AM_CONTROL = "commandRfAmControl";
  public final static String ICARG_RF_AM_ENABLE = "rfAmEnable"; 
  public final static String ICARG_RF_AM_DEPTH_PERCENT = "rfAmDepth_percent"; 
  public final static String ICARG_RF_AM_SOURCE = "rfAmSource"; 

  public final static String IC_RF_FM_CONTROL = "commandRfFmControl";
  public final static String ICARG_RF_FM_ENABLE = "rfFmEnable"; 
  public final static String ICARG_RF_FM_DEVIATION_KHZ = "rfFmDeviation_kHz"; 
  public final static String ICARG_RF_FM_SOURCE = "rfFmSource"; 

  public final static String IC_RF_PM_CONTROL = "commandRfPmControl";
  public final static String ICARG_RF_PM_ENABLE = "rfPmEnable"; 
  public final static String ICARG_RF_PM_DEVIATION_DEGREES = "rfPmDeviation_degrees"; 
  public final static String ICARG_RF_PM_SOURCE = "rfPmSource"; 

  public final static String IC_RF_PULSEM_CONTROL = "commandRfPulseMControl";
  public final static String ICARG_RF_PULSEM_ENABLE = "rfPulseMEnable"; 
  public final static String ICARG_RF_PULSEM_SOURCE = "rfPulseMSource"; 

  public final static String IC_RF_BPSK_CONTROL = "commandRfBpskControl";
  public final static String ICARG_RF_BPSK_ENABLE = "rfBpskEnable"; 
  public final static String ICARG_RF_BPSK_SOURCE = "rfBpskSource"; 

  public final static String IC_INTMODSOURCE_CONTROL = "commandIntModSourceControl";
  public final static String ICARG_INTMODSOURCE_FREQUENCY_KHZ = "intModSourceFrequency_kHz";

  public final static String IC_RESOLUTION_BANDWIDTH = "commandResolutionBandwidth";
  public final static String ICARG_RESOLUTION_BANDWIDTH_HZ = "resolutionBandwidth_Hz";

  public final static String IC_SET_RESOLUTION_BANDWIDTH_COUPLED = "commandSetResolutionBandwidthCoupled";

  public final static String IC_VIDEO_BANDWIDTH = "commandVideoBandwidth";
  public final static String ICARG_VIDEO_BANDWIDTH_HZ = "videoBandwidth_Hz";

  public final static String IC_SET_VIDEO_BANDWIDTH_COUPLED = "commandSetVideoBandwidthCoupled";
  
  public final static String IC_SWEEP_TIME = "commandSweepTime";
  public final static String ICARG_SWEEP_TIME_S = "sweepTime_s";

  public final static String IC_SET_SWEEP_TIME_COUPLED = "commandSetSweepTimeCoupled";
  
  public final static String IC_REFERENCE_LEVEL = "commandReferenceLevel";
  public final static String ICARG_REFERENCE_LEVEL_DBM = "referenceLevel_dBm";

  public final static String IC_RF_ATTENUATION = "commandRfAttenuation";
  public final static String ICARG_RF_ATTENUATION_DB = "rfAttenuation_dB";

  public final static String IC_SET_RF_ATTENUATION_COUPLED = "commandSetRfAttenuationCoupled";
  
  public final static String IC_RESOLUTION = "commandResolution";
  public final static String ICARG_RESOLUTION = "resolution";
  
  public final static String IC_MEASUREMENT_MODE = "commandMeasurementMode";
  public final static String ICARG_MEASUREMENT_MODE = "measurementMode";
  
  public final static String IC_AUTO_RANGE = "commandAutoRange";

  public final static String IC_RANGE = "commandRange";
  public final static String ICARG_RANGE = "range";
  
  public final static String IC_AUTO_ZERO = "commandAutoZero";
  public final static String ICARG_AUTO_ZERO = "autoZero";
  
  public final static String IC_TRIGGER_MODE = "commandTriggerMode";
  public final static String ICARG_TRIGGER_MODE = "triggerMode";
  
}
