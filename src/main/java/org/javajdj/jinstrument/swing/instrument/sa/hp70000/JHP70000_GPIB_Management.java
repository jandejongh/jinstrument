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
package org.javajdj.jinstrument.swing.instrument.sa.hp70000;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SpectrumAnalyzer;
import org.javajdj.jinstrument.gpib.sa.hp70000.HP70000_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.sa.hp70000.HP70000_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesManagementColor;
import org.javajdj.jinstrument.swing.base.JSpectrumAnalyzerPanel;
import org.javajdj.jinstrument.swing.cdi.JTinyCDIStatusAndControl;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JDialogCheckBox;

/** A Swing panel for Management Settings of a {@link HP70000_GPIB_Instrument} Spectrum Analyzer.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP70000_GPIB_Management
  extends JSpectrumAnalyzerPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP70000_GPIB_Management.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP70000_GPIB_Management (
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
    setLayout (new GridLayout (3, 1));
    
    final JPanel topPanel = new JPanel ();
    add (topPanel);
    final JPanel centerPanel = new JPanel ();
    add (centerPanel);
    final JPanel bottomPanel = new JPanel ();
    add (bottomPanel);
    
    topPanel.setLayout (new GridLayout (1, 3));
    
    topPanel.add (new JInstrumentPanel (
      hp70000,
      "Controller/Dev/Inst",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JTinyCDIStatusAndControl (
      spectrumAnalyzer.getDevice ().getController (),
      spectrumAnalyzer.getDevice (),
      spectrumAnalyzer,
      level + 2,
      getGuiPreferencesManagementColor ())));
    
    topPanel.add (new JHP70000_GPIB_Configuration (
      spectrumAnalyzer,
      "Configuration",
      level + 2,
      getGuiPreferencesManagementColor ()));

    final JPanel idAddressPanel = new JPanel ();
    topPanel.add (idAddressPanel);
    
    idAddressPanel.setLayout (new GridLayout (3, 1));
    
    idAddressPanel.add (new JInstrumentPanel (
      hp70000,
      "Id",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JString_JTextField (
        "unknown",
        16,
        "Id",
        (settings) -> ((HP70000_GPIB_Settings) settings).getId (),
        null,
        true)));
    
    idAddressPanel.add (new JInstrumentPanel (
      hp70000,
      "Identification Number",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JString_JTextField (
        "unknown",
        16,
        "Id",
        (settings) -> ((HP70000_GPIB_Settings) settings).getIdentificationNumber (),
        null,
        true)));

    idAddressPanel.add (JCenter.XY (new JLabel ("TBD")));
//    idAddressPanel.add (new JInstrumentPanel (
//      hp70000,
//      "Calibration Number",
//      level + 1,
//      getGuiPreferencesManagementColor (),
//      new JString_JTextField (
//        "Unknown",
//        16,
//        "Calibration Number",
//        (settings) -> ((HP3457A_GPIB_Settings) settings).getCalibrationNumberString (),
//        null,
//        true)));
    
    centerPanel.setLayout (new GridLayout (1, 1));
    centerPanel.add (new JHP70000_GPIB_Status (
      spectrumAnalyzer,
      "Status",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    bottomPanel.setLayout (new GridLayout (2, 1));

    final JPanel bottomTopPanel = new JPanel ();
    bottomPanel.add (bottomTopPanel);
    final JPanel bottomBottomPanel = new JPanel ();
    bottomPanel.add (bottomBottomPanel);
    
    bottomTopPanel.setLayout (new GridLayout (1, 2));
    
    bottomTopPanel.add (JCenter.XY (new JLabel ("TBD")));
//    bottomTopPanel.add (new JHP3457A_GPIB_Calibration (
//      spectrumAnalyzer,
//      "Calibration",
//      level + 1,
//      getGuiPreferencesManagementColor ()));
    
    bottomTopPanel.add (JCenter.XY (new JLabel ("TBD")));
//    bottomTopPanel.add (new JHP3457A_GPIB_State (
//      spectrumAnalyzer,
//      "State",
//      level + 1,
//      getGuiPreferencesManagementColor ()));
    
    bottomBottomPanel.setLayout (new GridLayout (1, 3));
    
    bottomBottomPanel.add (new JInstrumentPanel (
      hp70000,
      "Preset [IP]",
      level + 1,
      getGuiPreferencesManagementColor (),
      JCenter.XY (new JVoid_JColorCheckBox (
        "Preset [IP]",
        hp70000::preset,
        Color.blue))));
    
    bottomBottomPanel.add (new JInstrumentPanel (
      hp70000,
      "Measure Mode",
      level + 1,
      getGuiPreferencesManagementColor (),
      JCenter.XY (new JEnum_JComboBox<> (
        HP70000_GPIB_Settings.MeasureMode.class,
        "MeasureMode",
        (final InstrumentSettings settings) -> ((HP70000_GPIB_Settings) settings).getMeasureMode (),
        hp70000::setMeasureMode,
        true))));
    
    bottomBottomPanel.add (new JInstrumentPanel (
      hp70000,
      "Tracking Generator",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JDialogCheckBox (
        getBackground ().darker (),
        "Tracking Generator",
        new Dimension (1024, 768),
        new JHP70000_GPIB_Source (
          hp70000,
          "",
          level + 1,
          JInstrumentPanel.getGuiPreferencesManagementColor ()))));
    
  }

  public JHP70000_GPIB_Management (final HP70000_GPIB_Instrument spectrumAnalyzer, final int level)
  {
    this (spectrumAnalyzer, null, level, null);
  }
  
  public JHP70000_GPIB_Management (final HP70000_GPIB_Instrument spectrumAnalyzer)
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
      return "HP-70000 [GPIB] Management Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP70000_GPIB_Instrument))
        return new JHP70000_GPIB_Management ((HP70000_GPIB_Instrument) instrument);
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
    return JHP70000_GPIB_Management.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
