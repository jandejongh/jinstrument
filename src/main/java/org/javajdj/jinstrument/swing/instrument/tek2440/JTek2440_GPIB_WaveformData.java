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
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Waveform Data Settings</b>" +
//                          "<ul>" +
//                            "<li> the data format for transferring settings, status, and traces;</li>" +
//                            "<li>Features manual data transfers to and from the instrument;</li>" +
//                            "<li>Values marked in red are used internally in the software and therefore read-only.</li>" +
//                          "</ul>" +
                          "</html>");
    add (jDescription);
    
    final JPanel levelCrossingPanel = new JPanel ();
    levelCrossingPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Level Crossing"));
    add (levelCrossingPanel);
    
    levelCrossingPanel.setLayout (new GridLayout (2, 5));
    
    levelCrossingPanel.add (new JLabel ("Level"));
    this.jLevel = new JSlider (-100, 100); // XXX Limits?
    this.jLevel.setMajorTickSpacing (50);
    this.jLevel.setPaintTicks (true);
    this.jLevel.setPaintLabels (true);
    this.jLevel.setToolTipText ("-");
    this.jLevel.setBackground (Color.red);
    this.jLevel.addChangeListener (new JInstrumentSliderChangeListener_1Integer (
      "waveform analysis level",
      tek2440::setWaveformAnalysisLevel,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    levelCrossingPanel .add (this.jLevel);
    
    levelCrossingPanel.add (new JLabel ());

    levelCrossingPanel.add (new JLabel ("Hysteresis"));
    this.jHysteresis = new JSlider (0, 256); // XXX Limits?
    this.jHysteresis.setMajorTickSpacing (64);
    this.jHysteresis.setPaintTicks (true);
    this.jHysteresis.setPaintLabels (true);
    this.jHysteresis.setToolTipText ("-");
    this.jHysteresis.setBackground (Color.red);
    this.jHysteresis.addChangeListener (new JInstrumentSliderChangeListener_1Integer (
      "waveform analysis hysteresis",
      tek2440::setWaveformAnalysisHysteresis,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    levelCrossingPanel.add (this.jHysteresis);

    levelCrossingPanel.add (new JLabel ("Direction"));
    this.jDirection = new JComboBox<> (Tek2440_GPIB_Settings.Direction.values ());
    this.jDirection.setSelectedItem (null);
    this.jDirection.setEditable (false);
    this.jDirection.setBackground (Color.red);
    this.jDirection.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "waveform analysis direction",
      Tek2440_GPIB_Settings.Direction.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.Direction>)
        tek2440::setWaveformAnalysisDirection,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    levelCrossingPanel.add (JCenter.Y (this.jDirection));
    
    levelCrossingPanel.add (new JLabel ());
    levelCrossingPanel.add (new JLabel ());
    levelCrossingPanel.add (new JLabel ());    
    
    final JPanel intervalPanel = new JPanel ();
    intervalPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Interval"));
    add (intervalPanel);
    
    intervalPanel.setLayout (new GridLayout (2, 2));
    
    intervalPanel.add (new JLabel ("Start"));
    this.jStart = new JSlider (1, 1024);
    this.jStart.setMajorTickSpacing (1023);
    this.jStart.setMinorTickSpacing (512);
    this.jStart.setPaintTicks (true);
    this.jStart.setPaintLabels (true);
    this.jStart.setToolTipText ("-");
    this.jStart.setBackground (Color.red);
    this.jStart.addChangeListener (new JInstrumentSliderChangeListener_1Integer (
      "waveform analysis start position",
      tek2440::setWaveformAnalysisStartPosition,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    intervalPanel.add (this.jStart);

    intervalPanel.add (new JLabel ("Stop"));
    this.jStop = new JSlider (1, 1024);
    this.jStop.setMajorTickSpacing (1023);
    this.jStop.setMinorTickSpacing (512);
    this.jStop.setPaintTicks (true);
    this.jStop.setPaintLabels (true);
    this.jStop.setToolTipText ("-");
    this.jStop.setBackground (Color.red);
    this.jStop.addChangeListener (new JInstrumentSliderChangeListener_1Integer (
      "waveform analysis stop position",
      tek2440::setWaveformAnalysisStopPosition,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    intervalPanel.add (this.jStop);
        
    final JPanel commandPanel = new JPanel ();
    add (commandPanel);
    
    commandPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Commands"));
    
    commandPanel.setLayout (new GridLayout (1, 1));
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
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
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSlider jLevel;
  
  private final JComboBox<Tek2440_GPIB_Settings.Direction> jDirection;
  
  private final JSlider jHysteresis;
  
  private final JSlider jStart;
  
  private final JSlider jStop;
  
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
      if (instrument != JTek2440_GPIB_WaveformData.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_WaveformData.this.setInhibitInstrumentControl ();
        try
        {
          final int level = settings.getWaveformAnalysisLevel ();
          final Tek2440_GPIB_Settings.Direction direction = settings.getWaveformAnalysisDirection ();
          final int hysteresis = settings.getWaveformAnalysisHysteresis ();
          final int start = settings.getWaveformAnalysisStart ();
          final int stop = settings.getWaveformAnalysisStop ();
          JTek2440_GPIB_WaveformData.this.jLevel.setValue (level);
          JTek2440_GPIB_WaveformData.this.jLevel.setToolTipText (Integer.toString (level));
          JTek2440_GPIB_WaveformData.this.jLevel.setBackground (JTek2440_GPIB_WaveformData.this.getBackground ());
          JTek2440_GPIB_WaveformData.this.jDirection.setSelectedItem (direction);
          JTek2440_GPIB_WaveformData.this.jDirection.setBackground (JTek2440_GPIB_WaveformData.this.getBackground ());
          JTek2440_GPIB_WaveformData.this.jHysteresis.setValue (hysteresis);
          JTek2440_GPIB_WaveformData.this.jHysteresis.setToolTipText (Integer.toString (hysteresis));
          JTek2440_GPIB_WaveformData.this.jHysteresis.setBackground (JTek2440_GPIB_WaveformData.this.getBackground ());
          JTek2440_GPIB_WaveformData.this.jStart.setValue (start);
          JTek2440_GPIB_WaveformData.this.jStart.setToolTipText (Integer.toString (start));
          JTek2440_GPIB_WaveformData.this.jStart.setBackground (JTek2440_GPIB_WaveformData.this.getBackground ());
          JTek2440_GPIB_WaveformData.this.jStop.setValue (stop);
          JTek2440_GPIB_WaveformData.this.jStop.setToolTipText (Integer.toString (stop));
          JTek2440_GPIB_WaveformData.this.jStop.setBackground (JTek2440_GPIB_WaveformData.this.getBackground ());
        }
        finally
        {
          JTek2440_GPIB_WaveformData.this.resetInhibitInstrumentControl ();
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
