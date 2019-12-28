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

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.support.Service_FromMix;
import org.javajdj.jservice.support.Service_FromRunnable;

/** Abstract base implementation of {@link Controller}.
 * 
 * <p>
 * The base implementation provides listener management,
 * a thread-safe command queue,
 * and a command processor as a sub-service.
 * Actual processing of individual commands is left to sub-class implementations through abstract methods.
 * In addition, this class features a command-result queue with its own dispatcher sub-service.
 * This protects the main command processor from misbehaving listeners.
 * Finally, an independent <i>logging</i> service allows for debugging controller and/or instrument implementations.
 * 
 * <p>
 * Both the command processor and result dispatcher are extremely tolerant to exceptions thrown in sub-classes or listeners,
 * respectively, and merely rely on logging output to indicate potential problems.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public abstract class AbstractController
extends Service_FromMix
implements Controller
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (AbstractController.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public AbstractController (final String name, final List<Runnable> runnables, final List<Service> targetServices)
  {
    super (name, runnables, targetServices);
    addRunnable (this.commandProcessor);
    addRunnable (this.commandResultDispatcher);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<ControllerListener> controllerListeners = new LinkedHashSet<> ();
  
  private final Object controllerListenersLock = new Object ();
  
  private volatile Set<ControllerListener> controllerListenersCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addControllerListener (final ControllerListener l)
  {
    synchronized (this.controllerListenersLock)
    {
      if (l != null && ! this.controllerListeners.contains (l))
      {
        this.controllerListeners.add (l);
        this.controllerListenersCopy = new LinkedHashSet<> (this.controllerListeners);
      }
    }
  }
  
  @Override
  public final void removeControllerListener (final ControllerListener l)
  {
    synchronized (this.controllerListenersLock)
    {
      if (this.controllerListeners.remove (l))
        this.controllerListenersCopy = new LinkedHashSet<> (this.controllerListeners);
    }
  }
  
  protected final void fireProcessedCommand (final ControllerCommand controllerCommand)
  {
    // References are atomic.
    final Set<ControllerListener> listeners = this.controllerListenersCopy;
    for (final ControllerListener l : listeners)
      l.processedCommandNotification (this, controllerCommand);
  }
  
  protected final void fireControllerLogEntry (final ControllerListener.LogEntry logEntry)
  {
    // References are atomic.
    final Set<ControllerListener> listeners = this.controllerListenersCopy;
    for (final ControllerListener l : listeners)
      l.controllerLogEntry (this, logEntry);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COMMAND QUEUE
  // PROCESS COMMAND [ABSTRACT]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final BlockingQueue<ControllerCommand> commandQueue = new LinkedBlockingQueue<> ();
  
  @Override
  public final void addCommand (final ControllerCommand controllerCommand)
  {
    if (controllerCommand == null)
      throw new IllegalArgumentException ();
    if (! this.commandQueue.offer (controllerCommand))
      LOG.log (Level.WARNING, "Overflow on Controller Command Queue on {0}.", this);
    queueLogQueueCommand (Instant.now (), controllerCommand);
  }
  
  protected abstract void processCommand (final ControllerCommand controllerCommand, final Integer timeout_ms)
    throws UnsupportedOperationException, IOException, InterruptedException, TimeoutException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COMMAND PROCESSOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static int DEFAULT_COMMAND_TIMEOUT_MS = 3000;
  
  private final Runnable commandProcessor = () ->
  {
    LOG.log (Level.INFO, "Starting Controller Command Processor on {0}.", AbstractController.this.toString ());
    while (! Thread.currentThread ().isInterrupted ())
    {
      boolean error = false;
      boolean mustStop = false;
      ControllerCommand nextCommand = null;
      try
      {
        nextCommand = AbstractController.this.commandQueue.take ();
        queueLogStartCommand (Instant.now (), nextCommand);
        AbstractController.this.processCommand (nextCommand, DEFAULT_COMMAND_TIMEOUT_MS);
        queueLogEndCommand (Instant.now (), nextCommand);
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) Controller Command Processor on {0}.",
          AbstractController.this.toString ());
        if (nextCommand != null)
        {
          nextCommand.put (ControllerCommand.CC_EXCEPTION_KEY, ie);
          queueLogEndCommand (Instant.now (), nextCommand);
        }
        error = false;
        mustStop = true;
      }
      catch (TimeoutException te)
      {
        LOG.log (Level.WARNING, "TimeoutException (ignored; noted on command) in Controller Command Processor on {0}: {1}.",
          new Object[]{AbstractController.this.toString (), Arrays.toString (te.getStackTrace ())});
        if (nextCommand != null)
        {
          nextCommand.put (ControllerCommand.CC_EXCEPTION_KEY, te);
          queueLogEndCommand (Instant.now (), nextCommand);
        }
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "IOException (ignored; noted on command) in Controller Command Processor on {0}: {1}.",
          new Object[]{AbstractController.this.toString (), Arrays.toString (ioe.getStackTrace ())});
        if (nextCommand != null)
        {
          nextCommand.put (ControllerCommand.CC_EXCEPTION_KEY, ioe);
          queueLogEndCommand (Instant.now (), nextCommand);
        }
      }
      catch (UnsupportedOperationException uoe)
      {
        LOG.log (Level.WARNING, "UnsupportedOoperationException (ignored; noted on command) "
          + "in Controller Command Processor on {0}: {1}.",
          new Object[]{AbstractController.this.toString (), Arrays.toString (uoe.getStackTrace ())});
        if (nextCommand != null)
        {
          nextCommand.put (ControllerCommand.CC_EXCEPTION_KEY, uoe);
          queueLogEndCommand (Instant.now (), nextCommand);
        }
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception (ignored; noted on command) in Controller Command Processor on {0}: {1}.",
          new Object[]{AbstractController.this.toString (), Arrays.toString (e.getStackTrace ())});
        if (nextCommand != null)
        {
          nextCommand.put (ControllerCommand.CC_EXCEPTION_KEY, e);
          queueLogEndCommand (Instant.now (), nextCommand);
        }
      }
      finally
      {
        if (nextCommand != null && ! this.commandResultQueue.offer (nextCommand))
        {
          LOG.log (Level.WARNING, "Overflow in Command Result Queue on {0}.", AbstractController.this);
          error = true;
          mustStop = true;
        }
      }
      if (mustStop)
      {
        if (error)
          error ();
        return;
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) Controller Command Processor on {0}.",
      AbstractController.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COMMAND RESULT QUEUE
  // COMMAND RESULT DISPATCHER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final BlockingQueue<ControllerCommand> commandResultQueue = new LinkedBlockingQueue<> ();
  
  private final Runnable commandResultDispatcher = () ->
  {
    LOG.log (Level.INFO, "Starting Controller Command Result Dispatcher on {0}.", AbstractController.this.toString ());
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        final ControllerCommand nextCommand = this.commandResultQueue.take ();
        fireProcessedCommand (nextCommand);
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) Controller Command Result Dispatcher on {0}.",
          AbstractController.this.toString ());
        return;
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception (ignored) in Controller Command Result Dispatcher on {0}: {1}.",
          new Object[]{AbstractController.this.toString (), Arrays.toString (e.getStackTrace ())});
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) Controller Command Result Dispatcher on {0}.",
      AbstractController.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER LOGGING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final BlockingQueue<ControllerListener.LogEntry> controllerLogQueue = new LinkedBlockingQueue<> ();
  
  protected final void queueLogQueueCommand (final Instant instant, final ControllerCommand controllerCommand)
  {
    if (this.loggingService.getStatus () != Status.ACTIVE)
      return;
    if (! this.controllerLogQueue.offer (new ControllerListener.LogEntry (
      ControllerListener.LogEntryType.LOG_QUEUE_COMMAND,
      instant,
      controllerCommand,
      null,
      null)))
      LOG.log (Level.WARNING, "Overflow on controller-log buffer on {0}.", this);
  }
  
  protected final void queueLogStartCommand (final Instant instant, final ControllerCommand controllerCommand)
  {
    if (this.loggingService.getStatus () != Status.ACTIVE)
      return;
    if (! this.controllerLogQueue.offer (new ControllerListener.LogEntry (
      ControllerListener.LogEntryType.LOG_START_COMMAND,
      instant,
      controllerCommand,
      null,
      null)))
      LOG.log (Level.WARNING, "Overflow on controller-log buffer on {0}.", this);
  }
  
  protected final void queueLogEndCommand (final Instant instant, final ControllerCommand controllerCommand)
  {
    if (this.loggingService.getStatus () != Status.ACTIVE)
      return;
    if (! this.controllerLogQueue.offer (new ControllerListener.LogEntry (
      ControllerListener.LogEntryType.LOG_END_COMMAND,
      instant,
      controllerCommand,
      null,
      null)))
      LOG.log (Level.WARNING, "Overflow on controller-log buffer on {0}.", this);
  }
  
  protected final void queueLogMessage (final Instant instant, final String message)
  {
    if (this.loggingService.getStatus () != Status.ACTIVE)
      return;
    if (! this.controllerLogQueue.offer (new ControllerListener.LogEntry (
      ControllerListener.LogEntryType.LOG_MESSAGE,
      instant,
      null,
      message,
      null)))
      LOG.log (Level.WARNING, "Overflow on controller-log buffer on {0}.", this);
  }
  
  protected final void queueLogTx (final Instant instant, final byte[] bytes)
  {
    if (this.loggingService.getStatus () != Status.ACTIVE)
      return;
    if (! this.controllerLogQueue.offer (new ControllerListener.LogEntry (
      ControllerListener.LogEntryType.LOG_TX,
      instant,
      null,
      null,
      bytes)))
      LOG.log (Level.WARNING, "Overflow on controller-log buffer on {0}.", this);
  }
  
  protected final void queueLogRx (final Instant instant, final byte[] bytes)
  {
    if (this.loggingService.getStatus () != Status.ACTIVE)
      return;
    if (! this.controllerLogQueue.offer (new ControllerListener.LogEntry (
      ControllerListener.LogEntryType.LOG_RX,
      instant,
      null,
      null,
      bytes)))
    {
      LOG.log (Level.WARNING, "Overflow on Controller Log Queue on {0}.", this);
    }
  }
  
  private final Runnable logEntryDispatcher = () ->
  {
    LOG.log (Level.INFO, "Starting Controller Log Dispatcher on {0}.", AbstractController.this.toString ());
    this.controllerLogQueue.clear ();
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        final ControllerListener.LogEntry nextLogEntry = this.controllerLogQueue.take ();
        fireControllerLogEntry (nextLogEntry);
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) Controller Log Dispatcher on {0}.",
          AbstractController.this.toString ());
        this.controllerLogQueue.clear ();
        return;
      }
      // Prevent termination of the Thread due to a misbehaving listener.
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception (ignored) in Controller Log Dispatcher on {0}: {1}.",
          new Object[]{AbstractController.this.toString (), Arrays.toString (e.getStackTrace ())});
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) Controller Log Dispatcher on {0}.",
      AbstractController.this.toString ());
    this.controllerLogQueue.clear ();
  };
  
  private final Service loggingService = new Service_FromRunnable (this.logEntryDispatcher);
  
  @Override
  public final Service getLoggingService ()
  {
    return this.loggingService;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
