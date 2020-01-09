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
  
  protected AbstractInstrument (final String name,
    final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices,
    final boolean addStatusServices,
    final boolean addSettingsServices,
    final boolean addCommandProcessorServices,
    final boolean addAcquisitionServices)
  {
    super (name, runnables, targetServices);
    if (device == null)
      throw new IllegalArgumentException ();
    this.device = device;
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
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
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
  // Instrument
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // CURRENT INSTRUMENT STATUS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GET STATUS FROM INSTRUMENT SYNCHRONOUSLY [ABSTRACT]
  // REQUEST STATUS FROM INSTRUMENT ASYNCHRONOUSLY [ABSTRACT]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract InstrumentStatus getStatusFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException;
    
  protected abstract void requestStatusFromInstrumentASync ()
    throws IOException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT STATUS COLLECTOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Runnable instrumentStatusCollector = () ->
  {
    LOG.log (Level.INFO, "Starting Instrument Status Collector on {0}.", AbstractInstrument.this.toString ());
    boolean hadException = false;
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        if (hadException)
        {
          // Quick and dirty method to prevent continuous operation in case of (early) ignored Exceptions.
          // We should actually compensate for the time spent already...
          Thread.sleep ((long) AbstractInstrument.this.getStatusCollectorPeriod_s () * 1000);
          hadException = false;
        }
        // Mark start of sojourn.
        final long timeBeforeRead_ms = System.currentTimeMillis ();
        // Obtain current settings from the instrument.
        final InstrumentStatus statusRead = AbstractInstrument.this.getStatusFromInstrumentSync ();
        // Report the trace to our superclass.
        AbstractInstrument.this.statusReadFromInstrument (statusRead);
        // Mark end of sojourn.
        final long timeAfterRead_ms = System.currentTimeMillis ();
        // Calculate sojourn.
        final long sojourn_ms = timeAfterRead_ms - timeBeforeRead_ms;
        // Find out the remaining time to wait in order to respect the given period.
        final long remainingSleep_ms = ((long) AbstractInstrument.this.getStatusCollectorPeriod_s () * 1000) - sojourn_ms;
        if (remainingSleep_ms < 0)
          LOG.log (Level.WARNING, "Instrument Status Collector cannot meet timing settings in on {0}.",
            AbstractInstrument.this.toString ());
        if (remainingSleep_ms > 0)
          Thread.sleep (remainingSleep_ms);
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) Instrument Status Collector on {0}.",
          AbstractInstrument.this.toString ());
        return;
      }
      catch (TimeoutException te)
      {
        LOG.log (Level.WARNING, "TimeoutException (ignored) in Instrument Status Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (te.getStackTrace ())});
        hadException = true;
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "Terminating (IOException) Instrument Status Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (ioe.getStackTrace ())});
        error ();
        return;
      }
      catch (UnsupportedOperationException usoe)
      {
        LOG.log (Level.WARNING, "Terminating (UnsupportedOperationException) Instrument Status Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (usoe.getStackTrace ())});
        error ();
        return;
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Terminating (Exception) Instrument Status Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (e.getStackTrace ())});
        error ();
        return;
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) Instrument Status Collector on {0}.",
      AbstractInstrument.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS COLLECTOR PERIOD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS READ QUEUE
  // STATUS READ FROM INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final BlockingQueue<InstrumentStatus> statusReadQueue = new LinkedBlockingQueue<> ();
  
  protected void statusReadFromInstrument (final InstrumentStatus instrumentStatus)
  {
    if (instrumentStatus == null)
      throw new IllegalArgumentException ();
    if (! this.statusReadQueue.offer (instrumentStatus))
      LOG.log (Level.WARNING, "Overflow on Instrument Status Queue on {0}.", this);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT STATUS DISPATCHER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Runnable instrumentStatusDispatcher = () ->
  {
    LOG.log (Level.INFO, "Starting Instrument Status Dispatcher on {0}.", AbstractInstrument.this.toString ());
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        final InstrumentStatus oldStatus;
        final InstrumentStatus newStatus = AbstractInstrument.this.statusReadQueue.take ();
        synchronized (AbstractInstrument.this.currentInstrumentStatusLock)
        {
          oldStatus = AbstractInstrument.this.currentInstrumentStatus;
          if (oldStatus == null || ! newStatus.equals (oldStatus))
          {
            AbstractInstrument.this.currentInstrumentStatus = newStatus;
            AbstractInstrument.this.fireInstrumentStatusChanged (newStatus);
          }
        }
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) Instrument Status Dispatcher on {0}.",
          AbstractInstrument.this.toString ());
        return;
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception (ignored) in Instrument Status Dispatcher on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (e.getStackTrace ())});
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) Instrument Status Dispatcher on {0}.",
      AbstractInstrument.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // CURRENT INSTRUMENT SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GET SETTINGS FROM INSTRUMENT SYNCHRONOUSLY [ABSTRACT]
  // REQUEST SETTINGS FROM INSTRUMENT ASYNCHRONOUSLY [ABSTRACT]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract InstrumentSettings getSettingsFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException;
    
  protected abstract void requestSettingsFromInstrumentASync ()
    throws IOException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT SETTINGS COLLECTOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Runnable instrumentSettingsCollector = () ->
  {
    LOG.log (Level.INFO, "Starting Instrument Settings Collector on {0}.", AbstractInstrument.this.toString ());
    boolean hadException = false;
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        if (hadException)
        {
          // Quick and dirty method to prevent continuous operation in case of (early) ignored Exceptions.
          // We should actually compensate for the time spent already...
          Thread.sleep ((long) AbstractInstrument.this.getStatusCollectorPeriod_s () * 1000);
          hadException = false;
        }
        // Mark start of sojourn.
        final long timeBeforeRead_ms = System.currentTimeMillis ();
        // Obtain current settings from the instrument.
        final InstrumentSettings settingsRead = AbstractInstrument.this.getSettingsFromInstrumentSync ();
        // Report the trace to our superclass.
        AbstractInstrument.this.settingsReadFromInstrument (settingsRead);
        // Mark end of sojourn.
        final long timeAfterRead_ms = System.currentTimeMillis ();
        // Calculate sojourn.
        final long sojourn_ms = timeAfterRead_ms - timeBeforeRead_ms;
        // Find out the remaining time to wait in order to respect the given period.
        final long remainingSleep_ms = ((long) AbstractInstrument.this.getSettingsCollectorPeriod_s () * 1000) - sojourn_ms;
        if (remainingSleep_ms < 0)
          LOG.log (Level.WARNING, "Instrument Settings Collector cannot meet timing settings on {0}.",
            AbstractInstrument.this.toString ());
        if (remainingSleep_ms > 0)
          Thread.sleep (remainingSleep_ms);
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) Instrument Settings Collector on {0}.",
          AbstractInstrument.this.toString ());
        return;
      }
      catch (TimeoutException te)
      {
        LOG.log (Level.WARNING, "TimeoutException (ignored) in Instrument Settings Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (te.getStackTrace ())});
        hadException = true;
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "Terminating (IOException) Instrument Settings Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (ioe.getStackTrace ())});
        error ();
        return;
      }
      catch (UnsupportedOperationException usoe)
      {
        LOG.log (Level.WARNING, "Terminating (UnsupportedOperationException) Instrument Settings Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (usoe.getStackTrace ())});
        error ();
        return;
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Terminating (Exception) Instrument Settings Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (e.getStackTrace ())});
        error ();
        return;
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) Instrument Settings Collector on {0}.",
      AbstractInstrument.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SETTINGS COLLECTOR PERIOD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SETTINGS READ QUEUE
  // SETTINGS READ FROM INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final BlockingQueue<InstrumentSettings> settingsReadQueue = new LinkedBlockingQueue<> ();
  
  protected void settingsReadFromInstrument (final InstrumentSettings instrumentSettings)
  {
    if (instrumentSettings == null)
      throw new IllegalArgumentException ();
    if (! this.settingsReadQueue.offer (instrumentSettings))
      LOG.log (Level.WARNING, "Overflow on Instrument Settings Queue on {0}.", this);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT SETTINGS DISPATCHER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Runnable instrumentSettingsDispatcher = () ->
  {
    LOG.log (Level.INFO, "Starting Instrument Settings Dispatcher on {0}.", AbstractInstrument.this.toString ());
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        final InstrumentSettings oldSettings;
        final InstrumentSettings newSettings = AbstractInstrument.this.settingsReadQueue.take ();
        synchronized (AbstractInstrument.this.currentInstrumentSettingsLock)
        {
          oldSettings = AbstractInstrument.this.currentInstrumentSettings;
          if (oldSettings == null || ! newSettings.equals (oldSettings))
          {
            AbstractInstrument.this.currentInstrumentSettings = newSettings;
            AbstractInstrument.this.fireInstrumentSettingsChanged (newSettings);
          }
        }
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) Instrument Settings Dispatcher on {0}.",
          AbstractInstrument.this.toString ());
        return;
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception (ignored) in Instrument Settings Dispatcher on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (e.getStackTrace ())});
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) Instrument Settings Dispatcher on {0}.",
      AbstractInstrument.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COMMAND QUEUE
  // ADD COMMAND [Instrument]
  // PROCESS COMMAND [ABSTRACT]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final BlockingQueue<InstrumentCommand> commandQueue = new LinkedBlockingQueue<> ();
  
  @Override
  public final void addCommand (final InstrumentCommand instrumentCommand)
  {
    if (instrumentCommand == null)
      throw new IllegalArgumentException ();
    if (! this.commandQueue.offer (instrumentCommand))
      LOG.log (Level.WARNING, "Overflow on Instrument Command Queue on {0}.", this);
  }
  
  protected abstract void processCommand (final InstrumentCommand instrumentCommand)
    throws IOException, InterruptedException, TimeoutException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT COMMAND PROCESSOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Runnable instrumentCommandProcessor = () ->
  {
    LOG.log (Level.INFO, "Starting Instrument Command Processor on {0}.", AbstractInstrument.this.toString ());
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        final InstrumentCommand nextCommand = AbstractInstrument.this.commandQueue.take ();
        AbstractInstrument.this.processCommand (nextCommand);
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) Instrument Command Processor on {0}.",
          AbstractInstrument.this.toString ());
        return;
      }
      catch (TimeoutException te)
      {
        LOG.log (Level.WARNING, "TimeoutException (ignored) in Instrument Command Processor on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (te.getStackTrace ())});
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "IOException (ignored) in Instrument Command Processor on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (ioe.getStackTrace ())});
      }
      catch (UnsupportedOperationException uoe)
      {
        LOG.log (Level.WARNING, "UnsupportedOperationException (ignored) in Instrument Command Processor on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (uoe.getStackTrace ())});
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception (ignored) in Instrument Command Processor on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (e.getStackTrace ())});
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) Instrument Command Processor on {0}.",
      AbstractInstrument.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // LAST INSTRUMENT READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String LAST_INSTRUMENT_READING_PROPERTY_NAME = "lastInstrumentReading";
  
  private volatile InstrumentReading lastInstrumentReading = null;
  
  private final Object lastInstrumentReadingLock = new Object ();
  
  @Override
  public final InstrumentReading getLastInstrumentReading ()
  {
    synchronized (this.lastInstrumentReadingLock)
    {
      return this.lastInstrumentReading;
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GET READING FROM INSTRUMENT SYNCHRONOUSLY [ABSTRACT]
  // REQUEST READING FROM INSTRUMENT ASYNCHRONOUSLY [ABSTRACT]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract InstrumentReading getReadingFromInstrumentSync ()
    throws IOException, InterruptedException, TimeoutException;
    
  protected abstract void requestReadingFromInstrumentASync ()
    throws IOException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT READING COLLECTOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Runnable instrumentReadingCollector = () ->
  {
    LOG.log (Level.INFO, "Starting Instrument Reading Collector on {0}.", AbstractInstrument.this.toString ());
    boolean hadException = false;
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        if (hadException)
        {
          // Quick and dirty method to prevent continuous operation in case of (early) ignored Exceptions.
          // We should actually compensate for the time spent already...
          Thread.sleep ((long) AbstractInstrument.this.getStatusCollectorPeriod_s () * 1000);
          hadException = false;
        }
        // Mark start of sojourn.
        final long timeBeforeRead_ms = System.currentTimeMillis ();
        // Obtain current reading from the instrument.
        final InstrumentReading readingRead = AbstractInstrument.this.getReadingFromInstrumentSync ();
        // Report the trace to our superclass.
        AbstractInstrument.this.readingReadFromInstrument (readingRead);
        // Mark end of sojourn.
        final long timeAfterRead_ms = System.currentTimeMillis ();
        // Calculate sojourn.
        final long sojourn_ms = timeAfterRead_ms - timeBeforeRead_ms;
        // Find out the remaining time to wait in order to respect the given period.
        final long remainingSleep_ms = ((long) AbstractInstrument.this.getReadingCollectorPeriod_s () * 1000) - sojourn_ms;
        if (remainingSleep_ms < 0)
          LOG.log (Level.WARNING, "Instrument Reading Collector cannot meet timing reading on {0}.",
            AbstractInstrument.this.toString ());
        if (remainingSleep_ms > 0)
          Thread.sleep (remainingSleep_ms);
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) Instrument Reading Collector on {0}.",
          AbstractInstrument.this.toString ());
        return;
      }
      catch (TimeoutException te)
      {
        LOG.log (Level.WARNING, "TimeoutException (ignored) in Instrument Reading Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (te.getStackTrace ())});
        hadException = true;
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "Terminating (IOException) Instrument Reading Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (ioe.getStackTrace ())});
        error ();
        return;
      }
      catch (UnsupportedOperationException usoe)
      {
        LOG.log (Level.WARNING, "Terminating (UnsupportedOperationException) Instrument Reading Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (usoe.getStackTrace ())});
        error ();
        return;
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Terminating (Exception) Instrument Reading Collector on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (e.getStackTrace ())});
        error ();
        return;
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) Instrument Reading Collector on {0}.",
      AbstractInstrument.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READING COLLECTOR PERIOD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READING READ QUEUE
  // READING READ FROM INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final BlockingQueue<InstrumentReading> readingReadQueue = new LinkedBlockingQueue<> ();
  
  protected void readingReadFromInstrument (final InstrumentReading instrumentReading)
  {
    if (instrumentReading == null)
      throw new IllegalArgumentException ();
    if (! this.readingReadQueue.offer (instrumentReading))
      LOG.log (Level.WARNING, "Overflow on Instrument Reading Queue on {0}.", this);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT READING DISPATCHER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Runnable instrumentReadingDispatcher = () ->
  {
    LOG.log (Level.INFO, "Starting Instrument Reading Dispatcher on {0}.", AbstractInstrument.this.toString ());
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        final InstrumentReading newReading = AbstractInstrument.this.readingReadQueue.take ();
        synchronized (AbstractInstrument.this.lastInstrumentReadingLock)
        {
          AbstractInstrument.this.lastInstrumentReading = newReading;
          AbstractInstrument.this.fireInstrumentReading (newReading);
        }
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) Instrument Reading Dispatcher on {0}.",
          AbstractInstrument.this.toString ());
        return;
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception (ignored) in Instrument Reading Dispatcher on {0}: {1}.",
          new Object[]{AbstractInstrument.this.toString (), Arrays.toString (e.getStackTrace ())});
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) Instrument Reading Dispatcher on {0}.",
      AbstractInstrument.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
