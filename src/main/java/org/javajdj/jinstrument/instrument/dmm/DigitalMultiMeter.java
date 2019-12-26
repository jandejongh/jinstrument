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
package org.javajdj.jinstrument.instrument.dmm;

import java.io.IOException;
import org.javajdj.jinstrument.Instrument;

/** Extension of {@link Instrument} for single-channel multi-meters.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface DigitalMultiMeter
extends Instrument
{

  public enum MeasurementMode
  {
    NONE,
    UNKNOWN,
    UNSUPPORTED,
    DC_VOLTAGE,
    AC_VOLTAGE,
    RESISTANCE_2W,
    RESISTANCE_4W,
    DC_CURRENT,
    AC_CURRENT,
    FREQUENCY,
    CAPACITANCE,
    INDUCTANCE,
    IMPEDANCE,
    TEMPERATURE;
  }
  
  boolean supportsMeasurementMode (MeasurementMode measurementMode);
  
  void setMeasurementMode (MeasurementMode measurementMode)
    throws IOException, InterruptedException;

}
