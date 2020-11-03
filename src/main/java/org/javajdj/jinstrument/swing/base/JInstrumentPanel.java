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
package org.javajdj.jinstrument.swing.base;

import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    final Color panelColor)
  {
    super ();
    this.instrument = instrument;
    this.level = level;
    if (title != null)
      setPanelBorder (panelColor, title);
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // JSlider LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // XXX Is this really unavailable in Java 8?
  
  @FunctionalInterface
  public interface Provider<T>
  {
    T apply ();
  }
  
  public class JInstrumentSliderChangeListener_1Integer
    implements ChangeListener
  {

    final String settingString;
    
    final Instrument.InstrumentSetter_1Integer setter;

    final Provider<Boolean> inhibitSetter;
    
    public JInstrumentSliderChangeListener_1Integer (
      final String settingString,
      final Instrument.InstrumentSetter_1Integer setter,
      final Provider<Boolean> inhibitSetter)
    {
      if (settingString == null || settingString.trim ().isEmpty () || setter == null)
        throw new IllegalArgumentException ();
      this.settingString = settingString;
      this.setter = setter;
      this.inhibitSetter = inhibitSetter;
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
        try
        {
          this.setter.set (this.setter.intToArg (source.getValue ()));
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " from slider to {0}: {1}.",
            new Object[]{source.getValue (), e});
        }
      }
    }
    
  }
  
  public class JInstrumentSliderChangeListener_1Double
    implements ChangeListener
  {

    final String settingString;
    
    final Instrument.InstrumentSetter_1Double setter;

    final Provider<Boolean> inhibitSetter;
    
    public JInstrumentSliderChangeListener_1Double (
      final String settingString,
      final Instrument.InstrumentSetter_1Double setter,
      final Provider<Boolean> inhibitSetter)
    {
      if (settingString == null || settingString.trim ().isEmpty () || setter == null)
        throw new IllegalArgumentException ();
      this.settingString = settingString;
      this.setter = setter;
      this.inhibitSetter = inhibitSetter;
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
        try
        {
          this.setter.set (this.setter.intToArg (source.getValue ()));
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting " + this.settingString + " from slider to {0}: {1}.",
            new Object[]{source.getValue (), e});
        }
      }
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
