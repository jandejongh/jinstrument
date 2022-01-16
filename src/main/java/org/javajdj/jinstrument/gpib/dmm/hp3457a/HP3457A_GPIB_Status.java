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
package org.javajdj.jinstrument.gpib.dmm.hp3457a;

import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentStatus;

/** Implementation of {@link InstrumentStatus} for the HP-3457A.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP3457A_GPIB_Status
  implements InstrumentStatus
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP3457A_GPIB_Status.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private HP3457A_GPIB_Status (final byte serialPollStatusByte)
  {
    this.serialPollStatusByte = serialPollStatusByte;
  }
  
  public final static HP3457A_GPIB_Status fromSerialPollStatusByte (
    final byte serialPollStatusByte)
  {
    return new HP3457A_GPIB_Status (serialPollStatusByte);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERIAL POLL STATUS BYTE
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
    return "SPoll=0x" + Integer.toHexString (Byte.toUnsignedInt (this.serialPollStatusByte));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS DISSECTION [SERIAL POLL STATUS BYTE]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final boolean isUnused ()
  {
    return (this.serialPollStatusByte & 0x80) != 0;
  }
    
  public final boolean isServiceRequest ()
  {
    return (this.serialPollStatusByte & 0x40) != 0;    
  }
  
  public final boolean isError ()
  {
    return (this.serialPollStatusByte & 0x20) != 0;
  }
  
  public final boolean isReady ()
  {
    return (this.serialPollStatusByte & 0x10) != 0;        
  }
  
  public final boolean isPowerOn ()
  {
    return (this.serialPollStatusByte & 0x08) != 0;    
  }
  
  public final boolean isFrontPanel ()
  {
    return (this.serialPollStatusByte & 0x04) != 0;    
  }
  
  public final boolean isHighLow ()
  {
    return (this.serialPollStatusByte & 0x02) != 0;    
  }
  
  public final boolean isProgramComplete ()
  {
    return (this.serialPollStatusByte & 0x01) != 0;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASH CODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 53 * hash + this.serialPollStatusByte;
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
    final HP3457A_GPIB_Status other = (HP3457A_GPIB_Status) obj;
    return this.serialPollStatusByte == other.serialPollStatusByte;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
