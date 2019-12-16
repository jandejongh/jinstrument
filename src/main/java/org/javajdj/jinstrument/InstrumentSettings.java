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
package org.javajdj.jinstrument;

/** The settings of an {@link Instrument}.
 *
 * <p>
 * Objects implementing this interface carry the complete (relevant) settings of
 * an instrument.
 * It is highly recommended that such objects are immutable and equipped with
 * suitable implementations of {@link #equals} and {@link #hashCode}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface InstrumentSettings
  extends Cloneable
{
  
  InstrumentSettings clone () throws CloneNotSupportedException;
  
}
