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

import org.javajdj.jservice.Service;

/** A listener to instrument-related changes of an {@link Instrument}.
 * 
 * <p>
 * Note that since an {@link Instrument} is also a {@link Service},
 * it also supports notifications of property changes and service status
 * through <i>other</i> means.
 * 
 * @see Instrument#addInstrumentListener
 * @see Instrument#removeInstrumentListener
 * @see Service#addStatusListener
 * @see Service#addSettingsListener
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface InstrumentListener
{
  
  void newInstrumentStatus (Instrument instrument, InstrumentStatus instrumentStatus);
  
  void newInstrumentSettings (Instrument instrument, InstrumentSettings instrumentSettings);
  
  void newInstrumentReading (Instrument instrument, InstrumentReading instrumentReading);
  
  /** Notification of a debug-related event at an instrument.
   * 
   * <p>
   * An optional method on an {@link InstrumentListener} for the purpose of debugging.
   * The {@code debugId} is meant to distinguish between different debugging methods
   * in place, so listeners can skip notifications from non-related debug exercises.
   * The special value of zero for {@code debugId} means the issuer has not set it.
   * 
   * <P>
   * Other than that, the are not really any further restriction on the use of this method.
   * 
   * <p>
   * Unused arguments should be set to {@code null} or zero (for {@code @debugId}).
   * 
   * <p>
   * The default implementation is empty.
   * 
   * @param instrument          The {@link Instrument} at which the event occurs.
   * @param debugId             An identification of the debug method to which the event applies.
   * @param instrumentStatus    An optional instrument status object.
   * @param instrumentSettings  An optional instrument settings object.
   * @param instrumentReading   An optional instrument reading object.
   * @param debugObject1        An optional debug object.
   * @param debugObject2        An optional debug object.
   * @param debugObject3        An optional debug object.
   * 
   */
  default void newInstrumentDebug (
    Instrument instrument,
    int debugId,
    InstrumentStatus instrumentStatus,
    InstrumentSettings instrumentSettings,
    InstrumentReading instrumentReading,
    Object debugObject1,
    Object debugObject2,
    Object debugObject3)
  {
  }
  
  /** Recommended value for the {@code debugId} argument in {@link #newInstrumentDebug} for debugging the
   *  {@code byte} array in {@link InstrumentSettings}.
   * 
   * <p>
   * The {@code byte} array is passed in the {@code debugObject1} argument, and the
   * {@code instrument} argument should be set.
   * 
   * @see InstrumentSettings#getBytes
   * 
   */
  static final int INSTRUMENT_DEBUG_ID_SETTINGS_BYTES_1 = 1;
  
}
