/*
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.controller.gpib.prologix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.Bus;
import org.javajdj.jinstrument.BusType;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.ControllerCommand;
import org.javajdj.jinstrument.ControllerListener;
import org.javajdj.jinstrument.config.Configurable;
import org.javajdj.jinstrument.config.Configuration;
import org.javajdj.jinstrument.util.Util;
import org.javajdj.jinstrument.controller.gpib.AbstractGpibController;
import org.javajdj.jinstrument.controller.gpib.BusType_GPIB;
import org.javajdj.jinstrument.controller.gpib.DefaultGpibBus;
import org.javajdj.jinstrument.controller.gpib.GpibAddress;
import org.javajdj.jinstrument.controller.gpib.GpibControllerCommand;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.controller.gpib.ReadlineTerminationMode;

/** Implementation of {@link Controller} for the Prologix GPIB-ETHERNET controller.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class ProLogixGpibEthernetController
  extends AbstractGpibController
  implements Configurable<ProLogixGpibEthernetController>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (ProLogixGpibEthernetController.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static String SERVICE_NAME = "ProLogixGpibEthernetController";
  
  public final static int DEFAULT_TCP_PORT = 1234;
  
  public ProLogixGpibEthernetController (final String ipAddress, final int tcpConnectPort)
  {
    super (SERVICE_NAME, null, null);
    if (ipAddress.trim () == null || tcpConnectPort < 0 || tcpConnectPort > 65535)
      throw new IllegalArgumentException ();
    this.ipAddress = ipAddress.trim ();
    this.tcpConnectPort = tcpConnectPort;
    this.buses = new Bus[] {new DefaultGpibBus (getControllerUrl () + "/1")};
    addRunnable (new SocketManager ());
    addRunnable (new SocketReader ());
  }
  
  public ProLogixGpibEthernetController (final String ipAddress)
  {
    this (ipAddress, ProLogixGpibEthernetController.DEFAULT_TCP_PORT);
  }
  
  private static ProLogixGpibEthernetControllerConfiguration checkConfiguration (final Configuration configuration)
  {
    if (configuration == null || ! (configuration instanceof ProLogixGpibEthernetControllerConfiguration))
      throw new IllegalArgumentException ();
    return (ProLogixGpibEthernetControllerConfiguration) configuration;
  }
  
  public ProLogixGpibEthernetController (final Configuration configuration)
  {
    this (
      ProLogixGpibEthernetController.checkConfiguration (configuration).getIpAddress (),
      ProLogixGpibEthernetController.checkConfiguration (configuration).getTcpConnectPort ());
  }

  // XXX Really do not know how to set the proper generic types below (or in the declaration for that matter)...
  @Override
  public final Class getConfigurationType ()
  {
    return ProLogixGpibEthernetControllerConfiguration.class;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Controller
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getControllerUrl ()
  {
    return ProLogixGpibEthernetControllerType.PROLOGIX_GPIB_ETHERNET_CONTROLLER_TYPE_NAME
      + "://"
      + this.ipAddress
      + ":"
      + this.tcpConnectPort;
  }

  @Override
  public String toString ()
  {
    return getControllerUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Controller
  // BUS TYPE
  // BUSES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final BusType getBusType ()
  {
    return BusType_GPIB.getInstance ();
  }

  private final Bus[] buses;
  
  @Override
  public final Bus[] getBuses ()
  {
    return this.buses.clone ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // IP ADDRESS
  // TCP CONNECT PORT
  // SOCKET
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final String ipAddress;
  
  private final int tcpConnectPort;

  private volatile Socket socket = null;

  public final static long DEFAULT_COMMAND_PROCESSING_TIMEOUT_MS = 10000L;
  
  public final long getFallbackCommandProcessingTimeout_ms ()
  {
    return ProLogixGpibEthernetController.DEFAULT_COMMAND_PROCESSING_TIMEOUT_MS;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOCKET MANAGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final static int SOCKET_TIMEOUT_S = 1;
  
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
        ProLogixGpibEthernetController.this.socket = socket;
        ProLogixGpibEthernetController.this.socket.setSoTimeout (SOCKET_TIMEOUT_S);
        //
        // Below we preconfigure the ProLogix controller. Most configuration settings should NOT be changed
        // in the implementations of command processing, since various parts of that implementation
        // actually depend on (and blindly assume) these settings.
        // The only exception is "++eot_enable" which may be temporarily set for reception of (typically) binary blobs of data
        // from the device (like binary trace data), along with a suitable "eot_char" setting.
        // 
        proLogixCommandWritelnControllerCommand ("++mode 1");       // Controller Mode on ProLogix (instead of Device Mode).
        proLogixCommandWritelnControllerCommand ("++ifc");          // Become Controller-In-Charge on GPIB bus;
                                                                    // Interface Clear on GPIB.
        proLogixCommandWritelnControllerCommand ("++eoi 1");        // Enable EOI assertion with
                                                                    // the last character of any command sent over GPIB port.
                                                                    // (Believed/assumed to be IEEE-488 mandatory.)
        proLogixCommandWritelnControllerCommand ("++eos 3");        // Do not append anything (like CR/LF or whatever)
                                                                    // to the command
                                                                    // received from host (us).
                                                                    // This configuration is necessary
                                                                    // to allow for per-device termination
                                                                    // of commands sent.
                                                                    // In other words, clients (Device objects)
                                                                    // must add command termination themselves.
                                                                    // AND: the controller implementation (us) must escape
                                                                    // all CR, LF, ESC, and '+' characters.
        proLogixCommandWritelnControllerCommand ("++eot_enable 0"); // Do not append any character (to client) when EOI detected.
        proLogixCommandWritelnControllerCommand ("++auto 0");       // Do NOT automatically address instruments to talk
                                                                    // after sending them a command in order to read their response.
                                                                    // Meaning we must issue the read commands,
                                                                    // if applicable, ourselves.
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
      // At this point, opening the socket and writing to it (in order to pre-configure the controller) has succeeded.
      // and the 'socket' member has been set appropriately.
      // For now, we stay alive, do nothing but sleep, until interrupted.
      while (! Thread.currentThread ().isInterrupted ())
      {
        try
        {
          Thread.currentThread ().join (1000L);
        }
        catch (InterruptedException ie)
        {
          break;
        }
      }
      // At this point, we know we have been interrupted.
      // Try to close the socket to the client, report any errors while doing so,
      // but make sure the 'socket' member is set to null a posteriori.
      // Finally report termination of the Thread in the logs.
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
  // SOCKET READER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final LinkedBlockingQueue<Byte> readBytes = new LinkedBlockingQueue<> ();
  
  private class SocketReader implements Runnable
  {
    
    @Override
    public final void run ()
    {
      LOG.log (Level.INFO, "Starting Socket Reader on {0}.", ProLogixGpibEthernetController.this);
      while (! Thread.currentThread ().isInterrupted ())
      {
        try
        {
          // We allows ourselves a limited number of I/O Exceptions due to switching and initializing the
          // ProLogixGpibEthernetController.this.socket in SocketManager.
          // (We may have been started ahead of SocketManager...)
          int remainIOExceptions = 3;
          // References are atomic.
          // Note that ProLogixGpibEthernetController.this.socket is volatile!
          final Socket socket = ProLogixGpibEthernetController.this.socket;
          if (socket == null)
            Thread.sleep (1000L);
          else
          {
            try
            {
              final int byteRead = socket.getInputStream ().read ();
              // LOG.log (Level.FINE, "Read byte: {0}.", Util.bytesToHex (new byte[] {(byte) byteRead}));
              if (byteRead >= 0)
                if (! ProLogixGpibEthernetController.this.readBytes.offer ((byte) byteRead))
                  LOG.log (Level.WARNING, "Buffer overflow on read bytes!");
            }
            catch (SocketTimeoutException ste)
            {
              // Nothing to do here...
              // Provides opportunity to refresh the socket variable.
            }
            catch (IOException ioe)
            {
              if (--remainIOExceptions <= 0)
              {
                LOG.log (Level.WARNING, "Terminating Socket Reader on {0}: Too many I/O Exceptions!",
                  ProLogixGpibEthernetController.this);
                error ();
                return;
              }
              // Nothing to do here...
              // Provides opportunity to refresh the socket variable.
            }
          }
        }
        catch (InterruptedException ie)
        {
          LOG.log (Level.INFO, "Terminating Socket Reader (by request) on {0}.", ProLogixGpibEthernetController.this);
          return;
        }
      }
      LOG.log (Level.INFO, "Terminating Socket Reader (by request) on {0}.", ProLogixGpibEthernetController.this);
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROLOGIX READ RAW
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Reads a single byte from the ProLogix controller (through the internal buffer).
   * 
   * <p>
   * This is the lowest-level read access to the controller.
   * 
   * <p>
   * Data from the controller is read continuously by an internal service {@code Thread},
   * the Socket Reader,
   * and put into an internal buffer.
   * This methods reads a single byte from that buffer (or times out trying to).
   * 
   * @param timeout_ms The timeout in milliseconds.
   * 
   * @return The (next) byte read from the internal buffer.
   * 
   * @throws InterruptedException If we were interrupted while waiting for a byte to arrive from the socket.
   * @throws TimeoutException     If we waited for a next byte in vain for at least the provided timeout period.
   * 
   * @see #proLogixReadline
   * 
   */
  private byte proLogixReadByte (final long timeout_ms)
    throws InterruptedException, TimeoutException
  {
    final Byte byteRead = this.readBytes.poll (timeout_ms, TimeUnit.MILLISECONDS);
    if (byteRead == null)
      throw new TimeoutException ();
    return byteRead;
  }
  
  // Used for values returned by controller commands (i.e., not originating from the device).
  private static final ReadlineTerminationMode CONTROLLER_READLINE_TERMINATION_MODE = ReadlineTerminationMode.OPTCR_LF;
  
  /** Reads a single line from the ProLogix controller (through the internal buffer).
   * 
   * <p>
   * This method reads consecutive bytes from the input buffer until the line if properly terminated
   * as determined by the {@link ReadlineTerminationMode} or until a timeout occurs.
   * The termination character(s) is/are removed from returned byte array.
   * There is no additional processing (like escaped characters) on the input stream.
   * Also, the input buffer is not cleared in this method, and the controller is not
   * asked to read from a device whatsoever.
   * 
   * @param readlineTerminationMode The termination mode for the line (i.e., the {@link ReadlineTerminationMode}).
   * @param timeout_ms              The timeout (for reading the entire line) in milliseconds.
   * 
   * @return The line read as an array of bytes, excluding the termination character(s) (but which are written into the logs).
   * 
   * @throws IOException          If the bytes in the input stream (i.e., the buffer) are incorrectly formatted with
   *                                respect to the required termination mode.
   * @throws InterruptedException If this method was interrupted while waiting for a byte to arrive from the socket.
   * @throws TimeoutException     If reading the line took longer than the provided timeout period.
   * 
   * @see #proLogixReadByte
   * 
   */
  private byte[] proLogixReadline (
    final ReadlineTerminationMode readlineTerminationMode,
    final long timeout_ms)
    throws IOException, InterruptedException, TimeoutException
  {
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    final long deadline_millis = System.currentTimeMillis () + timeout_ms;
    final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    for (;;)
    {
      long now_millis = System.currentTimeMillis ();
      if (now_millis > deadline_millis)
      {
        if (baos.size () > 0)
          queueLogRx (Instant.now (), baos.toByteArray ());
        throw new TimeoutException ();
      }
      final byte byteRead = proLogixReadByte (deadline_millis - now_millis);
      now_millis = System.currentTimeMillis ();
      switch (readlineTerminationMode)
      {
        case CR:
        {
          if (byteRead == 0x0d)
          {
            final byte[] bytesRead = baos.toByteArray ();
            baos.write (byteRead);
            queueLogRx (Instant.now (), baos.toByteArray ());
            return bytesRead;
          }
          else
            baos.write (byteRead);
          break;
        }
        case LF:
        {
          if (byteRead == 0x0a)
          {
            final byte[] bytesRead = baos.toByteArray ();
            baos.write (byteRead);
            queueLogRx (Instant.now (), baos.toByteArray ());
            return bytesRead;
          }
          else
            baos.write (byteRead);
          break;
        }
        case CR_LF:
        {
          if (byteRead == 0x0d)
          {
            final byte nextByteRead = proLogixReadByte (deadline_millis - now_millis);
            if (nextByteRead == 0x0a)
            {
              final byte[] bytesRead = baos.toByteArray ();
              baos.write (byteRead);
              baos.write (nextByteRead);
              queueLogRx (Instant.now (), baos.toByteArray ());
              return bytesRead;
            }
            else
              throw new IOException ();
          }
          else
            baos.write (byteRead);
          break;
        }
        case OPTCR_LF:
        {
          switch (byteRead)
          {
            case 0x0d:
            {
              final byte nextByteRead = proLogixReadByte (deadline_millis - now_millis);
              if (nextByteRead == 0x0a)
              {
                final byte[] bytesRead = baos.toByteArray ();
                baos.write (byteRead);
                baos.write (nextByteRead);
                queueLogRx (Instant.now (), baos.toByteArray ());
                return bytesRead;
              }
              else
              {
                baos.write (byteRead);
                baos.write (nextByteRead);
                queueLogRx (Instant.now (), baos.toByteArray ());
                throw new IOException ();
              }
            }
            case 0x0a:
            {
              final byte[] bytesRead = baos.toByteArray ();
              baos.write (byteRead);
              queueLogRx (Instant.now (), baos.toByteArray ());
              return bytesRead;
            }
            default:
              baos.write (byteRead);
              break;
          }
          break;
        }
        case LF_CR:
        {
          if (byteRead == 0x0a)
          {
            final byte nextByteRead = proLogixReadByte (deadline_millis - now_millis);
            if (nextByteRead == 0x0d)
            {
              final byte[] bytesRead = baos.toByteArray ();
              baos.write (byteRead);
              baos.write (nextByteRead);
              queueLogRx (Instant.now (), baos.toByteArray ());
              return bytesRead;
            }
            else
            {
              baos.write (byteRead);
              baos.write (nextByteRead);
              queueLogRx (Instant.now (), baos.toByteArray ());
              throw new IOException ();
            }
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
  
  /** Clears the internal read buffer.
   * 
   * <p>
   * Bytes read that are drained by this method are reported on the log as drained bytes
   * as well as to {@link ControllerListener}s as received bytes.
   * 
   * @see ControllerListener#controllerLogEntry
   * @see ControllerListener.LogEntryType#LOG_RX
   * 
   */
  private void proLogixClearReadBuffer ()
  {
    final List<Byte> drainedBytes = new ArrayList<> ();
    this.readBytes.drainTo (drainedBytes);
    if (! drainedBytes.isEmpty ())
    {
      final byte[] bytes = new byte[drainedBytes.size ()];
      // Below yields an ArrayStoreException...
      // System.arraycopy (drainedBytes.toArray (), 0, bytes, 0, bytes.length);
      int i = 0;
      for (final Byte b : drainedBytes)
        bytes[i++] = b;
      LOG.log (Level.WARNING, "Draining bytes {0}.", Util.bytesToHex (bytes));
      queueLogRx (Instant.now (), bytes);
      queueLogMessage (Instant.now (), "Draining bytes " + Util.bytesToHex (bytes) + ".");
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROLOGIX WRITE RAW
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Sends a given byte array unprocessed to the ProLogix controller.
   * 
   * <p>
   * This is the lowest-level processCommand_write access to the controller.
   * 
   * <p>
   * Caller assumes responsibility of proper termination of the byte array if applicable,
   * as well as of escaping characters in the byte array in order to avoid them being swallowed by the controller.
   * 
   * <p>
   * In other words, the bytes provided are <i>exactly</i> the bytes sent to the controller,
   * but these bytes are then subject to processing at the controller
   * and my not, if applicable, be identical to the sequence of bytes sent to a (the currently addressed) GPIB device.
   * 
   * @param bytes The bytes to send, non-{@code null}.
   * 
   * @throws IllegalArgumentException If the argument is {@code null}.
   * @throws IOException              If the socket is {@code null},
   *                                    or if writing to the socket failed with an {@link IOException}.
   * @throws InterruptedException     If the {@link Thread} was interrupted while writing to the controller socket.
   * 
   */
  private void proLogixWriteRaw (final byte[] bytes)
    throws IOException, InterruptedException
  {
    if (bytes == null)
      throw new IllegalArgumentException ();
    queueLogTx (Instant.now (), bytes);
    final Socket socket = this.socket; // Single assignment; object references are atomic.
    if (socket == null)
      throw new IOException ();
    this.socket.getOutputStream ().write (bytes);
  }
    
  /** Sends a given (ASCII) string unprocessed to the ProLogix controller.
   * 
   * <p>
   * Converts the {@code String} to a (ASCII) byte array and invokes {@link #proLogixWriteRaw(byte[])}.
   * 
   * @param string The string to send, non-{@code null}.
   * 
   * @throws IllegalArgumentException If the argument is {@code null}.
   * @throws IOException              If the socket is {@code null},
   *                                    or if writing to the socket failed with an {@link IOException}.
   * @throws InterruptedException     If the {@link Thread} was interrupted while writing to the controller socket.
   * 
   * @see #proLogixWriteRaw(byte[])
   * 
   */
  private void proLogixWriteRaw (final String string)
    throws IOException, InterruptedException
  {
    if (string == null)
      throw new IllegalArgumentException ();
    proLogixWriteRaw (string.getBytes (Charset.forName ("US-ASCII")));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROLOGIX WRITE COOKED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static byte LF_BYTE   = (byte) 10;
  private final static byte CR_BYTE   = (byte) 13;
  private final static byte ESC_BYTE  = (byte) 27;
  private final static byte PLUS_BYTE = (byte) 43;
  
  private final static byte COMMAND_TERMINATOR_TO_PROLOGIX = LF_BYTE;
  
  /** Prepare (cook) a string (as byte array) for transmission to the ProLogix Controller.
   * 
   * <p>
   * This method 'escapes' those ASCII characters (i.e., line feed, carriage return, escape and '+')
   * that are otherwise swallowed by the ProLogix Controller.
   * It then appends an not-escaped {@link #COMMAND_TERMINATOR_TO_PROLOGIX} signaling the
   * end of the string to the controller. This latter byte will be swallowed by the controller, but is essential to
   * indicate the end of the command string (since we just escaped all other potential end-of-command characters).
   * 
   * <p>
   * This method assumes the '++eos 3' command-string termination mode, meaning that the controller does <i>not</i> append
   * any termination character whatsoever before sending the string to the GPIB device. It thus becomes the responsibility of
   * the {@link Controller} client (typically, a {@link GpibDevice} and this controller's implementation.
   * 
   * @param bytes The (command) string (as byte array).
   * 
   * @return The prepared (command) string (as byte array).
   * 
   * @throws IllegalArgumentException If the argument is {@code null}.
   * 
   */
  private static byte[] proLogixCookString (final byte[] bytes)
  {
    if (bytes == null)
      throw new IllegalArgumentException ();
    final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    for (final byte b : bytes)
      switch (b)
      {
        case LF_BYTE:
        case CR_BYTE:
        case ESC_BYTE:
        case PLUS_BYTE:
          // Insert ESC character to avoid swallowing of the character by the ProLogix controller.
          baos.write (ESC_BYTE);
        default:
          baos.write (b);
      }
    // Append a 'end-of-command' indication; this WILL be swallowed by the ProLogix controller.
    baos.write (COMMAND_TERMINATOR_TO_PROLOGIX);
    return baos.toByteArray ();
  }
  
  /** Cooks the given argument and writes it to the ProLogix controller.
   * 
   * <p>
   * Invokes {@link #proLogixWriteRaw(byte[]) after cooking the byte array
   * with {@link #proLogixCookString(byte[])}.
   * 
   * @param bytes The string to (cook and) write (as byte array).
   * 
   * @throws IllegalArgumentException If the argument is {@code null}.
   * @throws IOException              If the socket is {@code null},
   *                                    or if writing to the socket failed with an {@link IOException}.
   * @throws InterruptedException     If the {@link Thread} was interrupted while writing to the controller socket.
   * 
   */
  private void proLogixWriteCooked (final byte[] bytes)
    throws IOException, InterruptedException
  {
    proLogixWriteRaw (proLogixCookString (bytes));    
  }
  
  /** Cooks the given argument and writes it to the ProLogix controller.
   * 
   * <p>
   * Converts the {@code String} to a (ASCII) byte array and invokes {@link #proLogixWriteCooked(byte[])}.
   * 
   * @param string The string to (cook and) send, non-{@code null}.
   * 
   * @throws IllegalArgumentException If the argument is {@code null}.
   * @throws IOException              If the socket is {@code null},
   *                                    or if writing to the socket failed with an {@link IOException}.
   * @throws InterruptedException     If the {@link Thread} was interrupted while writing to the controller socket.
   * 
   * @see #proLogixWriteCookedBinary(byte[])
   * 
   */
  private void proLogixWriteCooked (final String string)
    throws IOException, InterruptedException
  {
    if (string == null)
      throw new IllegalArgumentException ();
    proLogixWriteCooked (string.getBytes (Charset.forName ("US-ASCII")));    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROLOGIX WRITE / WRITELN CONTROLLER COMMAND
  // PROLIGIX SWITCH CURRENT ADDRESS
  // PROLOGIX SET READ AFTER WRITE
  // PROLOGIX SET EOT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private void proLogixCommandWritelnControllerCommand (final String command)
    throws IOException, InterruptedException
  {
    if (command == null || command.length () < 2)
      throw new IllegalArgumentException ();
    if (! command.startsWith ("++"))
      throw new IllegalArgumentException ();
    proLogixWriteRaw (command + new String (new byte[]{COMMAND_TERMINATOR_TO_PROLOGIX}, Charset.forName ("US-ASCII")));
  }
  
  private void proLogixCommandSwitchCurrentDeviceAddress (final GpibAddress address)
  throws IOException, InterruptedException
  {
    if (address == null)
      throw new IllegalArgumentException ();
    String switchDeviceCommandString = "++addr " + Byte.toString (address.getPad ());
    if (address.hasSad ())
      switchDeviceCommandString += (" " + Byte.toString (address.getSad ()));
    proLogixCommandWritelnControllerCommand (switchDeviceCommandString);
  }

  private void proLogixCommandSetEOT (final boolean eotEnable, final byte eotChar)
    throws IOException, InterruptedException
  {
    proLogixCommandWritelnControllerCommand ("++eot_enable " + (eotEnable ? "1" : "0"));  
    proLogixCommandWritelnControllerCommand ("++eot_char " + Integer.toString (eotChar & 0xff));  
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROCESS SUPPORTED COMMANDS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final String RESET_CONTROLLER_COMMAND      = "++rst";
  private final String SELECTED_DEVICE_CLEAR_COMMAND = "++clr";
  private final String SERVICE_REQUEST_COMMAND       = "++srq";
  private final String SERIAL_POLL_COMMAND           = "++spoll";
  private final String READ_COMMAND                  = "++read";
  private final String READ_EOI_COMMAND              = "++read eoi";
  private final String READ_LF_COMMAND               = "++read 10";
  private final String READ_CR_COMMAND               = "++read 13";
  
  private final byte DEFAULT_EOT = (byte) 0xFF;
  
  private void processCommand_resetController (
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    proLogixCommandWritelnControllerCommand (RESET_CONTROLLER_COMMAND);    
  }
  
  private void processCommand_nop (
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (timeout_ms <= 0)
      throw new TimeoutException ();
  }
  
  private void processCommand_selectedDeviceClear (
    final GpibAddress gpibAddress,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null)
      throw new IllegalArgumentException ();
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    proLogixCommandWritelnControllerCommand (SELECTED_DEVICE_CLEAR_COMMAND);
  }
  
  private boolean processCommand_pollServiceRequestFromAny (final long timeout_ms)
    throws IOException, InterruptedException, TimeoutException
  {
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    final long now_millis = System.currentTimeMillis ();
    final long deadline_millis = now_millis + timeout_ms;
    proLogixClearReadBuffer ();
    proLogixCommandWritelnControllerCommand (SERVICE_REQUEST_COMMAND);
    final String srqString = new String (proLogixReadline (
      ProLogixGpibEthernetController.CONTROLLER_READLINE_TERMINATION_MODE,
      deadline_millis - System.currentTimeMillis ()), Charset.forName ("US-ASCII"));
    switch (srqString)
    {
      case "0":
        return false;
      case "1":
        return true;
      default:
        throw new IOException ();
    }    
  }
  
  private boolean processCommand_pollServiceRequest (
    final GpibAddress gpibAddress,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    long now_millis = System.currentTimeMillis ();
    final long deadline_millis = now_millis + timeout_ms;
    // Do a quick check on the SRQ status.
    // If no device has SRQ activated, we are already done.
    if (! processCommand_pollServiceRequestFromAny (timeout_ms))
      return false;
    // At this point we know that SOME device has activated SRQ.
    if (gpibAddress == null)
      return true;
    now_millis = System.currentTimeMillis ();
    if (now_millis > deadline_millis)
      throw new TimeoutException ();
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    now_millis = System.currentTimeMillis ();
    if (now_millis > deadline_millis)
      throw new TimeoutException ();
    // ANSI/IEEE Standard 488.1-1987: Bit 6 in Serial Poll Status Byte is SRQ.
    return (processCommand_serialPoll (gpibAddress, deadline_millis - now_millis, false) & 0x40) != 0;
  }
  
  private byte processCommand_serialPoll (
    final GpibAddress gpibAddress,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null)
      throw new IllegalArgumentException ();
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    proLogixCommandWritelnControllerCommand (SERIAL_POLL_COMMAND);
    final String statusByteString = new String (processCommand_readln (gpibAddress,
      ProLogixGpibEthernetController.CONTROLLER_READLINE_TERMINATION_MODE,
      timeout_ms,
      false,
      false), Charset.forName ("US-ASCII"));
    try
    {
      final int statusByteInt = Integer.parseInt (statusByteString);
      return (byte) (statusByteInt & 0xff);
    }
    catch (NumberFormatException nfe)
    {
      throw new IOException (nfe);
    }
  }
  
  private Byte processCommand_pollServiceRequestStatusByte (
    final GpibAddress gpibAddress,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    long now_millis = System.currentTimeMillis ();
    final long deadline_millis = now_millis + timeout_ms;
    // Do a quick check on the SRQ status.
    // If no device has SRQ activated, we are already done.
    if (! processCommand_pollServiceRequestFromAny (timeout_ms))
      return null;
    // At this point we know that SOME device has activated SRQ.
    // We must perform a Serial Poll on the selected device in order
    // to figure out if that device caused the SRQ.
    now_millis = System.currentTimeMillis ();
    if (now_millis > deadline_millis)
      throw new TimeoutException ();
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    now_millis = System.currentTimeMillis ();
    if (now_millis > deadline_millis)
      throw new TimeoutException ();
    final byte serialPollStatusByte = processCommand_serialPoll (gpibAddress, deadline_millis - now_millis, false);
    // ANSI/IEEE Standard 488.1-1987: Bit 6 in Serial Poll Status Byte is SRQ.
    return (serialPollStatusByte & 0x40) != 0 ? serialPollStatusByte : null;
  }
  
  private final static long EOI_LINGER_MS = 50L;
  
  private byte[] processCommand_readEOI (
    final GpibAddress gpibAddress,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null)
      throw new IllegalArgumentException ();
    if (timeout_ms <= EOI_LINGER_MS)
      throw new TimeoutException ();
    final long deadline_millis = System.currentTimeMillis () + timeout_ms - EOI_LINGER_MS;
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (true, DEFAULT_EOT);
    }
    proLogixCommandWritelnControllerCommand (READ_EOI_COMMAND);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    for (;;)
    {
      long now_millis = System.currentTimeMillis ();
      if (now_millis >= deadline_millis)
      {
        if (baos.size () > 0)
          queueLogRx (Instant.now (), baos.toByteArray ());
        throw new TimeoutException ();
      }
      byte byteRead = proLogixReadByte (deadline_millis - now_millis);
      while (byteRead == DEFAULT_EOT)
      {
        if (System.currentTimeMillis () >= deadline_millis)
        {
          baos.write (byteRead);
          queueLogRx (Instant.now (), baos.toByteArray ());      
          throw new TimeoutException ();
        }
        try
        {
          final byte nextByteRead = proLogixReadByte (EOI_LINGER_MS);
          baos.write (byteRead);
          byteRead = nextByteRead;
        }
        catch (TimeoutException te)
        {
          final byte[] bytesRead = baos.toByteArray ();
          baos.write (byteRead); // DEFALT_EOT; for proper Rx Logging...
          queueLogRx (Instant.now (), baos.toByteArray ());
          return bytesRead;
        }
      }
      baos.write (byteRead);
    }
  }
  
  private byte[] processCommand_readln (
    final GpibAddress gpibAddress,
    final ReadlineTerminationMode readlineTerminationMode,
    final long timeout_ms,
    final boolean topLevel,
    final boolean ask)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null || readlineTerminationMode == null)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    final long deadline_millis = System.currentTimeMillis () + timeout_ms;
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    if (ask)
    {
      switch (readlineTerminationMode)
      {
        case CR:
        case LF_CR:
          proLogixCommandWritelnControllerCommand (READ_CR_COMMAND);
          break;
        case LF:
        case CR_LF:
        case OPTCR_LF:
          proLogixCommandWritelnControllerCommand (READ_LF_COMMAND);
          break;
        default:
          throw new RuntimeException ();
      }
    }
    return proLogixReadline (readlineTerminationMode, timeout_ms);
  }
  
  private byte[] processCommand_readN (
    final GpibAddress gpibAddress,
    final int N,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null || N < 0)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    final long deadline_millis = System.currentTimeMillis () + timeout_ms;
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    proLogixCommandWritelnControllerCommand (READ_EOI_COMMAND);
    // proLogixCommandWritelnControllerCommand (READ_COMMAND);
    final byte[] bytes = new byte[N];
    for (int i = 0; i < N; i++)
    {
      try
      {
        long now_millis = System.currentTimeMillis ();
        if (now_millis >= deadline_millis)
          throw new TimeoutException ();
        bytes[i] = proLogixReadByte (deadline_millis - now_millis);
      }
      catch (Exception e)
      {
        if (i > 0)
        {
          final byte[] bytesRead = new byte[i];
          System.arraycopy (bytes, 0, bytesRead, 0, i);
          queueLogRx (Instant.now (), bytesRead);
        }
        throw (e);
      }
    }
    queueLogRx (Instant.now (), bytes);
    return bytes;
  }
  
  private void processCommand_write (
    final GpibAddress gpibAddress,
    final byte[] bytes,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
    }
    // proLogixWriteRaw (bytes);
    proLogixWriteCooked (bytes);
  }
  
  private byte[] processCommand_writeAndReadEOI (
    final GpibAddress gpibAddress,
    final byte[] bytes,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    final long deadline_millis = System.currentTimeMillis () + timeout_ms;
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (true, DEFAULT_EOT);
    }
    processCommand_write (gpibAddress, bytes, timeout_ms, false); // Will do the cooking...
    return processCommand_readEOI (gpibAddress, deadline_millis - System.currentTimeMillis (), false);
  }
  
  private byte[] processCommand_writeAndReadln (
    final GpibAddress gpibAddress,
    final byte[] bytes,
    final ReadlineTerminationMode readlineTerminationMode,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    final long deadline_millis = System.currentTimeMillis () + timeout_ms;
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    processCommand_write (gpibAddress, bytes, timeout_ms, false); // Will do the cooking...
    return processCommand_readln (
      gpibAddress,
      readlineTerminationMode,
      deadline_millis - System.currentTimeMillis (),
      false,
      true);
  }
  
  private byte[] processCommand_writeAndReadN (
    final GpibAddress gpibAddress,
    final byte[] bytes,
    final int N,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null || N < 0)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    final long deadline_millis = System.currentTimeMillis () + timeout_ms;
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    processCommand_write (gpibAddress, bytes, timeout_ms, false); // Will do the cooking...
    return processCommand_readN (gpibAddress, N, deadline_millis - System.currentTimeMillis (), false);
  }
  
  private byte[][] processCommand_writeAndReadlnN (
    final GpibAddress gpibAddress,
    final byte[] bytes,
    final ReadlineTerminationMode readlineTerminationMode,
    final int N,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null || N < 0)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    final long deadline_millis = System.currentTimeMillis () + timeout_ms;
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    final byte[][] bytesRead = new byte[N][];
    processCommand_write (gpibAddress, bytes, timeout_ms, false); // Will do the cooking...
    proLogixCommandWritelnControllerCommand (READ_COMMAND);
    for (int n = 0; n < N; n++)
      bytesRead[n] = processCommand_readln (
        gpibAddress, readlineTerminationMode, deadline_millis - System.currentTimeMillis (), false, false);
    return bytesRead;
  }
  
  private void processCommand_atomicSequence (
    final GpibAddress gpibAddress,
    final ControllerCommand[] sequence,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    // If there is no sequence, we are already done...
    if (sequence == null)
      return;
    final long deadline_millis = System.currentTimeMillis () + timeout_ms;
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    for (final ControllerCommand controllerCommand : sequence)
      processCommand (controllerCommand, deadline_millis - System.currentTimeMillis (), false);
  }
  
  private void processCommand_atomicRepeatUntil (
    final GpibAddress gpibAddress,
    final GpibControllerCommand commandToRepeat,
    final Function<GpibControllerCommand, Boolean> condition,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    // If there is no command to repeat, we are already finished.
    if (commandToRepeat == null)
      return;
    final long deadline_millis = System.currentTimeMillis () + timeout_ms;
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    boolean conditionMet = true;
    while (conditionMet)
    {
      processCommand (commandToRepeat, deadline_millis - System.currentTimeMillis (), false);
      conditionMet = (condition == null || condition.apply (commandToRepeat));
    }
  }
  
  private void processCommand_userRunnable (
    final GpibAddress gpibAddress,
    final Runnable runnable,
    final long timeout_ms,
    final boolean topLevel)
    throws IOException, InterruptedException, TimeoutException
  {
    if (gpibAddress == null)
      throw new IllegalArgumentException ();
    if (timeout_ms <= 0)
      throw new TimeoutException ();
    final long deadline_millis = System.currentTimeMillis () + timeout_ms;
    if (topLevel)
    {
      proLogixClearReadBuffer ();
      proLogixCommandSwitchCurrentDeviceAddress (gpibAddress);
      proLogixCommandSetEOT (false, LF_BYTE);
    }
    runnable.run ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AbstractController
  // PROCESS COMMAND
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  @Override
  protected final void processCommand (final ControllerCommand controllerCommand, final long timeout_ms)
    throws UnsupportedOperationException, IOException, InterruptedException, TimeoutException
  {
    processCommand (controllerCommand, timeout_ms, true);
  }
  
  protected final void processCommand (final ControllerCommand controllerCommand, final long timeout_ms, final boolean topLevel)
    throws UnsupportedOperationException, IOException, InterruptedException, TimeoutException
  {
    final String commandString = (String) controllerCommand.get (ControllerCommand.CC_COMMAND_KEY);
    try
    {
      if (timeout_ms <= 0)
        throw new TimeoutException ();
      switch (commandString)
      {
        case ControllerCommand.CCCMD_RESET_CONTROLLER:
          processCommand_resetController (timeout_ms, topLevel);
          break;
        case ControllerCommand.CCCMD_NOP:
          processCommand_nop (timeout_ms, topLevel);
          break;
        case ControllerCommand.CCCMD_GET_SETTINGS:
          throw new UnsupportedOperationException ();
        case GpibControllerCommand.CCCMD_GPIB_SELECTED_DEVICE_CLEAR:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          processCommand_selectedDeviceClear (address, timeout_ms, topLevel);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_POLL_SERVICE_REQUEST:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final boolean srq = processCommand_pollServiceRequest (address, timeout_ms, topLevel);
          controllerCommand.put (GpibControllerCommand.CCRET_VALUE_KEY, srq);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_SERIAL_POLL:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte statusByte = processCommand_serialPoll (address, timeout_ms, topLevel);
          controllerCommand.put (GpibControllerCommand.CCRET_VALUE_KEY, statusByte);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_POLL_SERVICE_REQUEST_STATUS_BYTE:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final Byte statusByte = processCommand_pollServiceRequestStatusByte (address, timeout_ms, topLevel);
          controllerCommand.put (GpibControllerCommand.CCRET_VALUE_KEY, statusByte);
          break;          
        }
        case GpibControllerCommand.CCCMD_GPIB_READ_EOI:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte[] bytesRead = processCommand_readEOI (address, timeout_ms, topLevel);
          controllerCommand.put (GpibControllerCommand.CCRET_VALUE_KEY, bytesRead);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_READLN:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final ReadlineTerminationMode readlineTerminationMode =
            (ReadlineTerminationMode) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE);
          final byte[] bytesRead = processCommand_readln (address, readlineTerminationMode, timeout_ms, topLevel, true);
          controllerCommand.put (GpibControllerCommand.CCRET_VALUE_KEY, bytesRead);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_READ_N:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final int N = (int) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READ_N);
          final byte[] bytesRead = processCommand_readN (address, N, timeout_ms, topLevel);
          controllerCommand.put (GpibControllerCommand.CCRET_VALUE_KEY, bytesRead);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_WRITE:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte[] bytes = (byte[]) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES);
          processCommand_write (address, bytes, timeout_ms, topLevel);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_WRITE_AND_READ_EOI:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte[] bytes = (byte[]) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES);
          final byte[] bytesRead = processCommand_writeAndReadEOI (address, bytes, timeout_ms, topLevel);
          controllerCommand.put (GpibControllerCommand.CCRET_VALUE_KEY, bytesRead);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_WRITE_AND_READLN:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte[] bytes = (byte[]) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES);
          final ReadlineTerminationMode readlineTerminationMode =
            (ReadlineTerminationMode) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE);
          final byte[] bytesRead = processCommand_writeAndReadln (address, bytes, readlineTerminationMode, timeout_ms, topLevel);
          controllerCommand.put (GpibControllerCommand.CCRET_VALUE_KEY, bytesRead);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_WRITE_AND_READ_N:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte[] bytes = (byte[]) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES);
          final int N = (int) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READ_N);
          final byte[] bytesRead = processCommand_writeAndReadN (address, bytes, N, timeout_ms, topLevel);
          controllerCommand.put (GpibControllerCommand.CCRET_VALUE_KEY, bytesRead);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_WRITE_AND_READLN_N:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final byte[] bytes = (byte[]) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_WRITE_BYTES);
          final ReadlineTerminationMode readlineTerminationMode =
            (ReadlineTerminationMode) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READLN_TERMINATION_MODE);
          final int N = (int) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_READ_N);
          final byte[][] bytesRead = processCommand_writeAndReadlnN (
            address, bytes, readlineTerminationMode, N, timeout_ms, topLevel);
          controllerCommand.put (GpibControllerCommand.CCRET_VALUE_KEY, bytesRead);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_ATOMIC_SEQUENCE:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final Object sequence = controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ATOMIC_SEQUENCE);
          final ControllerCommand[] controllerCommands = new ControllerCommand[Array.getLength (sequence)];
          System.arraycopy (sequence, 0, controllerCommands, 0, controllerCommands.length);
          processCommand_atomicSequence (address, controllerCommands, timeout_ms, topLevel);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_ATOMIC_REPEAT_UNTIL:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final GpibControllerCommand commandToRepeat =
            (GpibControllerCommand) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ATOMIC_REPEAT_UNTIL_COMMAND);
          final Function<GpibControllerCommand, Boolean> condition =
            (Function) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ATOMIC_REPEAT_UNTIL_CONDITION);
          processCommand_atomicRepeatUntil (address, commandToRepeat, condition, timeout_ms, topLevel);
          break;
        }
        case GpibControllerCommand.CCCMD_GPIB_USER_RUNNABLE:
        {
          final GpibAddress address = (GpibAddress) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_ADDRESS);
          final Runnable runnable = (Runnable) controllerCommand.get (GpibControllerCommand.CCARG_GPIB_USER_RUNNABLE);
          processCommand_userRunnable (address, runnable, timeout_ms, topLevel);
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
