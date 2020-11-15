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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for several (all) display settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Display
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Display.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Display (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    
    super (digitalStorageOscilloscope, level);
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;
    
    removeAll ();
    setLayout (new GridLayout (2, 1, 10, 10));
    setBorder (BorderFactory.createEmptyBorder (10, 10, 10, 10));
    
    final JPanel northPanel = new JPanel ();
    northPanel.setLayout (new GridLayout (1, 2, 10, 10));
    final JPanel northWestPanel = new JPanel ();
    northPanel.add (northWestPanel);
    final JPanel northEastPanel = new JPanel ();
    northPanel.add (northEastPanel);
    add (northPanel);
    
    northWestPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Display Selections"));
    
    northWestPanel.setLayout (new GridLayout (6, 1));
    
    this.jReadout = new SelectionPanel ("Readout");
    this.jReadout.jBoolean.addActionListener (new JInstrumentActionListener_1Boolean (
      "display readout",
      this.jReadout.jBoolean::getDisplayedValue,
      tek2440::setDisplayReadout,
      JTek2440_GPIB_Display.this::isInhibitInstrumentControl));
    northWestPanel.add (this.jReadout);
    
    this.jVectors = new SelectionPanel ("Vectors");
    this.jVectors.jBoolean.addActionListener (new JInstrumentActionListener_1Boolean (
      "display vectors",
      this.jVectors.jBoolean::getDisplayedValue,
      tek2440::setDisplayVectors,
      JTek2440_GPIB_Display.this::isInhibitInstrumentControl));
    northWestPanel.add (this.jVectors);
    
    this.jRef1 = new SelectionPanel ("Ref 1");
    this.jRef1.jBoolean.addActionListener (new JInstrumentActionListener_1Boolean (
      "display reference (waveform) 1",
      this.jRef1.jBoolean::getDisplayedValue,
      tek2440::displayReference1,
      JTek2440_GPIB_Display.this::isInhibitInstrumentControl));
    northWestPanel.add (this.jRef1);

    this.jRef2 = new SelectionPanel ("Ref 2");
    this.jRef2.jBoolean.addActionListener (new JInstrumentActionListener_1Boolean (
      "display reference (waveform) 2",
      this.jRef2.jBoolean::getDisplayedValue,
      tek2440::displayReference2,
      JTek2440_GPIB_Display.this::isInhibitInstrumentControl));
    northWestPanel.add (this.jRef2);

    this.jRef3 = new SelectionPanel ("Ref 3");
    this.jRef3.jBoolean.addActionListener (new JInstrumentActionListener_1Boolean (
      "display reference (waveform) 3",
      this.jRef3.jBoolean::getDisplayedValue,
      tek2440::displayReference3,
      JTek2440_GPIB_Display.this::isInhibitInstrumentControl));
    northWestPanel.add (this.jRef3);

    this.jRef4 = new SelectionPanel ("Ref 4");
    this.jRef4.jBoolean.addActionListener (new JInstrumentActionListener_1Boolean (
      "display reference (waveform) 4",
      this.jRef4.jBoolean::getDisplayedValue,
      tek2440::displayReference4,
      JTek2440_GPIB_Display.this::isInhibitInstrumentControl));
    northWestPanel.add (this.jRef4);

    northEastPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Instrument Front Panel Lock"));
    
    northEastPanel.setLayout (new GridLayout (1, 1));
    
    this.jLockMode = new JComboBox<> (Tek2440_GPIB_Settings.LockMode.values ());
    this.jLockMode.setSelectedItem (null);
    this.jLockMode.setEditable (false);
    this.jLockMode.setBackground (Color.red);
    this.jLockMode.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "[instrument] front-panel lock mode",
      Tek2440_GPIB_Settings.LockMode.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.LockMode>) tek2440::setLockMode,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    northEastPanel.add (JCenter.XY (this.jLockMode));
    
    this.jDisplayIntensityPanel = new JDisplayIntensityPanel ();
    this.jDisplayIntensityPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Display Intensities"));
    add (this.jDisplayIntensityPanel);
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }
  
  public JTek2440_GPIB_Display (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Display Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Display ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Display.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // SELECTION PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private class SelectionPanel
    extends JPanel
  {
    
    private final JColorCheckBox.JBoolean jBoolean;

    public SelectionPanel (final String key)
    {
      setLayout (new GridLayout (1, 2));
      add (new JLabel (key));
      this.jBoolean = new JColorCheckBox.JBoolean (Color.green);
      add (this.jBoolean);
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // DISPLAY SELECTION SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final SelectionPanel jReadout;
  
  private final SelectionPanel jVectors;
  
  private final SelectionPanel jRef1;
  
  private final SelectionPanel jRef2;
  
  private final SelectionPanel jRef3;
  
  private final SelectionPanel jRef4;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // INSTRUMENT FRONT-PANEL LOCK SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JComboBox<Tek2440_GPIB_Settings.LockMode> jLockMode;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // DISPLAY INTENSITY PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final class JDisplayIntensityPanel
    extends JPanel
  {

    private final JSlider jDisplayIntensity;
    
    private final JSlider jGraticuleIntensity;
    
    private final JSlider jIntensifiedZoneIntensity;
    
    private final JSlider jReadoutIntensity;
    
    public JDisplayIntensityPanel ()
    {
      super ();
      final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) JTek2440_GPIB_Display.this.getDigitalStorageOscilloscope ();
      setOpaque (true);
      setLayout (new BorderLayout ());
      final JPanel sliderPanel = new JPanel ();
      sliderPanel.setBorder (BorderFactory.createEmptyBorder (10, 0, 0, 0));
      sliderPanel.setLayout (new GridLayout (1, 4));
      // XXX Actually, 0-100 with 0.25 resolution...
      this.jDisplayIntensity = new JSlider (JSlider.VERTICAL, 0, 100, 0);
      this.jDisplayIntensity.setMajorTickSpacing (50);
      this.jDisplayIntensity.setMinorTickSpacing (10);
      this.jDisplayIntensity.setPaintTicks (true);
      this.jDisplayIntensity.setPaintLabels (true);
      this.jDisplayIntensity.setToolTipText ("-");
      this.jDisplayIntensity.addChangeListener (new JInstrumentSliderChangeListener_1Double (
        "display intensity",
        tek::setDisplayIntensity_Display,
        () -> JTek2440_GPIB_Display.this.inhibitInstrumentControl));
      sliderPanel.add (this.jDisplayIntensity);
      this.jGraticuleIntensity = new JSlider (JSlider.VERTICAL, 0, 100, 0);
      this.jGraticuleIntensity.setMajorTickSpacing (50);
      this.jGraticuleIntensity.setMinorTickSpacing (10);
      this.jGraticuleIntensity.setPaintTicks (true);
      this.jGraticuleIntensity.setPaintLabels (true);
      this.jGraticuleIntensity.setToolTipText ("-");
      this.jGraticuleIntensity.addChangeListener (new JInstrumentSliderChangeListener_1Double (
        "graticule intensity",
        tek::setDisplayIntensity_Graticule,
        () -> JTek2440_GPIB_Display.this.inhibitInstrumentControl));
      sliderPanel.add (this.jGraticuleIntensity);
      this.jIntensifiedZoneIntensity = new JSlider (JSlider.VERTICAL, 0, 100, 0);
      this.jIntensifiedZoneIntensity.setMajorTickSpacing (50);
      this.jIntensifiedZoneIntensity.setMinorTickSpacing (10);
      this.jIntensifiedZoneIntensity.setPaintTicks (true);
      this.jIntensifiedZoneIntensity.setPaintLabels (true);
      this.jIntensifiedZoneIntensity.setToolTipText ("-");
      this.jIntensifiedZoneIntensity.addChangeListener (new JInstrumentSliderChangeListener_1Double (
        "intensified-zone intensity",
        tek::setDisplayIntensity_IntensifiedZone,
        () -> JTek2440_GPIB_Display.this.inhibitInstrumentControl));
      sliderPanel.add (this.jIntensifiedZoneIntensity);
      this.jReadoutIntensity = new JSlider (JSlider.VERTICAL, 0, 100, 0);
      this.jReadoutIntensity.setMajorTickSpacing (50);
      this.jReadoutIntensity.setMinorTickSpacing (10);
      this.jReadoutIntensity.setPaintTicks (true);
      this.jReadoutIntensity.setPaintLabels (true);
      this.jReadoutIntensity.setToolTipText ("-");
      this.jReadoutIntensity.addChangeListener (new JInstrumentSliderChangeListener_1Double (
        "readout intensity",
        tek::setDisplayIntensity_Readout,
        () -> JTek2440_GPIB_Display.this.inhibitInstrumentControl));
      sliderPanel.add (this.jReadoutIntensity);
      add (sliderPanel, BorderLayout.CENTER);
      final JPanel labelPanel = new JPanel ();
      labelPanel.setLayout (new GridLayout (1, 4));
      labelPanel.setBorder (BorderFactory.createEmptyBorder (10, 0, 10, 0));
      final JLabel displayLabel = new JLabel ("Display      ");
      displayLabel.setHorizontalAlignment (SwingConstants.CENTER);
      labelPanel.add (displayLabel);
      final JLabel graticuleLabel = new JLabel ("Graticule    ");
      graticuleLabel.setHorizontalAlignment (SwingConstants.CENTER);
      labelPanel.add (graticuleLabel);
      final JLabel intensifiedZoneLabel = new JLabel ("Intens Zone  ");
      intensifiedZoneLabel.setHorizontalAlignment (SwingConstants.CENTER);
      labelPanel.add (intensifiedZoneLabel);
      final JLabel readoutLabel = new JLabel ("Readout       ");
      readoutLabel.setHorizontalAlignment (SwingConstants.CENTER);
      labelPanel.add (readoutLabel);
      add (labelPanel, BorderLayout.SOUTH);
    }
        
  }
  
  private final JDisplayIntensityPanel jDisplayIntensityPanel;
  
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
      if (instrument != JTek2440_GPIB_Display.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_Display.this.setInhibitInstrumentControl ();
        try
        {
          final boolean readout = settings.isDisplayReadout ();
          JTek2440_GPIB_Display.this.jReadout.jBoolean.setDisplayedValue (readout);
          final boolean vectors = settings.isDisplayVectors ();
          JTek2440_GPIB_Display.this.jVectors.jBoolean.setDisplayedValue (vectors);
          final boolean ref1 = settings.isDisplayRef1 ();
          JTek2440_GPIB_Display.this.jRef1.jBoolean.setDisplayedValue (ref1);
          final boolean ref2 = settings.isDisplayRef2 ();
          JTek2440_GPIB_Display.this.jRef2.jBoolean.setDisplayedValue (ref2);
          final boolean ref3 = settings.isDisplayRef3 ();
          JTek2440_GPIB_Display.this.jRef3.jBoolean.setDisplayedValue (ref3);
          final boolean ref4 = settings.isDisplayRef4 ();
          JTek2440_GPIB_Display.this.jRef4.jBoolean.setDisplayedValue (ref4);
          final Tek2440_GPIB_Settings.LockMode lockMode = settings.getLockMode ();
          JTek2440_GPIB_Display.this.jLockMode.setSelectedItem (lockMode);
          JTek2440_GPIB_Display.this.jLockMode.setBackground (JTek2440_GPIB_Display.this.getBackground ());
          final int displayIntensity = (int) Math.round (settings.getDisplayIntensity ());
          JTek2440_GPIB_Display.this.jDisplayIntensityPanel.jDisplayIntensity.setValue (displayIntensity);
          JTek2440_GPIB_Display.this.jDisplayIntensityPanel.jDisplayIntensity.setToolTipText (
            Integer.toString (displayIntensity));
          final int graticuleIntensity = (int) Math.round (settings.getGraticuleIntensity ());
          JTek2440_GPIB_Display.this.jDisplayIntensityPanel.jGraticuleIntensity.setValue (graticuleIntensity);
          JTek2440_GPIB_Display.this.jDisplayIntensityPanel.jGraticuleIntensity.setToolTipText (
            Integer.toString (graticuleIntensity));
          final int intensifiedZoneIntensity = (int) Math.round (settings.getIntensifiedZoneIntensity ());
          JTek2440_GPIB_Display.this.jDisplayIntensityPanel.jIntensifiedZoneIntensity.setValue (intensifiedZoneIntensity);
          JTek2440_GPIB_Display.this.jDisplayIntensityPanel.jIntensifiedZoneIntensity.setToolTipText (
            Integer.toString (intensifiedZoneIntensity));
          final int readoutIntensity = (int) Math.round (settings.getReadoutIntensity ());
          JTek2440_GPIB_Display.this.jDisplayIntensityPanel.jReadoutIntensity.setValue (readoutIntensity);
          JTek2440_GPIB_Display.this.jDisplayIntensityPanel.jReadoutIntensity.setToolTipText (
            Integer.toString (readoutIntensity));
        }
        finally
        {
          JTek2440_GPIB_Display.this.resetInhibitInstrumentControl ();
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
