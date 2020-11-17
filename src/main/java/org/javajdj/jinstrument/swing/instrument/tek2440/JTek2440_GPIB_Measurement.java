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
package org.javajdj.jinstrument.swing.instrument.tek2440;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for the Measurement settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Measurement
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Measurement.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Measurement (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final String title,
    final int level,
    final Color panelColor)
  {
    
    super (digitalStorageOscilloscope, title, level, panelColor);
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;
    
    removeAll ();
    setLayout (new GridLayout (4, 1, 10, 10));
    
    final JTextPane jDescription = new JTextPane ();
    jDescription.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Description"));
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Measurement Settings</b>" +
//                          "<ul>" +
//                            "<li> the data format for transferring settings, status, and traces;</li>" +
//                            "<li>Features manual data transfers to and from the instrument;</li>" +
//                            "<li>Values marked in red are used internally in the software and therefore read-only.</li>" +
//                          "</ul>" +
                          "</html>");
    add (jDescription);
    
    final JPanel mainPanel = new JPanel ();
    mainPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Main"));
    add (mainPanel);
    
    final JPanel channelPanel = new JPanel ();
    // channelPanel.setBorder (
    //   BorderFactory.createTitledBorder (
    //     BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
    //     "Channels"));
    add (channelPanel);
    
    final JPanel methodPanel = new JPanel ();
    // methodPanel.setBorder (
    //   BorderFactory.createTitledBorder (
    //     BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
    //     "Methods"));
    add (methodPanel);

    mainPanel.setLayout (new GridLayout (2, 5));
    
    mainPanel.add (new JLabel ("Method"));
    this.jMethod = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementMethod.values ());
    this.jMethod.setSelectedItem (null);
    this.jMethod.setEditable (false);
    this.jMethod.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jMethod.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement method",
      Tek2440_GPIB_Settings.MeasurementMethod.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementMethod>) tek2440::setMeasurementMethod,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    mainPanel.add (JCenter.Y (this.jMethod));
    
    mainPanel.add (new JLabel ());
    
    mainPanel.add (new JLabel ("Display"));
    this.jDisplay = new JColorCheckBox.JBoolean (Color.green);
    this.jDisplay.addActionListener (new JInstrumentActionListener_1Boolean (
      "display measurements",
      this.jDisplay::getDisplayedValue,
      tek2440::setMeasurementDisplay,
      this::isInhibitInstrumentControl));
    mainPanel.add (this.jDisplay);
    
    mainPanel.add (new JLabel ("Windowing"));
    this.jWindowing = new JColorCheckBox.JBoolean (Color.green);
    this.jWindowing.addActionListener (new JInstrumentActionListener_1Boolean (
      "[measurements] windowing",
      this.jWindowing::getDisplayedValue,
      tek2440::setMeasurementWindowing,
      this::isInhibitInstrumentControl));
    mainPanel.add (this.jWindowing);
    
    mainPanel.add (new JLabel ());
    
    mainPanel.add (new JLabel ("Display Markers"));
    this.jDisplayThresholdMarkers = new JColorCheckBox.JBoolean (Color.green);
    this.jDisplayThresholdMarkers.addActionListener (new JInstrumentActionListener_1Boolean (
      "display measurement threshold markers",
      this.jDisplayThresholdMarkers::getDisplayedValue,
      tek2440::setMeasurementDisplayMarkers,
      this::isInhibitInstrumentControl));
    mainPanel.add (this.jDisplayThresholdMarkers);
    
    channelPanel.setLayout (new GridLayout (1, 4));
    
    final JPanel jCh1Panel = new JPanel ();
    jCh1Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Channel 1"));
    channelPanel.add (jCh1Panel);
    
    jCh1Panel.setLayout (new GridLayout (3, 2));
    
    jCh1Panel.add (new JLabel ("Type"));
    this.jCh1Type = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementType.values ());
    this.jCh1Type.setSelectedItem (null);
    this.jCh1Type.setEditable (false);
    this.jCh1Type.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh1Type.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 1 type",
      Tek2440_GPIB_Settings.MeasurementType.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementType>) tek2440::setMeasurementChannel1Type,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh1Panel.add (JCenter.Y (this.jCh1Type));
    
    jCh1Panel.add (new JLabel ("Source"));
    this.jCh1Source = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementSource.values ());
    this.jCh1Source.setSelectedItem (null);
    this.jCh1Source.setEditable (false);
    this.jCh1Source.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh1Source.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 1 source",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementSource>) tek2440::setMeasurementChannel1Source,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh1Panel.add (JCenter.Y (this.jCh1Source));
    
    jCh1Panel.add (new JLabel ("DSource"));
    this.jCh1DSource = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementSource.values ());
    this.jCh1DSource.setSelectedItem (null);
    this.jCh1DSource.setEditable (false);
    this.jCh1DSource.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh1DSource.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 1 source",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementSource>) tek2440::setMeasurementChannel1DSource,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh1Panel.add (JCenter.Y (this.jCh1DSource));
    
    final JPanel jCh2Panel = new JPanel ();
    jCh2Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Channel 2"));
    channelPanel.add (jCh2Panel);
    
    jCh2Panel.setLayout (new GridLayout (3, 2));
    
    jCh2Panel.add (new JLabel ("Type"));
    this.jCh2Type = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementType.values ());
    this.jCh2Type.setSelectedItem (null);
    this.jCh2Type.setEditable (false);
    this.jCh2Type.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh2Type.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 2 type",
      Tek2440_GPIB_Settings.MeasurementType.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementType>) tek2440::setMeasurementChannel2Type,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh2Panel.add (JCenter.Y (this.jCh2Type));
    
    jCh2Panel.add (new JLabel ("Source"));
    this.jCh2Source = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementSource.values ());
    this.jCh2Source.setSelectedItem (null);
    this.jCh2Source.setEditable (false);
    this.jCh2Source.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh2Source.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 2 source",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementSource>) tek2440::setMeasurementChannel2Source,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh2Panel.add (JCenter.Y (this.jCh2Source));
    
    jCh2Panel.add (new JLabel ("DSource"));
    this.jCh2DSource = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementSource.values ());
    this.jCh2DSource.setSelectedItem (null);
    this.jCh2DSource.setEditable (false);
    this.jCh2DSource.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh2DSource.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 2 source",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementSource>) tek2440::setMeasurementChannel2DSource,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh2Panel.add (JCenter.Y (this.jCh2DSource));
    
    final JPanel jCh3Panel = new JPanel ();
    jCh3Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Channel 3"));
    channelPanel.add (jCh3Panel);
    
    jCh3Panel.setLayout (new GridLayout (3, 2));
    
    jCh3Panel.add (new JLabel ("Type"));
    this.jCh3Type = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementType.values ());
    this.jCh3Type.setSelectedItem (null);
    this.jCh3Type.setEditable (false);
    this.jCh3Type.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh3Type.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 3 type",
      Tek2440_GPIB_Settings.MeasurementType.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementType>) tek2440::setMeasurementChannel3Type,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh3Panel.add (JCenter.Y (this.jCh3Type));
    
    jCh3Panel.add (new JLabel ("Source"));
    this.jCh3Source = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementSource.values ());
    this.jCh3Source.setSelectedItem (null);
    this.jCh3Source.setEditable (false);
    this.jCh3Source.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh3Source.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 3 source",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementSource>) tek2440::setMeasurementChannel3Source,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh3Panel.add (JCenter.Y (this.jCh3Source));
    
    jCh3Panel.add (new JLabel ("DSource"));
    this.jCh3DSource = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementSource.values ());
    this.jCh3DSource.setSelectedItem (null);
    this.jCh3DSource.setEditable (false);
    this.jCh3DSource.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh3DSource.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 3 source",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementSource>) tek2440::setMeasurementChannel3DSource,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh3Panel.add (JCenter.Y (this.jCh3DSource));
    
    final JPanel jCh4Panel = new JPanel ();
    jCh4Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Channel 4"));
    channelPanel.add (jCh4Panel);
    
    jCh4Panel.setLayout (new GridLayout (3, 2));
    
    jCh4Panel.add (new JLabel ("Type"));
    this.jCh4Type = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementType.values ());
    this.jCh4Type.setSelectedItem (null);
    this.jCh4Type.setEditable (false);
    this.jCh4Type.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh4Type.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 4 type",
      Tek2440_GPIB_Settings.MeasurementType.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementType>) tek2440::setMeasurementChannel4Type,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh4Panel.add (JCenter.Y (this.jCh4Type));
    
    jCh4Panel.add (new JLabel ("Source"));
    this.jCh4Source = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementSource.values ());
    this.jCh4Source.setSelectedItem (null);
    this.jCh4Source.setEditable (false);
    this.jCh4Source.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh4Source.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 4 source",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementSource>) tek2440::setMeasurementChannel4Source,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh4Panel.add (JCenter.Y (this.jCh4Source));
    
    jCh4Panel.add (new JLabel ("DSource"));
    this.jCh4DSource = new JComboBox<> (Tek2440_GPIB_Settings.MeasurementSource.values ());
    this.jCh4DSource.setSelectedItem (null);
    this.jCh4DSource.setEditable (false);
    this.jCh4DSource.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jCh4DSource.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "measurement channel 4 source",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MeasurementSource>) tek2440::setMeasurementChannel4DSource,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jCh4Panel.add (JCenter.Y (this.jCh4DSource));
    
    methodPanel.setLayout (new GridLayout (1, 4));
    
    final JPanel jProximalPanel = new JPanel ();
    jProximalPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Proximal"));
    methodPanel.add (jProximalPanel);
    
    jProximalPanel.setLayout (new GridLayout (3, 2));
    
    jProximalPanel.add (new JLabel ("Unit"));
    this.jProximalUnit = new JComboBox<> (Tek2440_GPIB_Settings.ProximalUnit.values ());
    this.jProximalUnit.setSelectedItem (null);
    this.jProximalUnit.setEditable (false);
    this.jProximalUnit.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jProximalUnit.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "[measurement] proximal unit",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.ProximalUnit>) tek2440::setMeasurementProximalUnit,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jProximalPanel.add (JCenter.Y (this.jProximalUnit));
    
    jProximalPanel.add (new JLabel ("Volts"));
    this.jProximalVolts = new JTextField (12);
    this.jProximalVolts.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jProximalVoltsListener = new JInstrumentTextFieldListener_1Double (
      this.jProximalVolts,
      "[measurement] proximal volts level",
      this.jProximalVolts::getText,
      tek2440::setMeasurementProximalVoltsLevel,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jProximalVoltsListener.registerListener (this.jProximalVolts);
    jProximalPanel.add (JCenter.Y (this.jProximalVolts));
    
    jProximalPanel.add (new JLabel ("Percents"));
    this.jProximalPercents = new JTextField (12);
    this.jProximalPercents.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jProximalPercentsListener = new JInstrumentTextFieldListener_1Double (
      this.jProximalPercents,
      "[measurement] proximal percents level",
      this.jProximalPercents::getText,
      tek2440::setMeasurementProximalPercentsLevel,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jProximalPercentsListener.registerListener (this.jProximalPercents);
    jProximalPanel.add (JCenter.Y (this.jProximalPercents));
    
    final JPanel jMesialPanel = new JPanel ();
    jMesialPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Mesial"));
    methodPanel.add (jMesialPanel);
    
    jMesialPanel.setLayout (new GridLayout (3, 2));
    
    jMesialPanel.add (new JLabel ("Unit"));
    this.jMesialUnit = new JComboBox<> (Tek2440_GPIB_Settings.MesialUnit.values ());
    this.jMesialUnit.setSelectedItem (null);
    this.jMesialUnit.setEditable (false);
    this.jMesialUnit.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jMesialUnit.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "[measurement] mesial unit",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.MesialUnit>) tek2440::setMeasurementMesialUnit,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jMesialPanel.add (JCenter.Y (this.jMesialUnit));
    
    jMesialPanel.add (new JLabel ("Volts"));
    this.jMesialVolts = new JTextField (12);
    this.jMesialVolts.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jMesialVoltsListener = new JInstrumentTextFieldListener_1Double (
      this.jMesialVolts,
      "[measurement] mesial volts level",
      this.jMesialVolts::getText,
      tek2440::setMeasurementMesialVoltsLevel,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jMesialVoltsListener.registerListener (this.jMesialVolts);
    jMesialPanel.add (JCenter.Y (this.jMesialVolts));
    
    jMesialPanel.add (new JLabel ("Percents"));
    this.jMesialPercents = new JTextField (12);
    this.jMesialPercents.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jMesialPercentsListener = new JInstrumentTextFieldListener_1Double (
      this.jMesialPercents,
      "[measurement] mesial percents level",
      this.jMesialPercents::getText,
      tek2440::setMeasurementMesialPercentsLevel,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jMesialPercentsListener.registerListener (this.jMesialPercents);
    jMesialPanel.add (JCenter.Y (this.jMesialPercents));
    
    final JPanel jDMesialPanel = new JPanel ();
    jDMesialPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "DMesial"));
    methodPanel.add (jDMesialPanel);
    
    jDMesialPanel.setLayout (new GridLayout (3, 2));
    
    jDMesialPanel.add (new JLabel ("Unit"));
    this.jDMesialUnit = new JComboBox<> (Tek2440_GPIB_Settings.DMesialUnit.values ());
    this.jDMesialUnit.setSelectedItem (null);
    this.jDMesialUnit.setEditable (false);
    this.jDMesialUnit.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jDMesialUnit.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "[measurement] dmesial unit",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.DMesialUnit>) tek2440::setMeasurementDMesialUnit,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jDMesialPanel.add (JCenter.Y (this.jDMesialUnit));
    
    jDMesialPanel.add (new JLabel ("Volts"));
    this.jDMesialVolts = new JTextField (12);
    this.jDMesialVolts.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jDMesialVoltsListener = new JInstrumentTextFieldListener_1Double (
      this.jDMesialVolts,
      "[measurement] dmesial volts level",
      this.jDMesialVolts::getText,
      tek2440::setMeasurementDMesialVoltsLevel,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jDMesialVoltsListener.registerListener (this.jDMesialVolts);
    jDMesialPanel.add (JCenter.Y (this.jDMesialVolts));
    
    jDMesialPanel.add (new JLabel ("Percents"));
    this.jDMesialPercents = new JTextField (12);
    this.jDMesialPercents.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jDMesialPercentsListener = new JInstrumentTextFieldListener_1Double (
      this.jDMesialPercents,
      "[measurement] dmesial percents level",
      this.jDMesialPercents::getText,
      tek2440::setMeasurementDMesialPercentsLevel,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jDMesialPercentsListener.registerListener (this.jDMesialPercents);
    jDMesialPanel.add (JCenter.Y (this.jDMesialPercents));
    
    final JPanel jDistalPanel = new JPanel ();
    jDistalPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Distal"));
    methodPanel.add (jDistalPanel);
    
    jDistalPanel.setLayout (new GridLayout (3, 2));
    
    jDistalPanel.add (new JLabel ("Unit"));
    this.jDistalUnit = new JComboBox<> (Tek2440_GPIB_Settings.DistalUnit.values ());
    this.jDistalUnit.setSelectedItem (null);
    this.jDistalUnit.setEditable (false);
    this.jDistalUnit.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jDistalUnit.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "[measurement] distal unit",
      Tek2440_GPIB_Settings.MeasurementSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.DistalUnit>) tek2440::setMeasurementDistalUnit,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    jDistalPanel.add (JCenter.Y (this.jDistalUnit));
    
    jDistalPanel.add (new JLabel ("Volts"));
    this.jDistalVolts = new JTextField (12);
    this.jDistalVolts.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jDistalVoltsListener = new JInstrumentTextFieldListener_1Double (
      this.jDistalVolts,
      "[measurement] distal volts level",
      this.jDistalVolts::getText,
      tek2440::setMeasurementDistalVoltsLevel,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jDistalVoltsListener.registerListener (this.jDistalVolts);
    jDistalPanel.add (JCenter.Y (this.jDistalVolts));
    
    jDistalPanel.add (new JLabel ("Percents"));
    this.jDistalPercents = new JTextField (12);
    this.jDistalPercents.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jDistalPercentsListener = new JInstrumentTextFieldListener_1Double (
      this.jDistalPercents,
      "[measurement] distal percents level",
      this.jDistalPercents::getText,
      tek2440::setMeasurementDistalPercentsLevel,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jDistalPercentsListener.registerListener (this.jDistalPercents);
    jDistalPanel.add (JCenter.Y (this.jDistalPercents));
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }

  public JTek2440_GPIB_Measurement (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Measurement (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
  {
    this (digitalStorageOscilloscope, 0);
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
      return "Tektronix 2440 [GPIB] Measurement Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Measurement ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Measurement.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementMethod> jMethod;
  
  private final JColorCheckBox.JBoolean jWindowing;
  
  private final JColorCheckBox.JBoolean jDisplay;
  
  private final JColorCheckBox.JBoolean jDisplayThresholdMarkers;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementType> jCh1Type;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementSource> jCh1Source;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementSource> jCh1DSource;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementType> jCh2Type;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementSource> jCh2Source;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementSource> jCh2DSource;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementType> jCh3Type;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementSource> jCh3Source;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementSource> jCh3DSource;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementType> jCh4Type;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementSource> jCh4Source;
  
  private final JComboBox<Tek2440_GPIB_Settings.MeasurementSource> jCh4DSource;
  
  private final JComboBox<Tek2440_GPIB_Settings.ProximalUnit> jProximalUnit;
  
  private final JTextField jProximalVolts;
  
  private final JTextField jProximalPercents;
  
  private final JComboBox<Tek2440_GPIB_Settings.MesialUnit> jMesialUnit;
  
  private final JTextField jMesialVolts;
  
  private final JTextField jMesialPercents;
  
  private final JComboBox<Tek2440_GPIB_Settings.DMesialUnit> jDMesialUnit;
  
  private final JTextField jDMesialVolts;
  
  private final JTextField jDMesialPercents;
  
  private final JComboBox<Tek2440_GPIB_Settings.DistalUnit> jDistalUnit;
  
  private final JTextField jDistalVolts;
  
  private final JTextField jDistalPercents;
  
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
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JTek2440_GPIB_Measurement.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_Measurement.this.setInhibitInstrumentControl ();
        try
        {
          
          final Tek2440_GPIB_Settings.MeasurementMethod method = settings.getMeasurementMethod ();
          final boolean windowing = settings.isMeasurementWindowing ();
          final boolean display = settings.isMeasurementDisplay ();
          final boolean displayThresholdMarkers = settings.isMeasurementDisplayThresholdCrossingMarks ();
          
          JTek2440_GPIB_Measurement.this.jMethod.setSelectedItem (method);
          JTek2440_GPIB_Measurement.this.jMethod.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jWindowing.setDisplayedValue (windowing);
          JTek2440_GPIB_Measurement.this.jDisplay.setDisplayedValue (display);
          JTek2440_GPIB_Measurement.this.jDisplayThresholdMarkers.setDisplayedValue (displayThresholdMarkers);
  
          final Tek2440_GPIB_Settings.MeasurementType ch1Type = settings.getMeasurementChannel1Type ();
          final Tek2440_GPIB_Settings.MeasurementSource ch1Source = settings.getMeasurementChannel1Source ();
          final Tek2440_GPIB_Settings.MeasurementSource ch1DSource = settings.getMeasurementChannel1DelayedSource ();
          
          JTek2440_GPIB_Measurement.this.jCh1Type.setSelectedItem (ch1Type);
          JTek2440_GPIB_Measurement.this.jCh1Type.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jCh1Source.setSelectedItem (ch1Source);
          JTek2440_GPIB_Measurement.this.jCh1Source.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jCh1DSource.setSelectedItem (ch1DSource);
          JTek2440_GPIB_Measurement.this.jCh1DSource.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          
          final Tek2440_GPIB_Settings.MeasurementType ch2Type = settings.getMeasurementChannel2Type ();
          final Tek2440_GPIB_Settings.MeasurementSource ch2Source = settings.getMeasurementChannel2Source ();
          final Tek2440_GPIB_Settings.MeasurementSource ch2DSource = settings.getMeasurementChannel2DelayedSource ();
          
          JTek2440_GPIB_Measurement.this.jCh2Type.setSelectedItem (ch2Type);
          JTek2440_GPIB_Measurement.this.jCh2Type.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jCh2Source.setSelectedItem (ch2Source);
          JTek2440_GPIB_Measurement.this.jCh2Source.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jCh2DSource.setSelectedItem (ch2DSource);
          JTek2440_GPIB_Measurement.this.jCh2DSource.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());

          final Tek2440_GPIB_Settings.MeasurementType ch3Type = settings.getMeasurementChannel3Type ();
          final Tek2440_GPIB_Settings.MeasurementSource ch3Source = settings.getMeasurementChannel3Source ();
          final Tek2440_GPIB_Settings.MeasurementSource ch3DSource = settings.getMeasurementChannel3DelayedSource ();
          
          JTek2440_GPIB_Measurement.this.jCh3Type.setSelectedItem (ch3Type);
          JTek2440_GPIB_Measurement.this.jCh3Type.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jCh3Source.setSelectedItem (ch3Source);
          JTek2440_GPIB_Measurement.this.jCh3Source.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jCh3DSource.setSelectedItem (ch3DSource);
          JTek2440_GPIB_Measurement.this.jCh3DSource.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          
          final Tek2440_GPIB_Settings.MeasurementType ch4Type = settings.getMeasurementChannel4Type ();
          final Tek2440_GPIB_Settings.MeasurementSource ch4Source = settings.getMeasurementChannel4Source ();
          final Tek2440_GPIB_Settings.MeasurementSource ch4DSource = settings.getMeasurementChannel4DelayedSource ();
          
          JTek2440_GPIB_Measurement.this.jCh4Type.setSelectedItem (ch4Type);
          JTek2440_GPIB_Measurement.this.jCh4Type.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jCh4Source.setSelectedItem (ch4Source);
          JTek2440_GPIB_Measurement.this.jCh4Source.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jCh4DSource.setSelectedItem (ch4DSource);
          JTek2440_GPIB_Measurement.this.jCh4DSource.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          
          final Tek2440_GPIB_Settings.ProximalUnit proximalUnit = settings.getMeasurementProximalUnit ();
          final double proximalVolts = settings.getMeasurementProximalVoltsLevel ();
          final double proximalPercents = settings.getMeasurementProximalPercentsLevel ();
          
          JTek2440_GPIB_Measurement.this.jProximalUnit.setSelectedItem (proximalUnit);
          JTek2440_GPIB_Measurement.this.jProximalUnit.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jProximalVolts.setText (Double.toString (proximalVolts));
          JTek2440_GPIB_Measurement.this.jProximalVolts.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jProximalPercents.setText (Double.toString (proximalPercents));
          JTek2440_GPIB_Measurement.this.jProximalPercents.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          
          final Tek2440_GPIB_Settings.MesialUnit mesialUnit = settings.getMeasurementMesialUnit ();
          final double mesialVolts = settings.getMeasurementMesialVoltsLevel ();
          final double mesialPercents = settings.getMeasurementMesialPercentsLevel ();
          
          JTek2440_GPIB_Measurement.this.jMesialUnit.setSelectedItem (mesialUnit);
          JTek2440_GPIB_Measurement.this.jMesialUnit.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jMesialVolts.setText (Double.toString (mesialVolts));
          JTek2440_GPIB_Measurement.this.jMesialVolts.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jMesialPercents.setText (Double.toString (mesialPercents));
          JTek2440_GPIB_Measurement.this.jMesialPercents.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          
          final Tek2440_GPIB_Settings.DMesialUnit dMesialUnit = settings.getMeasurementDMesialUnit ();
          final double dMesialVolts = settings.getMeasurementDMesialVoltsLevel ();
          final double dMesialPercents = settings.getMeasurementDMesialPercentsLevel ();
          
          JTek2440_GPIB_Measurement.this.jDMesialUnit.setSelectedItem (dMesialUnit);
          JTek2440_GPIB_Measurement.this.jDMesialUnit.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jDMesialVolts.setText (Double.toString (dMesialVolts));
          JTek2440_GPIB_Measurement.this.jDMesialVolts.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jDMesialPercents.setText (Double.toString (dMesialPercents));
          JTek2440_GPIB_Measurement.this.jDMesialPercents.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          
          final Tek2440_GPIB_Settings.DistalUnit distalUnit = settings.getMeasurementDistalUnit ();
          final double distalVolts = settings.getMeasurementDistalVoltsLevel ();
          final double distalPercents = settings.getMeasurementDistalPercentsLevel ();
          
          JTek2440_GPIB_Measurement.this.jDistalUnit.setSelectedItem (distalUnit);
          JTek2440_GPIB_Measurement.this.jDistalUnit.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jDistalVolts.setText (Double.toString (distalVolts));
          JTek2440_GPIB_Measurement.this.jDistalVolts.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          JTek2440_GPIB_Measurement.this.jDistalPercents.setText (Double.toString (distalPercents));
          JTek2440_GPIB_Measurement.this.jDistalPercents.setBackground (JTek2440_GPIB_Measurement.this.getBackground ());
          
        }
        finally
        {
          JTek2440_GPIB_Measurement.this.resetInhibitInstrumentControl ();
        }
      });
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      // EMPTY
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
