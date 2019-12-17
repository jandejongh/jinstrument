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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.ControllerCommand;

/** Implementation of {@link Controller} for the Prologix GPIB-ETHERNET controller.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class ProLogixGpibEthernetController
  extends AbstractGpibController
{

  private static final Logger LOG = Logger.getLogger (ProLogixGpibEthernetController.class.getName ());
  
  public ProLogixGpibEthernetController (final String ipAddress, final int tcpConnectPort)
  {
    super ("ProLogixGpibEthernetController", null, null);
    if (ipAddress == null || tcpConnectPort < 0 || tcpConnectPort > 65535)
      throw new IllegalArgumentException ();
    this.ipAddress = ipAddress;
    this.tcpConnectPort = tcpConnectPort;
    addRunnable (new SocketManager ());
  }
  
  private final String ipAddress;
  
  private final int tcpConnectPort;

  @Override
  public String getControllerUrl ()
  {
    return ProLogixGpibEthernetControllerTypeHandler.PROLOGIX_URL_PREFIX + this.ipAddress + ":" + this.tcpConnectPort;
  }
  
  public final synchronized void resetController ()
  {
    if (this.socket == null)
      LOG.log (Level.WARNING, "No ProLogix socket opened!");
    else
      try
      {
        LOG.log (Level.INFO, "Resetting ProLogix controller...");
        new PrintWriter (this.socket.getOutputStream (), true).println ("++rst");
        LOG.log (Level.INFO, "ProLogix controller has been reset (apparently).");
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "IOException: {0}.", ioe.getMessage ());
      }
  }
  
  private void switchCurrentDeviceAddress (final Socket socket, final GpibAddress address)
  throws IOException, InterruptedException
  {
    if (address == null)
      throw new IllegalArgumentException ();
    if (socket == null)
      throw new IOException ();
    String switchDeviceCommandString = "++addr " + Byte.toString (address.getPad ());
    if (address.hasSad ())
      switchDeviceCommandString += (" " + Byte.toString (address.getSad ()));
    new PrintWriter (this.socket.getOutputStream (), true).println (switchDeviceCommandString);
  }
  
  private byte[] readRaw (final GpibAddress address, final boolean ask)
  throws IOException, InterruptedException
  {
    // XXX This requires a lock on the object, which was more or less what we were trying to avoid...
    // XXX What about escaping 0x0a, 0x0d, ..., in order to prevent the Prologix from sending the array only partially?
    final Socket socket = this.socket; // Single assignment; object references are atomic.
    throw new UnsupportedOperationException ();
  }

  private byte[] readln (final GpibAddress address, final GpibDevice.ReadlineTerminationMode readlineTerminationMode, boolean ask)
  throws IOException, InterruptedException
  {
    final Socket socket = this.socket; // Single assignment; object references are atomic.
    if (socket == null)
      throw new IOException ();
    if (address == null)
      throw new IOException ();
    if (readlineTerminationMode == null)
      throw new IllegalArgumentException ();
    switchCurrentDeviceAddress (socket, address);
    if (ask)
      socket.getOutputStream ().write ("++read eoi".getBytes (Charset.forName ("US-ASCII")));
    final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    for (;;)
    {
      final int byteRead = socket.getInputStream ().read (); // 0-255 or negative in case of EOF.
      if (byteRead < 0)
        throw new IOException ();
      switch (readlineTerminationMode)
      {
        case CR:
        {
          if (byteRead == 0x0d)
            return baos.toByteArray ();
          else
            baos.write (byteRead);
          break;
        }
        case LF:
        {
          if (byteRead == 0x0a)
            return baos.toByteArray ();
          else
            baos.write (byteRead);
          break;
        }
        case CR_LF:
        {
          if (byteRead == 0x0d)
          {
            if (socket.getInputStream ().read () == 0x0a)
              return baos.toByteArray ();
            else
              throw new IOException ();
          }
          else
            baos.write (byteRead);
          break;
        }
        case LF_CR:
        {
          if (byteRead == 0x0a)
          {
            if (socket.getInputStream ().read () == 0x0d)
              return baos.toByteArray ();
            else
              throw new IOException ();
          }
          else
            baos.write (byteRead);
          break;
        }
        default:
          throw new RuntimeException ();
      }
    }
  }
  
  private byte[] readN (final GpibAddress address, final int N, boolean ask)
  throws IOException, InterruptedException
  {
    final Socket socket = this.socket; // Single assignment; object references are atomic.
    if (socket == null)
      throw new IOException ();
    if (address == null)
      throw new IOException ();
    if (N < 0)
      throw new IllegalArgumentException ();
    switchCurrentDeviceAddress (socket, address);
    if (ask)
      socket.getOutputStream ().write ("++read eoi".getBytes (Charset.forName ("US-ASCII")));
    final byte[] bytes = new byte[N];
    for (int i = 0; i < N; i++)
    {
      final int byteRead = socket.getInputStream ().read (); // 0-255 or negative in case of EOF.
      if (byteRead < 0)
        throw new IOException ();
      bytes[i] = (byte) byteRead;
    }
    return bytes;
  }
  
  private synchronized void writeRaw (final GpibAddress address, final byte[] bytes)
  throws IOException, InterruptedException
  {
    final Socket socket = this.socket; // Single assignment; object references are atomic.
    if (socket == null)
      throw new IOException ();
    if (address == null)
      throw new IOException ();
    switchCurrentDeviceAddress (socket, address);
    this.socket.getOutputStream ().write (bytes);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOCKET
  // SOCKET MANAGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private volatile Socket socket = null;
  
  private class SocketManager implements Runnable
  {
    @Override
    public final void run ()
    {
      LOG.log (Level.INFO, "Starting Socket Manager on {0}.", ProLogixGpibEthernetController.this);
      if (ProLogixGpibEthernetController.this.socket != null)
      {
        LOG.log (Level.SEVERE, "Inproper cleanup of socket!");
        try
        {
          ProLogixGpibEthernetController.this.socket.close ();
        }
        catch (IOException ioe)
        {
          LOG.log (Level.WARNING, "IOException while closing socket: {0}.", ioe.getStackTrace ());
        }
        finally
        {
          ProLogixGpibEthernetController.this.socket = null;
        }
      }
      boolean error = false;
      try
      {          
        final Socket socket =
          new Socket (ProLogixGpibEthernetController.this.ipAddress, ProLogixGpibEthernetController.this.tcpConnectPort);
        final PrintWriter out = new PrintWriter (socket.getOutputStream (), true);
        out.println ("++ifc");    // Become controller; Interface Clear on GPIB.
        out.println ("++auto 1");
        ProLogixGpibEthernetController.this.socket = socket;
      }
      catch (UnknownHostException uhe)
      {
        LOG.log (Level.WARNING, "UnknownHostException during open: {0}.", uhe.getStackTrace ());
        error = true;
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "IOException during open: {0}.", ioe.getStackTrace ());
        error = true;
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Exception during open: {0}.", e.getStackTrace ());
        error = true;
      }
      if (error)
      {
        LOG.log (Level.INFO, "Terminating Socket Manager (error) on {0}.", ProLogixGpibEthernetController.this);
        if (ProLogixGpibEthernetController.this.socket != null)
        {
          try
          {
            ProLogixGpibEthernetController.this.socket.close ();
          }
          catch (IOException ioe)
          {
            LOG.log (Level.WARNING, "IOException while closing socket: {0}.", ioe.getStackTrace ());
          }
          finally
          {
            ProLogixGpibEthernetController.this.socket = null;
          }
        }
        error ();
        return;
      }
      // Wait indefinitely for an interrupt.
      // XXX What if we already had one?
      try
      {
        Thread.currentThread ().join ();
      }
      catch (InterruptedException ie)
      {
        // EMPTY
      }
      try
      {
        ProLogixGpibEthernetController.this.socket.close ();
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "IOException while closing socket: {0}.", ioe.getStackTrace ());
      }
      finally
      {
        ProLogixGpibEthernetController.this.socket = null;
      }
      LOG.log (Level.INFO, "Terminated Socket Manager (by request) on {0}.", ProLogixGpibEthernetController.this);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROCESS COMMAND
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  @Override
  protected final void processCommand (final ControllerCommand controllerCommand)
    throws UnsupportedOperationException, IOException, InterruptedException
  {
    if (controllerCommand == null)
      throw new IllegalArgumentException ();
    if (! controllerCommand.containsKey (ControllerCommand.CC_COMMAND_KEY))
      throw new IllegalArgumentException ();
    if (controllerCommand.get (ControllerCommand.CC_COMMAND_KEY) == null)
      throw new IllegalArgumentException ();
    if (! (controllerCommand.get (ControllerCommand.CC_COMMAND_KEY) instanceof String))
      throw new IllegalArgumentException ();
    final String commandString = (String) controllerCommand.get (ControllerCommand.CC_COMMAND_KEY);
    if (commandString.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    try
    {
      switch (commandString)
      {
        case ControllerCommand.CC_NOP_KEY:
          break;
        case ControllerCommand.CC_GET_SETTINGS_KEY:
        {
          throw new UnsupportedOperationException ();
          // break;
        }
        case GpibControllerCommand.CC_GPIB_READ:
        {
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_ADDRESS))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) instanceof GpibAddress))
            throw new IllegalArgumentException ();
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          controllerCommand.put (GpibControllerCommand.CCARG_GPIB_READ_BYTES, readRaw (address, true));
          break;
        }
        case GpibControllerCommand.CC_GPIB_READLN:
        {
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_ADDRESS))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) instanceof GpibAddress))
            throw new IllegalArgumentException ();
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE)
            instanceof GpibDevice.ReadlineTerminationMode))
            throw new IllegalArgumentException ();
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final GpibDevice.ReadlineTerminationMode readlineTerminationMode =
            (GpibDevice.ReadlineTerminationMode) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE);
          controllerCommand.put (GpibControllerCommand.CCARG_GPIB_READ_BYTES, readln (address, readlineTerminationMode, true));
          break;
        }
        case GpibControllerCommand.CC_GPIB_READ_N:
        {
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_ADDRESS))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) instanceof GpibAddress))
            throw new IllegalArgumentException ();
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_READ_N))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READ_N) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READ_N) instanceof Integer))
            throw new IllegalArgumentException ();
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final int N = (int) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READ_N);
          if (N < 0)
            throw new IllegalArgumentException ();
          controllerCommand.put (GpibControllerCommand.CCARG_GPIB_READ_BYTES, readN (address, N, true));
          break;
        }
        case GpibControllerCommand.CC_GPIB_WRITE:
        {
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_ADDRESS))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) instanceof GpibAddress))
            throw new IllegalArgumentException ();
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES).getClass ().isArray ()))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES).getClass ().getComponentType ()
            != byte.class)
            throw new IllegalArgumentException ();
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte[] bytes = (byte[]) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES);
          writeRaw (address, bytes);
          break;
        }
        case GpibControllerCommand.CC_GPIB_WRITE_AND_READ:
        {
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_ADDRESS))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) instanceof GpibAddress))
            throw new IllegalArgumentException ();
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES).getClass ().isArray ()))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES).getClass ().getComponentType ()
            != byte.class)
            throw new IllegalArgumentException ();
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte[] bytes = (byte[]) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES);
          writeRaw (address, bytes);
          controllerCommand.put (GpibControllerCommand.CCARG_GPIB_READ_BYTES, readRaw (address, true));
          break;
        }
        case GpibControllerCommand.CC_GPIB_WRITE_AND_READLN:
        {
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_ADDRESS))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) instanceof GpibAddress))
            throw new IllegalArgumentException ();
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES).getClass ().isArray ()))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES).getClass ().getComponentType ()
            != byte.class)
            throw new IllegalArgumentException ();
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE)
            instanceof GpibDevice.ReadlineTerminationMode))
            throw new IllegalArgumentException ();
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte[] bytes = (byte[]) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES);
          final GpibDevice.ReadlineTerminationMode readlineTerminationMode =
            (GpibDevice.ReadlineTerminationMode) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE);
          writeRaw (address, bytes);
          controllerCommand.put (GpibControllerCommand.CCARG_GPIB_READ_BYTES, readln (address, readlineTerminationMode, true));
          break;
        }
        case GpibControllerCommand.CC_GPIB_WRITE_AND_READ_N:
        {
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_ADDRESS))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS) instanceof GpibAddress))
            throw new IllegalArgumentException ();
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES).getClass ().isArray ()))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES).getClass ().getComponentType ()
            != byte.class)
            throw new IllegalArgumentException ();
          if (! controllerCommand.containsKey (GpibControllerCommand.CCARG_GPIB_READ_N))
            throw new IllegalArgumentException ();
          if (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READ_N) == null)
            throw new IllegalArgumentException ();
          if (! (controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READ_N) instanceof Integer))
            throw new IllegalArgumentException ();
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte[] bytes = (byte[]) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES);
          final int N = (int) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READ_N);
          if (N < 0)
            throw new IllegalArgumentException ();
          writeRaw (address, bytes);
          controllerCommand.put (GpibControllerCommand.CCARG_GPIB_READ_BYTES, readN (address, N, true));
          break;
        }
        default:
          throw new UnsupportedOperationException ();
      }
    }
    finally
    {
      // EMPTY
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
