package org.javajdj.jinstrument.r1.swing.dso;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.javajdj.jinstrument.r1.instrument.ChannelId;

/**
 *
 */
public class JOscilloscopeAllChannelsControl
extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JOscilloscopeAllChannelsControl.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JOscilloscopeAllChannelsControl (final JOscilloscopeManagedTraceDisplay manager)
  {
    super ();
    if (manager == null)
      throw new IllegalArgumentException ();
    this.manager = manager;
    final List<ChannelId> channelIds = this.manager.getChannelIds ();
    if (channelIds != null)
    {
      final Box box = new Box (BoxLayout.Y_AXIS);
      for (ChannelId ch : channelIds)
      {
        final JOscilloscopeSingleChannelControl chControl = new JOscilloscopeSingleChannelControl (manager, ch);
        chControl.setBorder
          (BorderFactory.createTitledBorder (BorderFactory.createLineBorder (Color.RED, 3, true), ch.toString ()));
        box.add (chControl);
      }
      add (box, BorderLayout.CENTER);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY manager
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JOscilloscopeManagedTraceDisplay manager;
  
}
