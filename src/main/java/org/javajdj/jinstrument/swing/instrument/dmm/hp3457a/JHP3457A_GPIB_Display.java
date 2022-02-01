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

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.javajdj.jinstrument.DigitalMultiMeter;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Instrument;
import org.javajdj.jinstrument.swing.base.JDigitalMultiMeterPanel;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for Display Settings of a {@link HP3457A_GPIB_Instrument} Digital Multi Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3457A_GPIB_Display
  extends JDigitalMultiMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3457A_GPIB_Display.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3457A_GPIB_Display (
    final DigitalMultiMeter digitalMultiMeter,
    final String title,
    final int level,
    final Color panelColor)
  {

    super (digitalMultiMeter, title, level, panelColor);
    if (! (digitalMultiMeter instanceof HP3457A_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final HP3457A_GPIB_Instrument hp3457a = (HP3457A_GPIB_Instrument) digitalMultiMeter;

    removeAll ();
    setLayout (new GridLayout (2, 1));

    add (JCenter.XY (new JLabel ("TBD: DISP [instrument/display]")));
    add (JCenter.XY (new JLabel ("TBD: NDIG [instrument/display]")));
    
  }

  public JHP3457A_GPIB_Display (final HP3457A_GPIB_Instrument digitalMultiMeter, final int level)
  {
    this (digitalMultiMeter, null, level, null);
  }
  
  public JHP3457A_GPIB_Display (final HP3457A_GPIB_Instrument digitalMultiMeter)
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
      return "HP-3457A [GPIB] Display Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3457A_GPIB_Instrument))
        return new JHP3457A_GPIB_Display ((HP3457A_GPIB_Instrument) instrument);
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
    return JHP3457A_GPIB_Display.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
