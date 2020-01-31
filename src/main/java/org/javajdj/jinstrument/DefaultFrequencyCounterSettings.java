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

import java.util.Arrays;
import java.util.logging.Logger;

/** *  Default implementation of {@link FrequencyCounterSettings}.
 * 
 * <p>
 * The object is immutable.
 * 
 * @param <M> The type of modes (functions, measurement functions, etc.) on the instrument.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class DefaultFrequencyCounterSettings<M>
  implements FrequencyCounterSettings<M>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultFrequencyCounterSettings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultFrequencyCounterSettings (
    final byte[] bytes,
    final M instrumentMode,
    final double gateTime_s,
    final Unit readingUnit)
  {
    this.bytes = bytes;
    this.instrumentMode = instrumentMode;
    this.gateTime_s = gateTime_s;
    this.readingUnit = readingUnit;
  }

  @Override
  public FrequencyCounterSettings clone () throws CloneNotSupportedException
  {
    return (FrequencyCounterSettings) super.clone ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentSettings
  // READING UNIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Unit readingUnit;
  
  @Override
  public final Unit getReadingUnit ()
  {
    return this.readingUnit;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FrequencyCounterSettings
  // BYTES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final byte[] bytes;
  
  @Override
  public final byte[] getBytes ()
  {
    return this.bytes;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FrequencyCounterSettings
  // INSTRUMENT MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final M instrumentMode;

  @Override
  public final M getInstrumentMode ()
  {
    return this.instrumentMode;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FrequencyCounterSettings
  // GATE TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double gateTime_s;

  @Override
  public final double getGateTime_s ()
  {
    return this.gateTime_s;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    // Must override if this.bytes is not available...
    if (this.bytes == null)
      throw new UnsupportedOperationException ();
    int hash = 7;
    hash = 53 * hash + Arrays.hashCode (this.bytes);
    return hash;
  }

  @Override
  public boolean equals (Object obj)
  {
    // Must override if this.bytes is not available...
    if (this.bytes == null)
      throw new UnsupportedOperationException ();
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass () != obj.getClass ())
      return false;
    final DefaultFrequencyCounterSettings other = (DefaultFrequencyCounterSettings) obj;
    return Arrays.equals (this.bytes, other.bytes);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}