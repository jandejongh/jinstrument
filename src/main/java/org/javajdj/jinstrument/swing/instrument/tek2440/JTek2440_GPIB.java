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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
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
import org.javajdj.jinstrument.swing.default_view.JDefaultDigitalStorageOscilloscopeView;
import org.javajdj.jswing.jbyte.JByte;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jtrace.JTrace;

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
    getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, null);
    getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, null);
    this.channel1Color = JTrace.DEFAULT_COLORS[0];
    this.channel2Color = JTrace.DEFAULT_COLORS[1];        
    add (getTracePanel (), BorderLayout.CENTER);
    
    this.northPanel = new JPanel ();    
    this.northPanel.setLayout (new FlowLayout (FlowLayout.LEFT, 2, 5));
    
    final JPanel jSimp = getSmallInstrumentManagementPanel ();
    jSimp.setPreferredSize (new Dimension (60, 80));
    jSimp.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Inst"));
    this.northPanel.add (jSimp);
    
    this.jSerialPollStatus = new JByte (Color.red,
      Arrays.asList (new String[]{"  ?", "RQS", "  ?", "  ?", "  ?", "  ?", "  ?", "  ?"}));
    this.jSerialPollStatus.setPreferredSize (new Dimension (240, 80));
    this.jSerialPollStatus.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Serial Poll"));
    this.northPanel.add (this.jSerialPollStatus);
    
    final JPanel jAutoSetupPanel = new JTek2440_GPIB_AutoSetup (
      digitalStorageOscilloscope,
      "Auto Setup",
      level + 2,
      DEFAULT_MANAGEMENT_COLOR);
    jAutoSetupPanel.setPreferredSize (new Dimension (200, 80));
    this.northPanel.add (jAutoSetupPanel);
    
    final JPanel jAcquisitionPanel = new JPanel ();
    jAcquisitionPanel.setLayout (new BorderLayout ());
    jAcquisitionPanel.setPreferredSize (new Dimension (80, 80));
    jAcquisitionPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Acq"));
    final JColorCheckBox jAcquisitionButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jAcquisitionButton.setDisplayedValue (true);
    jAcquisitionButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jAcquisitionDialog = new JOptionPane ().createDialog ("Tek-2440 Acquisition Settings");
    this.jAcquisitionDialog.setSize (460, 460);
    this.jAcquisitionDialog.setLocationRelativeTo (this);
    this.jAcquisitionDialog.setContentPane(new JTek2440_GPIB_Acquisition (digitalStorageOscilloscope, level + 1));
    jAcquisitionButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jAcquisitionDialog.setVisible (true);
    });
    jAcquisitionPanel.add (jAcquisitionButton, BorderLayout.CENTER);
    this.northPanel.add (jAcquisitionPanel);
    
    final JPanel jDisplayPanel = new JPanel ();
    jDisplayPanel.setLayout (new BorderLayout ());
    jDisplayPanel.setPreferredSize (new Dimension (80, 80));
    jDisplayPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Display"));
    final JColorCheckBox jDisplayButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jDisplayButton.setDisplayedValue (true);
    jDisplayButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jDisplayDialog = new JOptionPane ().createDialog ("Tek-2440 Display Settings");
    this.jDisplayDialog.setSize (800, 600);
    this.jDisplayDialog.setLocationRelativeTo (this);
    this.jDisplayDialog.setContentPane(new JTek2440_GPIB_Display (digitalStorageOscilloscope, level + 1));
    jDisplayButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jDisplayDialog.setVisible (true);
    });
    jDisplayPanel.add (jDisplayButton, BorderLayout.CENTER);
    this.northPanel.add (jDisplayPanel);
    
    final JPanel jSrqPanel = new JPanel ();
    jSrqPanel.setLayout (new BorderLayout ());
    jSrqPanel.setPreferredSize (new Dimension (80, 80));
    jSrqPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "SRQ/GET"));
    final JColorCheckBox jSrqButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jSrqButton.setDisplayedValue (true);
    jSrqButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jSrqDialog = new JOptionPane ().createDialog ("Tek-2440 GPIB Service Request and GET Settings");
    this.jSrqDialog.setSize (800, 600);
    this.jSrqDialog.setLocationRelativeTo (this);
    this.jSrqDialog.setContentPane(new JTek2440_GPIB_SRQ (digitalStorageOscilloscope, level + 1));
    jSrqButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jSrqDialog.setVisible (true);
    });
    jSrqPanel.add (jSrqButton, BorderLayout.CENTER);
    this.northPanel.add (jSrqPanel);
    
    final JPanel jRefPanel = new JPanel ();
    jRefPanel.setLayout (new BorderLayout ());
    jRefPanel.setPreferredSize (new Dimension (80, 80));
    jRefPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Ref"));
    final JColorCheckBox jRefButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jRefButton.setDisplayedValue (true);
    jRefButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jRefDialog = new JOptionPane ().createDialog ("Tek-2440 Service Reference (Waveform Memory) Settings");
    this.jRefDialog.setSize (800, 600);
    this.jRefDialog.setLocationRelativeTo (this);
    this.jRefDialog.setContentPane(new JTek2440_GPIB_Ref (digitalStorageOscilloscope, level + 1));
    jRefButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jRefDialog.setVisible (true);
    });
    jRefPanel.add (jRefButton, BorderLayout.CENTER);
    this.northPanel.add (jRefPanel);
    
    final JPanel jDataPanel = new JPanel ();
    jDataPanel.setLayout (new BorderLayout ());
    jDataPanel.setPreferredSize (new Dimension (80, 80));
    jDataPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Data"));
    final JColorCheckBox jDataButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jDataButton.setDisplayedValue (true);
    jDataButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jDataDialog = new JOptionPane ().createDialog ("Tek-2440 Data Settings");
    this.jDataDialog.setSize (800, 600);
    this.jDataDialog.setLocationRelativeTo (this);
    this.jDataDialog.setContentPane(new JTek2440_GPIB_Data (digitalStorageOscilloscope, level + 1));
    jDataButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jDataDialog.setVisible (true);
    });
    jDataPanel.add (jDataButton, BorderLayout.CENTER);
    this.northPanel.add (jDataPanel);
    
    final JPanel jSequencerPanel = new JPanel ();
    jSequencerPanel.setLayout (new BorderLayout ());
    jSequencerPanel.setPreferredSize (new Dimension (80, 80));
    jSequencerPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Seq"));
    final JColorCheckBox jSequencerButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jSequencerButton.setDisplayedValue (true);
    jSequencerButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jSequencerDialog = new JOptionPane ().createDialog ("Tek-2440 Sequencer Settings");
    this.jSequencerDialog.setSize (800, 600);
    this.jSequencerDialog.setLocationRelativeTo (this);
    this.jSequencerDialog.setContentPane(new JTek2440_GPIB_Sequencer (digitalStorageOscilloscope, level + 1));
    jSequencerButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jSequencerDialog.setVisible (true);
    });
    jSequencerPanel.add (jSequencerButton, BorderLayout.CENTER);
    this.northPanel.add (jSequencerPanel);
    
    add (northPanel, BorderLayout.NORTH);
    
    this.eastPanel = new JPanel ();
    this.eastPanel.setLayout (new GridLayout (2, 1));
    
    final JPanel eastNorthPanel = new JPanel ();
    eastNorthPanel.setLayout (new GridLayout (1, 2));
    
    final JPanel jCh1Panel = new JTek2440_GPIB_Channel (
      digitalStorageOscilloscope,
      Tek2440_GPIB_Instrument.Tek2440Channel.Channel1,
      channel1Color,
      "Channel 1",
      level + 1,
      DEFAULT_AMPLITUDE_COLOR);
    eastNorthPanel.add (jCh1Panel);
    
    final JPanel jCh2Panel = new JTek2440_GPIB_Channel (
      digitalStorageOscilloscope,
      Tek2440_GPIB_Instrument.Tek2440Channel.Channel2,
      channel2Color,
      "Channel 2",
      level + 1,
      DEFAULT_AMPLITUDE_COLOR);
    eastNorthPanel.add (jCh2Panel);
    
    this.eastPanel.add (eastNorthPanel);
    
    final JPanel eastSouthPanel = new JPanel ();
    eastSouthPanel.setLayout (new GridLayout (2, 1));
    this.eastPanel.add (eastSouthPanel);
    
    final JPanel centerPanel = new JPanel ();
    centerPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Generic"));
    centerPanel.setLayout (new GridLayout (3, 4, 4, 2));
    
    centerPanel.add (new JLabel ("Bandwidth"));
    this.jBandwithLimit = new JComboBox<> (Tek2440_GPIB_Settings.BandwidthLimit.values ());
    this.jBandwithLimit.setEditable (false);
    this.jBandwithLimit.setSelectedItem (null);
    this.jBandwithLimit.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "bandwidth limit",
      Tek2440_GPIB_Settings.BandwidthLimit.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.BandwidthLimit>) tek2440::setBandwidthLimit,
      JTek2440_GPIB.this::isInhibitInstrumentControl));
    centerPanel.add (this.jBandwithLimit);
    
    centerPanel.add (new JLabel ("Display Mode"));
    this.jVModeDisplay = new JComboBox<> (Tek2440_GPIB_Settings.VModeDisplay.values ());
    this.jVModeDisplay.setSelectedItem (null);
    this.jVModeDisplay.setEditable (false);
    this.jVModeDisplay.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "vertical (display) mode",
      Tek2440_GPIB_Settings.VModeDisplay.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.VModeDisplay>) tek2440::setVerticalDisplayMode,
      this::isInhibitInstrumentControl));
    centerPanel.add (this.jVModeDisplay);
    
    centerPanel.add (new JLabel ("Add"));
    this.jAdd = new JColorCheckBox.JBoolean (Color.green);
    this.jAdd.addActionListener (new JInstrumentActionListener_1Boolean (
      "display addition (of channels 1 and 2)",
      this.jAdd::getDisplayedValue,
      tek2440::setAddEnable,
      this::isInhibitInstrumentControl));
    centerPanel.add (this.jAdd);
    
    centerPanel.add (new JLabel ("Delay"));
    final JColorCheckBox jDelayButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jDelayButton.setDisplayedValue (true);
    this.jDelayDialog = new JOptionPane ().createDialog ("Tek-2440 Delay Settings");
    this.jDelayDialog.setSize (800, 600);
    this.jDelayDialog.setLocationRelativeTo (this);
    this.jDelayDialog.setContentPane (new JTek2440_GPIB_Delay (digitalStorageOscilloscope, level + 1));
    jDelayButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jDelayDialog.setVisible (true);
    });
    centerPanel.add (jDelayButton);
    
    centerPanel.add (new JLabel ("Mul"));
    this.jMul = new JColorCheckBox.JBoolean (Color.green);
    this.jMul.addActionListener (new JInstrumentActionListener_1Boolean (
      "display multiplication (of channels 1 and 2)",
      this.jMul::getDisplayedValue,
      tek2440::setMultEnable,
      this::isInhibitInstrumentControl));
    centerPanel.add (this.jMul);
    
    centerPanel.add (new JLabel ());
    centerPanel.add (new JLabel ());
    
    eastSouthPanel.add (centerPanel);
    
    final JPanel eastSouthSouthPanel = new JPanel ();
    eastSouthPanel.add (eastSouthSouthPanel);
    
    eastSouthSouthPanel.setLayout (new GridLayout (1, 2));
    
    final JPanel extGainPanel = new JTek2440_GPIB_ExtGain (
      digitalStorageOscilloscope,
      "Ext Gain",
      level + 1,
      DEFAULT_TRIGGER_COLOR);
    
    eastSouthSouthPanel.add (extGainPanel);
    
    final JLabel eastSouthSouthWestPanel = new JLabel ();
//    eastSouthSouthWestPanel.setBorder (
//      BorderFactory.createTitledBorder (
//        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
//        "Misc"));
    eastSouthSouthPanel.add (eastSouthSouthWestPanel);
    
    eastSouthSouthWestPanel.setLayout (new GridLayout (2, 2));
    
    final JPanel jPrintDevicePanel = new JPanel ();
    jPrintDevicePanel.setLayout (new BorderLayout ());
    jPrintDevicePanel.setPreferredSize (new Dimension (80, 80));
    jPrintDevicePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Print"));
    final JColorCheckBox jPrintDeviceButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jPrintDeviceButton.setDisplayedValue (true);
    jPrintDeviceButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jPrintDeviceDialog = new JOptionPane ().createDialog ("Tek-2440 Print Device Settings");
    this.jPrintDeviceDialog.setSize (800, 600);
    this.jPrintDeviceDialog.setLocationRelativeTo (this);
    this.jPrintDeviceDialog.setContentPane(new JTek2440_GPIB_PrintDevice (digitalStorageOscilloscope, level + 1));
    jPrintDeviceButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jPrintDeviceDialog.setVisible (true);
    });
    jPrintDevicePanel.add (jPrintDeviceButton, BorderLayout.CENTER);
    eastSouthSouthWestPanel.add (jPrintDevicePanel);
    
    final JPanel jMeasurementPanel = new JPanel ();
    jMeasurementPanel.setLayout (new BorderLayout ());
    jMeasurementPanel.setPreferredSize (new Dimension (80, 80));
    jMeasurementPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Measure"));
    final JColorCheckBox jMeasurementButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jMeasurementButton.setDisplayedValue (true);
    jMeasurementButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jMeasurementDialog = new JOptionPane ().createDialog ("Tek-2440 Measurement Settings");
    this.jMeasurementDialog.setSize (800, 600);
    this.jMeasurementDialog.setLocationRelativeTo (this);
    this.jMeasurementDialog.setContentPane(new JTek2440_GPIB_Measurement (digitalStorageOscilloscope, level + 1));
    jMeasurementButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jMeasurementDialog.setVisible (true);
    });
    jMeasurementPanel.add (jMeasurementButton, BorderLayout.CENTER);
    eastSouthSouthWestPanel.add (jMeasurementPanel);
    
    final JPanel jCursorPanel = new JPanel ();
    jCursorPanel.setLayout (new BorderLayout ());
    jCursorPanel.setPreferredSize (new Dimension (80, 80));
    jCursorPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Cursor"));
    final JColorCheckBox jCursorButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jCursorButton.setDisplayedValue (true);
    jCursorButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jCursorDialog = new JOptionPane ().createDialog ("Tek-2440 Cursor Settings");
    this.jCursorDialog.setSize (800, 600);
    this.jCursorDialog.setLocationRelativeTo (this);
    this.jCursorDialog.setContentPane(new JTek2440_GPIB_Cursor (digitalStorageOscilloscope, level + 1));
    jCursorButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jCursorDialog.setVisible (true);
    });
    jCursorPanel.add (jCursorButton, BorderLayout.CENTER);
    eastSouthSouthWestPanel.add (jCursorPanel);
    
    final JPanel jWaveformDataPanel = new JPanel ();
    jWaveformDataPanel.setLayout (new BorderLayout ());
    jWaveformDataPanel.setPreferredSize (new Dimension (80, 80));
    jWaveformDataPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Wave Data"));
    final JColorCheckBox jWaveformDataButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jWaveformDataButton.setDisplayedValue (true);
    jWaveformDataButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jWaveformDataDialog = new JOptionPane ().createDialog ("Tek-2440 Waveform Data Settings");
    this.jWaveformDataDialog.setSize (800, 600);
    this.jWaveformDataDialog.setLocationRelativeTo (this);
    this.jWaveformDataDialog.setContentPane(new JTek2440_GPIB_WaveformData (digitalStorageOscilloscope, level + 1));
    jWaveformDataButton.addActionListener ((ae) ->
    {
      JTek2440_GPIB.this.jWaveformDataDialog.setVisible (true);
    });
    jWaveformDataPanel.add (jWaveformDataButton, BorderLayout.CENTER);
    eastSouthSouthWestPanel.add (jWaveformDataPanel);
    
    add (this.eastPanel, BorderLayout.EAST);
    
    this.southPanel = new JPanel ();
    this.southPanel.setLayout (new GridLayout (1, 3));
    final JPanel horizontalPanel = new JTek2440_GPIB_Horizontal (
      digitalStorageOscilloscope,
      "Horizontal",
      level + 1,
      DEFAULT_TIME_COLOR);
    this.southPanel.add (horizontalPanel);
    
    final JPanel aTriggerPanel = new JTek2440_GPIB_ATrigger (
      digitalStorageOscilloscope,
      "A Triggering",
      level + 1,
      DEFAULT_TRIGGER_COLOR);
    this.southPanel.add (aTriggerPanel);
    
    final JPanel bTriggerPanel = new JTek2440_GPIB_BTrigger (
      digitalStorageOscilloscope,
      "B Triggering",
      level + 1,
      DEFAULT_TRIGGER_COLOR);
    this.southPanel.add (bTriggerPanel);
    
    add (this.southPanel, BorderLayout.SOUTH);
    
    this.westPanel = new JPanel ();
    // this.westPanel.add (new JLabel ("west"));
    add (this.westPanel, BorderLayout.WEST);
    
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
  // CHANNEL COLORS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Color channel1Color;
  
  private final Color channel2Color;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // NORTH/EAST/SOUTH/WEST PANELS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JPanel northPanel;
  
  private final JPanel eastPanel;
  
  private final JPanel southPanel;
  
  private final JPanel westPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // NORTH PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JByte jSerialPollStatus;
  
  private final JDialog jDisplayDialog;
  
  private final JDialog jAcquisitionDialog;
  
  private final JDialog jSrqDialog;
  
  private final JDialog jRefDialog;
  
  private final JDialog jDataDialog;
  
  private final JDialog jSequencerDialog;
  
  private final JDialog jPrintDeviceDialog;
  
  private final JDialog jMeasurementDialog;
  
  private final JDialog jCursorDialog;
  
  private final JDialog jWaveformDataDialog;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // EAST SOUTH PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JDialog jDelayDialog;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // VERTICAL MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JColorCheckBox.JBoolean jAdd;
    
  private final JColorCheckBox.JBoolean jMul;
  
  private final JComboBox<Tek2440_GPIB_Settings.VModeDisplay> jVModeDisplay;
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // BANDWIDTH LIMIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JComboBox<Tek2440_GPIB_Settings.BandwidthLimit> jBandwithLimit;
    
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
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB.this.setInhibitInstrumentControl ();
        try
        {
          if (! settings.isVModeChannel1 ())
            getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, null);
          if (! settings.isVModeChannel2 ())
            getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, null);
          JTek2440_GPIB.this.jBandwithLimit.setSelectedItem (settings.getBandwidthLimit ());
          JTek2440_GPIB.this.jAdd.setDisplayedValue (settings.isVModeAdd ());
          JTek2440_GPIB.this.jMul.setDisplayedValue (settings.isVModeMult ());
          JTek2440_GPIB.this.jVModeDisplay.setSelectedItem (settings.getVModeDisplay ());          
        }
        finally
        {
          JTek2440_GPIB.this.resetInhibitInstrumentControl ();
        }
      });
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
