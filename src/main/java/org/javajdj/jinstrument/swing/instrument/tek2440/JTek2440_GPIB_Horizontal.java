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
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;

/** A Swing panel for the Horizontal settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Horizontal
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Horizontal.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Horizontal (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final String title,
    final int level,
    final Color panelColor)
  {
    
    super (digitalStorageOscilloscope, title, level, panelColor);
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;
    
    setLayout (new GridLayout (5, 2, 0, 10));
    
    add (new JLabel ("Mode"));
    add (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.HorizontalMode.class,
      "horizontal mode",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getHorizontalMode (),
      tek2440::setHorizontalMode,
      true));
        
    add (new JLabel ("A Timebase"));
    add (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.SecondsPerDivision.class,
      "A timebase",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getASecondsPerDivision (),
      tek2440::setTimebaseA,
      true));
    
    add (new JLabel ("B Timebase"));
    add (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.SecondsPerDivision.class,
      "B timebase",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getBSecondsPerDivision (),
      tek2440::setTimebaseB,
      true));
    
    add (new JLabel ("Position"));
    add (new JDouble_JSlider (
      SwingConstants.HORIZONTAL,
      0.0,
      1023.0,
      0.0,
      1023.0,
      128.0,
      "horizontal position",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getHorizontalPosition (),
      tek2440::setHorizontalPosition,
      true));
    
    add (new JLabel ("Hor Ext Expansion"));
    add (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.HorizontalExternalExpansionFactor.class,
      "horizontal external expansion factor",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getHorizontalExternalExpansionFactor (),
      tek2440::setHorizontalExternalExpansion,
      true));
    
  }

  public JTek2440_GPIB_Horizontal (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Horizontal (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Horizontal Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Horizontal ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Horizontal.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
