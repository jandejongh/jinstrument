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

import java.awt.BorderLayout;
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
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for the Ref(erence) settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Ref
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Ref.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Ref (
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
    
    final JPanel northPanel = new JPanel ();
    northPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Reference [Waveform] Contents"));
    add (northPanel);
    
    final JPanel centerPanel = new JPanel ();
    centerPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Reference [Waveform] Transfer"));
    add (centerPanel);
    
    final JPanel southPanel = new JPanel ();
    southPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Reference [Waveform] Positions"));
    add (southPanel);
    
    //
    // jDesciption
    //
    
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Waveform Reference Settings and Operations</b>" +
                          "<ul>" +
                            "<li>Shows status (empty or not) of the reference waveforms;</li>" +
                            "<li>Shows and controls display status;</li>" +
                            "<li>Clear selected reference waveforms;</li>" +
                            "<li>Transfers acquisition data into selected reference waveforms;</li>" +
                            "<li>Positions (X; in buffer) selected reference waveforms.</li>" +
                          "</ul>" +
                          "</html>");
    
    //
    // northPanel
    //
    
    northPanel.setLayout (new GridLayout (4, 5, 10, 10));
    
    northPanel.add (new JLabel ());
    northPanel.add (new JLabel (" 1"));
    northPanel.add (new JLabel (" 2"));
    northPanel.add (new JLabel (" 3"));
    northPanel.add (new JLabel (" 4"));
    
    northPanel.add (new JLabel ("Contents"));
    
    northPanel.add (new JBoolean_JBoolean (
      (settings) -> ! ((Tek2440_GPIB_Settings) settings).isEmptyRef1 (),
      Color.green));
    
    northPanel.add (new JBoolean_JBoolean (
      (settings) -> ! ((Tek2440_GPIB_Settings) settings).isEmptyRef2 (),
      Color.green));
    
    northPanel.add (new JBoolean_JBoolean (
      (settings) -> ! ((Tek2440_GPIB_Settings) settings).isEmptyRef3 (),
      Color.green));
    
    northPanel.add (new JBoolean_JBoolean (
      (settings) -> ! ((Tek2440_GPIB_Settings) settings).isEmptyRef4 (),
      Color.green));
    
    northPanel.add (new JLabel ("Display"));

    northPanel.add (new JBoolean_JBoolean (
      "display reference (waveform) 1",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDisplayRef1 (),
      tek2440::setDisplayReference1,
      Color.green,
      true));    
    
    northPanel.add (new JBoolean_JBoolean (
      "display reference (waveform) 2",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDisplayRef2 (),
      tek2440::setDisplayReference2,
      Color.green,
      true));    
    
    northPanel.add (new JBoolean_JBoolean (
      "display reference (waveform) 3",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDisplayRef3 (),
      tek2440::setDisplayReference3,
      Color.green,
      true));    
    
    northPanel.add (new JBoolean_JBoolean (
      "display reference (waveform) 4",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDisplayRef4 (),
      tek2440::setDisplayReference4,
      Color.green,
      true));    
    
    northPanel.add (new JLabel ("Clear"));
    
    northPanel.add (new JVoid_JColorCheckBox (
      "clear reference (waveform) 1",
      tek2440::clearReference1,
      Color.blue));
    
    northPanel.add (new JVoid_JColorCheckBox (
      "clear reference (waveform) 2",
      tek2440::clearReference2,
      Color.blue));
    
    northPanel.add (new JVoid_JColorCheckBox (
      "clear reference (waveform) 3",
      tek2440::clearReference3,
      Color.blue));
    
    northPanel.add (new JVoid_JColorCheckBox (
      "clear reference (waveform) 4",
      tek2440::clearReference4,
      Color.blue));
    
    //
    // centerPanel [jFromPanel, jRefSavePanel]
    //
    
    centerPanel.setLayout (new GridLayout (1, 2, 0, 0));
    
    final JPanel jFromPanel = new JPanel ();
    jFromPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Source"));
    centerPanel.add (jFromPanel);
        
    final JPanel jRefSavePanel = new JPanel ();
    jRefSavePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Save To"));
    centerPanel.add (jRefSavePanel);
        
    jFromPanel.setLayout (new GridLayout (1, 1));
    
    jFromPanel.add (JCenter.XY (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.RefFrom.class,
      "reference (save) source/from",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getRefSource (),
      tek2440::setReferenceSource,
      true)));
    
    jRefSavePanel.setLayout (new GridLayout (2, 5));
    
    jRefSavePanel.add (JCenter.XY (new JLabel ("Ref1")));
    jRefSavePanel.add (JCenter.XY (new JLabel ("Ref2")));
    jRefSavePanel.add (JCenter.XY (new JLabel ("Ref3")));
    jRefSavePanel.add (JCenter.XY (new JLabel ("Ref4")));
    jRefSavePanel.add (JCenter.XY (new JLabel ("Stack")));
    
    jRefSavePanel.add (JCenter.XY (new JVoid_JColorCheckBox (
      "write reference (waveform) 1",
      tek2440::writeReference1,
      Color.blue)));
    
    jRefSavePanel.add (JCenter.XY (new JVoid_JColorCheckBox (
      "write reference (waveform) 2",
      tek2440::writeReference2,
      Color.blue)));
    
    jRefSavePanel.add (JCenter.XY (new JVoid_JColorCheckBox (
      "write reference (waveform) 3",
      tek2440::writeReference3,
      Color.blue)));
    
    jRefSavePanel.add (JCenter.XY (new JVoid_JColorCheckBox (
      "write reference (waveform) 4",
      tek2440::writeReference4,
      Color.blue)));
    
    jRefSavePanel.add (JCenter.XY (new JVoid_JColorCheckBox (
      "write reference (waveform) to stack",
      tek2440::writeReferenceStack,
      Color.blue)));
    
    //
    // southPanel [controlsPanel, labelsPanel]
    //
    
    southPanel.setLayout (new BorderLayout ());
    
    final JPanel controlsPanel = new JPanel ();
    southPanel.add (controlsPanel, BorderLayout.CENTER);
    
    final JPanel labelsPanel = new JPanel ();
    southPanel.add (labelsPanel, BorderLayout.SOUTH);
    
    controlsPanel.setLayout (new GridLayout (1, 5));
    
    controlsPanel.add (JCenter.XY (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.RefPositionMode.class,
      "reference position mode",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getRefPositionMode (),
      tek2440::setReferencePositionMode,
      true)));
    
    controlsPanel.add (new JDouble_JSlider (
      SwingConstants.VERTICAL,
      0.0,
      1023.0,
      0.0,
      1023.0,
      100.0,
      "reference 1 position",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getRefPosition1 (),
      tek2440::setReferencePosition_1,
      true).withPaintLabels ());
      
    controlsPanel.add (new JDouble_JSlider (
      SwingConstants.VERTICAL,
      0.0,
      1023.0,
      0.0,
      1023.0,
      100.0,
      "reference 2 position",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getRefPosition2 (),
      tek2440::setReferencePosition_2,
      true).withPaintLabels ());
      
    controlsPanel.add (new JDouble_JSlider (
      SwingConstants.VERTICAL,
      0.0,
      1023.0,
      0.0,
      1023.0,
      100.0,
      "reference 3 position",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getRefPosition3 (),
      tek2440::setReferencePosition_3,
      true).withPaintLabels ());
      
    controlsPanel.add (new JDouble_JSlider (
      SwingConstants.VERTICAL,
      0.0,
      1023.0,
      0.0,
      1023.0,
      100.0,
      "reference 4 position",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getRefPosition4 (),
      tek2440::setReferencePosition_4,
      true).withPaintLabels ());
      
    labelsPanel.setLayout (new GridLayout (1, 5));
    labelsPanel.setBorder (BorderFactory.createEmptyBorder (10, 0, 10, 0));
    
    labelsPanel.add (JCenter.XY (new JLabel ("Mode")));
    labelsPanel.add (JCenter.XY (new JLabel ("Ref1 Pos")));
    labelsPanel.add (JCenter.XY (new JLabel ("Ref2 Pos")));
    labelsPanel.add (JCenter.XY (new JLabel ("Ref3 Pos")));
    labelsPanel.add (JCenter.XY (new JLabel ("Ref4 Pos")));
        
  }

  public JTek2440_GPIB_Ref (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Ref (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Reference [Waveform] Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Ref ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Ref.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
