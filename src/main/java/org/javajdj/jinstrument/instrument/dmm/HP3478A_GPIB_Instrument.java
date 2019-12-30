/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.instrument.dmm;

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
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.controller.gpib.GpibControllerCommand;

/** Implementation of {@link Instrument} and {@link DigitalMultiMeter} for the HP-3478A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP3478A_GPIB_Instrument
  extends AbstractDigitalMultiMeter
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
  
  public HP3478A_GPIB_Instrument (final GpibDevice device)
  {
    super ("HP-3478A", device, null, null,
      false, // XXX Status
      true,  // Setings
      true,  // Command Processor
      true); // Acquisition
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
  
  private static final Map<MeasurementMode, List<Range>> SUPPORTED_RANGES_MAP =
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
  // READ LINE TERMINATION MODE
  // READLINE TIMEOUT
  // READ AND WRITE METHODS TO THE DEVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static GpibDevice.ReadlineTerminationMode READLINE_TERMINATION_MODE = GpibDevice.ReadlineTerminationMode.OPTCR_LF;
  
  private final static long READLINE_TIMEOUT_MS = 10000L;
  
  private void writeAsync (final String string)
  {
    ((GpibDevice) getDevice ()).writeAsync (string.getBytes (Charset.forName ("US-ASCII")));
  }
  
  private GpibControllerCommand generateGetSettingsCommandB ()
  {
    final GpibControllerCommand getSettingsCommandB =
      ((GpibDevice) getDevice ()).generateWriteAndReadNCommand ("B\r\n".getBytes (Charset.forName ("US-ASCII")),
      HP3478A_GPIB_Instrument.B_LENGTH);
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
  // INSTRUMENT STATUS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  protected InstrumentStatus getStatusFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
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
  
  private final static int B_LENGTH = 5;
  
  @Override
  protected void requestSettingsFromInstrumentASync () throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  @Override
  public synchronized DigitalMultiMeterSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final GpibControllerCommand getSettingsCommand = generateGetSettingsCommandB ();
    ((GpibDevice) getDevice ()).doControllerCommandSync (getSettingsCommand, READLINE_TIMEOUT_MS);
    final byte[] b = (byte[]) getSettingsCommand.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
    final DigitalMultiMeterSettings settings = HP3478A_GPIB_Instrument.settingsFromB (b);
    // LOG message for debugging the OL output.
    LOG.log (Level.INFO, "Settings received.");
    return settings;
  }
 
  private static DigitalMultiMeterSettings settingsFromB (final byte[] b)
  {
    if (b == null || b.length != HP3478A_GPIB_Instrument.B_LENGTH)
    {
      LOG.log (Level.WARNING, "Null B object or unexpected number of bytes read for Settings.");
      throw new IllegalArgumentException ();
    }
    // XXX Sanity checks!
    // LOG.log (Level.WARNING, "B bytes = {0}", Util.bytesToHex (b));
    final int resolutionInt = (b[0] & 0x03);
    final NumberOfDigits resolution;
    switch (resolutionInt)
    {
      case 1:
        resolution = NumberOfDigits.DIGITS_5_5;
        break;
      case 2:
        resolution = NumberOfDigits.DIGITS_4_5;
        break;
      case 3:
        resolution = NumberOfDigits.DIGITS_3_5;
        break;
      default:
        throw new RuntimeException ();
    }
    final int measurentModeInt = ((b[0] & 0xe0) >>> 5) & 0x07;
    final MeasurementMode measurementMode;
    switch (measurentModeInt)
    {
      case 1: measurementMode = MeasurementMode.DC_VOLTAGE;    break;
      case 2: measurementMode = MeasurementMode.AC_VOLTAGE;    break;
      case 3: measurementMode = MeasurementMode.RESISTANCE_2W; break;
      case 4: measurementMode = MeasurementMode.RESISTANCE_4W; break;
      case 5: measurementMode = MeasurementMode.DC_CURRENT;    break;
      case 6: measurementMode = MeasurementMode.AC_CURRENT;    break;
      case 7:
      {
        LOG.log (Level.WARNING, "The EXTENDED OHMS mode on the HP3478A is not supported on this Instrument!");
        measurementMode = MeasurementMode.UNSUPPORTED;
        break;
      }
      default:
      {
        LOG.log (Level.WARNING, "The Measurement Mode numbered {0} is not supported (and unexpected) on this Instrument!",
          measurentModeInt);
        throw new IllegalArgumentException ();
      }
    }
    final int rangeInt = ((b[0] & 0x1c) >>> 2) & 0x07;
    if (rangeInt == 0)
    {
      LOG.log (Level.WARNING, "The Range numbered {0} is not supported (and unexpected) on this Instrument!",
        rangeInt);
      throw new IllegalArgumentException ();        
    }
    final List<Range> ranges = SUPPORTED_RANGES_MAP.get (measurementMode);
    final Range range;
    switch (measurementMode)
    {
      case DC_VOLTAGE:
        if (rangeInt >= 6)
          throw new IllegalArgumentException ();
        range = ranges.get (rangeInt - 1);
        break;
      case AC_VOLTAGE:
        if (rangeInt >= 5)
          throw new IllegalArgumentException ();
        range = ranges.get (rangeInt - 1);
        break;
      case RESISTANCE_2W:
      case RESISTANCE_4W:
        range = ranges.get (rangeInt - 1);
        break;
      case DC_CURRENT:
      case AC_CURRENT:
        if (rangeInt >= 3)
          throw new IllegalArgumentException ();
        range = ranges.get (rangeInt - 1);
        break;
      default:
        throw new RuntimeException ();
    }
    final boolean autoRange = (b[1] & 0x02) != 0;
    return new DefaultDigitalMultiMeterSettings (
      b,
      resolution,
      measurementMode,
      autoRange,
      range);
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
      HP3478A_GPIB_Instrument.READLINE_TIMEOUT_MS);
    final byte[] preSettingsBytes  = (byte[]) preSettingsCommand.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
    final byte[] readingBytes      = (byte[]) readingCommand.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
    final byte[] postSettingsBytes = (byte[]) preSettingsCommand.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
    // Make sure settings are non-null and equal.
    if (preSettingsBytes == null || postSettingsBytes == null || ! Arrays.equals (preSettingsBytes, postSettingsBytes))
    {
      LOG.log (Level.WARNING, "Digital MultiMeter Settings samples before and after taking a reading do NOT match!");
      // Given the fact that we used an atomic sequence, it is justified to throw an IOException at this point.
      throw new IOException ();
    }
    // Collect settings and make sure they are valid.
    final DigitalMultiMeterSettings settings = settingsFromB (preSettingsBytes);
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
      false,
      null,
      false,
      false,
      readingValue,
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
            case DIGITS_5_5: writeAsync ("N5\r\n"); break;
            case DIGITS_4_5: writeAsync ("N4\r\n"); break;
            case DIGITS_3_5: writeAsync ("N3\r\n"); break;
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
            case DC_VOLTAGE:    writeAsync ("F1\r\n"); break;
            case AC_VOLTAGE:    writeAsync ("F2\r\n"); break;
            case RESISTANCE_2W: writeAsync ("F3\r\n"); break;
            case RESISTANCE_4W: writeAsync ("F4\r\n"); break;
            case DC_CURRENT:    writeAsync ("F5\r\n"); break;
            case AC_CURRENT:    writeAsync ("F6\r\n"); break;
            default:
              throw new UnsupportedOperationException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_AUTO_RANGE:
        {
          writeAsync ("RA\r\n");
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
                case 0:  writeAsync ("R-2\r\n"); break;
                case 1:  writeAsync ("R-1\r\n"); break;
                case 2:  writeAsync ("R0\r\n");  break;
                case 3:  writeAsync ("R1\r\n");  break;
                case 4:  writeAsync ("R2\r\n");  break;
                default: throw new UnsupportedOperationException ();
              }
              break;
            }
            case AC_VOLTAGE:
            {
              switch (rangeIndex)
              {
                case 0:  writeAsync ("R-1\r\n"); break;
                case 1:  writeAsync ("R0\r\n");  break;
                case 2:  writeAsync ("R1\r\n");  break;
                case 3:  writeAsync ("R2\r\n");  break;
                default: throw new UnsupportedOperationException ();
              }
              break;
            }
            case RESISTANCE_2W:
            case RESISTANCE_4W:
            {
              switch (rangeIndex)
              {
                case 0:  writeAsync ("R1\r\n"); break;
                case 1:  writeAsync ("R2\r\n"); break;
                case 2:  writeAsync ("R3\r\n"); break;
                case 3:  writeAsync ("R4\r\n"); break;
                case 4:  writeAsync ("R5\r\n"); break;
                case 5:  writeAsync ("R6\r\n"); break;
                case 6:  writeAsync ("R7\r\n"); break;
                default: throw new UnsupportedOperationException ();
              }
              break;
            }
            case AC_CURRENT:
            case DC_CURRENT:
            {
              switch (rangeIndex)
              {
                case 0:  writeAsync ("R-1\r\n"); break;
                case 1:  writeAsync ("R0\r\n");  break;
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
