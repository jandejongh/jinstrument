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
package org.javajdj.jinstrument.swing.instrument.fg.hp3325b;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesManagementColor;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesPowerColor;
import org.javajdj.jinstrument.swing.default_view.JDefaultFunctionGeneratorView;

/** A Swing panel for a {@link HP3325B_GPIB_Instrument} Function Generator.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3325B_GPIB
  extends JDefaultFunctionGeneratorView
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3325B_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3325B_GPIB (final HP3325B_GPIB_Instrument hp3325b, final int level)
  {

    super (hp3325b, level);
    
    removeAll ();
    setOpaque (true);
    setLayout (new GridLayout (3, 3));
    
    add (new JHP3325B_GPIB_Management (
      hp3325b,
      "Management",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    final JPanel waveformControlPanel = getWaveformControlPanel ();
    add (waveformControlPanel);
    waveformControlPanel.removeAll ();
    waveformControlPanel.setLayout (new GridLayout (3, 1));
    waveformControlPanel.add (new JInstrumentPanel (
      hp3325b,
      "Waveform",
      level + 1,
      getGuiPreferencesManagementColor (),
      getWaveformSelectorComponent ()));
    waveformControlPanel.add (new JInstrumentPanel (
      hp3325b,
      "HV Output",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JBoolean_JBoolean (
      "HV Output",
      (settings) -> ((HP3325B_GPIB_Settings) settings).isHighVoltageOutput (),
      hp3325b::setHighVoltageOutput,
      Color.green,
      true)));
    waveformControlPanel.add (new JInstrumentPanel (
      hp3325b,
      "Output",
      level + 1,
      getGuiPreferencesPowerColor (),
      new JEnum_JComboBox<> (
        HP3325B_GPIB_Settings.RFOutputMode.class,
        "Output",
        (final InstrumentSettings settings) -> ((HP3325B_GPIB_Settings) settings).getRFOutputMode (),
        hp3325b::setRFOutputMode,
        true)));
            
    add (new JHP3325B_GPIB_Misc (
      hp3325b,
      "Miscellaneous",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    add (getFrequencyPanel ());
    add (getAmplitudePanel ());
    add (getDCOffsetPanel ());
    
    add (new JHP3325B_GPIB_FrequencySweep (
      hp3325b,
      "Frequency Sweep",
      level + 1,
      getGuiPreferencesFrequencyColor ()));
    
    add (new JHP3325B_GPIB_Modulation (
      hp3325b,
      "Modulation",
      level + 1,
      getGuiPreferencesModulationColor ()));
    
    add (new JHP3325B_GPIB_Phase (
      hp3325b,
      "Phase",
      level + 1,
      getGuiPreferencesPhaseColor ()));
    
  }
  
  public JHP3325B_GPIB (final HP3325B_GPIB_Instrument functionGenerator)
  {
    this (functionGenerator, 0);
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
      return "HP-3325B [GPIB] FG View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3325B_GPIB_Instrument))
        return new JHP3325B_GPIB ((HP3325B_GPIB_Instrument) instrument);
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
    return JHP3325B_GPIB.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
