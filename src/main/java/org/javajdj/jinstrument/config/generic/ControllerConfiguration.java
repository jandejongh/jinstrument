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
package org.javajdj.jinstrument.config.generic;

import java.util.logging.Logger;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.ControllerType;
import org.javajdj.jinstrument.config.Configuration;

/** Implementation of {@link Configuration} for a {@link Controller}.
 *
 * @param <C> The (more) concrete {@link Controller} type.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class ControllerConfiguration<C extends Controller>
  implements Configuration<C>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (ControllerConfiguration.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates the object.
   * 
   * @param controllerType          The controller type.
   * @param controllerConfiguration The controller configuration specific to the controller type.
   * 
   */
  public ControllerConfiguration (
    final ControllerType controllerType,
    final Configuration<? extends Controller> controllerConfiguration)
  {
    this.controllerType = controllerType;
    this.controllerConfiguration = controllerConfiguration;
  }
  
  /** Creates the object with {@code null} controller type and configuration.
   * 
   */
  public ControllerConfiguration ()
  {
    this.controllerType = null;
    this.controllerConfiguration = null;
  }
  
  /** Creates a copy of this object with given controller type.
   * 
   * @param controllerType The controller type.
   * 
   * @return The new object.
   * 
   */
  public final ControllerConfiguration withControllerType (final ControllerType controllerType)
  {
    return new ControllerConfiguration (controllerType, this.controllerConfiguration);
  }
  
  /** Creates a copy of this object with given controller configuration.
   * 
   * @param controllerConfiguration  The controller configuration.
   * 
   * @return The new object.
   * 
   */
  public final ControllerConfiguration withControllerConfiguration (
    final Configuration<? extends Controller> controllerConfiguration)
  {
    return new ControllerConfiguration (this.controllerType, controllerConfiguration);
  }

  @Override
  public final Class<? super C> getConfiguredClass ()
  {
    return Controller.class;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private ControllerType controllerType;

  /** Returns the controller type.
   * 
   * @return The controller type.
   * 
   */
  public final ControllerType getControllerType ()
  {
    return this.controllerType;
  }

  /** Sets the controller type.
   * 
   * @param controllerType The new controller type.
   * 
   */
  public final void setControllerType (final ControllerType controllerType)
  {
    this.controllerType = controllerType;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER CONFIGURATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private Configuration<? extends Controller> controllerConfiguration;

  /** Returns the controller configuration.
   * 
   * @return The controller configuration.
   * 
   */
  public final Configuration<? extends Controller> getControllerConfiguration ()
  {
    return this.controllerConfiguration;
  }

  /** Sets the controller configuration.
   * 
   * @param controllerConfiguration  The new controller configuration.
   * 
   */
  public final void setControllerConfiguration (final Configuration<? extends Controller> controllerConfiguration)
  {
    this.controllerConfiguration = controllerConfiguration;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
