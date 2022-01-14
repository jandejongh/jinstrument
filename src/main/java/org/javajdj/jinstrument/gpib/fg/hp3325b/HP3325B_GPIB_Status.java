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
package org.javajdj.jinstrument.gpib.fg.hp3325b;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentStatus;

/** Implementation of {@link InstrumentStatus} for the HP-3325B.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class HP3325B_GPIB_Status
  implements InstrumentStatus
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP3325B_GPIB_Status.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP3325B_GPIB_Status (final byte serialPollStatusByte, final Integer errorInt)
  {
    this.serialPollStatusByte = serialPollStatusByte;
    this.errorInt = errorInt;
    this.busy = (serialPollStatusByte & 0x80) != 0;
    this.srq = (serialPollStatusByte & 0x40) != 0;
    this.sweepInProgress = (serialPollStatusByte & 0x20) != 0;
    this.zero = (serialPollStatusByte & 0x10) != 0;
    this.hardwareFailure = (serialPollStatusByte & 0x08) != 0;
    this.sweepStarted = (serialPollStatusByte & 0x04) != 0;
    this.sweepStopped = (serialPollStatusByte & 0x02) != 0;
    this.userError = (serialPollStatusByte & 0x01) != 0;
    if (this.zero)
      throw new IllegalArgumentException ();
  }

  public HP3325B_GPIB_Status (final byte serialPollStatusByte)
  {
    this (serialPollStatusByte, null);
  }
  
  public final HP3325B_GPIB_Status withErrorInt (final Integer errorInt)
  {
    return new HP3325B_GPIB_Status (this.serialPollStatusByte, errorInt);
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
  // ERROR INT
  // ERROR STRING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Integer errorInt;

  private boolean supportsErrorString ()
  {
    return (this.errorInt != null) && (isHardwareFailure () || isUserError ());
  }
  
  public static boolean supportsErrorString (final byte serialPollStatusByte)
  {
    return (serialPollStatusByte & 0x10) != 0  // ZERO bit -> must be zero!
      ||   (serialPollStatusByte & 0x08) != 0  // Hardware failure.
      ||   (serialPollStatusByte & 0x01) != 0; // User error.   
  }
  
  public final String getErrorString ()
  {
    if (! supportsErrorString ())
      return null;
    switch (this.errorInt)
    {
      //
      // 3-digit error codes
      //
      case 10:
        return "Hardware failure, DAC range";
      case 11:
        return "Bad checksum, low byte of ROM";
      case 12:
        return "Bad checksum, high byte of ROM";
      case 13:
        return "Machine data bus line stuck low";
      case 14:
        return "Keyboard shift register test failed";
      case 21:
        return "Signal too big during calibration";
      case 22:
        return "Signal too small during calibration";
      case 23:
        return "DC offset too positive during cal";
      case 24:
        return "DC offset too negative during cal";
      case 25:
        return "Unstable/noisy calibration";
      case 26:
        return "Calibration factor out of range: AC gain offset";
      case 27:
        return "Calibration factor out of range: AC gain slope";
      case 28:
        return "Calibration factor out of range: DC offset";
      case 29:
        return "Calibration factor out of range: DC Slope";
      case 30:
        return "External ref unlocked";
      case 31:
        return "Oscillator unlocked, VCO voltage too low";
      case 32:
        return "Oscillator unlocked, VCO voltage too high";
      case 33:
        return "HP-IB isolation circuits test failed self test";
      case 34:
        return "HP-IB IC failed self test";
      case 35:
        return "RS-232 test failed loop-back test";
      case 36:
        return "Memory lost (battery dead)";
      case 37:
        return "Unexpected interrupt";
      case 38:
        return "Sweep-limit-flag signal failed self test";
      case 39:
        return "Fractional-N IC failed self test";
      case 40:
        return "Modulation Source failed self test";
      case 41:
        return "Function-integrity-flag flip-flop always set";
      case 100:
        return "Entry parameter out of bounds";
      case 200:
        return "Invalid units suffix for entry";
      case 201:
        return "Invalid units suffix with high voltage";
      case 300:
        return "Frequency too large for function";
      case 400:
        return "Sweep time too large (same as sweep rate too small)";
      case 401:
        return "Sweep time too small";
      case 500:
        return "Amplitude/offset incompatible";
      case 501:
        return "Offset too big for amplitude";
      case 502:
        return "Amplitude too big for offset";
      case 503:
        return "Amplitude too small";
      case 600:
        return "Sweep frequency improper";
      case 601:
        return "Sweep frequency too large for function";
      case 602:
        return "Sweep bandwidth too small";
      case 603:
        return "Log sweep start freq too small";
      case 604:
        return "Log sweep stop frequency less than start frequency";
      case 605:
        return "Discrete sweep element is empty";
      case 700:
        return "Unknown command";
      case 701:
        return "Illegal query";
      case 751:
        return "Key ignored - in remote (press LOCAL)";
      case 752:
        return "Key ignored - local lockout";
      case 753:
        return "Feature disabled in compatibilty mode";
      case 754:
        return "Attempt to recall a register that has not been stored since power up (Use enhancements mode)";
      case 755:
        return "Amplitude modulation not allowed on selected function (warning only)";
      case 756:
        return "Modulation source arbitrary waveform is empty";
      case 757:
        return "Too many modulation source arbitrary waveform points";
      case 758:
        return "Firmware failure";
      case 759:
        return "Error while running XRUN routine";
      case 800:
        return "Illegal character received";
      case 801:
        return "Illegal digit for selection item";
      case 802:
        return "Illegal binary data block header";
      case 803:
        return "Illegal string, string overflow";
      case 810:
        return "RS-232 overrun - characters lost";
      case 811:
        return "RS-232 parity error";
      case 812:
        return "RS-232 frame error";
      case 900:
        return "Option not installed";
      //
      // 1-digit error codes
      //
      case 0:
        return "Hardware failure";
      case 1:
        return "Entry parameter out of bounds";
      case 2:
        return "Invalid units suffix";
      case 3:
        return "Frequency too large for function";
      case 4:
        return "Sweep time too large or too small";
      case 5:
        return "Offset and/or amplitude error";
      case 6:
        return "Sweep setting error";
      case 7:
        return "Illegal command/input value, key press, firmware failure or error while running XRUN routine";
      case 8:
        return "RS-232 failure";
      case 9:
        return "Option not installed";
      default:
        LOG.log (Level.WARNING, "Unknown HP-3325B error code: {0}!", this.errorInt);
        return "Unknown error code: " + this.errorInt;
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final String toString ()
  {
    return "0x" + Integer.toHexString (Byte.toUnsignedInt (this.serialPollStatusByte));
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BUSY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean busy;

  public final boolean isBusy ()
  {
    return this.busy;
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
  // SWEEP IN PROGRESS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean sweepInProgress;
  
  public final boolean isSweepInProgress ()
  {
    return this.sweepInProgress;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ZERO
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean zero;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HARDWARE FAILURE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean hardwareFailure;
  
  public final boolean isHardwareFailure ()
  {
    return this.hardwareFailure;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWEEP STARTED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean sweepStarted;
  
  public final boolean isSweepStarted ()
  {
    return this.sweepStarted;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWEEP STOPPED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean sweepStopped;
  
  public final boolean isSweepStopped ()
  {
    return this.sweepStopped;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // USER ERROR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean userError;
  
  public final boolean isUserError ()
  {
    return this.userError;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HASH CODE / EQUALS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final int hashCode ()
  {
    int hash = 5;
    hash = 37 * hash + this.serialPollStatusByte;
    hash = 37 * hash + Objects.hashCode (this.errorInt);
    return hash;
  }

  @Override
  public final boolean equals (Object obj)
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
    final HP3325B_GPIB_Status other = (HP3325B_GPIB_Status) obj;
    if (this.serialPollStatusByte != other.serialPollStatusByte)
    {
      return false;
    }
    return Objects.equals (this.errorInt, other.errorInt);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
