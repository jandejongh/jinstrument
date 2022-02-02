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
package org.javajdj.jinstrument.gpib.dmm.hp3457a;

import org.javajdj.jinstrument.*;

/** A (specific) command for a {@link HP3457A_GPIB_Instrument} Digital MultiMeter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public interface HP3457A_InstrumentCommand
  extends InstrumentCommand
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // OFFCIAL COMMANDS
  //
  // IN ORDER AS THEY APPEAR IN THE HP3457A MULTIMETER OPERATING MANUAL [03457-90003]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // ACAL
  public final static String IC_HP3457A_AUTO_CALIBRATE = "commandHp3457aAutoCalibrate";
  public final static String ICARG_HP3457A_AUTO_CALIBRATE = "hp3478aAutoCalibrate";

  // ACBAND
  public final static String IC_HP3457A_AC_BANDWIDTH = "commandHp3457aACBandwidth";
  public final static String ICARG_HP3457A_AC_BANDWIDTH = "hp3457aACBandwidth";

  // ACDCI
  public final static String IC_HP3457A_AC_DC_CURRENT = "commandHp3457aACDCCurrent";
  public final static String ICARG_HP3457A_AC_DC_CURRENT_RANGE = "hp3457aACDCCurrentRange";
  public final static String ICARG_HP3457A_AC_DC_CURRENT_RESOLUTION = "hp3457aACDCCurrentResolution";
  
  // ACDCV
  public final static String IC_HP3457A_AC_DC_VOLTS = "commandHp3457aACDCVolts";
  public final static String ICARG_HP3457A_AC_DC_VOLTS_RANGE = "hp3457aACDCVoltsRange";
  public final static String ICARG_HP3457A_AC_DC_VOLTS_RESOLUTION = "hp3457aACDCVoltsResolution";

  // ACI
  public final static String IC_HP3457A_AC_CURRENT = "commandHp3457aACCurrent";
  public final static String ICARG_HP3457A_AC_CURRENT_RANGE = "hp3457aACCurrentRange";
  public final static String ICARG_HP3457A_AC_CURRENT_RESOLUTION = "hp3457aACCurrentResolution";

  // ACV
  public final static String IC_HP3457A_AC_VOLTS = "commandHp3457aACVolts";
  public final static String ICARG_HP3457A_AC_VOLTS_RANGE = "hp3457aACVoltsRange";
  public final static String ICARG_HP3457A_AC_VOLTS_RESOLUTION = "hp3457aACVoltsResolution";

  // ADDRESS
  public final static String IC_HP3457A_SET_GPIB_ADDRESS = "commandHp3457aGpibAddress";
  public final static String ICARG_HP3457A_SET_GPIB_ADDRESS = "hp3457aGpibAddress";

  // ARANGE
  public final static String IC_HP3457A_SET_AUTORANGE = "commandHp3457aAutoRange";
  public final static String ICARG_HP3457A_SET_AUTORANGE = "hp3457aAutoRange";

  // AUXERR?
  public final static String IC_HP3457A_GET_AUXILIARY_ERROR = "commandHp3457aGetAuxiliaryError";

  // AZERO
  public final static String IC_HP3457A_SET_AUTOZERO_MODE = "commandHp3457aSetAutoZeroMode";
  public final static String ICARG_HP3457A_SET_AUTOZERO_MODE = "hp3457aSetAutoZeroMode";

  // AZERO?
  public final static String IC_HP3457A_GET_AUTOZERO_MODE = "commandHp3457aGetAutoZeroMode";

  // BEEP
  public final static String IC_HP3457A_SET_BEEP_MODE = "commandHp3457aSetBeepMode";
  public final static String ICARG_HP3457A_SET_BEEP_MODE = "hp3457aSetBeepMode";
  public final static String IC_HP3457A_BEEP = "commandHp3457aBeep";

  // CAL
  public final static String IC_HP3457A_CALIBRATE = "commandHp3457aCalibrate";

  // CALL
  public final static String IC_HP3457A_CALL_SUBPROGRAM = "commandHp3457aCallSubprogram";
  public final static String ICARG_HP3457A_CALL_SUBPROGRAM = "hp3457aCallSubprogram";

  // CALNUM?
  public final static String IC_HP3457A_GET_CALIBRATION_NUMBER = "commandHp3457aGetCalibrationNumber";

  // CHAN
  public final static String IC_HP3457A_SET_CHANNEL = "commandHp3457aSetChannel";
  public final static String ICARG_HP3457A_SET_CHANNEL = "hp3457aSetChannel";

  // CHAN?
  public final static String IC_HP3457A_GET_CHANNEL = "commandHp3457aGetChannel";

  // CLOSE
  public final static String IC_HP3457A_CLOSE_ACTUATOR_CHANNEL = "commandHp3457aCloseActuatorChannel";
  public final static String ICARG_HP3457A_CLOSE_ACTUATOR_CHANNEL = "hp3457aCloseActuatorChannel";

  // CRESET
  public final static String IC_HP3457A_CARD_RESET = "commandHp3457aCardReset";

  // CSB
  public final static String IC_HP3457A_CLEAR_STATUS_BYTE = "commandHp3457aClearStatusByte";

  // DCI
  public final static String IC_HP3457A_DC_CURRENT = "commandHp3457aDCCurrent";
  public final static String ICARG_HP3457A_DC_CURRENT_RANGE = "hp3457aDCCurrentRange";
  public final static String ICARG_HP3457A_DC_CURRENT_RESOLUTION = "hp3457aDCCurrentResolution";

  // DCV
  public final static String IC_HP3457A_DC_VOLTS = "commandHp3457aDCVolts";
  public final static String ICARG_HP3457A_DC_VOLTS_RANGE = "hp3457aDCVoltsRange";
  public final static String ICARG_HP3457A_DC_VOLTS_RESOLUTION = "hp3457aDCVoltsResolution";

  // DELAY
  public final static String IC_HP3457A_SET_DELAY = "commandHp3457aSetDelay";
  public final static String ICARG_HP3457A_SET_DELAY = "hp3457aSetDelay";

  // DELAY?
  public final static String IC_HP3457A_GET_DELAY = "commandHp3457aGetDelay";

  // DIAGNOSTIC
  public final static String IC_HP3457A_DIAGNOSTIC = "commandHp3457aDiagnostic";

  // DISP
  public final static String IC_HP3457A_DISPLAY = "commandHp3457aDisplay";
  public final static String ICARG_HP3457A_DISPLAY_CONTROL = "hp3457aDisplayControl";
  public final static String ICARG_HP3457A_DISPLAY_MESSAGE = "hp3457aDisplayMessage";

  // EMASK
  public final static String IC_HP3457A_SET_ERROR_MASK = "commandHp3457aSetErrorMask";
  public final static String ICARG_HP3457A_SET_ERROR_MASK = "hp3457aSetErrorMask";

  // END
  public final static String IC_HP3457A_SET_EOI = "commandHp3457aSetEoi";
  public final static String ICARG_HP3457A_SET_EOI = "hp3457aSetEoi";

  // ERR?
  public final static String IC_HP3457A_GET_ERROR = "commandHp3457aGetError";

  // F10-F58
  public final static String IC_HP3457A_SET_FUNCTION_FAST = "commandHp3457aSetFunctionFast";
  public final static String ICARG_HP3457A_SET_FUNCTION_FAST = "hp3457aSetFunctionFast";

  // FIXEDZ
  public final static String IC_HP3457A_SET_FIXED_IMPEDANCE = "commandHp3457aSetFixedImpedance";
  public final static String ICARG_HP3457A_SET_FIXED_IMPEDANCE = "hp3457aSetFixedImpedance";

  // FIXEDZ?
  public final static String IC_HP3457A_GET_FIXED_IMPEDANCE = "commandHp3457aGetFixedImpedance";

  // FREQ
  public final static String IC_HP3457A_FREQUENCY = "commandHp3457aFrequency";
  public final static String ICARG_HP3457A_FREQUENCY_AMPLITUDE_RANGE = "hp3457aFrequencyAmplitudeRange";

  // FSOURCE
  public final static String IC_HP3457A_SET_FREQUENCY_OR_PERIOD_SOURCE = "commandHp3457aSetFrequencyOrPeriodSource";
  public final static String ICARG_HP3457A_SET_FREQUENCY_OR_PERIOD_SOURCE = "hp3457aSetFrequencyOrPeriodSource";

  // FUNC
  public final static String IC_HP3457A_SET_FUNCTION = "commandHp3457aSetFunction";
  public final static String ICARG_HP3457A_SET_FUNCTION_FUNCTION = "hp3457aSetFunctionFunction";
  public final static String ICARG_HP3457A_SET_FUNCTION_RANGE = "hp3457aSetFunctionRange";
  public final static String ICARG_HP3457A_SET_FUNCTION_RESOLUTION = "hp3457aSetFunctionResolution";

  // ID?
  public final static String IC_HP3457A_GET_ID = "commandHp3457aGetId";

  // INBUF
  public final static String IC_HP3457A_SET_GPIB_INPUT_BUFFERING = "commandHp3457aSetGpibInputBuffering";
  public final static String ICARG_HP3457A_SET_GPIB_INPUT_BUFFERING = "hp3457aSetGpibInputBuffering";

  // ISCALE?
  public final static String IC_HP3457A_GET_INTEGER_SCALE_FACTOR = "commandHp3457aGetIntegerScaleFactor";

  // LFREQ
  public final static String IC_HP3457A_SET_LINE_FREQUENCY_REFERENCE = "commandHp3457aSetLineFrequencyReference";
  public final static String ICARG_HP3457A_SET_LINE_FREQUENCY_REFERENCE = "hp3457aSetLineFrequencyReference";

  // LFREQ?
  public final static String IC_HP3457A_GET_LINE_FREQUENCY_REFERENCE = "commandHp3457aGetLineFrequencyReference";

  // LINE?
  public final static String IC_HP3457A_GET_LINE_FREQUENCY = "commandHp3457aGetLineFrequency";

  // LOCK
  public final static String IC_HP3457A_SET_LOCKOUT = "commandHp3457aSetLockout";
  public final static String ICARG_HP3457A_SET_LOCKOUT = "hp3457aSetLockout";

  // MATH
  public final static String IC_HP3457A_SET_MATH_OPERATION = "commandHp3457aSetMathOperation";
  public final static String ICARG_HP3457A_SET_MATH_OPERATION_A = "hp3457aSetMathOperationA";
  public final static String ICARG_HP3457A_SET_MATH_OPERATION_B = "hp3457aSetMathOperationB";

  // MATH?
  public final static String IC_HP3457A_GET_MATH_OPERATION = "commandHp3457aGetMathOperation";

  // MCOUNT?
  public final static String IC_HP3457A_GET_NUMBER_OF_STORED_READINGS = "commandHp3457aGetNumberOfStoredReadings";

  // MEM
  public final static String IC_HP3457A_SET_READING_MEMORY_CONTROL = "commandHp3457aSetReadingMemoryControl";
  public final static String ICARG_HP3457A_SET_READING_MEMORY_CONTROL = "hp3457aSetReadingMemoryControl";

  // MFORMAT
  public final static String IC_HP3457A_FORMAT_READING_MEMORY = "commandHp3457aFormatReadingMemory";
  public final static String ICARG_HP3457A_FORMAT_READING_MEMORY = "hp3457aFormatReadingMemory";

  // MSIZE
  public final static String IC_HP3457A_SET_MEMORY_SIZES = "commandHp3457aSetMemorySizes";
  public final static String ICARG_HP3457A_SET_MEMORY_SIZES_READINGS = "hp3457aSetMemorySizesReadings";
  public final static String ICARG_HP3457A_SET_MEMORY_SIZES_SUBPROGRAMS = "hp3457aSetMemorySizesSubprograms";

  // MSIZE?
  public final static String IC_HP3457A_GET_MEMORY_SIZES = "commandHp3457aGetMemorySizes";

  // NDIG
  public final static String IC_HP3457A_SET_NUMBER_OF_DIGITS = "commandHp3457aSetNumberOfDigits";
  public final static String ICARG_HP3457A_SET_NUMBER_OF_DIGITS = "hp3457aSetNumberOfDigits";

  // NPLC
  public final static String IC_HP3457A_SET_ADC_NUMBER_OF_PLCS = "commandHp3457aSetAdcNumberOfPLCs";
  public final static String ICARG_HP3457A_SET_ADC_NUMBER_OF_PLCS = "hp3457aSetAdcNumberOfPLCs";

  // NPLC?
  public final static String IC_HP3457A_GET_ADC_NUMBER_OF_PLCS = "commandHp3457aGetAdcNumberOfPLCs";

  // NRDGS
  public final static String IC_HP3457A_SET_NUMBER_OF_READINGS = "commandHp3457aSetNumberOfReadings";
  public final static String ICARG_HP3457A_SET_NUMBER_OF_READINGS_COUNT = "hp3457aSetNumberOfReadingsCount";
  public final static String ICARG_HP3457A_SET_NUMBER_OF_READINGS_EVENT = "hp3457aSetNumberOfReadingsEvent";

  // NRDGS?
  public final static String IC_HP3457A_GET_NUMBER_OF_READINGS = "commandHp3457aGetNumberOfReadings";

  // OCOMP
  public final static String IC_HP3457A_SET_OFFSET_COMPENSATION = "commandHp3457aSetOffsetCompensation";
  public final static String ICARG_HP3457A_SET_OFFSET_COMPENSATION = "hp3457aSetOffsetCompensation";

  // OCOMP?
  public final static String IC_HP3457A_GET_OFFSET_COMPENSATION = "commandHp3457aGetOffsetCompensation";

  // OFORMAT
  public final static String IC_HP3457A_SET_OUTPUT_FORMAT = "commandHp3457aSetOutputFormat";
  public final static String ICARG_HP3457A_SET_OUTPUT_FORMAT = "hp3457aSetOutputFormat";

  // OHM
  public final static String IC_HP3457A_OHMS = "commandHp3457aOhms";
  public final static String ICARG_HP3457A_OHMS_RANGE = "hp3457aOhmsRange";
  public final static String ICARG_HP3457A_OHMS_RESOLUTION = "hp3457aOhmsResolution";

  // OHMF
  public final static String IC_HP3457A_4WIRE_OHMS = "commandHp3457a4WireOhms";
  public final static String ICARG_HP3457A_4WIRE_OHMS_RANGE = "hp3457a4WireOhmsRange";
  public final static String ICARG_HP3457A_4WIRE_OHMS_RESOLUTION = "hp3457a4WireOhmsResolution";

  // OPEN
  public final static String IC_HP3457A_OPEN_ACTUATOR_CHANNEL = "commandHp3457aOpenActuatorChannel";
  public final static String ICARG_HP3457A_OPEN_ACTUATOR_CHANNEL_CHANNEL = "hp3457aOpenActuatorChannelChannel";
  public final static String ICARG_HP3457A_OPEN_ACTUATOR_CHANNEL_CONTROL = "hp3457aOpenActuatorChannelControl";

  // OPT?
  public final static String IC_HP3457A_GET_INSTALLED_OPTION = "commandHp3457aGetInstalledOption";

  // PAUSE
  public final static String IC_HP3457A_PAUSE_SUBPROGRAM = "commandHp3457aPauseSubprogram";

  // PER
  public final static String IC_HP3457A_PERIOD = "commandHp3457aPeriod";
  public final static String ICARG_HP3457A_PERIOD_AMPLITUDE_RANGE = "hp3457aPeriodAmplitudeRange";
  
  // PRESET
  public final static String IC_HP3457A_SET_PRESET_STATE = "commandHp3457aSetPresetState";
  
  // R
  // RANGE
  public final static String IC_HP3457A_SET_RANGE = "commandHp3457aSetRange";
  public final static String ICARG_HP3457A_SET_RANGE_RANGE = "hp3457aSetRangeRange";
  public final static String ICARG_HP3457A_SET_RANGE_RESOLUTION = "hp3457aSetRangeResolution";
  
  // RANGE?
  public final static String IC_HP3457A_GET_RANGE = "commandHp3457aGetRange";
  
  // RESET
  public final static String IC_HP3457A_RESET = "commandHp3457aReset";
  
  // REV?
  public final static String IC_HP3457A_GET_REVISION = "commandHp3457aGetRevision";
  
  // RMATH - XXX is a "get operation"?
  public final static String IC_HP3457A_RECALL_MATH = "commandHp3457aRecallMath";
  public final static String ICARG_HP3457A_RECALL_MATH = "hp3457aRecallMath";
  
  // RMEM
  public final static String IC_HP3457A_RECALL_MEMORY = "commandHp3457aRecallMemory";
  public final static String ICARG_HP3457A_RECALL_MEMORY_FIRST = "hp3457aRecallMemoryFirst";
  public final static String ICARG_HP3457A_RECALL_MEMORY_COUNT = "hp3457aRecallMemoryCount";
  public final static String ICARG_HP3457A_RECALL_MEMORY_RECORD = "hp3457aRecallMemoryRecord";
  
  // RQS
  public final static String IC_HP3457A_SET_SERVICE_REQUEST_MASK = "commandHp3457aSetServiceRequestMask";
  public final static String ICARG_HP3457A_SET_SERVICE_REQUEST_MASK = "hp3457aSetServiceRequestMask";
  
  // RSTATE
  public final static String IC_HP3457A_RECALL_STATE = "commandHp3457aRecallState";
  public final static String ICARG_HP3457A_RECALL_STATE = "hp3457aRecallState";
  
  // SADV
  public final static String IC_HP3457A_SCAN_ADVANCE = "commandHp3457aScanAdvance";
  public final static String ICARG_HP3457A_SCAN_ADVANCE = "hp3457aScanAdvance";
  
  // SCRATCH
  public final static String IC_HP3457A_CLEAR_ALL_SUBPROGRAMS = "commandHp3457aClearAllSubprograms";
  
  // SECURE
  public final static String IC_HP3457A_SECURE = "commandHp3457aSecure";
  
  // SLIST
  public final static String IC_HP3457A_SET_SCAN_LIST = "commandHp3457aSetScanList";
  public final static String ICARG_HP3457A_SET_SCAN_LIST = "hp3457aSetScanList";
  
  // SLIST?
  public final static String IC_HP3457A_GET_SCAN_LIST_SIZE = "commandHp3457aGetScanListSize";
  
  // SMATH
  public final static String IC_HP3457A_STORE_MATH = "commandHp3457aStoreMath";
  public final static String ICARG_HP3457A_STORE_MATH_REGISTER = "hp3457aStoreMathRegister";
  public final static String ICARG_HP3457A_STORE_MATH_NUMBER = "hp3457aStoreMathNumber";
  
  // SRQ
  public final static String IC_HP3457A_FIRE_SERVICE_REQUEST = "commandHp3457aFireServiceRequest";
  
  // SSTATE
  public final static String IC_HP3457A_STORE_STATE = "commandHp3457aStoreState";
  public final static String ICARG_HP3457A_STORE_STATE = "hp3457aStoreState";
  
  // STB?
  public final static String IC_HP3457A_GET_STATUS_BYTE = "commandHp3457aGetStatusByte";
  
  // SUB
  public final static String IC_HP3457A_STORE_SUBPROGRAM = "commandHp3457aStoreSubprogram";
  public final static String ICARG_HP3457A_STORE_SUBPROGRAM = "hp3457aStoreSubprogram";
  
  // SUBEND
  public final static String IC_HP3457A_SUBPROGRAM_END = "commandHp3457aSubprogramEnd";
  
  // T -> TRIG
  
  // TARM
  public final static String IC_HP3457A_SET_TRIGGER_ARM = "commandHp3457aSetTriggerArm";
  public final static String ICARG_HP3457A_SET_TRIGGER_ARM_EVENT = "hp3457aSetTriggerArmEvent";
  public final static String ICARG_HP3457A_SET_TRIGGER_ARM_NR_ARMS = "hp3457aSetTriggerArmNrArms";
  
  // TARM?
  public final static String IC_HP3457A_GET_TRIGGER_ARM = "commandHp3457aGetTriggerArm";
  
  // TBUFF
  public final static String IC_HP3457A_SET_TRIGGER_BUFFERING = "commandHp3457aSetTriggerBufffering";
  public final static String ICARG_HP3457A_SET_TRIGGER_BUFFERING = "hp3457aSetTriggerBufffering";
  
  // TERM
  public final static String IC_HP3457A_SET_MEASUREMENT_TERMINALS = "commandHp3457aSetMeasurementTerminals";
  public final static String ICARG_HP3457A_SET_MEASUREMENT_TERMINALS = "hp3457aSetMeasurementTerminals";
  
  // TERM?
  public final static String IC_HP3457A_GET_MEASUREMENT_TERMINALS = "commandHp3457aGetMeasurementTerminals";
  
  // TEST
  public final static String IC_HP3457A_INTERNAL_TEST = "commandHp3457aInternalTest";
  
  // TIMER
  public final static String IC_HP3457A_SET_TIME_INTERVAL_FOR_NRDGS = "commandHp3457aSetTimeIntervalForNRDGS";
  public final static String ICARG_HP3457A_SET_TIME_INTERVAL_FOR_NRDGS = "hp3457aSetTimeIntervalForNRDGS";
  
  // TIMER?
  public final static String IC_HP3457A_GET_TIME_INTERVAL_FOR_NRDGS = "commandHp3457aGetTimeIntervalForNRDGS";
  
  // TONE
  public final static String IC_HP3457A_PLAY_TONE = "commandHp3457aPlayTone";
  public final static String ICARG_HP3457A_PLAY_TONE_FREQUENCY = "hp3457aPlayToneFrequency";
  public final static String ICARG_HP3457A_PLAY_TONE_DURATION = "hp3457aPlayToneDuration";
  
  // TRIG
  public final static String IC_HP3457A_SET_TRIGGER_EVENT = "commandHp3457aSetTriggerEvent";
  public final static String ICARG_HP3457A_SET_TRIGGER_EVENT = "hp3457aSetTriggerEvent";
  
  // TRIG?
  public final static String IC_HP3457A_GET_TRIGGER_EVENT = "commandHp3457aGetTriggerEvent";
  
  // ?
  public final static String IC_HP3457A_TRIGGER = "commandHp3457aTrigger";

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UPSUPPORTED COMMANDS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static String ICARG_HP3457A_PEEK_POKE_ADDRESS = "hp3457aPeekPokeAddress";

  public final static String IC_HP3457A_PEEK = "commandHp3457aPeek";
  public final static String ICARG_HP3457A_PEEK_ADDRESS = ICARG_HP3457A_PEEK_POKE_ADDRESS;

  public final static String IC_HP3457A_POKE = "commandHp3457aPoke";
  public final static String ICARG_HP3457A_POKE_ADDRESS = ICARG_HP3457A_PEEK_POKE_ADDRESS;
  public final static String ICARG_HP3457A_POKE_DATA = "hp3457aPokeData";

  public final static String IC_HP3457A_READ_CALIBRATION_DATA = "commandHp3457aReadCalibrationData";

  public final static String IC_HP3457A_WRITE_CALIBRATION_DATA = "commandHp3457aWriteCalibrationData";
  public final static String ICARG_HP3457A_WRITE_CALIBRATION_DATA = "hp3478aWriteCalibrationData";

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
