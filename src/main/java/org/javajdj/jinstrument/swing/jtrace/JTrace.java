/* 
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.swing.jtrace;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/** Panel showing one or more traces from for instance an instrument.
 *
 * <p>
 * Traces are each represented as double arrays, and distinguished with a
 * key of type {@code K}.
 * 
 * @param <K> The key type used for distinguishing traces.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 */
public class JTrace<K>
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTrace.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTrace ()
  {
    super ();
    setOpaque (true);
    setBackground (Color.black);
    addMouseListener (this.mouseAdapter);
    addMouseMotionListener (this.mouseAdapter);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE ENTRIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Map<K, TraceEntry> traceEntries = new LinkedHashMap<> ();
  
  private final Object traceEntriesLock = new Object ();
  
  public final TraceEntry getTraceEntry (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      return this.traceEntries.get (k);
    }
  }
  
  public final void setTraceEntry (final K k, final TraceEntry traceEntry)
  {
    synchronized (this.traceEntriesLock)
    {
      if (traceEntry == null)
        this.traceEntries.remove (k);
      else
        this.traceEntries.put (k, traceEntry);
    }
  }
  
  public final Map<K, TraceEntry> getTraceEntries ()
  {
    synchronized (this.traceEntriesLock)
    {
      return Collections.unmodifiableMap (new LinkedHashMap<> (this.traceEntries));
    }
  }
  
  public final void setTraceEntries (final Map<K, TraceEntry> traceEntries)
  {
    synchronized (this.traceEntriesLock)
    {
      if (traceEntries == null)
        this.traceEntries.clear ();
      else
        this.traceEntries.putAll (traceEntries);
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  private TraceEntry getOrCreateTraceEntry (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      final TraceEntry entry;
      if (this.traceEntries.containsKey (k))
        entry = this.traceEntries.get (k);
      else
      {
        entry = TraceEntry.EMPTY.withColor (nextDefaultColor ());
        this.traceEntries.put (k, entry);
        // NOTE: DOES NOT REPAINT -> THEREFORE private!
      }
      return entry;
    }
  }
  
  public final double[] getTrace (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getTrace ();
      else
        return null;
    }
  }
  
  public final void setTrace (final K k, final double[] trace)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withTrace (trace));
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  public final double getMinX (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getMinX ();
      else
        return Double.NaN;
    }    
  }
  
  public final void setMinX (final K k, final double minX)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withMinX (minX));
      SwingUtilities.invokeLater (() -> repaint ());
    }    
  }
  
  public final double getMaxX (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getMaxX ();
      else
        return Double.NaN;
    }    
  }
  
  public final void setMaxX (final K k, final double maxX)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withMaxX (maxX));
      SwingUtilities.invokeLater (() -> repaint ());
    }    
  }
  
  public final double getMinY (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getMinY ();
      else
        return Double.NaN;
    }    
  }
  
  public final void setMinY (final K k, final double minY)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withMinY (minY));
      SwingUtilities.invokeLater (() -> repaint ());
    }    
  }
  
  public final double getMaxY (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getMaxY ();
      else
        return Double.NaN;
    }    
  }
  
  public final void setMaxY (final K k, final double maxY)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withMaxY (maxY));
      SwingUtilities.invokeLater (() -> repaint ());
    }    
  }
  
  public final void setBoundaries (
    final K k,
    final double minX,
    final double maxX,
    final double minY,
    final double maxY)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withBoundaries (minX, maxX, minY, maxY));
      SwingUtilities.invokeLater (() -> repaint ());
    }        
  }
  
  public final void setColor (final K k, final Color color)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withColor (color));
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT TRACE COLORS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static Color[] DEFAULT_COLORS =
  {
    Color.yellow,
    Color.cyan,
    Color.pink,
    Color.blue
  };
  
  private volatile int nextDefaultColorIndex = 0;
  
  private final Object nextDefaultColorIndexLock = new Object ();
  
  private Color nextDefaultColor ()
  {
    final Color color;
    synchronized (this.nextDefaultColorIndexLock)
    {
      color = DEFAULT_COLORS[this.nextDefaultColorIndex];
      this.nextDefaultColorIndex = (this.nextDefaultColorIndex + 1) % DEFAULT_COLORS.length;
    }
    return color;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // xDivisions
  // yDivisions
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private int xDivisions = 10;
  
  public final int getXDivisions ()
  {
    return this.xDivisions;
  }

  public final void setXDivisions (int xDivisions)
  {
    if (xDivisions < 1)
      throw new IllegalArgumentException ();
    if (this.xDivisions != xDivisions)
    {
      this.xDivisions = xDivisions;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }

  private int yDivisions = 10;

  public final int getYDivisions ()
  {
    return this.yDivisions;
  }

  public final void setYDivisions (int yDivisions)
  {
    if (yDivisions < 1)
      throw new IllegalArgumentException ();
    if (this.yDivisions != yDivisions)
    {
      this.yDivisions = yDivisions;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // X AND Y [INTERNAL] MARGINS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final static int X_MARGIN = 20;
  
  private final static int Y_MARGIN = 10;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // GRATICULE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Color GRATICULE_COLOR = Color.green.darker ().darker ();
  private static final Color GRATICULE_HIGHLIGHT_COLOR = Color.green;
  
  private static final Stroke GRATICULE_STROKE =
      new BasicStroke (0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);
  
  private volatile boolean enableGraticule = true;
  
  public final void toggleEnableGraticule ()
  {
    synchronized (this)
    {
      this.enableGraticule = ! this.enableGraticule;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  public final void setEnableGraticule (final boolean enableGraticule)
  {
    synchronized (this)
    {
      if (this.enableGraticule != enableGraticule)
        toggleEnableGraticule ();
    }
  }
  
  private void paintGraticule (final Graphics2D g2d, final int width, final int height)
  {
    if (! this.enableGraticule)
      return;
    if (g2d == null)
      return;
    g2d.setStroke (GRATICULE_STROKE);
    for (int i = 0; i <= this.yDivisions; i++)
    {
      if (this.yDivisions % 2 == 0 && i == this.yDivisions / 2)
        g2d.setColor (GRATICULE_HIGHLIGHT_COLOR);
      else
        g2d.setColor (GRATICULE_COLOR);
      final int y = Y_MARGIN + (int) Math.round ((i / (double) this.yDivisions) * (height - 2 * Y_MARGIN));
      // Horizontal line at y.
      g2d.drawLine (X_MARGIN, y, width - X_MARGIN, y);
      if (this.xDivisions % 2 == 0 && i < this.yDivisions)
      {
        g2d.setColor (GRATICULE_HIGHLIGHT_COLOR);
        final int xCenter = (int) Math.round (width / 2.0);
        for (int j = 1; j <= 9; j++)
        {
          final int yMinor = y + (int) Math.round ((height - 2 * Y_MARGIN) * j / (10.0 * this.yDivisions));
          if (j == 5)
            g2d.drawLine (xCenter - 4, yMinor, xCenter + 5, yMinor);
          else
            g2d.drawLine (xCenter - 2, yMinor, xCenter + 3, yMinor);
        }
      }
    }
    for (int i = 0; i <= this.xDivisions; i++)
    {
      if (this.xDivisions % 2 == 0 && i == this.xDivisions / 2)
        g2d.setColor (GRATICULE_HIGHLIGHT_COLOR);
      else
        g2d.setColor (GRATICULE_COLOR);
      final int x = X_MARGIN + (int) Math.round ((i / (double) this.xDivisions) * (width - 2 * X_MARGIN));
      // Vertical line at x.
      g2d.drawLine (x, Y_MARGIN, x, height - Y_MARGIN);
      if (this.yDivisions % 2 == 0 && i < this.xDivisions)
      {
        g2d.setColor (GRATICULE_HIGHLIGHT_COLOR);
        final int yCenter = (int) Math.round (height / 2.0);
        for (int j = 1; j <= 9; j++)
        {
          final int xMinor = x + (int) Math.round ((width - 2 * X_MARGIN) * j / (10.0 * this.xDivisions));
          if (j == 5)
            g2d.drawLine (xMinor, yCenter - 4, xMinor, yCenter + 5);
          else
            g2d.drawLine (xMinor, yCenter - 2, xMinor, yCenter + 3);
        }
      }
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TRACES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean enableTraces = true;
  
  public final boolean isEnableTraces ()
  {
    return this.enableTraces;
  }
  
  public final void setEnableTraces (final boolean enableTraces)
  {
    final boolean changed;
    synchronized (this)
    {
      changed = enableTraces != this.enableTraces;
      this.enableTraces = enableTraces;
    }
    if (changed)
      SwingUtilities.invokeLater (() -> repaint ());
  }
  
  private static final Color CLIP_COLOR = Color.red;
  
  private void paintTraces
  (final Graphics2D g2d, final int width, final int height)
  {
    if (! this.enableTraces)
      return;
    if (g2d == null)
      return;
    final Map<K, TraceEntry> traceEntries;
    synchronized (this.traceEntriesLock)
    {
      traceEntries = new LinkedHashMap<> (this.traceEntries);
    }
    for (final Map.Entry<K, TraceEntry> traceEntryEntry : traceEntries.entrySet ())
    {
      if (traceEntryEntry == null)
        continue;;
      final K key = traceEntryEntry.getKey ();
      final TraceEntry traceEntry = traceEntryEntry.getValue ();
      if (traceEntry == null)
        continue;
      final double[] trace = traceEntry.getTrace ();
      final Color traceColor = traceEntry.getColor ();
      if (trace == null || traceColor == null)
        continue;
      if (trace.length == 0)
        continue;
      final double minX;
      final double maxX;
      if (Double.isFinite (traceEntry.getMinX ()) && Double.isFinite (traceEntry.getMaxX ()))
      {
        minX = traceEntry.getMinX ();
        maxX = traceEntry.getMaxX ();
      }
      else
      {
        minX = 0;
        maxX = trace.length;
      }
      final double minY;
      final double maxY;
      if (Double.isFinite (traceEntry.getMinY ()) && Double.isFinite (traceEntry.getMaxY ()))
      {
        minY = traceEntry.getMinY ();
        maxY = traceEntry.getMaxY ();
      }
      else
      {
        double y_min_now = Double.POSITIVE_INFINITY;
        double y_max_now = Double.NEGATIVE_INFINITY;
        for (int s = 0; s < trace.length; s++)
        {
          if (trace[s] < y_min_now)
            y_min_now = trace[s];
          if (trace[s] > y_max_now)
            y_max_now = trace[s];
        }
        if (Double.isInfinite (y_min_now)
          || Double.isInfinite (y_max_now)
          || y_max_now - y_min_now < 1.0E-12 /* XXX SOME EPSILON */)
        {
          y_min_now = 0;
          y_max_now = 1;
        }
        minY = y_min_now;
        maxY = y_max_now;
      }
      g2d.setColor (traceColor);
      final int traceLength = trace.length;
      double x_s_prev = 0;
      double y_s_prev = 0;
      for (int s = 0; s < traceLength; s++)
      {
        final double x_s = X_MARGIN + ((double) width - 2 * X_MARGIN) * s / trace.length;
        double y_s = Y_MARGIN + (height - 2 * Y_MARGIN) * (maxY - trace[s]) / (maxY - minY);
        if (y_s < Y_MARGIN)
        {
          g2d.setColor (CLIP_COLOR);
          y_s = Y_MARGIN;
          y_s_prev = Y_MARGIN;
        }
        else if (y_s > (height - Y_MARGIN))
        {
          g2d.setColor (CLIP_COLOR);
          y_s = height - Y_MARGIN;
          y_s_prev = height - Y_MARGIN;
        }
        else
          g2d.setColor (traceColor);
        if (s > 0)
          g2d.draw (new Line2D.Double (x_s_prev, y_s_prev, x_s, y_s));
        x_s_prev = x_s;
        y_s_prev = y_s;        
      }
    }
    
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // CROSSHAIR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Color CROSSHAIR_COLOR = Color.cyan;
  
  private volatile boolean enableCrossHair = true;
  
  public final void toggleEnableCrossHair ()
  {
    synchronized (this)
    {
      this.enableCrossHair = ! this.enableCrossHair;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  private void paintCrossHair
  (final Graphics2D g2d,
   final int width, final int height,
   final int mouseX, final int mouseY)
  {
    if (! (this.enableCrossHair && this.drawCrossHair))
      return;
    if (g2d == null)
      return;
    if (mouseX < X_MARGIN || mouseX > width - X_MARGIN)
      return;
    if (mouseY < Y_MARGIN || mouseY > height - Y_MARGIN)
      return;
    g2d.setColor (CROSSHAIR_COLOR);
    g2d.setStroke (GRATICULE_STROKE);
    // Draw the "crosshair" cursor.
    g2d.drawLine (mouseX, Y_MARGIN, mouseX, height - Y_MARGIN);
    g2d.drawLine (X_MARGIN, mouseY, width - X_MARGIN, mouseY);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // PAINT COMPONENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private void paintComponent
  (final Graphics2D g2d,
   final int width, final int height,
   final int mouseX, final int mouseY)
  {
    final Color oldColor = g2d.getColor ();
    final Stroke oldStroke = g2d.getStroke ();
    paintGraticule (g2d, width, height);
    paintTraces (g2d, width, height);
    paintCrossHair (g2d, width, height, mouseX, mouseY);
    g2d.setStroke (oldStroke);
    g2d.setColor (oldColor);     
  }
  
  @Override
  public void paintComponent (final Graphics g)
  {
    super.paintComponent (g);
    final Graphics2D g2d = (Graphics2D) g;
    if (g2d == null)
      return;
    final int width = getWidth ();
    final int height = getHeight ();
    final int mouseX = this.mouseX;
    final int mouseY = this.mouseY;
    paintComponent (g2d, width, height, mouseX, mouseY);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // MOUSE HANDLING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private boolean drawCrossHair = false;
  
  private int mouseX = -1;
  
  private int mouseY = -1;

  private final MouseAdapter mouseAdapter = new MouseAdapter ()
  {
    
    @Override
    public void mouseExited (final MouseEvent e)
    {
      JTrace.this.drawCrossHair = false;
      JTrace.this.repaint ();
    }

    @Override
    public void mouseEntered (final MouseEvent e)
    {
      JTrace.this.mouseX = e.getX ();
      JTrace.this.mouseY = e.getY ();
      JTrace.this.drawCrossHair = true;
      JTrace.this.repaint ();
    }

    @Override
    public void mouseMoved (final MouseEvent e)
    {
      JTrace.this.mouseX = e.getX ();
      JTrace.this.mouseY = e.getY ();
      JTrace.this.repaint ();
    }

  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
