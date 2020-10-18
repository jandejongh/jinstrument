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
package org.javajdj.jinstrument.swing.base;

import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import java.awt.Color;
import org.javajdj.jinstrument.PowerSupplyUnit;

/** A Swing (base) component for interacting with a {@link PowerSupplyUnit}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JPowerSupplyUnitPanel
  extends JInstrumentPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected JPowerSupplyUnitPanel (
    final PowerSupplyUnit powerSupplyUnit,
    final String title,
    final int level,
    final Color panelColor)
  {
    super (powerSupplyUnit, title, level, panelColor);
    if (powerSupplyUnit == null)
      throw new IllegalArgumentException ();
  }

  protected JPowerSupplyUnitPanel (final PowerSupplyUnit powerSupplyUnit, final int level)
  {
    this (powerSupplyUnit, null, level, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // POWER SUPPLY UNIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final PowerSupplyUnit getPowerSupplyUnit ()
  {
    return (PowerSupplyUnit) getInstrument ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
