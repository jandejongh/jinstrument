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

import java.util.logging.Logger;

/** Default implementation of {@link PowerSupplyUnitReading}.
 * 
 * <p>
 * The object is immutable.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class DefaultPowerSupplyUnitReading
  extends AbstractInstrumentReading<PowerSupplyUnitReading.Reading>
  implements PowerSupplyUnitReading
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultPowerSupplyUnitReading.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultPowerSupplyUnitReading (
    final PowerSupplyUnitSettings settings,
    final double readingVoltage_V,
    final double readingCurrent_A,
    final double readingPower_W,
    final PowerSupplyUnit.PowerSupplyMode powerSupplyMode,
    final Resolution resolution,
    final boolean error,
    final String errorMessage,
    final boolean overflow,
    final boolean uncalibrated,
    final boolean uncorrected)
  {
    super (
      settings,
      new DefaultReading (readingVoltage_V, readingCurrent_A, readingPower_W, powerSupplyMode),
      null,
      resolution,
      error,
      errorMessage,
      overflow,
      uncalibrated,
      uncorrected);
  }

  @Override
  public DefaultPowerSupplyUnitReading clone () throws CloneNotSupportedException
  {
    return (DefaultPowerSupplyUnitReading) super.clone ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentReading
  // PowerSupplyUnitReading
  // INSTRUMENT SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public PowerSupplyUnitSettings getInstrumentSettings ()
  {
    return (PowerSupplyUnitSettings) super.getInstrumentSettings ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnitReading
  // READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static class DefaultReading
    implements Reading
  {

    public DefaultReading (
      final double readingVoltage_V,
      final double readingCurrent_A,
      final double readingPower_W,
      final PowerSupplyUnit.PowerSupplyMode powerSupplyMode)
    {
      this.readingVoltage_V = readingVoltage_V;
      this.readingCurrent_A = readingCurrent_A;
      this.readingPower_W = readingPower_W;
      if (powerSupplyMode == null)
        throw new IllegalArgumentException ();
      this.powerSupplyMode = powerSupplyMode;
    }
    
    private final double readingVoltage_V;

    @Override
    public final double getReadingVoltage_V ()
    {
      return this.readingVoltage_V;
    }

    private final double readingCurrent_A;
    
    @Override
    public final double getReadingCurrent_A ()
    {
      return this.readingCurrent_A;
    }

    private final double readingPower_W;
    
    @Override
    public final double getReadingPower_W ()
    {
      return this.readingPower_W;
    }
    
    private final PowerSupplyUnit.PowerSupplyMode powerSupplyMode;

    @Override
    public final PowerSupplyUnit.PowerSupplyMode getPowerSupplyMode ()
    {
      return this.powerSupplyMode;
    }
    
  }
  
  @Override
  public final double getReadingVoltage_V ()
  {
    return getReadingValue ().getReadingVoltage_V ();
  }

  @Override
  public final double getReadingCurrent_A ()
  {
    return getReadingValue ().getReadingCurrent_A ();
  }

  @Override
  public final double getReadingPower_W ()
  {
    return getReadingValue ().getReadingPower_W ();
  }

  @Override
  public final PowerSupplyUnit.PowerSupplyMode getPowerSupplyMode ()
  {
    return getReadingValue ().getPowerSupplyMode ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
