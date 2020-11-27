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

import org.javajdj.junits.Unit;
import java.util.Arrays;
import java.util.logging.Logger;

/** Default implementation of {@link PowerSupplyUnitSettings}.
 * 
 * <p>
 * The object is immutable.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class DefaultPowerSupplyUnitSettings
  implements PowerSupplyUnitSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultPowerSupplyUnitSettings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultPowerSupplyUnitSettings (
    final byte[] bytes,
    final double softLimitVoltage_V,
    final double softLimitCurrent_A,
    final double softLimitPower_W,
    final double setVoltage_V,
    final double setCurrent_A,
    final double setPower_W,
    final boolean outputEnable)
  {
    this.bytes = bytes;
    this.softLimitVoltage_V = softLimitVoltage_V;
    this.softLimitCurrent_A = softLimitCurrent_A;
    this.softLimitPower_W = softLimitPower_W;
    this.setVoltage_V = setVoltage_V;
    this.setCurrent_A = setCurrent_A;
    this.setPower_W = setPower_W;
    this.outputEnable = outputEnable;
  }

  @Override
  public DefaultPowerSupplyUnitSettings clone () throws CloneNotSupportedException
  {
    return (DefaultPowerSupplyUnitSettings) super.clone ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentSettings
  // READING UNIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public final Unit getReadingUnit ()
  {
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentSettings
  // BYTES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final byte[] bytes;
  
  @Override
  public final byte[] getBytes ()
  {
    return this.bytes;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnitSettings
  // SOFT LIMIT VOLTAGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double softLimitVoltage_V;
  
  @Override
  public final double getSoftLimitVoltage_V ()
  {
    return this.softLimitVoltage_V;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnitSettings
  // SOFT LIMIT CURRENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double softLimitCurrent_A;
  
  @Override
  public final double getSoftLimitCurrent_A ()
  {
    return this.softLimitCurrent_A;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnitSettings
  // SOFT LIMIT POWER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double softLimitPower_W;
  
  @Override
  public final double getSoftLimitPower_W ()
  {
    return this.softLimitPower_W;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnitSettings
  // SET VOLTAGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double setVoltage_V;
  
  @Override
  public final double getSetVoltage_V ()
  {
    return this.setVoltage_V;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnitSettings
  // SET CURRENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double setCurrent_A;
  
  @Override
  public final double getSetCurrent_A ()
  {
    return this.setCurrent_A;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnitSettings
  // SET POWER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double setPower_W;
  
  @Override
  public final double getSetPower_W ()
  {
    return this.setPower_W;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnitSettings
  // OUTPUT ENABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean outputEnable;
  
  @Override
  public final boolean isOutputEnable ()
  {
    return this.outputEnable;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 53 * hash + Arrays.hashCode (this.bytes);
    hash = 53 * hash + (int) (Double.doubleToLongBits (this.softLimitVoltage_V) ^ (Double.doubleToLongBits (this.softLimitVoltage_V) >>> 32));
    hash = 53 * hash + (int) (Double.doubleToLongBits (this.softLimitCurrent_A) ^ (Double.doubleToLongBits (this.softLimitCurrent_A) >>> 32));
    hash = 53 * hash + (int) (Double.doubleToLongBits (this.softLimitPower_W) ^ (Double.doubleToLongBits (this.softLimitPower_W) >>> 32));
    hash = 53 * hash + (int) (Double.doubleToLongBits (this.setVoltage_V) ^ (Double.doubleToLongBits (this.setVoltage_V) >>> 32));
    hash = 53 * hash + (int) (Double.doubleToLongBits (this.setCurrent_A) ^ (Double.doubleToLongBits (this.setCurrent_A) >>> 32));
    hash = 53 * hash + (int) (Double.doubleToLongBits (this.setPower_W) ^ (Double.doubleToLongBits (this.setPower_W) >>> 32));
    hash = 53 * hash + (this.outputEnable ? 1 : 0);
    return hash;
  }

  @Override
  public boolean equals (Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass () != obj.getClass ())
    {
      return false;
    }
    final DefaultPowerSupplyUnitSettings other = (DefaultPowerSupplyUnitSettings) obj;
    if (Double.doubleToLongBits (this.softLimitVoltage_V) != Double.doubleToLongBits (other.softLimitVoltage_V))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.softLimitCurrent_A) != Double.doubleToLongBits (other.softLimitCurrent_A))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.softLimitPower_W) != Double.doubleToLongBits (other.softLimitPower_W))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.setVoltage_V) != Double.doubleToLongBits (other.setVoltage_V))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.setCurrent_A) != Double.doubleToLongBits (other.setCurrent_A))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.setPower_W) != Double.doubleToLongBits (other.setPower_W))
    {
      return false;
    }
    if (this.outputEnable != other.outputEnable)
    {
      return false;
    }
    if (!Arrays.equals (this.bytes, other.bytes))
    {
      return false;
    }
    return true;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
