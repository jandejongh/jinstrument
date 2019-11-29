package org.javajdj.jinstrument.r1.swing.sa;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.javajdj.jinstrument.r1.instrument.sa.DefaultSpectrumAnalyzerTrace;
import org.javajdj.jinstrument.r1.instrument.sa.DefaultSpectrumAnalyzerTraceSettings;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerClient;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerDetector;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerDetector_Simple;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerTrace;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerTraceSettings;
import org.javajdj.jinstrument.r1.swing.util.JStatusCheckBox;
import org.javajdj.jinstrument.r1.instrument.sa.SignalGenerator;

/**
 *
 */
public class JSpectrumAnalyzerManagedTraceDisplay
extends JPanel
implements SpectrumAnalyzerClient
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JSpectrumAnalyzerManagedTraceDisplay.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JSpectrumAnalyzerManagedTraceDisplay (final SignalGenerator spectrumAnalyzer)
  {
    super ();
    if (spectrumAnalyzer == null)
      throw new IllegalArgumentException ();
    this.spectrumAnalyzer = spectrumAnalyzer;
    setOpaque (true);
    setBackground (Color.white);
    setMinimumSize (new Dimension (1024, 600));
    setPreferredSize (new Dimension (1024, 600));
    setLayout (new BorderLayout ());
    this.display = new JSpectrumAnalyzerTraceDisplay (this);
    add (this.display, BorderLayout.CENTER);
    this.statusBar = new StatusBar ();
    add (this.statusBar, BorderLayout.WEST);
    this.messagePane = new JTextPane ();
    this.messagePane.setMinimumSize (new Dimension (1024, 100));
    this.messagePane.setMaximumSize (new Dimension (1024, 100));
    this.messagePane.setPreferredSize (new Dimension (1024, 100));
    add (/* new JScrollPane ( */ this.messagePane /* ) */, BorderLayout.SOUTH);
    startTraceThread (true);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SPECTRUM ANALYZER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final SignalGenerator spectrumAnalyzer;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSpectrumAnalyzerTraceDisplay display;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY traceHistory
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static final int MAX_TRACE_HISTORY = 1024; // XXX 1024;
  
  private final SpectrumAnalyzerTrace[] traceHistory
    = new SpectrumAnalyzerTrace[JSpectrumAnalyzerManagedTraceDisplay.MAX_TRACE_HISTORY];
  
  private int lastTraceIndexInHistory = -1;
  
  private boolean traceHistoryFilled = false;
  
  private SpectrumAnalyzerTrace avgTrace = null;
  
  private SpectrumAnalyzerTrace avgFTrace = null;
  
  private void clearTraceHistory ()
  {
    this.lastTraceIndexInHistory = -1;
    this.traceHistoryFilled = false;
    this.avgTrace = null;
    this.avgFTrace = null;
  }
  
  private void addTraceToHistory (final SpectrumAnalyzerTrace trace)
  {
    if (trace == null)
      throw new IllegalArgumentException ();
    this.lastTraceIndexInHistory++;
    if (this.lastTraceIndexInHistory == JSpectrumAnalyzerManagedTraceDisplay.MAX_TRACE_HISTORY)
    {
      this.lastTraceIndexInHistory = 0;
      this.traceHistoryFilled = true;
    }
    this.traceHistory[this.lastTraceIndexInHistory] = trace;
    if (this.avgTrace == null)
    {
      this.avgTrace = new DefaultSpectrumAnalyzerTrace
        (trace.getSettings (), new double[trace.getSettings ().getTraceLength ()], false, null, false, false);
      this.avgFTrace = new DefaultSpectrumAnalyzerTrace
        (trace.getSettings (), new double[trace.getSettings ().getTraceLength ()], false, null, false, false);
    }
    final int endIndex =
      this.traceHistoryFilled ? (JSpectrumAnalyzerManagedTraceDisplay.MAX_TRACE_HISTORY - 1) : this.lastTraceIndexInHistory;
    final double[] avgSamples = this.avgTrace.getSamples ();
    for (int s = 0; s < avgSamples.length; s++)
    {
      double cum_s = 0;
      for (int i = 0; i <= endIndex; i++)
        cum_s += this.traceHistory[i].getSamples ()[s];
      avgSamples[s] = cum_s / (endIndex + 1);
    }
    final double[] avgFSamples = this.avgFTrace.getSamples ();
    for (int s = 0; s < avgFSamples.length; s++)
    {
      final int neighbors = Math.min (avgFSamples.length / 40, Math.min (s, avgFSamples.length - 1 - s));
      double cum_s = 0;
      for (int i = s - neighbors; i <= s + neighbors; i++)
        cum_s += avgSamples[i];
      avgFSamples[s] = cum_s / (2 * neighbors + 1);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE THREAD [CLASS]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private class TraceThread
  extends Thread
  {

    private final boolean continuous;
    
    public TraceThread (final boolean continuous)
    {
      this.continuous = continuous;
    }
  
    private volatile SpectrumAnalyzerTraceSettings traceSettings = null;
    
    private volatile boolean newTraceSettings = false;
    
    protected synchronized void setCenterFrequency_MHz (final double f_MHz)
    {
      final SpectrumAnalyzerTraceSettings newSettings;
      if (this.traceSettings != null)
      {
        newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (f_MHz,
           this.traceSettings.getSpan_MHz (),
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
      }
      else
      {
        final double span_MHz = f_MHz / 10;
        newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (f_MHz,
           span_MHz,
           span_MHz / 100,
           true,
           span_MHz / 100,
           true,
           1.0,
           true,
           800,
           0.0,
           10.0,
           SpectrumAnalyzerDetector_Simple.ROSENFELL);
      }
      this.traceSettings = newSettings;
      this.newTraceSettings = true;
    }
    
    protected synchronized void setSpan_MHz (final double span_MHz)
    {
      final SpectrumAnalyzerTraceSettings newSettings;
      if (this.traceSettings != null)
      {
        newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           span_MHz,
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
      }
      else
      {
        final double f_MHz = span_MHz / 2;
        newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (f_MHz,
           span_MHz,
           span_MHz / 100,
           true,
           span_MHz / 100,
           true,
           1.0,
           true,
           800,
           0.0,
           10.0,
           SpectrumAnalyzerDetector_Simple.ROSENFELL);        
      }
      this.traceSettings = newSettings;
      this.newTraceSettings = true;
    }
    
    protected synchronized void setCenterFrequencyAndSpan_MHz (final double f_MHz, final double span_MHz)
    {
      final SpectrumAnalyzerTraceSettings newSettings;
      if (this.traceSettings != null)
      {
        newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (f_MHz,
           span_MHz,
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
      }
      else
      {
        newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (f_MHz,
           span_MHz,
           span_MHz / 100,
           true,
           span_MHz / 100,
           true,
           1.0,
           true,
           800,
           0.0,
           10.0,
           SpectrumAnalyzerDetector_Simple.ROSENFELL);        
      }
      this.traceSettings = newSettings;
      this.newTraceSettings = true;
    }
    
    protected synchronized void doubleSpan ()
    {
      if (this.traceSettings != null)
      {
        final SpectrumAnalyzerTraceSettings newSettings;
        newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz () * 2,
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
      }
    }
    
    protected synchronized void setResolutionBandwidth_Hz (final double rb_Hz)
    {
      if (this.traceSettings != null)
      {
        final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz (),
           rb_Hz,
           false,
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
      }
    }
    
    protected synchronized void setAutoResolutionBandwidth ()
    {
      if (this.traceSettings != null)
      {
        if (this.traceSettings.isAutoResolutionBandwidth ())
          return;
        final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz (),
           this.traceSettings.getResolutionBandwidth_Hz (),
           true,
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
      }
    }
    
    protected synchronized void setVideoBandwidth_Hz (final double vb_Hz)
    {
      if (this.traceSettings != null)
      {
        final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz (),
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           vb_Hz,
           false,
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
      }
    }
    
    protected synchronized void setAutoVideoBandwidth ()
    {
      if (this.traceSettings != null)
      {
        if (this.traceSettings.isAutoVideoBandwidth ())
          return;
        final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz (),
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           true,
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
      }
    }
    
    protected synchronized void setSweepTime_s (final double sweepTime_s)
    {
      if (this.traceSettings != null)
      {
        final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz (),
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           sweepTime_s,
           false,
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
      }      
    }
    
    protected synchronized void setAutoSweepTime ()
    {
      if (this.traceSettings != null)
      {
        if (this.traceSettings.isAutoSweepTime ())
          return;
        final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz (),
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           true,
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
      }
    }
    
    protected synchronized void setTraceLength (final int traceLength)
    {
      if (this.traceSettings != null)
      {
        final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz (),
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           traceLength,
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
      }      
    }
    
    protected synchronized void setReferenceLevel_dBm (final double referenceLevel_dBm)
    {
      if (this.traceSettings != null)
      {
        final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz (),
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           referenceLevel_dBm,
           this.traceSettings.getAttenuation_dB (),
           this.traceSettings.getDetector ());
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
      }      
    }
    
    protected synchronized void setAttenuation_dB (final double attenuation_dB)
    {
      if (this.traceSettings != null)
      {
        final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz (),
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           attenuation_dB,
           this.traceSettings.getDetector ());
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
      }      
    }
    
    protected synchronized boolean setDetectorFromString (final String detectorString)
    {
      if (this.traceSettings == null)
        return false;
      final SpectrumAnalyzerDetector detector = this.traceSettings.getDetector ().fromString (detectorString);
      if (detector != null)
      {
        final SpectrumAnalyzerTraceSettings newSettings = new DefaultSpectrumAnalyzerTraceSettings
          (this.traceSettings.getCenterFrequency_MHz (),
           this.traceSettings.getSpan_MHz (),
           this.traceSettings.getResolutionBandwidth_Hz (),
           this.traceSettings.isAutoResolutionBandwidth (),
           this.traceSettings.getVideoBandwidth_Hz (),
           this.traceSettings.isAutoVideoBandwidth (),
           this.traceSettings.getSweepTime_s (),
           this.traceSettings.isAutoSweepTime (),
           this.traceSettings.getTraceLength (),
           this.traceSettings.getReferenceLevel_dBm (),
           this.traceSettings.getAttenuation_dB (),
           detector);
        this.traceSettings = newSettings;
        this.newTraceSettings = true;
        return true;
      }
      else
        return false;
    }
    
    @Override
    public final void run ()
    {
      while (! isInterrupted ())
      {
        try
        {
          // XXX
          Thread.sleep (/* 1000L */ 10L);
          final SpectrumAnalyzerTraceSettings traceSettingsCopy;
          final boolean newTraceSettingsCopy;
          synchronized (this)
          {
            traceSettingsCopy = this.traceSettings;
            newTraceSettingsCopy = this.newTraceSettings;
            this.newTraceSettings = false;
          }
          // Obtain a trace from the spectrum analyzer with our current settings.
          final SpectrumAnalyzerTrace trace =
            JSpectrumAnalyzerManagedTraceDisplay.this.spectrumAnalyzer.getTrace (traceSettingsCopy);
          synchronized (this)
          {
            if (this.traceSettings == null || (newTraceSettingsCopy && ! this.newTraceSettings))
              this.traceSettings = trace.getSettings ();
          }
          // Report the trace to our superclass.
          JSpectrumAnalyzerManagedTraceDisplay.this.newTraceFromSpectrumAnalyzer (trace);
        }
        catch (InterruptedException ie)
        {
          LOG.log (Level.WARNING, "InterruptedException: {0}.", ie.getMessage ());
          JSpectrumAnalyzerManagedTraceDisplay.this.setTraceThread (null);
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
          JSpectrumAnalyzerManagedTraceDisplay.this.setTraceThread (null);
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
  
  private TraceThread traceThread = null;
  
  public final synchronized void startTraceThread (final boolean continuous)
  {
    this.traceThread = new TraceThread (continuous);
    this.traceThread.start ();
    if (this.statusBar != null)
      this.statusBar.updateComponent ();
  }
  
  private synchronized void setTraceThread (final TraceThread traceThread)
  {
    this.traceThread = traceThread;
    if (this.statusBar != null)
      this.statusBar.updateComponent ();
  }
  
  public final synchronized TraceThread getTraceThread ()
  {
    return this.traceThread;
  }
  
  public final synchronized boolean isRunningTraceThread ()
  {
    return this.traceThread != null && this.traceThread.isAlive ();
  }
  
  public final synchronized void disposeTraceThread ()
  {
    if (this.traceThread != null)
      this.traceThread.interrupt ();
    this.traceThread = null;
    if (this.statusBar != null)
      this.statusBar.updateComponent ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // METHOD newTraceFromSpectrumAnalyzer
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private synchronized void newTraceFromSpectrumAnalyzer (final SpectrumAnalyzerTrace trace)
  {
    if (trace == null)
      throw new IllegalArgumentException ();
    this.display.setTrace (trace);
    this.statusBar.updateComponent (trace.getSettings (), trace);
    if (this.lastTraceIndexInHistory >= 0
    && (! this.traceHistory[this.lastTraceIndexInHistory].getSettings ().equals (trace.getSettings ())))
      clearTraceHistory ();
    addTraceToHistory (trace);
    this.display.setAvgTrace (this.avgTrace);
    this.display.setAvgFTrace (this.avgFTrace);
    if (trace.isError ())
      addTraceMessage (trace.getErrorMessage ());
    // addTraceMessage ("New Trace!");
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY centerFrequency_MHz
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private synchronized void setCenterFrequency_MHz (final double f_MHz)
  {
    if (this.traceThread != null)
      this.traceThread.setCenterFrequency_MHz (f_MHz);
  }
 
  private synchronized void setSpan_MHz (final double span_MHz)
  {
    if (this.traceThread != null)
      this.traceThread.setSpan_MHz (span_MHz);
  }
 
  private synchronized void setResolutionBandwidth_Hz (final double rb_Hz)
  {
    if (this.traceThread != null)
      this.traceThread.setResolutionBandwidth_Hz (rb_Hz);
  }
 
  private synchronized void setVideoBandwidth_Hz (final double vb_Hz)
  {
    if (this.traceThread != null)
      this.traceThread.setVideoBandwidth_Hz (vb_Hz);
  }
 
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY centerFrequencyAndSpan_MHz
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private synchronized void setCenterFrequencyAndSpan_MHz (final double f_MHz, final double span_MHz)
  {
    if (this.traceThread != null)
      this.traceThread.setCenterFrequencyAndSpan_MHz (f_MHz, span_MHz);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // METHOD doubleSpan
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private synchronized void doubleSpan ()
  {
    if (this.traceThread != null)
      this.traceThread.doubleSpan ();
  }
  
  private synchronized void setAutoResolutionBandwidth ()
  {
    if (this.traceThread != null)
      this.traceThread.setAutoResolutionBandwidth ();    
  }
  
  private synchronized void setAutoVideoBandwidth ()
  {
    if (this.traceThread != null)
      this.traceThread.setAutoVideoBandwidth ();    
  }
  
  private synchronized void setSweepTime_s (final double sweepTime_s)
  {
    if (this.traceThread != null)
      this.traceThread.setSweepTime_s (sweepTime_s);
  }
  
  private synchronized void setAutoSweepTime ()
  {
    if (this.traceThread != null)
      this.traceThread.setAutoSweepTime ();    
  }
  
  private synchronized void setTraceLength (final int traceLength)
  {
    if (this.traceThread != null)
      this.traceThread.setTraceLength (traceLength);
  }
  
  private synchronized void setReferenceLevel_dBm (final double referenceLevel_dBm)
  {
    if (this.traceThread != null)
      this.traceThread.setReferenceLevel_dBm (referenceLevel_dBm);   
  }
  
  private synchronized void setAttenuation_dB (final double attenuation_dB)
  {
    if (this.traceThread != null)
      this.traceThread.setAttenuation_dB (attenuation_dB);   
  }
  
  private synchronized boolean setDetectorFromString (final String detectorString)
  {
    if (this.traceThread != null)
      return this.traceThread.setDetectorFromString (detectorString);
    else
      return false;
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
//    this.traceThread.setTraceSettings (newSettings);    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MOUSE HANDLING [FROM DISPLAY]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected final void mouseButton1Clicked (final double f_MHz, final double S_dBm)
  {
    setCenterFrequency_MHz (f_MHz);
  }
  
  protected final void mouseButton1DoubleClicked (final double f_MHz, final double S_dBm)
  {
  }
  
  protected final void mouseButton1Dragged (final double f1_MHz, final double f2_MHz, final double S1_dBm, final double S2_dBm)
  {
    if (f2_MHz > f1_MHz)
      setCenterFrequencyAndSpan_MHz ((f1_MHz + f2_MHz) / 2, f2_MHz - f1_MHz);
    else
      doubleSpan ();
  }
  
  protected final void mouseButton2Clicked (final double f_MHz, final double S_dBm)
  {
//        final double f_MHz = xToF_MHz (e.getX ());
//        final double span_MHz = JSpectrumAnalyzerTraceDisplay.this.trace.getSettings ().getSpan_MHz () / 2;
//        synchronized (JSpectrumAnalyzerTraceDisplay.this)
//        {
//          if (JSpectrumAnalyzerTraceDisplay.this.traceThread != null)
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
//          if (JSpectrumAnalyzerTraceDisplay.this.traceThread != null)
//            JSpectrumAnalyzerTraceDisplay.this.setCenterFrequencyAndSpan_MHz (newFc, newSpan);
//        }
  }
  
  protected final void mouseButton3Clicked (final double f_MHz, final double S_dBm)
  {
//        synchronized (JSpectrumAnalyzerTraceDisplay.this)
//        {
//          if (JSpectrumAnalyzerTraceDisplay.this.traceThread != null)
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
//          if (JSpectrumAnalyzerTraceDisplay.this.traceThread != null)
//            JSpectrumAnalyzerTraceDisplay.this.setCenterFrequencyAndSpan_MHz (newFc, newSpan);
//        }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // STATUS BAR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private class StatusBar extends JPanel
  {

    private final JStatusCheckBox statCheckBox;
    private final JStatusCheckBox acqThreadCheckBox;
    
    private final JStatusCheckBox errCheckBox;
    private final JStatusCheckBox calCheckBox;
    private final JStatusCheckBox corCheckBox;
    
    private final JTextField cfTextField;
    private final JTextField spTextField;
    private final JLabel rbLabel;
    private final JTextField rbTextField;
    private final JLabel vbLabel;
    private final JTextField vbTextField;
    private final JLabel stLabel;
    private final JTextField stTextField;
    private final JLabel tlLabel;
    private final JTextField tlTextField;
    
    private final JTextField rlTextField;
    private final JTextField atTextField;
    private final JTextField detTextField;
    
    private final JCheckBox latestCheckBox;
    private final JCheckBox avgCheckBox;
    private final JCheckBox avgFCheckBox;
    
    private final Color BORDER_COLOR = Color.blue;
    
    public StatusBar ()
    {
      final Box box = new Box (BoxLayout.Y_AXIS);
      //
      final Box instrumentStatusBox = new Box (BoxLayout.Y_AXIS);
      instrumentStatusBox.setBorder
        (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (BORDER_COLOR, 3, true), "Instrument"));
      //
      final Box statBox = new Box (BoxLayout.X_AXIS);
      final JLabel statLabel = new JLabel ("STS  ");
      statLabel.setToolTipText ("Instrument status; click for diagnostics when red!");
      statBox.add (statLabel);
      this.statCheckBox = new JStatusCheckBox ();
      statBox.add (this.statCheckBox);
      statBox.add (Box.createHorizontalGlue ());
      instrumentStatusBox.add (statBox);
      //
      final Box acqThreadBox = new Box (BoxLayout.X_AXIS);
      final JLabel acqThreadLabel = new JLabel ("ACQT ");
      acqThreadLabel.setToolTipText ("Acquisition thread; left for start/stop (continuous); middle for one-shot sweep!");
      acqThreadBox.add (acqThreadLabel);
      this.acqThreadCheckBox = new JStatusCheckBox ();
      this.acqThreadCheckBox.setSelected (JSpectrumAnalyzerManagedTraceDisplay.this.isRunningTraceThread ());
      this.acqThreadCheckBox.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          switch (e.getButton ())
          {
            case MouseEvent.BUTTON1:
              synchronized (JSpectrumAnalyzerManagedTraceDisplay.this)
              {
                if (isRunningTraceThread ())
                  JSpectrumAnalyzerManagedTraceDisplay.this.disposeTraceThread ();
                else
                  JSpectrumAnalyzerManagedTraceDisplay.this.startTraceThread (true);
              }
              break;
            case MouseEvent.BUTTON2:
              break;
            case MouseEvent.BUTTON3:
              break;
            default:
              break;
          }
        }
      });
      acqThreadBox.add (this.acqThreadCheckBox);
      acqThreadBox.add (Box.createHorizontalGlue ());
      instrumentStatusBox.add (acqThreadBox);
      //
      box.add (instrumentStatusBox);
      //
      final Box traceStatusBox = new Box (BoxLayout.Y_AXIS);
      traceStatusBox.setBorder
        (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (BORDER_COLOR, 3, true), "Trace Status"));
      //
      final Box errBox = new Box (BoxLayout.X_AXIS);
      final JLabel errLabel = new JLabel ("ERR  ");
      errLabel.setToolTipText ("Error in trace or instrument when red!");
      errBox.add (errLabel);
      this.errCheckBox = new JStatusCheckBox ();
      errBox.add (this.errCheckBox);
      errBox.add (Box.createHorizontalGlue ());
      traceStatusBox.add (errBox);
      //
      final Box calBox = new Box (BoxLayout.X_AXIS);
      final JLabel calLabel = new JLabel ("CAL  ");
      calLabel.setToolTipText ("Uncalibrated when red!");
      calBox.add (calLabel);
      this.calCheckBox = new JStatusCheckBox ();
      calBox.add (this.calCheckBox);
      calBox.add (Box.createHorizontalGlue ());
      traceStatusBox.add (calBox);
      //
      final Box corBox = new Box (BoxLayout.X_AXIS);
      final JLabel corLabel = new JLabel ("COR  ");
      corLabel.setToolTipText ("Uncorrected when red!");
      corBox.add (corLabel);
      this.corCheckBox = new JStatusCheckBox ();
      corBox.add (this.corCheckBox);
      corBox.add (Box.createHorizontalGlue ());
      traceStatusBox.add (corBox);
      //
      box.add (traceStatusBox);
      //
      final Box freqBox = new Box (BoxLayout.Y_AXIS);
      freqBox.setBorder (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (BORDER_COLOR, 3, true), "Frequency"));
      //
      final Box cfBox = new Box (BoxLayout.X_AXIS);
      final JLabel cfLabel = new JLabel ("CF   ");
      cfLabel.setToolTipText ("Center Frequency");
      cfBox.add (cfLabel);
      this.cfTextField = new JTextField (10);
      this.cfTextField.setEditable (false);
      this.cfTextField.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          final String inputString = JOptionPane.showInputDialog ("Center Frequency [MHz]:");
          if (inputString != null)
          {
            final String[] splitString = inputString.split ("\\s+");
            if (splitString == null || splitString.length == 0 || splitString.length > 2)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            final String valueString = splitString[0];
            final String unitString = splitString.length > 1 ? splitString[1] : null;
            final double conversionFactor;
            if (unitString != null)
            {
              if (unitString.equalsIgnoreCase ("Hz"))
                conversionFactor = 1.0e-6;
              else if (unitString.equalsIgnoreCase ("kHz"))
                conversionFactor = 1.0e-3;
              else if (unitString.equalsIgnoreCase ("MHz"))
                conversionFactor = 1.0;
              else if (unitString.equalsIgnoreCase ("GHz"))
                conversionFactor = 1.0e3;
              else
              {
                JOptionPane.showMessageDialog
                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
            else
              conversionFactor = 1.0;
            final double fMHz;
            try
            {
              fMHz = Double.parseDouble (valueString);
            }
            catch (NumberFormatException nfe)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            JSpectrumAnalyzerManagedTraceDisplay.this.setCenterFrequency_MHz (conversionFactor * fMHz);
          }
        }
      });
      cfBox.add (this.cfTextField);
      final JLabel cfUnitsLabel = new JLabel (" MHz");
      cfBox.add (cfUnitsLabel);
      freqBox.add (cfBox);
      //
      final Box spBox = new Box (BoxLayout.X_AXIS);
      final JLabel spLabel = new JLabel ("SP   ");
      spLabel.setToolTipText ("Span");
      spBox.add (spLabel);
      this.spTextField = new JTextField (10);
      this.spTextField.setEditable (false);
      this.spTextField.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          final String inputString = JOptionPane.showInputDialog ("Span [MHz]:");
          if (inputString != null)
          {
            final String[] splitString = inputString.split ("\\s+");
            if (splitString == null || splitString.length == 0 || splitString.length > 2)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            final String valueString = splitString[0];
            final String unitString = splitString.length > 1 ? splitString[1] : null;
            final double conversionFactor;
            if (unitString != null)
            {
              if (unitString.equalsIgnoreCase ("Hz"))
                conversionFactor = 1.0e-6;
              else if (unitString.equalsIgnoreCase ("kHz"))
                conversionFactor = 1.0e-3;
              else if (unitString.equalsIgnoreCase ("MHz"))
                conversionFactor = 1.0;
              else if (unitString.equalsIgnoreCase ("GHz"))
                conversionFactor = 1.0e3;
              else
              {
                JOptionPane.showMessageDialog
                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
            else
              conversionFactor = 1.0;
            final double span_MHz;
            try
            {
              span_MHz = Double.parseDouble (valueString);
            }
            catch (NumberFormatException nfe)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            JSpectrumAnalyzerManagedTraceDisplay.this.setSpan_MHz (conversionFactor * span_MHz);
          }
        }
      });
      spBox.add (this.spTextField);
      final JLabel spUnitsLabel = new JLabel (" MHz");
      spBox.add (spUnitsLabel);
      freqBox.add (spBox);
      //
      final Box rbBox = new Box (BoxLayout.X_AXIS);
      this.rbLabel = new JLabel ("RB   ");
      this.rbLabel.setToolTipText ("Resolution Bandwidth - click for AUTO");
      this.rbLabel.setOpaque (true);
      this.rbLabel.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          JSpectrumAnalyzerManagedTraceDisplay.this.setAutoResolutionBandwidth ();
        }
      });
      rbBox.add (this.rbLabel);
      this.rbTextField = new JTextField (10);
      this.rbTextField.setEditable (false);
      this.rbTextField.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          final String inputString = JOptionPane.showInputDialog ("Resolution Bandwidth [Hz]:");
          if (inputString != null)
          {
            final String[] splitString = inputString.split ("\\s+");
            if (splitString == null || splitString.length == 0 || splitString.length > 2)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            final String valueString = splitString[0];
            final String unitString = splitString.length > 1 ? splitString[1] : null;
            final double conversionFactor;
            if (unitString != null)
            {
              if (unitString.equalsIgnoreCase ("Hz"))
                conversionFactor = 1.0;
              else if (unitString.equalsIgnoreCase ("kHz"))
                conversionFactor = 1.0e3;
              else if (unitString.equalsIgnoreCase ("MHz"))
                conversionFactor = 1.0e6;
              else if (unitString.equalsIgnoreCase ("GHz"))
                conversionFactor = 1.0e9;
              else
              {
                JOptionPane.showMessageDialog
                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
            else
              conversionFactor = 1.0;
            final double rb_Hz;
            try
            {
              rb_Hz = Double.parseDouble (valueString);
            }
            catch (NumberFormatException nfe)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            JSpectrumAnalyzerManagedTraceDisplay.this.setResolutionBandwidth_Hz (conversionFactor * rb_Hz);
          }
        }
      });
      rbBox.add (this.rbTextField);
      final JLabel rbUnitsLabel = new JLabel ("    Hz");
      rbBox.add (rbUnitsLabel);
      freqBox.add (rbBox);
      //
      final Box vbBox = new Box (BoxLayout.X_AXIS);
      this.vbLabel = new JLabel ("VB   ");
      this.vbLabel.setToolTipText ("Video Bandwidth - click for AUTO");
      this.vbLabel.setOpaque (true);
      this.vbLabel.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          JSpectrumAnalyzerManagedTraceDisplay.this.setAutoVideoBandwidth ();
        }
      });
      vbBox.add (this.vbLabel);
      this.vbTextField = new JTextField (10);
      this.vbTextField.setEditable (false);
      this.vbTextField.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          final String inputString = JOptionPane.showInputDialog ("Video Bandwidth [Hz]:");
          if (inputString != null)
          {
            final String[] splitString = inputString.split ("\\s+");
            if (splitString == null || splitString.length == 0 || splitString.length > 2)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            final String valueString = splitString[0];
            final String unitString = splitString.length > 1 ? splitString[1] : null;
            final double conversionFactor;
            if (unitString != null)
            {
              if (unitString.equalsIgnoreCase ("Hz"))
                conversionFactor = 1.0;
              else if (unitString.equalsIgnoreCase ("kHz"))
                conversionFactor = 1.0e3;
              else if (unitString.equalsIgnoreCase ("MHz"))
                conversionFactor = 1.0e6;
              else if (unitString.equalsIgnoreCase ("GHz"))
                conversionFactor = 1.0e9;
              else
              {
                JOptionPane.showMessageDialog
                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
            else
              conversionFactor = 1.0;
            final double vb_Hz;
            try
            {
              vb_Hz = Double.parseDouble (valueString);
            }
            catch (NumberFormatException nfe)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            JSpectrumAnalyzerManagedTraceDisplay.this.setVideoBandwidth_Hz (conversionFactor * vb_Hz);
          }
        }
      });
      vbBox.add (this.vbTextField);
      final JLabel vbUnitsLabel = new JLabel ("    Hz");
      vbBox.add (vbUnitsLabel);
      freqBox.add (vbBox);
      //
      final Box stBox = new Box (BoxLayout.X_AXIS);
      this.stLabel = new JLabel ("ST   ");
      this.stLabel.setToolTipText ("Sweep Time - click for AUTO");
      this.stLabel.setOpaque (true);
      this.stLabel.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          JSpectrumAnalyzerManagedTraceDisplay.this.setAutoSweepTime ();
        }
      });
      stBox.add (this.stLabel);
      this.stTextField = new JTextField (10);
      this.stTextField.setEditable (false);
      this.stTextField.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          final String inputString = JOptionPane.showInputDialog ("Sweep Time [s]:");
          if (inputString != null)
          {
            final String[] splitString = inputString.split ("\\s+");
            if (splitString == null || splitString.length == 0 || splitString.length > 2)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            final String valueString = splitString[0];
            final String unitString = splitString.length > 1 ? splitString[1] : null;
            final double conversionFactor;
            if (unitString != null)
            {
              if (unitString.equalsIgnoreCase ("ms") || unitString.equals ("mis"))
                conversionFactor = 1.0e-3;
              else if (unitString.equalsIgnoreCase ("s"))
                conversionFactor = 1.0;
              else if (unitString.equalsIgnoreCase ("min"))
                conversionFactor = 60;
              else
              {
                JOptionPane.showMessageDialog
                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
            else
              conversionFactor = 1.0;
            final double st_s;
            try
            {
              st_s = Double.parseDouble (valueString);
            }
            catch (NumberFormatException nfe)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            JSpectrumAnalyzerManagedTraceDisplay.this.setSweepTime_s (conversionFactor * st_s);
          }
        }
      });
      stBox.add (this.stTextField);
      final JLabel stUnitsLabel = new JLabel ("      s");
      stBox.add (stUnitsLabel);
      freqBox.add (stBox);
      //
      final Box tlBox = new Box (BoxLayout.X_AXIS);
      this.tlLabel = new JLabel ("TL   ");
      this.tlLabel.setToolTipText ("Trace Length");
      this.tlLabel.setOpaque (true);
      tlBox.add (this.tlLabel);
      this.tlTextField = new JTextField (10);
      this.tlTextField.setEditable (false);
      this.tlTextField.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          final String inputString = JOptionPane.showInputDialog ("Trace Length [Samples]:");
          if (inputString != null)
          {
            final int tl_s;
            try
            {
              tl_s = Integer.parseInt (inputString);
            }
            catch (NumberFormatException nfe)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            JSpectrumAnalyzerManagedTraceDisplay.this.setTraceLength (tl_s);
          }
        }
      });
      tlBox.add (this.tlTextField);
      final JLabel tlUnitsLabel = new JLabel ("      S");
      tlBox.add (tlUnitsLabel);
      freqBox.add (tlBox);
      //
      box.add (freqBox);
      //
      final Box ampBox = new Box (BoxLayout.Y_AXIS);
      ampBox.setBorder (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (BORDER_COLOR, 3, true), "Amplitude"));
      //
      final Box rlBox = new Box (BoxLayout.X_AXIS);
      final JLabel rlLabel = new JLabel ("RL   ");
      rlLabel.setToolTipText ("Reference Level");
      rlBox.add (rlLabel);
      this.rlTextField = new JTextField (10);
      this.rlTextField.setEditable (false);
      this.rlTextField.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          final String inputString = JOptionPane.showInputDialog ("Reference Level [dBm]:");
          if (inputString != null)
          {
            final String[] splitString = inputString.split ("\\s+");
            if (splitString == null || splitString.length == 0 || splitString.length > 2)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            final String valueString = splitString[0];
            final String unitString = splitString.length > 1 ? splitString[1] : null;
            final double conversionFactor;
            if (unitString != null)
            {
              if (unitString.equalsIgnoreCase ("dBm"))
                conversionFactor = 1.0;
              else
              {
                JOptionPane.showMessageDialog
                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
            else
              conversionFactor = 1.0;
            final double rl_dBm;
            try
            {
              rl_dBm = Double.parseDouble (valueString);
            }
            catch (NumberFormatException nfe)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            JSpectrumAnalyzerManagedTraceDisplay.this.setReferenceLevel_dBm (conversionFactor * rl_dBm);
          }
        }
      });
      rlBox.add (this.rlTextField);
      final JLabel rlUnitsLabel = new JLabel (" dBm");
      rlBox.add (rlUnitsLabel);
      ampBox.add (rlBox);
      //
      final Box atBox = new Box (BoxLayout.X_AXIS);
      final JLabel atLabel = new JLabel ("AT   ");
      atLabel.setToolTipText ("Attenuation");
      atBox.add (atLabel);
      this.atTextField = new JTextField (10);
      this.atTextField.setEditable (false);
      this.atTextField.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          final String inputString = JOptionPane.showInputDialog ("Attenuation [dB]:");
          if (inputString != null)
          {
            final String[] splitString = inputString.split ("\\s+");
            if (splitString == null || splitString.length == 0 || splitString.length > 2)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            final String valueString = splitString[0];
            final String unitString = splitString.length > 1 ? splitString[1] : null;
            final double conversionFactor;
            if (unitString != null)
            {
              if (unitString.equalsIgnoreCase ("dB"))
                conversionFactor = 1.0;
              else
              {
                JOptionPane.showMessageDialog
                  (null, "Illegal unit " + unitString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
            else
              conversionFactor = 1.0;
            final double at_dB;
            try
            {
              at_dB = Double.parseDouble (valueString);
            }
            catch (NumberFormatException nfe)
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + valueString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
            JSpectrumAnalyzerManagedTraceDisplay.this.setAttenuation_dB (conversionFactor * at_dB);
          }
        }
      });
      atBox.add (this.atTextField);
      final JLabel atUnitsLabel = new JLabel ("    dB");
      atBox.add (atUnitsLabel);
      ampBox.add (atBox);
      //
      final Box detBox = new Box (BoxLayout.X_AXIS);
      final JLabel detLabel = new JLabel ("DET ");
      detLabel.setToolTipText ("Detector");
      detBox.add (detLabel);
      this.detTextField = new JTextField (10);
      this.detTextField.setEditable (false);
      this.detTextField.addMouseListener (new MouseAdapter ()
      {
        @Override
        public void mouseClicked (final MouseEvent e)
        {
          final String inputString = JOptionPane.showInputDialog ("Detector:");
          if (inputString != null)
          {
            if (! JSpectrumAnalyzerManagedTraceDisplay.this.setDetectorFromString (inputString))
            {
              JOptionPane.showMessageDialog
                (null, "Illegal value " + inputString + "!", "Illegal Input", JOptionPane.ERROR_MESSAGE);
              return;
            }
          }
        }
      });
      detBox.add (this.detTextField);
      final JLabel detUnitsLabel = new JLabel ("        ");
      detBox.add (detUnitsLabel);
      ampBox.add (detBox);
      //
      box.add (ampBox);
      //
      final Box displayBox = new Box (BoxLayout.Y_AXIS);
      displayBox.setBorder
        (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (BORDER_COLOR, 3, true), "Display"));
      //
      final Box latestBox = new Box (BoxLayout.X_AXIS);
      final JLabel latestLabel = new JLabel ("LTST ");
      latestLabel.setOpaque (true);
      latestLabel.setBackground (Color.black);
      latestLabel.setForeground (Color.yellow);
      latestLabel.setToolTipText ("Latest acquired trace!");
      latestBox.add (latestLabel);
      this.latestCheckBox = new JCheckBox ();
      this.latestCheckBox.setSelected (JSpectrumAnalyzerManagedTraceDisplay.this.display.isEnableTrace ());
      this.latestCheckBox.addItemListener ((final ItemEvent e) ->
      {
        JSpectrumAnalyzerManagedTraceDisplay.this.display.setEnableTrace (e.getStateChange () == ItemEvent.SELECTED);
      });
      latestBox.add (this.latestCheckBox);
      latestBox.add (Box.createHorizontalGlue ());
      displayBox.add (latestBox);
      //
      final Box avgBox = new Box (BoxLayout.X_AXIS);
      final JLabel avgLabel = new JLabel ("AVG  ");
      avgLabel.setOpaque (true);
      avgLabel.setBackground (Color.black);
      avgLabel.setForeground (Color.blue);
      avgLabel.setToolTipText ("Averaged trace (in time) with current settings!");
      avgBox.add (avgLabel);
      this.avgCheckBox = new JCheckBox ();
      this.avgCheckBox.setSelected (JSpectrumAnalyzerManagedTraceDisplay.this.display.isEnableAvgTrace ());
      this.avgCheckBox.addItemListener ((final ItemEvent e) ->
      {
        JSpectrumAnalyzerManagedTraceDisplay.this.display.setEnableAvgTrace (e.getStateChange () == ItemEvent.SELECTED);
      });
      avgBox.add (this.avgCheckBox);
      avgBox.add (Box.createHorizontalGlue ());
      displayBox.add (avgBox);
      //
      final Box avgFBox = new Box (BoxLayout.X_AXIS);
      final JLabel avgFLabel = new JLabel ("AVGF ");
      avgFLabel.setOpaque (true);
      avgFLabel.setBackground (Color.black);
      avgFLabel.setForeground (Color.pink);
      avgFLabel.setToolTipText ("Averaged trace (in t and f) with current settings!");
      avgFBox.add (avgFLabel);
      this.avgFCheckBox = new JCheckBox ();
      this.avgFCheckBox.setSelected (JSpectrumAnalyzerManagedTraceDisplay.this.display.isEnableAvgFTrace ());
      this.avgFCheckBox.addItemListener ((final ItemEvent e) ->
      {
        JSpectrumAnalyzerManagedTraceDisplay.this.display.setEnableAvgFTrace (e.getStateChange () == ItemEvent.SELECTED);
      });
      avgFBox.add (this.avgFCheckBox);
      avgFBox.add (Box.createHorizontalGlue ());
      displayBox.add (avgFBox);
      //
      box.add (displayBox);
      //
      add (Box.createVerticalGlue ());
      //
      add (box, BorderLayout.CENTER);
      updateComponent (null, null);
    }
    
    protected void updateComponent (final SpectrumAnalyzerTraceSettings settings, final SpectrumAnalyzerTrace trace)
    {
      SwingUtilities.invokeLater (() ->
      {
        if (settings == null || trace == null)
        {
          StatusBar.this.errCheckBox.setSelected (false);
          StatusBar.this.calCheckBox.setSelected (false);
          StatusBar.this.corCheckBox.setSelected (false);
          StatusBar.this.cfTextField.setText ("UNKNOWN");
          StatusBar.this.spTextField.setText ("UNKNOWN");
          StatusBar.this.rbLabel.setBackground (Color.orange);
          StatusBar.this.rbTextField.setText ("UNKNOWN");
          StatusBar.this.vbLabel.setBackground (Color.orange);
          StatusBar.this.vbTextField.setText ("UNKNOWN");
          StatusBar.this.stLabel.setBackground (Color.orange);
          StatusBar.this.stTextField.setText ("UNKNOWN");
          StatusBar.this.tlTextField.setText ("UNKNOWN");
          StatusBar.this.rlTextField.setText ("UNKNOWN");
          StatusBar.this.atTextField.setText ("UNKNOWN");
          StatusBar.this.detTextField.setText ("UNKNOWN");
        }
        else
        {
          StatusBar.this.errCheckBox.setSelected (! trace.isError ());
          StatusBar.this.calCheckBox.setSelected (! trace.isUncalibrated ());
          StatusBar.this.corCheckBox.setSelected (! trace.isUncorrected ());
          StatusBar.this.cfTextField.setText (Double.toString (settings.getCenterFrequency_MHz ()));
          StatusBar.this.spTextField.setText (Double.toString (settings.getSpan_MHz ()));
          StatusBar.this.rbLabel.setBackground (settings.isAutoResolutionBandwidth () ? Color.green : Color.orange);
          StatusBar.this.rbTextField.setText (Double.toString (settings.getResolutionBandwidth_Hz ()));
          StatusBar.this.vbLabel.setBackground (settings.isAutoVideoBandwidth () ? Color.green : Color.orange);
          StatusBar.this.vbTextField.setText (Double.toString (settings.getVideoBandwidth_Hz ()));
          StatusBar.this.stLabel.setBackground (settings.isAutoSweepTime () ? Color.green : Color.orange);
          StatusBar.this.stTextField.setText (Double.toString (settings.getSweepTime_s ()));
          StatusBar.this.tlTextField.setText (Integer.toString (settings.getTraceLength ()));
          StatusBar.this.rlTextField.setText (Double.toString (settings.getReferenceLevel_dBm ()));
          StatusBar.this.atTextField.setText (Double.toString (settings.getAttenuation_dB ()));
          StatusBar.this.detTextField.setText (settings.getDetector ().getFourCharString ());
        }
      });
    }
    
    protected void updateComponent ()
    {
      SwingUtilities.invokeLater (() ->
      {
          StatusBar.this.acqThreadCheckBox.setSelected (JSpectrumAnalyzerManagedTraceDisplay.this.isRunningTraceThread ());
      });
    }
  }
  
  private final StatusBar statusBar;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MESSAGE AREA
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTextPane messagePane;
 
  private static final Color MSG_COLOR = Color.black;
  private static final Color MSG_HEADER_COLOR_TRACE = Color.red;
  
  private Style messageStyle = null;
  
  private Style traceStyle = null;
  
  private void initMessagePane ()
  {
  }
  
  private void addTraceMessage (final String message)
  {
    if (this.messageStyle == null)
    {
      this.messageStyle = this.messagePane.addStyle ("MESSAGE_STYLE", null);
      StyleConstants.setForeground (this.messageStyle, MSG_COLOR);      
      this.traceStyle = this.messagePane.addStyle ("TRACE STYLE", null);
      StyleConstants.setForeground (this.traceStyle, MSG_HEADER_COLOR_TRACE);
    }
    if (message != null)
    {
      final StyledDocument doc = this.messagePane.getStyledDocument ();
      try
      {
        doc.insertString (doc.getLength (), "TRACE: ", this.traceStyle);
        doc.insertString (doc.getLength (), message + System.getProperty ("line.separator"), this.messageStyle);
      }
      catch (BadLocationException ble)
      {
      }
    }
  }
  
}
