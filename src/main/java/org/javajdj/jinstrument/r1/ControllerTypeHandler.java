package org.javajdj.jinstrument.r1;

/**
 *
 */
public interface ControllerTypeHandler
{
  
  boolean isValidUrl (final String controllerUrl);
  
  Controller openController (final String controllerUrl);
  
}
