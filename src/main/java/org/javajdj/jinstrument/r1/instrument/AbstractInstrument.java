package org.javajdj.jinstrument.r1.instrument;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.r1.Device;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.support.Service_FromMix;

/**
 *
 */
public abstract class AbstractInstrument
extends Service_FromMix
implements Instrument
{

  private static final Logger LOG = Logger.getLogger (AbstractInstrument.class.getName ());

  protected AbstractInstrument (final String name,
    final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices,
    final boolean addSettingsServices)
  {
    super (name, runnables, targetServices);
    if (device == null)
      throw new IllegalArgumentException ();
    this.device = device;
    if (addSettingsServices)
    {
      // XXX Aren't we losing the name of the Thread?
      addRunnable (new InstrumentStatusThread ());
      addRunnable (new InstrumentStatusDispatcherThread ());
      addRunnable (new InstrumentSettingsThread ());
      addRunnable (new InstrumentSettingsDispatcherThread ());
      addRunnable (new InstrumentCommandProcessorThread ());
    }
  }
  
  protected AbstractInstrument (final String name,
    final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices)
  {
    super (name, runnables, targetServices);
    if (device == null)
      throw new IllegalArgumentException ();
    this.device = device;
  }
  
  protected AbstractInstrument (final Device device,
    final List<Runnable> runnables,
    final List<Service> targetServices)
  {
    super (runnables, targetServices);
    if (device == null)
      throw new IllegalArgumentException ();
    this.device = device;
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GET STATUS FROM INSTRUMENT SYNCHRONOUSLY [ABSTRACT]
  // REQUEST STATUS FROM INSTRUMENT ASYNCHROUNOUSLY [ABSTRACT]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract InstrumentStatus getStatusFromInstrumentSync ()
    throws UnsupportedOperationException, IOException, InterruptedException;
    
  protected abstract void requestStatusFromInstrumentASync ()
    throws UnsupportedOperationException, IOException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT STATUS THREAD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected class InstrumentStatusThread
    extends Thread
  {
    
    public InstrumentStatusThread (final String name)
    {
      super (name);
    }
    
    public InstrumentStatusThread ()
    {
      this ("InstrumentStatusThread on " + AbstractInstrument.this);
    }
    
    @Override
    public final void run ()
    {
      while (! isInterrupted ())
      {
        try
        {
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
          final long remainingSleep_ms = ((long) AbstractInstrument.this.getStatusThreadPeriod_s () * 1000) - sojourn_ms;
          if (remainingSleep_ms < 0)
            LOG.log (Level.WARNING, "Cannot meet timing settings on {0}.", this);
          if (remainingSleep_ms > 0)
            Thread.sleep (remainingSleep_ms);
        }
        catch (InterruptedException ie)
        {
          LOG.log (Level.WARNING, "InterruptedException while acquiring instrument status: {0}.", ie.getStackTrace ());
          return;
        }
        catch (IOException ioe)
        {
          LOG.log (Level.WARNING, "IOException while acquiring instrument status: {0}.", ioe.getStackTrace ());
        }
        catch (UnsupportedOperationException usoe)
        {
          LOG.log (Level.WARNING, "UnsupportedOperationException while acquiring instrument status: {0}.", usoe.getStackTrace ());
        }
        // Prevent termination of the Thread due to a misbehaving sub-class method.
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Exception while acquiring instrument status: {0}.", e.getStackTrace ());
        }
      }
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS THREAD PERIOD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String STATUS_THREAD_PERIOD_S_PROPERTY_NAME = "statusThreadPeriod_s";
  
  public final static double DEFAULT_STATUS_THREAD_PERIOD_S = 5;
  
  private volatile double statusThreadPeriod_s = AbstractInstrument.DEFAULT_STATUS_THREAD_PERIOD_S;

  private final Object statusThreadPeriodLock = new Object ();
  
  public final double getStatusThreadPeriod_s ()
  {
    synchronized (this.statusThreadPeriodLock)
    {
      return this.statusThreadPeriod_s;
    }
  }
  
  public final void setStatusThreadPeriod_s (final double statusThreadPeriod_s)
  {
    if (statusThreadPeriod_s < 0)
      throw new IllegalArgumentException ();
    final double oldStatusThreadPeriod_s;
    synchronized (this.statusThreadPeriodLock)
    {
      if (this.statusThreadPeriod_s == statusThreadPeriod_s)
        return;
      oldStatusThreadPeriod_s = this.statusThreadPeriod_s;
      this.statusThreadPeriod_s = statusThreadPeriod_s;
    }
    fireSettingsChanged (
      AbstractInstrument.STATUS_THREAD_PERIOD_S_PROPERTY_NAME,
      oldStatusThreadPeriod_s,
      statusThreadPeriod_s);
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
      LOG.log (Level.WARNING, "Overflow on status-read buffer on {0}.", this);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT STATUS DISPATCHER THREAD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected class InstrumentStatusDispatcherThread
    extends Thread
  {
    
    public InstrumentStatusDispatcherThread (final String name)
    {
      super (name);
    }
    
    public InstrumentStatusDispatcherThread ()
    {
      this ("InstrumentStatusDispatcherThread on " + AbstractInstrument.this + ".");
    }
  
    @Override
    public final void run ()
    {
      while (! isInterrupted ())
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
          LOG.log (Level.WARNING, "InterruptedException while dispatching instrument status: {0}.", ie.getStackTrace ());
          return;
        }
        // Prevent termination of the Thread due to a misbehaving listener.
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Exception while dispatching instrument status: {0}.", e.getStackTrace ());
        }
      }
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GET SETTINGS FROM INSTRUMENT SYNCHRONOUSLY [ABSTRACT]
  // REQUEST SETTINGS FROM INSTRUMENT ASYNCHROUNOUSLY [ABSTRACT]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract InstrumentSettings getSettingsFromInstrumentSync ()
    throws UnsupportedOperationException, IOException, InterruptedException;
    
  protected abstract void requestSettingsFromInstrumentASync ()
    throws UnsupportedOperationException, IOException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT SETTINGS THREAD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected class InstrumentSettingsThread
    extends Thread
  {
    
    public InstrumentSettingsThread (final String name)
    {
      super (name);
    }
    
    public InstrumentSettingsThread ()
    {
      this ("InstrumentSettingsThread on " + AbstractInstrument.this);
    }
    
    @Override
    public final void run ()
    {
      while (! isInterrupted ())
      {
        try
        {
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
          final long remainingSleep_ms = ((long) AbstractInstrument.this.getSettingsThreadPeriod_s () * 1000) - sojourn_ms;
          if (remainingSleep_ms < 0)
            LOG.log (Level.WARNING, "Cannot meet timing settings on {0}.", this);
          if (remainingSleep_ms > 0)
            Thread.sleep (remainingSleep_ms);
        }
        catch (InterruptedException ie)
        {
          LOG.log (Level.WARNING, "InterruptedException while acquiring instrument settings: {0}.", ie.getStackTrace ());
          return;
        }
        catch (IOException ioe)
        {
          LOG.log (Level.WARNING, "IOException while acquiring instrument settings: {0}.", ioe.getMessage ());
        }
        catch (UnsupportedOperationException usoe)
        {
          LOG.log (Level.WARNING, "UnsupportedOperationException while acquiring instrument settings: {0}.",
            usoe.getStackTrace ());
        }
        // Prevent termination of the Thread due to a misbehaving sub-class method.
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Exception while acquiring instrument settings: {0}.", e.getStackTrace ());
        }
      }
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SETTINGS THREAD PERIOD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String SETTINGS_THREAD_PERIOD_S_PROPERTY_NAME = "settingsThreadPeriod_s";
  
  public final static double DEFAULT_SETTINGS_THREAD_PERIOD_S = 10;
  
  private volatile double settingsThreadPeriod_s = AbstractInstrument.DEFAULT_SETTINGS_THREAD_PERIOD_S;

  private final Object settingsThreadPeriodLock = new Object ();
  
  public final double getSettingsThreadPeriod_s ()
  {
    synchronized (this.settingsThreadPeriodLock)
    {
      return this.settingsThreadPeriod_s;
    }
  }
  
  public final void setSettingsThreadPeriod_s (final double settingsThreadPeriod_s)
  {
    if (settingsThreadPeriod_s < 0)
      throw new IllegalArgumentException ();
    final double oldSettingsThreadPeriod_s;
    synchronized (this.settingsThreadPeriodLock)
    {
      if (this.settingsThreadPeriod_s == settingsThreadPeriod_s)
        return;
      oldSettingsThreadPeriod_s = this.settingsThreadPeriod_s;
      this.settingsThreadPeriod_s = settingsThreadPeriod_s;
    }
    fireSettingsChanged (
      AbstractInstrument.SETTINGS_THREAD_PERIOD_S_PROPERTY_NAME,
      oldSettingsThreadPeriod_s,
      settingsThreadPeriod_s);
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
      LOG.log (Level.WARNING, "Overflow on settings-read buffer on {0}.", this);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT SETTINGS DISPATCHER THREAD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected class InstrumentSettingsDispatcherThread
    extends Thread
  {
    
    public InstrumentSettingsDispatcherThread (final String name)
    {
      super (name);
    }
    
    public InstrumentSettingsDispatcherThread ()
    {
      this ("InstrumentSettingsDispatcherThread on " + AbstractInstrument.this + ".");
    }
  
    @Override
    public final void run ()
    {
      while (! isInterrupted ())
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
          LOG.log (Level.WARNING, "InterruptedException while dispatching instrument settings: {0}.", ie.getStackTrace ());
          return;
        }
        // Prevent termination of the Thread due to a misbehaving listener.
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Exception while dispatching instrument settings: {0}.", e.getStackTrace ());
        }
      }
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
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
  // COMMAND QUEUE
  // PROCESS COMMAND [ABSTRACT]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final BlockingQueue<InstrumentCommand> commandQueue = new LinkedBlockingQueue<> ();
  
  protected void addCommand (final InstrumentCommand instrumentCommand)
  {
    if (instrumentCommand == null)
      throw new IllegalArgumentException ();
    if (! this.commandQueue.offer (instrumentCommand))
      LOG.log (Level.WARNING, "Overflow on command buffer on {0}.", this);
  }
  
  protected abstract void processCommand (final InstrumentCommand instrumentCommand)
    throws UnsupportedOperationException, IOException, InterruptedException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT COMMAND PROCESSOR THREAD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected class InstrumentCommandProcessorThread
    extends Thread
  {
    
    public InstrumentCommandProcessorThread (final String name)
    {
      super (name);
    }
    
    public InstrumentCommandProcessorThread ()
    {
      this ("InstrumentCommandProcessorThread on " + AbstractInstrument.this);
    }
    
    @Override
    public final void run ()
    {
      while (! isInterrupted ())
      {
        try
        {
          final InstrumentCommand nextCommand = AbstractInstrument.this.commandQueue.take ();
          AbstractInstrument.this.processCommand (nextCommand);
        }
        catch (InterruptedException ie)
        {
          LOG.log (Level.WARNING, "InterruptedException while processing command: {0}.", ie.getStackTrace ());
          return;
        }
        catch (IOException ioe)
        {
          LOG.log (Level.WARNING, "IOException while processing command: {0}.", ioe.getStackTrace ());
        }
        catch (UnsupportedOperationException uoe)
        {
          LOG.log (Level.WARNING, "UnsupportedOperationException while processing command: {0}.", uoe.getStackTrace ());
        }
        // Prevent termination of the Thread due to a misbehaving implementation of processCommand.
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Exception while processing command: {0}.", e.getStackTrace ());
        }
      }
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
