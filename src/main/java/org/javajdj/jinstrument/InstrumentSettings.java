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
package org.javajdj.jinstrument;

import org.javajdj.jservice.Service;
import org.javajdj.junits.Unit;

/** The settings of an {@link Instrument}.
 *
 * <p>
 * Implementations must be <i>immutable</i> and must properly implement the {@link #clone},
 * {@link Object#equals} and {@link Object#hashCode} methods.
 * 
 * <p>
 * Objects implementing this interface carry the complete (relevant) settings of
 * an instrument.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface InstrumentSettings
  extends Cloneable
{
  
  /** Creates a clone of this {@code Object}.
   * 
   * @return A clone of this {@code Object}.
   * 
   * @throws CloneNotSupportedException If cloning is not supported on this object (type);
   *           this is an implementation error as it violates the {@link InstrumentSettings} contract.
   * 
   */
  InstrumentSettings clone () throws CloneNotSupportedException;
  
  /** Returns an array of bytes uniquely identifying the settings of the {@link Instrument},
   *    or {@code null} if this is unsupported.
   * 
   * <p>
   * Many instruments support obtaining their complete settings as an array of bytes.
   * If and only if the bytes equal both in length and contents between two distinct {@link InstrumentSettings} objects
   * of the <i>same class</i>, then they are considered equal.
   * Implementations must, if this notion is supported on the instrument in question.
   * use a byte array to uniquely capture all instrument settings.
   * However, instruments that do <i>not</i> support this,
   * <i>must</i> set the bytes property to {@code null}
   * on all relevant {@link InstrumentSettings} objects.
   * 
   * <p>
   * Adherence to these specifications is crucial, as implementations and classes using
   * {@link InstrumentSettings} often rely heavily on proper implementation of
   * {@link Object#equals} and {@link Object#hashCode}, and the availability of a non-{@code null} bytes array
   * greatly reduces complexity of proper implementation of these methods.
   * 
   * @return An array of bytes uniquely identifying the settings of the {@link Instrument}, may be {@code null}.
   * 
   * @see Object#equals
   * @see Object#hashCode
   * 
   */
  byte[] getBytes ();
  
  /** Returns the default {@link Unit} of readings obtained from the instrument given these settings.
   * 
   * <p>
   * Some implementations of {@link Instrument} need to know the current settings of the instrument
   * in order to determine what unit/meaning a reading from this instrument has.
   * If so, implementation must properly set the reading {@link Unit} property on the settings.
   * However, for many other implementations this is not needed as the readings themselves already
   * contain sufficient information on what has been measured and against which {@link Unit}.
   * Such implementations should return {@code null} here.
   * 
   * <p>
   * Note that this method merely serves as a hint to {@link Instrument} implementations,
   * so do not use this method in order to obtain {@link Unit}s on {@link InstrumentReading}s.
   * Users of {@link InstrumentReading},
   * in particular implementations of {@link InstrumentView},
   * should always use {@link InstrumentReading#getUnit}
   * instead of this method.
   * 
   * @return The default unit of readings from the instrument with these settings, may be {@code null}.
   * 
   * @see Instrument
   * @see InstrumentReading
   * @see InstrumentView
   * 
   */ 
  Unit getReadingUnit ();
  
  /** Returns the {@link InstrumentConfiguration} (when supported) of the instrument.
   * 
   * <p>
   * The configuration of an instrument cannot change during a {@link Service} run of the {@link Instrument} object.
   * 
   * @return The instrument configuration, {@code null} if the notion of instrument configuration is unsupported,
   *           or if the configuration is still unknown.
   * 
   * @see InstrumentConfiguration
   * 
   */
  default InstrumentConfiguration getInstrumentConfiguration ()
  {
    return null;
  }
  
}
