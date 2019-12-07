package org.javajdj.jinstrument.r1.main;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.r1.DefaultManager;
import org.javajdj.jinstrument.r1.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.r1.controller.gpib.tcpip.prologix.ProLogixGpibEthernetController;
import org.javajdj.jinstrument.r1.controller.gpib.tcpip.prologix.ProLogixGpibEthernetControllerTypeHandler;
import org.javajdj.jinstrument.r1.instrument.dso.hp54502a.HP54502A_GPIB;
import org.javajdj.jinstrument.r1.swing.sa.JSpectrumAnalyzerManagedTraceDisplay;
import org.javajdj.jinstrument.r1.instrument.sa.hp70000.HP70000_GPIB;
import org.javajdj.jinstrument.r1.instrument.sa.hp8566b.HP8566B_GPIB;
import org.javajdj.jinstrument.r1.instrument.sg.hp8663a.HP8663A_GPIB;
import org.javajdj.jinstrument.r1.swing.dso.JOscilloscopeManagedTraceDisplay;
import org.javajdj.jinstrument.r1.swing.sg.JSignalGeneratorDisplay;

public class JInstrument
extends JFrame
{

  public JInstrument (final String title)
  throws HeadlessException
  {
    super (title == null ? "JInstrument V0.1-SNAPSHOT" : title);
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    final JTabbedPane tabbedPane = new JTabbedPane ();
    getContentPane ().add (tabbedPane, BorderLayout.CENTER);
    this.instrumentManager.registerInstrumentControllerTypeHandler (ProLogixGpibEthernetControllerTypeHandler.getInstance ());
    final ProLogixGpibEthernetController controller =
      (ProLogixGpibEthernetController) this.instrumentManager.openInstrumentController ("prologix://ham-prologix.jdj:1234");
    controller.open ();
    //
    // final GpibDevice hp70000Device = controller.openDevice ("gpib:3"); // hp70900a
//    final GpibDevice hp70000Device = controller.openDevice ("gpib:18"); // hp70900b
//    final HP70000_GPIB  hp70000Instrument = new HP70000_GPIB (hp70000Device);
//    tabbedPane.addTab ("HP70000[1]", createHp70000Tab (controller, hp70000Instrument));
    //
    final GpibDevice hp8566bDevice = controller.openDevice ("gpib:22"); // hp8566bb
    final HP8566B_GPIB  hp8566bInstrument = new HP8566B_GPIB (hp8566bDevice);
    tabbedPane.addTab ("HP8566B[1]", createHp8566bTab (controller, hp8566bInstrument));
    //
    final GpibDevice hp8663aDevice = controller.openDevice ("gpib:19"); // hp8663a
    final HP8663A_GPIB  hp8663aInstrument = new HP8663A_GPIB (hp8663aDevice);
    tabbedPane.addTab ("HP8663A[1]", createHp8663aTab (controller, hp8663aInstrument));
    //
    // final GpibDevice hp54502aDevice = controller.openDevice ("gpib:1"); // hp54502a
    // final HP54502A_GPIB hp54502aInstrument = new HP54502A_GPIB (hp54502aDevice);
    // tabbedPane.addTab ("HP54502A[1]", createHp54502aTab (controller, hp54502aInstrument));
    pack ();
  }

  public JInstrument ()
  {
    this (null);
  }
  
  private final DefaultManager instrumentManager = DefaultManager.getInstance ();
  
  private JComponent createHp70000Tab (final ProLogixGpibEthernetController controller, final HP70000_GPIB instrument)
  {
    final JPanel jHp70000 = new JPanel ();
    jHp70000.setLayout (new BorderLayout ());
    final JSpectrumAnalyzerManagedTraceDisplay display = new JSpectrumAnalyzerManagedTraceDisplay (instrument);
    jHp70000.add (display, BorderLayout.CENTER);
//    final JStatusBar statusBar = new JStatusBar ();
//    jHp54502a.add (statusBar, BorderLayout.NORTH);
//    final TraceThread traceThread = new TraceThread (display, instrument);
//    display.setTraceThread (traceThread);
//    traceThread.start ();
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return display.isRunningTraceThread ();
//      }
//      @Override public final void clickAction ()
//      {
//        if (display.getTraceThread () == null)
//        {
//          display.setTraceThread (new TraceThread (display, instrument));
//          display.getTraceThread ().start ();
//        }
//        else
//          display.disposeTraceThread ();
//      }
//    }, "TTHD");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return true;
//      }
//      @Override public final boolean isActionOnly ()
//      {
//        return true;
//      }
//      @Override public final void clickAction ()
//      {
//        display.toggleEnableGraticule ();
//      }
//    }, "GRTC");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return true;
//      }
//      @Override public final boolean isActionOnly ()
//      {
//        return true;
//      }
//      @Override public final void clickAction ()
//      {
//        display.toggleEnableCrossHair ();
//      }
//    }, "CRHR");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return display.isAllAuto ();
//      }
//      @Override public final void clickAction ()
//      {
//        display.setAllAuto ();
//      }
//    }, "AUTO");
    return jHp70000;
  }
  
  private JComponent createHp8566bTab (final ProLogixGpibEthernetController controller, final HP8566B_GPIB instrument)
  {
    final JPanel jHp8566b = new JPanel ();
    jHp8566b.setLayout (new BorderLayout ());
    final JSpectrumAnalyzerManagedTraceDisplay display = new JSpectrumAnalyzerManagedTraceDisplay (instrument);
    jHp8566b.add (display, BorderLayout.CENTER);
//    final JStatusBar statusBar = new JStatusBar ();
//    jHp54502a.add (statusBar, BorderLayout.NORTH);
//    final TraceThread traceThread = new TraceThread (display, instrument);
//    display.setTraceThread (traceThread);
//    traceThread.start ();
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return display.isRunningTraceThread ();
//      }
//      @Override public final void clickAction ()
//      {
//        if (display.getTraceThread () == null)
//        {
//          display.setTraceThread (new TraceThread (display, instrument));
//          display.getTraceThread ().start ();
//        }
//        else
//          display.disposeTraceThread ();
//      }
//    }, "TTHD");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return true;
//      }
//      @Override public final boolean isActionOnly ()
//      {
//        return true;
//      }
//      @Override public final void clickAction ()
//      {
//        display.toggleEnableGraticule ();
//      }
//    }, "GRTC");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return true;
//      }
//      @Override public final boolean isActionOnly ()
//      {
//        return true;
//      }
//      @Override public final void clickAction ()
//      {
//        display.toggleEnableCrossHair ();
//      }
//    }, "CRHR");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return display.isAllAuto ();
//      }
//      @Override public final void clickAction ()
//      {
//        display.setAllAuto ();
//      }
//    }, "AUTO");
    return jHp8566b;
  }
  
  private JComponent createHp54502aTab (final ProLogixGpibEthernetController controller, final HP54502A_GPIB instrument)
  {
    final JPanel jHp54502a = new JPanel ();
    jHp54502a.setLayout (new BorderLayout ());
    final JOscilloscopeManagedTraceDisplay display = new JOscilloscopeManagedTraceDisplay (instrument);
    jHp54502a.add (display, BorderLayout.CENTER);
//    final JStatusBar statusBar = new JStatusBar ();
//    jHp54502a.add (statusBar, BorderLayout.NORTH);
//    final TraceThread traceThread = new TraceThread (display, instrument);
//    display.setTraceThread (traceThread);
//    traceThread.start ();
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return display.isRunningTraceThread ();
//      }
//      @Override public final void clickAction ()
//      {
//        if (display.getTraceThread () == null)
//        {
//          display.setTraceThread (new TraceThread (display, instrument));
//          display.getTraceThread ().start ();
//        }
//        else
//          display.disposeTraceThread ();
//      }
//    }, "TTHD");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return true;
//      }
//      @Override public final boolean isActionOnly ()
//      {
//        return true;
//      }
//      @Override public final void clickAction ()
//      {
//        display.toggleEnableGraticule ();
//      }
//    }, "GRTC");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return true;
//      }
//      @Override public final boolean isActionOnly ()
//      {
//        return true;
//      }
//      @Override public final void clickAction ()
//      {
//        display.toggleEnableCrossHair ();
//      }
//    }, "CRHR");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return display.isAllAuto ();
//      }
//      @Override public final void clickAction ()
//      {
//        display.setAllAuto ();
//      }
//    }, "AUTO");
    return jHp54502a;
  }
  
  private JComponent createHp8663aTab (final ProLogixGpibEthernetController controller, final HP8663A_GPIB instrument)
  {
    final JPanel jHp8663a = new JPanel ();
    jHp8663a.setLayout (new BorderLayout ());
    final JSignalGeneratorDisplay display = new JSignalGeneratorDisplay (instrument);
    jHp8663a.add (display, BorderLayout.CENTER);
//    final JStatusBar statusBar = new JStatusBar ();
//    jHp54502a.add (statusBar, BorderLayout.NORTH);
//    final TraceThread traceThread = new TraceThread (display, instrument);
//    display.setTraceThread (traceThread);
//    traceThread.start ();
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return display.isRunningTraceThread ();
//      }
//      @Override public final void clickAction ()
//      {
//        if (display.getTraceThread () == null)
//        {
//          display.setTraceThread (new TraceThread (display, instrument));
//          display.getTraceThread ().start ();
//        }
//        else
//          display.disposeTraceThread ();
//      }
//    }, "TTHD");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return true;
//      }
//      @Override public final boolean isActionOnly ()
//      {
//        return true;
//      }
//      @Override public final void clickAction ()
//      {
//        display.toggleEnableGraticule ();
//      }
//    }, "GRTC");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return true;
//      }
//      @Override public final boolean isActionOnly ()
//      {
//        return true;
//      }
//      @Override public final void clickAction ()
//      {
//        display.toggleEnableCrossHair ();
//      }
//    }, "CRHR");
//    statusBar.registerItem (new JStatusBar.StatusableItem ()
//    {
//      @Override public final boolean isOK ()
//      {
//        return display.isAllAuto ();
//      }
//      @Override public final void clickAction ()
//      {
//        display.setAllAuto ();
//      }
//    }, "AUTO");
    return jHp8663a;
  }
  
  public static void main (String[] args)
  {
    SwingUtilities.invokeLater (() ->
    {
      final JFrame frame = new JInstrument ();
      frame.setVisible (true);
    });
  }
  
}
