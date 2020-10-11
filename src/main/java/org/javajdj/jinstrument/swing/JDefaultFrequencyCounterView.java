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
package org.javajdj.jinstrument.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.FrequencyCounter;
import org.javajdj.jinstrument.FrequencyCounterReading;
import org.javajdj.jinstrument.FrequencyCounterSettings;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.Unit;
import static org.javajdj.jinstrument.swing.JInstrumentPanel.setPanelBorder;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A one-size-fits-all Swing panel for control and status of a generic {@link FrequencyCounter}.
 * 
 * @param <M> The type of modes (functions, measurement functions, etc.) on the instrument.
 * @param <T> The type of trigger modes.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class JDefaultFrequencyCounterView<M, T>
  extends JFrequencyCounterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultFrequencyCounterView.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultFrequencyCounterView (final FrequencyCounter frequencyCounter, final int level)
  {
    
    super (frequencyCounter, level);
    
    setOpaque (true);
    setMinimumSize (new Dimension (1024, 600));
    setPreferredSize (new Dimension (1024, 600));
    
    setLayout (new GridLayout (3, 1));
    
    final JPanel northPanel = new JPanel ();
    northPanel.setLayout (new GridLayout (1, 3));
    final JInstrumentPanel managementPanel = new JDefaultInstrumentManagementView (getFrequencyCounter (), getLevel () + 1);
    setPanelBorder (
      managementPanel,
      getLevel () + 1,
      getGuiPreferencesManagementColor (),
      "Management");
    northPanel.add (managementPanel);
    this.jInstrumentSpecificPanel1 = new JPanel ();
    setPanelBorder (
      this.jInstrumentSpecificPanel1,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Instrument Specific");
    this.jInstrumentSpecificPanel1.setLayout (new GridLayout (1,1));
    final JLabel jInstrumentSpecificLabel1 = new JLabel ("Not Supported in this View");
    jInstrumentSpecificLabel1.setHorizontalAlignment (SwingConstants.CENTER);
    this.jInstrumentSpecificPanel1.add (jInstrumentSpecificLabel1);
    northPanel.add (this.jInstrumentSpecificPanel1);
    this.jInstrumentSpecificPanel2 = new JPanel ();
    setPanelBorder (
      this.jInstrumentSpecificPanel2,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Instrument Specific");
    this.jInstrumentSpecificPanel2.setLayout (new GridLayout (1,1));
    final JLabel jInstrumentSpecificLabel2 = new JLabel ("Not Supported in this View");
    jInstrumentSpecificLabel2.setHorizontalAlignment (SwingConstants.CENTER);
    this.jInstrumentSpecificPanel2.add (jInstrumentSpecificLabel2);
    northPanel.add (this.jInstrumentSpecificPanel2);
    add (northPanel);
    
    this.jReading = new JDefaultInstrumentReadingPanel_Double (
      frequencyCounter,
      null,
      getLevel () + 1,
      JInstrumentPanel.DEFAULT_FREQUENCY_COLOR,
      true,
      JInstrumentPanel.DEFAULT_FREQUENCY_COLOR,
      getFrequencyCounter ().getMinFrequency_Hz (),
      getFrequencyCounter ().getMaxFrequency_Hz (),
      getFrequencyCounter ().getFrequencyResolution_Hz (),
      true,
      Arrays.asList (new Unit[]{
        Unit.UNIT_Hz,
        Unit.UNIT_kHz,
        Unit.UNIT_MHz,
        Unit.UNIT_s,
        Unit.UNIT_ms,
        Unit.UNIT_mus,
        Unit.UNIT_ns,
        Unit.UNIT_ps}),
      true,
      true);
    add (this.jReading);
    
    final JPanel southPanel = new JPanel ();
    southPanel.setLayout (new GridLayout (1, 3));
    final JPanel modeFunctionPanel = new JPanel ();
    setPanelBorder (
      modeFunctionPanel,
      getLevel () + 2,
      getGuiPreferencesManagementColor (),
      "Mode / Function");
    modeFunctionPanel.setLayout (new FlowLayout ());
    this.jMode = new JComboBox (getFrequencyCounter ().getSupportedModes ().toArray ());
    this.jMode.addItemListener (this.jModeListener);
    modeFunctionPanel.add (this.jMode);
    southPanel.add (modeFunctionPanel);
    final JPanel gateTimePanel = new JPanel ();
    setPanelBorder (gateTimePanel,
      getLevel () + 2,
      getGuiPreferencesTimeColor (), "Gate Time [s]");
    gateTimePanel.setLayout (new GridLayout (3, 1));
    this.jGateTime = new JSevenSegmentNumber (
      getGuiPreferencesTimeColor (),
      getFrequencyCounter ().getMinGateTime_s (),
      getFrequencyCounter ().getMaxGateTime_s (),
      getFrequencyCounter ().getGateTimeResolution_s ());
    this.jGateTime.addMouseListener (this.jGateTimeListener);
    gateTimePanel.add (this.jGateTime);
    this.jGateTime_s = new JSlider ();
    gateTimePanel.add (this.jGateTime_s);
    this.jGateTime_ms = new JSlider ();
    gateTimePanel.add (this.jGateTime_ms);
    southPanel.add (gateTimePanel);
    final JPanel triggerPanel = new JPanel ();
    setPanelBorder (
      triggerPanel,
      getLevel () + 2,
      getGuiPreferencesTriggerColor (),
      "Triggering");
    triggerPanel.setLayout (new GridLayout (4, 1));
    final JPanel jTriggerModePanel = new JPanel ();
    setPanelBorder (
      jTriggerModePanel,
      getLevel () + 3,
      getGuiPreferencesVoltageColor (),
      "Trigger Mode");
    jTriggerModePanel.setLayout (new FlowLayout ());
    this.jTriggerMode = new JComboBox (getFrequencyCounter ().getSupportedTriggerModes ().toArray ());
    jTriggerModePanel.add (this.jTriggerMode);
    triggerPanel.add (jTriggerModePanel);
    this.jTriggerLevel = new JSevenSegmentNumber (
      getGuiPreferencesVoltageColor (),
      getFrequencyCounter ().getMinTriggerLevel_V (),
      getFrequencyCounter ().getMaxTriggerLevel_V (),
      getFrequencyCounter ().getTriggerLevelResolution_V ());
    setPanelBorder (
      this.jTriggerLevel,
      getLevel () + 3,
      getGuiPreferencesVoltageColor (),
      "Trigger Level [V]");
    triggerPanel.add (this.jTriggerLevel);
    this.jTriggerLevel_V = new JSlider ();
    setPanelBorder (
      this.jTriggerLevel_V,
      getLevel () + 3,
      getGuiPreferencesVoltageColor (),
      "Trigger Level [V]");
    triggerPanel.add (this.jTriggerLevel_V);
    this.jTriggerLevel_mV = new JSlider ();
    setPanelBorder (
      this.jTriggerLevel_mV,
      getLevel () + 3,
      getGuiPreferencesVoltageColor (),
      "Trigger Level [mV]");
    triggerPanel.add (this.jTriggerLevel_mV);
    southPanel.add (triggerPanel);
    add (southPanel);
    
    getFrequencyCounter ().addInstrumentListener (this.instrumentListener);
    
  }
  
  public JDefaultFrequencyCounterView (final FrequencyCounter frequencyCounter)
  {
    this (frequencyCounter, 0);
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
      return "Default Frequency Counter View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof FrequencyCounter))
        return new JDefaultFrequencyCounterView ((FrequencyCounter) instrument);
      else
        return null;
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getInstrumentViewUrl ()
  {
    return JDefaultFrequencyCounterView.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // SWING - COMPONENTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected final JPanel jInstrumentSpecificPanel1;
  
  protected final JPanel jInstrumentSpecificPanel2;
  
  private final JDefaultInstrumentReadingPanel_Double jReading;
  
  private final JComboBox jMode;
  
  private final JSevenSegmentNumber jGateTime;
  
  private final JSlider jGateTime_s;
  
  private final JSlider jGateTime_ms;
  
  private final JComboBox jTriggerMode;
  
  private final JSevenSegmentNumber jTriggerLevel;
  
  private final JSlider jTriggerLevel_V;
  
  private final JSlider jTriggerLevel_mV;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING - LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final ItemListener jModeListener = (ItemEvent ie) ->
  {
    if (ie.getStateChange () == ItemEvent.SELECTED)
    {
      final M newValue = (M) ie.getItem ();
      if (! JDefaultFrequencyCounterView.this.inhibitInstrumentControl)
        try
        {
          getFrequencyCounter ().setInstrumentMode (newValue);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting mode on instrument"
            + " from combo box to {0}.",
            new Object[]{newValue, e});
        }
    }
  };

  private final MouseListener jGateTimeListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newGateTime_s = getPeriodFromDialog_s ("Enter Gate Time [s]", null);
      if (newGateTime_s != null)
        try
        {
          JDefaultFrequencyCounterView.this.getFrequencyCounter ().setGateTime_s (newGateTime_s);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting gate time on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting gate time on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
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
      if (instrument != JDefaultFrequencyCounterView.this.getFrequencyCounter () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof FrequencyCounterSettings))
        throw new IllegalArgumentException ();
      final FrequencyCounterSettings settings = (FrequencyCounterSettings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JDefaultFrequencyCounterView.this.inhibitInstrumentControl = true;
        //
        try
        {
          JDefaultFrequencyCounterView.this.jMode.setSelectedItem (settings.getInstrumentMode ());
          JDefaultFrequencyCounterView.this.jGateTime.setNumber (settings.getGateTime_s ());
          JDefaultFrequencyCounterView.this.jTriggerMode.setSelectedItem (settings.getInstrumentMode ());
          JDefaultFrequencyCounterView.this.jTriggerLevel.setNumber (settings.getTriggerLevel_V ());
        }
        finally
        {
          JDefaultFrequencyCounterView.this.inhibitInstrumentControl = false;
        }
      });
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      if (instrument != JDefaultFrequencyCounterView.this.getFrequencyCounter ()|| instrumentReading == null)
        throw new IllegalArgumentException ();
      if (! (instrumentReading instanceof FrequencyCounterReading))
        throw new IllegalArgumentException ();
      final FrequencyCounterReading frequencyCounterReading = (FrequencyCounterReading) instrumentReading;
      SwingUtilities.invokeLater (() ->
      {
        // NOTHING TO DO!
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
