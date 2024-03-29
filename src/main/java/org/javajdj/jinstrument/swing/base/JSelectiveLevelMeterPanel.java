/*
 * Copyright 2010-2021 Jan de Jongh <jfcmdejongh@gmail.com>.
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
import org.javajdj.jinstrument.SelectiveLevelMeter;

/** A Swing (base) component for interacting with a {@link SelectiveLevelMeter}.
 *
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class JSelectiveLevelMeterPanel
  extends JInstrumentPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected JSelectiveLevelMeterPanel (
    final SelectiveLevelMeter selectiveLevelMeter,
    final String title,
    final int level,
    final Color panelColor)
  {
    super (selectiveLevelMeter, title, level, panelColor);
    if (selectiveLevelMeter == null)
      throw new IllegalArgumentException ();
  }

  protected JSelectiveLevelMeterPanel (final SelectiveLevelMeter selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SELECTIVE LEVEL METER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final SelectiveLevelMeter getSelectiveLevelMeter ()
  {
    return (SelectiveLevelMeter) getInstrument ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
