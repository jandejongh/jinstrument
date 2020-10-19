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

/** Default implementation of a {@link DigitalStorageOscilloscopeTrace}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultDigitalStorageOscilloscopeTrace
  extends AbstractInstrumentReading<double[]>
  implements DigitalStorageOscilloscopeTrace
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultDigitalStorageOscilloscopeTrace (
    final DigitalStorageOscilloscopeSettings settings,
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
  // DigitalStorageOscilloscopeTrace
  // SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final DigitalStorageOscilloscopeSettings getInstrumentSettings ()
  {
    return (DigitalStorageOscilloscopeSettings) super.getInstrumentSettings ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentTrace
  // TRACE LENGTH
  // READING VALUE
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
  // DigitalStorageOscilloscopeTrace
  // CONVERSIONS BETWEEN SAMPLE INDEX AND TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public double sampleIndexToTime_s (final double sampleIndex)
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public double time_sToSampleIndex (final double time_s)
  {
    throw new UnsupportedOperationException ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
