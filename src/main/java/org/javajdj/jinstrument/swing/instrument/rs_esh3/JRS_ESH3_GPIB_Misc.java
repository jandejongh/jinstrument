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
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SelectiveLevelMeter;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;
import org.javajdj.jinstrument.swing.component.JDialogButton;

/** A Swing panel for miscellaneous settings of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_Misc
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_Misc.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_Misc (
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
    setLayout (new GridLayout (4, 2));

    add (new JRS_ESH3_GPIB_Beep (
      rs_esh3,
      "Beep",
      level + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor ()));
    
    add (new JDialogButton (
      rs_esh3,
      "SRQ",
      new Dimension (80, 80),
      "R&S GPIB Service Request Settings",
      new Dimension (800, 600),
      new JRS_ESH3_GPIB_SRQ (rs_esh3, level + 1)));
    
    add (new JDialogButton (
      rs_esh3,
      "GPIB",
      new Dimension (80, 80),
      "R&S GPIB Settings",
      new Dimension (800, 600),
      new JRS_ESH3_GPIB_GPIB (rs_esh3, level + 1)));
    
    add (new JDialogButton (
      rs_esh3,
      "Text",
      new Dimension (80, 80),
      "Display Text",
      new Dimension (800, 600),
      new JRS_ESH3_GPIB_DisplayText (rs_esh3, level + 1)));
    
    add (new JRS_ESH3_GPIB_AntennaProbeCode (
      rs_esh3,
      "Antenna/Probe Code",
      level + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor ()));
    
    add (new JDialogButton (
      rs_esh3,
      "Recorder",
      new Dimension (80, 80),
      "R&S GPIB Recorder Settings",
      new Dimension (800, 600),
      new JRS_ESH3_GPIB_Recorder (
        rs_esh3,
        "Recorder",
        level + 1,
        JInstrumentPanel.getGuiPreferencesManagementColor ())));
        
    add (new JDialogButton (
      rs_esh3,
      "D/A",
      new Dimension (80, 80),
      "R&S GPIB D/A Commands",
      new Dimension (800, 600),
      new JRS_ESH3_GPIB_DA (
        rs_esh3,
        "D/A",
        level + 1,
        JInstrumentPanel.getGuiPreferencesManagementColor ())));
        
  }

  public JRS_ESH3_GPIB_Misc (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_Misc (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
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
      return "R&S ESH-3 [GPIB] Miscellaneous Settings [2]";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_Misc ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_Misc.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
