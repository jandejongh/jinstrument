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
package org.javajdj.jinstrument.swing;

import java.awt.Color;
import org.javajdj.jinstrument.instrument.sg.SignalGenerator;

/** A Swing (base) component for interacting with a {@link SignalGenerator}.
 *
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class JSignalGeneratorPanel
  extends JInstrumentPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected JSignalGeneratorPanel (
    final SignalGenerator signalGenerator,
    final String title,
    final int level,
    final Color panelColor)
  {
    super (signalGenerator, title, level, panelColor);
    // Super-class accepts null Instrument; we do not.
    if (signalGenerator == null)
      throw new IllegalArgumentException ();    
  }

  protected JSignalGeneratorPanel (final SignalGenerator signalGenerator, final int level)
  {
    this (signalGenerator, null, level, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SIGNAL GENERATOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final SignalGenerator signalGenerator ()
  {
    return (SignalGenerator) getInstrument ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
