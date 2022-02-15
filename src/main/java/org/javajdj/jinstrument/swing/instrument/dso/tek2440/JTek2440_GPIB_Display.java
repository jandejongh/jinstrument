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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jswing.jcenter.JCenter;

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
    add (northPanel);
    
    final JPanel southPanel = new JPanel ();
    add (southPanel);
    southPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Display Intensities"));
    
    // northPanel
    
    northPanel.setLayout (new GridLayout (1, 2, 10, 10));
    
    final JPanel northWestPanel = new JPanel ();
    northPanel.add (northWestPanel);
    northWestPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Display Selections"));
    
    final JPanel northEastPanel = new JPanel ();
    northPanel.add (northEastPanel);
    northEastPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Instrument Front Panel Lock"));
    
    //
    // northWestPanel
    //
    
    northWestPanel.setLayout (new GridLayout (6, 2));
    
    northWestPanel.add (new JLabel ("Readout"));
    northWestPanel.add (new JBoolean_JBoolean (
      "display readout",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDisplayReadout (),
      tek2440::setDisplayReadout,
      Color.green,
      true));
    
    northWestPanel.add (new JLabel ("Vectors"));
    northWestPanel.add (new JBoolean_JBoolean (
      "display vectors",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDisplayVectors (),
      tek2440::setDisplayVectors,
      Color.green,
      true));
    
    northWestPanel.add (new JLabel ("Ref 1"));
    northWestPanel.add (new JBoolean_JBoolean (
      "display reference (waveform) 1",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDisplayRef1 (),
      tek2440::setDisplayReference1,
      Color.green,
      true));
    
    northWestPanel.add (new JLabel ("Ref 2"));
    northWestPanel.add (new JBoolean_JBoolean (
      "display reference (waveform) 2",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDisplayRef2 (),
      tek2440::setDisplayReference2,
      Color.green,
      true));
    
    northWestPanel.add (new JLabel ("Ref 3"));
    northWestPanel.add (new JBoolean_JBoolean (
      "display reference (waveform) 3",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDisplayRef3 (),
      tek2440::setDisplayReference3,
      Color.green,
      true));
    
    northWestPanel.add (new JLabel ("Ref 4"));
    northWestPanel.add (new JBoolean_JBoolean (
      "display reference (waveform) 4",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDisplayRef4 (),
      tek2440::setDisplayReference4,
      Color.green,
      true));
    
    //
    // northEastPanel
    //
    
    northEastPanel.setLayout (new GridLayout (1, 1));
    
    northEastPanel.add (JCenter.XY (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.LockMode.class,
      "[instrument] front-panel lock mode",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getLockMode (),
      tek2440::setLockMode,
      true)));
    
    //
    // southPanel [sliderPanel + labelPanel]
    //
    
    southPanel.setLayout (new BorderLayout ());
      
    final JPanel sliderPanel = new JPanel ();
    sliderPanel.setBorder (BorderFactory.createEmptyBorder (10, 0, 0, 0));
    southPanel.add (sliderPanel, BorderLayout.CENTER);
    
    final JPanel labelPanel = new JPanel ();
    labelPanel.setBorder (BorderFactory.createEmptyBorder (10, 0, 10, 0));
    southPanel.add (labelPanel, BorderLayout.SOUTH);
    
    sliderPanel.setLayout (new GridLayout (1, 4));
      
    sliderPanel.add (new JDouble_JSlider (
      SwingConstants.VERTICAL,
      0.0,
      100.0, // XXX Actually, 0-100 with 0.25 resolution...
      0.0,
      50.0,
      10.0,
      "display intensity",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getDisplayIntensity (),
      tek2440::setDisplayIntensity_Display,
      true).withPaintLabels ());
      
    sliderPanel.add (new JDouble_JSlider (
      SwingConstants.VERTICAL,
      0.0,
      100.0, // XXX Actually, 0-100 with 0.25 resolution...
      0.0,
      50.0,
      10.0,
      "graticule intensity",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getGraticuleIntensity (),
      tek2440::setDisplayIntensity_Graticule,
      true).withPaintLabels ());
      
    sliderPanel.add (new JDouble_JSlider (
      SwingConstants.VERTICAL,
      0.0,
      100.0, // XXX Actually, 0-100 with 0.25 resolution...
      0.0,
      50.0,
      10.0,
      "intensified-zone intensity",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getIntensifiedZoneIntensity (),
      tek2440::setDisplayIntensity_IntensifiedZone,
      true).withPaintLabels ());
      
    sliderPanel.add (new JDouble_JSlider (
      SwingConstants.VERTICAL,
      0.0,
      100.0, // XXX Actually, 0-100 with 0.25 resolution...
      0.0,
      50.0,
      10.0,
      "readout intensity",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getReadoutIntensity (),
      tek2440::setDisplayIntensity_Readout,
      true).withPaintLabels ());
      
    labelPanel.setLayout (new GridLayout (1, 4));

    labelPanel.add (JCenter.XY (new JLabel ("Display      ")));    
    labelPanel.add (JCenter.XY (new JLabel ("Graticule    ")));
    labelPanel.add (JCenter.XY (new JLabel ("Intens Zone  ")));
    labelPanel.add (JCenter.XY (new JLabel ("Readout      ")));
    
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
