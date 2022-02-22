/*
 * Copyright 2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.swing.instrument.sa.hp70000;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SpectrumAnalyzer;
import org.javajdj.jinstrument.gpib.sa.hp70000.HP70000_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.sa.hp70000.HP70000_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.base.JSpectrumAnalyzerPanel;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for the Source settings of a {@link HP70000_GPIB_Instrument} Spectrum Analyzer [MMS].
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP70000_GPIB_Source
  extends JSpectrumAnalyzerPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP70000_GPIB_Source.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP70000_GPIB_Source (
    final SpectrumAnalyzer spectrumAnalyzer,
    final String title,
    final int level,
    final Color panelColor)
  {

    super (spectrumAnalyzer, title, level, panelColor);
    if (! (spectrumAnalyzer instanceof HP70000_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final HP70000_GPIB_Instrument hp70000 = (HP70000_GPIB_Instrument) spectrumAnalyzer;

    removeAll ();
    setLayout (new GridLayout (2, 2));
    
    add (new JInstrumentPanel (
      hp70000,
      "Active",
      level + 1,
      getGuiPreferencesPowerColor (),
      new JBoolean_JBoolean (
      "Active",
      (settings) -> ((HP70000_GPIB_Settings) settings).isSourceActive (),
      hp70000::setSourceActive,
      Color.green,
      true)));
    
    add (new JInstrumentPanel (
      hp70000,
      "Source Power [dBm]",
      level + 1,
      getGuiPreferencesPowerColor (),
      new Jdouble_JTextField (
        Double.NaN,
        6,
        "Source Power [dBm]",
        (settings) -> ((HP70000_GPIB_Settings) settings).getSourcePower_dBm (),
        hp70000::setSourcePower_dBm,
        true)));
    
    add (new JInstrumentPanel (
      hp70000,
      "ALC Mode",
      level + 1,
      getGuiPreferencesPowerColor (),
      new JEnum_JComboBox<> (
        HP70000_GPIB_Settings.SourceAlcMode.class,
        "ALC Mode",
        (final InstrumentSettings settings) -> ((HP70000_GPIB_Settings) settings).getSourceAlcMode (),
        hp70000::setSourceAlcMode,
        true)));
    
    add (JCenter.XY (new JLabel ("TBD")));
    
  }

  public JHP70000_GPIB_Source (final HP70000_GPIB_Instrument spectrumAnalyzer, final int level)
  {
    this (spectrumAnalyzer, null, level, null);
  }
  
  public JHP70000_GPIB_Source (final HP70000_GPIB_Instrument spectrumAnalyzer)
  {
    this (spectrumAnalyzer, 0);
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
      return "HP-70000 [GPIB] Source Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP70000_GPIB_Instrument))
        return new JHP70000_GPIB_Source ((HP70000_GPIB_Instrument) instrument);
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
    return JHP70000_GPIB_Source.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
