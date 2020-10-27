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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LEGACY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
//public interface SpectrumAnalyzerTraceSettings
//extends InstrumentTraceSettings
//{
//
//  @Override
//  SpectrumAnalyzerTraceSettings clone () throws CloneNotSupportedException;
//  
//  double getCenterFrequency_MHz ();
//  
//  double getSpan_MHz ();
//  
//  double getResolutionBandwidth_Hz ();
//  
//  boolean isAutoResolutionBandwidth ();
//  
//  double getVideoBandwidth_Hz ();
//  
//  boolean isAutoVideoBandwidth ();
//  
//  double getSweepTime_s ();
//  
//  boolean isAutoSweepTime ();
//  
//  double getReferenceLevel_dBm ();
//  
//  double getAttenuation_dB ();
//  
//  SpectrumAnalyzerDetector getDetector ();
//  
//  double sampleIndexToFrequency_MHz (double sampleIndex);
//  
//  double frequency_MHzToSampleIndex (double frequency_MHz);
//  
//  default double getStartFrequency_MHz ()
//  {
//    return getCenterFrequency_MHz () - 0.5 * getSpan_MHz ();
//  }
//  
//  default double getEndFrequency_MHz ()
//  {
//    return getCenterFrequency_MHz () + 0.5 * getSpan_MHz ();
//  }
//  
//  default double getBinSpan_MHz ()
//  {
//    return getSpan_MHz () / getTraceLength ();
//  }
//  
//  default double getBinCenter_MHz (final int sampleIndex)
//  {
//    if (sampleIndex < 0 || sampleIndex >= getTraceLength ())
//      throw new IllegalArgumentException ();
//    // Make sure we always return the center frequency without rounding errors.
//    if ((getTraceLength () % 2 == 1) && (sampleIndex == ((getTraceLength () - 1) / 2)))
//      return getCenterFrequency_MHz ();
//    else
//      return 0.5 * (getBinStart_MHz (sampleIndex) + getBinEnd_MHz (sampleIndex));
//  }
//  
//  default double getBinStart_MHz (final int sampleIndex)
//  {
//    if (sampleIndex < 0 || sampleIndex >= getTraceLength ())
//      throw new IllegalArgumentException ();
//    if (sampleIndex == 0)
//      return getStartFrequency_MHz ();
//    // Make sure we always return the center frequency without rounding errors.
//    else if ((getTraceLength () % 2 == 0) && (sampleIndex == (getTraceLength () / 2)))
//      return getCenterFrequency_MHz ();
//    else
//      return getStartFrequency_MHz () + sampleIndex * getBinSpan_MHz ();
//  }
//  
//  default double getBinEnd_MHz (final int sampleIndex)
//  {
//    if (sampleIndex < 0 || sampleIndex >= getTraceLength ())
//      throw new IllegalArgumentException ();
//    if (sampleIndex == getTraceLength () - 1)
//      return getEndFrequency_MHz ();
//    // Make sure we always return the center frequency without rounding errors.
//    else if ((getTraceLength () % 2 == 0) && (sampleIndex == ((getTraceLength () / 2) - 1)))
//      return getCenterFrequency_MHz ();
//    else
//      return getBinStart_MHz (sampleIndex + 1);
//  }
//  
//}

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
