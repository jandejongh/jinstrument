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

/** Extension of {@link InstrumentSettings} for single-channel multi-meters.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface DigitalMultiMeterSettings
  extends InstrumentSettings
{
  
  @Override
  DigitalMultiMeterSettings clone() throws CloneNotSupportedException;
  
  Resolution getResolution ();
  
  DigitalMultiMeter.MeasurementMode getMeasurementMode ();
  
  boolean isAutoRange ();
  
  DigitalMultiMeter.Range getRange ();
  
}
