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
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.DigitalStorageOscilloscopeTrace;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentChannel;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.cdi.JTinyCDIStatusAndControl;
import org.javajdj.jswing.jtrace.JTrace;
import org.javajdj.jswing.jtrace.TraceData;

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
  
  public JDefaultDigitalStorageOscilloscopeView (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final int level,
    final JTrace jTrace,
    final Function<InstrumentChannel, Boolean> isChannelEnabled)
  {
    
    super (digitalStorageOscilloscope, level);
    
    this.isChannelEnabled = isChannelEnabled;
    
    setOpaque (true);
    // setBackground (Color.white);
    
    setLayout (new GridLayout (2, 2));
    
    this.largeInstrumentManagementPanel = new JDefaultInstrumentManagementView (
      JDefaultDigitalStorageOscilloscopeView.this.getInstrument (),
      JDefaultDigitalStorageOscilloscopeView.this.getLevel () + 1);
    this.largeInstrumentManagementPanel.setPanelBorder (JInstrumentPanel.getGuiPreferencesManagementColor (), "Management");
    add (this.largeInstrumentManagementPanel);
    
    this.smallInstrumentManagementPanel = new JTinyCDIStatusAndControl (
      JDefaultDigitalStorageOscilloscopeView.this.getInstrument (),
      JDefaultDigitalStorageOscilloscopeView.this.getLevel () + 1);
    
    this.jTrace = (jTrace != null ? jTrace : new JTrace<> ());
    
    final JPanel tracePanel = new JPanel ();
    tracePanel.setLayout (new GridLayout (1, 1));
    JInstrumentPanel.setPanelBorder (tracePanel,
      level + 1,
      Color.pink,
      "Trace");
    tracePanel.add (this.jTrace);
    add (tracePanel);
    
    add (new JPanel ());
    add (new JPanel ());
    
    getInstrument ().addInstrumentListener (this.instrumentListener);
    
  }
  
  public JDefaultDigitalStorageOscilloscopeView (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final int level,
    final Function<InstrumentChannel, Boolean> isChannelEnabled)
  {
    this (digitalStorageOscilloscope, level, null, isChannelEnabled);
  }
  
  public JDefaultDigitalStorageOscilloscopeView (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final int level,
    final JTrace jTrace)
  {
    this (digitalStorageOscilloscope, level, jTrace, null);
  }
  
  public JDefaultDigitalStorageOscilloscopeView (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final int level)
  {
    this (digitalStorageOscilloscope, level, null, null);
  }
  
  public JDefaultDigitalStorageOscilloscopeView (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final JTrace jTrace)
  {
    this (digitalStorageOscilloscope, 0, jTrace, null);
  }
  
  public JDefaultDigitalStorageOscilloscopeView (
    final DigitalStorageOscilloscope digitalStorageOscilloscope)
  {
    this (digitalStorageOscilloscope, 0, null, null);
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
  // SWING
  // MANAGEMENT PANELS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JDefaultInstrumentManagementView largeInstrumentManagementPanel;

  protected final JDefaultInstrumentManagementView getLargeInstrumentManagementPanel ()
  {
    return this.largeInstrumentManagementPanel;
  }
  
  private final JTinyCDIStatusAndControl smallInstrumentManagementPanel;

  protected final JTinyCDIStatusAndControl getSmallInstrumentManagementPanel ()
  {
    return this.smallInstrumentManagementPanel;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TRACE PANEL [JTrace]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTrace jTrace;
  
  public final JTrace getJTrace ()
  {
    return this.jTrace;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // IS CHANNEL ENABLED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** This is a bit of a relic from the past; used to pass a method from non-sub-classes providing channel information.
   * 
   * XXX It is still there, but a protected (virtual) method seems better...
   * 
   */ 
  private final Function<InstrumentChannel, Boolean> isChannelEnabled;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SET TRACE [ON JTrace]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Instrument (display) specific massaging of generated {@link TraceData} for subclasses.
   * 
   * <p>
   * Allows sub-classes to augment the default generated {@link TraceData} object for the given {@link InstrumentChannel}.
   * The object may be changed in order to, for instance, add markers, or instrument-specific rescaling of the trace.
   * 
   * <p>
   * The default implementation simply returns the {@code traceData} argument.
   * 
   * @param channel   The {@link InstrumentChannel} to which the {@link TraceData} applies, non-{@code null}.
   * @param traceData The trace data, non-{@code null}.
   * 
   * @return The instrument (display) specific version of the {@code traceData} argument.
   * 
   */
  protected TraceData augmentTraceData (final InstrumentChannel channel, final TraceData traceData)
  {
    return traceData;
  }
  
  public final void setTrace (final DigitalStorageOscilloscopeTrace trace)
  {
    if (trace == null)
      return;
    final InstrumentChannel channel = trace.getInstrumentChannel ();
    if (channel != null)
      return;
    if (this.isChannelEnabled != null
      // Be careful below as the Function is perfectly allowed to return null: I don't know/care...
      && Objects.equals (this.isChannelEnabled.apply (channel), Boolean.FALSE))
    {
      this.jTrace.setTraceData (channel, null);    
    }
    else
    {
      final TraceData initialTraceData = 
        TraceData.createYn (trace.getReadingValue ())
        .withNRange (trace.getMinNHint (), trace.getMaxNHint ())
        .withXRange (trace.getMinXHint (), trace.getMaxXHint ())
        .withYRange (trace.getMinYHint (), trace.getMaxYHint ());
      if (initialTraceData == null)
        return;
      final TraceData finalTraceData = augmentTraceData (channel, initialTraceData);
      this.jTrace.setTraceData (channel, finalTraceData);
    }
    // For future use...
    // SwingUtilities.invokeLater (() -> repaint ());
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
      if (instrument != JDefaultDigitalStorageOscilloscopeView.this.getDigitalStorageOscilloscope ())
        return;
      JDefaultDigitalStorageOscilloscopeView.this.setTrace ((DigitalStorageOscilloscopeTrace) instrumentReading);
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
