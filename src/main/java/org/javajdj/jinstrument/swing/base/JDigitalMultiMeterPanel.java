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
package org.javajdj.jinstrument.swing.base;

import java.awt.Color;
import org.javajdj.jinstrument.DigitalMultiMeter;

/** A Swing (base) component for interacting with a {@link DigitalMultiMeter}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JDigitalMultiMeterPanel
  extends JInstrumentPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected JDigitalMultiMeterPanel (
    final DigitalMultiMeter digitalMultiMeter,
    final String title,
    final int level,
    final Color panelColor)
  {
    super (digitalMultiMeter, title, level, panelColor);
    if (digitalMultiMeter == null)
      throw new IllegalArgumentException ();
  }

  protected JDigitalMultiMeterPanel (final DigitalMultiMeter digitalMultiMeter, final int level)
  {
    this (digitalMultiMeter, null, level, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DIGITAL MULTIMETER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final DigitalMultiMeter getDigitalMultiMeter ()
  {
    return (DigitalMultiMeter) getInstrument ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
