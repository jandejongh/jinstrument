package org.javajdj.jinstrument.r1.instrument.sg;

import java.util.List;
import org.javajdj.jinstrument.r1.Device;
import org.javajdj.jinstrument.r1.instrument.AbstractInstrument;
import org.javajdj.jservice.Service;

/**
 *
 */
public abstract class AbstractSignalGenerator
  extends AbstractInstrument
  implements SignalGenerator
{

  public AbstractSignalGenerator (final String name,
    final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices)
  {
    super (name, device, runnables, targetServices);
  }
  
  public AbstractSignalGenerator (final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices)
  {
    super (device, runnables, targetServices);
  }
  
}
