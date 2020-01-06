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

import java.io.IOException;
import java.util.List;

/** Extension of {@link Instrument} for (mostly DC) power-supply units.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface PowerSupplyUnit
extends Instrument
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HARD LIMITS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  double getHardLimitVoltage_V ();
  
  double getHardLimitCurrent_A ();
  
  double getHardLimitPower_W ();
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SOFT LIMITS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  boolean isSupportsSoftLimitVoltage ();
  
  void setSoftLimitVoltage_V (double softLimitVoltage_V)
    throws IOException, InterruptedException, UnsupportedOperationException;
  
  boolean isSupportsSoftLimitCurrent ();
  
  void setSoftLimitCurrent_A (double softLimitCurrent_A)
    throws IOException, InterruptedException, UnsupportedOperationException;
  
  boolean isSupportsSoftLimitPower ();
  
  void setSoftLimitPower_W (double softLimitPower_W)
    throws IOException, InterruptedException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // POWER SUPPLY MODES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  enum PowerSupplyMode
  {
    CV,
    CC,
    CP;
  }
  
  List<PowerSupplyMode> getSupportedPowerSupplyModes ();
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
