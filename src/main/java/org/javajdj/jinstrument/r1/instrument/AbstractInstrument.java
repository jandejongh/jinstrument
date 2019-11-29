package org.javajdj.jinstrument.r1.instrument;

import org.javajdj.jinstrument.r1.Device;

/**
 *
 */
public abstract class AbstractInstrument
implements Instrument
{

  protected AbstractInstrument (final Device device)
  {
    if (device == null)
      throw new IllegalArgumentException ();
    this.device = device;
  }
  
  private final Device device;

  @Override
  public Device getDevice ()
  {
    return this.device;
  }
  
}
