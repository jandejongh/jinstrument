package org.javajdj.jinstrument.r1.controller.gpib.tcpip.prologix;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.r1.controller.gpib.GpibAddress;
import org.javajdj.jinstrument.r1.swing.util.JStatusBar;
import org.javajdj.jinstrument.r1.Controller;
import org.javajdj.jinstrument.r1.controller.gpib.GpibController;

/**
 *
 */
public final class ProLogixGpibEthernetController
  extends GpibController
  implements JStatusBar.StatusableItem, Controller
{

  private static final Logger LOG = Logger.getLogger (ProLogixGpibEthernetController.class.getName ());
  
  public ProLogixGpibEthernetController (final String ipAddress, final int tcpConnectPort)
  {
    if (ipAddress == null || tcpConnectPort < 0 || tcpConnectPort > 65535)
      throw new IllegalArgumentException ();
    this.ipAddress = ipAddress;
    this.tcpConnectPort = tcpConnectPort;
  }
  
  private final String ipAddress;
  
  private final int tcpConnectPort;
  
  private volatile Socket prologixSocket = null;
  
  @Override
  public final synchronized boolean isOK ()
  {
    return this.prologixSocket != null;
  }

  @Override
  public final synchronized void clickAction ()
  {
    if (this.prologixSocket == null)
      open ();
    else
      close ();
  }
  
  public final synchronized void resetController ()
  {
    if (this.prologixSocket == null)
      LOG.log (Level.WARNING, "No ProLogix socket opened!");
    else
      try
      {
        LOG.log (Level.INFO, "Resetting ProLogix controller...");
        new PrintWriter (this.prologixSocket.getOutputStream (), true).println ("++rst");
        LOG.log (Level.INFO, "ProLogix controller has been reset (apparently).");
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "IOException: {0}.", ioe.getMessage ());
      }
  }
  
  public synchronized void open ()
  {
    if (this.prologixSocket == null)
    {
      try
      {
        this.prologixSocket = new Socket (this.ipAddress, this.tcpConnectPort);
        final PrintWriter out = new PrintWriter (this.prologixSocket.getOutputStream (), true);
        out.println ("++ifc");    // Become controller; Interface Clear on GPIB.
        out.println ("++auto 1");
      }
      catch (UnknownHostException uhe)
      {
        LOG.log (Level.WARNING, "UnknownHostException during open: {0}.", uhe.getMessage ());
        this.prologixSocket = null;
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "IOException during open: {0}.", ioe.getMessage ());        
        this.prologixSocket = null;
      }
    }
  }
  
  @Override
  public synchronized void close ()
  {
    if (this.prologixSocket != null)
    {
      try
      {
        this.prologixSocket.close ();
      }
      catch (IOException ioe)
      {
        LOG.log (Level.WARNING, "IOException during close: {0}.", ioe.getMessage ());
      }
    }
    this.prologixSocket = null;
  }
  
  private volatile GpibAddress currentDeviceAddress = null;
  
  private volatile boolean currentDeviceAddressLocked = false;
  
  private synchronized void switchCurrentDeviceAddress (final GpibAddress address)
  throws IOException, InterruptedException
  {
    if (address == null)
      throw new IllegalArgumentException ();
    while (this.currentDeviceAddressLocked && ! address.equals (this.currentDeviceAddress))
      wait ();
    if (this.prologixSocket == null)
      throw new IOException ();
    if (! address.equals (this.currentDeviceAddress))
    {
      this.currentDeviceAddress = address;
      String switchDeviceCommandString = "++addr " + Byte.toString (address.getPad ());
      if (address.hasSad ())
        switchDeviceCommandString += (" " + Byte.toString (address.getSad ()));
      new PrintWriter (this.prologixSocket.getOutputStream (), true).println (switchDeviceCommandString);
      new PrintWriter (this.prologixSocket.getOutputStream (), true).println ("++auto 1");
    }
  }
  
  @Override
  protected final synchronized void lockGpibAddress (final GpibAddress address) throws IOException, InterruptedException
  {
    if (address == null)
      throw new IllegalArgumentException ();
    if (this.currentDeviceAddressLocked && address.equals (this.currentDeviceAddress))
      throw new IllegalArgumentException ();
    while (this.currentDeviceAddressLocked)
      wait ();
    switchCurrentDeviceAddress (address);
    this.currentDeviceAddressLocked = true;
  }

  @Override
  protected final synchronized boolean unlockGpibAddress (final GpibAddress address)
  {
    if (address == null)
      throw new IllegalArgumentException ();
    if (this.currentDeviceAddressLocked && address.equals (this.currentDeviceAddress))
    {
      this.currentDeviceAddressLocked = false;
      notifyAll ();
      return true;
    }
    else
      return false;
  }

  @Override
  protected final synchronized int read (final GpibAddress address)
  throws IOException, InterruptedException
  {
    if (this.prologixSocket == null)
      return -1;
    if (address == null)
      return -1;
    if (! address.equals (this.currentDeviceAddress))
      switchCurrentDeviceAddress (address);
    final int byteRead = this.prologixSocket.getInputStream ().read ();
    return byteRead;
  }

  @Override
  protected final synchronized void write (final GpibAddress address, final int b)
  throws IOException, InterruptedException
  {
    if (this.prologixSocket == null)
      throw new IOException ();
    if (address == null)
      throw new IOException ();
    if (! address.equals (this.currentDeviceAddress))
      switchCurrentDeviceAddress (address);
    this.prologixSocket.getOutputStream ().write (b);
  }
  
}
