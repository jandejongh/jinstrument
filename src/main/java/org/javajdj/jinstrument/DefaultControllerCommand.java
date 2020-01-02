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

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

/** Default implementation of a {@link ControllerCommand}.
 * 
 * <p>
 * The main purpose of this class is to provide custom constructors with (some) additional error checking.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultControllerCommand
  extends LinkedHashMap<String, Object>
  implements ControllerCommand
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultControllerCommand.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultControllerCommand (
    final String command,
    final String arg1, final Object val1,
    final String arg2, final Object val2,
    final String arg3, final Object val3,
    final String arg4, final Object val4)
  {
    super ();
    if (command == null || command.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    if (arg1 != null && arg1.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    if (arg2 != null && arg2.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    if (arg3 != null && arg3.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    if (arg4 != null && arg4.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    put (ControllerCommand.CC_COMMAND_KEY, command);
    if (arg1 != null)
      put (arg1, val1);
    if (arg2 != null)
      put (arg2, val2);
    if (arg3 != null)
      put (arg3, val3);
    if (arg4 != null)
      put (arg4, val4);
  }
  
  public DefaultControllerCommand (
    final String command,
    final String arg1, final Object val1,
    final String arg2, final Object val2,
    final String arg3, final Object val3)
  {
    this (command, arg1, val1, arg2, val2, arg3, val3, null, null);
  }
  
  public DefaultControllerCommand (
    final String command,
    final String arg1, final Object val1,
    final String arg2, final Object val2)
  {
    this (command, arg1, val1, arg2, val2, null, null, null, null);
  }
  
  public DefaultControllerCommand (
    final String command,
    final String arg1, final Object val1)
  {
    this (command, arg1, val1, null, null, null, null, null, null);
  }
  
  public DefaultControllerCommand (final String command)
  {
    this (command, null, null, null, null, null, null, null, null);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER COMMAND SOJOURN ADMINISTRATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final void resetCommandSojournAdministration ()
  {
    remove (ControllerCommand.CCADMIN_CONTROLLER_ARRIVAL_TIME);
    remove (ControllerCommand.CCADMIN_CONTROLLER_START_TIME);
    remove (ControllerCommand.CCADMIN_CONTROLLER_DEPARTURE_TIME);
  }
  
  @Override
  public final Instant getArriveAtControllerTime ()
  {
    return (Instant) get (ControllerCommand.CCADMIN_CONTROLLER_ARRIVAL_TIME);
  }

  @Override
  public final void markArrivalAtController (final Controller controller)
  {
    if (getArriveAtControllerTime () != null)
      throw new IllegalStateException ();
    put (ControllerCommand.CCADMIN_CONTROLLER_ARRIVAL_TIME, Instant.now ());
  }

  @Override
  public final Instant getStartAtControllerTime ()
  {
    return (Instant) get (ControllerCommand.CCADMIN_CONTROLLER_START_TIME);
  }

  @Override
  public final void markStartAtController (final Controller controller)
  {
    if (getArriveAtControllerTime () == null || getStartAtControllerTime () != null)
      throw new IllegalStateException ();
    put (ControllerCommand.CCADMIN_CONTROLLER_START_TIME, Instant.now ());
  }

  @Override
  public final Instant getDepartureAtControllerTime ()
  {
    return (Instant) get (ControllerCommand.CCADMIN_CONTROLLER_DEPARTURE_TIME);
  }

  @Override
  public final void markDepartureAtController (final Controller controller)
  {
    if (getArriveAtControllerTime () == null || getDepartureAtControllerTime () != null)
      throw new IllegalStateException ();
    put (ControllerCommand.CCADMIN_CONTROLLER_DEPARTURE_TIME, Instant.now ());
  }

  @Override
  public final Duration getQueueingTimeout ()
  {
    return (Duration) get (ControllerCommand.CCADMIN_CONTROLLER_QUEUEING_TIMEOUT);    
  }

  @Override
  public void setQueueingTimeout (final Duration queueingTimeout)
  {
    if (queueingTimeout == null || queueingTimeout.isNegative () || queueingTimeout.isZero ())
      throw new IllegalArgumentException ();
    if (getArriveAtControllerTime () != null)
      throw new IllegalStateException ();
    put (ControllerCommand.CCADMIN_CONTROLLER_QUEUEING_TIMEOUT, queueingTimeout);
  }

  @Override
  public final Duration getProcessingTimeout ()
  {
    return (Duration) get (ControllerCommand.CCADMIN_CONTROLLER_PROCESSING_TIMEOUT);
  }

  @Override
  public void setProcessingTimeout (final Duration processingTimeout)
  {
    if (processingTimeout == null || processingTimeout.isNegative () || processingTimeout.isZero ())
      throw new IllegalArgumentException ();
    if (getStartAtControllerTime () != null)
      throw new IllegalStateException ();
    put (ControllerCommand.CCADMIN_CONTROLLER_PROCESSING_TIMEOUT, processingTimeout);
  }

  @Override
  public final Duration getSojournTimeout ()
  {
    return (Duration) get (ControllerCommand.CCADMIN_CONTROLLER_SOJOURN_TIMEOUT);
  }
  
  @Override
  public void setSojournTimeout (final Duration sojournTimeout)
  {
    if (sojournTimeout == null || sojournTimeout.isNegative () || sojournTimeout.isZero ())
      throw new IllegalArgumentException ();
    if (getArriveAtControllerTime ()!= null)
      throw new IllegalStateException ();
    put (ControllerCommand.CCADMIN_CONTROLLER_SOJOURN_TIMEOUT, sojournTimeout);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
