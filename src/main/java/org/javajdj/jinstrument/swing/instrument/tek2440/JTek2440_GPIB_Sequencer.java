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
import java.util.Arrays;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for the Sequencer settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Sequencer
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Sequencer.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Sequencer (
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
    setLayout (new GridLayout (4, 1, 10, 0));
    
    final JTextPane jDescription = new JTextPane ();
    jDescription.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Description"));
    add (jDescription);
    
    final JPanel formatPanel = new JPanel ();
    formatPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Format"));
    add (formatPanel);
    
    final JPanel actionPanel = new JPanel ();
    actionPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Action(s)"));
    add (actionPanel);
    
    final JPanel maintenancePanel = new JPanel ();
    maintenancePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Sequencer Maintenance"));
    add (maintenancePanel);
    
    //
    // jDescription
    //
    
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Sequencer Settings and Commands</b>" +
                          "<ul>" +
                            "<li>Defines formatting in PRGm? return string;</li>" +
                            "<li>Defines what actions to associate with a sequence "
                                 + "or any particular step within that sequence;</li>" +
                            "<li>Defines protection (or forced unprotect) of a sequence.</li>" +
                          "</ul>" +
                          "</html>");
    
    //
    // formatPanel
    //
    
    formatPanel.setLayout (new GridLayout (1, 4));
    
    formatPanel.add (new JLabel ("Add Formatting [PRGm?]"));
    formatPanel.add (JCenter.Y (new JBoolean_JBoolean (
      "[sequencer] format [PRGm?]",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isSequencerFormatCharsEnabled (),
      tek2440::setSequencerEnableFormatChars,
      Color.green,
      true)));
    formatPanel.add (new JLabel ()); // Aesthetics, really...
    formatPanel.add (new JLabel ());

    //
    // actionsPanel
    //
    
    actionPanel.setLayout (new GridLayout (1, 1));
    actionPanel.add (JCenter.XY (new JLong_JBitsLong (
      Color.green,
      9,
      Arrays.asList (
        "Protect      ",
        "Pause        ",
        "SRQ          ",
        "Bell         ",
        "Print        ",
        "AutoSetup    ",
        "SelfTest     ",
        "SelfCal      ",
        "Repeat       "),
      false,
      "sequencer actions",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getSequencerActionsAsLong (),
      tek2440::setSequencerActionsFromLong,
      true)));
    
    //
    // maintenancePanel
    //
    
    maintenancePanel.setLayout (new GridLayout (1, 4));
    
    maintenancePanel.add (new JLabel ("Force"));
    maintenancePanel.add (JCenter.Y (new JBoolean_JBoolean (
      "[sequencer] force",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isSequencerForce (),
      tek2440::setSequencerForce,
      Color.green,
      true)));
    maintenancePanel.add (new JLabel ()); // Aesthetics, really...
    maintenancePanel.add (new JLabel ());
    
  }

  public JTek2440_GPIB_Sequencer (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Sequencer (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Sequencer Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Sequencer ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Sequencer.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
