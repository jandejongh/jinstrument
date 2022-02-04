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
import javax.swing.JPanel;
import org.javajdj.jinstrument.DigitalMultiMeter;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalMultiMeterPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesManagementColor;
import org.javajdj.jinstrument.swing.cdi.JTinyCDIStatusAndControl;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JDialogCheckBox;

/** A Swing panel for Management Settings of a {@link HP3457A_GPIB_Instrument} Digital Multi Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3457A_GPIB_Management
  extends JDigitalMultiMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3457A_GPIB_Management.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3457A_GPIB_Management (
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
    setLayout (new GridLayout (3, 1));
    
    final JPanel topPanel = new JPanel ();
    add (topPanel);
    final JPanel centerPanel = new JPanel ();
    add (centerPanel);
    final JPanel bottomPanel = new JPanel ();
    add (bottomPanel);
    
    topPanel.setLayout (new GridLayout (1, 3));
    topPanel.add (new JInstrumentPanel (
      hp3457a,
      "Controller/Dev/Inst",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JTinyCDIStatusAndControl (
      digitalMultiMeter.getDevice ().getController (),
      digitalMultiMeter.getDevice (),
      digitalMultiMeter,
      level + 2,
      getGuiPreferencesManagementColor ())));
    topPanel.add (new JHP3457A_GPIB_InstalledOptions (
      digitalMultiMeter,
      "Options",
      level + 2,
      getGuiPreferencesManagementColor ()));
    final JPanel idAddressPanel = new JPanel ();
    topPanel.add (idAddressPanel);
    idAddressPanel.setLayout (new GridLayout (3, 1));
    idAddressPanel.add (new JInstrumentPanel (
      hp3457a,
      "Id",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JString_JTextField (
        "Unknown",
        16,
        "Id",
        (settings) -> ((HP3457A_GPIB_Settings) settings).getId (),
        null,
        true)));
    idAddressPanel.add (new JInstrumentPanel (
      hp3457a,
      "GPIB Address",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JString_JTextField (
        "Unknown",
        16,
        "GPIB Address",
        (settings) -> {
          final Byte address = ((HP3457A_GPIB_Settings) settings).getGpibAddress ();
          return address == null ? "Unknown" : Byte.toString (address);
        },
        null,
        true)));
    idAddressPanel.add (new JInstrumentPanel (
      hp3457a,
      "Calibration Number",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JString_JTextField (
        "Unknown",
        16,
        "Calibration Number",
        (settings) -> ((HP3457A_GPIB_Settings) settings).getCalibrationNumberString (),
        null,
        true)));
    
    centerPanel.setLayout (new GridLayout (1, 1));
    centerPanel.add (new JHP3457A_GPIB_Status (
      digitalMultiMeter,
      "Status",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    bottomPanel.setLayout (new GridLayout (2, 1));

    final JPanel bottomTopPanel = new JPanel ();
    bottomPanel.add (bottomTopPanel);
    final JPanel bottomBottomPanel = new JPanel ();
    bottomPanel.add (bottomBottomPanel);
    
    bottomTopPanel.setLayout (new GridLayout (1, 2));
    
    bottomTopPanel.add (new JHP3457A_GPIB_Calibration (
      digitalMultiMeter,
      "Calibration",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    bottomTopPanel.add (new JHP3457A_GPIB_State (
      digitalMultiMeter,
      "State",
      level + 1,
      getGuiPreferencesManagementColor ()));
    
    bottomBottomPanel.setLayout (new GridLayout (1, 3));
    
    bottomBottomPanel.add (new JInstrumentPanel (
      hp3457a,
      "Internal Test",
      level + 1,
      getGuiPreferencesManagementColor (),
      JCenter.XY (new JVoid_JColorCheckBox (
      "Internal Test",
      hp3457a::internalTest,
      Color.blue))));
    
    bottomBottomPanel.add (new JInstrumentPanel (
      hp3457a,
      "Math",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JDialogCheckBox (
        getBackground ().darker (), 
        "Math",
        new Dimension (800, 600),
        new JHP3457A_GPIB_Math (
          hp3457a,
          "",
          level + 1,
          JInstrumentPanel.getGuiPreferencesManagementColor ()))));
    
    bottomBottomPanel.add (new JInstrumentPanel (
      hp3457a,
      "Subprograms",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JDialogCheckBox (
        getBackground ().darker (),
        "Subprograms",
        new Dimension (800, 600),
        new JHP3457A_GPIB_Subprograms (
          hp3457a,
          "",
          level + 1,
          JInstrumentPanel.getGuiPreferencesManagementColor ()))));
    
  }

  public JHP3457A_GPIB_Management (final HP3457A_GPIB_Instrument digitalMultiMeter, final int level)
  {
    this (digitalMultiMeter, null, level, null);
  }
  
  public JHP3457A_GPIB_Management (final HP3457A_GPIB_Instrument digitalMultiMeter)
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
      return "HP-3457A [GPIB] Management Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3457A_GPIB_Instrument))
        return new JHP3457A_GPIB_Management ((HP3457A_GPIB_Instrument) instrument);
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
    return JHP3457A_GPIB_Management.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
