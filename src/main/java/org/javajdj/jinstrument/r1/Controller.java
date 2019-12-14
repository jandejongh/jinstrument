package org.javajdj.jinstrument.r1;

import org.javajdj.jservice.Service;

/**
 *
 */
public interface Controller
  extends Service
{
  
  boolean isDeviceUrl (final String deviceUrl);
  
  Device openDevice (final String deviceUrl);
  
  void close ();
  
}
