package org.javajdj.jinstrument.r1.instrument;

/**
 *
 */
public interface InstrumentTrace
{
  
  InstrumentTraceSettings getSettings ();
  
  double[] getSamples ();
  
  boolean isError ();
  
  String getErrorMessage ();
  
  boolean isUncalibrated ();
  
  boolean isUncorrected ();
  
}
