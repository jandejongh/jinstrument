package org.javajdj.jinstrument.r1;

/**
 *
 */
public interface Controller
{
  
  boolean isDeviceUrl (final String deviceUrl);
  
  Device openDevice (final String deviceUrl);
  
  void close ();
  
}
