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
package org.javajdj.jinstrument.swing.config;

import java.awt.Dimension;
import java.awt.GridLayout;
import org.javajdj.jinstrument.controller.gpib.prologix.*;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.javajdj.jinstrument.config.Configuration;
import org.javajdj.jinstrument.config.Configurator;

/** Implementation of {@link Configurator} for the Prologix GPIB-ETHERNET controller.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class JProLogixGpibEthernetControllerConfigurator
  extends JPanel
  implements Configurator<ProLogixGpibEthernetController>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JProLogixGpibEthernetControllerConfigurator.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the configurator.
   * 
   */
  public JProLogixGpibEthernetControllerConfigurator ()
  {
    
    setLayout (new GridLayout (5, 2));
    setPreferredSize (new Dimension (320, 200));
    
    add (new JLabel ());
    add (new JLabel ());
    
    add (new JLabel ("Host:"));
    this.jHost = new JTextField ("myhost.mydomain", 32);
    add (this.jHost);
    
    add (new JLabel ());
    add (new JLabel ());
    
    add (new JLabel ("Port:"));
    this.jPort = new JTextField (Integer.toString (ProLogixGpibEthernetController.DEFAULT_TCP_PORT), 6);
    add (this.jPort);
    
    add (new JLabel ());
    add (new JLabel ());
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Configurator
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final Class<ProLogixGpibEthernetController> getConfiguredObjectClass ()
  {
    return ProLogixGpibEthernetController.class;
  }

  @Override
  public final Configuration<ProLogixGpibEthernetController> getConfiguration ()
  {
    final String hostString = this.jHost.getText ().trim ();
    if (hostString == null || hostString.isEmpty ())
    {
      this.errorMessage = "No host.";
      return null;
    }
    final String portString = this.jPort.getText ().trim ();
    final int port;
    try
    {
      port = Integer.parseInt (portString);
    }
    catch (NumberFormatException nfe)
    {
      this.errorMessage = "Illegal integer for port.";
      return null;
    }
    if (port < 0 || port > 65535)
    {
      this.errorMessage = "Port number must be between 0 and 65535 inclusive.";
      return null;
    }
    this.errorMessage = null;
    return new ProLogixGpibEthernetControllerConfiguration (hostString, port);
  }
  
  @Override
  public final Configuration<ProLogixGpibEthernetController> withConfiguration (
    final Configuration<ProLogixGpibEthernetController> configuration)
  {
    if (configuration == null || ! (configuration instanceof ProLogixGpibEthernetControllerConfiguration))
    {
      this.errorMessage = "No or invalid configuration; please report!";
      return null;
    }
    final String ipAddress = ((ProLogixGpibEthernetControllerConfiguration) configuration).getIpAddress ();
    final int port = ((ProLogixGpibEthernetControllerConfiguration) configuration).getTcpConnectPort ();
    if (ipAddress == null || ipAddress.trim ().isEmpty ())
    {
      this.errorMessage = "No host.";
      return null;
    }
    if (port < 0 || port > 65535)
    {
      this.errorMessage = "Port number must be between 0 and 65535 inclusive.";
      return null;
    }
    this.errorMessage = null;
    this.jHost.setText (ipAddress.trim ());
    this.jPort.setText (Integer.toString (port));
    return configuration;
  }
  
  @Override
  public final ProLogixGpibEthernetController createObject (final Configuration<ProLogixGpibEthernetController> configuration)
  {
    if (configuration == null || ! (configuration instanceof ProLogixGpibEthernetControllerConfiguration))
    {
      this.errorMessage = "No or invalid configuration; please report!";
      return null;      
    }
    final ProLogixGpibEthernetControllerConfiguration proLogixGpibEthernetControllerConfiguration =
      (ProLogixGpibEthernetControllerConfiguration) configuration;
    final String hostString = proLogixGpibEthernetControllerConfiguration.getIpAddress ();
    final int port = proLogixGpibEthernetControllerConfiguration.getTcpConnectPort ();
    if (hostString == null || hostString.isEmpty ())
    {
      this.errorMessage = "No host.";
      return null;
    }
    if (port < 0 || port > 65535)
    {
      this.errorMessage = "Port number must be between 0 and 65535 inclusive.";
      return null;
    }
    this.errorMessage = null;
    return new ProLogixGpibEthernetController (proLogixGpibEthernetControllerConfiguration);
  }
  
  private String errorMessage = null;
  
  @Override
  public final String getErrorMessage ()
  {
    return this.errorMessage;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JTextField jHost;
  private final JTextField jPort;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
