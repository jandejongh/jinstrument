package org.javajdj.jinstrument.r1.swing.util;

import java.awt.Color;
import java.awt.GridLayout;
import java.text.DecimalFormat;
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

  public JSevenSegmentNumber (final int numberOfDigits)
  {
    this.digits = new JSevenSegment[numberOfDigits];
    setLayout (new GridLayout (1, numberOfDigits));
    for (int i = 0; i < numberOfDigits; i++)
    {
      this.digits[i] = new JSevenSegment ();
      this.digits[i].setBorder (BorderFactory.createMatteBorder (2, 2, 2, 2, Color.black));
      add (this.digits[i]);
    }
  }
  
  private final JSevenSegment[] digits;
  
  public void setNumber (Number number)
  {
    // XXX Assume Double!
    final double d = (Double) number;
    // XXX Assume 0.01 Hz resolution.
    final long N = Math.round (1.0e6 * 100.0 * d);
    final String s = new DecimalFormat ("000000000000").format (N);
    for (int i = 0; i < 11; i++)
    {
      int digit = 0;
      try
      {
        digit = Integer.parseInt (new String (new char[] {s.charAt (i)}));
      }
      catch (NumberFormatException nfs)
      {
        LOG.log (Level.WARNING, "NumberFormatException, char = '{'0'}'.", s.charAt (i));
      }
      this.digits[i].writeNumber (digit);
      this.digits[i].setDecimalPoint (i==9);
      this.digits[i].repaint ();
    }
    
  }

  private static final Logger LOG = Logger.getLogger (JSevenSegmentNumber.class.getName ());
  
}
