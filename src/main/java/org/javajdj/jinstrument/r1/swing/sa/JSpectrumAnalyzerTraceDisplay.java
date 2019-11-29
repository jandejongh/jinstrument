package org.javajdj.jinstrument.r1.swing.sa;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerClient;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerTrace;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerTraceSettings;
import org.javajdj.jinstrument.r1.instrument.sa.math.SpectrumAnalyzerTraceMath;

/**
 *
 */
public class JSpectrumAnalyzerTraceDisplay
extends JPanel
implements SpectrumAnalyzerClient
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JSpectrumAnalyzerTraceDisplay.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JSpectrumAnalyzerTraceDisplay (final JSpectrumAnalyzerManagedTraceDisplay manager)
  {
    super ();
    this.manager = manager;
    setOpaque (true);
    setBackground (Color.black);
    setMinimumSize (new Dimension (1024, 600));
    setPreferredSize (new Dimension (1024, 600));
    addMouseListener (this.mouseAdapter);
    addMouseMotionListener (this.mouseAdapter);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY manager
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSpectrumAnalyzerManagedTraceDisplay manager;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY trace
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile SpectrumAnalyzerTrace trace = null;
  
  public final synchronized void setTrace (final SpectrumAnalyzerTrace trace)
  {
    this.trace = trace;
    SwingUtilities.invokeLater (() ->
    {
      repaint ();
    });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY avgTrace
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile SpectrumAnalyzerTrace avgTrace = null;
  
  public final synchronized void setAvgTrace (final SpectrumAnalyzerTrace avgTrace)
  {
    this.avgTrace = avgTrace;
    SwingUtilities.invokeLater (() ->
    {
      repaint ();
    });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY avgFTrace
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile SpectrumAnalyzerTrace avgFTrace = null;
  
  public final synchronized void setAvgFTrace (final SpectrumAnalyzerTrace avgFTrace)
  {
    this.avgFTrace = avgFTrace;
    SwingUtilities.invokeLater (() ->
    {
      repaint ();
    });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY referenceLevel_dBm
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
//  private double referenceLevel_dBm = 0;
//  
//  public final synchronized double getReferenceLevel_dBm ()
//  {
//    return this.referenceLevel_dBm;
//  }
//  
//  public final synchronized void setReferenceLevel_dBm (final double referenceLevel_dBm)
//  {
//    if (referenceLevel_dBm != this.referenceLevel_dBm)
//    {
//      this.referenceLevel_dBm = referenceLevel_dBm;
//      SwingUtilities.invokeLater (() ->
//      {
//        repaint ();
//      });
//    }
//  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GRAPHICS SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Color GRATICULE_COLOR = Color.green;
  private static final Stroke GRATICULE_STROKE =
      new BasicStroke (0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);
  private static final Color LEGEND_COLOR = Color.orange;
  private static final Color CLIP_COLOR = Color.red;
  private static final Color TRACE_COLOR = Color.yellow;
  private static final Color AVG_TRACE_COLOR = Color.blue;
  private static final Color AVG_F_TRACE_COLOR = Color.pink;
  private static final Color CROSSHAIR_COLOR = Color.cyan;
  private static final Color TRACE_POINT_HIGHLIGHT_COLOR = Color.pink;
  private static final Color RESOLUTION_BANDWIDTH_COLOR = Color.red;
  private static final Stroke RESOLUTION_BANDWIDTH_STROKE =
      new BasicStroke (0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);
  private static final Color NF_COLOR = Color.red;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PAINT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private void paintGraticule (final Graphics2D g2d, final SpectrumAnalyzerTrace trace, final int width, final int height)
  {
    if (! this.enableGraticule)
      return;
    if (g2d == null || trace == null)
      return;
    final double centerFrequency_MHz = trace.getSettings ().getCenterFrequency_MHz ();
    final double span_MHz = trace.getSettings ().getSpan_MHz ();
    final double referenceLevel_dBm = trace.getSettings ().getReferenceLevel_dBm ();
    g2d.setStroke (GRATICULE_STROKE);
    for (int i = 0; i <= 10; i++)
    {
      g2d.setColor (GRATICULE_COLOR);
      final int y = (int) ((i / 10.0) * height);
      g2d.drawLine (0, y, width, y);
      for (int j = 1; j <= 9; j++)
      {
        final int xCenter = width / 2;
        final int yMinor = y + (int) (height * j / 100.0);
        if (j == 5)
          g2d.drawLine (xCenter - 4, yMinor, xCenter + 5, yMinor);
        else
          g2d.drawLine (xCenter - 2, yMinor, xCenter + 3, yMinor);
      }
      if (i == 10)
        g2d.drawString ("" + (referenceLevel_dBm - 100), width / 2 + 10, height - 5);
      else
        g2d.drawString ("" + (referenceLevel_dBm - i * 10), width / 2 + 10, (int) (((double) i) / 10 * height) + 15);
    }
    for (int i = 0; i <= 10; i++)
    {
      g2d.setColor (GRATICULE_COLOR);
      final int x = (int) ((i / 10.0) * width);
      g2d.drawLine (x, 0, x, height);
      switch (i)
      {
        case 0:
          g2d.setColor (Color.red);
          g2d.drawString ("" + (centerFrequency_MHz - 0.5 * span_MHz), x + 10, 15);
          g2d.drawString ("" + (centerFrequency_MHz - 0.5 * span_MHz), x + 10, height - 10);
          break;
        case 5:
          g2d.setColor (Color.red);
          g2d.drawString ("" + centerFrequency_MHz, x - 90, 15);
          g2d.drawString ("" + centerFrequency_MHz, x - 90, height - 10);
          break;
        case 10:
          g2d.setColor (Color.red);
          g2d.drawString ("" + (centerFrequency_MHz + 0.5 * span_MHz), x - 90, 15);
          g2d.drawString ("" + (centerFrequency_MHz + 0.5 * span_MHz), x - 90, height - 10);
          break;
        default:
          break;
      }
    }
  }
  
  private void paintCrossHair
  (final Graphics2D g2d, final SpectrumAnalyzerTrace trace,
   final int width, final int height,
   final int mouseX, final int mouseY)
  {
    if (! (this.enableCrossHair && this.drawCrossHair))
      return;
    if (g2d == null || trace == null)
      return;
    g2d.setColor (GRATICULE_COLOR);
    g2d.setStroke (GRATICULE_STROKE);
    // Draw the "crosshair" cursor.
    g2d.drawLine (mouseX, 0, mouseX, height);
    g2d.drawLine (0, mouseY, width, mouseY);
    // Draw the trace highlighters.
    // On the fly, determine the number of lines to draw in the legend.
    int nrOfLines = 2; // f, S.
    final int s = xToTraceIndex (mouseX);
    g2d.fillRect (mouseX - 3, mouseY - 3, 7, 7);
    if (this.enableTrace)
    {
      final double St_dBm = trace.getSamples ()[s];
      final int y_trace = SToY (St_dBm);
      g2d.setColor (TRACE_COLOR);
      g2d.fillRect (mouseX - 3, y_trace - 3, 7, 7);
      nrOfLines++;
    }
    if (this.avgTrace != null && this.enableAvgTrace)
    {
      // XXX Locking!!
      final double SavgT_dBm = this.avgTrace.getSamples ()[s];
      final int y_trace = SToY (SavgT_dBm);
      g2d.setColor (AVG_TRACE_COLOR);
      g2d.fillRect (mouseX - 3, y_trace - 3, 7, 7);
      nrOfLines++;
    }
    if (this.avgFTrace != null && this.enableAvgFTrace)
    {
      // XXX Locking!!
      final double SavgFT_dBm = this.avgFTrace.getSamples ()[s];
      final int y_trace = SToY (SavgFT_dBm);
      g2d.setColor (AVG_F_TRACE_COLOR);
      g2d.fillRect (mouseX - 3, y_trace - 3, 7, 7);
      nrOfLines++;
    }
    // Draw the crosshair legend.
    final int lineSeparation = 14;
    final int startTextX = mouseX < width - 200 ? mouseX + 10 : mouseX - 200;
    final int startTextY = mouseY - 5 - (nrOfLines - 1) * lineSeparation;
    int textY = startTextY >= lineSeparation ? startTextY : (mouseY + lineSeparation);
    // Draw text for f, S of the crosshair.
    final double f_MHz = xToF_MHz (mouseX);
    final double Sc_dBm = yToS_dBm (mouseY);
    g2d.setColor (GRATICULE_COLOR);
    g2d.drawString ("f = " + f_MHz + " [MHz]", startTextX, textY);
    textY += lineSeparation;
    g2d.drawString ("S = " + Sc_dBm + " [dBm]", startTextX, textY);
    textY += lineSeparation;
    if (this.enableTrace)
    {
      final double St_dBm = trace.getSamples ()[s];
      g2d.setColor (TRACE_COLOR);
      g2d.drawString ("S = " + St_dBm + " [dBm]", startTextX, textY);
      textY += lineSeparation;
    }
    if (this.avgTrace != null && this.enableAvgTrace)
    {
      // XXX Locking!!
      final double SavgT_dBm = this.avgTrace.getSamples ()[s];
      g2d.setColor (AVG_TRACE_COLOR);
      g2d.drawString ("S = " + SavgT_dBm + " [dBm]", startTextX, textY);
      textY += lineSeparation;
    }
    if (this.avgFTrace != null && this.enableAvgFTrace)
    {
      // XXX Locking!!
      final double SavgFT_dBm = this.avgFTrace.getSamples ()[s];
      g2d.setColor (AVG_F_TRACE_COLOR);
      g2d.drawString ("S = " + SavgFT_dBm + " [dBm]", startTextX, textY);
      textY += lineSeparation;
    }
  }
  
  private void paintResolutionBandwith (final Graphics2D g2d, final SpectrumAnalyzerTrace trace, final int width, final int height)
  {
    if (! this.enableResolutionBandwidth)
      return;
    if (g2d == null || trace == null)
      return;
    final SpectrumAnalyzerTraceSettings settings = trace.getSettings ();
    final double span_MHz = settings.getSpan_MHz ();
    final double resolutionBandwidth_kHz = settings.getResolutionBandwidth_Hz () / 1000;
    final double halfRBOverSpan = 0.5 * 0.001 * resolutionBandwidth_kHz / span_MHz;
    if (halfRBOverSpan < 0.01)
      return;
    g2d.setColor (RESOLUTION_BANDWIDTH_COLOR);
    g2d.setStroke (RESOLUTION_BANDWIDTH_STROKE);
    final int xRBLow = (int) (width * (0.5 - halfRBOverSpan));
    final int xRBHigh = (int) (width * (0.5 + halfRBOverSpan));
    g2d.drawLine (xRBLow, 0, xRBLow, height);
    g2d.drawLine (xRBHigh, 0, xRBHigh, height);
  }
  
  private void paintLegend (final Graphics2D g2d, final SpectrumAnalyzerTrace trace, final int width, final int height)
  {
    g2d.setColor (LEGEND_COLOR);
    final SpectrumAnalyzerTraceSettings settings = trace.getSettings ();
    final double centerFrequency_MHz = settings.getCenterFrequency_MHz ();
    final double span_MHz = settings.getSpan_MHz ();
    final double referenceLevel_dBm = settings.getReferenceLevel_dBm ();
    final double resolutionBandwidth_kHz = settings.getResolutionBandwidth_Hz () / 1000;
    final boolean autoResolutionBandwidth = settings.isAutoResolutionBandwidth ();
    final double videoBandwidth_kHz = settings.getVideoBandwidth_Hz () / 1000;
    final boolean autoVideoBandwidth = settings.isAutoVideoBandwidth ();
    final double sweepTime_s = settings.getSweepTime_s ();
    final boolean autoSweepTime = settings.isAutoSweepTime ();
    final int traceLength = settings.getTraceLength ();
    if (centerFrequency_MHz >= 1.0E3)
      g2d.drawString ("CF = " + centerFrequency_MHz / 1.0E3 + " [GHz]", 40, 40);
    else if (centerFrequency_MHz >= 1.0)
      g2d.drawString ("CF = " + centerFrequency_MHz + " [MHz]", 40, 40);
    else
      g2d.drawString ("CF = " + centerFrequency_MHz * 1.0E3 + " [kHz]", 40, 40);
    if (span_MHz >= 1.0E3)
      g2d.drawString ("SP = " + span_MHz / 1.0E3 + " [GHz]", 40, 60);
    else if (span_MHz >= 1.0)
      g2d.drawString ("SP = " + span_MHz + " [MHz]", 40, 60);
    else if (span_MHz >= 1.0E-3)
      g2d.drawString ("SP = " + span_MHz * 1.0E3 + " [kHz]", 40, 60);
    else
      g2d.drawString ("SP = " + span_MHz * 1.0E6 + " [Hz]", 40, 60);
    g2d.drawString ("RL = " + referenceLevel_dBm + " [dBm]", 40, 80);
    g2d.drawString ("RB = " + resolutionBandwidth_kHz + " [kHz]" + (autoResolutionBandwidth ? "" : " [MAN]"), 40, 100);
    g2d.drawString ("VB = " + videoBandwidth_kHz + " [kHz]" + (autoVideoBandwidth ? "" : " [MAN]"), 40, 120);
    String stString;
    if (sweepTime_s >= 1.0)
      stString = "ST = " + sweepTime_s + " [s]";
    else if (sweepTime_s >= 0.001)
      stString = "ST = " + sweepTime_s * 1.0E3 + " [mis]";
    else
      stString = "ST = " + sweepTime_s * 1.0E6 + " [mus]";
    if (! autoSweepTime)
      stString += " [MAN]";
    g2d.drawString (stString, 40, 140);
    g2d.drawString ("TL = " + traceLength, 40, 160);
  }
  
  private void paintTrace
  (final Graphics2D g2d, final SpectrumAnalyzerTrace trace, final Color color, final int width, final int height)
  {
    if (g2d == null || trace == null || color == null)
      return;
    if (trace.getSettings ().getTraceLength () > 1)
    {
      final double referenceLevel_dBm = trace.getSettings ().getReferenceLevel_dBm ();
      g2d.setColor (color);
      double x_s_prev = 0;
      double y_s_prev = 0;
      for (int s = 0; s < this.trace.getSettings ().getTraceLength (); s++)
      {
        final double f_MHz_s = trace.getSettings ().sampleIndexToFrequency_MHz (s);
        final double x_s = f_MHzToX (f_MHz_s); // XXX Use width??
        double y_s = (((referenceLevel_dBm - trace.getSamples ()[s]) / 100.0) * height);
        if (y_s < 0)
        {
          g2d.setColor (CLIP_COLOR);
          y_s = 0;
        }
        else if (y_s > height)
        {
          g2d.setColor (CLIP_COLOR);
          y_s = height;
        }
        else
          g2d.setColor (color);        
        if (s > 0)
          g2d.draw (new Line2D.Double (x_s_prev, y_s_prev, x_s, y_s));
        x_s_prev = x_s;
        y_s_prev = y_s;
      }
    }
  }
  
  public final static double BOLTZMAN_K = 1.3806485279E-23; // J/K, source https://en.wikipedia.org/wiki/Boltzmann_constant.

  public final static double T = 290; // Assumed room/abient temperature, in K.
  
  private void paintThermalNoiseFloor (final Graphics2D g2d, final SpectrumAnalyzerTrace trace, final int width, final int height)
  {
    if (g2d == null || trace == null)
      return;
    final double referenceLevel_dBm = trace.getSettings ().getReferenceLevel_dBm ();
    final double kTRB_W = JSpectrumAnalyzerTraceDisplay.BOLTZMAN_K
      * JSpectrumAnalyzerTraceDisplay.T
      * trace.getSettings ().getResolutionBandwidth_Hz (); // W
    final double kTRB_mW = 1000.0 * kTRB_W;
    final double kTRB_dBm = 10.0 * Math.log10 (kTRB_mW);
    final int y_kTRB = (int) (((referenceLevel_dBm - kTRB_dBm) / 100.0) * height);
    if (y_kTRB >= 0 && y_kTRB <= height)
    {
      g2d.setColor (NF_COLOR);
      g2d.setStroke (RESOLUTION_BANDWIDTH_STROKE);
      g2d.drawLine (0, y_kTRB, width, y_kTRB);
    }
  }
  
  private void paintHighestPeak (final Graphics2D g2d, final SpectrumAnalyzerTrace trace,
   final int width, final int height)
  {
    if (g2d == null || trace == null || trace.getSettings () == null)
      return;
    final double fPeakIndex = SpectrumAnalyzerTraceMath.getHighestPeakIndex (trace);
    final double fPeak_MHz = trace.getSettings ().sampleIndexToFrequency_MHz (fPeakIndex);
    g2d.setColor (LEGEND_COLOR);
    if (fPeak_MHz >= 1.0E3)
      g2d.drawString ("HP_F = " + fPeak_MHz / 1.0E3 + " [GHz]", width - 200, 40);
    else if (fPeak_MHz >= 1.0)
      g2d.drawString ("HP_F = " + fPeak_MHz + " [MHz]", width - 200, 40);
    else
      g2d.drawString ("HP_F = " + fPeak_MHz * 1.0E3 + " [kHz]", width - 200, 40);
  }
  
  private void paintComponent
  (final Graphics2D g2d, final SpectrumAnalyzerTrace trace,
   final int width, final int height,
   final int mouseX, final int mouseY)
  {
    final Color oldColor = g2d.getColor ();
    final Stroke oldStroke = g2d.getStroke ();
    paintGraticule (g2d, trace, width, height);
    paintResolutionBandwith (g2d, trace, width, height);
    paintLegend (g2d, trace, width, height);
    if (this.enableTrace)
      paintTrace (g2d, trace, TRACE_COLOR, width, height);
    if (this.enableAvgTrace && this.avgTrace != null)
      paintTrace (g2d, this.avgTrace, AVG_TRACE_COLOR, width, height);
    if (this.enableAvgFTrace && this.avgFTrace != null)
      paintTrace (g2d, this.avgFTrace, AVG_F_TRACE_COLOR, width, height);
    paintCrossHair (g2d, trace, width, height, mouseX, mouseY);
    paintThermalNoiseFloor (g2d, trace, width, height);
    paintHighestPeak (g2d, trace, width, height);
    g2d.setStroke (oldStroke);
    g2d.setColor (oldColor);     
  }
  
  @Override
  public void paintComponent (final Graphics g)
  {
    super.paintComponent (g);
    final Graphics2D g2d = (Graphics2D) g;
    final SpectrumAnalyzerTrace trace = this.trace;
    if (g2d == null || this.trace == null)
      return;
    final int width = getWidth ();
    final int height = getHeight ();
    final int mouseX = this.mouseX;
    final int mouseY = this.mouseY;
    paintComponent (g2d, trace, width, height, mouseX, mouseY);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY enableTrace
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean enableTrace = true;
  
  public final boolean isEnableTrace ()
  {
    return this.enableTrace;
  }
  
  public final void setEnableTrace (final boolean enableTrace)
  {
    final boolean changed;
    synchronized (this)
    {
      changed = enableTrace != this.enableTrace;
      this.enableTrace = enableTrace;
    }
    if (changed)
      SwingUtilities.invokeLater (() ->
      {
        repaint ();
      });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY enableAvgTrace
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean enableAvgTrace = false;
  
  public final boolean isEnableAvgTrace ()
  {
    return this.enableAvgTrace;
  }
  
  public final void setEnableAvgTrace (final boolean enableAvgTrace)
  {
    final boolean changed;
    synchronized (this)
    {
      changed = enableAvgTrace != this.enableAvgTrace;
      this.enableAvgTrace = enableAvgTrace;
    }
    if (changed)
      SwingUtilities.invokeLater (() ->
      {
        repaint ();
      });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY enableAvgFTrace
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean enableAvgFTrace = false;
  
  public final boolean isEnableAvgFTrace ()
  {
    return this.enableAvgFTrace;
  }
  
  public final void setEnableAvgFTrace (final boolean enableAvgFTrace)
  {
    final boolean changed;
    synchronized (this)
    {
      changed = enableAvgFTrace != this.enableAvgFTrace;
      this.enableAvgFTrace = enableAvgFTrace;
    }
    if (changed)
      SwingUtilities.invokeLater (() ->
      {
        repaint ();
      });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY enableGraticule
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean enableGraticule = true;
  
  public final void toggleEnableGraticule ()
  {
    synchronized (this)
    {
      this.enableGraticule = ! this.enableGraticule;
    }
    SwingUtilities.invokeLater (() ->
    {
      repaint ();
    });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY enableCrossHair
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean enableCrossHair = true;
  
  public final void toggleEnableCrossHair ()
  {
    synchronized (this)
    {
      this.enableCrossHair = ! this.enableCrossHair;
    }
    SwingUtilities.invokeLater (() ->
    {
      repaint ();
    });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY enableResolutionBandwidth
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean enableResolutionBandwidth = true;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MOUSE HANDLING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private boolean drawCrossHair = false;
  
  private int mouseX = -1;
  
  private int mouseY = -1;

  private int mousePressX = -1;
  
  private int mousePressY = -1;
  
  private int xToTraceIndex (final int x)
  {
    final int index = (int) ((double) this.trace.getSettings ().getTraceLength () * x / getWidth ());
    return Math.max (0, Math.min (this.trace.getSettings ().getTraceLength () - 1, index));
  }
  
  private double xToF_MHz (final double x)
  {
    final SpectrumAnalyzerTraceSettings settings = this.trace.getSettings ();
    final double centerFrequency_MHz = settings.getCenterFrequency_MHz ();
    final double span_MHz = settings.getSpan_MHz ();
    final double fZero_MHz = centerFrequency_MHz - 0.5 * span_MHz;  
    return fZero_MHz + span_MHz * (x / getWidth ());
  }
  
  private double f_MHzToX (final double f_MHz)
  {
    final SpectrumAnalyzerTraceSettings settings = this.trace.getSettings ();
    final double centerFrequency_MHz = settings.getCenterFrequency_MHz ();
    final double span_MHz = settings.getSpan_MHz ();
    final double fZero_MHz = centerFrequency_MHz - 0.5 * span_MHz;  
    return getWidth () * (f_MHz - fZero_MHz) / span_MHz;
  }

  private double yToS_dBm (final int y)
  {
    return this.trace.getSettings ().getReferenceLevel_dBm () - ((100.0 * y) / getHeight ());
  }
  
  private int SToY (final double S_dBm)
  {
    final SpectrumAnalyzerTraceSettings settings = this.trace.getSettings ();
    final double referenceLevel_dBm = settings.getReferenceLevel_dBm ();
    if (S_dBm > referenceLevel_dBm)
      return 0;
    else if (S_dBm < referenceLevel_dBm - 100)
      return getHeight ();
    else
      return (int) (getHeight () * (referenceLevel_dBm - S_dBm) / 100);
  }
  
  private final MouseAdapter mouseAdapter = new MouseAdapter ()
  {
    
    @Override
    public void mouseExited (final MouseEvent e)
    {
      JSpectrumAnalyzerTraceDisplay.this.drawCrossHair = false;
      JSpectrumAnalyzerTraceDisplay.this.repaint ();
    }

    @Override
    public void mouseEntered (final MouseEvent e)
    {
      JSpectrumAnalyzerTraceDisplay.this.mouseX = e.getX ();
      JSpectrumAnalyzerTraceDisplay.this.mouseY = e.getY ();
      JSpectrumAnalyzerTraceDisplay.this.drawCrossHair = true;
      JSpectrumAnalyzerTraceDisplay.this.repaint ();
    }

    @Override
    public void mouseMoved (final MouseEvent e)
    {
      JSpectrumAnalyzerTraceDisplay.this.mouseX = e.getX ();
      JSpectrumAnalyzerTraceDisplay.this.mouseY = e.getY ();
      // JSpectrumAnalyzerTraceDisplay.this.drawCrossHair = true;
      JSpectrumAnalyzerTraceDisplay.this.repaint ();
    }

    @Override
    public void mouseDragged (final MouseEvent e)
    {
      mouseMoved (e);
    }

    @Override
    public void mouseClicked (final MouseEvent e)
    {
      if (JSpectrumAnalyzerTraceDisplay.this.manager != null)
      {
        final int x = e.getX ();
        final int y = e.getY ();
        final double f_MHz = xToF_MHz (x);
        final double S_dBm = yToS_dBm (y);
        switch (e.getButton ())
        {
          case MouseEvent.BUTTON1:
            JSpectrumAnalyzerTraceDisplay.this.manager.mouseButton1Clicked (f_MHz, S_dBm);
            break;
          case MouseEvent.BUTTON2:
            JSpectrumAnalyzerTraceDisplay.this.manager.mouseButton2Clicked (f_MHz, S_dBm);
            break;
          case MouseEvent.BUTTON3:
            JSpectrumAnalyzerTraceDisplay.this.manager.mouseButton3Clicked (f_MHz, S_dBm);
            break;
          default:
            break;
        }
      }
    }

    @Override
    public void mousePressed (final MouseEvent e)
    {
      JSpectrumAnalyzerTraceDisplay.this.mousePressX = e.getX ();
      JSpectrumAnalyzerTraceDisplay.this.mousePressY = e.getY ();
    }
    
    @Override
    public void mouseReleased (final MouseEvent e)
    {
      if (JSpectrumAnalyzerTraceDisplay.this.manager != null)
      {
        final int x1 = JSpectrumAnalyzerTraceDisplay.this.mousePressX;
        final int x2 = e.getX ();
        final int y1 = JSpectrumAnalyzerTraceDisplay.this.mousePressY;
        final int y2 = e.getY ();
        final double f1_MHz = xToF_MHz (x1);
        final double f2_MHz = xToF_MHz (x2);
        final double S1_dBm = yToS_dBm (y1);
        final double S2_dBm = yToS_dBm (y2);
        switch (e.getButton ())
        {
          case MouseEvent.BUTTON1:
            JSpectrumAnalyzerTraceDisplay.this.manager.mouseButton1Dragged (f1_MHz, f2_MHz, S1_dBm, S2_dBm);
            break;
          case MouseEvent.BUTTON2:
            JSpectrumAnalyzerTraceDisplay.this.manager.mouseButton2Dragged (f1_MHz, f2_MHz, S1_dBm, S2_dBm);
            break;
          case MouseEvent.BUTTON3:
            JSpectrumAnalyzerTraceDisplay.this.manager.mouseButton3Dragged (f1_MHz, f2_MHz, S1_dBm, S2_dBm);
            break;
          default:
            break;
        }
      }
    }
    
  };
  
}
