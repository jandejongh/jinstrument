/* 
 * Copyright 2010-20201 Jan de Jongh <jfcmdejongh@gmail.com>.
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

import org.javajdj.junits.Unit;
import java.util.Arrays;
import java.util.logging.Logger;

/** Default implementation of {@link SelectiveLevelMeterSettings}.
 * 
 * <p>
 * The object is immutable.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class DefaultSelectiveLevelMeterSettings
  implements SelectiveLevelMeterSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultSelectiveLevelMeterSettings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultSelectiveLevelMeterSettings (
    final byte[] bytes,
    final Unit readingUnit)
  {
    this.bytes = bytes;
    this.readingUnit = readingUnit;
  }

  @Override
  public SelectiveLevelMeterSettings clone () throws CloneNotSupportedException
  {
    return (SelectiveLevelMeterSettings) super.clone ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentSettings
  // READING UNIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Unit readingUnit;
  
  @Override
  public Unit getReadingUnit ()
  {
    return this.readingUnit;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentSettings
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
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 53 * hash + Arrays.hashCode (this.bytes);
    return hash;
  }

  @Override
  public boolean equals (Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass () != obj.getClass ())
    {
      return false;
    }
    final DefaultSelectiveLevelMeterSettings other = (DefaultSelectiveLevelMeterSettings) obj;
    if (! Arrays.equals (this.bytes, other.bytes))
    {
      return false;
    }
    return true;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
