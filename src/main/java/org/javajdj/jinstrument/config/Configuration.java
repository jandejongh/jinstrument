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

/** An object representing the (construction) configuration of some specific type. 
 *
 * @param <T> The type of objects to which the configuration applies.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see Configurable
 * @see Configurator
 * @see ConfiguratorType
 * 
 */
public interface Configuration<T>
{
  
  /** Returns the (most specific) {@code class} of objects to which this configuration applies.
   * 
   * <p>
   * This must be a class property.
   * 
   * @return The (most specific) {@code class} of objects to which this configuration applies (non-{@code null}).
   * 
   */
  Class<? super T> getConfiguredClass ();
  
}
