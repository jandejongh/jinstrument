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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Instrument;
import org.javajdj.jinstrument.swing.base.JFunctionGeneratorPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.component.JDialogButton;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for the Frequency Sweep Control settings of a {@link HP3325B_GPIB_Instrument} Function Generator.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3325B_GPIB_FrequencySweepControl
  extends JFunctionGeneratorPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3325B_GPIB_FrequencySweepControl.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3325B_GPIB_FrequencySweepControl (
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
    setLayout (new GridLayout (1, 3));
    
    final JPanel modeResetPanel = new JPanel ();
    add (modeResetPanel);
    modeResetPanel.setLayout (new GridLayout (2, 1));
    modeResetPanel.add (new JHP3325B_GPIB_FrequencySweepMode (
      functionGenerator,
      "Mode",
      level + 1,
      getGuiPreferencesManagementColor ()));
    final JPanel resetPanel = new JInstrumentPanel (
      hp3325b,
      "Reset SSweep",
      level + 1,
      getGuiPreferencesManagementColor ());
    modeResetPanel.add (resetPanel);
    resetPanel.setLayout (new GridLayout (1, 1));
    resetPanel.add (JCenter.XY (new JVoid_JColorCheckBox (
      "Reset SSweep",
      hp3325b::resetSingleSweep,
      Color.blue)));
    
    final JPanel startSweepPanel = new JInstrumentPanel (
      hp3325b,
      "Start/Stop",
      level + 1,
      getGuiPreferencesManagementColor ());
    add (startSweepPanel);
    startSweepPanel.setLayout (new GridLayout (2, 2));
    startSweepPanel.add (JCenter.XY (new JVoid_JColorCheckBox (
      "Single",
      hp3325b::startSingleSweep,
      Color.blue)));
    startSweepPanel.add (JCenter.XY (new JVoid_JColorCheckBox (
      "Cont",
      hp3325b::startContinuousSweep,
      Color.blue)));
    startSweepPanel.add (JCenter.XY (new JLabel ("Single")));
    startSweepPanel.add (JCenter.XY (new JLabel ("Cont")));
    
    add (new JDialogButton (
      hp3325b,
      "Discrete Sweep Table",
      new Dimension (80, 80),
      "DST",
      new Dimension (800, 600),
      new JHP3325B_GPIB_DST (
        hp3325b,
        "Discrete Sweep Table",
        level + 1,
        JInstrumentPanel.getGuiPreferencesFrequencyColor ())));
    
  }

  public JHP3325B_GPIB_FrequencySweepControl (final HP3325B_GPIB_Instrument functionGenerator, final int level)
  {
    this (functionGenerator, null, level, null);
  }
  
  public JHP3325B_GPIB_FrequencySweepControl (final HP3325B_GPIB_Instrument functionGenerator)
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
      return "HP-3325B [GPIB] Frequency Sweep Control Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3325B_GPIB_Instrument))
        return new JHP3325B_GPIB_FrequencySweepControl ((HP3325B_GPIB_Instrument) instrument);
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
    return JHP3325B_GPIB_FrequencySweepControl.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
