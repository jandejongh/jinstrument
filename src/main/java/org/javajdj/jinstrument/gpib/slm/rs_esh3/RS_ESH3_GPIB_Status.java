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
package org.javajdj.jinstrument.gpib.slm.rs_esh3;

import org.javajdj.jinstrument.InstrumentStatus;

/** Implementation of {@link InstrumentStatus} for the {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class RS_ESH3_GPIB_Status
  implements InstrumentStatus
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public RS_ESH3_GPIB_Status (final byte serialPollStatusByte)
  {
    this.serialPollStatusByte = serialPollStatusByte;
    this.extension = (serialPollStatusByte & 0x80) != 0;
    this.srq = (serialPollStatusByte & 0x40) != 0;
    this.abnormal = (serialPollStatusByte & 0x20) != 0;
    this.ready = (serialPollStatusByte & 0x10) != 0;
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
  // ERROR STRING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final String getErrorString ()
  {
    if (isAbnormal ())
    {
      if (isExtension ())
      {
        switch (this.serialPollStatusByte & 0xf)
        {
          case 0x00: return "GPIB Talker: No Listener";
          default: return "Unknown Extension in Status Byte";
        }
      }
      else
      {
        switch (this.serialPollStatusByte & 0xf)
        {
          case 0x00: return "IEC Syntax Error";
          case 0x01: return "IEC Instruction Conflicts with State";
          case 0x02: return "Data Above Limit";
          case 0x03: return "Data Below Limit";
          case 0x04: return "Memory Register not Occupied when RCL";
          case 0x05: return "Level/Offset Calibration";
          case 0x06: return "Calibration Checking Error";
          case 0x07: return "Calibration Correction > 6 dB";
          case 0x08: return "Scanning Missing Parameters";
          case 0x09: return "fStart > fStop";
          case 0x0a: return "fStart == fStop [XY/ZSG3]";
          case 0x0b: return "Max Level <= Min Level";
          case 0x0c: return "fStart/fStop < 1.4 [Log X Axis]";
          case 0x0d: return "Log X Axis and ZSG3";
          case 0x0e: return "Synth out of Sync";
          case 0x0f: return "Faulty Supply Voltage(s)";
          default: throw new RuntimeException ();
        }
      }
    }
    else
      return null;
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
  // EXTENSION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean extension;

  public final boolean isExtension ()
  {
    return this.extension;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SRQ
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean srq;
  
  public final boolean isSrq ()
  {
    return this.srq;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ABNORMAL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean abnormal;
  
  public final boolean isAbnormal ()
  {
    return this.abnormal;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean ready;
  
  public final boolean isReady ()
  {
    return this.ready;
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
      return true;
    if (obj == null)
      return false;
    if (getClass () != obj.getClass ())
      return false;
    final RS_ESH3_GPIB_Status other = (RS_ESH3_GPIB_Status) obj;
    return this.serialPollStatusByte == other.serialPollStatusByte;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
