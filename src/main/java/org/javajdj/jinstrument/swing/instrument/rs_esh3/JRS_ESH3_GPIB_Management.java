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
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SelectiveLevelMeter;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesManagementColor;
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;
import org.javajdj.jinstrument.swing.cdi.JSmallCDIStatusAndControl;
import org.javajdj.jinstrument.swing.component.JDialogButton;

/** A Swing panel for management settings of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_Management
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_Management.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_Management (
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
    add (topPanel);
    final JPanel bottomPanel = new JPanel ();
    add (bottomPanel);
    
    topPanel.setLayout (new GridLayout (2, 1));
    
    final JPanel topTopPanel = new JPanel ();
    topTopPanel.setLayout (new GridLayout (2,1));
    topPanel.add (topTopPanel);
    
    topTopPanel.add (new JSmallCDIStatusAndControl (
      selectiveLevelMeter.getDevice ().getController (),
      selectiveLevelMeter.getDevice (),
      selectiveLevelMeter,
      level + 2,
      getGuiPreferencesManagementColor (),
      true));
    
    topTopPanel.add (new JRS_ESH3_GPIB_OperatingMode (
      rs_esh3,
      "Operating Mode",
      level + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor ()));
    
    topPanel.add (new JRS_ESH3_GPIB_Status (
      selectiveLevelMeter,
      "Status",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    bottomPanel.setLayout (new GridLayout (2, 1));
    
    final JPanel bottomTopPanel = new JPanel ();
    bottomPanel.add (bottomTopPanel);
    bottomTopPanel.setLayout (new GridLayout (2, 1));
    
    final JPanel bottomTopTopPanel = new JPanel ();
    bottomTopPanel.add (bottomTopTopPanel);
    bottomTopTopPanel.setLayout (new GridLayout (1, 2));
      
    bottomTopTopPanel.add (new JRS_ESH3_GPIB_Refresh (
      selectiveLevelMeter,
      "Refresh",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    bottomTopTopPanel.add (new JRS_ESH3_GPIB_Calibration (
      selectiveLevelMeter,
      "Calibration",
      level + 1,
      getGuiPreferencesAmplitudeColor ()));
    
    final JPanel bottomTopBottomPanel = new JPanel ();
    bottomTopPanel.add (bottomTopBottomPanel);
    bottomTopBottomPanel.setLayout (new GridLayout (1, 3));
      
    bottomTopBottomPanel.add (new JRS_ESH3_GPIB_Trigger (
      rs_esh3,
      "Trigger",
      level + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor ()));
    
    bottomTopBottomPanel.add (new JDialogButton (
      rs_esh3,
      "Memory",
      new Dimension (80, 80),
      "Memory Commands",
      new Dimension (800, 600),
      new JRS_ESH3_GPIB_StoreRecall (
        rs_esh3,
        "Memory",
        level + 1,
        JInstrumentPanel.getGuiPreferencesManagementColor ())));
    
    bottomTopBottomPanel.add (new JDialogButton (
      rs_esh3,
      "Misc",
      new Dimension (80, 80),
      "Misc Commands",
      new Dimension (800, 600),
      new JRS_ESH3_GPIB_Misc (
        rs_esh3,
        "Misc",
        level + 1,
        JInstrumentPanel.getGuiPreferencesManagementColor ())));
    
    bottomPanel.add (new JRS_ESH3_GPIB_Scanning (
      selectiveLevelMeter,
      "Automatic Scanning",
      level + 2,
      getGuiPreferencesManagementColor ()));
    
  }

  public JRS_ESH3_GPIB_Management (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_Management (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
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
      return "R&S ESH-3 [GPIB] Management Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_Management ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_Management.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
