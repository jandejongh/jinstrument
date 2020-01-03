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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SignalGenerator;
import org.javajdj.jinstrument.SignalGenerator.ModulationSource;
import org.javajdj.jinstrument.SignalGeneratorSettings;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A one-size-fits-all Swing panel for control and status of a generic {@link SignalGenerator}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class JDefaultSignalGeneratorView
  extends JSignalGeneratorPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultSignalGeneratorView.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultSignalGeneratorView (final SignalGenerator signalGenerator)
  {
    
    super (signalGenerator, null, 0, null);
    
    setOpaque (true);
    setMinimumSize (new Dimension (1024, 600));
    setPreferredSize (new Dimension (1024, 600));
    
    setLayout (new GridLayout (3, 3));
    
    final JInstrumentPanel managementPanel = new JDefaultInstrumentManagementView (getSignalGenerator (), getLevel () + 1);
    managementPanel.setPanelBorder (JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR, "Management");
    add (managementPanel);
    
    final JPanel frequencyPanel = new JPanel ();
    frequencyPanel.setOpaque (true);
    frequencyPanel.setLayout (new GridLayout (5, 1));
    //
    this.jCenterFreq = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 11, 9);
    this.jCenterFreq.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newCenterFrequency_Hz = getFrequencyFromDialog_Hz ("Enter Center Frequency [Hz]", null);
        if (newCenterFrequency_Hz != null)
          try
          {
            signalGenerator.setCenterFrequency_MHz (1.0e-6 * newCenterFrequency_Hz);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting center frequency on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting center frequency on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    frequencyPanel.add (this.jCenterFreq);
    //
    this.jFreqSlider_MHz = new JSlider (0, 2559, 0);
    this.jFreqSlider_kHz = new JSlider (0, 999, 0);
    this.jFreqSlider_Hz = new JSlider (0, 999, 0);
    this.jFreqSlider_mHz = new JSlider (0, 999, 0);
    this.jFreqSlider_MHz.setToolTipText ("-");
    this.jFreqSlider_MHz.setMajorTickSpacing (500);
    this.jFreqSlider_MHz.setMinorTickSpacing (100);
    this.jFreqSlider_MHz.setPaintTicks (true);
    this.jFreqSlider_MHz.setPaintLabels (true);
    this.jFreqSlider_MHz.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ())
      {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting frequency on instrument from slider to {0} MHz.", source.getValue ());
            signalGenerator.setCenterFrequency_MHz (
              source.getValue ()
              + 1.0e-3 * this.jFreqSlider_kHz.getValue ()
              + 1.0e-6 * this.jFreqSlider_Hz.getValue ()
              + 1.0e-9 * this.jFreqSlider_mHz.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} MHz: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
      else
        source.setToolTipText (Integer.toString (source.getValue ()));
    });
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), this.jFreqSlider_MHz, "[MHz]");
    frequencyPanel.add (this.jFreqSlider_MHz);
    //
    this.jFreqSlider_kHz.setToolTipText ("-");
    this.jFreqSlider_kHz.setMajorTickSpacing (100);
    this.jFreqSlider_kHz.setMinorTickSpacing (10);
    this.jFreqSlider_kHz.setPaintTicks (true);
    this.jFreqSlider_kHz.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ()) {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            // LOG.log (Level.INFO, "Setting frequency on instrument from slider to {0} kHz.", source.getValue ());
            signalGenerator.setCenterFrequency_MHz (
              this.jFreqSlider_MHz.getValue ()
              + 1.0e-3 * source.getValue ()
              + 1.0e-6 * this.jFreqSlider_Hz.getValue ()
              + 1.0e-9 * this.jFreqSlider_mHz.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} kHz: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
    });
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), this.jFreqSlider_kHz, "[kHz]");
    frequencyPanel.add (this.jFreqSlider_kHz);
    //
    this.jFreqSlider_Hz.setToolTipText ("-");
    this.jFreqSlider_Hz.setMajorTickSpacing (100);
    this.jFreqSlider_Hz.setMinorTickSpacing (10);
    this.jFreqSlider_Hz.setPaintTicks (true);
    this.jFreqSlider_Hz.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ()) {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            // LOG.log (Level.INFO, "Setting frequency on instrument from slider to {0} Hz.", source.getValue ());
            signalGenerator.setCenterFrequency_MHz (
              this.jFreqSlider_MHz.getValue ()
              + 1.0e-3 * this.jFreqSlider_kHz.getValue ()
              + 1.0e-6 * source.getValue ()
              + 1.0e-9 * this.jFreqSlider_mHz.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} Hz: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
    });
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), this.jFreqSlider_Hz, "[Hz]");
    frequencyPanel.add (this.jFreqSlider_Hz);
    //
    this.jFreqSlider_mHz.setToolTipText ("-");
    this.jFreqSlider_mHz.setMajorTickSpacing (100);
    this.jFreqSlider_mHz.setMinorTickSpacing (10);
    this.jFreqSlider_mHz.setPaintTicks (true);
    this.jFreqSlider_mHz.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ()) {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            // LOG.log (Level.INFO, "Setting frequency on instrument from slider to {0} mHz.", source.getValue ());
            signalGenerator.setCenterFrequency_MHz (
              this.jFreqSlider_MHz.getValue ()
              + 1.0e-3 * this.jFreqSlider_kHz.getValue ()
              + 1.0e-6 * this.jFreqSlider_Hz.getValue ()
              + 1.0e-9 * source.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} mHz: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
    });
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), this.jFreqSlider_mHz, "[mHz]");
    frequencyPanel.add (this.jFreqSlider_mHz);
    //
    JInstrumentPanel.setPanelBorder (frequencyPanel, getLevel () + 1, "Frequency [Hz]");
    add (frequencyPanel);
    
    final JPanel frequencySweepPanel = new JPanel ();
    frequencySweepPanel.setLayout (new GridLayout (4, 1));
    final JPanel frequencySweepModePanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), frequencySweepModePanel, "Mode");
    this.jRfSweepMode = new JComboBox<> (SignalGenerator.RfSweepMode.values ());
    this.jRfSweepMode.addItemListener ((ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final SignalGenerator.RfSweepMode newValue = (SignalGenerator.RfSweepMode) ie.getItem ();
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting RF sweep mode on instrument from combo box to {0}.", newValue);
            signalGenerator.setRfSweepMode (newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting RF sweep mode on instrument"
              + " from combo box to {0}.",
              new Object[]{newValue, e});
          }
      }
    });
    frequencySweepModePanel.add (this.jRfSweepMode);
    frequencySweepPanel.add (frequencySweepModePanel);
    final JPanel frequencySweepSpanPanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), frequencySweepSpanPanel, "Span");
    frequencySweepSpanPanel.setLayout (new GridLayout (1, 1));
    this.jSpan = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 11, 9);
    this.jSpan.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newSpan_Hz = getFrequencyFromDialog_Hz ("Enter Span [Hz]", null);
        if (newSpan_Hz != null)
          try
          {
            signalGenerator.setSpan_MHz (1.0e-6 * newSpan_Hz);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting span on instrument: {0}.", ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting span on instrument: {0}.", ioe.getStackTrace ());
          }
      }
    });
    frequencySweepSpanPanel.add (this.jSpan);
    frequencySweepPanel.add (frequencySweepSpanPanel);
    JInstrumentPanel.setPanelBorder (frequencySweepPanel, getLevel () + 1, "Frequency Sweep");
    add (frequencySweepPanel);
    final JPanel frequencySweepStartPanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), frequencySweepStartPanel, "Start Frequency [Hz]");
    frequencySweepStartPanel.setLayout (new GridLayout (1, 1));
    this.jStartFreq = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 11, 9);
    this.jStartFreq.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newStartFrequency_Hz = getFrequencyFromDialog_Hz ("Enter Start Frequency [Hz]", null);
        if (newStartFrequency_Hz != null)
          try
          {
            signalGenerator.setStartFrequency_MHz (1.0e-6 * newStartFrequency_Hz);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting start frequency on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting start frequency on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    frequencySweepStartPanel.add (this.jStartFreq);
    frequencySweepPanel.add (frequencySweepStartPanel);
    final JPanel frequencySweepStopPanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), frequencySweepStopPanel, "Stop Frequency [Hz]");
    frequencySweepStopPanel.setLayout (new GridLayout (1, 1));
    this.jStopFreq = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 11, 9);
    this.jStopFreq.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newStopFrequency_Hz = getFrequencyFromDialog_Hz ("Enter Stop Frequency [Hz]", null);
        if (newStopFrequency_Hz != null)
          try
          {
            signalGenerator.setStopFrequency_MHz (1.0e-6 * newStopFrequency_Hz);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting stop frequency on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting stop frequency on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    frequencySweepStopPanel.add (this.jStopFreq);
    frequencySweepPanel.add (frequencySweepStopPanel);
    
    final JPanel sweepSources = new JPanel ();
    JInstrumentPanel.setPanelBorder (sweepSources, getLevel () + 1, "Sweep Source(s)");
    sweepSources.setLayout (new BorderLayout ());
    add (sweepSources);
    
    final JPanel amplitudePanel = new JPanel ();
    JInstrumentPanel.setPanelBorder (amplitudePanel, getLevel () + 1, "Amplitude [dBm]");
    amplitudePanel.setLayout (new GridLayout (3, 1));    
    //
    final JPanel amplitudeTopPanel = new JPanel ();
    amplitudeTopPanel.setLayout (new GridLayout (1, 2));
    //
    this.jAmpEnable = new JColorCheckBox (
      (Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? JInstrumentPanel.getGuiPreferencesAmplitudeColor () : null);
    final JPanel jAmpEnablePanel = new JPanel ();
    // jAmpEnablePanel.setLayout (new FlowLayout (FlowLayout.CENTER));
    jAmpEnablePanel.setLayout (new GridLayout (1, 1));
    jAmpEnablePanel.add (jAmpEnable);
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), jAmpEnablePanel, "RF Out");
    this.jAmpEnable.setDisplayedValue (false);
    this.jAmpEnable.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        super.mouseClicked (me);
        final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
        try
        {
          final boolean newValue = ! source.getDisplayedValue ();
          // XXX This should not be necessary! Should be done upon new instrument settings.
          source.setDisplayedValue (newValue);
          LOG.log (Level.INFO, "Toggling outputEnable on instrument; becomes {0}", newValue);
          signalGenerator.setOutputEnable (newValue);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while toggling outputEnable on instrument.");
        }
      }
    });
    // amplitudeTopPanel.add (this.jAmpEnable);
    amplitudeTopPanel.add (jAmpEnablePanel);
    //
    this.jAmplitudeAlt = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), true, 5, 2);
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), jAmplitudeAlt, "[dBm]");
    amplitudeTopPanel.add (this.jAmplitudeAlt);
    //
    amplitudePanel.add (amplitudeTopPanel);
    //
    this.jAmpSlider_dBm = new JSlider (-150, 30, -150);
    this.jAmpSlider_mdBm = new JSlider (-1000, 1000, 0);
    //
    this.jAmpSlider_dBm.setToolTipText ("-");
    this.jAmpSlider_dBm.setMajorTickSpacing (30);
    this.jAmpSlider_dBm.setMinorTickSpacing (10);
    this.jAmpSlider_dBm.setPaintTicks (true);
    this.jAmpSlider_dBm.setPaintLabels (true);
    this.jAmpSlider_dBm.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ()) {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting amplitude on instrument from slider to {0} dBm.", source.getValue ());
            signalGenerator.setAmplitude_dBm (
              source.getValue ()
              + 1.0e-3 * this.jAmpSlider_mdBm.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting amplitude from slider to {0} dBm: {1}.",
              new Object[]{source.getValue (), e});
          }
      }
    });
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), this.jAmpSlider_dBm, "[dBm]");
    amplitudePanel.add (this.jAmpSlider_dBm);
    //
    this.jAmpSlider_mdBm.setToolTipText ("-");
    this.jAmpSlider_mdBm.setMajorTickSpacing (1000);
    this.jAmpSlider_mdBm.setMinorTickSpacing (100);
    this.jAmpSlider_mdBm.setPaintTicks (true);
    this.jAmpSlider_mdBm.setPaintLabels (true);
    this.jAmpSlider_mdBm.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (!source.getValueIsAdjusting ()) {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting amplitude on instrument from slider to {0} mdBm.", source.getValue ());
            signalGenerator.setAmplitude_dBm (
              this.jAmpSlider_dBm.getValue ()
              + 1.0e-3 * source.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting amplitude from slider to {0} mdBm: {1}.",
              new Object[]{source.getValue (), e});
          }
      }
    });
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), this.jAmpSlider_mdBm, "[mdBm]");
    amplitudePanel.add (this.jAmpSlider_mdBm);
    //
    add (amplitudePanel);
    
    final JPanel amplitudeSweepPanel = new JPanel ();
    amplitudeSweepPanel.setLayout (new GridLayout (3, 1));
    final JPanel amplitudeSweepModePanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), amplitudeSweepModePanel, "Mode");
    amplitudeSweepPanel.add (amplitudeSweepModePanel);
    final JPanel amplitudeSweepStartPanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), amplitudeSweepStartPanel, "Start Amplitude");
    amplitudeSweepPanel.add (amplitudeSweepStartPanel);
    final JPanel amplitudeSweepStopPanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), amplitudeSweepStopPanel, "Stop Amplitude");
    amplitudeSweepPanel.add (amplitudeSweepStopPanel);
    JInstrumentPanel.setPanelBorder (amplitudeSweepPanel, getLevel () + 1, "Amplitude Sweep");
    add (amplitudeSweepPanel);
    
    final JPanel modSourcesPanel = new JPanel ();
    JInstrumentPanel.setPanelBorder (modSourcesPanel, getLevel () + 1, "Modulation Source(s)");
    modSourcesPanel.setLayout (new GridLayout (1, 1));
    final JPanel modSourcesInternalPanel = new JPanel ();
    modSourcesInternalPanel.setLayout (new GridLayout (3, 1));
    setSubPanelBorder (Color.black, modSourcesInternalPanel, "Internal");
    this.jModulationSourceInternalFrequency =
      new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesModulationColor (), false, 5, 2);
    setSubPanelBorder (
      JInstrumentPanel.getGuiPreferencesModulationColor (), this.jModulationSourceInternalFrequency, "Frequency [kHz]");
    modSourcesInternalPanel.add (this.jModulationSourceInternalFrequency);
    this.jModulationSourceInternalFrequency_kHz = new JSlider (0, 100, 1);
    this.jModulationSourceInternalFrequency_Hz = new JSlider (0, 999, 0);
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesModulationColor (), this.jModulationSourceInternalFrequency_kHz, "kHz");
    this.jModulationSourceInternalFrequency_kHz.setMajorTickSpacing (10);
    this.jModulationSourceInternalFrequency_kHz.setMinorTickSpacing (1);
    this.jModulationSourceInternalFrequency_kHz.setPaintTicks (true);
    this.jModulationSourceInternalFrequency_kHz.setPaintLabels (true);
    this.jModulationSourceInternalFrequency_kHz.setToolTipText ("-");
    this.jModulationSourceInternalFrequency_kHz.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ())
      {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting internal modulation source frequency on instrument from slider to {0} kHz.",
              source.getValue ());
            signalGenerator.setModulationSourceInternalFrequency_kHz (
              source.getValue ()
              + 1.0e-3 * this.jModulationSourceInternalFrequency_Hz.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting internal modulation source frequency on instrument"
              + " from slider to {0} kHz.",
              new Object[]{source.getValue (), e});          
          }
      }
      else
        source.setToolTipText (Integer.toString (source.getValue ()));
    });
    modSourcesInternalPanel.add (this.jModulationSourceInternalFrequency_kHz);
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesModulationColor (), this.jModulationSourceInternalFrequency_Hz, "Hz");
    this.jModulationSourceInternalFrequency_Hz.setMajorTickSpacing (100);
    this.jModulationSourceInternalFrequency_Hz.setMinorTickSpacing (10);
    this.jModulationSourceInternalFrequency_Hz.setPaintTicks (true);
    this.jModulationSourceInternalFrequency_Hz.setPaintLabels (true);
    this.jModulationSourceInternalFrequency_Hz.setToolTipText ("-");
    this.jModulationSourceInternalFrequency_Hz.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ())
      {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting internal modulation source frequency on instrument from slider to {0} Hz.",
              source.getValue ());
            signalGenerator.setModulationSourceInternalFrequency_kHz (
              this.jModulationSourceInternalFrequency_kHz.getValue ()
              + 1.0e-3 * source.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting internal modulation source frequency on instrument"
              + " from slider to {0} Hz.",
              new Object[]{source.getValue (), e});          
          }
      }
      else
        source.setToolTipText (Integer.toString (source.getValue ()));
    });
    modSourcesInternalPanel.add (this.jModulationSourceInternalFrequency_Hz);
    modSourcesPanel.add (modSourcesInternalPanel);
    add (modSourcesPanel);

    final JPanel modAnalogPanel = new JPanel ();
    JInstrumentPanel.setPanelBorder (modAnalogPanel, getLevel () + 1, "Modulation [Analog]");
    modAnalogPanel.setLayout (new GridLayout (3, 1));
    //
    final JPanel amPanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesModulationColor (), amPanel, "AM");
    amPanel.setLayout (new GridLayout (1, 3));
    this.jAmEnable = new JColorCheckBox (
      (Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? JInstrumentPanel.getGuiPreferencesModulationColor () : null);
    this.jAmDepth = new JSlider (0, 1000, 500);
    this.jAmEnable.setDisplayedValue (false);
    this.jAmEnable.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        super.mouseClicked (me);
        final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
        try
        {
          final boolean newValue = ! source.getDisplayedValue ();
          // XXX This should not be necessary! Should be done upon new instrument settings.
          source.setDisplayedValue (newValue);
          LOG.log (Level.INFO, "Toggling AM on instrument; becomes {0}", newValue);
          signalGenerator.setEnableAm (newValue,
            JDefaultSignalGeneratorView.this.jAmDepth.getValue () / 10.0,
            SignalGenerator.ModulationSource.values ()[JDefaultSignalGeneratorView.this.jAmSource.getSelectedIndex ()]);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while toggling AM on instrument.");
        }
      }      
    });
    amPanel.add (this.jAmEnable);
    setSubPanelBorder (Color.black, this.jAmDepth, "Depth [%]");
    this.jAmDepth.setMajorTickSpacing (100);
    this.jAmDepth.setPaintTicks (true);
    this.jAmDepth.setToolTipText ("-");
    this.jAmDepth.addChangeListener ((final ChangeEvent ce) ->
    {
      final JSlider source = (JSlider) ce.getSource ();
      final double newDepth = JDefaultSignalGeneratorView.this.jAmDepth.getValue () / 10.0;
      if (! source.getValueIsAdjusting ())
      {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting modulation depth on instrument from slider to {0} %.", newDepth);
            signalGenerator.setEnableAm (JDefaultSignalGeneratorView.this.jAmEnable.getDisplayedValue (),
              JDefaultSignalGeneratorView.this.jAmDepth.getValue () / 10.0,
              SignalGenerator.ModulationSource.values ()[JDefaultSignalGeneratorView.this.jAmSource.getSelectedIndex ()]);
            source.setToolTipText (Double.toString (newDepth));
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting modulation depth on instrument"
              + " from slider to {0} %.",
              new Object[]{newDepth, e});          
          }
      }
      else
        source.setToolTipText (Double.toString (newDepth));
    });
    amPanel.add (this.jAmDepth);
    this.jAmSource = new JComboBox<> (ModulationSource.values ());
    setSubPanelBorder (Color.black, this.jAmSource, "Source");
    this.jAmSource.addItemListener ((ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final SignalGenerator.ModulationSource newValue = (SignalGenerator.ModulationSource) ie.getItem ();
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting AM modulation source on instrument from combo box to {0}.", newValue);
            signalGenerator.setEnableAm (JDefaultSignalGeneratorView.this.jAmEnable.getDisplayedValue (),
              JDefaultSignalGeneratorView.this.jAmDepth.getValue () / 10.0,
              newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting AM modulation source on instrument"
              + " from combo box to {0}.",
              new Object[]{newValue, e});
          }
      }
    });
    amPanel.add (this.jAmSource);
    modAnalogPanel.add (amPanel);
    //
    final JPanel fmPanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesModulationColor (), fmPanel, "FM");
    fmPanel.setLayout (new GridLayout (1, 3));
    this.jFmEnable = new JColorCheckBox (
      (Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? JInstrumentPanel.getGuiPreferencesModulationColor () : null);
    this.jFmDeviation_kHz = new JSlider (0, 100, 0);
    this.jFmSource = new JComboBox (ModulationSource.values ());
    this.jFmEnable.setDisplayedValue (false);
    this.jFmEnable.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        super.mouseClicked (me);
        final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
        try
        {
          final boolean newValue = ! source.getDisplayedValue ();
          // XXX This should not be necessary! Should be done upon new instrument settings.
          source.setDisplayedValue (newValue);
          LOG.log (Level.INFO, "Toggling FM on instrument; becomes {0}", newValue);
          signalGenerator.setEnableFm (newValue,
            JDefaultSignalGeneratorView.this.jFmDeviation_kHz.getValue (),
            SignalGenerator.ModulationSource.values ()[JDefaultSignalGeneratorView.this.jFmSource.getSelectedIndex ()]);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while toggling FM on instrument.");
        }
      }
    });
    fmPanel.add (this.jFmEnable);
    setSubPanelBorder (Color.black, this.jFmDeviation_kHz, "Max Dev [kHz]");
    this.jFmDeviation_kHz.setMajorTickSpacing (50);
    this.jFmDeviation_kHz.setMinorTickSpacing (10);
    this.jFmDeviation_kHz.setPaintTicks (true);
    this.jFmDeviation_kHz.setToolTipText ("-");
    this.jFmDeviation_kHz.addChangeListener ((final ChangeEvent ce) ->
    {
      final JSlider source = (JSlider) ce.getSource ();
      final double newDeviation_kHz = JDefaultSignalGeneratorView.this.jFmDeviation_kHz.getValue ();
      if (! source.getValueIsAdjusting ())
      {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting FM max deviation on instrument from slider to {0} kHz.", newDeviation_kHz);
            signalGenerator.setEnableFm (JDefaultSignalGeneratorView.this.jFmEnable.getDisplayedValue (),
              newDeviation_kHz,
              SignalGenerator.ModulationSource.values ()[JDefaultSignalGeneratorView.this.jFmSource.getSelectedIndex ()]);
            source.setToolTipText (Double.toString (newDeviation_kHz));
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting FM max deviation on instrument"
              + " from slider to {0} kHz.",
              new Object[]{newDeviation_kHz, e});          
          }
      }
      else
        source.setToolTipText (Double.toString (newDeviation_kHz));
    });
    fmPanel.add (this.jFmDeviation_kHz);
    setSubPanelBorder (Color.black, this.jFmSource, "Source");
    this.jFmSource.addItemListener ((ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final SignalGenerator.ModulationSource newValue = (SignalGenerator.ModulationSource) ie.getItem ();
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting FM modulation source on instrument from combo box to {0}.", newValue);
            signalGenerator.setEnableFm (JDefaultSignalGeneratorView.this.jFmEnable.getDisplayedValue (),
              JDefaultSignalGeneratorView.this.jFmDeviation_kHz.getValue (),
              newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting FM modulation source on instrument"
              + " from combo box to {0}.",
              new Object[]{newValue, e});
          }
      }
    });
    fmPanel.add (this.jFmSource);
    modAnalogPanel.add (fmPanel);
    //
    final JPanel pmPanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesModulationColor (), pmPanel, "PM");
    pmPanel.setLayout (new GridLayout (1, 3));
    this.jPmEnable = new JColorCheckBox (
      (Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? JInstrumentPanel.getGuiPreferencesModulationColor () : null);
    this.jPmEnable.setDisplayedValue (false);
    this.jPmEnable.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        super.mouseClicked (me);
        final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
        try
        {
          final boolean newValue = ! source.getDisplayedValue ();
          // XXX This should not be necessary! Should be done upon new instrument settings.
          source.setDisplayedValue (newValue);
          LOG.log (Level.INFO, "Toggling PM on instrument; becomes {0}", newValue);
          signalGenerator.setEnablePm (newValue,
            JDefaultSignalGeneratorView.this.jPmDeviation_degrees.getValue (),
            SignalGenerator.ModulationSource.values ()[JDefaultSignalGeneratorView.this.jPmSource.getSelectedIndex ()]);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while toggling PM on instrument.");
        }
      }
    });
    pmPanel.add (this.jPmEnable);
    this.jPmDeviation_degrees = new JSlider (0, 360, 0);
    setSubPanelBorder (Color.black, this.jPmDeviation_degrees, "Max Dev");
    this.jPmDeviation_degrees.addChangeListener ((final ChangeEvent ce) ->
    {
      final JSlider source = (JSlider) ce.getSource ();
      final double newDeviation_degrees = JDefaultSignalGeneratorView.this.jPmDeviation_degrees.getValue ();
      if (! source.getValueIsAdjusting ())
      {
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting PM max deviation on instrument from slider to {0} degrees.", newDeviation_degrees);
            signalGenerator.setEnablePm (JDefaultSignalGeneratorView.this.jPmEnable.getDisplayedValue (),
              newDeviation_degrees,
              SignalGenerator.ModulationSource.values ()[JDefaultSignalGeneratorView.this.jPmSource.getSelectedIndex ()]);
            source.setToolTipText (Double.toString (newDeviation_degrees));
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting PM max deviation on instrument"
              + " from slider to {0} degrees.",
              new Object[]{newDeviation_degrees, e});          
          }
      }
      else
        source.setToolTipText (Double.toString (newDeviation_degrees));
    });
    pmPanel.add (this.jPmDeviation_degrees);
    this.jPmSource = new JComboBox (ModulationSource.values ());
    setSubPanelBorder (Color.black, this.jPmSource, "Source");
    this.jPmSource.addItemListener ((ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final SignalGenerator.ModulationSource newValue = (SignalGenerator.ModulationSource) ie.getItem ();
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting PM modulation source on instrument from combo box to {0}.", newValue);
            signalGenerator.setEnablePm (JDefaultSignalGeneratorView.this.jPmEnable.getDisplayedValue (),
              JDefaultSignalGeneratorView.this.jPmDeviation_degrees.getValue (),
              newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting PM modulation source on instrument"
              + " from combo box to {0}.",
              new Object[]{newValue, e});
          }
      }
    });
    pmPanel.add (this.jPmSource);
    modAnalogPanel.add (pmPanel);
    //
    add (modAnalogPanel);
    
    final JPanel modDigitalPanel = new JPanel ();
    JInstrumentPanel.setPanelBorder (modDigitalPanel, getLevel () + 1, "Modulation [Digital]");
    modDigitalPanel.setLayout (new GridLayout (2, 1));
    final JPanel pulsePanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesModulationColor (), pulsePanel, "PULSE");
    pulsePanel.setLayout (new GridLayout (1, 2));
    this.jPulseMEnable = new JColorCheckBox (
      (Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? JInstrumentPanel.getGuiPreferencesModulationColor () : null);
    this.jPulseMSource = new JComboBox (ModulationSource.values ());
    this.jPulseMEnable.setDisplayedValue (false);
    this.jPulseMEnable.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        super.mouseClicked (me);
        final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
        try
        {
          final boolean newValue = ! source.getDisplayedValue ();
          // XXX This should not be necessary! Should be done upon new instrument settings.
          source.setDisplayedValue (newValue);
          LOG.log (Level.INFO, "Toggling PulseM on instrument; becomes {0}", newValue);
          signalGenerator.setEnablePulseM (newValue,
            SignalGenerator.ModulationSource.values ()[JDefaultSignalGeneratorView.this.jPulseMSource.getSelectedIndex ()]);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while toggling PulseM on instrument.");
        }
      }
    });
    pulsePanel.add (this.jPulseMEnable);
    setSubPanelBorder (Color.black, this.jPulseMSource, "Source");
    this.jPulseMSource.addItemListener ((ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final SignalGenerator.ModulationSource newValue = (SignalGenerator.ModulationSource) ie.getItem ();
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting PulseM modulation source on instrument from combo box to {0}.", newValue);
            signalGenerator.setEnablePulseM (JDefaultSignalGeneratorView.this.jPulseMEnable.getDisplayedValue (),
              newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting PulseM modulation source on instrument"
              + " from combo box to {0}.",
              new Object[]{newValue, e});
          }
      }
    });
    pulsePanel.add (this.jPulseMSource);
    modDigitalPanel.add (pulsePanel);
    final JPanel bpskPanel = new JPanel ();
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesModulationColor (), bpskPanel, "BPSK");
    bpskPanel.setLayout (new GridLayout (1, 2));
    this.jBpskEnable = new JColorCheckBox (
      (Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? JInstrumentPanel.getGuiPreferencesModulationColor () : null);
    this.jBpskSource = new JComboBox (ModulationSource.values ());
    this.jBpskEnable.setDisplayedValue (false);
    this.jBpskEnable.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        super.mouseClicked (me);
        final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
        try
        {
          final boolean newValue = ! source.getDisplayedValue ();
          // XXX This should not be necessary! Should be done upon new instrument settings.
          source.setDisplayedValue (newValue);
          LOG.log (Level.INFO, "Toggling Bpsk on instrument; becomes {0}", newValue);
          signalGenerator.setEnableBpsk (newValue,
            SignalGenerator.ModulationSource.values ()[JDefaultSignalGeneratorView.this.jBpskSource.getSelectedIndex ()]);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while toggling Bpsk on instrument.");
        }
      }
    });
    bpskPanel.add (this.jBpskEnable);
    setSubPanelBorder (Color.black, this.jBpskSource, "Source");
    this.jBpskSource.addItemListener ((ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final SignalGenerator.ModulationSource newValue = (SignalGenerator.ModulationSource) ie.getItem ();
        if (! JDefaultSignalGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting BPSK modulation source on instrument from combo box to {0}.", newValue);
            signalGenerator.setEnableBpsk (JDefaultSignalGeneratorView.this.jBpskEnable.getDisplayedValue (),
              newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting BPSK modulation source on instrument"
              + " from combo box to {0}.",
              new Object[]{newValue, e});
          }
      }
    });
    bpskPanel.add (this.jBpskSource);
    modDigitalPanel.add (bpskPanel);
    add (modDigitalPanel);
    
    getSignalGenerator ().addInstrumentListener (this.instrumentListener);
    
  }
  
  private void setSubPanelBorder (final Color color, final JComponent panel, final String title)
  {
    panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (color, 2, true), title));    
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
      return "Default [RF] Signal Generator View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof SignalGenerator))
        return new JDefaultSignalGeneratorView ((SignalGenerator) instrument);
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
    return JDefaultSignalGeneratorView.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JSevenSegmentNumber jCenterFreq;
  
  private final JSlider jFreqSlider_MHz;
  
  private final JSlider jFreqSlider_kHz;
  
  private final JSlider jFreqSlider_Hz;
  
  private final JSlider jFreqSlider_mHz;
  
  private final JComboBox<SignalGenerator.RfSweepMode> jRfSweepMode;
  
  private final JSevenSegmentNumber jSpan;
  
  private final JSevenSegmentNumber jStartFreq;
  
  private final JSevenSegmentNumber jStopFreq;
  
  private final JColorCheckBox<Boolean> jAmpEnable;
  
  private final JSevenSegmentNumber jAmplitudeAlt;
  
  private final JSlider jAmpSlider_dBm;
  
  private final JSlider jAmpSlider_mdBm;
  
  private final JSevenSegmentNumber jModulationSourceInternalFrequency;
  
  private final JSlider jModulationSourceInternalFrequency_kHz;
  
  private final JSlider jModulationSourceInternalFrequency_Hz;
  
  private final JColorCheckBox<Boolean> jAmEnable;
  
  private final JSlider jAmDepth;
  
  private final JComboBox jAmSource;
  
  private final JColorCheckBox<Boolean> jFmEnable;
  
  private final JSlider jFmDeviation_kHz;
  
  private final JComboBox jFmSource;
  
  private final JColorCheckBox<Boolean> jPmEnable;
  
  private final JSlider jPmDeviation_degrees;
  
  private final JComboBox jPmSource;
  
  private final JColorCheckBox<Boolean> jPulseMEnable;
  
  private final JComboBox jPulseMSource;
  
  private final JColorCheckBox<Boolean> jBpskEnable;
  
  private final JComboBox jBpskSource;
    
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
      if (instrument != JDefaultSignalGeneratorView.this.getSignalGenerator () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof SignalGeneratorSettings))
        throw new IllegalArgumentException ();
      final SignalGeneratorSettings settings = (SignalGeneratorSettings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JDefaultSignalGeneratorView.this.inhibitInstrumentControl = true;
        //
        try
        {
          //
          final double f_MHz = settings.getCenterFrequency_MHz ();
          final long f_mHz_long = Math.round (f_MHz * 1e9);
          JDefaultSignalGeneratorView.this.jCenterFreq.setNumber (f_MHz * 1.0e6);
          final int f_int_MHz = (int) (f_mHz_long / 1000000000L);
          JDefaultSignalGeneratorView.this.jFreqSlider_MHz.setValue (f_int_MHz);
          JDefaultSignalGeneratorView.this.jFreqSlider_MHz.setToolTipText (Integer.toString (f_int_MHz));
          final int f_rem_int_kHz = (int) ((f_mHz_long % 1000000000L) / 1000000L);
          JDefaultSignalGeneratorView.this.jFreqSlider_kHz.setValue (f_rem_int_kHz);
          JDefaultSignalGeneratorView.this.jFreqSlider_kHz.setToolTipText (Integer.toString (f_rem_int_kHz));
          final int f_rem_int_Hz = (int) ((f_mHz_long % 1000000L) / 1000L);
          JDefaultSignalGeneratorView.this.jFreqSlider_Hz.setValue (f_rem_int_Hz);
          JDefaultSignalGeneratorView.this.jFreqSlider_Hz.setToolTipText (Integer.toString (f_rem_int_Hz));
          final int f_rem_int_mHz = (int) (f_mHz_long % 1000L);
          JDefaultSignalGeneratorView.this.jFreqSlider_mHz.setValue (f_rem_int_mHz);
          JDefaultSignalGeneratorView.this.jFreqSlider_mHz.setToolTipText (Integer.toString (f_rem_int_mHz));
          //
          final double span_MHz = settings.getSpan_MHz ();
          JDefaultSignalGeneratorView.this.jSpan.setNumber (span_MHz * 1.0e6);
          //
          final double startFrequency_MHz = settings.getStartFrequency_MHz ();
          JDefaultSignalGeneratorView.this.jStartFreq.setNumber (startFrequency_MHz * 1.0e6);
          final double stopFrequency_MHz = settings.getStopFrequency_MHz ();
          JDefaultSignalGeneratorView.this.jStopFreq.setNumber (stopFrequency_MHz * 1.0e6);
          //
          // XXX Fix me later, when the value is reliable.
          // JDefaultSignalGeneratorView.this.jAmpEnable.setSelected (settings.getOutputEnable ());
          final double S_dBm = settings.getS_dBm ();
          JDefaultSignalGeneratorView.this.jAmplitudeAlt.setNumber (settings.getS_dBm ());
          final long S_mdBm_long = Math.round (S_dBm * 1e3);
          final int S_int_dBm = (int) (S_mdBm_long / 1000L);
          JDefaultSignalGeneratorView.this.jAmpSlider_dBm.setValue (S_int_dBm);
          JDefaultSignalGeneratorView.this.jAmpSlider_dBm.setToolTipText (Integer.toString (S_int_dBm));
          final int S_rem_int_mdBm = (int) (S_mdBm_long % 1000L);
          JDefaultSignalGeneratorView.this.jAmpSlider_mdBm.setValue (S_rem_int_mdBm);
          JDefaultSignalGeneratorView.this.jAmpSlider_mdBm.setToolTipText (Integer.toString (S_rem_int_mdBm));
          //
          final double modulationSourceInternalFrequency_kHz = settings.getModulationSourceInternalFrequency_kHz ();
          JDefaultSignalGeneratorView.this.jModulationSourceInternalFrequency.setNumber (modulationSourceInternalFrequency_kHz);
          final long modSrcIntFreq_Hz_long = Math.round (modulationSourceInternalFrequency_kHz * 1e3);
          final int modSrcIntFreq_int_kHz = (int) (modSrcIntFreq_Hz_long / 1000L);
          JDefaultSignalGeneratorView.this.jModulationSourceInternalFrequency_kHz.setValue (modSrcIntFreq_int_kHz);
          JDefaultSignalGeneratorView.this.jModulationSourceInternalFrequency_kHz.setToolTipText (
            Integer.toString (modSrcIntFreq_int_kHz));
          final int modSrcIntFreq_rem_int_Hz = (int) (modSrcIntFreq_Hz_long % 1000L);
          JDefaultSignalGeneratorView.this.jModulationSourceInternalFrequency_Hz.setValue (modSrcIntFreq_rem_int_Hz);
          JDefaultSignalGeneratorView.this.jModulationSourceInternalFrequency_Hz.setToolTipText (
            Integer.toString (modSrcIntFreq_rem_int_Hz));
          //
          // XXX Fix me later, when the value is reliable.
          // JDefaultSignalGeneratorView.this.jAmEnable.setDisplayedValue (settings.getAmEnable ());
          final double amDepth_percent = settings.getAmDepth_percent ();
          JDefaultSignalGeneratorView.this.jAmDepth.setValue ((int) Math.round (amDepth_percent * 10.0));
          JDefaultSignalGeneratorView.this.jAmDepth.setToolTipText (new DecimalFormat ("##0.0").format (amDepth_percent));
          //
          // XXX Fix me later, when the value is reliable.
          // JDefaultSignalGeneratorView.this.jFmEnable.setDisplayedValue (settings.getFmEnable ());
          final double fmDeviation_kHz = settings.getFmDeviation_kHz ();
          JDefaultSignalGeneratorView.this.jFmDeviation_kHz.setValue ((int) Math.round (fmDeviation_kHz));
          JDefaultSignalGeneratorView.this.jFmDeviation_kHz.setToolTipText (new DecimalFormat ("##0.0").format (fmDeviation_kHz));
        }
        finally
        {
          JDefaultSignalGeneratorView.this.inhibitInstrumentControl = false;
        }
      });
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      LOG.log (Level.WARNING, "Unexpected InstrumentReading: {0}.", instrumentReading);
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
