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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
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
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for the ATrigger settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_ATrigger
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_ATrigger.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_ATrigger (
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
    setLayout (new GridLayout (1, 2, 10, 0));

    final JPanel leftPanel = new JPanel ();
    leftPanel.setLayout (new GridLayout (5, 1, 0, 10));
    add (leftPanel);
    final JPanel rightPanel = new JPanel ();
    rightPanel.setLayout (new GridLayout (5, 1, 0, 10));
    add (rightPanel);

    leftPanel.add (new JLabel ("Mode"));
    this.jMode = new JComboBox<>  (Tek2440_GPIB_Settings.ATriggerMode.values ());
    this.jMode.setSelectedItem (null);
    this.jMode.setEditable (false);
    this.jMode.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "A trigger mode",
      Tek2440_GPIB_Settings.ATriggerMode.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.ATriggerMode>) tek2440::setATriggerMode,
      this::isInhibitInstrumentControl));
    leftPanel.add (this.jMode);

    leftPanel.add (new JLabel ("Source"));
    this.jSource = new JComboBox<> (Tek2440_GPIB_Settings.ATriggerSource.values ());
    this.jSource.setSelectedItem (null);
    this.jSource.setEditable (false);
    this.jSource.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "A trigger source",
      Tek2440_GPIB_Settings.ATriggerSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.ATriggerSource>) tek2440::setATriggerSource,
      this::isInhibitInstrumentControl));
    leftPanel.add (this.jSource);

    leftPanel.add (new JLabel ("Coupling"));
    this.jCoupling = new JComboBox<> (Tek2440_GPIB_Settings.ATriggerCoupling.values ());
    this.jCoupling.setSelectedItem (null);
    this.jCoupling.setEditable (false);
    this.jCoupling.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "A trigger coupling",
      Tek2440_GPIB_Settings.ATriggerCoupling.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.ATriggerCoupling>) tek2440::setATriggerCoupling,
      this::isInhibitInstrumentControl));
    leftPanel.add (this.jCoupling);

    leftPanel.add (new JLabel ("Slope"));
    this.jSlope = new JComboBox<> (Tek2440_GPIB_Settings.Slope.values ());
    this.jSlope.setSelectedItem (null);
    this.jSlope.setEditable (false);
    this.jSlope.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "A trigger slope",
      Tek2440_GPIB_Settings.Slope.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.Slope>) tek2440::setATriggerSlope,
      this::isInhibitInstrumentControl));
    leftPanel.add (this.jSlope);

    leftPanel.add (new JLabel ("Level [V]"));
    this.jLevel = new JSlider (-200, 200); // XXX Arbitrary!
    this.jLevel.setToolTipText ("-");
    this.jLevel.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "A trigger level",
      tek2440::setATriggerLevel,
      this::isInhibitInstrumentControl));
    leftPanel.add (this.jLevel);

    rightPanel.add (new JLabel ("Position"));
    this.jPosition = new JComboBox<> (Tek2440_GPIB_Settings.ATriggerPosition.values ());
    this.jPosition.setSelectedItem (null);
    this.jPosition.setEditable (false);
    this.jPosition.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "A trigger position",
      Tek2440_GPIB_Settings.ATriggerPosition.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.ATriggerPosition>) tek2440::setATriggerPosition,
      this::isInhibitInstrumentControl));
    rightPanel.add (this.jPosition);

    rightPanel.add (new JLabel ("Holdoff"));
    this.jHoldoff = new JSlider (0, 100);
    this.jHoldoff.setToolTipText ("-");
    this.jHoldoff.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "A trigger holdoff",
      tek2440::setATriggerHoldoff,
      this::isInhibitInstrumentControl));
    rightPanel.add (this.jHoldoff);

    rightPanel.add (new JLabel ("Log Source"));
    this.jLogSource = new JComboBox<> (Tek2440_GPIB_Settings.ATriggerLogSource.values ());
    this.jLogSource.setSelectedItem (null);
    this.jLogSource.setEditable (false);
    this.jLogSource.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "A trigger log source",
      Tek2440_GPIB_Settings.ATriggerLogSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.ATriggerLogSource>) tek2440::setATriggerLogSource,
      this::isInhibitInstrumentControl));
    rightPanel.add (this.jLogSource);

    rightPanel.add (new JLabel ("A/B Select"));
    this.jABSelect = new JComboBox<> (Tek2440_GPIB_Settings.ABSelect.values ());
    this.jABSelect.setSelectedItem (null);
    this.jABSelect.setEditable (false);
    this.jABSelect.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "A/B (trigger) select",
      Tek2440_GPIB_Settings.ABSelect.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.ABSelect>) tek2440::setTriggerABSelect,
      this::isInhibitInstrumentControl));
    rightPanel.add (this.jABSelect);

    rightPanel.add (new JLabel ("Word"));
    final JColorCheckBox jWordButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jWordButton.setDisplayedValue (true);
    // jWordButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jWordDialog = new JOptionPane ().createDialog ("Tek-2440 Word Settings");
    this.jWordDialog.setSize (640, 480);
    this.jWordDialog.setLocationRelativeTo (this);
    this.jWordDialog.setContentPane (new JTek2440_GPIB_Word (tek2440, level + 1));
    jWordButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB_ATrigger.this.jWordDialog.setVisible (true);
    });
    rightPanel.add (jWordButton);

    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }

  public JTek2440_GPIB_ATrigger (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_ATrigger (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] ATrigger Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_ATrigger ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_ATrigger.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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

  private final JComboBox<Tek2440_GPIB_Settings.ATriggerMode> jMode;
  
  private final JComboBox<Tek2440_GPIB_Settings.ATriggerSource> jSource;
  
  private final JComboBox<Tek2440_GPIB_Settings.ATriggerCoupling> jCoupling;
  
  private final JComboBox<Tek2440_GPIB_Settings.Slope> jSlope;
  
  private final JSlider jLevel;
  
  private final JComboBox<Tek2440_GPIB_Settings.ATriggerPosition> jPosition;
  
  private final JSlider jHoldoff;
  
  private final JComboBox<Tek2440_GPIB_Settings.ATriggerLogSource> jLogSource;
  
  private final JComboBox<Tek2440_GPIB_Settings.ABSelect> jABSelect;
  
  private final JDialog jWordDialog;
  
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
      if (instrument != JTek2440_GPIB_ATrigger.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_ATrigger.this.setInhibitInstrumentControl ();
        try
        {
          JTek2440_GPIB_ATrigger.this.jMode.setSelectedItem (settings.getATriggerMode ());
          JTek2440_GPIB_ATrigger.this.jSource.setSelectedItem (settings.getATriggerSource ());
          JTek2440_GPIB_ATrigger.this.jCoupling.setSelectedItem (settings.getATriggerCoupling ());
          JTek2440_GPIB_ATrigger.this.jSlope.setSelectedItem (settings.getATriggerSlope ());
          JTek2440_GPIB_ATrigger.this.jLevel.setValue ((int) Math.round (settings.getATriggerLevel ()));
          JTek2440_GPIB_ATrigger.this.jLevel.setToolTipText (Double.toString (settings.getATriggerLevel ()));
          JTek2440_GPIB_ATrigger.this.jPosition.setSelectedItem (settings.getATriggerPosition ());
          JTek2440_GPIB_ATrigger.this.jHoldoff.setValue ((int) Math.round (settings.getATriggerHoldoff ()));
          JTek2440_GPIB_ATrigger.this.jHoldoff.setToolTipText (Double.toString (settings.getATriggerHoldoff ()));
          JTek2440_GPIB_ATrigger.this.jLogSource.setSelectedItem (settings.getATriggerLogSource ());
          JTek2440_GPIB_ATrigger.this.jABSelect.setSelectedItem (settings.getATriggerABSelect ());          
        }
        finally
        {
          JTek2440_GPIB_ATrigger.this.resetInhibitInstrumentControl ();
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
