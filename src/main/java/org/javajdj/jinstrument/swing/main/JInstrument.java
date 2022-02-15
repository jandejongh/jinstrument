/* 
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Bus;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.DefaultInstrumentRegistry;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentRegistry;
import org.javajdj.jinstrument.InstrumentType;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.config.ConfiguratorTypeDB;
import org.javajdj.jinstrument.config.DefaultConfiguratorTypeDB;
import org.javajdj.jinstrument.controller.gpib.BusType_GPIB;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.controller.gpib.prologix.ProLogixGpibEthernetControllerType;
import org.javajdj.jinstrument.gpib.dcl.hp6050a.HP6050A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dmm.hp3478a.HP3478A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.hp54502a.HP54502A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fc.hp5316a.HP5316A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fc.hp5328a.HP5328A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fg.hp8116a.HP8116A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.none.NONE_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.oa.hp8156a.HP8156A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.psu.hp6033a.HP6033A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.sa.hp70000.HP70000_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.sa.hp8566b.HP8566B_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.sg.hp8350.HP8350_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.sg.hp8663a.HP8663A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.slm.hp3586.HP3586_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.sna.wiltron560a.Wiltron560A_GPIB_Instrument;
import org.javajdj.jinstrument.swing.cdi.JTinyCDIStatusAndControl;
import org.javajdj.jinstrument.swing.config.JControllerConfiguratorType;
import org.javajdj.jinstrument.swing.config.JProLogixGpibEthernetControllerConfiguratorType;
import org.javajdj.jinstrument.swing.controller.gpib.JControllerDebug;
import org.javajdj.jinstrument.swing.controller.gpib.JGpibDeviceConsole;
import org.javajdj.jinstrument.swing.debug.JSettingsMonitor;
import org.javajdj.jinstrument.swing.default_view.JDefaultDigitalMultiMeterView;
import org.javajdj.jinstrument.swing.default_view.JDefaultDigitalStorageOscilloscopeView;
import org.javajdj.jinstrument.swing.default_view.JDefaultFrequencyCounterView;
import org.javajdj.jinstrument.swing.default_view.JDefaultFunctionGeneratorView;
import org.javajdj.jinstrument.swing.default_view.JDefaultInstrumentManagementUrlsView;
import org.javajdj.jinstrument.swing.default_view.JDefaultInstrumentManagementView;
import org.javajdj.jinstrument.swing.default_view.JDefaultPowerSupplyUnitView;
import org.javajdj.jinstrument.swing.default_view.JDefaultSelectiveLevelMeterView;
import org.javajdj.jinstrument.swing.default_view.JDefaultSignalGeneratorView;
import org.javajdj.jinstrument.swing.default_view.JDefaultSpectrumAnalyzerSettingsPanel;
import org.javajdj.jinstrument.swing.default_view.JDefaultSpectrumAnalyzerTraceDisplay;
import org.javajdj.jinstrument.swing.default_view.JDefaultSpectrumAnalyzerView;
import org.javajdj.jinstrument.swing.instrument.dmm.hp3457a.JHP3457A_GPIB;
import org.javajdj.jinstrument.swing.instrument.dmm.hp3478a.JHP3478A_GPIB;
import org.javajdj.jinstrument.swing.instrument.fc.hp5316a.JHP5316A_GPIB;
import org.javajdj.jinstrument.swing.instrument.psu.hp6033a.JHP6033A_GPIB;
import org.javajdj.jinstrument.swing.instrument.fg.hp8116a.JHP8116A_GPIB;
import org.javajdj.jinstrument.swing.instrument.fg.hp3325b.JHP3325B_GPIB;
import org.javajdj.jinstrument.swing.instrument.slm.rs_esh3.JRS_ESH3_GPIB;
import org.javajdj.jinstrument.swing.instrument.tek2440.JTek2440_GPIB;
import org.javajdj.jswing.dialog.JArraySelectorDialog;


/** Swing {@code JFrame} for opening and interacting with instruments like RF signal generators, spectrum analyzers, etc.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class JInstrument
extends JFrame
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JInstrument.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private JInstrument (final String title)
  throws HeadlessException
  {
    
    super (title == null ? "JInstrument V0.9-SNAPSHOT" : title);
    
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    
    createMenu ();
    
    this.jTabbedPane = new JTabbedPane ();
    getContentPane ().add (this.jTabbedPane, BorderLayout.CENTER);
    
    this.instrumentRegistry = createDefaultInstrumentRegistry ();
    
    this.jInstrumentRegistry = new JInstrumentRegistry (this.instrumentRegistry, 0);
    this.jTabbedPane.addTab ("Registry", this.jInstrumentRegistry);
    
    this.instrumentRegistry.addRegistryListener (this.instrumentRegistryListener);
    this.instrumentRegistryListener.instrumentRegistryChanged (this.instrumentRegistry);
    
    this.configuratorTypeDB = createDefaultConfiguratorTypeDB ();
    
    boolean considerDefaults = true;
    if (checkUserConfigDir ())
    {
      final boolean userConfigFileExists = Files.exists (new File (DEFAULT_USER_INSTRUMENT_REGISTRY_PATH).toPath ());
      if (checkUserInstrumentRegistry ())
      {
        this.syncInstrumentRegistryToUserConfigDir = true;
        JOptionPane.showMessageDialog (null, "Welcome to JInstrument!\n\n"
          + (userConfigFileExists ? " Found and loaded" : "Created")
          + " user configuration file "
          + JInstrument.DEFAULT_USER_INSTRUMENT_REGISTRY_PATH
          + " and will keep it in sync!");
        considerDefaults = false;
      }
    }

    if (considerDefaults)
    {
      if (JOptionPane.showConfirmDialog (this,
        "Do you want to the load the (compile-time, and my personal favorite) default Instrument Registry?",
        "Nothing loaded thus far...",
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
      {
        //
        final String DEFAULT_CONTROLLER_URL = "prologix-eth://ham-prologix.jdj:1234";
        final Controller defaultController = this.instrumentRegistry.openController (DEFAULT_CONTROLLER_URL);
        if (defaultController == null)
          LOG.log (Level.WARNING, "Could not open default controller: {0}!", DEFAULT_CONTROLLER_URL);
        //
        final String hp8566bDeviceUrl = DEFAULT_CONTROLLER_URL + "/1#" + "gpib:22";
        final GpibDevice hp8566bDevice = (GpibDevice) this.instrumentRegistry.openDevice (hp8566bDeviceUrl);
        final String hp8566bInstrumentUrl = HP8566B_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl ()
          + "@" + hp8566bDeviceUrl;
        final Instrument hp8566bInstrument = this.instrumentRegistry.openInstrument (hp8566bInstrumentUrl);
        //
        final String hp8663aDeviceUrl = DEFAULT_CONTROLLER_URL + "/1#" + "gpib:19";
        final GpibDevice hp8663aDevice = (GpibDevice) this.instrumentRegistry.openDevice (hp8663aDeviceUrl);
        final String hp8663aInstrumentUrl = HP8663A_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl ()
          +"@" + hp8663aDeviceUrl;
        final Instrument hp8663aInstrument = this.instrumentRegistry.openInstrument (hp8663aInstrumentUrl);
        //
        // final GpibDevice hp70000Device = controller.openDevice ("gpib:3"); // hp70900a
        // final GpibDevice hp70000Device = controller.openDevice ("gpib:18"); // hp70900b
        // final HP70000_GPIB  hp70000Instrument = new HP70000_GPIB (hp70000Device);
        //
        // final GpibDevice hp54502aDevice = controller.openDevice ("gpib:1"); // hp54502a
        // final HP54502A_GPIB hp54502aInstrument = new HP54502A_GPIB (hp54502aDevice);
        //
      }
    }
    
    pack ();
    
  }

  private JInstrument ()
  {
    this (null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TABBED PANE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JTabbedPane jTabbedPane;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // [DEFAULT] INSTRUMENT REGISTRY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private InstrumentRegistry instrumentRegistry;

  protected InstrumentRegistry createDefaultInstrumentRegistry ()
  {
    
    final DefaultInstrumentRegistry instrumentRegistry = new DefaultInstrumentRegistry ();
    
    instrumentRegistry.addBusType (BusType_GPIB.getInstance ());
    
    instrumentRegistry.addControllerType (ProLogixGpibEthernetControllerType.getInstance ());
    
    instrumentRegistry.addDeviceType (DeviceType_GPIB.getInstance ());
    
    instrumentRegistry.addInstrumentType (NONE_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP3325B_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP3457A_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP3478A_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP3586_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP5316A_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP5328A_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP6033A_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP6050A_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP8116A_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP8156A_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP8350_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP8566B_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP8663A_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP54502A_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (HP70000_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (RS_ESH3_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (Tek2440_GPIB_Instrument.INSTRUMENT_TYPE);
    instrumentRegistry.addInstrumentType (Wiltron560A_GPIB_Instrument.INSTRUMENT_TYPE);
    
    instrumentRegistry.addInstrumentViewType (JControllerDebug.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JGpibDeviceConsole.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultInstrumentManagementUrlsView.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultInstrumentManagementView.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JTinyCDIStatusAndControl.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JSettingsMonitor.INSTRUMENT_VIEW_TYPE);    
    instrumentRegistry.addInstrumentViewType (JDefaultDigitalStorageOscilloscopeView.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultSpectrumAnalyzerView.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultSpectrumAnalyzerSettingsPanel.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultSpectrumAnalyzerTraceDisplay.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultSignalGeneratorView.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultDigitalMultiMeterView.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultFunctionGeneratorView.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultPowerSupplyUnitView.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultFrequencyCounterView.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JDefaultSelectiveLevelMeterView.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JHP3325B_GPIB.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JHP3457A_GPIB.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JHP3478A_GPIB.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JHP5316A_GPIB.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JHP6033A_GPIB.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JHP8116A_GPIB.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JTek2440_GPIB.INSTRUMENT_VIEW_TYPE);
    instrumentRegistry.addInstrumentViewType (JRS_ESH3_GPIB.INSTRUMENT_VIEW_TYPE);
    
    return instrumentRegistry;
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // [DEFAULT] CONFIGURATOR-TYPE DB
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final ConfiguratorTypeDB configuratorTypeDB;
    
  protected ConfiguratorTypeDB createDefaultConfiguratorTypeDB ()
  {
    
    final ConfiguratorTypeDB configuratorTypeDB = DefaultConfiguratorTypeDB.getInstance ();
    
    configuratorTypeDB.addConfiguratorType (new JControllerConfiguratorType ());
    configuratorTypeDB.addConfiguratorType (new JProLogixGpibEthernetControllerConfiguratorType ());
    
    return configuratorTypeDB;
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // [SWING] INSTRUMENT REGISTRY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JInstrumentRegistry jInstrumentRegistry;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // [SWING] MENU
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void createMenu ()
  {
    //
    final JMenuBar jMenuBar = new JMenuBar ();
    //
    // File
    //
    final JMenu fileMenu = new JMenu ("File");
    final JMenuItem loadRegistryMenuItem = new JMenuItem ("Load Registry");
    loadRegistryMenuItem.addActionListener ((ActionEvent) -> { JInstrument.this.loadRegistry (null, true, true); });
    fileMenu.add (loadRegistryMenuItem);
    final JMenuItem saveRegistryMenuItem = new JMenuItem ("Save Registry");
    saveRegistryMenuItem.addActionListener ((ActionEvent) -> { JInstrument.this.saveRegistry (null, true); });
    fileMenu.add (saveRegistryMenuItem);
    final JMenuItem exitMenuItem = new JMenuItem ("Exit");
    exitMenuItem.addActionListener ((ActionEvent) -> { System.exit (0); });
    fileMenu.add (exitMenuItem);
    jMenuBar.add (fileMenu);
    //
    // Controller
    //
    final JMenu controllerMenu = new JMenu ("Controller");
    final JMenuItem openControllerMenuItem = new JMenuItem ("Open Controller");
    openControllerMenuItem.addActionListener ((ActionEvent ae) ->
    {
      final Controller controller =
        this.configuratorTypeDB.getConfiguratorType (Controller.class).doModalDialogForNewObject (
          this.instrumentRegistry,
          this.configuratorTypeDB,
          "Open Controller");
      if (controller != null)
      {
        if (this.instrumentRegistry.getControllerByUrl (controller.getControllerUrl ()) != null)
        {
          JOptionPane.showMessageDialog (
            this,
            "Controller with URL " + controller.getControllerUrl () + " is already registered!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        }
        else
          this.instrumentRegistry.addController (controller);
      }
    });
    controllerMenu.add (openControllerMenuItem);
    final JMenuItem closeControllerMenuItem = new JMenuItem ("Close Controller");
    closeControllerMenuItem.addActionListener ((ActionEvent ae) ->
    {
      final JArraySelectorDialog<Controller> jControllerDialog = new JArraySelectorDialog<> (
        this,
        "Close Controller",
        true,
        this.instrumentRegistry.getControllers ().toArray (new Controller[]{}),
        null,
        "Select Controller:",
        null);
      // Attempt to always have the dialog title completely visible.
      jControllerDialog.setMinimumSize (new Dimension (320, 240));
      jControllerDialog.setLocationRelativeTo (this);
      jControllerDialog.setVisible (true);
      final Controller selectedItem = jControllerDialog.getSelectedItem ();
      if (selectedItem != null)
        this.instrumentRegistry.removeController (selectedItem);
    });
    controllerMenu.add (closeControllerMenuItem);
    jMenuBar.add (controllerMenu);
    //
    // Device
    //
    final JMenu deviceMenu = new JMenu ("Device");
    final JMenuItem openDeviceMenuItem = new JMenuItem ("Open Device");
    openDeviceMenuItem.addActionListener ((ActionEvent ae) ->
    {
      JOptionPane.showMessageDialog (this, "Not implemented yet!", "Error", JOptionPane.ERROR_MESSAGE);
    });
    deviceMenu.add (openDeviceMenuItem);
    final JMenuItem closeDeviceMenuItem = new JMenuItem ("Close Device");
    closeDeviceMenuItem.addActionListener ((ActionEvent ae) ->
    {
      JOptionPane.showMessageDialog (this, "Not implemented yet!", "Error", JOptionPane.ERROR_MESSAGE);
    });
    deviceMenu.add (closeDeviceMenuItem);
    jMenuBar.add (deviceMenu);
    //
    // Instrument
    //
    final JMenu instrumentMenu = new JMenu ("Instrument");
    final JMenuItem openInstrumentMenuItem = new JMenuItem ("Open Instrument");
    openInstrumentMenuItem.addActionListener ((ActionEvent ae) ->
    {
      JOptionPane.showMessageDialog (this, "Not implemented yet!", "Error", JOptionPane.ERROR_MESSAGE);
    });
    instrumentMenu.add (openInstrumentMenuItem);
    final JMenuItem closeInstrumentMenuItem = new JMenuItem ("Close Instrument");
    closeInstrumentMenuItem.addActionListener ((ActionEvent ae) ->
    {
      JOptionPane.showMessageDialog (this, "Not implemented yet!", "Error", JOptionPane.ERROR_MESSAGE);
    });
    instrumentMenu.add (closeInstrumentMenuItem);
    jMenuBar.add (instrumentMenu);
    //
    // View
    //
    final JMenu viewMenu = new JMenu ("View");
    final JMenuItem openViewMenuItem = new JMenuItem ("Open View");
    openViewMenuItem.addActionListener ((ActionEvent ae) ->
    {
      JOptionPane.showMessageDialog (this, "Not implemented yet!", "Error", JOptionPane.ERROR_MESSAGE);
    });
    viewMenu.add (openViewMenuItem);
    final JMenuItem closeCurrentViewMenuItem = new JMenuItem ("Close Current View");
    closeCurrentViewMenuItem.addActionListener ((ActionEvent ae) ->
    {
      JOptionPane.showMessageDialog (this, "Not implemented yet!", "Error", JOptionPane.ERROR_MESSAGE);
    });
    viewMenu.add (closeCurrentViewMenuItem);
    final JMenuItem closeViewMenuItem = new JMenuItem ("Close View");
    closeViewMenuItem.addActionListener ((ActionEvent ae) ->
    {
      final JArraySelectorDialog<InstrumentView> jInstrumentViewDialog = new JArraySelectorDialog<> (
        this,
        "Close Instrument View",
        true,
        this.instrumentRegistry.getInstrumentViews ().toArray (new InstrumentView[]{}),
        null,
        "Select Instrument View:",
        null);
      // Attempt to always have the dialog title completely visible.
      jInstrumentViewDialog.setMinimumSize (new Dimension (320, 240));
      jInstrumentViewDialog.setLocationRelativeTo (this);
      jInstrumentViewDialog.setVisible (true);
      final InstrumentView selectedItem = jInstrumentViewDialog.getSelectedItem ();
      if (selectedItem != null)
        this.instrumentRegistry.removeInstrumentView (selectedItem);
    });
    viewMenu.add (closeViewMenuItem);
    jMenuBar.add (viewMenu);
    //
    // Help
    //
    final JMenu helpMenu = new JMenu ("Help");
    final JMenuItem helpAboutItem = new JMenuItem ("About");
    helpAboutItem.addActionListener ((ActionEvent) ->
    {
      JOptionPane.showMessageDialog (null, "JInstrument was designed and realized by Jan de Jongh [pa3gyf].");
    });
    helpMenu.add (helpAboutItem);
    jMenuBar.add (helpMenu);
    setJMenuBar (jMenuBar);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT SYSTEM INSTRUMENT REGISTRY PATH
  // DEFAULT USER INSTRUMENT REGISTRY PATH
  //
  // CHECK USER CONFIG DIR
  // CHECK USER INSTRUMENT REGISTRY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static String DEFAULT_SYSTEM_INSTRUMENT_REGISTRY_PATH = "/etc/jinstrument/registry.json";
  
  public final static String DEFAULT_USER_CONFIG_DIR = System.getProperty ("user.home") + "/.jinstrument/";
  
  public final static String DEFAULT_USER_INSTRUMENT_REGISTRY_PATH = DEFAULT_USER_CONFIG_DIR + "instrumentRegistry.json";
  
  private boolean checkUserConfigDir ()
  {
    if (! Files.exists (new File (DEFAULT_USER_CONFIG_DIR).toPath ()))
    {
      if (JOptionPane.showConfirmDialog (this,
        "Default JInstrument configuration directory " + DEFAULT_USER_CONFIG_DIR + " does not exist, create it?",
        "Welcome to JInstrument",
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
      {
        if (new File (DEFAULT_USER_CONFIG_DIR).mkdir ())
          return true;
        else
        {
          JOptionPane.showMessageDialog (this,
            "Sorry; I could not create your user configuration directory!\n\n"
            + "JInstrument will run just fine without the (user) configuration directory!\n\n"
            + "Just remember to manually save and load settings via the menu!\n\n",
            "Could not create directory!",
            JOptionPane.INFORMATION_MESSAGE);
          return false;
        }
      }
      else
      {
        JOptionPane.showMessageDialog (this,
          "No problem, really!\n\n"
            + "JInstrument will run just fine without the (user) configuration directory!\n\n"
            + "Just remember to manually save and load settings via the menu!\n\n",
          "No Problem!",
          JOptionPane.INFORMATION_MESSAGE);
        return false;      
      }
    }
    else
      return true;
  }
  
  private boolean checkUserInstrumentRegistry ()
  {
    if (Files.exists (new File (DEFAULT_USER_INSTRUMENT_REGISTRY_PATH).toPath ()))
    {
      if (loadRegistry (DEFAULT_USER_INSTRUMENT_REGISTRY_PATH, true, false) == null)
      {
        JOptionPane.showMessageDialog (this,
          "Error while loading the default user configuration (registry) file at "
            + DEFAULT_USER_INSTRUMENT_REGISTRY_PATH + "!\n\n"
            + "JInstrument will run just fine without the (user) configuration file!\n\n"
            + "Just remember to manually save and load settings via the menu!\n\n",
          "Error loading deafult user configuration (registry) file!",
          JOptionPane.ERROR_MESSAGE);
        return false;
      }
      else
        return true;
    }
    else
    {
      if (JOptionPane.showConfirmDialog (this,
        "Default JInstrument configuration directory " + DEFAULT_USER_CONFIG_DIR + " exists, but no registry found."
          + " create an empty one?",
        "Instrument Registry not found",
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
      {
        return saveRegistry (DEFAULT_USER_INSTRUMENT_REGISTRY_PATH, true);
      }
      else
        return false;
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOAD REGISTRY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private InstrumentRegistry loadRegistry (
    final String fileName,
    final boolean switchRegistry,
    final boolean reportFailure)
  {
    final File file;
    if (fileName == null)
    {
      final JFileChooser jFileChooser = new JFileChooser ();
      if (jFileChooser.showOpenDialog (this) != JFileChooser.APPROVE_OPTION)
        return null;
      file = new File (jFileChooser.getSelectedFile ().getAbsolutePath ());
    }
    else
      file = new File (fileName);
    try (final FileReader fileReader = new FileReader (file))
    {
      final JsonReader jsonReader = Json.createReader (fileReader);
      final JsonObject topLevelJsonObject = jsonReader.readObject ();
      jsonReader.close ();
      try
      {
        final String magic = topLevelJsonObject.getString ("magic");
        if (! "org.javajdj.jinstrument-instrumentRegistry-json".equals (magic))
          throw new ParseException ("magic", 0);
        final int fileFormatVersion = topLevelJsonObject.getInt ("fileFormatVersion");
        if (fileFormatVersion != 1)
          throw new ParseException ("fileFormatVersion", 0);
        final JsonObject registryObject = (JsonObject) topLevelJsonObject.get ("registry");
        final JsonArray busesArray = (JsonArray) registryObject.get ("buses");
        final List<String> busUrls = new ArrayList<> ();
        for (int b = 0; b < busesArray.size (); b++)
        {
          final JsonObject busOject = busesArray.getJsonObject (b);
          final String busUrl = busOject.getString ("url");
          busUrls.add (busUrl);
        }
        final JsonArray controllersArray = (JsonArray) registryObject.get ("controllers");
        final List<String> controllerUrls = new ArrayList<> ();
        for (int c = 0; c < controllersArray.size (); c++)
        {
          final JsonObject controllerOject = controllersArray.getJsonObject (c);
          final String controllerUrl = controllerOject.getString ("url");
          controllerUrls.add (controllerUrl);
        }
        final JsonArray devicesArray = (JsonArray) registryObject.get ("devices");
        final List<String> deviceUrls = new ArrayList<> ();
        for (int d = 0; d < devicesArray.size (); d++)
        {
          final JsonObject deviceOject = devicesArray.getJsonObject (d);
          final String deviceUrl = deviceOject.getString ("url");
          deviceUrls.add (deviceUrl);
        }
        final JsonArray instrumentsArray = (JsonArray) registryObject.get ("instruments");
        final List<String> instrumentUrls = new ArrayList<> ();
        for (int i = 0; i < instrumentsArray.size (); i++)
        {
          final JsonObject instrumentOject = instrumentsArray.getJsonObject (i);
          final String instrumentUrl = instrumentOject.getString ("url");
          instrumentUrls.add (instrumentUrl);
        } 
        final JsonArray instrumentViewsArray = (JsonArray) registryObject.get ("instrumentViews");
        final List<String> instrumentViewUrls = new ArrayList<> ();
        for (int iv = 0; iv < instrumentViewsArray.size (); iv++)
        {
          final JsonObject instrumentViewOject = instrumentViewsArray.getJsonObject (iv);
          final String instrumentViewUrl = instrumentViewOject.getString ("url");
          instrumentViewUrls.add (instrumentViewUrl);
        }
        final InstrumentRegistry instrumentRegistry = this.instrumentRegistry.withSameTypesButInstancesFromUrlLists
          (busUrls, controllerUrls, deviceUrls, instrumentUrls, instrumentViewUrls);
        if (instrumentRegistry == null)
          throw new ParseException ("registry", 0);
        if (switchRegistry)
          switchRegistry (instrumentRegistry);
        return instrumentRegistry;
      }
      catch (ParseException pe)
      {
        throw pe;
      }
      catch (Exception e)
      {
        throw new ParseException ("json", 0);
      }
    }
    catch (FileNotFoundException fnfe)
    {
      if (reportFailure)
        JOptionPane.showMessageDialog (null, "File Not Found Exception!");
      return null;      
    }
    catch (IOException ioe)
    {
      if (reportFailure)
        JOptionPane.showMessageDialog (null, "I/O Exception!");
      return null;
    }
    catch  (ParseException pe)
    {
      if (reportFailure)
        JOptionPane.showMessageDialog (null, "Parse Exception!");
      return null;
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWITCH REGISTRY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private void switchRegistry (final InstrumentRegistry newInstrumentRegistry)
  {
    this.instrumentRegistry.removeRegistryListener (this.instrumentRegistryListener);
    this.instrumentRegistry = newInstrumentRegistry;
    this.instrumentRegistry.addRegistryListener (this.instrumentRegistryListener);
    this.instrumentRegistryListener.instrumentRegistryChanged (this.instrumentRegistry);
    this.jInstrumentRegistry.setInstrumentRegistry (this.instrumentRegistry);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SAVE REGISTRY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private boolean saveRegistry (final String fileName, final boolean reportFailures)
  {
    final File file;
    if (fileName == null)
    {
      final JFileChooser jFileChooser = new JFileChooser ();
      if (jFileChooser.showSaveDialog (this) != JFileChooser.APPROVE_OPTION)
        return false;
      file = jFileChooser.getSelectedFile ();
    }
    else
      file = new File (fileName);
    final InstrumentRegistry instrumentRegistry = JInstrument.this.instrumentRegistry;
    try (final FileWriter fileWriter = new FileWriter (file))
    {
      //  Buses
      final JsonArrayBuilder jsonBusesArrayBuilder = Json.createArrayBuilder ();
      for (final Bus bus : instrumentRegistry.getBuses ())
      {
        final JsonObject jsonObject = Json.createObjectBuilder ()
          .add ("url", bus.getBusUrl ())
          .add ("typeurl", bus.getBusType ().getBusTypeUrl ())
          .build ();
        jsonBusesArrayBuilder.add (jsonObject);
      }
      // Controllers
      final JsonArrayBuilder jsonControllersArrayBuilder = Json.createArrayBuilder ();
      for (final Controller controller : instrumentRegistry.getControllers ())
      {
        final JsonObject jsonObject = Json.createObjectBuilder ()
          .add ("url", controller.getControllerUrl ())
          .build ();
        jsonControllersArrayBuilder.add (jsonObject);
      }
      // Devices
      final JsonArrayBuilder jsonDevicesArrayBuilder = Json.createArrayBuilder ();
      for (final Device device : instrumentRegistry.getDevices ())
      {
        final JsonObject jsonObject = Json.createObjectBuilder ()
          .add ("url", device.getDeviceUrl ())
          .build ();
        jsonDevicesArrayBuilder.add (jsonObject);
      }
      // Instruments
      final JsonArrayBuilder jsonInstrumentsArrayBuilder = Json.createArrayBuilder ();
      for (final Instrument instrument : instrumentRegistry.getInstruments ())
      {
        final JsonObject jsonObject = Json.createObjectBuilder ()
          .add ("url", instrument.getInstrumentUrl ())
          .build ();
        jsonInstrumentsArrayBuilder.add (jsonObject);
      }
      // Instrument Views
      final JsonArrayBuilder jsonInstrumentViewsArrayBuilder = Json.createArrayBuilder ();
      for (final InstrumentView instrumentView : instrumentRegistry.getInstrumentViews ())
      {
        final JsonObject jsonObject = Json.createObjectBuilder ()
          .add ("url", instrumentView.getInstrumentViewUrl ())
          .build ();
        jsonInstrumentViewsArrayBuilder.add (jsonObject);
      }
      final JsonObject registryJsonObject = Json.createObjectBuilder ()
        .add ("buses", jsonBusesArrayBuilder)
        .add ("controllers", jsonControllersArrayBuilder)
        .add ("devices", jsonDevicesArrayBuilder)
        .add ("instruments", jsonInstrumentsArrayBuilder)
        .add ("instrumentViews", jsonInstrumentViewsArrayBuilder)
        .build ();
      final JsonObject topLevelJsonObject = Json.createObjectBuilder ()
        .add ("magic", "org.javajdj.jinstrument-instrumentRegistry-json")
        .add ("date", Instant.now ().toString ())
        .add ("fileFormatVersion", 1)
        .add ("registry", registryJsonObject)
        .build ();
      final JsonWriterFactory jsonWriterFactory
        = Json.createWriterFactory (Collections.singletonMap (JsonGenerator.PRETTY_PRINTING, true));
      try (final JsonWriter jsonWriter = jsonWriterFactory.createWriter (fileWriter))
      {
        jsonWriter.writeObject (topLevelJsonObject);
      }
      return true;
    }
    catch (FileNotFoundException fnfe)
    {
      if (reportFailures)
        JOptionPane.showMessageDialog (null, "File Not Found Exception!");
      return false;
    }
    catch (IOException ioe)
    {
      if (reportFailures)
        JOptionPane.showMessageDialog (null, "I/O Exception!");
      return false;
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SYNC INSTRUMENT REGISTRY TO USER CONFIG DIR
  // INSTRUMENT REGISTRY LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private boolean syncInstrumentRegistryToUserConfigDir = false;
  
  private final InstrumentRegistry.Listener instrumentRegistryListener = new InstrumentRegistry.Listener ()
  {
    
    @Override
    public void instrumentRegistryChanged (final InstrumentRegistry instrumentRegistry)
    {
      SwingUtilities.invokeLater (() ->
      {
        final List<InstrumentView> instrumentViews = instrumentRegistry.getInstrumentViews ();
        final List<Component> components = new ArrayList<> (Arrays.asList (JInstrument.this.jTabbedPane.getComponents ()));
        for (final InstrumentView instrumentView : instrumentViews)
          if (instrumentView instanceof JComponent)
            if (! components.contains ((Component) instrumentView))
            {
              final Instrument instrument = instrumentView.getInstrument ();
              final InstrumentType instrumentType  = JInstrument.this.instrumentRegistry.getInstrumentType (instrument);
              JInstrument.this.jTabbedPane.add (instrumentType.getInstrumentTypeUrl (), (JComponent) instrumentView);
            }
        for (final Component component : components)
        {
          if (component != null
            && (component instanceof InstrumentView)
            && ! instrumentViews.contains ((InstrumentView) component))
          {
            JInstrument.this.jTabbedPane.remove (component);
          }
        }
        if (JInstrument.this.syncInstrumentRegistryToUserConfigDir)
          JInstrument.this.saveRegistry (JInstrument.DEFAULT_USER_INSTRUMENT_REGISTRY_PATH, true);
      });
    }
    
    @Override
    public void instrumentRegistrySetSelectedInstrumentView (
      final InstrumentRegistry instrumentRegistry,
      final InstrumentView instrumentView)
    {
      SwingUtilities.invokeLater (() ->
      {
        if (instrumentRegistry != null && instrumentRegistry == JInstrument.this.instrumentRegistry && instrumentView != null)
        {
          for (final Component component: JInstrument.this.jTabbedPane.getComponents ())
            if (component == instrumentView)
              JInstrument.this.jTabbedPane.setSelectedComponent (component);        
        }
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MAIN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static void main (final String[] args)
  {
    SwingUtilities.invokeLater (() ->
    {
      final JFrame frame = new JInstrument ();
      frame.setExtendedState (JFrame.MAXIMIZED_BOTH);
      frame.setVisible (true);
    });
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
