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

import javax.swing.JPanel;

/** An object capable of configuring (to be constructed) objects of some specific type. 
 *
 * <p>
 * It is highly recommended (but not necessary) that {@link Configurator} objects are instances of {@link JPanel}.
 * 
 * <p>
 * Implementation may assume single-threaded operation.
 * 
 * @param <T> The type of objects to be configured.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface Configurator<T>
{
  
  /** Returns the (run-time-){@code class} to be configured.
   * 
   * @return The run-time {@code class} to be configured.
   * 
   */
  Class<T> getConfiguredObjectClass ();
  
  /** Returns the current configuration (state) of this configurator (as entered by the user).
   * 
   * <p>
   * Errors are available afterwards in {@link #getErrorMessage}.
   * 
   * @return The current configuration (state) of this configurator, {@code null} if invalid.
   * 
   * @see #getErrorMessage
   * 
   */
  Configuration<T> getConfiguration ();
  
  /** Returns, after checking, a new configuration.
   * 
   * <p>
   * It to the courtesy of the implementation to update the configurator view
   * according to the new configuration applied.
   * 
   * <p>
   * Errors are available afterwards in {@link #getErrorMessage}.
   * 
   * @param configuration The new configuration.
   * 
   * @return The new configuration, {@code null} if the configuration supplied is invalid.
   * 
   */
  Configuration<T> withConfiguration (Configuration<T> configuration);
  
  /** Creates a new object with given configuration.
   * 
   * <p>
   * It to the courtesy of the implementation to update the configurator view
   * according to the new configuration applied.
   * 
   * <p>
   * Errors are available afterwards in {@link #getErrorMessage}.
   * 
   * @param configuration The configuration for the new object.
   * 
   * @return The new object, {@code null} if the configuration supplied is invalid.
   * 
   */
  T createObject (Configuration<T> configuration);
  
  /** Returns the error message from the latest failed operation.
   * 
   * @return The error message from the latest failed operation.
   * 
   * @see #getConfiguration
   * @see #withConfiguration
   * @see #createObject
   * 
   */
  String getErrorMessage ();
  
}
