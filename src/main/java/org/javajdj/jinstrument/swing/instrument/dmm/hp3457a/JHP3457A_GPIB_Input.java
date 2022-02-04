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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.javajdj.jinstrument.DigitalMultiMeter;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalMultiMeterPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesManagementColor;
import org.javajdj.jswing.jcolorcheckbox.JDialogCheckBox;

/** A Swing panel for the Input Settings of a {@link HP3457A_GPIB_Instrument} Digital Multi Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3457A_GPIB_Input
  extends JDigitalMultiMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3457A_GPIB_Input.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3457A_GPIB_Input (
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
    setLayout (new GridLayout (5, 2));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Terminals",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JEnum_JComboBox<> (
        HP3457A_GPIB_Settings.MeasurementTerminals.class,
        "Terminals",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getMeasurementTerminals (),
        hp3457a::setMeasurementTerminals,
        true)));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Input Channel [Options]",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JEnum_JComboBox<> (
        HP3457A_GPIB_Settings.InputChannel.class,
        "Input Channel [Options]",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getInputChannel (),
        hp3457a::setInputChannel,
        true)));
    
    add (new JInstrumentPanel (
      hp3457a,
      "AC Bandwidth",
      level + 1,
      getGuiPreferencesFrequencyColor (),
      new JEnum_JComboBox<> (
        HP3457A_GPIB_Settings.ACBandwidth.class,
        "AC Bandwidth",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getACBandwidth (),
        hp3457a::setACBandwidth,
        true)));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Fixed-Z",
      level + 1,
      getGuiPreferencesImpedanceColor (),
      new JBoolean_JBoolean (
      "Fixed-Z",
      (settings) -> ((HP3457A_GPIB_Settings) settings).isFixedImpedance (),
      hp3457a::setFixedImpedance,
      Color.green,
      true)));
    
    add (new JInstrumentPanel (
      hp3457a,
      "AutoZero",
      level + 1,
      getGuiPreferencesAmplitudeColor (),
      new JEnum_JComboBox<> (
        HP3457A_GPIB_Settings.AutoZeroMode.class,
        "AutoZero",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getAutoZeroMode (),
        hp3457a::setAutoZeroMode,
        true)));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Offset Compensation",
      level + 1,
      getGuiPreferencesVoltageColor (),
      new JBoolean_JBoolean (
      "Offset Compensation",
      (settings) -> ((HP3457A_GPIB_Settings) settings).isOffsetCompensation (),
      hp3457a::setOffsetCompensation,
      Color.green,
      true)));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Actuator Channels [Options]",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JHP3457A_GPIB_ActuatorChannels (hp3457a)));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Reset/Scanning [Options]",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JDialogCheckBox (
        getBackground ().darker (),
        "Reset/Scanning [Options]",
        new Dimension (800, 600),
        new JHP3457A_GPIB_OptionsControl (
          hp3457a,
          "",
          level + 1,
          JInstrumentPanel.getGuiPreferencesManagementColor ()))));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Frequency/Period Input Source",
      level + 1,
      getGuiPreferencesFrequencyColor (),
      new JEnum_JComboBox<> (
        DigitalMultiMeter.MeasurementMode.class,
        "Frequency/Period Input Source",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getFrequencyPeriodInputSource (),
        (final DigitalMultiMeter.MeasurementMode selection) ->
        {
          switch (selection)
          {
            case AC_VOLTAGE:
            case AC_DC_VOLTAGE:
            case AC_CURRENT:
            case AC_DC_CURRENT:
              hp3457a.setFrequencyOrPeriodSource (selection);
              break;
            default:
              JOptionPane.showMessageDialog (
                new JFrame (),
                "Illegal value as frequency/period input source: " + selection + "; falling back to AC_VOLTAGE.",
                "Error - Illegal value!",
                JOptionPane.ERROR_MESSAGE);
              hp3457a.setFrequencyOrPeriodSource (DigitalMultiMeter.MeasurementMode.AC_VOLTAGE);              
          }
        },
        true)));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Freq/Per Input Range [V/A]",
      level + 1,
      getGuiPreferencesAmplitudeColor (),
      new JLabel ("TBD")));
    
  }

  public JHP3457A_GPIB_Input (final HP3457A_GPIB_Instrument digitalMultiMeter, final int level)
  {
    this (digitalMultiMeter, null, level, null);
  }
  
  public JHP3457A_GPIB_Input (final HP3457A_GPIB_Instrument digitalMultiMeter)
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
      return "HP-3457A [GPIB] Input Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3457A_GPIB_Instrument))
        return new JHP3457A_GPIB_Input ((HP3457A_GPIB_Instrument) instrument);
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
    return JHP3457A_GPIB_Input.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
