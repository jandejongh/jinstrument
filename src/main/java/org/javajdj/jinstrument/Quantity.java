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

/** A physical (scalar) quantity.
 *
 * @see Unit
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public enum Quantity
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ENUM VALUES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  QUANTITY_NONE        (Unit.UNIT_NONE),
  QUANTITY_VOLTAGE     (Unit.UNIT_V),
  QUANTITY_CURRENT     (Unit.UNIT_A),
  QUANTITY_RESISTANCE  (Unit.UNIT_Ohm),
  QUANTITY_POWER       (Unit.UNIT_W),
  QUANTITY_TIME        (Unit.UNIT_s),
  QUANTITY_FREQUENCY   (Unit.UNIT_Hz),
  QUANTITY_CAPACITANCE (Unit.UNIT_F),
  QUANTITY_INDUCTANCE  (Unit.UNIT_H),
  
  ;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  Quantity (final Unit siUnit)
  {
    this.siUnit = siUnit;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SI UNIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Unit siUnit;
  
  public final Unit getSiUnit ()
  {
    return this.siUnit;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  @Override
  public String toString ()
  {
    return this.siUnit.toString ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // QUANTITY CONVERSION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  public static final boolean canConvert (final Quantity q1, final Quantity q2)
  {
    if (q1 == null || q2 == null)
      throw new IllegalArgumentException ();
    if (q1 == q2)
      return true;
    switch (q1)
    {
      case QUANTITY_NONE:
        return false;
      case QUANTITY_VOLTAGE:
        return false;
      case QUANTITY_CURRENT:
        return false;
      case QUANTITY_RESISTANCE:
        return false;
      case QUANTITY_POWER:
        return false;
      case QUANTITY_TIME:
        return q2 == QUANTITY_FREQUENCY;
      case QUANTITY_FREQUENCY:
        return q2 == QUANTITY_TIME;
      case QUANTITY_CAPACITANCE:
        return false;
      case QUANTITY_INDUCTANCE:
        return false;
      default:
        throw new RuntimeException ();
    }
  }
  
  public static final double convert (final double magnitude, final Quantity fromQ, final Quantity toQ)
  {
    if (fromQ == null || toQ == null)
      throw new IllegalArgumentException ();
    if (fromQ == toQ)
      return magnitude;
    switch (fromQ)
    {
      case QUANTITY_NONE:
      case QUANTITY_VOLTAGE:
      case QUANTITY_CURRENT:
      case QUANTITY_RESISTANCE:
      case QUANTITY_POWER:
        throw new IllegalArgumentException ();
      case QUANTITY_TIME:
        if (toQ != QUANTITY_FREQUENCY)
          throw new IllegalArgumentException ();
        return 1 / magnitude; 
      case QUANTITY_FREQUENCY:
        if (toQ != QUANTITY_TIME)
          throw new IllegalArgumentException ();
        return 1 / magnitude;
      case QUANTITY_CAPACITANCE:
      case QUANTITY_INDUCTANCE:
        throw new IllegalArgumentException ();
      default:
        throw new RuntimeException ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
