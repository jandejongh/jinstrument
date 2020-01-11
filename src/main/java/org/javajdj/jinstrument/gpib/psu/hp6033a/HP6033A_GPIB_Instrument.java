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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultPowerSupplyUnitReading;
import org.javajdj.jinstrument.DefaultPowerSupplyUnitSettings;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.PowerSupplyUnit;
import org.javajdj.jinstrument.PowerSupplyUnitSettings;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.controller.gpib.GpibControllerCommand;
import org.javajdj.jinstrument.gpib.psu.AbstractGpibPowerSupplyUnit;

/** Implementation of {@link Instrument} and {@link PowerSupplyUnit} for the HP-6033A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP6033A_GPIB_Instrument
extends AbstractGpibPowerSupplyUnit
implements PowerSupplyUnit
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP6033A_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP6033A_GPIB_Instrument (final GpibDevice device)
  {
    super ("HP-6033A", device, null, null, true, true, true, true, false);
    setStatusCollectorPeriod_s (0.5);
    setReadingCollectorPeriod_s (0.5);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static InstrumentType INSTRUMENT_TYPE = new InstrumentType ()
  {
    
    @Override
    public final String getInstrumentTypeUrl ()
    {
      return "HP-6033A [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP6033A_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP6033A_GPIB_Instrument ((GpibDevice) device);
    }
    
  };

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getInstrumentUrl ()
  {
    return HP6033A_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
  }

  @Override
  public String toString ()
  {
    return getInstrumentUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // POWER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public boolean isPoweredOn ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public void setPoweredOn (final boolean poweredOn)
  {
    throw new UnsupportedOperationException ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT STATUS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  protected InstrumentStatus getStatusFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final GpibDevice gpibDevice = (GpibDevice) getDevice ();
    final byte serialPollStatusByte = gpibDevice.serialPollSync (getSerialPollTimeout_ms ());
    final String statusRegisterString = writeAndReadlnSync ("STS?\r\n").trim ();
    if (! statusRegisterString.toUpperCase ().startsWith ("STS"))
      throw new IOException ();
    final int statusRegister;
    try
    {
      statusRegister = Integer.parseInt (statusRegisterString.substring (3).trim ());
    }
    catch (NumberFormatException | IndexOutOfBoundsException e)
    {
      throw new IOException (e);
    }
    return HP6033A_GPIB_Status.fromSerialPollStatusByteAndStatusRegister (serialPollStatusByte, statusRegister);
  }

  @Override
  protected void requestStatusFromInstrumentASync ()
    throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public InstrumentSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final GpibControllerCommand[] atomicSequenceCommands = new GpibControllerCommand[]
    {
      // REMEMBER TO PICK UP THE VALUE BELOW!
      generateWriteAndReadlnCommand ("VMAX?\r\n"),
      generateWriteAndReadlnCommand ("IMAX?\r\n"),
      generateWriteAndReadlnCommand ("VSET?\r\n"),
      generateWriteAndReadlnCommand ("ISET?\r\n"),
      generateWriteAndReadlnCommand ("OUT?\r\n")
    };
    atomicSequenceSync (atomicSequenceCommands);
    int i = 0;
    final String softLimitVoltage_V_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! softLimitVoltage_V_String.toUpperCase ().startsWith ("VMAX"))
      throw new IOException ();
    final double softLimitVoltage_V = Double.parseDouble (softLimitVoltage_V_String.substring (4).trim ());
    final String softLimitCurrent_A_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! softLimitCurrent_A_String.toUpperCase ().startsWith ("IMAX"))
      throw new IOException ();
    final double softLimitCurrent_A = Double.parseDouble (softLimitCurrent_A_String.substring (4).trim ());
    final String setVoltage_V_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! setVoltage_V_String.toUpperCase ().startsWith ("VSET"))
      throw new IOException ();
    final double setVoltage_V = Double.parseDouble (setVoltage_V_String.substring (4).trim ());
    final String setCurrent_A_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! setCurrent_A_String.toUpperCase ().startsWith ("ISET"))
      throw new IOException ();
    final double setCurrent_A = Double.parseDouble (setCurrent_A_String.substring (4).trim ());
    final String outputEnable_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! outputEnable_String.toUpperCase ().startsWith ("OUT"))
      throw new IOException ();
    final boolean outputEnable;
    switch (outputEnable_String.substring (3).trim ())
    {
      case "0": outputEnable = false; break;
      case "1": outputEnable = true; break;
      default: throw new IOException ();
    }
    return new DefaultPowerSupplyUnitSettings (
      null,
      softLimitVoltage_V,
      softLimitCurrent_A,
      Double.NaN,
      setVoltage_V,
      setCurrent_A,
      Double.NaN,
      outputEnable);
  }
 
  @Override
  protected void requestSettingsFromInstrumentASync ()
    throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  protected final InstrumentReading getReadingFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final GpibControllerCommand[] atomicSequenceCommands = new GpibControllerCommand[]
    {
      // REMEMBER TO PICK UP THE VALUE BELOW!
      generateWriteAndReadlnCommand ("VMAX?\r\n"),
      generateWriteAndReadlnCommand ("IMAX?\r\n"),
      generateWriteAndReadlnCommand ("VSET?\r\n"),
      generateWriteAndReadlnCommand ("ISET?\r\n"),
      generateWriteAndReadlnCommand ("OUT?\r\n"),
      generateWriteAndReadlnCommand ("VOUT?\r\n"),
      generateWriteAndReadlnCommand ("IOUT?\r\n"),
      generateWriteAndReadlnCommand ("STS?\r\n")
    };
    atomicSequenceSync (atomicSequenceCommands);
    int i = 0;
    final String softLimitVoltage_V_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! softLimitVoltage_V_String.toUpperCase ().startsWith ("VMAX"))
      throw new IOException ();
    final double softLimitVoltage_V = Double.parseDouble (softLimitVoltage_V_String.substring (4).trim ());
    final String softLimitCurrent_A_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! softLimitCurrent_A_String.toUpperCase ().startsWith ("IMAX"))
      throw new IOException ();
    final double softLimitCurrent_A = Double.parseDouble (softLimitCurrent_A_String.substring (4).trim ());
    final String setVoltage_V_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! setVoltage_V_String.toUpperCase ().startsWith ("VSET"))
      throw new IOException ();
    final double setVoltage_V = Double.parseDouble (setVoltage_V_String.substring (4).trim ());
    final String setCurrent_A_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! setCurrent_A_String.toUpperCase ().startsWith ("ISET"))
      throw new IOException ();
    final double setCurrent_A = Double.parseDouble (setCurrent_A_String.substring (4).trim ());
    final String outputEnable_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! outputEnable_String.toUpperCase ().startsWith ("OUT"))
      throw new IOException ();
    final boolean outputEnable;
    switch (outputEnable_String.substring (3).trim ())
    {
      case "0": outputEnable = false; break;
      case "1": outputEnable = true; break;
      default: throw new IOException ();
    }
    final String readVoltage_V_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! readVoltage_V_String.toUpperCase ().startsWith ("VOUT"))
      throw new IOException ();
    final double readVoltage_V = Double.parseDouble (readVoltage_V_String.substring (4));
    final String readCurrent_A_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! readCurrent_A_String.toUpperCase ().startsWith ("IOUT"))
      throw new IOException ();
    final double readCurrent_A = Double.parseDouble (readCurrent_A_String.substring (4));
    final String statusRegister_String =
      new String (
        ((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)),
        Charset.forName ("US-ASCII")).trim ();
    if (! statusRegister_String.toUpperCase ().startsWith ("STS"))
      throw new IOException ();
    final int statusRegister = Integer.parseInt (statusRegister_String.substring (3).trim ());
    final PowerSupplyMode powerSupplyMode;
    switch (statusRegister & 0x03)
    {
      case 0:  powerSupplyMode = PowerSupplyMode.NONE; break;
      case 1:  powerSupplyMode = PowerSupplyMode.CV; break;
      case 2:  powerSupplyMode = PowerSupplyMode.CC; break;
      case 3:  throw new UnsupportedOperationException ();
      default: throw new RuntimeException ();
    }
    final PowerSupplyUnitSettings settings =
      new DefaultPowerSupplyUnitSettings (
        null,
        softLimitVoltage_V,
        softLimitCurrent_A,
        Double.NaN,
        setVoltage_V,
        setCurrent_A,
        Double.NaN,
        outputEnable);
    settingsReadFromInstrument (settings);
    return new DefaultPowerSupplyUnitReading (
      settings,
      readVoltage_V,
      readCurrent_A,
      Double.NaN,
      powerSupplyMode,
      false,
      null,
      false,
      false);
  }

  @Override
  protected final void requestReadingFromInstrumentASync () throws IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractGpibInstrument
  // ON GPIB SERVICE REQUEST FROM INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected void onGpibServiceRequestFromInstrument ()
    throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnit
  // HARD LIMITS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final double getHardLimitVoltage_V ()
  {
    return 20;
  }

  @Override
  public final double getHardLimitCurrent_A ()
  {
    return 30;
  }

  @Override
  public final double getHardLimitPower_W ()
  {
    return 200; // 240?? Agilent 5959-3342 is not particularly clear on this (gives two values, 200 and 240; taking lowest).
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnit
  // SOFT LIMITS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final boolean isSupportsSoftLimitVoltage ()
  {
    // Confirmed 20200105 - Agilent Part Number 5959-3342.
    return true;
  }

  @Override
  public final boolean isSupportsSoftLimitCurrent ()
  {
    // Confirmed 20200105 - Agilent Part Number 5959-3342.
    return true;
  }

  @Override
  public final boolean isSupportsSoftLimitPower ()
  {
    // Confirmed 20200105 - Agilent Part Number 5959-3342.
    return false;
  }

  @Override
  public final void setSoftLimitPower_W (final double softLimitPower_W)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    throw new UnsupportedOperationException ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnit
  // SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final boolean isSupportsSetVoltage ()
  {
    // Confirmed 20200105 - Agilent Part Number 5959-3342.
    return true;
  }

  @Override
  public final boolean isSupportsSetCurrent ()
  {
    // Confirmed 20200105 - Agilent Part Number 5959-3342.
    return true;
  }

  @Override
  public final boolean isSupportsSetPower ()
  {
    // Confirmed 20200105 - Agilent Part Number 5959-3342.
    return false;
  }

  @Override
  public final void setSetPower_W (final double setPower_W)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnit
  // READINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final boolean isSupportsReadVoltage ()
  {
    // Confirmed 20200105 - Agilent Part Number 5959-3342.
    return true;
  }
  
  @Override
  public final boolean isSupportsReadCurrent ()
  {
    // Confirmed 20200105 - Agilent Part Number 5959-3342.
    return true;
  }
  
  @Override
  public final boolean isSupportsReadPower ()
  {
    // Confirmed 20200105 - Agilent Part Number 5959-3342.
    return false;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnit
  // OUTPUT ENABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final boolean isSupportsOutputEnable ()
  {
    // Confirmed 20200105 - Agilent Part Number 5959-3342.
    return true;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnit
  // POWER SUPPLY MODES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Confirmed 20200105 - Agilent Part Number 5959-3342.
  private final static PowerSupplyMode[] SUPPORTED_POWER_SUPPLY_MODES = new PowerSupplyMode[]
  {
    PowerSupplyMode.NONE,
    PowerSupplyMode.CV,
    PowerSupplyMode.CC
  };
  
  private final static List<PowerSupplyMode> SUPPORTED_POWER_SUPPLY_MODES_AS_LIST =
    Collections.unmodifiableList (Arrays.asList (HP6033A_GPIB_Instrument.SUPPORTED_POWER_SUPPLY_MODES));
    
  @Override
  public final List<PowerSupplyMode> getSupportedPowerSupplyModes ()
  {
    return HP6033A_GPIB_Instrument.SUPPORTED_POWER_SUPPLY_MODES_AS_LIST;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // PROCESS COMMAND
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  @Override
  protected void processCommand (final InstrumentCommand instrumentCommand)
    throws IOException, InterruptedException, TimeoutException
  {
    if (instrumentCommand == null)
      throw new IllegalArgumentException ();
    if (! instrumentCommand.containsKey (InstrumentCommand.IC_COMMAND_KEY))
      throw new IllegalArgumentException ();
    if (instrumentCommand.get (InstrumentCommand.IC_COMMAND_KEY) == null)
      throw new IllegalArgumentException ();
    if (! (instrumentCommand.get (InstrumentCommand.IC_COMMAND_KEY) instanceof String))
      throw new IllegalArgumentException ();
    final String commandString = (String) instrumentCommand.get (InstrumentCommand.IC_COMMAND_KEY);
    if (commandString.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    final GpibDevice device = (GpibDevice) getDevice ();
    InstrumentSettings newInstrumentSettings = null;
    try
    {
      switch (commandString)
      {
        case InstrumentCommand.IC_NOP_KEY:
          break;
        case InstrumentCommand.IC_GET_SETTINGS_KEY:
        {
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SOFT_LIMIT_VOLTAGE:
        {
          final double softLimitVoltage_V = (double) instrumentCommand.get (InstrumentCommand.ICARG_SOFT_LIMIT_VOLTAGE_V);
          writeSync ("VMAX " + softLimitVoltage_V + " V\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SOFT_LIMIT_CURRENT:
        {
          final double softLimitCurrent_A = (double) instrumentCommand.get (InstrumentCommand.ICARG_SOFT_LIMIT_CURRENT_A);
          writeSync ("IMAX " + softLimitCurrent_A + " A\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SOFT_LIMIT_POWER:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_SET_VOLTAGE:
        {
          final double setVoltage_V = (double) instrumentCommand.get (InstrumentCommand.ICARG_SET_VOLTAGE_V);
          writeSync ("VSET " + setVoltage_V + " V\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SET_CURRENT:
        {
          final double setCurrent_A = (double) instrumentCommand.get (InstrumentCommand.ICARG_SET_CURRENT_A);
          writeSync ("ISET " + setCurrent_A + " A\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_SET_POWER:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_OUTPUT_ENABLE:
        {
          final boolean outputEnable = (boolean) instrumentCommand.get (InstrumentCommand.ICARG_OUTPUT_ENABLE);
          writeSync ("OUT " + (outputEnable ? "1": "0") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        default:
          throw new UnsupportedOperationException ();
      }
    }
    finally
    {
      // EMPTY
    }
    if (newInstrumentSettings != null)
      settingsReadFromInstrument (newInstrumentSettings);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
