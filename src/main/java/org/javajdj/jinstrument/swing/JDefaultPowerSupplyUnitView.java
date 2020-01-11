/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.PowerSupplyUnit;
import org.javajdj.jinstrument.PowerSupplyUnitReading;
import org.javajdj.jinstrument.PowerSupplyUnitSettings;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A one-size-fits-all Swing panel for control and status of a generic {@link PowerSupplyUnit}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JDefaultPowerSupplyUnitView
  extends JPowerSupplyUnitPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultPowerSupplyUnitView.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultPowerSupplyUnitView (final PowerSupplyUnit powerSupplyUnit, final int level)
  {
    //
    super (powerSupplyUnit, level);
    //
    // This view does not support negative voltages, currents, or powers.
    // XXX This isn't the appropriate check, but it will do for now.
    if (getPowerSupplyUnit ().getHardLimitVoltage_V () < 0)
      throw new IllegalArgumentException ();
    if (getPowerSupplyUnit ().getHardLimitCurrent_A () < 0)
      throw new IllegalArgumentException ();
    if (getPowerSupplyUnit ().getHardLimitPower_W () < 0)
      throw new IllegalArgumentException ();
    //
    setOpaque (true);
    setLayout (new GridLayout (4, 3));
    //
    final JDefaultInstrumentManagementView instrumentManagementPanel= new JDefaultInstrumentManagementView (
      JDefaultPowerSupplyUnitView.this.getInstrument (),
      JDefaultPowerSupplyUnitView.this.getLevel () + 1);
    setPanelBorder (
      instrumentManagementPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Management");
    add (instrumentManagementPanel);
    //
    this.jInstrumentSpecificPanel1 = new JPanel ();
    setPanelBorder (
      this.jInstrumentSpecificPanel1,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Instrument Specific");
    this.jInstrumentSpecificPanel1.setLayout (new GridLayout (1,1));
    final JLabel jInstrumentSpecificLabel1 = new JLabel ("Not Supported in this View");
    jInstrumentSpecificLabel1.setHorizontalAlignment (SwingConstants.CENTER);
    this.jInstrumentSpecificPanel1.add (jInstrumentSpecificLabel1);
    add (this.jInstrumentSpecificPanel1);
    //
    this.jInstrumentSpecificPanel2 = new JPanel ();
    setPanelBorder (
      this.jInstrumentSpecificPanel2,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Instrument Specific");
    this.jInstrumentSpecificPanel2.setLayout (new GridLayout (1,1));
    final JLabel jInstrumentSpecificLabel2 = new JLabel ("Not Supported in this View");
    jInstrumentSpecificLabel2.setHorizontalAlignment (SwingConstants.CENTER);
    this.jInstrumentSpecificPanel2.add (jInstrumentSpecificLabel2);
    add (this.jInstrumentSpecificPanel2);
    //
    final JPanel jLimitsVoltage = new JPanel ();
    setPanelBorder (
      jLimitsVoltage,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesVoltageColor (),
      "Voltage Limits");
    jLimitsVoltage.setLayout (new GridLayout (2, 1));
    final JPanel jLimitsVoltageHard = new JPanel ();
    setPanelBorder (
      jLimitsVoltageHard,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesVoltageColor (),
      "Hard [V]");
    jLimitsVoltageHard.setLayout (new BorderLayout ());
    this.jHardLimitVoltage = new JSevenSegmentNumber (getGuiPreferencesVoltageColor (), -999, 999, 0.01);
    this.jHardLimitVoltage.setNumber (getPowerSupplyUnit ().getHardLimitVoltage_V ());
    jLimitsVoltageHard.add (this.jHardLimitVoltage);
    jLimitsVoltage.add (jLimitsVoltageHard);
    final JPanel jLimitsVoltageSoft = new JPanel ();
    setPanelBorder (
      jLimitsVoltageSoft,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesVoltageColor (),
      "Soft [V]");
    jLimitsVoltageSoft.setLayout (new BorderLayout ());
    this.jSoftLimitVoltage = new JSevenSegmentNumber (getGuiPreferencesVoltageColor (), -999, 999, 0.01);
    this.jSoftLimitVoltage.setBlank ();
    if (getPowerSupplyUnit ().isSupportsSoftLimitVoltage ())
    {
      this.jSoftLimitVoltage.addMouseListener (this.jSoftLimitVoltageListener);
      jLimitsVoltageSoft.add (this.jSoftLimitVoltage);
    }
    else
    {
      final JLabel notSupportedLabel = new JLabel ("Not Supported");
      notSupportedLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
      notSupportedLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jLimitsVoltageSoft.add (notSupportedLabel);
    }
    jLimitsVoltage.add (jLimitsVoltageSoft);
    add (jLimitsVoltage);
    //
    final JPanel jLimitsCurrent = new JPanel ();
    setPanelBorder (
      jLimitsCurrent,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesCurrentColor (),
      "Current Limits");
    jLimitsCurrent.setLayout (new GridLayout (2, 1));
    final JPanel jLimitsCurrentHard = new JPanel ();
    setPanelBorder (
      jLimitsCurrentHard,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesCurrentColor (),
      "Hard [A]");
    jLimitsCurrentHard.setLayout (new BorderLayout ());
    this.jHardLimitCurrent = new JSevenSegmentNumber (getGuiPreferencesCurrentColor (), 0, 99, 0.001);
    this.jHardLimitCurrent.setNumber (getPowerSupplyUnit ().getHardLimitCurrent_A ());
    jLimitsCurrentHard.add (this.jHardLimitCurrent);
    jLimitsCurrent.add (jLimitsCurrentHard);
    final JPanel jLimitsCurrentSoft = new JPanel ();
    setPanelBorder (
      jLimitsCurrentSoft,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesCurrentColor (),
      "Soft [A]");
    jLimitsCurrentSoft.setLayout (new BorderLayout ());
    this.jSoftLimitCurrent = new JSevenSegmentNumber (getGuiPreferencesCurrentColor (), 0, 99, 0.001);
    this.jSoftLimitCurrent.setBlank ();
    if (getPowerSupplyUnit ().isSupportsSoftLimitCurrent ())
    {
      this.jSoftLimitCurrent.addMouseListener (this.jSoftLimitCurrentListener);
      jLimitsCurrentSoft.add (this.jSoftLimitCurrent);
    }
    else
    {
      final JLabel notSupportedLabel = new JLabel ("Not Supported");
      notSupportedLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
      notSupportedLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jLimitsCurrentSoft.add (notSupportedLabel);
    }
    jLimitsCurrent.add (jLimitsCurrentSoft);
    add (jLimitsCurrent);
    //
    final JPanel jLimitsPower = new JPanel ();
    setPanelBorder (
      jLimitsPower,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesPowerColor (),
      "Power Limits");
    jLimitsPower.setLayout (new GridLayout (2, 1));
    final JPanel jLimitsPowerHard = new JPanel ();
    setPanelBorder (
      jLimitsPowerHard,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesPowerColor (),
      "Hard [W]");
    jLimitsPowerHard.setLayout (new BorderLayout ());
    this.jHardLimitPower = new JSevenSegmentNumber (getGuiPreferencesPowerColor (), 0, 999, 0.1);
    this.jHardLimitPower.setNumber (getPowerSupplyUnit ().getHardLimitPower_W ());
    jLimitsPowerHard.add (this.jHardLimitPower);
    jLimitsPower.add (jLimitsPowerHard);
    final JPanel jLimitsPowerSoft = new JPanel ();
    setPanelBorder (
      jLimitsPowerSoft,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesPowerColor (),
      "Soft [W]");
    jLimitsPowerSoft.setLayout (new BorderLayout ());
    this.jSoftLimitPower = new JSevenSegmentNumber (getGuiPreferencesPowerColor (), 0, 999, 0.1);
    this.jSoftLimitPower.setBlank ();
    if (getPowerSupplyUnit ().isSupportsSoftLimitPower ())
    {
      this.jSoftLimitPower.addMouseListener (this.jSoftLimitPowerListener);
      jLimitsPowerSoft.add (this.jSoftLimitPower);
    }
    else
    {
      final JLabel notSupportedLabel = new JLabel ("Not Supported");
      notSupportedLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
      notSupportedLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jLimitsPowerSoft.add (notSupportedLabel);
    }
    jLimitsPower.add (jLimitsPowerSoft);
    add (jLimitsPower);
    //
    final JPanel jSettingVoltagePanel = new JPanel ();
    setPanelBorder (
      jSettingVoltagePanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesVoltageColor (),
      "Voltage Setting [V]");
    jSettingVoltagePanel.setLayout (new GridLayout (3, 1));
    this.jSettingVoltage = new JSevenSegmentNumber (getGuiPreferencesVoltageColor (), -999, 999, 0.01);
    this.jSettingVoltage.setBlank ();
    this.jSettingsVoltage_V = new JSlider (0, (int) Math.ceil (getPowerSupplyUnit ().getHardLimitVoltage_V ()));
    this.jSettingsVoltage_V.setValue (0);
    this.jSettingsVoltage_V.setToolTipText ("-");
    this.jSettingsVoltage_V.setMajorTickSpacing (10);
    this.jSettingsVoltage_V.setMinorTickSpacing (1);
    this.jSettingsVoltage_V.setPaintTicks (true);
    this.jSettingsVoltage_V.setPaintLabels (true);
    setPanelBorder (
      jSettingsVoltage_V,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesVoltageColor (),
      "V");
    this.jSettingsVoltage_mV = new JSlider (0, 999);
    this.jSettingsVoltage_mV.setValue (0);
    this.jSettingsVoltage_mV.setToolTipText ("-");
    this.jSettingsVoltage_mV.setMajorTickSpacing (100);
    this.jSettingsVoltage_mV.setMinorTickSpacing (10);
    this.jSettingsVoltage_mV.setPaintTicks (true);
    this.jSettingsVoltage_mV.setPaintLabels (true);
    setPanelBorder (
      jSettingsVoltage_mV,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesVoltageColor (),
      "mV");
    if (getPowerSupplyUnit ().isSupportsSetVoltage ())
    {
      this.jSettingVoltage.addMouseListener (this.jSettingVoltageListener);
      jSettingVoltagePanel.add (this.jSettingVoltage);
      this.jSettingsVoltage_V.addChangeListener (this.jSettingVoltageSliderListener);
      this.jSettingsVoltage_mV.addChangeListener (this.jSettingVoltageSliderListener);
      jSettingVoltagePanel.add (this.jSettingsVoltage_V);
      jSettingVoltagePanel.add (this.jSettingsVoltage_mV);
    }
    else
    {
      jSettingVoltagePanel.setLayout (new BorderLayout ());
      final JLabel notSupportedLabel = new JLabel ("Not Supported");
      notSupportedLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
      notSupportedLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jSettingVoltagePanel.add (notSupportedLabel, BorderLayout.CENTER);
    }
    add (jSettingVoltagePanel);
    //
    final JPanel jSettingCurrentPanel = new JPanel ();
    setPanelBorder (
      jSettingCurrentPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesCurrentColor (),
      "Current Setting [A]");
    jSettingCurrentPanel.setLayout (new GridLayout (3, 1));
    this.jSettingCurrent = new JSevenSegmentNumber (getGuiPreferencesCurrentColor (), 0, 99, 0.001);
    this.jSettingCurrent.setBlank ();
    this.jSettingsCurrent_A = new JSlider (0, (int) Math.ceil (getPowerSupplyUnit ().getHardLimitCurrent_A ()));
    this.jSettingsCurrent_A.setValue (0);
    this.jSettingsCurrent_A.setToolTipText ("-");
    this.jSettingsCurrent_A.setMajorTickSpacing (10);
    this.jSettingsCurrent_A.setMinorTickSpacing (1);
    this.jSettingsCurrent_A.setPaintTicks (true);
    this.jSettingsCurrent_A.setPaintLabels (true);
    setPanelBorder (
      jSettingsCurrent_A,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesCurrentColor (),
      "A");
    this.jSettingsCurrent_mA = new JSlider (0, 999);
    this.jSettingsCurrent_mA.setValue (0);
    this.jSettingsCurrent_mA.setToolTipText ("-");
    this.jSettingsCurrent_mA.setMajorTickSpacing (100);
    this.jSettingsCurrent_mA.setMinorTickSpacing (10);
    this.jSettingsCurrent_mA.setPaintTicks (true);
    this.jSettingsCurrent_mA.setPaintLabels (true);
    setPanelBorder (
      jSettingsCurrent_mA,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesCurrentColor (),
      "mA");
    if (getPowerSupplyUnit ().isSupportsSetCurrent ())
    {
      this.jSettingCurrent.addMouseListener (this.jSettingCurrentListener);
      jSettingCurrentPanel.add (this.jSettingCurrent);
      this.jSettingsCurrent_A.addChangeListener (this.jSettingCurrentSliderListener);
      this.jSettingsCurrent_mA.addChangeListener (this.jSettingCurrentSliderListener);
      jSettingCurrentPanel.add (this.jSettingsCurrent_A);
      jSettingCurrentPanel.add (this.jSettingsCurrent_mA);
    }
    else
    {
      jSettingCurrentPanel.setLayout (new BorderLayout ());
      final JLabel notSupportedLabel = new JLabel ("Not Supported");
      notSupportedLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
      notSupportedLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jSettingCurrentPanel.add (notSupportedLabel, BorderLayout.CENTER);
    }
    add (jSettingCurrentPanel);
    //
    final JPanel jSettingPowerPanel = new JPanel ();
    setPanelBorder (
      jSettingPowerPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesPowerColor (),
      "Power Setting [W]");
    jSettingPowerPanel.setLayout (new GridLayout (3, 1));
    this.jSettingPower = new JSevenSegmentNumber (getGuiPreferencesPowerColor (), 0, 999, 0.1);
    this.jSettingPower.setBlank ();
    this.jSettingsPower_W = new JSlider (0, (int) Math.ceil (getPowerSupplyUnit ().getHardLimitPower_W ()));
    this.jSettingsPower_W.setValue (0);
    this.jSettingsPower_W.setToolTipText ("-");
    this.jSettingsPower_W.setMajorTickSpacing (100);
    this.jSettingsPower_W.setMinorTickSpacing (10);
    this.jSettingsPower_W.setPaintTicks (true);
    this.jSettingsPower_W.setPaintLabels (true);
    setPanelBorder (
      jSettingsPower_W,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesPowerColor (),
      "W");
    this.jSettingsPower_mW = new JSlider (0, 999);
    this.jSettingsPower_mW.setValue (0);
    this.jSettingsPower_mW.setToolTipText ("-");
    this.jSettingsPower_mW.setMajorTickSpacing (100);
    this.jSettingsPower_mW.setMinorTickSpacing (10);
    this.jSettingsPower_mW.setPaintTicks (true);
    this.jSettingsPower_mW.setPaintLabels (true);
    setPanelBorder (
      jSettingsPower_mW,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesPowerColor (),
      "mW");
    if (getPowerSupplyUnit ().isSupportsSetPower ())
    {
      this.jSettingPower.addMouseListener (this.jSettingPowerListener);
      jSettingPowerPanel.add (this.jSettingPower);
      this.jSettingsPower_W.addChangeListener (this.jSettingPowerSliderListener);
      this.jSettingsPower_mW.addChangeListener (this.jSettingPowerSliderListener);
      jSettingPowerPanel.add (this.jSettingsPower_W);
      jSettingPowerPanel.add (this.jSettingsPower_mW);
    }
    else
    {
      jSettingPowerPanel.setLayout (new BorderLayout ());
      final JLabel notSupportedLabel = new JLabel ("Not Supported");
      notSupportedLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
      notSupportedLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jSettingPowerPanel.add (notSupportedLabel, BorderLayout.CENTER);
    }
    add (jSettingPowerPanel);
    //
    final JPanel jReadingVoltage = new JPanel ();
    setPanelBorder (
      jReadingVoltage,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesVoltageColor (),
      "Voltage Reading [V]");
    jReadingVoltage.setLayout (new GridLayout (2, 1));
    this.jReadingVoltage = new JSevenSegmentNumber (getGuiPreferencesVoltageColor (), -999, 999, 0.01);
    this.jReadingVoltage.setBlank ();
    if (getPowerSupplyUnit ().isSupportsReadVoltage ())
      jReadingVoltage.add (this.jReadingVoltage);
    else
    {
      final JLabel notSupportedLabel = new JLabel ("Not Supported");
      notSupportedLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
      notSupportedLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jReadingVoltage.add (notSupportedLabel);
    }
    
    final JPanel jReadingCVAndOuputEnablePanel = new JPanel ();
    setPanelBorder (
      jReadingCVAndOuputEnablePanel,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesVoltageColor (),
      null);
    jReadingCVAndOuputEnablePanel.setLayout (new GridLayout (1, 2));
    
    final JPanel jReadingCVPanel = new JPanel ();
    setPanelBorder (
      jReadingCVPanel,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesVoltageColor (),
      "CV");
    jReadingCVPanel.setLayout (new GridLayout (1, 1));
    this.jReadingCV = new JColorCheckBox.JBoolean (getGuiPreferencesVoltageColor ());
    this.jReadingCV.setAlignmentX (Component.CENTER_ALIGNMENT);
    this.jReadingCV.setHorizontalAlignment (SwingConstants.CENTER);
    jReadingCVPanel.add (this.jReadingCV);
    jReadingCVAndOuputEnablePanel.add (jReadingCVPanel);
    
    final JPanel jOutputEnable_1Panel = new JPanel ();
    setPanelBorder (
      jOutputEnable_1Panel,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesVoltageColor (),
      "Output Enable");
    jOutputEnable_1Panel.setLayout (new GridLayout (1, 1));
    this.jOutputEnable_1 = new JColorCheckBox.JBoolean (getGuiPreferencesVoltageColor ());
    this.jOutputEnable_1.setAlignmentX (Component.CENTER_ALIGNMENT);
    this.jOutputEnable_1.setHorizontalAlignment (SwingConstants.CENTER);
    if (getPowerSupplyUnit ().isSupportsOutputEnable ())
      this.jOutputEnable_1.addMouseListener (this.jOutputEnableMouseListener);
    jOutputEnable_1Panel.add (this.jOutputEnable_1);
    jReadingCVAndOuputEnablePanel.add (jOutputEnable_1Panel);
    
    jReadingVoltage.add (jReadingCVAndOuputEnablePanel);
    //
    add (jReadingVoltage);
    //
    final JPanel jReadingCurrent = new JPanel ();
    setPanelBorder (
      jReadingCurrent,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesCurrentColor (),
      "Current Reading [A]");
    jReadingCurrent.setLayout (new GridLayout (2, 1));
    this.jReadingCurrent = new JSevenSegmentNumber (getGuiPreferencesCurrentColor (), 0, 99, 0.001);
    this.jReadingCurrent.setBlank ();
    if (getPowerSupplyUnit ().isSupportsReadCurrent ())
      jReadingCurrent.add (this.jReadingCurrent);
    else
    {
      final JLabel notSupportedLabel = new JLabel ("Not Supported");
      notSupportedLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
      notSupportedLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jReadingCurrent.add (notSupportedLabel);
    }
    
    final JPanel jReadingCCAndOuputEnablePanel = new JPanel ();
    setPanelBorder (
      jReadingCCAndOuputEnablePanel,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesCurrentColor (),
      null);
    jReadingCCAndOuputEnablePanel.setLayout (new GridLayout (1, 2));
    
    final JPanel jReadingCCPanel = new JPanel ();
    setPanelBorder (
      jReadingCCPanel,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesCurrentColor (),
      "CC");
    jReadingCCPanel.setLayout (new BorderLayout (1, 1));
    this.jReadingCC = new JColorCheckBox.JBoolean (getGuiPreferencesCurrentColor ());
    this.jReadingCC.setAlignmentX (Component.CENTER_ALIGNMENT);
    this.jReadingCC.setHorizontalAlignment (SwingConstants.CENTER);
    jReadingCCPanel.add (this.jReadingCC);
    jReadingCCAndOuputEnablePanel.add (jReadingCCPanel);
    
    final JPanel jOutputEnable_2Panel = new JPanel ();
    setPanelBorder (
      jOutputEnable_2Panel,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesCurrentColor (),
      "Output Enable");
    jOutputEnable_2Panel.setLayout (new GridLayout (1, 1));
    this.jOutputEnable_2 = new JColorCheckBox.JBoolean (getGuiPreferencesCurrentColor ());
    this.jOutputEnable_2.setAlignmentX (Component.CENTER_ALIGNMENT);
    this.jOutputEnable_2.setHorizontalAlignment (SwingConstants.CENTER);
    if (getPowerSupplyUnit ().isSupportsOutputEnable ())
      this.jOutputEnable_2.addMouseListener (this.jOutputEnableMouseListener);
    jOutputEnable_2Panel.add (this.jOutputEnable_2);
    jReadingCCAndOuputEnablePanel.add (jOutputEnable_2Panel);
    
    jReadingCurrent.add (jReadingCCAndOuputEnablePanel);
    add (jReadingCurrent);
    //
    final JPanel jReadingPower = new JPanel ();
    setPanelBorder (
      jReadingPower,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesPowerColor (),
      "Power Reading [W]");
    jReadingPower.setLayout (new GridLayout (2, 1));
    this.jReadingPower = new JSevenSegmentNumber (getGuiPreferencesPowerColor (), 0, 999, 0.1);
    this.jReadingPower.setBlank ();
    if (getPowerSupplyUnit ().isSupportsReadPower ()
      || (getPowerSupplyUnit ().isSupportsReadVoltage () && getPowerSupplyUnit ().isSupportsReadCurrent ()))
      jReadingPower.add (this.jReadingPower);
    else
    {
      final JLabel notSupportedLabel = new JLabel ("Not Supported");
      notSupportedLabel.setAlignmentX (Component.CENTER_ALIGNMENT);
      notSupportedLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jReadingPower.add (notSupportedLabel);
    }
    
    final JPanel jReadingCPAndOuputEnablePanel = new JPanel ();
    setPanelBorder (
      jReadingCPAndOuputEnablePanel,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesPowerColor (),
      null);
    jReadingCPAndOuputEnablePanel.setLayout (new GridLayout (1, 2));
        
    final JPanel jReadingCPPanel = new JPanel ();
    setPanelBorder (
      jReadingCPPanel,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesPowerColor (),
      "CP");
    jReadingCPPanel.setLayout (new BorderLayout (1, 1));
    this.jReadingCP = new JColorCheckBox.JBoolean (getGuiPreferencesPowerColor ());
    this.jReadingCP.setAlignmentX (Component.CENTER_ALIGNMENT);
    this.jReadingCP.setHorizontalAlignment (SwingConstants.CENTER);
    jReadingCPPanel.add (this.jReadingCP);
    jReadingCPAndOuputEnablePanel.add (jReadingCPPanel);
    
    final JPanel jOutputEnable_3Panel = new JPanel ();
    setPanelBorder (
      jOutputEnable_3Panel,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesPowerColor (),
      "Output Enable");
    jOutputEnable_3Panel.setLayout (new GridLayout (1, 1));
    this.jOutputEnable_3 = new JColorCheckBox.JBoolean (getGuiPreferencesPowerColor ());
    this.jOutputEnable_3.setAlignmentX (Component.CENTER_ALIGNMENT);
    this.jOutputEnable_3.setHorizontalAlignment (SwingConstants.CENTER);
    if (getPowerSupplyUnit ().isSupportsOutputEnable ())
      this.jOutputEnable_3.addMouseListener (this.jOutputEnableMouseListener);
    jOutputEnable_3Panel.add (this.jOutputEnable_3);
    jReadingCPAndOuputEnablePanel.add (jOutputEnable_3Panel);
    
    jReadingPower.add (jReadingCPAndOuputEnablePanel);
    add (jReadingPower);
    //
    getPowerSupplyUnit ().addInstrumentListener (this.instrumentListener);
    //
  }
  
  public JDefaultPowerSupplyUnitView (final PowerSupplyUnit powerSupplyUnit)
  {
    this (powerSupplyUnit, 0);
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
      return "Default Power Supply Unit View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof PowerSupplyUnit))
        return new JDefaultPowerSupplyUnitView ((PowerSupplyUnit) instrument);
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
    return JDefaultPowerSupplyUnitView.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // SWING - COMPONENTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected final JPanel jInstrumentSpecificPanel1;
  
  protected final JPanel jInstrumentSpecificPanel2;
  
  private final JSevenSegmentNumber jHardLimitVoltage;
  
  private final JSevenSegmentNumber jHardLimitCurrent;
  
  private final JSevenSegmentNumber jHardLimitPower;
  
  private final JSevenSegmentNumber jSoftLimitVoltage;
  
  private final JSevenSegmentNumber jSoftLimitCurrent;
  
  private final JSevenSegmentNumber jSoftLimitPower;
  
  private final JSevenSegmentNumber jSettingVoltage;
  
  private final JSlider jSettingsVoltage_V;
  
  private final JSlider jSettingsVoltage_mV;
  
  private final JSevenSegmentNumber jSettingCurrent;
  
  private final JSlider jSettingsCurrent_A;
  
  private final JSlider jSettingsCurrent_mA;
  
  private final JSevenSegmentNumber jSettingPower;
  
  private final JSlider jSettingsPower_W;
  
  private final JSlider jSettingsPower_mW;
  
  private final JSevenSegmentNumber jReadingVoltage;
  
  private final JSevenSegmentNumber jReadingCurrent;
  
  private final JSevenSegmentNumber jReadingPower;
  
  private final JColorCheckBox.JBoolean jReadingCV;
  
  private final JColorCheckBox.JBoolean jReadingCC;
  
  private final JColorCheckBox.JBoolean jReadingCP;
  
  private final JColorCheckBox.JBoolean jOutputEnable_1;
  
  private final JColorCheckBox.JBoolean jOutputEnable_2;
  
  private final JColorCheckBox.JBoolean jOutputEnable_3;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING - LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final MouseListener jSoftLimitVoltageListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newVoltage_V = getVoltageFromDialog_V ("Enter Soft-Limit Voltage [V]", null);
      if (newVoltage_V != null)
        try
        {
          JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ().setSoftLimitVoltage_V (newVoltage_V);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting soft-limit voltage on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting soft-limit voltage on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final MouseListener jSoftLimitCurrentListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newCurrent_A = getCurrentFromDialog_A ("Enter Soft-Limit Current [A]", null);
      if (newCurrent_A != null)
        try
        {
          JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ().setSoftLimitCurrent_A (newCurrent_A);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting soft-limit current on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting soft-limit current on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final MouseListener jSoftLimitPowerListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newPower_W = getPowerFromDialog_W ("Enter Soft-Limit Power [W]", null);
      if (newPower_W != null)
        try
        {
          JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ().setSoftLimitPower_W (newPower_W);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting soft-limit power on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting soft-limit power on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final MouseListener jSettingVoltageListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newVoltage_V = getVoltageFromDialog_V ("Enter Set Voltage [V]", null);
      if (newVoltage_V != null)
        try
        {
          JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ().setSetVoltage_V (newVoltage_V);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting set voltage on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting set voltage on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final ChangeListener jSettingVoltageSliderListener = (final ChangeEvent ce) ->
  {
    final JSlider source = (JSlider) ce.getSource ();
    final double settingsVoltage_V =
      JDefaultPowerSupplyUnitView.this.jSettingsVoltage_V.getValue ()
      + 1.0e-3 * JDefaultPowerSupplyUnitView.this.jSettingsVoltage_mV.getValue ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultPowerSupplyUnitView.this.inhibitInstrumentControl)
      {
        try
        {
          JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ().setSetVoltage_V (settingsVoltage_V);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting set voltage from slider to {0} V: {1}.",
            new Object[]{settingsVoltage_V, e});
        }
      }
    }
    else
    {
      JDefaultPowerSupplyUnitView.this.jSettingsVoltage_V.setToolTipText (
        Integer.toString ((int) Math.floor (settingsVoltage_V)));
      JDefaultPowerSupplyUnitView.this.jSettingsVoltage_mV.setToolTipText (
        Integer.toString (Math.floorMod ((int) Math.round (settingsVoltage_V * 1000), 1000)));
    }
  };
    
  private final MouseListener jSettingCurrentListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newCurrent_A = getCurrentFromDialog_A ("Enter Set Current [A]", null);
      if (newCurrent_A != null)
        try
        {
          JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ().setSetCurrent_A (newCurrent_A);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting set current on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting set current on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final ChangeListener jSettingCurrentSliderListener = (final ChangeEvent ce) ->
  {
    final JSlider source = (JSlider) ce.getSource ();
    final double settingsCurrent_A =
      JDefaultPowerSupplyUnitView.this.jSettingsCurrent_A.getValue ()
      + 1.0e-3 * JDefaultPowerSupplyUnitView.this.jSettingsCurrent_mA.getValue ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultPowerSupplyUnitView.this.inhibitInstrumentControl)
      {
        try
        {
          JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ().setSetCurrent_A (settingsCurrent_A);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting set current from slider to {0} A: {1}.",
            new Object[]{settingsCurrent_A, e});
        }
      }
    }
    else
    {
      JDefaultPowerSupplyUnitView.this.jSettingsCurrent_A.setToolTipText (
        Integer.toString ((int) Math.floor (settingsCurrent_A)));
      JDefaultPowerSupplyUnitView.this.jSettingsCurrent_mA.setToolTipText (
        Integer.toString (Math.floorMod ((int) Math.round (settingsCurrent_A * 1000), 1000)));
    }
  };
    
  private final MouseListener jSettingPowerListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newPower_W = getPowerFromDialog_W ("Enter Set Power [W]", null);
      if (newPower_W != null)
        try
        {
          JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ().setSetPower_W (newPower_W);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting set power on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting set power on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final ChangeListener jSettingPowerSliderListener = (final ChangeEvent ce) ->
  {
    final JSlider source = (JSlider) ce.getSource ();
    final double settingsPower_W =
      JDefaultPowerSupplyUnitView.this.jSettingsPower_W.getValue ()
      + 1.0e-3 * JDefaultPowerSupplyUnitView.this.jSettingsPower_mW.getValue ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultPowerSupplyUnitView.this.inhibitInstrumentControl)
      {
        try
        {
          JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ().setSetPower_W (settingsPower_W);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting set power from slider to {0} W: {1}.",
            new Object[]{settingsPower_W, e});
        }
      }
    }
    else
    {
      JDefaultPowerSupplyUnitView.this.jSettingsPower_W.setToolTipText (
        Integer.toString ((int) Math.floor (settingsPower_W)));
      JDefaultPowerSupplyUnitView.this.jSettingsPower_mW.setToolTipText (
        Integer.toString (Math.floorMod ((int) Math.round (settingsPower_W * 1000), 1000)));
    }
  };
    
  private final MouseListener jOutputEnableMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      super.mouseClicked (me);
      final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
      try
      {
        final boolean newValue = ! source.getDisplayedValue ();
        getPowerSupplyUnit ().setOutputEnable (newValue);
      }
      catch (IOException | InterruptedException e)
      {
        LOG.log (Level.INFO, "Caught exception while toggling outputEnable on instrument.");
      }
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final InstrumentListener instrumentListener = new InstrumentListener ()
  {
    
    @Override
    public void newInstrumentStatus (final Instrument instrument, final InstrumentStatus instrumentStatus)
    {
      // EMPTY
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ()|| instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof PowerSupplyUnitSettings))
        throw new IllegalArgumentException ();
      final PowerSupplyUnitSettings settings = (PowerSupplyUnitSettings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JDefaultPowerSupplyUnitView.this.inhibitInstrumentControl = true;
        //
        try
        {
          if (getPowerSupplyUnit ().isSupportsSoftLimitVoltage ())
            JDefaultPowerSupplyUnitView.this.jSoftLimitVoltage.setNumber (settings.getSoftLimitVoltage_V ());
          if (getPowerSupplyUnit ().isSupportsSoftLimitCurrent ())
            JDefaultPowerSupplyUnitView.this.jSoftLimitCurrent.setNumber (settings.getSoftLimitCurrent_A ());
          if (getPowerSupplyUnit ().isSupportsSoftLimitPower ())
            JDefaultPowerSupplyUnitView.this.jSoftLimitPower.setNumber (settings.getSoftLimitPower_W ());
          if (getPowerSupplyUnit ().isSupportsSetVoltage ())
          {
            final double setVoltage_V = settings.getSetVoltage_V ();
            JDefaultPowerSupplyUnitView.this.jSettingVoltage.setNumber (setVoltage_V);
            final int value_V = (int) Math.floor (setVoltage_V);
            JDefaultPowerSupplyUnitView.this.jSettingsVoltage_V.setValue (value_V);
            JDefaultPowerSupplyUnitView.this.jSettingsVoltage_V.setToolTipText (Integer.toString (value_V));
            final int value_mV = Math.floorMod ((int) Math.round (setVoltage_V * 1000), 1000);
            JDefaultPowerSupplyUnitView.this.jSettingsVoltage_mV.setValue (value_mV);
            JDefaultPowerSupplyUnitView.this.jSettingsVoltage_mV.setToolTipText (Integer.toString (value_mV));
          }
          if (getPowerSupplyUnit ().isSupportsSetCurrent ())
          {
            final double setCurrent_A = settings.getSetCurrent_A ();
            JDefaultPowerSupplyUnitView.this.jSettingCurrent.setNumber (setCurrent_A);
            final int value_A = (int) Math.floor (setCurrent_A);
            JDefaultPowerSupplyUnitView.this.jSettingsCurrent_A.setValue (value_A);
            JDefaultPowerSupplyUnitView.this.jSettingsCurrent_A.setToolTipText (Integer.toString (value_A));
            final int value_mA = Math.floorMod ((int) Math.round (setCurrent_A * 1000), 1000);
            JDefaultPowerSupplyUnitView.this.jSettingsCurrent_mA.setValue (value_mA);
            JDefaultPowerSupplyUnitView.this.jSettingsCurrent_mA.setToolTipText (Integer.toString (value_mA));
          }
          if (getPowerSupplyUnit ().isSupportsSetPower ())
          {
            final double setPower_W = settings.getSetPower_W ();
            JDefaultPowerSupplyUnitView.this.jSettingPower.setNumber (setPower_W);
            final int value_W = (int) Math.floor (setPower_W);
            JDefaultPowerSupplyUnitView.this.jSettingsPower_W.setValue (value_W);
            JDefaultPowerSupplyUnitView.this.jSettingsPower_W.setToolTipText (Integer.toString (value_W));
            final int value_mW = Math.floorMod ((int) Math.round (setPower_W * 1000), 1000);
            JDefaultPowerSupplyUnitView.this.jSettingsPower_mW.setValue (value_mW);
            JDefaultPowerSupplyUnitView.this.jSettingsPower_mW.setToolTipText (Integer.toString (value_mW));
          }
          JDefaultPowerSupplyUnitView.this.jOutputEnable_1.setDisplayedValue (settings.isOutputEnable ());
          JDefaultPowerSupplyUnitView.this.jOutputEnable_2.setDisplayedValue (settings.isOutputEnable ());
          JDefaultPowerSupplyUnitView.this.jOutputEnable_3.setDisplayedValue (settings.isOutputEnable ());
        }
        finally
        {
          JDefaultPowerSupplyUnitView.this.inhibitInstrumentControl = false;
        }
      });
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      if (instrument != JDefaultPowerSupplyUnitView.this.getPowerSupplyUnit ()|| instrumentReading == null)
        throw new IllegalArgumentException ();
      if (! (instrumentReading instanceof PowerSupplyUnitReading))
        throw new IllegalArgumentException ();
      final PowerSupplyUnitReading powerSupplyUnitReading = (PowerSupplyUnitReading) instrumentReading;
      SwingUtilities.invokeLater (() ->
      {
        if (getPowerSupplyUnit ().isSupportsReadVoltage ())
          JDefaultPowerSupplyUnitView.this.jReadingVoltage.setNumber (powerSupplyUnitReading.getReadingVoltage_V ());
        if (getPowerSupplyUnit ().isSupportsReadCurrent ())
          JDefaultPowerSupplyUnitView.this.jReadingCurrent.setNumber (powerSupplyUnitReading.getReadingCurrent_A ());
        if (getPowerSupplyUnit ().isSupportsReadPower ())
          JDefaultPowerSupplyUnitView.this.jReadingPower.setNumber (powerSupplyUnitReading.getReadingPower_W ());
        else if (getPowerSupplyUnit ().isSupportsReadVoltage () && getPowerSupplyUnit ().isSupportsReadCurrent ())
          JDefaultPowerSupplyUnitView.this.jReadingPower.setNumber (
            Math.abs (powerSupplyUnitReading.getReadingVoltage_V () * powerSupplyUnitReading.getReadingCurrent_A ()));
        JDefaultPowerSupplyUnitView.this.jReadingCV.setDisplayedValue (
          powerSupplyUnitReading.getPowerSupplyMode () == PowerSupplyUnit.PowerSupplyMode.CV);        
        JDefaultPowerSupplyUnitView.this.jReadingCC.setDisplayedValue (
          powerSupplyUnitReading.getPowerSupplyMode () == PowerSupplyUnit.PowerSupplyMode.CC);        
        JDefaultPowerSupplyUnitView.this.jReadingCP.setDisplayedValue (
          powerSupplyUnitReading.getPowerSupplyMode () == PowerSupplyUnit.PowerSupplyMode.CP);        
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
