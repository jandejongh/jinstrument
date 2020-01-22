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
package org.javajdj.jinstrument.swing;

import java.awt.Color;
import org.javajdj.jinstrument.FrequencyCounter;

/** A Swing (base) component for interacting with a {@link FrequencyCounter}.
 *
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class JFrequencyCounterPanel
  extends JInstrumentPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected JFrequencyCounterPanel (
    final FrequencyCounter frequencyCounter,
    final String title,
    final int level,
    final Color panelColor)
  {
    super (frequencyCounter, title, level, panelColor);
    // Super-class accepts null Instrument; we do not.
    if (frequencyCounter == null)
      throw new IllegalArgumentException ();    
  }

  protected JFrequencyCounterPanel (final FrequencyCounter frequencyCounter, final int level)
  {
    this (frequencyCounter, null, level, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FUNCTION GENERATOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final FrequencyCounter getFrequencyCounter ()
  {
    return (FrequencyCounter) getInstrument ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
