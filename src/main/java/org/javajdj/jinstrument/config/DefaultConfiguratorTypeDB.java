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
package org.javajdj.jinstrument.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** A default implementation of {@link ConfiguratorTypeDB}. 
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultConfiguratorTypeDB
  implements ConfiguratorTypeDB
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultConfiguratorTypeDB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private DefaultConfiguratorTypeDB ()
  {
    this.classMap = new HashMap<> ();
  }
  
  private final static DefaultConfiguratorTypeDB INSTANCE = new DefaultConfiguratorTypeDB ();
  
  /** Returns the single instance of this class.
   * 
   * <p>
   * The implementation follows the singleton design pattern.
   * 
   * @return The single instance of this class.
   * 
   */
  public final static DefaultConfiguratorTypeDB getInstance ()
  {
    return DefaultConfiguratorTypeDB.INSTANCE;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CLASS MAP
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Map<Class, List<ConfiguratorType>> classMap;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ConfigurationTypeDB
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public void addConfiguratorType (final ConfiguratorType configuratorType)
  {
    if (configuratorType == null)
      throw new IllegalArgumentException ();
    final Class configurableClass = configuratorType.getConfiguredObjectClass ();
    if (! this.classMap.containsKey (configurableClass))
      this.classMap.put (configurableClass, new ArrayList<> ());
    if (! this.classMap.get (configurableClass).contains (configuratorType))
      this.classMap.get (configurableClass).add (configuratorType);
  }

  @Override
  public void removeConfiguratorType (final ConfiguratorType configuratorType)
  {
    if (configuratorType != null && this.classMap.containsKey (configuratorType.getConfiguredObjectClass ()))
    {
      this.classMap.get (configuratorType.getConfiguredObjectClass ()).remove (configuratorType);
    }
  }
  
  @Override
  public <T> ConfiguratorType<T> getConfiguratorType (final Class<T> configurableClass)
  {
    if (configurableClass == null || ! this.classMap.containsKey (configurableClass))
      return null;
    // XXX For now, only exact 'class' matches...
    return this.classMap.get (configurableClass).get (0);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
