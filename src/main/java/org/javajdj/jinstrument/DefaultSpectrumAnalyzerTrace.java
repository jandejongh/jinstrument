/*
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
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

/** Default implementation of a {@link SpectrumAnalyzerTrace}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultSpectrumAnalyzerTrace
  extends AbstractInstrumentReading<double[]>
  implements SpectrumAnalyzerTrace
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultSpectrumAnalyzerTrace (
    final SpectrumAnalyzerSettings settings,
    final double[] samples,
    final Unit unit,
    final Resolution resolution,
    final boolean error,
    final String errorMessage,
    final boolean overflow,
    final boolean uncalibrated,
    final boolean uncorrected)
  {
    super (settings, samples, unit, resolution, error, errorMessage, overflow, uncalibrated, uncorrected);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // SpectrumAnalyzerTrace
  // SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final SpectrumAnalyzerSettings getInstrumentSettings ()
  {
    return (SpectrumAnalyzerSettings) super.getInstrumentSettings ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentTrace
  // TRACE LENGTH
  // READING VALUE
  // UNIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final int getTraceLength ()
  {
    return getReadingValue ().length;
  }

  @Override
  public final double[] getReadingValue ()
  {
    return (double[]) super.getReadingValue ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SpectrumAnalyzerTrace
  // CONVERSIONS BETWEEN SAMPLE INDEX AND FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public double sampleIndexToFrequency_MHz (final double sampleIndex)
  {
    // This formula has been verified for/with an operational hp70000 system (pa3gyf),
    // using low values for the trace length.
    // The first sample (index 0) is always taken at the center frequency minus half the span.
    // The step size is always the span divided by the trace length.
    // The two statements above are true, irrespective of whether the trace length is odd or even.
    // Note that this implies that we never "reach" the upper frequency, viz., the center frequency plus half the span.
    // I'm not sure if this is true "in general" for spectrum analyzers, but I highly doubt it.
    final double fStart_MHz = getInstrumentSettings ().getCenterFrequency_MHz () - 0.5 * getInstrumentSettings ().getSpan_MHz ();
    final double fStep_MHz = getInstrumentSettings ().getSpan_MHz () / getTraceLength ();
    return fStart_MHz + sampleIndex * fStep_MHz;
  }

  @Override
  public double frequency_MHzToSampleIndex (final double frequency_MHz)
  {
    final double fStart_MHz = getInstrumentSettings ().getCenterFrequency_MHz () - 0.5 * getInstrumentSettings ().getSpan_MHz ();
    final double fStep_MHz = getInstrumentSettings ().getSpan_MHz () / getTraceLength ();
    return (frequency_MHz - fStart_MHz) / fStep_MHz;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
