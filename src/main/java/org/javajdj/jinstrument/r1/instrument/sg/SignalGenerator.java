package org.javajdj.jinstrument.r1.instrument.sg;

import java.io.IOException;

/**
 *
 */
public interface SignalGenerator
{

  SignalGeneratorSettings getSettingsFromInstrument () throws IOException, InterruptedException;

  void setSettingsOnInstrument (SignalGeneratorSettings signalGeneratorSettings) throws IOException, InterruptedException;
  
  void setCenterFrequency_MHz (double centerFrequency_MHz) throws IOException, InterruptedException;
  
  void setAmplitude_dBm (double amplitude_dBm) throws IOException, InterruptedException;
  
}
