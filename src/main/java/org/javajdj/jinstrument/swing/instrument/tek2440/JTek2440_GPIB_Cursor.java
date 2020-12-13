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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
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
    add (jDescription);
    
    final JPanel mainPanel = new JPanel ();
    mainPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Main"));
    add (mainPanel);
    
    final JPanel cursorTypePanel = new JPanel ();
    add (cursorTypePanel);
    
    cursorTypePanel.setLayout (new GridLayout (1, 3));
    
    final JPanel voltsPanel = new JPanel ();
    voltsPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_VOLTAGE_COLOR, 2),
        "Volts Cursor"));
    cursorTypePanel.add (voltsPanel);
    
    final JPanel timePanel = new JPanel ();
    timePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_TIME_COLOR, 2),
        "Time Cursor"));
    cursorTypePanel.add (timePanel);
    
    final JPanel slopePanel = new JPanel ();
    slopePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_FREQUENCY_COLOR, 2),
        "Slope Cursor"));
    cursorTypePanel.add (slopePanel);
    
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

    final JPanel commandPanel = new JPanel ();
    commandPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Commands"));
    add (commandPanel);
    
    //
    // jDescription
    //
    
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Cursor Settings</b>" +
                          "<ul>" +
                            "<li>Allows for the definition of a Volts, Time, or Slope Cursor on a variety of sources;</li>" +
                            "<li>Supports absolute and \"delta\" measurements;</li>" +
                            "<li>Supports absolute, dB, % and degrees units.</li>" +
                          "</ul>" +
                          "</html>");
    
    //
    // mainPanel
    //
    
    mainPanel.setLayout (new GridLayout (2, 5));
    
    mainPanel.add (new JLabel ("Function"));
    mainPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.CursorFunction.class,
      "cursor function",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorFunction (),
      tek2440::setCursorFunction,
      true)));
    
    mainPanel.add (new JLabel ());
    
    mainPanel.add (new JLabel ("Mode"));
    mainPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.CursorMode.class,
      "cursor mode",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorMode (),
      tek2440::setCursorMode,
      true)));
    
    mainPanel.add (new JLabel ("Target"));
    mainPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.CursorTarget.class,
      "cursor target",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorTarget (),
      tek2440::setCursorTarget,
      true)));
    
    mainPanel.add (new JLabel ());
    
    mainPanel.add (new JLabel ("Cursor Select"));
    mainPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.CursorSelect.class,
      "cursor select",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorSelect (),
      tek2440::setCursorSelect,
      true)));
    
    //
    // voltsPanel
    //
    
    voltsPanel.setLayout (new GridLayout (3, 2));
    
    voltsPanel.add (new JLabel ("Unit"));
    voltsPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.CursorUnitVolts.class,
      "volts cursor unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorUnitVolts (),
      tek2440::setCursorUnitVolts,
      true)));
    
    voltsPanel.add (new JLabel ("Ref Unit"));
    voltsPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.RefVoltsUnit.class,
      "ref volts cursor unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorUnitRefVolts (),
      tek2440::setCursorUnitRefVolts,
      true)));
    
    voltsPanel.add (new JLabel ("Ref Value"));
    voltsPanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "ref volts cursor value",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorValueRefVolts (),
      tek2440::setCursorValueRefVolts,
      true)));
    
    //
    // timePanel
    //
    
    timePanel.setLayout (new GridLayout (3, 2));
    
    timePanel.add (new JLabel ("Unit"));
    timePanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.CursorUnitTime.class,
      "time cursor unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorUnitTime (),
      tek2440::setCursorUnitTime,
      true)));
    
    timePanel.add (new JLabel ("Ref Unit"));
    timePanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.RefTimeUnit.class,
      "ref time cursor unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorUnitRefTime (),
      tek2440::setCursorUnitRefTime,
      true)));
    
    timePanel.add (new JLabel ("Ref Value"));
    timePanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "ref time cursor value",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorValueRefTime (),
      tek2440::setCursorValueRefTime,
      true)));
    
    //
    // slopePanel
    //
    
    slopePanel.setLayout (new GridLayout (4, 2));
    
    slopePanel.add (new JLabel ("Unit"));
    slopePanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.CursorUnitSlope.class,
      "slope cursor unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorUnitSlope (),
      tek2440::setCursorUnitSlope,
      true)));
    
    slopePanel.add (new JLabel ("Ref X Unit"));
    slopePanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.RefSlopeXUnit.class,
      "ref slope cursor X unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorUnitRefSlopeX (),
      tek2440::setCursorUnitRefSlopeX,
      true)));
    
    slopePanel.add (new JLabel ("Ref Y Unit"));
    slopePanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.RefSlopeYUnit.class,
      "ref slope cursor Y unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorUnitRefSlopeY (),
      tek2440::setCursorUnitRefSlopeY,
      true)));
    
    slopePanel.add (new JLabel ("Ref Value"));
    slopePanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "ref slope cursor value",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorValueRefSlope (),
      tek2440::setCursorValueRefSlope,
      true)));
    
    //
    // position1Panel
    //
    
    position1Panel.setLayout (new GridLayout (3, 2));
    
    position1Panel.add (new JLabel ("Time [0, 1023]")); // [0, 1023]@0.01
    position1Panel.add (JCenter.Y (new JDouble_JSlider (
      SwingConstants.HORIZONTAL,
      0.0,
      1023.0,
      null,
      1023.0,
      512.5,
      "time position cursor 1",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorPosition1t (),
      tek2440::setCursorPosition1t,
      (i) -> i / 100.0,
      (d) -> (int) Math.round (d * 100.0),
      true)));
    
    position1Panel.add (new JLabel ("Y [+/- 4.1 Div]")); // [-4.1, 4.1]@0.01
    position1Panel.add (JCenter.Y (new JDouble_JSlider (
      SwingConstants.HORIZONTAL,
      -4.1,
      4.1,
      null,
      4.1,
      null,
      "Y position cursor 1",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorPosition1Y (),
      tek2440::setCursorPosition1Y,
      (i) -> i / 100.0,
      (d) -> (int) Math.round (d * 100.0),
      true)));
    
    position1Panel.add (new JLabel ("X [+/- 5.1 Div]")); // [-5.1, 5.1]@0.01
    position1Panel.add (JCenter.Y (new JDouble_JSlider (
      SwingConstants.HORIZONTAL,
      -5.1,
      5.1,
      null,
      5.1,
      null,
      "X position cursor 1",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorPosition1X (),
      tek2440::setCursorPosition1X,
      (i) -> i / 100.0,
      (d) -> (int) Math.round (d * 100.0),
      true)));
    
    //
    // position2Panel
    //
    
    position2Panel.setLayout (new GridLayout (3, 2));
    
    position2Panel.add (new JLabel ("Time [0, 1023]")); // [0, 1023]@0.01
    position2Panel.add (JCenter.Y (new JDouble_JSlider (
      SwingConstants.HORIZONTAL,
      0.0,
      1023.0,
      null,
      1023.0,
      512.5,
      "time position cursor 2",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorPosition2t (),
      tek2440::setCursorPosition2t,
      (i) -> i / 100.0,
      (d) -> (int) Math.round (d * 100.0),
      true)));
    
    position2Panel.add (new JLabel ("Y [+/- 4.1 Div]")); // [-4.1, 4.1]@0.01
    position2Panel.add (JCenter.Y (new JDouble_JSlider (
      SwingConstants.HORIZONTAL,
      -4.1,
      4.1,
      null,
      4.1,
      null,
      "Y position cursor 2",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorPosition2Y (),
      tek2440::setCursorPosition2Y,
      (i) -> i / 100.0,
      (d) -> (int) Math.round (d * 100.0),
      true)));
    
    position2Panel.add (new JLabel ("X [+/- 5.1 Div]")); // [-5.1, 5.1]@0.01
    position2Panel.add (JCenter.Y (new JDouble_JSlider (
      SwingConstants.HORIZONTAL,
      -5.1,
      5.1,
      null,
      5.1,
      null,
      "X position cursor 2",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getCursorPosition2X (),
      tek2440::setCursorPosition2X,
      (i) -> i / 100.0,
      (d) -> (int) Math.round (d * 100.0),
      true)));
    
    //
    // commandPanel
    //
    
    commandPanel.setLayout (new GridLayout (1, 1));
    
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
