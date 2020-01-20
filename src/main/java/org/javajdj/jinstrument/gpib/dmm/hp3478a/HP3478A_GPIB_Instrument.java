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
package org.javajdj.jinstrument.gpib.dmm.hp3478a;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.javajdj.jinstrument.DefaultInstrumentCommand;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.Unit;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.controller.gpib.GpibControllerCommand;
import org.javajdj.jinstrument.gpib.dmm.AbstractGpibDigitalMultiMeter;
import org.javajdj.jinstrument.DefaultDigitalMultiMeterReading;
import org.javajdj.jinstrument.DigitalMultiMeter;
import org.javajdj.jinstrument.DigitalMultiMeterSettings;
import org.javajdj.jinstrument.controller.gpib.ReadlineTerminationMode;

/** Implementation of {@link Instrument} and {@link DigitalMultiMeter} for the HP-3478A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP3478A_GPIB_Instrument
  extends AbstractGpibDigitalMultiMeter
  implements DigitalMultiMeter
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP3478A_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static double DEFAULT_HP3478A_READING_COLLECTOR_PERIOD_S = 2;
  
  public HP3478A_GPIB_Instrument (final GpibDevice device)
  {
    super ("HP-3478A", device, null, null,
      false,  // Initialization
      true,   // Status
      false,  // Setings (not needed; they come with Status!)
      true,   // Command Processor
      true,   // Acquisition
      false,  // Housekeeping
      false); // Service Request Polling
    setReadingCollectorPeriod_s (HP3478A_GPIB_Instrument.DEFAULT_HP3478A_READING_COLLECTOR_PERIOD_S);
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
      return "HP-3478A [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP3478A_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP3478A_GPIB_Instrument ((GpibDevice) device);
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
    return HP3478A_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
  }

  @Override
  public String toString ()
  {
    return getInstrumentUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DigitalMultiMeter
  // RESOLUTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final static NumberOfDigits[] SUPPORTED_RESOLUTIONS = new NumberOfDigits[]
  {
    NumberOfDigits.DIGITS_3_5,
    NumberOfDigits.DIGITS_4_5,
    NumberOfDigits.DIGITS_5_5
  };
  
  private final static List<NumberOfDigits> SUPPORTED_RESOLUTIONS_AS_LIST
    = Collections.unmodifiableList (Arrays.asList (HP3478A_GPIB_Instrument.SUPPORTED_RESOLUTIONS));
  
  @Override
  public final List<NumberOfDigits> getSupportedResolutions ()
  {
    return HP3478A_GPIB_Instrument.SUPPORTED_RESOLUTIONS_AS_LIST;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DigitalMultiMeter
  // MEASUREMENT MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final static MeasurementMode[] SUPPORTED_MEASUREMENT_MODES = new MeasurementMode[]
  {
    MeasurementMode.DC_VOLTAGE,
    MeasurementMode.AC_VOLTAGE,
    MeasurementMode.RESISTANCE_2W,
    MeasurementMode.RESISTANCE_4W,
    MeasurementMode.DC_CURRENT,
    MeasurementMode.AC_CURRENT
  };
  
  private final static List<MeasurementMode> SUPPORTED_MEASUREMENT_MODES_AS_LIST =
    Collections.unmodifiableList (Arrays.asList (HP3478A_GPIB_Instrument.SUPPORTED_MEASUREMENT_MODES));
  
  @Override
  public final List<MeasurementMode> getSupportedMeasurementModes ()
  {
    return HP3478A_GPIB_Instrument.SUPPORTED_MEASUREMENT_MODES_AS_LIST;
  }
  
  @Override
  public final boolean supportsMeasurementMode (final MeasurementMode measurementMode)
  {
    if (measurementMode == null)
      throw new IllegalArgumentException ();
    switch (measurementMode)
    {
      case DC_VOLTAGE:
      case AC_VOLTAGE:
      case RESISTANCE_2W:
      case RESISTANCE_4W:
      case DC_CURRENT:
      case AC_CURRENT:
        return true;
      default:
        return false;
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DigitalMultiMeter
  // RANGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private static class DefaultRange
    implements Range
  {
    
    private final double value;
    private final Unit unit;

    public DefaultRange (final double value, final Unit unit)
    {
      if (unit == null)
        throw new IllegalArgumentException ();
      this.value = value;
      this.unit = unit;
    }

    @Override
    public final double getValue ()
    {
      return this.value;
    }

    @Override
    public final Unit getUnit ()
    {
      return this.unit;
    }
    
  }
  
  public final static Map<MeasurementMode, List<Range>> SUPPORTED_RANGES_MAP =
    Arrays.stream (new Object[][]
    {
      { MeasurementMode.DC_VOLTAGE, Arrays.asList (new Range[] {
          new DefaultRange (30, Unit.UNIT_mV),
          new DefaultRange (300, Unit.UNIT_mV),
          new DefaultRange (3, Unit.UNIT_V),
          new DefaultRange (30, Unit.UNIT_V),
          new DefaultRange (300, Unit.UNIT_V)})},
      { MeasurementMode.AC_VOLTAGE, Arrays.asList (new Range[] {
          new DefaultRange (300, Unit.UNIT_mV),
          new DefaultRange (3, Unit.UNIT_V),
          new DefaultRange (30, Unit.UNIT_V),
          new DefaultRange (300, Unit.UNIT_V)})},
      { MeasurementMode.RESISTANCE_2W, Arrays.asList (new Range[] {
          new DefaultRange (30, Unit.UNIT_Ohm),
          new DefaultRange (300, Unit.UNIT_Ohm),
          new DefaultRange (3, Unit.UNIT_kOhm),
          new DefaultRange (30, Unit.UNIT_kOhm),
          new DefaultRange (300, Unit.UNIT_kOhm),
          new DefaultRange (3, Unit.UNIT_MOhm),
          new DefaultRange (30, Unit.UNIT_MOhm)})},
      { MeasurementMode.RESISTANCE_4W, Arrays.asList (new Range[] {
          new DefaultRange (30, Unit.UNIT_Ohm),
          new DefaultRange (300, Unit.UNIT_Ohm),
          new DefaultRange (3, Unit.UNIT_kOhm),
          new DefaultRange (30, Unit.UNIT_kOhm),
          new DefaultRange (300, Unit.UNIT_kOhm),
          new DefaultRange (3, Unit.UNIT_MOhm),
          new DefaultRange (30, Unit.UNIT_MOhm)})},
      { MeasurementMode.DC_CURRENT, Arrays.asList (new Range[] {
          new DefaultRange (300, Unit.UNIT_mA),
          new DefaultRange (3, Unit.UNIT_A)})},
      { MeasurementMode.AC_CURRENT, Arrays.asList (new Range[] {
          new DefaultRange (300, Unit.UNIT_mA),
          new DefaultRange (3, Unit.UNIT_A)})}
    }).collect (Collectors.toMap (kv -> (MeasurementMode) kv[0], kv -> (List<Range>) kv[1]));

  @Override
  public final List<Range> getSupportedRanges (final MeasurementMode measurementMode)
  {
    if (measurementMode == null)
      return null;
    return SUPPORTED_RANGES_MAP.get (measurementMode);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP3478A_GPIB_Instrument
  // AUTO ZERO
  // TRIGGER MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public void setAutoZero (final boolean autoZero) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_AUTO_ZERO,
      InstrumentCommand.ICARG_AUTO_ZERO, autoZero));
  }
  
  public enum HP3478ATriggerMode
  {
    T1_INTERNAL_TRIGGER,
    T2_EXTERNAL_TRIGGER,
    T3_SINGLE_TRIGGER,
    T4_TRIGGER_HOLD,
    T5_FAST_TRIGGER;
  }
  
  public void setTriggerMode (final HP3478ATriggerMode triggerMode) throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_TRIGGER_MODE,
      InstrumentCommand.ICARG_TRIGGER_MODE, triggerMode));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READ LINE TERMINATION MODE
  // CONTROLLER COMMAND GENERATORS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static ReadlineTerminationMode READLINE_TERMINATION_MODE = ReadlineTerminationMode.OPTCR_LF;
  
  private GpibControllerCommand generateGetSettingsCommandB ()
  {
    final GpibControllerCommand getSettingsCommandB =
      ((GpibDevice) getDevice ()).generateWriteAndReadNCommand ("B\r\n".getBytes (Charset.forName ("US-ASCII")),
      HP3478A_GPIB_Settings.B_LENGTH);
    return getSettingsCommandB;
  }
  
  private GpibControllerCommand generateGetReadingCommand ()
  {
    final GpibControllerCommand getReadingCommand =
      ((GpibDevice) getDevice ()).generateReadlnCommand (HP3478A_GPIB_Instrument.READLINE_TERMINATION_MODE);
    return getReadingCommand;
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
  // INSTRUMENT INITIALIZATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected final void initializeInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
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
    final HP3478A_GPIB_Settings settings = getSettingsFromInstrumentSync ();
    settingsReadFromInstrument (settings);
    return HP3478A_GPIB_Status.fromSerialPollStatusByteAndSettings (serialPollStatusByte, settings);
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
  protected void requestSettingsFromInstrumentASync () throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  @Override
  public HP3478A_GPIB_Settings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final GpibControllerCommand getSettingsCommand = generateGetSettingsCommandB ();
    ((GpibDevice) getDevice ()).doControllerCommandSync (getSettingsCommand, getGetSettingsTimeout_ms ());
    final byte[] b = (byte[]) getSettingsCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
    final HP3478A_GPIB_Settings settings = HP3478A_GPIB_Settings.fromB (b);
    return settings;
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
    final GpibControllerCommand preSettingsCommand = generateGetSettingsCommandB ();
    final GpibControllerCommand readingCommand = generateGetReadingCommand ();
    final GpibControllerCommand postSettingsCommand = generateGetSettingsCommandB ();
    ((GpibDevice) getDevice ()).atomicSequenceSync (
      new GpibControllerCommand[] {preSettingsCommand, readingCommand, postSettingsCommand},
      getReadlineTimeout_ms ());
    final byte[] preSettingsBytes  = (byte[]) preSettingsCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
    final byte[] readingBytes      = (byte[]) readingCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
    final byte[] postSettingsBytes = (byte[]) preSettingsCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
    // Make sure settings are non-null and equal.
    if (preSettingsBytes == null || postSettingsBytes == null || ! Arrays.equals (preSettingsBytes, postSettingsBytes))
    {
      LOG.log (Level.WARNING, "Digital MultiMeter Settings samples before and after taking a reading do NOT match!");
      // Given the fact that we used an atomic sequence, it is justified to throw an IOException at this point.
      throw new IOException ();
    }
    // Collect settings and make sure they are valid (throws IllegalArgumentException otherwise).
    final DigitalMultiMeterSettings settings = HP3478A_GPIB_Settings.fromB (preSettingsBytes);
    // While we're at it, report the settings read.
    settingsReadFromInstrument (settings);
    // XXX Few issues here:
    // XXX - We should wait for Data Available.
    // XXX - We do not record/report status
    // Collect the actual reading.
    final double readingValue;
    final String readingString = new String (readingBytes, Charset.forName ("US-ASCII"));
    try
    {
      readingValue = Double.parseDouble (readingString);      
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException (nfe);
    }
    return new DefaultDigitalMultiMeterReading (
      settings,
      readingValue,
      settings.getReadingUnit (),
      false,
      null,
      false,
      false,
      settings.getResolution ());
  }

  @Override
  protected final void requestReadingFromInstrumentASync () throws IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT HOUSEKEEPING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected final void instrumentHousekeeping ()
    throws IOException, InterruptedException, TimeoutException
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
    HP3478A_GPIB_Settings newInstrumentSettings = null;
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
        case InstrumentCommand.IC_RESOLUTION:
        {
          final NumberOfDigits resolution = (NumberOfDigits) instrumentCommand.get (InstrumentCommand.ICARG_RESOLUTION);
          final List<NumberOfDigits> supportedResolutions = HP3478A_GPIB_Instrument.SUPPORTED_RESOLUTIONS_AS_LIST;
          if (! supportedResolutions.contains (resolution))
          {
            LOG.log (Level.WARNING, "Resolution {0} is not supported on HP3478A.",
              new Object[]{resolution});
            throw new UnsupportedOperationException ();
          }
          switch (resolution)
          {
            case DIGITS_5_5: writeSync ("N5\r\n"); break;
            case DIGITS_4_5: writeSync ("N4\r\n"); break;
            case DIGITS_3_5: writeSync ("N3\r\n"); break;
            default:         throw new RuntimeException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_MEASUREMENT_MODE:
        {
          final MeasurementMode measurementMode =
            (MeasurementMode) instrumentCommand.get (InstrumentCommand.ICARG_MEASUREMENT_MODE);
          if (! supportsMeasurementMode (measurementMode))
            throw new UnsupportedOperationException ();
          switch (measurementMode)
          {
            case DC_VOLTAGE:    writeSync ("F1\r\n"); break;
            case AC_VOLTAGE:    writeSync ("F2\r\n"); break;
            case RESISTANCE_2W: writeSync ("F3\r\n"); break;
            case RESISTANCE_4W: writeSync ("F4\r\n"); break;
            case DC_CURRENT:    writeSync ("F5\r\n"); break;
            case AC_CURRENT:    writeSync ("F6\r\n"); break;
            default:
              throw new UnsupportedOperationException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_AUTO_RANGE:
        {
          writeSync ("RA\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_RANGE:
        {
          final DigitalMultiMeterSettings settings = getSettingsFromInstrumentSync ();
          final Range range = (Range) instrumentCommand.get (InstrumentCommand.ICARG_RANGE);
          final MeasurementMode mode = settings.getMeasurementMode ();
          final List<Range> supportedRanges = SUPPORTED_RANGES_MAP.get (mode);
          if (! supportedRanges.contains (range))
          {
            LOG.log (Level.WARNING, "Range {0} is not supported on HP3478A in mode {1}.",
              new Object[]{range, mode});
            throw new UnsupportedOperationException ();
          }
          final int rangeIndex = supportedRanges.indexOf (range);
          switch (mode)
          {
            case DC_VOLTAGE:
            {
              switch (rangeIndex)
              {
                case 0:  writeSync ("R-2\r\n"); break;
                case 1:  writeSync ("R-1\r\n"); break;
                case 2:  writeSync ("R0\r\n");  break;
                case 3:  writeSync ("R1\r\n");  break;
                case 4:  writeSync ("R2\r\n");  break;
                default: throw new UnsupportedOperationException ();
              }
              break;
            }
            case AC_VOLTAGE:
            {
              switch (rangeIndex)
              {
                case 0:  writeSync ("R-1\r\n"); break;
                case 1:  writeSync ("R0\r\n");  break;
                case 2:  writeSync ("R1\r\n");  break;
                case 3:  writeSync ("R2\r\n");  break;
                default: throw new UnsupportedOperationException ();
              }
              break;
            }
            case RESISTANCE_2W:
            case RESISTANCE_4W:
            {
              switch (rangeIndex)
              {
                case 0:  writeSync ("R1\r\n"); break;
                case 1:  writeSync ("R2\r\n"); break;
                case 2:  writeSync ("R3\r\n"); break;
                case 3:  writeSync ("R4\r\n"); break;
                case 4:  writeSync ("R5\r\n"); break;
                case 5:  writeSync ("R6\r\n"); break;
                case 6:  writeSync ("R7\r\n"); break;
                default: throw new UnsupportedOperationException ();
              }
              break;
            }
            case AC_CURRENT:
            case DC_CURRENT:
            {
              switch (rangeIndex)
              {
                case 0:  writeSync ("R-1\r\n"); break;
                case 1:  writeSync ("R0\r\n");  break;
                default: throw new UnsupportedOperationException ();
              }
              break;
            }
            default:
              throw new RuntimeException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_AUTO_ZERO:
        {
          final boolean autoZero = (boolean) instrumentCommand.get (InstrumentCommand.ICARG_AUTO_ZERO);
          writeSync (autoZero ? "Z1\r\n" : "Z0\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_TRIGGER_MODE:
        {
          final HP3478ATriggerMode triggerMode = (HP3478ATriggerMode) instrumentCommand.get (InstrumentCommand.ICARG_TRIGGER_MODE);
          switch (triggerMode)
          {
            case T1_INTERNAL_TRIGGER: writeSync ("T1\r\n"); break;
            case T2_EXTERNAL_TRIGGER: writeSync ("T2\r\n"); break;
            case T3_SINGLE_TRIGGER:   writeSync ("T3\r\n"); break;
            case T4_TRIGGER_HOLD:     writeSync ("T4\r\n"); break;
            case T5_FAST_TRIGGER:     writeSync ("T5\r\n"); break;
            default:
              throw new UnsupportedOperationException ();
          }
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
    {
      settingsReadFromInstrument (newInstrumentSettings);
      final byte serialPollStatusByte = serialPollSync ();
      final InstrumentStatus newInstrumentStatus =
        HP3478A_GPIB_Status.fromSerialPollStatusByteAndSettings (serialPollStatusByte, newInstrumentSettings);
      statusReadFromInstrument (newInstrumentStatus);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
