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
  
  public final static String ICARG_TEK2440_CHANNEL = "tek2440Channel";
  
  public final static String IC_TEK2440_HORIZONTAL_MODE = "commandTek2440HorizontalMode";
  public final static String ICARG_TEK2440_HORIZONTAL_MODE = "tek2440HorizontalMode";
  
  public final static String IC_TEK2440_TIMEBASE = "commandTek2440Timebase";
  public final static String ICARG_TEK2440_TIMEBASE_CHANNEL = ICARG_TEK2440_CHANNEL;
  public final static String ICARG_TEK2440_TIMEBASE = "tek2440Timebase";
  
  public final static String IC_TEK2440_HORIZONTAL_POSITION = "commandTek2440HorizontalPosition";
  public final static String ICARG_TEK2440_HORIZONTAL_POSITION = "tek2440HorizontalPosition";
  
  public final static String IC_TEK2440_HORIZONTAL_EXTERNAL_EXPANSION = "commandTek2440HorizontalExternalExpansion";
  public final static String ICARG_TEK2440_HORIZONTAL_EXTERNAL_EXPANSION = "tek2440HorizontalExternalExpansion";
  
  public final static String IC_TEK2440_CHANNEL_ENABLE = "commandTek2440ChannelEnable";
  public final static String ICARG_TEK2440_CHANNEL_ENABLE_CHANNEL = ICARG_TEK2440_CHANNEL;
  public final static String ICARG_TEK2440_CHANNEL_ENABLE = "tek2440ChannelEnable";
  
  public final static String IC_TEK2440_ADD_ENABLE = "commandTek2440AddEnable";
  public final static String ICARG_TEK2440_ADD_ENABLE = "tek2440AddEnable";
  
  public final static String IC_TEK2440_MULT_ENABLE = "commandTek2440MultEnable";
  public final static String ICARG_TEK2440_MULT_ENABLE = "tek2440MultEnable";
  
  public final static String IC_TEK2440_VERTICAL_DISPLAY_MODE = "commandTek2440VerticalDisplayMode";
  public final static String ICARG_TEK2440_VERTICAL_DISPLAY_MODE = "tek2440VerticalDisplayMode";
  
  public final static String IC_TEK2440_VOLTS_PER_DIV = "commandTek2440VoltsPerDiv";
  public final static String ICARG_TEK2440_VOLTS_PER_DIV_CHANNEL = ICARG_TEK2440_CHANNEL;
  public final static String ICARG_TEK2440_VOLTS_PER_DIV = "tek2440VoltsPerDiv";
  
  public final static String IC_TEK2440_CHANNEL_VARIABLE_Y = "commandTek2440ChannelVariableY";
  public final static String ICARG_TEK2440_CHANNEL_VARIABLE_Y_CHANNEL = ICARG_TEK2440_CHANNEL;
  public final static String ICARG_TEK2440_CHANNEL_VARIABLE_Y = "tek2440ChannelVariableY";
  
  public final static String IC_TEK2440_CHANNEL_COUPLING = "commandTek2440ChannelCoupling";
  public final static String ICARG_TEK2440_CHANNEL_COUPLING_CHANNEL = ICARG_TEK2440_CHANNEL;
  public final static String ICARG_TEK2440_CHANNEL_COUPLING = "tek2440ChannelCoupling";
  
  public final static String IC_TEK2440_CHANNEL_50_OHMS = "commandTek2440Channel50Ohms";
  public final static String ICARG_TEK2440_CHANNEL_50_OHMS_CHANNEL = ICARG_TEK2440_CHANNEL;
  public final static String ICARG_TEK2440_CHANNEL_50_OHMS = "tek2440Channel50Ohms";
  
  public final static String IC_TEK2440_CHANNEL_INVERT = "commandTek2440ChannelInvert";
  public final static String ICARG_TEK2440_CHANNEL_INVERT_CHANNEL = ICARG_TEK2440_CHANNEL;
  public final static String ICARG_TEK2440_CHANNEL_INVERT = "tek2440ChannelInvert";
  
  public final static String IC_TEK2440_CHANNEL_POSITION = "commandTek2440ChannelPosition";
  public final static String ICARG_TEK2440_CHANNEL_POSITION_CHANNEL = ICARG_TEK2440_CHANNEL;
  public final static String ICARG_TEK2440_CHANNEL_POSITION = "tek2440ChannelPosition";
  
  public final static String IC_TEK2440_DISPLAY_INTENSITY = "commandTek2440DisplayIntensity";
  public final static String ICARG_TEK2440_DISPLAY_INTENSITY_COMPONENT = "displayIntensityComponent";
  public final static String ICARG_TEK2440_DISPLAY_INTENSITY = "tek2440DisplayIntensity";

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
  
  public final static String IC_TEK2440_AUTO_SETUP_MODE = "commandTek2440AutoSetupMode";
  public final static String ICARG_TEK2440_AUTO_SETUP_MODE = "tek2440AutoSetupMode";
  
  public final static String IC_TEK2440_AUTO_SETUP_RESOLUTION = "commandTek2440AutoSetupResolution";
  public final static String ICARG_TEK2440_AUTO_SETUP_RESOLUTION = "tek2440AutoSetupResolution";
  
  public final static String IC_TEK2440_AUTO_SETUP = "commandTek2440AutoSetup";
  
  public final static String IC_TEK2440_ACQUISITION_MODE = "commandTek2440AcquisitionMode";
  public final static String ICARG_TEK2440_ACQUISITION_MODE = "tek2440AcquisitionMode";
  
  public final static String IC_TEK2440_ACQUISITION_REPETITIVE = "commandTek2440AcquisitionRepetitive";
  public final static String ICARG_TEK2440_ACQUISITION_REPETITIVE = "tek2440AcquisitionRepetitive";
  
  public final static String IC_TEK2440_ACQUISITION_NR_AVERAGED = "commandTek2440AcquisitionNrAveraged";
  public final static String ICARG_TEK2440_ACQUISITION_NR_AVERAGED = "tek2440AcquisitionNrAveraged";
  
  public final static String IC_TEK2440_ACQUISITION_NR_ENV_SWEEPS = "commandTek2440AcquisitionNrEnvSweeps";
  public final static String ICARG_TEK2440_ACQUISITION_NR_ENV_SWEEPS = "tek2440AcquisitionNrEnvSweeps";
  
  public final static String IC_TEK2440_ACQUISITION_SAVE_ON_DELTA = "commandTek2440AcquisitionSaveOnDelta";
  public final static String ICARG_TEK2440_ACQUISITION_SAVE_ON_DELTA = "tek2440AcquisitionSaveOnDelta";
  
  public final static String IC_TEK2440_RUN_MODE = "commandTek2440RunMode";
  public final static String ICARG_TEK2440_RUN_MODE = "tek2440RunMode";
  
  public final static String IC_TEK2440_SMOOTHING = "commandTek2440Smoothing";
  public final static String ICARG_TEK2440_SMOOTHING = "tek2440Smoothing";
  
  public final static String IC_TEK2440_A_TRIGGER_MODE = "commandTek2440ATriggerMode";
  public final static String ICARG_TEK2440_A_TRIGGER_MODE = "tek2440ATriggerMode";
  
  public final static String IC_TEK2440_A_TRIGGER_SOURCE = "commandTek2440ATriggerSource";
  public final static String ICARG_TEK2440_A_TRIGGER_SOURCE = "tek2440ATriggerSource";
  
  public final static String IC_TEK2440_A_TRIGGER_COUPLING = "commandTek2440ATriggerCoupling";
  public final static String ICARG_TEK2440_A_TRIGGER_COUPLING = "tek2440ATriggerCoupling";
  
  public final static String IC_TEK2440_A_TRIGGER_SLOPE = "commandTek2440ATriggerSlope";
  public final static String ICARG_TEK2440_A_TRIGGER_SLOPE = "tek2440ATriggerSlope";
  
  public final static String IC_TEK2440_A_TRIGGER_LEVEL = "commandTek2440ATriggerLevel";
  public final static String ICARG_TEK2440_A_TRIGGER_LEVEL = "tek2440ATriggerLevel";
  
  public final static String IC_TEK2440_A_TRIGGER_POSITION = "commandTek2440ATriggerPosition";
  public final static String ICARG_TEK2440_A_TRIGGER_POSITION = "tek2440ATriggerPosition";
  
  public final static String IC_TEK2440_A_TRIGGER_HOLDOFF = "commandTek2440ATriggerHoldoff";
  public final static String ICARG_TEK2440_A_TRIGGER_HOLDOFF = "tek2440ATriggerHoldoff";
  
  public final static String IC_TEK2440_A_TRIGGER_LOG_SOURCE = "commandTek2440ATriggerLogSource";
  public final static String ICARG_TEK2440_A_TRIGGER_LOG_SOURCE = "tek2440ATriggerLogSource";
  
  public final static String IC_TEK2440_TRIGGER_A_B_SELECT = "commandTek2440TriggerABSelect";
  public final static String ICARG_TEK2440_TRIGGER_A_B_SELECT = "tek2440TriggerABSelect";
  
  public final static String IC_TEK2440_B_TRIGGER_MODE = "commandTek2440BTriggerMode";
  public final static String ICARG_TEK2440_B_TRIGGER_MODE = "tek2440BTriggerMode";
  
  public final static String IC_TEK2440_B_TRIGGER_SOURCE = "commandTek2440BTriggerSource";
  public final static String ICARG_TEK2440_B_TRIGGER_SOURCE = "tek2440BTriggerSource";
  
  public final static String IC_TEK2440_B_TRIGGER_COUPLING = "commandTek2440BTriggerCoupling";
  public final static String ICARG_TEK2440_B_TRIGGER_COUPLING = "tek2440BTriggerCoupling";
  
  public final static String IC_TEK2440_B_TRIGGER_SLOPE = "commandTek2440BTriggerSlope";
  public final static String ICARG_TEK2440_B_TRIGGER_SLOPE = "tek2440BTriggerSlope";
  
  public final static String IC_TEK2440_B_TRIGGER_LEVEL = "commandTek2440BTriggerLevel";
  public final static String ICARG_TEK2440_B_TRIGGER_LEVEL = "tek2440BTriggerLevel";
  
  public final static String IC_TEK2440_B_TRIGGER_POSITION = "commandTek2440BTriggerPosition";
  public final static String ICARG_TEK2440_B_TRIGGER_POSITION = "tek2440BTriggerPosition";
  
  public final static String IC_TEK2440_B_TRIGGER_EXTERNAL_CLOCK = "commandTek2440BTriggerExternalClock";
  public final static String ICARG_TEK2440_B_TRIGGER_EXTERNAL_CLOCK = "tek2440BTriggerExternalClock";
  
  public final static String IC_TEK2440_ENABLE_DEBUG = "commandTek2440EnableDebug";
  public final static String ICARG_TEK2440_ENABLE_DEBUG = "tek2440EnableDebug";
  
  public final static String IC_TEK2440_SEQUENCER_ENABLE_FORMAT_CHARS = "commandTek2440SequencerEnableFormatChars";
  public final static String ICARG_TEK2440_SEQUENCER_ENABLE_FORMAT_CHARS = "tek2440SequencerEnableFormatChars";
  
  public final static String IC_TEK2440_SEQUENCER_FORCE = "commandTek2440SequencerForce";
  public final static String ICARG_TEK2440_SEQUENCER_FORCE = "tek2440SequencerForce";
  
  public final static String IC_TEK2440_SEQUENCER_ACTIONS_FROM_INT = "commandTek2440SequencerActionsFromInt";
  public final static String ICARG_TEK2440_SEQUENCER_ACTIONS_FROM_INT = "tek2440SequencerActionsFromInt";
  
}
