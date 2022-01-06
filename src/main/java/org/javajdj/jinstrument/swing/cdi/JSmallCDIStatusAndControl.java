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
package org.javajdj.jinstrument.swing.cdi;

import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.swing.JServiceControl;
import org.javajdj.jswing.jcenter.JCenter;

/** A small Swing panel for control and status of a generic {@link Instrument}.
 * 
 * <p>
 * Allows for control over the {@link Controller}, {@link Device} and {@link Instrument},
 * in clearly marked sub-panels.
 * 
 * @see JTinyCDIStatusAndControl
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class JSmallCDIStatusAndControl
  extends JInstrumentPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JSmallCDIStatusAndControl.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JSmallCDIStatusAndControl (
    final Controller controller,
    final Device device,
    final Instrument instrument,
    final int level,
    final Color subPanelColor,
    final boolean orientationHorizontal)
  {
    
    super (instrument, level);
    if (controller == null || device == null || instrument == null || subPanelColor == null)
      throw new IllegalArgumentException ();
    
    setOpaque (true);
    setPreferredSize (new Dimension (40, 80));
    
    setLayout (orientationHorizontal ? new GridLayout (1, 3) : new GridLayout (3, 1));
    
    final JPanel cPanel = new JInstrumentPanel (
      instrument,
      "Controller",
      level + 1,
      subPanelColor);
    add (cPanel);
    cPanel.setLayout (new GridLayout (1, 1));
    cPanel.add (JCenter.XY (new JServiceControl (controller, (Service.Status t) ->
    {
      switch (t)
      {
        case STOPPED: return null;
        case ACTIVE:  return Color.green;
        case ERROR:   return Color.orange;
        default:      throw new RuntimeException ();
      }
    })));
    
    final JPanel dPanel = new JInstrumentPanel (
      instrument,
      "Device",
      level + 1,
      subPanelColor);
    add (dPanel);
    dPanel.setLayout (new GridLayout (1, 1));
    dPanel.add (JCenter.XY (new JServiceControl (device, (Service.Status t) ->
    {
      switch (t)
      {
        case STOPPED: return null;
        case ACTIVE:  return Color.green;
        case ERROR:   return Color.orange;
        default:      throw new RuntimeException ();
      }
    })));
    
    final JPanel iPanel = new JInstrumentPanel (
      instrument,
      "Instrument",
      level + 1,
      subPanelColor);
    add (iPanel);
    iPanel.setLayout (new GridLayout (1, 1));
    iPanel.add (JCenter.XY (new JServiceControl (instrument, (Service.Status t) ->
    {
      switch (t)
      {
        case STOPPED: return null;
        case ACTIVE:  return Color.green;
        case ERROR:   return Color.orange;
        default:      throw new RuntimeException ();
      }
    })));
    
  }
  
  public JSmallCDIStatusAndControl (
    final Controller controller,
    final Device device,
    final Instrument instrument,
    final int level,
    final Color subPanelColor)
  {
    this (controller, device, instrument, level, subPanelColor, false);
  }
  
  public JSmallCDIStatusAndControl (
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
  
  public JSmallCDIStatusAndControl (
    final Instrument instrument,
    final int level)
  {
    this (instrument.getDevice ().getController (),
      instrument.getDevice (),
      instrument,
      level,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR);
  }
  
  public JSmallCDIStatusAndControl (final Instrument instrument)
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
      return "Small Con Dev Ins S&C";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null)
        return new JSmallCDIStatusAndControl (instrument);
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
    return JSmallCDIStatusAndControl.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
