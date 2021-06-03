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

/** The calibration data from an {@link Instrument}.
 *
 * <p>
 * Calibration data is assumed to be representable as an immutable Java byte array.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface InstrumentCalibrationData
{
  
  /** Returns the size of the calibration data, in number of bytes.
   * 
   * <p>
   * Calibration-data size (in terms of number of bytes) must only depend on the {@link InstrumentCalibrationData}
   * concrete <i>subtype</i> and <i>not</i> be instance-specific.
   * 
   * @return The size of the calibration data, in number of bytes (zero or positive).
   * 
   */
  int getSize ();
  
  /** Returns the calibration-data bytes.
   * 
   * <p>
   * Calibration-data bytes are set upon construction and cannot be
   * changed on the object thereafter.
   * 
   * <p>
   * Calibration-data size (in terms of number of bytes) must only depend on the {@link InstrumentCalibrationData}
   * concrete <i>subtype</i> and <i>not</i> be instance-specific.
   * 
   * <p>
   * Typically (and highly recommended), a copy of the internally stored byte array is supplied by implementation.
   * 
   * @return The calibration-data bytes; non-{@code null}.
   * 
   */
  byte[] getBytes ();
  
}
