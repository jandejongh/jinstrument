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
package org.javajdj.jinstrument.gpib.slm.rs_esh3;

import org.javajdj.jinstrument.*;

/** A command for a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface RS_ESH3_InstrumentCommand
  extends InstrumentCommand
{
  
  public final static String IC_RS_ESH3_ATTENUATION_MODE = "commandRsEsh3AttenuationMode";
  public final static String ICARG_RS_ESH3_ATTENUATION_MODE = "rsEsh3AttenuationMode";
  
  public final static String IC_RS_ESH3_RF_ATTENUATION = "commandRsEsh3RfAttenuation";
  public final static String ICARG_RS_ESH3_RF_ATTENUATION = "rsEsh3RfAttenuation";

  public final static String IC_RS_ESH3_LINEARITY_TEST = "commandRsEsh3LinearityTest";
  public final static String ICARG_RS_ESH3_LINEARITY_TEST = "rsEsh3LinearityTest";

  public final static String IC_RS_ESH3_FREQUENCY_START = "commandRsEsh3FrequencyStart";
  public final static String ICARG_RS_ESH3_FREQUENCY_START = "rsEsh3DFrequencyStart";
  
  public final static String IC_RS_ESH3_FREQUENCY_STOP = "commandRsEsh3FrequencyStop";
  public final static String ICARG_RS_ESH3_FREQUENCY_STOP = "rsEsh3DFrequencyStop";
  
  public final static String IC_RS_ESH3_IF_ATTENUATION = "commandRsEsh3IfAttenuation";
  public final static String ICARG_RS_ESH3_IF_ATTENUATION = "rsEsh3IfAttenuation";
 
  public final static String IC_RS_ESH3_IF_BANDWIDTH = "commandRsEsh3IfBandwidth";
  public final static String ICARG_RS_ESH3_IF_BANDWIDTH = "rsEsh3IfBandwidth";
  
  public final static String IC_RS_ESH3_DEMODULATION_MODE = "commandRsEsh3DemodulationMode";
  public final static String ICARG_RS_ESH3_DEMODULATION_MODE = "rsEsh3DemodulationMode";
  
  public final static String IC_RS_ESH3_CALIBRATE_CHECK = "commandRsEsh3CalibrateCheck";
  
  public final static String IC_RS_ESH3_CALIBRATE_TOTAL = "commandRsEsh3CalibrateTotal";
  
  public final static String IC_RS_ESH3_MAX_MIN_MODE = "commandRsEsh3MaxMinMode";
  public final static String ICARG_RS_ESH3_MAX_MIN_MODE = "rsEsh3MaxMinMode";
  
  public final static String IC_RS_ESH3_OPERATING_RANGE = "commandRsEsh3OperatingRange";
  public final static String ICARG_RS_ESH3_OPERATING_RANGE = "rsEsh3OperatingRange";
  
  public final static String IC_RS_ESH3_OPERATING_MODE = "commandRsEsh3OperatingMode";
  public final static String ICARG_RS_ESH3_OPERATING_MODE = "rsEsh3OperatingMode";
  
  public final static String IC_RS_ESH3_INDICATING_MODE = "commandRsEsh3IndicatingMode";
  public final static String ICARG_RS_ESH3_INDICATING_MODE = "rsEsh3IndicatingMode";
  
  public final static String IC_RS_ESH3_DATA_OUTPUT_MODE = "commandRsEsh3DataOutputMode";
  public final static String ICARG_RS_ESH3_DATA_OUTPUT_MODE = "rsEsh3DataOutputMode";
  
  public final static String IC_RS_ESH3_MEASUREMENT_TIME = "commandRsEsh3MeasurementTime";
  public final static String ICARG_RS_ESH3_MEASUREMENT_TIME = "rsEsh3MeasurementTime";
  
  public final static String IC_RS_ESH3_MINIMUM_LEVEL = "commandRsEsh3MinimumLevel";
  public final static String ICARG_RS_ESH3_MINIMUM_LEVEL = "rsEsh3MinimumLevel";
  
  public final static String IC_RS_ESH3_MAXIMUM_LEVEL = "commandRsEsh3MaximumLevel";
  public final static String ICARG_RS_ESH3_MAXIMUM_LEVEL = "rsEsh3MaximumLevel";
  
  public final static String IC_RS_ESH3_SCANNING_RUN = "commandRsEsh3ScanningRun";
  public final static String ICARG_RS_ESH3_SCANNING_RUN = "rsEsh3ScanningRun";
  
  public final static String IC_RS_ESH3_SCANNING_STOP_INTERRUPT = "commandRsEsh3ScanningStopInterrupt";
  
  public final static String IC_RS_ESH3_SCANNING_STOP_RESET = "commandRsEsh3ScanningStopReset";
  
  public final static String IC_RS_ESH3_SF00_SF_CLEAR = "commandRsEsh3Sf00SfClear";
  
  public final static String IC_RS_ESH3_SF10_SF11_TEST_LEVEL = "commandRsEsh3Sf10Sf11TestLevel";
  public final static String ICARG_RS_ESH3_SF10_SF11_TEST_LEVEL = "rsEsh3Sf10Sf11TestLevel";
  
  public final static String IC_RS_ESH3_SF20_SF21_TEST_MODULATION_DEPTH = "commandRsEsh3Sf20Sf21TestModulationDepth";
  public final static String ICARG_RS_ESH3_SF20_SF21_TEST_MODULATION_DEPTH = "rsEsh3Sf20Sf21TestModulationDepth";
  
  public final static String IC_RS_ESH3_SF22_SF23_TEST_MODULATION_DEPTH_POSITIVE_PEAK
    = "commandRsEsh3Sf22Sf23TestModulationDepthPosPeak";
  public final static String ICARG_RS_ESH3_SF22_SF23_TEST_MODULATION_DEPTH_POSITIVE_PEAK
    = "rsEsh3Sf22Sf23TestModulationDepthPosPeak";
  
  public final static String IC_RS_ESH3_SF24_SF25_TEST_MODULATION_DEPTH_NEGATIVE_PEAK
    = "commandRsEsh3Sf24Sf25TestModulationDepthNegPeak";
  public final static String ICARG_RS_ESH3_SF24_SF25_TEST_MODULATION_DEPTH_NEGATIVE_PEAK
    = "rsEsh3Sf24Sf25TestModulationDepthNegPeak";
  
  public final static String IC_RS_ESH3_SF30_SF31_TEST_FREQUENCY_OFFSET = "commandRsEsh3Sf30Sf31TestFrequencyOffset";
  public final static String ICARG_RS_ESH3_SF30_SF31_TEST_FREQUENCY_OFFSET = "rsEsh3Sf30Sf31TestFrequencyOffset";
  
  public final static String IC_RS_ESH3_SF40_SF41_TEST_FREQUENCY_DEVIATION = "commandRsEsh3Sf40Sf41TestFrequencyDeviation";
  public final static String ICARG_RS_ESH3_SF40_SF41_TEST_FREQUENCY_DEVIATION = "rsEsh3Sf40Sf41TestFrequencyDeviation";
  
  public final static String IC_RS_ESH3_SF42_SF43_TEST_FREQUENCY_DEVIATION_POSITIVE_PEAK
    = "commandRsEsh3Sf42Sf43TestFrequencyDeviationPosPeak";
  public final static String ICARG_RS_ESH3_SF42_SF43_TEST_FREQUENCY_DEVIATION_POSITIVE_PEAK
    = "rsEsh3Sf42Sf43TestFrequencyDeviationPosPeak";
  
  public final static String IC_RS_ESH3_SF44_SF45_TEST_FREQUENCY_DEVIATION_NEGATIVE_PEAK
    = "commandRsEsh3Sf44Sf45TestFrequencyDeviationNegPeak";
  public final static String ICARG_RS_ESH3_SF44_SF45_TEST_FREQUENCY_DEVIATION_NEGATIVE_PEAK
    = "rsEsh3Sf44Sf45TestFrequencyDeviationNegPeak";
  
  public final static String IC_RS_ESH3_SF50_SF51_FREQUENCY_SCAN_REPEAT_MODE = "commandRsEsh3Sf50Sf51FrequencyScanRepeatMode";
  public final static String ICARG_RS_ESH3_SF50_SF51_FREQUENCY_SCAN_REPEAT_MODE = "rsEsh3Sf50Sf51FrequencyScanRepeatMode";
  
  public final static String IC_RS_ESH3_SF90_SF91_FREQUENCY_SCAN_SPEED_MODE = "commandRsEsh3Sf90Sf91FrequencyScanSpeedMode";
  public final static String ICARG_RS_ESH3_SF90_SF91_FREQUENCY_SCAN_SPEED_MODE = "rsEsh3Sf90Sf91FrequencyScanSpeedMode";
  
  public final static String IC_RS_ESH3_SF52_SF53_STEP_SIZE_MODE = "commandRsEsh3Sf52Sf53StepSizeMode";
  public final static String ICARG_RS_ESH3_SF52_SF53_STEP_SIZE_MODE = "rsEsh3Sf52Sf53StepSizeMode";
  
  public final static String IC_RS_ESH3_STEP_SIZE_MHZ = "commandRsEsh3StepSizeMHz";
  public final static String ICARG_RS_ESH3_STEP_SIZE_MHZ = "rsEsh3StepSizeMHz";
  
  public final static String IC_RS_ESH3_STEP_SIZE_PERCENT = "commandRsEsh3StepSizePercent";
  public final static String ICARG_RS_ESH3_STEP_SIZE_PERCENT = "rsEsh3StepSizePercent";
  
  public final static String IC_RS_ESH3_STORE = "commandRsEsh3Store";
  public final static String ICARG_RS_ESH3_STORE = "rsEsh3Store";
  
  public final static String IC_RS_ESH3_RECALL = "commandRsEsh3Recall";
  public final static String ICARG_RS_ESH3_RECALL = "rsEsh3Recall";
  
  public final static String IC_RS_ESH3_SF80_SF81_FIELD_DATA_OUTPUT_MODE = "commandRsEsh3Sf80Sf81FieldDataOutputMode";
  public final static String ICARG_RS_ESH3_SF80_SF81_FIELD_DATA_OUTPUT_MODE = "rsEsh3Sf80Sf81FieldDataOutputMode";
  
  public final static String IC_RS_ESH3_EOI = "commandRsEsh3Eoi";
  public final static String ICARG_RS_ESH3_EOI = "rsEsh3Eoi";
  
  public final static String IC_RS_ESH3_TRIGGER = "commandRsEsh3Trigger";
  
  public final static String IC_RS_ESH3_RECORDER_CODE = "commandRsEsh3RecorderCode";
  public final static String ICARG_RS_ESH3_RECORDER_CODE = "rsEsh3RecorderCode";
  
  public final static String IC_RS_ESH3_ANTENNA_PROBE_CODE = "commandRsEsh3AntennaProbeCode";
  public final static String ICARG_RS_ESH3_ANTENNA_PROBE_CODE = "rsEsh3AntennaProbeCode";
  
  public final static String IC_RS_ESH3_SRQ_ON_DATA_READY = "commandRsEsh3SrqOnDataReady";
  public final static String ICARG_RS_ESH3_SRQ_ON_DATA_READY = "rsEsh3SrqOnDataReady";
  
  public final static String IC_RS_ESH3_SRQ_ON_ENTER_IN_LOCAL_STATE = "commandRsEsh3SrqOnEnterInLocalState";
  public final static String ICARG_RS_ESH3_SRQ_ON_ENTER_IN_LOCAL_STATE = "rsEsh3SrqOnEnterInLocalState";
  
  public final static String IC_RS_ESH3_GPIB_DELIMITER_IN_TALKER_OPERATION = "commandRsEsh3GpibDelimiterInTalkerOperation";
  public final static String ICARG_RS_ESH3_GPIB_DELIMITER_IN_TALKER_OPERATION = "rsEsh3GpibDelimiterInTalkerOperation";
  
  public final static String IC_RS_ESH3_DISPLAY_TEXT = "commandRsEsh3DisplayText";
  public final static String ICARG_RS_ESH3_DISPLAY_TEXT = "rsEsh3DisplayText";
  
  public final static String IC_RS_ESH3_BEEP = "commandRsEsh3Beep";
  
  public final static String IC_RS_ESH3_DA_X_REGISTER = "commandRsEsh3DAXRegister";
  public final static String ICARG_RS_ESH3_DA_X_REGISTER = "rsEsh3DAXRegister";
  
  public final static String IC_RS_ESH3_DA_Y_REGISTER = "commandRsEsh3DAYRegister";
  public final static String ICARG_RS_ESH3_DA_Y_REGISTER = "rsEsh3DAYRegister";
  
  public final static String IC_RS_ESH3_RECORDER_X_AXIS_MODE = "commandRsEsh3RecorderXAxisMode";
  public final static String ICARG_RS_ESH3_RECORDER_X_AXIS_MODE = "commandRsEsh3RecoderXAxisMode";
  
  public final static String IC_RS_ESH3_SF62_SF63_RECORDER_CODING_ENABLED = "commandRsEsh3Sf62Sf63RecorderCodingEnabled";
  public final static String ICARG_RS_ESH3_SF62_SF63_RECORDER_CODING_ENABLED = "rsEsh3Sf62Sf63RecorderCodingEnabled";
    
  public final static String IC_RS_ESH3_RECORDER_NO_YT = "commandRsEsh3RecorderNoYT";
  public final static String ICARG_RS_ESH3_RECORDER_NO_YT = "rsEsh3RecorderNoYT";
  
  public final static String IC_RS_ESH3_RECORDER_NO_XY = "commandRsEsh3RecorderNoXY";
  public final static String ICARG_RS_ESH3_RECORDER_NO_XY = "rsEsh3RecorderNoXY";
  
  public final static String IC_RS_ESH3_RECORDER_NO_ZSG3 = "commandRsEsh3RecorderNoZSG3";
  public final static String ICARG_RS_ESH3_RECORDER_NO_ZSG3 = "rsEsh3RecorderNoZSG3";
  
  public final static String IC_RS_ESH3_XY_RECORDER_SPECTRUM_MODE = "commandRsEsh3XYRecorderSpectrumMode";
  public final static String ICARG_RS_ESH3_XY_RECORDER_SPECTRUM_MODE = "rsEsh3XYRecorderSpectrumMode";
  
  public final static String IC_RS_ESH3_REFRESH = "commandRsEsh3Refresh";
  
}
