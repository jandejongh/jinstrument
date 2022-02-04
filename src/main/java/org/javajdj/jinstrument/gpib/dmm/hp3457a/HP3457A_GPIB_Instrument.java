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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.javajdj.jinstrument.DefaultDigitalMultiMeterReading;
import org.javajdj.jinstrument.DefaultInstrumentCommand;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.junits.Unit;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.gpib.dmm.AbstractGpibDigitalMultiMeter;
import org.javajdj.jinstrument.DigitalMultiMeter;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.junits.Resolution;
import org.javajdj.jinstrument.gpib.AbstractGpibInstrument;
import org.javajdj.util.hex.HexUtils;

/** Implementation of {@link Instrument} and {@link DigitalMultiMeter} for the HP-3457A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP3457A_GPIB_Instrument
  extends AbstractGpibDigitalMultiMeter
  implements DigitalMultiMeter
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP3457A_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP3457A_GPIB_Instrument (final GpibDevice device)
  {
    super (
      "HP-3457A", // final String name
      device,     // final GpibDevice device
      null,       // final List<Runnable> runnables
      null,       // final List<Service> targetServices
      true,       // final boolean addInitializationServices
      false,      // final boolean addStatusServices
      false,      // final boolean addSettingsServices
      true,       // final boolean addCommandProcessorServices
      false,      // final boolean addAcquisitionServices // XXX Needed?
      false,      // final boolean addHousekeepingServices
      true);      // final boolean addServiceRequestPollingServices
    //
    // I did an experiment on 20220123 on a more or less empty implementation:
    // Just checking SRQ and processing them in order to get a Status object.
    // It was (already) unstable @500ms SRQ polling; even @900ms; stable @1s.
    //
    // After fixing a related bug in AbstractGpibInstrument I could comfortably poll SRQ @200Hz (5 ms),
    // in absence of other instruments and other activities on this instrument.
    //
    resetOptimizeSettingsUpdates ();
    setGpibInstrumentServiceRequestCollectorPeriod_s (1.0 /* XXX for now XXX */); // Can go as low as 0.005.
    setReadEOITimeout_ms (5000);
    this.safeMode = true;
    this.operationSemaphore = new Semaphore (1);
    setControllerAccessSemaphore (this.operationSemaphore);
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
      return "HP-3457A [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP3457A_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP3457A_GPIB_Instrument ((GpibDevice) device);
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
    return HP3457A_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
  }

  @Override
  public String toString ()
  {
    return getInstrumentUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP3457A_GPIB_Instrument
  // OPERATION SEMAPHORE
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  // We use a Semaphore for sequencing initilization, acquisition/srq, and command processing.
  // This save us the trouble of creating atomic operations for the controller...
  // It is not ideal, and more or less assumes exclusive access to controller and instrument.
  private final Semaphore operationSemaphore;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DigitalMultiMeter
  // RESOLUTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final static Resolution[] SUPPORTED_RESOLUTIONS = new Resolution[]
  {
    Resolution.DIGITS_3_5,
    Resolution.DIGITS_4_5,
    Resolution.DIGITS_5_5,
    Resolution.DIGITS_6_5,
    // Resolution.DIGITS_7_5 // In Math HIRES format.
  };
  
  private final static List<Resolution> SUPPORTED_RESOLUTIONS_AS_LIST
    = Collections.unmodifiableList (Arrays.asList (HP3457A_GPIB_Instrument.SUPPORTED_RESOLUTIONS));
  
  @Override
  public final List<Resolution> getSupportedResolutions ()
  {
    return HP3457A_GPIB_Instrument.SUPPORTED_RESOLUTIONS_AS_LIST;
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
    MeasurementMode.AC_DC_VOLTAGE,
    MeasurementMode.RESISTANCE_2W,
    MeasurementMode.RESISTANCE_4W,
    MeasurementMode.DC_CURRENT,
    MeasurementMode.AC_CURRENT,
    MeasurementMode.AC_DC_CURRENT,
    MeasurementMode.FREQUENCY,
    MeasurementMode.PERIOD
  };
  
  private final static List<MeasurementMode> SUPPORTED_MEASUREMENT_MODES_AS_LIST =
    Collections.unmodifiableList (Arrays.asList (HP3457A_GPIB_Instrument.SUPPORTED_MEASUREMENT_MODES));
  
  @Override
  public final List<MeasurementMode> getSupportedMeasurementModes ()
  {
    return HP3457A_GPIB_Instrument.SUPPORTED_MEASUREMENT_MODES_AS_LIST;
  }
  
  // XXX Why is this needed; we have SUPPORTED_MEASUREMENT_MODES already...
  @Override
  public final boolean supportsMeasurementMode (final MeasurementMode measurementMode)
  {
    if (measurementMode == null)
      throw new IllegalArgumentException ();
    switch (measurementMode)
    {
      case DC_VOLTAGE:
      case AC_VOLTAGE:
      case AC_DC_VOLTAGE:
      case RESISTANCE_2W:
      case RESISTANCE_4W:
      case DC_CURRENT:
      case AC_CURRENT:
      case AC_DC_CURRENT:
      case FREQUENCY:
      case PERIOD:
        return true;
      default:
        return false;
    }
  }
  public static boolean isSupportedMeasurementMode (final MeasurementMode measurementMode)
  {
    if (measurementMode == null)
      return false;
    switch (measurementMode)
    {
      case DC_VOLTAGE:
      case AC_VOLTAGE:
      case AC_DC_VOLTAGE:
      case RESISTANCE_2W:
      case RESISTANCE_4W:
      case DC_CURRENT:
      case AC_CURRENT:
      case AC_DC_CURRENT:
      case FREQUENCY:
      case PERIOD:
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

  // XXX DefaultRange is probably obsolete, given the recent developments of Range intself.
  
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
    public final double getMaxAbsValue ()
    {
      return this.value;
    }

    @Override
    public final Unit getUnit ()
    {
      return this.unit;
    }
    
    public static Range fromRangeDouble (final MeasurementMode measurementMode, final double rangeDouble)
    {
      switch (measurementMode)
      {
        case DC_VOLTAGE:
        case AC_VOLTAGE:
        case AC_DC_VOLTAGE:
        {
          if (rangeDouble <= 0.03) return SUPPORTED_RANGES_MAP.get (measurementMode).get (0);
          if (rangeDouble <= 0.3)  return SUPPORTED_RANGES_MAP.get (measurementMode).get (1);
          if (rangeDouble <= 3)    return SUPPORTED_RANGES_MAP.get (measurementMode).get (2);
          if (rangeDouble <= 30)   return SUPPORTED_RANGES_MAP.get (measurementMode).get (3);
          if (rangeDouble <= 300)  return SUPPORTED_RANGES_MAP.get (measurementMode).get (4);
          return null;
        }
        case RESISTANCE_2W:
        case RESISTANCE_4W:
        {
          if (rangeDouble <= 30)    return SUPPORTED_RANGES_MAP.get (measurementMode).get (0);
          if (rangeDouble <= 300)   return SUPPORTED_RANGES_MAP.get (measurementMode).get (1);
          if (rangeDouble <= 3e3)   return SUPPORTED_RANGES_MAP.get (measurementMode).get (2);
          if (rangeDouble <= 30e3)  return SUPPORTED_RANGES_MAP.get (measurementMode).get (3);
          if (rangeDouble <= 300e3) return SUPPORTED_RANGES_MAP.get (measurementMode).get (4);
          if (rangeDouble <= 3e6)   return SUPPORTED_RANGES_MAP.get (measurementMode).get (5);
          if (rangeDouble <= 30e6)  return SUPPORTED_RANGES_MAP.get (measurementMode).get (6);
          if (rangeDouble <= 3e9)   return SUPPORTED_RANGES_MAP.get (measurementMode).get (7);
          return null;
        }
        case DC_CURRENT:
        {
          if (rangeDouble <= 300e-6) return SUPPORTED_RANGES_MAP.get (measurementMode).get (0);
          if (rangeDouble <= 3e-3)   return SUPPORTED_RANGES_MAP.get (measurementMode).get (1);
          if (rangeDouble <= 30e-3)  return SUPPORTED_RANGES_MAP.get (measurementMode).get (2);
          if (rangeDouble <= 300e-3) return SUPPORTED_RANGES_MAP.get (measurementMode).get (3);
          if (rangeDouble <= 3)      return SUPPORTED_RANGES_MAP.get (measurementMode).get (4);
          return null;          
        }
        case AC_CURRENT:
        case AC_DC_CURRENT:
        {
          if (rangeDouble <= 30e-3)  return SUPPORTED_RANGES_MAP.get (measurementMode).get (0);
          if (rangeDouble <= 300e-3) return SUPPORTED_RANGES_MAP.get (measurementMode).get (1);
          if (rangeDouble <= 3)      return SUPPORTED_RANGES_MAP.get (measurementMode).get (2);
          return null;                    
        }
        case FREQUENCY:
        case PERIOD:
          return SUPPORTED_RANGES_MAP.get (measurementMode).get (0);
        default:
          return null;
      }
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
          new DefaultRange (30, Unit.UNIT_mV),
          new DefaultRange (300, Unit.UNIT_mV),
          new DefaultRange (3, Unit.UNIT_V),
          new DefaultRange (30, Unit.UNIT_V),
          new DefaultRange (300, Unit.UNIT_V)})},
      { MeasurementMode.AC_DC_VOLTAGE, Arrays.asList (new Range[] {
          new DefaultRange (30, Unit.UNIT_mV),
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
          new DefaultRange (30, Unit.UNIT_MOhm),
          // Note: Extended Ohms; combines 300 MOhm and 3 GOhm range.
          new DefaultRange (3, Unit.UNIT_GOhm)})},
      { MeasurementMode.RESISTANCE_4W, Arrays.asList (new Range[] {
          new DefaultRange (30, Unit.UNIT_Ohm),
          new DefaultRange (300, Unit.UNIT_Ohm),
          new DefaultRange (3, Unit.UNIT_kOhm),
          new DefaultRange (30, Unit.UNIT_kOhm),
          new DefaultRange (300, Unit.UNIT_kOhm),
          new DefaultRange (3, Unit.UNIT_MOhm),
          new DefaultRange (30, Unit.UNIT_MOhm),
          // Note: Extended Ohms; combines 300 MOhm and 3 GOhm range.
          new DefaultRange (3, Unit.UNIT_GOhm)})},
      { MeasurementMode.DC_CURRENT, Arrays.asList (new Range[] {
          new DefaultRange (300, Unit.UNIT_muA),
          new DefaultRange (3, Unit.UNIT_mA),
          new DefaultRange (30, Unit.UNIT_mA),
          new DefaultRange (300, Unit.UNIT_mA),
          new DefaultRange (3, Unit.UNIT_A)})},
      { MeasurementMode.AC_CURRENT, Arrays.asList (new Range[] {
          new DefaultRange (30, Unit.UNIT_mA),
          new DefaultRange (300, Unit.UNIT_mA),
          new DefaultRange (3, Unit.UNIT_A)})},
      { MeasurementMode.AC_DC_CURRENT, Arrays.asList (new Range[] {
          new DefaultRange (30, Unit.UNIT_mA),
          new DefaultRange (300, Unit.UNIT_mA),
          new DefaultRange (3, Unit.UNIT_A)})},
      { MeasurementMode.FREQUENCY, Arrays.asList (new Range[] {
          new DefaultRange (1.5, Unit.UNIT_MHz)})},      
      { MeasurementMode.PERIOD, Arrays.asList (new Range[] {
          new DefaultRange (100, Unit.UNIT_ms)})}
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
    
    try
    {
      
      this.operationSemaphore.acquire ();
    
      // Clear the command queue so we're not bothered by pending commands.
      clearCommandQueue ();
      
      // Device Clear for the instrument.
      selectedDeviceCLearSync ();
      Thread.sleep (1000L); // Allow for Device Clear to settle on instrument.
      
      // Put the instrument in a known and (to us) reasonable state.
      // Make sure we get the ID of the instrument.
      // All in one take (note that the command will be executed atomically on the controller).
      final String id = new String (writeAndReadEOISync (
        "RESET"         // "RESET for reset () or "PRESET" for preset ()
        + ";END 2"      // setEoi ()
        + ";MEM 0"      // setReadingMemoryMode (HP3457A_GPIB_Settings.ReadingMemoryMode.OFF)
        + ";RQS 127"    // setServiceRequestMask (0x7f) // ALL
        + ";ID?;")      // getIdSync ()
        , Charset.forName ("US-ASCII")).trim ();
      
      // Get the installed option synchronously.
      final HP3457A_GPIB_Settings.InstalledOption installedOption = processCommand_getInstalledOption ();
      
      // Get the GPIB address from our device.
      final byte gpipAddress = getDevice ().getBusAddress ().getPad ();
      
      // Report new settings.
      settingsReadFromInstrument (
        HP3457A_GPIB_Settings.fromReset ()
          .withEoi (true)
          .withReadingMemoryMode (HP3457A_GPIB_Settings.ReadingMemoryMode.OFF)
          .withServiceRequestMask ((byte) 0x7f)
          .withId (id)
          .withInstalledOption (installedOption)
          .withGpibAddress (gpipAddress));
      
      // We do not query our status now since we only want to do what's necessary;
      // the status will follow soon enough through an SRQ.
      // final HP3457A_GPIB_Status status = getStatusFromInstrumentSync ();
      // statusReadFromInstrument (status);
      
      // Asynchronously request some (instrument-constant) properties.
      // We want to see these properties in the settings, but we do not want to take the risk of nuking initialization for them.
      // Execution of these commands will update the settings.
      getCalibrationNumberASync ();
      getNumberOfPowerLineCyclesASync ();
      getLineFrequency_HzASync ();
      getLineFrequencyReference_HzASync ();
      
    }
    catch (InterruptedException ie)
    {
      LOG.log (Level.WARNING, "HP-3457A_GPIB_Instrument was interrupted while initializing instrument {0}.",
        new Object []{this});
      throw (ie);
    }
    catch (IOException | TimeoutException e)
    {
      LOG.log (Level.WARNING, "HP-3457A_GPIB_Instrument encountered Exception {0} while initializing instrument {1}.",
        new Object []{e, this});
      e.printStackTrace (System.err);
      error ();
    }
    finally
    {
      // Make sure the acquisition process can proceed.
      this.operationSemaphore.release ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT STATUS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  protected HP3457A_GPIB_Status getStatusFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    // XXX This should really be an AtomicSequence.
    final GpibDevice gpibDevice = (GpibDevice) getDevice ();
    final byte serialPollStatusByte = gpibDevice.serialPollSync (getSerialPollTimeout_ms ());
    if (! HP3457A_GPIB_Status.isError (serialPollStatusByte))
      return HP3457A_GPIB_Status.fromSerialPollStatusByte (
        serialPollStatusByte);
    final short errorsShort = getErrorSync (getGetStatusTimeout_ms (), TimeUnit.MILLISECONDS);
    if (! HP3457A_GPIB_Status.isHardwareError (errorsShort))
      return HP3457A_GPIB_Status.fromSerialPollStatusByteAndErrorsShort (
        serialPollStatusByte,
        errorsShort);
    final short auxiliaryErrorsShort = getAuxiliaryErrorSync (getGetStatusTimeout_ms (), TimeUnit.MILLISECONDS);
    return HP3457A_GPIB_Status.fromSerialPollStatusByteAndErrorsAndAuxiliaryErrorsShort (
      serialPollStatusByte,
      errorsShort,
      auxiliaryErrorsShort);
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
  public HP3457A_GPIB_Settings getSettingsFromInstrumentSync ()
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

  @Override
  protected final InstrumentReading getReadingFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
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
    throw new UnsupportedOperationException ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractGpibInstrument
  // ON GPIB SERVICE REQUEST FROM INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected final InstrumentReading singleReading (final HP3457A_GPIB_Settings settings, final HP3457A_GPIB_Status status)
    throws IOException, InterruptedException, TimeoutException
  {
    if (settings == null || status == null)
      throw new RuntimeException ();
    final byte[] readBytes = readEOISync ();
    if (readBytes == null)
    {
      LOG.log (Level.WARNING, "Null array returned attempting to get reading.");
      return null;
    }
    final double readingValue;
    switch (settings.getReadingFormat ())
    {
      case ASCII:
      {
        if (readBytes.length != 16)
        {
          LOG.log (Level.WARNING, "Error parsing presumed reading {0} (unexpected length for ASCII) on instrument {1}.",
            new Object[]{HexUtils.bytesToHex (readBytes), this});
          return null;          
        }
        final String readString = new String (readBytes, Charset.forName ("US-ASCII")).trim ();
        try
        {
          readingValue = Double.parseDouble (readString);
        }
        catch (NumberFormatException nfe)
        {
          LOG.log (Level.WARNING, "Error parsing presumed reading {0} (String: {1}) on instrument {2}.",
            new Object[]{HexUtils.bytesToHex (readBytes), readString, this});
          return null;
        }
        break;
      }
      case SINT:
      {
        if (readBytes.length != 2)
        {
          LOG.log (Level.WARNING, "Error parsing presumed reading {0} (unexpected length for SINT) on instrument {1}.",
            new Object[]{HexUtils.bytesToHex (readBytes), this});
          return null;          
        }
        final short iValue = (short)(((readBytes[0] & 0xff) << 8) | (readBytes[1] & 0xff));
        final double integerScale = settings.isAutoRange () ? processCommand_getIntegerScale () : settings.getIntegerScale ();
        readingValue = integerScale * iValue;
        break;
      }
      case DINT:
      {
        if (readBytes.length != 4)
        {
          LOG.log (Level.WARNING, "Error parsing presumed reading {0} (unexpected length for DINT) on instrument {1}.",
            new Object[]{HexUtils.bytesToHex (readBytes), this});
          return null;          
        }
        final int iValue = (int) (((readBytes[0] & 0xff) << 24)
                                | ((readBytes[1] & 0xff) << 16)
                                | ((readBytes[2] & 0xff) << 8)
                                 | (readBytes[3] & 0xff));
        final double integerScale = settings.isAutoRange () ? processCommand_getIntegerScale () : settings.getIntegerScale ();
        readingValue = integerScale * iValue;
        break;
      }
      case SREAL:
      {
        if (readBytes.length != 4)
        {
          LOG.log (Level.WARNING, "Error parsing presumed reading {0} (unexpected length for SREAL) on instrument {1}.",
            new Object[]{HexUtils.bytesToHex (readBytes), this});
        }
        readingValue = Float.intBitsToFloat (ByteBuffer.wrap (readBytes).getInt ());
        break;
      }
      default:
      {
        LOG.log (Level.WARNING, "Null or illegal OFORMAT setting on instrument {0}.",
          new Object[]{this});
        return null;        
      }
    }
    final boolean overflow = status.isHighLow ();
    final String overflowMessage = status.isHighLow () ? "[Over/Under]Flow" : null;
    return new DefaultDigitalMultiMeterReading (
      settings,
      null,
      readingValue,
      settings.getReadingUnit (),
      settings.getResolution (),
      overflow,
      overflowMessage,
      overflow,
      false,  // uncalibrated
      false); // uncorrected
  }
  
  @Override
  protected void onGpibServiceRequestFromInstrument (final byte statusByte)
    throws IOException, InterruptedException, TimeoutException
  {
    if (! this.operationSemaphore.tryAcquire ())
      return;
    try
    {
      final HP3457A_GPIB_Status status;
      if (! HP3457A_GPIB_Status.isError (statusByte))
      {
        status = HP3457A_GPIB_Status.fromSerialPollStatusByte (statusByte);
        fireInstrumentStatusChanged (status);
      }
      else
      {
        final short errorsShort;
        try
        {
          // We cannot use getErrorSync since we have locked out the command processor.
          errorsShort = (short) processCommand_getInt ("ERR"); // ERR?
        }
        catch (InterruptedException | IOException |TimeoutException e)
        {
          status = HP3457A_GPIB_Status.fromSerialPollStatusByte (statusByte);
          fireInstrumentStatusChanged (status);
          throw e;          
        }
        if (! HP3457A_GPIB_Status.isHardwareError (errorsShort))
        {
          status = HP3457A_GPIB_Status.fromSerialPollStatusByteAndErrorsShort (
            statusByte,
            errorsShort);
          fireInstrumentStatusChanged (status);
        }
        else
        {
          final short auxiliaryErrorsShort;
          try
          {
            // We cannot use getAuxiliaryErrorSync since we have locked out the command processor.
            auxiliaryErrorsShort = (short) processCommand_getInt ("AUXERR"); // AUXERR?
          }
          catch (InterruptedException | IOException |TimeoutException e)
          {
            status = HP3457A_GPIB_Status.fromSerialPollStatusByteAndErrorsShort (
              statusByte,
              errorsShort);
            fireInstrumentStatusChanged (status);
            throw e;
          }
          status = HP3457A_GPIB_Status.fromSerialPollStatusByteAndErrorsAndAuxiliaryErrorsShort (
            statusByte,
            errorsShort,
            auxiliaryErrorsShort);
        }
      }
      if (status.isReady ()) // XXX Should we really proceed in case of errors/hardware errors?
      {
        // Check to see if we have any instrument settings already; we need them...
        final HP3457A_GPIB_Settings settings = (HP3457A_GPIB_Settings) getCurrentInstrumentSettings ();
        if (settings == null)
        {
          LOG.log (Level.WARNING, "No settings (yet) on instrument {0}.", new Object[]{this});
          return;
        }
        switch (settings.getTriggerEvent ())
        {
          case HOLD:
            return;
          case AUTO:
          case EXT:
          case SGL:
          case TIMER:
          {
            final int numberOfStoredReadings;
            switch (settings.getReadingMemoryMode ())
            {
              case OFF:
                numberOfStoredReadings = 1;
                break;
              case FIFO:
              case LIFO:
                try
                {
                  numberOfStoredReadings = (int) Math.round (Double.parseDouble (
                    new String (writeAndReadEOISync ("MCOUNT?;"), Charset.forName ("US-ASCII")).trim ()));
                  LOG.log (Level.WARNING, "Number of stored readings: {0}.", new Object[]{numberOfStoredReadings});
                }
                catch (NumberFormatException nfe)
                {
                  throw new IOException ();
                }
                break;
              default:
                throw new IOException ();
            }
            for (int i = 0; i < numberOfStoredReadings; i++)
            {
              final InstrumentReading reading = singleReading (settings, status);
              if (reading != null)
                readingReadFromInstrument (reading);
            }
            break;
          }
          case SYN:
          {
            final InstrumentReading reading = singleReading (settings, status);
            if (reading != null)
              readingReadFromInstrument (reading);
            break;
          }
        }
      }
    }
    finally
    {
      this.operationSemaphore.release ();
    }
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP3457A_GPIB_Instrument
  // SAFE MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean safeMode;
  
  public final boolean isSafeMode ()
  {
    return this.safeMode;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP3457A_GPIB_Instrument
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final void autoCalibrate (final HP3457A_GPIB_Settings.AutoCalibrationType autoCalibrationType)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_AUTO_CALIBRATE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_AUTO_CALIBRATE, autoCalibrationType));
  }
  
  public final void autoCalibrate_All ()
    throws IOException, InterruptedException
  {
    autoCalibrate (HP3457A_GPIB_Settings.AutoCalibrationType.All);
  }
  
  public final void autoCalibrate_AC ()
    throws IOException, InterruptedException
  {
    autoCalibrate (HP3457A_GPIB_Settings.AutoCalibrationType.AC);
  }
  
  public final void autoCalibrate_Ohms ()
    throws IOException, InterruptedException
  {
    autoCalibrate (HP3457A_GPIB_Settings.AutoCalibrationType.Ohms);
  }
  
  public final void setACBandwidthFomFrequency (final double frequency_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_AC_BANDWIDTH,
      HP3457A_InstrumentCommand.ICARG_HP3457A_AC_BANDWIDTH, frequency_Hz));
  }
  
  public final void setACBandwidthLow ()
    throws IOException, InterruptedException
  {
    setACBandwidthFomFrequency (100);
  }
  
  public final void setACBandwidthSlow ()
    throws IOException, InterruptedException
  {
    setACBandwidthLow ();
  }
  
  public final void setACBandwidthHigh ()
    throws IOException, InterruptedException
  {
    setACBandwidthFomFrequency (50000);
  }
  
  public final void setACBandwidthFast ()
    throws IOException, InterruptedException
  {
    setACBandwidthHigh ();
  }
  
  public final void setACBandwidth (final HP3457A_GPIB_Settings.ACBandwidth acBandwidth)
    throws IOException, InterruptedException
  {
    switch (acBandwidth)
    {
      case SLOW: setACBandwidthSlow (); break;
      case FAST: setACBandwidthFast (); break;
      default: throw new IllegalArgumentException ();
    }
  }
  
  public final void setACDCCurrent (final Double maxInput_A, final Double resolution_percent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_AC_DC_CURRENT,
      HP3457A_InstrumentCommand.ICARG_HP3457A_AC_DC_CURRENT_RANGE, maxInput_A,
      HP3457A_InstrumentCommand.ICARG_HP3457A_AC_DC_CURRENT_RESOLUTION, resolution_percent));    
  }
  
  public final void setACDCCurrent (final Double maxInput_A)
    throws IOException, InterruptedException
  {
    setACDCCurrent (maxInput_A, null);
  }
  
  public final void setACDCCurrent ()
    throws IOException, InterruptedException
  {
    setACDCCurrent (null, null);
  }
  
  public final void setACDCVolts (final Double maxInput_V, final Double resolution_percent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_AC_DC_VOLTS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_AC_DC_VOLTS_RANGE, maxInput_V,
      HP3457A_InstrumentCommand.ICARG_HP3457A_AC_DC_VOLTS_RESOLUTION, resolution_percent));    
  }
  
  public final void setACDCVolts (final Double maxInput_V)
    throws IOException, InterruptedException
  {
    setACDCVolts (maxInput_V, null);
  }
  
  public final void setACDCVolts ()
    throws IOException, InterruptedException
  {
    setACDCVolts (null, null);
  }
  
  public final void setACCurrent (final Double maxInput_A, final Double resolution_percent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_AC_CURRENT,
      HP3457A_InstrumentCommand.ICARG_HP3457A_AC_CURRENT_RANGE, maxInput_A,
      HP3457A_InstrumentCommand.ICARG_HP3457A_AC_CURRENT_RESOLUTION, resolution_percent));    
  }
  
  public final void setACCurrent (final Double maxInput_A)
    throws IOException, InterruptedException
  {
    setACCurrent (maxInput_A, null);
  }
  
  public final void setACCurrent ()
    throws IOException, InterruptedException
  {
    setACCurrent (null, null);
  }
  
  public final void setACVolts (final Double maxInput_V, final Double resolution_percent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_AC_VOLTS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_AC_VOLTS_RANGE, maxInput_V,
      HP3457A_InstrumentCommand.ICARG_HP3457A_AC_VOLTS_RESOLUTION, resolution_percent));    
  }
  
  public final void setACVolts (final Double maxInput_V)
    throws IOException, InterruptedException
  {
    setACVolts (maxInput_V, null);
  }
  
  public final void setACVolts ()
    throws IOException, InterruptedException
  {
    setACVolts (null, null);
  }
  
  public final void setGpibAddress (final int gpibAddress)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_GPIB_ADDRESS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_GPIB_ADDRESS, gpibAddress));
  }
  
  public final void setAutoRange (final boolean autoRange)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_AUTORANGE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_AUTORANGE, autoRange));
  }
  
  @Override
  public final void setAutoRange ()
    throws IOException, InterruptedException
  {
    setAutoRange (true);
  }
  
  public final void resetAutoRange ()
    throws IOException, InterruptedException
  {
    setAutoRange (false);
  }
  
  public final short getAuxiliaryErrorSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_AUXILIARY_ERROR);
    addAndProcessCommandSync (command, timeout, unit);
    return (short) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void setAutoZeroMode (final HP3457A_GPIB_Settings.AutoZeroMode autoZeroMode)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_AUTOZERO_MODE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_AUTOZERO_MODE, autoZeroMode));
  }
  
  public final HP3457A_GPIB_Settings.AutoZeroMode getAutoZeroModeSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_AUTOZERO_MODE);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP3457A_GPIB_Settings.AutoZeroMode) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void setBeepMode (final boolean beepEnabled)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_BEEP_MODE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_BEEP_MODE, beepEnabled));
  }
  
  public final void beep ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_BEEP));
  }
  
  public final void calibrate ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_CALIBRATE));
  }
  
  public final void call (final int subProgram)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_CALL_SUBPROGRAM,
      HP3457A_InstrumentCommand.ICARG_HP3457A_CALL_SUBPROGRAM, subProgram));
  }
  
  public final void getCalibrationNumberASync ()
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_CALIBRATION_NUMBER));
  }
  
  public final int getCalibrationNumberSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_CALIBRATION_NUMBER);
    addAndProcessCommandSync (command, timeout, unit);
    return (int) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void setInputChannel (final HP3457A_GPIB_Settings.InputChannel inputChannel)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_CHANNEL,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_CHANNEL, inputChannel));
  }
  
  public final int getChannelSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_CHANNEL);
    addAndProcessCommandSync (command, timeout, unit);
    return (int) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void closeActuatorChannel (final int actuatorChannel)
    throws IOException, InterruptedException
  {
    if (actuatorChannel != 8 && actuatorChannel != 9)
      throw new IllegalArgumentException ();
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_CLOSE_ACTUATOR_CHANNEL,
      HP3457A_InstrumentCommand.ICARG_HP3457A_CLOSE_ACTUATOR_CHANNEL, actuatorChannel));
  }
  
  public final void closeActuatorChannel8 ()
    throws IOException, InterruptedException
  {
    closeActuatorChannel (8);
  }
  
  public final void closeActuatorChannel9 ()
    throws IOException, InterruptedException
  {
    closeActuatorChannel (9);
  }
  
  public final void cardReset ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_CARD_RESET));
  }
  
  public final void clearStatusByte ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_CLEAR_STATUS_BYTE));
  }
  
  public final void setDCCurrent (final Double maxInput_A, final Double resolution_percent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_DC_CURRENT,
      HP3457A_InstrumentCommand.ICARG_HP3457A_DC_CURRENT_RANGE, maxInput_A,
      HP3457A_InstrumentCommand.ICARG_HP3457A_DC_CURRENT_RESOLUTION, resolution_percent));    
  }
  
  public final void setDCCurrent (final Double maxInput_A)
    throws IOException, InterruptedException
  {
    setDCCurrent (maxInput_A, null);
  }
  
  public final void setDCCurrent ()
    throws IOException, InterruptedException
  {
    setDCCurrent (null, null);
  }
  
  public final void setDCVolts (final Double maxInput_V, final Double resolution_percent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_DC_VOLTS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_DC_VOLTS_RANGE, maxInput_V,
      HP3457A_InstrumentCommand.ICARG_HP3457A_DC_VOLTS_RESOLUTION, resolution_percent));    
  }
  
  public final void setDCVolts (final Double maxInput_V)
    throws IOException, InterruptedException
  {
    setDCVolts (maxInput_V, null);
  }
  
  public final void setDCVolts ()
    throws IOException, InterruptedException
  {
    setDCVolts (null, null);
  }
  
  public final void setDelay (final double delay_s)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_DELAY,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_DELAY, delay_s));
  }
  
  public final double getDelay_sSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_DELAY);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void diagnostic ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_DIAGNOSTIC));
  }
  
  public final void display (final HP3457A_GPIB_Settings.DisplayControl displayControl, final String message)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_DISPLAY,
      HP3457A_InstrumentCommand.ICARG_HP3457A_DISPLAY_CONTROL, displayControl,
      HP3457A_InstrumentCommand.ICARG_HP3457A_DISPLAY_MESSAGE, message));    
  }
  
  public final void setErrorMask (final Short mask)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_ERROR_MASK,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_ERROR_MASK, mask));    
  }
  
  public final void setEoi (final boolean eoi)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_EOI,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_EOI, eoi));    
  }
  
  public final void setEoi ()
    throws IOException, InterruptedException
  {
    setEoi (true);
  }
  
  public final void resetEoi ()
    throws IOException, InterruptedException
  {
    setEoi (false);
  }
  
  public final short getErrorSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_ERROR);
    addAndProcessCommandSync (command, timeout, unit);
    return (short) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void setFunctionFast (final byte code)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_FUNCTION_FAST,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FUNCTION_FAST, code));    
  }
  
  public final void setFixedImpedance (final boolean fixedImpedance)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_FIXED_IMPEDANCE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FIXED_IMPEDANCE, fixedImpedance));    
  }
  
  public final void setFixedImpedance ()
    throws IOException, InterruptedException
  {
    setFixedImpedance (true);
  }
  
  public final void resetFixedImpedance ()
    throws IOException, InterruptedException
  {
    setFixedImpedance (false);
  }
  
  public final boolean getFixedImpedanceSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_FIXED_IMPEDANCE);
    addAndProcessCommandSync (command, timeout, unit);
    return (boolean) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final boolean isFixedImpedanceSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    return getFixedImpedanceSync (timeout, unit); 
  }

  public final void setFrequency (final Double maxInput_V_or_A)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_FREQUENCY,
      HP3457A_InstrumentCommand.ICARG_HP3457A_FREQUENCY_AMPLITUDE_RANGE, maxInput_V_or_A)); 
  }
  
  public final void setFrequency ()
    throws IOException, InterruptedException
  {
    setFrequency (null);
  }
  
  public final void setFrequencyOrPeriodSource (final MeasurementMode source)
    throws IOException, InterruptedException
  {
    switch (source)
    {
      case AC_VOLTAGE:
      case AC_DC_VOLTAGE:
      case AC_CURRENT:
      case AC_DC_CURRENT:
        break;
      default:
        throw new IllegalArgumentException ();
    }
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_FREQUENCY_OR_PERIOD_SOURCE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FREQUENCY_OR_PERIOD_SOURCE, source)); 
  }
  
  public final void setFunction (
    final MeasurementMode function,
    final Double maxInput_V_or_A_or_Ohm,
    final Double resolution_percent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_FUNCTION,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FUNCTION_FUNCTION, function,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FUNCTION_RANGE, maxInput_V_or_A_or_Ohm,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FUNCTION_RESOLUTION, resolution_percent));
  }
  
  public final void setFunction (
    final MeasurementMode function,
    final Double maxInput_V_or_A_or_Ohm)
    throws IOException, InterruptedException
  {
    setFunction (function, maxInput_V_or_A_or_Ohm, null);
  }
  
  public final void setFunction (
    final MeasurementMode function)
    throws IOException, InterruptedException
  {
    setFunction (function, null, null);
  }
  
  public final void setFunction ()
    throws IOException, InterruptedException
  {
    setFunction (null, null, null);
  }
  
  public final String getIdSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_ID);
    addAndProcessCommandSync (command, timeout, unit);
    return (String) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void setGpibInputBuffering (final boolean useBuffer)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_GPIB_INPUT_BUFFERING,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_GPIB_INPUT_BUFFERING, useBuffer));    
  }
  
  public final double getIntegerScaleFactorSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_INTEGER_SCALE_FACTOR);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void setLineFrequencyReference_Hz (final double lineFrequencyReference_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_LINE_FREQUENCY_REFERENCE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_LINE_FREQUENCY_REFERENCE, lineFrequencyReference_Hz));    
  }
  
  public final double getLineFrequencyReference_HzSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // XXX always int? Always 50/60?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_LINE_FREQUENCY_REFERENCE);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void getLineFrequencyReference_HzASync ()
    throws IOException, InterruptedException, TimeoutException
  {
    // XXX always int? Always 50/60?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_LINE_FREQUENCY_REFERENCE);
    addCommand (command);
  }
  
  public final double getLineFrequency_HzSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_LINE_FREQUENCY);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void getLineFrequency_HzASync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_LINE_FREQUENCY);
    addCommand (command);
  }
  
  public final void setLockout (final boolean lockout)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_LOCKOUT,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_LOCKOUT, lockout));        
  }

  public final void setLockout ()
    throws IOException, InterruptedException
  {
    setLockout (true);
  }
  
  public final void resetLockout ()
    throws IOException, InterruptedException
  {
    setLockout (false);
  }
  
  public final void setLocked (final boolean locked)
    throws IOException, InterruptedException
  {
    setLockout (locked);
  }

  public final void setMathOperations (
    final HP3457A_GPIB_Settings.MathOperation operationA,
    final HP3457A_GPIB_Settings.MathOperation operationB)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_MATH_OPERATION,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_MATH_OPERATION_A, operationA,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_MATH_OPERATION_B, operationB));        
  }
  
  public final HP3457A_GPIB_Settings.MathOperationDefinition getMathOperationsSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_MATH_OPERATION);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP3457A_GPIB_Settings.MathOperationDefinition) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final int getNumberOfStoredReadingsSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_NUMBER_OF_STORED_READINGS);
    addAndProcessCommandSync (command, timeout, unit);
    return (int) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }

  public final void setReadingMemoryMode (final HP3457A_GPIB_Settings.ReadingMemoryMode mode)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_READING_MEMORY_CONTROL,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_READING_MEMORY_CONTROL, mode));        
  }

  public final void clearReadingsAndSetReadingFormat (final HP3457A_GPIB_Settings.ReadingFormat format)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_FORMAT_READING_MEMORY,
      HP3457A_InstrumentCommand.ICARG_HP3457A_FORMAT_READING_MEMORY, format));        
  }

  public final void setMemorySizes (final Integer readings_bytes, final Integer subprograms_bytes)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_MEMORY_SIZES,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_MEMORY_SIZES_READINGS, readings_bytes,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_MEMORY_SIZES_SUBPROGRAMS, subprograms_bytes));        
  }

  public final void setMemorySizes (final Integer readings_bytes)
    throws IOException, InterruptedException
  {
    setMemorySizes (readings_bytes, null);
  }

  public final void setMemorySizes ()
    throws IOException, InterruptedException
  {
    setMemorySizes (null, null);
  }

  public final HP3457A_GPIB_Settings.MemorySizesDefinition getMemorySizesSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_MEMORY_SIZES);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP3457A_GPIB_Settings.MemorySizesDefinition) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }

  public final void setNumberOfDigits (final int numberOfDigits)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_NUMBER_OF_DIGITS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_NUMBER_OF_DIGITS, numberOfDigits));        
  }

  public final void setADCNumberOfPLCs (final double numberOfPLCs)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_ADC_NUMBER_OF_PLCS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_ADC_NUMBER_OF_PLCS, numberOfPLCs));        
  }

  public final void setNumberOfPowerLineCycles (final double numberOfPowerLineCycles)
    throws IOException, InterruptedException
  {
    setADCNumberOfPLCs (numberOfPowerLineCycles);
  }
  
  public final double getADCNumberOfPLCsSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_ADC_NUMBER_OF_PLCS);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void getADCNumberOfPLCsASync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_ADC_NUMBER_OF_PLCS);
    addCommand (command);
  }
  
  public final double getNumberOfPowerLineCyclesSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    return getADCNumberOfPLCsSync (timeout, unit);
  }
  
  public final void getNumberOfPowerLineCyclesASync ()
    throws IOException, InterruptedException, TimeoutException
  {
    getADCNumberOfPLCsASync ();
  }
  
  public final void setNumberOfReadings (final int count, final HP3457A_GPIB_Settings.TriggerEvent sampleEvent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_NUMBER_OF_READINGS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_NUMBER_OF_READINGS_COUNT, count,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_NUMBER_OF_READINGS_EVENT, sampleEvent));
  }

  public final void setNumberOfReadings (final int count)
    throws IOException, InterruptedException
  {
    if (getCurrentInstrumentSettings () == null)
      throw new UnsupportedOperationException ();
    setNumberOfReadings (count, ((HP3457A_GPIB_Settings) getCurrentInstrumentSettings ()).getSampleEvent ());
  }

  public final void setSampleEvent (final HP3457A_GPIB_Settings.TriggerEvent sampleEvent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_NUMBER_OF_READINGS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_NUMBER_OF_READINGS_COUNT, null,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_NUMBER_OF_READINGS_EVENT, sampleEvent));
  }

  public final HP3457A_GPIB_Settings.TriggerDefinition getNumberOfReadingsSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_NUMBER_OF_READINGS);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP3457A_GPIB_Settings.TriggerDefinition) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }

  public final void setOffsetCompensation (final boolean offsetCompensation)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_OFFSET_COMPENSATION,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_OFFSET_COMPENSATION, offsetCompensation));
  }

  public final void setOffsetCompensation ()
    throws IOException, InterruptedException
  {
    setOffsetCompensation (true);
  }
  
  public final void resetOffsetCompensation ()
    throws IOException, InterruptedException
  {
    setOffsetCompensation (false);
  }
  
  public final boolean getOffsetCompensationSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_OFFSET_COMPENSATION);
    addAndProcessCommandSync (command, timeout, unit);
    return (boolean) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final boolean isOffsetCompensationSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    return getOffsetCompensationSync (timeout, unit);
  }

  public final void setOutputFormat (final HP3457A_GPIB_Settings.ReadingFormat readingFormat)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_OUTPUT_FORMAT,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_OUTPUT_FORMAT, readingFormat));
  }

  public final void setReadingFormat (final HP3457A_GPIB_Settings.ReadingFormat readingFormat)
    throws IOException, InterruptedException
  {
    setOutputFormat (readingFormat);
  }

  public final void setOhms (final Double maxInput_Ohm, final Double resolution_percent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_OHMS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_OHMS_RANGE, maxInput_Ohm,
      HP3457A_InstrumentCommand.ICARG_HP3457A_OHMS_RESOLUTION, resolution_percent));    
  }
  
  public final void setOhms (final Double maxInput_Ohm)
    throws IOException, InterruptedException
  {
    setOhms (maxInput_Ohm, null);
  }
  
  public final void setOhms ()
    throws IOException, InterruptedException
  {
    setOhms (null, null);
  }
  
  public final void setOhms4W (final Double maxInput_Ohm, final Double resolution_percent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_4WIRE_OHMS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_4WIRE_OHMS_RANGE, maxInput_Ohm,
      HP3457A_InstrumentCommand.ICARG_HP3457A_4WIRE_OHMS_RESOLUTION, resolution_percent));    
  }
  
  public final void setOhms4W (final Double maxInput_Ohm)
    throws IOException, InterruptedException
  {
    setOhms4W (maxInput_Ohm, null);
  }
  
  public final void setOhms4W ()
    throws IOException, InterruptedException
  {
    setOhms4W (null, null);
  }
  
  public final void openActuatorChannel8 (final Boolean switchDelay)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_OPEN_ACTUATOR_CHANNEL,
      HP3457A_InstrumentCommand.ICARG_HP3457A_OPEN_ACTUATOR_CHANNEL_CHANNEL, 8,
      HP3457A_InstrumentCommand.ICARG_HP3457A_OPEN_ACTUATOR_CHANNEL_CONTROL, switchDelay));
  }

  public final void openActuatorChannel8 ()
    throws IOException, InterruptedException
  {
    openActuatorChannel8 (null);
  }

  public final void setActiveActuatorChannel8 (final boolean active)
    throws IOException, InterruptedException
  {
    if (active)
      openActuatorChannel8 (null);
    else
      closeActuatorChannel (8);
  }

  public final void setSwitchDelayChannel8 (final boolean switchDelay)
    throws IOException, InterruptedException
  {
    openActuatorChannel8 (switchDelay);
  }

  public final void openActuatorChannel9 (final Boolean switchDelay)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_OPEN_ACTUATOR_CHANNEL,
      HP3457A_InstrumentCommand.ICARG_HP3457A_OPEN_ACTUATOR_CHANNEL_CHANNEL, 9,
      HP3457A_InstrumentCommand.ICARG_HP3457A_OPEN_ACTUATOR_CHANNEL_CONTROL, switchDelay));
  }
  
  public final void openActuatorChannel9 ()
    throws IOException, InterruptedException
  {
    openActuatorChannel9 (null);
  }

  public final void setActiveActuatorChannel9 (final boolean active)
    throws IOException, InterruptedException
  {
    if (active)
      openActuatorChannel9 (null);
    else
      closeActuatorChannel (9);
  }

  public final void setSwitchDelayChannel9 (final boolean switchDelay)
    throws IOException, InterruptedException
  {
    openActuatorChannel9 (switchDelay);
  }

  public final HP3457A_GPIB_Settings.InstalledOption getInstalledOptionSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_OFFSET_COMPENSATION);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP3457A_GPIB_Settings.InstalledOption) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void pauseSubprogram ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_PAUSE_SUBPROGRAM));        
  }

  public final void setPeriod (final Double maxInput_V_or_A)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_PERIOD,
      HP3457A_InstrumentCommand.ICARG_HP3457A_PERIOD_AMPLITUDE_RANGE, maxInput_V_or_A)); 
  }
  
  public final void setPeriod ()
    throws IOException, InterruptedException
  {
    setPeriod (null);
  }
  
  public final void preset ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_SET_PRESET_STATE));
  }
  
  public final void setRange (final Double maxInput_V_A_Ohm, final Double resolution_percent)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_RANGE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_RANGE_RANGE, maxInput_V_A_Ohm,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_RANGE_RESOLUTION, resolution_percent));    
  }
  
  public final void setRange (final Double maxInput_V_A_Ohm)
    throws IOException, InterruptedException
  {
    setRange (maxInput_V_A_Ohm, null);
  }
  
  public final void setRange ()
    throws IOException, InterruptedException
  {
    setRange (null, null);
  }
  
  public final double /* XXX int? XXX */ getRangeSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_RANGE);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void reset ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_RESET));
  }
  
  public final String getRevisionSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_REVISION);
    addAndProcessCommandSync (command, timeout, unit);
    return (String) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final double recallMathSync (final HP3457A_GPIB_Settings.MathRegister register, final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_RECALL_MATH,
      HP3457A_InstrumentCommand.ICARG_HP3457A_RECALL_MATH, register);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final double[] recallMemorySync (
    final int first,
    final int count,
    final int record,
    final long timeout,
    final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_RECALL_MEMORY,
      HP3457A_InstrumentCommand.ICARG_HP3457A_RECALL_MEMORY_FIRST, first,
      HP3457A_InstrumentCommand.ICARG_HP3457A_RECALL_MEMORY_COUNT, count,
      HP3457A_InstrumentCommand.ICARG_HP3457A_RECALL_MEMORY_RECORD, record);
    addAndProcessCommandSync (command, timeout, unit);
    return (double[]) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void setServiceRequestMask (final byte mask)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_SERVICE_REQUEST_MASK,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_SERVICE_REQUEST_MASK, mask));    
  }
  
  public final void recallState (final int slot)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_RECALL_STATE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_RECALL_STATE, slot));    
  }
    
  public final void scanAdvance (final HP3457A_GPIB_Settings.ScanAdvanceMode mode)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SCAN_ADVANCE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SCAN_ADVANCE, mode));    
  }
  
  public final void clearAllSubprograms ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_CLEAR_ALL_SUBPROGRAMS));
  }
  
  public final void scratch ()
    throws IOException, InterruptedException
  {
    clearAllSubprograms ();
  }
  
  public final void secure ()
    throws IOException, InterruptedException
  {
    // XXX
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_SECURE));
  }
  
  public final void setScanList (final int[] scanList)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_SCAN_LIST,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_SCAN_LIST, scanList));    
  }
  
  public final int getScanListSizeSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_SCAN_LIST_SIZE);
    addAndProcessCommandSync (command, timeout, unit);
    return (int) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void storeMath (final HP3457A_GPIB_Settings.MathRegister register, final Double number)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_STORE_MATH,
      HP3457A_InstrumentCommand.ICARG_HP3457A_STORE_MATH_REGISTER, register,
      HP3457A_InstrumentCommand.ICARG_HP3457A_STORE_MATH_NUMBER, number));    
  }
  
  public final void fireServiceRequest ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_FIRE_SERVICE_REQUEST));
  }
    
  public final void storeState (final int slot)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_STORE_STATE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_STORE_STATE, slot));    
  }
  
  public final byte getStatusByteSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_STATUS_BYTE);
    addAndProcessCommandSync (command, timeout, unit);
    return (byte) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void storeSubprogram (final int slot)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_STORE_SUBPROGRAM,
      HP3457A_InstrumentCommand.ICARG_HP3457A_STORE_SUBPROGRAM, slot));    
  }
  
  public final void subprogramEnd ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_SUBPROGRAM_END));
  }
  
  public final void setTriggerArmEvent (final HP3457A_GPIB_Settings.TriggerEvent event, final Integer nrArms)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_TRIGGER_ARM,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_TRIGGER_ARM_EVENT, event,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_TRIGGER_ARM_NR_ARMS, nrArms));    
  }
  
  public final void setTriggerArmEvent (final HP3457A_GPIB_Settings.TriggerEvent event)
    throws IOException, InterruptedException
  {
    setTriggerArmEvent (event, null);
  }
  
  public final void setTriggerArmEvent ()
    throws IOException, InterruptedException
  {
    setTriggerArmEvent (null, null);
  }
  
  public final HP3457A_GPIB_Settings.TriggerEvent getTriggerArmEventSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_TRIGGER_ARM);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP3457A_GPIB_Settings.TriggerEvent) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void setTriggerBuffering (final boolean triggerBuffering)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_TRIGGER_BUFFERING,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_TRIGGER_BUFFERING, triggerBuffering));
  }

  public final void setTriggerBuffering ()
    throws IOException, InterruptedException
  {
    setTriggerBuffering (true);
  }
  
  public final void resetTriggerBuffering ()
    throws IOException, InterruptedException
  {
    setTriggerBuffering (false);
  }
  
  public final void setMeasurementTerminals (final HP3457A_GPIB_Settings.MeasurementTerminals terminals)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_MEASUREMENT_TERMINALS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_MEASUREMENT_TERMINALS, terminals));
  }

  public final HP3457A_GPIB_Settings.MeasurementTerminals getMeasurementTerminalsSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_MEASUREMENT_TERMINALS);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP3457A_GPIB_Settings.MeasurementTerminals) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void internalTest ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_INTERNAL_TEST));
  }
  
  public final void setTimeIntervalForNRDGS (final double interval_s)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_TIME_INTERVAL_FOR_NRDGS,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_TIME_INTERVAL_FOR_NRDGS, interval_s));
  }
  
  public final void setTimer (final double timer_s)
    throws IOException, InterruptedException
  {
    setTimeIntervalForNRDGS (timer_s);
  }
  
  public final double getTimeIntervalForNRDGS_sSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_TIME_INTERVAL_FOR_NRDGS);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final double getTimer_sSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    return getTimeIntervalForNRDGS_sSync (timeout, unit);
  }
  
  public final void playTone (final double frequency_Hz, final double duration_s)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_PLAY_TONE,
      HP3457A_InstrumentCommand.ICARG_HP3457A_PLAY_TONE_FREQUENCY, frequency_Hz,
      HP3457A_InstrumentCommand.ICARG_HP3457A_PLAY_TONE_DURATION, duration_s));
  }
  
  public final void setTriggerEvent (final HP3457A_GPIB_Settings.TriggerEvent event)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_SET_TRIGGER_EVENT,
      HP3457A_InstrumentCommand.ICARG_HP3457A_SET_TRIGGER_EVENT, event));
  }
  
  public final HP3457A_GPIB_Settings.TriggerEvent getTriggerEventSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_GET_TRIGGER_EVENT);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP3457A_GPIB_Settings.TriggerEvent) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void trigger ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_TRIGGER));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP3457A_GPIB_Instrument - ONOFFICIAL
  // PEEK/POKE
  // READ/WRITE CALIBRATION DATA
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final void peekASync (final int address)
    throws IOException, InterruptedException, TimeoutException
  {
    if (address < 0 || address > 32767)
      throw new IllegalArgumentException ();
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_PEEK,
      HP3457A_InstrumentCommand.ICARG_HP3457A_PEEK_ADDRESS, address);
    addCommand (command);
  }
  
  public final byte peekSync (final int address)
    throws IOException, InterruptedException, TimeoutException
  {
    if (address < 0 || address > 32767)
      throw new IllegalArgumentException ();
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3457A_InstrumentCommand.IC_HP3457A_PEEK,
      HP3457A_InstrumentCommand.ICARG_HP3457A_PEEK_ADDRESS, address);
    addAndProcessCommandSync (command, AbstractGpibInstrument.DEFAULT_GET_STATUS_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    if (! (command.containsKey (InstrumentCommand.IC_RETURN_VALUE_KEY)
           && command.get (InstrumentCommand.IC_RETURN_VALUE_KEY) != null))
    {
      LOG.log (Level.WARNING, "Error obtaining value from peekSync on instrument {0}.",
        new Object[]{this});
      // XXX We should really check any marked Exceptions on the InstrumentCommand!
      throw new IOException ();
    }
    return (byte) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void pokeSync (final int address, final byte data)
    throws IOException, InterruptedException, TimeoutException
  {
    if (address < 0 || address > 32767)
      throw new IllegalArgumentException ();
    throw new UnsupportedOperationException ();
  }

  /** The timeout in seconds for reading calibration data from the instrument.
   * 
   */
  public final static long READ_CALIBRATION_DATA_TIMEOUT_S = 120;
  
  public final HP3457A_GPIB_CalibrationData readCalibrationDataSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_READ_CALIBRATION_DATA);
    addAndProcessCommandSync (command, HP3457A_GPIB_Instrument.READ_CALIBRATION_DATA_TIMEOUT_S, TimeUnit.SECONDS);
    if (! (command.containsKey (InstrumentCommand.IC_RETURN_VALUE_KEY)
           && command.get (InstrumentCommand.IC_RETURN_VALUE_KEY) != null))
    {
      LOG.log (Level.WARNING, "Error obtaining value from readCalibrationDataSync on instrument {0}.",
        new Object[]{this});
      // XXX We should really check any marked Exceptions on the InstrumentCommand!
      throw new IOException ();
    }
    return (HP3457A_GPIB_CalibrationData) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);    
  }
  
  public final void readCalibrationDataASync ()
    throws IOException, InterruptedException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (HP3457A_InstrumentCommand.IC_HP3457A_READ_CALIBRATION_DATA);
    addCommand (command);
  }
  
  public final void writeCalibrationDataSync (final HP3457A_GPIB_CalibrationData calibrationData)
    throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // PROCESS COMMAND - UTILITY METHODS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  protected final void processCommand_setBoolean (final String instrumentCommandString, final boolean value)
    throws IOException, InterruptedException, TimeoutException
  {
    writeSync (instrumentCommandString.trim () + " " + (value ? "1" : "0") + ";");
  }
  
  protected final boolean processCommand_setBoolean (
    final InstrumentCommand instrumentCommand,
    final String argumentKey,
    final String instrumentCommandString)
    throws IOException, InterruptedException, TimeoutException
  {
    final boolean value = (boolean) instrumentCommand.get (argumentKey);
    processCommand_setBoolean (instrumentCommandString, value);
    return value;
  }
  
  protected final boolean processCommand_getBoolean (final String instrumentCommandString)
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryString =
      instrumentCommandString.trim ().endsWith ("?") ? instrumentCommandString.trim ()
                                                     : (instrumentCommandString.trim () + "?");
    final String queryReturn = new String (writeAndReadEOISync (queryString + ";"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final int queryReturnInt;
    try
    {
      queryReturnInt = (int) Math.round (Double.parseDouble (queryReturn));
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    final boolean value;
    switch (queryReturnInt)
    {
      case 0:
        value = false;
        break;
      case 1:
        value = true;
        break;
      default:
        throw new IOException ();
    }
    return value;
  }
  
  protected final boolean processCommand_getBoolean (
    final InstrumentCommand instrumentCommand,
    final String instrumentCommandString)
    throws IOException, InterruptedException, TimeoutException
  {
    final boolean value = processCommand_getBoolean (instrumentCommandString);
    instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, value);
    return value;
  }
  
  protected final boolean processCommand_setAndGetBoolean (
    final InstrumentCommand instrumentCommand,
    final String argumentKey,
    final String instrumentCommandString)
    throws IOException, InterruptedException, TimeoutException
  {
    final boolean value = (boolean) instrumentCommand.get (argumentKey);
    processCommand_setBoolean (instrumentCommandString, value);
    final boolean newValue = processCommand_getBoolean (instrumentCommand, instrumentCommandString);
    return newValue;
  }
  
  protected final void processCommand_setDouble (final String instrumentCommandString, final double value)
    throws IOException, InterruptedException, TimeoutException
  {
    writeSync (instrumentCommandString.trim () + " " + Double.toString (value) + ";");
  }
  
  protected final double processCommand_getDouble (final String instrumentCommandString)
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryString =
      instrumentCommandString.trim ().endsWith ("?") ? instrumentCommandString.trim ()
                                                     : (instrumentCommandString.trim () + "?");
    final String queryReturn = new String (writeAndReadEOISync (queryString + ";"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    try
    {
      return Double.parseDouble (queryReturn);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
  }
  
  protected final double processCommand_getDouble (
    final InstrumentCommand instrumentCommand,
    final String instrumentCommandString)
    throws IOException, InterruptedException, TimeoutException
  {
    final double value = processCommand_getDouble (instrumentCommandString);
    instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, value);
    return value;
  }
  
  protected final double processCommand_setAndGetDouble (
    final InstrumentCommand instrumentCommand,
    final String argumentKey,
    final String instrumentCommandString)
    throws IOException, InterruptedException, TimeoutException
  {
    final double value = (double) instrumentCommand.get (argumentKey);
    processCommand_setDouble (instrumentCommandString, value);
    final double newValue = processCommand_getDouble (instrumentCommand, instrumentCommandString);
    return newValue;
  }
  
  protected final void processCommand_setInt (final String instrumentCommandString, final int value)
    throws IOException, InterruptedException, TimeoutException
  {
    writeSync (instrumentCommandString.trim () + " " + Integer.toString (value) + ";");
  }
  
  protected final int processCommand_getInt (final String instrumentCommandString)
    throws IOException, InterruptedException, TimeoutException
  {
    final double doubleValue = processCommand_getDouble (instrumentCommandString);
    return (int) Math.round (doubleValue);
  }
  
  protected final int processCommand_setAndGetInt (final String instrumentCommandString, final int value)
    throws IOException, InterruptedException, TimeoutException
  {
    processCommand_setInt (instrumentCommandString, value);
    return processCommand_getInt (instrumentCommandString);
  }
  
  protected final int processCommand_getInt (
    final InstrumentCommand instrumentCommand,
    final String instrumentCommandString)
    throws IOException, InterruptedException, TimeoutException
  {
    final int value = processCommand_getInt (instrumentCommandString);
    instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, value);
    return value;
  }
  
  protected final int processCommand_setAndGetInt (
    final InstrumentCommand instrumentCommand,
    final String argumentKey,
    final String instrumentCommandString)
    throws IOException, InterruptedException, TimeoutException
  {
    final int value = (int) instrumentCommand.get (argumentKey);
    processCommand_setInt (instrumentCommandString, value);
    final int newValue = processCommand_getInt (instrumentCommand, instrumentCommandString);
    return newValue;
  }
  
  protected final HP3457A_GPIB_Settings.TriggerEvent processCommand_getTriggerArmEvent ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("TARM?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final HP3457A_GPIB_Settings.TriggerEvent triggerArmEvent;
    try
    {
      triggerArmEvent = HP3457A_GPIB_Settings.TriggerEvent.fromCode (
        (int) Math.round (Double.parseDouble (queryReturn)));
      switch (triggerArmEvent)
      {
        case AUTO:
        case EXT:
        case HOLD:
        case SYN:
          break;
        case SGL:
        case TIMER:
          throw new IOException ();
        default:
          throw new RuntimeException ();
      }
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return triggerArmEvent;
  }
  
  protected final HP3457A_GPIB_Settings.TriggerEvent processCommand_getTriggerEvent ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("TRIG?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final HP3457A_GPIB_Settings.TriggerEvent triggerEvent;
    try
    {
      triggerEvent = HP3457A_GPIB_Settings.TriggerEvent.fromCode (
        (int) Math.round (Double.parseDouble (queryReturn)));
      switch (triggerEvent)
      {
        case AUTO:
        case EXT:
        case HOLD:
        case SYN:
          break;
        case SGL:
        case TIMER:
          throw new IOException ();
        default:
          throw new RuntimeException ();
      }
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return triggerEvent;
  }
  
  protected final HP3457A_GPIB_Settings.TriggerEvent processCommand_getSampleEvent ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("NRDGS?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final String[] components = queryReturn.split (",");
    if (components == null || components.length != 2)
      throw new IOException ();
    final HP3457A_GPIB_Settings.TriggerEvent sampleEvent;
    try
    {
      sampleEvent = HP3457A_GPIB_Settings.TriggerEvent.fromCode (
        (int) Math.round (Double.parseDouble (components[1])));
      switch (sampleEvent)
      {
        case AUTO:
        case EXT:
        case SYN:
        case TIMER:
          break;
        case SGL:
        case HOLD:
          throw new IOException ();
        default:
          throw new RuntimeException ();
      }
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return sampleEvent;
  }
  
  protected final HP3457A_GPIB_Settings processCommand_updateTriggerSettings (final HP3457A_GPIB_Settings settings)
    throws IOException, InterruptedException, TimeoutException
  {
    if (settings == null)
      throw new IllegalArgumentException ();
    final HP3457A_GPIB_Settings.TriggerEvent triggerArmEventRead = processCommand_getTriggerArmEvent ();
    final HP3457A_GPIB_Settings.TriggerEvent triggerEventRead = processCommand_getTriggerEvent ();
    final HP3457A_GPIB_Settings.TriggerEvent sampleEventRead = processCommand_getSampleEvent ();
    return settings
            .withTriggerArmEvent (triggerArmEventRead)
            .withTriggerEvent (triggerEventRead)
            .withSampleEvent (sampleEventRead);
  }
  
  protected final HP3457A_GPIB_Settings.InstalledOption processCommand_getInstalledOption ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("OPT?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final int queryReturnInt;
    try
    {
      queryReturnInt = (int) Math.round (Double.parseDouble (queryReturn));
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    final HP3457A_GPIB_Settings.InstalledOption installedOption;
    switch (queryReturnInt)
    {
      case 0:
        installedOption = HP3457A_GPIB_Settings.InstalledOption.NONE;
        break;
      case 44491:
        installedOption = HP3457A_GPIB_Settings.InstalledOption.HP_44491;
        break;
      case 44492:
        installedOption = HP3457A_GPIB_Settings.InstalledOption.HP_44492;
        break;
      default:
        throw new IOException ();
    }
    return installedOption;
  }
  
  protected final HP3457A_GPIB_Settings.AutoZeroMode processCommand_getAutoZeroMode ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("AZERO?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final int queryReturnInt;
    try
    {
      queryReturnInt = (int) Math.round (Double.parseDouble (queryReturn));
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    final HP3457A_GPIB_Settings.AutoZeroMode autoZeroMode;
    switch (queryReturnInt)
    {
      case 0:
        autoZeroMode = HP3457A_GPIB_Settings.AutoZeroMode.FunctionOrRangeChanges;
        break;
      case 1:
        autoZeroMode = HP3457A_GPIB_Settings.AutoZeroMode.Always;
        break;
      default:
        throw new IOException ();
    }
    return autoZeroMode;
  }
  
  protected final HP3457A_GPIB_Settings.MeasurementTerminals processCommand_getMeasurementTerminals ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("TERM?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final HP3457A_GPIB_Settings.MeasurementTerminals terminals;
    try
    {
      terminals = HP3457A_GPIB_Settings.MeasurementTerminals.fromCode ((int) Math.round (Double.parseDouble (queryReturn)));
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return terminals;
  }
  
  protected final int processCommand_getCalibrationNumber ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("CALNUM?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final int calibrationNumber;
    try
    {
      calibrationNumber = (int) Math.round (Double.parseDouble (queryReturn));
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return calibrationNumber;
  }
  
  protected final double processCommand_getIntegerScale ()
    throws IOException, InterruptedException, TimeoutException
  {
    return processCommand_getDouble ("ISCALE");
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
    this.operationSemaphore.acquire ();
    final GpibDevice device = (GpibDevice) getDevice ();
    HP3457A_GPIB_Settings instrumentSettings = (HP3457A_GPIB_Settings) getCurrentInstrumentSettings ();
    HP3457A_GPIB_Settings newInstrumentSettings = null;
    try
    {
      switch (commandString)
      {
        case InstrumentCommand.IC_NOP_KEY:
          break;
        case InstrumentCommand.IC_GET_SETTINGS_KEY:
        {
          throw new UnsupportedOperationException ();
        }
        case InstrumentCommand.IC_RESOLUTION:
        {
          throw new UnsupportedOperationException ();
//          final Resolution resolution = (Resolution) instrumentCommand.get (InstrumentCommand.ICARG_RESOLUTION);
//          final List<Resolution> supportedResolutions = HP3457A_GPIB_Instrument.SUPPORTED_RESOLUTIONS_AS_LIST;
//          if (! supportedResolutions.contains (resolution))
//          {
//            LOG.log (Level.WARNING, "Resolution {0} is not supported on HP3457A.",
//              new Object[]{resolution});
//            throw new UnsupportedOperationException ();
//          }
//          switch (resolution)
//          {
//            case DIGITS_5_5: writeSync ("N5\r\n"); break;
//            case DIGITS_4_5: writeSync ("N4\r\n"); break;
//            case DIGITS_3_5: writeSync ("N3\r\n"); break;
//            default:         throw new RuntimeException ();
//          }
//          newInstrumentSettings = getSettingsFromInstrumentSync ();
//          break;
        }
        case InstrumentCommand.IC_MEASUREMENT_MODE:
        {
          final MeasurementMode measurementMode =
            (MeasurementMode) instrumentCommand.get (InstrumentCommand.ICARG_MEASUREMENT_MODE);
          if (! supportsMeasurementMode (measurementMode))
            throw new UnsupportedOperationException ();
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          switch (measurementMode)
          {
            case DC_VOLTAGE:    writeSync ("DCV;");   break;
            case AC_VOLTAGE:    writeSync ("ACV;");   break;
            case AC_DC_VOLTAGE: writeSync ("ACDCV;"); break;
            case RESISTANCE_2W: writeSync ("OHM;");   break;
            case RESISTANCE_4W: writeSync ("OHMF;");  break;
            case DC_CURRENT:    writeSync ("DCI;");   break;
            case AC_CURRENT:    writeSync ("ACI;");   break;
            case AC_DC_CURRENT: writeSync ("ACDCI;"); break;
            case FREQUENCY:     writeSync ("FREQ;");  break;
            case PERIOD:        writeSync ("PER;");   break;
            default:
              throw new UnsupportedOperationException ();
          }
          newInstrumentSettings = instrumentSettings.withMeasurementMode (measurementMode);
          break;
        }
        case InstrumentCommand.IC_AUTO_RANGE:
        {
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          writeSync ("ARANGE 1;");
          newInstrumentSettings = instrumentSettings.withAutoRange (true);
          break;
        }
        case InstrumentCommand.IC_RANGE:
        {
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          final Range range = (Range) instrumentCommand.get (InstrumentCommand.ICARG_RANGE);
          if (range == null)
            throw new IllegalArgumentException ();
          final MeasurementMode measurementMode = instrumentSettings.getMeasurementMode ();
          final List<Range> supportedRanges = SUPPORTED_RANGES_MAP.get (measurementMode);
          if (! supportedRanges.contains (range))
          {
            LOG.log (Level.WARNING, "Range {0} is not supported on HP3457A in mode {1}.",
              new Object[]{range, measurementMode});
            throw new UnsupportedOperationException ();
          }
          final double maxValue;
          switch (measurementMode)
          {
            case DC_VOLTAGE:
            case AC_VOLTAGE:
            case AC_DC_VOLTAGE:
              maxValue = Unit.convertToUnit (range.getMaxValue (), range.getUnit (), Unit.UNIT_V);
              break;
            case RESISTANCE_2W:
            case RESISTANCE_4W:
              maxValue = Unit.convertToUnit (range.getMaxValue (), range.getUnit (), Unit.UNIT_Ohm);
              break;
            case DC_CURRENT:
            case AC_CURRENT:
            case AC_DC_CURRENT:
              maxValue = Unit.convertToUnit (range.getMaxValue (), range.getUnit (), Unit.UNIT_A);
              break;
            case FREQUENCY:
            case PERIOD:
              LOG.log (Level.WARNING,
                "Cannot set frequency/period measurement range (unsupported by HP-3457A) on instrument {0}.",
                new Object[]{this});
              return;
            default:
              throw new UnsupportedOperationException ();
          }
          writeSync ("R " + Double.toString (maxValue) + ";");
          final Range newRange = DefaultRange.fromRangeDouble (measurementMode, processCommand_getDouble ("RANGE"));
          newInstrumentSettings = instrumentSettings.withRange (newRange);
          break;
        }
        case InstrumentCommand.IC_AUTO_ZERO:
        {
          final boolean autoZero = (boolean) instrumentCommand.get (InstrumentCommand.ICARG_AUTO_ZERO);
          writeSync (autoZero ? "AZERO 1;" : "AZERO 0;");
          if (instrumentSettings != null)
            newInstrumentSettings = instrumentSettings.withAutoZeroMode (
              autoZero ? HP3457A_GPIB_Settings.AutoZeroMode.Always
                       : HP3457A_GPIB_Settings.AutoZeroMode.FunctionOrRangeChanges);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_PEEK:
        {
          // Not officially documented; see https://www.eevblog.com/forum/metrology/dumping-cal-ram-of-a-hp3457a/
          final int address = (int) instrumentCommand.get (
            HP3457A_InstrumentCommand.ICARG_HP3457A_PEEK_ADDRESS);
          byte firstByte;
          final String queryReturn =
            new String (writeAndReadEOISync ("PEEK " + Integer.toString (address) + ";"), Charset.forName ("US-ASCII")).trim ();
          final int value;
          try
          {
            value = (int) Math.round (Double.parseDouble (queryReturn));
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          firstByte = (byte) ((value & 0xff00) >>> 8);
          if (! instrumentCommand.isSynchronous ())
            LOG.log (Level.INFO,
              "Peek from {0}, address {1}: {2}.",
              new Object[]{this, address, HexUtils.bytesToHex (new byte[]{firstByte})});
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, firstByte);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_POKE:
        {
          // Not officially documented; see https://www.eevblog.com/forum/metrology/dumping-cal-ram-of-a-hp3457a/
          throw new UnsupportedOperationException ();
          // break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_READ_CALIBRATION_DATA:
        {
          // Not officially documented; see https://www.eevblog.com/forum/metrology/dumping-cal-ram-of-a-hp3457a/
          final byte[] bytes = new byte[0x200-0x40];
          byte firstByte;
          byte secondByte = 0; // irrelevant...
          for (int address = 0x40; address <= 0x1ff; address++)
          {
            final String queryReturn =
              new String (writeAndReadEOISync ("PEEK " + address + ";"), Charset.forName ("US-ASCII")).trim ();
            final int value;
            try
            {
              value = (int) Math.round (Double.parseDouble (queryReturn));
            }
            catch (NumberFormatException nfe)
            {
              throw new IOException ();
            }
            firstByte = (byte) ((value & 0xff00) >>> 8);
            if (address > 0x40 && firstByte != secondByte)
            {
              LOG.log (Level.WARNING, "Sanity check failed on calibration data while reading.");
              throw new IOException ();
            }
            secondByte = (byte) (value & 0xff);
            bytes[address - 0x40] = firstByte;
          }
          if (! instrumentCommand.isSynchronous ())
            LOG.log (Level.INFO, "Read calibration data from {0}: {1}.", new Object[]{this, HexUtils.bytesToHex (bytes)});
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, new HP3457A_GPIB_CalibrationData (bytes));
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_WRITE_CALIBRATION_DATA:
        {
          // Not officially documented; see https://www.eevblog.com/forum/metrology/dumping-cal-ram-of-a-hp3457a/
          throw new UnsupportedOperationException ();
          // break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_AUTO_CALIBRATE:
        {
          // ACAL
          final HP3457A_GPIB_Settings.AutoCalibrationType autoCalibrationType =
            (HP3457A_GPIB_Settings.AutoCalibrationType) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_AUTO_CALIBRATE);
          switch (autoCalibrationType)
          {
            case All:  writeSync ("ACAL 1;"); break;
            case AC:   writeSync ("ACAL 2;"); break;
            case Ohms: writeSync ("ACAL 3;"); break;
            default:
              throw new UnsupportedOperationException ();
          }
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_AC_BANDWIDTH:
        {
          // ACBAND
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          final double frequency_Hz =
            (double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_AC_BANDWIDTH);
          writeSync ("ACBAND " + Double.toString (frequency_Hz) + ";");
          newInstrumentSettings = instrumentSettings.withACBandwidth (
            frequency_Hz < 400 ? HP3457A_GPIB_Settings.ACBandwidth.SLOW : HP3457A_GPIB_Settings.ACBandwidth.FAST);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_AC_DC_CURRENT:
        {
          // ACDCI
          final Double range_A =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_AC_DC_CURRENT_RANGE);
          final Double resolution_percent =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_AC_DC_CURRENT_RESOLUTION);
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          writeSync ("ACDCI" +
            (range_A != null ? ("," + Double.toString (range_A)) : "") +
            (resolution_percent != null ? ("," + Double.toString (resolution_percent)) : "")  +
            ";");
          newInstrumentSettings = instrumentSettings.withMeasurementMode (MeasurementMode.AC_DC_CURRENT);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_AC_DC_VOLTS:
        {
          // ACDCV
          final Double range_V =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_AC_DC_VOLTS_RANGE);
          final Double resolution_percent =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_AC_DC_VOLTS_RESOLUTION);
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          writeSync ("ACDCV" +
            (range_V != null ? ("," + Double.toString (range_V)) : "") +
            (resolution_percent != null ? ("," + Double.toString (resolution_percent)) : "")  +
            ";");
          newInstrumentSettings = instrumentSettings.withMeasurementMode (MeasurementMode.AC_DC_VOLTAGE);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_AC_CURRENT:
        {
          // ACI
          final Double range_A =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_AC_CURRENT_RANGE);
          final Double resolution_percent =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_AC_CURRENT_RESOLUTION);
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          writeSync ("ACI" +
            (range_A != null ? ("," + Double.toString (range_A)) : "") +
            (resolution_percent != null ? ("," + Double.toString (resolution_percent)) : "")  +
            ";");
          newInstrumentSettings = instrumentSettings.withMeasurementMode (MeasurementMode.AC_CURRENT);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_AC_VOLTS:
        {
          // ACV
          final Double range_V =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_AC_VOLTS_RANGE);
          final Double resolution_percent =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_AC_VOLTS_RESOLUTION);
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          writeSync ("ACV" +
            (range_V != null ? ("," + Double.toString (range_V)) : "") +
            (resolution_percent != null ? ("," + Double.toString (resolution_percent)) : "")  +
            ";");
          newInstrumentSettings = instrumentSettings.withMeasurementMode (MeasurementMode.AC_CURRENT);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_GPIB_ADDRESS:
        {
          // ADDRESS
          final int gpibAddress =
            (int) instrumentCommand.get (HP3457A_InstrumentCommand.ICARG_HP3457A_SET_GPIB_ADDRESS);
          writeSync ("ADDRESS " + Integer.toString (gpibAddress) + ";");
          if (instrumentSettings != null)
            newInstrumentSettings = instrumentSettings.withGpibAddress ((byte) gpibAddress);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_AUTORANGE:
        {
          // ARANGE
          final boolean autoRange =
            (boolean) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_AUTORANGE);
          writeSync ("ARANGE " + (autoRange ? "1" : "0") + ";");
          if (instrumentSettings != null)
            newInstrumentSettings = instrumentSettings.withAutoRange (autoRange);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_AUXILIARY_ERROR:
        {
          // AUXERR?
          final String queryReturn = new String (writeAndReadEOISync ("AUXERR?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final short auxiliaryError;
          try
          {
            auxiliaryError = Short.parseShort (queryReturn);
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, auxiliaryError);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_AUTOZERO_MODE:
        {
          // AZERO
          final HP3457A_GPIB_Settings.AutoZeroMode autoZeroMode =
            (HP3457A_GPIB_Settings.AutoZeroMode) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_AUTOZERO_MODE);
          switch (autoZeroMode)
          {
            case FunctionOrRangeChanges: writeSync ("AZERO 0;");
            case Always:                 writeSync ("AZERO 1;");
          }
          if (instrumentSettings != null)
            newInstrumentSettings = instrumentSettings.withAutoZeroMode (processCommand_getAutoZeroMode ());
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_AUTOZERO_MODE:
        {
          // AZERO?
          final HP3457A_GPIB_Settings.AutoZeroMode autoZeroMode = processCommand_getAutoZeroMode ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, autoZeroMode);
          if (instrumentSettings != null)
            newInstrumentSettings = instrumentSettings.withAutoZeroMode (autoZeroMode);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_BEEP_MODE:
        {
          // BEEP
          final boolean beepEnabled =
            (boolean) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_BEEP_MODE);
          writeSync ("BEEP " + (beepEnabled ? "1" : "0") + ";");
          if (instrumentSettings != null)
            newInstrumentSettings = instrumentSettings.withBeepEnabled (beepEnabled);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_BEEP:
        {
          // BEEP
          writeSync ("BEEP 2;");
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_CALIBRATE:
        {
          // CAL
          // XXX - MUST CHECK SERVICE MANUAL...
          throw new UnsupportedOperationException ();
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          // XXX
          // break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_CALL_SUBPROGRAM:
        {
          // CALL
          final int subProgram =
            (int) instrumentCommand.get (HP3457A_InstrumentCommand.ICARG_HP3457A_CALL_SUBPROGRAM);
          writeSync ("CALL " + Integer.toString (subProgram) + ";");
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_CALIBRATION_NUMBER:
        {
          // CALNUM?
          final int calibrationNumber = processCommand_getCalibrationNumber ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, calibrationNumber);
          if (instrumentSettings != null && instrumentSettings.getCalibrationNumber () != calibrationNumber)
            newInstrumentSettings = instrumentSettings.withCalibrationNumber (calibrationNumber);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_CHANNEL:
        {
          // CHAN
          final HP3457A_GPIB_Settings.InputChannel inputChannel =
            (HP3457A_GPIB_Settings.InputChannel) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_CHANNEL);
          final int newChannelNumber = processCommand_setAndGetInt ("CHAN", inputChannel.getCode ());
          final HP3457A_GPIB_Settings.InputChannel newInputChannel =
            HP3457A_GPIB_Settings.InputChannel.fromCode (newChannelNumber);
          if (instrumentSettings != null && instrumentSettings.getInputChannel () != newInputChannel)
            newInstrumentSettings = instrumentSettings.withInputChannel (newInputChannel);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_CHANNEL:
        {
          // CHAN?
          final int channelNumber = processCommand_getInt ("CHAN");
          final HP3457A_GPIB_Settings.InputChannel inputChannel = HP3457A_GPIB_Settings.InputChannel.fromCode (channelNumber);
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, inputChannel);
          if (instrumentSettings != null && instrumentSettings.getInputChannel () != inputChannel)
            newInstrumentSettings = instrumentSettings.withInputChannel (inputChannel);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_CLOSE_ACTUATOR_CHANNEL:
        {
          // CLOSE
          final int actuatorChannel =
            (int) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_CLOSE_ACTUATOR_CHANNEL);
          if (actuatorChannel != 8 && actuatorChannel != 9)
            throw new IllegalArgumentException ();
          writeSync ("CLOSE " + Integer.toString (actuatorChannel) + ";");
          if (instrumentSettings != null)
          {
            switch (actuatorChannel)
            {
              case 8:
                newInstrumentSettings = instrumentSettings.withOpenChannel8 (false);
                break;
              case 9:
                newInstrumentSettings = instrumentSettings.withOpenChannel9 (false);
                break;
              default:
                throw new RuntimeException ();
            }
          }
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_CARD_RESET:
        {
          // CRESET
          writeSync ("CRESET;");
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_CLEAR_STATUS_BYTE:
        {
          // CSB
          writeSync ("CSB;"); // XXX Why not writeASync??
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_DC_CURRENT:
        {
          // DCI
          final Double range_A =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_DC_CURRENT_RANGE);
          final Double resolution_percent =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_DC_CURRENT_RESOLUTION);
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          writeSync ("DCI" +
            (range_A != null ? ("," + Double.toString (range_A)) : "") +
            (resolution_percent != null ? ("," + Double.toString (resolution_percent)) : "")  +
            ";");
          newInstrumentSettings = instrumentSettings.withMeasurementMode (MeasurementMode.DC_CURRENT);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_DC_VOLTS:
        {
          // DCV
          final Double range_V =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_DC_VOLTS_RANGE);
          final Double resolution_percent =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_DC_VOLTS_RESOLUTION);
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          writeSync ("DCV" +
            (range_V != null ? ("," + Double.toString (range_V)) : "") +
            (resolution_percent != null ? ("," + Double.toString (resolution_percent)) : "")  +
            ";");
          newInstrumentSettings = instrumentSettings.withMeasurementMode (MeasurementMode.DC_VOLTAGE);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_DELAY:
        {
          // DELAY
          final double newDelay_s = processCommand_setAndGetDouble (
            instrumentCommand,
            HP3457A_InstrumentCommand.ICARG_HP3457A_SET_DELAY,
            "DELAY");
          if (instrumentSettings != null && instrumentSettings.getDelay_s () != newDelay_s)
            newInstrumentSettings = instrumentSettings.withDelay (newDelay_s);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_DELAY:
        {
          // DELAY?
          final double newDelay_s = processCommand_getDouble (instrumentCommand, "DELAY");
          if (instrumentSettings != null && instrumentSettings.getDelay_s () != newDelay_s)
            newInstrumentSettings = instrumentSettings.withDelay (newDelay_s);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_DIAGNOSTIC:
        {
          // DIAGNOSTIC
          // XXX - MUST CHECK SERVICE MANUAL...
          throw new UnsupportedOperationException ();
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          // XXX
          // break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_DISPLAY:
        {
          // DISP
          final HP3457A_GPIB_Settings.DisplayControl displayControl =
            (HP3457A_GPIB_Settings.DisplayControl) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_DISPLAY_CONTROL);
          final String message =
            (String) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_DISPLAY_MESSAGE);
          // XXX End-of-message characters need to be escaped!
          final String displayControlString;
          if (displayControl != null)
            switch (displayControl)
            {
              case Off:     displayControlString = "0"; break;
              case On:      displayControlString = "1"; break;
              case Message: displayControlString = "2"; break;
              default:
                throw new IllegalArgumentException ();
            }
          else
            displayControlString = null;
          writeSync ("DISP" +
            (displayControlString != null ? ("," + displayControlString) : "") +
            (message != null ? ("," + message) : "")  +
            ";");
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_ERROR_MASK:
        {
          // EMASK
          final Short errorMask =
            (Short) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_ERROR_MASK);
          writeSync ("EMASK " +
            (errorMask != null ? ("," + Integer.toString (errorMask & 0xffff)) : "") +
            ";");
          final short newErrorMask = (errorMask != null ? errorMask : (short) 2047);
          if (instrumentSettings != null && instrumentSettings.getErrorMask () != newErrorMask)
            newInstrumentSettings = instrumentSettings.withErrorMask (newErrorMask);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_EOI:
        {
          // END
          final boolean eoi =
            (boolean) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_EOI);
          // We must use "2" for true below (would expect "1")? Conform HP-3457A Operating Manual...
          writeSync ("END " + (eoi ? "2" : "0") + ";");
          if (instrumentSettings != null && instrumentSettings.isEoi () != eoi)
            newInstrumentSettings = instrumentSettings.withEoi (eoi);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_ERROR:
        {
          // ERR?
          final String queryReturn = new String (writeAndReadEOISync ("ERR?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final short errorShort;
          try
          {
            errorShort = (short) Math.round (Double.parseDouble (queryReturn));
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, errorShort);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_FUNCTION_FAST:
        {
          // F10-F58
          final byte functionCode =
            (byte) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FUNCTION_FAST);
          if (functionCode < 10 || functionCode > 58)
            throw new IllegalArgumentException ();
          writeSync ("F" + Byte.toString (functionCode) + ";");
          // XXX Update newInstrumentSettings
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_FIXED_IMPEDANCE:
        {
          // FIXEDZ
          final boolean newFixedZ = processCommand_setAndGetBoolean (
            instrumentCommand,
            HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FIXED_IMPEDANCE,
            "FIXEDZ");
          if (instrumentSettings != null&& instrumentSettings.isFixedImpedance () != newFixedZ)
            newInstrumentSettings = instrumentSettings.withFixedImpedance (newFixedZ);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_FIXED_IMPEDANCE:
        {
          // FIXEDZ?
          final boolean newFixedZ = processCommand_getBoolean (instrumentCommand, "FIXEDZ");
          if (instrumentSettings != null && instrumentSettings.isFixedImpedance () != newFixedZ)
            newInstrumentSettings = instrumentSettings.withFixedImpedance (newFixedZ);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_FREQUENCY:
        {
          // FREQ
          final Double maxInput_V_or_A = 
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_FREQUENCY_AMPLITUDE_RANGE);
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          writeSync ("FREQ" +
            (maxInput_V_or_A != null ? ("," + Double.toString (maxInput_V_or_A)) : "") +
            ";");
          newInstrumentSettings = instrumentSettings.withMeasurementMode (MeasurementMode.FREQUENCY);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_FREQUENCY_OR_PERIOD_SOURCE:
        {
          // FSOURCE
          final MeasurementMode source = 
            (MeasurementMode) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FREQUENCY_OR_PERIOD_SOURCE);
          if (source == null)
            writeSync ("FSOURCE;");
          else
            switch (source)
            {
              case AC_VOLTAGE:    writeSync ("FSOURCE 2;"); break;
              case AC_DC_VOLTAGE: writeSync ("FSOURCE 3;"); break;
              case AC_CURRENT:    writeSync ("FSOURCE 7;"); break;
              case AC_DC_CURRENT: writeSync ("FSOURCE 8;"); break;
              default:
                throw new IllegalArgumentException ();
            }
          if (instrumentSettings != null)
            newInstrumentSettings = instrumentSettings.withFrequencyPeriodInputSource (source);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_FUNCTION:
        {
          // FUNC
          final MeasurementMode function = 
            (MeasurementMode) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FUNCTION_FUNCTION);
          final Double maxInput_V_or_A_or_Ohm =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FUNCTION_RANGE);
          final Double resolution_percent =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_FUNCTION_RESOLUTION);
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          final int modeCode;
          switch (function)
          {
            case DC_VOLTAGE:    modeCode =  1; break;
            case AC_VOLTAGE:    modeCode =  2; break;
            case AC_DC_VOLTAGE: modeCode =  3; break;
            case RESISTANCE_2W: modeCode =  4; break;
            case RESISTANCE_4W: modeCode =  5; break;
            case DC_CURRENT:    modeCode =  6; break;
            case AC_CURRENT:    modeCode =  7; break;
            case AC_DC_CURRENT: modeCode =  8; break;
            case FREQUENCY:     modeCode =  9; break;
            case PERIOD:        modeCode = 10; break;
            default:
              throw new UnsupportedOperationException ();
          }
          writeSync ("FUNC" +
            (function != null ? ("," + Integer.toString (modeCode)) : "") +
            (maxInput_V_or_A_or_Ohm != null ? ("," + Double.toString (maxInput_V_or_A_or_Ohm)) : "") +
            (resolution_percent != null ? ("," + Double.toString (resolution_percent)) : "")  +
            ";");
          // XXX What about range/resolution?
          newInstrumentSettings = instrumentSettings.withMeasurementMode (function);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_ID:
        {
          // ID?
          final String queryReturn = new String (writeAndReadEOISync ("ID?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, queryReturn);
          if (instrumentSettings != null && ! queryReturn.equals (instrumentSettings.getId ()))
            newInstrumentSettings = instrumentSettings.withId (queryReturn);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_GPIB_INPUT_BUFFERING:
        {
          // INBUF
          final boolean newGpibInputBuffering = processCommand_setBoolean (instrumentCommand,
            HP3457A_InstrumentCommand.ICARG_HP3457A_SET_GPIB_INPUT_BUFFERING,
            "INBUF");
          if (instrumentSettings != null && instrumentSettings.isGpibInputBuffering () != newGpibInputBuffering)
            newInstrumentSettings = instrumentSettings.withGpibInputBuffering (newGpibInputBuffering);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_INTEGER_SCALE_FACTOR:
        {
          // ISCALE?
          final double integerScale = processCommand_getIntegerScale ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, integerScale);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_LINE_FREQUENCY_REFERENCE:
        {
          // LFREQ
          final double newLineFrequencyReference_Hz = processCommand_setAndGetDouble (
            instrumentCommand,
            HP3457A_InstrumentCommand.ICARG_HP3457A_SET_LINE_FREQUENCY_REFERENCE,
            "LFREQ");
          if (instrumentSettings != null && instrumentSettings.getLineFrequencyReference_Hz () != newLineFrequencyReference_Hz)
            newInstrumentSettings = instrumentSettings.withLineFrequencyReference_Hz (newLineFrequencyReference_Hz);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_LINE_FREQUENCY_REFERENCE:
        {
          // LFREQ?
          final double lineFrequencyReference_Hz = processCommand_getDouble (instrumentCommand, "LFREQ");
          if (instrumentSettings != null && instrumentSettings.getLineFrequencyReference_Hz () != lineFrequencyReference_Hz)
            newInstrumentSettings = instrumentSettings.withLineFrequencyReference_Hz (lineFrequencyReference_Hz);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_LINE_FREQUENCY:
        {
          // LINE?
          final double lineFrequency_Hz = processCommand_getDouble (instrumentCommand, "LINE");
          if (instrumentSettings != null && instrumentSettings.getLineFrequency_Hz () != lineFrequency_Hz)
            newInstrumentSettings = instrumentSettings.withLineFrequency_Hz (lineFrequency_Hz);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_LOCKOUT:
        {
          // LOCK
          final boolean lockout =
            (boolean) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_LOCKOUT);
          writeSync ("LOCK " + (lockout ? "1" : "0") + ";");
          if (instrumentSettings != null && instrumentSettings.isLocked () != lockout)
            newInstrumentSettings = instrumentSettings.withLocked (lockout);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_MATH_OPERATION:
        {
          // MATH
          final HP3457A_GPIB_Settings.MathOperation operationA = 
            (HP3457A_GPIB_Settings.MathOperation) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_MATH_OPERATION_A);
          final HP3457A_GPIB_Settings.MathOperation operationB =
            (HP3457A_GPIB_Settings.MathOperation) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_MATH_OPERATION_B);
          writeSync ("MATH" +
            (operationA != null ? ("," + Integer.toString (operationA.getCode ())) : "") +
            (operationB != null ? ("," + Integer.toString (operationB.getCode ())) : "") +
            ";");
          // XXX Update newInstrumentSettings
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_MATH_OPERATION:
        {
          // MATH?
          final String queryReturn = new String (writeAndReadEOISync ("MATH?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final String[] components = queryReturn.split (",");
          if (components == null || components.length != 2)
            throw new IOException ();
          final HP3457A_GPIB_Settings.MathOperation operationA;
          final HP3457A_GPIB_Settings.MathOperation operationB;
          try
          {
            operationA = HP3457A_GPIB_Settings.MathOperation.fromCode (Integer.parseInt (components[0]));
            operationB = HP3457A_GPIB_Settings.MathOperation.fromCode (Integer.parseInt (components[1]));
          }
          catch (IllegalArgumentException iae)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY,
            new HP3457A_GPIB_Settings.MathOperationDefinition (operationA, operationB));
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_NUMBER_OF_STORED_READINGS:
        {
          // MCOUNT?
          final String queryReturn = new String (writeAndReadEOISync ("MCOUNT?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final int count;
          try
          {
            count = (int) Math.round (Double.parseDouble (queryReturn));
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, count);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_READING_MEMORY_CONTROL:
        {
          // MEM
          final HP3457A_GPIB_Settings.ReadingMemoryMode readingMemoryMode =
            (HP3457A_GPIB_Settings.ReadingMemoryMode) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_READING_MEMORY_CONTROL);
          writeSync ("MEM" +
            (readingMemoryMode != null ? ("," + Integer.toString (readingMemoryMode.getCode ()))  : "") +
            ";");
          if (instrumentSettings != null)
            newInstrumentSettings = instrumentSettings.withReadingMemoryMode (readingMemoryMode);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_FORMAT_READING_MEMORY:
        {
          // MFORMAT
          final HP3457A_GPIB_Settings.ReadingFormat format =
            (HP3457A_GPIB_Settings.ReadingFormat) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_FORMAT_READING_MEMORY);
          writeSync ("MFORMAT" +
            (format != null ? ("," + Integer.toString (format.getCode ()))  : "") +
            ";");
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_MEMORY_SIZES:
        {
          // MSIZE
          final Integer readingsBytes =
            (Integer) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_MEMORY_SIZES_READINGS);
          final Integer subProgramsBytes =
            (Integer) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_MEMORY_SIZES_SUBPROGRAMS);
          writeSync ("MSIZE" +
            (readingsBytes != null ? ("," + Integer.toString (readingsBytes)) : "") +
            (subProgramsBytes != null ? ("," + Integer.toString (subProgramsBytes)) : "") +
            ";");
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_MEMORY_SIZES:
        {
          // MSIZE?
          final String queryReturn = new String (writeAndReadEOISync ("MSIZE?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final String[] components = queryReturn.split (",");
          if (components == null || components.length != 2)
            throw new IOException ();
          final int readings_bytes;
          final int subprograms_bytes;
          try
          {
            readings_bytes = Integer.parseInt (components[0]);
            subprograms_bytes = Integer.parseInt (components[1]);
          }
          catch (IllegalArgumentException iae)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY,
            new HP3457A_GPIB_Settings.MemorySizesDefinition (readings_bytes, subprograms_bytes));
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_NUMBER_OF_DIGITS:
        {
          // NDIG
          final Integer numberOfDigits =
            (Integer) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_NUMBER_OF_DIGITS);
          writeSync ("NDIG" +
            (numberOfDigits != null ? ("," + Integer.toString (numberOfDigits)) : "") +
            ";");
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_ADC_NUMBER_OF_PLCS:
        {
          // NPLC
          final Double numberOfPLCs =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_ADC_NUMBER_OF_PLCS);
          writeSync ("NPLC" +
            (numberOfPLCs != null ? ("," + Double.toString (numberOfPLCs)) : "") +
            ";");
          final double newNumberOfPLCs = numberOfPLCs != null ? numberOfPLCs : 0.005 /* default */;
          if (instrumentSettings != null && instrumentSettings.getNumberOfPowerLineCycles () != newNumberOfPLCs)
            newInstrumentSettings = instrumentSettings.withNumberOfPowerLineCycles (newNumberOfPLCs);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_ADC_NUMBER_OF_PLCS:
        {
          // NPLC?
          final String queryReturn = new String (writeAndReadEOISync ("NPLC?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final double numberOfPLCs;
          try
          {
            numberOfPLCs = Double.parseDouble (queryReturn);
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, numberOfPLCs);
          if (instrumentSettings != null && instrumentSettings.getNumberOfPowerLineCycles () != numberOfPLCs)
            newInstrumentSettings = instrumentSettings.withNumberOfPowerLineCycles (numberOfPLCs);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_NUMBER_OF_READINGS:
        {
          // NRDGS
          final Integer count =
            (Integer) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_NUMBER_OF_READINGS_COUNT);
          final HP3457A_GPIB_Settings.TriggerEvent sampleEvent =
            (HP3457A_GPIB_Settings.TriggerEvent) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_NUMBER_OF_READINGS_EVENT);
          writeSync ("NRDGS" +
            (count != null ? ("," + Integer.toString (count)) : "-1") +
            (sampleEvent != null ? ("," + Integer.toString (sampleEvent.getCode ())) : "-1") +
            ";");
          if (instrumentSettings != null)
            newInstrumentSettings = processCommand_updateTriggerSettings (instrumentSettings);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_NUMBER_OF_READINGS:
        {
          // NRDGS?
          final String queryReturn = new String (writeAndReadEOISync ("NRDGS?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final String[] components = queryReturn.split (",");
          if (components == null || components.length != 2)
            throw new IOException ();
          final int count;
          final HP3457A_GPIB_Settings.TriggerEvent sampleEvent;
          try
          {
            count = Integer.parseInt (components[0]);
            sampleEvent = HP3457A_GPIB_Settings.TriggerEvent.fromCode (Integer.parseInt (components[1]));
          }
          catch (IllegalArgumentException iae)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY,
            new HP3457A_GPIB_Settings.TriggerDefinition (count, sampleEvent));
          if (instrumentSettings != null)
          {
            newInstrumentSettings = processCommand_updateTriggerSettings (instrumentSettings);
            if (instrumentSettings.getNumberOfReadings () != count)
              newInstrumentSettings = newInstrumentSettings.withNumberOfReadings (count);
          }
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_OFFSET_COMPENSATION:
        {
          // OCOMP
          final boolean newOffsetCompensation = processCommand_setAndGetBoolean (
            instrumentCommand,
            HP3457A_InstrumentCommand.ICARG_HP3457A_SET_OFFSET_COMPENSATION,
            "OCOMP");
          if (instrumentSettings != null&& instrumentSettings.isOffsetCompensation ()!= newOffsetCompensation)
            newInstrumentSettings = instrumentSettings.withOffsetCompensation (newOffsetCompensation);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_OFFSET_COMPENSATION:
        {
          // OCOMP?
          final boolean newOffsetCompensation = processCommand_getBoolean (instrumentCommand, "OCOMP");
          if (instrumentSettings != null && instrumentSettings.isOffsetCompensation ()!= newOffsetCompensation)
            newInstrumentSettings = instrumentSettings.withOffsetCompensation (newOffsetCompensation);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_OUTPUT_FORMAT:
        {
          // OFORMAT
          final HP3457A_GPIB_Settings.ReadingFormat readingFormat =
            (HP3457A_GPIB_Settings.ReadingFormat) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_OUTPUT_FORMAT);
          writeSync ("OFORMAT " + Integer.toString (readingFormat.getCode ()) + ";");
          if (instrumentSettings != null && instrumentSettings.getReadingFormat ()!= readingFormat)
          {
            // Clear the reading memory (and set SREAL memory format).
            writeSync ("MFORMAT 4;");
            newInstrumentSettings = instrumentSettings.withReadingFormat (readingFormat);
          }
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_OHMS:
        {
          // OHM
          final Double range_ohms =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_OHMS_RANGE);
          final Double resolution_percent =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_OHMS_RESOLUTION);
          writeSync ("OHM" +
            (range_ohms != null ? ("," + Double.toString (range_ohms)) : "") +
            (resolution_percent != null ? ("," + Double.toString (resolution_percent)) : "")  +
            ";");
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_4WIRE_OHMS:
        {
          // OHMF
          final Double range_ohms =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_4WIRE_OHMS_RANGE);
          final Double resolution_percent =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_4WIRE_OHMS_RESOLUTION);
          writeSync ("OHMF" +
            (range_ohms != null ? ("," + Double.toString (range_ohms)) : "") +
            (resolution_percent != null ? ("," + Double.toString (resolution_percent)) : "")  +
            ";");
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_OPEN_ACTUATOR_CHANNEL:
        {
          // OPEN
          final int channel =
            (int) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_OPEN_ACTUATOR_CHANNEL_CHANNEL);
          final Boolean delay =
            (Boolean) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_OPEN_ACTUATOR_CHANNEL_CONTROL);
          if (channel != 8 && channel != 9)
            throw new IllegalArgumentException ();
          writeSync ("OPEN " + Integer.toString (channel) + (delay != null ? (delay ? ",1" : ",0") : "") + ";");
          if (instrumentSettings != null)
          {
            switch (channel)
            {
              case 8:
                if (delay != null)
                  newInstrumentSettings = instrumentSettings.withOpenChannel8 (true).withSwitchDelayChannel8 (delay);
                else
                  newInstrumentSettings = instrumentSettings.withOpenChannel8 (true).withSwitchDelayChannel8 (true);
                break;
              case 9:
                if (delay != null)
                  newInstrumentSettings = instrumentSettings.withOpenChannel9 (true).withSwitchDelayChannel9 (delay);
                else
                  newInstrumentSettings = instrumentSettings.withOpenChannel9 (true).withSwitchDelayChannel9 (true);
                break;
              default:
                throw new RuntimeException ();
            }
          }
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_INSTALLED_OPTION:
        {
          // OPT?
          final HP3457A_GPIB_Settings.InstalledOption installedOption = processCommand_getInstalledOption ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, installedOption);
          if (instrumentSettings == null)
            return;
          newInstrumentSettings = instrumentSettings.withInstalledOption (installedOption);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_PAUSE_SUBPROGRAM:
        {
          // PAUSE
          writeSync ("PAUSE;");
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_PERIOD:
        {
          // PER
          final Double maxInput_V_or_A = 
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_PERIOD_AMPLITUDE_RANGE);
          if (instrumentSettings == null)
            throw new UnsupportedOperationException ();
          writeSync ("PER" +
            (maxInput_V_or_A != null ? ("," + Double.toString (maxInput_V_or_A)) : "") +
            ";");
          newInstrumentSettings = instrumentSettings.withMeasurementMode (MeasurementMode.PERIOD);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_PRESET_STATE:
        {
          // PRESET
          writeSync ("PRESET;");
          newInstrumentSettings = HP3457A_GPIB_Settings.fromPreset ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_RANGE:
        {
          // R
          // RANGE
          final Double range =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_RANGE_RANGE);
          final Double resolution_percent =
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_RANGE_RESOLUTION);
          writeSync ("R" +
            (range != null ? ("," + Double.toString (range)) : "") +
            (resolution_percent != null ? ("," + Double.toString (resolution_percent)) : "")  +
            ";");
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_RANGE:
        {
          // RANGE?
          final String queryReturn = new String (writeAndReadEOISync ("RANGE?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final double range;
          try
          {
            range = Double.parseDouble (queryReturn);
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, range);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_RESET:
        {
          // RESET
          writeSync ("RESET;");
          newInstrumentSettings = HP3457A_GPIB_Settings.fromReset ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_REVISION:
        {
          // REV?
          // XXX - MUST CHECK SERVICE MANUAL...
          throw new UnsupportedOperationException ();
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          // XXX
          // break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_RECALL_MATH:
        {
          // RMATH
          final HP3457A_GPIB_Settings.MathRegister register = 
            (HP3457A_GPIB_Settings.MathRegister) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_RECALL_MATH);
          final String queryReturn = new String (writeAndReadEOISync (
            "RMATH"
              + (register != null ? ("," + register.getCode ()) : "")
              + ";"),
            Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final double value;
          try
          {
            value = Double.parseDouble (queryReturn);
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, value);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_RECALL_MEMORY:
        {
          // RMEM
          final Integer first =
            (Integer) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_RECALL_MEMORY_FIRST);
          final Integer count =
            (Integer) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_RECALL_MEMORY_COUNT);
          final Integer record =
            (Integer) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_RECALL_MEMORY_RECORD);
          final String queryReturn = new String (writeAndReadEOISync (
            "RMEM" +
            (first  != null ? ("," + Integer.toString (first))  : "") +
            (count  != null ? ("," + Integer.toString (count))  : "") +
            (record != null ? ("," + Integer.toString (record)) : "") +
            ";"));
          final String[] components = queryReturn.split (",");
          if (components == null)
            throw new IOException ();
          final double[] values = new double[components.length];
          try
          {
            for (int i = 0; i < values.length; i++)
              values[i] = Double.parseDouble (components[i]);
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, values);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_SERVICE_REQUEST_MASK:
        {
          // RQS
          final Byte serviceRequestMask =
            (Byte) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_SERVICE_REQUEST_MASK);
          writeSync ("RQS" +
            (serviceRequestMask != null ? ("," + Integer.toString (serviceRequestMask & 0xff)) : "") +
            ";");
          final byte newServiceRequestMask = (serviceRequestMask != null ? serviceRequestMask : (byte) 0);
          if (instrumentSettings != null && instrumentSettings.getServiceRequestMask () != newServiceRequestMask)
            newInstrumentSettings = instrumentSettings.withServiceRequestMask (newServiceRequestMask);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_RECALL_STATE:
        {
          // RSTATE
          final int slot =
            (int) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_RECALL_STATE);
          writeSync ("RSTATE" + "," + Integer.toString (slot) + ";");
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SCAN_ADVANCE:
        {
          // SADV
          final HP3457A_GPIB_Settings.ScanAdvanceMode mode =
            (HP3457A_GPIB_Settings.ScanAdvanceMode) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SCAN_ADVANCE);
          writeSync ("SADV" +
            (mode != null ? ("," + mode.getCode ()) : "") +
            ";");          
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_CLEAR_ALL_SUBPROGRAMS:
        {
          // SCRATCH
          writeSync ("SCRATCH;");
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SECURE:
        {
          // SECURE
          // XXX - MUST CHECK SERVICE MANUAL...
          throw new UnsupportedOperationException ();
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          // XXX
          // break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_SCAN_LIST:
        {
          // SLIST
          final int[] scanList = 
            (int[]) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_SCAN_LIST);
          if (scanList == null)
            throw new IllegalArgumentException ();
          final StringBuffer sb = new StringBuffer ();
          sb.append ("SLIST");
          for (int i = 0; i < scanList.length; i++)
            sb.append (",").append (Integer.toString (scanList[i]));
          sb.append (";");
          writeSync (sb.toString ());          
          // XXX?
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_SCAN_LIST_SIZE:
        {
          // SLIST?
          final String queryReturn = new String (writeAndReadEOISync ("SLIST?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final int scanListSize;
          try
          {
            scanListSize = Integer.parseInt (queryReturn);
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, scanListSize);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_STORE_MATH:
        {
          // SMATH
          final HP3457A_GPIB_Settings.MathRegister register =
            (HP3457A_GPIB_Settings.MathRegister) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_STORE_MATH_REGISTER);
          final Double number = 
            (Double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_STORE_MATH_NUMBER);
          writeSync ("SMATH" +
            (register != null ? ("," + Integer.toString (register.getCode ())) : "") +
            (number != null ? ("," + Double.toString (number)) : "") +
            ";");
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_FIRE_SERVICE_REQUEST:
        {
          // SRQ
          writeSync ("SRQ;");
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_STORE_STATE:
        {
          // SSTATE
          final int slot = 
            (int) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_STORE_STATE);
          if (slot < 0 || slot > 30) // XXX These should be constants. Perhaps a Slot definition class?
            throw new IllegalArgumentException ();
          writeSync ("SSTATE " + Integer.toString (slot) + ";");
          // XXX Update newInstrumentSettings
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_STATUS_BYTE:
        {
          // STB?
          final String queryReturn = new String (writeAndReadEOISync ("STB?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          final byte statusByte;
          try
          {
            final int statusInt = Integer.parseInt (queryReturn);
            if (statusInt < 0 || statusInt > 255)
              throw new IOException ();
            statusByte = (byte) statusInt; 
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, statusByte);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_STORE_SUBPROGRAM:
        {
          // SUB
          final int slot =
            (int) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_STORE_SUBPROGRAM);
          if (slot < 0 || slot > 19) // XXX These should be constants. Perhaps a Slot definition class?
            throw new IllegalArgumentException ();
          writeSync ("SUB " + Integer.toString (slot) + ";");
          // XXX Update newInstrumentSettings
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SUBPROGRAM_END:
        {
          // SUBEND
          writeSync ("SUBEND;");
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_TRIGGER_ARM:
        {
          // TARM
          final HP3457A_GPIB_Settings.TriggerEvent event =
            (HP3457A_GPIB_Settings.TriggerEvent) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_TRIGGER_ARM_EVENT);
          final Integer nrArms =
            (Integer) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_TRIGGER_ARM_NR_ARMS);
          writeSync ("TARM" +
            (event != null ? ("," + Integer.toString (event.getCode ())) : "") +
            (nrArms != null ? ("," + Integer.toString (nrArms)) : "") +
            ";");
          if (instrumentSettings != null)
          {
            final int newNumberOfTriggerArms =
              (event == HP3457A_GPIB_Settings.TriggerEvent.SGL && nrArms != null) ? nrArms : 0;
            newInstrumentSettings = processCommand_updateTriggerSettings (instrumentSettings)
                                      .withNumberOfTriggerArms (newNumberOfTriggerArms);
          }
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_TRIGGER_ARM:
        {
          // TARM?
          final HP3457A_GPIB_Settings.TriggerEvent triggerArmEvent = processCommand_getTriggerArmEvent ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, triggerArmEvent);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_TRIGGER_BUFFERING:
        {
          // TBUFF
          final boolean newTriggerBuffering = processCommand_setBoolean (
            instrumentCommand,
            HP3457A_InstrumentCommand.ICARG_HP3457A_SET_TRIGGER_BUFFERING,
            "TBUFF");
          if (instrumentSettings != null && instrumentSettings.isTriggerBuffering ()!= newTriggerBuffering)
            newInstrumentSettings = instrumentSettings.withTriggerBuffering (newTriggerBuffering);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_MEASUREMENT_TERMINALS:
        {
          // TERM
          final HP3457A_GPIB_Settings.MeasurementTerminals terminals =
            (HP3457A_GPIB_Settings.MeasurementTerminals) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_MEASUREMENT_TERMINALS);
          writeSync ("TERM" +
            (terminals != null ? ("," + Integer.toString (terminals.getCode ())) : "") +
            ";");
          if (instrumentSettings != null)
            newInstrumentSettings = instrumentSettings.withMeasurementTerminals (processCommand_getMeasurementTerminals ());
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_MEASUREMENT_TERMINALS:
        {
          // TERM?
          final HP3457A_GPIB_Settings.MeasurementTerminals terminals = processCommand_getMeasurementTerminals ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, terminals);
          if (instrumentSettings != null && instrumentSettings.getMeasurementTerminals () != terminals)
            newInstrumentSettings = instrumentSettings.withMeasurementTerminals (terminals);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_INTERNAL_TEST:
        {
          // TEST
          writeSync ("TEST;");
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_TIME_INTERVAL_FOR_NRDGS:
        {
          // TIMER
          final double newTimer_s = processCommand_setAndGetDouble (
            instrumentCommand,
            HP3457A_InstrumentCommand.ICARG_HP3457A_SET_TIME_INTERVAL_FOR_NRDGS,
            "TIMER");
          if (instrumentSettings != null && instrumentSettings.getTimer_s () != newTimer_s)
            newInstrumentSettings = instrumentSettings.withTimer (newTimer_s);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_TIME_INTERVAL_FOR_NRDGS:
        {
          // TIMER?
          final double newTimer_s = processCommand_getDouble (instrumentCommand, "TIMER");
          if (instrumentSettings != null && instrumentSettings.getTimer_s () != newTimer_s)
            newInstrumentSettings = instrumentSettings.withTimer (newTimer_s);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_PLAY_TONE:
        {
          // TONE
          final double frequency_Hz =
            (double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_PLAY_TONE_FREQUENCY);
          final double duration_s =
            (double) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_PLAY_TONE_DURATION);
          writeSync ("TONE " + Double.toString (frequency_Hz) + "," + Double.toString (duration_s) + ";");
          // Does not update (newInstrument)Settings!
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_SET_TRIGGER_EVENT:
        {
          // TRIG
          final HP3457A_GPIB_Settings.TriggerEvent event =
            (HP3457A_GPIB_Settings.TriggerEvent) instrumentCommand.get (
              HP3457A_InstrumentCommand.ICARG_HP3457A_SET_TRIGGER_EVENT);
          writeSync ("TRIG" +
            (event != null ? ("," + Integer.toString (event.getCode ())) : "") +
            ";");
          if (instrumentSettings != null)
            newInstrumentSettings = processCommand_updateTriggerSettings (instrumentSettings);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_GET_TRIGGER_EVENT:
        {
          // TRIG?
          final HP3457A_GPIB_Settings.TriggerEvent triggerEvent = processCommand_getTriggerEvent ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, triggerEvent);
          break;
        }
        case HP3457A_InstrumentCommand.IC_HP3457A_TRIGGER:
        {
          // ?
          writeSync ("?;");
          break;
        }
        default:
        {
          throw new UnsupportedOperationException ();
        }
      }
    }
    finally
    {
      this.operationSemaphore.release ();
    }
    if (newInstrumentSettings != null)
    {
      settingsReadFromInstrument (newInstrumentSettings);
      this.operationSemaphore.acquire ();
      final InstrumentStatus newInstrumentStatus;
      try
      {
        final byte serialPollStatusByte = serialPollSync ();
        newInstrumentStatus =
          HP3457A_GPIB_Status.fromSerialPollStatusByte (serialPollStatusByte);
      }
      finally
      {
        this.operationSemaphore.release ();
      }
      statusReadFromInstrument (newInstrumentStatus);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
