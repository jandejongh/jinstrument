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
package org.javajdj.jinstrument.swing.cdi;

import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jservice.Service;
import org.javajdj.jservice.swing.JServiceControl;

/** A tiny Swing panel for control and status of a generic {@link Instrument}.
 * 
 * <p>
 * Allows for control over the {@link Controller}, {@link Device} and {@link Instrument},
 * abbreviated, respectively as, well, you get the idea...
 * 
 * @see JSmallCDIStatusAndControl
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTinyCDIStatusAndControl
  extends JInstrumentPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTinyCDIStatusAndControl.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTinyCDIStatusAndControl (
    final Controller controller,
    final Device device,
    final Instrument instrument,
    final int level,
    final Color subPanelColor)
  {
    super (instrument, level);
    if (controller == null || device == null || instrument == null || subPanelColor == null)
      throw new IllegalArgumentException ();
    setOpaque (true);
    setLayout (new GridLayout (3, 2));
    setPreferredSize (new Dimension (40, 80));
    final JServiceControl controllerServiceCheckBox =
      new JServiceControl (controller, (Service.Status t) ->
      {
        switch (t)
        {
          case STOPPED: return null;
          case ACTIVE:  return Color.green;
          case ERROR:   return Color.orange;
          default:      throw new RuntimeException ();
        }
      });
    add (controllerServiceCheckBox);
    add (new JLabel ("C"));
    final JServiceControl deviceServiceCheckBox =
      new JServiceControl (device, (Service.Status t) ->
      {
        switch (t)
        {
          case STOPPED: return null;
          case ACTIVE:  return Color.green;
          case ERROR:   return Color.orange;
          default:      throw new RuntimeException ();
        }
      });
    add (deviceServiceCheckBox);
    add (new JLabel ("D"));
    final JServiceControl instrumentServiceCheckBox = new JServiceControl (instrument, (Service.Status t) ->
    {
      switch (t)
      {
        case STOPPED: return null;
        case ACTIVE:  return Color.green;
        case ERROR:   return Color.orange;
        default:      throw new RuntimeException ();
      }
    });
    add (instrumentServiceCheckBox);
    add (new JLabel ("I"));
  }
  
  public JTinyCDIStatusAndControl (
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
  
  public JTinyCDIStatusAndControl (
    final Instrument instrument,
    final int level)
  {
    this (instrument.getDevice ().getController (),
      instrument.getDevice (),
      instrument,
      level,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR);
  }
  
  public JTinyCDIStatusAndControl (final Instrument instrument)
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
      return "Tiny Con Dev Ins S&C";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null)
        return new JTinyCDIStatusAndControl (instrument);
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
    return JTinyCDIStatusAndControl.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
