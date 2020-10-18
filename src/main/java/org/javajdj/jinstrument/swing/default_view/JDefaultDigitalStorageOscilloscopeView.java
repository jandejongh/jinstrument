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
package org.javajdj.jinstrument.swing.default_view;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.swing.JDigitalStorageOscilloscopeDisplay;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;

/** A one-size-fits-all Swing panel for control and status of a generic {@link DigitalStorageOscilloscope}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JDefaultDigitalStorageOscilloscopeView
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultDigitalStorageOscilloscopeView.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultDigitalStorageOscilloscopeView (final DigitalStorageOscilloscope digitalStorageOscilloscope, final int level)
  {
    super (digitalStorageOscilloscope, level);
    setOpaque (true);
    // setBackground (Color.white);
    setLayout (new GridLayout (2, 2));
    final JDefaultInstrumentManagementView instrumentManagementPanel= new JDefaultInstrumentManagementView (
      JDefaultDigitalStorageOscilloscopeView.this.getInstrument (),
      JDefaultDigitalStorageOscilloscopeView.this.getLevel () + 1);
    instrumentManagementPanel.setPanelBorder (JInstrumentPanel.getGuiPreferencesManagementColor (), "Management");
    add (instrumentManagementPanel);
    this.display = new JDigitalStorageOscilloscopeDisplay (digitalStorageOscilloscope, level + 1);
    JInstrumentPanel.setPanelBorder (this.display,
      level + 1,
      Color.pink,
      "Trace");
    add (this.display);
    final JDigitalStorageOscilloscopePanel digitalStorageOscilloscopePanel = new JDigitalStorageOscilloscopePanel (
        JDefaultDigitalStorageOscilloscopeView.this.getDigitalStorageOscilloscope (),
        "Settings",
        JDefaultDigitalStorageOscilloscopeView.this.getLevel () + 1,
        Color.black);
    add (digitalStorageOscilloscopePanel);
    add (new JPanel ());
  }
  
  public JDefaultDigitalStorageOscilloscopeView (final DigitalStorageOscilloscope digitalStorageOscilloscope)
  {
    this (digitalStorageOscilloscope, 0);
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
      return "Default Digital Storage Oscilloscope View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof DigitalStorageOscilloscope))
        return new JDefaultDigitalStorageOscilloscopeView ((DigitalStorageOscilloscope) instrument);
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
    return JDefaultDigitalStorageOscilloscopeView.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JDigitalStorageOscilloscopeDisplay display;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
