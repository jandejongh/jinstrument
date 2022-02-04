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
import javax.swing.JPanel;
import org.javajdj.jinstrument.DigitalMultiMeter;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalMultiMeterPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for the Trigger and Acquisition Settings of a {@link HP3457A_GPIB_Instrument} Digital Multi Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3457A_GPIB_TriggerAcquisition
  extends JDigitalMultiMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3457A_GPIB_TriggerAcquisition.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3457A_GPIB_TriggerAcquisition (
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
    setLayout (new GridLayout (4, 1));
    final JPanel triggerArmPanel = new JPanel ();
    add (triggerArmPanel);
    final JPanel triggerPanel = new JPanel ();
    add (triggerPanel);
    final JPanel sampleEventPanel = new JPanel ();
    add (sampleEventPanel);
    final JPanel acquisitionPanel = new JPanel ();
    add (acquisitionPanel);

    triggerArmPanel.setLayout (new GridLayout (1, 2));
    triggerArmPanel.add (new JInstrumentPanel (
      hp3457a,
      "Trigger Arm Event",
      level + 1,
      getGuiPreferencesTriggerColor (),
      JCenter.XY (new JEnum_JComboBox<> (
        HP3457A_GPIB_Settings.TriggerEvent.class,
        "Trigger Arm Event",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getTriggerArmEvent (),
        hp3457a::setTriggerArmEvent,
        true))));
    triggerArmPanel.add (new JInstrumentPanel (
      hp3457a,
      "Number of Arms",
      level + 1,
      getGuiPreferencesTriggerColor (),
      JCenter.XY (new Jint_JTextField (
        0,
        5,
        "Number of Arms",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getNumberOfTriggerArms (),
        (final int selection) ->
        {
          hp3457a.setTriggerArmEvent (HP3457A_GPIB_Settings.TriggerEvent.SGL, selection);
        },
        true))));
     
    triggerPanel.setLayout (new GridLayout (1, 3));
    triggerPanel.add (new JInstrumentPanel (
      hp3457a,
      "Trigger Event",
      level + 1,
      getGuiPreferencesTriggerColor (),
      JCenter.XY (new JEnum_JComboBox<> (
        HP3457A_GPIB_Settings.TriggerEvent.class,
        "Trigger Event",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getTriggerEvent (),
        hp3457a::setTriggerEvent,
        true))));
    triggerPanel.add (new JInstrumentPanel (
      hp3457a,
      "Trigger Buffering",
      level + 1,
      getGuiPreferencesTriggerColor (),
      new JBoolean_JBoolean (
      "Trigger Buffering",
      (settings) -> ((HP3457A_GPIB_Settings) settings).isTriggerBuffering (),
      hp3457a::setTriggerBuffering,
      Color.green,
      true)));
    triggerPanel.add (new JInstrumentPanel (
      hp3457a,
      "Manual Trigger",
      level + 1,
      getGuiPreferencesTriggerColor (),
      new JVoid_JColorCheckBox (
        "Manual Trigger",
        hp3457a::trigger,
        Color.blue)));
        
    sampleEventPanel.setLayout (new GridLayout (1, 3));
    sampleEventPanel.add (new JInstrumentPanel (
      hp3457a,
      "Sample Event",
      level + 1,
      getGuiPreferencesTriggerColor (),
      JCenter.XY (new JEnum_JComboBox<> (
        HP3457A_GPIB_Settings.TriggerEvent.class,
        "Sample Event",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getSampleEvent (),
        hp3457a::setSampleEvent,
        true))));
    sampleEventPanel.add (new JInstrumentPanel (
      hp3457a,
      "Number of Readings",
      level + 1,
      getGuiPreferencesTriggerColor (),
      JCenter.XY (new Jint_JTextField (
        1,
        5,
        "Number of Readings",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getNumberOfReadings (),
        hp3457a::setNumberOfReadings,
        true))));
    final JPanel delayTimerPanel = new JPanel ();
    sampleEventPanel.add (delayTimerPanel);
    delayTimerPanel.setLayout (new GridLayout (2, 1));
    delayTimerPanel.add (new JInstrumentPanel (
      hp3457a,
      "Delay [s]",
      level + 1,
      getGuiPreferencesTimeColor (),
      JCenter.XY (new Jdouble_JTextField (
        Double.NaN,
        8,
        "Delay [s]",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getDelay_s (),
        hp3457a::setDelay,
        true))));
    delayTimerPanel.add (new JInstrumentPanel (
      hp3457a,
      "Timer [s]",
      level + 1,
      getGuiPreferencesTimeColor (),
      JCenter.XY (new Jdouble_JTextField (
        1.0,
        8,
        "Timer [s]",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getTimer_s (),
        hp3457a::setTimer,
        true))));
    
    acquisitionPanel.setLayout (new GridLayout (1, 3));
    acquisitionPanel.add (new JInstrumentPanel (
      hp3457a,
      "#PLCs",
      level + 1,
      getGuiPreferencesTimeColor (),
      JCenter.XY (new Jdouble_JTextField (
        Double.NaN,
        8,
        "#PLCs",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getNumberOfPowerLineCycles (),
        hp3457a::setNumberOfPowerLineCycles,
        true))));
    acquisitionPanel.add (new JInstrumentPanel (
      hp3457a,
      "Sample Time [s]",
      level + 1,
      getGuiPreferencesTimeColor (),
      JCenter.XY (new Jdouble_JTextField (
        Double.NaN,
        8,
        "Sample Time [s]",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getSampleTime_s (),
        null,
        true))));
    acquisitionPanel.add (new JInstrumentPanel (
      hp3457a,
      "Resolution [%]",
      level + 1,
      getGuiPreferencesTimeColor (),
      JCenter.XY (new JLabel ("TBD"))));
        
  }

  public JHP3457A_GPIB_TriggerAcquisition (final HP3457A_GPIB_Instrument digitalMultiMeter, final int level)
  {
    this (digitalMultiMeter, null, level, null);
  }
  
  public JHP3457A_GPIB_TriggerAcquisition (final HP3457A_GPIB_Instrument digitalMultiMeter)
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
      return "HP-3457A [GPIB] Trigger and Acquisition Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3457A_GPIB_Instrument))
        return new JHP3457A_GPIB_TriggerAcquisition ((HP3457A_GPIB_Instrument) instrument);
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
    return JHP3457A_GPIB_TriggerAcquisition.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
