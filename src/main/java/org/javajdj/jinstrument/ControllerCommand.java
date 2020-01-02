/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>, TNO.
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

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/** A command for a {@link Controller}.
 * 
 * <p>
 * Controller commands are {@code Map}s from {@link String}s to {@link Object}s.
 * The interface provides {@code String} definitions for the most common commands and
 * their arguments.
 * 
 * <p>
 * A controller command features a set of closely related methods dealing with the administration of the sojourn
 * of the command at a specific controller, and the applicable timeout values.
 * Implementations must store the related values under given keys.
 * 
 * @see Controller#addCommand
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface ControllerCommand
  extends Map<String, Object>
{
  
  public final static String CC_COMMAND_KEY = "command";
  
  public final static String CCRET_EXCEPTION_KEY = "commandReturnException";
  public final static String CCRET_STATUS_KEY = "commandReturnStatus";
  public final static String CCRET_VALUE_KEY = "commandReturnValue";
  
  public final static String CCCMD_RESET_CONTROLLER = "commandResetController";
  public final static String CCCMD_NOP = "commandNop";
  public final static String CCCMD_PROPE = "commandProbe";
  public final static String CCCMD_GET_SETTINGS = "commandGetSettings";
  
  public final static String CCADMIN_CONTROLLER_ARRIVAL_TIME = "controllerAdminArrivalTime";
  public final static String CCADMIN_CONTROLLER_START_TIME = "controllerAdminStartTime";
  public final static String CCADMIN_CONTROLLER_DEPARTURE_TIME = "controllerAdminDepartureTime";

  public final static String CCADMIN_CONTROLLER_QUEUEING_TIMEOUT = "controllerAdminQueueingTimeout";
  public final static String CCADMIN_CONTROLLER_PROCESSING_TIMEOUT = "controllerAdminProcessingTimeout";
  public final static String CCADMIN_CONTROLLER_SOJOURN_TIMEOUT = "controllerAdminSojournTimeout";
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER COMMAND SOJOURN ADMINISTRATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  void resetCommandSojournAdministration ();
  
  Instant getArriveAtControllerTime ();

  void markArrivalAtController (Controller controller);
  
  Instant getStartAtControllerTime ();

  void markStartAtController (Controller controller);
  
  Instant getDepartureAtControllerTime ();
  
  void markDepartureAtController (Controller controller);
  
  Duration getQueueingTimeout ();
  
  void setQueueingTimeout (Duration queueingTimeout);
  
  Duration getProcessingTimeout ();
  
  void setProcessingTimeout (Duration processingTimeout);
  
  Duration getSojournTimeout ();
  
  void setSojournTimeout (Duration sojournTimeout);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
