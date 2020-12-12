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
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jswing.jcenter.JCenter;

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
    
    final JPanel northPanel = new JPanel ();
    northPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "GPIB Mask Settings"));
    add (northPanel);
    
    final JPanel centerPanel = new JPanel ();
    centerPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Group Execute Trigger"));
    add (centerPanel);
    
    final JPanel southPanel = new JPanel ();
    southPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "[GPIB] Debug"));
    add (southPanel);
    
    //
    // northPanel
    //
    
    northPanel.setLayout (new GridLayout (9,2));

    northPanel.add (new JLabel ("Internal Error"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq on internal error",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isInternalErrorSrqEnabled (),
      tek2440::setEnableSrqInternalError,
      Color.green,
      true));
    
    northPanel.add (new JLabel ("Command Error"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq on command error",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isCommandErrorSrqEnabled (),
      tek2440::setEnableSrqCommandError,
      Color.green,
      true));
    
    northPanel.add (new JLabel ("Execution Error"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq on execution error",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isExecutionErrorSrqEnabled (),
      tek2440::setEnableSrqExecutionError,
      Color.green,
      true));
    
    northPanel.add (new JLabel ("Execution Warning"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq on execution warning",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isExecutionWarningSrqEnabled (),
      tek2440::setEnableSrqExecutionWarning,
      Color.green,
      true));
    
    northPanel.add (new JLabel ("Command Completion"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq on command completion",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isCommandCompletionSrqEnabled (),
      tek2440::setEnableSrqCommandCompletion,
      Color.green,
      true));
    
    northPanel.add (new JLabel ("Event"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq on event",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isEventSrqEnabled (),
      tek2440::setEnableSrqOnEvent,
      Color.green,
      true));
    
    northPanel.add (new JLabel ("Device Dependent"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq device dependent",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDeviceDependentSrqEnabled (),
      tek2440::setEnableSrqDeviceDependent,
      Color.green,
      true));
    
    northPanel.add (new JLabel ("User Button"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq on user button",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isUserButtonSrqEnabled (),
      tek2440::setEnableSrqOnUserButton,
      Color.green,
      true));
    
    northPanel.add (new JLabel ("Probe Identify Button"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq on probe identify button",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isProbeIdentifyButtonSrqEnabled (),
      tek2440::setEnableSrqOnProbeIdentifyButton,
      Color.green,
      true));
    
    //
    // centerPanel
    //
    
    centerPanel.setLayout (new GridLayout (2,2));
    
    final JTextField jGetSequence = new JString_JTextField (
      16,
      "GET user sequence",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getGroupTriggerSequence (),
      tek2440::setGroupExecuteTriggerUserSequence,
      true);
    
    final JComboBox<Tek2440_GPIB_Settings.GroupTriggerSRQMode> jGetMode = new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.GroupTriggerSRQMode.class,
      "GET mode",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getGroupTriggerSRQMode (),
      (Tek2440_GPIB_Settings.GroupTriggerSRQMode groupTriggerMode) ->
      {
        if (groupTriggerMode != Tek2440_GPIB_Settings.GroupTriggerSRQMode.ExecuteSequence)
          tek2440.setGroupExecuteTriggerMode (groupTriggerMode, null);
        else
        {
          final String userSequenceFromTextField = jGetSequence.getText ();
          if (userSequenceFromTextField == null || userSequenceFromTextField.trim ().isEmpty ())
            JOptionPane.showMessageDialog (null, "No valid user sequence!", "Error", JOptionPane.ERROR_MESSAGE);
          else
            tek2440.setGroupExecuteTriggerMode (groupTriggerMode, userSequenceFromTextField);
        }
      },
      true);
    
    centerPanel.add (new JLabel ("Mode"));
    centerPanel.add (JCenter.Y (jGetMode));
    
    centerPanel.add (new JLabel ("Sequence"));
    centerPanel.add (JCenter.Y (jGetSequence));
    
    //
    // southPanel
    //
    
    southPanel.setLayout (new GridLayout (1, 1));

    southPanel.add (JCenter.XY (new JBoolean_JBoolean (
      "GPIB debug",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isDebugEnabled (),
      tek2440::setEnableDebug,
      Color.green,
      true)));
    
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
