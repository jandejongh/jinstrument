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
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for the Acquisition settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Acquisition
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Acquisition.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Acquisition (
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
    setLayout (new GridLayout (2, 1, 0, 0));
    
    final JPanel northPanel = new JPanel ();
    northPanel.setLayout (new GridLayout (5, 2, 0, 10));
    northPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Acquisition"));
    add (northPanel);  
    
    final JPanel southPanel = new JPanel ();
    southPanel.setLayout (new GridLayout (1, 2, 0, 0));
    add (southPanel);
    
    final JPanel southWestPanel = new JPanel ();
    southWestPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Run"));
    southWestPanel.setLayout (new BoxLayout (southWestPanel, BoxLayout.X_AXIS));
    southPanel.add (southWestPanel);
    
    final JPanel southEastPanel = new JPanel ();
    southEastPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Smooth"));
    southEastPanel.setLayout (new GridLayout (1,1));
    southPanel.add (southEastPanel);
    
    northPanel.add (new JLabel ("Acquisition Mode"));
    northPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.AcquisitionMode.class,
      "acquisition",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getAcquisitionMode (),
      tek2440::setAcquisitionMode,
      true)));
        
    northPanel.add (new JLabel ("Repetitive"));
    northPanel.add (new JBoolean_JBoolean (
      "acquisition repetitive",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isAcquisitionRepetitive (),
      tek2440::setAcquisitionRepetitive,
      Color.green,
      true));
    
    northPanel.add (new JLabel ("# of Acquisitions Averaged"));
    northPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.NumberOfAcquisitionsAveraged.class,
      "number of acquisitions averaged",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getNumberOfAcquisitionsAveraged (),
      tek2440::setNumberOfAcquisitionsAveraged,
      true)));
    
    northPanel.add (new JLabel ("# of Envelope Sweeps"));
    northPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.NumberOfEnvelopeSweeps.class,
      "number of envelope sweeps",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getNumberOfEnvelopeSweeps (),
      tek2440::setNumberOfEnvelopeSweeps,
      true)));
    
    northPanel.add (new JLabel ("Save on Delta"));
    northPanel.add (JCenter.Y (new JBoolean_JBoolean (
      "acquisition save-on-delta",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isAcquisitionSaveOnDelta (),
      tek2440::setAcquisitionSaveOnDelta,
      Color.green,
      true)));
    
    southWestPanel.add (JCenter.XY (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.RunMode.class,
      "run mode",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getRunMode (),
      tek2440::setRunMode,
      true)));
    
    southEastPanel.add (JCenter.XY (new JBoolean_JBoolean (
      "smoothing",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isSmoothing (),
      tek2440::setSmoothing,
      Color.green,
      true)));
    
  }

  public JTek2440_GPIB_Acquisition (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Acquisition (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Acquisition Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Acquisition ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Acquisition.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
