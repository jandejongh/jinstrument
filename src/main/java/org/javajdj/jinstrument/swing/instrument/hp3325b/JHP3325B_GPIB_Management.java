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
package org.javajdj.jinstrument.swing.instrument.hp3325b;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JFunctionGeneratorPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesManagementColor;
import org.javajdj.jinstrument.swing.cdi.JTinyCDIStatusAndControl;

/** A Swing panel for the Management settings of a {@link HP3325B_GPIB_Instrument} Function Generator.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3325B_GPIB_Management
  extends JFunctionGeneratorPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3325B_GPIB_Management.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3325B_GPIB_Management (
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
    setLayout (new GridLayout (3, 1));  
    final JPanel topPanel = new JPanel ();
    add (topPanel);
    final JPanel middlePanel = new JPanel ();
    add (middlePanel);
    final JPanel bottomPanel = new JPanel ();
    add (bottomPanel);
    
    topPanel.setLayout (new GridLayout (1, 3));
    
    topPanel.add (new JInstrumentPanel (
      hp3325b,
      "",
      level + 2,
      getGuiPreferencesManagementColor (),
      new JTinyCDIStatusAndControl (
        functionGenerator.getDevice ().getController (),
        functionGenerator.getDevice (),
        functionGenerator,
        level + 2,
        getGuiPreferencesManagementColor ())));
    
    topPanel.add (new JHP3325B_GPIB_Options (
      functionGenerator,
      "Options Installed",
      level + 2,
      getGuiPreferencesManagementColor ()));
    
    topPanel.add (new JInstrumentPanel (
      hp3325b,
      "Id",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JString_JTextField (
        "Unknown",
        64,
        "Id",
        (settings) -> ((HP3325B_GPIB_Settings) settings).getId (),
        null,
        true)));
    
    middlePanel.setLayout (new GridLayout (1, 1));
    
    middlePanel.add (new JHP3325B_GPIB_Status (
      functionGenerator,
      "Status",
      level + 2,
      getGuiPreferencesManagementColor ()));
    
    bottomPanel.setLayout (new GridLayout (1, 3));
    
    bottomPanel.add (new JHP3325B_GPIB_AmplitudeCalibration (
      functionGenerator,
      "Amplitude Calibration",
      level + 1,
      getGuiPreferencesVoltageColor ()));
    
    bottomPanel.add (new JInstrumentPanel (
      hp3325b,
      "Ext Freq Ref Locked",
      level + 1,
      getGuiPreferencesFrequencyColor (),
      new JBoolean_JBoolean (
        "Ext Freq Ref Locked",
        (settings) -> ((HP3325B_GPIB_Settings) settings).isExternalReferenceLocked (),
        null,
        Color.green,
        true)));
    
    bottomPanel.add (new JInstrumentPanel (
      hp3325b,
      "Reset Instrument",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JVoid_JColorCheckBox (
        "Reset",
        hp3325b::reset,
        Color.blue)));
    
  }

  public JHP3325B_GPIB_Management (final HP3325B_GPIB_Instrument functionGenerator, final int level)
  {
    this (functionGenerator, null, level, null);
  }
  
  public JHP3325B_GPIB_Management (final HP3325B_GPIB_Instrument functionGenerator)
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
      return "HP-3325B [GPIB] Management Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3325B_GPIB_Instrument))
        return new JHP3325B_GPIB_Management ((HP3325B_GPIB_Instrument) instrument);
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
    return JHP3325B_GPIB_Management.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
