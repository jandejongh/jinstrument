/*
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.controller.gpib.prologix;

import org.javajdj.jinstrument.BusType;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.ControllerType;
import org.javajdj.jinstrument.controller.gpib.BusType_GPIB;

/** Implementation of {@link ControllerType} for the Prologix GPIB-ETHERNET controller.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class ProLogixGpibEthernetControllerType
implements ControllerType
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private ProLogixGpibEthernetControllerType ()
  {
  }
  
  private final static ProLogixGpibEthernetControllerType INSTANCE = new ProLogixGpibEthernetControllerType ();
  
  public final static ProLogixGpibEthernetControllerType getInstance ()
  {
    return ProLogixGpibEthernetControllerType.INSTANCE;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public String toString ()
  {
    return getControllerTypeUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ControllerType
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final Class<ProLogixGpibEthernetController> getControllerClass ()
  {
    return ProLogixGpibEthernetController.class;
  }
  
  public final static String PROLOGIX_GPIB_ETHERNET_CONTROLLER_TYPE_NAME = "prologix-eth";
  
  @Override
  public final String getControllerTypeUrl ()
  {
    return ProLogixGpibEthernetControllerType.PROLOGIX_GPIB_ETHERNET_CONTROLLER_TYPE_NAME;
  }

  @Override
  public final BusType getBusType ()
  {
    return BusType_GPIB.getInstance ();
  }
  
  public final static int DEFAULT_TCP_CONNECT_PORT = 1234;
  
  @Override
  public final Controller openController (final String relativeControllerUrl)
  {
    final String addressAndPortString = relativeControllerUrl;
    final String ipAddress;
    final int tcpConnectPort;
    if (! addressAndPortString.contains (":"))
    {
      ipAddress = addressAndPortString;
      tcpConnectPort = ProLogixGpibEthernetControllerType.DEFAULT_TCP_CONNECT_PORT;
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
