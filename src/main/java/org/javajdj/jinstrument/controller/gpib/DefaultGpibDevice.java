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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
  // Device
  // CONTROLLER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final GpibController controller;
  
  @Override
  public final GpibController getController ()
  {
    return this.controller;
  }
  
  @Override
  public final String getDeviceUrl ()
  {
    return getController ().getBuses ()[0].getBusUrl () + "#" + getBusAddress ().getBusAddressUrl ();
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
  // COMMAND
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public void addCommand (final GpibControllerCommand command)
  {
    this.controller.addCommand (command);    
  }
  
  @Override
  public void doControllerCommandSync (final GpibControllerCommand command, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final BlockingQueue<ControllerCommand> queue = new LinkedBlockingQueue<> ();
    final ControllerListener listener = (final Controller controller, final ControllerCommand controllerCommand) ->
    {
      if (controller == DefaultGpibDevice.this.controller && controllerCommand == command)
        queue.add (command);
    };
    this.controller.addControllerListener (listener);
    this.controller.addCommand (command);
    try
    {
      if (queue.poll (timeout_ms, TimeUnit.MILLISECONDS) == null)
      {
        final TimeoutException toe = new TimeoutException ();
        command.put (ControllerCommand.CC_EXCEPTION_KEY, toe);
      }
    }
    catch (InterruptedException ie)
    {
      command.put (ControllerCommand.CC_EXCEPTION_KEY, ie);
    }
    finally
    {
      this.controller.removeControllerListener (listener);
    }
    if (command.containsKey (ControllerCommand.CC_EXCEPTION_KEY))
    {
      final Exception e = (Exception) command.get (ControllerCommand.CC_EXCEPTION_KEY);
      if (e instanceof InterruptedException)
        throw (InterruptedException) e;
      else if (e instanceof IOException)
        throw (IOException) e;
      else if (e instanceof TimeoutException)
        throw (TimeoutException) e;
      else if (e instanceof UnsupportedOperationException)
        throw (UnsupportedOperationException) e;
      else
        throw new IllegalArgumentException (e);
    }    
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // read
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateReadCommand ()
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CC_GPIB_READ,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address);
    return command;
  }
  
  @Override
  public void readAsync ()
  {
    this.controller.addCommand (generateReadCommand ());
  }

  @Override
  public byte[] readSync (final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateReadCommand ();
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // readln
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateReadlnCommand (final GpibDevice.ReadlineTerminationMode readlineTerminationMode)
  {
    if (readlineTerminationMode == null)
      throw new IllegalArgumentException ();
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CC_GPIB_READLN,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE, readlineTerminationMode);
    return command;
  }
  
  @Override
  public void readlnAsync (final GpibDevice.ReadlineTerminationMode readlineTerminationMode)
  {
    this.controller.addCommand (generateReadlnCommand (readlineTerminationMode));    
  }
  
  @Override
  public byte[] readlnSync (final GpibDevice.ReadlineTerminationMode readlineTerminationMode, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateReadlnCommand (readlineTerminationMode);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // readN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateReadNCommand (final int N)
  {
    if (N < 0)
      throw new IllegalArgumentException ();
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CC_GPIB_READ_N,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_READ_N, N);
    return command;
  }
  
  @Override
  public void readNAsync (final int N)
  {
    this.controller.addCommand (generateReadNCommand (N));
  }

  @Override
  public byte[] readNSync (final int N, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateReadNCommand (N);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // write
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateWriteCommand (final byte[] bytes)
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CC_GPIB_WRITE,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes);
    return command;    
  }
  
  @Override
  public void writeAsync (final byte[] bytes)
  {
    this.controller.addCommand (generateWriteCommand (bytes));
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
  // writeAndRead
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateWriteAndReadCommand (final byte[] bytes)
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CC_GPIB_WRITE_AND_READ,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes);
    return command;
  }
  
  @Override
  public void writeAndReadAsync (final byte[] bytes)
  {
    this.controller.addCommand (generateWriteAndReadCommand (bytes));
  }

  @Override
  public byte[] writeAndReadSync (final byte[] bytes, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateWriteAndReadCommand (bytes);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // writeAndReadln
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateWriteAndReadlnCommand (
    final byte[] bytes,
    final GpibDevice.ReadlineTerminationMode readlineTerminationMode)
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CC_GPIB_WRITE_AND_READLN,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes,
      GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE, readlineTerminationMode);
    return command;
  }
  
  @Override
  public void writeAndReadlnAsync (final byte[] bytes, final GpibDevice.ReadlineTerminationMode readlineTerminationMode)
  {
    this.controller.addCommand (generateWriteAndReadlnCommand (bytes, readlineTerminationMode));
  }

  @Override
  public byte[] writeAndReadlnSync (
    final byte[] bytes,
    final GpibDevice.ReadlineTerminationMode readlineTerminationMode,
    final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateWriteAndReadlnCommand (bytes, readlineTerminationMode);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // writeAndReadN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateWriteAndReadNCommand (final byte[] bytes, final int N)
  {
    if (N < 0)
      throw new IllegalArgumentException ();
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CC_GPIB_WRITE_AND_READ_N,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes,
      GpibControllerCommand.CCARG_GPIB_READ_N, N);
    return command;    
  }
  
  @Override
  public void writeAndReadNAsync (final byte[] bytes, final int N)
  {
    this.controller.addCommand (generateWriteAndReadNCommand (bytes, N));
  }
  
  @Override
  public byte[] writeAndReadNSync (final byte[] bytes, final int N, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateWriteAndReadNCommand (bytes, N);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // writeAndReadlnN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateWriteAndReadlnNCommand (
    final byte[] bytes,
    final GpibDevice.ReadlineTerminationMode readlineTerminationMode,
    final int N)
  {
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CC_GPIB_WRITE_AND_READLN_N,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes,
      GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE, readlineTerminationMode,
      GpibControllerCommand.CCARG_GPIB_READ_N, N);
    return command;
  }
  
  @Override
  public void writeAndReadlnNAsync (
    final byte[] bytes,
    final GpibDevice.ReadlineTerminationMode readlineTerminationMode,
    final int N)
  {
    this.controller.addCommand (generateWriteAndReadlnNCommand (bytes, readlineTerminationMode, N));
  }

  @Override
  public byte[][] writeAndReadlnNSync (
    final byte[] bytes,
    final GpibDevice.ReadlineTerminationMode readlineTerminationMode,
    final int N,
    final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final GpibControllerCommand command = generateWriteAndReadlnNCommand (bytes, readlineTerminationMode, N);
    doControllerCommandSync (command, timeout_ms);
    return (byte[][]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // atomicSequence
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibControllerCommand generateAtomicSequenceCommand (final GpibControllerCommand[] sequence)
  {
    if (sequence == null)
      throw new IllegalArgumentException ();
    final GpibControllerCommand command = new DefaultGpibControllerCommand (GpibControllerCommand.CC_GPIB_ATOMIC_SEQUENCE,
      GpibControllerCommand.CCARG_GPIB_ATOMIC_SEQUENCE, sequence);
    return command;
  }
  
  @Override
  public void atomicSequenceAsync (final GpibControllerCommand[] sequence)
    throws UnsupportedOperationException
  {
    this.controller.addCommand (generateAtomicSequenceCommand (sequence));
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
