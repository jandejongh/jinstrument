package org.javajdj.jinstrument.r1.swing.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 */
public class JStatusBar
extends JPanel
{

  private static final Logger LOG = Logger.getLogger (JStatusBar.class.getName ());

  public JStatusBar ()
  {
    super ();
    setLayout (new BoxLayout (this, BoxLayout.X_AXIS));
    new MonitorThread ().start ();
  }
  
  @FunctionalInterface
  public static interface StatusableItem
  {
    
    boolean isOK ();
    
    default boolean isActionOnly () { return false; }
    
    default void clickAction () { }
    
  }
  
  private final static class HighlightOnSelectIcon
    extends javax.swing.plaf.metal.MetalCheckBoxIcon
  {

    public HighlightOnSelectIcon (final boolean hasStatus)
    {
      this.hasStatus = hasStatus;
    }

    private final boolean hasStatus;
    
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
      if (this.hasStatus)
      {
        if (cb.isSelected ())
          g.setColor (Color.green);
        else
          g.setColor (Color.red);
      }
      else
        g.setColor (Color.blue);
      g.fillRect (x + 1, y + 1, getIconWidth () - 2, getIconHeight () - 2);
      g.setColor (oldColor);
    }
    
  }
  
  private final static class StatusCheckBox
  extends JCheckBox
  {

    public StatusCheckBox (final boolean hasStatus)
    {
      super (new HighlightOnSelectIcon (hasStatus), false);
      setEnabled (false);
      setAlignmentX (LEFT_ALIGNMENT);
    }
    
  }
  
  private final static class RegisteredStatusableItem
  {
    
    public RegisteredStatusableItem (final String name)
    {
      this.name = name;
    }
    
    private final String name;
    
    private JCheckBox checkBox = null;
    
    protected final synchronized void setCheckBox (final JCheckBox checkBox)
    {
      if (checkBox == null)
        throw new IllegalArgumentException ();
      if (this.checkBox != null)
        throw new IllegalStateException ();
      this.checkBox = checkBox;
    }
    
  }
  
  private final Map<StatusableItem, RegisteredStatusableItem> registeredStatusableItems = new LinkedHashMap<> ();
  
  public final synchronized void registerItem (final StatusableItem item, final String name)
  {
    if (item == null || this.registeredStatusableItems.containsKey (item) || name == null || name.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    final RegisteredStatusableItem registeredStatusableItem = new RegisteredStatusableItem (name);
    this.registeredStatusableItems.put (item, registeredStatusableItem);
    SwingUtilities.invokeLater (() ->
    {
      final JCheckBox checkBox = new StatusCheckBox (! item.isActionOnly ());
      registeredStatusableItem.setCheckBox (checkBox);
      if (! item.isActionOnly ())
        checkBox.setSelected (item.isOK ());
      checkBox.addMouseListener (new MouseAdapter ()
      {
        @Override
        public final void mouseClicked (final MouseEvent e)
        {
          JStatusBar.this.mouseClickedOnItem (item);
        }
      });
      JStatusBar.this.add (checkBox);
      add (new JLabel (name));
      add (new JLabel ("  "));
    });
  }
  
  protected final synchronized Map<StatusableItem, RegisteredStatusableItem> getRegisteredItems ()
  {
    return new LinkedHashMap<> (this.registeredStatusableItems);
  }
  
  private void mouseClickedOnItem (final StatusableItem item)
  {
    if (item == null || ! this.registeredStatusableItems.containsKey (item))
      return;
    item.clickAction ();
  }
  
  private long pollIntervalMillis = 1000L;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //  
  // CLASS MonitorThread
  //  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private class MonitorThread
  extends Thread
  {

    @Override
    public void run ()
    {
      LOG.log (Level.INFO, "MonitorThread started: {0} on JStatusBar: {1}.",
        new Object[] {this, JStatusBar.this});
      try
      {
        while (true)
        {
          Thread.sleep (JStatusBar.this.pollIntervalMillis);
          final Map<StatusableItem, RegisteredStatusableItem> registeredStatusableItems = JStatusBar.this.getRegisteredItems ();
          for (final StatusableItem item : registeredStatusableItems.keySet ())
            if (! item.isActionOnly ())
              registeredStatusableItems.get (item).checkBox.setSelected (item.isOK ());
        }
      }
      catch (InterruptedException ie)
      {
        LOG.log (Level.INFO, "MonitorThread interrupted; exiting from run method: {0} on JStatusBar: {1}.",
          new Object[] {this, JStatusBar.this});
        final Map<StatusableItem, RegisteredStatusableItem> registeredStatusableItems = JStatusBar.this.getRegisteredItems ();
        for (final StatusableItem item : registeredStatusableItems.keySet ())
          registeredStatusableItems.get (item).checkBox.setBackground (Color.yellow);
      }
    }
    
  }

}
