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
package org.javajdj.jinstrument.gpib.sg.hp8663a;

import java.io.IOException;
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
import org.javajdj.jinstrument.gpib.sg.AbstractGpibSignalGenerator;
import org.javajdj.jinstrument.DefaultSignalGeneratorSettings;
import org.javajdj.jinstrument.SignalGenerator;
import org.javajdj.jinstrument.SignalGeneratorSettings;

/** Implementation of {@link Instrument} and {@link SignalGenerator} for the HP-8663A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP8663A_GPIB_Instrument
extends AbstractGpibSignalGenerator
implements SignalGenerator
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP8663A_GPIB_Instrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP8663A_GPIB_Instrument (final GpibDevice device)
  {
    super ("HP-8663A", device, null, null, false, true, true, true, false, false, false);
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
      return "HP-8663A [GPIB]";
    }
    
    @Override
    public final DeviceType getDeviceType ()
    {
      return DeviceType_GPIB.getInstance ();
    }

    @Override
    public final HP8663A_GPIB_Instrument openInstrument (final Device device)
    {
      if (device == null || ! (device instanceof GpibDevice))
      {
        LOG.log (Level.WARNING, "Incompatible Device (not a GpibDevice): {0}!", device);
        return null;
      }
      return new HP8663A_GPIB_Instrument ((GpibDevice) device);
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
    return HP8663A_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + getDevice ().getDeviceUrl ();
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
    final String statusLine = writeAndReadlnSync ("MS\n");
    if (statusLine.length () != HP8663A_GPIB_Status.STATUS_LENGTH)
    {
      LOG.log (Level.WARNING, "Unexpected number of bytes read for HP8663A status: {0} (should be {1}).",
        new Object[]{statusLine.length (), HP8663A_GPIB_Status.STATUS_LENGTH});
      throw new IOException ();
    }
    final byte[] statusBuffer = statusLine.getBytes ();
    // LOG message for debugging the status output.
    // LOG.log (Level.WARNING, "bytes = {0}", Util.bytesToHex (statusBuffer));
    LOG.log (Level.INFO, "Status received.");
    return new HP8663A_GPIB_Status (statusBuffer);
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
  
  private static final int L1_LENGTH = 189;
  
  private transient byte[] lastL1 = null;
    
  @Override
  protected void requestSettingsFromInstrumentASync () throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }
  
  @Override
  public SignalGeneratorSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException
  {
    final byte[] l1 = writeAndReadNSync ("L1\n", L1_LENGTH);
    if (l1.length != HP8663A_GPIB_Instrument.L1_LENGTH)
    {
      LOG.log (Level.WARNING, "Unexpected number of bytes read for L1: {0} (should be {1}).",
        new Object[]{l1.length, HP8663A_GPIB_Instrument.L1_LENGTH});
      throw new IOException ();
    }
    // LOG message for debugging the L1 output.
    // LOG.log (Level.WARNING, "bytes = {0}", bytesToHex (l1));
    LOG.log (Level.INFO, "L1 received.");
    // Also for debugging L1 output; but this one only reports changes.
    if (this.lastL1 != null)
      for (int i = 0; i < HP8663A_GPIB_Instrument.L1_LENGTH; i++)
        if (l1[i] != this.lastL1[i])
          LOG.log (Level.WARNING, "L1 string index {0} changed from {1} to {2}.",
            new Object[]{i, Integer.toHexString (this.lastL1[i] & 0xff), Integer.toHexString (l1[i] & 0xff)});
    this.lastL1 = l1;
    return HP8663A_GPIB_Instrument.settingsFromL1 (l1);
  }
 
  private static SignalGeneratorSettings settingsFromL1 (final byte[] l1)
  {
    if (l1 == null || l1.length != L1_LENGTH)
      throw new IllegalArgumentException ();
    if (l1[0] != 0x40 || l1[1] != 0x41)
      throw new IllegalArgumentException ();
    //
    final double f_MHz = 0.0
      + 1000.0    * (l1[7] & 0x0f)
      + 100.0     * ((l1[6] & 0xf0) >> 4)
      + 10.0      * (l1[6] & 0x0f)
      + 1.0       * ((l1[5] & 0xf0) >> 4)
      + 0.1       * (l1[5] & 0x0f)
      + 0.01      * ((l1[4] & 0xf0) >> 4)
      + 0.001     * (l1[4] & 0x0f)
      + 0.0001    * ((l1[3] & 0xf0) >> 4)
      + 0.00001   * (l1[3] & 0x0f)
      + 0.000001  * ((l1[2] & 0xf0) >> 4)
      + 0.0000001 * (l1[2] & 0x0f)
      ;
    final double span_MHz = 0.0
      // XXX First nibble of 53 might be always zero; or used for other purposes.
      + 1000.0    * (l1[53] & 0x0f)
      + 100.0     * ((l1[52] & 0xf0) >> 4)
      + 10.0      * (l1[52] & 0x0f)
      + 1         * ((l1[51] & 0xf0) >> 4)
      + 0.1       * (l1[51] & 0x0f)
      + 0.01      * ((l1[50] & 0xf0) >> 4)
      + 0.001     * (l1[50] & 0x0f)
      + 0.0001    * ((l1[49] & 0xf0) >> 4)
      + 0.00001   * (l1[49] & 0x0f)
      + 0.000001  * ((l1[48] & 0xf0) >> 4)
      + 0.0000001 * (l1[48] & 0x0f)
      ;
    final double startFrequency_MHz = 0.0
      // XXX First nibble of 38 might be always zero; or used for other purposes.
      + 1000.0    * (l1[40] & 0x0f)
      + 100.0     * ((l1[39] & 0xf0) >> 4)
      + 10.0      * (l1[39] & 0x0f)
      + 1         * ((l1[38] & 0xf0) >> 4)
      + 0.1       * (l1[38] & 0x0f)
      + 0.01      * ((l1[37] & 0xf0) >> 4)
      + 0.001     * (l1[37] & 0x0f)
      + 0.0001    * ((l1[36] & 0xf0) >> 4)
      + 0.00001   * (l1[36] & 0x0f)
      + 0.000001  * ((l1[35] & 0xf0) >> 4)
      + 0.0000001 * (l1[35] & 0x0f)
      ;
    final double stopFrequency_MHz = 0.0
      // XXX First nibble of 46 might be always zero; or used for other purposes.
      + 1000.0    * (l1[46] & 0x0f)
      + 100.0     * ((l1[45] & 0xf0) >> 4)
      + 10.0      * (l1[45] & 0x0f)
      + 1         * ((l1[44] & 0xf0) >> 4)
      + 0.1       * (l1[44] & 0x0f)
      + 0.01      * ((l1[43] & 0xf0) >> 4)
      + 0.001     * (l1[43] & 0x0f)
      + 0.0001    * ((l1[42] & 0xf0) >> 4)
      + 0.00001   * (l1[42] & 0x0f)
      + 0.000001  * ((l1[41] & 0xf0) >> 4)
      + 0.0000001 * (l1[41] & 0x0f)
      ;
    final double S_dBm = (0
      + 100.0 * (l1[17] & 0x0f)
      + 10.0  * ((l1[16] & 0xf0) >> 4)
      + 1.0   * (l1[16] & 0x0f)
      + 0.1   * ((l1[15] & 0xf0) >> 4)
      + 0.01  * (l1[15] & 0x0f)
      ) * (1 - 2 * ((l1[17] & 0x80) >> 7));
    final boolean outputEnable;
    // Guess work below... Candidates are 21 and 81.
    // On 21, the change is from 0xfe (on) to 0xfb (off)...
    // On 81, the change is from 0x02 (on) to 0x0a (off)...
    outputEnable = (l1[21] & 0x04) != 0;
    final double modulationSourceInternalFrequency_kHz = 0.0
      + 10.0 * ((l1[116]  & 0xf0) >> 4)
      + 1.0  * (l1[116] & 0x0f)
      + 0.1  * ((l1[117]  & 0xf0) >> 4)
      + 0.01 * (l1[117] & 0x0f);
    // LOG.log (Level.INFO, "Read 116/117: {0}/{1}; modulationSourceInternalFrequency_kHz: {2}.",
    //  new Object[]{Integer.toHexString (l1[116] & 0xff),
    //    Integer.toHexString (l1[117] & 0xff),
    //    modulationSourceInternalFrequency_kHz});
    // XXX NO CLUE YET HERE!
    final boolean amEnable = true;
    // Seems OK...
    final double amDepth_percent = 0.0
      + 10.0 * (l1[128]  & 0x0f)
      + 1.0  * ((l1[129] & 0xf0) >> 4)
      + 0.1  * (l1[129]  & 0x0f);
    // LOG.log (Level.INFO, "Read 128/129: {0}/{1}; amDepth_percent: {2}.",
    //  new Object[]{Integer.toHexString (l1[128] & 0xff),
    //    Integer.toHexString (l1[129] & 0xff),
    //    amDepth_percent});
    // XXX NO CLUE YET HERE!
    final boolean fmEnable = true;
    final double fmMaxDeviation_kHz = 0.0
      + 100.0 * ((l1[133] & 0xf0) >> 4)
      + 10.0  * (l1[133]  & 0x0f)
      + 1.0   * ((l1[134] & 0xf0) >> 4)
      + 0.1   * (l1[134]  & 0x0f);
    // LOG.log (Level.INFO, "Read 133/134: {0}/{1}; fmMaxDeviation_kHz: {2}.",
    //   new Object[]{Integer.toHexString (l1[133] & 0xff),
    //     Integer.toHexString (l1[134] & 0xff),
    //     fmMaxDeviation_kHz});
    //
    // 186/187: checksum.
    // The checksum is most likely taken over the first 185 bytes in the L1 array.
    // Perhaps starting at index 2, since the first two bytes are always 0x4041.
    //
    int sum = 0;
    for (int i=0; i < 186; i++)
      sum += (l1[i]);
    // LOG message for debugging 186/187 checksums [?].
    // LOG.log (Level.WARNING, "Sum 0...185 = {0}, 186={1}, 187={2}.",
    //   new Object[]{Integer.toHexString (sum & 0xffff),
    //     Integer.toHexString (l1[186] & 0xff),
    //    Integer.toHexString (l1[187] & 0xff)});
    // Through reverse engineering I discoved that the least-significant byte of the sum
    // is followed "at a distance" by L1[187].
    // The distance is non-zero, though.
    final int sumLSB = sum & 0xff;
    final int sumLSBMinus187 = (sumLSB - (l1[187] & 0xff)) & 0xff;
    if (sumLSBMinus187 != 0x86)
      LOG.log (Level.WARNING, "Potential checksum error (LSB/L1[187] mismatch].");
    final int sumMSB = (sum & 0xff00) >> 8;
    // Variable and LOG message for debugging the "checksum" on 186...
    // final int sumMSBMinus186 = (sumMSB - (l1[186] & 0xff)) & 0xff;
    // LOG.log (Level.WARNING, "MSB (Sum 2...185) - 186 = {0}.",
    //   new Object[]{Integer.toHexString (sumMSBMinus186)});
    return new DefaultSignalGeneratorSettings (
      l1,
      f_MHz,
      span_MHz,
      startFrequency_MHz,
      stopFrequency_MHz,
      S_dBm,
      outputEnable,
      modulationSourceInternalFrequency_kHz,
      amEnable,
      amDepth_percent,
      fmEnable,
      fmMaxDeviation_kHz); 
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
  // SignalGenerator
  //
  // XXX SINCE THESE ARE SignalGenerator generic, they could move to AbstractGpibSignalGenerator??
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public void setOutputEnable (final boolean outputEnable)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_OUTPUT_ENABLE,
      InstrumentCommand.ICARG_RF_OUTPUT_ENABLE,
      outputEnable));
  }
  
  @Override
  public void setCenterFrequency_MHz (final double centerFrequency_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_FREQUENCY,
      InstrumentCommand.ICARG_RF_FREQUENCY_MHZ,
      centerFrequency_MHz));
  }
  
  @Override
  public void setSpan_MHz (final double span_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_SPAN,
      InstrumentCommand.ICARG_RF_SPAN_MHZ,
      span_MHz));
  }
  
  @Override
  public void setStartFrequency_MHz (final double startFrequency_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_START_FREQUENCY,
      InstrumentCommand.ICARG_RF_START_FREQUENCY_MHZ,
      startFrequency_MHz));
  }
  
  @Override
  public void setStopFrequency_MHz (final double stopFrequency_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_STOP_FREQUENCY,
      InstrumentCommand.ICARG_RF_STOP_FREQUENCY_MHZ,
      stopFrequency_MHz));
  }
  
  @Override
  public void setRfSweepMode (final RfSweepMode rfSweepMode)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_SWEEP_MODE,
      InstrumentCommand.ICARG_RF_SWEEP_MODE,
      rfSweepMode));
  }
  
  @Override
  public void setAmplitude_dBm (final double amplitude_dBm)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_OUTPUT_LEVEL,
      InstrumentCommand.ICARG_RF_OUTPUT_LEVEL_DBM,
      amplitude_dBm));
  }
  
  @Override
  public void setEnableAm (
    final boolean enableAm,
    final double depth_percent,
    final SignalGenerator.ModulationSource modulationSource)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_AM_CONTROL,
      InstrumentCommand.ICARG_RF_AM_ENABLE, enableAm,
      InstrumentCommand.ICARG_RF_AM_DEPTH_PERCENT, depth_percent,
      InstrumentCommand.ICARG_RF_AM_SOURCE, modulationSource));
  }

  @Override
  public void setEnableFm (
    final boolean enableFm,
    final double fmDeviation_kHz,
    SignalGenerator.ModulationSource modulationSource)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_FM_CONTROL,
      InstrumentCommand.ICARG_RF_FM_ENABLE, enableFm,
      InstrumentCommand.ICARG_RF_FM_DEVIATION_KHZ, fmDeviation_kHz,
      InstrumentCommand.ICARG_RF_FM_SOURCE, modulationSource));
  }
  
  @Override
  public void setEnablePm (
    final boolean enablePm,
    final double pmDeviation_degrees,
    SignalGenerator.ModulationSource modulationSource)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_PM_CONTROL,
      InstrumentCommand.ICARG_RF_PM_ENABLE, enablePm,
      InstrumentCommand.ICARG_RF_PM_DEVIATION_DEGREES, pmDeviation_degrees,
      InstrumentCommand.ICARG_RF_PM_SOURCE, modulationSource));
  }
  
  @Override
  public void setEnablePulseM (
    final boolean enablePulseM,
    SignalGenerator.ModulationSource modulationSource)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_PULSEM_CONTROL,
      InstrumentCommand.ICARG_RF_PULSEM_ENABLE, enablePulseM,
      InstrumentCommand.ICARG_RF_PULSEM_SOURCE, modulationSource));
  }
  
  @Override
  public void setEnableBpsk (
    final boolean enableBpsk,
    SignalGenerator.ModulationSource modulationSource)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_BPSK_CONTROL,
      InstrumentCommand.ICARG_RF_BPSK_ENABLE, enableBpsk,
      InstrumentCommand.ICARG_RF_BPSK_SOURCE, modulationSource));
  }
  
  @Override
  public void setModulationSourceInternalFrequency_kHz (final double modulationSourceInternalFrequency_kHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_INTMODSOURCE_CONTROL,
      InstrumentCommand.ICARG_INTMODSOURCE_FREQUENCY_KHZ, modulationSourceInternalFrequency_kHz));
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
        case InstrumentCommand.IC_RF_OUTPUT_ENABLE:
        {
          final Boolean outputEnable;
          if (instrumentCommand.containsKey (InstrumentCommand.ICARG_RF_OUTPUT_ENABLE))
            outputEnable = (Boolean) instrumentCommand.get (InstrumentCommand.ICARG_RF_OUTPUT_ENABLE);
          else
            outputEnable = true;
          writeSync (outputEnable ? "AP\n" : "AO\n");
          break;
        }
        case InstrumentCommand.IC_RF_OUTPUT_LEVEL:
        {
          // XXX There seems to be no way to just set the output level, without also enabling RF output.
          final double amplitude_dBm = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_OUTPUT_LEVEL_DBM);
          writeSync ("AP " + amplitude_dBm + " DM\n");
          break;
        }
        case InstrumentCommand.IC_RF_FREQUENCY:
        {
          final double centerFrequency_MHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_FREQUENCY_MHZ);
          writeSync ("FR " + centerFrequency_MHz + " MZ\n");
          break;
        }
        case InstrumentCommand.IC_RF_SWEEP_MODE:
        {
          final RfSweepMode rfSweepMode = (RfSweepMode) instrumentCommand.get (InstrumentCommand.ICARG_RF_SWEEP_MODE);
          if (rfSweepMode == null)
            throw new IllegalArgumentException ();
          switch (rfSweepMode)
          {
            case OFF:
            {
              writeSync ("W1\n");
              break;
            }
            case AUTO:
            {
              writeSync ("W2\n");
              break;
            }
            case MANUAL:
            {
              writeSync ("W3\n");
              break;
            }
            case SINGLE:
            {
              writeSync ("W4\n");
              break;
            }
            default:
              throw new UnsupportedOperationException ();
          }
        }
        case InstrumentCommand.IC_RF_SPAN:
        {
          final double span_MHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_SPAN_MHZ);
          writeSync ("FS " + span_MHz + " MZ\n");
          break;
        }
        case InstrumentCommand.IC_RF_START_FREQUENCY:
        {
          final double startFrequency_MHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_START_FREQUENCY_MHZ);
          writeSync ("FA " + startFrequency_MHz + " MZ\n");
          break;
        }
        case InstrumentCommand.IC_RF_STOP_FREQUENCY:
        {
          final double stopFrequency_MHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_STOP_FREQUENCY_MHZ);
          writeSync ("FB " + stopFrequency_MHz + " MZ\n");
          break;
        }
        case InstrumentCommand.IC_RF_AM_CONTROL:
        {
          final Boolean amEnable;
          if (instrumentCommand.containsKey (InstrumentCommand.ICARG_RF_AM_ENABLE))
            amEnable = (Boolean) instrumentCommand.get (InstrumentCommand.ICARG_RF_AM_ENABLE);
          else
            amEnable = true;
          if (amEnable)
          {
            final double depth_percent = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_AM_DEPTH_PERCENT);
            final ModulationSource modulationSource =
              (ModulationSource) instrumentCommand.get (InstrumentCommand.ICARG_RF_AM_SOURCE);
            switch (modulationSource)
            {
              case INT:
                writeSync ("AM " + depth_percent + " PC\n");
                break;
              case INT_400:
                writeSync ("AM M1 " + depth_percent + " PC\n");
                break;
              case INT_1K:
                writeSync ("AM M2 " + depth_percent + " PC\n");
                break;
              case EXT_AC:
                writeSync ("AM M3 " + depth_percent + " PC\n");
                break;
              case EXT_DC:
                writeSync ("AM M4 " + depth_percent + " PC\n");
                break;
              default:
                throw new UnsupportedOperationException ();
            }            
          }
          else
            writeSync ("AM FO\n");
          break;
        }
        case InstrumentCommand.IC_RF_FM_CONTROL:
        {
          final Boolean fmEnable;
          if (instrumentCommand.containsKey (InstrumentCommand.ICARG_RF_FM_ENABLE))
            fmEnable = (Boolean) instrumentCommand.get (InstrumentCommand.ICARG_RF_FM_ENABLE);
          else
            fmEnable = true;
          if (fmEnable)
          {
            final double fmDeviation_kHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_FM_DEVIATION_KHZ);
            final ModulationSource modulationSource =
              (ModulationSource) instrumentCommand.get (InstrumentCommand.ICARG_RF_FM_SOURCE);
            switch (modulationSource)
            {
              case INT:
                writeSync ("FM " + fmDeviation_kHz + " KZ\n");
                break;
              case INT_400:
                writeSync ("FM M1 " + fmDeviation_kHz + " KZ\n");
                break;
              case INT_1K:
                writeSync ("FM M2 " + fmDeviation_kHz + " KZ\n");
                break;
              case EXT_AC:
                writeSync ("FM M3 " + fmDeviation_kHz + " KZ\n");
                break;
              case EXT_DC:
                writeSync ("FM M4 " + fmDeviation_kHz + " KZ\n");
                break;
              default:
                throw new UnsupportedOperationException ();
            }            
          }
          else
            writeSync ("FM FO\n");  
          break;
        }
        case InstrumentCommand.IC_RF_PM_CONTROL:
        {
          final Boolean pmEnable;
          if (instrumentCommand.containsKey (InstrumentCommand.ICARG_RF_PM_ENABLE))
            pmEnable = (Boolean) instrumentCommand.get (InstrumentCommand.ICARG_RF_PM_ENABLE);
          else
            pmEnable = true;
          if (pmEnable)
          {
            final double pmDeviation_degrees = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_PM_DEVIATION_DEGREES);
            final ModulationSource modulationSource =
              (ModulationSource) instrumentCommand.get (InstrumentCommand.ICARG_RF_PM_SOURCE);
            switch (modulationSource)
            {
              case INT:
                writeSync ("PM " + pmDeviation_degrees + " DG\n");
                break;
              case INT_400:
                writeSync ("PM M1 " + pmDeviation_degrees + " DG\n");
                break;
              case INT_1K:
                writeSync ("PM M2 " + pmDeviation_degrees + " DG\n");
                break;
              case EXT_AC:
                writeSync ("PM M3 " + pmDeviation_degrees + " DG\n");
                break;
              case EXT_DC:
                writeSync ("PM M4 " + pmDeviation_degrees + " DG\n");
                break;
              default:
                throw new UnsupportedOperationException ();
            }            
          }
          else
            writeSync ("PM FO\n");  
          break;
        }
        case InstrumentCommand.IC_RF_PULSEM_CONTROL:
        {
          final Boolean pulseMEnable;
          if (instrumentCommand.containsKey (InstrumentCommand.ICARG_RF_PULSEM_ENABLE))
            pulseMEnable = (Boolean) instrumentCommand.get (InstrumentCommand.ICARG_RF_PULSEM_ENABLE);
          else
            pulseMEnable = true;
          if (pulseMEnable)
          {
            final ModulationSource modulationSource =
              (ModulationSource) instrumentCommand.get (InstrumentCommand.ICARG_RF_PULSEM_SOURCE);
            switch (modulationSource)
            {
              case INT:
                writeSync ("PL\n");
                break;
              case INT_400:
                writeSync ("PL M1\n");
                break;
              case INT_1K:
                writeSync ("PL M2\n");
                break;
              case EXT_AC:
                throw new UnsupportedOperationException ();
              case EXT_DC:
                writeSync ("PL M4\n");
                break;
              default:
                throw new UnsupportedOperationException ();
            }            
          }
          else
            writeSync ("PL FO\n");
          break;
        }
        case InstrumentCommand.IC_RF_BPSK_CONTROL:
        {
          final Boolean bpskEnable;
          if (instrumentCommand.containsKey (InstrumentCommand.ICARG_RF_BPSK_ENABLE))
            bpskEnable = (Boolean) instrumentCommand.get (InstrumentCommand.ICARG_RF_BPSK_ENABLE);
          else
            bpskEnable = true;
          if (bpskEnable)
          {
            final ModulationSource modulationSource =
              (ModulationSource) instrumentCommand.get (InstrumentCommand.ICARG_RF_BPSK_SOURCE);
            switch (modulationSource)
            {
              case INT:
                writeSync ("BP\n");
                break;
              case INT_400:
                writeSync ("BP M1\n");
                break;
              case INT_1K:
                writeSync ("BP M2\n");
                break;
              case EXT_AC:
                throw new UnsupportedOperationException ();
              case EXT_DC:
                writeSync ("BP M4\n");
                break;
              default:
                throw new UnsupportedOperationException ();
            }
          }
          else
            writeSync ("BP FO\n");
          break;
        }
        case InstrumentCommand.IC_INTMODSOURCE_CONTROL:
        {
          final double intModSourceFrequency_kHz =
            (double) instrumentCommand.get (InstrumentCommand.ICARG_INTMODSOURCE_FREQUENCY_KHZ);
          writeSync ("MF " + intModSourceFrequency_kHz + " KZ\n");
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
