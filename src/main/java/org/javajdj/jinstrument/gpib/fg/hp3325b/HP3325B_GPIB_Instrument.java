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
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    resetOptimizeSettingsUpdates ();
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
    // XXX
    LOG.log (Level.WARNING, "Read status byte: {0}\n", statusByte);
    if (HP3325B_GPIB_Status.supportsErrorString (statusByte))
    {
      final Integer errorInt = getErrorSync (getReadEOITimeout_ms (), TimeUnit.MILLISECONDS);
      // XXX
      LOG.log (Level.WARNING, "Read error int: {0}\n", errorInt);
      return new HP3325B_GPIB_Status (statusByte, errorInt);
    }
    else
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
    
    // FunctionGenerator
    
    final Waveform waveform = getSettingsFromInstrumentSync_Waveform ();
    final double frequency_Hz = getSettingsFromInstrumentSync_Frequency_Hz ();
    final double amplitude_Vpp = getSettingsFromInstrumentSync_Amplitude_Vpp (waveform);
    final double dcOffset_V = getSettingsFromInstrumentSync_DCOffset_V ();
    
    // HP-3325B
    
    final boolean rs232Echo = getSettingsFromInstrumentSync_RS232Echo ();
    final boolean enhancements = getSettingsFromInstrumentSync_Enhancements ();
    final int errorCode = getSettingsFromInstrumentSync_ErrorCode ();
    final EnumSet<HP3325B_GPIB_Settings.ServiceRequestEnableMaskBit> serviceRequestEnableMask
      = getSettingsFromInstrumentSync_ServiceRequestEnableMask ();
    final boolean externalReferenceLocked = getSettingsFromInstrumentSync_ExternalReferenceLocked ();
    final boolean useResponseHeader = getSettingsFromInstrumentSync_UseResponseHeader ();
    final boolean highVoltageOutput = getSettingsFromInstrumentSync_HighVoltageOutput ();
    final String id = getSettingsFromInstrumentSync_Id ();
    final boolean amplitudeModulation = getSettingsFromInstrumentSync_AmpltitudeModulation ();
    final HP3325B_GPIB_Settings.DataTransferMode dataTransferMode = getSettingsFromInstrumentSync_DataTransferMode ();
    final double markerFrequency_Hz = getSettingsFromInstrumentSync_MarkerFrequency_Hz ();
    final double modulationSourceAmplitude_Vpp = getSettingsFromInstrumentSync_ModulationSourceAmplitude_Vpp ();
    final double modulationSourceFrequency_Hz = getSettingsFromInstrumentSync_ModulationSourceFrequency_Hz ();
    final HP3325B_GPIB_Settings.ModulationSourceWaveformFunction modulationSourceWaveformFunction =
      getSettingsFromInstrumentSync_ModulationSourceWaveformFunction ();
    final boolean phaseModulation = getSettingsFromInstrumentSync_PhaseModulation ();
    final EnumSet<HP3325B_GPIB_Settings.Option> optionsInstalled = getSettingsFromInstrumentSync_OptionsInstalled ();
    final double phase_degrees = getSettingsFromInstrumentSync_Phase_degrees ();
    final byte statusByteRs232 = getSettingsFromInstrumentSync_statusByteRs232 ();
    final HP3325B_GPIB_Settings.RFOutputMode rfOutputMode = getSettingsFromInstrumentSync_RFOutputMode ();
    final HP3325B_GPIB_Settings.SweepMode sweepMode = getSettingsFromInstrumentSync_SweepMode ();
    final double sweepStopFrequency_Hz = getSettingsFromInstrumentSync_SweepStopFrequency_Hz ();
    final double sweepStartFrequency_Hz = getSettingsFromInstrumentSync_SweepStartFrequency_Hz ();
    final double sweepTime_s = getSettingsFromInstrumentSync_SweepTime_s ();
    
    return new HP3325B_GPIB_Settings (
      waveform,
      frequency_Hz,
      amplitude_Vpp,
      dcOffset_V,
      rs232Echo,
      enhancements,
      errorCode,
      serviceRequestEnableMask,
      externalReferenceLocked,
      useResponseHeader,
      highVoltageOutput,
      id,
      amplitudeModulation,
      dataTransferMode,
      markerFrequency_Hz,
      modulationSourceAmplitude_Vpp,
      modulationSourceFrequency_Hz,
      modulationSourceWaveformFunction,
      phaseModulation,
      optionsInstalled,
      phase_degrees,
      statusByteRs232,
      rfOutputMode,
      sweepMode,
      sweepStopFrequency_Hz,
      sweepStartFrequency_Hz,
      sweepTime_s);
  }
 
  private double getSettingsFromInstrumentSync_Double (final String property, final String unitString)
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryString = new String (writeAndReadEOISync (property + "?\n"), Charset.forName ("US-ASCII")).trim ();
    if (queryString.startsWith (property) && ! queryString.endsWith (unitString))
      throw new IOException ();
    if ((! queryString.startsWith (property)) && queryString.endsWith (unitString))
      throw new IOException ();
    final String numberString = (queryString.startsWith (property)
      ? queryString.substring (property.length (), queryString.length () - unitString.length ())
      : queryString);
    final double number;
    try
    {
      number = Double.parseDouble (numberString);
    }
    catch (NumberFormatException nfe)
    {
      LOG.log (Level.WARNING, "Illegal number for Double from Instrument {0} in return String: {1}; numer String: {2}!",
        new Object[]{this, queryString, numberString});
      throw new IOException ();
    }
    return number;
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
    return getSettingsFromInstrumentSync_Double ("FR", "HZ");
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
  
  private boolean getSettingsFromInstrumentSync_Boolean (final String property)
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync (property + "?\n"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null
      || (queryReturn.length () != property.length () + 1 && queryReturn.length () != 1)
      || (queryReturn.length () == property.length () + 1 && ! queryReturn.startsWith (property)))
      throw new IOException ();
    final boolean returnValue;
    switch (queryReturn.length () == 1 ? queryReturn : queryReturn.substring (property.length ()))
    {
      case "0": returnValue = false; break;
      case "1": returnValue = true; break;
      default: throw new IOException ();
    }
    return returnValue;
  }
  
  private int getSettingsFromInstrumentSync_Integer1 (final String property)
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync (property + "?\n"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null
      || (queryReturn.length () != property.length () + 1 && queryReturn.length () != 1)
      || (queryReturn.length () == property.length () + 1 && ! queryReturn.startsWith (property)))
      throw new IOException ();
    final int returnValue;
    try
    {
      return Integer.parseInt (queryReturn.length () == 1 ? queryReturn : queryReturn.substring (property.length ()));
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
  }
  
  private int getSettingsFromInstrumentSync_Integer3 (final String property)
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync (property + "?\n"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null
      || (queryReturn.length () != property.length () + 3 && queryReturn.length () != 3)
      || (queryReturn.length () == property.length () + 3 && ! queryReturn.startsWith (property)))
      throw new IOException ();
    final int returnValue;
    try
    {
      return Integer.parseInt (queryReturn.length () == 3 ? queryReturn : queryReturn.substring (property.length ()));
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
  }
  
  private boolean getSettingsFromInstrumentSync_RS232Echo ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Boolean ("ECHO");
  }
  
  private boolean getSettingsFromInstrumentSync_Enhancements ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Boolean ("ENH");    
  }
  
  private int getSettingsFromInstrumentSync_ErrorCode ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Integer3 ("ERR");        
  }
  
  private EnumSet<HP3325B_GPIB_Settings.ServiceRequestEnableMaskBit> getSettingsFromInstrumentSync_ServiceRequestEnableMask ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("ESTB?\n"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null
      || (queryReturn.length () != 10 && queryReturn.length () != 3)
      // XXX Are there always three digits between ESTB and ENT?
      || (queryReturn.length () == 10 && ! (queryReturn.startsWith ("ESTB") && queryReturn.endsWith ("ENT"))))
      throw new IOException ();
    final int maskInt;
    try
    {
      maskInt = Integer.parseInt (queryReturn.length () == 3 ? queryReturn : queryReturn.substring (4, 7));
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    if (maskInt < 0 || maskInt > 15)
      throw new IOException ();
    final EnumSet<HP3325B_GPIB_Settings.ServiceRequestEnableMaskBit> maskSet =
      EnumSet.noneOf (HP3325B_GPIB_Settings.ServiceRequestEnableMaskBit.class);
    if ((maskInt & 0x01) != 0)
      maskSet.add (HP3325B_GPIB_Settings.ServiceRequestEnableMaskBit.ERR);
    if ((maskInt & 0x02) != 0)
      maskSet.add (HP3325B_GPIB_Settings.ServiceRequestEnableMaskBit.STOP);
    if ((maskInt & 0x04) != 0)
      maskSet.add (HP3325B_GPIB_Settings.ServiceRequestEnableMaskBit.START);
    if ((maskInt & 0x08) != 0)
      maskSet.add (HP3325B_GPIB_Settings.ServiceRequestEnableMaskBit.FAIL);
    return maskSet;
  }
  
  private boolean getSettingsFromInstrumentSync_ExternalReferenceLocked ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Boolean ("EXTR");    
  }

  private boolean getSettingsFromInstrumentSync_UseResponseHeader ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Boolean ("HEAD");
  }
  
  private boolean getSettingsFromInstrumentSync_HighVoltageOutput ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("HV?\n"), Charset.forName ("US-ASCII")).trim ();
    // XXX We do not support HEADER less operation here!
    switch (queryReturn)
    {
      case "RF1":
      case "RF2":
        // Option not installed -> return false.
        return false;
      case "HV1":
        // Option installed but inactive.
        return false;
      case "HV2":
        // Option installed and active.
        return true;
      default:
        throw new IOException ();
    }
  }

  private String getSettingsFromInstrumentSync_Id ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("ID?\n"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null || queryReturn.length () == 0)
      throw new IOException ();
    return queryReturn;
  }

  private boolean getSettingsFromInstrumentSync_AmpltitudeModulation ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Boolean ("MA");      
  }
  
  private HP3325B_GPIB_Settings.DataTransferMode getSettingsFromInstrumentSync_DataTransferMode ()
    throws IOException, InterruptedException, TimeoutException
  {
    final int response = getSettingsFromInstrumentSync_Integer1 ("MD");
    final HP3325B_GPIB_Settings.DataTransferMode dataTransferMode;
    switch (response)
    {
      case 1: dataTransferMode = HP3325B_GPIB_Settings.DataTransferMode.Mode1; break;
      case 2: dataTransferMode = HP3325B_GPIB_Settings.DataTransferMode.Mode2; break;
      default: throw new IOException ();
    }
    return dataTransferMode; 
  }
  
  private double getSettingsFromInstrumentSync_MarkerFrequency_Hz ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Double ("MF", "HZ");    
  }
  
  private double getSettingsFromInstrumentSync_ModulationSourceAmplitude_Vpp ()
    throws IOException, InterruptedException, TimeoutException
  {
    // XXX CURRENTLY ONLY SUPPORTS THE VO RETURN UNIT, NOT THE VR!
    return getSettingsFromInstrumentSync_Double ("MOAM", "VO");    
  }
  
  private double getSettingsFromInstrumentSync_ModulationSourceFrequency_Hz ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Double ("MOFR", "HZ");
  }
  
  private HP3325B_GPIB_Settings.ModulationSourceWaveformFunction getSettingsFromInstrumentSync_ModulationSourceWaveformFunction ()
    throws IOException, InterruptedException, TimeoutException
  {
    // XXX Does not yet support non-HEADER operation.
    final String queryString = new String (writeAndReadEOISync ("MOFU?\n"), Charset.forName ("US-ASCII")).trim ();
    if (! queryString.startsWith ("MOFU"))
    {
      LOG.log (Level.WARNING, "Unexpected settings reading (prefix MOFU) from Instrument {0}: {1}!",
        new Object[]{this, queryString});
      throw new IOException ();
    }
    if (queryString.length () != 5) // "MOFU" + single digit.
    {
      LOG.log (Level.WARNING, "Illegal/Unexpected modulation waveform reading (length) from Instrument {0}: {1}!",
        new Object[]{this, queryString});
      throw new IOException ();
    }
    final HP3325B_GPIB_Settings.ModulationSourceWaveformFunction waveform;
    switch (queryString.substring (4))
    {
      case "0": waveform = HP3325B_GPIB_Settings.ModulationSourceWaveformFunction.Off;       break;
      case "1": waveform = HP3325B_GPIB_Settings.ModulationSourceWaveformFunction.Sine;      break;
      case "2": waveform = HP3325B_GPIB_Settings.ModulationSourceWaveformFunction.Square;    break;
      case "3": waveform = HP3325B_GPIB_Settings.ModulationSourceWaveformFunction.Arbitrary; break;
      default:
        LOG.log (Level.WARNING, "Illegal identifier for modulation waveform from Instrument {0} in return String: {1}!",
          new Object[]{this, queryString});
        throw new IOException ();
    }
    return waveform;    
  }
  
  private boolean getSettingsFromInstrumentSync_PhaseModulation ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Boolean ("MP");
  }
  
  private EnumSet<HP3325B_GPIB_Settings.Option> getSettingsFromInstrumentSync_OptionsInstalled ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("OPT?\n"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null || queryReturn.length () == 0)
      throw new IOException ();
    final EnumSet<HP3325B_GPIB_Settings.Option> optionsInstalled;
    switch (queryReturn)
    {
      case "0,0":
      case "OPT0,0":
        optionsInstalled = EnumSet.noneOf (HP3325B_GPIB_Settings.Option.class);
        break;
      case "1,0":
      case "OPT1,0":
        optionsInstalled = EnumSet.of (HP3325B_GPIB_Settings.Option.Oven);
        break;
      case "0,2":
      case "OPT0,2":
        optionsInstalled = EnumSet.of (HP3325B_GPIB_Settings.Option.HighVoltage);
        break;
      case "1,2":
      case "OPT1,2":
        optionsInstalled = EnumSet.allOf (HP3325B_GPIB_Settings.Option.class);
        break;
      default:
        throw new IOException ();
    }
    return optionsInstalled;
  }
  
  private double getSettingsFromInstrumentSync_Phase_degrees ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Double ("PH", "DE");
  }
  
  private byte getSettingsFromInstrumentSync_statusByteRs232 ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("QSTB?\n"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null
      || (queryReturn.length () != 3 && queryReturn.length () != 7)
      || (queryReturn.length () == 7 && ! queryReturn.startsWith ("QSTB")))
      throw new IOException ();
    try
    {
      final byte statusByte =
        (byte) Integer.parseInt (queryReturn.length () == 3 ? queryReturn : queryReturn.substring (4));
      return statusByte;
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
  }
  
  private HP3325B_GPIB_Settings.RFOutputMode getSettingsFromInstrumentSync_RFOutputMode ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("RF?\n"), Charset.forName ("US-ASCII")).trim ();
    System.err.println ("RF? Query Return: " + queryReturn + ".");
    if (queryReturn == null
      || (queryReturn.length () != 1 && queryReturn.length () != 3)
      || (queryReturn.length () == 3 && ((! queryReturn.startsWith ("RF")) && ! queryReturn.startsWith ("HV"))))
      throw new IOException ();
    final HP3325B_GPIB_Settings.RFOutputMode rfOutputMode;
    switch (queryReturn.length () == 1 ? queryReturn : queryReturn.substring (2))
    {
      case "1": rfOutputMode = HP3325B_GPIB_Settings.RFOutputMode.Front; break;
      case "2": rfOutputMode = HP3325B_GPIB_Settings.RFOutputMode.Rear;  break;
      default: throw new IOException ();
    }
    return rfOutputMode;
  }
  
  private HP3325B_GPIB_Settings.SweepMode getSettingsFromInstrumentSync_SweepMode ()
    throws IOException, InterruptedException, TimeoutException
  {
    final String queryReturn = new String (writeAndReadEOISync ("SM?\n"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null
      || (queryReturn.length () != 1 && queryReturn.length () != 3)
      || (queryReturn.length () == 3 && ! queryReturn.startsWith ("SM")))
      throw new IOException ();
    final HP3325B_GPIB_Settings.SweepMode sweepMode;
    switch (queryReturn.length () == 1 ? queryReturn : queryReturn.substring (2))
    {
      case "1": sweepMode = HP3325B_GPIB_Settings.SweepMode.Linear;      break;
      case "2": sweepMode = HP3325B_GPIB_Settings.SweepMode.Logarithmic; break;
      case "3": sweepMode = HP3325B_GPIB_Settings.SweepMode.Discrete;    break;
      default: throw new IOException ();
    }
    return sweepMode;    
  }
  
  private double getSettingsFromInstrumentSync_SweepStopFrequency_Hz ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Double ("SP", "HZ");
  }
  
  private double getSettingsFromInstrumentSync_SweepStartFrequency_Hz ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Double ("ST", "HZ");    
  }
  
  private double getSettingsFromInstrumentSync_SweepTime_s ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getSettingsFromInstrumentSync_Double ("TI", "SE");    
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
  protected void onGpibServiceRequestFromInstrument (final byte statusByte)
    throws IOException, InterruptedException, TimeoutException
  {
    throw new UnsupportedOperationException ();
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FunctionGenerator
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns {@code false}.
   * 
   * @return {@code false}.
   * 
   */
  @Override
  public final boolean supportsOutputEnable ()
  {
    return false;
  }

  @Override
  public final void setOutputEnable (final boolean outputEnable)
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
  public final void setWaveform (final Waveform waveform)
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
  public final void setFrequency_Hz (final double frequency_Hz)
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
    
  public final void amplitudeCalibration ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_AMPLITUDE_CALIBRATION));
  }
  
  public final void assignZeroPhase ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_ASSIGN_ZERO_PHASE));
  }
  
  public final void setAmplitudeCalibrationMode (final HP3325B_GPIB_Settings.AmpltitudeCalibrationMode mode)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_AMPLITUDE_CALIBRATION_MODE,
      HP3325B_InstrumentCommand.ICARG_HP3325B_AMPLITUDE_CALIBRATION_MODE, mode));
  }
  
  public final void discreteSweepTableClear ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_DISCRETE_SWEEP_TABLE_CLEAR));
  }
  
  public final void display (final boolean enable)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_DISPLAY,
      HP3325B_InstrumentCommand.ICARG_HP3325B_DISPLAY, enable));
  }
  
  public final void displayOn ()
    throws IOException, InterruptedException
  {
    display (true);
  }
  
  public final void displayOff ()
    throws IOException, InterruptedException
  {
    display (false);
  }
  
  public final void discreteSweepStore (final int slot)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_DISCRETE_SWEEP_STORE,
      HP3325B_InstrumentCommand.ICARG_HP3325B_DISCRETE_SWEEP_STORE, slot));
  }
  
  public final void discreteSweepRecall (final int slot)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_DISCRETE_SWEEP_RECALL,
      HP3325B_InstrumentCommand.ICARG_HP3325B_DISCRETE_SWEEP_RECALL, slot));
  }
  
  public final void setDisplayString (final String string)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_DISPLAY_STRING,
      HP3325B_InstrumentCommand.ICARG_HP3325B_DISPLAY_STRING, string));
  }
  
  public final void setRs232Echo (final boolean echo)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_RS232_ECHO,
      HP3325B_InstrumentCommand.ICARG_HP3325B_RS232_ECHO, echo));
  }
  
  public final void setEnableEnhancements (final boolean enable)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_ENHANCEMENTS_CONTROL,
      HP3325B_InstrumentCommand.ICARG_HP3325B_ENHANCEMENTS_CONTROL, enable));
  }
  
  public final int getErrorSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_ERROR);
    addAndProcessCommandSync (command, timeout, unit);
    return (int) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void setServiceRequestEnableMask (final EnumSet<HP3325B_GPIB_Settings.ServiceRequestEnableMaskBit> mask)
    throws IOException, InterruptedException
  {
    if (mask == null)
      throw new IllegalArgumentException ();
    int maskAsInt = 0;
    for (final HP3325B_GPIB_Settings.ServiceRequestEnableMaskBit serviceRequestEnableMaskBit : mask)
      maskAsInt += serviceRequestEnableMaskBit.getValue ();
    setServiceRequestEnableMask (maskAsInt);
  }
  
  public final void setServiceRequestEnableMask (final int mask)
    throws IOException, InterruptedException
  {
    if (mask < 0 || mask > 15)
      throw new IllegalArgumentException ();
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_SERVICE_REQUEST_ENABLE,
      HP3325B_InstrumentCommand.ICARG_HP3325B_SERVICE_REQUEST_ENABLE, mask));    
  }
  
  public final boolean isExternalReferenceLockedSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_EXTERNAL_REFERENCE_LOCKED);
    addAndProcessCommandSync (command, timeout, unit);
    return (boolean) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void setUseResponseHeader (final boolean useHeader)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_RESPONSE_HEADER_CONTROL,
      HP3325B_InstrumentCommand.ICARG_HP3325B_RESPONSE_HEADER_CONTROL, useHeader));
  }
  
  public final void setHighVoltageOutput (final boolean enable)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_HIGH_VOLTAGE_OUTPUT,
      HP3325B_InstrumentCommand.ICARG_HP3325B_HIGH_VOLTAGE_OUTPUT, enable));
  }
  
  public final String getIdSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_IDENTIFICATION);
    addAndProcessCommandSync (command, timeout, unit);
    return (String) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
  
  public final void local ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_LOCAL));
  }
  
  public final void setAmplitudeModulation (final boolean enable)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_AMPLITUDE_MODULATION,
      HP3325B_InstrumentCommand.ICARG_HP3325B_AMPLITUDE_MODULATION, enable));
  }
  
  public final void setDataTransferMode (final HP3325B_GPIB_Settings.DataTransferMode mode)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_DATA_TRANSFER_MODE,
      HP3325B_InstrumentCommand.ICARG_HP3325B_DATA_TRANSFER_MODE, mode));
  }
  
  public final void setMarkerFrequency_Hz (final double frequency_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_MARKER_FREQUENCY,
      HP3325B_InstrumentCommand.ICARG_HP3325B_MARKER_FREQUENCY, frequency_Hz));
  }

  public final void setModulationSourceAmplitude_Vpp (final double amplitude_Vpp)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_MODULATION_SOURCE_AMPLITUDE,
      HP3325B_InstrumentCommand.ICARG_HP3325B_MODULATION_SOURCE_AMPLITUDE, amplitude_Vpp));
  }

  public final void setModulationSourceArbitraryWaveformData (final double[] waveformData)
    throws IOException, InterruptedException
  {
    if (waveformData == null || waveformData.length == 0 || waveformData.length > 4096)
      throw new IllegalArgumentException ();
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_MODULATION_SOURCE_WAVEFORM_FUNCTION,
      HP3325B_InstrumentCommand.ICARG_HP3325B_MODULATION_SOURCE_WAVEFORM_FUNCTION, waveformData));
  }

  public final void setModulationSourceFrequency_Hz (final double frequency_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_MODULATION_SOURCE_FREQUENCY,
      HP3325B_InstrumentCommand.ICARG_HP3325B_MODULATION_SOURCE_FREQUENCY, frequency_Hz));
  }

  public final void setModulationSourceWaveformFunction (
    final HP3325B_GPIB_Settings.ModulationSourceWaveformFunction waveformFunction)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_MODULATION_SOURCE_WAVEFORM_FUNCTION,
      HP3325B_InstrumentCommand.ICARG_HP3325B_MODULATION_SOURCE_WAVEFORM_FUNCTION, waveformFunction));
  }

  public final void setPhaseModulation (final boolean enable)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_PHASE_MODULATION,
      HP3325B_InstrumentCommand.ICARG_HP3325B_PHASE_MODULATION, enable));
  }
  
  public final void setStatusByteMaskChar (final char maskChar)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_STATUS_BYTE_MASK,
      HP3325B_InstrumentCommand.ICARG_HP3325B_STATUS_BYTE_MASK, maskChar));
  }
  
  public final EnumSet<HP3325B_GPIB_Settings.Option> getOptionsInstalledSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_OPTION);
    addAndProcessCommandSync (command, timeout, unit);
    return (EnumSet<HP3325B_GPIB_Settings.Option>) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);    
  }
  
  public final void setPhase_degrees (final double degrees)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_PHASE,
      HP3325B_InstrumentCommand.ICARG_HP3325B_PHASE, degrees));
  }

  public final byte getStatusByteRs232Sync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_RS232_STATUS_BYTE);
    addAndProcessCommandSync (command, timeout, unit);
    return (byte) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);    
  }
  
  public final void queryStatusByteRs232 ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_RS232_STATUS_BYTE));
  }
  
  public final void recallState (final int slot)
    throws IOException, InterruptedException
  {
    if (slot < 0 || slot > 9)
      throw new IllegalArgumentException ();
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_RECALL_STATE,
      HP3325B_InstrumentCommand.ICARG_HP3325B_RECALL_STATE, slot));
  }

  public final void recallStatePowerDown ()
    throws IOException, InterruptedException
  {
    // We use the special slot index 10 to denote the "power-down" state ("RE-").
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_RECALL_STATE,
      HP3325B_InstrumentCommand.ICARG_HP3325B_RECALL_STATE, 10));
  }
  
  public final void setRFOutputMode (final HP3325B_GPIB_Settings.RFOutputMode mode)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_REAR_FRONT,
      HP3325B_InstrumentCommand.ICARG_HP3325B_REAR_FRONT, mode));
  }
  
  public final void remote ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_REMOTE));
  }
  
  public final void reset ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_RESET));
  }

  public final void resetSingleSweep ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_RESET_SINGLE_SWEEP));
  }

  public final void startContinuousSweep ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_START_CONTINUOUS_SWEEP));
  }
  
  public final void setSweepMode (final HP3325B_GPIB_Settings.SweepMode mode)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_SWEEP_MODE,
      HP3325B_InstrumentCommand.ICARG_HP3325B_SWEEP_MODE, mode));
  }
  
  public final void setSweepStopFrequency_Hz (final double frequency_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_SWEEP_STOP_FREQUENCY,
      HP3325B_InstrumentCommand.ICARG_HP3325B_SWEEP_STOP_FREQUENCY, frequency_Hz));
  }

  public final void storeState (final int slot)
    throws IOException, InterruptedException
  {
    if (slot < 0 || slot > 9)
      throw new IllegalArgumentException ();
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_STORE_STATE,
      HP3325B_InstrumentCommand.ICARG_HP3325B_STORE_STATE, slot));
  }

  public final void startSingleSweep ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_START_SINGLE_SWEEP));
  }
  
  public final void setSweepStartFrequency_Hz (final double frequency_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_SWEEP_START_FREQUENCY,
      HP3325B_InstrumentCommand.ICARG_HP3325B_SWEEP_START_FREQUENCY, frequency_Hz));
  }

  public final void setSweepTime_s (final double sweepTime_s)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      HP3325B_InstrumentCommand.IC_HP3325B_SWEEP_TIME,
      HP3325B_InstrumentCommand.ICARG_HP3325B_SWEEP_TIME, sweepTime_s));
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
        case HP3325B_InstrumentCommand.IC_HP3325B_AMPLITUDE_CALIBRATION:
        {
          writeSync ("AC\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_ASSIGN_ZERO_PHASE:
        {
          writeSync ("AP\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_AMPLITUDE_CALIBRATION_MODE:
        {
          final HP3325B_GPIB_Settings.AmpltitudeCalibrationMode mode =
            (HP3325B_GPIB_Settings.AmpltitudeCalibrationMode) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_AMPLITUDE_CALIBRATION_MODE);
          switch (mode)
          {
            case BEST: writeSync ("CALM0\r"); break;
            case FAST: writeSync ("CALM1\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_DISCRETE_SWEEP_TABLE_CLEAR:
        {
          writeSync ("DCLR\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_DISPLAY:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_DISPLAY);
          writeSync (enable ? "DISP1\r" : "DISP0\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_DISCRETE_SWEEP_STORE:
        {
          final int slot =
            (int) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_DISCRETE_SWEEP_STORE);
          if (slot < 0 || slot > 100)
            throw new IllegalArgumentException ();
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMinimumIntegerDigits (2);
          df.setMaximumIntegerDigits (2);
          df.setMaximumFractionDigits (0);
          writeSync ("DSTO" + df.format (slot) + "\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_DISCRETE_SWEEP_RECALL:
        {
          final int slot =
            (int) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_DISCRETE_SWEEP_RECALL);
          if (slot < 0 || slot > 100)
            throw new IllegalArgumentException ();
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMinimumIntegerDigits (2);
          df.setMaximumIntegerDigits (2);
          df.setMaximumFractionDigits (0);
          writeSync ("DRCL" + df.format (slot) + "\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_DISPLAY_STRING:
        {
          final String string =
            (String) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_DISPLAY_STRING);
          if (string == null)
            writeSync ("DSP\r");
          else
            // XXX Should we not check for ASCII?
            writeSync ("DSP\"" + string + "\"\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_RS232_ECHO:
        {
          final boolean echo =
            (boolean) instrumentCommand.get (HP3325B_InstrumentCommand.ICARG_HP3325B_RS232_ECHO);
          writeSync (echo ? "ECHO1\r" : "ECHO0\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_ENHANCEMENTS_CONTROL:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_ENHANCEMENTS_CONTROL);
          writeSync (enable ? "ENH1\r" : "ENH0\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_ERROR:
        {
          final String errReturn = new String (writeAndReadEOISync ("ERR?"), Charset.forName ("US-ASCII")).trim ();
          if (errReturn == null
            || (errReturn.length () != 3 && errReturn.length () != 6)
            || (errReturn.length () == 6 && ! errReturn.startsWith ("ERR")))
            throw new IOException ();
          try
          {
            final int errorInt = Integer.parseInt (errReturn.length () == 3 ? errReturn : errReturn.substring (3));
            instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, errorInt);
          }
          catch (NumberFormatException nfe)
          {
            throw new IOException ();
          }
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_SERVICE_REQUEST_ENABLE:
        {
          final int mask =
            (int) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_SERVICE_REQUEST_ENABLE);
          if (mask < 0 || mask > 15)
            throw new IllegalArgumentException ();
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMinimumIntegerDigits (2);
          df.setMaximumIntegerDigits (2);
          df.setMaximumFractionDigits (0);
          writeSync ("estb" + df.format (mask) + "\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_EXTERNAL_REFERENCE_LOCKED:
        {
          final String queryReturn = new String (writeAndReadEOISync ("EXTR?"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null
            || (queryReturn.length () != 1 && queryReturn.length () != 5)
            || (queryReturn.length () == 5 && ! queryReturn.startsWith ("EXTR")))
            throw new IOException ();
          final boolean isExternalReferenceLocked;
          switch (queryReturn.length () == 1 ? queryReturn : queryReturn.substring (4))
          {
            case "0": isExternalReferenceLocked = false; break;
            case "1": isExternalReferenceLocked = true; break;
            default: throw new IOException ();
          }
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, isExternalReferenceLocked);
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_RESPONSE_HEADER_CONTROL:
        {
          final boolean useHeader =
            (boolean) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_RESPONSE_HEADER_CONTROL);
          writeSync (useHeader ? "HEAD1\r" : "HEAD0\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_HIGH_VOLTAGE_OUTPUT:
        {
          final boolean highVoltage =
            (boolean) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_HIGH_VOLTAGE_OUTPUT);
          writeSync (highVoltage ? "HV1\r" : "HV0\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_IDENTIFICATION:
        {
          final String queryReturn = new String (writeAndReadEOISync ("ID?\r"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null || queryReturn.length () == 0)
            throw new IOException ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, queryReturn);
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_LOCAL:
        {
          writeSync ("LCL\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_AMPLITUDE_MODULATION:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_AMPLITUDE_MODULATION);
          writeSync (enable ? "MA1\r" : "MA0\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_DATA_TRANSFER_MODE:
        {
          final HP3325B_GPIB_Settings.DataTransferMode mode =
            (HP3325B_GPIB_Settings.DataTransferMode) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_DATA_TRANSFER_MODE);
          switch (mode)
          {
            case Mode1: writeSync ("MD0\r"); break;
            case Mode2: writeSync ("MD1\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_MARKER_FREQUENCY:
        {
          final double frequency_Hz = (double) instrumentCommand.get (HP3325B_InstrumentCommand.ICARG_HP3325B_MARKER_FREQUENCY);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (3);
          writeSync ("MF" + df.format (frequency_Hz) + "HZ\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_MODULATION_SOURCE_AMPLITUDE:
        {
          final double amplitude_Vpp = (double) instrumentCommand.get (
            HP3325B_InstrumentCommand.ICARG_HP3325B_MODULATION_SOURCE_AMPLITUDE);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (1);
          writeSync ("MOAM" + df.format (amplitude_Vpp) + "VO\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_WRITE_MODULATION_SOURCE_ARBITRARY_WAVEFORM:
        {
          final double[] waveformData =
            (double[]) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_WRITE_MODULATION_SOURCE_ARBITRARY_WAVEFORM);
          if (waveformData == null || waveformData.length == 0 || waveformData.length > 4096)
            throw new IllegalArgumentException ();
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumIntegerDigits (1);
          df.setMaximumFractionDigits (1); // XXX Is this really the case?
          final StringBuffer sb = new StringBuffer ();
          sb.append ("MOAR");
          sb.append (df.format (waveformData[0]));
          for (int i = 1; i < waveformData.length; i++)
          {
            sb.append (",");
            sb.append (df.format (waveformData[0]));
          }
          sb.append ("ENT\r");
          writeSync (sb.toString ());
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_MODULATION_SOURCE_FREQUENCY:
        {
          final double frequency_Hz = (double) instrumentCommand.get (
            HP3325B_InstrumentCommand.ICARG_HP3325B_MODULATION_SOURCE_FREQUENCY);
          if (frequency_Hz < 0 || frequency_Hz > 10000)
            throw new IllegalArgumentException ();
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (1);
          writeSync ("MOFR" + df.format (frequency_Hz) + "HZ\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_MODULATION_SOURCE_WAVEFORM_FUNCTION:
        {
          final HP3325B_GPIB_Settings.ModulationSourceWaveformFunction waveformFunction =
            (HP3325B_GPIB_Settings.ModulationSourceWaveformFunction) instrumentCommand.get (
            HP3325B_InstrumentCommand.ICARG_HP3325B_MODULATION_SOURCE_WAVEFORM_FUNCTION);
          switch (waveformFunction)
          {
            case Off:       writeSync ("MOFU0\r"); break;
            case Sine:      writeSync ("MOFU1\r"); break;
            case Square:    writeSync ("MOFU2\r"); break;
            case Arbitrary: writeSync ("MOFU3\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_PHASE_MODULATION:
        {
          final boolean enable =
            (boolean) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_PHASE_MODULATION);
          writeSync (enable ? "MP1\r" : "MP0\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_STATUS_BYTE_MASK:
        {
          final char maskChar =
            (char) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_STATUS_BYTE_MASK);
          if (maskChar < '@' || maskChar > 'O')
            throw new IllegalArgumentException ();
          writeSync ("MS" + maskChar + "\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_OPTION:
        {
          final EnumSet<HP3325B_GPIB_Settings.Option> optionsInstalled = getSettingsFromInstrumentSync_OptionsInstalled ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, optionsInstalled);
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_PHASE:
        {
          final double degrees = (double) instrumentCommand.get (
            HP3325B_InstrumentCommand.ICARG_HP3325B_PHASE);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (1);
          writeSync ("PH" + df.format (degrees) + "DE\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_RS232_STATUS_BYTE:
        {
          final byte statusByte = getSettingsFromInstrumentSync_statusByteRs232 ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, statusByte);
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_RECALL_STATE:
        {
          final int slot =
            (int) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_RECALL_STATE);
          if (slot < 0 || slot > 10)
            throw new IllegalArgumentException ();
          // We use the special slot index 10 to denote the "power-down" state ("RE-").
          if (slot == 10)
            writeSync ("RE-\r");
          else
            writeSync ("RE" + slot + "\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_REAR_FRONT:
        {
          final HP3325B_GPIB_Settings.RFOutputMode mode =
            (HP3325B_GPIB_Settings.RFOutputMode) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_REAR_FRONT);
          switch (mode)
          {
            case Front: writeSync ("RF1\r"); break;
            case Rear:  writeSync ("RF2\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_REMOTE:
        {
          writeSync ("RMT\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_RESET:
        {
          writeSync ("*RST\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_RESET_SINGLE_SWEEP:
        {
          writeSync ("RSW\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_START_CONTINUOUS_SWEEP:
        {
          writeSync ("SC\r");
          // XXX Affects settings?
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_SWEEP_MODE:
        {
          final HP3325B_GPIB_Settings.SweepMode mode =
            (HP3325B_GPIB_Settings.SweepMode) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_SWEEP_MODE);
          switch (mode)
          {
            case Linear:      writeSync ("SM1\r"); break;
            case Logarithmic: writeSync ("SM2\r"); break;
            case Discrete:    writeSync ("SM3\r"); break;
            default: throw new IllegalArgumentException ();
          }
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_SWEEP_STOP_FREQUENCY:
        {
          final double frequency_Hz = (double) instrumentCommand.get (
            HP3325B_InstrumentCommand.ICARG_HP3325B_SWEEP_STOP_FREQUENCY);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (3);
          writeSync ("SP" + df.format (frequency_Hz) + "HZ\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_STORE_STATE:
        {
          final int slot =
            (int) instrumentCommand.get (
              HP3325B_InstrumentCommand.ICARG_HP3325B_RECALL_STATE);
          if (slot < 0 || slot > 9)
            throw new IllegalArgumentException ();
          writeSync ("SR" + slot + "\r");
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_START_SINGLE_SWEEP:
        {
          writeSync ("SS\r");
          // XXX Affects settings?
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_SWEEP_START_FREQUENCY:
        {
          final double frequency_Hz = (double) instrumentCommand.get (
            HP3325B_InstrumentCommand.ICARG_HP3325B_SWEEP_START_FREQUENCY);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (3);
          writeSync ("ST" + df.format (frequency_Hz) + "HZ\r");
          newInstrumentSettings = getSettingsFromInstrumentSync ();
          break;
        }
        case HP3325B_InstrumentCommand.IC_HP3325B_SWEEP_TIME:
        {
          final double time_s = (double) instrumentCommand.get (
            HP3325B_InstrumentCommand.ICARG_HP3325B_SWEEP_TIME);
          final DecimalFormat df = new DecimalFormat ("#");
          df.setMaximumFractionDigits (1);
          writeSync ("TI" + df.format (time_s) + "SE\r");
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
