package org.javajdj.jinstrument.r1.instrument.sa;

import java.io.IOException;
import org.javajdj.jinstrument.r1.instrument.Instrument;

/**
 *
 */
public interface SignalGenerator
extends Instrument
{

  SpectrumAnalyzerTrace getTrace () throws IOException, InterruptedException;
  
  SpectrumAnalyzerTrace getTrace (SpectrumAnalyzerTraceSettings settings) throws IOException, InterruptedException;
  
}
