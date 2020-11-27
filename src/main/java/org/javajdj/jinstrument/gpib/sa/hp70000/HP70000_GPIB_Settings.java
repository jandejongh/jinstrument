/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>.
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

import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultSpectrumAnalyzerSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.SpectrumAnalyzerSettings;
import org.javajdj.junits.Unit;

/** Implementation of {@link InstrumentStatus} for the HP-70000 MMS.
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
    final int traceLength)
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
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP70000_GPIB_Settings
  // TRACE LENGTH
  //
  // XXX SHOULD MOVE UP IN HIERARCHY? IS TRUE FOR EVERY SPECTRUM ANALYZER??
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final int traceLength;
  
  public final int getTraceLength ()
  {
    return this.traceLength;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
