package org.javajdj.jinstrument.r1.instrument.dso;

import java.util.logging.Logger;
import org.javajdj.jinstrument.r1.Device;
import org.javajdj.jinstrument.r1.instrument.AbstractInstrument;
import org.javajdj.jinstrument.r1.instrument.ChannelId;

/**
 *
 */
public abstract class AbstractOscilloscope<CI extends ChannelId>
extends AbstractInstrument
implements Oscilloscope<CI>
{

  private static final Logger LOG = Logger.getLogger (AbstractOscilloscope.class.getName ());

  protected AbstractOscilloscope (final Device device)
  {
    super (device);
  }

}
