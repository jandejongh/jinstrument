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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.*;

/** Representation of a General-Purpose Interface Bus (GPIB; IEEE-Std-488) {@link DeviceType}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DeviceType_GPIB
  implements DeviceType
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DeviceType_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private DeviceType_GPIB ()
  {
  }
  
  private volatile static DeviceType_GPIB INSTANCE = null;
  
  private final static Object INSTANCE_LOCK = new Object ();
  
  public final static DeviceType_GPIB getInstance ()
  {
    synchronized (DeviceType_GPIB.INSTANCE_LOCK)
    {
      if (DeviceType_GPIB.INSTANCE == null)
        DeviceType_GPIB.INSTANCE = new DeviceType_GPIB ();  
    }
    return DeviceType_GPIB.INSTANCE;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DeviceType
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getDeviceTypeUrl ()
  {
    return "gpib";
  }
  
  @Override
  public String toString ()
  {
    return getDeviceTypeUrl ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DeviceType
  // BUS TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final BusType getBusType ()
  {
    return BusType_GPIB.getInstance ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DeviceType
  // OPEN DEVICE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public Device openDevice (final Controller controller, final String busAddressUrl)
  {
    if (controller == null)
    {
      LOG.log (Level.WARNING, "Controller is null!");
      return null;
    }
    if (! BusType_GPIB.getInstance ().equals (controller.getBusType ()))
    {
      LOG.log (Level.WARNING, "Bus Type {0} for Controller ({1}) not supported!",
        new Object[]{controller.getBusType ().getBusTypeUrl (), controller.getControllerUrl ()});
      return null;
      
    }
    if (! (controller instanceof GpibController))
    {
      LOG.log (Level.WARNING, "Controller {0} must be a GpibController!", controller.getControllerUrl ());
      return null;
    }
    final GpibAddress gpibBusAddress = GpibAddress.fromUrl (busAddressUrl);
    if (gpibBusAddress == null)
    {
      LOG.log (Level.WARNING, "Invalid Gpib Bus Address {0}!", busAddressUrl);
      return null;
    }
    return new DefaultGpibDevice ((GpibController) controller, gpibBusAddress);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
