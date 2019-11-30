package org.javajdj.jinstrument.r1.instrument.dso;

import java.util.List;
import java.util.logging.Logger;
import org.javajdj.jinstrument.r1.Device;
import org.javajdj.jinstrument.r1.instrument.AbstractInstrument;
import org.javajdj.jinstrument.r1.instrument.ChannelId;
import org.javajdj.jservice.Service;

/**
 *
 */
public abstract class AbstractOscilloscope<CI extends ChannelId>
extends AbstractInstrument
implements Oscilloscope<CI>
{

  private static final Logger LOG = Logger.getLogger (AbstractOscilloscope.class.getName ());

  protected AbstractOscilloscope (final String name,
    final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices)
  {
    super (name, device, runnables, targetServices);
  }

  protected AbstractOscilloscope (final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices)
  {
    super (device, runnables, targetServices);
  }

}
