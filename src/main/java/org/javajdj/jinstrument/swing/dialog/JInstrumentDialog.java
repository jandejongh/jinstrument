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
package org.javajdj.jinstrument.swing.dialog;

import java.util.logging.Logger;
import javax.swing.JOptionPane;

/** A collection of dialogs accessed through static methods for use with {@code JInstrument}.
 * 
 * <p>
 * Unless noted otherwise,
 * the methods and classes in this class are
 * independent of controller, device and instrument.
 * In other words,
 * they can be used from any context in {@code JInstrument}.
 *
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class JInstrumentDialog
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JInstrumentDialog.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private JInstrumentDialog ()
  {
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING UTILITIES [DIALOGS]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static Integer getIntegerFromDialog (final String title, final Integer startValue)
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
  
  public static Double getFrequencyFromDialog_Hz (final String title, final Double startValue_Hz)
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
  
  public static Double getFrequencyFromDialog_MHz (final String title, final Double startValue_MHz)
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
  
  public static Double getPeriodFromDialog_s (final String title, final Double startValue_s)
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
  
  public static Double getPowerFromDialog_dBm (final String title, final Double startValue_dBm)
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
  
  public static Double getPowerRatioFromDialog_dB (final String title, final Double startValue_dB)
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
  
  public static Double getVoltageFromDialog_V (final String title, final Double startValue_V)
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
  
  public static Double getCurrentFromDialog_A (final String title, final Double startValue_A)
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
  
  public static Double getPowerFromDialog_W (final String title, final Double startValue_W)
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
  
  public static Double getDutyCycleFromDialog_percent (final String title, final Double startValue_percent)
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
  
  public static Double getPhaseFromDialog_degrees (final String title, final Double startValue_degrees)
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}