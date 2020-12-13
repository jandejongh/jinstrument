/*
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.swing.instrument.tek2440;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for the Data (Format) settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Data
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Data.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Data (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final String title,
    final int level,
    final Color panelColor)
  {
    
    super (digitalStorageOscilloscope, title, level, panelColor);
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;
    
    removeAll ();
    setLayout (new GridLayout (3, 1, 10, 0));
    
    final JTextPane jDescription = new JTextPane ();
    jDescription.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Description"));
    add (jDescription);
    
    final JPanel dataFormatPanel = new JPanel ();
    dataFormatPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Data [Waveforms] Formats"));
    add (dataFormatPanel);
    
    final JPanel dataTransferPanel = new JPanel ();
    dataTransferPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Data [Waveforms] Transfer"));
    add (dataTransferPanel);
    
    //
    // jDescription
    //
    
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Data Format and Transfer</b>" +
                          "<ul>" +
                            "<li>Controls the data format for transferring settings, status, and traces;</li>" +
                            "<li>Features manual data transfers to and from the instrument;</li>" +
                            "<li>Values marked in red are used internally in the software and therefore read-only.</li>" +
                          "</ul>" +
                          "</html>");
    
    //
    // dataFormatPanel
    //
    
    dataFormatPanel.setLayout (new GridLayout (3, 2));
    
    dataFormatPanel.add (new JLabel ("Encoding"));
    dataFormatPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.DataEncoding.class,
      "data encoding",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getDataEncoding (),
      tek2440::setDataEncoding,
      true)));
    
    dataFormatPanel.add (new JLabel ("Use Path [in Query Responses]"));
    dataFormatPanel.add (new JBoolean_JBoolean (
      "use path [query responses]",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isUsePath (),
      tek2440::setUsePath,
      Color.green,
      true));
    
    dataFormatPanel.add (new JLabel ("Long Response [Symbols]"));
    dataFormatPanel.add (new JBoolean_JBoolean (
      "use long response [symbols]",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isUseLongResponse (),
      tek2440::setUseLongResponse,
      Color.green,
      true));
    
    //
    // dataTransferPanel
    //
    
    dataTransferPanel.setLayout (new GridLayout (3, 2));
    
    dataTransferPanel.add (new JLabel ("Source"));
    dataTransferPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.DataSource.class,
      "data source",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getDataSource (),
      tek2440::setDataSource,
      true)));
    
    dataTransferPanel.add (new JLabel ("DSource"));
    dataTransferPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.DataSource.class,
      "data dSource",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getDataDSource (),
      tek2440::setDataDSource,
      true)));
    
    dataTransferPanel.add (new JLabel ("Target"));
    dataTransferPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.DataTarget.class,
      "data target",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getDataTarget (),
      tek2440::setDataTarget,
      true)));
    
  }

  public JTek2440_GPIB_Data (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Data (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
  {
    this (digitalStorageOscilloscope, 0);
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
      return "Tektronix 2440 [GPIB] Data [Format] Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Data ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Data.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
