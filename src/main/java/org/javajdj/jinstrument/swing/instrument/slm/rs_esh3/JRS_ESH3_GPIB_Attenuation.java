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
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SelectiveLevelMeter;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;

/** A Swing panel for the [RF/IF] Attenuation settings of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_Attenuation
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_Attenuation.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_Attenuation (
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
    setLayout (new GridLayout (3, 1));
    
    final JPanel modeRangeLinearityTestPanel = new JInstrumentPanel (
      rs_esh3,
      null,
      level + 1,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor ());
    modeRangeLinearityTestPanel.setLayout (new GridLayout (1, 2));
    add (modeRangeLinearityTestPanel);
    
    final JPanel modeRangePanel = new JInstrumentPanel (
      rs_esh3,
      null,
      level + 1,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor ());
    modeRangeLinearityTestPanel.add (modeRangePanel);
    modeRangePanel.setLayout (new GridLayout (2, 1));
    modeRangePanel.add (new JRS_ESH3_GPIB_AttenuationMode (
      rs_esh3,
      "Attenuation Mode",
      level + 2,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor ()));    
    modeRangePanel.add (new JRS_ESH3_GPIB_OperatingRange (
      rs_esh3,
      "Operating Range",
      level + 2,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor ()));
    
    modeRangeLinearityTestPanel.add (new JRS_ESH3_GPIB_LinearityTest (
      rs_esh3,
      "Linearity Test",
      level + 1,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor ()));
    
    add (new JRS_ESH3_GPIB_RF_Attenuation (
      rs_esh3,
      "RF Attenuation [dB]",
      level + 1,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor ()));
    
    add (new JRS_ESH3_GPIB_IF_Attenuation (
      rs_esh3,
      "IF Attenuation [dB]",
      level + 1,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor ()));
    
  }

  public JRS_ESH3_GPIB_Attenuation (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_Attenuation (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
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
      return "R&S ESH-3 [GPIB] Attenuation Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_Attenuation ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_Attenuation.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
