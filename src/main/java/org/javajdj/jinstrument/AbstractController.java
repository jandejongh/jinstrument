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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.support.Service_FromMix;

/** Abstract base implementation of {@link Controller}.
 * 
 * <p>
 * The base implementation provides listener management,
 * a thread-safe command queue,
 * and a command processor as a sub-service.
 * Actual processing of individual commands is left to sub-class implementations through abstract methods.
 * In addition, this class features a command-result queue with its own dispatcher sub-service.
 * This protects the main command processor from misbehaving listeners.
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
      LOG.log (Level.WARNING, "Overflow on command buffer on {0}.", this);
  }
  
  protected abstract void processCommand (final ControllerCommand controllerCommand)
    throws UnsupportedOperationException, IOException, InterruptedException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COMMAND PROCESSOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Runnable commandProcessor = () ->
  {
    while (! Thread.currentThread ().isInterrupted ())
    {
      boolean mustStop = false;
      ControllerCommand nextCommand = null;
      try
      {
        nextCommand = AbstractController.this.commandQueue.take ();
        AbstractController.this.processCommand (nextCommand);
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "InterruptedException while processing command: {0}.", ie.getStackTrace ());
        if (nextCommand != null)
          nextCommand.put (ControllerCommand.CC_EXCEPTION_KEY, ie);
        mustStop = true;
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "IOException while processing command: {0}.", ioe.getStackTrace ());
        if (nextCommand != null)
          nextCommand.put (ControllerCommand.CC_EXCEPTION_KEY, ioe);
      }
      catch (UnsupportedOperationException uoe)
      {
        LOG.log (Level.WARNING, "UnsupportedOperationException while processing command: {0}.", uoe.getStackTrace ());
        if (nextCommand != null)
          nextCommand.put (ControllerCommand.CC_EXCEPTION_KEY, uoe);
      }
      // Prevent termination of the Thread due to a misbehaving implementation of processCommand.
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception while processing command: {0}.", e.getStackTrace ());
        if (nextCommand != null)
          nextCommand.put (ControllerCommand.CC_EXCEPTION_KEY, e);
      }
      finally
      {
        if (nextCommand != null && ! this.commandResultQueue.offer (nextCommand))
        {
          LOG.log (Level.WARNING, "Overflow on command-result buffer on {0}.", this);
          mustStop = true;
        }
      }
      if (mustStop)
      {
        error ();
        return;
      }
    }
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
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        final ControllerCommand nextCommand = this.commandResultQueue.take ();
        fireProcessedCommand (nextCommand);
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "InterruptedException while dispatching controller command result: {0}.", ie.getStackTrace ());
        return;
      }
      // Prevent termination of the Thread due to a misbehaving listener.
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception while dispatching controller command result: {0}.", e.getStackTrace ());
      }
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
