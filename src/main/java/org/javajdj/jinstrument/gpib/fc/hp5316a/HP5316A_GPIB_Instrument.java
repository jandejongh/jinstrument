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
package org.javajdj.jinstrument.gpib.fc.hp5316a;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultFrequencyCounterReading;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.FrequencyCounter;
import org.javajdj.junits.Resolution;
import org.javajdj.jinstrument.gpib.fc.AbstractGpibFrequencyCounter;

/** Implementation of {@link Instrument} and {@link FrequencyCounter} for the HP-5316A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP5316A_GPIB_Instrument
  extends AbstractGpibFrequencyCounter
  implements FrequencyCounter
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP5316A_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP5316A_GPIB_Instrument (final GpibDevice device)
  {
    super (
      "HP-5316A",
      device,
      null,  // runnables
      null,  // targetServices
      false, // addInitializationServices
      false, // addStatusServices
      false, // addSettingsServices
      true,  // addCommandProcessorServices
      false, // addAcquisitionServices
      true,  // addHousekeepingServices
      true   // addServiceRequestPollingServices
    );
    setSerialPollTimeout_ms (500);
    setGpibInstrumentServiceRequestCollectorPeriod_s (1);
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
      return "HP-5316A [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP5316A_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP5316A_GPIB_Instrument ((GpibDevice) device);
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
    return HP5316A_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
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
  
  @Override
  protected void requestSettingsFromInstrumentASync () throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  @Override
  public InstrumentSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
  }
 
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  protected volatile boolean acquiring = false;
  
  protected volatile Instant acquisitionDeadline = null;
  
  protected volatile HP5316A_GPIB_Settings acquisitionSettings = null;
  
  protected final Object acquiringLock = new Object ();
  
  private void startAcquisitionIfNeeded (final HP5316A_GPIB_Settings settings)
    throws IOException, InterruptedException, TimeoutException
  {
    if (settings == null)
      throw new IllegalArgumentException ();
    synchronized (this.acquiringLock)
    {
      if ((! this.acquiring) || Instant.now ().isAfter (this.acquisitionDeadline))
      {
        if (this.acquiring)
          LOG.log (Level.WARNING, "Acquisition Timeout on HP-5316A!");
        this.acquiring = true;
        this.acquisitionDeadline =
          Instant.now ().plusMillis (Math.round (1.5 * 1000 * settings.getGateTime_s ()));
        this.acquisitionSettings = (HP5316A_GPIB_Settings) getCurrentInstrumentSettings ();
        writeSync (settings.getCanonicalStringForInstrument () + "\n");
        // writeSync ("FN7 WA1 SR1\n");
      }
    }
  }
  
  @Override
  protected final InstrumentReading getReadingFromInstrumentSync () throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
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
    HP5316A_GPIB_Settings settings = (HP5316A_GPIB_Settings) getCurrentInstrumentSettings ();
    if (settings == null)
    {
      settingsReadFromInstrument (HP5316A_GPIB_Settings.DEFAULT_SETTINGS);
      settings = (HP5316A_GPIB_Settings) getCurrentInstrumentSettings ();
    }
    startAcquisitionIfNeeded (settings);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractGpibInstrument
  // ON GPIB SERVICE REQUEST FROM INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static int READING_STRING_LENGTH = 19; // Excluding 0x0d/0x0a.
  
  @Override
  protected void onGpibServiceRequestFromInstrument ()
    throws IOException, InterruptedException, TimeoutException
  {
    final HP5316A_GPIB_Settings settings = (HP5316A_GPIB_Settings) getCurrentInstrumentSettings ();
    if (settings == null)
      throw new RuntimeException ();
    try
    {
      if((! this.acquiring)
        || this.acquisitionSettings == null
        || (! settings.equals (this.acquisitionSettings)))
      {
        LOG.log (Level.WARNING, "Dropping Service Request: Illegal State!");
        return;
      }
      final String stringRead = readlnSync ();
      if (stringRead == null || stringRead.length () != READING_STRING_LENGTH)
        throw new IOException ();
      boolean error = false;
      boolean overflow = false;
      String errorMessage = null;
      switch (stringRead.charAt (0))
      {
        case 'F':
          switch (settings.getMeasurementFunction ())
          {
            case FN01_FREQUENCY_A:
            case FN05_FREQUENCY_C:
            case FN09_CHECK_10MHZ:
            case FN13_FREQUENCY_A_AVG_ARMED_BY_B_POS_SLOPE:
            case FN14_FREQUENCY_A_AVG_ARMED_BY_B_NEG_SLOPE:
              break;
            default:
              throw new IOException ();
          }
          break;
        case 'O':
          overflow = true;
          errorMessage = "Overflow";
          break;
        case 'X':
          error = true;
          errorMessage = "Error";
          break;
        case ' ':
          switch (settings.getMeasurementFunction ())
          {
            case FN00_ROLLING_DISPLAY_TEST:
            case FN04_RATIO_A_OVER_B:
            case FN06_TOTALIZE_STOP:
            case FN10_A_GATED_BY_B:
            case FN12_TOTALIZE_START:
            case FN16_HPIB_INTERFACE_TEST:
              break;
            default:
              throw new IOException ();
          }
          break;
        case 'T':
          switch (settings.getMeasurementFunction ())
          {
            case FN02_TIME_INTERVAL_A_TO_B:
            case FN03_TIME_INTERVAL_DELAY:
            case FN07_PERIOD_A:
            case FN08_TIME_INTERVAL_AVERAGE_A_TO_B:
            case FN11_GATE_TIME:
              break;
            default:
              throw new IOException ();
          }
          break;
        default:
          throw new IOException ();
      }
      final double readingValue;
      try
      {
        readingValue = Double.parseDouble (stringRead.substring (1).trim ());
      }
      catch (NumberFormatException nfe)
      {
        throw new IOException (nfe);
      }
      final InstrumentReading reading = new DefaultFrequencyCounterReading (
        settings,
        null,
        readingValue,
        settings.getMeasurementFunction ().getUnit (),
        Resolution.DIGITS_8, // 8 digit display; more complicated tham that though (overflow behavior)... XXX ...
        error,
        errorMessage,
        overflow,
        false,  // Not reported by instrument.
        false); // Not reported by instrument.
      readingReadFromInstrument (reading);
    }
    finally
    {
      synchronized (this.acquiringLock)
      {
        this.acquiring = false;
        this.acquisitionDeadline = null;
        this.acquisitionSettings = null;
        startAcquisitionIfNeeded (settings);
      }
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FrequencyCounter
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final static List<HP5316A_GPIB_Settings.MeasurementFunction> SUPPORTED_MODES =
    Collections.unmodifiableList (Arrays.asList (HP5316A_GPIB_Settings.MeasurementFunction.values ()));
  
  @Override
  public final List<HP5316A_GPIB_Settings.MeasurementFunction> getSupportedModes ()
  {
    return HP5316A_GPIB_Instrument.SUPPORTED_MODES;
  }

  
  @Override
  public final double getMinFrequency_Hz ()
  {
    // Confirmed from 05316-90011 d.d. 20200123.
    return 0.1;
  }
  
  @Override
  public final double getMaxFrequency_Hz ()
  {
    // Confirmed from 05316-90011 d.d. 20200123.
    return 100e6;
  }
  
  @Override
  public final double getFrequencyResolution_Hz ()
  {
    // Confirmed from 05316-90011 d.d. 20200123.
    // The maximum displayed resolution is 1 nHz, but we are arbitrarily reducing the resolution to 1 mHz.
    return 1e-3;
  }
  
  @Override
  public final boolean supportsGetGateTime ()
  {
    // Confirmed from 05316-90011 d.d. 20200121.
    return true;
  }

  @Override
  public final boolean supportsSetGateTime ()
  {
    // Confirmed from 05316-90011 d.d. 20200121.
    return false;
  }

  @Override
  public final double getMinGateTime_s ()
  {
    // Confirmed from 05316-90011 d.d. 20200121.
    return 500e-6;
  }

  @Override
  public final double getMaxGateTime_s ()
  {
    // Confirmed from 05316-90011 d.d. 20200121.
    // Actually, the maximum gate time seems to be arbitrarily high,
    // max'ed only by the period of the input signal.
    return 10;
  }

  @Override
  public double getGateTimeResolution_s ()
  {
    // Confirmed from 05316-90011 d.d. 20200121.
    // The minimum gate time is 500 mus.
    return 100e-6;
  }
    
  private final static List<HP5316A_GPIB_Settings.TriggerSlope> SUPPORTED_TRIGGER_MODES =
    Collections.unmodifiableList (Arrays.asList (HP5316A_GPIB_Settings.TriggerSlope.values ()));
  
  @Override
  public List getSupportedTriggerModes ()
  {
    return HP5316A_GPIB_Instrument.SUPPORTED_TRIGGER_MODES;
  }

  @Override
  public double getMinTriggerLevel_V ()
  {
    // Confirmed from 05316-90011 d.d. 20200124.
    return -2.5;
  }

  @Override
  public double getMaxTriggerLevel_V ()
  {
    // Confirmed from 05316-90011 d.d. 20200124.
    return 2.5;
  }

  @Override
  public double getTriggerLevelResolution_V ()
  {
    // Confirmed from 05316-90011 d.d. 20200124.
    return 0.01;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP5316A_GPIB_Instrument
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        case InstrumentCommand.IC_INSTRUMENT_MODE:
        {
          final HP5316A_GPIB_Settings.MeasurementFunction mode =
            (HP5316A_GPIB_Settings.MeasurementFunction) instrumentCommand.get (InstrumentCommand.ICARG_INSTRUMENT_MODE);
          newInstrumentSettings = ((HP5316A_GPIB_Settings) getCurrentInstrumentSettings ()).withInstrumentMode (mode);
          break;
        }
        case InstrumentCommand.IC_GATE_TIME:
        {
          final double gateTime_s = (double) instrumentCommand.get (InstrumentCommand.ICARG_GATE_TIME_S);
          newInstrumentSettings = ((HP5316A_GPIB_Settings) getCurrentInstrumentSettings ()).withGateTime_s (gateTime_s);
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
