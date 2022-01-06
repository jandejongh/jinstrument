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

/** A <i>channel</i> on an {@link Instrument}.
 *
 * <p>
 * For implementations it is recommended to view a channel
 * as a symbolic representation of one of
 * the input (or output) measurement ports
 * on an instrument <i>type</i>,
 * and <i>not</i> on a specific <i>instrument</i>.
 * In other words,
 * it is recommended to share channel instances among instruments of the same type.
 * In future versions of {@code jinstrument},
 * this recommendation may turn into a requirement.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface InstrumentChannel
{
  
}
