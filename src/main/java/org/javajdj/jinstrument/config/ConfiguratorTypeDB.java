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

/** An in-store database of {@link ConfiguratorType}s. 
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see ConfiguratorType
 * 
 */
public interface ConfiguratorTypeDB
{
  
  /** Adds a configurator type.
   * 
   * @param configuratorType The configurator type.
   * 
   */
  void addConfiguratorType (ConfiguratorType configuratorType);
  
  /** Removes a configurator type.
   * 
   * @param configuratorType The configurator type.
   * 
   */
  void removeConfiguratorType (ConfiguratorType configuratorType);
  
  /** Returns a configurator type for given object {@code class}.
   * 
   * @param <T> The (generic) object type.
   * 
   * @param configurableClass The object {@code class} for which to return the {@link ConfiguratorType}.
   * 
   * @return The configurator type, {@code null} if not found.
   * 
   */
  <T> ConfiguratorType<T> getConfiguratorType (Class<T> configurableClass);
  
}
