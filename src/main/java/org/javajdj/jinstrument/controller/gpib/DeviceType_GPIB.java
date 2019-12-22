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

  @Override
  public final BusType getBusType ()
  {
    return BusType_GPIB.getInstance ();
  }
  
  @Override
  public boolean isValidUrl (String controllerUrl)
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public Device openDevice (String deviceUrl)
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
