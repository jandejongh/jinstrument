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
      settingsBytes = writeAndReadEOISync ("LLSET?\n");
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
      final byte[] debugSettingsBytes = writeAndReadEOISync ("LLSET?\n");
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
    if (settings.isVModeChannel1 ())
    {
      final byte[] data;
      // XXX This changes the settings??
      data = writeAndReadEOISync ("DAT SOU:CH1;CURVE?\n");
      final Tek2440_GPIB_Trace reading = new Tek2440_GPIB_Trace (settings, Tek2440Channel.Channel1, data);
      if (! settings.isVModeChannel2 ())
        return reading;
      else
        readingReadFromInstrument (reading);
    }
    if (settings.isVModeChannel2 ())
    {
      final byte[] data;
      data = writeAndReadEOISync ("DAT SOU:CH2;CURVE?\n");
      final Tek2440_GPIB_Trace reading = new Tek2440_GPIB_Trace (settings, Tek2440Channel.Channel2, data);
      return reading;
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
  
  public void setChannelTimebase (
    final Tek2440Channel channel,
    final Tek2440_GPIB_Settings.SecondsPerDivision secondsPerDivision)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_TIMEBASE,
      InstrumentCommand.ICARG_TIMEBASE_CHANNEL, channel,
      InstrumentCommand.ICARG_TIMEBASE, secondsPerDivision));
  }
  
  public void setChannelEnable (
    final Tek2440Channel channel,
    final boolean channelEnable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_CHANNEL_ENABLE,
      InstrumentCommand.ICARG_CHANNEL_ENABLE_CHANNEL, channel,
      InstrumentCommand.ICARG_CHANNEL_ENABLE, channelEnable));
  }
  
  public void setVoltsPerDiv (
    final Tek2440Channel channel,
    final Tek2440_GPIB_Settings.VoltsPerDivision voltsPerDivision)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_VOLTS_PER_DIV,
      InstrumentCommand.ICARG_VOLTS_PER_DIV_CHANNEL, channel,
      InstrumentCommand.ICARG_VOLTS_PER_DIV, voltsPerDivision));
  }
  
  public void   setChannelVariableY (
    final Tek2440Channel channel,
    final double variableY)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_CHANNEL_VARIABLE_Y,
      InstrumentCommand.ICARG_CHANNEL_VARIABLE_Y_CHANNEL, channel,
      InstrumentCommand.ICARG_CHANNEL_VARIABLE_Y, variableY));
  }

  public void setChannelCoupling (
    final Tek2440Channel channel,
    final Tek2440_GPIB_Settings.ChannelCoupling channelCoupling)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_CHANNEL_COUPLING,
      InstrumentCommand.ICARG_CHANNEL_COUPLING_CHANNEL, channel,
      InstrumentCommand.ICARG_CHANNEL_COUPLING, channelCoupling));
  }
  
  public void setChannelFiftyOhms (
    final Tek2440Channel channel,
    final boolean fiftyOhms)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_CHANNEL_50_OHMS,
      InstrumentCommand.ICARG_CHANNEL_50_OHMS_CHANNEL, channel,
      InstrumentCommand.ICARG_CHANNEL_50_OHMS, fiftyOhms));
  }
  
  public void setChannelInvert (
    final Tek2440Channel channel,
    final boolean invert)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_CHANNEL_INVERT,
      InstrumentCommand.ICARG_CHANNEL_INVERT_CHANNEL, channel,
      InstrumentCommand.ICARG_CHANNEL_INVERT, invert));
  }
  
  public void setChannelPosition (
    final Tek2440Channel channel,
    final double position)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_CHANNEL_POSITION,
      InstrumentCommand.ICARG_CHANNEL_POSITION_CHANNEL, channel,
      InstrumentCommand.ICARG_CHANNEL_POSITION, position));
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
      InstrumentCommand.IC_DISPLAY_INTENSITY,
      InstrumentCommand.ICARG_DISPLAY_INTENSITY_COMPONENT, component,
      InstrumentCommand.ICARG_DISPLAY_INTENSITY, intensity));
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
        case InstrumentCommand.IC_TIMEBASE:
        {
          final Tek2440Channel channel = (Tek2440Channel) instrumentCommand.get (InstrumentCommand.ICARG_TIMEBASE_CHANNEL);
          final Tek2440_GPIB_Settings.SecondsPerDivision secondsPerDivision =
            (Tek2440_GPIB_Settings.SecondsPerDivision) instrumentCommand.get (InstrumentCommand.ICARG_TIMEBASE);
          switch (channel)
          {
            case Channel1:
              writeSync ("HOR ASE:" + secondsPerDivision.getSecondsPerDivision_s ()  + "\r\n");
              break;
            case Channel2:
              writeSync ("HOR BSE:" + secondsPerDivision.getSecondsPerDivision_s ()  + "\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_VOLTS_PER_DIV:
        {
          final Tek2440Channel channel = (Tek2440Channel) instrumentCommand.get (InstrumentCommand.ICARG_VOLTS_PER_DIV_CHANNEL);
          final Tek2440_GPIB_Settings.VoltsPerDivision voltsPerDivision =
            (Tek2440_GPIB_Settings.VoltsPerDivision) instrumentCommand.get (InstrumentCommand.ICARG_VOLTS_PER_DIV);
          switch (channel)
          {
            case Channel1:
              writeSync ("CH1 VOL:" + voltsPerDivision.getVoltsPerDivision_V () + "\r\n");
              break;
            case Channel2:
              writeSync ("CH2 VOL:" + voltsPerDivision.getVoltsPerDivision_V () + "\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_CHANNEL_VARIABLE_Y:
        {
          final Tek2440Channel channel = (Tek2440Channel) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_VARIABLE_Y_CHANNEL);
          final double variableY =
            (double) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_VARIABLE_Y);
          switch (channel)
          {
            case Channel1:
              writeSync ("CH1 VAR:" + Double.toString (variableY) + "\r\n");
              break;
            case Channel2:
              writeSync ("CH2 VAR:" + Double.toString (variableY) + "\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_CHANNEL_ENABLE:
        {
          final Tek2440Channel channel = (Tek2440Channel) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_ENABLE_CHANNEL);
          final boolean channelEnable =
            (boolean) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_ENABLE);
          switch (channel)
          {
            case Channel1:
              writeSync ("VMO CH1:" + (channelEnable ? "ON" : "OFF") + "\r\n");
              break;
            case Channel2:
              writeSync ("VMO CH2:" + (channelEnable ? "ON" : "OFF") + "\r\n");
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_CHANNEL_COUPLING:
        {
          final Tek2440Channel channel = (Tek2440Channel) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_COUPLING_CHANNEL);
          final Tek2440_GPIB_Settings.ChannelCoupling channelCoupling =
            (Tek2440_GPIB_Settings.ChannelCoupling) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_COUPLING);
          switch (channel)
          {
            case Channel1:
              switch (channelCoupling)
              {
                case AC:  writeSync ("CH1 COU:AC\r\n");  break;
                case DC:  writeSync ("CH1 COU:DC\r\n");  break;
                case GND: writeSync ("CH1 COU:GND\r\n"); break;
                default:
                  throw new IllegalArgumentException ();
              }
              break;
            case Channel2:
              switch (channelCoupling)
              {
                case AC:  writeSync ("CH2 COU:AC\r\n");  break;
                case DC:  writeSync ("CH2 COU:DC\r\n");  break;
                case GND: writeSync ("CH2 COU:GND\r\n"); break;
                default:
                  throw new IllegalArgumentException ();
              }
              break;
            default:
              throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_CHANNEL_50_OHMS:
        {
          final Tek2440Channel channel = (Tek2440Channel) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_50_OHMS_CHANNEL);
          final boolean channelFiftyOhms =
            (boolean) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_50_OHMS);
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
        case InstrumentCommand.IC_CHANNEL_INVERT:
        {
          final Tek2440Channel channel = (Tek2440Channel) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_INVERT_CHANNEL);
          final boolean channelInvert =
            (boolean) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_INVERT);
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
        case InstrumentCommand.IC_CHANNEL_POSITION:
        {
          final Tek2440Channel channel = (Tek2440Channel) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_POSITION_CHANNEL);
          final double channelPosition =
            (double) instrumentCommand.get (InstrumentCommand.ICARG_CHANNEL_POSITION);
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
        case InstrumentCommand.IC_DISPLAY_INTENSITY:
        {
          final DisplayIntensityComponent component =
            (DisplayIntensityComponent) instrumentCommand.get (InstrumentCommand.ICARG_DISPLAY_INTENSITY_COMPONENT);
          if (component == null)
            throw new IllegalArgumentException ();
          final Double intensity = (Double) instrumentCommand.get (InstrumentCommand.ICARG_DISPLAY_INTENSITY);
          if (intensity == null || intensity < 0 || intensity > 100)
            throw new IllegalArgumentException ();
          final String componentString;
          switch (component)
          {
            case DISPLAY:          componentString = "DISP:";   break;
            case GRATICULE:        componentString = "GRA:";    break;
            case INTENSIFIED_ZONE: componentString = "INTENS:"; break;
            case READOUT:          componentString = "REA:";    break;
            default:
              throw new IllegalArgumentException ();
          }
          writeSync ("INTENSI " + componentString + Double.toString (intensity) + "\r\n");
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
