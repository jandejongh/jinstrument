package org.javajdj.jinstrument.r1.instrument;

/**
 *
 */
public interface InstrumentListener
{
  
  void newInstrumentStatus (Instrument instrument, InstrumentStatus instrumentStatus);
  
  void newInstrumentSettings (Instrument instrument, InstrumentSettings instrumentSettings);
  
}
