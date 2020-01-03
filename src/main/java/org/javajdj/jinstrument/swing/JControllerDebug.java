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
package org.javajdj.jinstrument.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.ControllerCommand;
import org.javajdj.jinstrument.ControllerListener;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.Util;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.swing.JServiceControl;

/** A log/debug panel for a {@link Controller}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class JControllerDebug
  extends JInstrumentPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JControllerDebug.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JControllerDebug (
    final Controller controller,
    final GpibDevice device,
    final Instrument instrument,
    final int level,
    final Color subPanelColor)
  {
    super (instrument, level);
    if (controller == null || device == null || instrument == null || subPanelColor == null)
      throw new IllegalArgumentException ();
    this.controller = controller;
    this.device = device;
    this.instrument = instrument;
    setOpaque (true);
    //
    setPanelBorder (Color.black, "Controller Debug Panel");
    setLayout (new BorderLayout ());
    //
    final JPanel northPanel = new JPanel ();
    northPanel.setLayout (new BorderLayout ());
    //
    final JPanel northNorthPanel = new JPanel ();
    northNorthPanel.setLayout (new FlowLayout (FlowLayout.LEFT));
    //
    final JPanel jTinyCDI = new JTinyCDIStatusAndControl (controller, device, instrument, level + 1, subPanelColor);
    jTinyCDI.setMinimumSize (new Dimension (40, 100));
    jTinyCDI.setPreferredSize (new Dimension (40, 100));
    northNorthPanel.add (jTinyCDI);
    //
    final JDefaultInstrumentManagementUrlsView jurls = new JDefaultInstrumentManagementUrlsView (instrument);
    jurls.setMinimumSize (new Dimension (600, 100));
    jurls.setPreferredSize (new Dimension (600, 100));
    northNorthPanel.add (jurls);
    //
    final JPanel jControllerLogger = new JPanel ();
    setPanelBorder (jControllerLogger, level + 2, subPanelColor, "Controller Logger");
    jControllerLogger.setLayout (new GridLayout (1, 1));
    jControllerLogger.setMinimumSize (new Dimension (200, 100));
    jControllerLogger.setPreferredSize (new Dimension (200, 100));
    final Service controllerLoggerService = this.controller.getLoggingService ();
    if (controllerLoggerService != null)
    {
      final JServiceControl controllerLoggerServiceCheckBox =
        new JServiceControl (controllerLoggerService, (Service.Status t) ->
        {
          switch (t)
          {
            case STOPPED: return null;
            case ACTIVE:  return Color.green;
            case ERROR:   return Color.orange;
            default:      throw new RuntimeException ();
          }
        });
      controllerLoggerServiceCheckBox.setHorizontalAlignment (SwingConstants.CENTER);
      controllerLoggerServiceCheckBox.setVerticalAlignment (SwingConstants.CENTER);
      jControllerLogger.add (controllerLoggerServiceCheckBox);
    }
    else
    {
      final JLabel jUnsupportedLabel = new JLabel ("Unsupported");
      jUnsupportedLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jUnsupportedLabel.setVerticalAlignment (SwingConstants.CENTER);
      jControllerLogger.add (jUnsupportedLabel);
    }
    northNorthPanel.add (jControllerLogger);
    //
    final JPanel buttonPanel = new JPanel ();
    setPanelBorder (buttonPanel, level + 2, subPanelColor, "Actions");
    buttonPanel.setLayout (new GridLayout (3, 3)); // For now, to keep single button centered and not being blown up.
    buttonPanel.setMinimumSize (new Dimension (300, 100));
    buttonPanel.setPreferredSize (new Dimension (300, 100));
    buttonPanel.add (new JLabel ());
    buttonPanel.add (new JLabel ());
    buttonPanel.add (new JLabel ());
    buttonPanel.add (new JLabel ());
    final JButton clearLogButton = new JButton ("Clear");
    buttonPanel.add (clearLogButton);
    buttonPanel.add (new JLabel ());
    buttonPanel.add (new JLabel ());
    buttonPanel.add (new JLabel ());
    buttonPanel.add (new JLabel ());
    clearLogButton.addActionListener ((final ActionEvent ae) ->
    {
      JControllerDebug.this.logEntries.clear ();
      JControllerDebug.this.logTableModel.fireTableDataChanged ();
    });
    northNorthPanel.add (buttonPanel);
    //
    northPanel.add (northNorthPanel, BorderLayout.NORTH);    
    //
    final JTextField jTextField = new JTextField ("To Be Implemented!", 80);
    jTextField.setEditable (false);
    setPanelBorder (jTextField, level + 2, subPanelColor, "Input");
    northPanel.add (jTextField, BorderLayout.SOUTH);
    //
    add (northPanel, BorderLayout.NORTH);
    //
    this.jLogPanel = new JLogPanel ();
    setPanelBorder (this.jLogPanel, level + 1, subPanelColor, "Log Entries");
    add (this.jLogPanel, BorderLayout.CENTER);
    this.controller.addControllerListener (this.controllerListener);
  }
  
  public JControllerDebug (
    final Instrument instrument,
    final int level,
    final Color subPanelColor)
  {
    this (instrument.getDevice ().getController (),
      (GpibDevice) instrument.getDevice (),
      instrument,
      level,
      subPanelColor);
  }
  
  public JControllerDebug (
    final Instrument instrument,
    final int level)
  {
    this (instrument.getDevice ().getController (),
      (GpibDevice) instrument.getDevice (),
      instrument,
      level,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR);
  }
  
  public JControllerDebug (final Instrument instrument)
  {
    this (instrument.getDevice ().getController (),
      (GpibDevice) instrument.getDevice (),
      instrument,
      1,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR);
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
      return "Controller Debug Panel";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null)
        return new JControllerDebug (instrument);
      else
        return null;
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentView
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getInstrumentViewUrl ()
  {
    return JControllerDebug.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // CONTROLLER
  // GPIB DEVICE
  // INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  private final Controller controller;
  
  private final GpibDevice device;
  
  private final Instrument instrument;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOG ENTRIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final List<ControllerListener.LogEntry> logEntries = new ArrayList<> ();
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final ControllerListener controllerListener = new ControllerListener ()
  {
    
    @Override
    public void processedCommandNotification (final Controller controller, final ControllerCommand controllerCommand)
    {
    }

    @Override
    public void controllerLogEntry (final Controller controller, final ControllerListener.LogEntry logEntry)
    {
      SwingUtilities.invokeLater (() ->
      {
        JControllerDebug.this.logEntries.add (logEntry);
        JControllerDebug.this.logTableModel.fireTableDataChanged ();
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOG PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final DefaultTableModel logTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final ControllerListener.LogEntry logEntry = JControllerDebug.this.logEntries.get (r);
        switch (c)
        {
          case 0:
            return logEntry.instant;
          case 1:
            return logEntry.logEntryType;
          case 2:
            return logEntry.controllerCommand;
          case 3:
            if (logEntry.controllerCommand != null)
              return logEntry.controllerCommand.get (ControllerCommand.CC_COMMAND_KEY);
            else
              return null;
          case 4:
            if (logEntry.controllerCommand != null)
              return logEntry.controllerCommand.get (ControllerCommand.CCRET_EXCEPTION_KEY);
            else
              return null;
          case 5:
            return logEntry.message;
          case 6:
          {
            final byte[] bytes = logEntry.bytes;
            return bytes != null ? Util.bytesToHex (bytes) : null;
          }
          case 7:
          {
            final byte[] bytes = logEntry.bytes;
            return bytes != null ? new String (bytes, Charset.forName ("US-ASCII")) : null;
          }
          default:
            throw new RuntimeException ();
        }
      }
      else
        return null;
    }

    @Override
    public boolean isCellEditable (final int r, final int c)
    {
      return false;
    }

    @Override
    public String getColumnName (int i)
    {
      switch (i)
      {
        case 0:
          return "Date";
        case 1:
          return "Type";
        case 2:
          return "Command Object";
        case 3:
          return "Command Name";
        case 4:
          return "Command Exception";
        case 5:
          return "Message";
        case 6:
          return "Bytes [HEX]";
        case 7:
          return "Bytes [ASCII]";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 8;
    }

    @Override
    public int getRowCount ()
    {
      if (JControllerDebug.this.logEntries != null)
        return JControllerDebug.this.logEntries.size ();
      else
        return 0;
    }
    
  };
  
  private final JTable logTable = new JTable (this.logTableModel);
  
  private class JLogPanel
    extends JPanel
  {
    
    public JLogPanel ()
    {
      setLayout (new GridLayout (1, 1));
      JControllerDebug.this.logTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      JControllerDebug.this.logTable.addMouseListener (JControllerDebug.this.logTableMouseListener);
      add (new JScrollPane (JControllerDebug.this.logTable));
    }
    
  }
  
  private final JLogPanel jLogPanel;
  
  private final MouseListener logTableMouseListener = new MouseAdapter ()
  {
    @Override
    public void mousePressed (final MouseEvent mouseEvent)
    {
      final JTable table = (JTable) mouseEvent.getSource ();
      if (table != JControllerDebug.this.logTable)
        return;
      final Point point = mouseEvent.getPoint ();
      final int row = table.rowAtPoint (point);
      if (mouseEvent.getClickCount () == 2 && table.getSelectedRow () != -1)
      {
        if (row < 0 && row >= JControllerDebug.this.logEntries.size ())
          return;
        final ControllerListener.LogEntry logEntry = JControllerDebug.this.logEntries.get (row);
        if (logEntry == null)
          return;
        final String commandString = (logEntry.controllerCommand == null ? null : (""
          + "Command             : " + logEntry.controllerCommand.get (ControllerCommand.CC_COMMAND_KEY) + "\n"
          + "  Arrrival          : " + logEntry.controllerCommand.getArriveAtControllerTime () + "\n"
          + "  Start             : " + logEntry.controllerCommand.getStartAtControllerTime () + "\n"
          + "  Departure         : " + logEntry.controllerCommand.getDepartureAtControllerTime () + "\n"
          + "  Queueing Timeout  : " + logEntry.controllerCommand.getQueueingTimeout () + "\n"
          + "  Processing Timeout: " + logEntry.controllerCommand.getProcessingTimeout () + "\n"
          + "  Sojourn Timeout   : " + logEntry.controllerCommand.getSojournTimeout () + "\n"
          + "  Return Status     : " + logEntry.controllerCommand.get (ControllerCommand.CCRET_STATUS_KEY) + "\n"
          + "  Return Exception  : " + logEntry.controllerCommand.get (ControllerCommand.CCRET_EXCEPTION_KEY) + "\n"
          + "  Return Value      : " + logEntry.controllerCommand.get (ControllerCommand.CCRET_VALUE_KEY) + "\n"
          ));
        final String bytesAsciiString = (logEntry.bytes == null ? null : ("\n"
          + "Bytes [ASCII]:\n\n"
          + new String (logEntry.bytes, Charset.forName ("US-ASCII")) + "\n"
          ));
        final String bytesHexString = (logEntry.bytes == null ? null : ("\n"
          + "Bytes [HEX]:\n\n"
          + Util.bytesToHex (logEntry.bytes) + "\n\n"
          ));
        final JTextArea jTextArea = new JTextArea (""
          + "Row                 : " + row + "\n"
          + "Date                : " + logEntry.instant + "\n"
          + "Type                : " + logEntry.logEntryType + "\n"
          + (commandString != null ? commandString : "")
          + "Message             : " + logEntry.message + "\n"
          + (bytesAsciiString != null ? bytesAsciiString : "")
          + (bytesHexString != null ? bytesHexString : "")
          );
        jTextArea.setFont (new Font (Font.MONOSPACED, Font.PLAIN, 14));
        jTextArea.setColumns (80);
        jTextArea.setRows (32);
        jTextArea.setOpaque (false);
        jTextArea.setEditable (false);
        jTextArea.setLineWrap (true);
        JOptionPane.showMessageDialog (JControllerDebug.this, jTextArea, "Log Entry " + row, JOptionPane.INFORMATION_MESSAGE);
      }
    }    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
