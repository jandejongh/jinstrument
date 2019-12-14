package org.javajdj.jinstrument.r1.controller.gpib.tcpip.prologix;

import org.javajdj.jinstrument.r1.Controller;
import org.javajdj.jinstrument.r1.ControllerTypeHandler;

/**
 *
 */
public class ProLogixGpibEthernetControllerTypeHandler
implements ControllerTypeHandler
{

  private ProLogixGpibEthernetControllerTypeHandler ()
  {
  }
  
  private final static ProLogixGpibEthernetControllerTypeHandler INSTANCE = new ProLogixGpibEthernetControllerTypeHandler ();
  
  public final static ProLogixGpibEthernetControllerTypeHandler getInstance ()
  {
    return ProLogixGpibEthernetControllerTypeHandler.INSTANCE;
  }
  
  public final static String PROLOGIX_URL_PREFIX = "prologix://";
  
  public final static boolean isValidUrlStatic (final String controllerUrl)
  {
    if (controllerUrl == null)
      return false;
    return controllerUrl.trim ().toLowerCase ().startsWith (PROLOGIX_URL_PREFIX);    
  }
  
  @Override
  public final boolean isValidUrl (final String controllerUrl)
  {
    return ProLogixGpibEthernetControllerTypeHandler.isValidUrlStatic (controllerUrl);
  }

  public final static int DEFAULT_TCP_CONNECT_PORT = 1234;
  
  @Override
  public final Controller openController (final String controllerUrl)
  {
    if (! isValidUrl (controllerUrl))
      return null;
    final String addressAndPortString =
      controllerUrl.replaceFirst (ProLogixGpibEthernetControllerTypeHandler.PROLOGIX_URL_PREFIX, "");
    final String ipAddress;
    final int tcpConnectPort;
    if (! addressAndPortString.contains (":"))
    {
      ipAddress = addressAndPortString;
      tcpConnectPort = ProLogixGpibEthernetControllerTypeHandler.DEFAULT_TCP_CONNECT_PORT;
    }
    else
    {
      ipAddress = addressAndPortString.replaceFirst (":.*", "");
      try
      {
        tcpConnectPort = Integer.parseInt (addressAndPortString.replaceFirst (".*:", ""));   
        if (tcpConnectPort < 0 || tcpConnectPort > 65535)
          return null;
      }
      catch (NumberFormatException nfe)
      {
        return null;
      }
    }
    return new ProLogixGpibEthernetController (ipAddress, tcpConnectPort);
  }
  
}
