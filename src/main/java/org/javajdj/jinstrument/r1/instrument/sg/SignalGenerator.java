package org.javajdj.jinstrument.r1.instrument.sg;

import java.io.IOException;
import org.javajdj.jinstrument.r1.instrument.Instrument;

/**
 *
 */
public interface SignalGenerator
  extends Instrument
{

  enum ModulationSource
  {
    INT,
    INT_400,
    INT_1K,
    EXT_AC,
    EXT_DC;
  }
  
  SignalGeneratorSettings getSettingsFromInstrument () throws IOException, InterruptedException;

  void setSettingsOnInstrument (SignalGeneratorSettings signalGeneratorSettings) throws IOException, InterruptedException;
  
  void setCenterFrequency_MHz (double centerFrequency_MHz) throws IOException, InterruptedException;
  
  void setOutputEnable (boolean outputEnable) throws IOException, InterruptedException;
  
  void setAmplitude_dBm (double amplitude_dBm) throws IOException, InterruptedException;
  
  void setModulationSourceInternalFrequency_kHz (double ModulationSourceInternalFrequency_kHz)
    throws IOException, InterruptedException;

  void setEnableAm (boolean enableAm, double depth_percent, ModulationSource modulationSource)
    throws IOException, InterruptedException;
  
  void setEnableFm (boolean enableFm) throws IOException, InterruptedException;
  
  void setEnablePm (boolean enablePm) throws IOException, InterruptedException;
  
}
