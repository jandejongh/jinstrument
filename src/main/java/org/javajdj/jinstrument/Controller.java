/* 
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>.
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

import org.javajdj.jservice.Service;

/** Representation of a software or hardware entity communicating with {@link Device}s over one or more {@link Bus}es.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface Controller
  extends Service
{
  
  void addControllerListener (ControllerListener controllerListener);
  
  void removeControllerListener (ControllerListener controllerListener);
  
  String getControllerUrl ();
  
  BusType getBusType ();
  
  Bus[] getBuses ();
  
  void addCommand (final ControllerCommand controllerCommand);
  
  default Service getLoggingService () { return null; }

}
