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
package org.javajdj.jinstrument;

/** A physical unit.
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
  
  UNIT_NONE (""),
  
  //
  // VOLTAGE
  //
  UNIT_pV  ("pV"),
  UNIT_nV  ("nV"),
  UNIT_muV ("\u03BCV"),
  UNIT_mV  ("mV"),
  UNIT_V   ("V"),
  UNIT_kV  ("kV"),
  
  //
  // CURRENT
  //
  UNIT_pA  ("pA"),
  UNIT_nA  ("nA"),
  UNIT_muA ("\u03BCA"),
  UNIT_mA  ("mA"),
  UNIT_A   ("A"),
  UNIT_kA  ("kA"),
  
  //
  // RESISTANCE / IMPEDANCE
  //
  UNIT_muOhm ("\u03BC\u03A9"),
  UNIT_mOhm  ("m\u03A9"),
  UNIT_Ohm   ("\u03A9"),
  UNIT_kOhm  ("k\u03A9"),
  UNIT_MOhm  ("M\u03A9"),
  UNIT_GOhm  ("G\u03A9"),
  
  //
  // POWER
  //
  UNIT_aW ("aW"),
  UNIT_fW ("fW"),
  UNIT_pW ("pW"),
  UNIT_nW ("nW"),
  UNIT_muW ("\u03BCW"),
  UNIT_mW ("mW"),
  UNIT_W ("W"),
  UNIT_kW ("kW"),
  UNIT_dBm ("dBm"),
  UNIT_dBmuV ("db\u03BCV"),
  UNIT_dBV ("dbV"),
  
  //
  // TIME [INTERVAL] / DURATION
  //
  UNIT_ps ("ps"),
  UNIT_ns ("ns"),
  UNIT_mus ("\u03BCs"),
  UNIT_ms ("ms"),
  UNIT_s ("s"),
  UNIT_ks ("ks"),
  UNIT_Ms ("Ms"),
  
  //
  // FREQUENCY
  //
  UNIT_muHz ("\u03BCHz"),
  UNIT_mHz ("mHz"),
  UNIT_Hz ("Hz"),
  UNIT_kHz ("kHz"),
  UNIT_MHz ("MHz"),
  UNIT_GHz ("GHz"),
  UNIT_THz ("THz"),
  
  ;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  Unit (final String string)
  {
    this.string = string;
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
