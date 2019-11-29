package org.javajdj.jinstrument.r1.instrument.sg;

import org.javajdj.jinstrument.r1.instrument.InstrumentSettings;

/**
 *
 */
public interface SignalGeneratorSettings
  extends InstrumentSettings
{
  
  @Override
  SignalGeneratorSettings clone() throws CloneNotSupportedException;
  
  double getCenterFrequency_MHz ();

  double getS_dBm ();
  
}
