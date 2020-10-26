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

import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.DigitalStorageOscilloscopeTrace;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jswing.jtrace.JTrace;

/** Panel showing the (latest) {@link DigitalStorageOscilloscopeTrace}(s) from a {@link DigitalStorageOscilloscopeTrace}.
 *
 * <p>
 * This class is (merely) a wrapper around a {@link JTrace} instance,
 * acting as a bridge between {@link DigitalStorageOscilloscopeTrace}
 * and {@link JTrace}. It listens for instrument reading from the
 * instrument passed at construction time, and translates them
 * into {@link JTrace} traces.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JDefaultDigitalStorageOscilloscopeTraceDisplay
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultDigitalStorageOscilloscopeTraceDisplay.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultDigitalStorageOscilloscopeTraceDisplay (final DigitalStorageOscilloscope digitalStorageOscilloscope, final int level)
  {
    super (digitalStorageOscilloscope, level);
    setLayout (new GridLayout (1, 1));
    setOpaque (true);
    setBackground (Color.black);
    this.jTrace = new JTrace<> ();
    add (this.jTrace);
    getInstrument ().addInstrumentListener (this.instrumentListener);
  }

  public JDefaultDigitalStorageOscilloscopeTraceDisplay (final DigitalStorageOscilloscope digitalStorageOscilloscope)
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
      return "Default Digital Storage Oscilloscope Trace [Only] View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof DigitalStorageOscilloscope))
        return new JDefaultDigitalStorageOscilloscopeTraceDisplay ((DigitalStorageOscilloscope) instrument);
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
    return JDefaultDigitalStorageOscilloscopeTraceDisplay.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // SET TRACE [ON JTrace]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final void setTrace (final DigitalStorageOscilloscopeTrace trace)
  {
    this.jTrace.setMinX (trace.getInstrumentChannel (), trace.getMinXHint ());
    this.jTrace.setMaxX (trace.getInstrumentChannel (), trace.getMaxXHint ());
    this.jTrace.setMinY (trace.getInstrumentChannel (), trace.getMinYHint ());
    this.jTrace.setMaxY (trace.getInstrumentChannel (), trace.getMaxYHint ());
    this.jTrace.setTrace (trace.getInstrumentChannel (), trace.getReadingValue ());
    // For future use...
    // SwingUtilities.invokeLater (() -> repaint ());
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING COMPONENTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTrace jTrace;
  
  public final JTrace getJTrace ()
  {
    return this.jTrace;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final InstrumentListener instrumentListener = new InstrumentListener ()
  {
    
    @Override
    public void newInstrumentStatus (final Instrument instrument, final InstrumentStatus instrumentStatus)
    {
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      if (instrument != JDefaultDigitalStorageOscilloscopeTraceDisplay.this.getDigitalStorageOscilloscope ())
        return;
      JDefaultDigitalStorageOscilloscopeTraceDisplay.this.setTrace ((DigitalStorageOscilloscopeTrace) instrumentReading);
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
