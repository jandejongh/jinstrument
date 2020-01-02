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

import java.io.IOException;

/** Extension of {@link Instrument} for (typically RF) single-channel spectrum analyzers.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface SpectrumAnalyzer
extends Instrument
{

  void setCenterFrequency_MHz (double centerFrequency_MHz)
    throws IOException, InterruptedException;

  void setSpan_MHz (double span_MHz)
    throws IOException, InterruptedException;
  
  void setResolutionBandwidth_Hz (double resolutionBandwidth_Hz)
    throws IOException, InterruptedException;

  void setResolutionBandwidthCoupled ()
    throws IOException, InterruptedException;
  
  void setVideoBandwidth_Hz (double videoBandwidth_Hz)
    throws IOException, InterruptedException;
  
  void setVideoBandwidthCoupled ()
    throws IOException, InterruptedException;
  
  void setSweepTime_s (double sweepTime_s)
    throws IOException, InterruptedException;
  
  void setSweepTimeCoupled ()
    throws IOException, InterruptedException;
  
  void setReferenceLevel_dBm (double referenceLevel_dBm)
    throws IOException, InterruptedException;
  
  void setRfAttenuation_dB (double rfAttenaution_dB)
    throws IOException, InterruptedException;
  
  void setRfAttenuationCoupled ()
    throws IOException, InterruptedException;
  
}
