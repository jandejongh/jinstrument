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
import org.javajdj.jinstrument.FunctionGenerator;

/** A Swing (base) component for interacting with a {@link FunctionGenerator}.
 *
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class JFunctionGeneratorPanel
  extends JInstrumentPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected JFunctionGeneratorPanel (
    final FunctionGenerator functionGenerator,
    final String title,
    final int level,
    final Color panelColor)
  {
    super (functionGenerator, title, level, panelColor);
    // Super-class accepts null Instrument; we do not.
    if (functionGenerator == null)
      throw new IllegalArgumentException ();    
  }

  protected JFunctionGeneratorPanel (final FunctionGenerator functionGenerator, final int level)
  {
    this (functionGenerator, null, level, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FUNCTION GENERATOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final FunctionGenerator getFunctionGenerator ()
  {
    return (FunctionGenerator) getInstrument ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
