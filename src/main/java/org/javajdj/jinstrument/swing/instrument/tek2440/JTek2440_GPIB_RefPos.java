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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
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

/** A Swing panel for the Ref(erence) positions settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_RefPos
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_RefPos.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_RefPos (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final String title,
    final int level,
    final Color panelColor)
  {
    //
    super (digitalStorageOscilloscope, title, level, panelColor);
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;
    //
    removeAll ();
    setLayout (new BorderLayout ());
    //
    final JPanel controlsPanel = new JPanel ();
    controlsPanel.setLayout (new GridLayout (1, 5));
    final JPanel jModePanel = new JPanel ();
    jModePanel.setLayout (new BoxLayout (jModePanel, BoxLayout.X_AXIS));
    this.jRefPosMode = new JComboBox<> (Tek2440_GPIB_Settings.RefPositionMode.values ());
    this.jRefPosMode.setMaximumSize (new Dimension (120, 20));
    // this.jRefPosMode.addItemListener (this.jRefPosModeListener);
    this.jRefPosMode.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "reference position mode",
      Tek2440_GPIB_Settings.RefPositionMode.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.RefPositionMode>) tek2440::setReferencePositionMode,
      () -> JTek2440_GPIB_RefPos.this.inhibitInstrumentControl));
    jModePanel.add (Box.createGlue ());
    jModePanel.add (this.jRefPosMode);
    jModePanel.add (Box.createGlue ());
    controlsPanel.add (jModePanel);
    this.jRefPos1 = new JSlider (JSlider.VERTICAL, 0, 1023, 0);
    this.jRefPos1.setMajorTickSpacing (1023);
    this.jRefPos1.setMinorTickSpacing (100);
    this.jRefPos1.setPaintTicks (true);
    this.jRefPos1.setPaintLabels (true);
    this.jRefPos1.setToolTipText ("-");
    this.jRefPos1.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "reference 1 position",
      tek2440::setReferencePosition_1,
      () -> JTek2440_GPIB_RefPos.this.inhibitInstrumentControl));
    controlsPanel.add (this.jRefPos1);
    this.jRefPos2 = new JSlider (JSlider.VERTICAL, 0, 1023, 0);
    this.jRefPos2.setMajorTickSpacing (1023);
    this.jRefPos2.setMinorTickSpacing (100);
    this.jRefPos2.setPaintTicks (true);
    this.jRefPos2.setPaintLabels (true);
    this.jRefPos2.setToolTipText ("-");
    this.jRefPos2.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "reference 2 position",
      tek2440::setReferencePosition_2,
      () -> JTek2440_GPIB_RefPos.this.inhibitInstrumentControl));
    controlsPanel.add (this.jRefPos2);
    this.jRefPos3 = new JSlider (JSlider.VERTICAL, 0, 1023, 0);
    this.jRefPos3.setMajorTickSpacing (1023);
    this.jRefPos3.setMinorTickSpacing (100);
    this.jRefPos3.setPaintTicks (true);
    this.jRefPos3.setPaintLabels (true);
    this.jRefPos3.setToolTipText ("-");
    this.jRefPos3.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "reference 3 position",
      tek2440::setReferencePosition_3,
      () -> JTek2440_GPIB_RefPos.this.inhibitInstrumentControl));
    controlsPanel.add (this.jRefPos3);
    this.jRefPos4 = new JSlider (JSlider.VERTICAL, 0, 1023, 0);
    this.jRefPos4.setMajorTickSpacing (1023);
    this.jRefPos4.setMinorTickSpacing (100);
    this.jRefPos4.setPaintTicks (true);
    this.jRefPos4.setPaintLabels (true);
    this.jRefPos4.setToolTipText ("-");
    this.jRefPos4.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "reference 4 position",
      tek2440::setReferencePosition_4,
      () -> JTek2440_GPIB_RefPos.this.inhibitInstrumentControl));
    controlsPanel.add (this.jRefPos4);
    add (controlsPanel, BorderLayout.CENTER);
    //
    final JPanel labelsPanel = new JPanel ();
    labelsPanel.setLayout (new GridLayout (1, 5));
    labelsPanel.setBorder (BorderFactory.createEmptyBorder (10, 0, 10, 0));
    final JLabel modeLabel = new JLabel ("Mode       ");
    modeLabel.setHorizontalAlignment (SwingConstants.CENTER);
    labelsPanel.add (modeLabel);
    final JLabel ref1PosLabel = new JLabel ("Ref1 Pos   ");
    ref1PosLabel.setHorizontalAlignment (SwingConstants.CENTER);
    labelsPanel.add (ref1PosLabel);
    final JLabel ref2PosLabel = new JLabel ("Ref2 Pos   ");
    ref2PosLabel.setHorizontalAlignment (SwingConstants.CENTER);
    labelsPanel.add (ref2PosLabel);
    final JLabel ref3PosLabel = new JLabel ("Ref3 Pos   ");
    ref3PosLabel.setHorizontalAlignment (SwingConstants.CENTER);
    labelsPanel.add (ref3PosLabel);
    final JLabel ref4PosLabel = new JLabel ("Ref4 Pos   ");
    ref4PosLabel.setHorizontalAlignment (SwingConstants.CENTER);
    labelsPanel.add (ref4PosLabel);
    add (labelsPanel, BorderLayout.SOUTH);
    //
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    //
  }

  public JTek2440_GPIB_RefPos (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_RefPos (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Reference [Waveform] Position Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_RefPos ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_RefPos.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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

  private final JComboBox<Tek2440_GPIB_Settings.RefPositionMode> jRefPosMode;
  
  private final JSlider jRefPos1;
  
  private final JSlider jRefPos2;
  
  private final JSlider jRefPos3;
  
  private final JSlider jRefPos4;
  
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
      if (instrument != JTek2440_GPIB_RefPos.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_RefPos.this.inhibitInstrumentControl = true;
        try
        {
          JTek2440_GPIB_RefPos.this.jRefPosMode.setSelectedItem (settings.getRefPositionMode ());
          JTek2440_GPIB_RefPos.this.jRefPos1.setValue ((int) Math.round (settings.getRefPosition1 ()));
          JTek2440_GPIB_RefPos.this.jRefPos1.setToolTipText (Double.toString (settings.getRefPosition1 ()));
          JTek2440_GPIB_RefPos.this.jRefPos2.setValue ((int) Math.round (settings.getRefPosition2 ()));
          JTek2440_GPIB_RefPos.this.jRefPos2.setToolTipText (Double.toString (settings.getRefPosition2 ()));
          JTek2440_GPIB_RefPos.this.jRefPos3.setValue ((int) Math.round (settings.getRefPosition3 ()));
          JTek2440_GPIB_RefPos.this.jRefPos3.setToolTipText (Double.toString (settings.getRefPosition3 ()));
          JTek2440_GPIB_RefPos.this.jRefPos4.setValue ((int) Math.round (settings.getRefPosition4 ()));
          JTek2440_GPIB_RefPos.this.jRefPos4.setToolTipText (Double.toString (settings.getRefPosition4 ()));
        }
        finally
        {
          JTek2440_GPIB_RefPos.this.inhibitInstrumentControl = false;
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
