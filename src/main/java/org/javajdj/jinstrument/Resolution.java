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

/** The resolution of (e.g.) an instrument reading or a display.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public enum Resolution
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ENUM VALUES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  DIGITS_1    (1),
  DIGITS_1_5  (1.5),
  DIGITS_2    (2),
  DIGITS_2_5  (2.5),
  DIGITS_3    (3),
  DIGITS_3_5  (3.5),
  DIGITS_4    (4),
  DIGITS_4_5  (4.5),
  DIGITS_5    (5),
  DIGITS_5_5  (5.5),
  DIGITS_6    (6),
  DIGITS_6_5  (6.5),
  DIGITS_7    (7),
  DIGITS_7_5  (7.5),
  DIGITS_8    (8),
  DIGITS_8_5  (8.5),
  DIGITS_9    (9),
  DIGITS_9_5  (9.5),
  DIGITS_10   (10),
  DIGITS_10_5 (10.5),
  DIGITS_11   (11),
  DIGITS_11_5 (11.5),
  DIGITS_12   (12),
  DIGITS_12_5 (12.5),
  DIGITS_13   (13),
  DIGITS_13_5 (13.5),
  DIGITS_14   (14),
  DIGITS_14_5 (14.5),
  DIGITS_15   (15),
  DIGITS_15_5 (15.5)
  ;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private Resolution (final double numberOfDigits)
  {
    this.numberOfDigits = numberOfDigits;
  }
  
  public static Resolution fromNumberOfDigits (final int numberOfDigits, final boolean addHalf)
  {
    if (numberOfDigits < 1 || numberOfDigits >= 16)
      throw new IllegalArgumentException ();
    switch (numberOfDigits)
    {
      case 1 : return addHalf ? DIGITS_1_5  : DIGITS_1;
      case 2 : return addHalf ? DIGITS_2_5  : DIGITS_2;
      case 3 : return addHalf ? DIGITS_3_5  : DIGITS_3;
      case 4 : return addHalf ? DIGITS_4_5  : DIGITS_4;
      case 5 : return addHalf ? DIGITS_5_5  : DIGITS_5;
      case 6 : return addHalf ? DIGITS_6_5  : DIGITS_6;
      case 7 : return addHalf ? DIGITS_7_5  : DIGITS_7;
      case 8 : return addHalf ? DIGITS_8_5  : DIGITS_8;
      case 9 : return addHalf ? DIGITS_9_5  : DIGITS_9;
      case 10: return addHalf ? DIGITS_10_5 : DIGITS_10;
      case 11: return addHalf ? DIGITS_11_5 : DIGITS_11;
      case 12: return addHalf ? DIGITS_12_5 : DIGITS_12;
      case 13: return addHalf ? DIGITS_13_5 : DIGITS_13;
      case 14: return addHalf ? DIGITS_14_5 : DIGITS_14;
      case 15: return addHalf ? DIGITS_15_5 : DIGITS_15;
      default:
        throw new IllegalArgumentException ();
    }    
  }
  
  public static Resolution fromNumberOfDigits (final int numberOfDigits)
  {
    return fromNumberOfDigits (numberOfDigits, false);
  }
  
  public static Resolution fromNumberOfDigits (final double numberOfDigits)
  {
    if (numberOfDigits < 1 || numberOfDigits >= 16)
      throw new IllegalArgumentException ();
    switch ((int) Math.round (10 * numberOfDigits))
    {
      case 10 : return DIGITS_1;
      case 15 : return DIGITS_1_5;
      case 20 : return DIGITS_2;
      case 25 : return DIGITS_2_5;
      case 30 : return DIGITS_3;
      case 35 : return DIGITS_3_5;
      case 40 : return DIGITS_4;
      case 45 : return DIGITS_4_5;
      case 50 : return DIGITS_5;
      case 55 : return DIGITS_5_5;
      case 60 : return DIGITS_6;
      case 65 : return DIGITS_6_5;
      case 70 : return DIGITS_7;
      case 75 : return DIGITS_7_5;
      case 80 : return DIGITS_8;
      case 85 : return DIGITS_8_5;
      case 90 : return DIGITS_9;
      case 95 : return DIGITS_9_5;
      case 100: return DIGITS_10;
      case 105: return DIGITS_10_5;
      case 110: return DIGITS_11;
      case 115: return DIGITS_11_5;
      case 120: return DIGITS_12;
      case 125: return DIGITS_12_5;
      case 130: return DIGITS_13;
      case 135: return DIGITS_13_5;
      case 140: return DIGITS_14;
      case 145: return DIGITS_14_5;
      case 150: return DIGITS_15;
      case 155: return DIGITS_15_5;
      default:
        throw new IllegalArgumentException ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NUMBER OF DIGITS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double numberOfDigits;
  
  public final double getNumberOfDigits ()
  {
    return this.numberOfDigits;
  }
  
  public final int getNumberOfDigitsInt ()
  {
    return (int) Math.floor (this.numberOfDigits);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
