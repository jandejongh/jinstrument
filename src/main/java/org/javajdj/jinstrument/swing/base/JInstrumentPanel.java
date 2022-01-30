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
package org.javajdj.jinstrument.swing.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.javajdj.jinstrument.DefaultInstrumentListener;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jswing.jbyte.JBitsLong;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jtextfieldlistener.JTextFieldListener;

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
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JInstrumentPanel.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JInstrumentPanel (
    final Instrument instrument,
    final String title,
    final int level,
    final Color panelColor,
    final JComponent component)
  {
    super ();
    this.instrument = instrument;
    this.title = title;
    this.level = level;
    if (this.title != null)
      setPanelBorder (panelColor, this.title);
    if (component != null)
    {
      setLayout (new GridLayout (1, 1));
      add (JCenter.XY (component));
    }
  }

  public JInstrumentPanel (
    final Instrument instrument,
    final String title,
    final int level,
    final Color panelColor)
  {
    this (instrument, title, level, panelColor, null);
  }
  
  public JInstrumentPanel (
    final String title,
    final int level,
    final Color panelColor)
  {
    this (null, title, level, panelColor);
  }
  
  public JInstrumentPanel (final Instrument instrument, final int level)
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
  // TITLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final String title;
  
  /** Returns the title of this panel.
   * 
   * <p>
   * The title, if non-{@code null}, is shown in a dedicated panel border.
   * 
   * @return The title, may be {@code null}.
   * 
   */
  public final String getTitle ()
  {
    return this.title;
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
      case 4: lineThickness = 1; break;
      case 5: lineThickness = 1; break;
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

  public final static Color DEFAULT_UPDATE_PENDING_COLOR = Color.red;
  
  public final static Color getGuiPreferencesUpdatePendingColor ()
  {
    return JInstrumentPanel.DEFAULT_UPDATE_PENDING_COLOR;
  }
  
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
  
  public final static Color DEFAULT_PHASE_COLOR = new Color (20, 164, 164);
  
  public final static Color getGuiPreferencesPhaseColor ()
  {
    return JInstrumentPanel.DEFAULT_PHASE_COLOR;
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
  
  public final static Color DEFAULT_DC_COLOR = Color.yellow;
  
  public final static Color getGuiPreferencesDCColor ()
  {
    return JInstrumentPanel.DEFAULT_DC_COLOR;
  }
  
  public final static Color DEFAULT_TRIGGER_COLOR = Color.pink;
  
  public final static Color getGuiPreferencesTriggerColor ()
  {
    return JInstrumentPanel.DEFAULT_TRIGGER_COLOR;
  }
  
  public final static Color DEFAULT_VOLTAGE_COLOR = Color.red;
  
  public final static Color getGuiPreferencesVoltageColor ()
  {
    return JInstrumentPanel.DEFAULT_VOLTAGE_COLOR;
  }
  
  public final static Color DEFAULT_CURRENT_COLOR = Color.green;
  
  public final static Color getGuiPreferencesCurrentColor ()
  {
    return JInstrumentPanel.DEFAULT_CURRENT_COLOR;
  }
  
  public final static Color DEFAULT_POWER_COLOR = Color.blue;
  
  public final static Color getGuiPreferencesPowerColor ()
  {
    return JInstrumentPanel.DEFAULT_POWER_COLOR;
  }
  
  public final static Color DEFAULT_TIME_COLOR = Color.magenta;
  
  public final static Color getGuiPreferencesTimeColor ()
  {
    return JInstrumentPanel.DEFAULT_TIME_COLOR;
  }
  
  public final static Color DEFAULT_IMPEDANCE_COLOR =  new Color (164, 20, 164);
  
  public final static Color getGuiPreferencesImpedanceColor ()
  {
    return JInstrumentPanel.DEFAULT_IMPEDANCE_COLOR;
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
  
  public final boolean isInhibitInstrumentControl ()
  {
    return this.inhibitInstrumentControl;
  }
  
  protected final void setInhibitInstrumentControl (final boolean inhibitInstrumentControl)
  {
    this.inhibitInstrumentControl = inhibitInstrumentControl;
  }
  
  protected final void setInhibitInstrumentControl ()
  {
    setInhibitInstrumentControl (true);
  }
  
  protected final void resetInhibitInstrumentControl ()
  {
    setInhibitInstrumentControl (false);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING UTILITIES [DIALOGS]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected Integer getIntegerFromDialog (final String title, final Integer startValue)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue);
    if (inputString != null)
    {
      final int value;
      try
      {
        value = Integer.parseInt (inputString.trim ());
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (
          null,
          "Illegal value " + inputString.trim () + "!",
          "Illegal Input",
          JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value;
    }
    else
      return null;
  }
  
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
        if (unitString.trim ().equalsIgnoreCase ("Hz"))
        {
          conversionFactor = 1.0;
        }
        else if (unitString.trim ().equalsIgnoreCase ("kHz"))
        {
          conversionFactor = 1.0e3;
        }
        else if (unitString.trim ().equalsIgnoreCase ("MHz"))
        {
          conversionFactor = 1.0e6;
        }
        else if (unitString.trim ().equalsIgnoreCase ("GHz"))
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
        if (unitString.trim ().equalsIgnoreCase ("Hz"))
        {
          conversionFactor = 1.0e-6;
        }
        else if (unitString.trim ().equalsIgnoreCase ("kHz"))
        {
          conversionFactor = 1.0e-3;
        }
        else if (unitString.trim ().equalsIgnoreCase ("MHz"))
        {
          conversionFactor = 1.0;
        }
        else if (unitString.trim ().equalsIgnoreCase ("GHz"))
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
        if (unitString.trim ().equalsIgnoreCase ("s"))
        {
          conversionFactor = 1.0;
        }
        else if (unitString.trim ().equalsIgnoreCase ("ms"))
        {
          conversionFactor = 1.0e-3;
        }
        else if (unitString.trim ().equalsIgnoreCase ("us"))
        {
          conversionFactor = 1.0e-6;
        }
        else if (unitString.trim ().equalsIgnoreCase ("ns"))
        {
          conversionFactor = 1.0e-9;
        }
        else if (unitString.trim ().equalsIgnoreCase ("ps"))
        {
          conversionFactor = 1.0e-12;
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
        if (unitString.trim ().equalsIgnoreCase ("dBm"))
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
        if (unitString.trim ().equalsIgnoreCase ("dB"))
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
  
  protected Double getVoltageFromDialog_V (final String title, final Double startValue_V)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue_V);
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
        if (unitString.trim ().equalsIgnoreCase ("uV"))
        {
          conversionFactor = 1e-6;
        }
        else if (unitString.trim ().equalsIgnoreCase ("mV"))
        {
          conversionFactor = 1e-3;
        }
        else if (unitString.trim ().equalsIgnoreCase ("V"))
        {
          conversionFactor = 1;
        }
        else if (unitString.trim ().equalsIgnoreCase ("kV"))
        {
          conversionFactor = 1e3;
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
      final double value;
      try
      {
        value = Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value * conversionFactor;
    }
    else
      return null;
  }
  
  protected Double getCurrentFromDialog_A (final String title, final Double startValue_A)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue_A);
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
        if (unitString.trim ().equalsIgnoreCase ("pA"))
        {
          conversionFactor = 1e-12;
        }
        else if (unitString.trim ().equalsIgnoreCase ("nA"))
        {
          conversionFactor = 1e-9;
        }
        else if (unitString.trim ().equalsIgnoreCase ("uA"))
        {
          conversionFactor = 1e-6;
        }
        else if (unitString.trim ().equalsIgnoreCase ("mA"))
        {
          conversionFactor = 1e-3;
        }
        else if (unitString.trim ().equalsIgnoreCase ("A"))
        {
          conversionFactor = 1;
        }
        else if (unitString.trim ().equalsIgnoreCase ("kA"))
        {
          conversionFactor = 1e3;
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
      final double value;
      try
      {
        value = Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value * conversionFactor;
    }
    else
      return null;
  }
  
  protected Double getPowerFromDialog_W (final String title, final Double startValue_W)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue_W);
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
        if (unitString.trim ().equalsIgnoreCase ("pW"))
        {
          conversionFactor = 1e-12;
        }
        else if (unitString.trim ().equalsIgnoreCase ("nW"))
        {
          conversionFactor = 1e-9;
        }
        else if (unitString.trim ().equalsIgnoreCase ("uW"))
        {
          conversionFactor = 1e-6;
        }
        else if (unitString.trim ().equalsIgnoreCase ("mW"))
        {
          conversionFactor = 1e-3;
        }
        else if (unitString.trim ().equalsIgnoreCase ("W"))
        {
          conversionFactor = 1;
        }
        else if (unitString.trim ().equalsIgnoreCase ("kW"))
        {
          conversionFactor = 1e3;
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
      final double value;
      try
      {
        value = Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value * conversionFactor;
    }
    else
      return null;
  }
  
  protected Double getDutyCycleFromDialog_percent (final String title, final Double startValue_percent)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue_percent);
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
        if (unitString.trim ().equalsIgnoreCase ("%"))
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
      final double value_percent;
      try
      {
        value_percent = Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value_percent * conversionFactor;
    }
    else
      return null;
  }
  
  protected Double getPhaseFromDialog_degrees (final String title, final Double startValue_degrees)
  {
    final String inputString = JOptionPane.showInputDialog (title, startValue_degrees);
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
        if (unitString.trim ().equalsIgnoreCase ("deg")
          || unitString.trim ().equalsIgnoreCase ("degrees"))
        {
          conversionFactor = 1.0;
        }
        else if (unitString.trim ().equalsIgnoreCase ("rad")
          || unitString.trim ().equalsIgnoreCase ("radians"))
        {
          conversionFactor = 360.0 / (2.0 * Math.PI);
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
      final double value;
      try
      {
        value = Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        JOptionPane.showMessageDialog (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      return value * conversionFactor;
    }
    else
      return null;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // [STANDARD] LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // XXX Is this really unavailable in Java 8?
  
  @FunctionalInterface
  public interface Action
  {
    void apply ();
  }
  
  // XXX Is this really unavailable in Java 8?
  
  @FunctionalInterface
  public interface Provider<T>
  {
    T apply ();
  }
  
  public class JActionButtonListener
    implements ActionListener
  {
    
    private final Action action;
    
    private final Provider<Boolean> inhibitAction;
    
    public JActionButtonListener (
      final Action action,
      final Provider<Boolean> inhibitAction)
    {
      this.action = action;
      this.inhibitAction = inhibitAction;
    }
    
    public JActionButtonListener (
      final Action action)
    {
      this (action, null);
    }
    
    public JActionButtonListener (
      final JDialog jDialog,
      final Provider<Boolean> inhibitAction)
    {
      this (() -> { if (jDialog != null) jDialog.setVisible (true); },
      inhibitAction);
    }
    
    public JActionButtonListener (
      final JDialog jDialog)
    {
      this (jDialog, null);
    }
    
    @Override
    public void actionPerformed (final ActionEvent ae)
    {
      if (this.inhibitAction != null && this.inhibitAction.apply ())
        return;
      if (this.action != null)
        this.action.apply ();
    }
    
  }
  
  public class JInstrumentActionListener_0
    implements ActionListener
  {

    private final String actionString;
    
    private final Instrument.InstrumentAction action;
    
    private final Provider<Boolean> inhibitAction;
    
    public JInstrumentActionListener_0 (
      final String actionString,
      final Instrument.InstrumentAction action,
      final Provider<Boolean> inhibitAction)
    {
      if (actionString == null || actionString.trim ().isEmpty () || action == null)
        throw new IllegalArgumentException ();
      this.actionString = actionString;
      this.action = action;
      this.inhibitAction = inhibitAction;
    }
    
    public JInstrumentActionListener_0 (
      final String actionString,
      final Instrument.InstrumentAction action)
    {
      this (actionString, action, null);
    }
    
    @Override
    public void actionPerformed (final ActionEvent ae)
    {
      if (this.inhibitAction != null && this.inhibitAction.apply ())
        return;
      try
      {
        this.action.action ();
      }
      catch (IOException | InterruptedException e)
      {
        LOG.log (Level.INFO, "Caught exception with action {0}: {1}.",
          new Object[]{this.actionString, e});
      }
    }
    
  }
  
  public class JInstrumentActionListener_1Boolean
    implements ActionListener
  {

    private final String settingString;
    
    private final Provider<Boolean> componentGetter;
    
    private final Instrument.InstrumentSetter_1Boolean instrumentSetter;
    
    private final Provider<Boolean> inhibitSetter;
    
    private final Consumer<Boolean> trueFalsePreListener;
    
    private final boolean showPendingUpdate;
    
    public JInstrumentActionListener_1Boolean (
      final String settingString,
      final Provider<Boolean> componentGetter,
      final Instrument.InstrumentSetter_1Boolean instrumentSetter,
      final Provider<Boolean> inhibitSetter,
      final Consumer<Boolean> trueFalsePreListener,
      final boolean showPendingUpdate)
    {
      if (settingString == null || settingString.trim ().isEmpty () || componentGetter == null || instrumentSetter == null)
        throw new IllegalArgumentException ();
      this.settingString = settingString;
      this.componentGetter = componentGetter;
      this.instrumentSetter = instrumentSetter;
      this.inhibitSetter = inhibitSetter;
      this.trueFalsePreListener = trueFalsePreListener;
      this.showPendingUpdate = true;
    }
    
    public JInstrumentActionListener_1Boolean (
      final String settingString,
      final Provider<Boolean> componentGetter,
      final Instrument.InstrumentSetter_1Boolean instrumentSetter,
      final Provider<Boolean> inhibitSetter,
      final Consumer<Boolean> trueFalsePreListener)
    {
      this (
        settingString,
        componentGetter,
        instrumentSetter,
        inhibitSetter,
        trueFalsePreListener,
        false);
    }
    
    public JInstrumentActionListener_1Boolean (
      final String settingString,
      final Provider<Boolean> getter,
      final Instrument.InstrumentSetter_1Boolean setter,
      final Provider<Boolean> inhibitSetter)
    {
      this (settingString, getter, setter, inhibitSetter, null);
    }
    
    @Override
    public void actionPerformed (final ActionEvent ae)
    {
      if (this.inhibitSetter != null && this.inhibitSetter.apply ())
        return;
      final Boolean currentValue = this.componentGetter.apply ();
      final boolean newValue = currentValue == null || (! currentValue);
      if (this.trueFalsePreListener != null)
        this.trueFalsePreListener.accept (newValue);
      if (this.showPendingUpdate && ae.getSource () instanceof JColorCheckBox.JBoolean)
        ((JColorCheckBox) ae.getSource ()).setDisplayedValue (null);
      try
      {
        this.instrumentSetter.set (newValue);
      }
      catch (IOException | InterruptedException e)
      {
        LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " to {0} on instrument {1}: {2}.",
          new Object[]{newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    }
    
  }
  
  public class JInstrumentSliderChangeListener_1Number<N extends Number>
    implements ChangeListener
  {

    private final String settingString;
    
    private final Instrument.InstrumentSetter_1Number setter;

    private final Provider<Boolean> inhibitSetter;
    
    private final Color preSetColor;
    
    private final Function<Integer, N> intToN;
    
    public JInstrumentSliderChangeListener_1Number (
      final String settingString,
      final Instrument.InstrumentSetter_1Number setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor,
      final Function<Integer,N> intToN)
    {
      if (settingString == null || settingString.trim ().isEmpty () || intToN == null)
        throw new IllegalArgumentException ();
      this.settingString = settingString;
      this.setter = setter;
      this.inhibitSetter = inhibitSetter;
      this.preSetColor = preSetColor;
      this.intToN = intToN;
    }
    
    @Override
    public void stateChanged (final ChangeEvent ce)
    {
      final JSlider source = (JSlider) ce.getSource ();
      source.setToolTipText (this.intToN.apply (source.getValue ()).toString ()); // XXX Relies on Number.toString...
      if (! source.getValueIsAdjusting ())
      {
        if (this.inhibitSetter != null && this.inhibitSetter.apply ())
          return;
        if (this.preSetColor != null)
          source.setBackground (this.preSetColor);
        final N newN = JInstrumentSliderChangeListener_1Number.this.intToN.apply (source.getValue ());
        if (this.setter != null)
        {
          try
          {
            this.setter.set (newN);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " from slider to {0} [int: {1}]: {2}.",
              new Object[]{newN, source.getValue (), Arrays.toString (e.getStackTrace ())});
          }
        }
      }
    }
    
  }
  
  public class JInstrumentSliderChangeListener_1Integer
    implements ChangeListener
  {

    private final String settingString;
    
    private final Instrument.InstrumentSetter_1int setter;

    private final Provider<Boolean> inhibitSetter;
    
    private final Color preSetColor;
    
    public JInstrumentSliderChangeListener_1Integer (
      final String settingString,
      final Instrument.InstrumentSetter_1int setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor)
    {
      if (settingString == null || settingString.trim ().isEmpty () || setter == null)
        throw new IllegalArgumentException ();
      this.settingString = settingString;
      this.setter = setter;
      this.inhibitSetter = inhibitSetter;
      this.preSetColor = preSetColor;
    }
    
    public JInstrumentSliderChangeListener_1Integer (
      final String settingString,
      final Instrument.InstrumentSetter_1int setter,
      final Provider<Boolean> inhibitSetter)
    {
      this (settingString, setter, inhibitSetter, null);
    }
    
    @Override
    public void stateChanged (final ChangeEvent ce)
    {
      final JSlider source = (JSlider) ce.getSource ();
      source.setToolTipText (Integer.toString (source.getValue ()));
      if (! source.getValueIsAdjusting ())
      {
        if (this.inhibitSetter != null && this.inhibitSetter.apply ())
          return;
        if (this.preSetColor != null)
          source.setBackground (this.preSetColor);
        try
        {
          this.setter.set (this.setter.intToArg (source.getValue ()));
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " from slider to {0}: {1}.",
            new Object[]{source.getValue (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
    
  }
  
  public class JInstrumentSliderChangeListener_1Double
    implements ChangeListener
  {

    private final String settingString;
    
    private final Instrument.InstrumentSetter_1double setter;

    private final Provider<Boolean> inhibitSetter;
    
    private final Color preSetColor;
    
    public JInstrumentSliderChangeListener_1Double (
      final String settingString,
      final Instrument.InstrumentSetter_1double setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor)
    {
      if (settingString == null || settingString.trim ().isEmpty () || setter == null)
        throw new IllegalArgumentException ();
      this.settingString = settingString;
      this.setter = setter;
      this.inhibitSetter = inhibitSetter;
      this.preSetColor = preSetColor;
    }
    
    public JInstrumentSliderChangeListener_1Double (
      final String settingString,
      final Instrument.InstrumentSetter_1double setter,
      final Provider<Boolean> inhibitSetter)
    {
      this (settingString, setter, inhibitSetter, null);
    }
    
    @Override
    public void stateChanged (final ChangeEvent ce)
    {
      final JSlider source = (JSlider) ce.getSource ();
      source.setToolTipText (Double.toString (source.getValue ()));
      if (! source.getValueIsAdjusting ())
      {
        if (this.inhibitSetter != null && this.inhibitSetter.apply ())
          return;
        if (this.preSetColor != null)
          source.setBackground (this.preSetColor);
        try
        {
          this.setter.set (this.setter.intToArg (source.getValue ()));
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " from slider to {0}: {1}.",
            new Object[]{source.getValue (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
    
  }
  
  public class JInstrumentTextFieldListener_1String
    extends JTextFieldListener
    implements FocusListener, ActionListener
  {

    private final JTextField jTextField;
    
    private final String settingString;
    
    private final Provider<String> getter;
    
    private final Instrument.InstrumentSetter_1String setter;

    private final Provider<Boolean> inhibitSetter;
    
    private final Color preSetColor;

    public JInstrumentTextFieldListener_1String (
      final JTextField jTextField,
      final String settingString,
      final Provider<String> getter,
      final Instrument.InstrumentSetter_1String setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor)
    {
      if (settingString == null || settingString.trim ().isEmpty () || setter == null)
        throw new IllegalArgumentException ();
      if (jTextField == null && getter == null)
        throw new IllegalArgumentException ();
      this.jTextField = jTextField;
      this.settingString = settingString;
      this.getter = getter;
      this.setter = setter;
      this.inhibitSetter = inhibitSetter;
      this.preSetColor = preSetColor;
    }

    public JInstrumentTextFieldListener_1String (
      final JTextField jTextField,
      final String settingString,
      final Instrument.InstrumentSetter_1String setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor)
    {
      this (jTextField, settingString, null, setter, inhibitSetter, preSetColor);
    }

    public JInstrumentTextFieldListener_1String (
      final String settingString,
      final Provider<String> getter,
      final Instrument.InstrumentSetter_1String setter,
      final Provider<Boolean> inhibitSetter)
    {
      this (null, settingString, getter, setter, inhibitSetter, null);
    }
    
    @Override
    public void actionPerformed ()
    {
      if (this.inhibitSetter != null && this.inhibitSetter.apply ())
        return;
      if (this.jTextField != null && this.preSetColor != null)
        this.jTextField.setBackground (this.preSetColor);
      final String newValue = this.getter != null ? this.getter.apply () : this.jTextField.getText ();
      try
      {
        this.setter.set (newValue);
      }
      catch (IOException | InterruptedException e)
      {
        LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " to {0} on instrument {1}: {2}.",
          new Object[]{newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    }
    
  }
  
  public class JInstrumentTextFieldListener_1Int
    extends JTextFieldListener
    implements FocusListener, ActionListener
  {

    private final JTextField jTextField;
    
    private final String settingString;
    
    private final Provider<String> getter;
    
    private final Instrument.InstrumentSetter_1int setter;

    private final Provider<Boolean> inhibitSetter;
    
    private final Color preSetColor;

    private final boolean reportParseErrorsInDialog;
    
    public JInstrumentTextFieldListener_1Int (
      final JTextField jTextField,
      final String settingString,
      final Provider<String> getter,
      final Instrument.InstrumentSetter_1int setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor,
      final boolean reportParseErrorsInDialog)
    {
      if (settingString == null || settingString.trim ().isEmpty () || setter == null)
        throw new IllegalArgumentException ();
      if (jTextField == null && getter == null)
        throw new IllegalArgumentException ();
      this.jTextField = jTextField;
      this.settingString = settingString;
      this.getter = getter;
      this.setter = setter;
      this.inhibitSetter = inhibitSetter;
      this.preSetColor = preSetColor;
      this.reportParseErrorsInDialog = reportParseErrorsInDialog;
    }

    public JInstrumentTextFieldListener_1Int (
      final JTextField jTextField,
      final String settingString,
      final Instrument.InstrumentSetter_1int setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor,
      final boolean reportParseErrorsInDialog)
    {
      this (
        jTextField,
        settingString,
        null,
        setter,
        inhibitSetter,
        preSetColor,
        reportParseErrorsInDialog);
    }

    public JInstrumentTextFieldListener_1Int (
      final String settingString,
      final Provider<String> getter,
      final Instrument.InstrumentSetter_1int setter,
      final Provider<Boolean> inhibitSetter,
      final boolean reportParseErrorsInDialog)
    {
      this (
        null,
        settingString,
        getter,
        setter,
        inhibitSetter,
        null,
        reportParseErrorsInDialog);
    }

    @Override
    public void actionPerformed ()
    {
      if (this.inhibitSetter != null && this.inhibitSetter.apply ())
        return;
      if (this.jTextField != null && this.preSetColor != null)
        this.jTextField.setBackground (this.preSetColor);
      final String newValue = this.getter != null ? this.getter.apply () : this.jTextField.getText ();
      try
      {
        this.setter.set (Integer.parseInt (newValue));
      }
      catch (NumberFormatException e)
      {
        LOG.log (Level.INFO, "Not an int: {0}.", newValue);
        if (this.reportParseErrorsInDialog)
          JOptionPane.showMessageDialog (null, "Not an int: " + newValue + "!", "Parse Error", JOptionPane.ERROR_MESSAGE);
      }
      catch (IOException | InterruptedException e)
      {
        LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " to {0} on instrument {1}: {2}.",
          new Object[]{newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    }
    
  }
  
  public class JInstrumentTextFieldListener_1Double
    extends JTextFieldListener
    implements FocusListener, ActionListener
  {

    private final JTextField jTextField;
    
    private final String settingString;
    
    private final Provider<String> getter;
    
    private final Instrument.InstrumentSetter_1double setter;

    private final Provider<Boolean> inhibitSetter;
    
    private final Color preSetColor;

    private final boolean reportParseErrorsInDialog;
    
    public JInstrumentTextFieldListener_1Double (
      final JTextField jTextField,
      final String settingString,
      final Provider<String> getter,
      final Instrument.InstrumentSetter_1double setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor,
      final boolean reportParseErrorsInDialog)
    {
      if (settingString == null || settingString.trim ().isEmpty () || setter == null)
        throw new IllegalArgumentException ();
      if (jTextField == null && getter == null)
        throw new IllegalArgumentException ();
      this.jTextField = jTextField;
      this.settingString = settingString;
      this.getter = getter;
      this.setter = setter;
      this.inhibitSetter = inhibitSetter;
      this.preSetColor = preSetColor;
      this.reportParseErrorsInDialog = reportParseErrorsInDialog;
    }

    public JInstrumentTextFieldListener_1Double (
      final JTextField jTextField,
      final String settingString,
      final Instrument.InstrumentSetter_1double setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor,
      final boolean reportParseErrorsInDialog)
    {
      this (
        jTextField,
        settingString,
        null,
        setter,
        inhibitSetter,
        preSetColor,
        reportParseErrorsInDialog);
    }

    public JInstrumentTextFieldListener_1Double (
      final String settingString,
      final Provider<String> getter,
      final Instrument.InstrumentSetter_1double setter,
      final Provider<Boolean> inhibitSetter,
      final boolean reportParseErrorsInDialog)
    {
      this (
        null,
        settingString,
        getter,
        setter,
        inhibitSetter,
        null,
        reportParseErrorsInDialog);
    }

    @Override
    public void actionPerformed ()
    {
      if (this.inhibitSetter != null && this.inhibitSetter.apply ())
        return;
      if (this.jTextField != null && this.preSetColor != null)
        this.jTextField.setBackground (this.preSetColor);
      final String newValue = this.getter != null ? this.getter.apply () : this.jTextField.getText ();
      try
      {
        this.setter.set (Double.parseDouble (newValue));
      }
      catch (NumberFormatException e)
      {
        LOG.log (Level.INFO, "Not a double: {0}.", newValue);
        if (this.reportParseErrorsInDialog)
          JOptionPane.showMessageDialog (null, "Not a double: " + newValue + "!", "Parse Error", JOptionPane.ERROR_MESSAGE);
      }
      catch (IOException | InterruptedException e)
      {
        LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " to {0} on instrument {1}: {2}.",
          new Object[]{newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    }
    
  }
  
  public class JInstrumentComboBoxItemListener_Enum<E extends Enum<E>>
    implements ItemListener
  {

    private final String settingString;
    
    private final Class<E> clazz;
    
    private final Instrument.InstrumentSetter_1Enum<E> setter;

    private final Provider<Boolean> inhibitSetter;

    private final Color preSetColor;
    
    public JInstrumentComboBoxItemListener_Enum (
      final String settingString,
      final Class<E> clazz,
      final Instrument.InstrumentSetter_1Enum setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor)
    {
      if (settingString == null || settingString.trim ().isEmpty () || clazz == null || setter == null)
        throw new IllegalArgumentException ();
      this.settingString = settingString;
      this.clazz = clazz;
      this.setter = setter;
      this.inhibitSetter = inhibitSetter;
      this.preSetColor = preSetColor;
    }
    
    public JInstrumentComboBoxItemListener_Enum (
      final String settingString,
      final Class<E> clazz,
      final Instrument.InstrumentSetter_1Enum setter,
      final Provider<Boolean> inhibitSetter)
    {
      this (settingString, clazz, setter, inhibitSetter, null);
    }
    
    @Override
    public void itemStateChanged (final ItemEvent ie)
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final E newValue = (E) ie.getItem ();
        if (this.inhibitSetter != null && this.inhibitSetter.apply ())
          return;
        if (this.preSetColor != null)
          ((JComboBox) ie.getItemSelectable ()).setBackground (this.preSetColor);
        try
        {
          this.setter.set (newValue);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " from combo box to {0}: {1}.",
            new Object[]{ie.getItem (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
    
  }
  
  public class JInstrumentJBitsLongListener
    implements Consumer<Long>
  {

    private final JBitsLong jBitsLong;
    
    private final String settingString;
    
    private final Instrument.InstrumentSetter_1Number setter;

    private final Provider<Boolean> inhibitSetter;
    
    private final Color preSetColor;
    
    public JInstrumentJBitsLongListener (
      final JBitsLong jBitsLong,
      final String settingString,
      final Instrument.InstrumentSetter_1Long setter,
      final Provider<Boolean> inhibitSetter,
      final Color preSetColor)
    {
      if (jBitsLong == null || settingString == null || settingString.trim ().isEmpty () || setter == null)
        throw new IllegalArgumentException ();
      this.jBitsLong = jBitsLong;
      this.settingString = settingString;
      this.setter = setter;
      this.inhibitSetter = inhibitSetter;
      this.preSetColor = preSetColor;
    }

    @Override
    public final void accept (final Long newValue)
    {
      if (this.inhibitSetter != null && this.inhibitSetter.apply ())
        return;
      if (this.preSetColor != null)
        this.jBitsLong.setBackground (this.preSetColor);
      try
      {
        this.setter.set (newValue);
      }
      catch (IOException | InterruptedException e)
      {
        LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " from JBitsLong to {0}: {1}.",
          new Object[]{newValue, Arrays.toString (e.getStackTrace ())});
      }
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // [STANDARD] COMPONENTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public class JDialogPopupButton
    extends JColorCheckBox<Void>
  {
    
    public JDialogPopupButton (
      final Color buttonColor,
      final JDialog jDialog,
      final Provider<Boolean> inhibitAction)
    {
      super ((Void d) -> buttonColor);
      addActionListener (new JActionButtonListener (jDialog, inhibitAction));
    }
    
    public JDialogPopupButton (
      final Color buttonColor,
      final JDialog jDialog)
    {
      super ((Void d) -> buttonColor);
      addActionListener (new JActionButtonListener (jDialog));
    }
    
    public JDialogPopupButton (
      final Color buttonColor,
      final JComponent atComponent,
      final String dialogTitle,
      final Dimension dialogDimension,
      final JComponent contentPane,
      final Provider<Boolean> inhibitAction)
    {
      this (buttonColor, createModalDialog (atComponent, dialogTitle, dialogDimension, contentPane), inhibitAction);
    }
    
    public JDialogPopupButton (
      final Color buttonColor,
      final JComponent atComponent,
      final String dialogTitle,
      final Dimension dialogDimension,
      final JComponent contentPane)
    {
      this (buttonColor, createModalDialog (atComponent, dialogTitle, dialogDimension, contentPane));
    }
    
  }
  
  private static JDialog createModalDialog (
      final JComponent atComponent,
      final String dialogTitle,
      final Dimension dialogDimension,
      final JComponent contentPane)
  {
    final JDialog jDialog = (new JOptionPane ()).createDialog (dialogTitle);
    if (dialogTitle != null)
      jDialog.setTitle (dialogTitle);
    if (dialogDimension != null)
    {
      jDialog.setPreferredSize (dialogDimension);
      jDialog.setMinimumSize (dialogDimension);
      jDialog.setMaximumSize (dialogDimension);
    }
    if (contentPane != null)
      jDialog.setContentPane (contentPane);
    jDialog.pack ();
    jDialog.setLocationRelativeTo (atComponent);
    return jDialog;
  }
  
  public class JVoid_JColorCheckBox
    extends JColorCheckBox<Void>
  {

    public JVoid_JColorCheckBox (
      final String actionString,
      final Instrument.InstrumentAction action,
      final Color color)
    {
      super ((Void d) -> color);
      addActionListener (new JInstrumentActionListener_0 (
        actionString,
        action));
    }
    
  }
  
  public class JBoolean_JBoolean
    extends JColorCheckBox.JBoolean
  {

    final Function<InstrumentSettings, Boolean> instrumentGetter;
    
    public JBoolean_JBoolean (
      final String settingString,
      final Function<InstrumentSettings, Boolean> instrumentGetter,
      final Instrument.InstrumentSetter_1Boolean instrumentSetter,
      final Color falseColor,
      final Color trueColor,
      final Consumer<Boolean> trueFalsePreListener,
      final boolean showPendingUpdates)
    {
      super ((t) ->
              {
                if (t == null) return getGuiPreferencesUpdatePendingColor ();
                else if (t) return trueColor;
                else return falseColor;
              });
      setDisplayedValue (null);
      this.instrumentGetter = instrumentGetter;
      if (this.instrumentGetter != null)
        getInstrument ().addInstrumentListener (this.instrumentListener);
      if (instrumentSetter != null)
        addActionListener (new JInstrumentActionListener_1Boolean (
          settingString,
          this::getDisplayedValue,
          instrumentSetter,
          JInstrumentPanel.this::isInhibitInstrumentControl,
          trueFalsePreListener,
          showPendingUpdates));
      else
        setEnabled (false);
    }

    public JBoolean_JBoolean (
      final String settingString,
      final Function<InstrumentSettings, Boolean> instrumentGetter,
      final Instrument.InstrumentSetter_1Boolean instrumentSetter,
      final Color trueColor,
      final Consumer<Boolean> trueFalsePreListener,
      final boolean showPendingUpdates)
    {
      this (
        settingString,
        instrumentGetter,
        instrumentSetter,
        JInstrumentPanel.this.getBackground (),
        trueColor,
        trueFalsePreListener,
        showPendingUpdates);
    }

    public JBoolean_JBoolean (
      final String settingString,
      final Function<InstrumentSettings, Boolean> instrumentGetter,
      final Instrument.InstrumentSetter_1Boolean instrumentSetter,
      final Color falseColor,
      final Color trueColor,
      final boolean showPendingUpdates)
    {
      this (
        settingString,
        instrumentGetter,
        instrumentSetter,
        falseColor,
        trueColor,
        null,
        showPendingUpdates);
    }
      
    public JBoolean_JBoolean (
      final String settingString,
      final Function<InstrumentSettings, Boolean> instrumentGetter,
      final Instrument.InstrumentSetter_1Boolean instrumentSetter,
      final Color trueColor,
      final boolean showPendingUpdates)
    {
      this (
        settingString,
        instrumentGetter,
        instrumentSetter,
        JInstrumentPanel.this.getBackground (),
        trueColor,
        null,
        showPendingUpdates);
    }
      
    public JBoolean_JBoolean (
      final Function<InstrumentSettings, Boolean> instrumentGetter,
      final Color falseColor,
      final Color trueColor)
    {
      this (
        null,
        instrumentGetter,
        null,
        falseColor,
        trueColor,
        null,
        false);
    }
      
    public JBoolean_JBoolean (
      final Function<InstrumentSettings, Boolean> instrumentGetter,
      final Color trueColor)
    {
      this (
        null,
        instrumentGetter,
        null,
        JInstrumentPanel.this.getBackground (),
        trueColor,
        null,
        false);
    }
      
    private final InstrumentListener instrumentListener = new DefaultInstrumentListener ()
    {
      
      @Override
      public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
      {
        if (instrument != JInstrumentPanel.this.getInstrument () || instrumentSettings == null)
          throw new IllegalArgumentException ();
        if (JBoolean_JBoolean.this.instrumentGetter == null)
          throw new RuntimeException ();
        final Boolean newValue = JBoolean_JBoolean.this.instrumentGetter.apply (instrumentSettings);
        SwingUtilities.invokeLater (() ->
        {
          JInstrumentPanel.this.setInhibitInstrumentControl ();
          try
          {
            JBoolean_JBoolean.this.setDisplayedValue (newValue);
          }
          finally
          {
            JInstrumentPanel.this.resetInhibitInstrumentControl ();
          }
        });
      }

    };
    
  }
  
  public class JEnum_JComboBox<E extends Enum<E>>
    extends JComboBox<E>
  {

    private final Function<InstrumentSettings, E> instrumentGetter;
    
    private final boolean showPendingUpdates;
    
    public JEnum_JComboBox (
      final Class<E> clazz,
      final String settingString,
      final Function<InstrumentSettings, E> instrumentGetter,
      final Instrument.InstrumentSetter_1Enum<E> instrumentSetter,
      final boolean showPendingUpdates)      
    {
      super (clazz.getEnumConstants ());
      setSelectedItem (null);
      setEditable (false);
      this.showPendingUpdates = showPendingUpdates;
      if (this.showPendingUpdates)
        setBackground (getGuiPreferencesUpdatePendingColor ());
      this.instrumentGetter = instrumentGetter;
      if (this.instrumentGetter != null)
        getInstrument ().addInstrumentListener (this.instrumentListener);
      addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
        settingString,
        clazz,
        instrumentSetter,
        JInstrumentPanel.this::isInhibitInstrumentControl,
        getGuiPreferencesUpdatePendingColor ()));
    }
    
    private final InstrumentListener instrumentListener = new DefaultInstrumentListener ()
    {
      
      @Override
      public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
      {
        if (instrument != JInstrumentPanel.this.getInstrument () || instrumentSettings == null)
          throw new IllegalArgumentException ();
        if (JEnum_JComboBox.this.instrumentGetter == null)
          throw new RuntimeException ();
        final E newValue = JEnum_JComboBox.this.instrumentGetter.apply (instrumentSettings);
        SwingUtilities.invokeLater (() ->
        {
          JInstrumentPanel.this.setInhibitInstrumentControl ();
          try
          {
            JEnum_JComboBox.this.setSelectedItem (newValue);
            if (JEnum_JComboBox.this.showPendingUpdates)
              JEnum_JComboBox.this.setBackground (JInstrumentPanel.this.getBackground ());
          }
          finally
          {
            JInstrumentPanel.this.resetInhibitInstrumentControl ();
          }
        });
      }

    };
    
  }
  
  public abstract class JNumber_JSlider<N extends Number>
    extends JSlider
  {

    private final Function<InstrumentSettings, N> instrumentGetter;
    
    private final Function<N, Integer> nToInt;
    
    private final Function<Integer, N> intToN;
    
    private final boolean showPendingUpdates;
    
    public JNumber_JSlider (
      final int orientation,
      final N minValue,
      final N maxValue,
      final N initialValue,
      final N majorTickSpacing,
      final N minorTickSpacing,
      final String settingString,
      final Function<InstrumentSettings, N> instrumentGetter,
      final Instrument.InstrumentSetter_1Number instrumentSetter,
      final Function<Integer,N> intToN,
      final Function<N, Integer> nToInt,
      final boolean showPendingUpdates)
    {
      super (nToInt.apply (minValue), nToInt.apply (maxValue));
      if (settingString == null || nToInt == null || intToN == null)
        throw new IllegalArgumentException ();
      setOrientation (orientation);
      setToolTipText ("-");
      this.showPendingUpdates = showPendingUpdates;
      if (this.showPendingUpdates)
        setBackground (getGuiPreferencesUpdatePendingColor ());
      this.instrumentGetter = instrumentGetter;
      this.nToInt = nToInt;
      this.intToN = intToN;
      if (initialValue != null)
        setValue (this.nToInt.apply (initialValue));
      if (majorTickSpacing != null)
        setMajorTickSpacing (this.nToInt.apply (majorTickSpacing));
      if (minorTickSpacing != null)
        setMinorTickSpacing (this.nToInt.apply (minorTickSpacing));
      if (majorTickSpacing != null || minorTickSpacing != null)
      {
        setPaintTicks (true);
      }
      if (this.instrumentGetter != null)
        getInstrument ().addInstrumentListener (this.instrumentListener);
      addChangeListener (new JInstrumentSliderChangeListener_1Number (
        settingString,
        instrumentSetter,
        JInstrumentPanel.this::isInhibitInstrumentControl,
        this.showPendingUpdates ? getGuiPreferencesUpdatePendingColor () : null,
        intToN));
    }
    
    private final InstrumentListener instrumentListener = new DefaultInstrumentListener ()
    {
      
      @Override
      public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
      {
        if (instrument != JInstrumentPanel.this.getInstrument () || instrumentSettings == null)
          throw new IllegalArgumentException ();
        if (JNumber_JSlider.this.instrumentGetter == null)
          throw new RuntimeException ();
        final N newValue = JNumber_JSlider.this.instrumentGetter.apply (instrumentSettings);
        SwingUtilities.invokeLater (() ->
        {
          JInstrumentPanel.this.setInhibitInstrumentControl ();
          try
          {
            JNumber_JSlider.this.setValue (JNumber_JSlider.this.nToInt.apply (newValue));
            JNumber_JSlider.this.setToolTipText (newValue.toString ());
            if (JNumber_JSlider.this.showPendingUpdates)
              JNumber_JSlider.this.setBackground (JInstrumentPanel.this.getBackground ());
          }
          finally
          {
            JInstrumentPanel.this.resetInhibitInstrumentControl ();
          }
        });
      }

    };
    
    public JNumber_JSlider withPaintLabels ()
    {
      setPaintLabels (true);
      // XXX
      // DO NOT USE WHEN THE VALUES NEEDS TRANSFORMING [E.G., WITH DOUBLES].
      return this;
    }
    
  }
  
  public class JInteger_JSlider
    extends JNumber_JSlider<Integer>
  {

    public JInteger_JSlider (
      final int orientation,
      final int minValue,
      final int maxValue,
      final int intialValue,
      final Integer majorTickSpacing,
      final Integer minorTickSpacing,
      final String settingString,
      final Function<InstrumentSettings, Integer> instrumentGetter,
      final Instrument.InstrumentSetter_1Integer instrumentSetter,
      final boolean showPendingUpdates)
    {
      super (
        orientation,
        minValue,
        maxValue,
        intialValue,
        majorTickSpacing,
        minorTickSpacing,
        settingString,
        instrumentGetter,
        instrumentSetter,
        Function.identity (),
        Function.identity (),
        showPendingUpdates);
    }
    
    public JInteger_JSlider (
      final int orientation,
      final int minValue,
      final int maxValue,
      final int intialValue,
      final String settingString,
      final Function<InstrumentSettings, Integer> instrumentGetter,
      final Instrument.InstrumentSetter_1Integer instrumentSetter,
      final boolean showPendingUpdates)
    {
      this (
        orientation,
        minValue,
        maxValue,
        intialValue,
        null,
        null,
        settingString,
        instrumentGetter,
        instrumentSetter,
        showPendingUpdates);
    }
    
    @Override
    public JInteger_JSlider withPaintLabels ()
    {
      setPaintLabels (true);
      return this;
    }
    
  }
  
  public class JDouble_JSlider
    extends JNumber_JSlider<Double>
  {

    public JDouble_JSlider (
      final int orientation,
      final Double minValue,
      final Double maxValue,
      final Double intialValue,
      final Double majorTickSpacing,
      final Double minorTickSpacing,
      final String settingString,
      final Function<InstrumentSettings, Double> instrumentGetter,
      final Instrument.InstrumentSetter_1double instrumentSetter,
      final Function<Integer, Double> i2d,
      final Function<Double, Integer> d2i,
      final boolean showPendingUpdates)
    {
      super (
        orientation,
        minValue,
        maxValue,
        intialValue,
        majorTickSpacing,
        minorTickSpacing,
        settingString,
        instrumentGetter,
        instrumentSetter,
        i2d,
        d2i,
        showPendingUpdates);
    }
    
    public JDouble_JSlider (
      final int orientation,
      final Double minValue,
      final Double maxValue,
      final Double intialValue,
      final String settingString,
      final Function<InstrumentSettings, Double> instrumentGetter,
      final Instrument.InstrumentSetter_1double instrumentSetter,
      final Function<Integer, Double> i2d,
      final Function<Double, Integer> d2i,
      final boolean showPendingUpdates)
    {
      this (
        orientation,
        minValue,
        maxValue,
        intialValue,
        null,
        null,
        settingString,
        instrumentGetter,
        instrumentSetter,
        i2d,
        d2i,
        showPendingUpdates);
    }
    
    public JDouble_JSlider (
      final int orientation,
      final Double minValue,
      final Double maxValue,
      final Double intialValue,
      final Double majorTickSpacing,
      final Double minorTickSpacing,
      final String settingString,
      final Function<InstrumentSettings, Double> instrumentGetter,
      final Instrument.InstrumentSetter_1double instrumentSetter,
      final boolean showPendingUpdates)
    {
      this (
        orientation,
        minValue,
        maxValue,
        intialValue,
        majorTickSpacing,
        minorTickSpacing,
        settingString,
        instrumentGetter,
        instrumentSetter,
        (i) -> Double.valueOf (i),
        (d) -> (int) Math.round (d),
        showPendingUpdates);
    }
    
    public JDouble_JSlider (
      final int orientation,
      final Double minValue,
      final Double maxValue,
      final Double intialValue,
      final String settingString,
      final Function<InstrumentSettings, Double> instrumentGetter,
      final Instrument.InstrumentSetter_1double instrumentSetter,
      final boolean showPendingUpdates)
    {
      this (
        orientation,
        minValue,
        maxValue,
        intialValue,
        null,
        null,
        settingString,
        instrumentGetter,
        instrumentSetter,
        (i) -> Double.valueOf (i),
        (d) -> (int) Math.round (d),
        showPendingUpdates);
    }
    
    @Override
    public final JDouble_JSlider withPaintLabels ()
    {
      setPaintLabels (true);
      return this;
    }
    
  }
  
  public class JLong_JBitsLong
    extends JBitsLong
  {

    private final Function<InstrumentSettings, Long> instrumentGetter;
    
    private final boolean showPendingUpdates;
    
    public JLong_JBitsLong (
      final Color trueColor,
      final int length,
      final List<String> labelStrings,
      final boolean autoUpdate,
      final String settingString,
      final Function<InstrumentSettings, Long> instrumentGetter,
      final Instrument.InstrumentSetter_1Long instrumentSetter,
      final boolean showPendingUpdates)
    {
      super (
        trueColor,
        length,
        labelStrings,
        null,
        autoUpdate);
      this.instrumentGetter = instrumentGetter;
      this.showPendingUpdates = showPendingUpdates;
      if (this.instrumentGetter != null)
        getInstrument ().addInstrumentListener (this.instrumentListener);
      if (instrumentSetter != null)
          setListener (new JInstrumentJBitsLongListener (
            this,
            settingString,
            instrumentSetter,
            JInstrumentPanel.this::isInhibitInstrumentControl,
            showPendingUpdates ? getGuiPreferencesUpdatePendingColor () : null));
      else
        setEnabled (false);
      if (this.showPendingUpdates)
        setBackground (getGuiPreferencesUpdatePendingColor ());
    }
    
    private final InstrumentListener instrumentListener = new DefaultInstrumentListener ()
    {
      
      @Override
      public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
      {
        if (instrument != JInstrumentPanel.this.getInstrument () || instrumentSettings == null)
          throw new IllegalArgumentException ();
        if (JLong_JBitsLong.this.instrumentGetter == null)
          throw new RuntimeException ();
        final Long newValue = JLong_JBitsLong.this.instrumentGetter.apply (instrumentSettings);
        SwingUtilities.invokeLater (() ->
        {
          JInstrumentPanel.this.setInhibitInstrumentControl ();
          try
          {
            JLong_JBitsLong.this.setDisplayedValue (newValue);
            if (JLong_JBitsLong.this.showPendingUpdates)
              JLong_JBitsLong.this.setBackground (JInstrumentPanel.this.getBackground ());
          }
          finally
          {
            JInstrumentPanel.this.resetInhibitInstrumentControl ();
          }
        });
      }

    };
    
  }
  
  public class JString_JTextField
    extends JTextField
  {

    private final Function<InstrumentSettings, String> instrumentGetter;
    
    private final boolean showPendingUpdates;
    
    public JString_JTextField (
      final String initialString,
      final Integer columns,
      final String settingString,
      final Function<InstrumentSettings, String> instrumentGetter,
      final Instrument.InstrumentSetter_1String instrumentSetter,
      final boolean showPendingUpdates)
    {
      super ();
      this.instrumentGetter = instrumentGetter;
      this.showPendingUpdates = showPendingUpdates;
      if (this.showPendingUpdates)
        setBackground (getGuiPreferencesUpdatePendingColor ());
      if (initialString != null)
        setText (initialString);
      if (columns != null)
        setColumns (columns);
      if (this.instrumentGetter != null)
        getInstrument ().addInstrumentListener (this.instrumentListener);
      if (instrumentSetter != null)
        addActionListener (new JInstrumentTextFieldListener_1String (
          this,
          settingString,
          this::getText,
          instrumentSetter,
          JInstrumentPanel.this::isInhibitInstrumentControl,
          showPendingUpdates ? getGuiPreferencesUpdatePendingColor () : null));
    }
    
    public JString_JTextField (
      final Integer columns,
      final String settingString,
      final Function<InstrumentSettings, String> instrumentGetter,
      final Instrument.InstrumentSetter_1String instrumentSetter,
      final boolean showPendingUpdates)
    {
      this (null, columns, settingString, instrumentGetter, instrumentSetter, showPendingUpdates);
    }
    
    public JString_JTextField (
      final String initialString,
      final String settingString,
      final Function<InstrumentSettings, String> instrumentGetter,
      final Instrument.InstrumentSetter_1String instrumentSetter,
      final boolean showPendingUpdates)
    {
      this (initialString, null, settingString, instrumentGetter, instrumentSetter, showPendingUpdates);
    }
    
    public JString_JTextField (
      final String settingString,
      final Function<InstrumentSettings, String> instrumentGetter,
      final Instrument.InstrumentSetter_1String instrumentSetter,
      final boolean showPendingUpdates)
    {
      this (null, null, settingString, instrumentGetter, instrumentSetter, showPendingUpdates);
    }
    
    private final InstrumentListener instrumentListener = new DefaultInstrumentListener ()
    {
      
      @Override
      public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
      {
        if (instrument != JInstrumentPanel.this.getInstrument () || instrumentSettings == null)
          throw new IllegalArgumentException ();
        if (JString_JTextField.this.instrumentGetter == null)
          throw new RuntimeException ();
        final String newValue = JString_JTextField.this.instrumentGetter.apply (instrumentSettings);
        SwingUtilities.invokeLater (() ->
        {
          JInstrumentPanel.this.setInhibitInstrumentControl ();
          try
          {
            JString_JTextField.this.setText (newValue);
            if (JString_JTextField.this.showPendingUpdates)
              JString_JTextField.this.setBackground (JInstrumentPanel.this.getBackground ());
          }
          finally
          {
            JInstrumentPanel.this.resetInhibitInstrumentControl ();
          }
        });
      }

    };
    
  }
  
  public class Jint_JTextField
    extends JTextField
  {

    private final Function<InstrumentSettings, Integer> instrumentGetter;
    
    private final boolean showPendingUpdates;
    
    public Jint_JTextField (
      final Integer initialValue,
      final Integer columns,
      final String settingString,
      final Function<InstrumentSettings, Integer> instrumentGetter,
      final Instrument.InstrumentSetter_1int instrumentSetter,
      final boolean showPendingUpdates)
    {
      super ();
      this.instrumentGetter = instrumentGetter;
      this.showPendingUpdates = showPendingUpdates;
      if (this.showPendingUpdates)
        setBackground (getGuiPreferencesUpdatePendingColor ());
      if (initialValue != null)
        setText (Integer.toString (initialValue));
      if (columns != null)
        setColumns (columns);
      if (this.instrumentGetter != null)
        getInstrument ().addInstrumentListener (this.instrumentListener);
      if (instrumentSetter != null)
        addActionListener (new JInstrumentTextFieldListener_1Int (
          this,
          settingString,
          instrumentSetter,
          JInstrumentPanel.this::isInhibitInstrumentControl,
          showPendingUpdates ? getGuiPreferencesUpdatePendingColor () : null,
          true));
    }
    
    private final InstrumentListener instrumentListener = new DefaultInstrumentListener ()
    {
      
      @Override
      public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
      {
        if (instrument != JInstrumentPanel.this.getInstrument () || instrumentSettings == null)
          throw new IllegalArgumentException ();
        if (Jint_JTextField.this.instrumentGetter == null)
          throw new RuntimeException ();
        final int newValue = Jint_JTextField.this.instrumentGetter.apply (instrumentSettings);
        SwingUtilities.invokeLater (() ->
        {
          JInstrumentPanel.this.setInhibitInstrumentControl ();
          try
          {
            Jint_JTextField.this.setText (Integer.toString (newValue));
            if (Jint_JTextField.this.showPendingUpdates)
              Jint_JTextField.this.setBackground (JInstrumentPanel.this.getBackground ());
          }
          finally
          {
            JInstrumentPanel.this.resetInhibitInstrumentControl ();
          }
        });
      }

    };
    
  }
  
  public class Jdouble_JTextField
    extends JTextField
  {

    private final Function<InstrumentSettings, Double> instrumentGetter;
    
    private final boolean showPendingUpdates;
    
    public Jdouble_JTextField (
      final Double initialValue,
      final Integer columns,
      final String settingString,
      final Function<InstrumentSettings, Double> instrumentGetter,
      final Instrument.InstrumentSetter_1double instrumentSetter,
      final boolean showPendingUpdates)
    {
      super ();
      this.instrumentGetter = instrumentGetter;
      this.showPendingUpdates = showPendingUpdates;
      if (this.showPendingUpdates)
        setBackground (getGuiPreferencesUpdatePendingColor ());
      if (initialValue != null)
        setText (Double.toString (initialValue));
      if (columns != null)
        setColumns (columns);
      if (this.instrumentGetter != null)
        getInstrument ().addInstrumentListener (this.instrumentListener);
      if (instrumentSetter != null)
        addActionListener (new JInstrumentTextFieldListener_1Double (
          this,
          settingString,
          instrumentSetter,
          JInstrumentPanel.this::isInhibitInstrumentControl,
          showPendingUpdates ? getGuiPreferencesUpdatePendingColor () : null,
          true));
    }
    
    private final InstrumentListener instrumentListener = new DefaultInstrumentListener ()
    {
      
      @Override
      public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
      {
        if (instrument != JInstrumentPanel.this.getInstrument () || instrumentSettings == null)
          throw new IllegalArgumentException ();
        if (Jdouble_JTextField.this.instrumentGetter == null)
          throw new RuntimeException ();
        final double newValue = Jdouble_JTextField.this.instrumentGetter.apply (instrumentSettings);
        SwingUtilities.invokeLater (() ->
        {
          JInstrumentPanel.this.setInhibitInstrumentControl ();
          try
          {
            Jdouble_JTextField.this.setText (Double.toString (newValue));
            if (Jdouble_JTextField.this.showPendingUpdates)
              Jdouble_JTextField.this.setBackground (JInstrumentPanel.this.getBackground ());
          }
          finally
          {
            JInstrumentPanel.this.resetInhibitInstrumentControl ();
          }
        });
      }

    };
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}