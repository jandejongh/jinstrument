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
package org.javajdj.jinstrument.swing.instrument.slm.rs_esh3;

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
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SelectiveLevelMeter;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;
import org.javajdj.jinstrument.swing.component.JDialogButton;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for the [Automatic] Scanning settings of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_Scanning
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_Scanning.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_Scanning (
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
    setLayout (new GridLayout (2, 1));
    
    final JPanel topPanel = new JPanel ();
    topPanel.setLayout (new GridLayout (1, 4));
    add (topPanel);
    
    final JPanel runPanel = new JInstrumentPanel (
      rs_esh3,
      "Run",
      level + 1,
      getGuiPreferencesManagementColor ());
    topPanel.add (runPanel);
    runPanel.setLayout (new GridLayout (1,1));
    
    final JColorCheckBox jRunButton = new JColorCheckBox ((t) -> Color.blue);
    jRunButton.addActionListener (this.jRunButtonListener);
    runPanel.add (JCenter.XY (jRunButton));
    
    topPanel.add (new JDialogButton (
      rs_esh3,
      "MemRun",
      new Dimension (80, 80),
      "MemRun",
      new Dimension (800, 600),
      new JRS_ESH3_GPIB_MemoryRun (
        rs_esh3,
        level + 1)));
    
    final JPanel stopInterruptPanel = new JInstrumentPanel (
      rs_esh3,
      "Interrupt",
      level + 1,
      getGuiPreferencesManagementColor ());
    topPanel.add (stopInterruptPanel);
    stopInterruptPanel.setLayout (new GridLayout (1,1));
    final JColorCheckBox jStopInterruptButton = new JColorCheckBox ((t) -> Color.blue);
    jStopInterruptButton.addActionListener (this.jStopInterruptButtonListener);
    stopInterruptPanel.add (JCenter.XY (jStopInterruptButton));
    
    final JPanel stopResetPanel = new JInstrumentPanel (
      rs_esh3,
      "Stop",
      level + 1,
      getGuiPreferencesManagementColor ());
    topPanel.add (stopResetPanel);
    stopResetPanel.setLayout (new GridLayout (1,1));
    final JColorCheckBox jStopResetButton = new JColorCheckBox ((t) -> Color.blue);
    jStopResetButton.addActionListener (this.jStopResetButtonListener);
    stopResetPanel.add (JCenter.XY (jStopResetButton));
        
    final JPanel bottomPanel = new JPanel ();
    bottomPanel.setLayout (new GridLayout (1, 2));
    add (bottomPanel);
    
    final JPanel repeatModePanel = new JInstrumentPanel (
      rs_esh3,
      "Repeat",
      level + 1,
      getGuiPreferencesManagementColor ());
    bottomPanel.add (repeatModePanel);
    repeatModePanel.setLayout (new GridLayout (1,1));
    repeatModePanel.add (JCenter.XY (new JEnum_JComboBox<> (
      RS_ESH3_GPIB_Settings.FrequencyScanRepeatMode.class,
      "Repeat Mode [SF50/51]",
      (final InstrumentSettings settings) -> ((RS_ESH3_GPIB_Settings) settings).getFrequencyScanRepeatMode (),
      rs_esh3::setFrequencyScanRepeatMode,
      true)));
    
    final JPanel scanSpeedModePanel = new JInstrumentPanel (
      rs_esh3,
      "Speed",
      level + 1,
      getGuiPreferencesManagementColor ());
    bottomPanel.add (scanSpeedModePanel);
    scanSpeedModePanel.setLayout (new GridLayout (1,1));
    scanSpeedModePanel.add (JCenter.XY (new JEnum_JComboBox<> (
      RS_ESH3_GPIB_Settings.FrequencyScanSpeedMode.class,
      "Speed Mode [SF90/91]",
      (final InstrumentSettings settings) -> ((RS_ESH3_GPIB_Settings) settings).getFrequencyScanSpeedMode (),
      rs_esh3::setFrequencyScanSpeedMode,
      true)));
    
  }

  public JRS_ESH3_GPIB_Scanning (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_Scanning (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
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
      return "R&S ESH-3 [GPIB] Scanning Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_Scanning ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_Scanning.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final ActionListener jRunButtonListener = (final ActionEvent ae) ->
  {
    try
    {
      ((RS_ESH3_GPIB_Instrument) getSelectiveLevelMeter ()).scanningRun ();
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while running scan on instrument {0}: {1}.",        
        new Object[]
          {getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
  private final ActionListener jStopInterruptButtonListener = (final ActionEvent ae) ->
  {
    try
    {
      ((RS_ESH3_GPIB_Instrument) getSelectiveLevelMeter ()).scanningStopInterrupt ();
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while stop/interrupting scan on instrument {0}: {1}.",        
        new Object[]
          {getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
  private final ActionListener jStopResetButtonListener = (final ActionEvent ae) ->
  {
    try
    {
      ((RS_ESH3_GPIB_Instrument) getSelectiveLevelMeter ()).scanningStopReset ();
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while stop/resetting scan on instrument {0}: {1}.",        
        new Object[]
          {getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
