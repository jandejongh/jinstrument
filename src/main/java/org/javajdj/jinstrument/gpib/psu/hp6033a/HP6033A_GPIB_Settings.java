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
package org.javajdj.jinstrument.gpib.psu.hp6033a;

import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.DefaultPowerSupplyUnitSettings;
import org.javajdj.jinstrument.PowerSupplyUnitSettings;

/** Implementation of {@link InstrumentSettings} and {@link PowerSupplyUnitSettings} for the HP-6033A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class HP6033A_GPIB_Settings
  extends DefaultPowerSupplyUnitSettings
  implements PowerSupplyUnitSettings
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP6033A_GPIB_Settings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public HP6033A_GPIB_Settings (
    final byte[] bytes,
    final double softLimitVoltage_V,
    final double softLimitCurrent_A,
    final double softLimitPower_W,
    final double setVoltage_V,
    final double setCurrent_A,
    final double setPower_W,
    final boolean outputEnable)
  {
    super (
      bytes,
      softLimitVoltage_V,
      softLimitCurrent_A,
      softLimitPower_W,
      setVoltage_V,
      setCurrent_A,
      setPower_W,
      outputEnable);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final boolean equals (final Object obj)
  {
    return super.equals (obj);
  }

  @Override
  public final int hashCode ()
  {
    return super.hashCode ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
