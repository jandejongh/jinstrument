package org.javajdj.jinstrument.r1.instrument.dso;

import org.javajdj.jinstrument.r1.instrument.InstrumentTraceSettings;

/**
 *
 */
public interface OscilloscopeTraceSettings
extends InstrumentTraceSettings
{

  @Override
  OscilloscopeTraceSettings clone () throws CloneNotSupportedException;
  
}
