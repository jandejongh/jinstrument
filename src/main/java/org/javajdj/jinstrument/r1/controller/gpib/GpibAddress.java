package org.javajdj.jinstrument.r1.controller.gpib;

/**
 *
 */
public final class GpibAddress
{
  
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

  @Override
  public final String toString ()
  {
    return "gpib:" + this.pad + "," + this.sad;
  }
  
}
