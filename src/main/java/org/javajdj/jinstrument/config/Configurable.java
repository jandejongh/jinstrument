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

/** A configurable object. 
 *
 * <p>
 * A configurable class supports a public constructor with a single argument
 * of proper {@link Configuration} type.
 * Optionally, implementations may support reconfiguration
 * through {@link #reconfigure}.
 * (The default implementation does not support reconfiguration.)
 * 
 * @param <T> The (more) concrete type of objects.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 * @see Configuration
 * @see ConfiguratorType
 * @see Configurator
 * 
 */
public interface Configurable<T extends Configurable>
{
  
  /** Gets the type of configuration objects.
   * 
   * <p>
   * This must be a {@code class} property.
   * 
   * <p>
   * XXX The generic signature of the return value is likely incorrect.
   * 
   * <p>
   * Note that in order to implement the {@link Configurator} framework for
   * a certain object type, it is not necessary to implement the {@link Configurable}
   * interface for that object.
   * The framework provides ample opportunities to implement {@link Configurator}
   * and {@link ConfiguratorType} for {@code class}es that do <i>not</i>
   * implement {@link Configurable}.
   * 
   * @return The type of configuration objects, non-{@code null}.
   * 
   */
  Class<Configuration<T>> getConfigurationType ();
  
  /** Returns whether this object can be reconfigured.
   * 
   * <p>
   * This must be a {@code class} property.
   * 
   * <p>
   * The default implementation returns {@code false}.
   * 
   * @return Whether the object can be reconfigured.
   * 
   */
  default boolean isReconfigurable ()
  {
    return false;
  }
  
  /** Reconfigures this object with given configuration.
   * 
   * <p>
   * The default implementation throws an {@link UnsupportedOperationException}.
   * 
   * @param configuration The new configuration, may be {@code null} to indicate default configuration.
   * 
   * @throws UnsupportedOperationException If reconfiguring the object is not supported.
   * 
   */
  default void reconfigure (final Configuration<T> configuration)
  {
    throw new UnsupportedOperationException ();
  }
    
}
