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
package org.javajdj.jinstrument.swing.instrument.dso.tek2440;

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

/** A Swing panel for the Waveform Data settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_WaveformData
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_WaveformData.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_WaveformData (
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
    
    final JPanel levelCrossingPanel = new JPanel ();
    levelCrossingPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Level Crossing"));
    add (levelCrossingPanel);

    final JPanel intervalPanel = new JPanel ();
    intervalPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Interval"));
    add (intervalPanel);
    
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
    jDescription.setText ("<html><b>Waveform Data Settings</b>" +
                          "<ul>" +
                            "<li>Features automatic measurement of average/minimum/maximum/number of crossings"
                                 + " on the waveform selected by the DATa SOUrce variable/settings;</li>" +
                            "<li>Supports variable threshold level and hysteresis"
                                 + " with respect to level-crossing detection;</li>" +
                            "<li>Supports restriction (start/stop) of the X range.</li>" +
                          "</ul>" +
                          "</html>");

    //
    // levelCrossingPanel
    //
    
    levelCrossingPanel.setLayout (new GridLayout (2, 5));
    
    levelCrossingPanel.add (new JLabel ("NCross Level"));
    levelCrossingPanel.add (new JInteger_JSlider (
      SwingConstants.HORIZONTAL,
      -100, // XXX Limits??
      100,  // XXX Limits??
      0,
      50,
      null,
      "waveform analysis level",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getWaveformAnalysisLevel (),
      tek2440::setWaveformAnalysisLevel,
      true).withPaintLabels ());
    
    levelCrossingPanel.add (new JLabel ());

    levelCrossingPanel.add (new JLabel ("NCross Hysteresis"));
    levelCrossingPanel.add (new JInteger_JSlider (
      SwingConstants.HORIZONTAL,
      0,
      256,
      0,
      64,
      null,
      "waveform analysis hysteresis",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getWaveformAnalysisHysteresis (),
      tek2440::setWaveformAnalysisHysteresis,
      true).withPaintLabels ());
    
    levelCrossingPanel.add (new JLabel ("NCross Direction"));
    levelCrossingPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.Direction.class,
      "waveform analysis direction",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getWaveformAnalysisDirection (),
      tek2440::setWaveformAnalysisDirection,
      true)));
    
    levelCrossingPanel.add (new JLabel ());
    levelCrossingPanel.add (new JLabel ());
    levelCrossingPanel.add (new JLabel ());    
    
    //
    // intervalPanel
    //
    
    intervalPanel.setLayout (new GridLayout (2, 2));
    
    intervalPanel.add (new JLabel ("Start"));
    intervalPanel.add (new JInteger_JSlider (
      SwingConstants.HORIZONTAL,
      1,
      1024,
      0,
      1023,
      512,
      "waveform analysis start position",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getWaveformAnalysisStart (),
      tek2440::setWaveformAnalysisStartPosition,
      true).withPaintLabels ());
    
    intervalPanel.add (new JLabel ("Stop"));
    intervalPanel.add (new JInteger_JSlider (
      SwingConstants.HORIZONTAL,
      1,
      1024,
      0,
      1023,
      512,
      "waveform analysis stop position",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getWaveformAnalysisStop (),
      tek2440::setWaveformAnalysisStopPosition,
      true).withPaintLabels ());
        
    //
    // commandPanel
    //
    
    commandPanel.setLayout (new GridLayout (1, 1));
    
  }

  public JTek2440_GPIB_WaveformData (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_WaveformData (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Waveform Data Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_WaveformData ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_WaveformData.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
