/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.javajdj.jinstrument.controller.gpib;

import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.ControllerType;

/** Implementation of {@link ControllerType} for the Prologix GPIB-ETHERNET controller.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class ProLogixGpibEthernetControllerTypeHandler
implements ControllerType
{

  private ProLogixGpibEthernetControllerTypeHandler ()
  {
  }
  
  private final static ProLogixGpibEthernetControllerTypeHandler INSTANCE = new ProLogixGpibEthernetControllerTypeHandler ();
  
  public final static ProLogixGpibEthernetControllerTypeHandler getInstance ()
  {
    return ProLogixGpibEthernetControllerTypeHandler.INSTANCE;
  }
  
  public final static String PROLOGIX_URL_PREFIX = "prologix-eth://";
  
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
