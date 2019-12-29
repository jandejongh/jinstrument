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
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.Util;
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
  // MEASUREMENT MODE SUPPORT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
  
  private String writeAndReadlnSync (final String string)
    throws InterruptedException, IOException, TimeoutException
  {
    final byte[] bytes = ((GpibDevice) getDevice ()).writeAndReadlnSync (string.getBytes (Charset.forName ("US-ASCII")),
      HP3478A_GPIB_Instrument.READLINE_TERMINATION_MODE,
      HP3478A_GPIB_Instrument.READLINE_TIMEOUT_MS);
    return new String (bytes, Charset.forName ("US-ASCII"));
  }
  
  private byte[] writeAndReadNSync (final String string, final int N)
    throws InterruptedException, IOException, TimeoutException
  {
    final byte[] bytes = ((GpibDevice) getDevice ()).writeAndReadNSync (string.getBytes (Charset.forName ("US-ASCII")),
      N,
      HP3478A_GPIB_Instrument.READLINE_TIMEOUT_MS);
    return bytes;
  }
  
  private String[] writeAndReadlnNSync (final String string, final int N)
    throws InterruptedException, IOException, TimeoutException
  {
    final byte[][] bytes = ((GpibDevice) getDevice ()).writeAndReadlnNSync (string.getBytes (Charset.forName ("US-ASCII")),
      HP3478A_GPIB_Instrument.READLINE_TERMINATION_MODE,
      N,
      HP3478A_GPIB_Instrument.READLINE_TIMEOUT_MS);
    final String[] strings = new String[bytes.length];
    for (int s = 0; s < strings.length; s++)
      strings[s] = new String (bytes[s], Charset.forName ("US-ASCII"));
    return strings;
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
    LOG.log (Level.WARNING, "B bytes = {0}", Util.bytesToHex (b));
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
        measurementMode = MeasurementMode.UNKNOWN;
      }
    }
    LOG.log (Level.INFO, "MeasurementMode={0}", measurementMode);
    return new DefaultDigitalMultiMeterSettings (
      b,
      measurementMode);
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
    return new DefaultDigitalMultiMeterReading (settings, false, null, false, false, readingValue);
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
