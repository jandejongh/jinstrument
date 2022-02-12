/* 
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument;

/** Object capable of generating {@link Controller} objects of some specific type from a URL description.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface ControllerType
{
  
  /** Returns the specific {@link Controller} type objects created by this object.
   * 
   * @param <T> The (generic) type of objects created.
   * 
   * @return The specific {@link Controller} type objects created by this object.
   * 
   * @see #openController
   * 
   */
  <T extends Controller> Class<T> getControllerClass ();
  
  /** Return the URL of this controller type.
   * 
   * <p>
   * The URL of a <i>controller type</i> is a simple non-empty {@code String},
   * but for interoperability with the {@link InstrumentRegistry} framework,
   * it should consist of <i>only</i> alphanumeric characters, dashes and underscores.
   * Also, distinct controller types should have distinct URLs.
   * 
   * @return The URL of this controller type.
   * 
   */
  String getControllerTypeUrl ();
  
  /** Return the {@link BusType} of this controller type.
   * 
   * <p>
   * Note that controllers with multiple buses <i>are</i> supported,
   * but they all have to be of the same bus type.
   * 
   * @return The URL of this controller type.
   * 
   */
  BusType getBusType ();
  
  /** Opens (creates) a new {@link Controller} from a relative URL.
   * 
   * <p>
   * The structure of the relative controller URL depends on the actual controller type.
   * Typically, it contains configuration parameters for controllers of type.
   * 
   * @param relativeControllerUrl The relative URL of the controller.
   * 
   * @return The new controller, {@code null} if creation failed.
   * 
   */
  Controller openController (final String relativeControllerUrl);
  
}
