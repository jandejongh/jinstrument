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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.javajdj.jinstrument.AbstractController;
import org.javajdj.jservice.Service;

/** Abstract base implementation of {@link GpibController}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public abstract class AbstractGpibController
extends AbstractController
implements GpibController
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
    final GpibDevice device = new DefaultGpibDevice (this, address, deviceUrl);
    this.devices.put (address, device);
    return device;
  }
  
  protected final Map<GpibAddress, GpibDevice> devices = new LinkedHashMap<>  ();

  private synchronized boolean isRegisteredDevice (final GpibDevice device)
  {
    return this.devices.values ().contains (device);
  }
  
}
