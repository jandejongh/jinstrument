package org.javajdj.jinstrument.r1.swing.sg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import org.javajdj.jinstrument.r1.instrument.Instrument;
import org.javajdj.jinstrument.r1.instrument.InstrumentListener;
import org.javajdj.jinstrument.r1.instrument.InstrumentSettings;
import org.javajdj.jinstrument.r1.instrument.sg.SignalGenerator;
import org.javajdj.jinstrument.r1.instrument.sg.SignalGenerator.ModulationSource;
import org.javajdj.jinstrument.r1.instrument.sg.SignalGeneratorSettings;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.swing.JServiceControl;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;
import org.javajdj.jswing.jbyte.JByte;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A one-size-fits-all Swing panel for control and status of a generic {@link SignalGenerator}.
 * 
 * @author Jan de Jongh
 *
 */
public class JSignalGeneratorDisplay
extends JPanel
// implements SpectrumAnalyzerClient
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JSignalGeneratorDisplay.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private boolean _toggle = false;
  
  public JSignalGeneratorDisplay (final SignalGenerator signalGenerator)
  {
    
    super ();
    
    if (signalGenerator == null)
      throw new IllegalArgumentException ();
    
    this.signalGenerator = signalGenerator;
    
    setOpaque (true);
    setMinimumSize (new Dimension (1024, 600));
    setPreferredSize (new Dimension (1024, 600));
    
    setLayout (new GridLayout (3, 3));
    
    final JPanel instrumentPanel = new JPanel ();
    instrumentPanel.setLayout (new GridLayout (4, 2));    
    setPanelBorder (instrumentPanel, "Instrument");
    instrumentPanel.add (new JLabel ("Enabled"));
    final Map<Boolean, Color> enableColorMap = new HashMap<> ();
    enableColorMap.put (false, null);
    enableColorMap.put (true, Color.red);
    final JServiceControl enabledCheckBox = new JServiceControl (this.signalGenerator, (Service.Status t) ->
    {
      switch (t)
      {
        case STOPPED: return null;
        case ACTIVE:  return Color.green;
        case ERROR:   return Color.orange;
        default:      throw new RuntimeException ();
      }
    });
    instrumentPanel.add (enabledCheckBox);
    instrumentPanel.add (new JLabel ("21"));
    instrumentPanel.add (this.jByte21);
    instrumentPanel.add (new JLabel ("81"));
    instrumentPanel.add (this.jByte81);
    instrumentPanel.add (new JLabel ("181"));
    instrumentPanel.add (this.jByte181);
    add (instrumentPanel);
    
    final JPanel frequencyPanel = new JPanel ();
    frequencyPanel.setOpaque (true);
    frequencyPanel.setLayout (new GridLayout (5, 1));
    //
    this.jCenterFreq = new JSevenSegmentNumber (FREQUENCY_COLOR, false, 11, 9);
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
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
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0} MHz.", source.getValue ());
        }
      }
      else
        source.setToolTipText (Integer.toString (source.getValue ()));
    });
    setSubPanelBorder (FREQUENCY_COLOR, this.jFreqSlider_MHz, "[MHz]");
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
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
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0} kHz.", source.getValue ());
        }
      }
    });
    setSubPanelBorder (FREQUENCY_COLOR, this.jFreqSlider_kHz, "[kHz]");
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
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
        else
        {
          // LOG.log (Level.INFO, "Suppressed instrument control for new value {0} Hz.", source.getValue ());
        }
      }
    });
    setSubPanelBorder (FREQUENCY_COLOR, this.jFreqSlider_Hz, "[Hz]");
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
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
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0} mHz.", source.getValue ());
        }
      }
    });
    setSubPanelBorder (FREQUENCY_COLOR, this.jFreqSlider_mHz, "[mHz]");
    frequencyPanel.add (this.jFreqSlider_mHz);
    //
    setPanelBorder (frequencyPanel, "Frequency [Hz]");
    add (frequencyPanel);
    
    final JPanel frequencySweepPanel = new JPanel ();
    frequencySweepPanel.setLayout (new GridLayout (4, 1));
    final JPanel frequencySweepModePanel = new JPanel ();
    setSubPanelBorder (FREQUENCY_COLOR, frequencySweepModePanel, "Mode");
    frequencySweepPanel.add (frequencySweepModePanel);
    final JPanel frequencySweepStartPanel = new JPanel ();
    setSubPanelBorder (FREQUENCY_COLOR, frequencySweepStartPanel, "Start Frequency");
    frequencySweepPanel.add (frequencySweepStartPanel);
    final JPanel frequencySweepStopPanel = new JPanel ();
    setSubPanelBorder (FREQUENCY_COLOR, frequencySweepStopPanel, "Stop Frequency");
    frequencySweepPanel.add (frequencySweepStopPanel);
    final JPanel frequencySweepSpanPanel = new JPanel ();
    setSubPanelBorder (FREQUENCY_COLOR, frequencySweepSpanPanel, "Span");
    frequencySweepPanel.add (frequencySweepSpanPanel);
    setPanelBorder (frequencySweepPanel, "Frequency Sweep");
    add (frequencySweepPanel);
    
    final JPanel sweepSources = new JPanel ();
    setPanelBorder (sweepSources, "Sweep Source(s)");
    sweepSources.setLayout (new BorderLayout ());
    add (sweepSources);
    
    final JPanel amplitudePanel = new JPanel ();
    setPanelBorder (amplitudePanel, "Amplitude [dBm]");
    amplitudePanel.setLayout (new GridLayout (3, 1));    
    //
    final JPanel amplitudeTopPanel = new JPanel ();
    amplitudeTopPanel.setLayout (new GridLayout (1, 2));
    //
    this.jAmpEnable = new JCheckBox ("Output Enable");
    this.jAmpEnable.addItemListener ((itemEvent) ->
    {
      if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
      {
        final boolean newOutputEnable = (itemEvent.getStateChange () == ItemEvent.SELECTED);
        try
        {
          signalGenerator.setOutputEnable (newOutputEnable);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting outputEnable to {0}: {1}.",
            new Object[]{newOutputEnable, e});          
        }
      }
     });
    amplitudeTopPanel.add (this.jAmpEnable);
    //
    this.jAmplitudeAlt = new JSevenSegmentNumber (AMPLITUDE_COLOR, true, 5, 2);
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
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
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0} dBm.", source.getValue ());
        }
      }
    });
    setSubPanelBorder (AMPLITUDE_COLOR, this.jAmpSlider_dBm, "[dBm]");
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
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
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0} mdBm.", source.getValue ());
        }
      }
    });
    setSubPanelBorder (AMPLITUDE_COLOR, this.jAmpSlider_mdBm, "[mdBm]");
    amplitudePanel.add (this.jAmpSlider_mdBm);
    //
    add (amplitudePanel);
    
    final JPanel amplitudeSweepPanel = new JPanel ();
    amplitudeSweepPanel.setLayout (new GridLayout (3, 1));
    final JPanel amplitudeSweepModePanel = new JPanel ();
    setSubPanelBorder (AMPLITUDE_COLOR, amplitudeSweepModePanel, "Mode");
    amplitudeSweepPanel.add (amplitudeSweepModePanel);
    final JPanel amplitudeSweepStartPanel = new JPanel ();
    setSubPanelBorder (AMPLITUDE_COLOR, amplitudeSweepStartPanel, "Start Amplitude");
    amplitudeSweepPanel.add (amplitudeSweepStartPanel);
    final JPanel amplitudeSweepStopPanel = new JPanel ();
    setSubPanelBorder (AMPLITUDE_COLOR, amplitudeSweepStopPanel, "Stop Amplitude");
    amplitudeSweepPanel.add (amplitudeSweepStopPanel);
    setPanelBorder (amplitudeSweepPanel, "Amplitude Sweep");
    add (amplitudeSweepPanel);
    
    final JPanel modSourcesPanel = new JPanel ();
    setPanelBorder (modSourcesPanel, "Modulation Source(s)");
    modSourcesPanel.setLayout (new GridLayout (1, 1));
    final JPanel modSourcesInternalPanel = new JPanel ();
    modSourcesInternalPanel.setLayout (new GridLayout (3, 1));
    setSubPanelBorder (Color.black, modSourcesInternalPanel, "Internal");
    this.jModulationSourceInternalFrequency = new JSevenSegmentNumber (MODULATION_COLOR, false, 5, 2);
    setSubPanelBorder (MODULATION_COLOR, this.jModulationSourceInternalFrequency, "Frequency [kHz]");
    modSourcesInternalPanel.add (this.jModulationSourceInternalFrequency);
    this.jModulationSourceInternalFrequency_kHz = new JSlider (0, 100, 1);
    this.jModulationSourceInternalFrequency_Hz = new JSlider (0, 999, 0);
    setSubPanelBorder (MODULATION_COLOR, this.jModulationSourceInternalFrequency_kHz, "kHz");
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
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
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0} kHz.", source.getValue ());
        }
      }
      else
        source.setToolTipText (Integer.toString (source.getValue ()));
    });
    modSourcesInternalPanel.add (this.jModulationSourceInternalFrequency_kHz);
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
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
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0} Hz.", source.getValue ());
        }
      }
      else
        source.setToolTipText (Integer.toString (source.getValue ()));
    });
    modSourcesInternalPanel.add (this.jModulationSourceInternalFrequency_Hz);
    modSourcesPanel.add (modSourcesInternalPanel);
    add (modSourcesPanel);

    final JPanel modAnalogPanel = new JPanel ();
    setPanelBorder (modAnalogPanel, "Modulation [Analog]");
    modAnalogPanel.setLayout (new GridLayout (3, 1));
    //
    final JPanel amPanel = new JPanel ();
    setSubPanelBorder (MODULATION_COLOR, amPanel, "AM");
    amPanel.setLayout (new GridLayout (1, 3));
    this.jAmEnable = new JColorCheckBox ((Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? MODULATION_COLOR : null);
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
          final boolean newValue = ! _toggle;
          LOG.log (Level.INFO, "Toggling AM on instrument; becomes {0}", newValue);
          // signalGenerator.setEnableAm (source.getDisplayedValue () == null || ! source.getDisplayedValue ());
          // signalGenerator.setEnableAm (source.getDisplayedValue () == null || ! source.getDisplayedValue ());
          signalGenerator.setEnableAm (
            newValue,
            JSignalGeneratorDisplay.this.jAmDepth.getValue () / 10.0,
            SignalGenerator.ModulationSource.values ()[JSignalGeneratorDisplay.this.jAmSource.getSelectedIndex ()]);
          _toggle = newValue;
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while toggling AM on instrument.");
        }
      }      
    });
    amPanel.add (this.jAmEnable);
    setSubPanelBorder (Color.black, this.jAmDepth, "Depth");
    this.jAmDepth.setMajorTickSpacing (100);
//  this.jAmDepth.setMinorTickSpacing (1);
    this.jAmDepth.setPaintTicks (true);
//  this.jAmDepth.setPaintLabels (true);
    this.jAmDepth.setToolTipText ("-");
    this.jAmDepth.addChangeListener ((final ChangeEvent ce) ->
    {
      final JSlider source = (JSlider) ce.getSource ();
      final double newDepth = JSignalGeneratorDisplay.this.jAmDepth.getValue () / 10.0;
      if (! source.getValueIsAdjusting ())
      {
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting modulation depth on instrument from slider to {0} %.", newDepth);
            signalGenerator.setEnableAm (
              JSignalGeneratorDisplay.this.jAmEnable.getDisplayedValue (),
              JSignalGeneratorDisplay.this.jAmDepth.getValue () / 10.0,
              SignalGenerator.ModulationSource.values ()[JSignalGeneratorDisplay.this.jAmSource.getSelectedIndex ()]);
            source.setToolTipText (Double.toString (newDepth));
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting modulation depth on instrument"
              + " from slider to {0} %.",
              new Object[]{newDepth, e});          
          }
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0} %.", newDepth);
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting AM modulation source on instrument from combo box to {0} %.", newValue);
            signalGenerator.setEnableAm (
              JSignalGeneratorDisplay.this.jAmEnable.getDisplayedValue (),
              JSignalGeneratorDisplay.this.jAmDepth.getValue () / 10.0,
              newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting AM modulation source on instrument"
              + " from combo box to {0}.",
              new Object[]{newValue, e});
          }
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0}.", newValue);
        }
      }
    });
    amPanel.add (this.jAmSource);
    modAnalogPanel.add (amPanel);
    //
    final JPanel fmPanel = new JPanel ();
    setSubPanelBorder (MODULATION_COLOR, fmPanel, "FM");
    fmPanel.setLayout (new GridLayout (1, 3));
    this.jFmEnable = new JColorCheckBox ((Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? MODULATION_COLOR : null);
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
          final boolean newValue = ! _toggle;
          LOG.log (Level.INFO, "Toggling FM on instrument; becomes {0}", newValue);
          // signalGenerator.setEnableFm (source.getDisplayedValue () == null || ! source.getDisplayedValue ());
          // signalGenerator.setEnableFm (source.getDisplayedValue () == null || ! source.getDisplayedValue ());
          signalGenerator.setEnableFm (
            newValue,
            JSignalGeneratorDisplay.this.jFmDeviation_kHz.getValue (),
            SignalGenerator.ModulationSource.values ()[JSignalGeneratorDisplay.this.jFmSource.getSelectedIndex ()]);
          _toggle = newValue;
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while toggling FM on instrument.");
        }
      }
    });
    fmPanel.add (this.jFmEnable);
    setSubPanelBorder (Color.black, this.jFmDeviation_kHz, "Max Dev");
    this.jFmDeviation_kHz.addChangeListener ((final ChangeEvent ce) ->
    {
      final JSlider source = (JSlider) ce.getSource ();
      final double newDeviation_kHz = JSignalGeneratorDisplay.this.jFmDeviation_kHz.getValue ();
      if (! source.getValueIsAdjusting ())
      {
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting FM max deviation on instrument from slider to {0} kHz.", newDeviation_kHz);
            signalGenerator.setEnableFm (
              JSignalGeneratorDisplay.this.jFmEnable.getDisplayedValue (),
              newDeviation_kHz,
              SignalGenerator.ModulationSource.values ()[JSignalGeneratorDisplay.this.jFmSource.getSelectedIndex ()]);
            source.setToolTipText (Double.toString (newDeviation_kHz));
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting FM max deviation on instrument"
              + " from slider to {0} kHz.",
              new Object[]{newDeviation_kHz, e});          
          }
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0} kHz.", newDeviation_kHz);
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting FM modulation source on instrument from combo box to {0}.", newValue);
            signalGenerator.setEnableFm (
              JSignalGeneratorDisplay.this.jFmEnable.getDisplayedValue (),
              JSignalGeneratorDisplay.this.jFmDeviation_kHz.getValue (),
              newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting FM modulation source on instrument"
              + " from combo box to {0}.",
              new Object[]{newValue, e});
          }
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0}.", newValue);
        }
      }
    });
    fmPanel.add (this.jFmSource);
    modAnalogPanel.add (fmPanel);
    //
    final JPanel pmPanel = new JPanel ();
    setSubPanelBorder (MODULATION_COLOR, pmPanel, "PM");
    pmPanel.setLayout (new GridLayout (1, 3));
    this.jPmEnable = new JColorCheckBox ((Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? MODULATION_COLOR : null);
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
          final boolean newValue = ! _toggle;
          LOG.log (Level.INFO, "Toggling PM on instrument; becomes {0}", newValue);
          // signalGenerator.setEnableFm (source.getDisplayedValue () == null || ! source.getDisplayedValue ());
          // signalGenerator.setEnableFm (source.getDisplayedValue () == null || ! source.getDisplayedValue ());
          signalGenerator.setEnablePm (
            newValue,
            JSignalGeneratorDisplay.this.jPmDeviation_degrees.getValue (),
            SignalGenerator.ModulationSource.values ()[JSignalGeneratorDisplay.this.jPmSource.getSelectedIndex ()]);
          _toggle = newValue;
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
      final double newDeviation_degrees = JSignalGeneratorDisplay.this.jPmDeviation_degrees.getValue ();
      if (! source.getValueIsAdjusting ())
      {
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting PM max deviation on instrument from slider to {0} degrees.", newDeviation_degrees);
            signalGenerator.setEnablePm (JSignalGeneratorDisplay.this.jPmEnable.getDisplayedValue (),
              newDeviation_degrees,
              SignalGenerator.ModulationSource.values ()[JSignalGeneratorDisplay.this.jPmSource.getSelectedIndex ()]);
            source.setToolTipText (Double.toString (newDeviation_degrees));
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting PM max deviation on instrument"
              + " from slider to {0} degrees.",
              new Object[]{newDeviation_degrees, e});          
          }
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0} degrees.", newDeviation_degrees);
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
        if (! JSignalGeneratorDisplay.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting PM modulation source on instrument from combo box to {0}.", newValue);
            signalGenerator.setEnablePm (
              JSignalGeneratorDisplay.this.jPmEnable.getDisplayedValue (),
              JSignalGeneratorDisplay.this.jPmDeviation_degrees.getValue (),
              newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting PM modulation source on instrument"
              + " from combo box to {0}.",
              new Object[]{newValue, e});
          }
        else
        {
          LOG.log (Level.INFO, "Suppressed instrument control for new value {0}.", newValue);
        }
      }
    });
    pmPanel.add (this.jPmSource);
    modAnalogPanel.add (pmPanel);
    //
    add (modAnalogPanel);
    
    final JPanel modDigitalPanel = new JPanel ();
    setPanelBorder (modDigitalPanel, "Modulation [Digital]");
    modDigitalPanel.setLayout (new GridLayout (2, 1));
    final JPanel pulsePanel = new JPanel ();
    setSubPanelBorder (MODULATION_COLOR, pulsePanel, "PULSE");
    modDigitalPanel.add (pulsePanel);
    final JPanel bpskPanel = new JPanel ();
    setSubPanelBorder (MODULATION_COLOR, bpskPanel, "BPSK");
    modDigitalPanel.add (bpskPanel);
    add (modDigitalPanel);
    
    this.signalGenerator.addInstrumentListener (this.instrumentListener);
    
  }
  
  private void setPanelBorder (final JComponent panel, final String title)
  {
    panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (Color.black, 4, true), title));
  }
  
  private void setSubPanelBorder (final Color color, final JComponent panel, final String title)
  {
    panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (color, 2, true), title));    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private Color FREQUENCY_COLOR = Color.blue;
  private Color AMPLITUDE_COLOR = Color.red;
  private Color MODULATION_COLOR = Color.green;
  
  private final JSevenSegmentNumber jCenterFreq;
  
  private final JSlider jFreqSlider_MHz;
  
  private final JSlider jFreqSlider_kHz;
  
  private final JSlider jFreqSlider_Hz;
  
  private final JSlider jFreqSlider_mHz;
  
  private final JCheckBox jAmpEnable;
  
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
  
  private final JByte jByte21 = new JByte ();
  private final JByte jByte81 = new JByte ();
  private final JByte jByte181 = new JByte ();
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SIGNAL GENERATOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final SignalGenerator signalGenerator;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Inhibits changes to settings on the instrument.
   * 
   * Only used by the Swing EDT to prevent that Swing components being updated from new instrument settings generate (change)
   * events and (as a result) instrument operations as a result.
   * The invocation of such operations should only result from the user manipulating Swing components.
   * 
   * <p>
   * Since the field is only used from the Swing EDT, it does not have to be volatile.
   * 
   */
  private boolean inhibitInstrumentControl = false;
  
  private final InstrumentListener instrumentListener = new InstrumentListener ()
  {
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JSignalGeneratorDisplay.this.signalGenerator || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof SignalGeneratorSettings))
        throw new IllegalArgumentException ();
      final SignalGeneratorSettings settings = (SignalGeneratorSettings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JSignalGeneratorDisplay.this.inhibitInstrumentControl = true;
        //
        JSignalGeneratorDisplay.this.jByte21.setDisplayedValue (settings.getBytes ()[21]);
        JSignalGeneratorDisplay.this.jByte81.setDisplayedValue (settings.getBytes ()[81]);
        JSignalGeneratorDisplay.this.jByte181.setDisplayedValue (settings.getBytes ()[181]);
        //
        final double f_MHz = settings.getCenterFrequency_MHz ();
        final long f_mHz_long = Math.round (f_MHz * 1e9);
        JSignalGeneratorDisplay.this.jCenterFreq.setNumber (f_MHz * 1.0e6);
        final int f_int_MHz = (int) (f_mHz_long / 1000000000L);
        JSignalGeneratorDisplay.this.jFreqSlider_MHz.setValue (f_int_MHz);
        JSignalGeneratorDisplay.this.jFreqSlider_MHz.setToolTipText (Integer.toString (f_int_MHz));
        final int f_rem_int_kHz = (int) ((f_mHz_long % 1000000000L) / 1000000L);
        JSignalGeneratorDisplay.this.jFreqSlider_kHz.setValue (f_rem_int_kHz);
        JSignalGeneratorDisplay.this.jFreqSlider_kHz.setToolTipText (Integer.toString (f_rem_int_kHz));
        final int f_rem_int_Hz = (int) ((f_mHz_long % 1000000L) / 1000L);
        JSignalGeneratorDisplay.this.jFreqSlider_Hz.setValue (f_rem_int_Hz);
        JSignalGeneratorDisplay.this.jFreqSlider_Hz.setToolTipText (Integer.toString (f_rem_int_Hz));
        final int f_rem_int_mHz = (int) (f_mHz_long % 1000L);
        JSignalGeneratorDisplay.this.jFreqSlider_mHz.setValue (f_rem_int_mHz);
        JSignalGeneratorDisplay.this.jFreqSlider_mHz.setToolTipText (Integer.toString (f_rem_int_mHz));
        //
        JSignalGeneratorDisplay.this.jAmpEnable.setSelected (settings.getOutputEnable ());
        final double S_dBm = settings.getS_dBm ();
        JSignalGeneratorDisplay.this.jAmplitudeAlt.setNumber (settings.getS_dBm ());
        final long S_mdBm_long = Math.round (S_dBm * 1e3);
        final int S_int_dBm = (int) (S_mdBm_long / 1000L);
        JSignalGeneratorDisplay.this.jAmpSlider_dBm.setValue (S_int_dBm);
        JSignalGeneratorDisplay.this.jAmpSlider_dBm.setToolTipText (Integer.toString (S_int_dBm));
        final int S_rem_int_mdBm = (int) (S_mdBm_long % 1000L);
        JSignalGeneratorDisplay.this.jAmpSlider_mdBm.setValue (S_rem_int_mdBm);
        JSignalGeneratorDisplay.this.jAmpSlider_mdBm.setToolTipText (Integer.toString (S_rem_int_mdBm));
        //
        final double modulationSourceInternalFrequency_kHz = settings.getModulationSourceInternalFrequency_kHz ();
        JSignalGeneratorDisplay.this.jModulationSourceInternalFrequency.setNumber (modulationSourceInternalFrequency_kHz);
        final long modSrcIntFreq_Hz_long = Math.round (modulationSourceInternalFrequency_kHz * 1e3);
        final int modSrcIntFreq_int_kHz = (int) (modSrcIntFreq_Hz_long / 1000L);
        JSignalGeneratorDisplay.this.jModulationSourceInternalFrequency_kHz.setValue (modSrcIntFreq_int_kHz);
        JSignalGeneratorDisplay.this.jModulationSourceInternalFrequency_kHz.setToolTipText (
          Integer.toString (modSrcIntFreq_int_kHz));
        final int modSrcIntFreq_rem_int_Hz = (int) (modSrcIntFreq_Hz_long % 1000L);
        JSignalGeneratorDisplay.this.jModulationSourceInternalFrequency_Hz.setValue (modSrcIntFreq_rem_int_Hz);
        JSignalGeneratorDisplay.this.jModulationSourceInternalFrequency_Hz.setToolTipText (
          Integer.toString (modSrcIntFreq_rem_int_Hz));
        //
        JSignalGeneratorDisplay.this.jAmEnable.setDisplayedValue (settings.getAmEnable ());
        final double amDepth_percent = settings.getAmDepth_percent ();
        JSignalGeneratorDisplay.this.jAmDepth.setValue ((int) Math.round (amDepth_percent * 10.0));
        JSignalGeneratorDisplay.this.jAmDepth.setToolTipText (new DecimalFormat ("##0.0").format (amDepth_percent));
        //
        JSignalGeneratorDisplay.this.inhibitInstrumentControl = false;
        //
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
