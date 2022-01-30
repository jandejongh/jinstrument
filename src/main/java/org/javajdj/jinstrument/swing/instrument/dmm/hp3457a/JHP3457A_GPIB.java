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
package org.javajdj.jinstrument.swing.instrument.dmm.hp3457a;

import java.awt.GridLayout;
import java.util.Arrays;
import java.util.logging.Logger;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Instrument;
import org.javajdj.jinstrument.swing.default_view.JDefaultDigitalMultiMeterView;
import org.javajdj.jinstrument.swing.default_view.JDefaultInstrumentReadingPanel_Double;
import org.javajdj.junits.Unit;

/** A Swing panel for (complete) control and status of a {@link HP3457A_GPIB_Instrument} Digital MultiMeter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3457A_GPIB
  extends JDefaultDigitalMultiMeterView
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3457A_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3457A_GPIB (final HP3457A_GPIB_Instrument digitalMultiMeter, final int level)
  {
    
    super (
      digitalMultiMeter,
      level,
      false,  // populateInstrumentManagementPanel
      false,  // populateInstrumentSpecificPanel
      true,   // populateModeRangeResolutionPanel
      false); // populateReadingPanel

    remove (getInstrumentSpecificPanel ());

    setLayout (new GridLayout (2, 3));
    
    getInstrumentManagementPanel ().removeAll ();
    getInstrumentManagementPanel ().setLayout (new GridLayout (1, 1));
    getInstrumentManagementPanel ().add (new JHP3457A_GPIB_Management (
      digitalMultiMeter,
      null,
      level + 1,
      null));
    
    add (new JHP3457A_GPIB_Input (
      digitalMultiMeter,
      "Input",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    add (new JHP3457A_GPIB_TriggerAcquisition (
      digitalMultiMeter,
      "Triggering/Acquisition",
      level + 1,
      getGuiPreferencesTriggerColor ()));
    
    add (new JHP3457A_GPIB_Misc (
      digitalMultiMeter,
      "Miscellaneous",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    getReadingPanel ().setLayout (new GridLayout (1, 1));
    getReadingPanel ().add (new JDefaultInstrumentReadingPanel_Double (
      digitalMultiMeter,
      null,
      level + 1,
      getGuiPreferencesVoltageColor (),
      false,
      getGuiPreferencesManagementColor (),
      0.000001,
      1000.0,
      0.000001,
      true,
      Arrays.asList (new Unit[]{
        Unit.UNIT_mV,
        Unit.UNIT_V,
        Unit.UNIT_mA,
        Unit.UNIT_A,
        Unit.UNIT_Ohm,
        Unit.UNIT_kOhm,
        Unit.UNIT_Hz,
        Unit.UNIT_kHz,
        Unit.UNIT_ms,
        Unit.UNIT_s}),
      true,
      true));

  }
  
  public JHP3457A_GPIB (final HP3457A_GPIB_Instrument digitalMultiMeter)
  {
    this (digitalMultiMeter, 0);
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
      return "HP-3457A [GPIB] DMM View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3457A_GPIB_Instrument))
        return new JHP3457A_GPIB ((HP3457A_GPIB_Instrument) instrument);
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
    return JHP3457A_GPIB.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
