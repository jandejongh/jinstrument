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

/** A default (empty) implementation of an {@link InstrumentListener}.
 * 
 * @see InstrumentListener
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultInstrumentListener
  implements InstrumentListener
{
  
  @Override
  public void newInstrumentStatus (final Instrument instrument, final InstrumentStatus instrumentStatus)
  {
    // EMPTY
  }

  @Override  
  public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
  {
    // EMPTY
  }
  
  @Override
  public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
  {
    // EMPTY
  }
  
}
