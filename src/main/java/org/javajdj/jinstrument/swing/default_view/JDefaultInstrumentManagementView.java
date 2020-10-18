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
package org.javajdj.jinstrument.swing.default_view;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.swing.JServiceControl;

/** A one-size-fits-all Swing panel for control and status of a generic {@link Instrument}.
 * 
 * <p>
 * Allows for control over the {@link Controller}, {@link Device} and {@link Instrument}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class JDefaultInstrumentManagementView
  extends JInstrumentPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultInstrumentManagementView.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultInstrumentManagementView (
    final Controller controller,
    final Device device,
    final Instrument instrument,
    final int level,
    final Color subPanelColor)
  {
    super (instrument, level);
    if (controller == null || device == null || instrument == null || subPanelColor == null)
      throw new IllegalArgumentException ();
    this.controller = controller;
    this.device = device;
    this.instrument = instrument;
    setOpaque (true);
    setLayout (new GridLayout (3, 1));
    final JPanel controllerPanel = new JInstrumentPanel (null, "Controller", level + 1, subPanelColor);
    controllerPanel.setLayout (new GridLayout (2, 2));
    controllerPanel.add (new JLabel ("URL"));
    controllerPanel.add (new JLabel (this.controller.getControllerUrl ()));
    controllerPanel.add (new JLabel ("Service"));
    final JServiceControl controllerServiceCheckBox =
      new JServiceControl (this.controller, (Service.Status t) ->
      {
        switch (t)
        {
          case STOPPED: return null;
          case ACTIVE:  return Color.green;
          case ERROR:   return Color.orange;
          default:      throw new RuntimeException ();
        }
      });
    controllerPanel.add (controllerServiceCheckBox);
    add (controllerPanel);
    final JPanel devicePanel = new JInstrumentPanel (null, "Device", level + 1, subPanelColor);
    devicePanel.setLayout (new GridLayout (2, 2));
    devicePanel.add (new JLabel ("URL"));
    devicePanel.add (new JLabel (this.device.getDeviceUrl ()));
    devicePanel.add (new JLabel ("Service"));
    final JServiceControl deviceServiceCheckBox =
      new JServiceControl (this.device, (Service.Status t) ->
      {
        switch (t)
        {
          case STOPPED: return null;
          case ACTIVE:  return Color.green;
          case ERROR:   return Color.orange;
          default:      throw new RuntimeException ();
        }
      });
    devicePanel.add (deviceServiceCheckBox);
    add (devicePanel);
    final JPanel instrumentPanel = new JInstrumentPanel (instrument, "Instrument", level + 1, subPanelColor);
    instrumentPanel.setLayout (new GridLayout (2, 2));
    instrumentPanel.add (new JLabel ("Service"));
    final JServiceControl instrumentServiceCheckBox = new JServiceControl (this.instrument, (Service.Status t) ->
    {
      switch (t)
      {
        case STOPPED: return null;
        case ACTIVE:  return Color.green;
        case ERROR:   return Color.orange;
        default:      throw new RuntimeException ();
      }
    });
    instrumentPanel.add (instrumentServiceCheckBox);
    instrumentPanel.add (new JLabel ("Status"));
    this.jInstrumentStatus = new JTextField ();
    this.jInstrumentStatus.setText ("Unknown");
    this.jInstrumentStatus.setEditable (false);
    instrumentPanel.add (this.jInstrumentStatus);
    add (instrumentPanel);
    this.instrument.addInstrumentListener (this.instrumentListener);
  }
  
  public JDefaultInstrumentManagementView (
    final Instrument instrument,
    final int level,
    final Color subPanelColor)
  {
    this (instrument.getDevice ().getController (),
      instrument.getDevice (),
      instrument,
      level,
      subPanelColor);
  }
  
  public JDefaultInstrumentManagementView (
    final Instrument instrument,
    final int level)
  {
    this (instrument.getDevice ().getController (),
      instrument.getDevice (),
      instrument,
      level,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR);
  }
  
  public JDefaultInstrumentManagementView (final Instrument instrument)
  {
    this (instrument.getDevice ().getController (),
      instrument.getDevice (),
      instrument,
      1,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR);
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
      return "Default Instrument Management View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null)
        return new JDefaultInstrumentManagementView (instrument);
      else
        return null;
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentView
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getInstrumentViewUrl ()
  {
    return JDefaultInstrumentManagementView.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // CONTROLLER
  // DEVICE
  // INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  private final Controller controller;
  
  private final Device device;
  
  private final Instrument instrument;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  private final JTextField jInstrumentStatus;
    
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
      if (instrument != JDefaultInstrumentManagementView.this.instrument || instrumentStatus == null)
        throw new IllegalArgumentException ();
      SwingUtilities.invokeLater (() ->
      {
        JDefaultInstrumentManagementView.this.jInstrumentStatus.setText (instrumentStatus.toString ());
      });
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
