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
public class HP6033A_GPIB_Status
  implements InstrumentStatus
{

  private static final Logger LOG = Logger.getLogger (HP6033A_GPIB_Status.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private HP6033A_GPIB_Status (final byte serialPollStatusByte, final int statusRegister)
  {
    this.serialPollStatusByte = serialPollStatusByte;
    if ((statusRegister & 0xfffffe00) != 0)
      throw new IllegalArgumentException ();
    this.statusRegister = statusRegister;
    // XXX Sanity checks...
    // XXX LOG Warning in case of error...
  }
  
  public final static HP6033A_GPIB_Status fromSerialPollStatusByteAndStatusRegister (
    final byte serialPollStatusByte,
    final int statusRegister)
  {
    return new HP6033A_GPIB_Status (serialPollStatusByte, statusRegister);
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
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String toString ()
  {
    return "SPoll=0x" + Integer.toHexString (Byte.toUnsignedInt (this.serialPollStatusByte))
      + ", StReg=0x" + Integer.toUnsignedString (this.statusRegister, 16);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS DISSECTION [SERIAL POLL STATUS BYTE]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final boolean isPowerOnReset ()
  {
    return (this.serialPollStatusByte & 0x80) != 0;
  }
    
  public final boolean isServiceRequest ()
  {
    return (this.serialPollStatusByte & 0x40) != 0;    
  }
  
  public final boolean isCalibrationFailed ()
  {
    return (this.serialPollStatusByte & 0x20) != 0;
  }
  
  public final boolean isFrontPanelServiceRequest ()
  {
    return (this.serialPollStatusByte & 0x10) != 0;        
  }
  
  public final boolean isHardwareError ()
  {
    return (this.serialPollStatusByte & 0x08) != 0;    
  }
  
  public final boolean isSyntaxError ()
  {
    return (this.serialPollStatusByte & 0x04) != 0;    
  }
  
  public final boolean isDataReady ()
  {
    return (this.serialPollStatusByte & 0x01) != 0;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS REGISTER DISSECTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final boolean hasError ()
  {
    return this.statusRegister != 0;
  }
  
  public final boolean hasADLinkError ()
  {
    return ((this.statusRegister & 0x20) != 0);
  }
    
  public final boolean hasADInternalSelfTestError ()
  {
    return ((this.statusRegister & 0x10) != 0);
  }
    
  public final boolean hasADSlopeError ()
  {
    return ((this.statusRegister & 0x08) != 0);
  }
    
  public final boolean hasROMSelfTestError ()
  {
    return ((this.statusRegister & 0x04) != 0);
  }
    
  public final boolean hasRAMSelfTestError ()
  {
    return ((this.statusRegister & 0x02) != 0);
  }
    
  public final boolean hasCalRAMError ()
  {
    return ((this.statusRegister & 0x01) != 0);
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASH CODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    int hash = 5;
    hash = 37 * hash + this.serialPollStatusByte;
    hash = 37 * hash + this.statusRegister;
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
    return true;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
