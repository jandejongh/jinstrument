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

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jtextfieldlistener.JTextFieldListener;

/** A Swing panel for GPIB SRQ (Service Request) settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_SRQ
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_SRQ.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_SRQ (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    
    super (digitalStorageOscilloscope, level);
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;
    
    removeAll ();
    setLayout (new GridLayout (3, 1, 0, 2));
    
    this.jSrqMaskPanel = new JSrqMaskPanel ();
    this.jSrqMaskPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "GPIB Mask Settings"));
    add (this.jSrqMaskPanel);
    
    final JPanel gpibGetPanel = new JPanel ();
    gpibGetPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Group Execute Trigger"));
    gpibGetPanel.setLayout (new GridLayout (2,2));
    gpibGetPanel.add (new JLabel ("Mode"));
    this.jGetMode = new JComboBox<> (Tek2440_GPIB_Settings.GroupTriggerSRQMode.values ());
    this.jGetMode.addItemListener (this.jGetModeItemListener);
    gpibGetPanel.add (JCenter.Y (this.jGetMode));
    gpibGetPanel.add (new JLabel ("Sequence"));
    this.jGetSequence = new JTextField (16);
    JTextFieldListener.addJTextFieldListener (this.jGetSequence, this.jGetSequenceListener);
    gpibGetPanel.add (JCenter.Y (this.jGetSequence));
    add (gpibGetPanel);
    
    final JPanel gpibDebugPanel = new JPanel ();
    gpibDebugPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "[GPIB] Debug"));
    gpibDebugPanel.setLayout (new GridLayout (1, 6));
    gpibDebugPanel.add (new JLabel ("Enable Debug"));
    this.jDebug = new JColorCheckBox.JBoolean (Color.green);
    this.jDebug.addActionListener (new JInstrumentActionListener_1Boolean (
      "debug",
      this.jDebug::getDisplayedValue,
      tek2440::setEnableDebug,
      JTek2440_GPIB_SRQ.this::isInhibitInstrumentControl));
    gpibDebugPanel.add (this.jDebug);
    gpibDebugPanel.add (new JLabel ()); // Aesthetics, really...
    gpibDebugPanel.add (new JLabel ());
    gpibDebugPanel.add (new JLabel ());
    gpibDebugPanel.add (new JLabel ());
    add (gpibDebugPanel);
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }
  
  public JTek2440_GPIB_SRQ (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Service Request, Group Excute Trigger, and Debug Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_SRQ ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_SRQ.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // SRQ MASK PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final class JSrqMaskPanel
    extends JPanel
  {

    private final JColorCheckBox.JBoolean jSrqInternalError;
    private final JColorCheckBox.JBoolean jSrqCommandError;
    private final JColorCheckBox.JBoolean jSrqExecutionError;
    private final JColorCheckBox.JBoolean jSrqExecutionWarning;
    private final JColorCheckBox.JBoolean jSrqCommandCompletion;
    private final JColorCheckBox.JBoolean jSrqEvent;
    private final JColorCheckBox.JBoolean jSrqDeviceDependent;
    private final JColorCheckBox.JBoolean jSrqUserButton;
    private final JColorCheckBox.JBoolean jSrqProbeIdentifyButton;
    
    public JSrqMaskPanel ()
    {
      super ();
      final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) JTek2440_GPIB_SRQ.this.getDigitalStorageOscilloscope ();
      setOpaque (true);
      setLayout (new GridLayout (9,2));
      add (new JLabel ("Internal Error"));
      this.jSrqInternalError = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqInternalError.addActionListener (new JInstrumentActionListener_1Boolean (
        "enable srq on internal error",
        this.jSrqInternalError::getDisplayedValue,
        tek2440::setEnableSrqInternalError,
        JTek2440_GPIB_SRQ.this::isInhibitInstrumentControl));
      add (this.jSrqInternalError);
      add (new JLabel ("Command Error"));
      this.jSrqCommandError = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqCommandError.addActionListener (new JInstrumentActionListener_1Boolean (
        "enable srq on command error",
        this.jSrqCommandError::getDisplayedValue,
        tek2440::setEnableSrqCommandError,
        JTek2440_GPIB_SRQ.this::isInhibitInstrumentControl));
      add (this.jSrqCommandError);
      add (new JLabel ("Execution Error"));
      this.jSrqExecutionError = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqExecutionError.addActionListener (new JInstrumentActionListener_1Boolean (
        "enable srq on execution error",
        this.jSrqExecutionError::getDisplayedValue,
        tek2440::setEnableSrqExecutionError,
        JTek2440_GPIB_SRQ.this::isInhibitInstrumentControl));
      add (this.jSrqExecutionError);
      add (new JLabel ("Execution Warning"));
      this.jSrqExecutionWarning = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqExecutionWarning.addActionListener (new JInstrumentActionListener_1Boolean (
        "enable srq on execution warning",
        this.jSrqExecutionWarning::getDisplayedValue,
        tek2440::setEnableSrqExecutionWarning,
        JTek2440_GPIB_SRQ.this::isInhibitInstrumentControl));
      add (this.jSrqExecutionWarning);
      add (new JLabel ("Command Completion"));
      this.jSrqCommandCompletion = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqCommandCompletion.addActionListener (new JInstrumentActionListener_1Boolean (
        "enable srq on command completion",
        this.jSrqCommandCompletion::getDisplayedValue,
        tek2440::setEnableSrqCommandCompletion,
        JTek2440_GPIB_SRQ.this::isInhibitInstrumentControl));
      add (this.jSrqCommandCompletion);
      add (new JLabel ("Event"));
      this.jSrqEvent = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqEvent.addActionListener (new JInstrumentActionListener_1Boolean (
        "enable srq on event",
        this.jSrqEvent::getDisplayedValue,
        tek2440::setEnableSrqOnEvent,
        JTek2440_GPIB_SRQ.this::isInhibitInstrumentControl));
      add (this.jSrqEvent);
      add (new JLabel ("Device Dependent"));
      this.jSrqDeviceDependent = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqDeviceDependent.addActionListener (new JInstrumentActionListener_1Boolean (
        "enable srq device dependent",
        this.jSrqDeviceDependent::getDisplayedValue,
        tek2440::setEnableSrqDeviceDependent,
        JTek2440_GPIB_SRQ.this::isInhibitInstrumentControl));
      add (this.jSrqDeviceDependent);
      add (new JLabel ("User Button"));
      this.jSrqUserButton = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqUserButton.addActionListener (new JInstrumentActionListener_1Boolean (
        "enable srq on user button",
        this.jSrqUserButton::getDisplayedValue,
        tek2440::setEnableSrqOnUserButton,
        JTek2440_GPIB_SRQ.this::isInhibitInstrumentControl));
      add (this.jSrqUserButton);
      add (new JLabel ("Probe Identify Button"));
      this.jSrqProbeIdentifyButton = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqProbeIdentifyButton.addActionListener (new JInstrumentActionListener_1Boolean (
        "enable srq on probe identify button",
        this.jSrqProbeIdentifyButton::getDisplayedValue,
        tek2440::setEnableSrqOnProbeIdentifyButton,
        JTek2440_GPIB_SRQ.this::isInhibitInstrumentControl));
      add (this.jSrqProbeIdentifyButton);
    }
        
  }
  
  private final JSrqMaskPanel jSrqMaskPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // GET [GROUP EXECUTE TRIGGER]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JComboBox<Tek2440_GPIB_Settings.GroupTriggerSRQMode> jGetMode;
  
  private final JTextField jGetSequence;
  
  private final ItemListener jGetModeItemListener = (ItemEvent ie) ->
  {
    if (ie.getStateChange () == ItemEvent.SELECTED)
    {
      final Tek2440_GPIB_Settings.GroupTriggerSRQMode newValue = (Tek2440_GPIB_Settings.GroupTriggerSRQMode) ie.getItem ();
      final String userSequence = JTek2440_GPIB_SRQ.this.jGetSequence.getText ().trim ();
      final Tek2440_GPIB_Instrument tek2440 = ((Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ());
      if (! JTek2440_GPIB_SRQ.this.isInhibitInstrumentControl ())
        try
        {
          if (newValue == Tek2440_GPIB_Settings.GroupTriggerSRQMode.ExecuteSequence && userSequence.isEmpty ())
            tek2440.setGroupExecuteTriggerMode (Tek2440_GPIB_Settings.GroupTriggerSRQMode.Off, null);
          else
            tek2440.setGroupExecuteTriggerMode (newValue,
              (newValue == Tek2440_GPIB_Settings.GroupTriggerSRQMode.ExecuteSequence)
                ? userSequence
                : null);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting GET mode on instrument"
            + " from combo box to {0} (user sequence: {1}): {2}.",
            new Object[]{newValue, userSequence, Arrays.toString (e.getStackTrace ())});
        }
    }
  };
  
  private final JTextFieldListener jGetSequenceListener = new JTextFieldListener ()
  {

    @Override
    public void actionPerformed ()
    {
      final Tek2440_GPIB_Settings.GroupTriggerSRQMode mode =
        (Tek2440_GPIB_Settings.GroupTriggerSRQMode) JTek2440_GPIB_SRQ.this.jGetMode.getSelectedItem ();
      final String newValue = JTek2440_GPIB_SRQ.this.jGetSequence.getText ();
      final Tek2440_GPIB_Instrument tek2440 = ((Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ());
      if (! JTek2440_GPIB_SRQ.this.isInhibitInstrumentControl ())
        try
        {
          if (mode == Tek2440_GPIB_Settings.GroupTriggerSRQMode.ExecuteSequence)
          {
            if (newValue == null || newValue.trim ().isEmpty ())
              tek2440.setGroupExecuteTriggerMode (Tek2440_GPIB_Settings.GroupTriggerSRQMode.Off, null);
            else
              tek2440.setGroupExecuteTriggerMode (Tek2440_GPIB_Settings.GroupTriggerSRQMode.ExecuteSequence, newValue);
          }
          else if (newValue != null && ! newValue.trim ().isEmpty ())
            tek2440.setGroupExecuteTriggerMode (Tek2440_GPIB_Settings.GroupTriggerSRQMode.ExecuteSequence, newValue);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting GET user sequence on instrument"
            + " from text field to {0} (mode: {1}): {2}.",
            new Object[]{newValue, mode, getInstrument (), Arrays.toString (e.getStackTrace ())});
        }
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // [GPIB] DEBUG
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JColorCheckBox.JBoolean jDebug;
  
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
      // EMPTY
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JTek2440_GPIB_SRQ.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_SRQ.this.setInhibitInstrumentControl ();
        try
        {
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqInternalError.setDisplayedValue (
            settings.isInternalErrorSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqCommandError.setDisplayedValue (
            settings.isCommandErrorSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqExecutionError.setDisplayedValue (
            settings.isExecutionErrorSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqExecutionWarning.setDisplayedValue (
            settings.isExecutionWarningSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqCommandCompletion.setDisplayedValue (
            settings.isCommandCompletionSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqEvent.setDisplayedValue (
            settings.isEventSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqDeviceDependent.setDisplayedValue (
            settings.isDeviceDependentSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqUserButton.setDisplayedValue (
            settings.isUserButtonSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqProbeIdentifyButton.setDisplayedValue (
            settings.isProbeIdentifyButtonSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jGetMode.setSelectedItem (
            settings.getGroupTriggerSRQMode ());
          JTek2440_GPIB_SRQ.this.jGetSequence.setText (
            settings.getGroupTriggerSequence ());
          JTek2440_GPIB_SRQ.this.jDebug.setDisplayedValue (
            settings.isDebugEnabled ());
        }
        finally
        {
          JTek2440_GPIB_SRQ.this.resetInhibitInstrumentControl ();
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
