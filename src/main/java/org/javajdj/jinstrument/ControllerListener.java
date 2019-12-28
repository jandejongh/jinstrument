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

import java.time.Instant;

/** A listener to (command completions of) a {@link Controller}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
@FunctionalInterface
public interface ControllerListener
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER CORE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification from a controller that is has finished processing (or gave up on) a specific command.
   * 
   * <p>
   * The result of processing the command, including failure indications, are present in the command argument,
   * but its precise structure is implementation dependent.
   * 
   * @param controller        The controller, non-{@code null}.
   * @param controllerCommand The command, non-{@code null}.
   * 
   */
  void processedCommandNotification (Controller controller, ControllerCommand controllerCommand);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER LOG
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of logging (debug) output from a controller.
   * 
   * @param controller The controller, non-{@code null}.
   * @param logEntry   The log entry, non-{@code null}.
   * 
   */
  default void controllerLogEntry (Controller controller, LogEntry logEntry) { }
  
  /** A log entry type.
   * 
   */
  enum LogEntryType
  {
    /** Command has been queued at a controller.
     * 
     */
    LOG_QUEUE_COMMAND,
    /** Command processing has started at a controller.
     * 
     */
    LOG_START_COMMAND,
    /** Command processing has ended at a controller.
     * 
     */
    LOG_END_COMMAND,
    /** Free-format logging / debug message from a controller.
     * 
     */
    LOG_MESSAGE,
    /** Notification of bytes sent from a controller object to the actual controller device.
     * 
     */
    LOG_TX,
    /** Notification of bytes received by a controller object from the actual controller device.
     * 
     */
    LOG_RX;  
  }
  
  /** A log entry.
   * 
   * @see ControllerListener#controllerLogEntry
   * 
   */
  class LogEntry
  {
    
    public LogEntry (
      final LogEntryType logEntryType,
      final Instant instant,
      final ControllerCommand controllerCommand,
      final String message,
      final byte[] bytes)
    {
      this.logEntryType = logEntryType;
      this.instant = instant;
      this.controllerCommand = controllerCommand;
      this.message = message;
      this.bytes = bytes;
    }
    
    public final LogEntryType logEntryType;
    public final Instant instant;
    public final ControllerCommand controllerCommand;
    public final String message;
    public final byte[] bytes;
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
