package org.javajdj.jinstrument.r1.instrument.dso;

import org.javajdj.jinstrument.r1.instrument.InstrumentTrace;

/**
 *
 */
public interface OscilloscopeTrace
extends InstrumentTrace
{
  
  @Override
  OscilloscopeTraceSettings getSettings ();
  
}
