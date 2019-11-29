package org.javajdj.jinstrument.r1.instrument;

/**
 *
 */
public interface InstrumentTraceSettings
  extends InstrumentSettings
{
  
  @Override
  InstrumentTraceSettings clone () throws CloneNotSupportedException;
  
  int getTraceLength ();
  
}
