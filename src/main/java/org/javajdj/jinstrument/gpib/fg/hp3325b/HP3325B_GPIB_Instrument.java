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

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.DefaultInstrumentCommand;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.FunctionGeneratorSettings;
import org.javajdj.jinstrument.gpib.fg.AbstractGpibFunctionGenerator;

/** Implementation of {@link Instrument} and {@link FunctionGenerator} for the HP-3325B.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP3325B_GPIB_Instrument
  extends AbstractGpibFunctionGenerator
  implements FunctionGenerator
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP3325B_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP3325B_GPIB_Instrument (final GpibDevice device)
  {
    super (
      "HP-3325B", // name
      device,     // device
      null,       // runnables
      null,       // targetServices
      true,       // addInitializationServices
      true,       // addStatusServices
      false,      // addSettingsServices XXX false for now...
      true,       // addCommandProcessorServices
      false,      // addAcquisitionServices
      false,      // addHousekeepingServices
      false);     // addServiceRequestPollingServices
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
      return "HP-3325B [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP3325B_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP3325B_GPIB_Instrument ((GpibDevice) device);
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
    return HP3325B_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
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
    settingsReadFromInstrument (getSettingsFromInstrumentSync ());    
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
    return new HP3325B_GPIB_Status (statusByte);
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
  public FunctionGeneratorSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    
    final Waveform waveform = getSettingsFromInstrumentSync_Waveform ();
    final double frequency_Hz = getSettingsFromInstrumentSync_Frequency_Hz ();
    final double amplitude_Vpp = getSettingsFromInstrumentSync_Amplitude_Vpp (waveform);
    final double dcOffset_V = getSettingsFromInstrumentSync_DCOffset_V ();
    
    return new HP3325B_GPIB_Settings (
      waveform,
      frequency_Hz,
      amplitude_Vpp,
      dcOffset_V);
  }
 
  private Waveform getSettingsFromInstrumentSync_Waveform ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String fuString = new String (writeAndReadEOISync ("FU?\n"), Charset.forName ("US-ASCII")).trim ();
    if (! fuString.startsWith ("FU"))
    {
      LOG.log (Level.WARNING, "Unexpected settings reading (prefix FU) from Instrument {0}: {1}!",
        new Object[]{this, fuString});
      throw new IOException ();
    }
    if (fuString.length () != 3) // "FU" + single digit.
    {
      LOG.log (Level.WARNING, "Illegal/Unexpected waveform reading (length) from Instrument {0}: {1}!",
        new Object[]{this, fuString});
      throw new IOException ();
    }
    final Waveform waveform;
    switch (fuString.substring (2))
    {
      case "0": waveform = Waveform.DC;        break;
      case "1": waveform = Waveform.SINE;      break;
      case "2": waveform = Waveform.SQUARE;    break;
      case "3": waveform = Waveform.TRIANGLE;  break;
      case "4": waveform = Waveform.RAMP_UP;   break;
      case "5": waveform = Waveform.RAMP_DOWN; break;
      default:
        LOG.log (Level.WARNING, "Illegal identifier for waveform from Instrument {0} in return String: {1}!",
          new Object[]{this, fuString});
        throw new IOException ();
    }
    return waveform;
  }
  
  private double getSettingsFromInstrumentSync_Frequency_Hz ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String frString = new String (writeAndReadEOISync ("FR?\n"), Charset.forName ("US-ASCII")).trim ();
    // Initial sanity checks.
    if (! frString.startsWith ("FR"))
    {
      LOG.log (Level.WARNING, "Unexpected settings reading (prefix FR) from Instrument {0}: {1}!",
        new Object[]{this, frString});
      throw new IOException ();
    }
    if (frString.length () < 5) // "FR" + single digit + two-char unit.
    {
      LOG.log (Level.WARNING, "Illegal/Unexpected frequency reading (length) from Instrument {0}: {1}!",
        new Object[]{this, frString});
      throw new IOException ();
    }
    final String frUnitString = frString.substring (frString.length () - 2, frString.length ());
    final int factor;
    switch (frUnitString)
    {
      case "HZ": factor =       1; break;
      case "KH": factor =    1000; break;
      case "MH": factor = 1000000; break;
      default:
        LOG.log (Level.WARNING, "Illegal unit for frequency from Instrument {0} in return String: {1}!",
          new Object[]{this, frString});
        throw new IOException ();
    }
    final double number;
    try
    {
      number = Double.parseDouble (frString.substring (2, frString.length () - 2));
    }
    catch (NumberFormatException nfe)
    {
      LOG.log (Level.WARNING, "Illegal number for frequency from Instrument {0} in return String: {1}!",
        new Object[]{this, frString});
      throw new IOException ();
    }
    final double frequency_Hz = factor * number;
    return frequency_Hz;
  }
  
  private double getSettingsFromInstrumentSync_Amplitude_Vpp (final Waveform waveform)
    throws IOException, InterruptedException, TimeoutException
  {
    final String amString = new String (writeAndReadEOISync ("AM?\n"), Charset.forName ("US-ASCII")).trim ();
    // Initial sanity checks.
    if (! amString.startsWith ("AM"))
    {
      LOG.log (Level.WARNING, "Unexpected settings reading (prefix AM) from Instrument {0}: {1}!",
        new Object[]{this, amString});
      throw new IOException ();
    }
    if (amString.length () < 5) // "AM" + single digit + two-char unit.
    {
      LOG.log (Level.WARNING, "Illegal/Unexpected amplitude reading (length) from Instrument {0}: {1}!",
        new Object[]{this, amString});
      throw new IOException ();
    }
    final double number;
    try
    {
      number = Double.parseDouble (amString.substring (2, amString.length () - 2));
    }
    catch (NumberFormatException nfe)
    {
      LOG.log (Level.WARNING, "Illegal number for amplitude from Instrument {0} in return String: {1}!",
        new Object[]{this, amString});
      throw new IOException ();
    }
    final String amUnitString = amString.substring (amString.length () - 2, amString.length ());
    final double amplitude_Vpp;
    switch (amUnitString)
    {
      case "VO": amplitude_Vpp = number; break; // Volts
      case "MV": amplitude_Vpp = 0.001 * number; break; // milliVolts
      case "VR": amplitude_Vpp = rmsToVpp (waveform, number); break; // Volts rms
      case "MR": amplitude_Vpp = rmsToVpp (waveform, 0.001 * number); break; // milliVolts rms
      case "DB": amplitude_Vpp = rmsToVpp (waveform, Math.sqrt (0.001 * Math.pow (10, number / 10) * 50)); break; // dBm [50 Ohms]
      case "DV": amplitude_Vpp = rmsToVpp (waveform, Math.pow (10, number / 20)); break; // dBVrms
      default:
        LOG.log (Level.WARNING, "Illegal unit for amplitude from Instrument {0} in return String: {1}!",
          new Object[]{this, amString});
        throw new IOException ();
    }
    return amplitude_Vpp;
  }
  
  private double getSettingsFromInstrumentSync_DCOffset_V ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String ofString = new String (writeAndReadEOISync ("OF?\n"), Charset.forName ("US-ASCII")).trim ();
    // Initial sanity checks.
    if (! ofString.startsWith ("OF"))
    {
      LOG.log (Level.WARNING, "Unexpected settings reading (prefix OF) from Instrument {0}: {1}!",
        new Object[]{this, ofString});
      throw new IOException ();
    }
    if (ofString.length () < 5) // "OF" + single digit + two-char unit.
    {
      LOG.log (Level.WARNING, "Illegal/Unexpected DC offset reading (length) from Instrument {0}: {1}!",
        new Object[]{this, ofString});
      throw new IOException ();
    }
    final double number;
    try
    {
      number = Double.parseDouble (ofString.substring (2, ofString.length () - 2));
    }
    catch (NumberFormatException nfe)
    {
      LOG.log (Level.WARNING, "Illegal number for DC offset from Instrument {0} in return String: {1}!",
        new Object[]{this, ofString});
      throw new IOException ();
    }
    final String dcOffsetUnitString = ofString.substring (ofString.length () - 2, ofString.length ());
    final double dcOffset_V;
    switch (dcOffsetUnitString)
    {
      case "VO": dcOffset_V = number; break; // Volts
      case "MV": dcOffset_V = 0.001 * number; break; // milliVolts
      default:
        LOG.log (Level.WARNING, "Illegal unit for DC offset from Instrument {0} in return String: {1}!",
          new Object[]{this, ofString});
        throw new IOException ();
    }
    return dcOffset_V;
  }
  
  private double rmsToVpp (final Waveform waveform, final double Vrms)
  {
    final double Vpp;
    switch (waveform)
    {
      // XXX TO BE TESTED!!
      case DC:       return Vrms; // XXX REALLY??
      case SINE:     return 2 * Math.sqrt (2) * Vrms;
      case SQUARE:   return 2 * Vrms;
      case TRIANGLE: 
      case RAMP_UP:
      case RAMP_DOWN: return 2 * Math.sqrt (3) * Vrms;
      default:
        throw new RuntimeException ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    throw new UnsupportedOperationException ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractGpibInstrument
  // ON GPIB SERVICE REQUEST FROM INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected void onGpibServiceRequestFromInstrument (final Byte statusByte)
    throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FunctionGenerator
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final boolean supportsOutputEnable ()
  {
    return false;
  }

  @Override
  public void setOutputEnable (final boolean outputEnable)
    throws IOException, InterruptedException
  {
    throw new UnsupportedOperationException ();
  }
  
  private final static Waveform[] SUPPORTED_WAVEFORMS = new Waveform[]
  {
    Waveform.DC,
    Waveform.SINE,
    Waveform.SQUARE,
    Waveform.TRIANGLE,
    Waveform.RAMP_UP,
    Waveform.RAMP_DOWN
  };
  
  private final static List<Waveform> SUPPORTED_WAVEFORMS_AS_LIST
    = Collections.unmodifiableList (Arrays.asList (HP3325B_GPIB_Instrument.SUPPORTED_WAVEFORMS));
  
  @Override
  public final List<Waveform> getSupportedWaveforms ()
  {
    return HP3325B_GPIB_Instrument.SUPPORTED_WAVEFORMS_AS_LIST;
  }
  
  @Override
  public void setWaveform (final Waveform waveform)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_WAVEFORM,
      InstrumentCommand.ICARG_WAVEFORM, waveform));
  }

  @Override
  public final double getMinFrequency_Hz ()
  {
    return 1E-6; // 1 muHz; Sine/Square/Triangle/Ramps
  }

  @Override
  public final double getMaxFrequency_Hz ()
  {
    return 29999999.999; // 29.999999999 MHz Sine / 10.999999999 MHz Square/Triangle/Ramps
  }
  
  @Override
  public void setFrequency_Hz (final double frequency_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_LF_FREQUENCY,
      InstrumentCommand.ICARG_LF_FREQUENCY_HZ, frequency_Hz));
  }

  @Override
  public final double getMinAmplitude_Vpp ()
  {
    return 0.001;
  }

  @Override
  public final double getMaxAmplitude_Vpp ()
  {
    return 10;
  }

  @Override
  public final void setAmplitude_Vpp (final double amplitude_Vpp)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_AMPLITUDE,
      InstrumentCommand.ICARG_AMPLITUDE_VPP, amplitude_Vpp));
  }

  @Override
  public final boolean supportsDCOffset ()
  {
    return true;
  }

  @Override
  public final double getMinDCOffset_V ()
  {
    return -5;
  }

  @Override
  public final double getMaxDCOffset_V ()
  {
    return 5;
  }

  @Override
  public final void setDCOffset_V (final double dcOffset_V)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_DC_OFFSET,
      InstrumentCommand.ICARG_DC_OFFSET_V, dcOffset_V));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP3325B_GPIB_Instrument
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
        case InstrumentCommand.IC_WAVEFORM:
        {
          final Waveform waveform = (Waveform) instrumentCommand.get (InstrumentCommand.ICARG_WAVEFORM);
          switch (waveform)
          {
            case DC:        writeSync ("FU0\r\n"); break;
            case SINE:      writeSync ("FU1\r\n"); break;
            case SQUARE:    writeSync ("FU2\r\n"); break;
            case TRIANGLE:  writeSync ("FU3\r\n"); break;
            case RAMP_UP:   writeSync ("FU4\r\n"); break;
            case RAMP_DOWN: writeSync ("FU5\r\n"); break;
            default: throw new UnsupportedOperationException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_LF_FREQUENCY:
        {
          final double frequency_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_LF_FREQUENCY_HZ);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (3);
          writeSync ("FR" + df.format (frequency_Hz) + "HZ\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_AMPLITUDE:
        {
          final double amplitude_Vpp = (double) instrumentCommand.get (InstrumentCommand.ICARG_AMPLITUDE_VPP);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (3);
          writeSync ("AM" + df.format (amplitude_Vpp) + "VO\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case InstrumentCommand.IC_DC_OFFSET:
        {
          final double dcOffset_V = (double) instrumentCommand.get (InstrumentCommand.ICARG_DC_OFFSET_V);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (1);
          writeSync ("OF" + df.format (dcOffset_V * 1000) + "MV\r");
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
