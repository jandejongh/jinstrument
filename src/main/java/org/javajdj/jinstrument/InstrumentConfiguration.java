/* 
 * Copyright 2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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

/** The configuration of an {@link Instrument}.
 *
 * <p>
 * The configuration of a (physical) instrument refers to deployed hardware
 * and software modules that constitute (or form part of) that instrument,
 * and <i>that cannot change</i> during a {@link Service} run of the {@link Instrument} object.
 * 
 * <p>
 * The most important and most obvious use case for an instrument's configuration
 * is in modular measurement systems like the HP-70000 MMS.
 * Another example is with systems that modify behavior due to software/firmware upgrades.
 * Finally, configurations can be used to provide information about installed options.
 * 
 * @see InstrumentSettings#getInstrumentConfiguration
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface InstrumentConfiguration
{
  
}
