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
import javax.swing.JSlider;
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

/** A Swing panel for the Cursor settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Cursor
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Cursor.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Cursor (
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
    setLayout (new GridLayout (5, 1, 10, 0));
    
    final JTextPane jDescription = new JTextPane ();
    jDescription.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Description"));
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Cursor Settings</b>" +
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
    
    mainPanel.setLayout (new GridLayout (2, 5));
    
    mainPanel.add (new JLabel ("Function"));
    this.jFunction = new JComboBox<> (Tek2440_GPIB_Settings.CursorFunction.values ());
    this.jFunction.setSelectedItem (null);
    this.jFunction.setEditable (false);
    this.jFunction.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jFunction.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "cursor function",
      Tek2440_GPIB_Settings.CursorFunction.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.CursorFunction>) tek2440::setCursorFunction,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    mainPanel.add (JCenter.Y (this.jFunction));
    
    mainPanel.add (new JLabel ());
    
    mainPanel.add (new JLabel ("Mode"));
    this.jMode = new JComboBox<> (Tek2440_GPIB_Settings.CursorMode.values ());
    this.jMode.setSelectedItem (null);
    this.jMode.setEditable (false);
    this.jMode.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jMode.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "cursor mode",
      Tek2440_GPIB_Settings.CursorMode.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.CursorMode>) tek2440::setCursorMode,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    mainPanel.add (JCenter.Y (this.jMode));
    
    mainPanel.add (new JLabel ("Target"));
    this.jTarget = new JComboBox<> (Tek2440_GPIB_Settings.CursorTarget.values ());
    this.jTarget.setSelectedItem (null);
    this.jTarget.setEditable (false);
    this.jTarget.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jTarget.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "cursor target",
      Tek2440_GPIB_Settings.CursorTarget.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.CursorTarget>) tek2440::setCursorTarget,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    mainPanel.add (JCenter.Y (this.jTarget));
    
    mainPanel.add (new JLabel ());
    
    mainPanel.add (new JLabel ("Cursor Select"));
    this.jSelect = new JComboBox<> (Tek2440_GPIB_Settings.CursorSelect.values ());
    this.jSelect.setSelectedItem (null);
    this.jSelect.setEditable (false);
    this.jSelect.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jSelect.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "cursor select",
      Tek2440_GPIB_Settings.CursorSelect.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.CursorSelect>) tek2440::setCursorSelect,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    mainPanel.add (JCenter.Y (this.jSelect));
    
    final JPanel cursorTypePanel = new JPanel ();
    add (cursorTypePanel);
    
    cursorTypePanel.setLayout (new GridLayout (1, 3));
    
    final JPanel voltsPanel = new JPanel ();
    voltsPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_VOLTAGE_COLOR, 2),
        "Volts Cursor"));
    cursorTypePanel.add (voltsPanel);
    
    voltsPanel.setLayout (new GridLayout (3, 2));
    
    voltsPanel.add (new JLabel ("Unit"));
    this.jUnitVolts = new JComboBox<> (Tek2440_GPIB_Settings.CursorUnitVolts.values ());
    this.jUnitVolts.setSelectedItem (null);
    this.jUnitVolts.setEditable (false);
    this.jUnitVolts.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jUnitVolts.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "volts cursor unit",
      Tek2440_GPIB_Settings.CursorUnitVolts.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.CursorUnitVolts>) tek2440::setCursorUnitVolts,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    voltsPanel.add (JCenter.Y (jUnitVolts));
    
    voltsPanel.add (new JLabel ("Ref Unit"));
    this.jUnitRefVolts = new JComboBox<> (Tek2440_GPIB_Settings.RefVoltsUnit.values ());
    this.jUnitRefVolts.setSelectedItem (null);
    this.jUnitRefVolts.setEditable (false);
    this.jUnitRefVolts.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jUnitRefVolts.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "ref volts cursor unit",
      Tek2440_GPIB_Settings.RefVoltsUnit.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.RefVoltsUnit>) tek2440::setCursorUnitRefVolts,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    voltsPanel.add (JCenter.Y (jUnitRefVolts));
    
    voltsPanel.add (new JLabel ("Ref Value"));
    this.jValueRefVolts = new JTextField (12);
    this.jValueRefVolts.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jValueRefVoltsListener = new JInstrumentTextFieldListener_1Double (
      this.jValueRefVolts,
      "ref volts cursor value",
      this.jValueRefVolts::getText,
      tek2440::setCursorValueRefVolts,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jValueRefVoltsListener.registerListener (this.jValueRefVolts);
    voltsPanel.add (JCenter.Y (this.jValueRefVolts));    
    
    final JPanel timePanel = new JPanel ();
    timePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_TIME_COLOR, 2),
        "Time Cursor"));
    cursorTypePanel.add (timePanel);
    
    timePanel.setLayout (new GridLayout (3, 2));
    
    timePanel.add (new JLabel ("Unit"));
    this.jUnitTime = new JComboBox<> (Tek2440_GPIB_Settings.CursorUnitTime.values ());
    this.jUnitTime.setSelectedItem (null);
    this.jUnitTime.setEditable (false);
    this.jUnitTime.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jUnitTime.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "time cursor unit",
      Tek2440_GPIB_Settings.CursorUnitTime.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.CursorUnitTime>) tek2440::setCursorUnitTime,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    timePanel.add (JCenter.Y (jUnitTime));
    
    timePanel.add (new JLabel ("Ref Unit"));
    this.jUnitRefTime = new JComboBox<> (Tek2440_GPIB_Settings.RefTimeUnit.values ());
    this.jUnitRefTime.setSelectedItem (null);
    this.jUnitRefTime.setEditable (false);
    this.jUnitRefTime.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jUnitRefTime.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "ref time cursor unit",
      Tek2440_GPIB_Settings.RefTimeUnit.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.RefTimeUnit>) tek2440::setCursorUnitRefTime,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    timePanel.add (JCenter.Y (jUnitRefTime));
    
    timePanel.add (new JLabel ("Ref Value"));
    this.jValueRefTime = new JTextField (12);
    this.jValueRefTime.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jValueRefTimeListener = new JInstrumentTextFieldListener_1Double (
      this.jValueRefTime,
      "ref time cursor value",
      this.jValueRefTime::getText,
      tek2440::setCursorValueRefTime,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jValueRefTimeListener.registerListener (this.jValueRefTime);
    timePanel.add (JCenter.Y (this.jValueRefTime));    
    
    final JPanel slopePanel = new JPanel ();
    slopePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_FREQUENCY_COLOR, 2),
        "Slope Cursor"));
    cursorTypePanel.add (slopePanel);
    
    slopePanel.setLayout (new GridLayout (4, 2));
    
    slopePanel.add (new JLabel ("Unit"));
    this.jUnitSlope = new JComboBox<> (Tek2440_GPIB_Settings.CursorUnitSlope.values ());
    this.jUnitSlope.setSelectedItem (null);
    this.jUnitSlope.setEditable (false);
    this.jUnitSlope.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jUnitSlope.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "slope cursor unit",
      Tek2440_GPIB_Settings.CursorUnitSlope.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.CursorUnitSlope>) tek2440::setCursorUnitSlope,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    slopePanel.add (JCenter.Y (jUnitSlope));
    
    slopePanel.add (new JLabel ("Ref X Unit"));
    this.jUnitRefSlopeX = new JComboBox<> (Tek2440_GPIB_Settings.RefSlopeXUnit.values ());
    this.jUnitRefSlopeX.setSelectedItem (null);
    this.jUnitRefSlopeX.setEditable (false);
    this.jUnitRefSlopeX.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jUnitRefSlopeX.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "ref slope cursor X unit",
      Tek2440_GPIB_Settings.RefSlopeXUnit.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.RefSlopeXUnit>) tek2440::setCursorUnitRefSlopeX,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    slopePanel.add (JCenter.Y (jUnitRefSlopeX));
    
    slopePanel.add (new JLabel ("Ref Y Unit"));
    this.jUnitRefSlopeY = new JComboBox<> (Tek2440_GPIB_Settings.RefSlopeYUnit.values ());
    this.jUnitRefSlopeY.setSelectedItem (null);
    this.jUnitRefSlopeY.setEditable (false);
    this.jUnitRefSlopeY.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jUnitRefSlopeY.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "ref slope cursor Y unit",
      Tek2440_GPIB_Settings.RefSlopeYUnit.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.RefSlopeYUnit>) tek2440::setCursorUnitRefSlopeY,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    slopePanel.add (JCenter.Y (jUnitRefSlopeY));
    
    slopePanel.add (new JLabel ("Ref Value"));
    this.jValueRefSlope = new JTextField (12);
    this.jValueRefSlope.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    final JInstrumentTextFieldListener_1Double jValueRefSlopeListener = new JInstrumentTextFieldListener_1Double (
      this.jValueRefSlope,
      "ref slope cursor value",
      this.jValueRefSlope::getText,
      tek2440::setCursorValueRefSlope,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor (),
      true);
    jValueRefSlopeListener.registerListener (this.jValueRefSlope);
    slopePanel.add (JCenter.Y (this.jValueRefSlope));    
    
    final JPanel cursorPositionPanel = new JPanel ();
    add (cursorPositionPanel);
    
    cursorPositionPanel.setLayout (new GridLayout (1, 2));
    
    final JPanel position1Panel = new JPanel ();
    position1Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Cursor 1 Position"));
    cursorPositionPanel.add (position1Panel);
    
    final JPanel position2Panel = new JPanel ();
    position2Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Cursor 2 Position"));
    cursorPositionPanel.add (position2Panel);

    position1Panel.setLayout (new GridLayout (3, 2));
    
    position1Panel.add (new JLabel ("Time [0, 1023]"));
    this.jPositionTime1 = new JSlider (0, 1023);
    this.jPositionTime1.setMajorTickSpacing (1023);
    this.jPositionTime1.setMinorTickSpacing (128);
    this.jPositionTime1.setPaintTicks (true);
    // this.jPositionTime1.setPaintLabels (true);    
    this.jPositionTime1.setToolTipText ("-");
    this.jPositionTime1.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jPositionTime1.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "time position cursor 1",
      tek2440::setCursorPosition1t,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    position1Panel.add (JCenter.Y (this.jPositionTime1));
    
    position1Panel.add (new JLabel ("Y [+/- 4.1 Div]"));
    this.jPositionY1 = new JSlider (-410, 410); // [-4.1, 4.1]@0.01
    this.jPositionY1.setMajorTickSpacing (410);
    // this.jPositionY1.setMinorTickSpacing (82);
    this.jPositionY1.setPaintTicks (true);
    // this.jPositionY1.setPaintLabels (true);    
    this.jPositionY1.setToolTipText ("-");
    this.jPositionY1.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jPositionY1.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "Y position cursor 1",
      (final double a) -> tek2440.setCursorPosition1Y (a / 100),
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    position1Panel.add (JCenter.Y (this.jPositionY1));
    
    position1Panel.add (new JLabel ("X [+/- 5.1 Div]"));
    this.jPositionX1 = new JSlider (-510, 510); // [-5.1, 5.1]@0.01
    this.jPositionX1.setMajorTickSpacing (510);
    // this.jPositionX1.setMinorTickSpacing (102);
    this.jPositionX1.setPaintTicks (true);
    // this.jPositionX1.setPaintLabels (true);    
    this.jPositionX1.setToolTipText ("-");
    this.jPositionX1.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jPositionX1.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "X position cursor 1",
      (final double a) -> tek2440.setCursorPosition1X (a / 100),
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    position1Panel.add (JCenter.Y (this.jPositionX1));
    
    position2Panel.setLayout (new GridLayout (3, 2));
    
    position2Panel.add (new JLabel ("Time [0, 1023]"));
    this.jPositionTime2 = new JSlider (0, 1023);
    this.jPositionTime2.setMajorTickSpacing (1023);
    this.jPositionTime2.setMinorTickSpacing (128);
    this.jPositionTime2.setPaintTicks (true);
    // this.jPositionTime2.setPaintLabels (true);    
    this.jPositionTime2.setToolTipText ("-");
    this.jPositionTime2.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jPositionTime2.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "time position cursor 2",
      tek2440::setCursorPosition2t,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    position2Panel.add (JCenter.Y (this.jPositionTime2));
    
    position2Panel.add (new JLabel ("Y [+/- 4.1 Div]"));
    this.jPositionY2 = new JSlider (-410, 410); // [-4.1, 4.1]@0.01
    this.jPositionY2.setMajorTickSpacing (410);
    // this.jPositionY2.setMinorTickSpacing (82);
    this.jPositionY2.setPaintTicks (true);
    // this.jPositionY2.setPaintLabels (true);    
    this.jPositionY2.setToolTipText ("-");
    this.jPositionY2.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jPositionY2.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "Y position cursor 2",
      (final double a) -> tek2440.setCursorPosition2Y (a / 100),
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    position2Panel.add (JCenter.Y (this.jPositionY2));
    
    position2Panel.add (new JLabel ("X [+/- 5.1 Div]"));
    this.jPositionX2 = new JSlider (-510, 510); // [-5.1, 5.1]@0.01
    this.jPositionX2.setMajorTickSpacing (510);
    // this.jPositionX2.setMinorTickSpacing (102);
    this.jPositionX2.setPaintTicks (true);
    // this.jPositionX2.setPaintLabels (true);    
    this.jPositionX2.setToolTipText ("-");
    this.jPositionX2.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jPositionX2.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "X position cursor 2",
      (final double a) -> tek2440.setCursorPosition2X (a / 100),
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    position2Panel.add (JCenter.Y (this.jPositionX2));
    
    final JPanel commandPanel = new JPanel ();
    commandPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Commands"));
    add (commandPanel);
    
    commandPanel.setLayout (new GridLayout (1, 1));
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }

  public JTek2440_GPIB_Cursor (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Cursor (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Cursor Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Cursor ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Cursor.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JComboBox<Tek2440_GPIB_Settings.CursorFunction> jFunction;
  
  private final JComboBox<Tek2440_GPIB_Settings.CursorTarget> jTarget;
  
  private final JComboBox<Tek2440_GPIB_Settings.CursorMode> jMode;
  
  private final JComboBox<Tek2440_GPIB_Settings.CursorSelect> jSelect;
  
  private final JComboBox<Tek2440_GPIB_Settings.CursorUnitVolts> jUnitVolts;
  
  private final JComboBox<Tek2440_GPIB_Settings.RefVoltsUnit> jUnitRefVolts;
  
  private final JTextField jValueRefVolts;
  
  private final JComboBox<Tek2440_GPIB_Settings.CursorUnitTime> jUnitTime;
  
  private final JComboBox<Tek2440_GPIB_Settings.RefTimeUnit> jUnitRefTime;
  
  private final JTextField jValueRefTime;
  
  private final JComboBox<Tek2440_GPIB_Settings.CursorUnitSlope> jUnitSlope;
  
  private final JComboBox<Tek2440_GPIB_Settings.RefSlopeXUnit> jUnitRefSlopeX;
  
  private final JComboBox<Tek2440_GPIB_Settings.RefSlopeYUnit> jUnitRefSlopeY;
  
  private final JTextField jValueRefSlope;
  
  private final JSlider jPositionTime1;
  
  private final JSlider jPositionY1;
  
  private final JSlider jPositionX1;
  
  private final JSlider jPositionTime2;
  
  private final JSlider jPositionY2;
  
  private final JSlider jPositionX2;
  
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
      if (instrument != JTek2440_GPIB_Cursor.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_Cursor.this.setInhibitInstrumentControl ();
        try
        {
          
          final Tek2440_GPIB_Settings.CursorFunction cursorFunction = settings.getCursorFunction ();
          final Tek2440_GPIB_Settings.CursorTarget cursorTarget = settings.getCursorTarget ();
          final Tek2440_GPIB_Settings.CursorMode cursorMode = settings.getCursorMode ();
          final Tek2440_GPIB_Settings.CursorSelect cursorSelect = settings.getCursorSelect ();
          JTek2440_GPIB_Cursor.this.jFunction.setSelectedItem (cursorFunction);
          JTek2440_GPIB_Cursor.this.jFunction.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jTarget.setSelectedItem (cursorTarget);
          JTek2440_GPIB_Cursor.this.jTarget.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jMode.setSelectedItem (cursorMode);
          JTek2440_GPIB_Cursor.this.jMode.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jSelect.setSelectedItem (cursorSelect);
          JTek2440_GPIB_Cursor.this.jSelect.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          
          final Tek2440_GPIB_Settings.CursorUnitVolts unitVolts = settings.getCursorUnitVolts ();
          final Tek2440_GPIB_Settings.RefVoltsUnit unitRefVolts = settings.getCursorUnitRefVolts ();
          final double valueRefVolts = settings.getCursorValueRefVolts ();
          JTek2440_GPIB_Cursor.this.jUnitVolts.setSelectedItem (unitVolts);
          JTek2440_GPIB_Cursor.this.jUnitVolts.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jUnitRefVolts.setSelectedItem (unitRefVolts);
          JTek2440_GPIB_Cursor.this.jUnitRefVolts.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jValueRefVolts.setText (Double.toString (valueRefVolts));
          JTek2440_GPIB_Cursor.this.jValueRefVolts.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          
          final Tek2440_GPIB_Settings.CursorUnitTime unitTime = settings.getCursorUnitTime ();
          final Tek2440_GPIB_Settings.RefTimeUnit unitRefTime = settings.getCursorUnitRefTime ();
          final double valueRefTime = settings.getCursorValueRefTime ();
          JTek2440_GPIB_Cursor.this.jUnitTime.setSelectedItem (unitTime);
          JTek2440_GPIB_Cursor.this.jUnitTime.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jUnitRefTime.setSelectedItem (unitRefTime);
          JTek2440_GPIB_Cursor.this.jUnitRefTime.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jValueRefTime.setText (Double.toString (valueRefTime));
          JTek2440_GPIB_Cursor.this.jValueRefTime.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          
          final Tek2440_GPIB_Settings.CursorUnitSlope unitSlope = settings.getCursorUnitSlope ();
          final Tek2440_GPIB_Settings.RefSlopeXUnit unitRefSlopeX = settings.getCursorUnitRefSlopeX ();
          final Tek2440_GPIB_Settings.RefSlopeYUnit unitRefSlopeY = settings.getCursorUnitRefSlopeY ();
          final double valueRefSlope = settings.getCursorValueRefSlope ();
          JTek2440_GPIB_Cursor.this.jUnitSlope.setSelectedItem (unitSlope);
          JTek2440_GPIB_Cursor.this.jUnitSlope.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jUnitRefSlopeX.setSelectedItem (unitRefSlopeX);
          JTek2440_GPIB_Cursor.this.jUnitRefSlopeX.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jUnitRefSlopeY.setSelectedItem (unitRefSlopeY);
          JTek2440_GPIB_Cursor.this.jUnitRefSlopeY.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jValueRefSlope.setText (Double.toString (valueRefSlope));
          JTek2440_GPIB_Cursor.this.jValueRefSlope.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          
          final double positionTime1 = settings.getCursorPosition1t ();
          final double positionY1 = settings.getCursorPosition1Y ();
          final double positionX1 = settings.getCursorPosition1X ();          
          final double positionTime2 = settings.getCursorPosition2t ();
          final double positionY2 = settings.getCursorPosition2Y ();
          final double positionX2 = settings.getCursorPosition2X ();
          JTek2440_GPIB_Cursor.this.jPositionTime1.setValue ((int) Math.round (positionTime1));
          JTek2440_GPIB_Cursor.this.jPositionTime1.setToolTipText (Double.toString (positionTime1));
          JTek2440_GPIB_Cursor.this.jPositionTime1.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jPositionY1.setValue ((int) Math.round (100 * positionY1));
          JTek2440_GPIB_Cursor.this.jPositionY1.setToolTipText (Double.toString (positionY1));
          JTek2440_GPIB_Cursor.this.jPositionY1.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jPositionX1.setValue ((int) Math.round (100 * positionX1));
          JTek2440_GPIB_Cursor.this.jPositionX1.setToolTipText (Double.toString (positionX1));
          JTek2440_GPIB_Cursor.this.jPositionX1.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jPositionTime2.setValue ((int) Math.round (positionTime2));
          JTek2440_GPIB_Cursor.this.jPositionTime2.setToolTipText (Double.toString (positionTime2));
          JTek2440_GPIB_Cursor.this.jPositionTime2.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jPositionY2.setValue ((int) Math.round (100 * positionY2));
          JTek2440_GPIB_Cursor.this.jPositionY2.setToolTipText (Double.toString (positionY2));
          JTek2440_GPIB_Cursor.this.jPositionY2.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          JTek2440_GPIB_Cursor.this.jPositionX2.setValue ((int) Math.round (100 * positionX2));
          JTek2440_GPIB_Cursor.this.jPositionX2.setToolTipText (Double.toString (positionX2));
          JTek2440_GPIB_Cursor.this.jPositionX2.setBackground (JTek2440_GPIB_Cursor.this.getBackground ());
          
        }
        finally
        {
          JTek2440_GPIB_Cursor.this.resetInhibitInstrumentControl ();
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
