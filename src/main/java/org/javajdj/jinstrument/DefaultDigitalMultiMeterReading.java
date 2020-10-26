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

import java.util.logging.Logger;

/** Default implementation of {@link DigitalMultiMeterReading}.
 * 
 * <p>
 * The object is immutable.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class DefaultDigitalMultiMeterReading
  extends AbstractInstrumentReading<Double>
  implements DigitalMultiMeterReading
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultDigitalMultiMeterReading.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultDigitalMultiMeterReading (
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
    super (settings, channel, readingValue, unit, resolution, error, errorMessage, overflow, uncalibrated, uncorrected);
    if (unit == null)
      throw new IllegalArgumentException ();
  }

  @Override
  public DefaultDigitalMultiMeterReading clone () throws CloneNotSupportedException
  {
    return (DefaultDigitalMultiMeterReading) super.clone ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // DigitalMultiMeterReading
  // INSTRUMENT SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public DigitalMultiMeterSettings getInstrumentSettings ()
  {
    return (DigitalMultiMeterSettings) super.getInstrumentSettings ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DigitalMultiMeterReading
  // READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public double getMultiMeterReading ()
  {
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
