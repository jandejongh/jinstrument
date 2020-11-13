/*
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>, TNO.
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
package org.javajdj.jinstrument.gpib.dso.tek2440;

import org.javajdj.jinstrument.*;

/** A command for a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface Tek2440_InstrumentCommand
  extends InstrumentCommand
{
  
  public final static String ICARG_TEK2440_REFERENCE_NUMBER = "tek2440ReferenceNumber";
  
  public final static String IC_TEK2440_REFERENCE_POSITION_MODE = "commandTek2440ReferencePositionMode";
  public final static String ICARG_TEK2440_REFERENCE_POSITION_MODE = "tek2440ReferencePositionMode";
  
  public final static String IC_TEK2440_REFERENCE_POSITION = "commandTek2440ReferencePosition";
  public final static String ICARG_TEK2440_REFERENCE_POSITION_NUMBER = ICARG_TEK2440_REFERENCE_NUMBER;
  public final static String ICARG_TEK2440_REFERENCE_POSITION = "tek2440ReferencePosition";
  
  public final static String IC_TEK2440_REFERENCE_SOURCE = "commandTek2440ReferenceSource";
  public final static String ICARG_TEK2440_REFERENCE_SOURCE = "tek2440ReferenceSource";

  public final static String IC_TEK2440_REFERENCE_CLEAR = "commandTek2440ReferenceClear";
  public final static String ICARG_TEK2440_REFERENCE_CLEAR_NUMBER = ICARG_TEK2440_REFERENCE_NUMBER;

  public final static String IC_TEK2440_REFERENCE_WRITE = "commandTek2440ReferenceWrite";
  public final static String ICARG_TEK2440_REFERENCE_WRITE_NUMBER = ICARG_TEK2440_REFERENCE_NUMBER; // 0 == STACK.

  public final static String IC_TEK2440_REFERENCE_DISPLAY = "commandTek2440ReferenceDisplay";
  public final static String ICARG_TEK2440_REFERENCE_DISPLAY_NUMBER = ICARG_TEK2440_REFERENCE_NUMBER;
  public final static String ICARG_TEK2440_REFERENCE_DISPLAY = "tek2440ReferenceDisplay";

  public final static String IC_TEK2440_LONG_RESPONSE = "commandTek2440LongResponse";
  public final static String ICARG_TEK2440_LONG_RESPONSE = "tek2440LongResponse";

  public final static String IC_TEK2440_USE_PATH = "commandTek2440UsePath";
  public final static String ICARG_TEK2440_USE_PATH = "tek2440UsePath";

  public final static String IC_TEK2440_DELAY_EVENTS_MODE = "commandTek2440DelayEventsMode";
  public final static String ICARG_TEK2440_DELAY_EVENTS_MODE = "tek2440DelayEventsMode";

  public final static String IC_TEK2440_DELAY_EVENTS = "commandTek2440DelayEvents";
  public final static String ICARG_TEK2440_DELAY_EVENTS = "tek2440DelayEvents";

  public final static String IC_TEK2440_DELAY_TIMES_DELTA = "commandTek2440DelayTimesDelta";
  public final static String ICARG_TEK2440_DELAY_TIMES_DELTA = "tek2440DelayTimesDelta";

  public final static String IC_TEK2440_DELAY_TIME = "commandTek2440DelayTime";
  public final static String ICARG_TEK2440_DELAY_TIME_TARGET = "tek2440DelayTimeTarget";
  public final static String ICARG_TEK2440_DELAY_TIME = "tek2440DelayTime";

  public final static String IC_TEK2440_EXT_GAIN = "commandTek2440ExtGain";
  public final static String ICARG_TEK2440_EXT_GAIN_TARGET = "tek2440ExtGainTarget";
  public final static String ICARG_TEK2440_EXT_GAIN = "tek2440ExtGain";

  public final static String IC_TEK2440_WORD_CLOCK = "commandTek2440WordClock";
  public final static String ICARG_TEK2440_WORD_CLOCK = "tek2440WordClock";

  public final static String IC_TEK2440_WORD_RADIX = "commandTek2440WordRadix";
  public final static String ICARG_TEK2440_WORD_RADIX = "tek2440WordRadix";
  
  public final static String IC_TEK2440_WORD = "commandTek2440Word";
  public final static String ICARG_TEK2440_WORD = "tek2440Word";
  
  public final static String IC_TEK2440_DISPLAY_VECTORS = "commandTek2440DisplayVectors";
  public final static String ICARG_TEK2440_DISPLAY_VECTORS = "tek2440DisplayVectors";
  
  public final static String IC_TEK2440_DISPLAY_READOUT = "commandTek2440DisplayReadout";
  public final static String ICARG_TEK2440_DISPLAY_READOUT = "tek2440DisplayReadout";
  
  public final static String IC_TEK2440_BANDWIDTH_LIMIT = "commandTek2440BandwidthLimit";
  public final static String ICARG_TEK2440_BANDWIDTH_LIMIT = "tek2440BandwidthLimit";
  
  public final static String IC_TEK2440_ENABLE_SRQ_INTERNAL_ERROR = "commandTek2440EnableSrqInternalError";
  public final static String ICARG_TEK2440_ENABLE_SRQ_INTERNAL_ERROR = "tek2440EnableSrqInternalError";
  
  public final static String IC_TEK2440_ENABLE_SRQ_COMMAND_ERROR = "commandTek2440EnableSrqCommandError";
  public final static String ICARG_TEK2440_ENABLE_SRQ_COMMAND_ERROR = "tek2440EnableSrqCommandError";
  
  public final static String IC_TEK2440_ENABLE_SRQ_EXECUTION_ERROR = "commandTek2440EnableSrqExecutionError";
  public final static String ICARG_TEK2440_ENABLE_SRQ_EXECUTION_ERROR = "tek2440EnableSrqExecutionError";
  
  public final static String IC_TEK2440_ENABLE_SRQ_EXECUTION_WARNING = "commandTek2440EnableSrqExecutionWarning";
  public final static String ICARG_TEK2440_ENABLE_SRQ_EXECUTION_WARNING = "tek2440EnableSrqExecutionWarning";
  
  public final static String IC_TEK2440_ENABLE_SRQ_COMMAND_COMPLETION = "commandTek2440EnableSrqCommandCompletion";
  public final static String ICARG_TEK2440_ENABLE_SRQ_COMMAND_COMPLETION = "tek2440EnableSrqCommandCompletion";
  
  public final static String IC_TEK2440_ENABLE_SRQ_ON_EVENT = "commandTek2440EnableSrqOnEvent";
  public final static String ICARG_TEK2440_ENABLE_SRQ_ON_EVENT = "tek2440EnableSrqOnEvent";
  
  public final static String IC_TEK2440_ENABLE_SRQ_DEVICE_DEPENDENT = "commandTek2440EnableSrqDeviceDependent";
  public final static String ICARG_TEK2440_ENABLE_SRQ_DEVICE_DEPENDENT = "tek2440EnableSrqDeviceDependent";
  
  public final static String IC_TEK2440_ENABLE_SRQ_ON_USER_BUTTON = "commandTek2440EnableSrqOnUserButton";
  public final static String ICARG_TEK2440_ENABLE_SRQ_ON_USER_BUTTON = "tek2440EnableSrqOnUserButton";
  
  public final static String IC_TEK2440_ENABLE_SRQ_ON_PROBE_IDENTIFY_BUTTON = "commandTek2440EnableSrqOnProbeIdentifyButton";
  public final static String ICARG_TEK2440_ENABLE_SRQ_ON_PROBE_IDENTIFY_BUTTON = "tek2440EnableSrqOnProbeIdentifyButton";
  
  public final static String IC_TEK2440_GROUP_EXECUTE_TRIGGER_MODE = "commandTek2440GroupExecuteTriggerMode";
  public final static String ICARG_TEK2440_GROUP_EXECUTE_TRIGGER_MODE = "tek2440GroupExecuteTriggerMode";
  public final static String ICARG_TEK2440_GROUP_EXECUTE_TRIGGER_MODE_USER_SEQUENCE = "tek2440GroupExecuteTriggerModeUserSequence";
  
}
