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
import javax.swing.JLabel;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JFunctionGeneratorPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesManagementColor;

/** A Swing panel for the Dangerous Miscellaneous settings of a {@link HP3325B_GPIB_Instrument} Function Generator.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3325B_GPIB_Misc_RareDangerous
  extends JFunctionGeneratorPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3325B_GPIB_Misc_RareDangerous.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3325B_GPIB_Misc_RareDangerous (
    final FunctionGenerator functionGenerator,
    final String title,
    final int level,
    final Color panelColor)
  {

    super (functionGenerator, title, level, panelColor);
    if (! (functionGenerator instanceof HP3325B_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final HP3325B_GPIB_Instrument hp3325b = (HP3325B_GPIB_Instrument) functionGenerator;

    removeAll ();
    setLayout (new GridLayout (6, 1));
    
    add (new JInstrumentPanel (
      hp3325b,
      "Response Header",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JBoolean_JBoolean (
      "Response Header",
      (settings) -> ((HP3325B_GPIB_Settings) settings).isUseResponseHeader (),
      hp3325b::setUseResponseHeader,
      Color.green,
      true)));
    
    add (new JInstrumentPanel (
      hp3325b,
      "DT Mode",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JEnum_JComboBox<> (
        HP3325B_GPIB_Settings.DataTransferMode.class,
        "DT Mode",
        (final InstrumentSettings settings) -> ((HP3325B_GPIB_Settings) settings).getDataTransferMode (),
        hp3325b::setDataTransferMode,
        true)));
    
    add (new JInstrumentPanel (
      hp3325b,
      "ESTB",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JLabel ("ESTB: XXX TBD")));
    
    add (new JInstrumentPanel (
      hp3325b,
      "Enhancements",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JBoolean_JBoolean (
      "Enhancements",
      (settings) -> ((HP3325B_GPIB_Settings) settings).isEnhancements (),
      hp3325b::setEnableEnhancements,
      Color.green,
      true)));
    
    add (new JInstrumentPanel (
      hp3325b,
      "RS-232 Echo",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JBoolean_JBoolean (
      "RS-232 Echo",
      (settings) -> ((HP3325B_GPIB_Settings) settings).isRs232Echo (),
      hp3325b::setRs232Echo,
      Color.green,
      true)));
    
    add (new JInstrumentPanel (
      hp3325b,
      "QSTB",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JVoid_JColorCheckBox (
        "QSTB",
        hp3325b::queryStatusByteRs232,
        Color.blue)));
    
  }

  public JHP3325B_GPIB_Misc_RareDangerous (final HP3325B_GPIB_Instrument functionGenerator, final int level)
  {
    this (functionGenerator, null, level, null);
  }
  
  public JHP3325B_GPIB_Misc_RareDangerous (final HP3325B_GPIB_Instrument functionGenerator)
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
      return "HP-3325B [GPIB] Dangerous Miscellaneous Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3325B_GPIB_Instrument))
        return new JHP3325B_GPIB_Misc_RareDangerous ((HP3325B_GPIB_Instrument) instrument);
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
    return JHP3325B_GPIB_Misc_RareDangerous.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
