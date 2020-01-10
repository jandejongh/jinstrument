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
package org.javajdj.jinstrument.swing.instrument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
import org.javajdj.jinstrument.gpib.fg.hp8116a.HP8116A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fg.hp8116a.HP8116A_GPIB_Settings;
import org.javajdj.jinstrument.gpib.fg.hp8116a.HP8116A_GPIB_Status;
import org.javajdj.jinstrument.swing.JDefaultFunctionGeneratorView;
import org.javajdj.jswing.jbyte.JByte;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A Swing panel for (complete) control and status of a {@link HP8116A_GPIB_Instrument} Function Generator.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP8116A_GPIB
  extends JDefaultFunctionGeneratorView
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP8116A_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP8116A_GPIB (final HP8116A_GPIB_Instrument functionGenerator, final int level)
  {
    //
    super (functionGenerator, level);
    //
    this.jInstrumentSpecificPanel.removeAll ();
    setPanelBorder (
      this.jInstrumentSpecificPanel,
      getLevel () + 1,
      getGuiPreferencesManagementColor (),
      "HP-8116A Specific");
    this.jInstrumentSpecificPanel.setLayout (new GridLayout (5, 2));
    //
    this.jSerialPollStatus = new JByte (Color.red,
      Arrays.asList (new String[]{"BNE", "SRQ", "SWP", "VRN", "SYSF", "STX", "PERR", "TERR"}));
    setPanelBorder (
      this.jSerialPollStatus,
      level + 2,
      getGuiPreferencesManagementColor (),
      "Serial Poll Status [Read-Only]");
    this.jInstrumentSpecificPanel.add (this.jSerialPollStatus);
    //
    this.jControlMode = new JComboBox<> (HP8116A_GPIB_Instrument.HP8116AControlMode.values ());
    setPanelBorder (
      this.jControlMode,
      level + 2,
      getGuiPreferencesModulationColor (),
      "Control Mode");
    this.jControlMode.setSelectedItem (null);
    this.jControlMode.addActionListener (this.jControlModeListener);
    this.jInstrumentSpecificPanel.add (this.jControlMode);
    //
    this.jTriggerMode = new JComboBox<> (HP8116A_GPIB_Instrument.HP8116ATriggerMode.values ());
    setPanelBorder (
      this.jTriggerMode,
      level + 2,
      getGuiPreferencesTriggerColor (),
      "Trigger Mode");
    this.jTriggerMode.setSelectedItem (null);
    this.jTriggerMode.addActionListener (this.jTriggerModeListener);    
    this.jInstrumentSpecificPanel.add (this.jTriggerMode);
    //
    this.jTriggerControl = new JComboBox<> (HP8116A_GPIB_Instrument.HP8116ATriggerControl.values ());
    setPanelBorder (
      this.jTriggerControl,
      level + 2,
      getGuiPreferencesTriggerColor (),
      "Trigger Control");
    this.jTriggerControl.setSelectedItem (null);
    this.jTriggerControl.addActionListener (this.jTriggerControlListener);
    this.jInstrumentSpecificPanel.add (this.jTriggerControl);
    //
    final JPanel jPhase = new JPanel ();
    jPhase.setLayout (new GridLayout (1, 2));
    this.jStartPhase = new JComboBox<> (HP8116A_GPIB_Instrument.HP8116AStartPhase.values ());
    setPanelBorder (
      this.jStartPhase,
      level + 3,
      getGuiPreferencesFrequencyColor (),
      "Start Phase");
    this.jStartPhase.setSelectedItem (null);
    this.jStartPhase.addActionListener (this.jStartPhaseListener);
    jPhase.add (this.jStartPhase);
    final JPanel jComplementaryOutputPanel = new JPanel ();
    setPanelBorder (
      jComplementaryOutputPanel,
      level + 3,
      getGuiPreferencesFrequencyColor (),
      "Complementary Output");
    this.jComplementaryOutput = new JColorCheckBox.JBoolean (Color.red);
    this.jComplementaryOutput.addActionListener (this.jComplementaryOutputListener);
    jComplementaryOutputPanel.add (this.jComplementaryOutput, BorderLayout.CENTER);
    jPhase.add (jComplementaryOutputPanel);
    this.jInstrumentSpecificPanel.add (jPhase);
    //
    final JPanel jAutoVernierPanel = new JPanel ();
    setPanelBorder (
      jAutoVernierPanel,
      level + 3,
      getGuiPreferencesManagementColor (),
      "Auto Vernier");
    this.jAutoVernier = new JColorCheckBox.JBoolean (Color.red);
    this.jAutoVernier.addActionListener (this.jAutoVernierListener);
    jAutoVernierPanel.add (this.jAutoVernier, BorderLayout.CENTER);
    this.jInstrumentSpecificPanel.add (jAutoVernierPanel);
    //
    final JPanel jOutputLimitsPanel = new JPanel ();
    setPanelBorder (
      jOutputLimitsPanel,
      level + 3,
      getGuiPreferencesAmplitudeColor (),
      "Output Limits");
    this.jOutputLimits = new JColorCheckBox.JBoolean (Color.red);
    this.jOutputLimits.addActionListener (this.jOutputLimitsListener);
    jOutputLimitsPanel.add (this.jOutputLimits, BorderLayout.CENTER);
    this.jInstrumentSpecificPanel.add (jOutputLimitsPanel);
    //
    this.jDutyCycle = new JLabel ();
    setPanelBorder (
      this.jDutyCycle,
      level + 3,
      getGuiPreferencesAmplitudeColor (),
      "Duty Cycle [%]");
    this.jDutyCycle.addMouseListener (this.jDutyCycleListener);
    this.jInstrumentSpecificPanel.add (this.jDutyCycle);
    //
    this.jPulseWidth = new JLabel ();
    setPanelBorder (
      this.jPulseWidth,
      level + 3,
      getGuiPreferencesFrequencyColor (),
      "Pulse Width [s]");
    this.jPulseWidth.addMouseListener (this.jPulseWidthListener);
    this.jInstrumentSpecificPanel.add (this.jPulseWidth);
    //
    final JPanel jBurstDialogButtonPanel = new JPanel ();
    setPanelBorder (
      jBurstDialogButtonPanel,
      level + 3,
      getGuiPreferencesFrequencyColor (),
      "Burst Options Dialog [OPTION001]");
    this.jBurstDialogButton = new JColorCheckBox<> ((t) -> Color.blue);
    this.jBurstDialogButton.addActionListener (this.jBurstButtonDialogButtonActionListener);
    jBurstDialogButtonPanel.add (this.jBurstDialogButton, BorderLayout.CENTER);
    this.jInstrumentSpecificPanel.add (jBurstDialogButtonPanel);
    //
    this.jBurst = new JPanel ();
    this.jBurst.setLayout (new GridLayout (6, 1));
    this.jBurst.setMaximumSize (new Dimension (640, 480));
    this.jBurst.setPreferredSize (new Dimension (640, 480));
    //
    this.jBurstLength = new JLabel ();
    setPanelBorder (
      this.jBurstLength,
      level + 1,
      getGuiPreferencesFrequencyColor (),
      "Burst Length [#]");
    this.jBurstLength.addMouseListener (this.jBurstLengthListener);
    this.jBurst.add (this.jBurstLength);
    this.jRepeatInterval = new JLabel ();
    setPanelBorder (
      this.jRepeatInterval,
      level + 1,
      getGuiPreferencesFrequencyColor (),
      "Repeat Interval [s]");
    this.jRepeatInterval.addMouseListener (this.jRepeatIntervalListener);
    this.jBurst.add (this.jRepeatInterval);
    this.jStartFrequency = new JSevenSegmentNumber (
      getGuiPreferencesFrequencyColor (),
      getFunctionGenerator ().getMinFrequency_Hz (),
      getFunctionGenerator ().getMaxFrequency_Hz (),
      0.001);
    setPanelBorder (
      this.jStartFrequency,
      level + 1,
      getGuiPreferencesFrequencyColor (),
      "Start Frequency [Hz]");
    this.jStartFrequency.addMouseListener (this.jStartFrequencyListener);
    this.jBurst.add (this.jStartFrequency);
    this.jStopFrequency = new JSevenSegmentNumber (
      getGuiPreferencesFrequencyColor (),
      getFunctionGenerator ().getMinFrequency_Hz (),
      getFunctionGenerator ().getMaxFrequency_Hz (),
      0.001);
    setPanelBorder (
      this.jStopFrequency,
      level + 1,
      getGuiPreferencesFrequencyColor (),
      "Stop Frequency [Hz]");
    this.jStopFrequency.addMouseListener (this.jStopFrequencyListener);
    this.jBurst.add (this.jStopFrequency);
    this.jSweepTime = new JLabel ();
    setPanelBorder (
      this.jSweepTime,
      level + 1,
      getGuiPreferencesFrequencyColor (),
      "Sweep Time [s]");
    this.jSweepTime.addMouseListener (this.jSweepTimeListener);
    this.jBurst.add (this.jSweepTime);
    this.jMarkerFrequency = new JSevenSegmentNumber (
      getGuiPreferencesFrequencyColor (),
      getFunctionGenerator ().getMinFrequency_Hz (),
      getFunctionGenerator ().getMaxFrequency_Hz (),
      0.001);
    setPanelBorder (
      this.jMarkerFrequency,
      level + 1,
      getGuiPreferencesFrequencyColor (),
      "Marker Frequency [Hz]");
    this.jMarkerFrequency.addMouseListener (this.jMarkerFrequencyListener);
    this.jBurst.add (this.jMarkerFrequency);
    //
    getFunctionGenerator ().addInstrumentListener (this.instrumentListener);
  }
  
  public JHP8116A_GPIB (final HP8116A_GPIB_Instrument functionGenerator)
  {
    this (functionGenerator, 0);
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
      return "HP-8116A [GPIB] FG View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP8116A_GPIB_Instrument))
        return new JHP8116A_GPIB ((HP8116A_GPIB_Instrument) instrument);
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
    return JHP8116A_GPIB.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JComboBox<HP8116A_GPIB_Instrument.HP8116AControlMode> jControlMode;
  
  private final ActionListener jControlModeListener = (final ActionEvent ae) ->
  {
    if (! JHP8116A_GPIB.this.inhibitInstrumentControl)
    {
      final HP8116A_GPIB_Instrument.HP8116AControlMode controlMode =
        (HP8116A_GPIB_Instrument.HP8116AControlMode) JHP8116A_GPIB.this.jControlMode.getSelectedItem ();
      try
      {
        ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setControlMode (controlMode);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while setting control mode {0} on instrument {1}: {2}.",        
          new Object[]
            {controlMode, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    }
  };
  
  private final JComboBox<HP8116A_GPIB_Instrument.HP8116ATriggerMode> jTriggerMode;
  
  private final ActionListener jTriggerModeListener = (final ActionEvent ae) ->
  {
    if (! JHP8116A_GPIB.this.inhibitInstrumentControl)
    {
      final HP8116A_GPIB_Instrument.HP8116ATriggerMode triggerMode =
        (HP8116A_GPIB_Instrument.HP8116ATriggerMode) JHP8116A_GPIB.this.jTriggerMode.getSelectedItem ();
      try
      {
        ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setTriggerMode (triggerMode);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while setting trigger mode {0} on instrument {1}: {2}.",        
          new Object[]
            {triggerMode, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    }
  };
  
  private final JComboBox<HP8116A_GPIB_Instrument.HP8116ATriggerControl> jTriggerControl;
  
  private final ActionListener jTriggerControlListener = (final ActionEvent ae) ->
  {
    if (! JHP8116A_GPIB.this.inhibitInstrumentControl)
    {
      final HP8116A_GPIB_Instrument.HP8116ATriggerControl triggerControl =
        (HP8116A_GPIB_Instrument.HP8116ATriggerControl) JHP8116A_GPIB.this.jTriggerControl.getSelectedItem ();
      try
      {
        ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setTriggerControl (triggerControl);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while setting trigger control {0} on instrument {1}: {2}.",        
          new Object[]
            {triggerControl, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    }
  };
  
  private final JComboBox<HP8116A_GPIB_Instrument.HP8116AStartPhase> jStartPhase;
  
  private final ActionListener jStartPhaseListener = (final ActionEvent ae) ->
  {
    if (! JHP8116A_GPIB.this.inhibitInstrumentControl)
    {
      final HP8116A_GPIB_Instrument.HP8116AStartPhase startPhase =
        (HP8116A_GPIB_Instrument.HP8116AStartPhase) JHP8116A_GPIB.this.jStartPhase.getSelectedItem ();
      try
      {
        ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setStartPhase (startPhase);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while setting trigger mode {0} on instrument {1}: {2}.",        
          new Object[]
            {startPhase, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    }
  };
  
  private final JColorCheckBox.JBoolean jComplementaryOutput;
  
  private final ActionListener jComplementaryOutputListener = (final ActionEvent ae) ->
  {
    final Boolean currentValue = JHP8116A_GPIB.this.jComplementaryOutput.getDisplayedValue ();
    final boolean newValue = currentValue == null || (! currentValue);
    try
    {
      ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setComplementaryOutput (newValue);
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while setting complementary output {0} on instrument {1}: {2}.",        
        new Object[]
          {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
    
  private final JColorCheckBox.JBoolean jAutoVernier;
  
  private final ActionListener jAutoVernierListener = (final ActionEvent ae) ->
  {
    final Boolean currentValue = JHP8116A_GPIB.this.jAutoVernier.getDisplayedValue ();
    final boolean newValue = currentValue == null || (! currentValue);
    try
    {
      ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setAutoVernier (newValue);
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while setting auto vernier {0} on instrument {1}: {2}.",        
        new Object[]
          {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
    
  private final JColorCheckBox.JBoolean jOutputLimits;
  
  private final ActionListener jOutputLimitsListener = (final ActionEvent ae) ->
  {
    final Boolean currentValue = JHP8116A_GPIB.this.jOutputLimits.getDisplayedValue ();
    final boolean newValue = currentValue == null || (! currentValue);
    try
    {
      ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setOutputLimits (newValue);
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while setting auto vernier {0} on instrument {1}: {2}.",        
        new Object[]
          {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
    
  private final JLabel jDutyCycle;
  
  private final MouseListener jDutyCycleListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newDutyCycle_percent = getDutyCycleFromDialog_percent ("Enter Duty Cycle [%]", null);
      if (newDutyCycle_percent != null)
      {
        try
        {
          ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setDutyCycle_percent (newDutyCycle_percent);
        }
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting duty cycle {0} on instrument {1}: {2}.",        
            new Object[]
              {newDutyCycle_percent, getInstrument (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
  };
    
  private final JLabel jPulseWidth;
  
  private final MouseListener jPulseWidthListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newPulseWidth_s = getPeriodFromDialog_s ("Enter Pulse Width [s]", null);
      if (newPulseWidth_s != null)
      {
        try
        {
          ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setPulseWidth_s (newPulseWidth_s);
        }
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting pulse width {0} on instrument {1}: {2}.",        
            new Object[]
              {newPulseWidth_s, getInstrument (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
  };
  
  private final JLabel jBurstLength;
  
  private final MouseListener jBurstLengthListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Integer newBurstLength = getIntegerFromDialog ("Enter Burst Length [#]", null);
      if (newBurstLength != null)
      {
        try
        {
          ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setBurstLength (newBurstLength);
        }
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting burst length {0} on instrument {1}: {2}.",        
            new Object[]
              {newBurstLength, getInstrument (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
  };
    
  private final JLabel jRepeatInterval;
  
  private final MouseListener jRepeatIntervalListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newRepeatInterval = getPeriodFromDialog_s ("Enter Repeat Interval [s]", null);
      if (newRepeatInterval != null)
      {
        try
        {
          ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setRepeatInterval_s (newRepeatInterval);
        }
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting repeat interval {0} on instrument {1}: {2}.",        
            new Object[]
              {newRepeatInterval, getInstrument (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
  };
    
  private final JSevenSegmentNumber jStartFrequency;
  
  private final MouseListener jStartFrequencyListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newStartFrequency = getFrequencyFromDialog_Hz ("Enter Start Frequency [Hz]", null);
      if (newStartFrequency != null)
      {
        try
        {
          ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setStartFrequency_Hz (newStartFrequency);
        }
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting start frequency {0} on instrument {1}: {2}.",        
            new Object[]
              {newStartFrequency, getInstrument (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
  };
    
  private final JSevenSegmentNumber jStopFrequency;
  
  private final MouseListener jStopFrequencyListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newStopFrequency = getFrequencyFromDialog_Hz ("Enter Stop Frequency [Hz]", null);
      if (newStopFrequency != null)
      {
        try
        {
          ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setStopFrequency_Hz (newStopFrequency);
        }
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting stop frequency {0} on instrument {1}: {2}.",        
            new Object[]
              {newStopFrequency, getInstrument (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
  };
    
  private final JLabel jSweepTime;
  
  private final MouseListener jSweepTimeListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newSweepTime = getPeriodFromDialog_s ("Enter Sweep Time [s]", null);
      if (newSweepTime != null)
      {
        try
        {
          ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setSweepTime_s (newSweepTime);
        }
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting sweep time {0} on instrument {1}: {2}.",        
            new Object[]
              {newSweepTime, getInstrument (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
  };
    
  private final JSevenSegmentNumber jMarkerFrequency;

  private final MouseListener jMarkerFrequencyListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newMarkerFrequency = getFrequencyFromDialog_Hz ("Enter Marker Frequency [Hz]", null);
      if (newMarkerFrequency != null)
      {
        try
        {
          ((HP8116A_GPIB_Instrument) getFunctionGenerator ()).setMarkerFrequency_Hz (newMarkerFrequency);
        }
        catch (Exception e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting marker frequency {0} on instrument {1}: {2}.",        
            new Object[]
              {newMarkerFrequency, getInstrument (), Arrays.toString (e.getStackTrace ())});
        }
      }
    }
  };
  
  private final JColorCheckBox jBurstDialogButton;
  
  private final ActionListener jBurstButtonDialogButtonActionListener = (ActionEvent ae) ->
  {
    JOptionPane.showMessageDialog (JHP8116A_GPIB.this, JHP8116A_GPIB.this.jBurst);
  };
  
  private final JPanel jBurst;
  
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
      if (instrument != JHP8116A_GPIB.this.getFunctionGenerator ()|| instrumentStatus == null)
        throw new IllegalArgumentException ();
      if (! (instrumentStatus instanceof HP8116A_GPIB_Status))
        throw new IllegalArgumentException ();
      SwingUtilities.invokeLater (() ->
      {
        JHP8116A_GPIB.this.jSerialPollStatus.setDisplayedValue (
          ((HP8116A_GPIB_Status) instrumentStatus).getSerialPollStatusByte ());
      });
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JHP8116A_GPIB.this.getFunctionGenerator ()|| instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof HP8116A_GPIB_Settings))
        throw new IllegalArgumentException ();
      final HP8116A_GPIB_Settings settings = (HP8116A_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JHP8116A_GPIB.this.inhibitInstrumentControl = true;
        try
        {
          JHP8116A_GPIB.this.jTriggerMode.setSelectedItem (settings.getTriggerMode ());
          JHP8116A_GPIB.this.jTriggerControl.setSelectedItem (settings.getTriggerControl ());
          JHP8116A_GPIB.this.jControlMode.setSelectedItem (settings.getControlMode ());
          JHP8116A_GPIB.this.jStartPhase.setSelectedItem (settings.getStartPhase ());
          JHP8116A_GPIB.this.jAutoVernier.setDisplayedValue (settings.isAutoVernier ());
          JHP8116A_GPIB.this.jOutputLimits.setDisplayedValue (settings.isOutputLimits ());
          JHP8116A_GPIB.this.jComplementaryOutput.setDisplayedValue (settings.isComplementaryOutput ());
          JHP8116A_GPIB.this.jBurstLength.setText (Integer.toString (settings.getBurstLength ()));
          JHP8116A_GPIB.this.jRepeatInterval.setText (Double.toString (settings.getRepeatInterval_s ()));
          JHP8116A_GPIB.this.jStartFrequency.setNumber (settings.getStartFrequency_Hz ());
          JHP8116A_GPIB.this.jStopFrequency.setNumber (settings.getStopFrequency_Hz ());
          JHP8116A_GPIB.this.jSweepTime.setText (Double.toString (settings.getSweepTime_s ()));
          JHP8116A_GPIB.this.jMarkerFrequency.setNumber (settings.getMarkerFrequency_Hz ());
          JHP8116A_GPIB.this.jDutyCycle.setText (Double.toString (settings.getDutyCycle_percent ()));
          JHP8116A_GPIB.this.jPulseWidth.setText (Double.toString (settings.getPulseWidth_s ()));
        }
        finally
        {
          JHP8116A_GPIB.this.inhibitInstrumentControl = false;
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
