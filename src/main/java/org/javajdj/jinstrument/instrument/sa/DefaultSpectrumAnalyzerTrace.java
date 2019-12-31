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
package org.javajdj.jinstrument.instrument.sa;

import org.javajdj.jinstrument.Unit;

/** Default implementation of a {@link SpectrumAnalyzerTrace}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultSpectrumAnalyzerTrace
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
    final boolean error,
    final String errorMessage,
    final boolean uncalibrated,
    final boolean uncorrected)
  {
    if (settings == null || samples == null)
      throw new IllegalArgumentException ();
    this.settings = settings;
    this.samples = samples;
    this.error = error;
    this.errorMessage = errorMessage;
    this.uncalibrated = uncalibrated;
    this.uncorrected = uncorrected;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // SpectrumAnalyzerTrace
  // SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final SpectrumAnalyzerSettings settings;
  
  @Override
  public final SpectrumAnalyzerSettings getInstrumentSettings ()
  {
    return this.settings;
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
    return this.samples.length;
  }

  
  private final double [] samples;
  
  @Override
  public final double[] getReadingValue ()
  {
    return this.samples;
  }

  @Override
  public Unit getUnit ()
  {
    return Unit.UNIT_dBm;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // ERROR
  // ERROR MESSAGE
  // UNCALIBRATED
  // UNCORRECTED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean error;
  
  @Override
  public final boolean isError ()
  {
    return this.error;
  }
  
  private final String errorMessage;

  @Override
  public final String getErrorMessage ()
  {
    return this.errorMessage != null ? this.errorMessage : "";
  }
  
  private final boolean uncalibrated;
  
  @Override
  public final boolean isUncalibrated ()
  {
    return this.uncalibrated;
  }
  
  private final boolean uncorrected;
  
  @Override
  public final boolean isUncorrected ()
  {
    return this.uncorrected;
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
    final double fStart_MHz = this.settings.getCenterFrequency_MHz () - 0.5 * this.settings.getSpan_MHz ();
    final double fStep_MHz = this.settings.getSpan_MHz () / getTraceLength ();
    return fStart_MHz + sampleIndex * fStep_MHz;
  }

  @Override
  public double frequency_MHzToSampleIndex (final double frequency_MHz)
  {
    final double fStart_MHz = this.settings.getCenterFrequency_MHz () - 0.5 * this.settings.getSpan_MHz ();
    final double fStep_MHz = this.settings.getSpan_MHz () / getTraceLength ();
    return (frequency_MHz - fStart_MHz) / fStep_MHz;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
