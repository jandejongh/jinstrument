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

import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import java.awt.Color;
import org.javajdj.jinstrument.SpectrumAnalyzer;

/** A Swing (base) component for interacting with a {@link SpectrumAnalyzer}.
 *
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class JSpectrumAnalyzerPanel
  extends JInstrumentPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected JSpectrumAnalyzerPanel (
    final SpectrumAnalyzer spectrumAnalyzer,
    final String title,
    final int level,
    final Color panelColor)
  {
    super (spectrumAnalyzer, title, level, panelColor);
    if (spectrumAnalyzer == null)
      throw new IllegalArgumentException ();
  }

  protected JSpectrumAnalyzerPanel (final SpectrumAnalyzer spectrumAnalyzer, final int level)
  {
    this (spectrumAnalyzer, null, level, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SPECTRUM ANALYZER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final SpectrumAnalyzer getSpectrumAnalyzer ()
  {
    return (SpectrumAnalyzer) getInstrument ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
