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
import javax.swing.JLabel;
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
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

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
    //
    super (digitalStorageOscilloscope, level);
    //
    removeAll ();
    setLayout (new GridLayout (2, 1, 0, 2));
    //
    this.jSrqMaskPanel = new JSrqMaskPanel ();
    this.jSrqMaskPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "GPIB Mask Settings"));
    add (this.jSrqMaskPanel);
    //
    final JLabel southLabel = new JLabel ("Other Settings; TBD");
    southLabel.setHorizontalAlignment (SwingConstants.CENTER);
    southLabel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Other Settings; TBD"));
    add (southLabel);
    //
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    //
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
      return "Tektronix 2440 [GPIB] Service Request Settings";
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
    private final JColorCheckBox.JBoolean jSrqCommandCompletion;
    private final JColorCheckBox.JBoolean jSrqExecutionWarning;
    private final JColorCheckBox.JBoolean jSrqExecutionError;
    private final JColorCheckBox.JBoolean jSrqEvent;
    private final JColorCheckBox.JBoolean jSrqUserButton;
    private final JColorCheckBox.JBoolean jSrqProbeIdentifyButton;
    private final JColorCheckBox.JBoolean jSrqDeviceDependent;
    
    public JSrqMaskPanel ()
    {
      super ();
      final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) JTek2440_GPIB_SRQ.this.getDigitalStorageOscilloscope ();
      setOpaque (true);
      setLayout (new GridLayout (9,2));
      add (new JLabel ("Internal Error"));
      this.jSrqInternalError = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqInternalError.setEnabled (false);
      add (this.jSrqInternalError);
      add (new JLabel ("Command Error"));
      this.jSrqCommandError = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqCommandError.setEnabled (false);
      add (this.jSrqCommandError);
      add (new JLabel ("Command Completion"));
      this.jSrqCommandCompletion = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqCommandCompletion.setEnabled (false);
      add (this.jSrqCommandCompletion);
      add (new JLabel ("Execution Warning"));
      this.jSrqExecutionWarning = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqExecutionWarning.setEnabled (false);
      add (this.jSrqExecutionWarning);
      add (new JLabel ("Execution Error"));
      this.jSrqExecutionError = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqExecutionError.setEnabled (false);
      add (this.jSrqExecutionError);
      add (new JLabel ("Event"));
      this.jSrqEvent = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqEvent.setEnabled (false);
      add (this.jSrqEvent);
      add (new JLabel ("User Button"));
      this.jSrqUserButton = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqUserButton.setEnabled (false);
      add (this.jSrqUserButton);
      add (new JLabel ("Probe Identify Button"));
      this.jSrqProbeIdentifyButton = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqProbeIdentifyButton.setEnabled (false);
      add (this.jSrqProbeIdentifyButton);
      add (new JLabel ("Device Dependent"));
      this.jSrqDeviceDependent = new JColorCheckBox.JBoolean (Color.green);
      this.jSrqDeviceDependent.setEnabled (false);
      add (this.jSrqDeviceDependent);
    }
        
  }
  
  private final JSrqMaskPanel jSrqMaskPanel;
  
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
        JTek2440_GPIB_SRQ.this.inhibitInstrumentControl = true;
        try
        {
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqInternalError.setDisplayedValue (
            settings.isInternalErrorSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqCommandError.setDisplayedValue (
            settings.isCommandErrorSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqCommandCompletion.setDisplayedValue (
            settings.isCommandCompletionSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqExecutionWarning.setDisplayedValue (
            settings.isExecutionWarningSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqExecutionError.setDisplayedValue (
            settings.isExecutionErrorSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqEvent.setDisplayedValue (
            settings.isEventSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqUserButton.setDisplayedValue (
            settings.isUserButtonSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqProbeIdentifyButton.setDisplayedValue (
            settings.isProbeIdentifyButtonSrqEnabled ());
          JTek2440_GPIB_SRQ.this.jSrqMaskPanel.jSrqDeviceDependent.setDisplayedValue (
            settings.isDeviceDependentSrqEnabled ());
        }
        finally
        {
          JTek2440_GPIB_SRQ.this.inhibitInstrumentControl = false;
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
