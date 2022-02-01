/* 
 * Copyright 2010-2021 Jan de Jongh <jfcmdejongh@gmail.com>.
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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jservice.support.RunnableInvoker;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.support.Service_FromMix;

/** Abstract base implementation of {@link Instrument}.
 * 
 * <p>
 * The base class provides various sub-services like for obtaining instrument settings and status and for data acquisition.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public abstract class AbstractInstrument
extends Service_FromMix
implements Instrument
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (AbstractInstrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected AbstractInstrument (
    final String name,
    final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices,
    final boolean addInitializationServices,
    final boolean addStatusServices,
    final boolean addSettingsServices,
    final boolean addCommandProcessorServices,
    final boolean addAcquisitionServices,
    final boolean addHousekeepingServices)
  {
    super (name, runnables, targetServices);
    if (device == null)
      throw new IllegalArgumentException ();
    this.device = device;
    if (addInitializationServices)
    {
      addRunnable (this.instrumentInitializer);
    }
    if (addStatusServices)
    {
      addRunnable (this.instrumentStatusCollector);
    }
    addRunnable (this.instrumentStatusDispatcher);      
    if (addSettingsServices)
    {
      addRunnable (this.instrumentSettingsCollector);
    }
    addRunnable (this.instrumentSettingsDispatcher);
    if (addCommandProcessorServices)
    {
      addRunnable (this.instrumentCommandProcessor);      
    }
    if (addAcquisitionServices)
    {
      addRunnable (this.instrumentReadingCollector);
    }
    addRunnable (this.instrumentReadingDispatcher);
    if (addHousekeepingServices)
    {
      addRunnable (this.instrumentHousekeeper);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Device device;

  @Override
  public Device getDevice ()
  {
    return this.device;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<InstrumentListener> instrumentListeners = new LinkedHashSet<> ();
  
  private final Object instrumentListenersLock = new Object ();
  
  private volatile Set<InstrumentListener> instrumentListenersCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addInstrumentListener (final InstrumentListener l)
  {
    synchronized (this.instrumentListenersLock)
    {
      if (l != null && ! this.instrumentListeners.contains (l))
      {
        this.instrumentListeners.add (l);
        this.instrumentListenersCopy = new LinkedHashSet<> (this.instrumentListeners);
      }
    }
  }
  
  @Override
  public final void removeInstrumentListener (final InstrumentListener l)
  {
    synchronized (this.instrumentListenersLock)
    {
      if (this.instrumentListeners.remove (l))
        this.instrumentListenersCopy = new LinkedHashSet<> (this.instrumentListeners);
    }
  }
  
  protected final void fireInstrumentStatusChanged (final InstrumentStatus instrumentStatus)
  {
    // References are atomic.
    final Set<InstrumentListener> listeners = this.instrumentListenersCopy;
    for (final InstrumentListener l : listeners)
      l.newInstrumentStatus (this, instrumentStatus);
  }
  
  protected final void fireInstrumentSettingsChanged (final InstrumentSettings instrumentSettings)
  {
    // References are atomic.
    final Set<InstrumentListener> listeners = this.instrumentListenersCopy;
    for (final InstrumentListener l : listeners)
      l.newInstrumentSettings (this, instrumentSettings);
  }
  
  protected final void fireInstrumentReading (final InstrumentReading instrumentReading)
  {
    // References are atomic.
    final Set<InstrumentListener> listeners = this.instrumentListenersCopy;
    for (final InstrumentListener l : listeners)
      l.newInstrumentReading (this, instrumentReading);
  }
  
  protected final void fireInstrumentDebug (
    final Instrument instrument,
    final int debugId,
    final InstrumentStatus instrumentStatus,
    final InstrumentSettings instrumentSettings,
    final InstrumentReading instrumentReading,
    final Object debugObject1,
    final Object debugObject2,
    final Object debugObject3)
  {
    // References are atomic.
    final Set<InstrumentListener> listeners = this.instrumentListenersCopy;
    for (final InstrumentListener l : listeners)
      l.newInstrumentDebug (
        instrument,
        debugId,
        instrumentStatus,
        instrumentSettings,
        instrumentReading,
        debugObject1,
        debugObject2,
        debugObject3);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT ID
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String INSTRUMENT_ID_PROPERTY_NAME = "instrumentId";
  
  private volatile String instrumentId = "unknown";

  private final Object instrumentIdLock = new Object ();
  
  @Override
  public final String getInstrumentId ()
  {
    return this.instrumentId;
  }
  
  protected final void setInstrumentId (final String instrumentId)
  {
    if (instrumentId == null)
      throw new IllegalArgumentException ();
    synchronized (this.instrumentIdLock)
    {
     if (! instrumentId.equals (this.instrumentId))
     {
        final String oldInstrumentId = this.instrumentId;
        this.instrumentId = instrumentId;
        fireSettingsChanged (INSTRUMENT_ID_PROPERTY_NAME,
          oldInstrumentId,
          this.instrumentId);
      }
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT INITIALIZATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract void initializeInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException;
  
  private final Runnable instrumentInitializer = RunnableInvoker.onceFromRunnable ("Instrument Initializer",
    this,
    this::initializeInstrumentSync,
    null,
    null,
    true,
    b -> { if (b) error (); },
    Level.INFO,
    Level.WARNING);

  public final static String INSTRUMENT_INITIALIZER_TIMEOUT_MS_PROPERTY_NAME = "instrumentInitializerTimeout_ms";
  
  public final static long DEFAULT_INSTRUMENT_INITIALIZER_TIMEOUT_MS = 1000L;
  
  // XXX Need locking or AtomicLong here...
  private volatile long instrumentInitializerTimeout_ms = DEFAULT_INSTRUMENT_INITIALIZER_TIMEOUT_MS;
  
  public final long getInstrumentInitializerTimeout_ms ()
  {
    return this.instrumentInitializerTimeout_ms;
  }
  
  public final void setInstrumentInitializerTimeout_ms (final long instrumentInitializerTimeout_ms)
  {
    if (instrumentInitializerTimeout_ms <= 0)
      throw new IllegalArgumentException ();
    if (instrumentInitializerTimeout_ms != this.instrumentInitializerTimeout_ms)
    {
      final long oldInstrumentModeSetterTimeout_ms = this.instrumentInitializerTimeout_ms;
      this.instrumentInitializerTimeout_ms = instrumentInitializerTimeout_ms;
      fireSettingsChanged (INSTRUMENT_INITIALIZER_TIMEOUT_MS_PROPERTY_NAME,
        oldInstrumentModeSetterTimeout_ms,
        this.instrumentInitializerTimeout_ms);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT STATUS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract InstrumentStatus getStatusFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException;
    
  protected abstract void requestStatusFromInstrumentASync ()
    throws IOException;
  
  private final Runnable instrumentStatusCollector = RunnableInvoker.periodicallyFromSupplierConsumerChain (
    "Instrument Status Collector",
    this,
    this::getStatusCollectorPeriod_s,
    true,
    RunnableInvoker.OverloadPolicy.IGNORE_AND_DROP,
    this::getStatusFromInstrumentSync,
    this::statusReadFromInstrument,
    new LinkedHashSet<> (Arrays.<Class<? extends Exception>>asList (TimeoutException.class)),
    null,
    true,
    b -> { if (b) error (); },
    Level.INFO,
    Level.WARNING,
    Level.WARNING);
  
  public final static String CURRENT_INSTRUMENT_STATUS_PROPERTY_NAME = "currentInstrumentStatus";
  
  private volatile InstrumentStatus currentInstrumentStatus = null;
  
  private final Object currentInstrumentStatusLock = new Object ();
  
  @Override
  public final InstrumentStatus getCurrentInstrumentStatus ()
  {
    synchronized (this.currentInstrumentStatusLock)
    {
      return this.currentInstrumentStatus;
    }
  }
  
  public final static String STATUS_COLLECTOR_PERIOD_S_PROPERTY_NAME = "statusCollectorPeriod_s";
  
  public final static double DEFAULT_STATUS_COLLECTOR_PERIOD_S = 5;
  
  private volatile double statusCollectorPeriod_s = AbstractInstrument.DEFAULT_STATUS_COLLECTOR_PERIOD_S;

  private final Object statusCollectorPeriodLock = new Object ();
  
  public final double getStatusCollectorPeriod_s ()
  {
    synchronized (this.statusCollectorPeriodLock)
    {
      return this.statusCollectorPeriod_s;
    }
  }
  
  public final void setStatusCollectorPeriod_s (final double statusCollectorPeriod_s)
  {
    if (statusCollectorPeriod_s < 0)
      throw new IllegalArgumentException ();
    final double oldStatusCollectorPeriod_s;
    synchronized (this.statusCollectorPeriodLock)
    {
      if (this.statusCollectorPeriod_s == statusCollectorPeriod_s)
        return;
      oldStatusCollectorPeriod_s = this.statusCollectorPeriod_s;
      this.statusCollectorPeriod_s = statusCollectorPeriod_s;
    }
    fireSettingsChanged (AbstractInstrument.STATUS_COLLECTOR_PERIOD_S_PROPERTY_NAME,
      oldStatusCollectorPeriod_s,
      statusCollectorPeriod_s);
  }
  
  private final BlockingQueue<InstrumentStatus> statusReadQueue = new LinkedBlockingQueue<> ();
  
  protected void statusReadFromInstrument (final InstrumentStatus instrumentStatus)
  {
    if (instrumentStatus == null)
      throw new IllegalArgumentException ();
    if (! this.statusReadQueue.offer (instrumentStatus))
      LOG.log (Level.WARNING, "Overflow on Instrument Status Queue on {0}.", this);
  }
  
  private InstrumentStatus takeStatus ()
    throws InterruptedException
  {
    return this.statusReadQueue.take ();
  }
  
  private void processStatus (final InstrumentStatus instrumentStatus)
  {
    synchronized (this.currentInstrumentStatusLock)
    {
      final InstrumentStatus oldStatus = this.currentInstrumentStatus;
      if (oldStatus != null && instrumentStatus.equals (oldStatus))
        return;
      this.currentInstrumentStatus = instrumentStatus;
    }
    fireInstrumentStatusChanged (instrumentStatus);
  }
  
  private final Runnable instrumentStatusDispatcher = RunnableInvoker.constantlyFromSupplierConsumerChain (
    "Instrument Status Dispatcher",
    this,
    this::takeStatus,
    this::processStatus,
    null,
    null,
    false,
    b -> { if (b) error (); },
    Level.INFO,
    Level.WARNING);
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract InstrumentSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException;
    
  protected abstract void requestSettingsFromInstrumentASync ()
    throws IOException;
  
  private final Runnable instrumentSettingsCollector = RunnableInvoker.periodicallyFromSupplierConsumerChain (
    "Instrument Settings Collector",
    this,
    this::getSettingsCollectorPeriod_s,
    true,
    RunnableInvoker.OverloadPolicy.IGNORE_AND_DROP,
    this::getSettingsFromInstrumentSync,
    this::settingsReadFromInstrument,
    new LinkedHashSet<> (Arrays.<Class<? extends Exception>>asList (TimeoutException.class, IOException.class)),
    null,
    true,
    b -> { if (b) error (); },
    Level.INFO,
    Level.WARNING,
    Level.WARNING);
  
  public final static String CURRENT_INSTRUMENT_SETTINGS_PROPERTY_NAME = "currentInstrumentSettings";
  
  private volatile InstrumentSettings currentInstrumentSettings = null;
  
  private final Object currentInstrumentSettingsLock = new Object ();
  
  @Override
  public final InstrumentSettings getCurrentInstrumentSettings ()
  {
    synchronized (this.currentInstrumentSettingsLock)
    {
      return this.currentInstrumentSettings;
    }
  }
  
  public final static String SETTINGS_COLLECTOR_PERIOD_S_PROPERTY_NAME = "settingsCollectorPeriod_s";
  
  public final static double DEFAULT_SETTINGS_COLLECTOR_PERIOD_S = 10;
  
  private volatile double settingsCollectorPeriod_s = AbstractInstrument.DEFAULT_SETTINGS_COLLECTOR_PERIOD_S;

  private final Object settingsCollectorPeriodLock = new Object ();
  
  public final double getSettingsCollectorPeriod_s ()
  {
    synchronized (this.settingsCollectorPeriodLock)
    {
      return this.settingsCollectorPeriod_s;
    }
  }
  
  public final void setSettingsCollectorPeriod_s (final double settingsCollectorPeriod_s)
  {
    if (settingsCollectorPeriod_s < 0)
      throw new IllegalArgumentException ();
    final double oldSettingsCollectorPeriod_s;
    synchronized (this.settingsCollectorPeriodLock)
    {
      if (this.settingsCollectorPeriod_s == settingsCollectorPeriod_s)
        return;
      oldSettingsCollectorPeriod_s = this.settingsCollectorPeriod_s;
      this.settingsCollectorPeriod_s = settingsCollectorPeriod_s;
    }
    fireSettingsChanged (
      AbstractInstrument.SETTINGS_COLLECTOR_PERIOD_S_PROPERTY_NAME,
      oldSettingsCollectorPeriod_s,
      settingsCollectorPeriod_s);
  }
  
  public final static String OPTIMIZE_SETTINGS_UPDATES_PROPERTY_NAME = "optimizeSettingsUpdates";
  
  public final static boolean DEFAULT_OPTIMIZE_SETTINGS_UPDATES = true;
  
  private volatile boolean optimizeSettingsUpdates = AbstractInstrument.DEFAULT_OPTIMIZE_SETTINGS_UPDATES;

  public final boolean isOptimizeSettingsUpdates ()
  {
    return this.optimizeSettingsUpdates;
  }
  
  protected final void setOptimizeSettingsUpdates (final boolean optimizeSettingsUpdates)
  {
    if (optimizeSettingsUpdates == this.optimizeSettingsUpdates)
      return;
    this.optimizeSettingsUpdates = optimizeSettingsUpdates;
    fireSettingsChanged (
      AbstractInstrument.OPTIMIZE_SETTINGS_UPDATES_PROPERTY_NAME,
      ! optimizeSettingsUpdates,
      optimizeSettingsUpdates);
  }
  
  protected final void setOptimizeSettingsUpdates ()
  {
    setOptimizeSettingsUpdates (true);
  }
  
  protected final void resetOptimizeSettingsUpdates ()
  {
    setOptimizeSettingsUpdates (false);
  }

  private final BlockingQueue<InstrumentSettings> settingsReadQueue = new LinkedBlockingQueue<> ();
  
  protected void settingsReadFromInstrument (final InstrumentSettings instrumentSettings)
  {
    if (instrumentSettings == null)
      throw new IllegalArgumentException ();
    if (! this.settingsReadQueue.offer (instrumentSettings))
      LOG.log (Level.WARNING, "Overflow on Instrument Settings Queue on {0}.", this);
  }
  
  private InstrumentSettings takeSettings ()
    throws InterruptedException
  {
    return this.settingsReadQueue.take ();
  }
  
  private void processSettings (final InstrumentSettings instrumentSettings)
  {
    synchronized (this.currentInstrumentSettingsLock)
    {
      final InstrumentSettings oldSettings = this.currentInstrumentSettings;
      if (isOptimizeSettingsUpdates () && oldSettings != null && instrumentSettings.equals (oldSettings))
        return;
      this.currentInstrumentSettings = instrumentSettings;
    }
    fireInstrumentSettingsChanged (instrumentSettings);
  }
  
  private final Runnable instrumentSettingsDispatcher = RunnableInvoker.constantlyFromSupplierConsumerChain (
    "Instrument Settings Dispatcher",
    this,
    this::takeSettings,
    this::processSettings,
    null,
    null,
    false,
    b -> { if (b) error (); },
    Level.INFO,
    Level.WARNING);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT COMMAND PROCESSOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  // BlockingQueue implementations are thread-safe.
  private final BlockingQueue<InstrumentCommand> commandQueue = new LinkedBlockingQueue<> ();
  
  private final Object commandQueueLock = new Object ();
  
  /** The name of the command queue capacity property.
   * 
   */
  public final static String COMMAND_QUEUE_CAPACITY_PROPERTY_NAME = "commandQueueCapacity";
  
  /** The name of the command queue size property.
   * 
   */
  public final static String COMMAND_QUEUE_SIZE_PROPERTY_NAME = "commandQueueSize";
  
  /** The default command queue capacity.
   * 
   */
  public static final int DEFAULT_COMMAND_QUEUE_CAPACITY = 10;
  
  private int commandQueueCapacity = DEFAULT_COMMAND_QUEUE_CAPACITY;
  
  /** Gets the command queue capacity.
   * 
   * @return The command queue capacity ({@code >= 0}).
   * 
   */
  public final int getCommandQueueCapacity ()
  {
    return this.commandQueueCapacity;
  }
  
  /** Sets the command queue capacity to a new value, leaving its current entries intact (even if beyond capacity).
   * 
   * @param commandQueueCapacity The new command queue capacity ({@code >= 1}).
   * 
   * @throws IllegalArgumentException If the argument is zero or negative.
   * 
   * @see #setCommandQueueCapacityAndClear
   * 
   */
  public final void setCommandQueueCapacity (final int commandQueueCapacity)
  {
    if (commandQueueCapacity <= 0)
      throw new IllegalArgumentException ();
    final int oldCommandQueueCapacity;
    synchronized (this.commandQueueLock)
    {
      oldCommandQueueCapacity = this.commandQueueCapacity;
      if (commandQueueCapacity != oldCommandQueueCapacity)
        this.commandQueueCapacity = commandQueueCapacity;
      else
        return;
    }
    // Make sure we fire settings changed outside the critical region.
    fireSettingsChanged (COMMAND_QUEUE_CAPACITY_PROPERTY_NAME, oldCommandQueueCapacity, commandQueueCapacity);
  }
  
  /** Sets the command queue capacity to a new value and clears the (entire) command queue.
   * 
   * <p>
   * The commands deleted from the queue are provided with proper return values
   * indicating the fact that the commands were not executed;
   * the {@link InstrumentCommand#IC_RETURN_EXCEPTION_KEY} value is set to a
   * newly created {@link InterruptedException},
   * and any {@link Semaphore} set as value in {@link InstrumentCommand#IC_COMPLETION_SEMAPHORE_KEY}
   * is released.
   * 
   * @param commandQueueCapacity The new command queue capacity ({@code >= 1}).
   * 
   * @throws IllegalArgumentException If the argument is zero or negative.
   * 
   * @see InstrumentCommand#IC_RETURN_STATUS_KEY
   * @see InstrumentCommand#IC_RETURN_VALUE_KEY
   * @see InstrumentCommand#IC_RETURN_EXCEPTION_KEY
   * @see InstrumentCommand#IC_COMPLETION_SEMAPHORE_KEY
   * 
   * @see Semaphore#release
   * 
   * @see #setCommandQueueCapacity
   * @see #clearCommandQueue
   * 
   */
  public final void setCommandQueueCapacityAndClear (final int commandQueueCapacity)
  {
    if (commandQueueCapacity <= 0)
      throw new IllegalArgumentException ();
    final int oldCommandQueueCapacity;
    final int oldQueueSize;
    final BlockingQueue<InstrumentCommand> remainingCommands = new LinkedBlockingQueue<> ();
    synchronized (this.commandQueueLock)
    {
      oldCommandQueueCapacity = this.commandQueueCapacity;
      oldQueueSize = this.commandQueue.size ();
      if (commandQueueCapacity != oldCommandQueueCapacity)
        this.commandQueueCapacity = commandQueueCapacity;
      this.commandQueue.drainTo (remainingCommands);
      this.commandQueue.clear ();
    }
    // Make sure we fire settings changed outside the critical region.
    fireSettingsChanged (COMMAND_QUEUE_CAPACITY_PROPERTY_NAME, oldCommandQueueCapacity, commandQueueCapacity);
    if (oldQueueSize > 0)
      fireSettingsChanged (COMMAND_QUEUE_SIZE_PROPERTY_NAME, oldQueueSize, 0);
    for (final InstrumentCommand instrumentCommand : remainingCommands)
    {
      instrumentCommand.put (InstrumentCommand.IC_RETURN_STATUS_KEY, Boolean.FALSE);
      instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, null);
      instrumentCommand.put (InstrumentCommand.IC_RETURN_EXCEPTION_KEY, new InterruptedException ());
      if (instrumentCommand.containsKey (InstrumentCommand.IC_COMPLETION_SEMAPHORE_KEY))
        ((Semaphore) instrumentCommand.get (InstrumentCommand.IC_COMPLETION_SEMAPHORE_KEY)).release ();
    }
  }
  
  /** Clears the command queue.
   * 
   * <p>
   * Sub-classes may at some point in their lifetime decide to ignore
   * all pending/queued commands in the command queue.
   * Note that this method does not affect the {@link Service} status of the instrument,
   * nor does it attempt to interrupt/terminate processing of the current command.
   * 
   * <p>
   * The commands deleted from the queue are provided with proper return values
   * indicating the fact that the commands were not executed;
   * the {@link InstrumentCommand#IC_RETURN_EXCEPTION_KEY} value is set to a
   * newly created {@link InterruptedException},
   * and any {@link Semaphore} set as value in {@link InstrumentCommand#IC_COMPLETION_SEMAPHORE_KEY}
   * is released.
   * 
   * @see InstrumentCommand#IC_RETURN_STATUS_KEY
   * @see InstrumentCommand#IC_RETURN_VALUE_KEY
   * @see InstrumentCommand#IC_RETURN_EXCEPTION_KEY
   * @see InstrumentCommand#IC_COMPLETION_SEMAPHORE_KEY
   * 
   * @see Semaphore#release
   * 
   */
  protected final void clearCommandQueue ()
  {
    final BlockingQueue<InstrumentCommand> remainingCommands = new LinkedBlockingQueue<> ();
    final int oldQueueSize;
    synchronized (this.commandQueueLock)
    {
      oldQueueSize = this.commandQueue.size ();
      this.commandQueue.drainTo (remainingCommands);
      this.commandQueue.clear ();
    }
    // Make sure we fire settings changed outside the critical region.
    if (oldQueueSize > 0)
      fireSettingsChanged (COMMAND_QUEUE_SIZE_PROPERTY_NAME, oldQueueSize, 0);
    for (final InstrumentCommand instrumentCommand : remainingCommands)
    {
      instrumentCommand.put (InstrumentCommand.IC_RETURN_STATUS_KEY, Boolean.FALSE);
      instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, null);
      instrumentCommand.put (InstrumentCommand.IC_RETURN_EXCEPTION_KEY, new InterruptedException ());
      if (instrumentCommand.containsKey (InstrumentCommand.IC_COMPLETION_SEMAPHORE_KEY))
        ((Semaphore) instrumentCommand.get (InstrumentCommand.IC_COMPLETION_SEMAPHORE_KEY)).release ();
    }
  }
  
  private void addCommand (
    final InstrumentCommand instrumentCommand,
    final boolean synchronous,
    final long timeout,
    final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    if (instrumentCommand == null)
      throw new IllegalArgumentException ();
    final boolean overflow;
    final int newQueueSize;
    final Semaphore semaphore = synchronous ? new Semaphore (0) : null;
    synchronized (this.commandQueueLock)
    {
      // This statement cannot block...
      overflow = this.commandQueue.size () >= this.commandQueueCapacity || ! this.commandQueue.offer (instrumentCommand);
      newQueueSize = this.commandQueue.size ();
    }
    if (overflow)
      LOG.log (Level.WARNING, "Overflow on Instrument Command Queue on {0}.", this);
      // XXX Shouldn't we think of throwing an Exception here, instead of just logging and ignoring the command?
    else
    {
      // Make sure we fire settings changed outside the critical region.
      fireSettingsChanged (COMMAND_QUEUE_SIZE_PROPERTY_NAME, newQueueSize - 1, newQueueSize);
      if (synchronous)
        if (! semaphore.tryAcquire (timeout, unit))
          throw new TimeoutException ();  
    }
  }
  
  @Override
  public final void addCommand (final InstrumentCommand instrumentCommand)
  {
    try
    {
      addCommand (instrumentCommand, false, Long.MIN_VALUE /* dummy */, null /* dummy */);
    }
    catch (Exception e)
    {
      throw new RuntimeException (e);
    }
  }

  @Override
  public final void addAndProcessCommandSync (final InstrumentCommand instrumentCommand, final long timeout, final TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException
  {
    addCommand (instrumentCommand, true, timeout, unit);
  }
  
  private InstrumentCommand takeCommand ()
    throws InterruptedException
  {
    // Take a command from the queue; we cannot do this inside the this.commandQueueLock due to the risk of deadlock.
    // That's OK though, since this.commandQueue is thread safe.
    final InstrumentCommand instrumentCommand = this.commandQueue.take ();;
    final int newQueueSize;
    synchronized (this.commandQueueLock)
    {
      newQueueSize = this.commandQueue.size ();
    }
    fireSettingsChanged (COMMAND_QUEUE_SIZE_PROPERTY_NAME, newQueueSize + 1, newQueueSize);
    return instrumentCommand;
  }
  
  /** Processes a command.
   * 
   * <p>
   * To be implemented by sub-classes.
   * 
   * <p>
   * Sub-classes must make sure to properly put a return value in the map (see {@link InstrumentCommand#IC_RETURN_VALUE_KEY}).
   * 
   * @param instrumentCommand The command.
   * 
   * @throws IOException          If an I/O Exception was encountered while processing the command.
   * @throws InterruptedException If processing the command was interrupted.
   * @throws TimeoutException     If processing the command exceeded a timeout (sojourn/queueing/processing).
   * 
   */
  protected abstract void processCommand (final InstrumentCommand instrumentCommand)
    throws IOException, InterruptedException, TimeoutException;
  
  private void processCommandAndReleasePotentialWaiter (final InstrumentCommand instrumentCommand)
    throws IOException, InterruptedException, TimeoutException
  {
    try
    {
      instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, null);
      processCommand (instrumentCommand);
      instrumentCommand.put (InstrumentCommand.IC_RETURN_STATUS_KEY, Boolean.TRUE);
      // Setting InstrumentCommand.IC_RETURN_VALUE_KEY is the responsibility of #processCommand.
      instrumentCommand.put (InstrumentCommand.IC_RETURN_EXCEPTION_KEY, null);
    }
    catch (Exception e)
    {
      if (instrumentCommand != null)
      {
        instrumentCommand.put (InstrumentCommand.IC_RETURN_STATUS_KEY, Boolean.FALSE);
        instrumentCommand.put (InstrumentCommand.IC_RETURN_VALUE_KEY, null);
        instrumentCommand.put (InstrumentCommand.IC_RETURN_EXCEPTION_KEY, e);
      }
      throw (e);
    }
    finally
    {
      if (instrumentCommand.containsKey (InstrumentCommand.IC_COMPLETION_SEMAPHORE_KEY))
        ((Semaphore) instrumentCommand.get (InstrumentCommand.IC_COMPLETION_SEMAPHORE_KEY)).release ();
    }
  }
  
  private final Runnable instrumentCommandProcessor = RunnableInvoker.constantlyFromSupplierConsumerChain (
    "Instrument Command Processor",                // name (for logging only)
    this,                                          // host (for logging only)
    this::takeCommand,                             // resultSupplier
    this::processCommandAndReleasePotentialWaiter, // resultConsumer
    (Set<Class<? extends Exception>>) null,        // mustIgnore
    (Set<Class<? extends Exception>>) null,        // mustTerminate
    false,                                         // defaultTerminateUponException
    b -> { if (b) error (); },                     // terminateListener; arg: due to abnormal condition (true) or interrupt (false)
    Level.INFO,                                    // startTerminateLogLevel
    Level.WARNING);                                // runnableExceptionLogLevel
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract InstrumentReading getReadingFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException;
    
  protected abstract void requestReadingFromInstrumentASync ()
    throws IOException;
  
  private final Runnable instrumentReadingCollector = RunnableInvoker.periodicallyFromSupplierConsumerChain (
    "Instrument Reading Collector",
    this,
    this::getReadingCollectorPeriod_s,
    true,
    RunnableInvoker.OverloadPolicy.IGNORE_AND_DROP,
    this::getReadingFromInstrumentSync,
    this::readingReadFromInstrument,
    new LinkedHashSet<> (Arrays.<Class<? extends Exception>>asList (TimeoutException.class, IOException.class)),
    new LinkedHashSet<> (Arrays.<Class<? extends Exception>>asList (UnsupportedOperationException.class)),
    false,
    b -> { if (b) error (); },
    Level.INFO,
    Level.WARNING,
    Level.WARNING);
  
  public final static String READING_COLLECTOR_PERIOD_S_PROPERTY_NAME = "readingCollectorPeriod_s";
  
  public final static double DEFAULT_READING_COLLECTOR_PERIOD_S = 10;
  
  private volatile double readingCollectorPeriod_s = AbstractInstrument.DEFAULT_READING_COLLECTOR_PERIOD_S;

  private final Object readingCollectorPeriodLock = new Object ();
  
  public final double getReadingCollectorPeriod_s ()
  {
    synchronized (this.readingCollectorPeriodLock)
    {
      return this.readingCollectorPeriod_s;
    }
  }
  
  public final void setReadingCollectorPeriod_s (final double readingCollectorPeriod_s)
  {
    if (readingCollectorPeriod_s < 0)
      throw new IllegalArgumentException ();
    final double oldReadingCollectorPeriod_s;
    synchronized (this.readingCollectorPeriodLock)
    {
      if (this.readingCollectorPeriod_s == readingCollectorPeriod_s)
        return;
      oldReadingCollectorPeriod_s = this.readingCollectorPeriod_s;
      this.readingCollectorPeriod_s = readingCollectorPeriod_s;
    }
    fireSettingsChanged (
      AbstractInstrument.READING_COLLECTOR_PERIOD_S_PROPERTY_NAME,
      oldReadingCollectorPeriod_s,
      readingCollectorPeriod_s);
  }
  
  private final BlockingQueue<InstrumentReading> readingReadQueue = new LinkedBlockingQueue<> ();
  
  protected void readingReadFromInstrument (final InstrumentReading instrumentReading)
  {
    if (instrumentReading == null)
      return;
    if (! this.readingReadQueue.offer (instrumentReading))
      LOG.log (Level.WARNING, "Overflow on Instrument Reading Queue on {0}.", this);
  }
  
  private InstrumentReading takeReading ()
    throws InterruptedException
  {
    return this.readingReadQueue.take ();
  }
  
  private void processReading (final InstrumentReading instrumentReading)
  {
    fireInstrumentReading (instrumentReading);
  }
  
  private final Runnable instrumentReadingDispatcher = RunnableInvoker.constantlyFromSupplierConsumerChain (
    "Instrument Reading Dispatcher",
    this,
    this::takeReading,
    this::processReading,
    null,
    null,
    false,
    b -> { if (b) error (); },
    Level.INFO,
    Level.WARNING);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT HOUSEKEEPING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract void instrumentHousekeeping ()
    throws IOException, InterruptedException, TimeoutException;
    
  private final Runnable instrumentHousekeeper = RunnableInvoker.periodicallyFromRunnable (
    "Instrument Housekeeper",
    this,
    this::getHousekeeperPeriod_s,
    true,
    RunnableInvoker.OverloadPolicy.IGNORE_AND_DROP,
    this::instrumentHousekeeping,
    new LinkedHashSet<> (Arrays.<Class<? extends Exception>>asList (TimeoutException.class, IOException.class)),
    new LinkedHashSet<> (Arrays.<Class<? extends Exception>>asList (UnsupportedOperationException.class)),
    false,
    b -> { if (b) error (); },
    Level.INFO,
    Level.WARNING,
    Level.WARNING);
  
  public final static String HOUSEKEEPER_PERIOD_S_PROPERTY_NAME = "housekeeperPeriod_s";
  
  public final static double DEFAULT_HOUSEKEEPER_PERIOD_S = 1;
  
  private volatile double housekeeperPeriod_s = AbstractInstrument.DEFAULT_HOUSEKEEPER_PERIOD_S;

  private final Object housekeeperPeriodLock = new Object ();
  
  public final double getHousekeeperPeriod_s ()
  {
    synchronized (this.housekeeperPeriodLock)
    {
      return this.housekeeperPeriod_s;
    }
  }
  
  public final void setHousekeeperPeriod_s (final double housekeeperPeriod_s)
  {
    if (housekeeperPeriod_s < 0)
      throw new IllegalArgumentException ();
    final double oldHousekeeperPeriod_s;
    synchronized (this.housekeeperPeriodLock)
    {
      if (this.housekeeperPeriod_s == housekeeperPeriod_s)
        return;
      oldHousekeeperPeriod_s = this.housekeeperPeriod_s;
      this.housekeeperPeriod_s = housekeeperPeriod_s;
    }
    fireSettingsChanged (
      AbstractInstrument.HOUSEKEEPER_PERIOD_S_PROPERTY_NAME,
      oldHousekeeperPeriod_s,
      housekeeperPeriod_s);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
