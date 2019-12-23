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

import org.javajdj.jinstrument.BusAddress;

/** A device address on a GPIB (General-Purpose Interface Bus).
 *
 * <p>
 * Support primary (PAD) and secondary (SAD) address parts. The objects are immutable.
 * 
 * <p>
 * Implements {@link BusAddress} for GPIB.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class GpibAddress
  implements BusAddress
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public GpibAddress (final byte pad, final byte sad)
  {
    if (pad < 0 || pad > 30)
      throw new IllegalArgumentException ();
    if (sad != 0 && (sad < GpibAddress.MIN_NON_ZERO_SAD || sad > GpibAddress.MAX_NON_ZERO_SAD))
      throw new IllegalArgumentException ();
    this.pad = pad;
    this.sad = sad;
  }
  
  public GpibAddress (final byte pad)
  {
    this (pad, (byte) 0);
  }

  public final static GpibAddress fromUrl (final String gpibAddressUrl)
  {
    if (gpibAddressUrl == null || ! gpibAddressUrl.trim ().toLowerCase ().startsWith ("gpib:"))
      return null;
    final String gpibAddressString = gpibAddressUrl.trim ().replaceFirst (".*:", "");
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PAD / SAD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final byte pad;
  
  public final static byte MIN_NON_ZERO_SAD = (byte) 0x60;
  
  public final static byte MAX_NON_ZERO_SAD = (byte) 0x7e;
  
  private final byte sad;
 
  public final byte getPad ()
  {
    return this.pad;
  }
  
  public final byte getSad ()
  {
    return this.sad;
  }
  
  public final boolean hasSad ()
  {
    return this.sad != 0;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BusAddress
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final String getBusAddressUrl ()
  {
    final String busAddressUrl;
    if (hasSad ())
      busAddressUrl = "gpib:" + this.pad + "," + this.sad;
    else
      busAddressUrl = "gpib:" + this.pad;
    return busAddressUrl;
  }
  
  @Override
  public String toString ()
  {
    return getBusAddressUrl ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HASH CODE / EQUALS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final int hashCode ()
  {
    int hash = 7;
    hash = 97 * hash + this.pad;
    return hash;
  }

  @Override
  public final boolean equals (final Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass () != obj.getClass ())
      return false;
    final GpibAddress other = (GpibAddress) obj;
    if (this.pad != other.pad)
      return false;
    return (this.sad == (byte) 0 || other.sad == (byte) 0 || this.sad == other.sad);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
