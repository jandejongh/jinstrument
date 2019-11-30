package org.javajdj.jinstrument.r1.instrument;

import java.util.List;
import org.javajdj.jinstrument.r1.Device;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.support.Service_FromMix;

/**
 *
 */
public abstract class AbstractInstrument
extends Service_FromMix
implements Instrument
{

  protected AbstractInstrument (final String name,
    final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices)
  {
    super (name, runnables, targetServices);
    if (device == null)
      throw new IllegalArgumentException ();
    this.device = device;
  }
  
  protected AbstractInstrument (final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices)
  {
    super (runnables, targetServices);
    if (device == null)
      throw new IllegalArgumentException ();
    this.device = device;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Device device;

  @Override
  public Device getDevice ()
  {
    return this.device;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
