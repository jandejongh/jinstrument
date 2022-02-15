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

import java.awt.GridLayout;
import java.util.logging.Logger;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.swing.default_view.JDefaultSelectiveLevelMeterView;

/** A Swing panel for (complete) control and status of a {@link JRS_ESH3_GPIB} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB
  extends JDefaultSelectiveLevelMeterView
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    
    super (selectiveLevelMeter, level, false);
    if (! (selectiveLevelMeter instanceof RS_ESH3_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final RS_ESH3_GPIB_Instrument rs_esh3 = (RS_ESH3_GPIB_Instrument) selectiveLevelMeter;
    
    removeAll ();
    setOpaque (true);
    setLayout (new GridLayout (2, 4));
    
    add (new JRS_ESH3_GPIB_Management (
      selectiveLevelMeter,
      "Management",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    add (new JRS_ESH3_GPIB_Attenuation (
      selectiveLevelMeter,
      "Attenuation",
      level + 1,
      getGuiPreferencesAmplitudeColor ()));
    
    add (new JRS_ESH3_GPIB_Frequency (
      selectiveLevelMeter,
      "Frequency [MHz]",
      level + 1,
      getGuiPreferencesFrequencyColor ()));
    
    add (new JRS_ESH3_GPIB_IFBW_Demodulation (
      selectiveLevelMeter,
      null,
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    add (new JRS_ESH3_GPIB_MeasurementTime (
      selectiveLevelMeter,
      "Measurement Time [s]",
      level + 1,
      getGuiPreferencesTimeColor ()));
    
    add (new JRS_ESH3_GPIB_Frequency_Step (
      selectiveLevelMeter,
      "Frequency Step",
      level + 1,
      getGuiPreferencesFrequencyColor ()));
      
    add (new JRS_ESH3_GPIB_Levels (
      selectiveLevelMeter,
      "Levels",
      level + 2,
      getGuiPreferencesAmplitudeColor ()));
    
    add (new JRS_ESH3_GPIB_SF_Test (
      selectiveLevelMeter,
      "SF/Test",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
  }
  
  public JRS_ESH3_GPIB (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
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
      return "R&S ESH-3 [GPIB] SLM View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
