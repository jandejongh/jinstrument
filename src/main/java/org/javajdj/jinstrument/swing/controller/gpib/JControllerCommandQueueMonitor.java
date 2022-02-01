/*
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.swing.controller.gpib;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.AbstractController;
import org.javajdj.jinstrument.AbstractInstrument;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jswing.jbyte.JVUBar;

/** A Swing panel for monitoring the command queue of an {@link AbstractController}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 * @see JVUBar.Integer
 * 
 */
public class JControllerCommandQueueMonitor
  extends JPanel
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JControllerCommandQueueMonitor.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JControllerCommandQueueMonitor (final AbstractController controller)
  {
    
    super ();
    
    if (controller == null)
      throw new IllegalArgumentException ();
    this.controller = controller;
    
    setLayout (new GridLayout (1, 1));
    
    this.jVUBar = new JVUBar.Integer (
      1,      // minValue
      11,     // maxValue
      true);  // horizontalOrientation
    add (this.jVUBar);
        
    this.controller.addSettingsListener (this.propertyChangeListener);
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final AbstractController controller;
  
  public final Controller getController ()
  {
    return this.controller;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JVUBar jVUBar;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PROPERTY-CHANGE LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final PropertyChangeListener propertyChangeListener = (final PropertyChangeEvent pce) ->
  {
    if (pce == null
      || pce.getSource () != JControllerCommandQueueMonitor.this.getController ())
      return;
    switch (pce.getPropertyName ())
    {
      case AbstractInstrument.COMMAND_QUEUE_CAPACITY_PROPERTY_NAME:
        // XXX Redimension jVUBar?
        break;
      case AbstractInstrument.COMMAND_QUEUE_SIZE_PROPERTY_NAME:
        SwingUtilities.invokeLater (() ->
        {
          JControllerCommandQueueMonitor.this.jVUBar.setDisplayedValue ((int) pce.getNewValue ());
        });
        break;
    }
  };
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
