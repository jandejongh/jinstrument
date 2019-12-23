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

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.javajdj.jinstrument.Instrument;

/** A Swing (base) component for interacting with an {@link Instrument}.
 * 
 * <p>
 * Contains utility classes and methods, as well as a notion of 'depth' of the panel.
 *
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class JInstrumentPanel
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected JInstrumentPanel (
    final Instrument instrument,
    final String title,
    final int level,
    final Color panelColor)
  {
    super ();
    this.instrument = instrument;
    this.level = level;
    if (title != null)
      setPanelBorder (panelColor, title);
  }

  protected JInstrumentPanel (
    final String title,
    final int level,
    final Color panelColor)
  {
    this (null, title, level, panelColor);
  }
  
  protected JInstrumentPanel (final Instrument instrument, final int level)
  {
    this (instrument, null, level, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // Agrees with InstrumentView.getInstrument...
  
  private final Instrument instrument;
  
  public final Instrument getInstrument ()
  {
    return this.instrument;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LEVEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final int level;
  
  public final int getLevel ()
  {
    return this.level;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PANEL BORDER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static void setPanelBorder (final JComponent panel, final int level, final Color color, final String title)
  {
    if (panel == null)
      throw new IllegalArgumentException ();
    final int lineThickness;
    switch (level)
    {
      case 0: lineThickness = 4; break;
      case 1: lineThickness = 2; break;
      case 2: lineThickness = 1; break;
      case 3: lineThickness = 1; break;
      default: throw new IllegalArgumentException ();
    }
    if (title != null)
    {
      final Border border =
        BorderFactory.createTitledBorder (
          BorderFactory.createLineBorder (color != null ? color : Color.black, lineThickness, true), title);
      panel.setBorder (border);
    }
  }
  
  public final static void setPanelBorder (final JComponent panel, final int level, final String title)
  {
    JInstrumentPanel.setPanelBorder (panel, level, Color.black, title);
  }
  
  public final void setPanelBorder (final Color color, final String title)
  {
    setPanelBorder (this, getLevel (), color, title);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GUI PREFERENCES - COLORS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static Color DEFAULT_MANAGEMENT_COLOR = Color.orange;
  
  public final static Color getGuiPreferencesManagementColor ()
  {
    return JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;
  }
  
  public final static Color DEFAULT_FREQUENCY_COLOR = Color.blue;
  
  public final static Color getGuiPreferencesFrequencyColor ()
  {
    return JInstrumentPanel.DEFAULT_FREQUENCY_COLOR;
  }
  
  public final static Color DEFAULT_AMPLITUDE_COLOR = Color.red;
  
  public final static Color getGuiPreferencesAmplitudeColor ()
  {
    return JInstrumentPanel.DEFAULT_AMPLITUDE_COLOR;
  }
  
  public final static Color DEFAULT_MODULATION_COLOR = Color.green;
   
  public final static Color getGuiPreferencesModulationColor ()
  {
    return JInstrumentPanel.DEFAULT_MODULATION_COLOR;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INHIBIT INSTRUMENT CONTROL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Inhibits backfiring changes to settings on the instrument while updating the Swing GUI.
   * 
   * Only used by the Swing EDT to prevent that Swing components being updated from new instrument settings generate (change)
   * events and (as a result) instrument operations as a result.
   * The invocation of such operations should only result from the user manipulating Swing components.
   * 
   * <p>
   * Since the field is only used from the Swing EDT, it does not have to be volatile.
   * 
   */
  protected boolean inhibitInstrumentControl = false;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING UTILITIES [DIALOGS]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected Double getFrequencyFromDialog_Hz (final String title, final Double startValue_Hz)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue_Hz);
    if (inputString != null)
    {
      final String[] splitString = inputString.split ("\\s+");
      if (splitString == null || splitString.length == 0 || splitString.length > 2)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      final String valueString = splitString[0];
      final String unitString = splitString.length > 1 ? splitString[1] : null;
      final double conversionFactor;
      if (unitString != null)
      {
        if (unitString.equalsIgnoreCase ("Hz"))
        {
          conversionFactor = 1.0;
        }
        else if (unitString.equalsIgnoreCase ("kHz"))
        {
          conversionFactor = 1.0e3;
        }
        else if (unitString.equalsIgnoreCase ("MHz"))
        {
          conversionFactor = 1.0e6;
        }
        else if (unitString.equalsIgnoreCase ("GHz"))
        {
          conversionFactor = 1.0e9;
        }
        else
        {
          JOptionPane.showMessageDialog (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
          return null;
        }
      }
      else
      {
        conversionFactor = 1.0;
      }
      final double value_Hz;
      try
      {
        value_Hz = Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value_Hz * conversionFactor;
    }
    else
      return null;
  }
  
  protected Double getFrequencyFromDialog_MHz (final String title, final Double startValue_MHz)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue_MHz);
    if (inputString != null)
    {
      final String[] splitString = inputString.split ("\\s+");
      if (splitString == null || splitString.length == 0 || splitString.length > 2)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      final String valueString = splitString[0];
      final String unitString = splitString.length > 1 ? splitString[1] : null;
      final double conversionFactor;
      if (unitString != null)
      {
        if (unitString.equalsIgnoreCase ("Hz"))
        {
          conversionFactor = 1.0e-6;
        }
        else if (unitString.equalsIgnoreCase ("kHz"))
        {
          conversionFactor = 1.0e-3;
        }
        else if (unitString.equalsIgnoreCase ("MHz"))
        {
          conversionFactor = 1.0;
        }
        else if (unitString.equalsIgnoreCase ("GHz"))
        {
          conversionFactor = 1.0e3;
        }
        else
        {
          JOptionPane.showMessageDialog (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
          return null;
        }
      }
      else
      {
        conversionFactor = 1.0;
      }
      final double value_MHz;
      try
      {
        value_MHz = Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value_MHz * conversionFactor;
    }
    else
      return null;
  }
  
  protected Double getPeriodFromDialog_s (final String title, final Double startValue_s)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue_s);
    if (inputString != null)
    {
      final String[] splitString = inputString.split ("\\s+");
      if (splitString == null || splitString.length == 0 || splitString.length > 2)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      final String valueString = splitString[0];
      final String unitString = splitString.length > 1 ? splitString[1] : null;
      final double conversionFactor;
      if (unitString != null)
      {
        if (unitString.equalsIgnoreCase ("s"))
        {
          conversionFactor = 1.0;
        }
        else if (unitString.equalsIgnoreCase ("ms"))
        {
          conversionFactor = 1.0e-3;
        }
        else if (unitString.equalsIgnoreCase ("us"))
        {
          conversionFactor = 1.0-6;
        }
        else if (unitString.equalsIgnoreCase ("ns"))
        {
          conversionFactor = 1.0-9;
        }
        else if (unitString.equalsIgnoreCase ("ps"))
        {
          conversionFactor = 1.0-12;
        }
        else
        {
          JOptionPane.showMessageDialog (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
          return null;
        }
      }
      else
      {
        conversionFactor = 1.0;
      }
      final double value_s;
      try
      {
        value_s = Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value_s * conversionFactor;
    }
    else
      return null;
  }
  
  protected Double getPowerFromDialog_dBm (final String title, final Double startValue_dBm)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue_dBm);
    if (inputString != null)
    {
      final String[] splitString = inputString.split ("\\s+");
      if (splitString == null || splitString.length == 0 || splitString.length > 2)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      final String valueString = splitString[0];
      final String unitString = splitString.length > 1 ? splitString[1] : null;
      final double conversionFactor;
      if (unitString != null)
      {
        if (unitString.equalsIgnoreCase ("dBm"))
        {
          conversionFactor = 1.0;
        }
        else
        {
          JOptionPane.showMessageDialog (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
          return null;
        }
      }
      else
      {
        conversionFactor = 1.0;
      }
      final double value_dBm;
      try
      {
        value_dBm = Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value_dBm * conversionFactor;
    }
    else
      return null;
  }
  
  protected Double getPowerRatioFromDialog_dB (final String title, final Double startValue_dB)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue_dB);
    if (inputString != null)
    {
      final String[] splitString = inputString.split ("\\s+");
      if (splitString == null || splitString.length == 0 || splitString.length > 2)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      final String valueString = splitString[0];
      final String unitString = splitString.length > 1 ? splitString[1] : null;
      final double conversionFactor;
      if (unitString != null)
      {
        if (unitString.equalsIgnoreCase ("dB"))
        {
          conversionFactor = 1.0;
        }
        else
        {
          JOptionPane.showMessageDialog (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
          return null;
        }
      }
      else
      {
        conversionFactor = 1.0;
      }
      final double value_dB;
      try
      {
        value_dB = Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value_dB * conversionFactor;
    }
    else
      return null;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
