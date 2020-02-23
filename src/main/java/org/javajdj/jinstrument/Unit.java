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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
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
  
  //
  // TEMPERATURE
  //
  UNIT_K (Quantity.QUANTITY_TEMPERATURE, 1, "K"),
  UNIT_C (Quantity.QUANTITY_TEMPERATURE, (c) -> c + Unit.ZERO_CELSIUS_K, (k) -> k - Unit.ZERO_CELSIUS_K, "C"),
  // XXX NEED Fahrenheit!
  
  //
  // ENERGY
  //
  UNIT_aJ  (Quantity.QUANTITY_ENERGY, 1E-18, "aJ"),
  UNIT_fJ  (Quantity.QUANTITY_ENERGY, 1E-15, "fJ"),
  UNIT_pJ  (Quantity.QUANTITY_ENERGY, 1E-12, "pJ"),
  UNIT_nJ  (Quantity.QUANTITY_ENERGY, 1E-9,  "nJ"),
  UNIT_muJ (Quantity.QUANTITY_ENERGY, 1E-6,  "\u03BCJ"),
  UNIT_mJ  (Quantity.QUANTITY_ENERGY, 1E-3,  "mJ"),
  UNIT_J   (Quantity.QUANTITY_ENERGY, 1,     "J"),
  UNIT_kJ  (Quantity.QUANTITY_ENERGY, 1E3,   "kJ"),
  UNIT_MJ  (Quantity.QUANTITY_ENERGY, 1E6,   "MJ"),
  UNIT_GJ  (Quantity.QUANTITY_ENERGY, 1E9,   "GJ"),
  UNIT_TJ  (Quantity.QUANTITY_ENERGY, 1E12,  "TJ"),
  UNIT_PJ  (Quantity.QUANTITY_ENERGY, 1E15,  "PJ"),
  UNIT_EJ  (Quantity.QUANTITY_ENERGY, 1E18,  "EJ"),
  
  //
  // DISTANCE
  //
  UNIT_am  (Quantity.QUANTITY_DISTANCE, 1E-18, "am"),
  UNIT_fm  (Quantity.QUANTITY_DISTANCE, 1E-15, "fm"),
  UNIT_pm  (Quantity.QUANTITY_DISTANCE, 1E-12, "pm"),
  UNIT_nm  (Quantity.QUANTITY_DISTANCE, 1E-9,  "nm"),
  UNIT_mum (Quantity.QUANTITY_DISTANCE, 1E-6,  "\u03BCm"),
  UNIT_mm  (Quantity.QUANTITY_DISTANCE, 1E-3,  "mm"),
  UNIT_cm  (Quantity.QUANTITY_DISTANCE, 1E-2,  "cm"),
  UNIT_dm  (Quantity.QUANTITY_DISTANCE, 1E-1,  "dm"),
  UNIT_m   (Quantity.QUANTITY_DISTANCE, 1,     "m"),
  UNIT_dam (Quantity.QUANTITY_DISTANCE, 1E1,   "dam"),
  UNIT_hm  (Quantity.QUANTITY_DISTANCE, 1E2,   "hm"),
  UNIT_km  (Quantity.QUANTITY_DISTANCE, 1E3,   "km"),
  UNIT_Mm  (Quantity.QUANTITY_DISTANCE, 1E6,   "Mm"),
  UNIT_Gm  (Quantity.QUANTITY_DISTANCE, 1E9,   "Gm"),
  UNIT_Tm  (Quantity.QUANTITY_DISTANCE, 1E12,  "Tm"),
  UNIT_Pm  (Quantity.QUANTITY_DISTANCE, 1E15,  "Pm"),
  UNIT_Em  (Quantity.QUANTITY_DISTANCE, 1E18,  "Em"),
  
  ;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC STATIC CONSTANTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static double ZERO_CELSIUS_K = 273.15;
  
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
  // AUTO RANGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private enum AutoRangeBin
  {
    
    INTERVAL_1000_POS_INFTY (1000, Double.POSITIVE_INFINITY),
    INTERVAL_100_1000       (100, 1000),
    INTERVAL_10_100         (10, 100),
    INTERVAL_1_10           (1, 10),
    INTERVAL_0p1_1          (0.1, 1),
    INTERVAL_0p01_0p1       (0.01, 0.1),
    INTERVAL_0p001_0p01     (0.001, 0.01),
    INTERVAL_ZERO_p001      (0, 0.001);

    private AutoRangeBin (final double rangeMin, final double rangeMax)
    {
      this.rangeMin = rangeMin;
      this.rangeMax = rangeMax;
    }
    
    private final double rangeMin;

    public final double getRangeMin ()
    {
      return this.rangeMin;
    }

    private final double rangeMax;
    
    public final double getRangeMax ()
    {
      return this.rangeMax;
    }
   
    public final static AutoRangeBin fromDouble (final double d)
    {
      if (d < 0)
        return fromDouble (-d);
      for (final AutoRangeBin bin : AutoRangeBin.values ())
        if (d >= bin.getRangeMin () && d < bin.getRangeMax ())
          return bin;
      throw new RuntimeException ();
    }
    
  }
  
  public static enum AutoRangePolicy
  {
    
    PREFER_1_1000 (2),
    PREFER_1_100  (1),
    PREFER_1_10   (0),
    PREFER_0p1_1  (0);
    
    private AutoRangePolicy (final int preferredDecimalPointIndex)
    {
      this.preferredDecimalPointIndex = preferredDecimalPointIndex;
    }
    
    private final int preferredDecimalPointIndex;
    
    public final int getPreferredDecimalPointIndex ()
    {
      return this.preferredDecimalPointIndex;
    }
    
  }
  
  private static double scoreMagnitude (final AutoRangePolicy autoRangePolicy, final double magnitude)
  {
    if (autoRangePolicy == null)
      throw new IllegalArgumentException ();
    if (magnitude < 0)
      return scoreMagnitude (autoRangePolicy, -magnitude);
    final AutoRangeBin autoRangeBin = AutoRangeBin.fromDouble (magnitude);
    switch (autoRangeBin)
    {
      case INTERVAL_1000_POS_INFTY:
        switch (autoRangePolicy)
        {
          case PREFER_1_1000:
            return -magnitude/1000;
          case PREFER_1_100:
            return -magnitude/100;
          case PREFER_1_10:
            return -magnitude/10;
          case PREFER_0p1_1:
            return -magnitude;
          default:
            throw new RuntimeException ();
        }
      case INTERVAL_100_1000:
        switch (autoRangePolicy)
        {
          case PREFER_1_1000:
            return magnitude;
          case PREFER_1_100:
            return -magnitude/100;
          case PREFER_1_10:
            return -magnitude/10;
          case PREFER_0p1_1:
            return -magnitude;
          default:
            throw new RuntimeException ();
        }
      case INTERVAL_10_100:
        switch (autoRangePolicy)
        {
          case PREFER_1_1000:
            return magnitude / 10;
          case PREFER_1_100:
            return magnitude;
          case PREFER_1_10:
            return -magnitude/10;
          case PREFER_0p1_1:
            return -magnitude;
          default:
            throw new RuntimeException ();
        }
      case INTERVAL_1_10:
        switch (autoRangePolicy)
        {
          case PREFER_1_1000:
            return magnitude / 100;
          case PREFER_1_100:
            return magnitude / 10;
          case PREFER_1_10:
            return magnitude;
          case PREFER_0p1_1:
            return -magnitude;
          default:
            throw new RuntimeException ();
        }
      case INTERVAL_0p1_1:
        switch (autoRangePolicy)
        {
          case PREFER_1_1000:
            return -magnitude;
          case PREFER_1_100:
            return -magnitude;
          case PREFER_1_10:
            return -magnitude;
          case PREFER_0p1_1:
            return magnitude;
          default:
            throw new RuntimeException ();
        }
      case INTERVAL_0p01_0p1:
      case INTERVAL_0p001_0p01:
      case INTERVAL_ZERO_p001:
        return -magnitude;
      default:
        throw new RuntimeException ();
    }
  }
  
  public static final Unit autoRange (
    final AutoRangePolicy autoRangePolicy,
    final double magnitude,
    final Unit fromUnit,
    final Resolution fromResolution,
    final Unit[] toUnits,
    final Resolution toResolution,
    final boolean strictQuantity,
    final boolean round)
  {
    if (autoRangePolicy == null)
      throw new IllegalArgumentException ();
    if (fromUnit == null)
      throw new IllegalArgumentException ();
    if (toUnits == null || toUnits.length == 0)
      return fromUnit;
    final SortedMap<Double, Set<Unit>> unitScores = new TreeMap<> ();
    final double defaultScore = scoreMagnitude (autoRangePolicy, magnitude);
    unitScores.put (defaultScore, new LinkedHashSet<> ());
    unitScores.get (defaultScore).add (fromUnit);
    for (final Unit toUnit : toUnits)
      if (toUnit != fromUnit)
      {
        if (strictQuantity && fromUnit.getQuantity () != toUnit.getQuantity ())
          continue;
        final double unitScore = scoreMagnitude (autoRangePolicy, Unit.convertToUnit (magnitude, fromUnit, toUnit));
        if (unitScore < unitScores.lastKey ())
          continue;
        if (! unitScores.containsKey (unitScore))
          unitScores.put (unitScore, new LinkedHashSet<> ());
        unitScores.get (unitScore).add (toUnit);
      }
    return unitScores.get (unitScores.lastKey ()).iterator ().next ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
