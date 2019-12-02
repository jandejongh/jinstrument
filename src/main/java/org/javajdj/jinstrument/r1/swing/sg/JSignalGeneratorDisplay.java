package org.javajdj.jinstrument.r1.swing.sg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import org.javajdj.jinstrument.r1.instrument.sg.SignalGenerator;
import org.javajdj.jinstrument.r1.instrument.sg.SignalGeneratorSettings;
import org.javajdj.jinstrument.r1.swing.util.JSevenSegmentNumber;

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
    
    final JPanel reserved1Panel = new JPanel ();
    reserved1Panel.setLayout (new BorderLayout ());
    setPanelBorder (reserved1Panel, "Instrument");
    add (reserved1Panel);
    
    final JPanel frequencyPanel = new JPanel ();
    frequencyPanel.setOpaque (true);
    frequencyPanel.setLayout (new GridLayout (5, 1));
    //
    this.jCenterFreq = new JSevenSegmentNumber (THEME_COLOR, false, 11, 9);
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
    setSubPanelBorder (this.jFreqSlider_MHz, "[MHz]");
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
    setSubPanelBorder (this.jFreqSlider_kHz, "[kHz]");
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
    setSubPanelBorder (this.jFreqSlider_Hz, "[Hz]");
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
    setSubPanelBorder (this.jFreqSlider_mHz, "[mHz]");
    frequencyPanel.add (this.jFreqSlider_mHz);
    //
    setPanelBorder (frequencyPanel, "Frequency");
    add (frequencyPanel);
    
    final JPanel fSweepPanel = new JPanel ();
    fSweepPanel.setLayout (new BorderLayout ());
    setPanelBorder (fSweepPanel, "Frequency Sweep");
    add (fSweepPanel);
    
    final JPanel sweepSources = new JPanel ();
    setPanelBorder (sweepSources, "Sweep Source(s)");
    sweepSources.setLayout (new BorderLayout ());
    add (sweepSources);
    
    final JPanel amplitudePanel = new JPanel ();
    setPanelBorder (amplitudePanel, "Amplitude");
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
        try
        {
          signalGenerator.setOutputEnable (itemEvent.getStateChange () == ItemEvent.SELECTED);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting outputEnable to {0}: {1}.",
            new Object[]{itemEvent.getStateChange () == ItemEvent.SELECTED, e});          
        }
      }
     });
    amplitudeTopPanel.add (this.jAmpEnable);
    //
    this.jAmplitudeAlt = new JSevenSegmentNumber (Color.red, true, 5, 2);
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
    setSubPanelBorder (Color.red, this.jAmpSlider_dBm, "[dBm]");
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
    setSubPanelBorder (Color.red, this.jAmpSlider_mdBm, "[mdBm]");
    amplitudePanel.add (this.jAmpSlider_mdBm);
    //
    add (amplitudePanel);
    
    final JPanel aSweepPanel = new JPanel ();
    setPanelBorder (aSweepPanel, "Amplitude Sweep");
    aSweepPanel.setLayout (new BorderLayout ());
    add (aSweepPanel);
    
    final JPanel modSourcesPanel = new JPanel ();
    setPanelBorder (modSourcesPanel, "Modulation Source(s)");
    modSourcesPanel.setLayout (new BorderLayout ());
    add (modSourcesPanel);

    final JPanel modAnalogPanel = new JPanel ();
    setPanelBorder (modAnalogPanel, "Modulation [Analog]");
    modAnalogPanel.setLayout (new GridLayout (3, 1));
    final JPanel amPanel = new JPanel ();
    setSubPanelBorder (Color.green, amPanel, "AM");
    modAnalogPanel.add (amPanel);
    final JPanel fmPanel = new JPanel ();
    setSubPanelBorder (Color.green, fmPanel, "FM");
    modAnalogPanel.add (fmPanel);
    final JPanel pmPanel = new JPanel ();
    setSubPanelBorder (Color.green, pmPanel, "PM");
    modAnalogPanel.add (pmPanel);
    add (modAnalogPanel);
    
    final JPanel modDigitalPanel = new JPanel ();
    setPanelBorder (modDigitalPanel, "Modulation [Digital]");
    modDigitalPanel.setLayout (new GridLayout (2, 1));
    final JPanel bpskPanel = new JPanel ();
    setSubPanelBorder (Color.green, bpskPanel, "BPSK");
    modDigitalPanel.add (bpskPanel);
    final JPanel pulsePanel = new JPanel ();
    setSubPanelBorder (Color.green, pulsePanel, "PULSE");
    modDigitalPanel.add (pulsePanel);
    add (modDigitalPanel);
            
    startSettingsThread (true);
    
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
  
  private void setSubPanelBorder (final JComponent panel, final String title)
  {
    setSubPanelBorder (THEME_COLOR, panel, title);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private Color THEME_COLOR = Color.blue;
  
  private final JSevenSegmentNumber jCenterFreq;
  
  private final JSlider jFreqSlider_MHz;
  
  private final JSlider jFreqSlider_kHz;
  
  private final JSlider jFreqSlider_Hz;
  
  private final JSlider jFreqSlider_mHz;
  
  private final JCheckBox jAmpEnable;
  
  private final JSevenSegmentNumber jAmplitudeAlt;
  
  private final JSlider jAmpSlider_dBm;
  
  private final JSlider jAmpSlider_mdBm;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SIGNAL GENERATOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final SignalGenerator signalGenerator;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SETTINGS THREAD [CLASS]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private class SettingsThread
  extends Thread
  {

    private final boolean continuous;
    
    public SettingsThread (final boolean continuous)
    {
      this.continuous = continuous;
    }
  
    private volatile SignalGeneratorSettings settings = null;
    
    private volatile boolean newSettings = false;
    
    @Override
    public final void run ()
    {
      while (! isInterrupted ())
      {
        try
        {
          // XXX
          Thread.sleep (5000L);
          final SignalGeneratorSettings settingsCopy;
          final boolean newSettingsCopy;
          synchronized (this)
          {
            settingsCopy = this.settings;
            newSettingsCopy = this.newSettings;
            this.newSettings = false;
          }
          // Obtain current settings from the signal generator.
          final SignalGeneratorSettings settingsRead = JSignalGeneratorDisplay.this.signalGenerator.getSettingsFromInstrument ();
          synchronized (this)
          {
            if (this.settings == null || (newSettingsCopy && ! this.newSettings))
              this.settings = settingsRead;
          }
          // Report the trace to our superclass.
          JSignalGeneratorDisplay.this.newSettingsFromSignalGenerator (settingsRead);
        }
        catch (InterruptedException ie)
        {
          LOG.log (Level.WARNING, "InterruptedException: {0}.", ie.getMessage ());
          JSignalGeneratorDisplay.this.setSettingsThread (null);
          return;
        }
        catch (IOException ioe)
        {
          LOG.log (Level.WARNING, "IOException: {0}.", ioe.getMessage ());
        }
        catch (NumberFormatException nfe)
        {
          LOG.log (Level.WARNING, "NumberFormatException: {0}.", nfe.getMessage ());
        }
        if (! this.continuous)
        {
          JSignalGeneratorDisplay.this.setSettingsThread (null);
          return;            
        }
      }
    }
  
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE THREAD [MEMBER]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private SettingsThread settingsThread = null;
  
  public final synchronized void startSettingsThread (final boolean continuous)
  {
    this.settingsThread = new SettingsThread (continuous);
    this.settingsThread.start ();
//    if (this.statusBar != null)
//      this.statusBar.updateComponent ();
  }
  
  private synchronized void setSettingsThread (final SettingsThread settingsThread)
  {
    this.settingsThread = settingsThread;
//    if (this.statusBar != null)
//      this.statusBar.updateComponent ();
  }
  
  public final synchronized SettingsThread getSettingsThread ()
  {
    return this.settingsThread;
  }
  
  public final synchronized boolean isRunningSettingsThread ()
  {
    return this.settingsThread != null && this.settingsThread.isAlive ();
  }
  
  public final synchronized void disposeSettingsThread ()
  {
    if (this.settingsThread != null)
      this.settingsThread.interrupt ();
    this.settingsThread = null;
//    if (this.statusBar != null)
//      this.statusBar.updateComponent ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // METHOD newTraceFromSpectrumAnalyzer
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Inhibits changes to settings on the instrument.
   * 
   * Only used by the Swing EDT to prevent that Swing components being updated from new instrument settings generate (change)
   * events and generate instrument operations as a result.
   * The invocation of such operations should only result from the user manipulating Swing components.
   * 
   * <p>
   * Since the field is only used from the Swing EDT, it does not have to be volatile.
   * 
   * @see #newSettingsFromSignalGenerator
   * 
   */
  private boolean inhibitInstrumentControl = false;
  
  private synchronized void newSettingsFromSignalGenerator (final SignalGeneratorSettings settings)
  {
    //
    if (settings == null)
      throw new IllegalArgumentException ();
    //
    SwingUtilities.invokeLater (() ->
    {
      JSignalGeneratorDisplay.this.inhibitInstrumentControl = true;
      //
      final double f_MHz = settings.getCenterFrequency_MHz ();
      final long f_mHz_long = Math.round (f_MHz * 1e9);
      //
      JSignalGeneratorDisplay.this.jCenterFreq.setNumber (f_MHz * 1.0e6);
      //
      final int f_int_MHz = (int) (f_mHz_long / 1000000000L);
      JSignalGeneratorDisplay.this.jFreqSlider_MHz.setValue (f_int_MHz);
      JSignalGeneratorDisplay.this.jFreqSlider_MHz.setToolTipText (Integer.toString (f_int_MHz));
      //
      final int f_rem_int_kHz = (int) ((f_mHz_long % 1000000000L) / 1000000L);
      JSignalGeneratorDisplay.this.jFreqSlider_kHz.setValue (f_rem_int_kHz);
      JSignalGeneratorDisplay.this.jFreqSlider_kHz.setToolTipText (Integer.toString (f_rem_int_kHz));
      //
      final int f_rem_int_Hz = (int) ((f_mHz_long % 1000000L) / 1000L);
      JSignalGeneratorDisplay.this.jFreqSlider_Hz.setValue (f_rem_int_Hz);
      JSignalGeneratorDisplay.this.jFreqSlider_Hz.setToolTipText (Integer.toString (f_rem_int_Hz));
      //
      final int f_rem_int_mHz = (int) (f_mHz_long % 1000L);
      JSignalGeneratorDisplay.this.jFreqSlider_mHz.setValue (f_rem_int_mHz);
      JSignalGeneratorDisplay.this.jFreqSlider_mHz.setToolTipText (Integer.toString (f_rem_int_mHz));
      //
      final double S_dBm = settings.getS_dBm ();
      final long S_mdBm_long = Math.round (S_dBm * 1e3);
      //
      JSignalGeneratorDisplay.this.jAmplitudeAlt.setNumber (settings.getS_dBm ());
      //
      final int S_int_dBm = (int) (S_mdBm_long / 1000L);
      JSignalGeneratorDisplay.this.jAmpSlider_dBm.setValue (S_int_dBm);
      JSignalGeneratorDisplay.this.jAmpSlider_dBm.setToolTipText (Integer.toString (S_int_dBm));
      //
      final int S_rem_int_mdBm = (int) (S_mdBm_long % 1000L);
      JSignalGeneratorDisplay.this.jAmpSlider_mdBm.setValue (S_rem_int_mdBm);
      JSignalGeneratorDisplay.this.jAmpSlider_mdBm.setToolTipText (Integer.toString (S_rem_int_mdBm));
      //
      JSignalGeneratorDisplay.this.jAmpEnable.setSelected (settings.getOutputEnable ());
      JSignalGeneratorDisplay.this.inhibitInstrumentControl = false;
    });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY allAuto
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final synchronized boolean isAllAuto ()
  {
//    return this.trace != null
//      && this.trace.getSettings ().isAutoResolutionBandwidth ()
//      && this.trace.getSettings ().isAutoVideoBandwidth ()
//      && this.trace.getSettings ().isAutoSweepTime ();
    return false;
  }
  
  public final synchronized void setAllAuto ()
  {
    // XXX locking!!!
//    final SpectrumAnalyzerTraceSettings traceSettings = this.trace.getSettings ();
//    final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
//        (traceSettings.getCenterFrequency_MHz (),
//         traceSettings.getSpan_MHz (),
//         traceSettings.getResolutionBandwidth_Hz (),
//         true,
//         traceSettings.getVideoBandwidth_Hz (),
//         true,
//         traceSettings.getTraceLength (),
//         traceSettings.getSweepTime_s (),
//         true,
//         traceSettings.getReferenceLevel_dBm ());
//    this.settingsThread.setTraceSettings (newSettings);    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MOUSE HANDLING [FROM DISPLAY]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected final void mouseButton1Clicked (final double f_MHz, final double S_dBm)
  {
//    setCenterFrequency_MHz (f_MHz);
  }
  
  protected final void mouseButton1DoubleClicked (final double f_MHz, final double S_dBm)
  {
  }
  
  protected final void mouseButton1Dragged (final double f1_MHz, final double f2_MHz, final double S1_dBm, final double S2_dBm)
  {
//    if (f2_MHz > f1_MHz)
//      setCenterFrequencyAndSpan_MHz ((f1_MHz + f2_MHz) / 2, f2_MHz - f1_MHz);
//    else
//      doubleSpan ();
  }
  
  protected final void mouseButton2Clicked (final double f_MHz, final double S_dBm)
  {
//        final double f_MHz = xToF_MHz (e.getX ());
//        final double span_MHz = JSpectrumAnalyzerTraceDisplay.this.trace.getSettings ().getSpan_MHz () / 2;
//        synchronized (JSpectrumAnalyzerTraceDisplay.this)
//        {
//          if (JSpectrumAnalyzerTraceDisplay.this.settingsThread != null)
//            JSpectrumAnalyzerTraceDisplay.this.setCenterFrequencyAndSpan_MHz (f_MHz, span_MHz);
//        }
  }
  
  protected final void mouseButton2DoubleClicked (final double f_MHz, final double S_dBm)
  {
  }
  
  protected final void mouseButton2Dragged (final double f1_MHz, final double f2_MHz, final double S1_dBm, final double S2_dBm)
  {
//        synchronized (JSpectrumAnalyzerTraceDisplay.this)
//        {
//          final double f1 = xToF_MHz (x);
//          final double f2 = xToF_MHz (JSpectrumAnalyzerTraceDisplay.this.mousePressX);
//          final double newFc = (f1 + f2) / 2;
//          final double newSpan = Math.abs (f2 - f1);
//          if (JSpectrumAnalyzerTraceDisplay.this.settingsThread != null)
//            JSpectrumAnalyzerTraceDisplay.this.setCenterFrequencyAndSpan_MHz (newFc, newSpan);
//        }
  }
  
  protected final void mouseButton3Clicked (final double f_MHz, final double S_dBm)
  {
//        synchronized (JSpectrumAnalyzerTraceDisplay.this)
//        {
//          if (JSpectrumAnalyzerTraceDisplay.this.settingsThread != null)
//            if (e.getClickCount () == 1)
//            {
//              final double f_MHz = xToF_MHz (e.getX ());
//              final double span_MHz = JSpectrumAnalyzerTraceDisplay.this.trace.getSettings ().getSpan_MHz () * 2;
//              JSpectrumAnalyzerTraceDisplay.this.setCenterFrequencyAndSpan_MHz (f_MHz, span_MHz);
//            }
//            else
//              // XXX TODO Should really use 'FS' (Full Scan) here!
//              JSpectrumAnalyzerTraceDisplay.this.setCenterFrequencyAndSpan_MHz (13250, 26500);            
//        }
  }
  
  protected final void mouseButton3DoubleClicked (final double f_MHz, final double S_dBm)
  {
  }
  
  protected final void mouseButton3Dragged (final double f1_MHz, final double f2_MHz, final double S1_dBm, final double S2_dBm)
  {
//        synchronized (JSpectrumAnalyzerTraceDisplay.this)
//        {
//          final double f1 = xToF_MHz (x);
//          final double f2 = xToF_MHz (JSpectrumAnalyzerTraceDisplay.this.mousePressX);
//          final double newFc = (f1 + f2) / 2;
//          final double newSpan = JSpectrumAnalyzerTraceDisplay.this.trace.getSettings ().getSpan_MHz () * 2;
//          if (JSpectrumAnalyzerTraceDisplay.this.settingsThread != null)
//            JSpectrumAnalyzerTraceDisplay.this.setCenterFrequencyAndSpan_MHz (newFc, newSpan);
//        }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS BAR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
//  private class StatusBar extends JPanel
//  {
//
//    private final JStatusCheckBox statCheckBox;
//    private final JStatusCheckBox acqThreadCheckBox;
//    
//    private final JStatusCheckBox errCheckBox;
//    private final JStatusCheckBox calCheckBox;
//    private final JStatusCheckBox corCheckBox;
//    
//    private final JTextField cfTextField;
//    private final JTextField spTextField;
//    private final JLabel rbLabel;
//    private final JTextField rbTextField;
//    private final JLabel vbLabel;
//    private final JTextField vbTextField;
//    private final JLabel stLabel;
//    private final JTextField stTextField;
//    private final JLabel tlLabel;
//    private final JTextField tlTextField;
//    
//    private final JTextField rlTextField;
//    private final JTextField atTextField;
//    private final JTextField detTextField;
//    
//    private final JCheckBox latestCheckBox;
//    private final JCheckBox avgCheckBox;
//    private final JCheckBox avgFCheckBox;
//    
//    private final Color BORDER_COLOR = Color.blue;
//    
//    public StatusBar ()
//    {
//      final Box box = new Box (BoxLayout.Y_AXIS);
//      //
//      final Box instrumentStatusBox = new Box (BoxLayout.Y_AXIS);
//      instrumentStatusBox.setBorder
//        (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (BORDER_COLOR, 3, true), "Instrument"));
//      //
//      final Box statBox = new Box (BoxLayout.X_AXIS);
//      final JLabel statLabel = new JLabel ("STS  ");
//      statLabel.setToolTipText ("Instrument status; click for diagnostics when red!");
//      statBox.add (statLabel);
//      this.statCheckBox = new JStatusCheckBox ();
//      statBox.add (this.statCheckBox);
//      statBox.add (Box.createHorizontalGlue ());
//      instrumentStatusBox.add (statBox);
//      //
//      final Box acqThreadBox = new Box (BoxLayout.X_AXIS);
//      final JLabel acqThreadLabel = new JLabel ("ACQT ");
//      acqThreadLabel.setToolTipText ("Acquisition thread; left for start/stop (continuous); middle for one-shot sweep!");
//      acqThreadBox.add (acqThreadLabel);
//      this.acqThreadCheckBox = new JStatusCheckBox ();
//      this.acqThreadCheckBox.setSelected (JSignalGeneratorDisplay.this.isRunningSettingsThread ());
//      this.acqThreadCheckBox.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          switch (e.getButton ())
//          {
//            case MouseEvent.BUTTON1:
//              synchronized (JSignalGeneratorDisplay.this)
//              {
//                if (isRunningSettingsThread ())
//                  JSignalGeneratorDisplay.this.disposeSettingsThread ();
//                else
//                  JSignalGeneratorDisplay.this.startSettingsThread (true);
//              }
//              break;
//            case MouseEvent.BUTTON2:
//              break;
//            case MouseEvent.BUTTON3:
//              break;
//            default:
//              break;
//          }
//        }
//      });
//      acqThreadBox.add (this.acqThreadCheckBox);
//      acqThreadBox.add (Box.createHorizontalGlue ());
//      instrumentStatusBox.add (acqThreadBox);
//      //
//      box.add (instrumentStatusBox);
//      //
//      final Box traceStatusBox = new Box (BoxLayout.Y_AXIS);
//      traceStatusBox.setBorder
//        (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (BORDER_COLOR, 3, true), "Trace Status"));
//      //
//      final Box errBox = new Box (BoxLayout.X_AXIS);
//      final JLabel errLabel = new JLabel ("ERR  ");
//      errLabel.setToolTipText ("Error in trace or instrument when red!");
//      errBox.add (errLabel);
//      this.errCheckBox = new JStatusCheckBox ();
//      errBox.add (this.errCheckBox);
//      errBox.add (Box.createHorizontalGlue ());
//      traceStatusBox.add (errBox);
//      //
//      final Box calBox = new Box (BoxLayout.X_AXIS);
//      final JLabel calLabel = new JLabel ("CAL  ");
//      calLabel.setToolTipText ("Uncalibrated when red!");
//      calBox.add (calLabel);
//      this.calCheckBox = new JStatusCheckBox ();
//      calBox.add (this.calCheckBox);
//      calBox.add (Box.createHorizontalGlue ());
//      traceStatusBox.add (calBox);
//      //
//      final Box corBox = new Box (BoxLayout.X_AXIS);
//      final JLabel corLabel = new JLabel ("COR  ");
//      corLabel.setToolTipText ("Uncorrected when red!");
//      corBox.add (corLabel);
//      this.corCheckBox = new JStatusCheckBox ();
//      corBox.add (this.corCheckBox);
//      corBox.add (Box.createHorizontalGlue ());
//      traceStatusBox.add (corBox);
//      //
//      box.add (traceStatusBox);
//      //
//      final Box freqBox = new Box (BoxLayout.Y_AXIS);
//      freqBox.setBorder (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (BORDER_COLOR, 3, true), "Frequency"));
//      //
//      final Box cfBox = new Box (BoxLayout.X_AXIS);
//      final JLabel cfLabel = new JLabel ("CF   ");
//      cfLabel.setToolTipText ("Center Frequency");
//      cfBox.add (cfLabel);
//      this.cfTextField = new JTextField (10);
//      this.cfTextField.setEditable (false);
//      this.cfTextField.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          final String inputString = JOptionPane.showInputDialog ("Center Frequency [MHz]:");
//          if (inputString != null)
//          {
//            final String[] splitString = inputString.split ("\\s+");
//            if (splitString == null || splitString.length == 0 || splitString.length > 2)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            final String valueString = splitString[0];
//            final String unitString = splitString.length > 1 ? splitString[1] : null;
//            final double conversionFactor;
//            if (unitString != null)
//            {
//              if (unitString.equalsIgnoreCase ("Hz"))
//                conversionFactor = 1.0e-6;
//              else if (unitString.equalsIgnoreCase ("kHz"))
//                conversionFactor = 1.0e-3;
//              else if (unitString.equalsIgnoreCase ("MHz"))
//                conversionFactor = 1.0;
//              else if (unitString.equalsIgnoreCase ("GHz"))
//                conversionFactor = 1.0e3;
//              else
//              {
//                JOptionPane.showMessageDialog
//                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//                return;
//              }
//            }
//            else
//              conversionFactor = 1.0;
//            final double fMHz;
//            try
//            {
//              fMHz = Double.parseDouble (valueString);
//            }
//            catch (NumberFormatException nfe)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            JSignalGeneratorDisplay.this.setCenterFrequency_MHz (conversionFactor * fMHz);
//          }
//        }
//      });
//      cfBox.add (this.cfTextField);
//      final JLabel cfUnitsLabel = new JLabel (" MHz");
//      cfBox.add (cfUnitsLabel);
//      freqBox.add (cfBox);
//      //
//      final Box spBox = new Box (BoxLayout.X_AXIS);
//      final JLabel spLabel = new JLabel ("SP   ");
//      spLabel.setToolTipText ("Span");
//      spBox.add (spLabel);
//      this.spTextField = new JTextField (10);
//      this.spTextField.setEditable (false);
//      this.spTextField.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          final String inputString = JOptionPane.showInputDialog ("Span [MHz]:");
//          if (inputString != null)
//          {
//            final String[] splitString = inputString.split ("\\s+");
//            if (splitString == null || splitString.length == 0 || splitString.length > 2)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            final String valueString = splitString[0];
//            final String unitString = splitString.length > 1 ? splitString[1] : null;
//            final double conversionFactor;
//            if (unitString != null)
//            {
//              if (unitString.equalsIgnoreCase ("Hz"))
//                conversionFactor = 1.0e-6;
//              else if (unitString.equalsIgnoreCase ("kHz"))
//                conversionFactor = 1.0e-3;
//              else if (unitString.equalsIgnoreCase ("MHz"))
//                conversionFactor = 1.0;
//              else if (unitString.equalsIgnoreCase ("GHz"))
//                conversionFactor = 1.0e3;
//              else
//              {
//                JOptionPane.showMessageDialog
//                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//                return;
//              }
//            }
//            else
//              conversionFactor = 1.0;
//            final double span_MHz;
//            try
//            {
//              span_MHz = Double.parseDouble (valueString);
//            }
//            catch (NumberFormatException nfe)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            JSignalGeneratorDisplay.this.setSpan_MHz (conversionFactor * span_MHz);
//          }
//        }
//      });
//      spBox.add (this.spTextField);
//      final JLabel spUnitsLabel = new JLabel (" MHz");
//      spBox.add (spUnitsLabel);
//      freqBox.add (spBox);
//      //
//      final Box rbBox = new Box (BoxLayout.X_AXIS);
//      this.rbLabel = new JLabel ("RB   ");
//      this.rbLabel.setToolTipText ("Resolution Bandwidth - click for AUTO");
//      this.rbLabel.setOpaque (true);
//      this.rbLabel.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          JSignalGeneratorDisplay.this.setAutoResolutionBandwidth ();
//        }
//      });
//      rbBox.add (this.rbLabel);
//      this.rbTextField = new JTextField (10);
//      this.rbTextField.setEditable (false);
//      this.rbTextField.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          final String inputString = JOptionPane.showInputDialog ("Resolution Bandwidth [Hz]:");
//          if (inputString != null)
//          {
//            final String[] splitString = inputString.split ("\\s+");
//            if (splitString == null || splitString.length == 0 || splitString.length > 2)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            final String valueString = splitString[0];
//            final String unitString = splitString.length > 1 ? splitString[1] : null;
//            final double conversionFactor;
//            if (unitString != null)
//            {
//              if (unitString.equalsIgnoreCase ("Hz"))
//                conversionFactor = 1.0;
//              else if (unitString.equalsIgnoreCase ("kHz"))
//                conversionFactor = 1.0e3;
//              else if (unitString.equalsIgnoreCase ("MHz"))
//                conversionFactor = 1.0e6;
//              else if (unitString.equalsIgnoreCase ("GHz"))
//                conversionFactor = 1.0e9;
//              else
//              {
//                JOptionPane.showMessageDialog
//                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//                return;
//              }
//            }
//            else
//              conversionFactor = 1.0;
//            final double rb_Hz;
//            try
//            {
//              rb_Hz = Double.parseDouble (valueString);
//            }
//            catch (NumberFormatException nfe)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            JSignalGeneratorDisplay.this.setResolutionBandwidth_Hz (conversionFactor * rb_Hz);
//          }
//        }
//      });
//      rbBox.add (this.rbTextField);
//      final JLabel rbUnitsLabel = new JLabel ("    Hz");
//      rbBox.add (rbUnitsLabel);
//      freqBox.add (rbBox);
//      //
//      final Box vbBox = new Box (BoxLayout.X_AXIS);
//      this.vbLabel = new JLabel ("VB   ");
//      this.vbLabel.setToolTipText ("Video Bandwidth - click for AUTO");
//      this.vbLabel.setOpaque (true);
//      this.vbLabel.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          JSignalGeneratorDisplay.this.setAutoVideoBandwidth ();
//        }
//      });
//      vbBox.add (this.vbLabel);
//      this.vbTextField = new JTextField (10);
//      this.vbTextField.setEditable (false);
//      this.vbTextField.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          final String inputString = JOptionPane.showInputDialog ("Video Bandwidth [Hz]:");
//          if (inputString != null)
//          {
//            final String[] splitString = inputString.split ("\\s+");
//            if (splitString == null || splitString.length == 0 || splitString.length > 2)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            final String valueString = splitString[0];
//            final String unitString = splitString.length > 1 ? splitString[1] : null;
//            final double conversionFactor;
//            if (unitString != null)
//            {
//              if (unitString.equalsIgnoreCase ("Hz"))
//                conversionFactor = 1.0;
//              else if (unitString.equalsIgnoreCase ("kHz"))
//                conversionFactor = 1.0e3;
//              else if (unitString.equalsIgnoreCase ("MHz"))
//                conversionFactor = 1.0e6;
//              else if (unitString.equalsIgnoreCase ("GHz"))
//                conversionFactor = 1.0e9;
//              else
//              {
//                JOptionPane.showMessageDialog
//                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//                return;
//              }
//            }
//            else
//              conversionFactor = 1.0;
//            final double vb_Hz;
//            try
//            {
//              vb_Hz = Double.parseDouble (valueString);
//            }
//            catch (NumberFormatException nfe)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            JSignalGeneratorDisplay.this.setVideoBandwidth_Hz (conversionFactor * vb_Hz);
//          }
//        }
//      });
//      vbBox.add (this.vbTextField);
//      final JLabel vbUnitsLabel = new JLabel ("    Hz");
//      vbBox.add (vbUnitsLabel);
//      freqBox.add (vbBox);
//      //
//      final Box stBox = new Box (BoxLayout.X_AXIS);
//      this.stLabel = new JLabel ("ST   ");
//      this.stLabel.setToolTipText ("Sweep Time - click for AUTO");
//      this.stLabel.setOpaque (true);
//      this.stLabel.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          JSignalGeneratorDisplay.this.setAutoSweepTime ();
//        }
//      });
//      stBox.add (this.stLabel);
//      this.stTextField = new JTextField (10);
//      this.stTextField.setEditable (false);
//      this.stTextField.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          final String inputString = JOptionPane.showInputDialog ("Sweep Time [s]:");
//          if (inputString != null)
//          {
//            final String[] splitString = inputString.split ("\\s+");
//            if (splitString == null || splitString.length == 0 || splitString.length > 2)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            final String valueString = splitString[0];
//            final String unitString = splitString.length > 1 ? splitString[1] : null;
//            final double conversionFactor;
//            if (unitString != null)
//            {
//              if (unitString.equalsIgnoreCase ("ms") || unitString.equals ("mis"))
//                conversionFactor = 1.0e-3;
//              else if (unitString.equalsIgnoreCase ("s"))
//                conversionFactor = 1.0;
//              else if (unitString.equalsIgnoreCase ("min"))
//                conversionFactor = 60;
//              else
//              {
//                JOptionPane.showMessageDialog
//                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//                return;
//              }
//            }
//            else
//              conversionFactor = 1.0;
//            final double st_s;
//            try
//            {
//              st_s = Double.parseDouble (valueString);
//            }
//            catch (NumberFormatException nfe)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            JSignalGeneratorDisplay.this.setSweepTime_s (conversionFactor * st_s);
//          }
//        }
//      });
//      stBox.add (this.stTextField);
//      final JLabel stUnitsLabel = new JLabel ("      s");
//      stBox.add (stUnitsLabel);
//      freqBox.add (stBox);
//      //
//      final Box tlBox = new Box (BoxLayout.X_AXIS);
//      this.tlLabel = new JLabel ("TL   ");
//      this.tlLabel.setToolTipText ("Trace Length");
//      this.tlLabel.setOpaque (true);
//      tlBox.add (this.tlLabel);
//      this.tlTextField = new JTextField (10);
//      this.tlTextField.setEditable (false);
//      this.tlTextField.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          final String inputString = JOptionPane.showInputDialog ("Trace Length [Samples]:");
//          if (inputString != null)
//          {
//            final int tl_s;
//            try
//            {
//              tl_s = Integer.parseInt (inputString);
//            }
//            catch (NumberFormatException nfe)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            JSignalGeneratorDisplay.this.setTraceLength (tl_s);
//          }
//        }
//      });
//      tlBox.add (this.tlTextField);
//      final JLabel tlUnitsLabel = new JLabel ("      S");
//      tlBox.add (tlUnitsLabel);
//      freqBox.add (tlBox);
//      //
//      box.add (freqBox);
//      //
//      final Box ampBox = new Box (BoxLayout.Y_AXIS);
//      ampBox.setBorder (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (BORDER_COLOR, 3, true), "Amplitude"));
//      //
//      final Box rlBox = new Box (BoxLayout.X_AXIS);
//      final JLabel rlLabel = new JLabel ("RL   ");
//      rlLabel.setToolTipText ("Reference Level");
//      rlBox.add (rlLabel);
//      this.rlTextField = new JTextField (10);
//      this.rlTextField.setEditable (false);
//      this.rlTextField.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          final String inputString = JOptionPane.showInputDialog ("Reference Level [dBm]:");
//          if (inputString != null)
//          {
//            final String[] splitString = inputString.split ("\\s+");
//            if (splitString == null || splitString.length == 0 || splitString.length > 2)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            final String valueString = splitString[0];
//            final String unitString = splitString.length > 1 ? splitString[1] : null;
//            final double conversionFactor;
//            if (unitString != null)
//            {
//              if (unitString.equalsIgnoreCase ("dBm"))
//                conversionFactor = 1.0;
//              else
//              {
//                JOptionPane.showMessageDialog
//                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//                return;
//              }
//            }
//            else
//              conversionFactor = 1.0;
//            final double rl_dBm;
//            try
//            {
//              rl_dBm = Double.parseDouble (valueString);
//            }
//            catch (NumberFormatException nfe)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            JSignalGeneratorDisplay.this.setReferenceLevel_dBm (conversionFactor * rl_dBm);
//          }
//        }
//      });
//      rlBox.add (this.rlTextField);
//      final JLabel rlUnitsLabel = new JLabel (" dBm");
//      rlBox.add (rlUnitsLabel);
//      ampBox.add (rlBox);
//      //
//      final Box atBox = new Box (BoxLayout.X_AXIS);
//      final JLabel atLabel = new JLabel ("AT   ");
//      atLabel.setToolTipText ("Attenuation");
//      atBox.add (atLabel);
//      this.atTextField = new JTextField (10);
//      this.atTextField.setEditable (false);
//      this.atTextField.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          final String inputString = JOptionPane.showInputDialog ("Attenuation [dB]:");
//          if (inputString != null)
//          {
//            final String[] splitString = inputString.split ("\\s+");
//            if (splitString == null || splitString.length == 0 || splitString.length > 2)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            final String valueString = splitString[0];
//            final String unitString = splitString.length > 1 ? splitString[1] : null;
//            final double conversionFactor;
//            if (unitString != null)
//            {
//              if (unitString.equalsIgnoreCase ("dB"))
//                conversionFactor = 1.0;
//              else
//              {
//                JOptionPane.showMessageDialog
//                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//                return;
//              }
//            }
//            else
//              conversionFactor = 1.0;
//            final double at_dB;
//            try
//            {
//              at_dB = Double.parseDouble (valueString);
//            }
//            catch (NumberFormatException nfe)
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//            JSignalGeneratorDisplay.this.setAttenuation_dB (conversionFactor * at_dB);
//          }
//        }
//      });
//      atBox.add (this.atTextField);
//      final JLabel atUnitsLabel = new JLabel ("    dB");
//      atBox.add (atUnitsLabel);
//      ampBox.add (atBox);
//      //
//      final Box detBox = new Box (BoxLayout.X_AXIS);
//      final JLabel detLabel = new JLabel ("DET ");
//      detLabel.setToolTipText ("Detector");
//      detBox.add (detLabel);
//      this.detTextField = new JTextField (10);
//      this.detTextField.setEditable (false);
//      this.detTextField.addMouseListener (new MouseAdapter ()
//      {
//        @Override
//        public void mouseClicked (final MouseEvent e)
//        {
//          final String inputString = JOptionPane.showInputDialog ("Detector:");
//          if (inputString != null)
//          {
//            if (! JSignalGeneratorDisplay.this.setDetectorFromString (inputString))
//            {
//              JOptionPane.showMessageDialog
//                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
//              return;
//            }
//          }
//        }
//      });
//      detBox.add (this.detTextField);
//      final JLabel detUnitsLabel = new JLabel ("        ");
//      detBox.add (detUnitsLabel);
//      ampBox.add (detBox);
//      //
//      box.add (ampBox);
//      //
//      final Box displayBox = new Box (BoxLayout.Y_AXIS);
//      displayBox.setBorder
//        (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (BORDER_COLOR, 3, true), "Display"));
//      //
//      final Box latestBox = new Box (BoxLayout.X_AXIS);
//      final JLabel latestLabel = new JLabel ("LTST ");
//      latestLabel.setOpaque (true);
//      latestLabel.setBackground (Color.black);
//      latestLabel.setForeground (Color.yellow);
//      latestLabel.setToolTipText ("Latest acquired trace!");
//      latestBox.add (latestLabel);
//      this.latestCheckBox = new JCheckBox ();
//      this.latestCheckBox.setSelected (JSignalGeneratorDisplay.this.display.isEnableTrace ());
//      this.latestCheckBox.addItemListener ((final ItemEvent e) ->
//      {
//        JSignalGeneratorDisplay.this.display.setEnableTrace (e.getStateChange () == ItemEvent.SELECTED);
//      });
//      latestBox.add (this.latestCheckBox);
//      latestBox.add (Box.createHorizontalGlue ());
//      displayBox.add (latestBox);
//      //
//      final Box avgBox = new Box (BoxLayout.X_AXIS);
//      final JLabel avgLabel = new JLabel ("AVG  ");
//      avgLabel.setOpaque (true);
//      avgLabel.setBackground (Color.black);
//      avgLabel.setForeground (Color.blue);
//      avgLabel.setToolTipText ("Averaged trace (in time) with current settings!");
//      avgBox.add (avgLabel);
//      this.avgCheckBox = new JCheckBox ();
//      this.avgCheckBox.setSelected (JSignalGeneratorDisplay.this.display.isEnableAvgTrace ());
//      this.avgCheckBox.addItemListener ((final ItemEvent e) ->
//      {
//        JSignalGeneratorDisplay.this.display.setEnableAvgTrace (e.getStateChange () == ItemEvent.SELECTED);
//      });
//      avgBox.add (this.avgCheckBox);
//      avgBox.add (Box.createHorizontalGlue ());
//      displayBox.add (avgBox);
//      //
//      final Box avgFBox = new Box (BoxLayout.X_AXIS);
//      final JLabel avgFLabel = new JLabel ("AVGF ");
//      avgFLabel.setOpaque (true);
//      avgFLabel.setBackground (Color.black);
//      avgFLabel.setForeground (Color.pink);
//      avgFLabel.setToolTipText ("Averaged trace (in t and f) with current settings!");
//      avgFBox.add (avgFLabel);
//      this.avgFCheckBox = new JCheckBox ();
//      this.avgFCheckBox.setSelected (JSignalGeneratorDisplay.this.display.isEnableAvgFTrace ());
//      this.avgFCheckBox.addItemListener ((final ItemEvent e) ->
//      {
//        JSignalGeneratorDisplay.this.display.setEnableAvgFTrace (e.getStateChange () == ItemEvent.SELECTED);
//      });
//      avgFBox.add (this.avgFCheckBox);
//      avgFBox.add (Box.createHorizontalGlue ());
//      displayBox.add (avgFBox);
//      //
//      box.add (displayBox);
//      //
//      add (Box.createVerticalGlue ());
//      //
//      add (box, BorderLayout.CENTER);
//      updateComponent (null, null);
//    }
//    
//    protected void updateComponent (final SpectrumAnalyzerTraceSettings settings, final SpectrumAnalyzerTrace trace)
//    {
//      SwingUtilities.invokeLater (() ->
//      {
//        if (settings == null || trace == null)
//        {
//          StatusBar.this.errCheckBox.setSelected (false);
//          StatusBar.this.calCheckBox.setSelected (false);
//          StatusBar.this.corCheckBox.setSelected (false);
//          StatusBar.this.cfTextField.setText ("UNKNOWN");
//          StatusBar.this.spTextField.setText ("UNKNOWN");
//          StatusBar.this.rbLabel.setBackground (Color.orange);
//          StatusBar.this.rbTextField.setText ("UNKNOWN");
//          StatusBar.this.vbLabel.setBackground (Color.orange);
//          StatusBar.this.vbTextField.setText ("UNKNOWN");
//          StatusBar.this.stLabel.setBackground (Color.orange);
//          StatusBar.this.stTextField.setText ("UNKNOWN");
//          StatusBar.this.tlTextField.setText ("UNKNOWN");
//          StatusBar.this.rlTextField.setText ("UNKNOWN");
//          StatusBar.this.atTextField.setText ("UNKNOWN");
//          StatusBar.this.detTextField.setText ("UNKNOWN");
//        }
//        else
//        {
//          StatusBar.this.errCheckBox.setSelected (! trace.isError ());
//          StatusBar.this.calCheckBox.setSelected (! trace.isUncalibrated ());
//          StatusBar.this.corCheckBox.setSelected (! trace.isUncorrected ());
//          StatusBar.this.cfTextField.setText (Double.toString (settings.getCenterFrequency_MHz ()));
//          StatusBar.this.spTextField.setText (Double.toString (settings.getSpan_MHz ()));
//          StatusBar.this.rbLabel.setBackground (settings.isAutoResolutionBandwidth () ? Color.green : Color.orange);
//          StatusBar.this.rbTextField.setText (Double.toString (settings.getResolutionBandwidth_Hz ()));
//          StatusBar.this.vbLabel.setBackground (settings.isAutoVideoBandwidth () ? Color.green : Color.orange);
//          StatusBar.this.vbTextField.setText (Double.toString (settings.getVideoBandwidth_Hz ()));
//          StatusBar.this.stLabel.setBackground (settings.isAutoSweepTime () ? Color.green : Color.orange);
//          StatusBar.this.stTextField.setText (Double.toString (settings.getSweepTime_s ()));
//          StatusBar.this.tlTextField.setText (Integer.toString (settings.getTraceLength ()));
//          StatusBar.this.rlTextField.setText (Double.toString (settings.getReferenceLevel_dBm ()));
//          StatusBar.this.atTextField.setText (Double.toString (settings.getAttenuation_dB ()));
//          StatusBar.this.detTextField.setText (settings.getDetector ().getFourCharString ());
//        }
//      });
//    }
//    
//    protected void updateComponent ()
//    {
//      SwingUtilities.invokeLater (() ->
//      {
//          StatusBar.this.acqThreadCheckBox.setSelected (JSignalGeneratorDisplay.this.isRunningSettingsThread ());
//      });
//    }
//  }
//  
//  private final StatusBar statusBar;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MESSAGE AREA
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
//  private final JTextPane messagePane;
// 
//  private static final Color MSG_COLOR = Color.black;
//  private static final Color MSG_HEADER_COLOR_TRACE = Color.red;
//  
//  private Style messageStyle = null;
//  
//  private Style traceStyle = null;
//  
//  private void initMessagePane ()
//  {
//  }
//  
//  private void addTraceMessage (final String message)
//  {
//    if (this.messageStyle == null)
//    {
//      this.messageStyle = this.messagePane.addStyle ("MESSAGE_STYLE", null);
//      StyleConstants.setForeground (this.messageStyle, MSG_COLOR);      
//      this.traceStyle = this.messagePane.addStyle ("TRACE STYLE", null);
//      StyleConstants.setForeground (this.traceStyle, MSG_HEADER_COLOR_TRACE);
//    }
//    if (message != null)
//    {
//      final StyledDocument doc = this.messagePane.getStyledDocument ();
//      try
//      {
//        doc.insertString (doc.getLength (), "TRACE: ", this.traceStyle);
//        doc.insertString (doc.getLength (), message + System.getProperty ("line.separator"), this.messageStyle);
//      }
//      catch (BadLocationException ble)
//      {
//      }
//    }
//  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
