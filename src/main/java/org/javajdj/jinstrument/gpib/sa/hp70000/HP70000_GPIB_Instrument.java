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
      final double sourceAmDepth_percent = getSourceAmDepth_percentDirect ();
      setSourceAmActiveDirect (false);
      final double sourceAmFrequency_Hz = getSourceAmFrequency_HzDirect ();
      final double sourceAttenuation_dB = getSourceAttenuation_dBDirect ();
      setSourceAttenuationCoupledDirect (true);
      final boolean sourceBlanking = isSourceBlankingDirect ();
      final HP70000_GPIB_Settings.SourceModulationInput sourceModulationInput = getSourceModulationInputDirect ();
      final HP70000_GPIB_Settings.SourceOscillator sourceOscillator = getSourceOscillatorDirect ();
      final double sourcePowerOffset_dB = getSourcePowerOffset_dBDirect ();
      setSourcePowerStepSizeModeDirect (HP70000_GPIB_Settings.SourcePowerStepSizeMode.Automatic);
      final double sourcePowerStepSize_dB = getSourcePowerStepSize_dBDirect ();
      setSourcePowerSweepActiveDirect (false);
      final double sourcePowerSweepRange_dB = getSourcePowerSweepRange_dBDirect ();
      final double sourceTracking_Hz = getSourceTracking_HzDirect ();
      setSourcePeakTrackingAutoDirect (false);
      settingsReadFromInstrument (settings
        .withId (idString)
        .withIdentificationNumber (idnString)
        .withConfigurationString (configurationString)
        .withMeasureMode (measureMode)
        .withSourcePower_dBm (sourcePower_dBm)
        .withSourceActive (false)
        .withSourceAlcMode (sourceAlcMode)
        .withSourceAmDepth_percent (sourceAmDepth_percent)
        .withSourceAmActive (false)
        .withSourceAmFrequency_Hz (sourceAmFrequency_Hz)
        .withSourceAttenuation_dB (sourceAttenuation_dB)
        .withSourceAttenuationCoupled (true)
        .withSourceBlanking (sourceBlanking)
        .withSourceModulationInput (sourceModulationInput)
        .withSourceOscillator (sourceOscillator)
        .withSourcePowerOffset_dB (sourcePowerOffset_dB)
        .withSourcePowerStepSizeMode (HP70000_GPIB_Settings.SourcePowerStepSizeMode.Automatic)
        .withSourcePowerStepSize_dB (sourcePowerStepSize_dB)
        .withSourcePowerSweepActive (false)
        .withSourcePowerSweepRange_dB (sourcePowerSweepRange_dB)
        .withSourceTracking_Hz (sourceTracking_Hz)
        .withSourcePeakTrackingAuto (false)
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
      oldSettings != null ? oldSettings.getSourceAlcMode () : null,
      oldSettings != null ? oldSettings.getSourceAmDepth_percent () : Double.NaN,
      oldSettings != null ? oldSettings.isSourceAmActive () : false,
      oldSettings != null ? oldSettings.getSourceAmFrequency_Hz () : Double.NaN,
      oldSettings != null ? oldSettings.getSourceAttenuation_dB (): Double.NaN,
      oldSettings != null ? oldSettings.isSourceAttenuationCoupled (): false,
      oldSettings != null ? oldSettings.isSourceBlanking (): false,
      oldSettings != null ? oldSettings.getSourceModulationInput () : null,
      oldSettings != null ? oldSettings.getSourceOscillator () : null,
      oldSettings != null ? oldSettings.getSourcePowerOffset_dB () : 0,
      oldSettings != null ? oldSettings.getSourcePowerStepSizeMode () : null,
      oldSettings != null ? oldSettings.getSourcePowerStepSize_dB () : 0,
      oldSettings != null ? oldSettings.isSourcePowerSweepActive () : false,
      oldSettings != null ? oldSettings.getSourcePowerSweepRange_dB () : 0,
      oldSettings != null ? oldSettings.getSourceTracking_Hz () : 0,  // XXX Read this every trace update with peak-tracking?
      oldSettings != null ? oldSettings.isSourcePeakTrackingAuto () : false
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
    // XXX This will activate the locking?
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
    
  // IP
  
  protected final void presetDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // IP
    writeSync ("IP;");
  }
  
  public final void presetSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // IP
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_PRESET_STATE);
    addAndProcessCommandSync (command, timeout, unit);
  }
    
  public final void presetASync ()
    throws IOException, InterruptedException
  {
    // IP
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_PRESET_STATE);
    addCommand (command);
  }
    
  public final void preset ()
    throws IOException, InterruptedException
  {
    // IP
    presetASync ();
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
  
  // SRCAM
  
  protected final double getSourceAmDepth_percentDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAM?
    final String queryReturn = new String (writeAndReadEOISync ("SRCAM?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final double sourceAmDepth_percent;
    try
    {
      sourceAmDepth_percent = Double.parseDouble (queryReturn);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return sourceAmDepth_percent;
  }
    
  public final double getSourceAmDepth_percentSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAM?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_AM_DEPTH);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourceAmDepth_percentASync ()
    throws IOException, InterruptedException
  {
    // SRCAM?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_AM_DEPTH);
    addCommand (command);
  }
    
  protected final void setSourceAmDepth_percentDirect (final double sourceAmDepth_percent)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAM
    writeSync ("SRCAM " + Double.toString (sourceAmDepth_percent) + ";");
  }
    
  public final void setSourceAmDepth_percent (final double sourceAmDepth_percent)
    throws IOException, InterruptedException
  {
    // SRCAM
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_AM_DEPTH,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_AM_DEPTH, sourceAmDepth_percent));
  }
  
  protected final void setSourceAmActiveDirect (final boolean sourceAmActive)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAM
    writeSync ("SRCAM " + (sourceAmActive ? "ON" : "OFF") + ";");
  }
    
  public final void setSourceAmActive (final boolean sourceAmActive)
    throws IOException, InterruptedException
  {
    // SRCAM
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_AM_ACTIVE,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_AM_ACTIVE, sourceAmActive));
  }
    
  // SRCAMF
  
  protected final double getSourceAmFrequency_HzDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAMF?
    final String queryReturn = new String (writeAndReadEOISync ("SRCAMF?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final double sourceAmFrequency_Hz;
    try
    {
      sourceAmFrequency_Hz = Double.parseDouble (queryReturn);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return sourceAmFrequency_Hz;
  }
    
  public final double getSourceAmFrequency_HzSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAMF?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_AM_FREQUENCY);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourceAmFrequency_HzASync ()
    throws IOException, InterruptedException
  {
    // SRCAMF?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_AM_FREQUENCY);
    addCommand (command);
  }
    
  protected final void setSourceAmFrequency_HzDirect (final double sourceAmFrequency_Hz)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAMF
    writeSync ("SRCAMF " + Double.toString (sourceAmFrequency_Hz) + "HZ;");
  }
    
  public final void setSourceAmFrequency_Hz (final double sourceAmFrequency_Hz)
    throws IOException, InterruptedException
  {
    // SRCAMF
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_AM_FREQUENCY,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_AM_FREQUENCY, sourceAmFrequency_Hz));
  }
    
  // SRCAT
  
  protected final double getSourceAttenuation_dBDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAT?
    final String queryReturn = new String (writeAndReadEOISync ("SRCAT?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final double sourceAttenuation_dB;
    try
    {
      sourceAttenuation_dB = Double.parseDouble (queryReturn);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return sourceAttenuation_dB;
  }
    
  public final double getSourceAttenuation_dBSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAT?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_ATTENUATION);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourceAttenuation_dBASync ()
    throws IOException, InterruptedException
  {
    // SRCAT?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_ATTENUATION);
    addCommand (command);
  }
    
  protected final void setSourceAttenuation_dBDirect (final double sourceAttenuation_dB)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAT
    writeSync ("SRCAT " + Double.toString (sourceAttenuation_dB) + "DB;");
  }
    
  public final void setSourceAttenuation_dB (final double sourceAttenuation_dB)
    throws IOException, InterruptedException
  {
    // SRCAT
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_ATTENUATION,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_ATTENUATION, sourceAttenuation_dB));
  }
  
  protected final void setSourceAttenuationCoupledDirect (final boolean sourceAttenuationCoupled)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCAT
    writeSync ("SRCAT " + (sourceAttenuationCoupled ? "AUTO" : "MAN") + ";");
  }
    
  public final void setSourceAttenuationCoupled (final boolean sourceAttenuationCoupled)
    throws IOException, InterruptedException
  {
    // SRCAT
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_ATTENUATION_COUPLED,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_ATTENUATION_COUPLED, sourceAttenuationCoupled));
  }
    
  // SRCBLNK
  
  protected final boolean isSourceBlankingDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCBLNK?
    final String queryReturn = new String (writeAndReadEOISync ("SRCBLNK?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final boolean sourceBlanking;
    switch (queryReturn)
    {
      case "0": sourceBlanking = false; break;
      case "1": sourceBlanking = true;  break;
      default: throw new IOException ();
    }
    return sourceBlanking;
  }
  
  public final boolean isSourceBlankingSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCBLNK?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_BLANKING);
    addAndProcessCommandSync (command, timeout, unit);
    return (boolean) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void isSourceBlankingASync ()
    throws IOException, InterruptedException
  {
    // SRCBLNK?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_BLANKING);
    addCommand (command);
  }
    
  protected final void setSourceBlankingDirect (final boolean sourceBlanking)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCBLNK
    writeSync ("SRCBLNK " + (sourceBlanking ? "ON" : "OFF") + ";");
  }
    
  public final void setSourceBlanking (final boolean sourceBlanking)
    throws IOException, InterruptedException
  {
    // SRCBLNK
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_BLANKING,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_BLANKING, sourceBlanking));
  }
    
  // SRCMOD
  
  protected final HP70000_GPIB_Settings.SourceModulationInput getSourceModulationInputDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCMOD?
    final String queryReturn = new String (writeAndReadEOISync ("SRCMOD?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final HP70000_GPIB_Settings.SourceModulationInput sourceModulationInput;
    switch (queryReturn)
    {
      case "INT": sourceModulationInput = HP70000_GPIB_Settings.SourceModulationInput.Internal; break;
      case "EXT": sourceModulationInput = HP70000_GPIB_Settings.SourceModulationInput.External; break;
      default: throw new IOException ();
    }
    return sourceModulationInput;
  }
    
  public final HP70000_GPIB_Settings.SourceModulationInput getSourceModulationInputSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCMOD?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_MODULATION_INPUT);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP70000_GPIB_Settings.SourceModulationInput) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourceModulationInputASync ()
    throws IOException, InterruptedException
  {
    // SRCMOD?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_MODULATION_INPUT);
    addCommand (command);
  }
    
  protected final void setSourceModulationInputDirect (final HP70000_GPIB_Settings.SourceModulationInput sourceModulationInput)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCMOD
    switch (sourceModulationInput)
    {
      case Internal: writeSync ("SRCMOD INT;"); break;
      case External: writeSync ("SRCMOD EXT;");  break;
      default: throw new IllegalArgumentException ();
    }
  }
    
  public final void setSourceModulationInput (final HP70000_GPIB_Settings.SourceModulationInput sourceModulationInput)
    throws IOException, InterruptedException
  {
    // SRCMOD
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_MODULATION_INPUT,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_MODULATION_INPUT, sourceModulationInput));
  }
  
  // SRCOSC
  
  protected final HP70000_GPIB_Settings.SourceOscillator getSourceOscillatorDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCOSC?
    final String queryReturn = new String (writeAndReadEOISync ("SRCOSC?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final HP70000_GPIB_Settings.SourceOscillator sourceOscillator;
    switch (queryReturn)
    {
      case "INT": sourceOscillator = HP70000_GPIB_Settings.SourceOscillator.Internal; break;
      case "EXT": sourceOscillator = HP70000_GPIB_Settings.SourceOscillator.External; break;
      default: throw new IOException ();
    }
    return sourceOscillator;
  }
    
  public final HP70000_GPIB_Settings.SourceOscillator getSourceOscillatorSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCOSC?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_OSCILLATOR);
    addAndProcessCommandSync (command, timeout, unit);
    return (HP70000_GPIB_Settings.SourceOscillator) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourceOscillatorASync ()
    throws IOException, InterruptedException
  {
    // SRCOSC?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_OSCILLATOR);
    addCommand (command);
  }
    
  protected final void setSourceOscillatorDirect (final HP70000_GPIB_Settings.SourceOscillator sourceOscillator)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCOSC
    switch (sourceOscillator)
    {
      case Internal: writeSync ("SRCOSC INT;"); break;
      case External: writeSync ("SRCOSC EXT;");  break;
      default: throw new IllegalArgumentException ();
    }
  }
    
  public final void setSourceOscillator (final HP70000_GPIB_Settings.SourceOscillator sourceOscillator)
    throws IOException, InterruptedException
  {
    // SRCOSC
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_OSCILLATOR,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_OSCILLATOR, sourceOscillator));
  }
  
  // SRCPOFS
  
  protected final double getSourcePowerOffset_dBDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPOFS?
    final String queryReturn = new String (writeAndReadEOISync ("SRCPOFS?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final double sourcePowerOffset_dB;
    try
    {
      sourcePowerOffset_dB = Double.parseDouble (queryReturn);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return sourcePowerOffset_dB;
  }
    
  public final double getSourcePowerOffset_dBSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPOFS?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER_OFFSET);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourcePowerOffset_dBASync ()
    throws IOException, InterruptedException
  {
    // SRCPOFS?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER_OFFSET);
    addCommand (command);
  }
    
  protected final void setSourcePowerOffset_dBDirect (final double sourcePowerOffset_dB)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPOFS
    writeSync ("SRCPOFS " + Double.toString (sourcePowerOffset_dB) + "DB;");
  }
    
  public final void setSourcePowerOffset_dB (final double sourcePowerOffset_dB)
    throws IOException, InterruptedException
  {
    // SRCPOFS
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER_OFFSET,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER_OFFSET, sourcePowerOffset_dB));
  }
  
  // SRCPSTP
  
  protected final double getSourcePowerStepSize_dBDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPSTP?
    final String queryReturn = new String (writeAndReadEOISync ("SRCPSTP?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final double sourcePowerStepSize_dB;
    try
    {
      sourcePowerStepSize_dB = Double.parseDouble (queryReturn);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return sourcePowerStepSize_dB;
  }
    
  public final double getSourcePowerStepSize_dBSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPSTP?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER_STEP_SIZE);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourcePowerStepSize_dBASync ()
    throws IOException, InterruptedException
  {
    // SRCPSTP?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER_STEP_SIZE);
    addCommand (command);
  }
    
  protected final void setSourcePowerStepSize_dBDirect (final double sourcePowerStepSize_dB)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPSTP
    writeSync ("SRCPSTP " + Double.toString (sourcePowerStepSize_dB) + "DB;");
  }
    
  public final void setSourcePowerStepSize_dB (final double sourcePowerStepSize_dB)
    throws IOException, InterruptedException
  {
    // SRCPSTP
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER_STEP_SIZE,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER_STEP_SIZE, sourcePowerStepSize_dB));
  }
  
  protected final void setSourcePowerStepSizeModeDirect (
    final HP70000_GPIB_Settings.SourcePowerStepSizeMode sourcePowerStepSizeMode)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPSTP
    switch (sourcePowerStepSizeMode)
    {
      case Automatic: writeSync ("SRCPSTP AUTO;"); break;
      case Manual:    writeSync ("SRCPSTP MAN;");  break;
      default: throw new IllegalArgumentException ();
    }
  }
    
  public final void setSourcePowerStepSizeMode (
    final HP70000_GPIB_Settings.SourcePowerStepSizeMode sourcePowerStepSizeMode)
    throws IOException, InterruptedException
  {
    // SRCPSTP
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER_STEP_SIZE_MODE,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER_STEP_SIZE_MODE, sourcePowerStepSizeMode));
  }
    
  // SRCPSWP
  
  protected final double getSourcePowerSweepRange_dBDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPSWP?
    final String queryReturn = new String (writeAndReadEOISync ("SRCPSWP?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final double sourcePowerSweepRange_dB;
    try
    {
      sourcePowerSweepRange_dB = Double.parseDouble (queryReturn);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return sourcePowerSweepRange_dB;
  }
    
  public final double getSourcePowerSweepRange_dBSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPSWP?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER_SWEEP_RANGE);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourcePowerSweepRange_dBASync ()
    throws IOException, InterruptedException
  {
    // SRCPSWP?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER_SWEEP_RANGE);
    addCommand (command);
  }
    
  protected final void setSourcePowerSweepRange_dBDirect (final double sourcePowerSweepRange_dB)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPSWP
    writeSync ("SRCPSWP " + Double.toString (sourcePowerSweepRange_dB) + "DB;");
  }
    
  public final void setSourcePowerSweepRange_dB (final double sourcePowerSweepRange_dB)
    throws IOException, InterruptedException
  {
    // SRCPSWP
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER_SWEEP_RANGE,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER_SWEEP_RANGE, sourcePowerSweepRange_dB));
  }
  
  protected final void setSourcePowerSweepActiveDirect (final boolean sourcePowerSweepActive)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCPSWP
    writeSync ("SRCPSWP " + (sourcePowerSweepActive ? "ON" : "OFF") + ";");
  }
    
  public final void setSourcePowerSweepActive (final boolean sourcePowerSweepActive)
    throws IOException, InterruptedException
  {
    // SRCPSWP
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER_SWEEP_ACTIVE,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER_SWEEP_ACTIVE, sourcePowerSweepActive));
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
    
  // SRCTK
  
  protected final double getSourceTracking_HzDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCTK?
    final String queryReturn = new String (writeAndReadEOISync ("SRCTK?;"), Charset.forName ("US-ASCII")).trim ();
    if (queryReturn == null)
      throw new IOException ();
    final double sourceTracking_Hz;
    try
    {
      sourceTracking_Hz = Double.parseDouble (queryReturn);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException ();
    }
    return sourceTracking_Hz;
  }
    
  public final double getSourceTracking_HzSync (final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCTK?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_TRACKING);
    addAndProcessCommandSync (command, timeout, unit);
    return (double) command.get (InstrumentCommand.IC_RETURN_VALUE_KEY);
  }
    
  public final void getSourceTracking_HzASync ()
    throws IOException, InterruptedException
  {
    // SRCTK?
    final InstrumentCommand command = new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_TRACKING);
    addCommand (command);
  }
    
  protected final void setSourceTracking_HzDirect (final double sourceTracking_Hz)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCTK
    writeSync ("SRCTK " + Double.toString (sourceTracking_Hz) + "HZ;");
  }
    
  public final void setSourceTracking_Hz (final double sourceTracking_Hz)
    throws IOException, InterruptedException
  {
    // SRCTK
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_TRACKING,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_TRACKING, sourceTracking_Hz));
  }
    
  protected final void setSourceTrackingAutoOnceDirect ()
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCTK;
    writeSync ("SRCTK;");
  }
    
  public final void setSourceTrackingAutoOnce ()
    throws IOException, InterruptedException
  {
    // SRCTK;
    addCommand (new DefaultInstrumentCommand (HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_TRACKING_AUTO_ONCE));
  }
  
  // SRCTKPK;

  protected final void setSourcePeakTrackingAutoDirect (final boolean sourcePeakTrackingAuto)
    throws IOException, InterruptedException, TimeoutException
  {
    // SRCTKPK;
    if (sourcePeakTrackingAuto)
      writeSync ("SRCTKPK;");
    else
      // XXX Assuming that manually setting the tracking (to 0 Hz) will disable auto-peak-tracking.
      setSourceTracking_HzDirect (0);
  }
    
  public final void setSourcePeakTrackingAuto (final boolean sourcePeakTrackingAuto)
    throws IOException, InterruptedException
  {
    // SRCTKPK;
    addCommand (new DefaultInstrumentCommand (
      HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_PEAK_TRACKING_AUTO,
      HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_PEAK_TRACKING_AUTO, sourcePeakTrackingAuto));
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
          // CONFIG?
          final String queryReturn = new String (writeAndReadEOISync ("CONFIG?;"), Charset.forName ("US-ASCII")).trim ();
          if (queryReturn == null)
            throw new IOException ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, queryReturn);
          if (instrumentSettings != null && ! queryReturn.equals (instrumentSettings.getConfigurationString ()))
            newInstrumentSettings = instrumentSettings.withConfigurationString (queryReturn);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_PRESET_STATE:
        {
          // IP
          presetDirect ();
          // XXX
          // newInstrumentSettings = instrumentSettings.fromPreset ();
          newInstrumentSettings = (HP70000_GPIB_Settings) getSettingsFromInstrumentSyncImp ();
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
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_AM_DEPTH:
        {
          // SRCAM?
          final double sourceAmDepth_percent = getSourceAmDepth_percentDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourceAmDepth_percent);
          if (instrumentSettings != null && sourceAmDepth_percent != instrumentSettings.getSourceAmDepth_percent ())
            newInstrumentSettings = instrumentSettings.withSourceAmDepth_percent (sourceAmDepth_percent);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_AM_DEPTH:
        {
          // SRCAM
          final double sourceAmDepth_percent =
            (double) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_AM_DEPTH);
          setSourceAmDepth_percentDirect (sourceAmDepth_percent);
          final double newSourceAmDepth_percent = getSourceAmDepth_percentDirect ();
          if (instrumentSettings != null
            && (newSourceAmDepth_percent != instrumentSettings.getSourceAmDepth_percent ()
                || ! instrumentSettings.isSourceAmActive ()))
            newInstrumentSettings = instrumentSettings
              .withSourceAmDepth_percent (newSourceAmDepth_percent)
              .withSourceAmActive (true);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_AM_ACTIVE:
        {
          // SRCAM
          final boolean sourceAmActive =
            (boolean) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_AM_ACTIVE);
          setSourceAmActiveDirect (sourceAmActive);
          if (instrumentSettings != null && sourceAmActive != instrumentSettings.isSourceAmActive ())
            newInstrumentSettings = instrumentSettings.withSourceAmActive (sourceAmActive);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_AM_FREQUENCY:
        {
          // SRCAMF?
          final double sourceAmFrequency_Hz = getSourceAmFrequency_HzDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourceAmFrequency_Hz);
          if (instrumentSettings != null && sourceAmFrequency_Hz != instrumentSettings.getSourceAmFrequency_Hz ())
            newInstrumentSettings = instrumentSettings.withSourceAmFrequency_Hz (sourceAmFrequency_Hz);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_AM_FREQUENCY:
        {
          // SRCAMF
          final double sourceAmFrequency_Hz =
            (double) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_AM_FREQUENCY);
          setSourceAmFrequency_HzDirect (sourceAmFrequency_Hz);
          final double newSourceAmFrequency_Hz = getSourceAmFrequency_HzDirect ();
          if (instrumentSettings != null
            && (newSourceAmFrequency_Hz != instrumentSettings.getSourceAmFrequency_Hz ()))
            newInstrumentSettings = instrumentSettings.withSourceAmFrequency_Hz (newSourceAmFrequency_Hz);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_ATTENUATION:
        {
          // SRCAT?
          final double sourceAttenuation_dB = getSourceAttenuation_dBDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourceAttenuation_dB);
          if (instrumentSettings != null && sourceAttenuation_dB != instrumentSettings.getSourceAttenuation_dB ())
            newInstrumentSettings = instrumentSettings.withSourceAttenuation_dB (sourceAttenuation_dB);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_ATTENUATION:
        {
          // SRCAT
          final double sourceAttenuation_dB =
            (double) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_ATTENUATION);
          setSourceAttenuation_dBDirect (sourceAttenuation_dB);
          final double newSourcePower_dBm = getSourcePower_dBmDirect ();
          final double newSourceAttenuation_dB = getSourceAttenuation_dBDirect ();
          if (instrumentSettings != null
            && (newSourceAttenuation_dB != instrumentSettings.getSourceAttenuation_dB ()
                || newSourcePower_dBm != instrumentSettings.getSourcePower_dBm ()
                || ! instrumentSettings.isSourceAttenuationCoupled ()))
            newInstrumentSettings = instrumentSettings
              .withSourcePower_dBm (newSourcePower_dBm)
              .withSourceAttenuation_dB (newSourceAttenuation_dB)
              .withSourceAttenuationCoupled (false);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_ATTENUATION_COUPLED:
        {
          // SRCAT
          final boolean sourceAttenuationCoupled =
            (boolean) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_ATTENUATION_COUPLED);
          setSourceAttenuationCoupledDirect (sourceAttenuationCoupled);
          final double sourcePower_dBm = getSourcePower_dBmDirect ();
          final double sourceAttenuation_dB = getSourceAttenuation_dBDirect ();
          if (instrumentSettings != null && sourceAttenuationCoupled != instrumentSettings.isSourceAttenuationCoupled ())
            newInstrumentSettings = instrumentSettings
              .withSourceAttenuationCoupled (sourceAttenuationCoupled)
              .withSourcePower_dBm (sourcePower_dBm)
              .withSourceAttenuation_dB (sourceAttenuation_dB);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_BLANKING:
        {
          // SRCBLNK?
          final boolean sourceBlanking = isSourceBlankingDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourceBlanking);
          if (instrumentSettings != null && sourceBlanking != instrumentSettings.isSourceBlanking ())
            newInstrumentSettings = instrumentSettings.withSourceBlanking (sourceBlanking);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_BLANKING:
        {
          // SRCBLNK
          final boolean sourceBlanking =
            (boolean) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_BLANKING);
          setSourceBlankingDirect (sourceBlanking);
          if (instrumentSettings != null && sourceBlanking != instrumentSettings.isSourceBlanking ())
            newInstrumentSettings = instrumentSettings.withSourceBlanking (sourceBlanking);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_MODULATION_INPUT:
        {
          // SRCMOD?
          final HP70000_GPIB_Settings.SourceModulationInput sourceModulationInput = getSourceModulationInputDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourceModulationInput);
          if (instrumentSettings != null && ! sourceModulationInput.equals (instrumentSettings.getSourceModulationInput ()))
            newInstrumentSettings = instrumentSettings.withSourceModulationInput (sourceModulationInput);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_MODULATION_INPUT:
        {
          // SRCMOD
          final HP70000_GPIB_Settings.SourceModulationInput sourceModulationInput =
            (HP70000_GPIB_Settings.SourceModulationInput)
              instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_MODULATION_INPUT);
          setSourceModulationInputDirect (sourceModulationInput);
          if (instrumentSettings != null && ! sourceModulationInput.equals (instrumentSettings.getSourceModulationInput ()))
            newInstrumentSettings = instrumentSettings.withSourceModulationInput (sourceModulationInput);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_OSCILLATOR:
        {
          // SRCOSC?
          final HP70000_GPIB_Settings.SourceOscillator sourceOscillator = getSourceOscillatorDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourceOscillator);
          if (instrumentSettings != null && ! sourceOscillator.equals (instrumentSettings.getSourceOscillator ()))
            newInstrumentSettings = instrumentSettings.withSourceOscillator (sourceOscillator);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_OSCILLATOR:
        {
          // SRCOSC
          final HP70000_GPIB_Settings.SourceOscillator sourceOscillator =
            (HP70000_GPIB_Settings.SourceOscillator)
              instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_OSCILLATOR);
          setSourceOscillatorDirect (sourceOscillator);
          if (instrumentSettings != null && ! sourceOscillator.equals (instrumentSettings.getSourceOscillator ()))
            newInstrumentSettings = instrumentSettings.withSourceOscillator (sourceOscillator);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER_OFFSET:
        {
          // SRCPOFS?
          final double sourcePowerOffset_dB = getSourcePowerOffset_dBDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourcePowerOffset_dB);
          if (instrumentSettings != null && sourcePowerOffset_dB != instrumentSettings.getSourcePowerOffset_dB ())
            newInstrumentSettings = instrumentSettings.withSourcePowerOffset_dB (sourcePowerOffset_dB);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER_OFFSET:
        {
          // SRCPOFS
          final double sourcePowerOffset_dB = (double)
            instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER_OFFSET);
          setSourcePowerOffset_dBDirect (sourcePowerOffset_dB);
          final double newSourcePowerOffset_dB = getSourcePowerOffset_dBDirect ();
          if (instrumentSettings != null && newSourcePowerOffset_dB != instrumentSettings.getSourcePowerOffset_dB ())
            newInstrumentSettings = instrumentSettings.withSourcePowerOffset_dB (newSourcePowerOffset_dB);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER_STEP_SIZE:
        {
          // SRCPSTP?
          final double sourcePowerStepSize_dB = getSourcePowerStepSize_dBDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourcePowerStepSize_dB);
          if (instrumentSettings != null && sourcePowerStepSize_dB != instrumentSettings.getSourcePowerStepSize_dB ())
            newInstrumentSettings = instrumentSettings.withSourcePowerStepSize_dB (sourcePowerStepSize_dB);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER_STEP_SIZE:
        {
          // SRCPSTP
          final double sourcePowerStepSize_dB =
            (double) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER_STEP_SIZE);
          setSourcePowerStepSize_dBDirect (sourcePowerStepSize_dB);
          final double newSourcePowerStepSize_dB = getSourcePowerStepSize_dBDirect ();
          if (instrumentSettings != null
            && (newSourcePowerStepSize_dB != instrumentSettings.getSourcePowerStepSize_dB ()
                || ! HP70000_GPIB_Settings.SourcePowerStepSizeMode.Manual.equals (
                       instrumentSettings.getSourcePowerStepSizeMode ())))
            newInstrumentSettings = instrumentSettings
              .withSourcePowerStepSize_dB (newSourcePowerStepSize_dB)
              .withSourcePowerStepSizeMode (HP70000_GPIB_Settings.SourcePowerStepSizeMode.Manual);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER_STEP_SIZE_MODE:
        {
          // SRCPSTP
          final HP70000_GPIB_Settings.SourcePowerStepSizeMode sourcePowerStepSizeMode =
            (HP70000_GPIB_Settings.SourcePowerStepSizeMode)
              instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER_STEP_SIZE_MODE);
          setSourcePowerStepSizeModeDirect (sourcePowerStepSizeMode);
          final double sourcePowerStepSize_dB = getSourcePowerStepSize_dBDirect ();
          if (instrumentSettings != null
            && ((! sourcePowerStepSizeMode.equals (instrumentSettings.getSourcePowerStepSizeMode ()))
                || sourcePowerStepSize_dB != instrumentSettings.getSourcePowerStepSize_dB ()))
            newInstrumentSettings = instrumentSettings
              .withSourcePowerStepSizeMode (sourcePowerStepSizeMode)
              .withSourcePowerOffset_dB (sourcePowerStepSize_dB);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_POWER_SWEEP_RANGE:
        {
          // SRCPSWP?
          final double sourcePowerSweepRange_dB = getSourcePowerSweepRange_dBDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourcePowerSweepRange_dB);
          if (instrumentSettings != null && sourcePowerSweepRange_dB != instrumentSettings.getSourcePowerSweepRange_dB ())
            newInstrumentSettings = instrumentSettings.withSourcePowerSweepRange_dB (sourcePowerSweepRange_dB);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER_SWEEP_RANGE:
        {
          // SRCPSWP
          final double sourcePowerSweepRange_dB =
            (double) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER_SWEEP_RANGE);
          setSourcePowerSweepRange_dBDirect (sourcePowerSweepRange_dB);
          final double newSourcePowerSweepRange_dB = getSourcePowerSweepRange_dBDirect ();
          if (instrumentSettings != null
            && (newSourcePowerSweepRange_dB != instrumentSettings.getSourcePowerSweepRange_dB ()
                || ! instrumentSettings.isSourcePowerSweepActive ()))
            newInstrumentSettings = instrumentSettings
              .withSourcePowerSweepRange_dB (newSourcePowerSweepRange_dB)
              .withSourcePowerSweepActive (true);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_POWER_SWEEP_ACTIVE:
        {
          // SRCPSWP
          final boolean sourcePowerSweepActive =
            (boolean)
              instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_POWER_SWEEP_ACTIVE);
          setSourcePowerSweepActiveDirect (sourcePowerSweepActive);
          final double sourcePowerSweepRange_dB = getSourcePowerSweepRange_dBDirect ();
          if (instrumentSettings != null
            && ((! instrumentSettings.isSourcePowerSweepActive ())
                || sourcePowerSweepRange_dB != instrumentSettings.getSourcePowerSweepRange_dB ()))
            newInstrumentSettings = instrumentSettings
              .withSourcePowerSweepActive (sourcePowerSweepActive)
              .withSourcePowerOffset_dB (sourcePowerSweepRange_dB);
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
          final double newSourceAttenuation_dB = getSourceAttenuation_dBDirect ();
          if (instrumentSettings != null
            && (newSourcePower_dBm != instrumentSettings.getSourcePower_dBm ()
                || newSourceAttenuation_dB != instrumentSettings.getSourceAttenuation_dB ()
                || ! instrumentSettings.isSourceActive ()))
            newInstrumentSettings = instrumentSettings
              .withSourcePower_dBm (newSourcePower_dBm)
              .withSourceActive (true)
              .withSourceAttenuation_dB (newSourceAttenuation_dB);
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
        case HP70000_InstrumentCommand.IC_HP70000_GET_SOURCE_TRACKING:
        {
          // SRCTK?
          final double sourceTracking_Hz = getSourceTracking_HzDirect ();
          instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, sourceTracking_Hz);
          if (instrumentSettings != null && sourceTracking_Hz != instrumentSettings.getSourceTracking_Hz ())
            newInstrumentSettings = instrumentSettings.withSourceTracking_Hz (sourceTracking_Hz);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_TRACKING:
        {
          // SRCTK
          final double sourceTracking_Hz =
            (double) instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_TRACKING);
          setSourceTracking_HzDirect (sourceTracking_Hz);
          final double newSourceTracking_Hz = getSourceTracking_HzDirect ();
          if (instrumentSettings != null
            && (newSourceTracking_Hz != instrumentSettings.getSourceTracking_Hz ()
                || instrumentSettings.isSourcePeakTrackingAuto ()))
            newInstrumentSettings = instrumentSettings
              .withSourceTracking_Hz (newSourceTracking_Hz)
              .withSourcePeakTrackingAuto (false); // XXX Wild guess; is this correct?
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_TRACKING_AUTO_ONCE:
        {
          // SRCTK;
          setSourceTrackingAutoOnceDirect ();
          final double newSourceTracking_Hz = getSourceTracking_HzDirect ();
          // XXX Does this affect sourcePeakTrackingAuto?
          if (instrumentSettings != null
            && (newSourceTracking_Hz != instrumentSettings.getSourceTracking_Hz ()))
            newInstrumentSettings = instrumentSettings.withSourceTracking_Hz (newSourceTracking_Hz);
          break;
        }
        case HP70000_InstrumentCommand.IC_HP70000_SET_SOURCE_PEAK_TRACKING_AUTO:
        {
          // SRCTKPK;
          final boolean sourcePeakTrackingAuto =
            (boolean)
              instrumentCommand.get (HP70000_InstrumentCommand.ICARG_HP70000_SET_SOURCE_PEAK_TRACKING_AUTO);
          setSourcePeakTrackingAutoDirect (sourcePeakTrackingAuto);
          final double sourceTracking_Hz = getSourceTracking_HzDirect ();
          if (instrumentSettings != null
            && (sourcePeakTrackingAuto != instrumentSettings.isSourcePeakTrackingAuto ()
                || sourceTracking_Hz != instrumentSettings.getSourceTracking_Hz ()))
            newInstrumentSettings = instrumentSettings
              .withSourcePeakTrackingAuto (sourcePeakTrackingAuto)
              .withSourceTracking_Hz (sourceTracking_Hz);
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
