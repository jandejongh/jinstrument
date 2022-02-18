/*
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SpectrumAnalyzer;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.base.JSpectrumAnalyzerPanel;
import org.javajdj.jswing.jcenter.JCenter;

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
  
  protected JDefaultSpectrumAnalyzerView (
    final SpectrumAnalyzer spectrumAnalyzer,
    final int level,
    final boolean populateInstrumentManagementPanel,
    final boolean populateTracePanel,
    final boolean populateSettingsPanel)
  {
    super (spectrumAnalyzer, level);

    setOpaque (true);

    setLayout (new GridLayout (2, 2));
    
    this.jInstrumentManagementPanel = new JPanel ();
    add (this.jInstrumentManagementPanel);
    if (populateInstrumentManagementPanel)
    {
      this.jInstrumentManagementPanel.setLayout (new GridLayout (1, 1));
      this.jInstrumentManagementPanel.add (new JDefaultInstrumentManagementView (
        spectrumAnalyzer,
        level + 1));
    }
    setPanelBorder (this.jInstrumentManagementPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Management");
    
    this.jTracePanel = new JPanel ();
    add (this.jTracePanel);
    if (populateTracePanel)
    {
      this.jTracePanel.setLayout (new GridLayout (1, 1));
      this.jTracePanel.add (new JDefaultSpectrumAnalyzerTraceDisplay (spectrumAnalyzer, level + 1));
    }    
    // jinstrument Issue#0027.
    // setPanelBorder (this.jTracePanel,
    //   level + 1,
    //   Color.pink,
    //  "Trace");
    
    this.jSettingsPanel = new JPanel ();
    add (this.jSettingsPanel);
    if (populateSettingsPanel)
    {
      this.jSettingsPanel.setLayout (new GridLayout (1, 1));
      this.jSettingsPanel.add (new JDefaultSpectrumAnalyzerSettingsPanel (
        JDefaultSpectrumAnalyzerView.this.getSpectrumAnalyzer (),
        "Settings",
        JDefaultSpectrumAnalyzerView.this.getLevel () + 1,
        Color.black));
    }
    
    this.jInstrumentSpecificPanel = new JPanel ();
    add (this.jInstrumentSpecificPanel);
    this.jInstrumentSpecificPanel.setLayout (new GridLayout (1, 1));
    this.jInstrumentSpecificPanel.add (JCenter.XY (new JLabel ("Not supported in this View")));
    setPanelBorder (this.jInstrumentSpecificPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Instrument Specific");
    
  }
  
  public JDefaultSpectrumAnalyzerView (final SpectrumAnalyzer spectrumAnalyzer, final int level)
  {
    this (spectrumAnalyzer, level, true, true, true);
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
  // SWING
  // INSTRUMENT MANAGEMENT PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JPanel jInstrumentManagementPanel;
  
  protected final JPanel getInstrumentManagementPanel ()
  {
    return this.jInstrumentManagementPanel;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TRACE PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JPanel jTracePanel;
  
  protected final JPanel getTracePanel ()
  {
    return this.jTracePanel;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // SETTINGS PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JPanel jSettingsPanel;
  
  protected final JPanel getSettingsPanel ()
  {
    return this.jSettingsPanel;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // INSTRUMENT SPECIFIC PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JPanel jInstrumentSpecificPanel;
  
  protected final JPanel getInstrumentSpecificPanel ()
  {
    return this.jInstrumentSpecificPanel;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
