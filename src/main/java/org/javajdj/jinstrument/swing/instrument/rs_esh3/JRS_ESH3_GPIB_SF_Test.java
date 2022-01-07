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
package org.javajdj.jinstrument.swing.instrument.rs_esh3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SelectiveLevelMeter;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;
import org.javajdj.jinstrument.swing.component.JDialogButton;
import org.javajdj.jinstrument.swing.component.JRealTimeReading;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for the SF/Test settings
 *    of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_SF_Test
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_SF_Test.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_SF_Test (
    final SelectiveLevelMeter selectiveLevelMeter,
    final String title,
    final int level,
    final Color panelColor)
  {

    super (selectiveLevelMeter, title, level, panelColor);
    if (! (selectiveLevelMeter instanceof RS_ESH3_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final RS_ESH3_GPIB_Instrument rs_esh3 = (RS_ESH3_GPIB_Instrument) selectiveLevelMeter;

    removeAll ();
    setLayout (new GridLayout (5, 1));
    
    this.jTestLevel = new JRS_ESH3_GPIB_SF_Test_Level (
      selectiveLevelMeter,
      "Level",
      level + 1,
      getGuiPreferencesAmplitudeColor ());
    add (jTestLevel);
    
    this.jTestModulationDepth = new JRS_ESH3_GPIB_SF_Test_ModulationDepth (
      selectiveLevelMeter,
      "Modulation Depth Avg/Peak+/Peak- [%]",
      level + 1,
      getGuiPreferencesModulationColor ());
    add (this.jTestModulationDepth);
    
    this.jTestFrequencyOffset = new JRS_ESH3_GPIB_SF_Test_FrequencyOffset (
      selectiveLevelMeter,
      "Frequency Offset [kHz]",
      level + 1,
      getGuiPreferencesFrequencyColor ());
    add (this.jTestFrequencyOffset);
        
    this.jTestFrequencyDeviation = new JRS_ESH3_GPIB_SF_Test_FrequencyDeviation (
      selectiveLevelMeter,
      "Frequency Deviation [kHz]",
      level + 1,
      getGuiPreferencesFrequencyColor ());
    add (this.jTestFrequencyDeviation);
    
    final JPanel clearPanel = new JPanel ();
    clearPanel.setLayout (new GridLayout (1, 3));
    add (clearPanel);
    
    final JInstrumentPanel clearSelectionPanel = new JInstrumentPanel (
      rs_esh3,
      "Clear Selection",
      level + 2,
      getGuiPreferencesManagementColor ());
    clearPanel.add (clearSelectionPanel);
    clearSelectionPanel.setLayout (new GridLayout (1, 1));
    final JColorCheckBox jClearSelectionButton = new JColorCheckBox ((t) -> Color.blue);
    jClearSelectionButton.addActionListener (this.jClearSelectionButtonListener);
    clearSelectionPanel.add (JCenter.XY (jClearSelectionButton));
    
    final JInstrumentPanel clearReadingPanel = new JInstrumentPanel (
      rs_esh3,
      "Clear Readings",
      level + 2,
      getGuiPreferencesManagementColor ());
    clearPanel.add (clearReadingPanel);
    clearReadingPanel.setLayout (new GridLayout (1, 1));
    final JColorCheckBox jClearReadingButton = new JColorCheckBox ((t) -> Color.blue);
    jClearReadingButton.addActionListener (this.jClearReadingButtonListener);
    clearReadingPanel.add (JCenter.XY (jClearReadingButton));

    final JRealTimeReading graph = new JRealTimeReading (
        rs_esh3,
        "Readings Graph(s)",
        level,
        getGuiPreferencesManagementColor ());
    
    clearPanel.add (new JDialogButton (
      rs_esh3,
      "Graph",
      new Dimension (80, 80),
      "Graph",
      new Dimension (800, 600),
      graph));
    
  }

  public JRS_ESH3_GPIB_SF_Test (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_SF_Test (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
  {
    this (selectiveLevelMeter, 0);
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
      return "R&S ESH-3 [GPIB] SF/Test Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_SF_Test ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_SF_Test.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JRS_ESH3_GPIB_SF_Test_Level jTestLevel;
  private final JRS_ESH3_GPIB_SF_Test_ModulationDepth jTestModulationDepth;
  private final JRS_ESH3_GPIB_SF_Test_FrequencyOffset jTestFrequencyOffset;
  private final JRS_ESH3_GPIB_SF_Test_FrequencyDeviation jTestFrequencyDeviation;
  
  private final ActionListener jClearSelectionButtonListener = (final ActionEvent ae) ->
  {
    try
    {
      ((RS_ESH3_GPIB_Instrument) getSelectiveLevelMeter ()).sfClear ();
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while running SF Clear on instrument {0}: {1}.",        
        new Object[]
          {getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
  private final ActionListener jClearReadingButtonListener = (final ActionEvent ae) ->
  {
    JRS_ESH3_GPIB_SF_Test.this.jTestLevel.clearReading ();
    JRS_ESH3_GPIB_SF_Test.this.jTestModulationDepth.clearReading ();
    JRS_ESH3_GPIB_SF_Test.this.jTestFrequencyOffset.clearReading ();
    JRS_ESH3_GPIB_SF_Test.this.jTestFrequencyDeviation.clearReading ();
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
