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
package org.javajdj.jinstrument.swing.default_view;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SpectrumAnalyzer;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.base.JSpectrumAnalyzerPanel;
import org.javajdj.jinstrument.swing.JSpectrumAnalyzerSettingsPanel;
import org.javajdj.jinstrument.swing.JSpectrumAnalyzerTraceDisplay;

/** A one-size-fits-all Swing panel for control and status of a generic {@link SpectrumAnalyzer}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JDefaultSpectrumAnalyzerView
  extends JSpectrumAnalyzerPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultSpectrumAnalyzerView.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultSpectrumAnalyzerView (final SpectrumAnalyzer spectrumAnalyzer, final int level)
  {
    super (spectrumAnalyzer, level);
    setOpaque (true);
    // setBackground (Color.white);
    setLayout (new GridLayout (2, 2));
    final JDefaultInstrumentManagementView instrumentManagementPanel= new JDefaultInstrumentManagementView (
      JDefaultSpectrumAnalyzerView.this.getInstrument (),
      JDefaultSpectrumAnalyzerView.this.getLevel () + 1);
    instrumentManagementPanel.setPanelBorder (JInstrumentPanel.getGuiPreferencesManagementColor (), "Management");
    add (instrumentManagementPanel);
    this.display = new JSpectrumAnalyzerTraceDisplay (spectrumAnalyzer, level + 1);
    JInstrumentPanel.setPanelBorder (this.display,
      level + 1,
      Color.pink,
      "Trace");
    add (this.display);
    final JSpectrumAnalyzerSettingsPanel spectrumAnalyzerSettingsPanel = new JSpectrumAnalyzerSettingsPanel (
        JDefaultSpectrumAnalyzerView.this.getSpectrumAnalyzer (),
        "Settings",
        JDefaultSpectrumAnalyzerView.this.getLevel () + 1,
        Color.black);
    add (spectrumAnalyzerSettingsPanel);
    add (new JPanel ());
  }
  
  public JDefaultSpectrumAnalyzerView (final SpectrumAnalyzer spectrumAnalyzer)
  {
    this (spectrumAnalyzer, 0);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT VIEW TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static InstrumentViewType INSTRUMENT_VIEW_TYPE = new InstrumentViewType ()
  {
    
    @Override
    public final String getInstrumentViewTypeUrl ()
    {
      return "Default Spectrum Analyzer View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof SpectrumAnalyzer))
        return new JDefaultSpectrumAnalyzerView ((SpectrumAnalyzer) instrument);
      else
        return null;
    }
    
  };

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentView
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getInstrumentViewUrl ()
  {
    return JDefaultSpectrumAnalyzerView.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
      + "<>"
      + getInstrument ().getInstrumentUrl ();
  }
  
  @Override
  public String toString ()
  {
    return getInstrumentViewUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSpectrumAnalyzerTraceDisplay display;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
