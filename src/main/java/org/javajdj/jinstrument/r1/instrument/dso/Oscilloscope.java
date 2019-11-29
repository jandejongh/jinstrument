package org.javajdj.jinstrument.r1.instrument.dso;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.javajdj.jinstrument.r1.instrument.Instrument;
import org.javajdj.jinstrument.r1.instrument.ChannelId;

/**
 *
 */
public interface Oscilloscope<CI extends ChannelId>
extends Instrument
{

  List<CI> getChannelIds ();
  
  OscilloscopeTrace getTrace (CI channel, OscilloscopeTraceSettings settings) throws IOException, InterruptedException;
  
  Map<CI, OscilloscopeTrace> getTraceMap (Map<CI, OscilloscopeTraceSettings> settings) throws IOException, InterruptedException;
  
  // XXX
  // OscilloscopeTrace getTrace (C channel, OscilloscopeTraceSettings settings) throws IOException, InterruptedException;
  
}
