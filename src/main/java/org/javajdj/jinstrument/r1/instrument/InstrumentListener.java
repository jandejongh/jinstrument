/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>, TNO.
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
package org.javajdj.jinstrument.r1.instrument;

import org.javajdj.jinstrument.Instrument;
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
  
}
