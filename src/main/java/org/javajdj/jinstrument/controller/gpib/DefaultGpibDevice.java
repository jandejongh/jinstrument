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
package org.javajdj.jinstrument.controller.gpib;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.ControllerCommand;
import org.javajdj.jinstrument.ControllerListener;
import org.javajdj.jservice.support.Service_FromMix;

/** Implementation of {@link GpibDevice}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultGpibDevice
  extends Service_FromMix
  implements GpibDevice
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultGpibDevice.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultGpibDevice (final GpibController controller, final GpibAddress address)
  {
    super ("DefaultGpibDevice", null, null);
    if (controller == null || address == null)
      throw new IllegalArgumentException ();
    this.controller = controller;
    this.address = address;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String toString ()
  {
    return getDeviceUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Device
  // DEVICE URL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final String getDeviceUrl ()
  {
    return getController ().getBuses ()[0].getBusUrl () + "#" + getBusAddress ().getBusAddressUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Device/GpibDevice
  // CONTROLLER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final GpibController controller;
  
  @Override
  public final GpibController getController ()
  {
    return this.controller;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Device/GpibDevice
  // BUS ADDRESS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final GpibAddress address;
  
  @Override
  public final GpibAddress getBusAddress ()
  {
    return this.address;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // DO CONTROLLER COMMAND
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static long FALLBACK_SOJOURN_TIMEOUT_MS = 10000;
  
  @Override
  public void doControllerCommandAsync (
    final GpibControllerCommand command,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
  {
    if (queueingTimeout_ms != null)
      command.setQueueingTimeout (Duration.ofMillis (queueingTimeout_ms));
    if (processingTimeout_ms != null)
      command.setProcessingTimeout (Duration.ofMillis (processingTimeout_ms));
    if (sojournTimeout_ms != null)
      command.setSojournTimeout (Duration.ofMillis (sojournTimeout_ms));
    if ((queueingTimeout_ms == null || processingTimeout_ms == null) && sojournTimeout_ms == null)
    {
      LOG.log (Level.WARNING, "Please fix or report: No upper bound on sojourn time on device {0} for command {1};"
        + " inserting fallback sojourn timeout {2}.",
        new Object[]{this, command, DefaultGpibDevice.FALLBACK_SOJOURN_TIMEOUT_MS});
      command.setQueueingTimeout (Duration.ofMillis (DefaultGpibDevice.FALLBACK_SOJOURN_TIMEOUT_MS));
    }
    getController ().addCommand (command);
  }
  
  @Override
  public void doControllerCommandSync (final GpibControllerCommand command, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    //
    // XXX
    //
    // This piece of code needs some review.
    // (1) There should be some easier BlockingQueue type that we can use (just entry or something like that).
    // (2) Upon timeout here, we modify the command, but that might have been done already by the controller.
    // (3) Shouldn't we remove the command from the controller queue upon timeout?
    // (4) Isn't it easier to put this complexity into the controller itself?
    //
    // XXX
    //
    final BlockingQueue<ControllerCommand> queue = new LinkedBlockingQueue<> ();
    final ControllerListener listener = (final Controller controller, final ControllerCommand controllerCommand) ->
    {
      if (controller == getController () && controllerCommand == command)
        queue.add (command);
    };
    getController ().addControllerListener (listener);
    doControllerCommandAsync (command, null, null, timeout_ms);
    try
    {
      if (queue.poll (timeout_ms, TimeUnit.MILLISECONDS) == null)
      {
        final TimeoutException toe = new TimeoutException ();
        command.put (ControllerCommand.CCRET_EXCEPTION_KEY, toe);
      }
    }
    catch (InterruptedException ie)
    {
      command.put (ControllerCommand.CCRET_EXCEPTION_KEY, ie);
    }
    finally
    {
      getController ().removeControllerListener (listener);
    }
    if (command.containsKey (ControllerCommand.CCRET_EXCEPTION_KEY))
    {
      final Exception e = (Exception) command.get (ControllerCommand.CCRET_EXCEPTION_KEY);
      if (e instanceof InterruptedException)
        throw (InterruptedException) e;
      else if (e instanceof IOException)
        throw (IOException) e;
      else if (e instanceof TimeoutException)
        throw (TimeoutException) e;
      else if (e instanceof UnsupportedOperationException)
        throw (UnsupportedOperationException) e;
      else
      {
        LOG.log (Level.WARNING, "Unexpected Exception on {0}: {1}.",
          new Object[]{this.toString (), e == null ? "null" : Arrays.toString (e.getStackTrace ())});
        throw new IOException (e);
      }
    }    
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // SELECTED DEVICE CLEAR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public GpibControllerCommand generateSelectedDeviceClearCommand ()
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (
      GpibControllerCommand.CCCMD_GPIB_SELECTED_DEVICE_CLEAR,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address);
    return command;
  }
  
  @Override
  public void selectedDeviceCLearAsync (
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
    throws UnsupportedOperationException
  {
    doControllerCommandAsync (
      generateSelectedDeviceClearCommand (),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public void selectedDeviceClearSync (final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException
  {
    final GpibControllerCommand command = generateSelectedDeviceClearCommand ();
    doControllerCommandSync (command, timeout_ms);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // [POLL] SERVICE REQUEST
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generatePollServiceRequestCommand ()
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_POLL_SERVICE_REQUEST,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address);
    return command;
  }
  
  @Override
  public void pollServiceRequestAsync (
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
    throws UnsupportedOperationException
  {
    doControllerCommandAsync (
      generatePollServiceRequestCommand (),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public boolean pollServiceRequestSync (final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException
  {
    final GpibControllerCommand command = generatePollServiceRequestCommand ();
    doControllerCommandSync (command, timeout_ms);
    return (boolean) command.get (GpibControllerCommand.CCRET_VALUE_KEY);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // SERIAL POLL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateSerialPollCommand ()
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_SERIAL_POLL,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address);
    return command;
  }
  
  @Override
  public void serialPollAsync (
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
    throws UnsupportedOperationException
  {
    doControllerCommandAsync (
      generateSerialPollCommand (),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public byte serialPollSync (final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException
  {
    final GpibControllerCommand command = generateSerialPollCommand ();
    doControllerCommandSync (command, timeout_ms);
    return (byte) command.get (GpibControllerCommand.CCRET_VALUE_KEY);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // READ EOI
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateReadEOICommand ()
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_READ_EOI,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address);
    return command;
  }
  
  @Override
  public void readEOIAsync (
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
  {
    doControllerCommandAsync (
      generateReadEOICommand (),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public byte[] readEOISync (final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateReadEOICommand ();
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCRET_VALUE_KEY);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // READ LINE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateReadlnCommand (final ReadlineTerminationMode readlineTerminationMode)
  {
    if (readlineTerminationMode == null)
      throw new IllegalArgumentException ();
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_READLN,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE, readlineTerminationMode);
    return command;
  }
  
  @Override
  public void readlnAsync (
    final ReadlineTerminationMode readlineTerminationMode,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
  {
    doControllerCommandAsync (
      generateReadlnCommand (readlineTerminationMode),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);    
  }
  
  @Override
  public byte[] readlnSync (final ReadlineTerminationMode readlineTerminationMode, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateReadlnCommand (readlineTerminationMode);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCRET_VALUE_KEY);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // READ N
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateReadNCommand (final int N)
  {
    if (N < 0)
      throw new IllegalArgumentException ();
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_READ_N,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_READ_N, N);
    return command;
  }
  
  @Override
  public void readNAsync (
    final int N,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
  {
    doControllerCommandAsync (
      generateReadNCommand (N),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public byte[] readNSync (final int N, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateReadNCommand (N);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCRET_VALUE_KEY);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // WRITE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateWriteCommand (final byte[] bytes)
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_WRITE,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes);
    return command;    
  }
  
  @Override
  public void writeAsync (
    final byte[] bytes,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
  {
    doControllerCommandAsync (
      generateWriteCommand (bytes),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public void writeSync (final byte[] bytes, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateWriteCommand (bytes);
    doControllerCommandSync (command, timeout_ms);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // WRITE AND READ EOI
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateWriteAndReadEOICommand (final byte[] bytes)
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_WRITE_AND_READ_EOI,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes);
    return command;
  }
  
  @Override
  public void writeAndReadEOIAsync (
    final byte[] bytes,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
  {
    doControllerCommandAsync (
      generateWriteAndReadEOICommand (bytes),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public byte[] writeAndReadEOISync (final byte[] bytes, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateWriteAndReadEOICommand (bytes);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCRET_VALUE_KEY);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // WRITE AND READ LINE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateWriteAndReadlnCommand (
    final byte[] bytes,
    final ReadlineTerminationMode readlineTerminationMode)
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_WRITE_AND_READLN,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes,
      GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE, readlineTerminationMode);
    return command;
  }
  
  @Override
  public void writeAndReadlnAsync (
    final byte[] bytes,
    final ReadlineTerminationMode readlineTerminationMode,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
  {
    doControllerCommandAsync (
      generateWriteAndReadlnCommand (bytes, readlineTerminationMode),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public byte[] writeAndReadlnSync (
    final byte[] bytes,
    final ReadlineTerminationMode readlineTerminationMode,
    final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateWriteAndReadlnCommand (bytes, readlineTerminationMode);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCRET_VALUE_KEY);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // WRITE AND READ N
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateWriteAndReadNCommand (final byte[] bytes, final int N)
  {
    if (N < 0)
      throw new IllegalArgumentException ();
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_WRITE_AND_READ_N,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes,
      GpibControllerCommand.CCARG_GPIB_READ_N, N);
    return command;    
  }
  
  @Override
  public void writeAndReadNAsync (
    final byte[] bytes,
    final int N,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
  {
    doControllerCommandAsync (
      generateWriteAndReadNCommand (bytes, N),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }
  
  @Override
  public byte[] writeAndReadNSync (final byte[] bytes, final int N, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateWriteAndReadNCommand (bytes, N);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCRET_VALUE_KEY);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // WRITE AND READ LINE N
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateWriteAndReadlnNCommand (
    final byte[] bytes,
    final ReadlineTerminationMode readlineTerminationMode,
    final int N)
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_WRITE_AND_READLN_N,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes,
      GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE, readlineTerminationMode,
      GpibControllerCommand.CCARG_GPIB_READ_N, N);
    return command;
  }
  
  @Override
  public void writeAndReadlnNAsync (
    final byte[] bytes,
    final ReadlineTerminationMode readlineTerminationMode,
    final int N,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
  {
    doControllerCommandAsync (
      generateWriteAndReadlnNCommand (bytes, readlineTerminationMode, N),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public byte[][] writeAndReadlnNSync (
    final byte[] bytes,
    final ReadlineTerminationMode readlineTerminationMode,
    final int N,
    final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateWriteAndReadlnNCommand (bytes, readlineTerminationMode, N);
    doControllerCommandSync (command, timeout_ms);
    return (byte[][]) command.get (GpibControllerCommand.CCRET_VALUE_KEY);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // ATOMIC SEQUENCE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateAtomicSequenceCommand (final GpibControllerCommand[] sequence)
  {
    if (sequence == null)
      throw new IllegalArgumentException ();
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_ATOMIC_SEQUENCE,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_ATOMIC_SEQUENCE, sequence);
    return command;
  }
  
  @Override
  public void atomicSequenceAsync (
    final GpibControllerCommand[] sequence,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)    
    throws UnsupportedOperationException
  {
    doControllerCommandAsync (
      generateAtomicSequenceCommand (sequence),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public void atomicSequenceSync (final GpibControllerCommand[] sequence, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException
  {
    final GpibControllerCommand command = generateAtomicSequenceCommand (sequence);
    doControllerCommandSync (command, timeout_ms);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // ATOMIC REPEAT UNTIL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateAtomicRepeatUntilCommand (
    final GpibControllerCommand commandToRepeat,
    final Function<GpibControllerCommand, Boolean> condition)
  {
    if (commandToRepeat == null || condition == null)
      throw new IllegalArgumentException ();
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_ATOMIC_REPEAT_UNTIL,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_ATOMIC_REPEAT_UNTIL_COMMAND, commandToRepeat,
      GpibControllerCommand.CCARG_GPIB_ATOMIC_REPEAT_UNTIL_CONDITION, condition);
    return command;
  }
  
  @Override
  public void atomicRepeatUntilAsync (
    final GpibControllerCommand commandToRepeat,
    final Function<GpibControllerCommand, Boolean> condition,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
    throws UnsupportedOperationException
  {
    doControllerCommandAsync (
      generateAtomicRepeatUntilCommand (commandToRepeat, condition),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public void atomicRepeatUntilSync (
    final GpibControllerCommand commandToRepeat,
    final Function<GpibControllerCommand, Boolean> condition,
    final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException
  {
    final GpibControllerCommand command = generateAtomicRepeatUntilCommand (commandToRepeat, condition);
    doControllerCommandSync (command, timeout_ms);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GpibDevice
  // USER RUNNABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateUserRunnableCommand (final Runnable runnable)
  {
    if (runnable == null)
      throw new IllegalArgumentException ();
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CCCMD_GPIB_USER_RUNNABLE,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_USER_RUNNABLE, runnable);
    return command;
  }
  
  @Override
  public void userRunnableAsync (
    final Runnable runnable,
    final Long queueingTimeout_ms,
    final Long processingTimeout_ms,
    final Long sojournTimeout_ms)
    throws UnsupportedOperationException
  {
    doControllerCommandAsync (
      generateUserRunnableCommand (runnable),
      queueingTimeout_ms,
      processingTimeout_ms,
      sojournTimeout_ms);
  }

  @Override
  public void userRunnableSync (
    final Runnable runnable,
    final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException
  {
    final GpibControllerCommand command = generateUserRunnableCommand (runnable);
    doControllerCommandSync (command, timeout_ms);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
