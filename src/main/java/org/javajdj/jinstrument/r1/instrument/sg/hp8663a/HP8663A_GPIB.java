package org.javajdj.jinstrument.r1.instrument.sg.hp8663a;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.r1.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.r1.instrument.DefaultInstrumentCommand;
import org.javajdj.jinstrument.r1.instrument.InstrumentCommand;
import org.javajdj.jinstrument.r1.instrument.InstrumentSettings;
import org.javajdj.jinstrument.r1.instrument.sg.AbstractSignalGenerator;
import org.javajdj.jinstrument.r1.instrument.sg.DefaultSignalGeneratorSettings;
import org.javajdj.jinstrument.r1.instrument.sg.SignalGenerator;
import org.javajdj.jinstrument.r1.instrument.sg.SignalGeneratorSettings;

/**
 *
 */
public class HP8663A_GPIB
extends AbstractSignalGenerator
implements SignalGenerator
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP8663A_GPIB.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP8663A_GPIB (final GpibDevice device)
  {
    super ("HP8663A", device, null, null);
    this.out = new PrintWriter (device.getOutputStream (), true);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // OUT PRINTWRITER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final PrintWriter out;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // POWER ON
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public boolean isPoweredOn ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public void setPoweredOn (boolean poweredOn)
  {
    throw new UnsupportedOperationException ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READ LINE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private String readLine () throws IOException
  {
    // XXX THIS METHOD SHOULD REALLY NOT BE NEEDED!!
    String line = "";
    boolean done = false;
    while (! done)
    {
      final int intRead = ((GpibDevice) getDevice ()).getInputStream ().read ();
      done = (intRead == 10);
      if (! done)
        line += Character.toString ((char) intRead);
    }
    return line;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
}
  private static final int L1_LENGTH = 189;
  
  private transient byte[] lastL1 = null;
  
  private SignalGeneratorSettings getSettingsFromInstrumentSyncNoLock (GpibDevice gpibDevice)
    throws IOException, InterruptedException
  {
    final byte[] l1Buffer = new byte[HP8663A_GPIB.L1_LENGTH];
    this.out.println ("L1");
    final int bytesRead = gpibDevice.getInputStream ().read (l1Buffer);
    // LOG.log (Level.WARNING, "number of bytes read = {0}", bytesRead);
    LOG.log (Level.WARNING, "bytes = {0}", bytesToHex (l1Buffer));
    if (this.lastL1 != null)
      for (int i = 0; i < HP8663A_GPIB.L1_LENGTH; i++)
        if (l1Buffer[i] != this.lastL1[i])
          LOG.log (Level.WARNING, "L1 string index {0} changed from {1} to {2}.",
            new Object[]{i, Integer.toHexString (this.lastL1[i] & 0xff), Integer.toHexString (l1Buffer[i] & 0xff)});
    this.lastL1 = l1Buffer;
    return HP8663A_GPIB.settingsFromL1 (l1Buffer);
  }
  
  @Override
  public synchronized SignalGeneratorSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException
  {
    final GpibDevice device = (GpibDevice) getDevice ();
    try
    {
      device.lockDevice ();
      return getSettingsFromInstrumentSyncNoLock (device);
    }
    finally
    {
      device.unlockDevice ();
    }
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
    final double amDepth_percent = 0.0
      + 10.0 * (l1[128]  & 0x0f)
      + 1.0  * ((l1[129] & 0xf0) >> 4)
      + 0.1  * (l1[129]  & 0x0f);
    // LOG.log (Level.INFO, "Read 128/129: {0}/{1}; amDepth_percent: {2}.",
    //  new Object[]{Integer.toHexString (l1[128] & 0xff),
    //    Integer.toHexString (l1[129] & 0xff),
    //    amDepth_percent});
    //
    //
    // 186/187: checksum.
    // The checksum is most likely taken over the first 185 bytes in the L1 array.
    // Perhaps starting at index 2, since the first two bytes are always 0x4041.
    //
    int sum = 0;
    for (int i=0; i < 186; i++)
      sum += (l1[i]);
    LOG.log (Level.WARNING, "Sum 0...185 = {0}, 186={1}, 187={2}.",
      new Object[]{Integer.toHexString (sum & 0xffff),
        Integer.toHexString (l1[186] & 0xff),
        Integer.toHexString (l1[187] & 0xff)});
    // Through reverse engineering I discoved that the least-significant byte of the sum
    // is followed "at a distance" by L1[187].
    // The distance is non-zero, though.
    final int sumLSB = sum & 0xff;
    final int sumLSBMinus187 = (sumLSB - (l1[187] & 0xff)) & 0xff;
    if (sumLSBMinus187 != 0x86)
      LOG.log (Level.WARNING, "Potential checksum error (LSB/L1[187] mismatch].");
    final int sumMSB = (sum & 0xff00) >> 8;
    final int sumMSBMinus186 = (sumMSB - (l1[186] & 0xff)) & 0xff;
    LOG.log (Level.WARNING, "MSB (Sum 2...185) - 186 = {0}.",
      new Object[]{Integer.toHexString (sumMSBMinus186)});
    
    return new DefaultSignalGeneratorSettings (
      l1,
      f_MHz,
      S_dBm,
      outputEnable,
      modulationSourceInternalFrequency_kHz,
      amEnable,
      amDepth_percent); 
  }
  
  @Override
  public synchronized void setSettingsOnInstrument (final SignalGeneratorSettings signalGeneratorSettings)
  {
    throw new UnsupportedOperationException ("Not supported yet.");
  }
  
  @Override
  public synchronized void setOutputEnable (final boolean outputEnable)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_OUTPUT_ENABLE,
      InstrumentCommand.ICARG_RF_OUTPUT_ENABLE,
      outputEnable));
  }
  
  @Override
  public synchronized void setAmplitude_dBm (final double amplitude_dBm)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_OUTPUT_LEVEL,
      InstrumentCommand.ICARG_RF_OUTPUT_LEVEL_DBM,
      amplitude_dBm));
  }
  
  @Override
  public synchronized void setCenterFrequency_MHz (final double centerFrequency_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_FREQUENCY,
      InstrumentCommand.ICARG_RF_FREQUENCY_MHZ,
      centerFrequency_MHz));
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
  public final void setModulationSourceInternalFrequency_kHz (final double modulationSourceInternalFrequency_kHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_INTMODSOURCE_CONTROL,
      InstrumentCommand.ICARG_INTMODSOURCE_FREQUENCY_KHZ, modulationSourceInternalFrequency_kHz));
  }

  @Override
  protected void requestSettingsFromInstrumentASync () throws UnsupportedOperationException, IOException
  {
    throw new UnsupportedOperationException ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROCESS COMMAND
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  @Override
  protected void processCommand (final InstrumentCommand instrumentCommand)
    throws UnsupportedOperationException, IOException, InterruptedException
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
      device.lockDevice ();
      switch (commandString)
      {
        case InstrumentCommand.IC_NOP_KEY:
          break;
        case InstrumentCommand.IC_GET_SETTINGS_KEY:
        {
          final byte[] l1Buffer = new byte[HP8663A_GPIB.L1_LENGTH];
          this.out.println ("L1");
          final int bytesRead = device.getInputStream ().read (l1Buffer);
          // LOG.log (Level.WARNING, "number of bytes read = {0}", bytesRead);
          LOG.log (Level.WARNING, "bytes = {0}", bytesToHex (l1Buffer));
          if (this.lastL1 != null)
            for (int i = 0; i < HP8663A_GPIB.L1_LENGTH; i++)
              if (l1Buffer[i] != this.lastL1[i])
                LOG.log (Level.WARNING, "L1 string index {0} changed from {1} to {2}.",
                  new Object[]
                  {
                    i, Integer.toHexString (this.lastL1[i] & 0xff), Integer.toHexString (l1Buffer[i] & 0xff)
                  });
          this.lastL1 = l1Buffer;
          newInstrumentSettings = HP8663A_GPIB.settingsFromL1 (l1Buffer);
          break;
        }
        case InstrumentCommand.IC_RF_OUTPUT_ENABLE:
        {
          final Boolean outputEnable;
          if (instrumentCommand.containsKey (InstrumentCommand.ICARG_RF_OUTPUT_ENABLE))
            outputEnable = (Boolean) instrumentCommand.get (InstrumentCommand.ICARG_RF_OUTPUT_ENABLE);
          else
            outputEnable = true;
          if (outputEnable)
            this.out.println ("AP");
          else
            this.out.println ("AO");
          break;
        }
        case InstrumentCommand.IC_RF_OUTPUT_LEVEL:
        {
          // XXX There seems to be no way to just set the output level, without also enabling RF output.
          final double amplitude_dBm = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_OUTPUT_LEVEL_DBM);
          this.out.println ("AP " + amplitude_dBm + " DM");
          break;
        }
        case InstrumentCommand.IC_RF_FREQUENCY:
        {
          final double centerFrequency_MHz = (double) instrumentCommand.get (InstrumentCommand.ICARG_RF_FREQUENCY_MHZ);
          this.out.println ("FR " + centerFrequency_MHz + " MZ");
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
                this.out.println ("AM " + depth_percent + " PC");
                break;
              case INT_400:
                this.out.println ("AM M1 " + depth_percent + " PC");
                break;
              case INT_1K:
                this.out.println ("AM M2 " + depth_percent + " PC");
                break;
              case EXT_AC:
                this.out.println ("AM M3 " + depth_percent + " PC");
                break;
              case EXT_DC:
                this.out.println ("AM M4 " + depth_percent + " PC");
                break;
              default:
                throw new UnsupportedOperationException ();
            }            
          }
          else
            this.out.println ("AM FO");
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
                this.out.println ("FM " + fmDeviation_kHz + " KZ");
                break;
              case INT_400:
                this.out.println ("FM M1 " + fmDeviation_kHz + " KZ");
                break;
              case INT_1K:
                this.out.println ("FM M2 " + fmDeviation_kHz + " KZ");
                break;
              case EXT_AC:
                this.out.println ("FM M3 " + fmDeviation_kHz + " KZ");
                break;
              case EXT_DC:
                this.out.println ("FM M4 " + fmDeviation_kHz + " KZ");
                break;
              default:
                throw new UnsupportedOperationException ();
            }            
          }
          else
            this.out.println ("FM FO");  
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
                this.out.println ("PM " + pmDeviation_degrees + " DG");
                break;
              case INT_400:
                this.out.println ("PM M1 " + pmDeviation_degrees + " DG");
                break;
              case INT_1K:
                this.out.println ("PM M2 " + pmDeviation_degrees + " DG");
                break;
              case EXT_AC:
                this.out.println ("PM M3 " + pmDeviation_degrees + " DG");
                break;
              case EXT_DC:
                this.out.println ("PM M4 " + pmDeviation_degrees + " DG");
                break;
              default:
                throw new UnsupportedOperationException ();
            }            
          }
          else
            this.out.println ("PM FO");  
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
                this.out.println ("PL");
                break;
              case INT_400:
                this.out.println ("PL M1");
                break;
              case INT_1K:
                this.out.println ("PL M2");
                break;
              case EXT_AC:
                throw new UnsupportedOperationException ();
              case EXT_DC:
                this.out.println ("PL M4");
                break;
              default:
                throw new UnsupportedOperationException ();
            }            
          }
          else
            this.out.println ("PL FO");
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
                this.out.println ("BP");
                break;
              case INT_400:
                this.out.println ("BP M1");
                break;
              case INT_1K:
                this.out.println ("BP M2");
                break;
              case EXT_AC:
                throw new UnsupportedOperationException ();
              case EXT_DC:
                this.out.println ("BP M4");
                break;
              default:
                throw new UnsupportedOperationException ();
            }
          }
          else
            this.out.println ("BP FO");
          break;
        }
        case InstrumentCommand.IC_INTMODSOURCE_CONTROL:
        {
          final double intModSourceFrequency_kHz =
            (double) instrumentCommand.get (InstrumentCommand.ICARG_INTMODSOURCE_FREQUENCY_KHZ);
          this.out.println ("MF " + intModSourceFrequency_kHz + " KZ");
          break;
        }
        default:
          throw new UnsupportedOperationException ();
      }
    }
    finally
    {
      device.unlockDevice ();      
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
