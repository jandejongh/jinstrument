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
package org.javajdj.jinstrument.instrument.controller.gpib;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.ControllerCommand;
import org.javajdj.jinstrument.ControllerListener;
import org.javajdj.jinstrument.DefaultControllerCommand;
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

  public DefaultGpibDevice (final GpibController controller, final GpibAddress address, final String deviceUrl)
  {
    super ("DefaultGpibDevice", null, null);
    if (controller == null || address == null || deviceUrl == null || deviceUrl.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    this.controller = controller;
    this.address = address;
    this.deviceUrl = deviceUrl;
  }

  private final GpibController controller;
  
  @Override
  public final GpibController getController ()
  {
    return this.controller;
  }
  
  private final GpibAddress address;
  
  @Override
  public final GpibAddress getAddress ()
  {
    return this.address;
  }
  
  private String deviceUrl;

  @Override
  public final String getDeviceUrl ()
  {
    return this.deviceUrl;
  }
  
  private void doControllerCommandSync (final ControllerCommand command, final long timeout_ms)
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
  
  @Override
  public void readAsync ()
  {
    this.controller.addCommand (new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_READ,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address));
  }

  @Override
  public byte[] readSync (final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final ControllerCommand command = new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_READ,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }

  @Override
  public void readlnAsync (final GpibDevice.ReadlineTerminationMode readlineTerminationMode)
  {
    if (readlineTerminationMode == null)
      throw new IllegalArgumentException ();
    this.controller.addCommand (new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_READLN,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE, readlineTerminationMode));    
  }
  
  @Override
  public byte[] readlnSync (final GpibDevice.ReadlineTerminationMode readlineTerminationMode, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final ControllerCommand command = new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_READLN,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE, readlineTerminationMode);    
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }

  @Override
  public void readNAsync (final int N)
  {
    if (N < 0)
      throw new IllegalArgumentException ();
    this.controller.addCommand (new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_READ_N,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_READ_N, N));
  }

  @Override
  public byte[] readNSync (final int N, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    if (N < 0)
      throw new IllegalArgumentException ();
    final ControllerCommand command = new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_READ_N,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_READ_N, N);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }

  @Override
  public void writeAsync (final byte[] bytes)
  {
    this.controller.addCommand (new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_WRITE,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes));
  }

  @Override
  public void writeSync (final byte[] bytes, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final ControllerCommand command = new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_WRITE,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes);
    doControllerCommandSync (command, timeout_ms);
  }

  @Override
  public void writeAndReadAsync (final byte[] bytes)
  {
    this.controller.addCommand (new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_WRITE_AND_READ,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes));
  }

  @Override
  public byte[] writeAndReadSync (final byte[] bytes, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final ControllerCommand command = new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_WRITE_AND_READ,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }
  
  @Override
  public void writeAndReadlnAsync (final byte[] bytes, final GpibDevice.ReadlineTerminationMode readlineTerminationMode)
  {
    this.controller.addCommand (new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_WRITE_AND_READLN,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes,
      GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE, readlineTerminationMode));
  }

  @Override
  public byte[] writeAndReadlnSync (
    final byte[] bytes,
    final GpibDevice.ReadlineTerminationMode readlineTerminationMode,
    final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    final ControllerCommand command = new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_WRITE_AND_READLN,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes,
      GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE, readlineTerminationMode);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }
  
  @Override
  public void writeAndReadNAsync (final byte[] bytes, final int N)
  {
    if (N < 0)
      throw new IllegalArgumentException ();
    this.controller.addCommand (new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_WRITE_AND_READ_N,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes,
      GpibControllerCommand.CCARG_GPIB_READ_N, N));
  }
  
  @Override
  public byte[] writeAndReadNSync (final byte[] bytes, final int N, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException
  {
    if (N < 0)
      throw new IllegalArgumentException ();
    final ControllerCommand command = new DefaultControllerCommand (GpibControllerCommand.CC_GPIB_WRITE_AND_READ_N,
      GpibControllerCommand.CCARG_GPIB_ADDRESS, this.address,
      GpibControllerCommand.CCARG_GPIB_WRITE_BYTES, bytes,
      GpibControllerCommand.CCARG_GPIB_READ_N, N);
    doControllerCommandSync (command, timeout_ms);
    return (byte[]) command.get (GpibControllerCommand.CCARG_GPIB_READ_BYTES);
  }

}
