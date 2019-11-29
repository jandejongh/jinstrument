package org.javajdj.jinstrument.r1;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 */
public class DefaultManager
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultManager.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private DefaultManager ()
  {
  }
  
  private static DefaultManager INSTANCE = null;
  
  public synchronized static DefaultManager getInstance ()
  {
    if (DefaultManager.INSTANCE == null)
      DefaultManager.INSTANCE = new DefaultManager ();
    return DefaultManager.INSTANCE;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PLUGINS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Set<Plugin> plugins = new LinkedHashSet<> ();
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<ControllerTypeHandler> handlers = new LinkedHashSet<>  ();
  
  public final synchronized void registerInstrumentControllerTypeHandler (final ControllerTypeHandler handler)
  {
    if (handler == null)
      throw new IllegalArgumentException ();
    this.handlers.add (handler);
  }
  
  public final synchronized void unregisterInstrumentControllerTypeHandler (final ControllerTypeHandler handler)
  {
    this.handlers.remove (handler);
  }
  
  private final Set<Controller> controllers = new LinkedHashSet<> ();
  
  public final Controller openInstrumentController (final String controllerUrl)
  {
    if (controllerUrl == null || controllerUrl.trim ().isEmpty ())
      return null;
    for (ControllerTypeHandler handler : this.handlers)
      if (handler.isValidUrl (controllerUrl))
      {
        final Controller controller = handler.openController (controllerUrl);
        if (controller != null)
        {
          this.controllers.add (controller);
          return controller;
        }
      }
    return null;
  }
  
  public final void closeInstrumentController (final Controller controller)
  {
    if (controller != null)
      if (this.controllers.contains (controller))
      {
        this.controllers.remove (controller);
        controller.close ();
      }
  }
  
  public final Device openDevice (final Controller controller, final String deviceUrl)
  {
    if (controller == null)
    {
      // XXX COULD allow implicit opening of the controller...
      return null;
    }
    if (! this.controllers.contains (controller))
      return null;
    if (! controller.isDeviceUrl (deviceUrl))
      return null;
    return controller.openDevice (deviceUrl);
  }
  
  public final void closeDevice (final Device device)
  {
    throw new UnsupportedOperationException ();    
  }
  
//  public final DeviceClient registerInstrumentClient
//  (final Instrument instrument, final DeviceClient instrumentClient)
//  {
//    throw new UnsupportedOperationException ();    
//  }
//  
//  public final void unregisterInstrumentClient (final DeviceClient instrumentClient)
//  {
//    throw new UnsupportedOperationException ();    
//  }
//  
}
