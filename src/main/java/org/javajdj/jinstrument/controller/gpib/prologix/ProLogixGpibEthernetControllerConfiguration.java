/*
 * Copyright 2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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

import java.util.logging.Logger;
import org.javajdj.jinstrument.config.Configuration;

/** Implementation of {@link Configuration} for the Prologix GPIB-ETHERNET controller.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see ProLogixGpibEthernetController
 * 
 */
public final class ProLogixGpibEthernetControllerConfiguration
  implements Configuration<ProLogixGpibEthernetController>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (ProLogixGpibEthernetControllerConfiguration.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the object.
   * 
   * @param ipAddress      The IP address / host name.
   * @param tcpConnectPort The TCP port to connect to.
   * 
   */
  public ProLogixGpibEthernetControllerConfiguration (final String ipAddress, final int tcpConnectPort)
  {
    this.ipAddress = ipAddress;
    this.tcpConnectPort = tcpConnectPort;
  }
  
  /** Constructs the object with default IP address and TCP port.
   * 
   * @see ProLogixGpibEthernetController#DEFAULT_IP_ADDRESS
   * @see ProLogixGpibEthernetController#DEFAULT_TCP_PORT
   * 
   */
  public ProLogixGpibEthernetControllerConfiguration ()
  {
    this.ipAddress = ProLogixGpibEthernetController.DEFAULT_IP_ADDRESS;
    this.tcpConnectPort = ProLogixGpibEthernetController.DEFAULT_TCP_PORT;
  }
  
  /** Constructs a copy of this object with given IP address.
   * 
   * @param ipAddress The IP address.
   * 
   * @return The new object.
   * 
   */
  public final ProLogixGpibEthernetControllerConfiguration withIpAdress (final String ipAddress)
  {
    return new ProLogixGpibEthernetControllerConfiguration (ipAddress, this.tcpConnectPort);
  }
  
  /** Constructs a copy of this object with given TCP port.
   * 
   * @param tcpConnectPort The TCP port.
   * 
   * @return The new object.
   * 
   */
  public final ProLogixGpibEthernetControllerConfiguration withTcpConnectPort (final int tcpConnectPort)
  {
    return new ProLogixGpibEthernetControllerConfiguration (this.ipAddress, tcpConnectPort);
  }

  @Override
  public final Class<ProLogixGpibEthernetController> getConfiguredClass ()
  {
    return ProLogixGpibEthernetController.class;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // IP ADDRESS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private String ipAddress;

  /** Gets the IP address.
   * 
   * @return The IP address.
   * 
   */
  public final String getIpAddress ()
  {
    return this.ipAddress;
  }

  /** Sets the IP address.
   * 
   * @param ipAddress The new IP address.
   * 
   */
  public final void setIpAddress (final String ipAddress)
  {
    this.ipAddress = ipAddress;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TCP CONNECT PORT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private int tcpConnectPort;

  /** Gets the TCP port.
   * 
   * @return The TCP port.
   * 
   */
  public final int getTcpConnectPort ()
  {
    return this.tcpConnectPort;
  }

  /** Sets the TCP port.
   * 
   * @param tcpConnectPort The new TCP port.
   * 
   */
  public final void setTcpConnectPort (final int tcpConnectPort)
  {
    this.tcpConnectPort = tcpConnectPort;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
