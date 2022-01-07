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
package org.javajdj.jinstrument.gpib.slm.rs_esh3;

import java.util.Objects;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultSelectiveLevelMeterSettings;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.SelectiveLevelMeterSettings;
import org.javajdj.junits.Unit;

/** Implementation of {@link InstrumentSettings}
 *  and {@link SelectiveLevelMeterSettings} for the Rohde{@code &}Schwarz ESH-3.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class RS_ESH3_GPIB_Settings
  extends DefaultSelectiveLevelMeterSettings
  implements SelectiveLevelMeterSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (RS_ESH3_GPIB_Settings.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected RS_ESH3_GPIB_Settings (
    final byte[] bytes,
    final Unit readingUnit,
    final double frequency_Hz,
    final double frequencyStart_Hz,
    final double frequencyStop_Hz,
    final AttenuationMode attenuationMode,
    final double rfAttenuation_dB,
    final boolean linearityTest,
    final double ifAttenuation_dB,
    final IfBandwidth ifBandwidth,
    final DemodulationMode demodulationMode,
    final boolean maxMinMode,
    final OperatingRange operatingRange,
    final OperatingMode operatingMode,
    final IndicatingMode indicatingMode,
    final DataOutputMode dataOutputMode,
    final double measurementTime_s,
    final double minimumLevel_dB,
    final double maximumLevel_dB,
    final StepSizeMode stepSizeMode,
    final double stepSize_MHz,
    final double stepSize_Percent,
    final boolean testLevel,
    final boolean testModulationDepth,
    final boolean testModulationDepthPositivePeak,
    final boolean testModulationDepthNegativePeak,
    final boolean testFrequencyOffset,
    final boolean testFrequencyDeviation,
    final boolean testFrequencyDeviationPositivePeak,
    final boolean testFrequencyDeviationNegativePeak,
    final FrequencyScanRepeatMode frequencyScanRepeatMode,
    final FrequencyScanSpeedMode frequencyScanSpeedMode,
    final FieldDataOuputMode fieldDataOuputMode,
    final boolean eoi,
    final boolean recorderCode,
    final boolean antennaProbeCode,
    final boolean srqOnDataReady,
    final boolean srqOnEnterInLocalState,
    final int gpibDelimiterInTalkerOperation,
    final String displayText,
    final RecorderXAxisMode recorderXAxisMode,
    final boolean recorderCodingEnabled_sf_62_63,
    final boolean noYTRecorder,
    final boolean noXYRecorder,
    final boolean noZSG3Recorder,
    final XYRecorderSpectrumMode xyRecorderSpectrumMode)
  {
    super (bytes, readingUnit);
    this.frequency_Hz = frequency_Hz;
    this.frequencyStart_Hz = frequencyStart_Hz;
    this.frequencyStop_Hz = frequencyStop_Hz;
    this.attenuationMode = attenuationMode;
    this.rfAttenuation_dB = rfAttenuation_dB;
    this.linearityTest = linearityTest;
    this.ifAttenuation_dB = ifAttenuation_dB;
    this.ifBandwidth = ifBandwidth;
    this.demodulationMode = demodulationMode;
    this.maxMinMode = maxMinMode;
    this.operatingRange = operatingRange;
    this.operatingMode = operatingMode;
    this.indicatingMode = indicatingMode;
    this.dataOutputMode = dataOutputMode;
    this.measurementTime_s = measurementTime_s;
    this.minimumLevel_dB = minimumLevel_dB;
    this.maximumLevel_dB = maximumLevel_dB;
    this.stepSizeMode = stepSizeMode;
    this.stepSize_MHz = stepSize_MHz;
    this.stepSize_Percent = stepSize_Percent;
    this.testLevel = testLevel;
    this.testModulationDepth = testModulationDepth;
    this.testModulationDepthPositivePeak = testModulationDepthPositivePeak;
    this.testModulationDepthNegativePeak = testModulationDepthNegativePeak;
    this.testFrequencyOffset = testFrequencyOffset;
    this.testFrequencyDeviation = testFrequencyDeviation;
    this.testFrequencyDeviationPositivePeak = testFrequencyDeviationPositivePeak;
    this.testFrequencyDeviationNegativePeak = testFrequencyDeviationNegativePeak;
    this.frequencyScanRepeatMode = frequencyScanRepeatMode;
    this.frequencyScanSpeedMode = frequencyScanSpeedMode;
    this.fieldDataOuputMode = fieldDataOuputMode;
    this.eoi = eoi;
    this.recorderCode = recorderCode;
    this.antennaProbeCode = antennaProbeCode;
    this.srqOnDataReady = srqOnDataReady;
    this.srqOnEnterInLocalState = srqOnEnterInLocalState;
    this.gpibDelimiterInTalkerOperation = gpibDelimiterInTalkerOperation;
    this.displayText = displayText;
    this.recorderXAxisMode = recorderXAxisMode;
    this.recorderCodingEnabled_sf_62_63 = recorderCodingEnabled_sf_62_63;
    this.noYTRecorder = noYTRecorder;
    this.noXYRecorder = noXYRecorder;
    this.noZSG3Recorder = noZSG3Recorder;
    this.xyRecorderSpectrumMode = xyRecorderSpectrumMode;
  }

  @Override
  public final RS_ESH3_GPIB_Settings clone ()
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public static RS_ESH3_GPIB_Settings powerOnSettings ()
  {
    return new RS_ESH3_GPIB_Settings (
      null,
      Unit.UNIT_NONE,
      1.0e6,
      1.0e6,
      1.0e6,
      AttenuationMode.AutoRangingLowDistortion,
      10.0,
      false,
      40.0,
      IfBandwidth.IF_BW_10KHz,
      DemodulationMode.A3,
      false,
      OperatingRange.Range20dB,
      OperatingMode.GeneratorOff,
      IndicatingMode.Average,
      DataOutputMode.dB,
      0.1,
      -140.0,
      200.0,
      StepSizeMode.Linear,
      0.1,
      10.0,
      true,
      false,
      false,
      false,
      false,
      false,
      false,
      false,
      FrequencyScanRepeatMode.SingleScan,
      FrequencyScanSpeedMode.Normal,
      FieldDataOuputMode.E,
      true,
      false,
      false,
      true,
      true,
      13,
      null,
      RecorderXAxisMode.Linear,
      false,
      true,
      true,
      true,
      XYRecorderSpectrumMode.PolygonalCurve);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double frequency_Hz;
  
  private final double frequencyStart_Hz;
  
  private final double frequencyStop_Hz;
  
  public final double getFrequency_Hz ()
  {
    return this.frequency_Hz;
  }
  
  public final double getFrequencyStart_Hz ()
  {
    return this.frequencyStart_Hz;
  }
  
  public final double getFrequencyStop_Hz ()
  {
    return this.frequencyStop_Hz;
  }

  public final RS_ESH3_GPIB_Settings withFrequency_Hz (final double frequency_Hz)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withFrequencyStart_Hz (final double frequencyStart_Hz)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withFrequencyStop_Hz (final double frequencyStop_Hz)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ATTENUATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum AttenuationMode
  {
    AutoRangingLowNoise      ("Auto [Noise]"),
    AutoRangingLowDistortion ("Auto [Dist]"),
    Manual                   ("Manual");
    
    AttenuationMode (final String string)
    {
      this.string = string;
    }
    
    private final String string;
    
    @Override
    public final String toString ()
    {
      return this.string;
    }
    
  }
  
  private final AttenuationMode attenuationMode;
  
  public final AttenuationMode getAttenuationMode ()
  {
    return this.attenuationMode;
  }

  private final double rfAttenuation_dB;
  
  public final double getRfAttenuation_dB ()
  {
    return this.rfAttenuation_dB;
  }

  private final boolean linearityTest;
  
  public final boolean getLinearityTest ()
  {
    return this.linearityTest;
  }
  
  public final boolean isLinearityTest ()
  {
    return getLinearityTest ();
  }
  
  private final double ifAttenuation_dB;
  
  public final double getIfAttenuation_dB ()
  {
    return this.ifAttenuation_dB;
  }
  
  public final RS_ESH3_GPIB_Settings withAttenuationMode (final AttenuationMode attenuationMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withRfAttenuation_dB (final double rfAttenuation_dB)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withLinearityTest (final boolean linearityTest)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withIfAttenuation_dB (final double ifAttenuation_dB)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // IF BANDWIDTH
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum IfBandwidth
  {
    
    IF_BW_10KHz  (10000, "10 kHz"),
    IF_BW_2400Hz ( 2400, "2400 Hz"),
    IF_BW_500Hz  (  500, "500 Hz"),
    IF_BW_200Hz  (  200, "200 Hz");
    
    private IfBandwidth (final double bandwidth_Hz, final String string)
    {
      this.bandwidth_Hz = bandwidth_Hz;
      this.string = string;
    }
    
    private final double bandwidth_Hz;
    
    public final double getBandwidth_Hz ()
    {
      return this.bandwidth_Hz;
    }
    
    private final String string;
    
    @Override
    public final String toString ()
    {
      return this.string;
    }
    
  }
  
  private final IfBandwidth ifBandwidth;
  
  public final IfBandwidth getIfBandwidth ()
  {
    return this.ifBandwidth;
  }
  
  public final RS_ESH3_GPIB_Settings withIfBandwidth (final IfBandwidth ifBandwidth)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEMODULATION MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum DemodulationMode
  {
    OFF,
    A0,
    A1,
    A3,
    A3J_LSB,
    A3J_USB,
    F3;
  }
  
  private final DemodulationMode demodulationMode;
  
  public final DemodulationMode getDemodulationMode ()
  {
    return this.demodulationMode;
  }
  
  public final RS_ESH3_GPIB_Settings withDemodulationMode (final DemodulationMode demodulationMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MAX/MIN MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean maxMinMode;
  
  public final boolean getMaxMinMode ()
  {
    return this.maxMinMode;
  }

  public final boolean isMaxMinMode ()
  {
    return getMaxMinMode ();
  }

  public final RS_ESH3_GPIB_Settings withMaxMinMode (final boolean maxMinMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // OPERATING RANGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum OperatingRange
  {
    
    Range20dB (20),
    Range40dB (40),
    Range60dB (60);
    
    private OperatingRange (final double range_dB)
    {
      this.range_dB = range_dB;
    }
    
    private final double range_dB;
    
    public final double getRange_dB ()
    {
      return this.range_dB;
    }
    
    @Override
    public final String toString ()
    {
      return Integer.toString ((int) this.range_dB) + " dB";
    }
    
  }
  
  private final OperatingRange operatingRange;
  
  public final OperatingRange getOperatingRange ()
  {
    return this.operatingRange;
  }

  public final RS_ESH3_GPIB_Settings withOperatingRange (final OperatingRange operatingRange)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // OPERATING MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum OperatingMode
  {
    
    GeneratorOff               ("Gen Off"),
    RemoteFrequencyMeasurement ("Rem Freq"),
    TwoPortMeasurement         ("Two Port");

    private OperatingMode (final String string)
    {
      this.string = string;
    }
    
    private final String string;
    
    @Override
    public final String toString ()
    {
      return this.string;
    }
    
  }
  
  private final OperatingMode operatingMode;
  
  public final OperatingMode getOperatingMode ()
  {
    return this.operatingMode;
  }

  public final RS_ESH3_GPIB_Settings withOperatingMode (final OperatingMode operatingMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INDICATING MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum IndicatingMode
  {
    
    Average,
    Peak,
    CISPR,
    MIL;
    
  }
  
  private final IndicatingMode indicatingMode;
  
  public final IndicatingMode getIndicatingMode ()
  {
    return this.indicatingMode;
  }

  public final RS_ESH3_GPIB_Settings withIndicatingMode (final IndicatingMode indicatingMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DATA OUTPUT MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum DataOutputMode
  {
    
    dB,
    dBm,
    V_A;
    
  }

  private final DataOutputMode dataOutputMode;
  
  public final DataOutputMode getDataOutputMode ()
  {
    return this.dataOutputMode;
  }

  public final RS_ESH3_GPIB_Settings withDataOutputMode (final DataOutputMode dataOutputMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MEASUREMENT TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double measurementTime_s;
  
  public final double getMeasurementTime_s ()
  {
    return this.measurementTime_s;
  }
  
  public final RS_ESH3_GPIB_Settings withMeasurementTime_s (final double measurementTime_s)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MINIMUM/MAXIMUM LEVEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double minimumLevel_dB;
  
  public final double getMinimumLevel_dB ()
  {
    return this.minimumLevel_dB;
  }
  
  private final double maximumLevel_dB;
  
  public final double getMaximumLevel_dB ()
  {
    return this.maximumLevel_dB;
  }
  
  public final RS_ESH3_GPIB_Settings withMinimumLevel_dB (final double minimumLevel_dB)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withMaximumLevel_dB (final double maximumLevel_dB)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STEP SIZE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum StepSizeMode
  {
    Linear,
    Logarithmic;
  }
  
  private final StepSizeMode stepSizeMode;
  
  public final StepSizeMode getStepSizeMode ()
  {
    return this.stepSizeMode;
  }
  
  private final double stepSize_MHz;
  
  public final double getStepSize_MHz ()
  {
    return this.stepSize_MHz;
  }
  
  private final double stepSize_Percent;
  
  public final double getStepSize_Percent ()
  {
    return this.stepSize_Percent;
  }
  
  public final RS_ESH3_GPIB_Settings withStepSizeMode (final StepSizeMode stepSizeMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withStepSize_MHz (final double stepSize_MHz)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withStepSize_Percent (final double stepSize_Percent)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TEST [MEASUREMENT]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean testLevel;
  
  public final boolean getTestLevel ()
  {
    return this.testLevel;
  }
  
  public final boolean isTestLevel ()
  {
    return getTestLevel ();
  }
  
  private final boolean testModulationDepth;
  
  public final boolean getTestModulationDepth ()
  {
    return this.testModulationDepth;
  }
  
  public final boolean isTestModulationDepth ()
  {
    return getTestModulationDepth ();
  }
  
  private final boolean testModulationDepthPositivePeak;
  
  public final boolean getTestModulationDepthPositivePeak ()
  {
    return this.testModulationDepthPositivePeak;
  }
  
  public final boolean isTestModulationDepthPositivePeak ()
  {
    return getTestModulationDepthPositivePeak ();
  }
  
  private final boolean testModulationDepthNegativePeak;

  public final boolean getTestModulationDepthNegativePeak ()
  {
    return this.testModulationDepthNegativePeak;
  }
  
  public final boolean isTestModulationDepthNegativePeak ()
  {
    return getTestModulationDepthNegativePeak ();
  }
  
  private final boolean testFrequencyOffset;
  
  public final boolean getTestFrequencyOffset ()
  {
    return this.testFrequencyOffset;
  }
  
  public final boolean isTestFrequencyOffset ()
  {
    return getTestFrequencyOffset ();
  }
  
  private final boolean testFrequencyDeviation;
  
  public final boolean getTestFrequencyDeviation ()
  {
    return this.testFrequencyDeviation;
  }
  
  public final boolean isTestFrequencyDeviation ()
  {
    return getTestFrequencyDeviation ();
  }
  
  private final boolean testFrequencyDeviationPositivePeak;
  
  public final boolean getTestFrequencyDeviationPositivePeak ()
  {
    return this.testFrequencyDeviationPositivePeak;
  }
  
  public final boolean isTestFrequencyDeviationPositivePeak ()
  {
    return getTestFrequencyDeviationPositivePeak ();
  }

  private final boolean testFrequencyDeviationNegativePeak;
  
  public final boolean getTestFrequencyDeviationNegativePeak ()
  {
    return this.testFrequencyDeviationNegativePeak;
  }
  
  public final boolean isTestFrequencyDeviationNegativePeak ()
  {
    return getTestFrequencyDeviationNegativePeak ();
  }

  public final RS_ESH3_GPIB_Settings withTestClearedThroughSf00 ()
  {
    // XXX Is this complete??
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      true,  // this.testLevel,
      false, // this.testModulationDepth,
      false, // this.testModulationDepthPositivePeak,
      false, // this.testModulationDepthNegativePeak,
      false, // this.testFrequencyOffset,
      false, // this.testFrequencyDeviation,
      false, // this.testFrequencyDeviationPositivePeak,
      false, // this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withTestLevel (final boolean testLevel)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withTestModulationDepth (final boolean testModulationDepth)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withTestModulationDepthPositivePeak (final boolean testModulationDepthPositivePeak)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withTestModulationDepthNegativePeak (final boolean testModulationDepthNegativePeak)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withTestFrequencyOffset (final boolean testFrequencyOffset)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withTestFrequencyDeviation (final boolean testFrequencyDeviation)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withTestFrequencyDeviationPositivePeak (final boolean testFrequencyDeviationPositivePeak)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withTestFrequencyDeviationNegativePeak (final boolean testFrequencyDeviationNegativePeak)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FREQUENCY SCAN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum FrequencyScanRepeatMode
  {
    SingleScan,
    AutoRepeat;
  }
  
  private final FrequencyScanRepeatMode frequencyScanRepeatMode;
  
  public final FrequencyScanRepeatMode getFrequencyScanRepeatMode ()
  {
    return this.frequencyScanRepeatMode;
  }
  
  public enum FrequencyScanSpeedMode
  {
    Normal,
    Fast;
  }
  
  private final FrequencyScanSpeedMode frequencyScanSpeedMode;
  
  public final FrequencyScanSpeedMode getFrequencyScanSpeedMode ()
  {
    return this.frequencyScanSpeedMode;
  }
  
  public final RS_ESH3_GPIB_Settings withFrequencyScanRepeatMode (final FrequencyScanRepeatMode frequencyScanRepeatMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withFrequencyScanSpeedMode (final FrequencyScanSpeedMode frequencyScanSpeedMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FIELD DATA OUTPUT MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum FieldDataOuputMode
  {
    E,
    H;
  }

  private final FieldDataOuputMode fieldDataOuputMode;
  
  public final FieldDataOuputMode getFieldDataOuputMode ()
  {
    return this.fieldDataOuputMode;
  }
  
  public final RS_ESH3_GPIB_Settings withFieldDataOutputMode (final FieldDataOuputMode fieldDataOuputMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EOI
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean eoi;
  
  public final boolean getEoi ()
  {
    return this.eoi;
  }

  public final boolean isEoi ()
  {
    return getEoi ();
  }

  public final RS_ESH3_GPIB_Settings withEoi (final boolean eoi)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RECORDER CODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean recorderCode;
  
  public final boolean getRecorderCode ()
  {
    return this.recorderCode;
  }

  public final boolean isRecorderCode ()
  {
    return getRecorderCode ();
  }
  
  public final RS_ESH3_GPIB_Settings withRecorderCode (final boolean recorderCode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ANTENNA PROBE CODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean antennaProbeCode;
  
  public final boolean getAntennaProbeCode ()
  {
    return this.antennaProbeCode;
  }

  public final boolean isAntennaProbeCode ()
  {
    return getAntennaProbeCode ();
  }

  public final RS_ESH3_GPIB_Settings withAntennaProbeCode (final boolean antennaProbeCode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SRQ
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean srqOnDataReady;
  
  public final boolean getSrqOnDataReady ()
  {
    return this.srqOnDataReady;
  }

  public final boolean isSrqOnDataReady ()
  {
    return getSrqOnDataReady ();
  }
  
  private final boolean srqOnEnterInLocalState;
  
  public final boolean getSrqOnEnterInLocalState ()
  {
    return this.srqOnEnterInLocalState;
  }

  public final boolean isSrqOnEnterInLocalState ()
  {
    return getSrqOnEnterInLocalState ();
  }
  
  public final RS_ESH3_GPIB_Settings withSrqOnDataReady (final boolean srqOnDataReady)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withSrqOnEnterInLocalState (final boolean srqOnEnterInLocalState)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GPIB DELIMITER IN TALKER OPERATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final int gpibDelimiterInTalkerOperation;
  
  public final int getGpibDelimiterInTalkerOperation ()
  {
    return this.gpibDelimiterInTalkerOperation;
  }
  
  public final RS_ESH3_GPIB_Settings withGpibDelimiterInTalkerOperation (final int gpibDelimiterInTalkerOperation)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAY TEXT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final String displayText;
  
  public final String getDisplayText ()
  {
    return this.displayText;
  }    
  
  public final RS_ESH3_GPIB_Settings withDisplayText (final String displayText)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RECORDER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum RecorderXAxisMode
  {
    Linear,
    Logarithmic;
  }
  
  private final RecorderXAxisMode recorderXAxisMode;
  
  public final RecorderXAxisMode getRecorderXAxisMode ()
  {
    return this.recorderXAxisMode;
  }
  
  private final boolean recorderCodingEnabled_sf_62_63;
  
  public final boolean getRecorderCodingEnabled_sf_62_63 ()
  {
    return this.recorderCodingEnabled_sf_62_63;
  }

  public final boolean isRecorderCodingEnabled_sf_62_63 ()
  {
    return getRecorderCodingEnabled_sf_62_63 ();
  }
  
  private final boolean noYTRecorder;
  
  public final boolean getNoYTRecorder ()
  {
    return this.noYTRecorder;
  }
  
  public final boolean isNoYTRecorder ()
  {
    return getNoYTRecorder ();
  }
  
  private final boolean noXYRecorder;
  
  public final boolean getNoXYRecorder ()
  {
    return this.noXYRecorder;
  }
  
  public final boolean isNoXYRecorder ()
  {
    return getNoXYRecorder ();
  }
  
  private final boolean noZSG3Recorder;
  
  public final boolean getNoZSG3Recorder ()
  {
    return this.noZSG3Recorder;
  }
  
  public final boolean isNoZSG3Recorder ()
  {
    return getNoZSG3Recorder ();
  }
  
  public enum XYRecorderSpectrumMode
  {
    PolygonalCurve,
    LineSpectrum;
  }
  
  private final XYRecorderSpectrumMode xyRecorderSpectrumMode;
  
  public final XYRecorderSpectrumMode getXYRecorderSpectrumMode ()
  {
    return this.xyRecorderSpectrumMode;
  }
  
  public final RS_ESH3_GPIB_Settings withRecorderXAxisMode (final RecorderXAxisMode recorderXAxisMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withRecorderCodingEnabled_sf_62_63 (final boolean recorderCodingEnabled_sf_62_63)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withNoYTRecorder (final boolean noYTRecorder)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withNoXYRecorder (final boolean noXYRecorder)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      noXYRecorder,
      this.noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withNoZSG3Recorder (final boolean noZSG3Recorder)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      noZSG3Recorder,
      this.xyRecorderSpectrumMode);
  }
  
  public final RS_ESH3_GPIB_Settings withXYRecorderSpectrumMode (final XYRecorderSpectrumMode xyRecorderSpectrumMode)
  {
    return new RS_ESH3_GPIB_Settings (
      getBytes (),
      getReadingUnit (),
      this.frequency_Hz,
      this.frequencyStart_Hz,
      this.frequencyStop_Hz,
      this.attenuationMode,
      this.rfAttenuation_dB,
      this.linearityTest,
      this.ifAttenuation_dB,
      this.ifBandwidth,
      this.demodulationMode,
      this.maxMinMode,
      this.operatingRange,
      this.operatingMode,
      this.indicatingMode,
      this.dataOutputMode,
      this.measurementTime_s,
      this.minimumLevel_dB,
      this.maximumLevel_dB,
      this.stepSizeMode,
      this.stepSize_MHz,
      this.stepSize_Percent,
      this.testLevel,
      this.testModulationDepth,
      this.testModulationDepthPositivePeak,
      this.testModulationDepthNegativePeak,
      this.testFrequencyOffset,
      this.testFrequencyDeviation,
      this.testFrequencyDeviationPositivePeak,
      this.testFrequencyDeviationNegativePeak,
      this.frequencyScanRepeatMode,
      this.frequencyScanSpeedMode,
      this.fieldDataOuputMode,
      this.eoi,
      this.recorderCode,
      this.antennaProbeCode,
      this.srqOnDataReady,
      this.srqOnEnterInLocalState,
      this.gpibDelimiterInTalkerOperation,
      this.displayText,
      this.recorderXAxisMode,
      this.recorderCodingEnabled_sf_62_63,
      this.noYTRecorder,
      this.noXYRecorder,
      this.noZSG3Recorder,
      xyRecorderSpectrumMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 61 * hash + (int) (Double.doubleToLongBits (this.frequency_Hz) ^ (Double.doubleToLongBits (this.frequency_Hz) >>> 32));
    hash = 61 * hash +
      (int) (Double.doubleToLongBits (this.frequencyStart_Hz) ^ (Double.doubleToLongBits (this.frequencyStart_Hz) >>> 32));
    hash = 61 * hash +
      (int) (Double.doubleToLongBits (this.frequencyStop_Hz) ^ (Double.doubleToLongBits (this.frequencyStop_Hz) >>> 32));
    hash = 61 * hash + Objects.hashCode (this.attenuationMode);
    hash = 61 * hash +
      (int) (Double.doubleToLongBits (this.rfAttenuation_dB) ^ (Double.doubleToLongBits (this.rfAttenuation_dB) >>> 32));
    hash = 61 * hash + (this.linearityTest ? 1 : 0);
    hash = 61 * hash +
      (int) (Double.doubleToLongBits (this.ifAttenuation_dB) ^ (Double.doubleToLongBits (this.ifAttenuation_dB) >>> 32));
    hash = 61 * hash + Objects.hashCode (this.ifBandwidth);
    hash = 61 * hash + Objects.hashCode (this.demodulationMode);
    hash = 61 * hash + (this.maxMinMode ? 1 : 0);
    hash = 61 * hash + Objects.hashCode (this.operatingRange);
    hash = 61 * hash + Objects.hashCode (this.operatingMode);
    hash = 61 * hash + Objects.hashCode (this.indicatingMode);
    hash = 61 * hash + Objects.hashCode (this.dataOutputMode);
    hash = 61 * hash +
      (int) (Double.doubleToLongBits (this.measurementTime_s) ^ (Double.doubleToLongBits (this.measurementTime_s) >>> 32));
    hash = 61 * hash +
      (int) (Double.doubleToLongBits (this.minimumLevel_dB) ^ (Double.doubleToLongBits (this.minimumLevel_dB) >>> 32));
    hash = 61 * hash +
      (int) (Double.doubleToLongBits (this.maximumLevel_dB) ^ (Double.doubleToLongBits (this.maximumLevel_dB) >>> 32));
    hash = 61 * hash + Objects.hashCode (this.stepSizeMode);
    hash = 61 * hash +
      (int) (Double.doubleToLongBits (this.stepSize_MHz) ^ (Double.doubleToLongBits (this.stepSize_MHz) >>> 32));
    hash = 61 * hash +
      (int) (Double.doubleToLongBits (this.stepSize_Percent) ^ (Double.doubleToLongBits (this.stepSize_Percent) >>> 32));
    hash = 61 * hash + (this.testLevel ? 1 : 0);
    hash = 61 * hash + (this.testModulationDepth ? 1 : 0);
    hash = 61 * hash + (this.testModulationDepthPositivePeak ? 1 : 0);
    hash = 61 * hash + (this.testModulationDepthNegativePeak ? 1 : 0);
    hash = 61 * hash + (this.testFrequencyOffset ? 1 : 0);
    hash = 61 * hash + (this.testFrequencyDeviation ? 1 : 0);
    hash = 61 * hash + (this.testFrequencyDeviationPositivePeak ? 1 : 0);
    hash = 61 * hash + (this.testFrequencyDeviationNegativePeak ? 1 : 0);
    hash = 61 * hash + Objects.hashCode (this.frequencyScanRepeatMode);
    hash = 61 * hash + Objects.hashCode (this.frequencyScanSpeedMode);
    hash = 61 * hash + Objects.hashCode (this.fieldDataOuputMode);
    hash = 61 * hash + (this.eoi ? 1 : 0);
    hash = 61 * hash + (this.recorderCode ? 1 : 0);
    hash = 61 * hash + (this.antennaProbeCode ? 1 : 0);
    hash = 61 * hash + (this.srqOnDataReady ? 1 : 0);
    hash = 61 * hash + (this.srqOnEnterInLocalState ? 1 : 0);
    hash = 61 * hash + this.gpibDelimiterInTalkerOperation;
    hash = 61 * hash + Objects.hashCode (this.displayText);
    hash = 61 * hash + Objects.hashCode (this.recorderXAxisMode);
    hash = 61 * hash + (this.recorderCodingEnabled_sf_62_63 ? 1 : 0);
    hash = 61 * hash + (this.noYTRecorder ? 1 : 0);
    hash = 61 * hash + (this.noXYRecorder ? 1 : 0);
    hash = 61 * hash + (this.noZSG3Recorder ? 1 : 0);
    hash = 61 * hash + Objects.hashCode (this.xyRecorderSpectrumMode);
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
    final RS_ESH3_GPIB_Settings other = (RS_ESH3_GPIB_Settings) obj;
    if (Double.doubleToLongBits (this.frequency_Hz) != Double.doubleToLongBits (other.frequency_Hz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.frequencyStart_Hz) != Double.doubleToLongBits (other.frequencyStart_Hz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.frequencyStop_Hz) != Double.doubleToLongBits (other.frequencyStop_Hz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.rfAttenuation_dB) != Double.doubleToLongBits (other.rfAttenuation_dB))
    {
      return false;
    }
    if (this.linearityTest != other.linearityTest)
    {
      return false;
    }
    if (Double.doubleToLongBits (this.ifAttenuation_dB) != Double.doubleToLongBits (other.ifAttenuation_dB))
    {
      return false;
    }
    if (this.maxMinMode != other.maxMinMode)
    {
      return false;
    }
    if (Double.doubleToLongBits (this.measurementTime_s) != Double.doubleToLongBits (other.measurementTime_s))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.minimumLevel_dB) != Double.doubleToLongBits (other.minimumLevel_dB))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.maximumLevel_dB) != Double.doubleToLongBits (other.maximumLevel_dB))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.stepSize_MHz) != Double.doubleToLongBits (other.stepSize_MHz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.stepSize_Percent) != Double.doubleToLongBits (other.stepSize_Percent))
    {
      return false;
    }
    if (this.testLevel != other.testLevel)
    {
      return false;
    }
    if (this.testModulationDepth != other.testModulationDepth)
    {
      return false;
    }
    if (this.testModulationDepthPositivePeak != other.testModulationDepthPositivePeak)
    {
      return false;
    }
    if (this.testModulationDepthNegativePeak != other.testModulationDepthNegativePeak)
    {
      return false;
    }
    if (this.testFrequencyOffset != other.testFrequencyOffset)
    {
      return false;
    }
    if (this.testFrequencyDeviation != other.testFrequencyDeviation)
    {
      return false;
    }
    if (this.testFrequencyDeviationPositivePeak != other.testFrequencyDeviationPositivePeak)
    {
      return false;
    }
    if (this.testFrequencyDeviationNegativePeak != other.testFrequencyDeviationNegativePeak)
    {
      return false;
    }
    if (this.eoi != other.eoi)
    {
      return false;
    }
    if (this.recorderCode != other.recorderCode)
    {
      return false;
    }
    if (this.antennaProbeCode != other.antennaProbeCode)
    {
      return false;
    }
    if (this.srqOnDataReady != other.srqOnDataReady)
    {
      return false;
    }
    if (this.srqOnEnterInLocalState != other.srqOnEnterInLocalState)
    {
      return false;
    }
    if (this.gpibDelimiterInTalkerOperation != other.gpibDelimiterInTalkerOperation)
    {
      return false;
    }
    if (this.recorderCodingEnabled_sf_62_63 != other.recorderCodingEnabled_sf_62_63)
    {
      return false;
    }
    if (this.noYTRecorder != other.noYTRecorder)
    {
      return false;
    }
    if (this.noXYRecorder != other.noXYRecorder)
    {
      return false;
    }
    if (this.noZSG3Recorder != other.noZSG3Recorder)
    {
      return false;
    }
    if (!Objects.equals (this.displayText, other.displayText))
    {
      return false;
    }
    if (this.attenuationMode != other.attenuationMode)
    {
      return false;
    }
    if (this.ifBandwidth != other.ifBandwidth)
    {
      return false;
    }
    if (this.demodulationMode != other.demodulationMode)
    {
      return false;
    }
    if (this.operatingRange != other.operatingRange)
    {
      return false;
    }
    if (this.operatingMode != other.operatingMode)
    {
      return false;
    }
    if (this.indicatingMode != other.indicatingMode)
    {
      return false;
    }
    if (this.dataOutputMode != other.dataOutputMode)
    {
      return false;
    }
    if (this.stepSizeMode != other.stepSizeMode)
    {
      return false;
    }
    if (this.frequencyScanRepeatMode != other.frequencyScanRepeatMode)
    {
      return false;
    }
    if (this.frequencyScanSpeedMode != other.frequencyScanSpeedMode)
    {
      return false;
    }
    if (this.fieldDataOuputMode != other.fieldDataOuputMode)
    {
      return false;
    }
    if (this.recorderXAxisMode != other.recorderXAxisMode)
    {
      return false;
    }
    return this.xyRecorderSpectrumMode == other.xyRecorderSpectrumMode;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
