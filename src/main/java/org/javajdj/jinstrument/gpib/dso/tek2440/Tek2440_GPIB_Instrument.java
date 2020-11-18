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
package org.javajdj.jinstrument.gpib.dso.tek2440;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultInstrumentCommand;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.gpib.dso.AbstractGpibDigitalStorageOscilloscope;
import org.javajdj.jinstrument.InstrumentChannel;
import org.javajdj.jinstrument.InstrumentListener;


/** Implementation of {@link Instrument} and {@link DigitalStorageOscilloscope} for the Tektronix-2440.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class Tek2440_GPIB_Instrument
  extends AbstractGpibDigitalStorageOscilloscope
  implements DigitalStorageOscilloscope
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (Tek2440_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public Tek2440_GPIB_Instrument (final GpibDevice device)
  {
    super ("Tek-2440", device, null, null,
      true,   // Initialization
      true,   // Status
      true,   // Setings
      true,   // Command Processor
      true,   // Acquisition
      false,  // Housekeeping
      false); // Service Request Polling
    setStatusCollectorPeriod_s (5);
    setSettingsCollectorPeriod_s (3);
    setHousekeeperPeriod_s (5);
    setGpibInstrumentServiceRequestCollectorPeriod_s (4.0);
    setPollServiceRequestTimeout_ms (3000);
    setSerialPollTimeout_ms (3000);
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
      return "Tek-2440 [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final Tek2440_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new Tek2440_GPIB_Instrument ((GpibDevice) device);
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
    return Tek2440_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
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
  // DigitalStorageOscilloscope
  // NUMBER OF CHANNELS
  // CHANNELS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum Tek2440Channel
    implements InstrumentChannel
  {
    
    Channel1,
    Channel2;

    @Override
    public final String toString ()
    {
      switch (this)
      {
        case Channel1: return "1";
        case Channel2: return "2";
        default: throw new RuntimeException ();
      }
    }
        
  }
  
  public final static int NUMBER_OF_CHANNELS = 2;
  
  @Override
  public final int getNumberOfChannels ()
  {
    return NUMBER_OF_CHANNELS;
  }
  
  public final InstrumentChannel[] getChannels ()
  {
    return Tek2440Channel.values ();
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
    final byte[] idBytes = writeAndReadEOISync ("ID?\n");
    final String idString = new String (idBytes, Charset.forName ("US-ASCII"));
    setInstrumentId (idString);
    if (getCurrentInstrumentSettings () == null)
    {
      final Tek2440_GPIB_Settings settings = getSettingsFromInstrumentSync ();
      settingsReadFromInstrument (settings);
      // There is actually a critical race here because new settings are
      // processed in another thread...
      // At least wait for a non-null settings object for other methods.
      while (getCurrentInstrumentSettings () == null)
        Thread.sleep (100L);
    }
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
    final byte statusByte = serialPollSync ();
    final String errString = new String (writeAndReadEOISync ("ERR?\n"), Charset.forName ("US-ASCII"));
    // System.err.println ("Error String: " + errString);
    final InstrumentStatus instrumentStatus = new Tek2440_GPIB_Status (statusByte, errString);
    return instrumentStatus;
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
  
  protected final static boolean SETTINGS_USE_LOW_LEVEL = false;
  
  @Override
  public Tek2440_GPIB_Settings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final byte[] settingsBytes;
    final Tek2440_GPIB_Settings settings;
    if (SETTINGS_USE_LOW_LEVEL)
    {
      settingsBytes = writeAndReadEOISync ("LLS?\n");
      settings = Tek2440_GPIB_Settings.fromLlSetData (settingsBytes);
      // XXX DEBUG
      fireInstrumentDebug (
        this,
        InstrumentListener.INSTRUMENT_DEBUG_ID_SETTINGS_BYTES_1,
        null,
        null,
        null,
        settings.getBytes (),
        null,
        null);
    }
    else
    {
      settingsBytes = writeAndReadEOISync ("SET?\n");
      settings = Tek2440_GPIB_Settings.fromSetData (settingsBytes);
      // XXX DEBUG
      final byte[] debugSettingsBytes = writeAndReadEOISync ("LLS?\n");
      fireInstrumentDebug (
        this,
        InstrumentListener.INSTRUMENT_DEBUG_ID_SETTINGS_BYTES_1,
        null,
        null,
        null,
        debugSettingsBytes,
        null,
        null);
    }
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
    if (getCurrentInstrumentSettings () == null)
      initializeInstrumentSync ();
    final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) getCurrentInstrumentSettings ();
    // XXX Smells like an EnumMap<DataSource...> here...
    final boolean acqCh1 = settings.isVModeChannel1 ();
    final boolean acqCh2 = settings.isVModeChannel2 ();
    final boolean acqAdd = settings.isVModeAdd ();
    final boolean acqMult = settings.isVModeMult ();
    if (acqCh1)
    {
      final byte[] data;
      data = writeAndReadEOISync ("DAT SOU:CH1;CURVE?\n");
      final Tek2440_GPIB_Trace reading = new Tek2440_GPIB_Trace (settings, Tek2440_GPIB_Settings.DataSource.Ch1, data);
      if (! (acqCh2 || acqAdd || acqMult))
        return reading;
      else
        readingReadFromInstrument (reading);
    }
    if (acqCh2)
    {
      final byte[] data;
      data = writeAndReadEOISync ("DAT SOU:CH2;CURVE?\n");
      final Tek2440_GPIB_Trace reading = new Tek2440_GPIB_Trace (settings, Tek2440_GPIB_Settings.DataSource.Ch2, data);
      if (! (acqAdd || acqMult))
        return reading;
      else
        readingReadFromInstrument (reading);
    }
    if (acqAdd)
    {
      final byte[] data;
      data = writeAndReadEOISync ("DAT SOU:ADD;CURVE?\n");
      final Tek2440_GPIB_Trace reading = new Tek2440_GPIB_Trace (settings, Tek2440_GPIB_Settings.DataSource.Add, data);
      if (! (acqMult))
        return reading;
      else
        readingReadFromInstrument (reading);
    }
    if (acqMult)
    {
      final byte[] data;
      data = writeAndReadEOISync ("DAT SOU:MUL;CURVE?\n");
      final Tek2440_GPIB_Trace reading = new Tek2440_GPIB_Trace (settings, Tek2440_GPIB_Settings.DataSource.Mult, data);
      if (! (false))
        return reading;
      else
        readingReadFromInstrument (reading);      
    }
    return null;
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
  // DigitalStorageOscilloscope
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  // EMPTY
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Tek2440_GPIB_Instrument
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static int TEK2440_X_DIVISIONS = 10;
  
  public final static int TEK2440_Y_DIVISIONS = 8;
  
  public void setHorizontalMode (
    final Tek2440_GPIB_Settings.HorizontalMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_HORIZONTAL_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_HORIZONTAL_MODE, mode));
  }
  
  public void setChannelTimebase (
    final Tek2440Channel channel,
    final Tek2440_GPIB_Settings.SecondsPerDivision secondsPerDivision)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_TIMEBASE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_TIMEBASE_CHANNEL, channel,
      Tek2440_InstrumentCommand.ICARG_TEK2440_TIMEBASE, secondsPerDivision));
  }
  
  public void setHorizontalPosition (
    final double position)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_HORIZONTAL_POSITION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_HORIZONTAL_POSITION, position));
  }
  
  public void setHorizontalExternalExpansion (
    final Tek2440_GPIB_Settings.HorizontalExternalExpansionFactor expansion)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_HORIZONTAL_EXTERNAL_EXPANSION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_HORIZONTAL_EXTERNAL_EXPANSION, expansion));
  }
  
  public void setChannelEnable (
    final Tek2440Channel channel,
    final boolean channelEnable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_ENABLE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_ENABLE_CHANNEL, channel,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_ENABLE, channelEnable));
  }
  
  public void setAddEnable (
    final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ADD_ENABLE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ADD_ENABLE, enable));
  }
  
  public void setMultEnable (
    final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MULT_ENABLE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MULT_ENABLE, enable));
  }
  
  public void setVerticalDisplayMode (
    final Tek2440_GPIB_Settings.VModeDisplay mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_VERTICAL_DISPLAY_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_VERTICAL_DISPLAY_MODE, mode));
  }
  
  public void setVoltsPerDiv (
    final Tek2440Channel channel,
    final Tek2440_GPIB_Settings.VoltsPerDivision voltsPerDivision)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_VOLTS_PER_DIV,
      Tek2440_InstrumentCommand.ICARG_TEK2440_VOLTS_PER_DIV_CHANNEL, channel,
      Tek2440_InstrumentCommand.ICARG_TEK2440_VOLTS_PER_DIV, voltsPerDivision));
  }
  
  public void   setChannelVariableY (
    final Tek2440Channel channel,
    final double variableY)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_VARIABLE_Y,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_VARIABLE_Y_CHANNEL, channel,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_VARIABLE_Y, variableY));
  }

  public void setChannelCoupling (
    final Tek2440Channel channel,
    final Tek2440_GPIB_Settings.ChannelCoupling channelCoupling)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_COUPLING,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_COUPLING_CHANNEL, channel,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_COUPLING, channelCoupling));
  }
  
  public void setChannelFiftyOhms (
    final Tek2440Channel channel,
    final boolean fiftyOhms)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_50_OHMS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_50_OHMS_CHANNEL, channel,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_50_OHMS, fiftyOhms));
  }
  
  public void setChannelInvert (
    final Tek2440Channel channel,
    final boolean invert)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_INVERT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_INVERT_CHANNEL, channel,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_INVERT, invert));
  }
  
  public void setChannelPosition (
    final Tek2440Channel channel,
    final double position)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_POSITION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_POSITION_CHANNEL, channel,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_POSITION, position));
  }
  
  public enum DisplayIntensityComponent
  {
    DISPLAY,
    GRATICULE,
    INTENSIFIED_ZONE,
    READOUT;
  }
  
  public void setDisplayIntensity (
    final DisplayIntensityComponent component,
    final double intensity)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DISPLAY_INTENSITY,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DISPLAY_INTENSITY_COMPONENT, component,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DISPLAY_INTENSITY, intensity));
  }
  
  public void setDisplayIntensity_Display (final double intensity)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setDisplayIntensity (DisplayIntensityComponent.DISPLAY, intensity);
  }
  
  public void setDisplayIntensity_Graticule (final double intensity)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setDisplayIntensity (DisplayIntensityComponent.GRATICULE, intensity);
  }
  
  public void setDisplayIntensity_IntensifiedZone (final double intensity)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setDisplayIntensity (DisplayIntensityComponent.INTENSIFIED_ZONE, intensity);
  }
  
  public void setDisplayIntensity_Readout (final double intensity)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setDisplayIntensity (DisplayIntensityComponent.READOUT, intensity);
  }
  
  public void setReferencePositionMode (
    final Tek2440_GPIB_Settings.RefPositionMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_POSITION_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_POSITION_MODE, mode));
  }
  
  public void setReferencePosition (
    final int referenceNumber,
    final double referencePosition)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_POSITION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_POSITION_NUMBER, referenceNumber,
      Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_POSITION, referencePosition));
  }
  
  public void setReferencePosition_1 (final double referencePosition)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setReferencePosition (1, referencePosition);
  }
  
  public void setReferencePosition_2 (final double referencePosition)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setReferencePosition (2, referencePosition);
  }
  
  public void setReferencePosition_3 (final double referencePosition)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setReferencePosition (3, referencePosition);
  }
  
  public void setReferencePosition_4 (final double referencePosition)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setReferencePosition (4, referencePosition);
  }
  
  public void setReferenceSource (final Tek2440_GPIB_Settings.RefFrom refSource)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_SOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_SOURCE, refSource));
  }
  
  public void clearReference (final int referenceNumber)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_CLEAR,
      Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_CLEAR_NUMBER, referenceNumber));
  }
  
  public void clearReference1 ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    clearReference (1);
  }
  
  public void clearReference2 ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    clearReference (2);
  }
  
  public void clearReference3 ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    clearReference (3);
  }
  
  public void clearReference4 ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    clearReference (4);
  }
  
  public void writeReference (final int referenceNumber)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_WRITE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_WRITE_NUMBER, referenceNumber));
  }
  
  public void writeReferenceStack ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    writeReference (0);
  }
  
  public void writeReference1 ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    writeReference (1);
  }
  
  public void writeReference2 ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    writeReference (2);
  }
  
  public void writeReference3 ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    writeReference (3);
  }
  
  public void writeReference4 ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    writeReference (4);
  }
  
  public void displayReference (final int referenceNumber, final boolean display)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_DISPLAY,
      Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_DISPLAY_NUMBER, referenceNumber,
      Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_DISPLAY, display));
  }
  
  public void displayReference1 (final boolean display)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    displayReference (1, display);
  }
  
  public void displayReference2 (final boolean display)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    displayReference (2, display);
  }
  
  public void displayReference3 (final boolean display)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    displayReference (3, display);
  }
  
  public void displayReference4 (final boolean display)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    displayReference (4, display);
  }
  
  public void setUseLongResponse (final boolean useLongResponse)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_LONG_RESPONSE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_LONG_RESPONSE, useLongResponse));
  }
  
  public void setUsePath (final boolean usePath)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_USE_PATH,
      Tek2440_InstrumentCommand.ICARG_TEK2440_USE_PATH, usePath));
  }
  
  public void setDelayEventsMode (final boolean delayEventsEnabled)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DELAY_EVENTS_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DELAY_EVENTS_MODE, delayEventsEnabled));
  }
  
  public void setDelayEvents (final int delayEvents)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DELAY_EVENTS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DELAY_EVENTS, delayEvents));
  }
  
  public void setDelayTimesDelta (final boolean delayTimesDelta)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DELAY_TIMES_DELTA,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DELAY_TIMES_DELTA, delayTimesDelta));
  }
  
  public void setDelayTime (final int delayTimeTarget, final double delayTime_s)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DELAY_TIME,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DELAY_TIME_TARGET, delayTimeTarget,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DELAY_TIME, delayTime_s));
  }
  
  public void setDelayTime1 (final double delayTime_s)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setDelayTime (1, delayTime_s);
  }
  
  public void setDelayTime2 (final double delayTime_s)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setDelayTime (2, delayTime_s);    
  }
  
  public void setExtGain (final int extGainTarget, final Tek2440_GPIB_Settings.ExtGain extGain)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_EXT_GAIN,
      Tek2440_InstrumentCommand.ICARG_TEK2440_EXT_GAIN_TARGET, extGainTarget,
      Tek2440_InstrumentCommand.ICARG_TEK2440_EXT_GAIN, extGain));
  }
  
  public void setExtGain1 (final Tek2440_GPIB_Settings.ExtGain extGain)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setExtGain (1, extGain);
  }
  
  public void setExtGain2 (final Tek2440_GPIB_Settings.ExtGain extGain)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    setExtGain (2, extGain);    
  }
  
  public void setWordClock (final Tek2440_GPIB_Settings.WordClock clock)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_WORD_CLOCK,
      Tek2440_InstrumentCommand.ICARG_TEK2440_WORD_CLOCK, clock));
  }
  
  public void setWordRadix (final Tek2440_GPIB_Settings.WordRadix radix)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_WORD_RADIX,
      Tek2440_InstrumentCommand.ICARG_TEK2440_WORD_RADIX, radix));
  }
    
  public void setWord (final String word)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_WORD,
      Tek2440_InstrumentCommand.ICARG_TEK2440_WORD, word));
  }
    
  public void setDisplayVectors (final boolean display)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DISPLAY_VECTORS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DISPLAY_VECTORS, display));
  }
  
  public void setDisplayReadout (final boolean display)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DISPLAY_READOUT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DISPLAY_READOUT, display));
  }
  
  public void setBandwidthLimit (final Tek2440_GPIB_Settings.BandwidthLimit bandwidthLimit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_BANDWIDTH_LIMIT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_BANDWIDTH_LIMIT, bandwidthLimit));
  }
  
  public void setEnableSrqInternalError (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_INTERNAL_ERROR,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_INTERNAL_ERROR, enable));
  }
  
  public void setEnableSrqCommandError (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_COMMAND_ERROR,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_COMMAND_ERROR, enable));
  }
  
  public void setEnableSrqExecutionError (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_EXECUTION_ERROR,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_EXECUTION_ERROR, enable));
  }
  
  public void setEnableSrqExecutionWarning (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_EXECUTION_WARNING,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_EXECUTION_WARNING, enable));
  }
  
  public void setEnableSrqCommandCompletion (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_COMMAND_COMPLETION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_COMMAND_COMPLETION, enable));
  }
  
  public void setEnableSrqOnEvent (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_ON_EVENT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_ON_EVENT, enable));
  }
  
  public void setEnableSrqDeviceDependent (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_DEVICE_DEPENDENT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_DEVICE_DEPENDENT, enable));
  }
  
  public void setEnableSrqOnUserButton (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_ON_USER_BUTTON,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_ON_USER_BUTTON, enable));
  }
  
  public void setEnableSrqOnProbeIdentifyButton (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_ON_PROBE_IDENTIFY_BUTTON,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_ON_PROBE_IDENTIFY_BUTTON, enable));
  }
  
  public void setGroupExecuteTriggerMode (final Tek2440_GPIB_Settings.GroupTriggerSRQMode mode, final String userSequence)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    if (mode == null)
      throw new IllegalArgumentException ();
    if (mode == Tek2440_GPIB_Settings.GroupTriggerSRQMode.ExecuteSequence
      && (userSequence == null || userSequence.trim ().isEmpty ()))
      throw new IllegalArgumentException ();
    if (mode != Tek2440_GPIB_Settings.GroupTriggerSRQMode.ExecuteSequence && userSequence != null)
      throw new IllegalArgumentException ();
    final String userSequenceString = (userSequence == null) ? null : userSequence.trim ();
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_GROUP_EXECUTE_TRIGGER_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_GROUP_EXECUTE_TRIGGER_MODE, mode,
      Tek2440_InstrumentCommand.ICARG_TEK2440_GROUP_EXECUTE_TRIGGER_MODE_USER_SEQUENCE, userSequenceString));
  }
  
  public void setAutoSetupMode (final Tek2440_GPIB_Settings.AutoSetupMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_AUTO_SETUP_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_AUTO_SETUP_MODE, mode));
  }
  
  public void setAutoSetupResolution (final Tek2440_GPIB_Settings.AutoSetupResolution resolution)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_AUTO_SETUP_RESOLUTION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_AUTO_SETUP_RESOLUTION, resolution));
  }
  
  public void autoSetup ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (Tek2440_InstrumentCommand.IC_TEK2440_AUTO_SETUP));
  }
  
  public void setAcquisitionRepetitive (final boolean repetitive)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ACQUISITION_REPETITIVE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ACQUISITION_REPETITIVE, repetitive));
  }
  
  public void setAcquisitionMode (final Tek2440_GPIB_Settings.AcquisitionMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ACQUISITION_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ACQUISITION_MODE, mode));
  }
  
  public void setNumberOfAcquisitionsAveraged (final Tek2440_GPIB_Settings.NumberOfAcquisitionsAveraged number)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ACQUISITION_NR_AVERAGED,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ACQUISITION_NR_AVERAGED, number));
  }
  
  public void setNumberOfEnvelopeSweeps (final Tek2440_GPIB_Settings.NumberOfEnvelopeSweeps number)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ACQUISITION_NR_ENV_SWEEPS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ACQUISITION_NR_ENV_SWEEPS, number));
  }
  
  public void setAcquisitionSaveOnDelta (final boolean saveOnDelta)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ACQUISITION_SAVE_ON_DELTA,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ACQUISITION_SAVE_ON_DELTA, saveOnDelta));
  }
  
  public void setRunMode (final Tek2440_GPIB_Settings.RunMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_RUN_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_RUN_MODE, mode));
  }
  
  public void setSmoothing (final boolean smoothing)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_SMOOTHING,
      Tek2440_InstrumentCommand.ICARG_TEK2440_SMOOTHING, smoothing));
  }
  
  public void setATriggerMode (final Tek2440_GPIB_Settings.ATriggerMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_MODE, mode));
  }
  
  public void setATriggerSource (final Tek2440_GPIB_Settings.ATriggerSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_SOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_SOURCE, source));
  }
  
  public void setATriggerCoupling (final Tek2440_GPIB_Settings.ATriggerCoupling coupling)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_COUPLING,
      Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_COUPLING, coupling));
  }
  
  public void setATriggerSlope (final Tek2440_GPIB_Settings.Slope slope)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_SLOPE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_SLOPE, slope));
  }
  
  public void setATriggerLevel (final double level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_LEVEL, level));
  }
  
  public void setATriggerPosition (final Tek2440_GPIB_Settings.ATriggerPosition position)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_POSITION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_POSITION, position));
  }
  
  public void setATriggerHoldoff (final double holdoff)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_HOLDOFF,
      Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_HOLDOFF, holdoff));
  }
  
  public void setATriggerLogSource (final Tek2440_GPIB_Settings.ATriggerLogSource logSource)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_LOG_SOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_LOG_SOURCE, logSource));
  }
  
  public void setTriggerABSelect (final Tek2440_GPIB_Settings.ABSelect abSelect)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_TRIGGER_A_B_SELECT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_TRIGGER_A_B_SELECT, abSelect));
  }
  
  public void setBTriggerMode (final Tek2440_GPIB_Settings.BTriggerMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_MODE, mode));
  }
  
  public void setBTriggerSource (final Tek2440_GPIB_Settings.BTriggerSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_SOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_SOURCE, source));
  }
  
  public void setBTriggerCoupling (final Tek2440_GPIB_Settings.BTriggerCoupling coupling)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_COUPLING,
      Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_COUPLING, coupling));
  }
  
  public void setBTriggerSlope (final Tek2440_GPIB_Settings.Slope slope)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_SLOPE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_SLOPE, slope));
  }
  
  public void setBTriggerLevel (final double level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_LEVEL, level));
  }
  
  public void setBTriggerPosition (final Tek2440_GPIB_Settings.BTriggerPosition position)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_POSITION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_POSITION, position));
  }
  
  public void setBTriggerExternalClock (final boolean externalClock)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_EXTERNAL_CLOCK,
      Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_EXTERNAL_CLOCK, externalClock));
  }
  
  public void setEnableDebug (final boolean enableDebug)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_DEBUG,
      Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_DEBUG, enableDebug));
  }
  
  public void setSequencerEnableFormatChars (final boolean sequencerEnableFormatChars)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_SEQUENCER_ENABLE_FORMAT_CHARS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_SEQUENCER_ENABLE_FORMAT_CHARS, sequencerEnableFormatChars));
  }

  public void setSequencerForce (final boolean sequencerForce)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_SEQUENCER_FORCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_SEQUENCER_FORCE, sequencerForce));
  }

  public void setSequencerActionsFromInt (final int actions)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_SEQUENCER_ACTIONS_FROM_INT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_SEQUENCER_ACTIONS_FROM_INT, actions));
  }

  public void setPrintDeviceType (final Tek2440_GPIB_Settings.PrintDeviceType type)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_PRINT_DEVICE_TYPE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_DEVICE_TYPE, type));
  }

  public void setPrintPageSize (final Tek2440_GPIB_Settings.PrintPageSize pageSize)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_PRINT_PAGE_SIZE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_PAGE_SIZE, pageSize));
  }

  public void setPrintGraticule (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_PRINT_GRATICULE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_GRATICULE, enable));
  }

  public void setPrintSettings (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_PRINT_SETTINGS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_SETTING, enable));
  }

  public void setPrintText (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_PRINT_TEXT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_TEXT, enable));
  }

  public void setPrintWaveforms (final boolean enable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_PRINT_WAVEFORMS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_WAVEFORMS, enable));
  }

  public void executePrint ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_PRINT));
  }

  public void setLockMode (final Tek2440_GPIB_Settings.LockMode lockMode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_LOCK_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_LOCK_MODE, lockMode));
  }

  public void setWaveformAnalysisLevel (final int level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_WAVEFORM_ANALYSIS_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_WAVEFORM_ANALYSIS_LEVEL, level));
  }

  public void setWaveformAnalysisDirection (final Tek2440_GPIB_Settings.Direction direction)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_WAVEFORM_ANALYSIS_DIRECTION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_WAVEFORM_ANALYSIS_DIRECTION, direction));
  }

  public void setWaveformAnalysisHysteresis (final int hysteresis)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_WAVEFORM_ANALYSIS_HYSTERESIS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_WAVEFORM_ANALYSIS_HYSTERESIS, hysteresis));
  }

  public void setWaveformAnalysisStartPosition (final int position)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_WAVEFORM_ANALYSIS_START_POSITION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_WAVEFORM_ANALYSIS_START_POSITION, position));
  }

  public void setWaveformAnalysisStopPosition (final int position)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_WAVEFORM_ANALYSIS_STOP_POSITION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_WAVEFORM_ANALYSIS_STOP_POSITION, position));
  }

  public void setCursorFunction (final Tek2440_GPIB_Settings.CursorFunction function)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_FUNCTION,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_FUNCTION, function));
  }

  public void setCursorTarget (final Tek2440_GPIB_Settings.CursorTarget target)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_TARGET,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_TARGET, target));
  }

  public void setCursorMode (final Tek2440_GPIB_Settings.CursorMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_MODE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_MODE, mode));
  }

  public void setCursorSelect (final Tek2440_GPIB_Settings.CursorSelect select)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_SELECT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_SELECT, select));
  }

  public void setCursorUnitVolts (final Tek2440_GPIB_Settings.CursorUnitVolts unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_VOLTS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_VOLTS, unit));
  }
  
  public void setCursorUnitRefVolts (final Tek2440_GPIB_Settings.RefVoltsUnit unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_REF_VOLTS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_REF_VOLTS, unit));
  }
  
  public void setCursorValueRefVolts (final double value)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_VALUE_REF_VOLTS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_VALUE_REF_VOLTS, value));
  }
  
  public void setCursorUnitTime (final Tek2440_GPIB_Settings.CursorUnitTime unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_TIME,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_TIME, unit));
  }
  
  public void setCursorUnitRefTime (final Tek2440_GPIB_Settings.RefTimeUnit unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_REF_TIME,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_REF_TIME, unit));
  }
  
  public void setCursorValueRefTime (final double value)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_VALUE_REF_TIME,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_VALUE_REF_TIME, value));
  }
  
  public void setCursorUnitSlope (final Tek2440_GPIB_Settings.CursorUnitSlope unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_SLOPE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_SLOPE, unit));
  }
  
  public void setCursorUnitRefSlopeX (final Tek2440_GPIB_Settings.RefSlopeXUnit unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_REF_SLOPE_X,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_REF_SLOPE_X, unit));
  }
  
  public void setCursorUnitRefSlopeY (final Tek2440_GPIB_Settings.RefSlopeYUnit unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_REF_SLOPE_Y,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_REF_SLOPE_Y, unit));
  }
  
  public void setCursorValueRefSlope (final double value)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_VALUE_REF_SLOPE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_VALUE_REF_SLOPE, value));
  }
  
  public void setCursorPosition1X (final double x)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_ONE_X,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_ONE_X, x));
  }
  
  public void setCursorPosition1Y (final double y)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_ONE_Y,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_ONE_Y, y));
  }
  
  public void setCursorPosition1t (final double t)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_ONE_T,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_ONE_T, t));
  }
  
  public void setCursorPosition2X (final double x)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_TWO_X,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_TWO_X, x));
  }
  
  public void setCursorPosition2Y (final double y)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_TWO_Y,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_TWO_Y, y));
  }
  
  public void setCursorPosition2t (final double t)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_TWO_T,
      Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_TWO_T, t));
  }
  
  public void setMeasurementMethod (final Tek2440_GPIB_Settings.MeasurementMethod method)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_METHOD,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_METHOD, method));
  }
  
  public void setMeasurementWindowing (final boolean windowing)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_WINDOWING,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_WINDOWING, windowing));
  }
  
  public void setMeasurementDisplay (final boolean dislay)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DISPLAY,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DISPLAY, dislay));
  }
  
  public void setMeasurementDisplayMarkers (final boolean dislay)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DISPLAY_MARKERS,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DISPLAY_MARKERS, dislay));
  }
  
  public void setMeasurementChannel1Type (final Tek2440_GPIB_Settings.MeasurementType type)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL1_TYPE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL1_TYPE, type));
  }
  
  public void setMeasurementChannel1Source (final Tek2440_GPIB_Settings.MeasurementSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL1_SOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL1_SOURCE, source));
  }
  
  public void setMeasurementChannel1DSource (final Tek2440_GPIB_Settings.MeasurementSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL1_DSOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL1_DSOURCE, source));
  }
  
  public void setMeasurementChannel2Type (final Tek2440_GPIB_Settings.MeasurementType type)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL2_TYPE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL2_TYPE, type));
  }
  
  public void setMeasurementChannel2Source (final Tek2440_GPIB_Settings.MeasurementSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL2_SOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL2_SOURCE, source));
  }
  
  public void setMeasurementChannel2DSource (final Tek2440_GPIB_Settings.MeasurementSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL2_DSOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL2_DSOURCE, source));
  }
  
  public void setMeasurementChannel3Type (final Tek2440_GPIB_Settings.MeasurementType type)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL3_TYPE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL3_TYPE, type));
  }
  
  public void setMeasurementChannel3Source (final Tek2440_GPIB_Settings.MeasurementSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL3_SOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL3_SOURCE, source));
  }
  
  public void setMeasurementChannel3DSource (final Tek2440_GPIB_Settings.MeasurementSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL3_DSOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL3_DSOURCE, source));
  }
  
  public void setMeasurementChannel4Type (final Tek2440_GPIB_Settings.MeasurementType type)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL4_TYPE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL4_TYPE, type));
  }
  
  public void setMeasurementChannel4Source (final Tek2440_GPIB_Settings.MeasurementSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL4_SOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL4_SOURCE, source));
  }
  
  public void setMeasurementChannel4DSource (final Tek2440_GPIB_Settings.MeasurementSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL4_DSOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL4_DSOURCE, source));
  }
  
  public void setMeasurementProximalUnit (final Tek2440_GPIB_Settings.ProximalUnit unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_PROXIMAL_UNIT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_PROXIMAL_UNIT, unit));
  }
  
  public void setMeasurementProximalVoltsLevel (final double level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_PROXIMAL_VOLTS_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_PROXIMAL_VOLTS_LEVEL, level));
  }
  
  public void setMeasurementProximalPercentsLevel (final double level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_PROXIMAL_PERCENTS_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_PROXIMAL_PERCENTS_LEVEL, level));
  }
  
  public void setMeasurementMesialUnit (final Tek2440_GPIB_Settings.MesialUnit unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_MESIAL_UNIT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_MESIAL_UNIT, unit));
  }
  
  public void setMeasurementMesialVoltsLevel (final double level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_MESIAL_VOLTS_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_MESIAL_VOLTS_LEVEL, level));
  }
  
  public void setMeasurementMesialPercentsLevel (final double level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_MESIAL_PERCENTS_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_MESIAL_PERCENTS_LEVEL, level));
  }
  
  public void setMeasurementDMesialUnit (final Tek2440_GPIB_Settings.DMesialUnit unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DMESIAL_UNIT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DMESIAL_UNIT, unit));
  }
  
  public void setMeasurementDMesialVoltsLevel (final double level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DMESIAL_VOLTS_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DMESIAL_VOLTS_LEVEL, level));
  }
  
  public void setMeasurementDMesialPercentsLevel (final double level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DMESIAL_PERCENTS_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DMESIAL_PERCENTS_LEVEL, level));
  }
  
  public void setMeasurementDistalUnit (final Tek2440_GPIB_Settings.DistalUnit unit)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DISTAL_UNIT,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DISTAL_UNIT, unit));
  }
  
  public void setMeasurementDistalVoltsLevel (final double level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DISTAL_VOLTS_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DISTAL_VOLTS_LEVEL, level));
  }
  
  public void setMeasurementDistalPercentsLevel (final double level)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DISTAL_PERCENTS_LEVEL,
      Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DISTAL_PERCENTS_LEVEL, level));
  }
  
  public void setDataEncoding (final Tek2440_GPIB_Settings.DataEncoding encoding)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DATA_ENCODING,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DATA_ENCODING, encoding));
  }
  
  public void setDataSource (final Tek2440_GPIB_Settings.DataSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DATA_SOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DATA_SOURCE, source));
  }
  
  public void setDataDSource (final Tek2440_GPIB_Settings.DataSource source)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DATA_DSOURCE,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DATA_DSOURCE, source));
  }
  
  public void setDataTarget (final Tek2440_GPIB_Settings.DataTarget target)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      Tek2440_InstrumentCommand.IC_TEK2440_DATA_TARGET,
      Tek2440_InstrumentCommand.ICARG_TEK2440_DATA_TARGET, target));
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
    if (! instrumentCommand.containsKey (Tek2440_InstrumentCommand.IC_COMMAND_KEY))
      throw new IllegalArgumentException ();
    if (instrumentCommand.get (Tek2440_InstrumentCommand.IC_COMMAND_KEY) == null)
      throw new IllegalArgumentException ();
    if (! (instrumentCommand.get (Tek2440_InstrumentCommand.IC_COMMAND_KEY) instanceof String))
      throw new IllegalArgumentException ();
    final String commandString = (String) instrumentCommand.get (Tek2440_InstrumentCommand.IC_COMMAND_KEY);
    if (commandString.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    final GpibDevice device = (GpibDevice) getDevice ();
    InstrumentSettings newInstrumentSettings = null;
    try
    {
      switch (commandString)
      {
        case Tek2440_InstrumentCommand.IC_NOP_KEY:
          break;
        case Tek2440_InstrumentCommand.IC_GET_SETTINGS_KEY:
        {
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_HORIZONTAL_MODE:
        {
          final Tek2440_GPIB_Settings.HorizontalMode mode =
            (Tek2440_GPIB_Settings.HorizontalMode) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_HORIZONTAL_MODE);
          switch (mode)
          {
            case A_Sweep:         writeSync ("HOR MOD:ASW\r\n"); break;
            case B_Sweep:         writeSync ("HOR MOD:BSW\r\n"); break;
            case A_Intensified_B: writeSync ("HOR MOD:AIN\r\n"); break;
            default:              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_TIMEBASE:
        {
          final Tek2440Channel channel =
            (Tek2440Channel) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_TIMEBASE_CHANNEL);
          final Tek2440_GPIB_Settings.SecondsPerDivision secondsPerDivision =
            (Tek2440_GPIB_Settings.SecondsPerDivision) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_TIMEBASE);
          switch (channel)
          {
            case Channel1: writeSync ("HOR ASE:" + secondsPerDivision.getSecondsPerDivision_s ()  + "\r\n"); break;
            case Channel2: writeSync ("HOR BSE:" + secondsPerDivision.getSecondsPerDivision_s ()  + "\r\n"); break;
            default:       throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_HORIZONTAL_POSITION:
        {
          final double position =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_HORIZONTAL_POSITION);
          writeSync ("HOR POS:" + Double.toString (position) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_HORIZONTAL_EXTERNAL_EXPANSION:
        {
          final Tek2440_GPIB_Settings.HorizontalExternalExpansionFactor expansion =
            (Tek2440_GPIB_Settings.HorizontalExternalExpansionFactor) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_HORIZONTAL_EXTERNAL_EXPANSION);
          if (expansion == null)
            throw new IllegalArgumentException ();
          writeSync ("HOR EXTE:" + Integer.toString (expansion.toInt ()) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_VOLTS_PER_DIV:
        {
          final Tek2440Channel channel =
            (Tek2440Channel) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_VOLTS_PER_DIV_CHANNEL);
          final Tek2440_GPIB_Settings.VoltsPerDivision voltsPerDivision =
            (Tek2440_GPIB_Settings.VoltsPerDivision) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_VOLTS_PER_DIV);
          switch (channel)
          {
            case Channel1: writeSync ("CH1 VOL:" + voltsPerDivision.getVoltsPerDivision_V () + "\r\n"); break;
            case Channel2: writeSync ("CH2 VOL:" + voltsPerDivision.getVoltsPerDivision_V () + "\r\n"); break;
            default:       throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_VARIABLE_Y:
        {
          final Tek2440Channel channel =
            (Tek2440Channel) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_VARIABLE_Y_CHANNEL);
          final double variableY =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_VARIABLE_Y);
          switch (channel)
          {
            case Channel1: writeSync ("CH1 VAR:" + Double.toString (variableY) + "\r\n"); break;
            case Channel2: writeSync ("CH2 VAR:" + Double.toString (variableY) + "\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_ENABLE:
        {
          final Tek2440Channel channel =
            (Tek2440Channel) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_ENABLE_CHANNEL);
          final boolean channelEnable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_ENABLE);
          switch (channel)
          {
            case Channel1: writeSync ("VMO CH1:" + (channelEnable ? "ON" : "OFF") + "\r\n"); break;
            case Channel2: writeSync ("VMO CH2:" + (channelEnable ? "ON" : "OFF") + "\r\n"); break;
            default:       throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ADD_ENABLE:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ADD_ENABLE);
          writeSync ("VMO ADD:" + (enable ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MULT_ENABLE:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MULT_ENABLE);
          writeSync ("VMO MUL:" + (enable ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_VERTICAL_DISPLAY_MODE:
        {
          final Tek2440_GPIB_Settings.VModeDisplay mode =
            (Tek2440_GPIB_Settings.VModeDisplay) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_VERTICAL_DISPLAY_MODE);
          switch (mode)
          {
            case YT: writeSync ("VMO DISP:YT\r\n"); break;
            case XY: writeSync ("VMO DISP:XY\r\n"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_COUPLING:
        {
          final Tek2440Channel channel =
            (Tek2440Channel) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_COUPLING_CHANNEL);
          final Tek2440_GPIB_Settings.ChannelCoupling channelCoupling =
            (Tek2440_GPIB_Settings.ChannelCoupling) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_COUPLING);
          switch (channel)
          {
            case Channel1:
              switch (channelCoupling)
              {
                case AC:  writeSync ("CH1 COU:AC\r\n");  break;
                case DC:  writeSync ("CH1 COU:DC\r\n");  break;
                case GND: writeSync ("CH1 COU:GND\r\n"); break;
                default:  throw new IllegalArgumentException ();
              }
              break;
            case Channel2:
              switch (channelCoupling)
              {
                case AC:  writeSync ("CH2 COU:AC\r\n");  break;
                case DC:  writeSync ("CH2 COU:DC\r\n");  break;
                case GND: writeSync ("CH2 COU:GND\r\n"); break;
                default:  throw new IllegalArgumentException ();
              }
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_50_OHMS:
        {
          final Tek2440Channel channel =
            (Tek2440Channel) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_50_OHMS_CHANNEL);
          final boolean channelFiftyOhms =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_50_OHMS);
          switch (channel)
          {
            case Channel1:
              writeSync ("CH1 FIF:" + (channelFiftyOhms ? "ON" : "OFF") + "\r\n");
              break;
            case Channel2:
              writeSync ("CH2 FIF:" + (channelFiftyOhms ? "ON" : "OFF") + "\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_INVERT:
        {
          final Tek2440Channel channel =
            (Tek2440Channel) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_INVERT_CHANNEL);
          final boolean channelInvert =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_INVERT);
          switch (channel)
          {
            case Channel1:
              writeSync ("CH1 INV:" + (channelInvert ? "ON" : "OFF") + "\r\n");
              break;
            case Channel2:
              writeSync ("CH2 INV:" + (channelInvert ? "ON" : "OFF") + "\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CHANNEL_POSITION:
        {
          final Tek2440Channel channel =
            (Tek2440Channel) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_POSITION_CHANNEL);
          final double channelPosition =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CHANNEL_POSITION);
          switch (channel)
          {
            case Channel1:
              writeSync ("CH1 POS:" + channelPosition + "\r\n");
              break;
            case Channel2:
              writeSync ("CH2 POS:" + channelPosition + "\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DISPLAY_INTENSITY:
        {
          final DisplayIntensityComponent component =
            (DisplayIntensityComponent) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DISPLAY_INTENSITY_COMPONENT);
          if (component == null)
            throw new IllegalArgumentException ();
          final Double intensity =
            (Double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DISPLAY_INTENSITY);
          if (intensity == null || intensity < 0 || intensity > 100)
            throw new IllegalArgumentException ();
          final String componentString;
          switch (component)
          {
            case DISPLAY:          componentString = "DISP:";   break;
            case GRATICULE:        componentString = "GRA:";    break;
            case INTENSIFIED_ZONE: componentString = "INTENS:"; break;
            case READOUT:          componentString = "REA:";    break;
            default: throw new IllegalArgumentException ();
          }
          writeSync ("INTENSI " + componentString + Double.toString (intensity) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_POSITION_MODE:
        {
          final Tek2440_GPIB_Settings.RefPositionMode mode =
            (Tek2440_GPIB_Settings.RefPositionMode) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_POSITION_MODE);
          if (mode == null)
            throw new IllegalArgumentException ();
          switch (mode)
          {
            case Independent:
              writeSync ("REFP MOD:IND\r\n");
              break;
            case Lock:
              writeSync ("REFP MOD:LOC\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_POSITION:
        {
          final int refNumber =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_POSITION_NUMBER);
          final double refPosition =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_POSITION);
          if (refPosition < 0 || refPosition > 1023)
            throw new IllegalArgumentException ();
          switch (refNumber)
          {
            case 1: writeSync ("REFP REF1:" + Double.toString (refPosition) + "\r\n"); break;
            case 2: writeSync ("REFP REF2:" + Double.toString (refPosition) + "\r\n"); break;
            case 3: writeSync ("REFP REF3:" + Double.toString (refPosition) + "\r\n"); break;
            case 4: writeSync ("REFP REF4:" + Double.toString (refPosition) + "\r\n"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_SOURCE:
        {
          final Tek2440_GPIB_Settings.RefFrom refSource =
            (Tek2440_GPIB_Settings.RefFrom) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_SOURCE);
          if (refSource == null)
            throw new IllegalArgumentException ();
          switch (refSource)
          {
            case Ch1:     writeSync ("REFF CH1\r\n");   break;
            case Ch2:     writeSync ("REFF CH2\r\n");   break;
            case Add:     writeSync ("REFF ADD\r\n");   break;
            case Mult:    writeSync ("REFF MUL\r\n");   break;
            case Ch1Del:  writeSync ("REFF CH1D\r\n");  break;
            case Ch2Del:  writeSync ("REFF CH2D\r\n");  break;
            case AddDel:  writeSync ("REFF ADDD\r\n");  break;
            case MultDel: writeSync ("REFF MULTD\r\n"); break;
            case Ref1:    writeSync ("REFF REF1\r\n");  break;
            case Ref2:    writeSync ("REFF REF2\r\n");  break;
            case Ref3:    writeSync ("REFF REF3\r\n");  break;
            case Ref4:    writeSync ("REFF REF4\r\n");  break;
            default: throw new IllegalArgumentException ();           
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_CLEAR:
        {
          final int refNumber =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_CLEAR_NUMBER);
          switch (refNumber)
          {
            case 1: writeSync ("REFD REF1:EMP\r\n"); break;
            case 2: writeSync ("REFD REF2:EMP\r\n"); break;
            case 3: writeSync ("REFD REF3:EMP\r\n"); break;
            case 4: writeSync ("REFD REF4:EMP\r\n"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_WRITE:
        {
          final int refNumber =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_WRITE_NUMBER);
          switch (refNumber)
          {
            case 0: writeSync ("SAVER STAC\r\n"); break;
            case 1: writeSync ("SAVER REF1\r\n"); break;
            case 2: writeSync ("SAVER REF2\r\n"); break;
            case 3: writeSync ("SAVER REF3\r\n"); break;
            case 4: writeSync ("SAVER REF4\r\n"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_REFERENCE_DISPLAY:
        {
          final int refNumber =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_DISPLAY_NUMBER);
          final boolean display =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_REFERENCE_DISPLAY);
          switch (refNumber)
          {
            case 1: writeSync ("REFD REF1:" + (display? "ON" : "OFF") + "\r\n"); break;
            case 2: writeSync ("REFD REF2:" + (display? "ON" : "OFF") + "\r\n"); break;
            case 3: writeSync ("REFD REF3:" + (display? "ON" : "OFF") + "\r\n"); break;
            case 4: writeSync ("REFD REF4:" + (display? "ON" : "OFF") + "\r\n"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_LONG_RESPONSE:
        {
          final boolean longResponse =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_LONG_RESPONSE);
          writeSync ("LON " + (longResponse? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_USE_PATH:
        {
          final boolean usePath =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_USE_PATH);
          writeSync ("PAT " + (usePath? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DELAY_EVENTS_MODE:
        {
          final boolean delayEventsEnabled =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DELAY_EVENTS_MODE);
          writeSync ("DLYE MOD:" + (delayEventsEnabled? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DELAY_EVENTS:
        {
          final int delayEvents =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DELAY_EVENTS);
          writeSync ("DLYE VAL:" + Integer.toString (delayEvents) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DELAY_TIMES_DELTA:
        {
          final boolean delayTimesDelta =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DELAY_TIMES_DELTA);
          writeSync ("DLYT DELT:" + (delayTimesDelta? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DELAY_TIME:
        {
          final int delayTimeTarget =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DELAY_TIME_TARGET);
          final double delayTime_s =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DELAY_TIME);
          switch (delayTimeTarget)
          {
            case 1:
              writeSync ("DLYT DLY1:" + delayTime_s + "\r\n");
              break;
            case 2:
              writeSync ("DLYT DLY2:" + delayTime_s + "\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_EXT_GAIN:
        {
          final int extGainTarget =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_EXT_GAIN_TARGET);
          final Tek2440_GPIB_Settings.ExtGain extGain =
            (Tek2440_GPIB_Settings.ExtGain) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_EXT_GAIN);
          switch (extGainTarget)
          {
            case 1:
              writeSync ("EXTG EXT1:" + extGain.toTek2440String () + "\r\n");
              break;
            case 2:
              writeSync ("EXTG EXT2:" + extGain.toTek2440String () + "\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_WORD_CLOCK:
        {
          final Tek2440_GPIB_Settings.WordClock clock =
            (Tek2440_GPIB_Settings.WordClock) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_WORD_CLOCK);
          switch (clock)
          {
            case ASync:
              writeSync ("SETW CLO:ASY\r\n");
              break;
            case Fall:
              writeSync ("SETW CLO:FAL\r\n");
              break;
            case Rise:
              writeSync ("SETW CLO:RIS\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_WORD_RADIX:
        {
          final Tek2440_GPIB_Settings.WordRadix radix =
            (Tek2440_GPIB_Settings.WordRadix) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_WORD_RADIX);
          switch (radix)
          {
            case Octal:
              writeSync ("SETW RAD:OCT\r\n");
              break;
            case Hexadecimal:
              writeSync ("SETW RAD:HEX\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_WORD:
        {
          final String word =
            ((String) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_WORD)).trim ();
          if (word == null)
            throw new IllegalArgumentException ();
          switch (word.length ())
          {
            case 17:
              writeSync ("SETW WOR:#Y" + word + "\r\n");
              break;
            case 19:
              writeSync ("SETW WOR:" + word + "\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DISPLAY_VECTORS:
        {
          final boolean display =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DISPLAY_VECTORS);
          writeSync ("INTENSI VEC:" + (display? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DISPLAY_READOUT:
        {
          final boolean display =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DISPLAY_READOUT);
          writeSync ("REA " + (display? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_BANDWIDTH_LIMIT:
        {
          final Tek2440_GPIB_Settings.BandwidthLimit bandwidthLimit =
            (Tek2440_GPIB_Settings.BandwidthLimit) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_BANDWIDTH_LIMIT);
          switch (bandwidthLimit)
          {
            case BWL_20_MHz:  writeSync ("BWL TWE\r\n"); break;
            case BWL_100_MHz: writeSync ("BWL HUN\r\n"); break;
            case BWL_FULL:    writeSync ("BWL FUL\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_INTERNAL_ERROR:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_INTERNAL_ERROR);
          writeSync ("INR " + (enable? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_COMMAND_ERROR:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_COMMAND_ERROR);
          writeSync ("CER " + (enable? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_EXECUTION_ERROR:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_EXECUTION_ERROR);
          writeSync ("EXR " + (enable? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_EXECUTION_WARNING:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_EXECUTION_WARNING);
          writeSync ("EXW " + (enable? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_COMMAND_COMPLETION:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_COMMAND_COMPLETION);
          writeSync ("OPC " + (enable? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_ON_EVENT:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_ON_EVENT);
          writeSync ("RQS " + (enable? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_DEVICE_DEPENDENT:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_DEVICE_DEPENDENT);
          writeSync ("DEVD " + (enable? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_ON_USER_BUTTON:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_ON_USER_BUTTON);
          writeSync ("USE " + (enable? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_SRQ_ON_PROBE_IDENTIFY_BUTTON:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_SRQ_ON_PROBE_IDENTIFY_BUTTON);
          writeSync ("PID " + (enable? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_GROUP_EXECUTE_TRIGGER_MODE:
        {
          final Tek2440_GPIB_Settings.GroupTriggerSRQMode mode =
            (Tek2440_GPIB_Settings.GroupTriggerSRQMode) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_GROUP_EXECUTE_TRIGGER_MODE);
          final String userSequence =
            (String) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_GROUP_EXECUTE_TRIGGER_MODE_USER_SEQUENCE);
          if (mode == null)
            throw new IllegalArgumentException ();
          if (mode == Tek2440_GPIB_Settings.GroupTriggerSRQMode.ExecuteSequence
            && (userSequence == null || userSequence.trim ().isEmpty ()))
            throw new IllegalArgumentException ();
          if (mode != Tek2440_GPIB_Settings.GroupTriggerSRQMode.ExecuteSequence && userSequence != null)
            throw new IllegalArgumentException ();
          switch (mode)
          {
            case Off:             writeSync ("DT OFF\r\n");                          break;
            case Run:             writeSync ("DT RUN\r\n");                          break;
            case SodRun:          writeSync ("DT SODRUN\r\n");                       break;
            case Step:            writeSync ("DT STE\r\n");                          break;
            case ExecuteSequence: writeSync ("DT " + userSequence.trim () + "\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_AUTO_SETUP_MODE:
        {
          final Tek2440_GPIB_Settings.AutoSetupMode mode =
            (Tek2440_GPIB_Settings.AutoSetupMode) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_AUTO_SETUP_MODE);
          switch (mode)
          {
            case View:   writeSync ("AUTOS MOD:VIE\r\n");  break;
            case Period: writeSync ("AUTOS MOD:PERI\r\n"); break;
            case Pulse:  writeSync ("AUTOS MOD:PUL\r\n");  break;
            case Rise:   writeSync ("AUTOS MOD:RIS\r\n");  break;
            case Fall:   writeSync ("AUTOS MOD:FAL\r\n");  break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_AUTO_SETUP_RESOLUTION:
        {
          final Tek2440_GPIB_Settings.AutoSetupResolution resolution =
            (Tek2440_GPIB_Settings.AutoSetupResolution) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_AUTO_SETUP_RESOLUTION);
          switch (resolution)
          {
            case Low:  writeSync ("AUTOS RES:LO\r\n"); break;
            case High: writeSync ("AUTOS RES:HI\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_AUTO_SETUP:
        {
          writeSync ("AUTOS EXE\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ACQUISITION_MODE:
        {
          final Tek2440_GPIB_Settings.AcquisitionMode mode =
            (Tek2440_GPIB_Settings.AcquisitionMode) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ACQUISITION_MODE);
          switch (mode)
          {
            case Normal:   writeSync ("ACQ MOD:NOR\r\n"); break;
            case Average:  writeSync ("ACQ MOD:AVG\r\n"); break;
            case Envelope: writeSync ("ACQ MOD:ENV\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ACQUISITION_REPETITIVE:
        {
          final boolean repetitive =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ACQUISITION_REPETITIVE);
          writeSync ("ACQ REP:" + (repetitive ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ACQUISITION_NR_AVERAGED:
        {
          final Tek2440_GPIB_Settings.NumberOfAcquisitionsAveraged number =
            (Tek2440_GPIB_Settings.NumberOfAcquisitionsAveraged) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ACQUISITION_NR_AVERAGED);
          if (number == null)
            throw new IllegalArgumentException ();
          writeSync ("ACQ NUMAV:" + number.toInt () + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ACQUISITION_NR_ENV_SWEEPS:
        {
          final Tek2440_GPIB_Settings.NumberOfEnvelopeSweeps number =
            (Tek2440_GPIB_Settings.NumberOfEnvelopeSweeps) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ACQUISITION_NR_ENV_SWEEPS);
          if (number == null)
            throw new IllegalArgumentException ();
          if (number == Tek2440_GPIB_Settings.NumberOfEnvelopeSweeps.ENV_SWEEPS_CONTROL)
            writeSync ("ACQ NUME:CON\r\n");
          else
            writeSync ("ACQ NUME:" + number.getIntValue () + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ACQUISITION_SAVE_ON_DELTA:
        {
          final boolean saveOnDelta =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ACQUISITION_SAVE_ON_DELTA);
          writeSync ("ACQ SAVD:" + (saveOnDelta ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_RUN_MODE:
        {
          final Tek2440_GPIB_Settings.RunMode mode =
            (Tek2440_GPIB_Settings.RunMode) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_RUN_MODE);
          switch (mode)
          {
            case Acquire: writeSync ("RUN ACQ\r\n"); break;
            case Save:    writeSync ("RUN SAV\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_SMOOTHING:
        {
          final boolean smoothing =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_SMOOTHING);
          writeSync ("SMO " + (smoothing ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_MODE:
        {
          final Tek2440_GPIB_Settings.ATriggerMode mode =
            (Tek2440_GPIB_Settings.ATriggerMode) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_MODE);
          switch (mode)
          {
            case Auto:           writeSync ("ATR MOD:AUTO\r\n");  break;
            case AutoLevel:      writeSync ("ATR MOD:AUTOL\r\n"); break;
            case Normal:         writeSync ("ATR MOD:NOR\r\n");   break;
            case SingleSequence: writeSync ("ATR MOD:SGL\r\n");   break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_SOURCE:
        {
          final Tek2440_GPIB_Settings.ATriggerSource source =
            (Tek2440_GPIB_Settings.ATriggerSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_SOURCE);
          switch (source)
          {
            case Ch1:      writeSync ("ATR SOU:CH1\r\n");  break;
            case Ch2:      writeSync ("ATR SOU:CH2\r\n");  break;
            case Ext1:     writeSync ("ATR SOU:EXT1\r\n"); break;
            case Ext2:     writeSync ("ATR SOU:EXT2\r\n"); break;
            case Line:     writeSync ("ATR SOU:LIN\r\n");  break;
            case Vertical: writeSync ("ATR SOU:VER\r\n");  break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_COUPLING:
        {
          final Tek2440_GPIB_Settings.ATriggerCoupling coupling =
            (Tek2440_GPIB_Settings.ATriggerCoupling) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_COUPLING);
          switch (coupling)
          {
            case AC:          writeSync ("ATR COU:AC\r\n");  break;
            case DC:          writeSync ("ATR COU:DC\r\n");  break;
            case LFReject:    writeSync ("ATR COU:LFR\r\n"); break;
            case HFReject:    writeSync ("ATR COU:HFR\r\n"); break;
            case NoiseReject: writeSync ("ATR COU:NOI\r\n"); break;
            case TV:          writeSync ("ATR COU:TV\r\n");  break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_SLOPE:
        {
          final Tek2440_GPIB_Settings.Slope slope =
            (Tek2440_GPIB_Settings.Slope) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_SLOPE);
          switch (slope)
          {
            case Plus:  writeSync ("ATR SLO:PLU\r\n");  break;
            case Minus: writeSync ("ATR SLO:MINU\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_LEVEL:
        {
          final double level =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_LEVEL);
          writeSync ("ATR LEV:" + Double.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_POSITION:
        {
          final Tek2440_GPIB_Settings.ATriggerPosition position =
            (Tek2440_GPIB_Settings.ATriggerPosition) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_POSITION);
          writeSync ("ATR POS:" + Integer.toString (position.toInt ()) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_HOLDOFF:
        {
          final double holdoff =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_HOLDOFF);
          writeSync ("ATR HOL:" + Double.toString (holdoff) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_A_TRIGGER_LOG_SOURCE:
        {
          final Tek2440_GPIB_Settings.ATriggerLogSource logSource =
            (Tek2440_GPIB_Settings.ATriggerLogSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_A_TRIGGER_LOG_SOURCE);
          switch (logSource)
          {
            case Off:     writeSync ("ATR LOG:OFF\r\n"); break;
            case A_and_B: writeSync ("ATR LOG:A.B\r\n"); break;
            case Word:    writeSync ("ATR LOG:WOR\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_TRIGGER_A_B_SELECT:
        {
          final Tek2440_GPIB_Settings.ABSelect abSelect =
            (Tek2440_GPIB_Settings.ABSelect) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_TRIGGER_A_B_SELECT);
          switch (abSelect)
          {
            case A: writeSync ("ATR ABSE:A\r\n"); break;
            case B: writeSync ("ATR ABSE:B\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_MODE:
        {
          final Tek2440_GPIB_Settings.BTriggerMode mode =
            (Tek2440_GPIB_Settings.BTriggerMode) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_MODE);
          switch (mode)
          {
            case RunsAft: writeSync ("BTR MOD:RUNS\r\n"); break;
            case TrigAft: writeSync ("BTR MOD:TRI\r\n");  break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_SOURCE:
        {
          final Tek2440_GPIB_Settings.BTriggerSource source =
            (Tek2440_GPIB_Settings.BTriggerSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_SOURCE);
          switch (source)
          {
            case Ch1:      writeSync ("BTR SOU:CH1\r\n");  break;
            case Ch2:      writeSync ("BTR SOU:CH2\r\n");  break;
            case Ext1:     writeSync ("BTR SOU:EXT1\r\n"); break;
            case Ext2:     writeSync ("BTR SOU:EXT2\r\n"); break;
            case Vertical: writeSync ("BTR SOU:VER\r\n");  break;
            case Word:     writeSync ("BTR SOU:WOR\r\n");  break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_COUPLING:
        {
          final Tek2440_GPIB_Settings.BTriggerCoupling coupling =
            (Tek2440_GPIB_Settings.BTriggerCoupling) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_COUPLING);
          switch (coupling)
          {
            case AC:          writeSync ("BTR COU:AC\r\n");  break;
            case DC:          writeSync ("BTR COU:DC\r\n");  break;
            case LFReject:    writeSync ("BTR COU:LFR\r\n"); break;
            case HFReject:    writeSync ("BTR COU:HFR\r\n"); break;
            case NoiseReject: writeSync ("BTR COU:NOI\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_SLOPE:
        {
          final Tek2440_GPIB_Settings.Slope slope =
            (Tek2440_GPIB_Settings.Slope) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_SLOPE);
          switch (slope)
          {
            case Plus:  writeSync ("BTR SLO:PLU\r\n");  break;
            case Minus: writeSync ("BTR SLO:MINU\r\n"); break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_LEVEL:
        {
          final double level =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_LEVEL);
          writeSync ("BTR LEV:" + Double.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_POSITION:
        {
          final Tek2440_GPIB_Settings.BTriggerPosition position =
            (Tek2440_GPIB_Settings.BTriggerPosition) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_POSITION);
          writeSync ("BTR POS:" + Integer.toString (position.toInt ()) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_B_TRIGGER_EXTERNAL_CLOCK:
        {
          final boolean externalClock =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_B_TRIGGER_EXTERNAL_CLOCK);
          writeSync ("BTR EXTCL:" + (externalClock ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_ENABLE_DEBUG:
        {
          final boolean enableDebug =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_ENABLE_DEBUG);
          writeSync ("DEB " + (enableDebug ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_SEQUENCER_ENABLE_FORMAT_CHARS:
        {
          final boolean sequencerEnableFormatChars =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_SEQUENCER_ENABLE_FORMAT_CHARS);
          writeSync ("FORM " + (sequencerEnableFormatChars ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_SEQUENCER_FORCE:
        {
          final boolean sequencerForce =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_SEQUENCER_FORCE);
          writeSync ("SETU FORC:" + (sequencerForce ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_SEQUENCER_ACTIONS_FROM_INT:
        {
          final int actions =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_SEQUENCER_ACTIONS_FROM_INT);
          writeSync ("SETU ACT:" + Integer.toString (actions) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_PRINT_DEVICE_TYPE:
        {
          final Tek2440_GPIB_Settings.PrintDeviceType type =
            (Tek2440_GPIB_Settings.PrintDeviceType) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_DEVICE_TYPE);
          switch (type)
          {
            case ThinkJet: writeSync ("DEVI TYP:THI\r\n"); break;
            case HPGL:     writeSync ("DEVI TYP:HPG\r\n"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_PRINT_PAGE_SIZE:
        {
          final Tek2440_GPIB_Settings.PrintPageSize pageSize =
            (Tek2440_GPIB_Settings.PrintPageSize) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_PAGE_SIZE);
          switch (pageSize)
          {
            case A4: writeSync ("DEVI PAG:A4\r\n"); break;
            case US: writeSync ("DEVI PAG:US\r\n"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_PRINT_GRATICULE:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_GRATICULE);
          writeSync ("DEVI GRA:" + (enable ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_PRINT_SETTINGS:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_SETTING);
          writeSync ("DEVI SETTI:" + (enable ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_PRINT_TEXT:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_TEXT);
          writeSync ("DEVI TEX:" + (enable ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_PRINT_WAVEFORMS:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_PRINT_WAVEFORMS);
          writeSync ("DEVI WAV:" + (enable ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_PRINT:
        {
          writeSync ("PRI\r\n");
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_LOCK_MODE:
        {
          final Tek2440_GPIB_Settings.LockMode lockMode =
            (Tek2440_GPIB_Settings.LockMode) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_LOCK_MODE);
          switch (lockMode)
          {
            case LLO: writeSync ("LOC LLO\r\n"); break;
            case Off: writeSync ("LOC OFF\r\n"); break;
            case On:  writeSync ("LOC ON\r\n");  break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_WAVEFORM_ANALYSIS_LEVEL:
        {
          final int level =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_WAVEFORM_ANALYSIS_LEVEL);
          writeSync ("LEV " + Integer.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_WAVEFORM_ANALYSIS_DIRECTION:
        {
          final Tek2440_GPIB_Settings.Direction direction =
            (Tek2440_GPIB_Settings.Direction) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_WAVEFORM_ANALYSIS_DIRECTION);
          switch (direction)
          {
            case Plus:  writeSync ("DIR PLU\r\n");  break;
            case Minus: writeSync ("DIR MINU\r\n"); break;
            default:    throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_WAVEFORM_ANALYSIS_HYSTERESIS:
        {
          final int hysteresis =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_WAVEFORM_ANALYSIS_HYSTERESIS);
          writeSync ("HYS " + Integer.toString (hysteresis) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_WAVEFORM_ANALYSIS_START_POSITION:
        {
          final int position =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_WAVEFORM_ANALYSIS_START_POSITION);
          writeSync ("STAR " + Integer.toString (position) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_WAVEFORM_ANALYSIS_STOP_POSITION:
        {
          final int position =
            (int) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_WAVEFORM_ANALYSIS_STOP_POSITION);
          writeSync ("STO " + Integer.toString (position) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_FUNCTION:
        {
          final Tek2440_GPIB_Settings.CursorFunction function =
            (Tek2440_GPIB_Settings.CursorFunction) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_FUNCTION);
          switch (function)
          {
            case Off:        writeSync ("CURS FUN:OFF\r\n");   break; // XXX Manual ERROR!
            case OnePerTime: writeSync ("CURS FUN:ONE/T\r\n"); break;
            case Slope:      writeSync ("CURS FUN:SLO\r\n");   break;
            case Time:       writeSync ("CURS FUN:TIM\r\n");   break;
            case Volts:      writeSync ("CURS FUN:VOLT\r\n");  break; // XXX Manual ERROR!
            case V_dot_t:    writeSync ("CURS FUN:V.T\r\n");   break;
            default:         throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_TARGET:
        {
          final Tek2440_GPIB_Settings.CursorTarget target =
            (Tek2440_GPIB_Settings.CursorTarget) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_TARGET);
          switch (target)
          {
            case Ch1:     writeSync ("CURS TAR:CH1\r\n");   break;
            case Ch2:     writeSync ("CURS TAR:CH2\r\n");   break;
            case Add:     writeSync ("CURS TAR:ADD\r\n");   break;
            case Mult:    writeSync ("CURS TAR:MUL\r\n");   break;
            case Ch1Del:  writeSync ("CURS TAR:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("CURS TAR:CH2D\r\n");  break;
            case AddDel:  writeSync ("CURS TAR:ADDD\r\n");  break;
            case MultDel: writeSync ("CURS TAR:MULTD\r\n"); break;
            case Ref1:    writeSync ("CURS TAR:REF1\r\n");  break;
            case Ref2:    writeSync ("CURS TAR:REF2\r\n");  break;
            case Ref3:    writeSync ("CURS TAR:REF3\r\n");  break;
            case Ref4:    writeSync ("CURS TAR:REF4\r\n");  break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_MODE:
        {
          final Tek2440_GPIB_Settings.CursorMode mode =
            (Tek2440_GPIB_Settings.CursorMode) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_MODE);
          switch (mode)
          {
            case Absolute: writeSync ("CURS MOD:ABSO\r\n"); break;
            case Delta:    writeSync ("CURS MOD:DELT\r\n"); break;
            default:       throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_SELECT:
        {
          final Tek2440_GPIB_Settings.CursorSelect select =
            (Tek2440_GPIB_Settings.CursorSelect) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_SELECT);
          switch (select)
          {
            case One: writeSync ("CURS SEL:ONE\r\n"); break;
            case Two: writeSync ("CURS SEL:TWO\r\n"); break;
            default:  throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_VOLTS:
        {
          final Tek2440_GPIB_Settings.CursorUnitVolts unit =
            (Tek2440_GPIB_Settings.CursorUnitVolts) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_VOLTS);
          switch (unit)
          {
            case Base:    writeSync ("CURS UNI:VOL:BAS\r\n");  break;
            case Percent: writeSync ("CURS UNI:VOL:PERC\r\n"); break;
            case dB:      writeSync ("CURS UNI:VOL:DB\r\n");   break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_REF_VOLTS:
        {
          final Tek2440_GPIB_Settings.RefVoltsUnit unit =
            (Tek2440_GPIB_Settings.RefVoltsUnit) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_REF_VOLTS);
          switch (unit)
          {
            case Divisions:    writeSync ("CURS REFV:UNI:DIV\r\n"); break;
            case Volts:        writeSync ("CURS REFV:UNI:V\r\n"); break;
            case VoltsSquared: writeSync ("CURS REFV:UNI:VV\r\n"); break;
            default:           throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_VALUE_REF_VOLTS:
        {
          final double value =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_VALUE_REF_VOLTS);
          writeSync ("CURS REFV:VAL:" + Double.toString (value) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_TIME:
        {
          final Tek2440_GPIB_Settings.CursorUnitTime unit =
            (Tek2440_GPIB_Settings.CursorUnitTime) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_TIME);
          switch (unit)
          {
            case Base:    writeSync ("CURS UNI:TIM:BAS\r\n");  break;
            case Percent: writeSync ("CURS UNI:TIM:PERC\r\n"); break;
            case Degrees: writeSync ("CURS UNI:TIM:DEG\r\n");  break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_REF_TIME:
        {
          final Tek2440_GPIB_Settings.RefTimeUnit unit =
            (Tek2440_GPIB_Settings.RefTimeUnit) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_REF_TIME);
          switch (unit)
          {
            case Seconds:    writeSync ("CURS REFT:UNI:SEC\r\n"); break;
            case ClockTicks: writeSync ("CURS REFT:UNI:CLK\r\n"); break;
            default:         throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_VALUE_REF_TIME:
        {
          final double value =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_VALUE_REF_TIME);
          writeSync ("CURS REFT:VAL:" + Double.toString (value) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_SLOPE:
        {
          final Tek2440_GPIB_Settings.CursorUnitSlope unit =
            (Tek2440_GPIB_Settings.CursorUnitSlope) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_SLOPE);
          switch (unit)
          {
            case Base:    writeSync ("CURS UNI:SLO:BAS\r\n");  break;
            case Percent: writeSync ("CURS UNI:SLO:PERC\r\n"); break;
            case dB:      writeSync ("CURS UNI:SLO:DB\r\n");   break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_REF_SLOPE_X:
        {
          final Tek2440_GPIB_Settings.RefSlopeXUnit unit =
            (Tek2440_GPIB_Settings.RefSlopeXUnit) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_REF_SLOPE_X);
          switch (unit)
          {
            case Divisions:    writeSync ("CURS REFS:XUN:DIV\r\n"); break;
            case Seconds:      writeSync ("CURS REFS:XUN:SEC\r\n"); break;
            case Volts:        writeSync ("CURS REFS:XUN:V\r\n");   break;
            case VoltsSquared: writeSync ("CURS REFS:XUN:VV\r\n");  break;
            case ClockTicks:   writeSync ("CURS REFS:XUN:CLK\r\n"); break;
            default:           throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_UNIT_REF_SLOPE_Y:
        {
          final Tek2440_GPIB_Settings.RefSlopeYUnit unit =
            (Tek2440_GPIB_Settings.RefSlopeYUnit) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_UNIT_REF_SLOPE_Y);
          switch (unit)
          {
            case Divisions:    writeSync ("CURS REFS:YUN:DIV\r\n"); break;
            case Volts:        writeSync ("CURS REFS:YUN:V\r\n");   break;
            case VoltsSquared: writeSync ("CURS REFS:YUN:VV\r\n");  break;
            default:           throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_VALUE_REF_SLOPE:
        {
          final double value =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_VALUE_REF_SLOPE);
          writeSync ("CURS REFS:VAL:" + Double.toString (value) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_ONE_X:
        {
          final double x =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_ONE_X);
          writeSync ("CURS XPO:ONE:" + Double.toString (x) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_ONE_Y:
        {
          final double y =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_ONE_Y);
          writeSync ("CURS YPO:ONE:" + Double.toString (y) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_ONE_T:
        {
          final double t =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_ONE_T);
          writeSync ("CURS TPO:ONE:" + Double.toString (t) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_TWO_X:
        {
          final double x =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_TWO_X);
          writeSync ("CURS XPO:TWO:" + Double.toString (x) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_TWO_Y:
        {
          final double y =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_TWO_Y);
          writeSync ("CURS YPO:TWO:" + Double.toString (y) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_CURSOR_POSITION_TWO_T:
        {
          final double t =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_CURSOR_POSITION_TWO_T);
          writeSync ("CURS TPO:TWO:" + Double.toString (t) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_METHOD:
        {
          final Tek2440_GPIB_Settings.MeasurementMethod method =
            (Tek2440_GPIB_Settings.MeasurementMethod) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_METHOD);
          switch (method)
          {
            case Cursor:    writeSync ("MEAS MET:CURS\r\n"); break;
            case Histogram: writeSync ("MEAS MET:HIS\r\n");  break;
            case MinMax:    writeSync ("MEAS MET:MINM\r\n"); break;
            default:        throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_WINDOWING:
        {
          final boolean windowing =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_WINDOWING);
          writeSync ("MEAS WIN:" + (windowing ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DISPLAY:
        {
          final boolean display =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DISPLAY);
          writeSync ("MEAS DISP:" + (display ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DISPLAY_MARKERS:
        {
          final boolean display =
            (boolean) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DISPLAY_MARKERS);
          writeSync ("MEAS MAR:" + (display ? "ON" : "OFF") + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL1_TYPE:
        {
          final Tek2440_GPIB_Settings.MeasurementType type =
            (Tek2440_GPIB_Settings.MeasurementType) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL1_TYPE);
          switch (type)
          {
            case Area:       writeSync ("MEAS ONE:TYP:ARE\r\n");  break;
            case Base:       writeSync ("MEAS ONE:TYP:BAS\r\n");  break;
            case DMesial:    writeSync ("MEAS ONE:TYP:DME\r\n");  break;
            case Delay:      writeSync ("MEAS ONE:TYP:DELA\r\n"); break;
            case Distal:     writeSync ("MEAS ONE:TYP:DIST\r\n"); break;
            case DutyCycle:  writeSync ("MEAS ONE:TYP:DUT\r\n");  break;
            case Fall:       writeSync ("MEAS ONE:TYP:FAL\r\n");  break;
            case Frequency:  writeSync ("MEAS ONE:TYP:FRE\r\n");  break;
            case Maximum:    writeSync ("MEAS ONE:TYP:MAX\r\n");  break;
            case Mean:       writeSync ("MEAS ONE:TYP:MEAN\r\n"); break;
            case Mesial:     writeSync ("MEAS ONE:TYP:MESI\r\n"); break;
            case Mid:        writeSync ("MEAS ONE:TYP:MID\r\n");  break;
            case Minimum:    writeSync ("MEAS ONE:TYP:MINI\r\n"); break;
            case Overshoot:  writeSync ("MEAS ONE:TYP:OVE\r\n");  break;
            case Peak2Peak:  writeSync ("MEAS ONE:TYP:PK2\r\n");  break;
            case Period:     writeSync ("MEAS ONE:TYP:PERI\r\n"); break;
            case Proximal:   writeSync ("MEAS ONE:TYP:PROX\r\n"); break;
            case RMS:        writeSync ("MEAS ONE:TYP:RMS\r\n");  break;
            case Rise:       writeSync ("MEAS ONE:TYP:RIS\r\n");  break;
            case Top:        writeSync ("MEAS ONE:TYP:TOP\r\n");  break;
            case Undershoot: writeSync ("MEAS ONE:TYP:UND\r\n");  break;
            case Width:      writeSync ("MEAS ONE:TYP:WID\r\n");  break;
            default:         throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL1_SOURCE:
        {
          final Tek2440_GPIB_Settings.MeasurementSource source =
            (Tek2440_GPIB_Settings.MeasurementSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL1_SOURCE);
          switch (source)
          {
            case Ch1:     writeSync ("MEAS ONE:SOU:CH1\r\n");   break;
            case Ch2:     writeSync ("MEAS ONE:SOU:CH2\r\n");   break;
            case Add:     writeSync ("MEAS ONE:SOU:ADD\r\n");   break;
            case Mult:    writeSync ("MEAS ONE:SOU:MUL\r\n");   break;
            case Ch1Del:  writeSync ("MEAS ONE:SOU:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("MEAS ONE:SOU:CH2D\r\n");  break;
            case AddDel:  writeSync ("MEAS ONE:SOU:ADDD\r\n");  break;
            case MultDel: writeSync ("MEAS ONE:SOU:MULTD\r\n"); break;
            case Ref1:    writeSync ("MEAS ONE:SOU:REF1\r\n");  break;
            case Ref2:    writeSync ("MEAS ONE:SOU:REF2\r\n");  break;
            case Ref3:    writeSync ("MEAS ONE:SOU:REF3\r\n");  break;
            case Ref4:    writeSync ("MEAS ONE:SOU:REF4\r\n");  break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL1_DSOURCE:
        {
          final Tek2440_GPIB_Settings.MeasurementSource source =
            (Tek2440_GPIB_Settings.MeasurementSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL1_DSOURCE);
          switch (source)
          {
            case Ch1:     writeSync ("MEAS ONE:DSO:CH1\r\n");   break;
            case Ch2:     writeSync ("MEAS ONE:DSO:CH2\r\n");   break;
            case Add:     writeSync ("MEAS ONE:DSO:ADD\r\n");   break;
            case Mult:    writeSync ("MEAS ONE:DSO:MUL\r\n");   break;
            case Ch1Del:  writeSync ("MEAS ONE:DSO:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("MEAS ONE:DSO:CH2D\r\n");  break;
            case AddDel:  writeSync ("MEAS ONE:DSO:ADDD\r\n");  break;
            case MultDel: writeSync ("MEAS ONE:DSO:MULTD\r\n"); break;
            case Ref1:    writeSync ("MEAS ONE:DSO:REF1\r\n");  break;
            case Ref2:    writeSync ("MEAS ONE:DSO:REF2\r\n");  break;
            case Ref3:    writeSync ("MEAS ONE:DSO:REF3\r\n");  break;
            case Ref4:    writeSync ("MEAS ONE:DSO:REF4\r\n");  break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL2_TYPE:
        {
          final Tek2440_GPIB_Settings.MeasurementType type =
            (Tek2440_GPIB_Settings.MeasurementType) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL2_TYPE);
          switch (type)
          {
            case Area:       writeSync ("MEAS TWO:TYP:ARE\r\n");  break;
            case Base:       writeSync ("MEAS TWO:TYP:BAS\r\n");  break;
            case DMesial:    writeSync ("MEAS TWO:TYP:DME\r\n");  break;
            case Delay:      writeSync ("MEAS TWO:TYP:DELA\r\n"); break;
            case Distal:     writeSync ("MEAS TWO:TYP:DIST\r\n"); break;
            case DutyCycle:  writeSync ("MEAS TWO:TYP:DUT\r\n");  break;
            case Fall:       writeSync ("MEAS TWO:TYP:FAL\r\n");  break;
            case Frequency:  writeSync ("MEAS TWO:TYP:FRE\r\n");  break;
            case Maximum:    writeSync ("MEAS TWO:TYP:MAX\r\n");  break;
            case Mean:       writeSync ("MEAS TWO:TYP:MEAN\r\n"); break;
            case Mesial:     writeSync ("MEAS TWO:TYP:MESI\r\n"); break;
            case Mid:        writeSync ("MEAS TWO:TYP:MID\r\n");  break;
            case Minimum:    writeSync ("MEAS TWO:TYP:MINI\r\n"); break;
            case Overshoot:  writeSync ("MEAS TWO:TYP:OVE\r\n");  break;
            case Peak2Peak:  writeSync ("MEAS TWO:TYP:PK2\r\n");  break;
            case Period:     writeSync ("MEAS TWO:TYP:PERI\r\n"); break;
            case Proximal:   writeSync ("MEAS TWO:TYP:PROX\r\n"); break;
            case RMS:        writeSync ("MEAS TWO:TYP:RMS\r\n");  break;
            case Rise:       writeSync ("MEAS TWO:TYP:RIS\r\n");  break;
            case Top:        writeSync ("MEAS TWO:TYP:TOP\r\n");  break;
            case Undershoot: writeSync ("MEAS TWO:TYP:UND\r\n");  break;
            case Width:      writeSync ("MEAS TWO:TYP:WID\r\n");  break;
            default:         throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL2_SOURCE:
        {
          final Tek2440_GPIB_Settings.MeasurementSource source =
            (Tek2440_GPIB_Settings.MeasurementSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL2_SOURCE);
          switch (source)
          {
            case Ch1:     writeSync ("MEAS TWO:SOU:CH1\r\n");   break;
            case Ch2:     writeSync ("MEAS TWO:SOU:CH2\r\n");   break;
            case Add:     writeSync ("MEAS TWO:SOU:ADD\r\n");   break;
            case Mult:    writeSync ("MEAS TWO:SOU:MUL\r\n");   break;
            case Ch1Del:  writeSync ("MEAS TWO:SOU:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("MEAS TWO:SOU:CH2D\r\n");  break;
            case AddDel:  writeSync ("MEAS TWO:SOU:ADDD\r\n");  break;
            case MultDel: writeSync ("MEAS TWO:SOU:MULTD\r\n"); break;
            case Ref1:    writeSync ("MEAS TWO:SOU:REF1\r\n");  break;
            case Ref2:    writeSync ("MEAS TWO:SOU:REF2\r\n");  break;
            case Ref3:    writeSync ("MEAS TWO:SOU:REF3\r\n");  break;
            case Ref4:    writeSync ("MEAS TWO:SOU:REF4\r\n");  break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL2_DSOURCE:
        {
          final Tek2440_GPIB_Settings.MeasurementSource source =
            (Tek2440_GPIB_Settings.MeasurementSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL2_DSOURCE);
          switch (source)
          {
            case Ch1:     writeSync ("MEAS TWO:DSO:CH1\r\n");   break;
            case Ch2:     writeSync ("MEAS TWO:DSO:CH2\r\n");   break;
            case Add:     writeSync ("MEAS TWO:DSO:ADD\r\n");   break;
            case Mult:    writeSync ("MEAS TWO:DSO:MUL\r\n");   break;
            case Ch1Del:  writeSync ("MEAS TWO:DSO:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("MEAS TWO:DSO:CH2D\r\n");  break;
            case AddDel:  writeSync ("MEAS TWO:DSO:ADDD\r\n");  break;
            case MultDel: writeSync ("MEAS TWO:DSO:MULTD\r\n"); break;
            case Ref1:    writeSync ("MEAS TWO:DSO:REF1\r\n");  break;
            case Ref2:    writeSync ("MEAS TWO:DSO:REF2\r\n");  break;
            case Ref3:    writeSync ("MEAS TWO:DSO:REF3\r\n");  break;
            case Ref4:    writeSync ("MEAS TWO:DSO:REF4\r\n");  break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL3_TYPE:
        {
          final Tek2440_GPIB_Settings.MeasurementType type =
            (Tek2440_GPIB_Settings.MeasurementType) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL3_TYPE);
          switch (type)
          {
            case Area:       writeSync ("MEAS THR:TYP:ARE\r\n");  break;
            case Base:       writeSync ("MEAS THR:TYP:BAS\r\n");  break;
            case DMesial:    writeSync ("MEAS THR:TYP:DME\r\n");  break;
            case Delay:      writeSync ("MEAS THR:TYP:DELA\r\n"); break;
            case Distal:     writeSync ("MEAS THR:TYP:DIST\r\n"); break;
            case DutyCycle:  writeSync ("MEAS THR:TYP:DUT\r\n");  break;
            case Fall:       writeSync ("MEAS THR:TYP:FAL\r\n");  break;
            case Frequency:  writeSync ("MEAS THR:TYP:FRE\r\n");  break;
            case Maximum:    writeSync ("MEAS THR:TYP:MAX\r\n");  break;
            case Mean:       writeSync ("MEAS THR:TYP:MEAN\r\n"); break;
            case Mesial:     writeSync ("MEAS THR:TYP:MESI\r\n"); break;
            case Mid:        writeSync ("MEAS THR:TYP:MID\r\n");  break;
            case Minimum:    writeSync ("MEAS THR:TYP:MINI\r\n"); break;
            case Overshoot:  writeSync ("MEAS THR:TYP:OVE\r\n");  break;
            case Peak2Peak:  writeSync ("MEAS THR:TYP:PK2\r\n");  break;
            case Period:     writeSync ("MEAS THR:TYP:PERI\r\n"); break;
            case Proximal:   writeSync ("MEAS THR:TYP:PROX\r\n"); break;
            case RMS:        writeSync ("MEAS THR:TYP:RMS\r\n");  break;
            case Rise:       writeSync ("MEAS THR:TYP:RIS\r\n");  break;
            case Top:        writeSync ("MEAS THR:TYP:TOP\r\n");  break;
            case Undershoot: writeSync ("MEAS THR:TYP:UND\r\n");  break;
            case Width:      writeSync ("MEAS THR:TYP:WID\r\n");  break;
            default:         throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL3_SOURCE:
        {
          final Tek2440_GPIB_Settings.MeasurementSource source =
            (Tek2440_GPIB_Settings.MeasurementSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL3_SOURCE);
          switch (source)
          {
            case Ch1:     writeSync ("MEAS THR:SOU:CH1\r\n");   break;
            case Ch2:     writeSync ("MEAS THR:SOU:CH2\r\n");   break;
            case Add:     writeSync ("MEAS THR:SOU:ADD\r\n");   break;
            case Mult:    writeSync ("MEAS THR:SOU:MUL\r\n");   break;
            case Ch1Del:  writeSync ("MEAS THR:SOU:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("MEAS THR:SOU:CH2D\r\n");  break;
            case AddDel:  writeSync ("MEAS THR:SOU:ADDD\r\n");  break;
            case MultDel: writeSync ("MEAS THR:SOU:MULTD\r\n"); break;
            case Ref1:    writeSync ("MEAS THR:SOU:REF1\r\n");  break;
            case Ref2:    writeSync ("MEAS THR:SOU:REF2\r\n");  break;
            case Ref3:    writeSync ("MEAS THR:SOU:REF3\r\n");  break;
            case Ref4:    writeSync ("MEAS THR:SOU:REF4\r\n");  break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL3_DSOURCE:
        {
          final Tek2440_GPIB_Settings.MeasurementSource source =
            (Tek2440_GPIB_Settings.MeasurementSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL3_DSOURCE);
          switch (source)
          {
            case Ch1:     writeSync ("MEAS THR:DSO:CH1\r\n");   break;
            case Ch2:     writeSync ("MEAS THR:DSO:CH2\r\n");   break;
            case Add:     writeSync ("MEAS THR:DSO:ADD\r\n");   break;
            case Mult:    writeSync ("MEAS THR:DSO:MUL\r\n");   break;
            case Ch1Del:  writeSync ("MEAS THR:DSO:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("MEAS THR:DSO:CH2D\r\n");  break;
            case AddDel:  writeSync ("MEAS THR:DSO:ADDD\r\n");  break;
            case MultDel: writeSync ("MEAS THR:DSO:MULTD\r\n"); break;
            case Ref1:    writeSync ("MEAS THR:DSO:REF1\r\n");  break;
            case Ref2:    writeSync ("MEAS THR:DSO:REF2\r\n");  break;
            case Ref3:    writeSync ("MEAS THR:DSO:REF3\r\n");  break;
            case Ref4:    writeSync ("MEAS THR:DSO:REF4\r\n");  break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL4_TYPE:
        {
          final Tek2440_GPIB_Settings.MeasurementType type =
            (Tek2440_GPIB_Settings.MeasurementType) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL4_TYPE);
          switch (type)
          {
            case Area:       writeSync ("MEAS FOU:TYP:ARE\r\n");  break;
            case Base:       writeSync ("MEAS FOU:TYP:BAS\r\n");  break;
            case DMesial:    writeSync ("MEAS FOU:TYP:DME\r\n");  break;
            case Delay:      writeSync ("MEAS FOU:TYP:DELA\r\n"); break;
            case Distal:     writeSync ("MEAS FOU:TYP:DIST\r\n"); break;
            case DutyCycle:  writeSync ("MEAS FOU:TYP:DUT\r\n");  break;
            case Fall:       writeSync ("MEAS FOU:TYP:FAL\r\n");  break;
            case Frequency:  writeSync ("MEAS FOU:TYP:FRE\r\n");  break;
            case Maximum:    writeSync ("MEAS FOU:TYP:MAX\r\n");  break;
            case Mean:       writeSync ("MEAS FOU:TYP:MEAN\r\n"); break;
            case Mesial:     writeSync ("MEAS FOU:TYP:MESI\r\n"); break;
            case Mid:        writeSync ("MEAS FOU:TYP:MID\r\n");  break;
            case Minimum:    writeSync ("MEAS FOU:TYP:MINI\r\n"); break;
            case Overshoot:  writeSync ("MEAS FOU:TYP:OVE\r\n");  break;
            case Peak2Peak:  writeSync ("MEAS FOU:TYP:PK2\r\n");  break;
            case Period:     writeSync ("MEAS FOU:TYP:PERI\r\n"); break;
            case Proximal:   writeSync ("MEAS FOU:TYP:PROX\r\n"); break;
            case RMS:        writeSync ("MEAS FOU:TYP:RMS\r\n");  break;
            case Rise:       writeSync ("MEAS FOU:TYP:RIS\r\n");  break;
            case Top:        writeSync ("MEAS FOU:TYP:TOP\r\n");  break;
            case Undershoot: writeSync ("MEAS FOU:TYP:UND\r\n");  break;
            case Width:      writeSync ("MEAS FOU:TYP:WID\r\n");  break;
            default:         throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL4_SOURCE:
        {
          final Tek2440_GPIB_Settings.MeasurementSource source =
            (Tek2440_GPIB_Settings.MeasurementSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL4_SOURCE);
          switch (source)
          {
            case Ch1:     writeSync ("MEAS FOU:SOU:CH1\r\n");   break;
            case Ch2:     writeSync ("MEAS FOU:SOU:CH2\r\n");   break;
            case Add:     writeSync ("MEAS FOU:SOU:ADD\r\n");   break;
            case Mult:    writeSync ("MEAS FOU:SOU:MUL\r\n");   break;
            case Ch1Del:  writeSync ("MEAS FOU:SOU:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("MEAS FOU:SOU:CH2D\r\n");  break;
            case AddDel:  writeSync ("MEAS FOU:SOU:ADDD\r\n");  break;
            case MultDel: writeSync ("MEAS FOU:SOU:MULTD\r\n"); break;
            case Ref1:    writeSync ("MEAS FOU:SOU:REF1\r\n");  break;
            case Ref2:    writeSync ("MEAS FOU:SOU:REF2\r\n");  break;
            case Ref3:    writeSync ("MEAS FOU:SOU:REF3\r\n");  break;
            case Ref4:    writeSync ("MEAS FOU:SOU:REF4\r\n");  break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_CHANNEL4_DSOURCE:
        {
          final Tek2440_GPIB_Settings.MeasurementSource source =
            (Tek2440_GPIB_Settings.MeasurementSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_CHANNEL4_DSOURCE);
          switch (source)
          {
            case Ch1:     writeSync ("MEAS FOU:DSO:CH1\r\n");   break;
            case Ch2:     writeSync ("MEAS FOU:DSO:CH2\r\n");   break;
            case Add:     writeSync ("MEAS FOU:DSO:ADD\r\n");   break;
            case Mult:    writeSync ("MEAS FOU:DSO:MUL\r\n");   break;
            case Ch1Del:  writeSync ("MEAS FOU:DSO:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("MEAS FOU:DSO:CH2D\r\n");  break;
            case AddDel:  writeSync ("MEAS FOU:DSO:ADDD\r\n");  break;
            case MultDel: writeSync ("MEAS FOU:DSO:MULTD\r\n"); break;
            case Ref1:    writeSync ("MEAS FOU:DSO:REF1\r\n");  break;
            case Ref2:    writeSync ("MEAS FOU:DSO:REF2\r\n");  break;
            case Ref3:    writeSync ("MEAS FOU:DSO:REF3\r\n");  break;
            case Ref4:    writeSync ("MEAS FOU:DSO:REF4\r\n");  break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_PROXIMAL_UNIT:
        {
          final Tek2440_GPIB_Settings.ProximalUnit unit =
            (Tek2440_GPIB_Settings.ProximalUnit) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_PROXIMAL_UNIT);
          switch (unit)
          {
            case Volts:     writeSync ("MEAS PROX:UNI:VOL\r\n");  break;
            case Percents:  writeSync ("MEAS PROX:UNI:PERC\r\n"); break;
            default:        throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_PROXIMAL_VOLTS_LEVEL:
        {
          final double level =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_PROXIMAL_VOLTS_LEVEL);
          writeSync ("MEAS PROX:VLE:" + Double.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_PROXIMAL_PERCENTS_LEVEL:
        {
          final double level =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_PROXIMAL_PERCENTS_LEVEL);
          writeSync ("MEAS PROX:PLE:" + Double.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_MESIAL_UNIT:
        {
          final Tek2440_GPIB_Settings.MesialUnit unit =
            (Tek2440_GPIB_Settings.MesialUnit) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_MESIAL_UNIT);
          switch (unit)
          {
            case Volts:     writeSync ("MEAS MESI:UNI:VOL\r\n");  break;
            case Percents:  writeSync ("MEAS MESI:UNI:PERC\r\n"); break;
            default:        throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_MESIAL_VOLTS_LEVEL:
        {
          final double level =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_MESIAL_VOLTS_LEVEL);
          writeSync ("MEAS MESI:VLE:" + Double.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_MESIAL_PERCENTS_LEVEL:
        {
          final double level =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_MESIAL_PERCENTS_LEVEL);
          writeSync ("MEAS MESI:PLE:" + Double.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DMESIAL_UNIT:
        {
          final Tek2440_GPIB_Settings.DMesialUnit unit =
            (Tek2440_GPIB_Settings.DMesialUnit) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DMESIAL_UNIT);
          switch (unit)
          {
            case Volts:     writeSync ("MEAS DME:UNI:VOL\r\n");  break;
            case Percents:  writeSync ("MEAS DME:UNI:PERC\r\n"); break;
            default:        throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DMESIAL_VOLTS_LEVEL:
        {
          final double level =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DMESIAL_VOLTS_LEVEL);
          writeSync ("MEAS DME:VLE:" + Double.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DMESIAL_PERCENTS_LEVEL:
        {
          final double level =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DMESIAL_PERCENTS_LEVEL);
          writeSync ("MEAS DME:PLE:" + Double.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DISTAL_UNIT:
        {
          final Tek2440_GPIB_Settings.DistalUnit unit =
            (Tek2440_GPIB_Settings.DistalUnit) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DISTAL_UNIT);
          switch (unit)
          {
            case Volts:     writeSync ("MEAS DIST:UNI:VOL\r\n");  break;
            case Percents:  writeSync ("MEAS DIST:UNI:PERC\r\n"); break;
            default:        throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DISTAL_VOLTS_LEVEL:
        {
          final double level =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DISTAL_VOLTS_LEVEL);
          writeSync ("MEAS DIST:VLE:" + Double.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_MEASUREMENT_DISTAL_PERCENTS_LEVEL:
        {
          final double level =
            (double) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_MEASUREMENT_DISTAL_PERCENTS_LEVEL);
          writeSync ("MEAS DIST:PLE:" + Double.toString (level) + "\r\n");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DATA_ENCODING:
        {
          final Tek2440_GPIB_Settings.DataEncoding encoding =
            (Tek2440_GPIB_Settings.DataEncoding) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DATA_ENCODING);
          switch (encoding)
          {
            case ASCII:     writeSync ("DAT ENC:ASC\r\n"); break;
            case RIBinary:  writeSync ("DAT ENC:RIB\r\n"); break;
            case RPBinary:  writeSync ("DAT ENC:RPB\r\n"); break;
            case RIPartial: writeSync ("DAT ENC:RIP\r\n"); break;
            case RPPartial: writeSync ("DAT ENC:RPP\r\n"); break;
            default:        throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DATA_SOURCE:
        {
          final Tek2440_GPIB_Settings.DataSource source =
            (Tek2440_GPIB_Settings.DataSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DATA_SOURCE);
          switch (source)
          {
            case Ch1:     writeSync ("DAT SOU:CH1\r\n");   break;
            case Ch2:     writeSync ("DAT SOU:CH2\r\n");   break;
            case Add:     writeSync ("DAT SOU:ADD\r\n");   break;
            case Mult:    writeSync ("DAT SOU:MUL\r\n");   break;
            case Ref1:    writeSync ("DAT SOU:REF1\r\n");  break;
            case Ref2:    writeSync ("DAT SOU:REF2\r\n");  break;
            case Ref3:    writeSync ("DAT SOU:REF3\r\n");  break;
            case Ref4:    writeSync ("DAT SOU:REF4\r\n");  break;
            case Ch1Del:  writeSync ("DAT SOU:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("DAT SOU:CH2D\r\n");  break;
            case AddDel:  writeSync ("DAT SOU:ADDD\r\n");  break;
            case MultDel: writeSync ("DAT SOU:MULTD\r\n"); break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DATA_DSOURCE:
        {
          final Tek2440_GPIB_Settings.DataSource source =
            (Tek2440_GPIB_Settings.DataSource) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DATA_DSOURCE);
          switch (source)
          {
            case Ch1:     writeSync ("DAT DSOU:CH1\r\n");   break;
            case Ch2:     writeSync ("DAT DSOU:CH2\r\n");   break;
            case Add:     writeSync ("DAT DSOU:ADD\r\n");   break;
            case Mult:    writeSync ("DAT DSOU:MUL\r\n");   break;
            case Ref1:    writeSync ("DAT DSOU:REF1\r\n");  break;
            case Ref2:    writeSync ("DAT DSOU:REF2\r\n");  break;
            case Ref3:    writeSync ("DAT DSOU:REF3\r\n");  break;
            case Ref4:    writeSync ("DAT DSOU:REF4\r\n");  break;
            case Ch1Del:  writeSync ("DAT DSOU:CH1D\r\n");  break;
            case Ch2Del:  writeSync ("DAT DSOU:CH2D\r\n");  break;
            case AddDel:  writeSync ("DAT DSOU:ADDD\r\n");  break;
            case MultDel: writeSync ("DAT DSOU:MULTD\r\n"); break;
            default:      throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case Tek2440_InstrumentCommand.IC_TEK2440_DATA_TARGET:
        {
          final Tek2440_GPIB_Settings.DataTarget target =
            (Tek2440_GPIB_Settings.DataTarget) instrumentCommand.get (
              Tek2440_InstrumentCommand.ICARG_TEK2440_DATA_TARGET);
          switch (target)
          {
            case Ref1: writeSync ("DAT TAR:REF1\r\n");  break;
            case Ref2: writeSync ("DAT TAR:REF2\r\n");  break;
            case Ref3: writeSync ("DAT TAR:REF3\r\n");  break;
            case Ref4: writeSync ("DAT TAR:REF4\r\n");  break;
            default:   throw new IllegalArgumentException ();
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
