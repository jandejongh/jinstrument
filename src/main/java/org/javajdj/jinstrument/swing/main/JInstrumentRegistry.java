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
package org.javajdj.jinstrument.swing.main;

import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.javajdj.jinstrument.Bus;
import org.javajdj.jinstrument.BusType;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.ControllerType;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.DeviceType;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentRegistry;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;

/** A Swing component for interacting with a (the) {@link InstrumentRegistry}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JInstrumentRegistry
  extends JInstrumentPanel
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public JInstrumentRegistry (final InstrumentRegistry instrumentRegistry, final int level)
  {
    //
    super (null, level);
    //
    this.instrumentRegistry = instrumentRegistry;
    //
    setLayout (new GridLayout (4, 1));
    //
    // Types
    //
    final JPanel jTypesPanel = new JPanel ();
//    JInstrumentPanel.setPanelBorder (
//      jTypesPanel,
//      level + 1,
//      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
//      "Types");
    jTypesPanel.setLayout (new GridLayout (1, 5));
    this.jBusTypePanel = new JBusTypePanel ();
    JInstrumentPanel.setPanelBorder (
      this.jBusTypePanel,
      level + 2,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
      "Bus Types");
    jTypesPanel.add (this.jBusTypePanel);
    this.jControllerTypePanel = new JControllerTypePanel ();
    JInstrumentPanel.setPanelBorder (
      this.jControllerTypePanel,
      level + 2,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
      "Controller Types");
    jTypesPanel.add (this.jControllerTypePanel);
    this.jDeviceTypePanel = new JDeviceTypePanel ();
    JInstrumentPanel.setPanelBorder (
      this.jDeviceTypePanel,
      level + 2,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
      "Device Types");
    jTypesPanel.add (this.jDeviceTypePanel);
    this.jInstrumentTypePanel = new JInstrumentTypePanel ();
    JInstrumentPanel.setPanelBorder (
      this.jInstrumentTypePanel,
      level + 2,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
      "Instrument Types");
    jTypesPanel.add (this.jInstrumentTypePanel);
    this.jInstrumentViewTypePanel = new JInstrumentViewTypePanel ();
    JInstrumentPanel.setPanelBorder (
      this.jInstrumentViewTypePanel,
      level + 2,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
      "Instrument View Types");
    jTypesPanel.add (this.jInstrumentViewTypePanel);
    add (jTypesPanel);
    //
    // Instances
    //
    final JPanel jBusesControllersDevicesPanel = new JPanel ();
//    JInstrumentPanel.setPanelBorder (jBusesControllersDevicesPanel,
//      level + 1,
//      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
//      "");
    jBusesControllersDevicesPanel.setLayout (new GridLayout (1, 3));
    this.jBusesPanel = new JBusesPanel ();
    JInstrumentPanel.setPanelBorder (
      this.jBusesPanel,
      level + 2,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
      "Buses");
    jBusesControllersDevicesPanel.add (this.jBusesPanel);   
    this.jControllersPanel = new JControllersPanel ();
    JInstrumentPanel.setPanelBorder (
      this.jControllersPanel,
      level + 2,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
      "Controllers");
    jBusesControllersDevicesPanel.add (this.jControllersPanel);
    this.jDevicesPanel = new JDevicesPanel ();
    JInstrumentPanel.setPanelBorder (
      this.jDevicesPanel,
      level + 2,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
      "Devices");
    jBusesControllersDevicesPanel.add (this.jDevicesPanel);
    add (jBusesControllersDevicesPanel);
    //
    // Instruments
    //
    this.jInstrumentsPanel = new JInstrumentsPanel ();
    JInstrumentPanel.setPanelBorder (
      this.jInstrumentsPanel,
      level + 1,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
      "Instruments");
    add (this.jInstrumentsPanel);
    //
    // Instrument Views
    //
    this.jInstrumentViewsPanel = new JInstrumentViewsPanel ();
    JInstrumentPanel.setPanelBorder (
      this.jInstrumentViewsPanel,
      level + 1,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR,
      "Instrument Views");
    add (this.jInstrumentViewsPanel);
    //
    if (this.instrumentRegistry != null)
      this.instrumentRegistry.addRegistryListener (this.instrumentRegistryListener);
    //
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT REGISTRY
  // INSTRUMENT REGISTRY LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private InstrumentRegistry instrumentRegistry;
  
  public final void setInstrumentRegistry (final InstrumentRegistry instrumentRegistry)
  {
    if (this.instrumentRegistry != null)
      this.instrumentRegistry.removeRegistryListener (this.instrumentRegistryListener);
    this.instrumentRegistry = instrumentRegistry;
    if (this.instrumentRegistry != null)
      this.instrumentRegistry.addRegistryListener (this.instrumentRegistryListener);
    this.instrumentRegistryListener.instrumentRegistryChanged (this.instrumentRegistry);
  }
  
  private final InstrumentRegistry.Listener instrumentRegistryListener = ((final InstrumentRegistry instrumentRegistry) ->
  {
    if (instrumentRegistry != JInstrumentRegistry.this.instrumentRegistry)
      return;
    SwingUtilities.invokeLater (() ->
    {
      JInstrumentRegistry.this.busTypesTableModel.fireTableDataChanged ();
      JInstrumentRegistry.this.controllerTypesTableModel.fireTableDataChanged ();
      JInstrumentRegistry.this.deviceTypesTableModel.fireTableDataChanged ();
      JInstrumentRegistry.this.instrumentTypesTableModel.fireTableDataChanged ();
      JInstrumentRegistry.this.instrumentViewTypesTableModel.fireTableDataChanged ();
      JInstrumentRegistry.this.busesTableModel.fireTableDataChanged ();
      JInstrumentRegistry.this.controllersTableModel.fireTableDataChanged ();
      JInstrumentRegistry.this.devicesTableModel.fireTableDataChanged ();
      JInstrumentRegistry.this.instrumentsTableModel.fireTableDataChanged ();
      JInstrumentRegistry.this.instrumentViewsTableModel.fireTableDataChanged ();
    });
  });

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BUS TYPE PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultTableModel busTypesTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final BusType busType = JInstrumentRegistry.this.instrumentRegistry.getBusTypes ().get (r);
        switch (c)
        {
          case 0:
            return busType.getBusTypeUrl ();
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
          return "URL";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 1;
    }

    @Override
    public int getRowCount ()
    {
      if (JInstrumentRegistry.this.instrumentRegistry != null
        && JInstrumentRegistry.this.instrumentRegistry.getBusTypes () != null)
        return JInstrumentRegistry.this.instrumentRegistry.getBusTypes ().size ();
      else
        return 0;
    }
    
  };
  
  private final JTable busTypesTable = new JTable (this.busTypesTableModel);
  
  private class JBusTypePanel
    extends JPanel
  {

    public JBusTypePanel ()
    {
      setLayout (new GridLayout (1, 1));
      JInstrumentRegistry.this.busTypesTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      add (new JScrollPane (JInstrumentRegistry.this.busTypesTable));
    }
    
  }
  
  private final JBusTypePanel jBusTypePanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER TYPE PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultTableModel controllerTypesTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final ControllerType controllerType = JInstrumentRegistry.this.instrumentRegistry.getControllerTypes ().get (r);
        switch (c)
        {
          case 0:
            return controllerType.getControllerTypeUrl ();
          case 1:
            return controllerType.getBusType ().getBusTypeUrl ();
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
          return "URL";
        case 1:
          return "Bus Type";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 2;
    }

    @Override
    public int getRowCount ()
    {
      if (JInstrumentRegistry.this.instrumentRegistry != null
        && JInstrumentRegistry.this.instrumentRegistry.getControllerTypes () != null)
        return JInstrumentRegistry.this.instrumentRegistry.getControllerTypes ().size ();
      else
        return 0;
    }
    
  };
  
  private final JTable controllerTypesTable = new JTable (this.controllerTypesTableModel);
  
  private class JControllerTypePanel
    extends JPanel
  {
    
    public JControllerTypePanel ()
    {
      setLayout (new GridLayout (1, 1));
      JInstrumentRegistry.this.controllerTypesTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      JInstrumentRegistry.this.controllerTypesTable.addMouseListener (JInstrumentRegistry.this.controllerTypesPanelMouseListener);
      add (new JScrollPane (JInstrumentRegistry.this.controllerTypesTable));
    }
    
  }
  
  private final JControllerTypePanel jControllerTypePanel;
  
  private final MouseListener controllerTypesPanelMouseListener = new MouseAdapter ()
  {
    @Override
    public void mousePressed (final MouseEvent mouseEvent)
    {
      final JTable table = (JTable) mouseEvent.getSource ();
      final Point point = mouseEvent.getPoint ();
      final int row = table.rowAtPoint (point);
      if (mouseEvent.getClickCount () == 2 && table.getSelectedRow () != -1)
      {
        final ControllerType controllerType =
          JInstrumentRegistry.this.instrumentRegistry.getControllerTypes ().get (row);
        if (controllerType == null)
          return;
        final JTextField jtfControllerUrl = new JTextField (controllerType.getControllerTypeUrl () + "://");
        final JTextField jtfUserControllerName = new JTextField ();
        final Object[] displayObjects =
        {
          "Controller URL", jtfControllerUrl,
          "User Instrument Name", jtfUserControllerName
        };
        final int option = JOptionPane.showConfirmDialog (
               null,
               displayObjects,
               "New Controller",
               JOptionPane.OK_CANCEL_OPTION,
               JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION)
          return;
        final String controllerUrl = jtfControllerUrl.getText ();
        if (controllerUrl == null || controllerUrl.trim ().isEmpty ())
        {
          JOptionPane.showConfirmDialog (null,
            "No Controller URL entered!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        final String userControllerName = jtfUserControllerName.getText ();
        if (userControllerName == null || userControllerName.trim ().isEmpty ())
        {
          JOptionPane.showConfirmDialog (null,
            "No User Controller Name entered!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        final Controller controller = JInstrumentRegistry.this.instrumentRegistry.openController (controllerUrl);
        if (controller == null)
        {
          JOptionPane.showConfirmDialog (null,
            "Could not open Controller with URL " + controllerUrl + "!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          // return;
        }
      }
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEVICE TYPE PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultTableModel deviceTypesTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final DeviceType deviceType = JInstrumentRegistry.this.instrumentRegistry.getDeviceTypes ().get (r);
        switch (c)
        {
          case 0:
            return deviceType.getClass ().getSimpleName (); // XXX deviceType -> name??
          case 1:
            return deviceType.getBusType ().getBusTypeUrl ();
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
          return "URL";
        case 1:
          return "Bus Type";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 2;
    }

    @Override
    public int getRowCount ()
    {
      if (JInstrumentRegistry.this.instrumentRegistry != null
        && JInstrumentRegistry.this.instrumentRegistry.getDeviceTypes () != null)
        return JInstrumentRegistry.this.instrumentRegistry.getDeviceTypes ().size ();
      else
        return 0;
    }
    
  };
  
  private final JTable deviceTypesTable = new JTable (this.deviceTypesTableModel);
  
  private class JDeviceTypePanel
    extends JPanel
  {
    
    public JDeviceTypePanel ()
    {
      setLayout (new GridLayout (1, 1));
      JInstrumentRegistry.this.deviceTypesTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      JInstrumentRegistry.this.deviceTypesTable.addMouseListener (JInstrumentRegistry.this.deviceTypesPanelMouseListener);
      add (new JScrollPane (JInstrumentRegistry.this.deviceTypesTable));
    }
    
  }
  
  private final JDeviceTypePanel jDeviceTypePanel;
  
  private final MouseListener deviceTypesPanelMouseListener = new MouseAdapter ()
  {
    @Override
    public void mousePressed (final MouseEvent mouseEvent)
    {
      final JTable table = (JTable) mouseEvent.getSource ();
      final Point point = mouseEvent.getPoint ();
      final int row = table.rowAtPoint (point);
      if (mouseEvent.getClickCount () == 2 && table.getSelectedRow () != -1)
      {
        final DeviceType deviceType =
          JInstrumentRegistry.this.instrumentRegistry.getDeviceTypes ().get (row);
        if (deviceType == null)
          return;
        final List<Bus> buses = JInstrumentRegistry.this.instrumentRegistry.getBuses ();
        if (buses == null || buses.isEmpty ())
        {
          JOptionPane.showConfirmDialog (
            null,
            "No Buses!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        final JComboBox<Bus> jcbBuses = new JComboBox (buses.toArray ());
        final JTextField jtfBusAddress = new JTextField ();
        final Object[] displayObjects =
        {
          "Bus", jcbBuses,
          "Bus Address", jtfBusAddress
        };
        final int option = JOptionPane.showConfirmDialog (
               null,
               displayObjects,
               "New Device",
               JOptionPane.OK_CANCEL_OPTION,
               JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION)
          return;
        final Bus bus = (Bus) jcbBuses.getSelectedItem ();
        if (bus == null)
        {
          JOptionPane.showConfirmDialog (null,
            "No Bus selected!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        final String busAddressUrl = jtfBusAddress.getText ();
        if (busAddressUrl == null || busAddressUrl.trim ().isEmpty ())
        {
          JOptionPane.showConfirmDialog (null,
            "No Bus Address entered!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        final Device device = JInstrumentRegistry.this.instrumentRegistry.openDevice (deviceType, bus, busAddressUrl);
        if (device == null)
        {
          JOptionPane.showConfirmDialog (
            null,
            "Could not open Device with Type " + deviceType.getDeviceTypeUrl ()
              + " on Bus " + bus.getBusUrl ()
              + " with Bus Address " + busAddressUrl + ".",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          // return;
        }
      }
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT TYPE PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultTableModel instrumentTypesTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final InstrumentType instrumentType = JInstrumentRegistry.this.instrumentRegistry.getInstrumentTypes ().get (r);
        switch (c)
        {
          case 0:
            return instrumentType.getInstrumentTypeUrl ();
          case 1:
            return instrumentType.getDeviceType ().getClass ().getSimpleName (); // XXX deviceType -> name??
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
          return "URL";
        case 1:
          return "Device Type";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 2;
    }

    @Override
    public int getRowCount ()
    {
      if (JInstrumentRegistry.this.instrumentRegistry != null
        && JInstrumentRegistry.this.instrumentRegistry.getInstrumentTypes () != null)
        return JInstrumentRegistry.this.instrumentRegistry.getInstrumentTypes ().size ();
      else
        return 0;
    }
    
  };
  
  private final JTable instrumentTypesTable = new JTable (this.instrumentTypesTableModel);
  
  private class JInstrumentTypePanel
    extends JPanel
  {
    
    public JInstrumentTypePanel ()
    {
      setLayout (new GridLayout (1, 1));
      JInstrumentRegistry.this.instrumentTypesTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      JInstrumentRegistry.this.instrumentTypesTable.addMouseListener (JInstrumentRegistry.this.instrumentTypesPanelMouseListener);
      add (new JScrollPane (JInstrumentRegistry.this.instrumentTypesTable));
    }
    
  }
  
  private final JInstrumentTypePanel jInstrumentTypePanel;
  
  private final MouseListener instrumentTypesPanelMouseListener = new MouseAdapter ()
  {
    @Override
    public void mousePressed (final MouseEvent mouseEvent)
    {
      final JTable table = (JTable) mouseEvent.getSource ();
      final Point point = mouseEvent.getPoint ();
      final int row = table.rowAtPoint (point);
      if (mouseEvent.getClickCount () == 2 && table.getSelectedRow () != -1)
      {
        final InstrumentType instrumentType =
          JInstrumentRegistry.this.instrumentRegistry.getInstrumentTypes ().get (row);
        if (instrumentType == null)
          return;
        final List<Device> devices = JInstrumentRegistry.this.instrumentRegistry.getDevices ();
        if (devices == null || devices.isEmpty ())
        {
          JOptionPane.showConfirmDialog (
            null,
            "No Devices!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        final JComboBox<Device> jcbDevices = new JComboBox (devices.toArray ());
        final JTextField jtfUserInstrumentName = new JTextField ();
        final Object[] displayObjects =
        {
          "Device", jcbDevices,
          "User Instrument Name", jtfUserInstrumentName
        };
        final int option = JOptionPane.showConfirmDialog (
               null,
               displayObjects,
               "New Instrument",
               JOptionPane.OK_CANCEL_OPTION,
               JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION)
          return;
        final Device device = (Device) jcbDevices.getSelectedItem ();
        if (device == null)
        {
          JOptionPane.showConfirmDialog (null,
            "No Device selected!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        final String userInstrumentName = jtfUserInstrumentName.getText ();
        if (userInstrumentName == null || userInstrumentName.trim ().isEmpty ())
        {
          JOptionPane.showConfirmDialog (null,
            "No User Instrument Name entered!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        final Instrument instrument = JInstrumentRegistry.this.instrumentRegistry.openInstrument (instrumentType, device);
        if (instrument == null)
        {
          JOptionPane.showConfirmDialog (
            null,
            "Could not open Instrument with Type " + instrumentType.getInstrumentTypeUrl ()
              + " for Device " + device.getDeviceUrl () + ".",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          // return;
        }
      }
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT VIEW TYPE PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultTableModel instrumentViewTypesTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final InstrumentViewType instrumentViewType =
          JInstrumentRegistry.this.instrumentRegistry.getInstrumentViewTypes ().get (r);
        switch (c)
        {
          case 0:
            return instrumentViewType.getInstrumentViewTypeUrl ();
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
          return "URL";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 1;
    }

    @Override
    public int getRowCount ()
    {
      if (JInstrumentRegistry.this.instrumentRegistry != null
        && JInstrumentRegistry.this.instrumentRegistry.getInstrumentViewTypes () != null)
        return JInstrumentRegistry.this.instrumentRegistry.getInstrumentViewTypes ().size ();
      else
        return 0;
    }
    
  };
  
  private final JTable instrumentViewTypesTable = new JTable (this.instrumentViewTypesTableModel);
  
  private class JInstrumentViewTypePanel
    extends JPanel
  {
    
    public JInstrumentViewTypePanel ()
    {
      setLayout (new GridLayout (1, 1));
      JInstrumentRegistry.this.instrumentViewTypesTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      JInstrumentRegistry.this.instrumentViewTypesTable.addMouseListener (
        JInstrumentRegistry.this.instrumentViewTypePanelMouseListener);
      add (new JScrollPane (JInstrumentRegistry.this.instrumentViewTypesTable));
    }
    
  }
  
  private final JInstrumentViewTypePanel jInstrumentViewTypePanel;
  
  private final MouseListener instrumentViewTypePanelMouseListener = new MouseAdapter ()
  {
    @Override
    public void mousePressed (final MouseEvent mouseEvent)
    {
      final JTable table = (JTable) mouseEvent.getSource ();
      final Point point = mouseEvent.getPoint ();
      final int row = table.rowAtPoint (point);
      if (mouseEvent.getClickCount () == 2 && table.getSelectedRow () != -1)
      {
        final InstrumentViewType instrumentViewType =
          JInstrumentRegistry.this.instrumentRegistry.getInstrumentViewTypes ().get (row);
        if (instrumentViewType == null)
          return;
        final List<Instrument> instruments = JInstrumentRegistry.this.instrumentRegistry.getInstruments ();
        if (instruments == null || instruments.isEmpty ())
        {
          JOptionPane.showConfirmDialog (
            null,
            "No Instruments!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        final String[] instrumentStrings = new String[instruments.size ()];
        for (int i = 0; i < instruments.size (); i++)
          instrumentStrings[i] = instruments.get (i).getInstrumentUrl ();
        final String instrumentUrl = (String) JOptionPane.showInputDialog (
               null,
               "Select Instrument for " + instrumentViewType.getInstrumentViewTypeUrl (),
               "New Instrument View Type",
               JOptionPane.PLAIN_MESSAGE,
               null,            
               instrumentStrings,
               instrumentStrings[0]);
        if (instrumentUrl == null)
          return;
        final Instrument instrument = JInstrumentRegistry.this.instrumentRegistry.getInstrumentByUrl (instrumentUrl);
        if (instrument == null)
        {
          JOptionPane.showConfirmDialog (null,
            "Instrument " + instrumentUrl + " not found!",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        final InstrumentView instrumentView =
          JInstrumentRegistry.this.instrumentRegistry.openInstrumentView (instrumentViewType, instrument);
        if (instrumentView == null)
        {
          JOptionPane.showConfirmDialog (
            null,
            "Could not open Instrument View Type " + instrumentViewType.getInstrumentViewTypeUrl ()
              + " for Instrument " + instrumentUrl + ".",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
          return;
        }
        if (! (instrumentView instanceof JComponent))
        {
          JOptionPane.showConfirmDialog (
            null,
            "Instrument View " + instrumentView.getInstrumentViewUrl () + " opened, but cannot display (no JComponent).",
            "Warning",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);
        }
      }
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BUSES PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultTableModel busesTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final Bus bus = JInstrumentRegistry.this.instrumentRegistry.getBuses ().get (r);
        switch (c)
        {
          case 0:
            return bus.getBusUrl ();
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
          return "URL";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 1;
    }

    @Override
    public int getRowCount ()
    {
      if (JInstrumentRegistry.this.instrumentRegistry != null
        && JInstrumentRegistry.this.instrumentRegistry.getBuses ()!= null)
        return JInstrumentRegistry.this.instrumentRegistry.getBuses ().size ();
      else
        return 0;
    }
    
  };
  
  private final JTable busesTable = new JTable (this.busesTableModel);
  
  private class JBusesPanel
    extends JPanel
  {
    
    public JBusesPanel ()
    {
      setLayout (new GridLayout (1, 1));
      JInstrumentRegistry.this.busesTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      add (new JScrollPane (JInstrumentRegistry.this.busesTable));
    }
    
  }
  
  private final JBusesPanel jBusesPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultTableModel controllersTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final Controller controller = JInstrumentRegistry.this.instrumentRegistry.getControllers ().get (r);
        switch (c)
        {
          case 0:
            return controller.getControllerUrl ();
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
          return "URL";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 1;
    }

    @Override
    public int getRowCount ()
    {
      if (JInstrumentRegistry.this.instrumentRegistry != null
        && JInstrumentRegistry.this.instrumentRegistry.getControllers ()!= null)
        return JInstrumentRegistry.this.instrumentRegistry.getControllers ().size ();
      else
        return 0;
    }
    
  };
  
  private final JTable controllersTable = new JTable (this.controllersTableModel);
  
  private class JControllersPanel
    extends JPanel
  {
    
    public JControllersPanel ()
    {
      setLayout (new GridLayout (1, 1));
      JInstrumentRegistry.this.controllersTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      add (new JScrollPane (JInstrumentRegistry.this.controllersTable));
    }
    
  }
  
  private final JControllersPanel jControllersPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEVICE PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultTableModel devicesTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final Device device = JInstrumentRegistry.this.instrumentRegistry.getDevices ().get (r);
        switch (c)
        {
          case 0:
            return device.getDeviceUrl ();
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
          return "URL";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 1;
    }

    @Override
    public int getRowCount ()
    {
      if (JInstrumentRegistry.this.instrumentRegistry != null
        && JInstrumentRegistry.this.instrumentRegistry.getDevices ()!= null)
        return JInstrumentRegistry.this.instrumentRegistry.getDevices ().size ();
      else
        return 0;
    }
    
  };
  
  private final JTable devicesTable = new JTable (this.devicesTableModel);
  
  private class JDevicesPanel
    extends JPanel
  {
    
    public JDevicesPanel ()
    {
      setLayout (new GridLayout (1, 1));
      JInstrumentRegistry.this.devicesTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      add (new JScrollPane (JInstrumentRegistry.this.devicesTable));
    }
    
  }
  
  private final JDevicesPanel jDevicesPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultTableModel instrumentsTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final Instrument instrument = JInstrumentRegistry.this.instrumentRegistry.getInstruments ().get (r);
        switch (c)
        {
          case 0:
            return instrument.getInstrumentUrl ();
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
          return "URL";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 1;
    }

    @Override
    public int getRowCount ()
    {
      if (JInstrumentRegistry.this.instrumentRegistry != null
        && JInstrumentRegistry.this.instrumentRegistry.getInstruments ()!= null)
        return JInstrumentRegistry.this.instrumentRegistry.getInstruments ().size ();
      else
        return 0;
    }
    
  };
  
  private final JTable instrumentsTable = new JTable (this.instrumentsTableModel);
  
  private class JInstrumentsPanel
    extends JPanel
  {
    
    public JInstrumentsPanel ()
    {
      setLayout (new GridLayout (1, 1));
      JInstrumentRegistry.this.instrumentsTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      add (new JScrollPane (JInstrumentRegistry.this.instrumentsTable));
    }
    
  }
  
  private final JInstrumentsPanel jInstrumentsPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT VIEW PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultTableModel instrumentViewsTableModel = new DefaultTableModel ()
  {
    
    @Override
    public Object getValueAt (final int r, final int c)
    {
      if (r >= 0 && r < getRowCount ())
      {
        final InstrumentView instrumentView = JInstrumentRegistry.this.instrumentRegistry.getInstrumentViews ().get (r);
        switch (c)
        {
          case 0:
            return instrumentView.getInstrumentViewUrl ();
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
          return "URL";
        default:
          throw new RuntimeException ();
      }
    }

    @Override
    public int getColumnCount ()
    {
      return 1;
    }

    @Override
    public int getRowCount ()
    {
      if (JInstrumentRegistry.this.instrumentRegistry != null
        && JInstrumentRegistry.this.instrumentRegistry.getInstrumentViews ()!= null)
        return JInstrumentRegistry.this.instrumentRegistry.getInstrumentViews ().size ();
      else
        return 0;
    }
    
  };
  
  private final JTable instrumentViewsTable = new JTable (this.instrumentViewsTableModel);
  
  private class JInstrumentViewsPanel
    extends JPanel
  {
    
    public JInstrumentViewsPanel ()
    {
      setLayout (new BoxLayout (this, BoxLayout.X_AXIS));
      JInstrumentRegistry.this.instrumentViewsTable.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
      JInstrumentRegistry.this.instrumentViewsTable.addMouseListener (JInstrumentRegistry.this.instrumentViewsPanelMouseListener);
      add (new JScrollPane (JInstrumentRegistry.this.instrumentViewsTable));
      final JPanel buttonPanel = new JPanel ();
      setPanelBorder (buttonPanel, 2, Color.black, "Operations [on Selection]");
      buttonPanel.setLayout (new FlowLayout (FlowLayout.CENTER));
      final JButton removeButton = new JButton ("Close");
      removeButton.addActionListener ((final ActionEvent ae) ->
      {
        final int index = JInstrumentRegistry.this.instrumentViewsTable.getSelectedRow ();
        if (index < 0)
          return;
        final List<InstrumentView> instrumentViews = JInstrumentRegistry.this.instrumentRegistry.getInstrumentViews ();
        if (index >= instrumentViews.size ())
          return;
        JInstrumentRegistry.this.instrumentRegistry.closeInstrumentView (instrumentViews.get (index));
      });
      buttonPanel.add (removeButton);
      add (buttonPanel);
    }
    
  }
  
  private final JInstrumentViewsPanel jInstrumentViewsPanel;
  
  private final MouseListener instrumentViewsPanelMouseListener = new MouseAdapter ()
  {
    @Override
    public void mousePressed (final MouseEvent mouseEvent)
    {
      final JTable table = (JTable) mouseEvent.getSource ();
      final Point point = mouseEvent.getPoint ();
      final int row = table.rowAtPoint (point);
      if (mouseEvent.getClickCount () == 2 && table.getSelectedRow () != -1)
      {
        final InstrumentView instrumentView =
          JInstrumentRegistry.this.instrumentRegistry.getInstrumentViews ().get (row);
        JInstrumentRegistry.this.instrumentRegistry.setSelectedInstrumentView (instrumentView);
      }
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
