package org.javajdj.jinstrument.r1.instrument;

import org.javajdj.jinstrument.r1.Device;

/**
 *
 */
public interface Instrument
{
  
  Device getDevice ();
  
  boolean isPoweredOn ();
  
  void setPoweredOn (boolean poweredOn);
  
}
