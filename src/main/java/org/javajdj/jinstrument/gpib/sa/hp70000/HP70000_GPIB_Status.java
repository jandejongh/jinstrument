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

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.logging.Level;
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
  
  private HP70000_GPIB_Status (
    final byte serialPollStatusByte,
    final String errorString,
    final String messageString)
  {
    this.serialPollStatusByte = serialPollStatusByte;
    this.errorString = errorString;
    this.messageString = HP70000_GPIB_Status.sanityMessageString (messageString);
    this.uncalibrated = messageString != null && messageString.charAt (0) == '1';
    this.uncorrected = messageString != null && messageString.charAt (2) == '1';
  }
  
  public final static HP70000_GPIB_Status fromSerialPollStatusByte (
    final byte serialPollStatusByte)
  {
    return new HP70000_GPIB_Status (serialPollStatusByte, null, null);
  }
  
  public final static HP70000_GPIB_Status fromSerialPollStatusByteAndErrorString (
    final byte serialPollStatusByte,
    final String errorString)
  {
    return new HP70000_GPIB_Status (serialPollStatusByte, errorString, null);
  }
  
  public final static HP70000_GPIB_Status fromSerialPollStatusByteAndMessageString (
    final byte serialPollStatusByte,
    final String messageString)
  {
    return new HP70000_GPIB_Status (serialPollStatusByte, null, messageString);
  }
  
  public final static HP70000_GPIB_Status fromSerialPollStatusByteAndErrorStringAndMessageString (
    final byte serialPollStatusByte,
    final String errorString,
    final String messageString)
  {
    return new HP70000_GPIB_Status (serialPollStatusByte, errorString, messageString);
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
  // ERROR STRING [ERR?]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  // XXX Need to interpret error string.
  
  private final String errorString;
  
  public final String getErrorString ()
  {
    return this.errorString;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MESSAGE STRING [MSG?]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final String messageString;
  
  public final String getMessageString ()
  {
    return this.messageString;
  }
  
  public final String getMessageStringCooked ()
  {
    if (this.messageString == null || this.messageString.length () == 8)
      return null;
    else
      return this.messageString.substring (8);
  }
  
  private static String sanityMessageString (final String messageString)
  {
    if (messageString == null)
      return null;
    if (messageString.length () < 8)
    {
      // XXX
      LOG.log (Level.SEVERE, "Found unexpected message String: {0}", messageString);
      throw new IllegalArgumentException ();
    }
    if (messageString.charAt (0) != '0' && messageString.charAt (0) != '1')
      throw new IllegalArgumentException ();
    if (messageString.charAt (1) != ',')
      throw new IllegalArgumentException ();
    if (messageString.charAt (2) != '0' && messageString.charAt (2) != '1')
      throw new IllegalArgumentException ();
    if (messageString.charAt (3) != ',')
      throw new IllegalArgumentException ();
    if (messageString.charAt (4) != '#')
      throw new IllegalArgumentException ();
    if (messageString.charAt (5) != 'A')
      throw new IllegalArgumentException ();
    final byte msb = messageString.getBytes (Charset.forName ("US-ASCII"))[6];
    final byte lsb = messageString.getBytes (Charset.forName ("US-ASCII"))[7];
    final int length = (msb & 0xff) * 256 + (lsb & 0xff);
    if (messageString.length () != 8 + length)
    {
      LOG.log (Level.WARNING, "Unexpected format for message string {0}, length = {1} (should be {2}).",
        new Object[]{messageString, messageString.length (), 8 + length});
      // XXX This is not very constructive...
      throw new IllegalArgumentException ();
    }
    return messageString;
  }
  
  private final boolean uncalibrated;
  
  public final boolean isUncalibrated ()
  {
    return this.uncalibrated;
  }
  
  private final boolean uncorrected;
  
  public final boolean isUncorrected ()
  {
    return this.uncorrected;
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
             + ", Err=" + this.errorString
             + ", Msg=" + this.messageString;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASH CODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final int hashCode ()
  {
    int hash = 3;
    hash = 89 * hash + this.serialPollStatusByte;
    hash = 89 * hash + Objects.hashCode (this.errorString);
    hash = 89 * hash + Objects.hashCode (this.messageString);
    return hash;
  }

  @Override
  public final boolean equals (Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass () != obj.getClass ())
      return false;
    final HP70000_GPIB_Status other = (HP70000_GPIB_Status) obj;
    if (this.serialPollStatusByte != other.serialPollStatusByte)
      return false;
    if (! Objects.equals (this.errorString, other.errorString))
      return false;
    return Objects.equals (this.messageString, other.messageString);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
