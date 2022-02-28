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
package org.javajdj.jinstrument.gpib.sa.hp70000;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultSpectrumAnalyzerSettings;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.SpectrumAnalyzerSettings;
import org.javajdj.junits.Unit;

/** Implementation of {@link InstrumentSettings} for the HP-70000 Spectrum Analyzer [MMS].
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP70000_GPIB_Settings
  extends DefaultSpectrumAnalyzerSettings
  implements SpectrumAnalyzerSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP70000_GPIB_Settings.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP70000_GPIB_Settings (
    final byte[] bytes,
    final Unit unit,
    final double centerFrequency_MHz,
    final double span_MHz,
    final double resolutionBandwitdh_Hz,
    final boolean resolutionBandwidthCoupled,
    final double videoBandwitdh_Hz,
    final boolean videoBandwidthCoupled,
    final double sweepTime_s,
    final boolean sweepTimeCoupled,
    final double referenceLevel_dBm,
    final double rfAttenuation_dB,
    final boolean rfAttenuationCoupled,
    final int traceLength,
    final String id,
    final String configurationString,
    final MeasureMode measureMode,
    final String identificationNumber,
    final double sourcePower_dBm,
    final boolean sourceActive,
    final SourceAlcMode sourceAlcMode,
    final double sourceAmDepth_percent,
    final boolean sourceAmActive,
    final double sourceAmFrequency_Hz,
    final double sourceAttenuation_dB,
    final boolean sourceAttenuationCoupled,
    final boolean sourceBlanking,
    final SourceModulationInput sourceModulationInput,
    final SourceOscillator sourceOscillator,
    final double sourcePowerOffset_dB,
    final SourcePowerStepSizeMode sourcePowerStepSizeMode,
    final double sourcePowerStepSize_dB,
    final boolean sourcePowerSweepActive,
    final double sourcePowerSweepRange_dB,
    final double sourceTracking_Hz,
    final boolean sourcePeakTrackingAuto)
  {
    super (
      bytes,
      unit,
      centerFrequency_MHz,
      span_MHz,
      resolutionBandwitdh_Hz,
      resolutionBandwidthCoupled,
      videoBandwitdh_Hz,
      videoBandwidthCoupled,
      sweepTime_s,
      sweepTimeCoupled,
      referenceLevel_dBm,
      rfAttenuation_dB,
      rfAttenuationCoupled);
    this.traceLength = traceLength;
    this.id = id;
    this.configurationString = configurationString;
    this.measureMode = measureMode;
    this.identificationNumber = identificationNumber;
    this.sourcePower_dBm = sourcePower_dBm;
    this.sourceActive = sourceActive;
    this.sourceAlcMode = sourceAlcMode;
    this.sourceAmDepth_percent = sourceAmDepth_percent;
    this.sourceAmActive = sourceAmActive;
    this.sourceAmFrequency_Hz = sourceAmFrequency_Hz;
    this.sourceAttenuation_dB = sourceAttenuation_dB;
    this.sourceAttenuationCoupled = sourceAttenuationCoupled;
    this.sourceBlanking = sourceBlanking;
    this.sourceModulationInput = sourceModulationInput;
    this.sourceOscillator = sourceOscillator;
    this.sourcePowerOffset_dB = sourcePowerOffset_dB;
    this.sourcePowerStepSizeMode = sourcePowerStepSizeMode;
    this.sourcePowerStepSize_dB = sourcePowerStepSize_dB;
    this.sourcePowerSweepActive = sourcePowerSweepActive;
    this.sourcePowerSweepRange_dB = sourcePowerSweepRange_dB;
    this.sourceTracking_Hz = sourceTracking_Hz;
    this.sourcePeakTrackingAuto = sourcePeakTrackingAuto;
  }

  private HP70000_GPIB_Settings with (final Map<String, Object> map)
  {
    if (map == null)
      throw new IllegalArgumentException ();
    return new HP70000_GPIB_Settings (
      map.containsKey (BYTES_KEY) ? (byte[]) map.get (BYTES_KEY) : getBytes (),
      getReadingUnit (),
      getCenterFrequency_MHz (),
      getSpan_MHz (),
      getResolutionBandwidth_Hz (),
      isResolutionBandwidthCoupled (),
      getVideoBandwidth_Hz (),
      isVideoBandwidthCoupled (),
      getSweepTime_s (),
      isSweepTimeCoupled (),
      getReferenceLevel_dBm (),
      getRfAttenuation_dB (),
      isRfAttenuationCoupled (),
      map.containsKey (TRACE_LENGTH_KEY) ? (int) map.get (TRACE_LENGTH_KEY) : this.traceLength,
      map.containsKey (ID_KEY) ? (String) map.get (ID_KEY) : this.id,
      map.containsKey (CONFIGURATION_STRING_KEY) ? (String) map.get (CONFIGURATION_STRING_KEY) : this.configurationString,
      map.containsKey (MEASURE_MODE_KEY) ? (MeasureMode) map.get (MEASURE_MODE_KEY) : this.measureMode,
      map.containsKey (IDENTIFICATION_NUMBER_KEY) ? (String) map.get (IDENTIFICATION_NUMBER_KEY) : this.identificationNumber,
      map.containsKey (SOURCE_POWER_KEY) ? (double) map.get (SOURCE_POWER_KEY) : this.sourcePower_dBm,
      map.containsKey (SOURCE_ACTIVE_KEY) ? (boolean) map.get (SOURCE_ACTIVE_KEY) : this.sourceActive,
      map.containsKey (SOURCE_ALC_MODE_KEY) ? (SourceAlcMode) map.get (SOURCE_ALC_MODE_KEY) : this.sourceAlcMode,
      map.containsKey (SOURCE_AM_DEPTH_KEY) ? (double) map.get (SOURCE_AM_DEPTH_KEY) : this.sourceAmDepth_percent,
      map.containsKey (SOURCE_AM_ACTIVE_KEY) ? (boolean) map.get (SOURCE_AM_ACTIVE_KEY) : this.sourceAmActive,
      map.containsKey (SOURCE_AM_FREQUENCY_KEY) ? (double) map.get (SOURCE_AM_FREQUENCY_KEY) : this.sourceAmFrequency_Hz,
      map.containsKey (SOURCE_ATTENUATION_KEY) ? (double) map.get (SOURCE_ATTENUATION_KEY) : this.sourceAttenuation_dB,
      map.containsKey (SOURCE_ATTENUATION_COUPLED_KEY) ? (boolean) map.get (SOURCE_ATTENUATION_COUPLED_KEY)
                                                       : this.sourceAttenuationCoupled,
      map.containsKey (SOURCE_BLANKING_KEY) ? (boolean) map.get (SOURCE_BLANKING_KEY) : this.sourceBlanking,
      map.containsKey (SOURCE_MODULATION_INPUT_KEY) ? (SourceModulationInput) map.get (SOURCE_MODULATION_INPUT_KEY)
                                                    : this.sourceModulationInput,
      map.containsKey (SOURCE_OSCILLATOR_KEY) ? (SourceOscillator) map.get (SOURCE_OSCILLATOR_KEY) : this.sourceOscillator,
      map.containsKey (SOURCE_POWER_OFFSET_KEY) ? (double) map.get (SOURCE_POWER_OFFSET_KEY) : this.sourcePowerOffset_dB,
      map.containsKey (SOURCE_POWER_STEP_SIZE_MODE_KEY) ? (SourcePowerStepSizeMode) map.get (SOURCE_POWER_STEP_SIZE_MODE_KEY)
                                                        : this.sourcePowerStepSizeMode,
      map.containsKey (SOURCE_POWER_STEP_SIZE_KEY) ? (double) map.get (SOURCE_POWER_STEP_SIZE_KEY) : this.sourcePowerStepSize_dB,
      map.containsKey (SOURCE_POWER_SWEEP_ACTIVE_KEY) ? (boolean) map.get (SOURCE_POWER_SWEEP_ACTIVE_KEY)
                                                      : this.sourcePowerSweepActive,
      map.containsKey (SOURCE_POWER_SWEEP_RANGE_KEY) ? (double) map.get (SOURCE_POWER_SWEEP_RANGE_KEY)
                                                     : this.sourcePowerSweepRange_dB,
      map.containsKey (SOURCE_TRACKING_KEY) ? (double) map.get (SOURCE_TRACKING_KEY) : this.sourceTracking_Hz,
      map.containsKey (SOURCE_PEAK_TRACKING_AUTO_KEY) ? (boolean) map.get (SOURCE_PEAK_TRACKING_AUTO_KEY)
                                                      : this.sourcePeakTrackingAuto
    );
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentSettings
  // BYTES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  private final static String BYTES_KEY = "bytes";
  
  public final HP70000_GPIB_Settings withBytes (final byte[] bytes)
  {
    return with (new HashMap<String, Object> () {{ put (BYTES_KEY, bytes); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP70000_GPIB_Settings
  // TRACE LENGTH
  //
  // XXX SHOULD MOVE UP IN HIERARCHY? IS TRUE FOR EVERY SPECTRUM ANALYZER??
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final static String TRACE_LENGTH_KEY = "traceLength";
  
  private final int traceLength;
  
  public final int getTraceLength ()
  {
    return this.traceLength;
  }
  
  public final HP70000_GPIB_Settings withTraceLength (final int traceLength)
  {
    return with (new HashMap<String, Object> () {{ put (TRACE_LENGTH_KEY, traceLength); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONFIGURATION STRING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String CONFIGURATION_STRING_KEY = "configurationString";
  
  private final String configurationString;
  
  public final String getConfigurationString ()
  {
    return this.configurationString;
  }
  
  public final HP70000_GPIB_Settings withConfigurationString (final String configurationString)
  {
    return with (new HashMap<String, Object> () {{ put (CONFIGURATION_STRING_KEY, configurationString); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ID
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String ID_KEY = "id";
  
  private final String id;
  
  public final String getId ()
  {
    return this.id;
  }
  
  public final HP70000_GPIB_Settings withId (final String id)
  {
    return with (new HashMap<String, Object> () {{ put (ID_KEY, id); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // IDENTIFICATION NUMBER [V.U.F.]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String IDENTIFICATION_NUMBER_KEY = "identificationNumber";
  
  private final String identificationNumber;
  
  public final String getIdentificationNumber ()
  {
    return this.identificationNumber;
  }
  
  public final HP70000_GPIB_Settings withIdentificationNumber (final String identificationNumber)
  {
    return with (new HashMap<String, Object> () {{ put (IDENTIFICATION_NUMBER_KEY, identificationNumber); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT PRESET
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static HP70000_GPIB_Settings fromPreset ()
  {
    // XXX There is no way to implement this without knowing the instrument's configuration.
    throw new UnsupportedOperationException ();
//    return new HP70000_GPIB_Settings (
//      null,              // bytes
//      Unit.UNIT_dBm,     // unit
//      10000,             // centerFrequency_MHz XXX?
//      10000,             // span_MHz XXX?
//      0,                 // resolutionBandwitdh_Hz
//      true,
//      0,
//      true,
//      0,
//      true,
//      0,
//      0,
//      true,
//      0,
//      ID_KEY,
//      CONFIGURATION_STRING_KEY,
//      MeasureMode.SRMilCpl,
//      IDENTIFICATION_NUMBER_KEY,
//      0,
//      true,
//      SourceAlcMode.Normal,
//      0,
//      true,
//      0,
//      0,
//      true,
//      true,
//      SourceModulationInput.External,
//      SourceOscillator.Internal,
//      0,
//      SourcePowerStepSizeMode.Automatic,
//      0,
//      true,
//      0,
//      0,
//      true);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MEASURE MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum MeasureMode
  {
    SpectrumAnalysis,
    StimulusResponse,
    SRMilCpl; // XXX What the heck is this?
  }
  
  private final static String MEASURE_MODE_KEY = "measureMode";
    
  private final MeasureMode measureMode;
  
  public final MeasureMode getMeasureMode ()
  {
    return this.measureMode;
  }
  
  public final HP70000_GPIB_Settings withMeasureMode (final MeasureMode measureMode)
  {
    return with (new HashMap<String, Object> () {{ put (MEASURE_MODE_KEY, measureMode); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: POWER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_POWER_KEY = "sourcePower";
  
  private final double sourcePower_dBm;
  
  public final double getSourcePower_dBm ()
  {
    return this.sourcePower_dBm;
  }
  
  public final HP70000_GPIB_Settings withSourcePower_dBm (final double sourcePower_dBm)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_POWER_KEY, sourcePower_dBm); }});
  }
  
  private final static String SOURCE_ACTIVE_KEY = "sourceActive";
  
  private final boolean sourceActive;
  
  public final boolean isSourceActive ()
  {
    return this.sourceActive;
  }
  
  public final HP70000_GPIB_Settings withSourceActive (final boolean sourceActive)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_ACTIVE_KEY, sourceActive); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: ALC
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_ALC_MODE_KEY = "sourceAlcMode";
  
  public enum SourceAlcMode
  {
    Normal,
    Alternative,
    External;
  }
  
  private final SourceAlcMode sourceAlcMode;
  
  public final SourceAlcMode getSourceAlcMode ()
  {
    return this.sourceAlcMode;
  }
  
  public final HP70000_GPIB_Settings withSourceAlcMode (final SourceAlcMode sourceAlcMode)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_ALC_MODE_KEY, sourceAlcMode); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: AM DEPTH
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_AM_DEPTH_KEY = "sourceAmDepth";
  
  private final double sourceAmDepth_percent;
  
  public final double getSourceAmDepth_percent ()
  {
    return this.sourceAmDepth_percent;
  }
  
  public final HP70000_GPIB_Settings withSourceAmDepth_percent (final double sourceAmDepth_percent)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_AM_DEPTH_KEY, sourceAmDepth_percent); }});
  }
  
  private final static String SOURCE_AM_ACTIVE_KEY = "sourceAmActive";
  
  private final boolean sourceAmActive;
  
  public final boolean isSourceAmActive ()
  {
    return this.sourceAmActive;
  }
  
  public final HP70000_GPIB_Settings withSourceAmActive (final boolean sourceAmActive)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_AM_ACTIVE_KEY, sourceAmActive); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: AM FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_AM_FREQUENCY_KEY = "sourceAmFrequency";
  
  private final double sourceAmFrequency_Hz;
  
  public final double getSourceAmFrequency_Hz ()
  {
    return this.sourceAmFrequency_Hz;
  }
  
  public final HP70000_GPIB_Settings withSourceAmFrequency_Hz (final double sourceAmFrequency_Hz)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_AM_FREQUENCY_KEY, sourceAmFrequency_Hz); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: ATTENUATOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_ATTENUATION_KEY = "sourceAttenuation";
  
  private final double sourceAttenuation_dB;
  
  public final double getSourceAttenuation_dB ()
  {
    return this.sourceAttenuation_dB;
  }
  
  public final HP70000_GPIB_Settings withSourceAttenuation_dB (final double sourceAttenuation_dB)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_ATTENUATION_KEY, sourceAttenuation_dB); }});
  }
  
  private final static String SOURCE_ATTENUATION_COUPLED_KEY = "sourceAttenuationCoupled";
  
  private final boolean sourceAttenuationCoupled;
  
  public final boolean isSourceAttenuationCoupled ()
  {
    return this.sourceAttenuationCoupled;
  }
  
  public final HP70000_GPIB_Settings withSourceAttenuationCoupled (final boolean sourceAttenuationCoupled)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_ATTENUATION_COUPLED_KEY, sourceAttenuationCoupled); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: BLANK
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_BLANKING_KEY = "sourceBlanking";
  
  private final boolean sourceBlanking;
  
  public final boolean isSourceBlanking ()
  {
    return this.sourceBlanking;
  }
  
  public final HP70000_GPIB_Settings withSourceBlanking (final boolean sourceBlanking)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_BLANKING_KEY, sourceBlanking); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: MODULATION INPUT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_MODULATION_INPUT_KEY = "sourceModulationInput";
  
  public enum SourceModulationInput
  {
    Internal,
    External;
  }
  
  private final SourceModulationInput sourceModulationInput;
  
  public final SourceModulationInput getSourceModulationInput ()
  {
    return this.sourceModulationInput;
  }
  
  public final HP70000_GPIB_Settings withSourceModulationInput (final SourceModulationInput sourceModulationInput)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_MODULATION_INPUT_KEY, sourceModulationInput); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: OSCILLATOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_OSCILLATOR_KEY = "sourceOscillator";
  
  public enum SourceOscillator
  {
    Internal,
    External;
  }
  
  private final SourceOscillator sourceOscillator;
  
  public final SourceOscillator getSourceOscillator ()
  {
    return this.sourceOscillator;
  }
  
  public final HP70000_GPIB_Settings withSourceOscillator (final SourceOscillator sourceOscillator)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_OSCILLATOR_KEY, sourceOscillator); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: POWER OFFSET
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_POWER_OFFSET_KEY = "sourcePowerOffset";
  
  private final double sourcePowerOffset_dB;
  
  public final double getSourcePowerOffset_dB ()
  {
    return this.sourcePowerOffset_dB;
  }
  
  public final HP70000_GPIB_Settings withSourcePowerOffset_dB (final double sourcePowerOffset_dB)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_POWER_OFFSET_KEY, sourcePowerOffset_dB); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: POWER STEP SIZE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_POWER_STEP_SIZE_MODE_KEY = "sourcePowerStepSizeMode";
  
  public enum SourcePowerStepSizeMode
  {
    Automatic,
    Manual;
  }
  
  private final SourcePowerStepSizeMode sourcePowerStepSizeMode;
  
  public final SourcePowerStepSizeMode getSourcePowerStepSizeMode ()
  {
    return this.sourcePowerStepSizeMode;
  }
  
  public final HP70000_GPIB_Settings withSourcePowerStepSizeMode (final SourcePowerStepSizeMode sourcePowerStepSizeMode)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_POWER_STEP_SIZE_MODE_KEY, sourcePowerStepSizeMode); }});
  }
  
  private final static String SOURCE_POWER_STEP_SIZE_KEY = "sourcePowerStepSize";
  
  private final double sourcePowerStepSize_dB;
  
  public final double getSourcePowerStepSize_dB ()
  {
    return this.sourcePowerStepSize_dB;
  }
  
  public final HP70000_GPIB_Settings withSourcePowerStepSize_dB (final double sourcePowerStepSize_dB)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_POWER_STEP_SIZE_KEY, sourcePowerStepSize_dB); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: POWER SWEEP
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_POWER_SWEEP_ACTIVE_KEY = "sourcePowerSweepActive";
  
  private final boolean sourcePowerSweepActive;
  
  public final boolean isSourcePowerSweepActive ()
  {
    return this.sourcePowerSweepActive;
  }
  
  public final HP70000_GPIB_Settings withSourcePowerSweepActive (final boolean sourcePowerSweepActive)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_POWER_SWEEP_ACTIVE_KEY, sourcePowerSweepActive); }});
  }
  
  private final static String SOURCE_POWER_SWEEP_RANGE_KEY = "sourcePowerSweepRange";
  
  private final double sourcePowerSweepRange_dB;
  
  public final double getSourcePowerSweepRange_dB ()
  {
    return this.sourcePowerSweepRange_dB;
  }
  
  public final HP70000_GPIB_Settings withSourcePowerSweepRange_dB (final double sourcePowerSweepRange_dB)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_POWER_SWEEP_RANGE_KEY, sourcePowerSweepRange_dB); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: TRACKING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_TRACKING_KEY = "sourceTracking";
  
  private final double sourceTracking_Hz;
  
  public final double getSourceTracking_Hz ()
  {
    return this.sourceTracking_Hz;
  }
  
  public final HP70000_GPIB_Settings withSourceTracking_Hz (final double sourceTracking_Hz)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_TRACKING_KEY, sourceTracking_Hz); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOURCE: TRACKING PEAK
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static String SOURCE_PEAK_TRACKING_AUTO_KEY = "sourcePeakTrackingAuto";
  
  private final boolean sourcePeakTrackingAuto;
  
  public final boolean isSourcePeakTrackingAuto ()
  {
    return this.sourcePeakTrackingAuto;
  }
  
  public final HP70000_GPIB_Settings withSourcePeakTrackingAuto (final boolean sourcePeakTrackingAuto)
  {
    return with (new HashMap<String, Object> () {{ put (SOURCE_PEAK_TRACKING_AUTO_KEY, sourcePeakTrackingAuto); }});
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
