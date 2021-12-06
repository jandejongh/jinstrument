/*
 * Copyright 2010-2021 Jan de Jongh <jfcmdejongh@gmail.com>.
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

import java.util.List;
import org.javajdj.jservice.Service;

/** A logger for {@link InstrumentReading}s from a single {@link Instrument}.
 *
 * @param <R> The type of the actual reading.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface InstrumentReadingLogger<R>
  extends Service
{
  
  /** Gets the instrument this logger reads from.
   * 
   * @return The instrument this logger reads from (non-{@code null} and immutable).
   * 
   */
  Instrument getInstrument ();
  
  /** Gets the number of series.
   * 
   * <p>
   * The number of series is only affected by invocations
   * of {@link #clear} or {@link #nextSeries}.
   * 
   * @return The number of series; may be {@code null}.
   * 
   */
  int getNumberOfSeries ();
  
  /** Gets the length (number of readings) in given series.
   * 
   * @param s The index of the series.
   * 
   * @return The length (in terms of number of readings) of the series.
   * 
   * @throws IllegalArgumentException If the series index is out of range (series are indexed starting from zero).
   * 
   */
  int getSeriesLength (int s);
  
  /** Gets all readings (in unmodifiable form).
   * 
   * <p>
   * The structure returned is <i>not</i> backed by new readings from the {@link Instrument},
   * and does <i>not</i> allow modifications.
   * 
   * @return All readings in this logger as a list of series.
   * 
   */
  List<List<InstrumentReading<R>>> getReadings ();
  
  /** Gets the readings for gives series (index).
   * 
   * <p>
   * The structure returned is <i>not</i> backed by new readings from the {@link Instrument},
   * and does <i>not</i> allow modifications.
   * 
   * @param s The index of the series.
   * 
   * @return The readings of the series (as a list).
   * 
   * @throws IllegalArgumentException If the series index is out of range (series are indexed starting from zero).
   * 
   */
  List<InstrumentReading<R>> getReadingsForSeries (int s);
  
  /** Gets a single reading from gives series (index).
   * 
   * @param s The index of the series.
   * @param n The index of the reading within the series.
   * 
   * @return The reading (non-{@code null}).
   * 
   * @throws IllegalArgumentException If the series or reading index is out of range
   *                                  (series and readings therein are indexed starting from zero).
   * 
   */
  InstrumentReading<R> getReading (int s, int n);
    
  /** Clears all data (series and their respective readings).
   * 
   */
  void clear ();
  
  /** Removes all readings in a series (but does not remove the series).
   * 
   * @param s The index of the series to be emptied.
   * 
   */
  void emptySeries (int s);
  
  /** Start a new series.
   * 
   * @return The index of the new series.
   * 
   */
  int nextSeries ();
  
}
