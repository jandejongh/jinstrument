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
package org.javajdj.jinstrument.swing.instrument.slm.rs_esh3;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Settings;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;

/** A Swing panel for GPIB SRQ (Service Request) settings of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_SRQ
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_SRQ.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_SRQ (final RS_ESH3_GPIB_Instrument selectedLevelMeter, final int level)
  {
    
    super (selectedLevelMeter, level);
    if (! (selectedLevelMeter instanceof RS_ESH3_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final RS_ESH3_GPIB_Instrument rs_esh3 = (RS_ESH3_GPIB_Instrument) selectedLevelMeter;
    
    removeAll ();
    setLayout (new GridLayout (4, 1, 0, 2));
    
    final JTextPane jDescription = new JTextPane ();
    jDescription.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Description"));
    add (jDescription);
    
    final JPanel northPanel = new JPanel ();
    northPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Service Request [GPIB] Mask Settings"));
    add (northPanel);
    
    final JPanel centerPanel = new JPanel ();
    centerPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Center Panel"));
    add (centerPanel);
    
    final JPanel southPanel = new JPanel ();
    southPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "South Panel"));
    add (southPanel);
    
    //
    // jDesciption
    //
    
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Service Request Settings</b>" +
                          "<ul>" +
                            "<li>Shows and controls the settings on the instrument "
                                   + "that determine its Service Request triggers;</li>" +
                            "<li>Since the implementation depends on certain settings below, "
                                   + "it is advised not to change them.</li>" +
                          "</ul>" +
                          "</html>");
    
    //
    // northPanel
    //
    
    northPanel.setLayout (new GridLayout (2,2));

    northPanel.add (new JLabel ("Data Ready"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq on data ready (measurement or calibration)",
      (settings) -> ((RS_ESH3_GPIB_Settings) settings).isSrqOnDataReady (),
      rs_esh3::setSrqOnDataReady,
      Color.green,
      true));
    
    northPanel.add (new JLabel ("Press <ENTER> in Local State"));
    northPanel.add (new JBoolean_JBoolean (
      "enable srq on user pressing <ENTER> in LOCAL state",
      (settings) -> ((RS_ESH3_GPIB_Settings) settings).isSrqOnEnterInLocalState (),
      rs_esh3::setSrqOnEnterInLocalState,
      Color.green,
      true));
    
    //
    // centerPanel
    //
    
    centerPanel.setLayout (new GridLayout (1,1));
    centerPanel.add (new JLabel ("Not Used"));
    
    //
    // southPanel
    //
    
    southPanel.setLayout (new GridLayout (1, 1));
    southPanel.add (new JLabel ("Not Used"));

  }
  
  public JRS_ESH3_GPIB_SRQ (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
  {
    this (selectiveLevelMeter, 0);
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
      return "R&S ESH-3 [GPIB] Service Request Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_SRQ ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_SRQ.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
