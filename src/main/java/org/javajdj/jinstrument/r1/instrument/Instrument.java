package org.javajdj.jinstrument.r1.instrument;

import org.javajdj.jinstrument.r1.Device;
import org.javajdj.jservice.Service;

/**
 *
 */
public interface Instrument
  extends Service
{
  
  Device getDevice ();
  
  boolean isPoweredOn ();
  
  void setPoweredOn (boolean poweredOn);
  
}
