package org.javajdj.jinstrument.r1.controller.gpib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.javajdj.jinstrument.r1.Controller;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.support.Service_FromMix;

/**
 *
 */
public abstract class AbstractGpibController
extends Service_FromMix
implements Controller
{

  private static final Logger LOG = Logger.getLogger (AbstractGpibController.class.getName ());

  public AbstractGpibController (final String name, final List<Runnable> runnables, final List<Service> targetServices)
  {
    super (name, runnables, targetServices);
  }
  
  protected final GpibAddress createAddress (final String deviceUrl)
  {
    if (deviceUrl == null || ! deviceUrl.trim ().toLowerCase ().startsWith ("gpib:"))
      return null;
    final String gpibAddressString = deviceUrl.trim ().replaceFirst (".*:", "");
    final String padString;
    final String sadString;
    if (gpibAddressString.contains (","))
    {
      padString = gpibAddressString.replaceFirst (",.*", "");
      sadString = gpibAddressString.replaceFirst (".*,", "");
    }
    else
    {
      padString = gpibAddressString;
      sadString = null;
    }
    try
    {
      final int pad = Integer.parseInt (padString);
      if (pad < 0 || pad > 30)
        return null;
      if (sadString != null)
      {
        final int sad = Integer.parseInt (sadString);
        if (sad != 0 && (sad < GpibAddress.MIN_NON_ZERO_SAD || sad > GpibAddress.MAX_NON_ZERO_SAD))
          return null;
        return new GpibAddress ((byte) pad, (byte) sad);
      }
      else
        return new GpibAddress ((byte) pad);
    }
    catch (NumberFormatException nfe)
    {
      return null;
    }
    
  }
  
  @Override
  public final boolean isDeviceUrl (final String deviceUrl)
  {
    return createAddress (deviceUrl) != null;
  }

  @Override
  public final synchronized GpibDevice openDevice (final String deviceUrl)
  {
    final GpibAddress address = createAddress (deviceUrl);
    if (address == null)
      return null;
    if (this.devices.containsKey (address))
      return null;
    final GpibDevice device = new GpibDevice (this, address);
    this.devices.put (address, device);
    return device;
  }
  
  protected abstract int read (GpibAddress address) throws IOException, InterruptedException;
  
  protected abstract void write (GpibAddress address, int b) throws IOException, InterruptedException;

  protected abstract void lockGpibAddress (GpibAddress address) throws IOException, InterruptedException;
  
  protected abstract boolean unlockGpibAddress (GpibAddress address);
  
  private synchronized int read (final GpibDeviceInputStream inputStream)
  throws IOException, InterruptedException
  {
    if (inputStream == null)
      throw new IllegalArgumentException ();
    final GpibDevice device = inputStream.device;
    if (! isRegisteredDevice (device))
      return -1;
    if (this.inputStreams.get (device) != inputStream)
      return -1;
    return read (device.getAddress ());
  }

  private synchronized void write (final GpibDeviceOutputStream outputStream, final int b)
  throws IOException, InterruptedException
  {
    if (outputStream == null)
      throw new IllegalArgumentException ();
    final GpibDevice device = outputStream.device;
    if (! isRegisteredDevice (device))
      throw new IOException ();
    if (this.outputStreams.get (device) != outputStream)
      throw new IOException ();
    write (device.getAddress (), b);
  }
  
  private final Map<GpibDevice, GpibDeviceInputStream> inputStreams = new LinkedHashMap<> ();
  private final Map<GpibDevice, GpibDeviceOutputStream> outputStreams = new LinkedHashMap<> ();
  
  protected final Map<GpibAddress, GpibDevice> devices = new LinkedHashMap<>  ();

  private synchronized boolean isRegisteredDevice (final GpibDevice device)
  {
    return this.devices.values ().contains (device);
  }
  
  public final synchronized InputStream getInputStream (final GpibDevice device)
  {
    if (device == null)
      throw new IllegalArgumentException ();
    if (! this.inputStreams.containsKey (device))
      this.inputStreams.put (device, new GpibDeviceInputStream (device));
    return this.inputStreams.get (device);
  }
  
  private synchronized void closeInputStream (final GpibDeviceInputStream inputStream)
  {
    if (inputStream == null)
      throw new IllegalArgumentException ();
    this.inputStreams.remove (inputStream.device);
  }
  
  public final synchronized OutputStream getOutputStream (final GpibDevice device)
  {
    if (device == null)
      throw new IllegalArgumentException ();
    if (! this.outputStreams.containsKey (device))
      this.outputStreams.put (device, new GpibDeviceOutputStream (device));
    return this.outputStreams.get (device);
  }
  
  private synchronized void closeOutputStream (final GpibDeviceOutputStream outputStream)
  {
    if (outputStream == null)
      throw new IllegalArgumentException ();
    this.outputStreams.remove (outputStream.device);
  }
  
  private class GpibDeviceInputStream
  extends InputStream
  {

    public GpibDeviceInputStream (final GpibDevice device)
    {
      if (device == null)
        throw new IllegalArgumentException ();
      this.device = device;
    }

    private final GpibDevice device;
    
    private boolean closed = false;
    
    @Override
    public int read () throws IOException
    {
      if (this.closed)
        return -1;
      try
      {
        return AbstractGpibController.this.read (this);      
      }
      catch (InterruptedException ie)
      {
        throw new IOException (ie);
      }
    }

    @Override
    public void close () throws IOException
    {
      this.closed = true;
      AbstractGpibController.this.closeInputStream (this);
    }
    
  }
  
  private class GpibDeviceOutputStream
  extends OutputStream
  {

    public GpibDeviceOutputStream (final GpibDevice device)
    {
      if (device == null)
        throw new IllegalArgumentException ();
      this.device = device;
    }
    
    private final GpibDevice device;

    private boolean closed = false;
    
    @Override
    public void write (final int b) throws IOException
    {
      if (this.closed)
        throw new IOException ();
      try
      {
        AbstractGpibController.this.write (this, b);      
      }
      catch (InterruptedException ie)
      {
        throw new IOException (ie);
      }
    }
    
    @Override
    public void close () throws IOException
    {
      this.closed = true;
      AbstractGpibController.this.closeOutputStream (this);
    }
    
  }
  
}
