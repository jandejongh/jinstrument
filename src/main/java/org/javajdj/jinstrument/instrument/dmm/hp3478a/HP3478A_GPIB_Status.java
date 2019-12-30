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
package org.javajdj.jinstrument.instrument.dmm.hp3478a;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentStatus;

/** Implementation of {@link InstrumentStatus} for the HP-3478A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP3478A_GPIB_Status
  implements InstrumentStatus
{

  private static final Logger LOG = Logger.getLogger (HP3478A_GPIB_Status.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private HP3478A_GPIB_Status (final HP3478A_GPIB_Settings settings)
  {
    this.errorByte = settings.getBytes ()[3];
    if ((this.errorByte & 0xc0) != 0)
      throw new IllegalArgumentException ();
    if (this.errorByte != 0)
      LOG.log (Level.WARNING, "The HP3478A_GPIB instrument reports an error!");
  }
  
  public final static HP3478A_GPIB_Status fromSettings (final HP3478A_GPIB_Settings settings)
  {
    return new HP3478A_GPIB_Status (settings);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ERROR BYTE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final byte errorByte;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String toString ()
  {
    return "0x" + Integer.toHexString (Byte.toUnsignedInt (this.errorByte));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ERROR DISSECTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final boolean hasError ()
  {
    return this.errorByte != 0;
  }
  
  public final boolean hasADLinkError ()
  {
    return ((this.errorByte & 0x20) != 0);
  }
    
  public final boolean hasADInternalSelfTestError ()
  {
    return ((this.errorByte & 0x10) != 0);
  }
    
  public final boolean hasADSlopeError ()
  {
    return ((this.errorByte & 0x08) != 0);
  }
    
  public final boolean hasROMSelfTestError ()
  {
    return ((this.errorByte & 0x04) != 0);
  }
    
  public final boolean hasRAMSelfTestError ()
  {
    return ((this.errorByte & 0x02) != 0);
  }
    
  public final boolean hasCalRAMError ()
  {
    return ((this.errorByte & 0x01) != 0);
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
