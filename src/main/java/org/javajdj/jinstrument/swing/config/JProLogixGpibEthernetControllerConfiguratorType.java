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

import org.javajdj.jinstrument.controller.gpib.prologix.*;
import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentRegistry;
import org.javajdj.jinstrument.config.Configurator;
import org.javajdj.jinstrument.config.ConfiguratorType;
import org.javajdj.jinstrument.config.ConfiguratorTypeDB;

/** Implementation of {@link ConfiguratorType} for the Prologix GPIB-ETHERNET controller.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class JProLogixGpibEthernetControllerConfiguratorType
  implements ConfiguratorType<ProLogixGpibEthernetController>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JProLogixGpibEthernetControllerConfiguratorType.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the configurator type.
   * 
   */
  public JProLogixGpibEthernetControllerConfiguratorType ()
  {    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ConfiguratorType
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final Class<ProLogixGpibEthernetController> getConfiguredObjectClass ()
  {
    return ProLogixGpibEthernetController.class;
  }

  @Override
  public final Configurator<ProLogixGpibEthernetController> createConfigurator (
    final InstrumentRegistry instrumentRegistry,
    final ConfiguratorTypeDB configuratorTypeDB)
  {
    return new JProLogixGpibEthernetControllerConfigurator ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
