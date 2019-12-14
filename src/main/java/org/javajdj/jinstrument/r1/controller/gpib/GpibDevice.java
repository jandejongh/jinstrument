package org.javajdj.jinstrument.r1.controller.gpib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.javajdj.jinstrument.r1.Device;

/**
 *
 */
public class GpibDevice
implements Device
{

  public GpibDevice (final AbstractGpibController controller, final GpibAddress address)
  {
    if (controller == null || address == null)
      throw new IllegalArgumentException ();
    this.controller = controller;
    this.address = address;
  }

  private final AbstractGpibController controller;
  
  public final AbstractGpibController getController ()
  {
    return this.controller;
  }
  
  private final GpibAddress address;
  
  public final GpibAddress getAddress ()
  {
    return this.address;
  }
  
  public final InputStream getInputStream ()
  {
    return this.controller.getInputStream (this);
  }
  
  public final OutputStream getOutputStream ()
  {
    return this.controller.getOutputStream (this);
  }
  
  public final void lockDevice () throws IOException, InterruptedException
  {
    this.controller.lockGpibAddress (this.address);
  }
  
  public final void unlockDevice ()
  {
    this.controller.unlockGpibAddress (this.address);
  }
  
}
