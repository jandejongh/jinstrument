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
package org.javajdj.jinstrument.gpib.psu.hp6033a;

import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentStatus;

/** Implementation of {@link InstrumentStatus} for the HP-6033A.
 *
 * <p>
 * This implementation requires both the status byte of a GPIB Serial Poll,
 * as well as the Status Register present in the result from the 'STS?' command on the HP-6033A.
 * Both values should be obtained as close in time as possible.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class HP6033A_GPIB_Status
  implements InstrumentStatus
{

  private static final Logger LOG = Logger.getLogger (HP6033A_GPIB_Status.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP6033A_GPIB_Status (
    final byte serialPollStatusByte,
    final int statusRegister,
    final int errorNumber)
  {
    this.serialPollStatusByte = serialPollStatusByte;
    if ((statusRegister & 0xfffffe00) != 0)
      throw new IllegalArgumentException ();
    // XXX Sanity checks...
    this.statusRegister = statusRegister;
    if (errorNumber < 0 || errorNumber > 9)
      throw new IllegalArgumentException ();
    this.errorNumber = errorNumber;
    // XXX LOG Warning in case of error...
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
  // STATUS REGISTER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final int statusRegister;
  
  public final int getStatusRegister ()
  {
    return this.statusRegister;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ERROR CODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final int errorNumber;
  
  public final int getErrorNumber ()
  {
    return this.errorNumber;
  }
  
  public final String getErrorString ()
  {
    switch (this.errorNumber)
    {
      case 0: return "No Errors";
      case 1: return "Unrecognized Character";
      case 2: return "Improper Number";
      case 3: return "Unrecognized String";
      case 4: return "Syntax Error";
      case 5: return "Number Out Of Range";
      case 6: return "Attempt To Exceed Soft Limits";
      case 7: return "Improper Soft Limit";
      case 8: return "Data Requested Without A Query Being Sent";
      case 9: return "Relay Error";
      default: throw new RuntimeException ();
    }
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
      + ", StReg=0x" + Integer.toUnsignedString (this.statusRegister, 16)
      + ", Err=" + Integer.toString (this.errorNumber);
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
    hash = 79 * hash + this.serialPollStatusByte;
    hash = 79 * hash + this.statusRegister;
    hash = 79 * hash + this.errorNumber;
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
    final HP6033A_GPIB_Status other = (HP6033A_GPIB_Status) obj;
    if (this.serialPollStatusByte != other.serialPollStatusByte)
    {
      return false;
    }
    if (this.statusRegister != other.statusRegister)
    {
      return false;
    }
    if (this.errorNumber != other.errorNumber)
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
