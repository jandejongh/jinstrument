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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
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
    
    removeAll ();
    setLayout (new BorderLayout ());
    
    this.xPanel = new JTek2440XPanel (digitalStorageOscilloscope);
    this.aTriggerPanel = new JTek2440_GPIB_ATrigger (digitalStorageOscilloscope, "A Triggering", level + 1, DEFAULT_TRIGGER_COLOR);
    this.bTriggerPanel = new JTek2440_GPIB_BTrigger (digitalStorageOscilloscope, "B Triggering", level + 1, DEFAULT_TRIGGER_COLOR);
    this.jCh1Panel = new JTek2440ChannelPanel (digitalStorageOscilloscope, Tek2440_GPIB_Instrument.Tek2440Channel.Channel1);
    this.jCh2Panel = new JTek2440ChannelPanel (digitalStorageOscilloscope, Tek2440_GPIB_Instrument.Tek2440Channel.Channel2);
    
    getTracePanel ().setBorder (null);
    getTracePanel ().getJTrace ().setXDivisions (Tek2440_GPIB_Instrument.TEK2440_X_DIVISIONS);
    getTracePanel ().getJTrace ().setYDivisions (Tek2440_GPIB_Instrument.TEK2440_Y_DIVISIONS);
    // Next lines ensure that color scheme is consistent across the channels.
    getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, null);
    getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, null);
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
        "SRQ"));
    final JColorCheckBox jSrqButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jSrqButton.setDisplayedValue (true);
    jSrqButton.setHorizontalAlignment (SwingConstants.CENTER);
    this.jSrqDialog = new JOptionPane ().createDialog ("Tek-2440 GPIB Service Request Settings");
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
    eastNorthPanel.add (this.jCh1Panel);
    eastNorthPanel.add (this.jCh2Panel);
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
    this.jBandwithLimit.setEnabled (false);
    centerPanel.add (this.jBandwithLimit);
    
    centerPanel.add (new JLabel ("Display Mode"));
    this.jVModeDisplay = new JComboBox<> (Tek2440_GPIB_Settings.VModeDisplay.values ());
    this.jVModeDisplay.setEditable (false);
    this.jVModeDisplay.setEnabled (false);
    centerPanel.add (this.jVModeDisplay);
    
    centerPanel.add (new JLabel ("Add"));
    this.jAdd = new JColorCheckBox.JBoolean (Color.red);
    this.jAdd.setEnabled (false);
    // this.jAdd.setHorizontalAlignment (SwingConstants.CENTER);
    centerPanel.add (this.jAdd);
    
    centerPanel.add (new JLabel ("Delay"));
    final JColorCheckBox jDelayButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jDelayButton.setDisplayedValue (true);
    // jDelayButton.setHorizontalAlignment (SwingConstants.CENTER);
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
    this.jMul = new JColorCheckBox.JBoolean (Color.red);
    this.jMul.setEnabled (false);
    // this.jMul.setHorizontalAlignment (SwingConstants.CENTER);
    centerPanel.add (this.jMul);
    
    centerPanel.add (new JLabel ());
    centerPanel.add (new JLabel ());
    
    eastSouthPanel.add (centerPanel);
    
    final JPanel extGainPanel = new JTek2440_GPIB_ExtGain (
      digitalStorageOscilloscope,
      "Ext Gain",
      level + 1,
      DEFAULT_TRIGGER_COLOR);
    
    eastSouthPanel.add (extGainPanel);
    
    add (this.eastPanel, BorderLayout.EAST);
    
    this.southPanel = new JPanel ();
    this.southPanel.setLayout (new GridLayout (1, 3));
    this.southPanel.add (this.xPanel);
    this.southPanel.add (this.aTriggerPanel);
    this.southPanel.add (this.bTriggerPanel);    
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
  // TEK 2440 PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static abstract class JTek2440Panel
    extends JDigitalStorageOscilloscopePanel
  {

    public JTek2440Panel (
      final Tek2440_GPIB_Instrument digitalStorageOscilloscope,
      final String title,
      final int level,
      final Color panelColor)
    {
      super (digitalStorageOscilloscope, title, level, panelColor);
    }

    public JTek2440Panel (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
    {
      this (digitalStorageOscilloscope, null, 1, null);
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TEK 2440 X PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTek2440XPanel xPanel;
  
  public class JTek2440XPanel
    extends JTek2440Panel
  {

    public JTek2440XPanel (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
    {
      super (digitalStorageOscilloscope, "X", 1, JInstrumentPanel.getGuiPreferencesTimeColor ());
      setLayout (new GridLayout (5, 2, 0, 10));
      add (new JLabel ("A Timebase"));
      this.jChATimebase = new JComboBox<> (Tek2440_GPIB_Settings.SecondsPerDivision.values ());
      this.jChATimebase.addItemListener (JTek2440_GPIB.this.jChTimebaseListener);
      add (this.jChATimebase);
      add (new JLabel ("B Timebase"));
      this.jChBTimebase = new JComboBox<> (Tek2440_GPIB_Settings.SecondsPerDivision.values ());
      this.jChBTimebase.addItemListener (JTek2440_GPIB.this.jChTimebaseListener);
      add (this.jChBTimebase);
      add (new JLabel ("Hor Ext Expansion"));
      this.jExtExp = new JComboBox (Tek2440_GPIB_Settings.HorizontalExternalExpansionFactor.values ());
      this.jExtExp.setEditable (false);
      this.jExtExp.setEnabled (false);
      add (this.jExtExp);
      add (new JLabel ("Position"));
      this.jPosition = new JSlider (0, 1023);
      this.jPosition.setEnabled (false);
      add (this.jPosition);
      add (new JLabel ("Mode"));
      this.jMode = new JComboBox<> (Tek2440_GPIB_Settings.HorizontalMode.values ());
      this.jMode.setEditable (false);
      this.jMode.setEnabled (false);
      add (this.jMode);
    }
    
    private final JComboBox<Tek2440_GPIB_Settings.SecondsPerDivision> jChATimebase;
    
    private final JComboBox<Tek2440_GPIB_Settings.SecondsPerDivision> jChBTimebase;  
    
    private final JComboBox<Tek2440_GPIB_Settings.HorizontalExternalExpansionFactor> jExtExp;
    
    private final JSlider jPosition;
    
    private final JComboBox<Tek2440_GPIB_Settings.HorizontalMode> jMode;
    
  }
  
  private final ItemListener jChTimebaseListener = (ItemEvent ie) ->
  {
    if (ie.getStateChange () == ItemEvent.SELECTED)
    {
      final Tek2440_GPIB_Settings.SecondsPerDivision newValue = (Tek2440_GPIB_Settings.SecondsPerDivision) ie.getItem ();
      final Tek2440_GPIB_Instrument tek2440 = ((Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ());
      if (! JTek2440_GPIB.this.inhibitInstrumentControl)
        try
        {
          if (ie.getSource () == JTek2440_GPIB.this.xPanel.jChATimebase)
            tek2440.setChannelTimebase (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, newValue);
          else if (ie.getSource () == JTek2440_GPIB.this.xPanel.jChBTimebase)
            tek2440.setChannelTimebase (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, newValue);
          else
            LOG.log (Level.SEVERE, "Unexpected event source!");            
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting Secs/Div on instrument"
            + " from combo box to {0}: {1}.",
            new Object[]{newValue, e});
        }
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TEK 2440 TRIGGER PANELS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTek2440_GPIB_ATrigger aTriggerPanel;
  
  private final JTek2440_GPIB_BTrigger bTriggerPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TEK 2440 CHANNEL PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTek2440ChannelPanel jCh1Panel;
  
  private final JTek2440ChannelPanel jCh2Panel;
  
  public class JTek2440ChannelPanel
    extends JTek2440Panel
  {

    private final Tek2440_GPIB_Instrument.Tek2440Channel channel;
    
    public JTek2440ChannelPanel (
      final Tek2440_GPIB_Instrument digitalStorageOscilloscope,
      final Tek2440_GPIB_Instrument.Tek2440Channel channel)
    {
      super (digitalStorageOscilloscope, "Channel " + channel.toString (), 1, JInstrumentPanel.getGuiPreferencesAmplitudeColor ());
      if (channel == null)
        throw new IllegalArgumentException ();
      this.channel = channel;
      setLayout (new GridLayout (6,2));
      add (new JLabel("Enable"));
      switch (channel)
      {
        case Channel1:
          this.jEnabled = new JColorCheckBox.JBoolean (JTrace.DEFAULT_COLORS[0]);
          break;
        case Channel2:
          this.jEnabled = new JColorCheckBox.JBoolean (JTrace.DEFAULT_COLORS[1]);
          break;
        default:
          throw new IllegalArgumentException ();
      }
      this.jEnabled.setHorizontalAlignment (SwingConstants.CENTER);
      this.jEnabled.addActionListener (this.jEnabledListener);
      add (this.jEnabled);
      add (new JLabel ("V/Div"));
      this.jVoltsPerDiv = new JComboBox<> (Tek2440_GPIB_Settings.VoltsPerDivision.values ());
      this.jVoltsPerDiv.addItemListener (this.jVoltsPerDivListener);
      add (this.jVoltsPerDiv);
      add (new JLabel("Var"));
      this.jVar = new JSlider (0, 100);
      this.jVar.setPreferredSize (new Dimension (100, 40));
      this.jVar.addChangeListener (this.jVarChangeListener);
      add (this.jVar);
      add (new JLabel("Coupling"));
      this.jCoupling = new JComboBox<> (Tek2440_GPIB_Settings.ChannelCoupling.values ());
      this.jCoupling.addItemListener (this.jCouplingListener);
      add (this.jCoupling);
      final JPanel j50Combi = new JPanel ();
      j50Combi.setLayout (new GridLayout (1, 2));
      j50Combi.add (new JLabel("50 \u03A9"));
      this.jFifty = new JColorCheckBox.JBoolean (Color.red);
      this.jFifty.setHorizontalAlignment (SwingConstants.CENTER);
      this.jFifty.addActionListener (this.jFiftyListener);
      j50Combi.add (this.jFifty);
      add (j50Combi);
      final JPanel jInvertCombi = new JPanel ();
      jInvertCombi.setLayout (new GridLayout (1, 2));
      jInvertCombi.add (new JLabel("Invert"));
      this.jInvert = new JColorCheckBox.JBoolean (Color.red);
      this.jInvert.setHorizontalAlignment (SwingConstants.CENTER);
      this.jInvert.addActionListener (this.jInvertListener);
      jInvertCombi.add (this.jInvert);
      add (jInvertCombi);
      add (new JLabel("Pos [DIV]"));
      this.jPosition = new JSlider (-10, 10);
      this.jPosition.setPreferredSize (new Dimension (100, 40));
      this.jPosition.setMajorTickSpacing (10);
      this.jPosition.setMinorTickSpacing (1);
      this.jPosition.setPaintTicks (true);
      this.jPosition.setToolTipText ("-");
      this.jPosition.addChangeListener (this.jPositionChangeListener);
      add (this.jPosition);
    }
    
    private final JColorCheckBox.JBoolean jEnabled;

    private final ActionListener jEnabledListener = (final ActionEvent ae) ->
    {
      final Boolean currentValue = JTek2440ChannelPanel.this.jEnabled.getDisplayedValue ();
      final boolean newValue = currentValue == null || (! currentValue);
      try
      {
        final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
        tek.setChannelEnable (JTek2440ChannelPanel.this.channel, newValue);
        if (! newValue)
          getTracePanel ().getJTrace ().setTrace (JTek2440ChannelPanel.this.channel, null);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while setting channel enable {0} on instrument {1}: {2}.",        
          new Object[]
            {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    };
  
    private final JComboBox<Tek2440_GPIB_Settings.VoltsPerDivision> jVoltsPerDiv;
    
    private final ItemListener jVoltsPerDivListener = (ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final Tek2440_GPIB_Settings.VoltsPerDivision newValue = (Tek2440_GPIB_Settings.VoltsPerDivision) ie.getItem ();
        final Tek2440_GPIB_Instrument tek2440 = ((Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ());
        if (! JTek2440_GPIB.this.inhibitInstrumentControl)
          try
          {
            tek2440.setVoltsPerDiv (JTek2440ChannelPanel.this.channel, newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.WARNING, "Caught exception while setting V/Div on instrument"
              + " from combo box to {0}: {1}.",
              new Object[]{newValue, Arrays.toString (e.getStackTrace ())});
          }
      }
    };
    
    private final JSlider jVar;
       
    private final ChangeListener jVarChangeListener = (final ChangeEvent ce) ->
    {
      final JSlider source = (JSlider) ce.getSource ();
      source.setToolTipText (Integer.toString (source.getValue ()));
      if (! source.getValueIsAdjusting ())
      {
        if (! JTek2440_GPIB.this.inhibitInstrumentControl)
          try
          {
            final double newValue = source.getValue ();
            final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
            tek.setChannelVariableY (JTek2440ChannelPanel.this.channel, newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting channel variable-Y from slider to {0}: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
    };
  
    private final JComboBox<Tek2440_GPIB_Settings.ChannelCoupling> jCoupling;
  
    private final ItemListener jCouplingListener = (ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final Tek2440_GPIB_Settings.ChannelCoupling newValue = (Tek2440_GPIB_Settings.ChannelCoupling) ie.getItem ();
        final Tek2440_GPIB_Instrument tek2440 = ((Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ());
        if (! JTek2440_GPIB.this.inhibitInstrumentControl)
          try
          {
            tek2440.setChannelCoupling (JTek2440ChannelPanel.this.channel, newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.WARNING, "Caught exception while setting channel coupling on instrument"
              + " from combo box to {0}: {1}.",
              new Object[]{newValue, Arrays.toString (e.getStackTrace ())});
          }
      }
    };
    
    private final JColorCheckBox.JBoolean jFifty;
    
    private final ActionListener jFiftyListener = (final ActionEvent ae) ->
    {
      final Boolean currentValue = JTek2440ChannelPanel.this.jFifty.getDisplayedValue ();
      final boolean newValue = currentValue == null || (! currentValue);
      try
      {
        final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
        tek.setChannelFiftyOhms (JTek2440ChannelPanel.this.channel, newValue);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while setting channel 50 Ohms {0} on instrument {1}: {2}.",        
          new Object[]
            {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    };
  
    private final JColorCheckBox.JBoolean jInvert;
    
    private final ActionListener jInvertListener = (final ActionEvent ae) ->
    {
      final Boolean currentValue = JTek2440ChannelPanel.this.jInvert.getDisplayedValue ();
      final boolean newValue = currentValue == null || (! currentValue);
      try
      {
        final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
        tek.setChannelInvert (JTek2440ChannelPanel.this.channel, newValue);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while setting channel invert {0} on instrument {1}: {2}.",        
          new Object[]
            {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    };
  
    private final JSlider jPosition;
    
    private final ChangeListener jPositionChangeListener = (final ChangeEvent ce) ->
    {
      final JSlider source = (JSlider) ce.getSource ();
      source.setToolTipText (Integer.toString (source.getValue ()));
      if (! source.getValueIsAdjusting ())
      {
        if (! JTek2440_GPIB.this.inhibitInstrumentControl)
          try
          {
            final double newValue = source.getValue ();
            final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
            tek.setChannelPosition (JTek2440ChannelPanel.this.channel, newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting channel position from slider to {0}: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
    };
  
  }
    
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
        JTek2440_GPIB.this.inhibitInstrumentControl = true;
        try
        {
          JTek2440_GPIB.this.xPanel.jChATimebase.setSelectedItem (settings.getASecondsPerDivision ());
          JTek2440_GPIB.this.xPanel.jChBTimebase.setSelectedItem (settings.getBSecondsPerDivision ());
          JTek2440_GPIB.this.xPanel.jExtExp.setSelectedItem (settings.getHorizontalExternalExpansionFactor ());
          final int roundedXPosition = (int) Math.round (settings.getHorizontalPosition ());
          JTek2440_GPIB.this.xPanel.jPosition.setValue (roundedXPosition);
          JTek2440_GPIB.this.xPanel.jPosition.setToolTipText (Integer.toString (roundedXPosition));
          JTek2440_GPIB.this.xPanel.jMode.setSelectedItem (settings.getHorizontalMode ());
          JTek2440_GPIB.this.jCh1Panel.jEnabled.setDisplayedValue (settings.isVModeChannel1 ());
          JTek2440_GPIB.this.jCh2Panel.jEnabled.setDisplayedValue (settings.isVModeChannel2 ());
          if (! settings.isVModeChannel1 ())
            getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, null);
          if (! settings.isVModeChannel2 ())
            getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, null);
          JTek2440_GPIB.this.jCh1Panel.jVoltsPerDiv.setSelectedItem (settings.getAVoltsPerDivision ());
          JTek2440_GPIB.this.jCh2Panel.jVoltsPerDiv.setSelectedItem (settings.getBVoltsPerDivision ());
          JTek2440_GPIB.this.jCh1Panel.jVar.setValue ((int) Math.round (settings.getCh1VariableVoltsPerDivision ()));
          JTek2440_GPIB.this.jCh1Panel.jVar.setToolTipText
            (Integer.toString ((int) Math.round (settings.getCh1VariableVoltsPerDivision ())));
          JTek2440_GPIB.this.jCh2Panel.jVar.setValue ((int) Math.round (settings.getCh2VariableVoltsPerDivision ()));
          JTek2440_GPIB.this.jCh2Panel.jVar.setToolTipText
            (Integer.toString ((int) Math.round (settings.getCh2VariableVoltsPerDivision ())));
          JTek2440_GPIB.this.jCh1Panel.jCoupling.setSelectedItem
            (settings.getChannelCoupling (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1));
          JTek2440_GPIB.this.jCh2Panel.jCoupling.setSelectedItem
            (settings.getChannelCoupling (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2));
          JTek2440_GPIB.this.jCh1Panel.jFifty.setDisplayedValue
            (settings.isFiftyOhms (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1));
          JTek2440_GPIB.this.jCh2Panel.jFifty.setDisplayedValue
            (settings.isFiftyOhms (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2));
          JTek2440_GPIB.this.jCh1Panel.jInvert.setDisplayedValue
            (settings.isInvert (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1));
          JTek2440_GPIB.this.jCh2Panel.jInvert.setDisplayedValue
            (settings.isInvert (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2));
          final int ch1PositionDiv = (int) Math.round (settings.getPosition (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1));
          final int ch2PositionDiv = (int) Math.round (settings.getPosition (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2));
          JTek2440_GPIB.this.jCh1Panel.jPosition.setValue (ch1PositionDiv);
          JTek2440_GPIB.this.jCh1Panel.jPosition.setToolTipText (Integer.toString (ch1PositionDiv));
          JTek2440_GPIB.this.jCh2Panel.jPosition.setValue (ch2PositionDiv);
          JTek2440_GPIB.this.jCh2Panel.jPosition.setToolTipText (Integer.toString (ch2PositionDiv));
          JTek2440_GPIB.this.jBandwithLimit.setSelectedItem (settings.getBandwidthLimit ());
          JTek2440_GPIB.this.jAdd.setDisplayedValue (settings.isVModeAdd ());
          JTek2440_GPIB.this.jMul.setDisplayedValue (settings.isVModeMult ());
          JTek2440_GPIB.this.jVModeDisplay.setSelectedItem (settings.getVModeDisplay ());          
        }
        finally
        {
          JTek2440_GPIB.this.inhibitInstrumentControl = false;
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
