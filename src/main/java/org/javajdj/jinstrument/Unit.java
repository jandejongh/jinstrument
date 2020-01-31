/* 
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument;

import java.util.function.Function;

/** A physical (scalar) unit.
 *
 * @see Quantity
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public enum Unit
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ENUM VALUES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  //
  // DIMENSIONLESS
  //
  
  UNIT_NONE (Quantity.QUANTITY_NONE, 1, ""),
  
  //
  // VOLTAGE
  //
  UNIT_pV  (Quantity.QUANTITY_VOLTAGE, 1E-12, "pV"),
  UNIT_nV  (Quantity.QUANTITY_VOLTAGE, 1E-9 , "nV"),
  UNIT_muV (Quantity.QUANTITY_VOLTAGE, 1E-6 , "\u03BCV"),
  UNIT_mV  (Quantity.QUANTITY_VOLTAGE, 1E-3 , "mV"),
  UNIT_V   (Quantity.QUANTITY_VOLTAGE, 1    , "V"),
  UNIT_kV  (Quantity.QUANTITY_VOLTAGE, 1E3  , "kV"),
  
  //
  // CURRENT
  //
  UNIT_pA  (Quantity.QUANTITY_CURRENT, 1E-12, "pA"),
  UNIT_nA  (Quantity.QUANTITY_CURRENT, 1E-9 , "nA"),
  UNIT_muA (Quantity.QUANTITY_CURRENT, 1E-6 , "\u03BCA"),
  UNIT_mA  (Quantity.QUANTITY_CURRENT, 1E-3 , "mA"),
  UNIT_A   (Quantity.QUANTITY_CURRENT, 1    , "A"),
  UNIT_kA  (Quantity.QUANTITY_CURRENT, 1E3  , "kA"),
  
  //
  // RESISTANCE / IMPEDANCE
  //
  UNIT_muOhm (Quantity.QUANTITY_RESISTANCE, 1E-6, "\u03BC\u03A9"),
  UNIT_mOhm  (Quantity.QUANTITY_RESISTANCE, 1E-3, "m\u03A9"),
  UNIT_Ohm   (Quantity.QUANTITY_RESISTANCE, 1   , "\u03A9"),
  UNIT_kOhm  (Quantity.QUANTITY_RESISTANCE, 1E3 , "k\u03A9"),
  UNIT_MOhm  (Quantity.QUANTITY_RESISTANCE, 1E6 , "M\u03A9"),
  UNIT_GOhm  (Quantity.QUANTITY_RESISTANCE, 1E9 , "G\u03A9"),
  
  //
  // POWER
  //
  UNIT_aW  (Quantity.QUANTITY_POWER, 1E-18, "aW"),
  UNIT_fW  (Quantity.QUANTITY_POWER, 1E-15, "fW"),
  UNIT_pW  (Quantity.QUANTITY_POWER, 1E-12, "pW"),
  UNIT_nW  (Quantity.QUANTITY_POWER, 1E-9 , "nW"),
  UNIT_muW (Quantity.QUANTITY_POWER, 1E-6 , "\u03BCW"),
  UNIT_mW  (Quantity.QUANTITY_POWER, 1E-3 , "mW"),
  UNIT_W   (Quantity.QUANTITY_POWER, 1    , "W"),
  UNIT_kW  (Quantity.QUANTITY_POWER, 1E3  , "kW"),
  UNIT_dBm (Quantity.QUANTITY_POWER, (d) -> 1E-3  * Math.pow (10, d / 10), (d) -> 10 * Math.log10 (d * 1E3), "dBm"),
  
  //
  // TIME [INTERVAL] / DURATION
  //
  UNIT_ps  (Quantity.QUANTITY_TIME, 1E-12, "ps"),
  UNIT_ns  (Quantity.QUANTITY_TIME, 1E-9 , "ns"),
  UNIT_mus (Quantity.QUANTITY_TIME, 1E-6 , "\u03BCs"),
  UNIT_ms  (Quantity.QUANTITY_TIME, 1E-3 , "ms"),
  UNIT_s   (Quantity.QUANTITY_TIME, 1    , "s"),
  UNIT_ks  (Quantity.QUANTITY_TIME, 1E3  , "ks"),
  UNIT_Ms  (Quantity.QUANTITY_TIME, 1E6  , "Ms"),
  UNIT_Gs  (Quantity.QUANTITY_TIME, 1E9  , "Gs"),
  
  //
  // FREQUENCY
  //
  UNIT_muHz (Quantity.QUANTITY_FREQUENCY, 1E-6, "\u03BCHz"),
  UNIT_mHz  (Quantity.QUANTITY_FREQUENCY, 1E-3, "mHz"),
  UNIT_Hz   (Quantity.QUANTITY_FREQUENCY, 1   , "Hz"),
  UNIT_kHz  (Quantity.QUANTITY_FREQUENCY, 1E3 , "kHz"),
  UNIT_MHz  (Quantity.QUANTITY_FREQUENCY, 1E6 , "MHz"),
  UNIT_GHz  (Quantity.QUANTITY_FREQUENCY, 1E9 , "GHz"),
  UNIT_THz  (Quantity.QUANTITY_FREQUENCY, 1E12, "THz"),
  
  //
  // CAPACITANCE
  //
  UNIT_pF  (Quantity.QUANTITY_CAPACITANCE, 1E-12,"pF"),
  UNIT_nF  (Quantity.QUANTITY_CAPACITANCE, 1E-9, "nF"),
  UNIT_muF (Quantity.QUANTITY_CAPACITANCE, 1E-6, "\u03BCF"),
  UNIT_mF  (Quantity.QUANTITY_CAPACITANCE, 1E-3, "mF"),
  UNIT_F   (Quantity.QUANTITY_CAPACITANCE, 1   , "F"),
  UNIT_kF  (Quantity.QUANTITY_CAPACITANCE, 1E3 , "kF"),
  UNIT_MF  (Quantity.QUANTITY_CAPACITANCE, 1E6 , "MF"),
  
  //
  // INDUCTANCE
  //
  UNIT_pH  (Quantity.QUANTITY_INDUCTANCE, 1E-12,"pH"),
  UNIT_nH  (Quantity.QUANTITY_INDUCTANCE, 1E-9, "nH"),
  UNIT_muH (Quantity.QUANTITY_INDUCTANCE, 1E-6, "\u03BCH"),
  UNIT_mH  (Quantity.QUANTITY_INDUCTANCE, 1E-3, "mH"),
  UNIT_H   (Quantity.QUANTITY_INDUCTANCE, 1   , "H"),
  UNIT_kH  (Quantity.QUANTITY_INDUCTANCE, 1E3 , "kH"),
  UNIT_MH  (Quantity.QUANTITY_INDUCTANCE, 1E6 , "MH"),
  
  ;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  Unit (
    final Quantity quantity,
    final Function<Double, Double> toSiUnit,
    final Function<Double, Double> fromSiUnit,
    final boolean multiplicativeToSiUnit,
    final String string)
  {
    this.quantity = quantity;
    this.toSiUnit = toSiUnit;
    this.fromSiUnit = fromSiUnit;
    this.multiplicativeToSiUnit = multiplicativeToSiUnit;
    this.string = string;
  }
  
  Unit (
    final Quantity quantity,
    final double toSiUnitFactor,
    final String string)
  {
    this (quantity, (d) -> toSiUnitFactor * d, (d) -> d / toSiUnitFactor, true, string);
  }
  
  Unit (
    final Quantity quantity,
    final Function<Double, Double> toSiUnit,
    final Function<Double, Double> fromSiUnit,
    final String string)
  {
    this (quantity, toSiUnit, fromSiUnit, false, string);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // QUANTITY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Quantity quantity;
  
  public final Quantity getQuantity ()
  {
    return this.quantity;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TO / FROM SI UNIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Function<Double, Double> toSiUnit;
  
  public final Function<Double, Double> getToSiUnit ()
  {
    return this.toSiUnit;
  }
  
  private final Function<Double, Double> fromSiUnit;
  
  public final Function<Double, Double> getFromSiUnit ()
  {
    return this.fromSiUnit;
  }
  
  public final boolean isSiUnit ()
  {
    return this.quantity.getSiUnit () == this;
  }
  
  private final boolean multiplicativeToSiUnit;

  public final boolean isMultiplicativeToSiUnit ()
  {
    return this.multiplicativeToSiUnit;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  private final String string;

  @Override
  public String toString ()
  {
    return this.string;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UNIT CONVERSION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static final double convertToUnit (final double magnitude, final Unit fromUnit, final Unit toUnit)
  {
    if (fromUnit == null || toUnit == null)
      throw new IllegalArgumentException ();
    if (fromUnit == toUnit)
      return magnitude;
    final Quantity fromQuantity = fromUnit.getQuantity ();
    final Quantity toQuantity = toUnit.getQuantity ();
    if (fromQuantity == toQuantity)
      return toUnit.getFromSiUnit ().apply (fromUnit.getToSiUnit ().apply (magnitude));
    if (! Quantity.canConvert (fromQuantity, toQuantity))
      throw new IllegalArgumentException ();
    final double fromSi = fromUnit.getToSiUnit ().apply (magnitude);
    final double toSi = Quantity.convert (fromSi, fromQuantity, toQuantity);
    final double to = toUnit.getFromSiUnit ().apply (toSi);
    return to;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
