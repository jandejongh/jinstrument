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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.ControllerType;
import org.javajdj.jinstrument.InstrumentRegistry;
import org.javajdj.jinstrument.config.Configuration;
import org.javajdj.jinstrument.config.Configurator;
import org.javajdj.jinstrument.config.ConfiguratorType;
import org.javajdj.jinstrument.config.ConfiguratorTypeDB;
import org.javajdj.jinstrument.config.generic.ControllerConfiguration;
import org.javajdj.jswing.jcenter.JCenter;

/** Implementation of {@link Configurator} for a {@link Controller}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class JControllerConfigurator
  extends JPanel
  implements Configurator<Controller>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JControllerConfigurator.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the configurator.
   * 
   * @param instrumentRegistry The instrument registry.
   * @param configuratorTypeDB The configurator-type DB.
   * 
   */
  public JControllerConfigurator (
    final InstrumentRegistry instrumentRegistry,
    final ConfiguratorTypeDB configuratorTypeDB)
  {
    
    this.instrumentRegistry = instrumentRegistry;
    this.configuratorTypeDB = configuratorTypeDB;
    
    setLayout (new GridLayout (2, 1));
    setPreferredSize (new Dimension (320, 200));
    
    final JPanel jControllerTypePanel = new JPanel ();
    jControllerTypePanel.setBorder(BorderFactory.createTitledBorder ("Controller Type"));
    add (jControllerTypePanel);
    
    jControllerTypePanel.setLayout (new BorderLayout ());
    this.jControllerType = new JComboBox<ControllerType> (
      instrumentRegistry.getControllerTypes ().toArray (new ControllerType[]{}))
    {
      @Override
      public final Dimension getMaximumSize ()
      {
        final Dimension dim = super.getMaximumSize ();
        dim.width = getPreferredSize ().width;
        dim.height = getPreferredSize ().height;
        return dim;
      }
    };
    this.jControllerType.setEditable (false);
    this.jControllerType.setSelectedItem (null);
    this.jControllerType.addItemListener (this.jControllerTypeListener);
    jControllerTypePanel.add (JCenter.XY (this.jControllerType));
    
    this.jControllerConfigurationPanel = new JPanel ();
    this.jControllerConfigurationPanel.setBorder(BorderFactory.createTitledBorder ("Controller Configuration"));
    add (this.jControllerConfigurationPanel);
    
    this.jControllerConfigurationPanel.setLayout (new BorderLayout ());
    this.jControllerConfigurationPanel.add (new JLabel ("Please select controller type."));
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Configurator
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final Class<Controller> getConfiguredObjectClass ()
  {
    return Controller.class;
  }
  
  @Override
  public final Configuration<Controller> getConfiguration ()
  {
    final ControllerType selectedControllerType = (ControllerType) this.jControllerType.getSelectedItem ();
    if (selectedControllerType == null)
    {
      this.errorMessage = "No controller type.";
      return null;
    }
    if (this.jControllerConfigurationConfigurator == null)
    {
      this.errorMessage = "No controller configuration.";
      return null;
    }
    final Configuration<? extends Controller> controllerConfiguration =
      this.jControllerConfigurationConfigurator.getConfiguration ();
    if (controllerConfiguration == null)
    {
      this.errorMessage = "No or invalid controller configuration: "
        + this.jControllerConfigurationConfigurator.getErrorMessage ();
      return null;
    }
    this.errorMessage = null;
    return new ControllerConfiguration (selectedControllerType, controllerConfiguration);
  }

  @Override
  public final Configuration<Controller> withConfiguration (final Configuration<Controller> configuration)
  {
    if (configuration == null || ! (configuration instanceof ControllerConfiguration))
    {
      this.errorMessage = "No or invalid configuration; please report!";
      return null;
    }
    final ControllerType controllerType = ((ControllerConfiguration) configuration).getControllerType ();
    final Configuration<Controller> controllerConfiguration =
      ((ControllerConfiguration) configuration).getControllerConfiguration ();
    if (controllerType == null)
    {
      this.errorMessage = "No controller type.";
      return null;
    }
    if (controllerConfiguration == null)
    {
      this.errorMessage = "No (specific) controller configuration.";
      return null;
    }
    this.errorMessage = null;
    this.jControllerType.setSelectedItem (controllerType);
    setSelectedControllerTypeConfiguration (controllerType, controllerConfiguration);
    return configuration;
  }

  @Override
  public final Controller createObject (final Configuration<Controller> configuration)
  {
    if (configuration == null || ! (configuration instanceof ControllerConfiguration))
    {
      this.errorMessage = "No or invalid configuration; please report!";
      return null;      
    }
    //
    // XXX The use of generic in this method is a bit smelly...
    //
    final ControllerType controllerType = ((ControllerConfiguration) configuration).getControllerType ();
    final Configuration<Controller> controllerConfiguration =
      ((ControllerConfiguration) configuration).getControllerConfiguration ();
    if (controllerType == null)
    {
      this.errorMessage = "No controller type.";
      return null;
    }
    if (controllerConfiguration == null)
    {
      this.errorMessage = "No (specific) controller configuration.";
      return null;
    }
    final ConfiguratorType<Controller> configuratorType =
      this.configuratorTypeDB.getConfiguratorType (controllerType.getControllerClass ());
    if (configuratorType == null)
    {
      this.errorMessage = "No configurator-type for (spepcif) controller type; please report!";
      return null;      
    }
    // XXX configuratorType uses JDialog's for error reporting...
    final Controller controller = (Controller) configuratorType.createFromConfiguration (
        this.instrumentRegistry,
        this.configuratorTypeDB,
        controllerConfiguration);
    // XXX Should get error message from configuratorType?
    this.errorMessage = null;
    return controller;
  }
  
  private String errorMessage = null;
  
  @Override
  public final String getErrorMessage ()
  {
    return this.errorMessage;
  }

  private final InstrumentRegistry instrumentRegistry;
  private final ConfiguratorTypeDB configuratorTypeDB;
  
  private ConfiguratorType jControllerConfigurationConfiguratorType;
  private Configurator jControllerConfigurationConfigurator;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JComboBox<ControllerType> jControllerType;
  
  private final JPanel jControllerConfigurationPanel;
  
  private void setSelectedControllerTypeConfiguration (
    final ControllerType controllerType,
    final Configuration controllerConfiguration)
  {
    this.jControllerConfigurationPanel.removeAll ();
    if (controllerType != null)
    {
      this.jControllerConfigurationConfiguratorType =
        this.configuratorTypeDB.getConfiguratorType (controllerType.getControllerClass ());
      if (this.jControllerConfigurationConfiguratorType == null)
      {
        this.jControllerConfigurationPanel.add (new JLabel ("Unsupported; please report!"));
        this.jControllerConfigurationConfigurator = null;
      }
      else
      {
        this.jControllerConfigurationConfigurator =
          this.jControllerConfigurationConfiguratorType.createConfigurator (this.instrumentRegistry, this.configuratorTypeDB);
        if (this.jControllerConfigurationConfigurator != null && controllerConfiguration != null)
        {
          if (this.jControllerConfigurationConfigurator.withConfiguration (controllerConfiguration) == null)
          {
            JControllerConfigurator.this.jControllerConfigurationPanel.add (new JLabel ("Invalid configuration; please report!"));
            JControllerConfigurator.this.jControllerConfigurationConfigurator = null;       
          }
        }
        if (this.jControllerConfigurationConfigurator == null
          || ! (this.jControllerConfigurationConfigurator instanceof JPanel))
        {
          JControllerConfigurator.this.jControllerConfigurationPanel.add (new JLabel ("Unsupported; please report!"));
          JControllerConfigurator.this.jControllerConfigurationConfigurator = null;
        }
        else
        {
          JControllerConfigurator.this.jControllerConfigurationPanel.add (
            (JPanel) JControllerConfigurator.this.jControllerConfigurationConfigurator);          
        }
      }
    }
    else
      this.jControllerConfigurationPanel.add (new JLabel ("Please select controller type."));
  }
  
  private final ItemListener jControllerTypeListener = (final ItemEvent ie) ->
  {
    if (ie == null
      || ie.getSource () != JControllerConfigurator.this.jControllerType
      || ie.getStateChange () != ItemEvent.SELECTED)
      return;
    final ControllerType selectedControllerType = (ControllerType) JControllerConfigurator.this.jControllerType.getSelectedItem ();
    JControllerConfigurator.this.jControllerConfigurationPanel.removeAll ();
    if (selectedControllerType != null)
    {
      JControllerConfigurator.this.jControllerConfigurationConfiguratorType =
        JControllerConfigurator.this.configuratorTypeDB.getConfiguratorType (selectedControllerType.getControllerClass ());
      JControllerConfigurator.this.setSelectedControllerTypeConfiguration (selectedControllerType, null);
    }
    revalidate ();    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
