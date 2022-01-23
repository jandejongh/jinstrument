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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentStatus;

/** Implementation of {@link InstrumentStatus} for the HP-3457A.
 * 
 * <p>
 * Supports serial-poll status byte, errors ({@code ERR?}) and auxiliary errors ({@code AUXERR?}).
 * 
 * <p>
 * Instances are immutable and proper {@link #hashCode} and {@link #equals} implementations are provided.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class HP3457A_GPIB_Status
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
  
  private HP3457A_GPIB_Status (final byte serialPollStatusByte, final Short errorsShort, final Short auxiliaryErrorsShort)
  {
    this.serialPollStatusByte = serialPollStatusByte;
    this.errorsShort = errorsShort;
    this.errors = (this.errorsShort != null ? Error.fromShort (this.errorsShort) : null);
    this.auxiliaryErrorsShort = auxiliaryErrorsShort;
    this.auxiliaryErrors = (this.auxiliaryErrorsShort != null ? AuxiliaryError.fromShort (this.auxiliaryErrorsShort) : null);
  }
  
  public final static HP3457A_GPIB_Status fromSerialPollStatusByte (
    final byte serialPollStatusByte)
  {
    return new HP3457A_GPIB_Status (serialPollStatusByte, null, null);
  }
  
  public final static HP3457A_GPIB_Status fromSerialPollStatusByteAndErrorsShort (
    final byte serialPollStatusByte, final short errorsShort)
  {
    return new HP3457A_GPIB_Status (serialPollStatusByte, errorsShort, null);
  }
  
  public final static HP3457A_GPIB_Status fromSerialPollStatusByteAndErrorsAndAuxiliaryErrorsShort (
    final byte serialPollStatusByte, final short errorsShort, final short auxiliaryErrors)
  {
    return new HP3457A_GPIB_Status (serialPollStatusByte, errorsShort, auxiliaryErrors);
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERIAL POLL STATUS BYTE [SPOLL; STB?]
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
    return "0x"
      + Integer.toHexString (Byte.toUnsignedInt (this.serialPollStatusByte))
      + (this.errorsShort != null ? ("[errors=" + Short.toString (this.errorsShort) + "]") : "")
      + (this.auxiliaryErrorsShort != null ? ("[aux errors=" + Short.toString (this.auxiliaryErrorsShort) + "]") : "");
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS DISSECTION [SERIAL POLL STATUS BYTE]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final boolean isUnusedSet ()
  {
    return (this.serialPollStatusByte & 0x80) != 0;
  }
  
  /** Indicates that service is requested and the SRQ line is set true.
   * 
   * @return Whether service is requested and the SRQ line is set true.
   * 
   */
  public final boolean isServiceRequest ()
  {
    return (this.serialPollStatusByte & 0x40) != 0;    
  }
  
  /** Indicates that an error has been logged into the error register.
   * 
   * @return Whether an error has been logged into the error register.
   * 
   */
  public final boolean isError ()
  {
    return (this.serialPollStatusByte & 0x20) != 0;
  }
  
  /** Given a serial-poll status byte, indicates whether an error has been logged into the error register.
   * 
   * @param serialPollStatusByte The serial-poll status byte.
   * 
   * @return Whether an error has been logged into the error register (for given serial-poll status byte).
   * 
   */
  public static boolean isError (final byte serialPollStatusByte)
  {
    return (serialPollStatusByte & 0x20) != 0;
  }
  
  /** Indicates that the HP 3457 has completed execution of all commands and is ready to accept more commands or trigger events.
   * 
   * @return Whether the HP 3457 has completed execution of all commands and is ready to accept more commands or trigger events.
   * 
   */
  public final boolean isReady ()
  {
    return (this.serialPollStatusByte & 0x10) != 0;        
  }
  
  /** Indicates that a power-on cycle has occurred.
   * 
   * @return Whether a power-on cycle has occurred.
   * 
   */
  public final boolean isPowerOn ()
  {
    return (this.serialPollStatusByte & 0x08) != 0;    
  }
  
  /** Indicates that the SRQ command has been executed from the front panel.
   * 
   * @return Whether the SRQ command has been executed from the front panel.
   * 
   */
  public final boolean isFrontPanel ()
  {
    return (this.serialPollStatusByte & 0x04) != 0;    
  }
  
  /** Indicates that the high or low limit of the PFAIL math operation has been exceeded.
   * 
   * @return Whether the high or low limit of the PFAIL math operation has been exceeded.
   * 
   */
  public final boolean isHighLow ()
  {
    return (this.serialPollStatusByte & 0x02) != 0;    
  }
  
  /** Indicates that a stored subprogram has been executed.
   * 
   * @return Whether a stored subprogram has been executed.
   * 
   */
  public final boolean isProgramComplete ()
  {
    return (this.serialPollStatusByte & 0x01) != 0;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ERROR [ERR?]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum Error
  {
    
    HARDWARE            ((short) 0x0001, "Hardware error"),
    CALIBRATION         ((short) 0x0002, "CAL/ACAL Calibration error"),
    TRIGGER_TOO_FAST    ((short) 0x0004, "Trigger too fast"),
    SYNTAX              ((short) 0x0008, "Bad syntax"),
    BAD_HEADER          ((short) 0x0010, "Unrecognizable command"),
    BAD_PARAMETER       ((short) 0x0020, "Unrecognizable or mismatched parameter"),
    BAD_PARAMETER_RANGE ((short) 0x0040, "Parameter out of range"),
    PARAMETER_REQUIRED  ((short) 0x0080, "Parameter missing"),
    PARAMETER_IGNORED   ((short) 0x0100, "Parameter ignored"),
    NOT_CALIBRATED      ((short) 0x0200, "Not or poorly calibrated"),
    AUTOCAL_REQUIRED    ((short) 0x0400, "Must perform autocalibration");
    
    private Error (final short bit, final String errorMessage)
    {
      this.bit = bit;
      this.errorMessage = errorMessage;
    }
    
    private final short bit;
    
    public final short getBit ()
    {
      return this.bit;
    }
    
    private final String errorMessage;
    
    public static Error fromBit (final short bit)
    {
      switch (bit)
      {
        case 0x0001: return HARDWARE;
        case 0x0002: return CALIBRATION;
        case 0x0004: return TRIGGER_TOO_FAST;
        case 0x0008: return SYNTAX;
        case 0x0010: return BAD_HEADER;
        case 0x0020: return BAD_PARAMETER;
        case 0x0040: return BAD_PARAMETER_RANGE;
        case 0x0080: return PARAMETER_REQUIRED;
        case 0x0100: return PARAMETER_IGNORED;
        case 0x0200: return NOT_CALIBRATED;
        case 0x0400: return AUTOCAL_REQUIRED;
        default:     throw new IllegalArgumentException ();
      }
    }
    
    public static EnumSet<Error> fromShort (final short code)
    {
      if ((code & 0xF8000) != 0)
        throw new IllegalArgumentException ();
      final EnumSet<Error> errors = EnumSet.noneOf (Error.class);
      for (final Error error : Error.values ())
        if ((code & error.getBit ()) != 0)
          errors.add (error);
      return errors;
    }
    
    @Override
    public final String toString ()
    {
      return this.errorMessage;
    }
    
  }
  
  private final Short errorsShort;
  
  public final Short getErrorsShort ()
  {
    return this.errorsShort;
  }
  
  /** Given an errors short, indicates whether a hardware error has occurred.
   * 
   * @param errorsShort The errors short (as returned by {@code ERR?}).
   * 
   * @return Whether a hardware error has occurred (for given errors short).
   * 
   * @see Error#HARDWARE
   * 
   */
  public static boolean isHardwareError (final short errorsShort)
  {
    return (errorsShort & Error.HARDWARE.getBit ()) != 0;
  }
  
  public final boolean isHardwareError ()
  {
    return this.errorsShort != null && HP3457A_GPIB_Status.isHardwareError (this.errorsShort);
  }
  
  private final EnumSet<Error> errors;
    
  public final Set<Error> getErrors ()
  {
    return Collections.unmodifiableSet (this.errors);
  }
  
  public final String getErrorsString ()
  {
    return Objects.toString (this.errors.toString ());
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AUXILIARY ERROR [AUXERR?]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum AuxiliaryError
  {
    
    ISOLATION               ((short) 0x0001, "Isolation error during HP 3457 operation in any mode"
                                               + "(self-test, ACAL, measurements, etc."),
    SLAVE_PROCESSOR         ((short) 0x0002, "Slave processor self-test failed"),
    ISOLATION_SELF_TEST     ((short) 0x0004, "Isolation self-test failed"),
    INTEGRATOR              ((short) 0x0008, "Integrator convergence error"),
    FRONT_END_ZERO          ((short) 0x0010, "Front end zero measurement error"),
    CURRENT_GAIN_DIVIDER    ((short) 0x0020, "Current source, gain selection, or input divider failure"),
    AMPS                    ((short) 0x0040, "Amps self-test failed"),
    AC_AMP_DC_OFFSET        ((short) 0x0080, "AC amplifier's DC offset check failed"),
    AC_SELF_TEST            ((short) 0x0100, "AC self-test failed"),
    OHMS_PRECHARGE          ((short) 0x0200, "Ohm's precharge failure during ACAL"),
    ROM_32K                 ((short) 0x0400, "32k ROM failure"),
    ROM_8K                  ((short) 0x0800, "8k ROM failure"),
    NVRAM                   ((short) 0x1000, "Non-volatile RAM test failed"),
    VRAM                    ((short) 0x2000, "Volatile RAM test failed"),
    CAL_RAM_PROTECT         ((short) 0x4000, "Calibration RAM protect circuit failure");
    
    private AuxiliaryError (final short bit, final String errorMessage)
    {
      this.bit = bit;
      this.errorMessage = errorMessage;
    }
    
    private final short bit;
    
    public final short getBit ()
    {
      return this.bit;
    }
    
    private final String errorMessage;
    
    public static AuxiliaryError fromBit (final short bit)
    {
      switch (bit)
      {
        case 0x0001: return ISOLATION;
        case 0x0002: return SLAVE_PROCESSOR;
        case 0x0004: return ISOLATION_SELF_TEST;
        case 0x0008: return INTEGRATOR;
        case 0x0010: return FRONT_END_ZERO;
        case 0x0020: return CURRENT_GAIN_DIVIDER;
        case 0x0040: return AMPS;
        case 0x0080: return AC_AMP_DC_OFFSET;
        case 0x0100: return AC_SELF_TEST;
        case 0x0200: return OHMS_PRECHARGE;
        case 0x0400: return ROM_32K;
        case 0x0800: return ROM_8K;
        case 0x1000: return NVRAM;
        case 0x2000: return VRAM;
        case 0x4000: return CAL_RAM_PROTECT;
        default:     throw new IllegalArgumentException ();
      }
    }
    
    public static EnumSet<AuxiliaryError> fromShort (final short code)
    {
      if ((code & 0x80000) != 0)
        throw new IllegalArgumentException ();
      final EnumSet<AuxiliaryError> errors = EnumSet.noneOf (AuxiliaryError.class);
      for (final AuxiliaryError error : AuxiliaryError.values ())
        if ((code & error.getBit ()) != 0)
          errors.add (error);
      return errors;
    }
    
    @Override
    public String toString ()
    {
      return this.errorMessage;
    }
    
  }
  
  private final Short auxiliaryErrorsShort;
  
  public final Short getAuxiliaryErrorsShort ()
  {
    return this.auxiliaryErrorsShort;
  }
  
  private final EnumSet<AuxiliaryError> auxiliaryErrors;
    
  public final Set<AuxiliaryError> getAuxiliaryErrors ()
  {
    return Collections.unmodifiableSet (this.auxiliaryErrors);
  }
  
  public final String getAuxiliaryErrorsString ()
  {
    return Objects.toString (this.auxiliaryErrors.toString ());
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASH CODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final int hashCode ()
  {
    int hash = 5;
    hash = 83 * hash + this.serialPollStatusByte;
    hash = 83 * hash + Objects.hashCode (this.errorsShort);
    hash = 83 * hash + Objects.hashCode (this.auxiliaryErrorsShort);
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
    final HP3457A_GPIB_Status other = (HP3457A_GPIB_Status) obj;
    if (this.serialPollStatusByte != other.serialPollStatusByte)
      return false;
    if (! Objects.equals (this.errorsShort, other.errorsShort))
      return false;
    return Objects.equals (this.auxiliaryErrorsShort, other.auxiliaryErrorsShort);
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
