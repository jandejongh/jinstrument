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
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesTimeColor;
import org.javajdj.jinstrument.swing.debug.JInstrumentCommandQueueMonitor;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JDialogCheckBox;

/** A Swing panel for Miscellaneous Settings of a {@link HP3457A_GPIB_Instrument} Digital Multi Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3457A_GPIB_Misc
  extends JDigitalMultiMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3457A_GPIB_Misc.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3457A_GPIB_Misc (
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
    
    add (new JInstrumentPanel (
      hp3457a,
      "Memory",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JDialogCheckBox (
        getBackground ().darker (),
        "Memory",
        new Dimension (800, 600),
        new JHP3457A_GPIB_Memory (
          hp3457a,
          "",
          level + 1,
          JInstrumentPanel.getGuiPreferencesManagementColor ()))));
    
    add (new JInstrumentPanel (
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
      
    add (new JInstrumentPanel (
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
      
    add (new JInstrumentPanel (
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
    
    add (new JInstrumentPanel (
      hp3457a,
      "Audio",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JHP3457A_GPIB_Audio (
        digitalMultiMeter,
        null,
        level + 1,
        null)));
        
    add (new JInstrumentPanel (
      hp3457a,
      "Reading Format",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JEnum_JComboBox<> (
        HP3457A_GPIB_Settings.ReadingFormat.class,
        "Reading Format",
        (final InstrumentSettings settings) -> ((HP3457A_GPIB_Settings) settings).getReadingFormat (),
        hp3457a::setReadingFormat,
        true)));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Command Queue Length",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JInstrumentCommandQueueMonitor (hp3457a, level + 1)));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Options Control",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JDialogCheckBox (
        getBackground ().darker (),
        "Options Control",
        new Dimension (800, 600),
        new JHP3457A_GPIB_OptionsControl (
          hp3457a,
          "",
          level + 1,
          JInstrumentPanel.getGuiPreferencesManagementColor ()))));
    
    add (new JInstrumentPanel (
      hp3457a,
      "Rare/Dangerous",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JDialogCheckBox (
        getBackground ().darker (),
        "Rare/Dangerous",
        new Dimension (800, 600),
        new JHP3457A_GPIB_Misc_RareDangerous (
          hp3457a,
          "",
          level + 1,
          JInstrumentPanel.getGuiPreferencesManagementColor ()))));
    
  }

  public JHP3457A_GPIB_Misc (final HP3457A_GPIB_Instrument digitalMultiMeter, final int level)
  {
    this (digitalMultiMeter, null, level, null);
  }
  
  public JHP3457A_GPIB_Misc (final HP3457A_GPIB_Instrument digitalMultiMeter)
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
      return "HP-3457A [GPIB] Miscellaneous Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3457A_GPIB_Instrument))
        return new JHP3457A_GPIB_Misc ((HP3457A_GPIB_Instrument) instrument);
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
    return JHP3457A_GPIB_Misc.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
