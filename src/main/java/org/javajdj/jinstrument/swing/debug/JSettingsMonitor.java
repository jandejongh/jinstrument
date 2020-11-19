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
package org.javajdj.jinstrument.swing.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.default_view.JDefaultInstrumentManagementView;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.setPanelBorder;

/** A Swing panel for monitoring changes in {@link InstrumentSettings}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class JSettingsMonitor
  extends JInstrumentPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JSettingsMonitor.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JSettingsMonitor (final Instrument instrument, final int level)
  {
    
    super (instrument, level);
    
    setOpaque (true);
    setMinimumSize (new Dimension (1024, 600));
    setPreferredSize (new Dimension (1024, 600));
    
    setLayout (new BorderLayout ());
    
    final JInstrumentPanel managementPanel = new JDefaultInstrumentManagementView (getInstrument (), getLevel () + 1);
    setPanelBorder (
      managementPanel, getLevel () + 1,
      getGuiPreferencesManagementColor (),
      "Management");
    add (managementPanel, BorderLayout.NORTH);
    
    this.westPanel = new WestPanel ();
    add (this.westPanel, BorderLayout.WEST);
    
    this.eastPanel = new EastPanel ();
    add (this.eastPanel, BorderLayout.EAST);
    
    this.jByteArrayCompare = new JByteArrayCompare ();
    this.jByteArrayCompare.setAlignmentX (SwingConstants.CENTER);
    this.jByteArrayCompare.setAlignmentY (SwingConstants.CENTER);
    add (this.jByteArrayCompare, BorderLayout.CENTER);
    
    getInstrument ().addInstrumentListener (this.instrumentListener);
    
  }
  
  public JSettingsMonitor (final Instrument instrument)
  {
    this (instrument, 0);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT VIEW TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static InstrumentViewType INSTRUMENT_VIEW_TYPE = new InstrumentViewType ()
  {
    
    @Override
    public final String getInstrumentViewTypeUrl ()
    {
      return "Settings Monitor [debug]";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null)
        return new JSettingsMonitor (instrument);
      else
        return null;
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getInstrumentViewUrl ()
  {
    return JSettingsMonitor.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
      + "<>"
      + getInstrument ().getInstrumentUrl ();
  }
  
  @Override
  public String toString ()
  {
    return getInstrumentViewUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SETTINGS BYTES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum ByteStatus
  {
    EQUAL,
    DIFFERS,
    ADDED,
    REMOVED;   
  }
  
  public class ByteRecord
  {
    
    public final ByteStatus status;
    
    public final Byte oldValue;

    public final Byte newValue;
    
    public ByteRecord (final ByteStatus status, final Byte oldValue, Byte newValue)
    {
      if (status == null)
        throw new IllegalArgumentException ();
      this.status = status;
      this.oldValue = oldValue;
      this.newValue = newValue;
    }
    
  }
  
  private volatile byte[] prevBytes = null;
  
  private final Object bytesLock = new Object ();
  
  private void addBytes (final byte[] bytes)
  {
    final ByteRecord[] byteRecords;
    synchronized (JSettingsMonitor.this.bytesLock)
    {
      if (bytes == null && this.prevBytes == null)
      {
        byteRecords = new ByteRecord[0];
      }
      else if (bytes != null && this.prevBytes == null)
      {
        byteRecords = new ByteRecord[bytes.length];
        for (int i = 0; i < byteRecords.length; i++)
          byteRecords[i] = new ByteRecord (ByteStatus.ADDED, null, bytes[i]);
      }
      else if (bytes == null && this.prevBytes != null)
      {
        byteRecords = new ByteRecord[this.prevBytes.length];
        for (int i = 0; i < byteRecords.length; i++)
          byteRecords[i] = new ByteRecord (ByteStatus.REMOVED, this.prevBytes[i], null);
      }
      else
      {
        byteRecords = new ByteRecord[Math.max (bytes.length, this.prevBytes.length)];
        for (int i = 0; i < Math.min (bytes.length, this.prevBytes.length); i++)
          byteRecords[i] = new ByteRecord ((bytes[i] == this.prevBytes[i]) ? ByteStatus.EQUAL : ByteStatus.DIFFERS,
            this.prevBytes[i],
            bytes[i]);
        for (int i = bytes.length; i < this.prevBytes.length; i++)
          byteRecords[i] = new ByteRecord (ByteStatus.REMOVED, this.prevBytes[i], null);
        for (int i = this.prevBytes.length; i < bytes.length; i++)
          byteRecords[i] = new ByteRecord (ByteStatus.ADDED, null, bytes[i]);
      }
      this.prevBytes = bytes;
    }
    this.jByteArrayCompare.addByteRecords (byteRecords);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // WEST PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private class WestPanel
    extends JPanel
  {

    private final JLabel jIndex;
    
    private final JLabel jOldValue;
    private final JLabel jNewValue;
    
    private final JButton jStartStop;
    
    private final JButton jClear;
    
    public WestPanel ()
    {
      setLayout (new GridLayout (20, 1));
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
      this.jIndex = new JLabel ("i=-");
      this.jIndex.setHorizontalAlignment (SwingConstants.CENTER);
      add (this.jIndex);
      add (new JLabel ());
      this.jOldValue = new JLabel ("old=-");
      this.jOldValue.setPreferredSize (new Dimension (140, 40));
      this.jOldValue.setHorizontalAlignment (SwingConstants.CENTER);
      add (this.jOldValue);
      this.jNewValue = new JLabel ("new=-");
      this.jNewValue.setPreferredSize (new Dimension (140, 40));
      this.jNewValue.setHorizontalAlignment (SwingConstants.CENTER);
      add (this.jNewValue);
      add (new JLabel ());
      add (new JLabel ());
      this.jStartStop = new JButton ("Stop");
      this.jStartStop.setHorizontalAlignment (SwingConstants.CENTER);
      this.jStartStop.addActionListener ((ActionEvent ae) -> JSettingsMonitor.this.jByteArrayCompare.toggleAcquisitionEnabled ());
      add (this.jStartStop);
      add (new JLabel ());
      add (new JLabel ());
      this.jClear = new JButton ("Clear");
      this.jClear.setHorizontalAlignment (SwingConstants.CENTER);
      this.jClear.addActionListener ((ActionEvent ae) -> JSettingsMonitor.this.jByteArrayCompare.clear ());
      add (this.jClear);
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
    }
    
  }
  
  private final WestPanel westPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // EAST PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private class EastPanel
    extends JPanel
  {
    
    private final JLabel jCurrentTotal;
    
    private final JButton jFirst;
    
    private final JButton jPrevious;
    
    private final JButton jNext;
    
    private final JButton jLast;
    
    public EastPanel ()
    {
      setLayout (new GridLayout (20, 1));
      add (new JLabel ());
      add (new JLabel ());
      this.jCurrentTotal = new JLabel ("-/0");
      this.jCurrentTotal.setHorizontalAlignment (SwingConstants.CENTER);
      add (this.jCurrentTotal);
      add (new JLabel ());
      add (new JLabel ());
      this.jFirst = new JButton ("First");
      this.jFirst.setHorizontalAlignment (SwingConstants.CENTER);
      this.jFirst.addActionListener ((ActionEvent ae) -> JSettingsMonitor.this.jByteArrayCompare.first ());
      add (this.jFirst);
      add (new JLabel ());
      add (new JLabel ());
      this.jNext = new JButton ("Next");
      this.jNext.setHorizontalAlignment (SwingConstants.CENTER);
      this.jNext.addActionListener ((ActionEvent ae) -> JSettingsMonitor.this.jByteArrayCompare.next ());
      add (this.jNext);
      add (new JLabel ());
      add (new JLabel ());
      this.jPrevious = new JButton ("Previous");
      this.jPrevious.addActionListener ((ActionEvent ae) -> JSettingsMonitor.this.jByteArrayCompare.previous ());
      add (this.jPrevious);
      add (new JLabel ());
      add (new JLabel ());
      this.jLast = new JButton ("Last");
      this.jLast.setHorizontalAlignment (SwingConstants.CENTER);
      this.jLast.addActionListener ((ActionEvent ae) -> JSettingsMonitor.this.jByteArrayCompare.last ());
      add (this.jLast);
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
      add (new JLabel ());
    }
    
  }
  
  private final EastPanel eastPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public class JByteArrayCompare
    extends JPanel
  {

    public JByteArrayCompare ()
    {
      super ();
      setOpaque (true);
      setPreferredSize (new Dimension (CELLS_PER_ROW * CELL_WIDTH, 50 * CELL_HEIGHT));
      addMouseListener (this.mouseAdapter);
      addMouseMotionListener (this.mouseAdapter);
    }
        
    private final List<ByteRecord[]> byteRecordsList = new ArrayList<> ();
    
    private final Object byteRecordsListLock = new Object ();
    
    private volatile int current = 0;
    
    public final void addByteRecords (final ByteRecord[] byteRecords)
    {
      final boolean needsRepaint;
      synchronized (this.byteRecordsListLock)
      {
        if (! this.acquisitionEnabled)
          return;
        if (this.byteRecordsList.isEmpty ())
        {
          this.current = 0;
          needsRepaint = true;
        }
        else if (this.current == this.byteRecordsList.size () - 1)
        {
          this.current++;
          needsRepaint = true;
        }
        else
          needsRepaint = false;
        this.byteRecordsList.add (byteRecords);
        JSettingsMonitor.this.eastPanel.jCurrentTotal.setText ((current () + 1) + "/" + total ());
      }
      if (needsRepaint)
        SwingUtilities.invokeLater (() -> repaint ());
    }
    
    public final static int CELL_WIDTH = 16;
  
    public final static int CELL_HEIGHT = 16;
  
    public final static int CELLS_PER_ROW = 64;
    
    public volatile boolean acquisitionEnabled = true;
    
    public final void acquisitionEnable ()
    {
      final boolean needsRepaint;
      synchronized (this.byteRecordsListLock)
      {
        needsRepaint = ! this.acquisitionEnabled;
        this.acquisitionEnabled = true;
        JSettingsMonitor.this.westPanel.jStartStop.setText ("Stop");
      }
      if (needsRepaint)
        SwingUtilities.invokeLater (() -> repaint ());
    }
      
    public final void acquisitionDisable ()
    {
      final boolean needsRepaint;
      synchronized (this.byteRecordsListLock)
      {
        needsRepaint = this.acquisitionEnabled;
        this.acquisitionEnabled = false;
        JSettingsMonitor.this.westPanel.jStartStop.setText ("Start");
      }
      if (needsRepaint)
        SwingUtilities.invokeLater (() -> repaint ());
    }
    
    public final boolean isAcquisitionEnabled ()
    {
      synchronized (this.byteRecordsListLock)
      {
        return this.acquisitionEnabled;
      }
    }
    
    public final void setAcquisitionEnabled (final boolean acquisitionEnabled)
    {
      synchronized (this.byteRecordsListLock)
      {
        if (acquisitionEnabled)
          acquisitionEnable ();
        else
          acquisitionDisable ();
      }
    }
      
    public final void toggleAcquisitionEnabled ()
    {
      synchronized (this.byteRecordsListLock)
      {
        setAcquisitionEnabled (! this.acquisitionEnabled);
      }
    }
      
    public int total ()
    {
      synchronized (this.byteRecordsListLock)
      {
        return this.byteRecordsList.size ();
      }
    }
    
    public int current ()
    {
      synchronized (this.byteRecordsListLock)
      {
        return this.current;          
      }
    }
    
    public void first ()
    {
      final boolean needsRepaint;
      synchronized (this.byteRecordsListLock)
      {
        if (total () <= 1)
          return;
        if (this.current != 0)
        {
          this.current = 0;
          JSettingsMonitor.this.eastPanel.jCurrentTotal.setText ((current () + 1) + "/" + total ());
          needsRepaint = true;
        }
        else
          needsRepaint = false;
      }
      if (needsRepaint)
        SwingUtilities.invokeLater (() -> repaint ());
    }
    
    public void next ()
    {
      final boolean needsRepaint;
      synchronized (this.byteRecordsListLock)
      {
        if (total () <= 1)
          return;
        if (this.current < total () - 1)
        {
          this.current++;
          JSettingsMonitor.this.eastPanel.jCurrentTotal.setText ((current () + 1) + "/" + total ());
          needsRepaint = true;
        }
        else
          needsRepaint = false;
      }
      if (needsRepaint)
        SwingUtilities.invokeLater (() -> repaint ());
    }
    
    public void previous ()
    {
      synchronized (this.byteRecordsListLock)
      {
        if (total () <= 1 || current () <= 0)
          return;
        this.current--;
        JSettingsMonitor.this.eastPanel.jCurrentTotal.setText ((current () + 1) + "/" + total ());
      }
      SwingUtilities.invokeLater (() -> repaint ());      
    }
    
    public void last ()
    {
      final boolean needsRepaint;
      synchronized (this.byteRecordsListLock)
      {
        if (total () <= 1)
          return;
        if (this.current < total () - 1)
        {
          this.current = total () - 1;
          JSettingsMonitor.this.eastPanel.jCurrentTotal.setText ((current () + 1) + "/" + total ());
          needsRepaint = true;
        }
        else
          needsRepaint = false;
      }
      if (needsRepaint)
        SwingUtilities.invokeLater (() -> repaint ());
    }
    
    public void clear ()
    {
      synchronized (this.byteRecordsListLock)
      {
        this.current = 0;
        this.byteRecordsList.clear ();
        JSettingsMonitor.this.eastPanel.jCurrentTotal.setText ("-/0");
      }
      SwingUtilities.invokeLater (() -> repaint ());      
    }
    
    @Override
    public void paintComponent (final Graphics g)
    {
      super.paintComponent (g);
      final ByteRecord[] byteRecords;
      synchronized (this.byteRecordsListLock)
      {
        if (this.current < 0 || this.current >= this.byteRecordsList.size ())
          return;
        byteRecords = this.byteRecordsList.get (this.current);
      }
      if (byteRecords == null || byteRecords.length == 0)
        return;
      final Graphics2D g2d = (Graphics2D) g;
      if (g2d == null)
        return;
      final Color savedColor = g2d.getColor ();
      final int width = getWidth ();
      final int height = getHeight ();
      final int bsLength = byteRecords.length;
      final int nrRows = (int) Math.ceil (((float) bsLength) / CELLS_PER_ROW);
      // xOffset and yOffset are used to center the matrix.
      final int xOffset = (width - CELLS_PER_ROW * CELL_WIDTH) / 2;
      final int yOffset = (height - nrRows * CELL_HEIGHT) / 2;
      int i = 0;
      for (int row = 0; row < nrRows; row++)
      {
        for (int col = 0; col < CELLS_PER_ROW; col++)
        {
          final Color fillColor;
          if (i < bsLength)
            switch (byteRecords[i].status)
            {
              case EQUAL:
                fillColor = Color.green; break;
              case DIFFERS:
                fillColor = Color.red; break;
              case ADDED:
                fillColor = Color.yellow; break;
              case REMOVED:
                fillColor = Color.pink; break;
              default:
                throw new RuntimeException ();
            }
          else
            fillColor = Color.gray;
          g2d.setColor (fillColor);
          g2d.fillRect (xOffset + col * CELL_WIDTH, yOffset + row * CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT);
          i++;
        }
      }
      g2d.setColor (Color.black);
      for (int row = 0; row <= nrRows; row++)
        g2d.drawLine (xOffset, yOffset + row * CELL_HEIGHT, width - xOffset, yOffset + row * CELL_HEIGHT);
      for (int col = 0; col <= CELLS_PER_ROW; col++)
        g2d.drawLine (xOffset + col * CELL_WIDTH, yOffset, xOffset + col * CELL_WIDTH, height - yOffset);
      if (this.drawCellInfo)
      {
        final int x = this.mouseX;
        final int y = this.mouseY;
        if (x < xOffset || x > width - xOffset || y < yOffset || y > height - yOffset)
        {
          JSettingsMonitor.this.westPanel.jIndex.setText ("i=-");
          JSettingsMonitor.this.westPanel.jOldValue.setText ("old=-");
          JSettingsMonitor.this.westPanel.jNewValue.setText ("new=-");    
        }
        else
        {
          JSettingsMonitor.this.westPanel.jIndex.setText ("-");
          final int row = (y - yOffset) / CELL_HEIGHT;
          final int col = Math.min ((x - xOffset) / CELL_WIDTH, CELLS_PER_ROW - 1);
          if (row >= nrRows)
          {
            JSettingsMonitor.this.westPanel.jIndex.setText ("i=-");
            JSettingsMonitor.this.westPanel.jOldValue.setText ("old=-");
            JSettingsMonitor.this.westPanel.jNewValue.setText ("new=-");            
          }
          else
          {
            final int index = row * CELLS_PER_ROW + col;
            if (index >= bsLength)
            {
              JSettingsMonitor.this.westPanel.jIndex.setText ("i=-");
              JSettingsMonitor.this.westPanel.jOldValue.setText ("old=-");
              JSettingsMonitor.this.westPanel.jNewValue.setText ("new=-");   
            }
            else
            {
              JSettingsMonitor.this.westPanel.jIndex.setText ("i=" + index);
              // final ByteStatus byteStatus = byteRecords[index].status;
              final Byte oldValue = byteRecords[index].oldValue;
              final String oldValueString = (oldValue == null)
                ? "null"
                : Integer.toBinaryString (0x100 | (oldValue & 0xff)).substring (1);
              final Byte newValue = byteRecords[index].newValue;
              final String newValueString = (newValue == null)
                ? "null"
                : Integer.toBinaryString (0x100 | (newValue & 0xff)).substring (1);
              JSettingsMonitor.this.westPanel.jOldValue.setText ("old=" + oldValueString);
              JSettingsMonitor.this.westPanel.jNewValue.setText ("new=" + newValueString);
            }
          }
        }
      }
      else
      {
        JSettingsMonitor.this.westPanel.jIndex.setText ("i=-");
        JSettingsMonitor.this.westPanel.jOldValue.setText ("old=-");
        JSettingsMonitor.this.westPanel.jNewValue.setText ("new=-");
      }
      g2d.setColor (savedColor);
    }
    
    private boolean drawCellInfo = false;
  
    private int mouseX = -1;
  
    private int mouseY = -1;

    private final MouseAdapter mouseAdapter = new MouseAdapter ()
   {
    
      @Override
      public void mouseExited (final MouseEvent e)
      {
        JByteArrayCompare.this.drawCellInfo = false;
        JByteArrayCompare.this.repaint ();
      }

      @Override
      public void mouseEntered (final MouseEvent e)
      {
        JByteArrayCompare.this.mouseX = e.getX ();
        JByteArrayCompare.this.mouseY = e.getY ();
        JByteArrayCompare.this.drawCellInfo = true;
        JByteArrayCompare.this.repaint ();
      }

      @Override
      public void mouseMoved (final MouseEvent e)
      {
        JByteArrayCompare.this.mouseX = e.getX ();
        JByteArrayCompare.this.mouseY = e.getY ();
        JByteArrayCompare.this.repaint ();
      }

    };
  
  }
  
  private final JByteArrayCompare jByteArrayCompare;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final InstrumentListener instrumentListener = new InstrumentListener ()
  {
    
    @Override
    public void newInstrumentStatus (final Instrument instrument, final InstrumentStatus instrumentStatus)
    {
      // EMPTY
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings settings)
    {
      // EMPTY
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      // EMPTY
    }

    @Override
    public void newInstrumentDebug (
      final Instrument instrument,
      final int debugId,
      final InstrumentStatus instrumentStatus,
      final InstrumentSettings instrumentSettings,
      final InstrumentReading instrumentReading,
      final Object debugObject1,
      final Object debugObject2,
      final Object debugObject3)
    {
      if (instrument != JSettingsMonitor.this.getInstrument ()
        || debugId != InstrumentListener.INSTRUMENT_DEBUG_ID_SETTINGS_BYTES_1
        || debugObject1 == null)
        return;
      final byte[] origBytes = (byte[]) debugObject1;
      // We always pass the object with the first '%' character as first element,
      // stripping some header stuff if needed.
      // NOTE that the '%' is included in the array passed!
      final String headerString = new String (origBytes, 0, 10, Charset.forName ("US-ASCII"));
      final String[] headerParts = headerString.split ("%");
      if (headerParts.length < 2)
      {
        LOG.log (Level.WARNING, "Received illegally constructed debug object.");
        return;
      }
      final byte[] settingsBytes;
      if (headerParts[0].isEmpty ())
        settingsBytes = origBytes;
      else
        settingsBytes = Arrays.copyOfRange (origBytes, headerParts[0].length (), origBytes.length);
      SwingUtilities.invokeLater (() ->
      {
        JSettingsMonitor.this.inhibitInstrumentControl = true;
        //
        try
        {
          JSettingsMonitor.this.addBytes (settingsBytes);
        }
        finally
        {
          JSettingsMonitor.this.inhibitInstrumentControl = false;
        }
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
