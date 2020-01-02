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

/** Extension of {@link InstrumentSettings} for (typically RF) single-channel spectrum analyzers.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface SpectrumAnalyzerSettings
  extends InstrumentSettings
{
  
  @Override
  SpectrumAnalyzerSettings clone() throws CloneNotSupportedException;
  
  byte[] getBytes ();
  
  double getCenterFrequency_MHz ();
  
  double getSpan_MHz ();
  
  double getResolutionBandwidth_Hz ();
  
  boolean isResolutionBandwidthCoupled ();
  
  double getVideoBandwidth_Hz ();
  
  boolean isVideoBandwidthCoupled ();
  
  double getSweepTime_s ();
  
  boolean isSweepTimeCoupled ();
  
  double getReferenceLevel_dBm ();
  
  double getRfAttenuation_dB ();
  
  boolean isRfAttenuationCoupled ();
  
}
