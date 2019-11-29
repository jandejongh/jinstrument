package org.javajdj.jinstrument.r1.swing.dso;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 *
 */
public class JOscilloscopeControl
extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JOscilloscopeControl.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JOscilloscopeControl (final JOscilloscopeManagedTraceDisplay manager)
  {
    super ();
    if (manager == null)
      throw new IllegalArgumentException ();
    this.manager = manager;
    final Box box = new Box (BoxLayout.X_AXIS);
    final JOscilloscopeAllChannelsControl chControl = new JOscilloscopeAllChannelsControl (manager);
    chControl.setBorder
      (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (Color.RED, 3, true), "Y"));
    box.add (chControl);
    final JOscilloscopeTimeBaseControl tbControl = new JOscilloscopeTimeBaseControl (manager);
    tbControl.setBorder
      (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (Color.RED, 3, true), "TIME"));
    box.add (tbControl);
    final JOscilloscopeTriggerControl trControl = new JOscilloscopeTriggerControl (manager);
    trControl.setBorder
      (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (Color.RED, 3, true), "TRIG"));
    box.add (trControl);
    add (box, BorderLayout.CENTER);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY manager
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JOscilloscopeManagedTraceDisplay manager;
  
}
