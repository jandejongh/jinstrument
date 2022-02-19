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
package org.javajdj.jinstrument.gpib.sa.hp70000;

import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentStatus;

/** Implementation of {@link InstrumentStatus} for the HP-70000 Spectrum Analyzer [MMS].
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP70000_GPIB_Status
  implements InstrumentStatus
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP70000_GPIB_Status.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private HP70000_GPIB_Status (final byte serialPollStatusByte, final String errorString)
  {
    this.serialPollStatusByte = serialPollStatusByte;
    this.errorString = errorString;
  }
  
  public final static HP70000_GPIB_Status fromSerialPollStatusByte (
    final byte serialPollStatusByte)
  {
    return new HP70000_GPIB_Status (serialPollStatusByte, null);
  }
  
  public final static HP70000_GPIB_Status fromSerialPollStatusByteAndErrorString (
    final byte serialPollStatusByte,
    final String errorString)
  {
    return new HP70000_GPIB_Status (serialPollStatusByte, errorString);
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
  // ERROR STRING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final String errorString;
  
  public final String getErrorString ()
  {
    return this.errorString;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String toString ()
  {
    return "SPoll=0x" + Integer.toHexString (Byte.toUnsignedInt (this.serialPollStatusByte))
             + ", Err=" + this.errorString;

  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS DISSECTION [SERIAL POLL STATUS BYTE]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static boolean isServiceRequest (final byte statusByte)
  {
    return (statusByte & 0x40) != 0;    
  }
  
  public final boolean isServiceRequest ()
  {
    return HP70000_GPIB_Status.isServiceRequest (this.serialPollStatusByte);
  }
  
  public final static boolean isErrorPresent (final byte statusByte)
  {
    return (statusByte & 0x20) != 0;
  }
  
  public final boolean isErrorPresent ()
  {
    return HP70000_GPIB_Status.isErrorPresent (this.serialPollStatusByte);
  }
  
  public final static boolean isCommandComplete (final byte statusByte)
  {
    return (statusByte & 0x10) != 0;        
  }
  
  public final boolean isCommandComplete ()
  {
    return HP70000_GPIB_Status.isCommandComplete (this.serialPollStatusByte);
  }
  
  public final static boolean isEndOfSweep (final byte statusByte)
  {
    return (statusByte & 0x04) != 0;    
  }
  
  public final boolean isEndOfSweep ()
  {
    return HP70000_GPIB_Status.isEndOfSweep (this.serialPollStatusByte);
  }
  
  public final static boolean isMessagePresent (final byte statusByte)
  {
    return (statusByte & 0x02) != 0;    
  }
  
  public final boolean isMessagePresent ()
  {
    return HP70000_GPIB_Status.isMessagePresent (this.serialPollStatusByte);
  }
  
  public final static boolean isTriggerArmed (final byte statusByte)
  {
    return (statusByte & 0x01) != 0;
  }
  
  public final boolean isTriggerArmed ()
  {
    return HP70000_GPIB_Status.isTriggerArmed (this.serialPollStatusByte);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASH CODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    final HP70000_GPIB_Status other = (HP70000_GPIB_Status) obj;
    if (this.serialPollStatusByte != other.serialPollStatusByte)
    {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 17 * hash + this.serialPollStatusByte;
    return hash;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
