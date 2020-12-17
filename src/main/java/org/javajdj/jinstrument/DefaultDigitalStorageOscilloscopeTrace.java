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

import org.javajdj.junits.Resolution;
import org.javajdj.junits.Unit;

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
    final InstrumentChannel instrumentChannel,
    final double[] samples,
    final double minNHint,
    final double maxNHint,
    final double minXHint,
    final double maxXHint,
    final double minYHint,
    final double maxYHint,
    final Unit unit,
    final Resolution resolution,
    final boolean error,
    final String errorMessage,
    final boolean overflow,
    final boolean uncalibrated,
    final boolean uncorrected)
  {
    super (
      settings,
      instrumentChannel,
      samples,
      unit,
      resolution,
      error,
      errorMessage,
      overflow,
      uncalibrated,
      uncorrected);
    if (instrumentChannel == null)
      throw new IllegalArgumentException ();
    this.instrumentChannel = instrumentChannel;
    this.minNHint = minNHint;
    this.maxNHint = maxNHint;
    this.minXHint = minXHint;
    this.maxXHint = maxXHint;
    this.minYHint = minYHint;
    this.maxYHint = maxYHint;
  }
  
  public DefaultDigitalStorageOscilloscopeTrace (
    final DigitalStorageOscilloscopeSettings settings,
    final InstrumentChannel instrumentChannel,
    final double[] samples,
    final double minXHint,
    final double maxXHint,
    final double minYHint,
    final double maxYHint,
    final Unit unit,
    final Resolution resolution,
    final boolean error,
    final String errorMessage,
    final boolean overflow,
    final boolean uncalibrated,
    final boolean uncorrected)
  {
    this (
      settings,
      instrumentChannel,
      samples,
      0,
      samples != null ? samples.length : 0,
      minXHint,
      maxXHint,
      minYHint,
      maxYHint,
      unit,
      resolution,
      error,
      errorMessage,
      overflow,
      uncalibrated,
      uncorrected);
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
  // DigitalStorageOscilloscopeTrace
  // INSTRUMENT CHANNEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final InstrumentChannel instrumentChannel;
  
  @Override
  public final InstrumentChannel getInstrumentChannel ()
  {
    return this.instrumentChannel;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentTrace
  // TRACE LENGTH
  // READING VALUE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns the trace length in terms of number of samples.
   * 
   * @return The trace length in terms of number of samples.
   * 
   */
  @Override
  public final int getTraceLength ()
  {
    return getReadingValue ().length;
  }

  /** Returns the trace as a {@code double} array.
   * 
   * @return The trace as a {@code double} array.
   * 
   */
  @Override
  public final double[] getReadingValue ()
  {
    return (double[]) super.getReadingValue ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DigitalStorageOscilloscopeTrace
  // BOUNDARIES HINTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double minNHint;
  
  @Override
  public final double getMinNHint ()
  {
    return this.minNHint;
  }

  private final double maxNHint;
  
  @Override
  public final double getMaxNHint ()
  {
    return this.maxNHint;
  }

  private final double minXHint;
  
  /** Returns a (strong) hint for the (minimum) X value corresponding to the {@code n = 0} index value.
   * 
   * @return A (strong) hint for the (minimum) X value corresponding to the {@code n = 0} index value.
   * 
   */
  @Override
  public final double getMinXHint ()
  {
    return this.minXHint;
  }

  private final double maxXHint;
  
  /** Returns a (strong) hint for the (maximum) X value corresponding to the {@link #getTraceLength} index value.
   * 
   * <p>
   * XXX Check this; shouldn't this be {@code getTraceLength - 1}?.
   * 
   * @return A (strong) hint for the (maximum) X value corresponding to the {@code n = 0} index value.
   * 
   */
  @Override
  public final double getMaxXHint ()
  {
    return this.maxXHint;
  }

  private final double minYHint;
  
  @Override
  public final double getMinYHint ()
  {
    return this.minYHint;
  }

  private final double maxYHint;

  @Override
  public final double getMaxYHint ()
  {
    return this.maxYHint;
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
