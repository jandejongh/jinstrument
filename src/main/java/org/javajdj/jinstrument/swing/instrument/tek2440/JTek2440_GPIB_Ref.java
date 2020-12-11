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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

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
    
    final JPanel southPanel = new JTek2440_GPIB_RefPos (
      digitalStorageOscilloscope,
      "Reference [Waveform] Positions",
      level,
      DEFAULT_MANAGEMENT_COLOR);
    add (southPanel);
    //
    northPanel.setLayout (new GridLayout (4, 5, 10, 10));
    
    northPanel.add (new JLabel ());
    northPanel.add (new JLabel (" 1"));
    northPanel.add (new JLabel (" 2"));
    northPanel.add (new JLabel (" 3"));
    northPanel.add (new JLabel (" 4"));
    
    northPanel.add (new JLabel ("Contents"));
    
    this.jRefContents1 = new JColorCheckBox.JBoolean (Color.green);
    this.jRefContents1.setDisplayedValue (false);
    this.jRefContents1.setEnabled (false);
    northPanel.add (this.jRefContents1);
    
    this.jRefContents2 = new JColorCheckBox.JBoolean (Color.green);
    this.jRefContents2.setDisplayedValue (false);
    this.jRefContents2.setEnabled (false);
    northPanel.add (this.jRefContents2);
    
    this.jRefContents3 = new JColorCheckBox.JBoolean (Color.green);
    this.jRefContents3.setDisplayedValue (false);
    this.jRefContents3.setEnabled (false);
    northPanel.add (this.jRefContents3);
    
    this.jRefContents4 = new JColorCheckBox.JBoolean (Color.green);
    this.jRefContents4.setDisplayedValue (false);
    this.jRefContents4.setEnabled (false);
    northPanel.add (this.jRefContents4);
    
    northPanel.add (new JLabel ("Display"));
    
    this.jRefDisplay1 = new JColorCheckBox.JBoolean (Color.green);
    this.jRefDisplay1.setDisplayedValue (false);
    this.jRefDisplay1.addActionListener (new JInstrumentActionListener_1Boolean (
      "display reference (waveform) 1",
      this.jRefDisplay1::getDisplayedValue,
      tek2440::setDisplayReference1,
      JTek2440_GPIB_Ref.this::isInhibitInstrumentControl));
    northPanel.add (this.jRefDisplay1);
    
    this.jRefDisplay2 = new JColorCheckBox.JBoolean (Color.green);
    this.jRefDisplay2.setDisplayedValue (false);
    this.jRefDisplay2.addActionListener (new JInstrumentActionListener_1Boolean (
      "display reference (waveform) 2",
      this.jRefDisplay2::getDisplayedValue,
      tek2440::setDisplayReference2,
      JTek2440_GPIB_Ref.this::isInhibitInstrumentControl));
    northPanel.add (this.jRefDisplay2);
    
    this.jRefDisplay3 = new JColorCheckBox.JBoolean (Color.green);
    this.jRefDisplay3.setDisplayedValue (false);
    this.jRefDisplay3.addActionListener (new JInstrumentActionListener_1Boolean (
      "display reference (waveform) 3",
      this.jRefDisplay3::getDisplayedValue,
      tek2440::setDisplayReference3,
      JTek2440_GPIB_Ref.this::isInhibitInstrumentControl));
    northPanel.add (this.jRefDisplay3);
    
    this.jRefDisplay4 = new JColorCheckBox.JBoolean (Color.green);
    this.jRefDisplay4.setDisplayedValue (false);
    this.jRefDisplay4.addActionListener (new JInstrumentActionListener_1Boolean (
      "display reference (waveform) 4",
      this.jRefDisplay4::getDisplayedValue,
      tek2440::setDisplayReference4,
      JTek2440_GPIB_Ref.this::isInhibitInstrumentControl));
    northPanel.add (this.jRefDisplay4);
        
    northPanel.add (new JLabel ("Clear"));
    
    this.jRefClear1 = new JColorCheckBox.JBoolean (getBackground ().darker ());
    this.jRefClear1.setDisplayedValue (true);
    this.jRefClear1.addActionListener (new JInstrumentActionListener_0 (
      "clear reference (waveform) 1",
      tek2440::clearReference1));
    northPanel.add (this.jRefClear1);
    
    this.jRefClear2 = new JColorCheckBox.JBoolean (getBackground ().darker ());
    this.jRefClear2.setDisplayedValue (true);
    this.jRefClear2.addActionListener (new JInstrumentActionListener_0 (
      "clear reference (waveform) 2",
      tek2440::clearReference2));
    northPanel.add (this.jRefClear2);
    
    this.jRefClear3 = new JColorCheckBox.JBoolean (getBackground ().darker ());
    this.jRefClear3.setDisplayedValue (true);
    this.jRefClear3.addActionListener (new JInstrumentActionListener_0 (
      "clear reference (waveform) 3",
      tek2440::clearReference3));
    northPanel.add (this.jRefClear3);
    
    this.jRefClear4 = new JColorCheckBox.JBoolean (getBackground ().darker ());
    this.jRefClear4.setDisplayedValue (true);
    this.jRefClear4.addActionListener (new JInstrumentActionListener_0 (
      "clear reference (waveform) 4",
      tek2440::clearReference4));
    northPanel.add (this.jRefClear4);
    
    centerPanel.setLayout (new GridLayout (1, 2, 0, 0));
    
    final JPanel jFromPanel = new JPanel ();
    jFromPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Source"));
    jFromPanel.setLayout (new BoxLayout (jFromPanel, BoxLayout.Y_AXIS));
    
    this.jRefSaveFrom = new JComboBox<> (Tek2440_GPIB_Settings.RefFrom.values ());
    this.jRefSaveFrom.setEditable (false);
    this.jRefSaveFrom.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "reference (save) source/from",
      Tek2440_GPIB_Settings.RefFrom.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.RefFrom>) tek2440::setReferenceSource,
      () -> JTek2440_GPIB_Ref.this.inhibitInstrumentControl));
    final JPanel fromPanelX = new JPanel ();
    fromPanelX.setLayout (new BoxLayout (fromPanelX, BoxLayout.X_AXIS));
    fromPanelX.add (Box.createGlue ());
    final JPanel jRefSaveFromPanel = new JPanel ();
    jRefSaveFromPanel.add (this.jRefSaveFrom);
    fromPanelX.add (jRefSaveFromPanel);
    fromPanelX.add (Box.createGlue ());
    jFromPanel.add (Box.createGlue ());
    jFromPanel.add (fromPanelX);
    jFromPanel.add (Box.createGlue ());
    
    centerPanel.add (jFromPanel);
    
    final JPanel jRefSavePanel = new JPanel ();
    jRefSavePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Save To"));
    
    jRefSavePanel.setLayout (new GridLayout (2, 5));
    jRefSavePanel.add (new JLabel ("Ref1"));
    jRefSavePanel.add (new JLabel ("Ref2"));
    jRefSavePanel.add (new JLabel ("Ref3"));
    jRefSavePanel.add (new JLabel ("Ref4"));
    jRefSavePanel.add (new JLabel ("Stack"));
    
    this.jRefSave1 = new JColorCheckBox.JBoolean (getBackground ().darker ());
    this.jRefSave1.setDisplayedValue (true);
    // this.jRefSave1.setEnabled (false);
    this.jRefSave1.addActionListener (new JInstrumentActionListener_0 (
      "Write reference (waveform) 1",
      tek2440::writeReference1));
    jRefSavePanel.add (this.jRefSave1);
    
    this.jRefSave2 = new JColorCheckBox.JBoolean (getBackground ().darker ());
    this.jRefSave2.setDisplayedValue (true);
    // this.jRefSave2.setEnabled (false);
    this.jRefSave2.addActionListener (new JInstrumentActionListener_0 (
      "Write reference (waveform) 2",
      tek2440::writeReference2));
    jRefSavePanel.add (this.jRefSave2);
    
    this.jRefSave3 = new JColorCheckBox.JBoolean (getBackground ().darker ());
    this.jRefSave3.setDisplayedValue (true);
    // this.jRefSave3.setEnabled (false);
    this.jRefSave3.addActionListener (new JInstrumentActionListener_0 (
      "Write reference (waveform) 3",
      tek2440::writeReference3));
    jRefSavePanel.add (this.jRefSave3);
    
    this.jRefSave4 = new JColorCheckBox.JBoolean (getBackground ().darker ());
    this.jRefSave4.setDisplayedValue (true);
    // this.jRefSave4.setEnabled (false);
    this.jRefSave4.addActionListener (new JInstrumentActionListener_0 (
      "Write reference (waveform) 4",
      tek2440::writeReference4));
    jRefSavePanel.add (this.jRefSave4);
    
    this.jRefSaveStack = new JColorCheckBox.JBoolean (getBackground ().darker ());
    this.jRefSaveStack.setDisplayedValue (true);
    // this.jRefSaveStack.setEnabled (false);
    this.jRefSaveStack.addActionListener (new JInstrumentActionListener_0 (
      "Write reference (waveform) to stack",
      tek2440::writeReferenceStack));
    jRefSavePanel.add (this.jRefSaveStack);
    
    centerPanel.add (jRefSavePanel);
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
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
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JColorCheckBox.JBoolean jRefContents1;
  
  private final JColorCheckBox.JBoolean jRefContents2;
  
  private final JColorCheckBox.JBoolean jRefContents3;
  
  private final JColorCheckBox.JBoolean jRefContents4;
  
  private final JColorCheckBox.JBoolean jRefDisplay1;
  
  private final JColorCheckBox.JBoolean jRefDisplay2;
  
  private final JColorCheckBox.JBoolean jRefDisplay3;
  
  private final JColorCheckBox.JBoolean jRefDisplay4;
  
  private final JColorCheckBox.JBoolean jRefClear1;
  
  private final JColorCheckBox.JBoolean jRefClear2;
  
  private final JColorCheckBox.JBoolean jRefClear3;
  
  private final JColorCheckBox.JBoolean jRefClear4;
  
  private final JComboBox<Tek2440_GPIB_Settings.RefFrom> jRefSaveFrom;
  
  private final JColorCheckBox.JBoolean jRefSave1;
  
  private final JColorCheckBox.JBoolean jRefSave2;
  
  private final JColorCheckBox.JBoolean jRefSave3;
  
  private final JColorCheckBox.JBoolean jRefSave4;
  
  private final JColorCheckBox.JBoolean jRefSaveStack;
  
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
      if (instrument != JTek2440_GPIB_Ref.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_Ref.this.inhibitInstrumentControl = true;
        try
        {
          JTek2440_GPIB_Ref.this.jRefContents1.setDisplayedValue (! settings.isEmptyRef1 ());
          JTek2440_GPIB_Ref.this.jRefContents2.setDisplayedValue (! settings.isEmptyRef2 ());
          JTek2440_GPIB_Ref.this.jRefContents3.setDisplayedValue (! settings.isEmptyRef3 ());
          JTek2440_GPIB_Ref.this.jRefContents4.setDisplayedValue (! settings.isEmptyRef4 ());
          JTek2440_GPIB_Ref.this.jRefDisplay1.setDisplayedValue (settings.isDisplayRef1 ());
          JTek2440_GPIB_Ref.this.jRefDisplay2.setDisplayedValue (settings.isDisplayRef2 ());
          JTek2440_GPIB_Ref.this.jRefDisplay3.setDisplayedValue (settings.isDisplayRef3 ());
          JTek2440_GPIB_Ref.this.jRefDisplay4.setDisplayedValue (settings.isDisplayRef4 ());
          JTek2440_GPIB_Ref.this.jRefSaveFrom.setSelectedItem (settings.getRefSource ());
        }
        finally
        {
          JTek2440_GPIB_Ref.this.inhibitInstrumentControl = false;
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
