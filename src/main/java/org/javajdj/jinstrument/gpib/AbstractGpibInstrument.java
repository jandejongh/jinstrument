/*
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.gpib;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.AbstractInstrument;
import org.javajdj.jinstrument.controller.gpib.GpibControllerCommand;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.controller.gpib.ReadlineTerminationMode;
import org.javajdj.jservice.Service;

/** Abstract base class for {@link GpibInstrument}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public abstract class AbstractGpibInstrument
  extends AbstractInstrument
  implements GpibInstrument
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (AbstractGpibInstrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected AbstractGpibInstrument (
    final String name,
    final GpibDevice device,
    final List<Runnable> runnables,
    final List<Service> targetServices,
    final boolean addInitializationServices,
    final boolean addStatusServices,
    final boolean addSettingsServices,
    final boolean addCommandProcessorServices,
    final boolean addAcquisitionServices,
    final boolean addHousekeepingServices,
    final boolean addServiceRequestPollingServices)
  {
    super (
      name,
      device,
      runnables,
      targetServices,
      addInitializationServices,
      addStatusServices,
      addSettingsServices,
      addCommandProcessorServices,
      addAcquisitionServices,
      addHousekeepingServices);
    if (addServiceRequestPollingServices)
    {
      addRunnable (this.gpibInstrumentServiceRequestCollector);
    }
    addRunnable (this.gpibServiceRequestDispatcher);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // GpibInstrument
  // DEVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibDevice getDevice ()
  {
    return (GpibDevice) super.getDevice ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READLINE TERMINATION MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String READLINE_TERMINATION_MODE_PROPERTY_NAME = "readlineTerminationMode";
  
  public final static ReadlineTerminationMode DEFAULT_READLINE_TERMINATION_MODE = ReadlineTerminationMode.OPTCR_LF;
  
  public final ReadlineTerminationMode getReadlineTerminationMode ()
  {
    return DEFAULT_READLINE_TERMINATION_MODE;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SELECTED DEVICE CLEAR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String SELECTED_DEVICE_CLEAR_TIMEOUT_MS_PROPERTY_NAME = "selectedDeviceClearTimeout_ms";
  
  public final static long DEFAULT_SELECTED_DEVICE_CLEAR_TIMEOUT_MS = 1000L;
  
  protected final byte selectedDeviceCLearSync ()
    throws InterruptedException, IOException, TimeoutException
  {
    final byte statusByte = getDevice ().serialPollSync (getSelectedDeviceClearTimeout_ms ());
    return statusByte;
  }
    
  public final long getSelectedDeviceClearTimeout_ms ()
  {
    return DEFAULT_SELECTED_DEVICE_CLEAR_TIMEOUT_MS;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // [POLL] SERVICE REQUEST
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String POLL_SERVICE_REQUEST_TIMEOUT_MS_PROPERTY_NAME = "pollServiceRequestTimeout_ms";
  
  public final static long DEFAULT_POLL_SERVICE_REQUEST_TIMEOUT_MS = 100L;
  
  // XXX Need locking or AtomicLong here...
  private volatile long pollServiceRequestTimeout_ms = DEFAULT_POLL_SERVICE_REQUEST_TIMEOUT_MS;
  
  public final long getPollServiceRequestTimeout_ms ()
  {
    return this.pollServiceRequestTimeout_ms;
  }
  
  public final void setPollServiceRequestTimeout_ms (final long pollServiceRequestTimeout_ms)
  {
    if (pollServiceRequestTimeout_ms <= 0)
      throw new IllegalArgumentException ();
    if (pollServiceRequestTimeout_ms != this.pollServiceRequestTimeout_ms)
    {
      final long oldPollServiceRequestTimeout_ms = this.pollServiceRequestTimeout_ms;
      this.pollServiceRequestTimeout_ms = pollServiceRequestTimeout_ms;
      fireSettingsChanged (
        POLL_SERVICE_REQUEST_TIMEOUT_MS_PROPERTY_NAME,
        oldPollServiceRequestTimeout_ms,
        this.pollServiceRequestTimeout_ms);
    }
  }
  
  protected final boolean pollServiceRequestSync ()
    throws InterruptedException, IOException, TimeoutException
  {
    final boolean serviceRequest = getDevice ().pollServiceRequestSync (getPollServiceRequestTimeout_ms ());
    return serviceRequest;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERIAL POLL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String SERIAL_POLL_TIMEOUT_MS_PROPERTY_NAME = "serialPollTimeout_ms";
  
  public final static long DEFAULT_SERIAL_POLL_TIMEOUT_MS = 1000L;
  
  // XXX Need locking or AtomicLong here...
  private volatile long serialPollTimeout_ms = DEFAULT_SERIAL_POLL_TIMEOUT_MS;
  
  public final long getSerialPollTimeout_ms ()
  {
    return this.serialPollTimeout_ms;
  }
  
  public final void setSerialPollTimeout_ms (final long serialPollTimeout_ms)
  {
    if (serialPollTimeout_ms <= 0)
      throw new IllegalArgumentException ();
    if (serialPollTimeout_ms != this.serialPollTimeout_ms)
    {
      final long oldSerialPollTimeout_ms = this.serialPollTimeout_ms;
      this.serialPollTimeout_ms = serialPollTimeout_ms;
      fireSettingsChanged (SERIAL_POLL_TIMEOUT_MS_PROPERTY_NAME, oldSerialPollTimeout_ms, this.serialPollTimeout_ms);
    }
  }
  
  protected final byte serialPollSync ()
    throws InterruptedException, IOException, TimeoutException
  {
    final byte statusByte = getDevice ().serialPollSync (getSerialPollTimeout_ms ());
    return statusByte;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // WRITE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static long DEFAULT_WRITE_TIMEOUT_MS = 2000L;
  
  public final long getWriteTimeout_ms ()
  {
    return DEFAULT_WRITE_TIMEOUT_MS;
  }
  
  protected final GpibControllerCommand generateWriteCommand (final String string)
  {
    return getDevice ().generateWriteCommand (string.getBytes (Charset.forName ("US-ASCII")));
  }
  
  protected void writeSync (final String string, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    getDevice ().writeSync (string.getBytes (Charset.forName ("US-ASCII")), timeout_ms);
  }
  
  protected final void writeSync (final String string)
    throws InterruptedException, IOException, TimeoutException
  {
    writeSync (string, getWriteTimeout_ms ());
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String READ_N_TIMEOUT_MS_PROPERTY_NAME = "readNTimeout_ms";
  
  private final static long DEFAULT_READ_N_TIMEOUT_MS = 10000L;
  
  // XXX Need locking or AtomicLong here...
  private volatile long readNTimeout_ms = DEFAULT_READ_N_TIMEOUT_MS;
  
  public final long getReadNTimeout_ms ()
  {
    return this.readNTimeout_ms;
  }
  
  public final void setReadNTimeout_ms (final long readNTimeout_ms)
  {
    if (readNTimeout_ms <= 0)
      throw new IllegalArgumentException ();
    if (readNTimeout_ms != this.readNTimeout_ms)
    {
      final long oldReadNTimeout_ms = this.readNTimeout_ms;
      this.readNTimeout_ms = readNTimeout_ms;
      fireSettingsChanged (READ_N_TIMEOUT_MS_PROPERTY_NAME, oldReadNTimeout_ms, this.readNTimeout_ms);
    }
  }
  
  protected final GpibControllerCommand generateReadNCommand (final int N)
  {
    return getDevice ().generateReadNCommand (N);
  }
  
  protected final byte[] readNSync (final int N)
    throws InterruptedException, IOException, TimeoutException
  {
    final byte[] bytes = getDevice ().readNSync (N, getReadNTimeout_ms ());
    return bytes;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READLINE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static long DEFAULT_READLINE_TIMEOUT_MS = 1000L;
  
  public final long getReadlineTimeout_ms ()
  {
    return DEFAULT_READLINE_TIMEOUT_MS;
  }
  
  protected final GpibControllerCommand generateReadlnCommand ()
  {
    return getDevice ().generateReadlnCommand (getReadlineTerminationMode ());
  }
  
  protected final String readlnSync ()
    throws InterruptedException, IOException, TimeoutException
  {
    final byte[] bytes = getDevice ().readlnSync (getReadlineTerminationMode (), getReadlineTimeout_ms ());
    return new String (bytes, Charset.forName ("US-ASCII"));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // WRITE AND READLN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected final GpibControllerCommand generateWriteAndReadlnCommand (final String string)
  {
    return getDevice ().generateWriteAndReadlnCommand (
      string.getBytes (Charset.forName ("US-ASCII")),
      getReadlineTerminationMode ());
  }
  
  protected final String writeAndReadlnSync (final String string)
    throws InterruptedException, IOException, TimeoutException
  {
    final byte[] bytes = getDevice ().writeAndReadlnSync (
      string.getBytes (Charset.forName ("US-ASCII")),
      getReadlineTerminationMode (),
      getReadlineTimeout_ms ());
    return new String (bytes, Charset.forName ("US-ASCII"));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // WRITE AND READN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  protected final GpibControllerCommand generateWriteAndReadNCommand (final String string, final int N)
  {
    return getDevice ().generateWriteAndReadNCommand (
      string.getBytes (Charset.forName ("US-ASCII")),
      N);
  }
  
  protected final byte[] writeAndReadNSync (final String string, final int N)
    throws InterruptedException, IOException, TimeoutException
  {
    final byte[] bytes = getDevice ().writeAndReadNSync (string.getBytes (Charset.forName ("US-ASCII")),
      N,
      getReadNTimeout_ms ());
    return bytes;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ATOMIC SEQUENCE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static long DEFAULT_ATOMIC_SEQUENCE_TIMEOUT_MS = 10000L;
  
  public final long getAtomicSequenceTimeout_ms ()
  {
    return DEFAULT_ATOMIC_SEQUENCE_TIMEOUT_MS;
  }
  
  protected final void atomicSequenceSync (final GpibControllerCommand[] sequence)
    throws InterruptedException, IOException, TimeoutException
  {
    getDevice ().atomicSequenceSync (sequence, getAtomicSequenceTimeout_ms ());
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // USER RUNNABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static long DEFAULT_USER_RUNNABLE_TIMEOUT_MS = 10000L;
  
  protected final GpibControllerCommand generateUserRunnableCommand (final Runnable runnable)
  {
    return getDevice ().generateUserRunnableCommand (runnable);
  }
  
  public final long getUserRunnableTimeout_ms ()
  {
    return DEFAULT_USER_RUNNABLE_TIMEOUT_MS;
  }
  
  protected final void userRunnableSync (final Runnable runnable)
    throws InterruptedException, IOException, TimeoutException
  {
    getDevice ().userRunnableSync (runnable, getUserRunnableTimeout_ms ());
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static long DEFAULT_GET_STATUS_TIMEOUT_MS = 1000L;
  
  public final long getGetStatusTimeout_ms ()
  {
    return DEFAULT_GET_STATUS_TIMEOUT_MS;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static long DEFAULT_GET_SETTINGS_TIMEOUT_MS = 1000L;
  
  public final long getGetSettingsTimeout_ms ()
  {
    return DEFAULT_GET_SETTINGS_TIMEOUT_MS;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static long DEFAULT_GET_READING_TIMEOUT_MS = 10000L;
  
  public final long getGetReadingTimeout_ms ()
  {
    return DEFAULT_GET_READING_TIMEOUT_MS;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ON GPIB SERVICE REQUEST FROM INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected abstract void onGpibServiceRequestFromInstrument ()
    throws IOException, InterruptedException, TimeoutException;
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GPIB INSTRUMENT SERVICE REQUEST COLLECTOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Runnable gpibInstrumentServiceRequestCollector = () ->
  {
    LOG.log (Level.INFO, "Starting GPIB Instrument Service Request Collector on {0}.", AbstractGpibInstrument.this.toString ());
    boolean hadException = false;
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        if (hadException)
        {
          // Quick and dirty method to prevent continuous operation in case of (early) ignored Exceptions.
          // We should actually compensate for the time spent already...
          Thread.sleep ((long) (AbstractGpibInstrument.this.getGpibInstrumentServiceRequestCollectorPeriod_s () * 1000));
          hadException = false;
        }
        // Mark start of sojourn.
        final long timeBeforeProbe_ms = System.currentTimeMillis ();
        // Obtain SRQ status from the instrument.
        final boolean srqRead = AbstractGpibInstrument.this.pollServiceRequestSync ();
        // Report a Service Request.
        if (srqRead)
          AbstractGpibInstrument.this.gpibServiceRequestFromInstrument ();
        // Mark end of sojourn.
        final long timeAfterProbeAndDelivery_ms = System.currentTimeMillis ();
        // Calculate sojourn.
        final long sojourn_ms = timeAfterProbeAndDelivery_ms - timeBeforeProbe_ms;
        // Find out the remaining time to wait in order to respect the given period.
        final long remainingSleep_ms =
          ((long) AbstractGpibInstrument.this.getGpibInstrumentServiceRequestCollectorPeriod_s () * 1000) - sojourn_ms;
        if (remainingSleep_ms < 0)
        {
          LOG.log (Level.WARNING, "GPIB Instrument Service Request Collector cannot meet timing reading on {0}.",
            AbstractGpibInstrument.this.toString ());
          hadException = true;
        }
        else if (remainingSleep_ms > 0)
          Thread.sleep (remainingSleep_ms);
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) GPIB Instrument Service Request Collector on {0}.",
          AbstractGpibInstrument.this.toString ());
        return;
      }
      catch (TimeoutException te)
      {
        LOG.log (Level.WARNING, "TimeoutException (ignored) in GPIB Instrument Service Request Collector on {0}: {1}.",
          new Object[]{AbstractGpibInstrument.this.toString (), Arrays.toString (te.getStackTrace ())});
        hadException = true;
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "Terminating (IOException) GPIB Instrument Service Request Collector on {0}: {1}.",
          new Object[]{AbstractGpibInstrument.this.toString (), Arrays.toString (ioe.getStackTrace ())});
        error ();
        return;
      }
      catch (UnsupportedOperationException usoe)
      {
        LOG.log (Level.WARNING,
          "Terminating (UnsupportedOperationException) GPIB Instrument Service Request Collector on {0}: {1}.",
          new Object[]{AbstractGpibInstrument.this.toString (), Arrays.toString (usoe.getStackTrace ())});
        error ();
        return;
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Terminating (Exception) GPIB Instrument Service Request Collector on {0}: {1}.",
          new Object[]{AbstractGpibInstrument.this.toString (), Arrays.toString (e.getStackTrace ())});
        error ();
        return;
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) GPIB Instrument Service Request Collector on {0}.",
      AbstractGpibInstrument.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GPIB INSTRUMENT SERVICE REQUEST COLLECTOR PERIOD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String GPIB_INSTRUMENT_SERVICE_REQUEST_COLLECTOR_PERIOD_S_PROPERTY_NAME =
    "gpibInstrumentServiceRequestCollectorPeriod_s";
  
  public final static double DEFAULT_GPIB_INSTRUMENT_SERVICE_REQUEST_COLLECTOR_PERIOD_S = 0.1;
  
  private volatile double gpibInstrumentServiceRequestCollectorPeriod_s =
    AbstractGpibInstrument.DEFAULT_GPIB_INSTRUMENT_SERVICE_REQUEST_COLLECTOR_PERIOD_S;

  private final Object gpibInstrumentServiceRequestCollectorPeriodLock = new Object ();
  
  public final double getGpibInstrumentServiceRequestCollectorPeriod_s ()
  {
    synchronized (this.gpibInstrumentServiceRequestCollectorPeriodLock)
    {
      return this.gpibInstrumentServiceRequestCollectorPeriod_s;
    }
  }
  
  public final void setGpibInstrumentServiceRequestCollectorPeriod_s (final double gpibInstrumentServiceRequestCollectorPeriod_s)
  {
    if (gpibInstrumentServiceRequestCollectorPeriod_s < 0)
      throw new IllegalArgumentException ();
    final double oldGpibInstrumentServiceRequestCollectorPeriod_s;
    synchronized (this.gpibInstrumentServiceRequestCollectorPeriodLock)
    {
      if (this.gpibInstrumentServiceRequestCollectorPeriod_s == gpibInstrumentServiceRequestCollectorPeriod_s)
        return;
      oldGpibInstrumentServiceRequestCollectorPeriod_s = this.gpibInstrumentServiceRequestCollectorPeriod_s;
      this.gpibInstrumentServiceRequestCollectorPeriod_s = gpibInstrumentServiceRequestCollectorPeriod_s;
    }
    fireSettingsChanged (AbstractGpibInstrument.GPIB_INSTRUMENT_SERVICE_REQUEST_COLLECTOR_PERIOD_S_PROPERTY_NAME,
      oldGpibInstrumentServiceRequestCollectorPeriod_s,
      gpibInstrumentServiceRequestCollectorPeriod_s);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GPIB SERVICE REQUEST QUEUE
  // GPIB SERVICE REQUEST FROM INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final BlockingQueue<Boolean> gpibServiceRequestQueue = new LinkedBlockingQueue<> ();
  
  protected void gpibServiceRequestFromInstrument ()
  {
    if (! this.gpibServiceRequestQueue.offer (true))
      LOG.log (Level.WARNING, "Overflow on GPIB Instrument Service Request Queue on {0}.", this);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GPIB SERVICE REQUEST DISPATCHER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Runnable gpibServiceRequestDispatcher = () ->
  {
    LOG.log (Level.INFO, "Starting GPIB Instrument Service Request Dispatcher on {0}.", AbstractGpibInstrument.this.toString ());
    while (! Thread.currentThread ().isInterrupted ())
    {
      try
      {
        final boolean srq = AbstractGpibInstrument.this.gpibServiceRequestQueue.take ();
        if (srq)
        {
          AbstractGpibInstrument.this.onGpibServiceRequestFromInstrument ();
          // XXX It seems like a good idea to clear the SR Queue here...
        }
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "Terminating (by request) GPIB Instrument Service Request Dispatcher on {0}.",
          AbstractGpibInstrument.this.toString ());
        return;
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception (ignored) in GPIB Instrument Service Request Dispatcher on {0}: {1}.",
          new Object[]{AbstractGpibInstrument.this.toString (), Arrays.toString (e.getStackTrace ())});
      }
    }
    LOG.log (Level.INFO, "Terminating (by request) GPIB Instrument Service Request Dispatcher on {0}.",
      AbstractGpibInstrument.this.toString ());
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
