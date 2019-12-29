/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.swing;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.instrument.dmm.DefaultDigitalMultiMeterReading;
import org.javajdj.jinstrument.instrument.dmm.DigitalMultiMeter;
import org.javajdj.jinstrument.instrument.dmm.DigitalMultiMeterReading;
import org.javajdj.jinstrument.instrument.dmm.DigitalMultiMeterSettings;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A one-size-fits-all Swing panel for control and status of a generic {@link DigitalMultiMeter}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JDefaultDigitalMultiMeterView
  extends JDigitalMultiMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultDigitalMultiMeterView.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultDigitalMultiMeterView (final DigitalMultiMeter digitalMultiMeter, final int level)
  {
    //
    super (digitalMultiMeter, level);
    this.digitalMultiMeter = digitalMultiMeter;
    this.measurementModes = new ArrayList<> ();
    for (final DigitalMultiMeter.MeasurementMode measurementMode : DigitalMultiMeter.MeasurementMode.values ())
      if (this.digitalMultiMeter.supportsMeasurementMode (measurementMode))
        this.measurementModes.add (measurementMode);
    //
    setOpaque (true);
    setLayout (new GridLayout (2, 2));
    //
    final JDefaultInstrumentManagementView instrumentManagementPanel= new JDefaultInstrumentManagementView (
      JDefaultDigitalMultiMeterView.this.getInstrument (),
      JDefaultDigitalMultiMeterView.this.getLevel () + 1);
    instrumentManagementPanel.setPanelBorder (JInstrumentPanel.getGuiPreferencesManagementColor (), "Management");
    add (instrumentManagementPanel);
    //
    final JPanel triggerPanel = new JPanel ();
    setPanelBorder (triggerPanel, level + 1, JInstrumentPanel.getGuiPreferencesManagementColor (), "Trigger");
    triggerPanel.setLayout (new GridLayout (1,1));
    final JLabel triggerTbdLabel = new JLabel ("TBD");
    triggerTbdLabel.setHorizontalAlignment (SwingConstants.CENTER);
    triggerPanel.add (triggerTbdLabel);
    add (triggerPanel);
    //
    final JPanel controlPanel = new JPanel ();
    setPanelBorder (controlPanel, level + 1, JInstrumentPanel.getGuiPreferencesManagementColor (), "Mode/Range");
    controlPanel.setLayout (new GridLayout (1, 2));
    final JPanel modePanel = new JPanel ();
    setPanelBorder (modePanel, level + 2, JInstrumentPanel.getGuiPreferencesManagementColor (), "Mode");
    modePanel.setLayout (new GridLayout (this.measurementModes.size (), 1));
    this.modeButtonGroup = new ButtonGroup ();
    this.modeButtonMap = new HashMap<> ();
    for (final DigitalMultiMeter.MeasurementMode measurementMode : this.measurementModes)
    {
      final JRadioButton modeButton = new JRadioButton (measurementMode.toString ());
      this.modeButtonMap.put (modeButton, measurementMode);
      this.modeButtonGroup.add (modeButton);
      modeButton.addActionListener (this.modeButtonActionListener);
      modePanel.add (modeButton);
    }
    controlPanel.add (modePanel);
    final JPanel rangePanel = new JPanel ();
    setPanelBorder (rangePanel, level + 2, JInstrumentPanel.getGuiPreferencesManagementColor (), "Range");
    rangePanel.setLayout (new GridLayout (1,1));
    final JLabel rangeTbdLabel = new JLabel ("TBD");
    rangeTbdLabel.setHorizontalAlignment (SwingConstants.CENTER);
    rangePanel.add (rangeTbdLabel);
    controlPanel.add (rangePanel);
    add (controlPanel);
    //
    final JPanel readingPanel = new JPanel ();
    setPanelBorder (readingPanel, level + 1, JInstrumentPanel.getGuiPreferencesManagementColor (), "Reading");
    readingPanel.setLayout (new GridLayout (2, 1));
    this.jReadingValue7 = new JSevenSegmentNumber (Color.red, 0, 1000, 1e-6); // XXX Arbitrary argument values!
    readingPanel.add (this.jReadingValue7);
    this.jReadingValue = new JLabel ();
    this.jReadingValue.setHorizontalAlignment (SwingConstants.CENTER);
    readingPanel.add (this.jReadingValue);
    add (readingPanel);
    //
    getDigitalMultiMeter ().addInstrumentListener (this.instrumentListener);
    //
  }
  
  public JDefaultDigitalMultiMeterView (final DigitalMultiMeter digitalMultiMeter)
  {
    this (digitalMultiMeter, 0);
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
      return "Default Digital MultiMeter View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof DigitalMultiMeter))
        return new JDefaultDigitalMultiMeterView ((DigitalMultiMeter) instrument);
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
    return JDefaultDigitalMultiMeterView.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // DIGITAL MULTI-METER
  // SUPPORTED MEASUREMENT MODES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final DigitalMultiMeter digitalMultiMeter;
  
  private final List<DigitalMultiMeter.MeasurementMode> measurementModes;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final ButtonGroup modeButtonGroup;  
  
  private final Map<JRadioButton, DigitalMultiMeter.MeasurementMode> modeButtonMap;
  
  private DigitalMultiMeter.MeasurementMode getSelectedMeasurementModeFromSwing ()
  {
    for (final JRadioButton modeButton : this.modeButtonMap.keySet ())
      if (modeButton.isSelected ())
        return this.modeButtonMap.get (modeButton);
    return null;
  }
  
  private void setMeasurementMode (final DigitalMultiMeter.MeasurementMode measurementMode)
  {
    this.modeButtonGroup.clearSelection ();
    for (final JRadioButton modeButton : this.modeButtonMap.keySet ())
    {
      modeButton.setSelected (this.modeButtonMap.get (modeButton) == measurementMode);
      this.modeButtonGroup.setSelected (modeButton.getModel (), this.modeButtonMap.get (modeButton) == measurementMode);
      modeButton.repaint ();
    }
  }
  
  private ActionListener modeButtonActionListener = (final ActionEvent ae) ->
  {
    final JRadioButton modeButton = (JRadioButton) ae.getSource ();
    final DigitalMultiMeter.MeasurementMode newMeasurementMode = JDefaultDigitalMultiMeterView.this.modeButtonMap.get (modeButton);
    try
    {
      JDefaultDigitalMultiMeterView.this.digitalMultiMeter.setMeasurementMode (newMeasurementMode);
    }
    catch (Exception e)
    {
      LOG.log (Level.INFO, "Caught exception while setting measurement mode {0} on instrument {1}: {2}.",        
        new Object[]
          {newMeasurementMode, JDefaultDigitalMultiMeterView.this.digitalMultiMeter, Arrays.toString (e.getStackTrace ())});
    }
  };
  
  private JLabel jReadingValue;
  
  private JSevenSegmentNumber jReadingValue7;
  
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
      if (instrument != JDefaultDigitalMultiMeterView.this.getDigitalMultiMeter () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof DigitalMultiMeterSettings))
        throw new IllegalArgumentException ();
      final DigitalMultiMeterSettings settings = (DigitalMultiMeterSettings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JDefaultDigitalMultiMeterView.this.inhibitInstrumentControl = true;
        //
        try
        {
          JDefaultDigitalMultiMeterView.this.setMeasurementMode (settings.getMeasurementMode ());
        }
        finally
        {
          JDefaultDigitalMultiMeterView.this.inhibitInstrumentControl = false;
        }
      });
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      if (instrument != JDefaultDigitalMultiMeterView.this.getDigitalMultiMeter () || instrumentReading == null)
        throw new IllegalArgumentException ();
      if (! (instrumentReading instanceof DigitalMultiMeterReading))
        throw new IllegalArgumentException ();
      final DigitalMultiMeterReading digitalMultiMeterReading = (DefaultDigitalMultiMeterReading) instrumentReading;
      SwingUtilities.invokeLater (() ->
      {
        final double readingValue = digitalMultiMeterReading.getMultiMeterReading ();
        JDefaultDigitalMultiMeterView.this.jReadingValue7.setNumber (readingValue);
        JDefaultDigitalMultiMeterView.this.jReadingValue.setText (Double.toString (readingValue));
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
