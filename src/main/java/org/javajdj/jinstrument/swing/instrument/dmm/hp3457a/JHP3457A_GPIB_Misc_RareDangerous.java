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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
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
import org.javajdj.jinstrument.DigitalMultiMeter;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_CalibrationData;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalMultiMeterPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesManagementColor;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JDialogCheckBox;

/** A Swing panel for Rare/Dangerous Settings of a {@link HP3457A_GPIB_Instrument} Digital Multi Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3457A_GPIB_Misc_RareDangerous
  extends JDigitalMultiMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3457A_GPIB_Misc_RareDangerous.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3457A_GPIB_Misc_RareDangerous (
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
    
    setLayout (new GridLayout (6, 1));
    final JPanel servicePanel = new JPanel ();
    add (servicePanel);
    final JPanel maskPanel = new JPanel ();
    add (maskPanel);
    final JPanel linePanel = new JPanel ();
    add (linePanel);
    final JPanel gpibPanel = new JPanel ();
    add (gpibPanel);
    final JPanel instrumentPanel = new JPanel ();
    add (instrumentPanel);
    final JPanel toDoPanel = new JPanel ();
    add (toDoPanel);
    
    servicePanel.setLayout (new GridLayout (1, 4));
    servicePanel.add (new JInstrumentPanel (
      hp3457a,
      "Save Calibration Data",
      level + 1,
      getGuiPreferencesManagementColor (),
      JCenter.XY (new JVoid_JColorCheckBox (
      "Save Calibration Data",
      this::saveCalibrationData,
      Color.blue))));
    servicePanel.add (new JInstrumentPanel (
      hp3457a,
      "Restore Calibration Data",
      level + 1,
      getGuiPreferencesManagementColor (),
      JCenter.XY (new JVoid_JColorCheckBox (
      "Restore Calibration Data",
      this::restoreCalibrationData,
      Color.blue))));
    servicePanel.add (new JInstrumentPanel (
      hp3457a,
      "Peek/Poke RAM",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JDialogCheckBox (
        getBackground ().darker (),
        "Peek/Poke RAM",
        new Dimension (800, 600),
        new JHP3457A_GPIB_PeekPokeRAM (
          hp3457a,
          "",
          level + 1,
          JInstrumentPanel.getGuiPreferencesManagementColor ()))));
    servicePanel.add (new JInstrumentPanel (
      hp3457a,
      "Service",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JDialogCheckBox (
        getBackground ().darker (),
        "Service",
        new Dimension (800, 600),
        new JHP3457A_GPIB_Service (
          hp3457a,
          "",
          level + 1,
          JInstrumentPanel.getGuiPreferencesManagementColor ()))));
    
    maskPanel.setLayout (new GridLayout (1, 2));
    maskPanel.add (new JInstrumentPanel (
      hp3457a,
      "Service Request Mask",
      level + 1,
      getGuiPreferencesManagementColor (),
      new Jbyte_JBitsLong (
        Color.green,
        8,
        Arrays.asList (new String[]{" X ", "RQS", "ERR", "RDY", "PON", " FP", "H/L", "SPC"}),
        true,
        "Service Request Mask",
        (settings) -> ((HP3457A_GPIB_Settings) settings).getServiceRequestMask (),
        hp3457a::setServiceRequestMask,
        true)));
    maskPanel.add (new JInstrumentPanel (
      hp3457a,
      "Error Mask",
      level + 1,
      getGuiPreferencesManagementColor (),
      new Jshort_JBitsLong (
        Color.green,
        11,
        Arrays.asList (new String[]{"AUC", "OOC", "PIG", "PMI", "PRA", "PRU", "COU", "STX", "TTF", "CAL", "HWE"}),
        true,
        "Error Mask",
        (settings) -> ((HP3457A_GPIB_Settings) settings).getErrorMask (),
        hp3457a::setErrorMask,
        true)));
    
    linePanel.setLayout (new GridLayout (1, 2));
    linePanel.add (new JInstrumentPanel (
      hp3457a,
      "Measured Line Frequency [Hz]",
      level + 1,
      getGuiPreferencesFrequencyColor (),
      new Jdouble_JTextField (
        Double.NaN,
        8,
        "Measured Line Frequency [Hz]",
        (settings) -> ((HP3457A_GPIB_Settings) settings).getLineFrequency_Hz (),
        null,
        true)));
    linePanel.add (new JInstrumentPanel (
      hp3457a,
      "Line Frequency Reference [Hz]",
      level + 1,
      getGuiPreferencesFrequencyColor (),
      new Jdouble_JTextField (
        Double.NaN,
        8,
        "Line Frequency Reference [Hz]",
        (settings) -> ((HP3457A_GPIB_Settings) settings).getLineFrequencyReference_Hz (),
        hp3457a::setLineFrequencyReference_Hz,
        true)));
    
    gpibPanel.setLayout (new GridLayout (1, 4));
    gpibPanel.add (new JInstrumentPanel (
      hp3457a,
      "Clear Status Byte",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JVoid_JColorCheckBox (
        "Clear Status Byte",
        hp3457a::clearStatusByte,
        Color.blue)));
    gpibPanel.add (new JInstrumentPanel (
      hp3457a,
      "Fire Service Request",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JVoid_JColorCheckBox (
        "Fire Service Request",
        hp3457a::fireServiceRequest,
        Color.blue)));
    gpibPanel.add (new JInstrumentPanel (
      hp3457a,
      "GPIB Input Buffering",
      level + 1,
      getGuiPreferencesManagementColor (),
      JCenter.XY (new JBoolean_JBoolean (
        "GPIB Input Buffering",
        (settings) -> ((HP3457A_GPIB_Settings) settings).isGpibInputBuffering (),
        hp3457a::setGpibInputBuffering,
        Color.green,
        true))));
    gpibPanel.add (new JInstrumentPanel (
      hp3457a,
      "GPIB EOI",
      level + 1,
      getGuiPreferencesManagementColor (),
      JCenter.XY (new JBoolean_JBoolean (
        "GPIB EOI",
        (settings) -> ((HP3457A_GPIB_Settings) settings).isEoi (),
        hp3457a::setEoi,
        Color.green,
        true))));
    
    instrumentPanel.setLayout (new GridLayout (1, 3));
    instrumentPanel.add (new JInstrumentPanel (
      hp3457a,
      "Display",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JDialogCheckBox (
        getBackground ().darker (),
        "Display",
        new Dimension (800, 600),
        new JHP3457A_GPIB_Display (
          hp3457a,
          "",
          level + 1,
          JInstrumentPanel.getGuiPreferencesManagementColor ()))));
    instrumentPanel.add (new JInstrumentPanel (
      hp3457a,
      "Lock",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JBoolean_JBoolean (
      "Lock",
      (settings) -> ((HP3457A_GPIB_Settings) settings).isLocked (),
      hp3457a::setLocked,
      Color.green,
      true)));
    instrumentPanel.add (new JInstrumentPanel (
      hp3457a,
      "Audio",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JHP3457A_GPIB_Audio (
        digitalMultiMeter,
        null,
        level + 1,
        null)));
        
    toDoPanel.setLayout (new GridLayout (1, 1));
    toDoPanel.add (JCenter.XY (new JLabel ("F10-F58 [acquisition]")));
    
  }

  public JHP3457A_GPIB_Misc_RareDangerous (final HP3457A_GPIB_Instrument digitalMultiMeter, final int level)
  {
    this (digitalMultiMeter, null, level, null);
  }
  
  public JHP3457A_GPIB_Misc_RareDangerous (final HP3457A_GPIB_Instrument digitalMultiMeter)
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
      return "HP-3457A [GPIB] Rare/Dangerous Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3457A_GPIB_Instrument))
        return new JHP3457A_GPIB_Misc_RareDangerous ((HP3457A_GPIB_Instrument) instrument);
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
    return JHP3457A_GPIB_Misc_RareDangerous.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // SAVE CALIBRATION DATA
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private void saveCalibrationData ()
  {
    
    if (JOptionPane.showConfirmDialog (
      null,
      "Read Calibration Data from Instrument " + getInstrument ().getInstrumentUrl () + "?",
      "Read Calibration Data?",
      JOptionPane.YES_NO_OPTION) == 1)
      return;
    
    final JOptionPane pleaseWaitOptionPane
      = new JOptionPane ("Please wait while loading calibration data (this dialog needs work!)...");
    final JDialog pleaseWaitDialog
      = pleaseWaitOptionPane.createDialog ((Frame) null, "Please Wait (this dialog needs work!)...");
    pleaseWaitDialog.setMinimumSize (new Dimension (200, 100));
    pleaseWaitDialog.setModalityType (Dialog.ModalityType.MODELESS);
    pleaseWaitDialog.setVisible (true);
    
    final HP3457A_GPIB_CalibrationData calibrationData;
    try
    {
      calibrationData = ((HP3457A_GPIB_Instrument) getDigitalMultiMeter ()).readCalibrationDataSync ();
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
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RESTORE CALIBRATION DATA
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private void restoreCalibrationData ()
  {
    // XXX
    JOptionPane.showMessageDialog (
      null,
      "Not implemented yet!",
      "Not implemented yet!",
      JOptionPane.ERROR_MESSAGE);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
