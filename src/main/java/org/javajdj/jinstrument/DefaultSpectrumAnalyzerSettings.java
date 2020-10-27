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
package org.javajdj.jinstrument;

import java.util.Arrays;
import java.util.logging.Logger;

/** Default implementation of {@link SpectrumAnalyzerSettings}.
 * 
 * <p>
 * The object is immutable.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class DefaultSpectrumAnalyzerSettings
  implements SpectrumAnalyzerSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultSpectrumAnalyzerSettings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultSpectrumAnalyzerSettings (
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
    final boolean rfAttenuationCoupled)
  {
    this.bytes = bytes;
    this.unit = unit;
    this.centerFrequency_MHz = centerFrequency_MHz;
    this.span_MHz = span_MHz;
    this.resolutionBandwidth_Hz = resolutionBandwitdh_Hz;
    this.resolutionBandwidthCoupled = resolutionBandwidthCoupled;
    this.videoBandwidth_Hz = videoBandwitdh_Hz;
    this.videoBandwidthCoupled = videoBandwidthCoupled;
    this.sweepTime_s = sweepTime_s;
    this.sweepTimeCoupled = sweepTimeCoupled;
    this.referenceLevel_dBm = referenceLevel_dBm;
    this.rfAttenuation_dB = rfAttenuation_dB;
    this.rfAttenuationCoupled = rfAttenuationCoupled;
  }

  @Override
  public SpectrumAnalyzerSettings clone () throws CloneNotSupportedException
  {
    return (SpectrumAnalyzerSettings) super.clone ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentSettings
  // READING UNIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Unit unit;
  
  @Override
  public Unit getReadingUnit ()
  {
    return this.unit;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentSettings
  // BYTES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final byte[] bytes;
  
  @Override
  public final byte[] getBytes ()
  {
    return this.bytes;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SpectrumAnalyzerSettings
  // CENTER FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double centerFrequency_MHz;
  
  @Override
  public final double getCenterFrequency_MHz ()
  {
    return this.centerFrequency_MHz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SpectrumAnalyzerSettings
  // SPAN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double span_MHz;
  
  @Override
  public final double getSpan_MHz ()
  {
    return this.span_MHz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SpectrumAnalyzerSettings
  // RESOLUTION BANDWIDTH
  // RESOLUTION BANDWIDTH COUPLED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double resolutionBandwidth_Hz;
  
  @Override
  public final double getResolutionBandwidth_Hz ()
  {
    return this.resolutionBandwidth_Hz;
  }
  
  private final boolean resolutionBandwidthCoupled;
  
  @Override
  public final boolean isResolutionBandwidthCoupled ()
  {
    return this.resolutionBandwidthCoupled;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SpectrumAnalyzerSettings
  // VIDEO BANDWIDTH
  // VIDEO BANDWIDTH COUPLED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double videoBandwidth_Hz;
  
  @Override
  public final double getVideoBandwidth_Hz ()
  {
    return this.videoBandwidth_Hz;
  }
  
  private final boolean videoBandwidthCoupled;
  
  @Override
  public final boolean isVideoBandwidthCoupled ()
  {
    return this.videoBandwidthCoupled;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SpectrumAnalyzerSettings
  // SWEEP TIME
  // SWEEP TIME COUPLED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double sweepTime_s;
  
  @Override
  public final double getSweepTime_s ()
  {
    return this.sweepTime_s;
  }
  
  private final boolean sweepTimeCoupled;
  
  @Override
  public final boolean isSweepTimeCoupled ()
  {
    return this.sweepTimeCoupled;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SpectrumAnalyzerSettings
  // REFERENCE LEVEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double referenceLevel_dBm;
  
  @Override
  public final double getReferenceLevel_dBm ()
  {
    return this.referenceLevel_dBm;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SpectrumAnalyzerSettings
  // RF ATTENUATION
  // RF ATTENUATION COUPLED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double rfAttenuation_dB;
  
  @Override
  public final double getRfAttenuation_dB ()
  {
    return this.rfAttenuation_dB;
  }
  
  private final boolean rfAttenuationCoupled;
  
  @Override
  public final boolean isRfAttenuationCoupled ()
  {
    return this.rfAttenuationCoupled;
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
    hash = 53 * hash + Arrays.hashCode (this.bytes);
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
    final DefaultSpectrumAnalyzerSettings other = (DefaultSpectrumAnalyzerSettings) obj;
    if (!Arrays.equals (this.bytes, other.bytes))
    {
      return false;
    }
    return true;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
