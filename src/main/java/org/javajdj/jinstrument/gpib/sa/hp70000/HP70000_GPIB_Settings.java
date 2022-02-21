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
    final boolean sourceActive)
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
      map.containsKey (SOURCE_ACTIVE_KEY) ? (boolean) map.get (SOURCE_ACTIVE_KEY) : this.sourceActive
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
  // SOURCE ACTIVE
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
