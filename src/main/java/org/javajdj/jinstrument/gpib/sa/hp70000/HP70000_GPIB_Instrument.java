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
package org.javajdj.jinstrument.gpib.sa.hp70000;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultInstrumentCommand;
import org.javajdj.jinstrument.DefaultSpectrumAnalyzerTrace;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.junits.Resolution;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.gpib.sa.AbstractGpibSpectrumAnalyzer;
import org.javajdj.jinstrument.SpectrumAnalyzer;
import org.javajdj.jinstrument.SpectrumAnalyzerSettings;
import org.javajdj.junits.Unit;
import org.javajdj.jinstrument.controller.gpib.GpibControllerCommand;

/** Implementation of {@link Instrument} and {@link SpectrumAnalyzer} for the HP-70000 MMS.
 *
 * <p>
 * Except where noted otherwise, the implementation follows
 *   'Programming Manual - HP 70000 Modular Spectrum Analyzer - HP 70900B Local Oscillator -
 *   Source-Controlled Modules - HP Part No. 70900-90284 - March 1994 - Edition A.0.0'.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP70000_GPIB_Instrument
  extends AbstractGpibSpectrumAnalyzer
  implements SpectrumAnalyzer
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP70000_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP70000_GPIB_Instrument (final GpibDevice device)
  {
    super (
      "HP-70000", // name
      device,     // device
      null,       // runnables
      null,       // targetServices
      true,       // addInitializationServices
      true,       // addStatusServices
      false,      // addSettingsServices
      true,       // addCommandProcessorServices
      false,      // addAcquisitionServices
      false,      // addHousekeepingServices
      false);     // addServiceRequestPollingServices
    // Make sure every settings update counts.
    // XXX We can change this once the byte[]-based settings/state implementation works [if ever].
    setOptimizeSettingsUpdates (false);
    setInstrumentInitializerTimeout_ms (2000L);
    setStatusCollectorPeriod_s (1.0);
    // setSettingsCollectorPeriod_s (2.0);
    // setGpibInstrumentServiceRequestCollectorPeriod_s (0.75);
    // setPollServiceRequestTimeout_ms (2000);
    setSerialPollTimeout_ms (1000);
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
      return "HP-70000 [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP70000_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP70000_GPIB_Instrument ((GpibDevice) device);
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
    return HP70000_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
  }

  @Override
  public String toString ()
  {
    return getInstrumentUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP70000_GPIB_Instrument
  // OPERATION SEMAPHORE
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  // We use a Semaphore for sequencing initilization, acquisition/srq, and command processing.
  // This save us the trouble of creating atomic operations for the controller...
  // It is not ideal, and more or less assumes exclusive access to controller and instrument.
  private final Semaphore operationSemaphore;
  
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
  
  public final static String INITIALIZATION_STRING = 
    "CONTS;"     // Continuous Sweep.
    + "RQS 4;"   // Generate GPIB Service Request at End of Sweep -> DOES NOT WORK WELL WITH PROLOGIX GPIB-ETHERNET.
//  + "RQS 119;" // Generate GPIB Service Request 0x77 -> always.
    + "TDF B;";  // Trace Data Format (Binary).
  
  @Override
  protected void initializeInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    try
    {
      this.operationSemaphore.acquire ();
      writeSync (HP70000_GPIB_Instrument.INITIALIZATION_STRING);
      final HP70000_GPIB_Settings settings = getSettingsFromInstrumentSyncImp ();
      if (settings == null)
        throw new IOException (); // Forces error state, so user can retry.
      final String idString = new String (writeAndReadEOISync ("ID?;"), Charset.forName ("US-ASCII")).trim ();
      final String idnString = new String (writeAndReadEOISync ("IDN?;"), Charset.forName ("US-ASCII")).trim ();
      final String configurationString = new String (writeAndReadEOISync ("CONFIG?;"), Charset.forName ("US-ASCII")).trim ();
      final HP70000_GPIB_Settings.MeasureMode measureMode = getMeasureModeDirect ();
      final double sourcePower_dBm = getSourcePower_dBmDirect ();
      setSourceActiveDirect (false);
      final HP70000_GPIB_Settings.SourceAlcMode sourceAlcMode = getSourceAlcModeDirect ();
      settingsReadFromInstrument (settings
        .withId (idString)
        .withIdentificationNumber (idnString)
        .withConfigurationString (configurationString)
        .withMeasureMode (measureMode)
        .withSourcePower_dBm (sourcePower_dBm)
        .withSourceActive (false)
        .withSourceAlcMode (sourceAlcMode)
        );
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

  protected InstrumentStatus getStatusFromInstrumentSync (final boolean lock)
    throws IOException, InterruptedException, TimeoutException
  {
    
    if (lock)
      this.operationSemaphore.acquire ();
    
    try
    {
      
      final byte statusByte = serialPollSync ();
      final String errorString;
      final String messageString;
      
      if (HP70000_GPIB_Status.isErrorPresent (statusByte))
        errorString = writeAndReadlnSync ("ERR?;");
      else
        errorString = null;
      
      if (HP70000_GPIB_Status.isMessagePresent (statusByte))
        messageString = writeAndReadlnSync ("MSG?;");
      else
        messageString = null;
      
      final HP70000_GPIB_Status status = HP70000_GPIB_Status.fromSerialPollStatusByteAndErrorStringAndMessageString (
        statusByte,
        errorString,
        messageString);
      
      if (status.isEndOfSweep ())
      {
        final InstrumentReading reading = getReadingFromInstrumentSync ();
        if (reading != null)
          readingReadFromInstrument (reading);
      }
      
      return status;
      
    }
    finally
    {
      if (lock)
        this.operationSemaphore.release ();
    }
    
  }

  @Override
  protected InstrumentStatus getStatusFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    return getStatusFromInstrumentSync (true);
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
  
  protected HP70000_GPIB_Settings getSettingsFromInstrumentSyncImp ()
    throws IOException, InterruptedException, TimeoutException
  {
    // XXX TODO: Picking up the state (< 1000 bytes) takes a lot of time with zero benefits (currently).
    final GpibControllerCommand statePreampleCommand = generateWriteAndReadNCommand ("STATE?;", 14); // Fixed size!
    final GpibControllerCommand stateCommand = generateReadNCommand (1065); // N will be overwritten!
    final GpibControllerCommand[] atomicSequenceCommands = new GpibControllerCommand[]
    {
      // REMEMBER TO PICK UP THE VALUE BELOW!
      // NOTE: ONCE WE CAN DECODE THE STATE PROPERLY, WE CAN EXTRACT THE PARAMETERS DIRECTLY :-)...
      generateWriteAndReadlnCommand ("TRDEF TRA,?;"),
      generateWriteAndReadlnCommand ("CF?;"),
      generateWriteAndReadlnCommand ("SP?;"),
      generateWriteAndReadlnCommand ("RB?;"),
      generateWriteAndReadlnCommand ("VB?;"),
      generateWriteAndReadlnCommand ("ST?;"),
      generateWriteAndReadlnCommand ("RL?;"),
      generateWriteAndReadlnCommand ("AT?;"),
      generateWriteAndReadlnCommand ("DET?;"),
      statePreampleCommand,
      generateUserRunnableCommand (() ->
      {
        // The last two bytes of the preamble encode the length of the rest of the state, excluding the terminating 0x0a.
        final byte[] statePreamble = (byte[]) statePreampleCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
        // Sanity check to prevent us from trying a lengthy block in vain.
        if (! new String (statePreamble, Charset.forName ("US-ASCII")).startsWith ("#A"))
          throw new IllegalArgumentException ();
        if (statePreamble[2] != 4)
          throw new IllegalArgumentException ();
        if (! new String (statePreamble, Charset.forName ("US-ASCII")).substring (3).startsWith ("2STATE"))
          throw new IllegalArgumentException ();
        final int length =
          (statePreamble[statePreamble.length - 2] & 0xff) * 256
          + (statePreamble[statePreamble.length - 1] & 0xff);
        stateCommand.put (GpibControllerCommand.CCARG_GPIB_READ_N, length + 1 /* MUST ADD ONE FOR 0x0A! */);
      }),
      stateCommand
    };
    atomicSequenceSync (atomicSequenceCommands);
    final byte[] state = (byte[]) stateCommand.get (GpibControllerCommand.CCRET_VALUE_KEY);
    int i = 0;
    final int traceLength = Integer.parseInt (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double centerFrequency_MHz = 1e-6 * Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double span_MHz = 1e-6 * Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double resolutionBandwidth_Hz = Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double videoBandwidth_Hz = Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double sweepTime_s = Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double referenceLevel_dBm = Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final double rfAttenuation_dB = Double.parseDouble (
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ());
    final String detString = 
      new String (((byte[]) atomicSequenceCommands[i++].get (GpibControllerCommand.CCRET_VALUE_KEY)), Charset.forName ("US-ASCII"))
        .trim ();
    final HP70000_GPIB_Settings oldSettings = (HP70000_GPIB_Settings) getCurrentInstrumentSettings ();
    return new HP70000_GPIB_Settings (
      state,
      Unit.UNIT_dBm, // XXX This needs proper checking; may be in LIN or zero-span...
      centerFrequency_MHz,
      span_MHz,
      resolutionBandwidth_Hz,
      false,
      videoBandwidth_Hz,
      false,
      sweepTime_s,
      false,
      referenceLevel_dBm,
      rfAttenuation_dB,
      false,
      traceLength,
      oldSettings != null ? oldSettings.getId () : "unknown",
      oldSettings != null ? oldSettings.getConfigurationString () : null,
      oldSettings != null ? oldSettings.getMeasureMode () : null,
      oldSettings != null ? oldSettings.getIdentificationNumber () : null,
      oldSettings != null ? oldSettings.getSourcePower_dBm () : Double.NaN,
      oldSettings != null ? oldSettings.isSourceActive () : false,
      oldSettings != null ? oldSettings.getSourceAlcMode () : null
    );
  }
 
  @Override
  public SpectrumAnalyzerSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    // We do not use/support getting settings from the framework (AbstractInstrument).
    // Use getSettingsFromInstrumentSyncImp internally instead.
    LOG.log (Level.SEVERE, "Illegal call to getSettingsFromInstrumentSync - this is a software error on instrument {0}!",
      new Object[]{this});
    throw new UnsupportedOperationException ();
  }
 
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // INSTRUMENT READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private double binaryTracePairToDouble (final byte ms, final byte ls)
  {
    final short mData = (short) (((ms & 0xff) << 8) | (ls & 0xff));
    return 0.01 * mData;
  }
  
  @Override
  protected final InstrumentReading getReadingFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    // XXX Why go through the trouble of getting the settings from the instrument (again)?
    final HP70000_GPIB_Settings settings = (HP70000_GPIB_Settings) getSettingsFromInstrumentSyncImp ();
    settingsReadFromInstrument (settings);
    final int traceLength = settings.getTraceLength ();
    // In the approach below we initiate the trace and await its completion and transfer.
    // This fails with high sweep-time settings, say a few seconds and higher.
    // TDF B: Two bytes per trace point.
    // final byte[] binaryTraceBytes = writeAndReadNSync ("SNGLS;TDF B;TS;TRA?;\r\n", 2 * traceLength);
    // In the other approach, we directly read Trace A, assuming we are invoked from a Service Request.
    final byte[] binaryTraceBytes = writeAndReadNSync ("TRA?;", 2 * traceLength);
    final double samples[] = new double[traceLength];
    int i_buf = 0;
    for (int s = 0; s < samples.length; s++)
      samples[s] = binaryTracePairToDouble (binaryTraceBytes[i_buf++], binaryTraceBytes[i_buf++]);
    return new DefaultSpectrumAnalyzerTrace (
      settings,
      null,
      samples,
      Unit.UNIT_dBm,       // XXX Always correct?
      Resolution.DIGITS_5, // XXX Educated guess: dynamic range > 100 dB, 0.01 dB resolution...
      false, // XXX
      null,
      false,
      false,
      false);
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
    LOG.log (Level.WARNING, "SRQ; statusByte=", statusByte);
    final InstrumentReading reading = getReadingFromInstrumentSync ();
    if (reading != null)
      readingReadFromInstrument (reading);
    else
      LOG.log (Level.WARNING, "Null Reading on {0}!", this.toString ());
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HP70000_GPIB_Instrument
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  // ABORT
  
  /** Interrupts operation of all user-defined functions (asynchronous).
   * 
   * <p>
   * ABORT
   * 
   * @throws IOException          If an I/O Exception occurred while pushing the command to the instrument.
   * @throws InterruptedException If an interrupt of the current {@code Thread} was detected
   *                                while pushing the command to the instrument.
   * 
   */
  public final void abort ()
    throws IOException, InterruptedException
  {
    // ABORT
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_ABORT));
  }
  
  // CONFIG
  
  public final String getConfigurationStringSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // CONFIG?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_CONFIGURATION_STRING);
    addAndProcessCommandSync (command, timeout, unit);
    return (String) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getConfigurationStringASync ()
    throws IOException, InterruptedException
  {
    // CONFIG?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_CONFIGURATION_STRING);
    addCommand (command);
  }
  
  // ID
  
  public final String getIdSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // ID?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_ID);
    addAndProcessCommandSync (command, timeout, unit);
    return (String) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getIdASync ()
    throws IOException, InterruptedException
  {
    // ID?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_ID);
    addCommand (command);
  }

  // IDN
  
  public final String getIdentificationNumberSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // IDN?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_IDENTIFICATION_NUMBER);
    addAndProcessCommandSync (command, timeout, unit);
    return (String) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  // MEASURE
  
  protected final HP70000_GPIB_Settings.MeasureMode getMeasureModeDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // MEASURE?
    final String queryReturn = new String (writeAndReadEOISync ("MEASURE?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    switch (queryReturn)
    {
      case "SA":
        return HP70000_GPIB_Settings.MeasureMode.SpectrumAnalysis;
      case "SR":
        return HP70000_GPIB_Settings.MeasureMode.StimulusResponse;
      case "SRMILCPL":
        return HP70000_GPIB_Settings.MeasureMode.SRMilCpl;
      default:
        throw new IOException ();
    }
  }
    
  public final HP70000_GPIB_Settings.MeasureMode getMeasureModeSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // MEASURE?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_MEASURE_MODE);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP70000_GPIB_Settings.MeasureMode) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getMeasureModeASync ()
    throws IOException, InterruptedException
  {
    // MEASURE?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_MEASURE_MODE);
    addCommand (command);
  }
    
  protected final void setMeasureModeDirect (final HP70000_GPIB_Settings.MeasureMode measureMode)
    throws IOException, InterruptedException, TimeoutException
  {
    // MEASURE
    switch (measureMode)
    {
      case SpectrumAnalysis: writeSync ("MEASURE SA;"); break;
      case StimulusResponse: writeSync ("MEASURE SR;"); break;
      case SRMilCpl:         writeSync ("MEASURE SRMILCPL;"); break;
      default: throw new IllegalArgumentException ();
    }
  }
    
  public final void setMeasureMode (final HP70000_GPIB_Settings.MeasureMode measureMode)
    throws IOException, InterruptedException
  {
    // MEASURE
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_MEASURE_MODE,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_MEASURE_MODE, measureMode));
  }
  
  // SRCPWR
  
  protected final double getSourcePower_dBmDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPWR?
    final String queryReturn = new String (writeAndReadEOISync ("SRCPWR?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final double sourcePower_dBm; // XXX Are we sure that unit is always dBm?
    try
    {
      sourcePower_dBm = Double.parseDouble (queryReturn);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return sourcePower_dBm;
  }
    
  public final double getSourcePower_dBmSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPWR?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourcePower_dBmASync ()
    throws IOException, InterruptedException
  {
    // SRCPWR?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER);
    addCommand (command);
  }
    
  protected final void setSourcePower_dBmDirect (final double sourcePower_dBm)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPWR
    writeSync ("SRCPWR " + Double.toString (sourcePower_dBm) + "DBM;");
  }
    
  public final void setSourcePower_dBm (final double sourcePower_dBm)
    throws IOException, InterruptedException
  {
    // SRCPWR
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER, sourcePower_dBm));
  }
  
  protected final void setSourceActiveDirect (final boolean sourceActive)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPWR
    writeSync ("SRCPWR " + (sourceActive ? "ON" : "OFF") + ";");
  }
    
  public final void setSourceActive (final boolean sourceActive)
    throws IOException, InterruptedException
  {
    // SRCPWR
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_ACTIVE,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_ACTIVE, sourceActive));
  }
    
  // SRCALC
  
  protected final HP70000_GPIB_Settings.SourceAlcMode getSourceAlcModeDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCALC?
    final String queryReturn = new String (writeAndReadEOISync ("SRCALC?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final HP70000_GPIB_Settings.SourceAlcMode sourceAlcMode;
    switch (queryReturn)
    {
      case "NORM": sourceAlcMode = HP70000_GPIB_Settings.SourceAlcMode.Normal;      break;
      case "ALT":  sourceAlcMode = HP70000_GPIB_Settings.SourceAlcMode.Alternative; break;
      case "EXT":  sourceAlcMode = HP70000_GPIB_Settings.SourceAlcMode.External;    break;
      default: throw new IOException ();
    }
    return sourceAlcMode;
  }
    
  public final HP70000_GPIB_Settings.SourceAlcMode getSourceAlcModeSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCALC?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_ALC_MODE);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP70000_GPIB_Settings.SourceAlcMode) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourceAlcModeASync ()
    throws IOException, InterruptedException
  {
    // SRCALC?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_ALC_MODE);
    addCommand (command);
  }
    
  protected final void setSourceAlcModeDirect (final HP70000_GPIB_Settings.SourceAlcMode sourceAlcMode)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCALC
    switch (sourceAlcMode)
    {
      case Normal:      writeSync ("SRCALC NORM;"); break;
      case Alternative: writeSync ("SRCALC ALT;");  break;
      case External:    writeSync ("SRCALC EXT;");  break;
      default: throw new IllegalArgumentException ();
    }
  }
    
  public final void setSourceAlcMode (final HP70000_GPIB_Settings.SourceAlcMode sourceAlcMode)
    throws IOException, InterruptedException
  {
    // SRCALC
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_ALC_MODE,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_ALC_MODE, sourceAlcMode));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractInstrument
  // PROCESS COMMAND
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  @Override
  protected final void processCommand (final InstrumentCommand instrumentCommand)
    throws IOException, InterruptedException, TimeoutException
  {
    processCommand (instrumentCommand, true);
  }
  
  protected final void processCommand (final InstrumentCommand instrumentCommand, final boolean topLevel)
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
    if (topLevel)
      this.operationSemaphore.acquire ();
    final GpibDevice device = (GpibDevice) getDevice ();
    final HP70000_GPIB_Settings instrumentSettings = (HP70000_GPIB_Settings) getCurrentInstrumentSettings ();
    InstrumentSettings newInstrumentSettings = null;
    try
    {
      switch (commandString)
      {
        case InstrumentCommand.IC_NOP_KEY:
          break;
        case InstrumentCommand.IC_GET_SETTINGS_KEY:
        {
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_RF_FREQUENCY:
        {
          final double centerFrequency_MHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_FREQUENCY_MHZ);
          writeSync ("CF " + centerFrequency_MHz + " MZ;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_RF_SPAN:
        {
          final double span_MHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_SPAN_MHZ);
          writeSync ("SP " + span_MHz + " MZ;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_RESOLUTION_BANDWIDTH:
        {
          final double resolutionBandwidth_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RESOLUTION_BANDWIDTH_HZ);
          writeSync ("RB " + resolutionBandwidth_Hz + " HZ;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_SET_RESOLUTION_BANDWIDTH_COUPLED:
        {
          writeSync ("RB AUTO;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_VIDEO_BANDWIDTH:
        {
          final double videoBandwidth_Hz = (double) instrumentCommand.get (InstrumentCommand.ICARG_VIDEO_BANDWIDTH_HZ);
          writeSync ("VB " + videoBandwidth_Hz + " HZ;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_SET_VIDEO_BANDWIDTH_COUPLED:
        {
          writeSync ("VB AUTO;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_SWEEP_TIME:
        {
          final double sweepTime_s = (double) instrumentCommand.get (InstrumentCommand.ICARG_SWEEP_TIME_S);
          writeSync ("ST " + sweepTime_s + " SC;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_SET_SWEEP_TIME_COUPLED:
        {
          writeSync ("ST AUTO;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_REFERENCE_LEVEL:
        {
          final double referenceLevel_dBm = (double) instrumentCommand.get (InstrumentCommand.ICARG_REFERENCE_LEVEL_DBM);
          writeSync ("RL " + referenceLevel_dBm + " DM;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_RF_ATTENUATION:
        {
          final double rfAttenuation_dB = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_ATTENUATION_DB);
          writeSync ("AT " + rfAttenuation_dB + " DB;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case InstrumentCommand.IC_SET_RF_ATTENUATION_COUPLED:
        {
          writeSync ("AT AUTO;");
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_ABORT:
        {
          // ABORT
          writeSync ("ABORT;");
          // XXX Does this affect the Settings?
          newInstrumentSettings = getSettingsFromInstrumentSyncImp ();
          break;          
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_ID:
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
        case HP70000_InstrumentCommand.IC_HP70000_GET_IDENTIFICATION_NUMBER:
        {
          // IDN?
          final String queryReturn = new String (writeAndReadEOISync ("IDN?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, queryReturn);
          if (instrumentSettings != null && ! queryReturn.equals (instrumentSettings.getIdentificationNumber ()))
            newInstrumentSettings = instrumentSettings.withIdentificationNumber (queryReturn);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_CONFIGURATION_STRING:
        {
          // ID?
          final String queryReturn = new String (writeAndReadEOISync ("CONFIG?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, queryReturn);
          if (instrumentSettings != null && ! queryReturn.equals (instrumentSettings.getConfigurationString ()))
            newInstrumentSettings = instrumentSettings.withConfigurationString (queryReturn);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_MEASURE_MODE:
        {
          // MEASURE?
          final HP70000_GPIB_Settings.MeasureMode measureMode = getMeasureModeDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, measureMode);
          if (instrumentSettings != null && ! measureMode.equals (instrumentSettings.getMeasureMode ()))
            newInstrumentSettings = instrumentSettings.withMeasureMode (measureMode);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_MEASURE_MODE:
        {
          // MEASURE
          final HP70000_GPIB_Settings.MeasureMode measureMode =
            (HP70000_GPIB_Settings.MeasureMode) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_MEASURE_MODE);
          setMeasureModeDirect (measureMode);
          if (instrumentSettings != null && ! measureMode.equals (instrumentSettings.getMeasureMode ()))
            newInstrumentSettings = instrumentSettings.withMeasureMode (measureMode);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER:
        {
          // SRCPWR?
          final double sourcePower_dBm = getSourcePower_dBmDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourcePower_dBm);
          if (instrumentSettings != null && sourcePower_dBm != instrumentSettings.getSourcePower_dBm ())
            newInstrumentSettings = instrumentSettings.withSourcePower_dBm (sourcePower_dBm);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER:
        {
          // SRCPWR
          final double sourcePower_dBm =
            (double) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER);
          setSourcePower_dBmDirect (sourcePower_dBm);
          final double newSourcePower_dBm = getSourcePower_dBmDirect ();
          if (instrumentSettings != null
            && (newSourcePower_dBm != instrumentSettings.getSourcePower_dBm () || ! instrumentSettings.isSourceActive ()))
            newInstrumentSettings = instrumentSettings.withSourcePower_dBm (newSourcePower_dBm).withSourceActive (true);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_ACTIVE:
        {
          // SRCPWR
          final boolean sourceActive =
            (boolean) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_ACTIVE);
          setSourceActiveDirect (sourceActive);
          if (instrumentSettings != null && sourceActive != instrumentSettings.isSourceActive ())
            newInstrumentSettings = instrumentSettings.withSourceActive (sourceActive);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_ALC_MODE:
        {
          // SRCALC?
          final HP70000_GPIB_Settings.SourceAlcMode sourceAlcMode = getSourceAlcModeDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourceAlcMode);
          if (instrumentSettings != null && ! sourceAlcMode.equals (instrumentSettings.getSourceAlcMode ()))
            newInstrumentSettings = instrumentSettings.withSourceAlcMode (sourceAlcMode);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_ALC_MODE:
        {
          // SRCALC
          final HP70000_GPIB_Settings.SourceAlcMode sourceAlcMode =
            (HP70000_GPIB_Settings.SourceAlcMode)
              instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_ALC_MODE);
          setSourceAlcModeDirect (sourceAlcMode);
          if (instrumentSettings != null && ! sourceAlcMode.equals (instrumentSettings.getSourceAlcMode ()))
            newInstrumentSettings = instrumentSettings.withSourceAlcMode (sourceAlcMode);
          break;
        }
        default:
          throw new UnsupportedOperationException ();
      }
    }
    finally
    {
      if (topLevel)
        this.operationSemaphore.release ();
    }
    if (newInstrumentSettings != null)
    {
      settingsReadFromInstrument (newInstrumentSettings);
      if (topLevel)
        statusReadFromInstrument (getStatusFromInstrumentSync (true));
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
