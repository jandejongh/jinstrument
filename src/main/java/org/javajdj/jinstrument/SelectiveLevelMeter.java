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

import java.io.IOException;

/** Extension of {@link Instrument} for (single-channel) selective-level meters.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface SelectiveLevelMeter
extends Instrument
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns the minimum (center) frequency supported.
   * 
   * <p>
   * Must be constant throughout the lifetime of the instrument.
   * 
   * @return The minimum (center) frequency supported, in Hz.
   * 
   * @see #getMaxFrequency_Hz
   * 
   */
  double getMinFrequency_Hz ();
  
  /** Returns the maximum (center) frequency supported.
   * 
   * <p>
   * Must be constant throughout the lifetime of the instrument.
   * 
   * @return The maximum (center) frequency supported, in Hz.
   * 
   * @see #getMinFrequency_Hz
   * 
   */
  double getMaxFrequency_Hz ();
  
  /** Returns the (highest) frequency resolution.
   * 
   * <p>
   * Must be constant throughout the lifetime of the instrument.
   * 
   * @return The (highest) frequency resolution supported, in Hz.
   * 
   */
  double getHighestFrequencyResolution_Hz ();
  
  /** Sets the (center) frequency of the instrument.
   * 
   * @param centerFrequency_Hz The (new) center frequency, in Hz.
   * 
   * @throws IllegalArgumentException If the argument is out of range for the instrument.
   * @throws IOException              If communication with the controller, device or instrument resulted in an error.
   * @throws InterruptedException     If the current thread was interrupted while awaiting command completion.
   * 
   * @see #getMinFrequency_Hz
   * @see #getMaxFrequency_Hz
   * 
   */
  void setFrequency_Hz (double centerFrequency_Hz)
    throws IOException, InterruptedException;
  
}
