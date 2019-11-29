package org.javajdj.jinstrument.r1.swing.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.UIManager;

/**
 *
 */
public class JStatusCheckBox
extends JCheckBox
{
  
  public JStatusCheckBox ()
  {
    super (new HighlightOnSelectIcon (), false);
    setEnabled (false);
    setAlignmentX (LEFT_ALIGNMENT);
  }
    
  private final static class HighlightOnSelectIcon
    extends javax.swing.plaf.metal.MetalCheckBoxIcon
  {

    public HighlightOnSelectIcon ()
    {
    }

    private final Icon wrappedIcon = UIManager.getIcon ("CheckBox.icon");

    @Override
    protected void drawCheck (final Component c, final Graphics g, final int x, final int y)
    {
      final Color oldColor = g.getColor ();
      g.setColor (Color.BLACK);
      super.drawCheck (c, g, x, y);
      g.setColor (oldColor);
    }

    @Override
    public void paintIcon (Component c, Graphics g, int x, int y)
    {
      this.wrappedIcon.paintIcon (c, g, x, y);
      JCheckBox cb = (JCheckBox) c;
      final Color oldColor = g.getColor ();
      if (cb.isSelected ())
        g.setColor (Color.green);
      else
        g.setColor (Color.red);
      g.fillRect (x + 1, y + 1, getIconWidth () - 2, getIconHeight () - 2);
      g.setColor (oldColor);
    }
    
  }
  
}
