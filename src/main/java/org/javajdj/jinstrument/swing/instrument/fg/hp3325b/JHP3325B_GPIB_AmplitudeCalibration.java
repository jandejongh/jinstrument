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
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JFunctionGeneratorPanel;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for the Amplitude Calibration settings of a {@link HP3325B_GPIB_Instrument} Function Generator.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3325B_GPIB_AmplitudeCalibration
  extends JFunctionGeneratorPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3325B_GPIB_AmplitudeCalibration.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3325B_GPIB_AmplitudeCalibration (
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
    setLayout (new GridLayout (2, 2));

    add (JCenter.XY (new JEnum_JComboBox<> (
      HP3325B_GPIB_Settings.AmpltitudeCalibrationMode.class,
      "CALM",
      (final InstrumentSettings settings) -> ((HP3325B_GPIB_Settings) settings).getAmplitudeCalibrationMode (),
      hp3325b::setAmplitudeCalibrationMode,
      true)));
    
    add (JCenter.XY (new JVoid_JColorCheckBox (
      "Calibrate",
      hp3325b::amplitudeCalibration,
      Color.blue)));    

    add (JCenter.XY (new JLabel ("Mode")));
    
    add (JCenter.XY (new JLabel ("ACal")));
    
  }

  public JHP3325B_GPIB_AmplitudeCalibration (final HP3325B_GPIB_Instrument functionGenerator, final int level)
  {
    this (functionGenerator, null, level, null);
  }
  
  public JHP3325B_GPIB_AmplitudeCalibration (final HP3325B_GPIB_Instrument functionGenerator)
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
      return "HP-3325B [GPIB] Amplitude Calibration Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3325B_GPIB_Instrument))
        return new JHP3325B_GPIB_AmplitudeCalibration ((HP3325B_GPIB_Instrument) instrument);
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
    return JHP3325B_GPIB_AmplitudeCalibration.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
