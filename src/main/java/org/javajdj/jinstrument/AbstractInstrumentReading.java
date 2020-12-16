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
import java.time.Instant;
import java.util.logging.Logger;

/** Abstract base implementation of {@link InstrumentReading}.
 * 
 * @param <R> The type of the actual reading.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public abstract class AbstractInstrumentReading<R>
  implements InstrumentReading<R>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (AbstractInstrumentReading.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected AbstractInstrumentReading (
    final InstrumentSettings instrumentSettings,
    final InstrumentChannel instrumentChannel,
    final Instant readingTime,
    final R readingValue,
    final Unit unit,
    final Resolution resolution,
    final boolean error,
    final String errorMessage,
    final boolean overflow,
    final boolean uncalibrated,
    final boolean uncorrected)
  {
    if (instrumentSettings == null || readingValue == null)
      throw new IllegalArgumentException ();
    this.instrumentSettings = instrumentSettings;
    this.instrumentChannel = instrumentChannel;
    this.readingTime = readingTime != null ? readingTime : Instant.now ();
    this.readingValue = readingValue;
    this.unit = unit;
    this.resolution = resolution;
    this.error = error;
    this.errorMessage = errorMessage;
    this.overflow = overflow;
    this.uncalibrated = uncalibrated;
    this.uncorrected = uncorrected;    
  }
  
  protected AbstractInstrumentReading (
    final InstrumentSettings instrumentSettings,
    final InstrumentChannel instrumentChannel,
    final R readingValue,
    final Unit unit,
    final Resolution resolution,
    final boolean error,
    final String errorMessage,
    final boolean overflow,
    final boolean uncalibrated,
    final boolean uncorrected)
  {
    this (
      instrumentSettings,
      instrumentChannel,
      null,
      readingValue,
      unit,
      resolution,
      error,
      errorMessage,
      overflow,
      uncalibrated,
      uncorrected);
  }
  
  protected AbstractInstrumentReading (
    final InstrumentSettings instrumentSettings,
    final InstrumentChannel instrumentChannel,
    final R readingValue,
    final Unit unit,
    final Resolution resolution)
  {
    this (
      instrumentSettings,
      instrumentChannel,
      null,
      readingValue,
      unit,
      resolution,
      false,
      null,
      false,
      false,
      false);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // INSTRUMENT SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final InstrumentSettings instrumentSettings;
  
  @Override
  public InstrumentSettings getInstrumentSettings ()
  {
    return this.instrumentSettings;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // INSTRUMENT CHANNEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final InstrumentChannel instrumentChannel;
  
  @Override
  public InstrumentChannel getInstrumentChannel ()
  {
    return this.instrumentChannel;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // READING TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Instant readingTime;
  
  @Override
  public final Instant getReadingTime ()
  {
    return this.readingTime;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // READING VALUE
  // UNIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final R readingValue;
  
  @Override
  public R getReadingValue ()
  {
    return this.readingValue;
  }
  
  private final Unit unit;
  
  @Override
  public final Unit getUnit ()
  {
    return this.unit;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // RESOLUTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Resolution resolution;
  
  @Override
  public final Resolution getResolution ()
  {
    return this.resolution;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // ERROR
  // ERROR MESSAGE
  // OVERFLOW
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
    return this.errorMessage;
  }
  
  public final boolean overflow;
  
  @Override
  public final boolean isOverflow ()
  {
    return this.overflow;
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
