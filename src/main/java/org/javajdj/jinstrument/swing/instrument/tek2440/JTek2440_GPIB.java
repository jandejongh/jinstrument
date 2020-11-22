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
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
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
import org.javajdj.jswing.jtrace.JTraceDisplay;

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
    
    getTracePanel ().setBorder (null);
    getTracePanel ().getJTrace ().setXDivisions (Tek2440_GPIB_Instrument.TEK2440_X_DIVISIONS);
    getTracePanel ().getJTrace ().setYDivisions (Tek2440_GPIB_Instrument.TEK2440_Y_DIVISIONS);
    // Next lines ensure that color scheme is consistent across the channels.
    getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Ch1, null);
    getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Ch2, null);
    getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Add, null);
    getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Mult, null);
    final Color channel1Color = JTraceDisplay.DEFAULT_COLORS[0];
    final Color channel2Color = JTraceDisplay.DEFAULT_COLORS[1];
    final Color addColor      = JTraceDisplay.DEFAULT_COLORS[2];
    final Color multColor     = JTraceDisplay.DEFAULT_COLORS[3];
    add (getTracePanel (), BorderLayout.CENTER);
    
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
      "Tek-2440 Service Reference (Waveform Memory) Settings",
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
          getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Ch1, null);
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
          getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Ch2, null);
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
        if (! b) getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Add, null);
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
        if (! b) getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Mult, null);
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
        getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Ch1, null);
      if (! settings.isVModeChannel2 ())
        getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Ch2, null);
      if (! settings.isVModeAdd ())
        getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Add, null);
      if (! settings.isVModeMult ())
        getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Settings.DataSource.Mult, null);
      
      if (! settings.isVModeChannel1 ())
        getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, null);
      if (! settings.isVModeChannel2 ())
        getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, null);
      
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
