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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.javajdj.jinstrument.InstrumentRegistry;

/** An object capable of generating {@link Configurator} instances of a specific type for a specific class.
 *
 * @param <T> The type of objects for which the configuration applies.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see Configurable
 * @see Configurator
 * 
 */
public interface ConfiguratorType<T>
{
  
  /** Returns the (run-time) class of configured objects.
   * 
   * @return The (run-time) class of configured objects.
   * 
   */
  Class<T> getConfiguredObjectClass ();
  
  /** Creates a new {@link Configurator}.
   * 
   * @param instrumentRegistry The instrument registry.
   * @param configuratorTypeDB The configurator-type DB.
   * 
   * @return The new {@link Configurator}.
   * 
   */
  Configurator<T> createConfigurator (InstrumentRegistry instrumentRegistry, ConfiguratorTypeDB configuratorTypeDB);
  
  /** Pops up a modal dialog for obtaining a {@link Configuration}.
   * 
   * <p>
   * Implementations must alert the user of erroneous values entered.
   * 
   * @param instrumentRegistry The instrument registry.
   * @param configuratorTypeDB The configurator-type DB.
   * @param dialogTitle        The dialog title.
   * 
   * @return The {@link Configuration} obtained through user input; {@code null} if the user cancelled or
   *           entered erroneous information.
   * 
   */
  default Configuration<T> doModalDialogForConfiguration (
    final InstrumentRegistry instrumentRegistry,
    final ConfiguratorTypeDB configuratorTypeDB,
    final String dialogTitle)
  {
    final Configurator<T> configurator = createConfigurator (instrumentRegistry, configuratorTypeDB);
    if (configurator == null
      || ! (configurator instanceof JPanel))
    {
      JOptionPane.showMessageDialog (null, "Unsupported; please report!", "Error", JOptionPane.ERROR_MESSAGE);
      return null;
    }
    if (JOptionPane.showConfirmDialog (
      null,
      (JPanel) configurator,
      dialogTitle,
      JOptionPane.OK_CANCEL_OPTION,
      JOptionPane.PLAIN_MESSAGE)
      != JOptionPane.OK_OPTION)
      return null;
    final Configuration<T> configuration = configurator.getConfiguration ();
    if (configuration == null)
    {
      final String errorMessage = configurator.getErrorMessage ();
      JOptionPane.showMessageDialog (
        null,
        errorMessage != null ? errorMessage : "Unknown/unspecified; please report!",
        "Error",
        JOptionPane.ERROR_MESSAGE);
    }
    return configuration;
  }
  
  /** Creates a new object from a {@link Configuration}.
   * 
   * <p>
   * Implementations must alert the user of erroneous configurations supplied (or other errors).
   * 
   * @param instrumentRegistry The instrument registry.
   * @param configuratorTypeDB The configurator-type DB.
   * @param configuration      The configuration, if {@code null}, a default configured object is returned.
   * 
   * @return The newly created object; {@code null} if creation failed.
   * 
   */
  default T createFromConfiguration (
    final InstrumentRegistry instrumentRegistry,
    final ConfiguratorTypeDB configuratorTypeDB,
    final Configuration<T> configuration)
  {
    final Configurator<T> configurator = createConfigurator (instrumentRegistry, configuratorTypeDB);
    if (configurator == null)
    {
      JOptionPane.showMessageDialog (null, "No configurator; please report!", "Error", JOptionPane.ERROR_MESSAGE);
      return null;
    }
    final T newObject = configurator.createObject (configuration);
    if (newObject == null)
    {
      final String errorMessage = configurator.getErrorMessage ();
      JOptionPane.showMessageDialog (
        null,
        errorMessage != null ? errorMessage : "Unknown/unspecified; please report!",
        "Error",
        JOptionPane.ERROR_MESSAGE);
    }
    return newObject;
  }
  
  /** Creates a new object through a modal dialog.
   * 
   * <p>
   * Implementations must alert the user of erroneous values entered.
   * 
   * @param instrumentRegistry The instrument registry.
   * @param configuratorTypeDB The configurator-type DB.
   * @param dialogTitle        The dialog title.
   * 
   * @return The newly created object; {@code null} if the user cancelled or
   *           entered erroneous information, or object creation failed from some other reason.
   * 
   * @see #doModalDialogForConfiguration
   * @see #createFromConfiguration
   * 
   */
  default T doModalDialogForNewObject (
    final InstrumentRegistry instrumentRegistry,
    final ConfiguratorTypeDB configuratorTypeDB,
    final String dialogTitle)
  {
    final Configuration<T> configuration = doModalDialogForConfiguration (instrumentRegistry, configuratorTypeDB, dialogTitle);
    return configuration != null ? createFromConfiguration (instrumentRegistry, configuratorTypeDB, configuration) : null;
  }
  
}
