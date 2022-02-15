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

import org.javajdj.jswing.jcenter.JCenter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;

/** A Swing panel for the Ref(erence) settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Word
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Word.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Word (
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
    setLayout (new GridLayout (3, 1, 10, 0));
    
    final JTextPane jDescription = new JTextPane ();
    jDescription.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Description"));
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Word [Probe] Settings</b>" +
                          "<ul>" +
                            "<li>tbd;</li>" +
                            "<li>tbd.</li>" +
                          "</ul>" +
                          "</html>");
    add (jDescription);
    
    final JPanel settingsPanel = new JPanel ();
    settingsPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Settings"));
    add (settingsPanel);
    
    final JPanel wordPanel = new JPanel ();
    wordPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Word"));
    add (wordPanel);
    
    settingsPanel.setLayout (new GridLayout (2, 2, 0, 10));

    settingsPanel.add (new JLabel ("Clock"));

    this.jClock = new JComboBox<> (Tek2440_GPIB_Settings.WordClock.values ());
    this.jClock.setSelectedItem (null);
    this.jClock.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "word clock",
      Tek2440_GPIB_Settings.WordClock.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.WordClock>) tek2440::setWordClock,
      JTek2440_GPIB_Word.this::isInhibitInstrumentControl));
    settingsPanel.add (new JCenteredComboBox (this.jClock));
    
    settingsPanel.add (new JLabel ("Radix"));
    
    this.jRadix = new JComboBox<> (Tek2440_GPIB_Settings.WordRadix.values ());
    this.jRadix.setSelectedItem (null);
    this.jRadix.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "word radix",
      Tek2440_GPIB_Settings.WordRadix.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.WordRadix>) tek2440::setWordRadix,
      JTek2440_GPIB_Word.this::isInhibitInstrumentControl));
    settingsPanel.add (new JCenteredComboBox (this.jRadix));
    
    wordPanel.setLayout (new GridLayout (1, 1, 0, 0));
    
    this.jWord = new JTextField ("XXXXXXXXXXXXXXXXX"); // Yes, 17!
    final Font newTextFieldFont=new Font (this.jWord.getFont().getName(), this.jWord.getFont().getStyle(), 32);    
    this.jWord.setFont (newTextFieldFont);
    final JInstrumentTextFieldListener_1String jWordListener = new JInstrumentTextFieldListener_1String (
      "word",
      this.jWord::getText,
      tek2440::setWord,
      this::isInhibitInstrumentControl);
    jWordListener.registerListener (this.jWord);
    wordPanel.add (JCenter.XY (this.jWord));
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }

  public JTek2440_GPIB_Word (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Word (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Word [Probe] Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Word ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Word.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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

  private final JComboBox<Tek2440_GPIB_Settings.WordClock> jClock;
  
  private final JComboBox<Tek2440_GPIB_Settings.WordRadix> jRadix;
  
  private final JTextField jWord;
  
  private final static class JCenteredComboBox<E>
    extends JPanel
  {

    public JCenteredComboBox (final JComboBox<E> jComboBox)
    {
      super ();
      setLayout (new BoxLayout (this, BoxLayout.X_AXIS));
      final JPanel column = new JPanel ();
      // add (Box.createHorizontalGlue ());
      add (column);
      add (Box.createHorizontalGlue ());      
      column.setLayout (new BoxLayout (column, BoxLayout.Y_AXIS));
      column.add (Box.createVerticalGlue ());
      jComboBox.setPreferredSize (new Dimension (40, 12));
      column.add (jComboBox);
      column.add (Box.createVerticalGlue ());
    }

  }
  
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
      if (instrument != JTek2440_GPIB_Word.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_Word.this.setInhibitInstrumentControl ();
        try
        {
          JTek2440_GPIB_Word.this.jClock.setSelectedItem (settings.getWordClock ());
          JTek2440_GPIB_Word.this.jRadix.setSelectedItem (settings.getWordRadix ());
          JTek2440_GPIB_Word.this.jWord.setText (settings.getWord ().substring (2)); // Remove trailing #Y.
        }
        finally
        {
          JTek2440_GPIB_Word.this.resetInhibitInstrumentControl ();
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
