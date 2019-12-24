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
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
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
import org.javajdj.jinstrument.controller.gpib.BusType_GPIB;
import org.javajdj.jinstrument.controller.gpib.DeviceType_GPIB;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.controller.gpib.ProLogixGpibEthernetControllerType;
import org.javajdj.jinstrument.instrument.sa.HP8566B_GPIB_Instrument;
import org.javajdj.jinstrument.instrument.sg.HP8663A_GPIB_Instrument;

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
    
    super (title == null ? "JInstrument V0.1-SNAPSHOT" : title);
    
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    createMenu ();
    
    this.jTabbedPane = new JTabbedPane ();
    getContentPane ().add (this.jTabbedPane, BorderLayout.CENTER);
    
    this.instrumentRegistry = DefaultInstrumentRegistry.getInstance ();
    
    this.instrumentRegistry.addRegistryListener (this.instrumentRegistryListener);
    
    this.instrumentRegistry.addBusType (BusType_GPIB.getInstance ());
    
    this.instrumentRegistry.addControllerType (ProLogixGpibEthernetControllerType.getInstance ());
    
    this.instrumentRegistry.addDeviceType (DeviceType_GPIB.getInstance ());
    
    this.instrumentRegistry.addInstrumentType (HP8566B_GPIB_Instrument.INSTRUMENT_TYPE);
    this.instrumentRegistry.addInstrumentType (HP8663A_GPIB_Instrument.INSTRUMENT_TYPE);
    
    this.instrumentRegistry.addInstrumentViewType (JDefaultInstrumentManagementView.INSTRUMENT_VIEW_TYPE);
    this.instrumentRegistry.addInstrumentViewType (JDefaultSpectrumAnalyzerView.INSTRUMENT_VIEW_TYPE);
    this.instrumentRegistry.addInstrumentViewType (JSpectrumAnalyzerTraceDisplay.INSTRUMENT_VIEW_TYPE);
    this.instrumentRegistry.addInstrumentViewType (JDefaultSignalGeneratorView.INSTRUMENT_VIEW_TYPE);
    this.instrumentRegistry.addInstrumentViewType (JSpectrumAnalyzerSettingsPanel.INSTRUMENT_VIEW_TYPE);
    
    final Controller defaultController = this.instrumentRegistry.openController (JInstrument.DEFAULT_CONTROLLER_URL);
    if (defaultController == null)
      LOG.log (Level.WARNING, "Could not open default controller: \'{0}\'!", JInstrument.DEFAULT_CONTROLLER_URL);
    this.jTabbedPane.addTab ("Registry", new JInstrumentRegistry (this.instrumentRegistry, 0));
    
    final String hp8566bDeviceUrl = JInstrument.DEFAULT_CONTROLLER_URL + "/1#" + "gpib:22";
    final GpibDevice hp8566bDevice = (GpibDevice) this.instrumentRegistry.openDevice (hp8566bDeviceUrl);
    final String hp8566bInstrumentUrl = HP8566B_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + hp8566bDeviceUrl;
    final Instrument hp8566bInstrument = this.instrumentRegistry.openInstrument (hp8566bInstrumentUrl);

    final String hp8663aDeviceUrl = JInstrument.DEFAULT_CONTROLLER_URL + "/1#" + "gpib:19";
    final GpibDevice hp8663aDevice = (GpibDevice) this.instrumentRegistry.openDevice (hp8663aDeviceUrl);
    final String hp8663aInstrumentUrl = HP8663A_GPIB_Instrument.INSTRUMENT_TYPE.getInstrumentTypeUrl () + "@" + hp8663aDeviceUrl;
    final Instrument hp8663aInstrument = this.instrumentRegistry.openInstrument (hp8663aInstrumentUrl);
    
    //
    // final GpibDevice hp70000Device = controller.openDevice ("gpib:3"); // hp70900a
    // final GpibDevice hp70000Device = controller.openDevice ("gpib:18"); // hp70900b
    // final HP70000_GPIB  hp70000Instrument = new HP70000_GPIB (hp70000Device);
    // tabbedPane.addTab ("HP70000[1]", new JDefaultSpectrumAnalyzerView (hp70000Instrument));
    //
    // final GpibDevice hp54502aDevice = controller.openDevice ("gpib:1"); // hp54502a
    // final HP54502A_GPIB hp54502aInstrument = new HP54502A_GPIB (hp54502aDevice);
    // tabbedPane.addTab ("HP54502A[1]", createHp54502aTab (controller, hp54502aInstrument));
    //
    
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
  // INSTRUMENT REGISTRY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final DefaultInstrumentRegistry instrumentRegistry;
  
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
    loadRegistryMenuItem.addActionListener ((ActionEvent) -> { JInstrument.this.loadRegistry (); });
    fileMenu.add (loadRegistryMenuItem);
    final JMenuItem saveRegistryMenuItem = new JMenuItem ("Save Registry");
    saveRegistryMenuItem.addActionListener ((ActionEvent) -> { JInstrument.this.saveRegistry (); });
    fileMenu.add (saveRegistryMenuItem);
    final JMenuItem exitMenuItem = new JMenuItem ("Exit");
    exitMenuItem.addActionListener ((ActionEvent) -> { System.exit (0); });
    fileMenu.add (exitMenuItem);
    jMenuBar.add (fileMenu);
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
  // LOAD REGISTRY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void loadRegistry ()
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SAVE REGISTRY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void saveRegistry ()
  {
    final InstrumentRegistry instrumentRegistry = JInstrument.this.instrumentRegistry;
    final JFileChooser jFileChooser = new JFileChooser ();
    if (jFileChooser.showSaveDialog (this) != JFileChooser.APPROVE_OPTION)
      return;
    final File file = jFileChooser.getSelectedFile ();
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
      final JsonWriterFactory jsonWriterFactory
        = Json.createWriterFactory (Collections.singletonMap (JsonGenerator.PRETTY_PRINTING, true));
      try (final JsonWriter jsonWriter = jsonWriterFactory.createWriter (fileWriter))
      {
        jsonWriter.writeObject (registryJsonObject);
      }
    }
    catch (FileNotFoundException fnfe)
    {
      JOptionPane.showMessageDialog (null, "File Not Found Exception!");
    }
    catch (IOException ioe)
    {
      JOptionPane.showMessageDialog (null, "I/O Exception!");
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT REGISTRY LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final InstrumentRegistry.Listener instrumentRegistryListener = (final InstrumentRegistry instrumentRegistry) ->
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
      // XXX TODO: Remove InstrumentViews...
    });
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT CONTROLLER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static String DEFAULT_CONTROLLER_URL = "prologix-eth://ham-prologix.jdj:1234";
  
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
