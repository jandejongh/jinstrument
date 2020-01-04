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
package org.javajdj.jinstrument.gpib.fg.hp8116a;

import org.javajdj.jinstrument.InstrumentStatus;

/** Implementation of {@link InstrumentStatus} for the HP-8116A
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP8116A_GPIB_Status
  implements InstrumentStatus
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP8116A_GPIB_Status (final byte serialPollStatusByte)
  {
    this.serialPollStatusByte = serialPollStatusByte;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERIAL POLL BYTE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final byte serialPollStatusByte;

  public final byte getSerialPollStatusByte ()
  {
    return this.serialPollStatusByte;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String toString ()
  {
    return "0x" + Integer.toHexString (Byte.toUnsignedInt (this.serialPollStatusByte));
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HASH CODE / EQUALS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    int hash = 5;
    hash = 89 * hash + this.serialPollStatusByte;
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
    final HP8116A_GPIB_Status other = (HP8116A_GPIB_Status) obj;
    if (this.serialPollStatusByte != other.serialPollStatusByte)
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
