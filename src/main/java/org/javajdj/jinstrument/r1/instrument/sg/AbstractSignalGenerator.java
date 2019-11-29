package org.javajdj.jinstrument.r1.instrument.sg;

import org.javajdj.jinstrument.r1.Device;
import org.javajdj.jinstrument.r1.instrument.AbstractInstrument;

/**
 *
 */
public abstract class AbstractSignalGenerator
  extends AbstractInstrument
  implements SignalGenerator
{

  public AbstractSignalGenerator (final Device device)
  {
    super (device);
  }
  
}
