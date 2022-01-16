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
package org.javajdj.jinstrument.swing.instrument.dmm.hp3478a;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dmm.hp3478a.HP3478A_GPIB_CalibrationData;
import org.javajdj.jinstrument.gpib.dmm.hp3478a.HP3478A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dmm.hp3478a.HP3478A_GPIB_Settings;
import org.javajdj.jinstrument.gpib.dmm.hp3478a.HP3478A_GPIB_Status;
import org.javajdj.jinstrument.swing.default_view.JDefaultDigitalMultiMeterView;
import org.javajdj.jswing.jbyte.JByte;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for (complete) control and status of a {@link HP3478A_GPIB_Instrument} Digital MultiMeter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3478A_GPIB
  extends JDefaultDigitalMultiMeterView
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3478A_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3478A_GPIB (final HP3478A_GPIB_Instrument digitalMultiMeter, final int level)
  {
    super (digitalMultiMeter, level);
    this.jInstrumentSpecificPanel.removeAll ();
    setPanelBorder (this.jInstrumentSpecificPanel, level + 1, DEFAULT_MANAGEMENT_COLOR, "HP-3478A Specific");
    this.jInstrumentSpecificPanel.setLayout (new GridLayout (5, 2));
    //
    final JPanel jSerialPollStatusPanel = new JPanel ();
    setPanelBorder (jSerialPollStatusPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "Serial Poll Status [Read-Only]");
    jSerialPollStatusPanel.setLayout (new GridLayout (2, 1));
    this.jSerialPollStatus = new JByte ();
    jSerialPollStatusPanel.add (this.jSerialPollStatus);
    final JPanel jSerialPollTags = new JPanel ();
    jSerialPollTags.setLayout (new GridLayout (1, 8));
    jSerialPollTags.add (new JLabel ("POR"));
    jSerialPollTags.add (new JLabel ("SRQ"));
    jSerialPollTags.add (new JLabel ("CALF"));
    jSerialPollTags.add (new JLabel ("FSRQ"));
    jSerialPollTags.add (new JLabel ("HWE"));
    jSerialPollTags.add (new JLabel ("STX"));
    jSerialPollTags.add (new JLabel ("NU"));
    jSerialPollTags.add (new JLabel ("DRDY"));
    jSerialPollStatusPanel.add (jSerialPollTags);
    this.jInstrumentSpecificPanel.add (jSerialPollStatusPanel);
    //
    final JPanel jCalibrationPanel = new JPanel ();
    jCalibrationPanel.setLayout (new GridLayout (1, 2));
    setPanelBorder (jCalibrationPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "Calibration");
    final JPanel jCalibrationSavePanel = new JPanel ();
    jCalibrationSavePanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (jCalibrationSavePanel, level + 3, Color.black, "Save");
    final JColorCheckBox jCalibrationSaveButton = new JColorCheckBox ((t) -> Color.blue);
    jCalibrationSaveButton.addActionListener (this.jCalibrationSaveListener);
    jCalibrationSavePanel.add (jCalibrationSaveButton);
    jCalibrationPanel.add (jCalibrationSavePanel);
    final JPanel jCalibrationRestorePanel = new JPanel ();
    jCalibrationRestorePanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (jCalibrationRestorePanel, level + 3, Color.black, "Restore");
    final JColorCheckBox jCalibrationRestoreButton = new JColorCheckBox ((t) -> Color.blue);
    jCalibrationRestorePanel.add (jCalibrationRestoreButton);
    jCalibrationPanel.add (jCalibrationRestorePanel);
    this.jInstrumentSpecificPanel.add (jCalibrationPanel);
    //
    final JPanel jAutoZeroPanel = new JPanel ();
    jAutoZeroPanel.setLayout (new GridLayout (1, 1));
    this.jAutoZero = new JColorCheckBox<> ((t) -> (t != null && t) ? Color.red : null);
    this.jAutoZero.addActionListener (this.jAutoZeroListener);
    jAutoZeroPanel.add (this.jAutoZero);
    setPanelBorder (jAutoZeroPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "Auto Zero");
    this.jInstrumentSpecificPanel.add (jAutoZeroPanel);
    //
    final JPanel jErrorPanel = new JPanel ();
    jErrorPanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (jErrorPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "Error Byte [Read-Only]");    
    this.jError = new JLabel ("Unknown");
    jErrorPanel.add (this.jError);
    this.jInstrumentSpecificPanel.add (jErrorPanel);
    //
    final JPanel jTriggerPanel = new JPanel ();
    jTriggerPanel.setLayout (new GridLayout (1, 2));
    setPanelBorder (jTriggerPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "Triggering");
    final JPanel jInternalTriggerEnabledPanel = new JPanel ();
    jInternalTriggerEnabledPanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (jInternalTriggerEnabledPanel, level + 3, Color.black, "Internal");
    this.jInternalTriggerEnabled = new JColorCheckBox<> ((t) -> (t != null && t) ? Color.red : null);
    this.jInternalTriggerEnabled.addActionListener (this.jInternalTriggerEnabledListener);
    jInternalTriggerEnabledPanel.add (this.jInternalTriggerEnabled);
    jTriggerPanel.add (jInternalTriggerEnabledPanel);
    final JPanel jExternalTriggerEnabledPanel = new JPanel ();
    jExternalTriggerEnabledPanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (jExternalTriggerEnabledPanel, level + 3, Color.black, "External");
    this.jExternalTriggerEnabled = new JColorCheckBox<> ((t) -> (t != null && t) ? Color.red : null);
    this.jExternalTriggerEnabled.addActionListener (this.jExternalTriggerEnabledListener);
    jExternalTriggerEnabledPanel.add (this.jExternalTriggerEnabled);
    jTriggerPanel.add (jExternalTriggerEnabledPanel);
    this.jInstrumentSpecificPanel.add (jTriggerPanel);
    //
    final JPanel j50_60HzPanel = new JPanel ();
    j50_60HzPanel.setLayout (new GridLayout (1, 2));
    setPanelBorder (j50_60HzPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "50/60 Hz [Read-Only]");
    final JPanel j50HzPanel = new JPanel ();
    j50HzPanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (j50HzPanel, level + 3, Color.black, "50 Hz");
    this.j50Hz = new JColorCheckBox<> ((t) -> (t != null && t) ? Color.red : null);
    j50HzPanel.add (this.j50Hz);
    j50_60HzPanel.add (j50HzPanel);
    final JPanel j60HzdPanel = new JPanel ();
    j60HzdPanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (j60HzdPanel, level + 3, Color.black, "60 Hz");
    this.j60Hz = new JColorCheckBox<> ((t) -> (t != null && t) ? Color.red : null);
    j60HzdPanel.add (this.j60Hz);
    j50_60HzPanel.add (j60HzdPanel);
    this.jInstrumentSpecificPanel.add (j50_60HzPanel);
    //
    final JPanel jFrontRearPanel = new JPanel ();
    jFrontRearPanel.setLayout (new GridLayout (1, 2));
    setPanelBorder (jFrontRearPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "Input [Read-Only]");
    final JPanel jFrontEnabledPanel = new JPanel ();
    jFrontEnabledPanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (jFrontEnabledPanel, level + 3, Color.black, "Front");
    this.jFrontEnabled = new JColorCheckBox<> ((t) -> (t != null && t) ? Color.red : null);
    jFrontEnabledPanel.add (this.jFrontEnabled);
    jFrontRearPanel.add (jFrontEnabledPanel);
    final JPanel jRearEnabledPanel = new JPanel ();
    jRearEnabledPanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (jRearEnabledPanel, level + 3, Color.black, "Rear");
    this.jRearEnabled = new JColorCheckBox<> ((t) -> (t != null && t) ? Color.red : null);
    jRearEnabledPanel.add (this.jRearEnabled);
    jFrontRearPanel.add (jRearEnabledPanel);
    this.jInstrumentSpecificPanel.add (jFrontRearPanel);
    //
    final JPanel jSerialPollMaskPanel = new JPanel ();
    jSerialPollMaskPanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (jSerialPollMaskPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "Serial Poll Mask (SRQ) [Read-Only]");    
    this.jSerialPollMask = new JLabel ("Unknown");
    jSerialPollMaskPanel.add (this.jSerialPollMask);
    this.jInstrumentSpecificPanel.add (jSerialPollMaskPanel);
    //
    final JPanel jCalRamEnabledPanel = new JPanel ();
    jCalRamEnabledPanel.setLayout (new GridLayout (1, 1));
    this.jCalRamEnabled = new JColorCheckBox<> ((t) -> (t != null && t) ? Color.red : null);
    jCalRamEnabledPanel.add (this.jCalRamEnabled);
    setPanelBorder (jCalRamEnabledPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "Calibration RAM Enabled [Read-Only]");
    this.jInstrumentSpecificPanel.add (jCalRamEnabledPanel);
    //
    final JPanel jDacSettingsPanel = new JPanel ();
    jDacSettingsPanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (jDacSettingsPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "Internal DAC Settings [Read-Only]");    
    this.jDacSettings = new JLabel ("Unknown");
    jDacSettingsPanel.add (this.jDacSettings);
    this.jInstrumentSpecificPanel.add (jDacSettingsPanel);
    //
    getDigitalMultiMeter ().addInstrumentListener (this.instrumentListener);
  }
  
  public JHP3478A_GPIB (final HP3478A_GPIB_Instrument digitalMultiMeter)
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
      return "HP-3478AA [GPIB] DMM View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3478A_GPIB_Instrument))
        return new JHP3478A_GPIB ((HP3478A_GPIB_Instrument) instrument);
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
    return JHP3478A_GPIB.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JByte jSerialPollStatus;
  
  private final ActionListener jCalibrationSaveListener = (final ActionEvent ae) ->
  {
    if (JOptionPane.showConfirmDialog (
      null,
      "Read Calibration Data from Instrument " + getInstrument ().getInstrumentUrl () + "?",
      "Reaed Calibration Data?",
      JOptionPane.YES_NO_OPTION) == 1)
      return;
    final JOptionPane pleaseWaitOptionPane
      = new JOptionPane ("Please wait while loading calibration data (this dialog needs work!)...");
    final JDialog pleaseWaitDialog
      = pleaseWaitOptionPane.createDialog ((Frame) null, "Please Wait (this dialog needs work!)...");
    pleaseWaitDialog.setMinimumSize (new Dimension (200, 100));
    pleaseWaitDialog.setModalityType (Dialog.ModalityType.MODELESS);
    pleaseWaitDialog.setVisible (true);
    
    final HP3478A_GPIB_CalibrationData calibrationData;
    try
    {
      calibrationData = ((HP3478A_GPIB_Instrument) getDigitalMultiMeter ()).readCalibrationDataSync ();
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while reading calibration data on instrument {0}: {1}.",        
        new Object[]{getInstrument (), Arrays.toString (e.getStackTrace ())});
      pleaseWaitDialog.setVisible (false);
      pleaseWaitDialog.dispose ();
      JOptionPane.showMessageDialog (null,
        "Reading calibration data failed: " + e.toString () + "; check log for details!",
        "Error",
        JOptionPane.ERROR_MESSAGE);
      return;
    }
    pleaseWaitDialog.setVisible (false);
    pleaseWaitDialog.dispose ();
    final JFileChooser jFileChooser = new JFileChooser ((File) null);
    if (jFileChooser.showSaveDialog (this) != JFileChooser.APPROVE_OPTION)
      return;      
    final File file = jFileChooser.getSelectedFile ();
    if (file == null)
    {
      JOptionPane.showMessageDialog (null, "No file? (Please report this error!)", "Error", JOptionPane.ERROR_MESSAGE);
      return;      
    }
    if (file.exists () && JOptionPane.showConfirmDialog (null, "Overwrite?", "Overwrite file?", JOptionPane.YES_NO_OPTION) != 0)
      return;
    try
    {
      Files.write (file.toPath (), calibrationData.getBytes ());
    }
    catch (IOException ioe)
    {
      JOptionPane.showMessageDialog (null, "I/O Exception!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
  };
  
  private final JColorCheckBox<Boolean> jAutoZero;
  
  private final ActionListener jAutoZeroListener = (final ActionEvent ae) ->
  {
    final Boolean currentValue = JHP3478A_GPIB.this.jAutoZero.getDisplayedValue ();
    final boolean newValue = currentValue == null || (! currentValue);
    try
    {
      ((HP3478A_GPIB_Instrument) getDigitalMultiMeter ()).setAutoZero (newValue);
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while setting auto zero {0} on instrument {1}: {2}.",        
        new Object[]
          {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
  private final JColorCheckBox<Boolean> jInternalTriggerEnabled;
  
  private final ActionListener jInternalTriggerEnabledListener = (final ActionEvent ae) ->
  {
    try
    {
      ((HP3478A_GPIB_Instrument) getDigitalMultiMeter ()).setTriggerMode (
        HP3478A_GPIB_Instrument.HP3478ATriggerMode.T1_INTERNAL_TRIGGER);
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while setting trigger {0} on instrument {1}: {2}.",        
        new Object[]
          {HP3478A_GPIB_Instrument.HP3478ATriggerMode.T1_INTERNAL_TRIGGER,
           getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
  private final JColorCheckBox<Boolean> jExternalTriggerEnabled;
  
  private final ActionListener jExternalTriggerEnabledListener = (final ActionEvent ae) ->
  {
    try
    {
      ((HP3478A_GPIB_Instrument) getDigitalMultiMeter ()).setTriggerMode (
        HP3478A_GPIB_Instrument.HP3478ATriggerMode.T2_EXTERNAL_TRIGGER);
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while setting trigger {0} on instrument {1}: {2}.",        
        new Object[]
          {HP3478A_GPIB_Instrument.HP3478ATriggerMode.T2_EXTERNAL_TRIGGER,
           getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
  private final JColorCheckBox<Boolean> jCalRamEnabled;
  
  private final JColorCheckBox<Boolean> jFrontEnabled;
  
  private final JColorCheckBox<Boolean> jRearEnabled;
  
  private final JColorCheckBox<Boolean> j50Hz;
  
  private final JColorCheckBox<Boolean> j60Hz;
  
  private final JLabel jSerialPollMask;
  
  private final JLabel jError;
  
  private final JLabel jDacSettings;
  
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
      if (instrument != JHP3478A_GPIB.this.getDigitalMultiMeter () || instrumentStatus == null)
        throw new IllegalArgumentException ();
      if (! (instrumentStatus instanceof HP3478A_GPIB_Status))
        throw new IllegalArgumentException ();
      SwingUtilities.invokeLater (() ->
      {
        JHP3478A_GPIB.this.jSerialPollStatus.setDisplayedValue (
          ((HP3478A_GPIB_Status) instrumentStatus).getSerialPollStatusByte ());
      });
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JHP3478A_GPIB.this.getDigitalMultiMeter () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof HP3478A_GPIB_Settings))
        throw new IllegalArgumentException ();
      final HP3478A_GPIB_Settings settings = (HP3478A_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JHP3478A_GPIB.this.inhibitInstrumentControl = true;
        //
        try
        {
          JHP3478A_GPIB.this.jAutoZero.setDisplayedValue (settings.isAutoZero ());
          JHP3478A_GPIB.this.jInternalTriggerEnabled.setDisplayedValue (settings.isInternalTriggerEnabled ());
          JHP3478A_GPIB.this.jExternalTriggerEnabled.setDisplayedValue (settings.isExternalTriggerEnabled ());
          JHP3478A_GPIB.this.jCalRamEnabled.setDisplayedValue (settings.isCalRamEnabled ());
          JHP3478A_GPIB.this.jFrontEnabled.setDisplayedValue (settings.isFrontOperation ());
          JHP3478A_GPIB.this.jRearEnabled.setDisplayedValue (settings.isRearOperation ());
          JHP3478A_GPIB.this.j50Hz.setDisplayedValue (settings.is50HzOperation ());
          JHP3478A_GPIB.this.j60Hz.setDisplayedValue (settings.is60HzOperation ());
          JHP3478A_GPIB.this.jSerialPollMask.setText (
            "0x" + Integer.toHexString (Byte.toUnsignedInt (settings.getSerialPollMaskByte ())));
          JHP3478A_GPIB.this.jError.setText (
            "0x" + Integer.toHexString (Byte.toUnsignedInt (settings.getErrorByte ())));
          JHP3478A_GPIB.this.jDacSettings.setText (
            "0x" + Integer.toHexString (Byte.toUnsignedInt (settings.getDacSettingsByte ())));
        }
        finally
        {
          JHP3478A_GPIB.this.inhibitInstrumentControl = false;
        }
      });
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      // EMPTY
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
