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
package org.javajdj.jinstrument.gpib.slm.rs_esh3;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultInstrumentCommand;
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
import org.javajdj.jinstrument.SelectiveLevelMeter;
import org.javajdj.jinstrument.gpib.slm.AbstractGpibSelectiveLevelMeter;

/** Implementation of {@link Instrument} and {@link SelectiveLevelMeter} for the Rohde{@code &}Schwarz ESH-3.
 *
 * <p>
 * Note that the implementation is quite limited because the R{@code &}S ESH-3
 * does not support reading instrument settings over GPIB.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class RS_ESH3_GPIB_Instrument
  extends AbstractGpibSelectiveLevelMeter
  implements SelectiveLevelMeter
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (RS_ESH3_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public RS_ESH3_GPIB_Instrument (final GpibDevice device)
  {
    super (
      "R&S ESH-3", // final String name
      device,      // final GpibDevice device
      null,        // final List<Runnable> runnables
      null,        // final List<Service> targetServices
      true,        // final boolean addInitializationServices
      false,       // final boolean addStatusServices
      false,       // final boolean addSettingsServices
      true,        // final boolean addCommandProcessorServices
      false,       // final boolean addAcquisitionServices
      false,       // final boolean addHousekeepingServices
      true);       // final boolean addServiceRequestPollingServices
    // XXX
    setGpibInstrumentServiceRequestCollectorPeriod_s (1.0);
    setHousekeeperPeriod_s (10.0);
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
      return "R&S ESH-3 [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final RS_ESH3_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new RS_ESH3_GPIB_Instrument ((GpibDevice) device);
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
    return RS_ESH3_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
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
    selectedDeviceCLearSync ();
    // XXX
    writeSync ("U1,P1,J1,WTJINSTR PA3GYF\r");
    settingsReadFromInstrument (RS_ESH3_GPIB_Settings.powerOnSettings ());
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
    final RS_ESH3_GPIB_Status status = new RS_ESH3_GPIB_Status (statusByte);
    fireInstrumentStatusChanged (status);
    if (status.isReady ())
    {
      // This is where we must read a line from the ESH-3.
      // Could be an instrument reading, but does not have to...
      // 20211227: I've noted that we get a SRQ for each line.
      processSrqEoiString (readEOISync ());
    }
  }
  
  protected void processSrqEoiString (final byte[] bytes)
  {
    // Initial sanity checks.
    if (bytes == null || bytes.length < 3)
    {
      LOG.log (Level.WARNING, "Illegal/Unexpected reading (length) from Instrument {0}: {1}!",
        new Object[]{this, bytes});
      return;
    }
    // We expect ASCII characters.
    try
    {
      final String asciiString = new String (bytes, 0, bytes.length, "ASCII");
      switch (asciiString.substring (0, 2))
      {
        case "FR":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Frequency Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double frequency_Mhz;
          try
          {
            frequency_Mhz = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Frequency Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.Frequency_MHz,
            frequency_Mhz));
          break;
        }
        case "VL":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Level Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double level_dB;
          try
          {
            level_dB = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Level Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.Level_dB,
            level_dB));
          break;
        }
        case "VN":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Voltage Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double voltage_muV;
          try
          {
            voltage_muV = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Voltage Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.Voltage_muV,
            voltage_muV));
          break;
        }
        case "VM":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Power [dBm] Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double power_dBm;
          try
          {
            power_dBm = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Power [dBm] Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.Power_dBm,
            power_dBm));
          break;
        }
        case "CL":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Current [dBmuA] Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double level_dB;
          try
          {
            level_dB = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Current [dBmuA] Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.Current_dBmuA,
            level_dB));
          break;
        }          
        case "CN":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Current [muA] Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double current_muA;
          try
          {
            current_muA = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Current [muA] Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.Current_muA,
            current_muA));
          break;          
        }
        case "EL":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Electric Field Strength [dBmuV/m] Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double level_dBmuVpm;
          try
          {
            level_dBmuVpm = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Electric Field Strength [dBmuV/m] Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.EFieldStrength_dBmuVpm,
            level_dBmuVpm));
          break;                    
        }
        case "EN":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Electric Field Strength [muV/m] Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double eFieldStrength_muVpm;
          try
          {
            eFieldStrength_muVpm = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Electric Field Strength [muV/m] Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.EFieldStrength_muVpm,
            eFieldStrength_muVpm));
          break;                    
        }
        case "ML":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Magnetic Field Strength [dBmuA/m] Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double level_dBmuApm;
          try
          {
            level_dBmuApm = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Magnetic Field Strength [dBmuA/m] Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.HFieldStrength_dBmuApm,
            level_dBmuApm));
          break;                    
        }
        case "MN":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Magnetic Field Strength [muA/m] Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double hFieldStrength_muApm;
          try
          {
            hFieldStrength_muApm = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Magnetic Field Strength [muA/m] Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.HFieldStrength_muApm,
            hFieldStrength_muApm));
          break;                    
        }
        case "AM":
        case "AP":
        case "AN":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Demodulation Depth Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double demodulationDepth_Percent;
          try
          {
            demodulationDepth_Percent = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Demodulation Depth Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          final RS_ESH3_GPIB_Reading.ReadingType readingType;
          switch (asciiString.substring (0, 2))
          {
            case "AM": readingType = RS_ESH3_GPIB_Reading.ReadingType.ModulationDepth_Percent;             break;
            case "AP": readingType = RS_ESH3_GPIB_Reading.ReadingType.ModulationDepthPositivePeak_Percent; break;
            case "AN": readingType = RS_ESH3_GPIB_Reading.ReadingType.ModulationDepthNegativePeak_Percent; break;
            default:   throw new RuntimeException ();
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            readingType,
            demodulationDepth_Percent));
          break;
        }
        case "OS":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Frequency Offset Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double frequencyOffset_Mhz;
          try
          {
            frequencyOffset_Mhz = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Frequency Offset Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            RS_ESH3_GPIB_Reading.ReadingType.FrequencyOffset_kHz,
            frequencyOffset_Mhz));
          break;
        }
        case "DF":
        case "DP":
        case "DN":
        {
          if (! RS_ESH3_GPIB_Reading.ReadingStatusType.isValidDefiningChar (asciiString.charAt (2)))
          {
            LOG.log (Level.WARNING, "Frequency Deviation Reading Status Type Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.charAt (2)});
            return;
          }
          final RS_ESH3_GPIB_Reading.ReadingStatusType readingStatusType =
            RS_ESH3_GPIB_Reading.ReadingStatusType.fromDefiningChar (asciiString.charAt (2));
          final double frequencyDeviation_kHz;
          try
          {
            frequencyDeviation_kHz = Double.parseDouble (asciiString.substring (3));
          }
          catch (NumberFormatException nfe)
          {
            LOG.log (Level.WARNING, "Parsing Frequency Deviation Reading Value Failed on Instrument {0}: {1}!",
              new Object[]{this, asciiString.substring (3)});
            return;            
          }
          final RS_ESH3_GPIB_Reading.ReadingType readingType;
          switch (asciiString.substring (0, 2))
          {
            case "DF": readingType = RS_ESH3_GPIB_Reading.ReadingType.FrequencyDeviation_kHz;             break;
            case "DP": readingType = RS_ESH3_GPIB_Reading.ReadingType.FrequencyDeviationPositivePeak_kHz; break;
            case "DN": readingType = RS_ESH3_GPIB_Reading.ReadingType.FrequencyDeviationNegativePeak_kHz; break;
            default:   throw new RuntimeException ();
          }
          readingReadFromInstrument (new RS_ESH3_GPIB_Reading (
            (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (),
            readingStatusType,
            readingType,
            frequencyDeviation_kHz));
          break;
        }
        default:
          LOG.log (Level.WARNING, "Illegal/Unsupported reading (prefix) from Instrument {0}: {1}!",
            new Object[]{this, asciiString});
      }
    }
    catch (UnsupportedEncodingException uee)
    {
      LOG.log (Level.WARNING, "Illegal/Unexpected reading (encoding) from Instrument {0}: {1}!",
        new Object[]{this, bytes});
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SelectiveLevelMeter
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final double getMinFrequency_Hz ()
  {
    return 9E3;
  }

  @Override
  public final double getMaxFrequency_Hz ()
  {
    return 29.9999E6;
  }

  private double inRangeFrequency_Hz (final double frequency_Hz)
  {
    return Math.max (getMinFrequency_Hz (), Math.min (getMaxFrequency_Hz (), frequency_Hz));
  }
  
  @Override
  public final double getHighestFrequencyResolution_Hz ()
  {
    return 100.0;
  }

  @Override
  public void setFrequency_Hz (final double frequency_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_LF_FREQUENCY,
      InstrumentCommand.ICARG_LF_FREQUENCY_HZ, frequency_Hz));
  }

  public void setFrequencyStart_Hz (final double frequency_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_FREQUENCY_START,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_FREQUENCY_START, frequency_Hz));
  }

  public void setFrequencyStop_Hz (final double frequency_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_FREQUENCY_STOP,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_FREQUENCY_STOP, frequency_Hz));
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RS_ESH3_GPIB_Instrument
  // COMMANDS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  public void setAttenuationMode (final RS_ESH3_GPIB_Settings.AttenuationMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_ATTENUATION_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_ATTENUATION_MODE, mode));
  }
  
  public void setRfAttenuation_dB (final double attenuation_dB)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_RF_ATTENUATION,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RF_ATTENUATION, attenuation_dB));
  }
  
  public void setLinearityTest (final boolean test)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_LINEARITY_TEST,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_LINEARITY_TEST, test));    
  }
  
  public void setIfAttenuation_dB (final double attenuation_dB)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_IF_ATTENUATION,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_IF_ATTENUATION, attenuation_dB));
  }
  
  public void setIfBandwidth (final RS_ESH3_GPIB_Settings.IfBandwidth bandwidth)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_IF_BANDWIDTH,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_IF_BANDWIDTH, bandwidth));
  }
  
  public void setDemodulationMode (final RS_ESH3_GPIB_Settings.DemodulationMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_DEMODULATION_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_DEMODULATION_MODE, mode));
  }
  
  public void calibrateCheck ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_CALIBRATE_CHECK));
  }
  
  public void calibrateTotal ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_CALIBRATE_TOTAL));
  }
  
  public void setMaxMinMode (final boolean mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_MAX_MIN_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_MAX_MIN_MODE, mode));    
  }
  
  public void setOperatingRange (final RS_ESH3_GPIB_Settings.OperatingRange range)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_OPERATING_RANGE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_OPERATING_RANGE, range));
  }
  
  public void setOperatingMode (final RS_ESH3_GPIB_Settings.OperatingMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_OPERATING_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_OPERATING_MODE, mode));
  }
  
  public void setIndicatingMode (final RS_ESH3_GPIB_Settings.IndicatingMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_INDICATING_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_INDICATING_MODE, mode));
  }
  
  public void setDataOutputMode (final RS_ESH3_GPIB_Settings.DataOutputMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_DATA_OUTPUT_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_DATA_OUTPUT_MODE, mode));
  }
  
  public void setMeasurementTime_s (final double time_s)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_MEASUREMENT_TIME,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_MEASUREMENT_TIME, time_s));
  }
  
  public void setMinimumLevel_dB (final double level_dB)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_MINIMUM_LEVEL,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_MINIMUM_LEVEL, level_dB));
  }
  
  public void setMaximumLevel_dB (final double level_dB)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_MAXIMUM_LEVEL,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_MAXIMUM_LEVEL, level_dB));
  }
  
  public void scanningRun ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SCANNING_RUN,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SCANNING_RUN, null));
  }
  
  public void scanningMemoryRun (final int[] slots)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SCANNING_RUN,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SCANNING_RUN, slots));
  }
  
  public void scanningStopInterrupt ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SCANNING_STOP_INTERRUPT));
  }
  
  public void scanningStopReset ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SCANNING_STOP_RESET));
  }
  
  public void setStepSizeMode (final RS_ESH3_GPIB_Settings.StepSizeMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF52_SF53_STEP_SIZE_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF52_SF53_STEP_SIZE_MODE, mode));
  }
  
  public void setStepSize_MHz (final double stepSize_MHz)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_STEP_SIZE_MHZ,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_STEP_SIZE_MHZ, stepSize_MHz));
  }
  
  public void setStepSize_Percent (final double stepSize_Percent)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_STEP_SIZE_PERCENT,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_STEP_SIZE_PERCENT, stepSize_Percent));
  }
  
  public void sfClear ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF00_SF_CLEAR));    
  }
  
  public void setTestLevel (final boolean test)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF10_SF11_TEST_LEVEL,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF10_SF11_TEST_LEVEL, test));    
  }
  
  public void setTestModulationDepth (final boolean test)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF20_SF21_TEST_MODULATION_DEPTH,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF20_SF21_TEST_MODULATION_DEPTH, test));    
  }
  
  public void setTestModulationDepthPositivePeak (final boolean test)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF22_SF23_TEST_MODULATION_DEPTH_POSITIVE_PEAK,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF22_SF23_TEST_MODULATION_DEPTH_POSITIVE_PEAK, test));    
  }
  
  public void setTestModulationDepthNegativePeak (final boolean test)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF24_SF25_TEST_MODULATION_DEPTH_NEGATIVE_PEAK,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF24_SF25_TEST_MODULATION_DEPTH_NEGATIVE_PEAK, test));    
  }
  
  public void setTestFrequencyOffset (final boolean test)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF30_SF31_TEST_FREQUENCY_OFFSET,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF30_SF31_TEST_FREQUENCY_OFFSET, test));    
  }
  
  public void setTestFrequencyDeviation (final boolean test)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF40_SF41_TEST_FREQUENCY_DEVIATION,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF40_SF41_TEST_FREQUENCY_DEVIATION, test));    
  }
  
  public void setTestFrequencyDeviationPositivePeak (final boolean test)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF42_SF43_TEST_FREQUENCY_DEVIATION_POSITIVE_PEAK,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF42_SF43_TEST_FREQUENCY_DEVIATION_POSITIVE_PEAK, test));    
  }
  
  public void setTestFrequencyDeviationNegativePeak (final boolean test)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF44_SF45_TEST_FREQUENCY_DEVIATION_NEGATIVE_PEAK,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF44_SF45_TEST_FREQUENCY_DEVIATION_NEGATIVE_PEAK, test));    
  }
  
  public void setFrequencyScanRepeatMode (final RS_ESH3_GPIB_Settings.FrequencyScanRepeatMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF50_SF51_FREQUENCY_SCAN_REPEAT_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF50_SF51_FREQUENCY_SCAN_REPEAT_MODE, mode));        
  }
  
  public void setFrequencyScanSpeedMode (final RS_ESH3_GPIB_Settings.FrequencyScanSpeedMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF90_SF91_FREQUENCY_SCAN_SPEED_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF90_SF91_FREQUENCY_SCAN_SPEED_MODE, mode));        
  }
  
  public void store (final int slot)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_STORE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_STORE, slot));        
  }
  
  public void recall (final int slot)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECALL,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECALL, slot));        
  }
  
  public void setFieldDataOuputMode (final RS_ESH3_GPIB_Settings.FieldDataOuputMode mode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF80_SF81_FIELD_DATA_OUTPUT_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF80_SF81_FIELD_DATA_OUTPUT_MODE, mode));
  }

  public void setEoi (final boolean eoi)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_EOI,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_EOI, eoi));
  }

  public void trigger ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (RS_ESH3_InstrumentCommand.IC_RS_ESH3_TRIGGER));
  }

  public void setRecorderCode (final boolean code)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECORDER_CODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECORDER_CODE, code));
  }

  public void setAntennaProbeCode (final boolean code)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_ANTENNA_PROBE_CODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_ANTENNA_PROBE_CODE, code));
  }

  public void setSrqOnDataReady (final boolean srq)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SRQ_ON_DATA_READY,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SRQ_ON_DATA_READY, srq));
  }
  
  public void setSrqOnEnterInLocalState (final boolean srq)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SRQ_ON_ENTER_IN_LOCAL_STATE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SRQ_ON_ENTER_IN_LOCAL_STATE, srq));
  }

  public void setGpibDelimiterInTalkerOperation (final int delimiter)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_GPIB_DELIMITER_IN_TALKER_OPERATION,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_GPIB_DELIMITER_IN_TALKER_OPERATION, delimiter));
  }
  
  public void setDisplayText (final String text)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_DISPLAY_TEXT,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_DISPLAY_TEXT, text));
  }
  
  public void beep ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (RS_ESH3_InstrumentCommand.IC_RS_ESH3_BEEP));
  }

  public void loadDAXRegister (final int value)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_DA_X_REGISTER,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_DA_X_REGISTER, value));
  }
  
  public void loadDAYRegister (final int value)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_DA_Y_REGISTER,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_DA_Y_REGISTER, value));
  }
  
  public void setRecorderXAxisMode (final RS_ESH3_GPIB_Settings.RecorderXAxisMode recorderXAxisMode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECORDER_X_AXIS_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECORDER_X_AXIS_MODE, recorderXAxisMode));
  }
  
  public void setRecorderCodingEnabled_sf_62_63 (final boolean enabled)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF62_SF63_RECORDER_CODING_ENABLED,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF62_SF63_RECORDER_CODING_ENABLED, enabled));    
  }
  
  public void setNoYTRecorder (final boolean disabled)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECORDER_NO_YT,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECORDER_NO_YT, disabled));        
  }
  
  public void setNoXYRecorder (final boolean disabled)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECORDER_NO_XY,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECORDER_NO_XY, disabled));        
  }
  
  public void setNoZSG3Recorder (final boolean disabled)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECORDER_NO_ZSG3,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECORDER_NO_ZSG3, disabled));        
  }
  
  public void setXYRecorderSpectrumMode (final RS_ESH3_GPIB_Settings.XYRecorderSpectrumMode mode)
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_XY_RECORDER_SPECTRUM_MODE,
      RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_XY_RECORDER_SPECTRUM_MODE, mode));            
  }
  
  public void refresh ()
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      RS_ESH3_InstrumentCommand.IC_RS_ESH3_REFRESH));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AUXILIARY PROCESS COMMAND METHODS
  //
  // processCommandRefresh
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected final void processCommandRefresh (final RS_ESH3_GPIB_Settings settings)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    
    if (settings == null)
      throw new IllegalArgumentException ();
    
    //
    // XXX WOULD BE NICE IF THIS WAS IMPLEMENTED AS AN ATOMIC OPERATION!
    //
    
    //
    // FREQUENCY
    //
    setFrequency_Hz (settings.getFrequency_Hz ());
    setFrequencyStart_Hz (settings.getFrequencyStart_Hz ());
    setFrequencyStop_Hz (settings.getFrequencyStop_Hz ());
    
    //
    // ATTENUATION
    //
    final RS_ESH3_GPIB_Settings.AttenuationMode attenuationMode = settings.getAttenuationMode ();
    if (attenuationMode == null)
      throw new IllegalArgumentException ();
    setAttenuationMode (attenuationMode);
    switch (attenuationMode)
    {
      case AutoRangingLowNoise:
      case AutoRangingLowDistortion:
        break;
      case Manual:
        setRfAttenuation_dB (settings.getRfAttenuation_dB ());
        setIfAttenuation_dB (settings.getIfAttenuation_dB ());
        break;
      default:
        throw new RuntimeException ();
    }
    setLinearityTest (settings.getLinearityTest ());
    
    //
    // IF BANDWIDTH
    //
    setIfBandwidth (settings.getIfBandwidth ());
    
    //
    // DEMODULATION MODE
    //
    setDemodulationMode (settings.getDemodulationMode ());
    
    //
    // MAX/MIN MODE
    //
    setMaxMinMode (settings.getMaxMinMode ());
    
    //
    // OPERATING RANGE
    //
    setOperatingRange (settings.getOperatingRange ());

    //
    // OPERATING MODE
    //
    setOperatingMode (settings.getOperatingMode ());
    
    //
    // INDICATING MODE
    //
    setIndicatingMode (settings.getIndicatingMode ());
    
    //
    // DATA OUTPUT MODE
    //
    setDataOutputMode (settings.getDataOutputMode ());
    
    //
    // MEASUREMENT TIME
    //
    setMeasurementTime_s (settings.getMeasurementTime_s ());
    
    //
    // MINIMUM/MAXIMUM LEVEL
    //
    setMinimumLevel_dB (settings.getMinimumLevel_dB ());
    setMaximumLevel_dB (settings.getMaximumLevel_dB ());
    
    //
    // STEP SIZE
    //
    final RS_ESH3_GPIB_Settings.StepSizeMode stepSizeMode = settings.getStepSizeMode ();
    if (stepSizeMode == null)
      throw new IllegalArgumentException ();
    setStepSizeMode (stepSizeMode);
    switch (stepSizeMode)
    {
      case Linear:
        setStepSize_MHz (settings.getStepSize_MHz ());
        break;
      case Logarithmic:
        setStepSize_Percent (settings.getStepSize_Percent ());
        break;
      default:
        throw new RuntimeException ();
    }
    
    //
    // TEST [MEASUREMENT]
    //
    setTestLevel (settings.getTestLevel ());
    setTestModulationDepth (settings.getTestModulationDepth ());
    setTestModulationDepthPositivePeak (settings.getTestModulationDepthPositivePeak ());
    setTestModulationDepthNegativePeak (settings.getTestModulationDepthNegativePeak ());
    setTestFrequencyOffset (settings.getTestFrequencyOffset ());
    setTestFrequencyDeviation (settings.getTestFrequencyDeviation ());
    setTestFrequencyDeviationPositivePeak (settings.getTestFrequencyDeviationPositivePeak ());
    setTestFrequencyDeviationNegativePeak (settings.getTestFrequencyDeviationNegativePeak ());
    
    //
    // FREQUENCY SCAN
    //
    setFrequencyScanRepeatMode (settings.getFrequencyScanRepeatMode ());
    setFrequencyScanSpeedMode (settings.getFrequencyScanSpeedMode ());
    
    //
    // FIELD DATA OUTPUT MODE
    //
    setFieldDataOuputMode (settings.getFieldDataOuputMode ());
    
    //
    // EOI
    //
    setEoi (settings.getEoi ());
    
    //
    // RECORDER CODE
    //
    setRecorderCode (settings.getRecorderCode ());
    
    //
    // ANTENNA PROBE CODE
    //
    setAntennaProbeCode (settings.getAntennaProbeCode ());
  
    //
    // SRQ
    //
    setSrqOnDataReady (settings.getSrqOnDataReady ());
    setSrqOnEnterInLocalState (settings.getSrqOnEnterInLocalState ());
  
    //
    // GPIB DELIMITER IN TALKER OPERATION
    //
    setGpibDelimiterInTalkerOperation (settings.getGpibDelimiterInTalkerOperation ());
    
    //
    // DISPLAY TEXT
    //
    setDisplayText (settings.getDisplayText ());
    
    //
    // RECORDER
    //
    setRecorderXAxisMode (settings.getRecorderXAxisMode ());
    final boolean recorderCodingEnabled_sf_62_63 = settings.getRecorderCodingEnabled_sf_62_63 ();
    setRecorderCodingEnabled_sf_62_63 (recorderCodingEnabled_sf_62_63);
    if (recorderCodingEnabled_sf_62_63)
    {
      setNoYTRecorder (settings.getNoYTRecorder ());
      setNoXYRecorder (settings.getNoXYRecorder ());
      setNoZSG3Recorder (settings.getNoZSG3Recorder ());
      setXYRecorderSpectrumMode (settings.getXYRecorderSpectrumMode ());
    }
    
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
    final RS_ESH3_GPIB_Settings currentInstrumentSettings = (RS_ESH3_GPIB_Settings) getCurrentInstrumentSettings (); // Non-null...
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
        case InstrumentCommand.IC_LF_FREQUENCY:
        {
          final double frequency_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_LF_FREQUENCY_HZ);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (4);
          writeSync ("FR" + df.format (inRangeFrequency_Hz (frequency_Hz) * 1E-6) + "\r");
          newInstrumentSettings = currentInstrumentSettings.withFrequency_Hz (inRangeFrequency_Hz (frequency_Hz));
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_FREQUENCY_START:
        {
          final double frequency_Hz = (double) instrumentCommand.get (RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_FREQUENCY_START);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (4);
          writeSync ("SA" + df.format (inRangeFrequency_Hz (frequency_Hz) * 1E-6) + "\r");
          newInstrumentSettings = currentInstrumentSettings.withFrequencyStart_Hz (inRangeFrequency_Hz (frequency_Hz));
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_FREQUENCY_STOP:
        {
          final double frequency_Hz = (double) instrumentCommand.get (RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_FREQUENCY_STOP);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (4);
          writeSync ("SO" + df.format (inRangeFrequency_Hz (frequency_Hz) * 1E-6) + "\r");
          newInstrumentSettings = currentInstrumentSettings.withFrequencyStop_Hz (inRangeFrequency_Hz (frequency_Hz));
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_ATTENUATION_MODE:
        {
          final RS_ESH3_GPIB_Settings.AttenuationMode mode =
            (RS_ESH3_GPIB_Settings.AttenuationMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_ATTENUATION_MODE);
          switch (mode)
          {
            case AutoRangingLowNoise:      writeSync ("A1\r"); break;
            case AutoRangingLowDistortion: writeSync ("A2\r"); break;
            case Manual:
              // Not supported on instrument; must manually set RF/IF Attenuation for that.
              LOG.log (Level.WARNING, "Do not know how to explicitly set Manual Attenuation Mode on Instrument: {0}!", this);
              break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withAttenuationMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_RF_ATTENUATION:
        {
          final double attenuation_dB =
            (double) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RF_ATTENUATION);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (2);
          writeSync ("RA" + df.format (attenuation_dB) + "\r");
          // XXX Should also set manual AttenuationMode?
          newInstrumentSettings = currentInstrumentSettings.withRfAttenuation_dB (attenuation_dB);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_LINEARITY_TEST:
        {
          final boolean test =
            (boolean) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_LINEARITY_TEST);
          writeSync (test ? "G1\r" : "G0\r");
          newInstrumentSettings = currentInstrumentSettings.withLinearityTest (test);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_IF_ATTENUATION:
        {
          final double attenuation_dB =
            (double) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_IF_ATTENUATION);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (2);
          writeSync ("IA" + df.format (attenuation_dB) + "\r");
          // XXX Should also set manual AttenuationMode?
          newInstrumentSettings = currentInstrumentSettings.withIfAttenuation_dB (attenuation_dB);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_IF_BANDWIDTH:
        {
          final RS_ESH3_GPIB_Settings.IfBandwidth bandwidth =
            (RS_ESH3_GPIB_Settings.IfBandwidth) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_IF_BANDWIDTH);
          switch (bandwidth)
          {
            case IF_BW_10KHz:  writeSync ("B1\r"); break;
            case IF_BW_2400Hz: writeSync ("B2\r"); break;
            case IF_BW_500Hz:  writeSync ("B3\r"); break;
            case IF_BW_200Hz:  writeSync ("B4\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withIfBandwidth (bandwidth);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_DEMODULATION_MODE:
        {
          final RS_ESH3_GPIB_Settings.DemodulationMode mode =
            (RS_ESH3_GPIB_Settings.DemodulationMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_DEMODULATION_MODE);
          switch (mode)
          {
            case OFF:     writeSync ("D0\r"); break;
            case A0:      writeSync ("D1\r"); break;
            case A1:      writeSync ("D2\r"); break;
            case A3:      writeSync ("D3\r"); break;
            case A3J_LSB: writeSync ("D4\r"); break;
            case A3J_USB: writeSync ("D5\r"); break;
            case F3:      writeSync ("D6\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withDemodulationMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_CALIBRATE_CHECK:
        {
          writeSync ("C1\r");
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_CALIBRATE_TOTAL:
        {
          writeSync ("C2\r");
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_MAX_MIN_MODE:
        {
          final boolean mode =
            (boolean) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_MAX_MIN_MODE);
          writeSync (mode ? "K1\r" : "K0\r");
          newInstrumentSettings = currentInstrumentSettings.withMaxMinMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_OPERATING_RANGE:
        {
          final RS_ESH3_GPIB_Settings.OperatingRange range =
            (RS_ESH3_GPIB_Settings.OperatingRange) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_OPERATING_RANGE);
          switch (range)
          {
            case Range20dB: writeSync ("L1\r"); break;
            case Range40dB: writeSync ("L2\r"); break;
            case Range60dB: writeSync ("L3\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withOperatingRange (range);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_OPERATING_MODE:
        {
          final RS_ESH3_GPIB_Settings.OperatingMode mode =
            (RS_ESH3_GPIB_Settings.OperatingMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_OPERATING_MODE);
          switch (mode)
          {
            case GeneratorOff:               writeSync ("M0\r"); break;
            case RemoteFrequencyMeasurement: writeSync ("M1\r"); break;
            case TwoPortMeasurement:         writeSync ("M2\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withOperatingMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_INDICATING_MODE:
        {
          final RS_ESH3_GPIB_Settings.IndicatingMode mode =
            (RS_ESH3_GPIB_Settings.IndicatingMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_INDICATING_MODE);
          switch (mode)
          {
            case Average: writeSync ("N1\r"); break;
            case Peak:    writeSync ("N2\r"); break;
            case CISPR:   writeSync ("N3\r"); break;
            case MIL:     writeSync ("N4\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withIndicatingMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_DATA_OUTPUT_MODE:
        {
          final RS_ESH3_GPIB_Settings.DataOutputMode mode =
            (RS_ESH3_GPIB_Settings.DataOutputMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_DATA_OUTPUT_MODE);
          switch (mode)
          {
            case dB:  writeSync ("O1\r"); break;
            case dBm: writeSync ("O2\r"); break;
            case V_A: writeSync ("O3\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withDataOutputMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_MEASUREMENT_TIME:
        {
          final double time_s =
            (double) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_MEASUREMENT_TIME);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (3);
          writeSync ("TS" + df.format (time_s) + "\r");
          newInstrumentSettings = currentInstrumentSettings.withMeasurementTime_s (time_s);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_MINIMUM_LEVEL:
        {
          final double level_dB =
            (double) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_MINIMUM_LEVEL);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (1);
          writeSync ("SL" + df.format (level_dB) + "\r");
          newInstrumentSettings = currentInstrumentSettings.withMinimumLevel_dB (level_dB);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_MAXIMUM_LEVEL:
        {
          final double level_dB =
            (double) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_MAXIMUM_LEVEL);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (1);
          writeSync ("SU" + df.format (level_dB) + "\r");
          newInstrumentSettings = currentInstrumentSettings.withMaximumLevel_dB (level_dB);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SCANNING_RUN:
        {
          final int[] slots =
            (int[]) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SCANNING_RUN);
          if (slots == null || slots.length == 0)
            writeSync ("SR\r");
          else
          {
            // XXX
            throw new UnsupportedOperationException ();
          }
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SCANNING_STOP_INTERRUPT:
        {
          writeSync ("SP\r");
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SCANNING_STOP_RESET:
        {
          writeSync ("SC\r");
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF52_SF53_STEP_SIZE_MODE:
        {
          final RS_ESH3_GPIB_Settings.StepSizeMode mode =
            (RS_ESH3_GPIB_Settings.StepSizeMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF52_SF53_STEP_SIZE_MODE);
          switch (mode)
          {
            case Linear:      writeSync ("SF52\r"); break;
            case Logarithmic: writeSync ("SF53\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withStepSizeMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_STEP_SIZE_MHZ:
        {
          final double stepSize_MHz =
            (double) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_STEP_SIZE_MHZ);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (4);
          writeSync ("SE" + df.format (stepSize_MHz) + "\r");
          // XXX Assumption below...
          newInstrumentSettings = currentInstrumentSettings
            .withStepSizeMode (RS_ESH3_GPIB_Settings.StepSizeMode.Linear)
            .withStepSize_MHz (stepSize_MHz);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_STEP_SIZE_PERCENT:
        {
          final double stepSize_Percent =
            (double) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_STEP_SIZE_PERCENT);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (2);
          writeSync ("SE" + df.format (stepSize_Percent) + "\r");
          // XXX Assumption below...
          newInstrumentSettings = currentInstrumentSettings
            .withStepSizeMode (RS_ESH3_GPIB_Settings.StepSizeMode.Logarithmic)
            .withStepSize_Percent (stepSize_Percent);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF00_SF_CLEAR:
        {
          writeSync ("SF00\r");
          newInstrumentSettings = currentInstrumentSettings.withTestClearedThroughSf00 ();
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF10_SF11_TEST_LEVEL:
        {
          final boolean test =
            (boolean) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF10_SF11_TEST_LEVEL);
          writeSync (test ? "SF11\r" : "SF10\r");
          newInstrumentSettings = currentInstrumentSettings.withTestLevel (test);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF20_SF21_TEST_MODULATION_DEPTH:
        {
          final boolean test = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF20_SF21_TEST_MODULATION_DEPTH);
          writeSync (test ? "SF21\r" : "SF20\r");
          newInstrumentSettings = currentInstrumentSettings.withTestModulationDepth (test);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF22_SF23_TEST_MODULATION_DEPTH_POSITIVE_PEAK:
        {
          final boolean test = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF22_SF23_TEST_MODULATION_DEPTH_POSITIVE_PEAK);
          writeSync (test ? "SF23\r" : "SF22\r");
          newInstrumentSettings = currentInstrumentSettings.withTestModulationDepthPositivePeak (test);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF24_SF25_TEST_MODULATION_DEPTH_NEGATIVE_PEAK:
        {
          final boolean test = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF24_SF25_TEST_MODULATION_DEPTH_NEGATIVE_PEAK);
          writeSync (test ? "SF25\r" : "SF24\r");
          newInstrumentSettings = currentInstrumentSettings.withTestModulationDepthNegativePeak (test);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF30_SF31_TEST_FREQUENCY_OFFSET:
        {
          final boolean test = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF30_SF31_TEST_FREQUENCY_OFFSET);
          writeSync (test ? "SF31\r" : "SF30\r");
          newInstrumentSettings = currentInstrumentSettings.withTestFrequencyOffset (test);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF40_SF41_TEST_FREQUENCY_DEVIATION:
        {
          final boolean test = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF40_SF41_TEST_FREQUENCY_DEVIATION);
          writeSync (test ? "SF41\r" : "SF40\r");
          newInstrumentSettings = currentInstrumentSettings.withTestFrequencyDeviation (test);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF42_SF43_TEST_FREQUENCY_DEVIATION_POSITIVE_PEAK:
        {
          final boolean test = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF42_SF43_TEST_FREQUENCY_DEVIATION_POSITIVE_PEAK);
          writeSync (test ? "SF43\r" : "SF42\r");
          newInstrumentSettings = currentInstrumentSettings.withTestFrequencyDeviationPositivePeak (test);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF44_SF45_TEST_FREQUENCY_DEVIATION_NEGATIVE_PEAK:
        {
          final boolean test = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF44_SF45_TEST_FREQUENCY_DEVIATION_NEGATIVE_PEAK);
          writeSync (test ? "SF45\r" : "SF44\r");
          newInstrumentSettings = currentInstrumentSettings.withTestFrequencyDeviationNegativePeak (test);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF50_SF51_FREQUENCY_SCAN_REPEAT_MODE:
        {
          final RS_ESH3_GPIB_Settings.FrequencyScanRepeatMode mode =
            (RS_ESH3_GPIB_Settings.FrequencyScanRepeatMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF50_SF51_FREQUENCY_SCAN_REPEAT_MODE);
          switch (mode)
          {
            case SingleScan: writeSync ("SF50\r"); break;
            case AutoRepeat: writeSync ("SF51\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withFrequencyScanRepeatMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF90_SF91_FREQUENCY_SCAN_SPEED_MODE:
        {
          final RS_ESH3_GPIB_Settings.FrequencyScanSpeedMode mode =
            (RS_ESH3_GPIB_Settings.FrequencyScanSpeedMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF90_SF91_FREQUENCY_SCAN_SPEED_MODE);
          switch (mode)
          {
            case Normal: writeSync ("SF90\r"); break;
            case Fast:   writeSync ("SF91\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withFrequencyScanSpeedMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_STORE:
        {
          final Integer slot =
            (Integer) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_STORE);
          if (slot == null || slot <= 0 || slot > 9)
            throw new IllegalArgumentException ();
          writeSync ("ST" + slot + "\r");
          // XXX
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECALL:
        {
          final Integer slot =
            (Integer) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECALL);
          if (slot == null || slot < 0 || slot > 9)
            throw new IllegalArgumentException ();
          writeSync ("RC" + slot + "\r");
          // XXX
          // newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }        
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF80_SF81_FIELD_DATA_OUTPUT_MODE:
        {
          final RS_ESH3_GPIB_Settings.FieldDataOuputMode mode =
            (RS_ESH3_GPIB_Settings.FieldDataOuputMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF80_SF81_FIELD_DATA_OUTPUT_MODE);
          switch (mode)
          {
            case E: writeSync ("SF80\r"); break;
            case H: writeSync ("SF81\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withFieldDataOutputMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_EOI:
        {
          final boolean eoi = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_EOI);
          writeSync (eoi ? "U1\r" : "U0\r");
          newInstrumentSettings = currentInstrumentSettings.withEoi (eoi);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_TRIGGER:
        {
          writeSync ("X1\r");
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECORDER_CODE:
        {
          final boolean code = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECORDER_CODE);
          writeSync (code ? "Y1\r" : "Y0\r");
          newInstrumentSettings = currentInstrumentSettings.withRecorderCode (code);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_ANTENNA_PROBE_CODE:
        {
          final boolean code = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_ANTENNA_PROBE_CODE);
          writeSync (code ? "Z1\r" : "Z0\r");
          newInstrumentSettings = currentInstrumentSettings.withAntennaProbeCode (code);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SRQ_ON_DATA_READY:
        {
          final boolean srq = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SRQ_ON_DATA_READY);
          writeSync (srq ? "P1\r" : "P0\r");
          newInstrumentSettings = currentInstrumentSettings.withSrqOnDataReady (srq);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SRQ_ON_ENTER_IN_LOCAL_STATE:
        {
          final boolean srq = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SRQ_ON_ENTER_IN_LOCAL_STATE);
          writeSync (srq ? "J1\r" : "J0\r");
          newInstrumentSettings = currentInstrumentSettings.withSrqOnEnterInLocalState (srq);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_GPIB_DELIMITER_IN_TALKER_OPERATION:
        {
          final int delimiter = (int) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_GPIB_DELIMITER_IN_TALKER_OPERATION);
          if (delimiter < 0 || delimiter > 31)
            throw new IllegalArgumentException ();
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (0);
          df.setMinimumIntegerDigits (2);
          writeSync ("WZ" + df.format (delimiter) + "\r");
          newInstrumentSettings = currentInstrumentSettings.withGpibDelimiterInTalkerOperation (delimiter);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_DISPLAY_TEXT:
        {
          final String text = (String) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_DISPLAY_TEXT);
          final String commandTextLimited;
          if (text == null)
            // XXX Not sure if this cancels an earlier String, set the empty String, or something else...
            // XXX Do I need 13 chracters here???
            commandTextLimited = "WT\r";
          else
            commandTextLimited = "WT" + text.substring (0, Math.min (13, text.length ())) + "\r";
          writeSync (commandTextLimited);
          newInstrumentSettings = currentInstrumentSettings.withDisplayText (commandTextLimited);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_BEEP:
        {
          writeSync ("BP\r");
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_DA_X_REGISTER:
        {
          final int value = (int) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_DA_X_REGISTER);
          if (value < 0 || value > 1023)
            throw new IllegalArgumentException ();
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (0);
          df.setMinimumIntegerDigits (4);
          writeSync ("XP" + df.format (value) + "\r");
          // XXX X and Y D/A Registers are not part of instrument state?
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_DA_Y_REGISTER:
        {
          final int value = (int) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_DA_Y_REGISTER);
          if (value < 0 || value > 255)
            throw new IllegalArgumentException ();
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (0);
          df.setMinimumIntegerDigits (3);
          writeSync ("YP" + df.format (value) + "\r");
          // XXX X and Y D/A Registers are not part of instrument state?
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECORDER_X_AXIS_MODE:
        {
          final RS_ESH3_GPIB_Settings.RecorderXAxisMode mode =
            (RS_ESH3_GPIB_Settings.RecorderXAxisMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECORDER_X_AXIS_MODE);
          switch (mode)
          {
            case Linear:      writeSync ("SF60\r"); break;
            case Logarithmic: writeSync ("SF61\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withRecorderXAxisMode (mode);
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_SF62_SF63_RECORDER_CODING_ENABLED:
        {
          final boolean enabled = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_SF62_SF63_RECORDER_CODING_ENABLED);
          writeSync (enabled ? "SF62\r" : "SF63\r");
          newInstrumentSettings = currentInstrumentSettings.withRecorderCodingEnabled_sf_62_63 (enabled);
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECORDER_NO_YT:
        {
          final boolean disabled = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECORDER_NO_YT);
          if (disabled)
            writeSync ("SF64\r");
          else
            LOG.log (Level.WARNING, "Do not know how to explicitly enable YT Recorder on Instrument: {0}!", this);
          newInstrumentSettings = currentInstrumentSettings.withNoYTRecorder (disabled);
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECORDER_NO_XY:
        {
          final boolean disabled = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECORDER_NO_XY);
          if (disabled)
            writeSync ("SF65\r");
          else
            LOG.log (Level.WARNING, "Do not know how to explicitly enable XY Recorder on Instrument: {0}!", this);
          newInstrumentSettings = currentInstrumentSettings.withNoXYRecorder (disabled);
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_RECORDER_NO_ZSG3:
        {
          final boolean disabled = (boolean) instrumentCommand.get (
            RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_RECORDER_NO_ZSG3);
          if (disabled)
            writeSync ("SF66\r");
          else
            LOG.log (Level.WARNING, "Do not know how to explicitly enable ZSG3 Recorder on Instrument: {0}!", this);
          newInstrumentSettings = currentInstrumentSettings.withNoZSG3Recorder (disabled);
          break;          
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_XY_RECORDER_SPECTRUM_MODE:
        {
          final RS_ESH3_GPIB_Settings.XYRecorderSpectrumMode mode =
            (RS_ESH3_GPIB_Settings.XYRecorderSpectrumMode) instrumentCommand.get (
              RS_ESH3_InstrumentCommand.ICARG_RS_ESH3_XY_RECORDER_SPECTRUM_MODE);
          switch (mode)
          {
            case PolygonalCurve: writeSync ("SF70\r"); break;
            case LineSpectrum:   writeSync ("SF71\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = currentInstrumentSettings.withXYRecorderSpectrumMode (mode);
          break;
        }
        case RS_ESH3_InstrumentCommand.IC_RS_ESH3_REFRESH:
        {
          if (currentInstrumentSettings != null)
            newInstrumentSettings = currentInstrumentSettings;
          else
            newInstrumentSettings = RS_ESH3_GPIB_Settings.powerOnSettings ();
          processCommandRefresh ((RS_ESH3_GPIB_Settings) newInstrumentSettings);
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
