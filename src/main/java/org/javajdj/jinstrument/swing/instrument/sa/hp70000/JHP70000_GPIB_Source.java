/*
 * Copyright 2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.swing.instrument.sa.hp70000;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SpectrumAnalyzer;
import org.javajdj.jinstrument.gpib.sa.hp70000.HP70000_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.sa.hp70000.HP70000_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.base.JSpectrumAnalyzerPanel;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for the Source settings of a {@link HP70000_GPIB_Instrument} Spectrum Analyzer [MMS].
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP70000_GPIB_Source
  extends JSpectrumAnalyzerPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP70000_GPIB_Source.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP70000_GPIB_Source (
    final SpectrumAnalyzer spectrumAnalyzer,
    final String title,
    final int level,
    final Color panelColor)
  {

    super (spectrumAnalyzer, title, level, panelColor);
    if (! (spectrumAnalyzer instanceof HP70000_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final HP70000_GPIB_Instrument hp70000 = (HP70000_GPIB_Instrument) spectrumAnalyzer;

    removeAll ();
    
    setLayout (new GridLayout (4, 1));
    
    final JPanel powerPanel = new JInstrumentPanel (
      hp70000,
      "Power/Active",
      level + 1,
      getGuiPreferencesPowerColor ());
    add (powerPanel);
    
    final JPanel modulationPanel = new JInstrumentPanel (
      hp70000,
      "Modulation [AM]",
      level + 1,
      getGuiPreferencesModulationColor ());
    add (modulationPanel);
    
    final JPanel powerSweepPanel = new JInstrumentPanel (
      hp70000,
      "Power Sweep",
      level + 1,
      getGuiPreferencesPowerColor ());
    add (powerSweepPanel);
    
    final JPanel trackingPanel = new JInstrumentPanel (
      hp70000,
      "Tracking",
      level + 1,
      getGuiPreferencesFrequencyColor ());
    add (trackingPanel);
    
    powerPanel.setLayout (new GridLayout (2, 5));
    
    powerPanel.add (new JInstrumentPanel (
      hp70000,
      "Active",
      level + 1,
      getGuiPreferencesPowerColor (),
      new JBoolean_JBoolean (
      "Active",
      (settings) -> ((HP70000_GPIB_Settings) settings).isSourceActive (),
      hp70000::setSourceActive,
      Color.green,
      true)));
    
    powerPanel.add (new JInstrumentPanel (
      hp70000,
      "Source Power [dBm]",
      level + 1,
      getGuiPreferencesPowerColor (),
      new Jdouble_JTextField (
        Double.NaN,
        6,
        "Source Power [dBm]",
        (settings) -> ((HP70000_GPIB_Settings) settings).getSourcePower_dBm (),
        hp70000::setSourcePower_dBm,
        true)));
    
    powerPanel.add (new JInstrumentPanel (
      hp70000,
      "ALC Mode",
      level + 1,
      getGuiPreferencesPowerColor (),
      new JEnum_JComboBox<> (
        HP70000_GPIB_Settings.SourceAlcMode.class,
        "ALC Mode",
        (final InstrumentSettings settings) -> ((HP70000_GPIB_Settings) settings).getSourceAlcMode (),
        hp70000::setSourceAlcMode,
        true)));
    
    powerPanel.add (new JInstrumentPanel (
      hp70000,
      "Source Attenuation [dB]",
      level + 1,
      getGuiPreferencesPowerColor (),
      new Jdouble_JTextField (
        Double.NaN,
        6,
        "Source Attenuation [dB]",
        (settings) -> ((HP70000_GPIB_Settings) settings).getSourceAttenuation_dB (),
        hp70000::setSourceAttenuation_dB,
        true)));
    
    powerPanel.add (new JInstrumentPanel (
      hp70000,
      "Coupled",
      level + 1,
      getGuiPreferencesPowerColor (),
      new JBoolean_JBoolean (
      "Coupled",
      (settings) -> ((HP70000_GPIB_Settings) settings).isSourceAttenuationCoupled (),
      hp70000::setSourceAttenuationCoupled,
      Color.green,
      true)));
    
    powerPanel.add (new JInstrumentPanel (
      hp70000,
      "Blanking",
      level + 1,
      getGuiPreferencesPowerColor (),
      new JBoolean_JBoolean (
      "Blanking",
      (settings) -> ((HP70000_GPIB_Settings) settings).isSourceBlanking (),
      hp70000::setSourceBlanking,
      Color.green,
      true)));
    
    powerPanel.add (new JInstrumentPanel (
      hp70000,
      "Oscillator",
      level + 1,
      getGuiPreferencesFrequencyColor (),
      new JEnum_JComboBox<> (
        HP70000_GPIB_Settings.SourceOscillator.class,
        "Oscillator",
        (final InstrumentSettings settings) -> ((HP70000_GPIB_Settings) settings).getSourceOscillator (),
        hp70000::setSourceOscillator,
        true)));

    powerPanel.add (new JInstrumentPanel (
      hp70000,
      "Power Offset [dB]",
      level + 1,
      getGuiPreferencesPowerColor (),
      new Jdouble_JTextField (
        Double.NaN,
        6,
        "Power Offset [dB]",
        (settings) -> ((HP70000_GPIB_Settings) settings).getSourcePowerOffset_dB (),
        hp70000::setSourcePowerOffset_dB,
        true)));
    
    powerPanel.add (new JInstrumentPanel (
      hp70000,
      "Power Step-Size Mode",
      level + 1,
      getGuiPreferencesPowerColor (),
      new JEnum_JComboBox<> (
        HP70000_GPIB_Settings.SourcePowerStepSizeMode.class,
        "Power Step-Size Mode",
        (final InstrumentSettings settings) -> ((HP70000_GPIB_Settings) settings).getSourcePowerStepSizeMode (),
        hp70000::setSourcePowerStepSizeMode,
        true)));

    powerPanel.add (new JInstrumentPanel (
      hp70000,
      "Power Step Size [dB]",
      level + 1,
      getGuiPreferencesPowerColor (),
      new Jdouble_JTextField (
        Double.NaN,
        6,
        "Power Step Size [dB]",
        (settings) -> ((HP70000_GPIB_Settings) settings).getSourcePowerStepSize_dB (),
        hp70000::setSourcePowerStepSize_dB,
        true)));
    
    modulationPanel.setLayout (new GridLayout (1, 4));
    
    modulationPanel.add (new JInstrumentPanel (
      hp70000,
      "AM",
      level + 1,
      getGuiPreferencesModulationColor (),
      new JBoolean_JBoolean (
      "AM",
      (settings) -> ((HP70000_GPIB_Settings) settings).isSourceAmActive (),
      hp70000::setSourceAmActive,
      Color.green,
      true)));
    
    modulationPanel.add (new JInstrumentPanel (
      hp70000,
      "AM Depth [%]",
      level + 1,
      getGuiPreferencesModulationColor (),
      new Jdouble_JTextField (
        Double.NaN,
        6,
        "AM Depth [%]",
        (settings) -> ((HP70000_GPIB_Settings) settings).getSourceAmDepth_percent (),
        hp70000::setSourceAmDepth_percent,
        true)));
    
    modulationPanel.add (new JInstrumentPanel (
      hp70000,
      "AM Frequency [Hz]",
      level + 1,
      getGuiPreferencesFrequencyColor (),
      new Jdouble_JTextField (
        Double.NaN,
        12,
        "AM Frequency [Hz]",
        (settings) -> ((HP70000_GPIB_Settings) settings).getSourceAmFrequency_Hz (),
        hp70000::setSourceAmFrequency_Hz,
        true)));
    
    modulationPanel.add (new JInstrumentPanel (
      hp70000,
      "Modulation Input",
      level + 1,
      getGuiPreferencesModulationColor (),
      new JEnum_JComboBox<> (
        HP70000_GPIB_Settings.SourceModulationInput.class,
        "Modulation Input",
        (final InstrumentSettings settings) -> ((HP70000_GPIB_Settings) settings).getSourceModulationInput (),
        hp70000::setSourceModulationInput,
        true)));

    powerSweepPanel.setLayout (new GridLayout (1, 2));
    
    powerSweepPanel.add (new JInstrumentPanel (
      hp70000,
      "Power Sweep",
      level + 1,
      getGuiPreferencesPowerColor (),
      new JBoolean_JBoolean (
      "Power Sweep",
      (settings) -> ((HP70000_GPIB_Settings) settings).isSourcePowerSweepActive (),
      hp70000::setSourcePowerSweepActive,
      Color.green,
      true)));
    
    powerSweepPanel.add (new JInstrumentPanel (
      hp70000,
      "Power Sweep Range [dB]",
      level + 1,
      getGuiPreferencesPowerColor (),
      new Jdouble_JTextField (
        Double.NaN,
        6,
        "Power Sweep Range [dB]",
        (settings) -> ((HP70000_GPIB_Settings) settings).getSourcePowerSweepRange_dB (),
        hp70000::setSourcePowerSweepRange_dB,
        true)));

    trackingPanel.setLayout (new GridLayout (1, 3));
    
    trackingPanel.add (new JInstrumentPanel (
      hp70000,
      "Tracking [Hz]",
      level + 1,
      getGuiPreferencesFrequencyColor (),
      new Jdouble_JTextField (
        Double.NaN,
        12,
        "Tracking [Hz]",
        (settings) -> ((HP70000_GPIB_Settings) settings).getSourceTracking_Hz (),
        hp70000::setSourceTracking_Hz,
        true)));
        
    trackingPanel.add (new JInstrumentPanel (
      hp70000,
      "Auto Track [once]",
      level + 1,
      getGuiPreferencesFrequencyColor (),
      JCenter.XY (new JVoid_JColorCheckBox (
        "Auto Track [once]",
        hp70000::setSourceTrackingAutoOnce,
        Color.blue))));
    
    trackingPanel.add (new JInstrumentPanel (
      hp70000,
      "Peak Track [auto]",
      level + 1,
      getGuiPreferencesFrequencyColor (),
      new JBoolean_JBoolean (
      "Peak Track [auto]",
      (settings) -> ((HP70000_GPIB_Settings) settings).isSourcePeakTrackingAuto (),
      hp70000::setSourcePeakTrackingAuto,
      Color.green,
      true)));
    
  }

  public JHP70000_GPIB_Source (final HP70000_GPIB_Instrument spectrumAnalyzer, final int level)
  {
    this (spectrumAnalyzer, null, level, null);
  }
  
  public JHP70000_GPIB_Source (final HP70000_GPIB_Instrument spectrumAnalyzer)
  {
    this (spectrumAnalyzer, 0);
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
      return "HP-70000 [GPIB] Source Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP70000_GPIB_Instrument))
        return new JHP70000_GPIB_Source ((HP70000_GPIB_Instrument) instrument);
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
    return JHP70000_GPIB_Source.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
