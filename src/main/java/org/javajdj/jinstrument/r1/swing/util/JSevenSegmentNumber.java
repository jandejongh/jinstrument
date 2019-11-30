package org.javajdj.jinstrument.r1.swing.util;

import java.awt.Color;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 */
public class JSevenSegmentNumber
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private static final Logger LOG = Logger.getLogger (JSevenSegmentNumber.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JSevenSegmentNumber (final Color mediumColor, final boolean sign, final int numberOfDigits, final int decimalPointIndex)
  {
    this.minus = sign ? new JSevenSegment () : null;
    setLayout (new GridLayout (1, this.minus != null ? (numberOfDigits + 1) : numberOfDigits));
    if (this.minus != null)
      add (this.minus);
    this.digits = new JSevenSegment[numberOfDigits];
    for (int i = 0; i < numberOfDigits; i++)
    {
      this.digits[i] = new JSevenSegment (mediumColor);
      this.digits[i].setBorder (BorderFactory.createMatteBorder (2, 2, 2, 2, Color.black));
      add (this.digits[i]);
    }
    char[] formatArray = new char[numberOfDigits];
    Arrays.fill (formatArray, '0');
    this.decimalFormat = new DecimalFormat (new String (formatArray));
    this.decimalPointIndex = decimalPointIndex;
  }
  
  public JSevenSegmentNumber (final boolean sign, final int numberOfDigits, final int decimalPointIndex)
  {
    this (null, sign, numberOfDigits, decimalPointIndex);
  }
  
  private final JSevenSegment minus;
  
  private final JSevenSegment[] digits;
  
  private final DecimalFormat decimalFormat;
  
  private final int decimalPointIndex;
  
  public void setNumber (final double d)
  {
    if (this.minus != null)
      if (d < 0)
        this.minus.setMinus ();
      else
        this.minus.setBlank ();
    final long N;
    if (this.decimalPointIndex >= 0)
      N = (long) Math.round (d * Math.pow (10, this.digits.length - this.decimalPointIndex - 1));
    else
      N = (long) Math.round (d);
    final String s = this.decimalFormat.format (N);
    if (s.length () != this.digits.length)
    {
      LOG.log (Level.SEVERE, "Formatted string size ({0}) does not match number of digits ({1}); String={2}!",
        new Object[]{s.length (), this.digits.length, s});
      throw new RuntimeException ();
    }
    for (int i = 0; i < this.digits.length; i++)
    {
      int digit = 0;
      try
      {
        digit = Integer.parseInt (new String (new char[] {s.charAt (i)}));
      }
      catch (NumberFormatException nfs)
      {
        LOG.log (Level.SEVERE, "NumberFormatException, char = {0}.", s.charAt (i));
        throw new RuntimeException (nfs);
      }
      this.digits[i].setNumber (digit);
      this.digits[i].setDecimalPoint (i == this.decimalPointIndex);
      this.digits[i].repaint ();
    }  
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
