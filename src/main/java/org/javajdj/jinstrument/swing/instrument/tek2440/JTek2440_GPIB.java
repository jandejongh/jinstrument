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
package org.javajdj.jinstrument.swing.instrument.tek2440;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentChannel;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Status;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.default_view.JDefaultDigitalStorageOscilloscopeView;
import org.javajdj.jswing.jbyte.JByte;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jtrace.JTrace;
import org.javajdj.jswing.jtrace.TraceDB;
import org.javajdj.jswing.jtrace.TraceData;
import org.javajdj.jswing.jtrace.TraceMarker;

/** A Swing panel for (complete) control and status of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB
  extends JDefaultDigitalStorageOscilloscopeView
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    
    super (digitalStorageOscilloscope, level);
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;
    
    removeAll ();
    setLayout (new BorderLayout ());
    
    // Improve the appearance of the JTrace instance.
    // XXX This probably is unnecessary in later versions of jswing/jtrace.
    getJTrace ().setBorder (null);
    getJTrace ().setXMargin (4);
    getJTrace ().setYMargin (4);
    getJTrace ().setSidePanelWidth (JTrace.SidePanelWidth.TEN);
    getJTrace ().setSidePanelColor (Color.black);
    getJTrace ().setMarginStubColor (Color.black);
    // Fix the number of division to match those of the Tek-2440.
    getJTrace ().setXDivisions (Tek2440_GPIB_Instrument.TEK2440_X_DIVISIONS);
    getJTrace ().setYDivisions (Tek2440_GPIB_Instrument.TEK2440_Y_DIVISIONS);
    // Next lines ensure that color scheme is consistent across the channels.
    getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Ch1, null);
    getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Ch2, null);
    getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Add, null);
    getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Mult, null);
    final Color channel1Color = JTrace.DEFAULT_TRACE_COLORS[0];
    final Color channel2Color = JTrace.DEFAULT_TRACE_COLORS[1];
    final Color addColor      = JTrace.DEFAULT_TRACE_COLORS[2];
    final Color multColor     = JTrace.DEFAULT_TRACE_COLORS[3];
    add (getJTrace (), BorderLayout.CENTER);
    
    final JPanel northPanel = new JPanel ();
    add (northPanel, BorderLayout.NORTH);
    
    final JPanel eastPanel = new JPanel ();
    eastPanel.setLayout (new GridLayout (2, 1));
    
    final JPanel eastNorthPanel = new JPanel ();
    eastPanel.add (eastNorthPanel);
        
    final JPanel eastSouthPanel = new JPanel ();
    eastPanel.add (eastSouthPanel);
    
    eastSouthPanel.setLayout (new GridLayout (4, 1));
    
    final JPanel eastSouthNorthPanel = new JPanel ();
    eastSouthPanel.add (eastSouthNorthPanel);
    
    final JPanel eastSouthCenterNorthPanel = new JPanel ();
    eastSouthPanel.add (eastSouthCenterNorthPanel);
    
    final JPanel eastSouthCenterSouthPanel = new JPanel ();
    eastSouthPanel.add (eastSouthCenterSouthPanel);
    
    final JPanel eastSouthSouthPanel = new JPanel ();
    eastSouthPanel.add (eastSouthSouthPanel);
    
    final JPanel southPanel = new JPanel ();
    add (southPanel, BorderLayout.SOUTH);
    
    final JPanel westPanel = new JPanel ();
    add (westPanel, BorderLayout.WEST);
    
    northPanel.setLayout (new FlowLayout (FlowLayout.LEFT, 2, 5));
    
    final JPanel jSimp = getSmallInstrumentManagementPanel ();
    jSimp.setPreferredSize (new Dimension (60, 80));
    jSimp.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Inst"));
    northPanel.add (jSimp);
    
    this.jSerialPollStatus = new JByte (Color.red,
      Arrays.asList (new String[]{"  ?", "RQS", "  ?", "  ?", "  ?", "  ?", "  ?", "  ?"}));
    this.jSerialPollStatus.setPreferredSize (new Dimension (240, 80));
    this.jSerialPollStatus.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Serial Poll"));
    northPanel.add (this.jSerialPollStatus);
    
    final JPanel jAutoSetupPanel = new JTek2440_GPIB_AutoSetup (
      digitalStorageOscilloscope,
      "Auto Setup",
      level + 2,
      DEFAULT_MANAGEMENT_COLOR);
    jAutoSetupPanel.setPreferredSize (new Dimension (280, 80));
    northPanel.add (jAutoSetupPanel);
    
    northPanel.add (new JDialogButton (
      tek2440,
      "Acq",
      new Dimension (80, 80),
      "Tek-2440 Acquisition Settings",
      new Dimension (460, 460),
      new JTek2440_GPIB_Acquisition (digitalStorageOscilloscope, level + 1)));
    
    northPanel.add (new JDialogButton (
      tek2440,
      "Display",
      new Dimension (80, 80),
      "Tek-2440 Display Settings",
      new Dimension (800, 600),
      new JTek2440_GPIB_Display (digitalStorageOscilloscope, level + 1)));
    
    northPanel.add (new JDialogButton (
      tek2440,
      "SRQ/GET",
      new Dimension (80, 80),
      "Tek-2440 GPIB Service Request and GET Settings",
      new Dimension (800, 600),
      new JTek2440_GPIB_SRQ (digitalStorageOscilloscope, level + 1)));
    
    northPanel.add (new JDialogButton (
      tek2440,
      "Ref",
      new Dimension (80, 80),
      "Tek-2440 Reference (Waveform Memory) Settings",
      new Dimension (800, 600),
      new JTek2440_GPIB_Ref (digitalStorageOscilloscope, level + 1)));
    
    northPanel.add (new JDialogButton (
      tek2440,
      "Data",
      new Dimension (80, 80),
      "Tek-2440 Data Settings",
      new Dimension (800, 600),
      new JTek2440_GPIB_Data (digitalStorageOscilloscope, level + 1)));
    
    northPanel.add (new JDialogButton (
      tek2440,
      "Seq",
      new Dimension (80, 80),
      "Tek-2440 Sequencer Settings",
      new Dimension (800, 600),
      new JTek2440_GPIB_Sequencer (digitalStorageOscilloscope, level + 1)));
    
    northPanel.add (new JDialogButton (
      tek2440,
      "Print",
      new Dimension (80, 80),
      "Tek-2440 Print-Device Settings",
      new Dimension (800, 600),
      new JTek2440_GPIB_PrintDevice (digitalStorageOscilloscope, level + 1)));
    
    northPanel.add (new JDialogButton (
      tek2440,
      "Measure",
      new Dimension (80, 80),
      "Tek-2440 Measurement Settings",
      new Dimension (1024, 768),
      new JTek2440_GPIB_Measurement (digitalStorageOscilloscope, level + 1)));
        
    northPanel.add (new JDialogButton (
      tek2440,
      "Cursor",
      new Dimension (80, 80),
      "Tek-2440 Cursor Settings",
      new Dimension (1024, 768),
      new JTek2440_GPIB_Cursor (digitalStorageOscilloscope, level + 1)));
    
    northPanel.add (new JDialogButton (
      tek2440,
      "Wave An",
      new Dimension (80, 80),
      "Tek-2440 Waveform Data Settings",
      new Dimension (800, 600),
      new JTek2440_GPIB_WaveformData (digitalStorageOscilloscope, level + 1)));
    
    eastNorthPanel.setLayout (new GridLayout (1, 2));
    
    eastNorthPanel.add (new JTek2440_GPIB_Channel (
      digitalStorageOscilloscope,
      Tek2440_GPIB_Instrument.Tek2440Channel.Channel1,
      channel1Color,
      "Channel 1",
      level + 1,
      DEFAULT_AMPLITUDE_COLOR,
      (final Boolean channelEnabled) ->
      {
        if (! channelEnabled)
          getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Ch1, null);
      }));
    
    eastNorthPanel.add (new JTek2440_GPIB_Channel (
      digitalStorageOscilloscope,
      Tek2440_GPIB_Instrument.Tek2440Channel.Channel2,
      channel2Color,
      "Channel 2",
      level + 1,
      DEFAULT_AMPLITUDE_COLOR,
      (final Boolean channelEnabled) ->
      {
        if (! channelEnabled)
          getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Ch2, null);
      }));
    
    eastSouthNorthPanel.setLayout (new GridLayout (1, 2));
    
    final JPanel addPanel = new JPanel ();
    addPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_AMPLITUDE_COLOR, 2),
        "Add"));
    addPanel.setLayout (new GridLayout (1, 1));
    addPanel.add (JCenter.XY (new JBoolean_JBoolean (
      "display addition (of channels 1 and 2)",
      (InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).isVModeAdd (),
      tek2440::setAddEnable,
      addColor,
      (b) ->
      {
        // Remove the trace immediately from the Trace panel.
        if (! b) getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Add, null);
      },
      true)));    
    eastSouthNorthPanel.add (addPanel);
    
    final JPanel multPanel = new JPanel ();
    multPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_AMPLITUDE_COLOR, 2),
        "Mult"));
    multPanel.setLayout (new GridLayout (1, 1));
    multPanel.add (JCenter.XY (new JBoolean_JBoolean (
      "display multiplication (of channels 1 and 2)",
      (InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).isVModeMult (),
      tek2440::setMultEnable,
      multColor,
      (b) ->
      {
        // Remove the trace immediately from the Trace panel.
        if (! b) getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Mult, null);
      },
      true)));
    eastSouthNorthPanel.add (multPanel);
    
    eastSouthCenterNorthPanel.setLayout (new GridLayout (1, 1));
    
    final JPanel bandwidthPanel = new JPanel ();
    bandwidthPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_FREQUENCY_COLOR, 2),
        "Bandwidth"));
    bandwidthPanel.setLayout (new GridLayout (1, 1));
    bandwidthPanel.add (JCenter.XY (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.BandwidthLimit.class,
      "bandwidth limit",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getBandwidthLimit (),
      tek2440::setBandwidthLimit,
      true)));
    eastSouthCenterNorthPanel.add (bandwidthPanel);
    
    eastSouthCenterSouthPanel.setLayout (new GridLayout (1, 1));
    
    final JPanel vModeDisplayPanel = new JPanel ();
    vModeDisplayPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Vertical Mode"));
    vModeDisplayPanel.setLayout (new GridLayout (1, 1));
    vModeDisplayPanel.add (JCenter.XY (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.VModeDisplay.class,
      "vertical (display) mode",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getVModeDisplay (),
      tek2440::setVerticalDisplayMode,
      true)));
    eastSouthCenterSouthPanel.add (vModeDisplayPanel);

    eastSouthSouthPanel.setLayout (new GridLayout (1, 3));

    final JPanel ext1GainPanel = new JTek2440_GPIB_ExtGain (
      digitalStorageOscilloscope,
      "Ext 1 Gain",
      level + 1,
      DEFAULT_TRIGGER_COLOR,
      1);
    eastSouthSouthPanel.add (ext1GainPanel);
    
    final JPanel ext2GainPanel = new JTek2440_GPIB_ExtGain (
      digitalStorageOscilloscope,
      "Ext 2 Gain",
      level + 1,
      DEFAULT_TRIGGER_COLOR,
      2);
    eastSouthSouthPanel.add (ext2GainPanel);
    
    eastSouthSouthPanel.add (new JDialogButton (
      tek2440,
      DEFAULT_TIME_COLOR,
      "Delay",
      null,
      "Tek-2440 Delay Settings",
      new Dimension (800, 600),
      new JTek2440_GPIB_Delay (digitalStorageOscilloscope, level + 1)));
    
    add (eastPanel, BorderLayout.EAST);
    
    southPanel.setLayout (new GridLayout (1, 3));
    
    final JPanel horizontalPanel = new JTek2440_GPIB_Horizontal (
      digitalStorageOscilloscope,
      "Horizontal",
      level + 1,
      DEFAULT_TIME_COLOR);
    southPanel.add (horizontalPanel);
    
    final JPanel aTriggerPanel = new JTek2440_GPIB_ATrigger (
      digitalStorageOscilloscope,
      "A Triggering",
      level + 1,
      DEFAULT_TRIGGER_COLOR);
    southPanel.add (aTriggerPanel);
    
    final JPanel bTriggerPanel = new JTek2440_GPIB_BTrigger (
      digitalStorageOscilloscope,
      "B Triggering",
      level + 1,
      DEFAULT_TRIGGER_COLOR);
    southPanel.add (bTriggerPanel);
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }
  
  public JTek2440_GPIB (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] DSO View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // JDefaultDigitalStorageOscilloscopeView
  // AUGMENT TRACE DATA
  // POST SETTING NEW TRACE DATA
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  protected TraceData augmentTraceData (final InstrumentChannel channel, final TraceData traceData)
  {
    return traceData;
  }

  @Override
  protected void postSettingNewTraceData (final InstrumentChannel channel)
  {
    if (channel == null || ! (channel instanceof Tek2440_GPIB_Settings.DataSource))
      return;
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) getInstrument ();
    final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) tek2440.getCurrentInstrumentSettings ();
    // Register a proper Y == 0 marker.
    // XXX We should really have some locking on the TraceDB...
    switch ((Tek2440_GPIB_Settings.DataSource) channel)
    {
      case Ch1:
      case Ch2:
      {
        // Get the Y position of the signal/trace; the returned value is in divisions.
        final double position = settings.getPosition ((Tek2440_GPIB_Settings.DataSource) channel);
        // Transform to display/trace Y value.
        final double position_Y = position
          * settings.getVoltsPerDivision ((Tek2440_GPIB_Settings.DataSource) channel).getVoltsPerDivision_V ();
        // Enable the Y == 0 marker and appropriately set its offset.
        final TraceDB traceDB = getJTrace ().getTraceDB ();
        traceDB.addTraceMarkers (channel, Collections.singleton (TraceMarker.ZERO_Y_SIDE_MARKER));
        traceDB.setYOffset (channel, - position_Y);
        break;
      }
      case Add:
      case Mult:
        LOG.log (Level.WARNING, "Do not know what to do here for channel {0}!", channel);
        break;
      case Ch1Del:
      case Ch2Del:
      case AddDel:
      case MultDel:
        LOG.log (Level.WARNING, "Do not know what to do here for channel {0}!", channel);
        break;
      case Ref1:
      case Ref2:
      case Ref3:
      case Ref4:
        LOG.log (Level.WARNING, "Do not know what to do here for channel {0}!", channel);
        break;
      default:
        throw new RuntimeException ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JByte jSerialPollStatus;
  
  private final class JDialogButton
    extends JInstrumentPanel
  {

    public JDialogButton (
      final Tek2440_GPIB_Instrument instrument,
      final Color boxColor,
      final String boxTitle,
      final Dimension boxPreferredSize,
      final String dialogTitle,
      final Dimension dialogSize,
      final Container contentPane)
    {
      super (instrument, boxTitle, 2, boxColor == null ? DEFAULT_MANAGEMENT_COLOR : boxColor);
      if (contentPane == null)
        throw new IllegalArgumentException ();
      setLayout (new GridLayout (1, 1));
      if (boxPreferredSize != null)
        setPreferredSize (boxPreferredSize);
      final JColorCheckBox jDisplayButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
      jDisplayButton.setDisplayedValue (true);
      final JDialog jDialog = new JOptionPane ().createDialog (dialogTitle);
      if (dialogSize != null)
        jDialog.setSize (dialogSize);
      jDialog.setLocationRelativeTo (null);
      jDialog.setContentPane (contentPane);
      jDisplayButton.addActionListener ((ae) ->
      {
        jDialog.setVisible (true);
      });
      add (JCenter.XY (jDisplayButton));
    }
    
    public JDialogButton (
      final Tek2440_GPIB_Instrument instrument,
      final String boxTitle,
      final Dimension boxPreferredSize,
      final String dialogTitle,
      final Dimension dialogSize,
      final Container contentPane)
    {
      this (
        instrument,
        DEFAULT_MANAGEMENT_COLOR,
        boxTitle,
        boxPreferredSize,
        dialogTitle,
        dialogSize,
        contentPane);
    }
    
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
      if (instrument != JTek2440_GPIB.this.getDigitalStorageOscilloscope () || instrumentStatus == null)
        throw new IllegalArgumentException ();
      if (! (instrumentStatus instanceof Tek2440_GPIB_Status))
        throw new IllegalArgumentException ();
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB.this.jSerialPollStatus.setDisplayedValue (
          ((Tek2440_GPIB_Status) instrumentStatus).getSerialPollStatusByte ());
      });
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      
      if (instrument != JTek2440_GPIB.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      
      if (! settings.isVModeChannel1 ())
        JTek2440_GPIB.this.getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Ch1, null);
      if (! settings.isVModeChannel2 ())
        JTek2440_GPIB.this.getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Ch2, null);
      if (! settings.isVModeAdd ())
        JTek2440_GPIB.this.getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Add, null);
      if (! settings.isVModeMult ())
        JTek2440_GPIB.this.getJTrace ().setTraceData (Tek2440_GPIB_Settings.DataSource.Mult, null);
      
      if (! settings.isVModeChannel1 ())
        JTek2440_GPIB.this.getJTrace ().setTraceData (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, null);
      if (! settings.isVModeChannel2 ())
        JTek2440_GPIB.this.getJTrace ().setTraceData (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, null);
      
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      // EMPTY
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
