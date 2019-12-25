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
package org.javajdj.jinstrument.swing;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;

/** A Swing panel showing relevant URLs of a generic {@link Instrument}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class JDefaultInstrumentManagementUrlsView
  extends JInstrumentPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultInstrumentManagementUrlsView.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultInstrumentManagementUrlsView (
    final Controller controller,
    final Device device,
    final Instrument instrument,
    final int level,
    final Color subPanelColor)
  {
    super (instrument, level);
    if (controller == null || device == null || instrument == null || subPanelColor == null)
      throw new IllegalArgumentException ();
    setLayout (new GridLayout (3, 1));
    add (new JLabel ("Controller: " + controller.getControllerUrl ()));
    add (new JLabel ("Device: " + device.getDeviceUrl ()));
    add (new JLabel ("Instrument: " + instrument.getInstrumentUrl ()));
  }
  
  public JDefaultInstrumentManagementUrlsView (
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
  
  public JDefaultInstrumentManagementUrlsView (
    final Instrument instrument,
    final int level)
  {
    this (instrument.getDevice ().getController (),
      instrument.getDevice (),
      instrument,
      level,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR);
  }
  
  public JDefaultInstrumentManagementUrlsView (final Instrument instrument)
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
      return "Default Instrument URL View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null)
        return new JDefaultInstrumentManagementUrlsView (instrument);
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
    return JDefaultInstrumentManagementUrlsView.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
