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
package org.javajdj.jinstrument.gpib;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeoutException;
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
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected AbstractGpibInstrument (
    final String name,
    final GpibDevice device,
    final List<Runnable> runnables,
    final List<Service> targetServices,
    final boolean addStatusServices,
    final boolean addSettingsServices,
    final boolean addCommandProcessorServices,
    final boolean addAcquisitionServices)
  {
    super (
      name,
      device,
      runnables,
      targetServices,
      addStatusServices,
      addSettingsServices,
      addCommandProcessorServices,
      addAcquisitionServices);
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
