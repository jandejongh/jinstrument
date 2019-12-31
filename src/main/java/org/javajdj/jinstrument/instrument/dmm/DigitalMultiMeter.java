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
import java.util.List;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.Unit;

/** Extension of {@link Instrument} for single-channel multi-meters.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface DigitalMultiMeter
extends Instrument
{

  enum NumberOfDigits
  {
    DIGITS_1,
    DIGITS_1_5,
    DIGITS_2,
    DIGITS_2_5,
    DIGITS_3,
    DIGITS_3_5,
    DIGITS_4,
    DIGITS_4_5,
    DIGITS_5,
    DIGITS_5_5,
    DIGITS_6,
    DIGITS_6_5,
    DIGITS_7,
    DIGITS_7_5,
    DIGITS_8,
    DIGITS_8_5,
    DIGITS_9,
    DIGITS_9_5,
    DIGITS_10,
    DIGITS_10_5,
    DIGITS_11,
    DIGITS_11_5,
    DIGITS_12,
    DIGITS_12_5,
    DIGITS_13,
    DIGITS_13_5,
    DIGITS_14,
    DIGITS_14_5,
    DIGITS_15,
    DIGITS_15_5;
  }
  
  enum MeasurementMode
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
  
  interface Range
  {
    double getValue ();
    Unit getUnit ();
  }
  
  List<NumberOfDigits> getSupportedResolutions ();
  
  List<MeasurementMode> getSupportedMeasurementModes ();
  
  List<Range> getSupportedRanges (MeasurementMode measurementMode);

  boolean supportsMeasurementMode (MeasurementMode measurementMode);
  
  void setResolution (NumberOfDigits resolution)
    throws IOException, InterruptedException;
    
  void setMeasurementMode (MeasurementMode measurementMode)
    throws IOException, InterruptedException;
  
  void setAutoRange ()
    throws IOException, InterruptedException;
  
  void setRange (Range range)
    throws IOException, InterruptedException;
  
}
