/*
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.gpib.dmm.hp3457a;

import org.javajdj.jinstrument.*;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Reading.ReadingStatusType;
import org.javajdj.junits.Resolution;
import org.javajdj.junits.Unit;

/** Implementation of {@link InstrumentReading} for the HP-3457A.
 *
 * <p>
 * Note that a reading over GPIB from the HP-3457A
 * represents one of several physical quantities (DC Volts, AC Volts, etc.).
 * The distinction in this implementation is made through the {@link ReadingStatusType} {@code enum}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP3457A_GPIB_Reading
  extends DefaultDigitalMultiMeterReading
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP3457A_GPIB_Reading (
    final DigitalMultiMeterSettings settings,
    final InstrumentChannel channel,
    final double readingValue,
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
      channel,
      readingValue,
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
  // READING TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final Enum<?> getReadingType ()
  {
    return  getInstrumentSettings ().getMeasurementMode ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
