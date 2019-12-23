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

/** Representation of the General-Purpose Interface Bus (GPIB; IEEE-Std-488) {@link BusType}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class BusType_GPIB
  implements BusType
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private BusType_GPIB ()
  {
  }
  
  private volatile static BusType_GPIB INSTANCE = null;
  
  private final static Object INSTANCE_LOCK = new Object ();
  
  public final static BusType_GPIB getInstance ()
  {
    synchronized (BusType_GPIB.INSTANCE_LOCK)
    {
      if (BusType_GPIB.INSTANCE == null)
        BusType_GPIB.INSTANCE = new BusType_GPIB ();  
    }
    return BusType_GPIB.INSTANCE;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BusType
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final String getBusTypeUrl ()
  {
    return "GPIB";
  }
  
  @Override
  public final String toString ()
  {
    return getBusTypeUrl ();
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BusType
  // DEVICE TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final DeviceType getDeviceType ()
  {
    return DeviceType_GPIB.getInstance ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
