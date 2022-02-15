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
package org.javajdj.jinstrument.swing.instrument.dso.tek2440;

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

/** A Swing panel for the [Print] Device settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_PrintDevice
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_PrintDevice.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_PrintDevice (
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
    setLayout (new GridLayout (5, 1, 10, 0));
    
    final JTextPane jDescription = new JTextPane ();
    jDescription.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Description"));
    add (jDescription);
    
    final JPanel printDeviceTypePanel = new JPanel ();
    printDeviceTypePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "[Print] Device Type"));
    add (printDeviceTypePanel);
    
    final JPanel printDevicePageSizePanel = new JPanel ();
    printDevicePageSizePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "[Print Device] Page Size"));
    add (printDevicePageSizePanel);
    
    final JPanel pageLayoutPanel = new JPanel ();
    pageLayoutPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Page Layout"));
    add (pageLayoutPanel);
    
    final JPanel commandPanel = new JPanel ();
    commandPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Print Command"));
    add (commandPanel);
    
    //
    // jDescription
    //
    
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Print Settings</b>" +
                          "<ul>" +
                            "<li>Defines the device type (ThinkJet or HPGL);</li>" +
                            "<li>Defines the paper size (US or A4);</li>" +
                            "<li>Defines what to include in the printout;</li>" +
                            "<li>Provides the button to start printing.</li>" +
                          "</ul>" +
                          "</html>");

    //
    // printDeviceTypePanel
    //
    
    printDeviceTypePanel.setLayout (new GridLayout (1, 1, 0, 0));
    
    printDeviceTypePanel.add (JCenter.XY (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.PrintDeviceType.class,
      "print device type",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getPrintDeviceType (),
      tek2440::setPrintDeviceType,
      true)));
    
    //
    // printDevicePageSizePanel
    //
    
    printDevicePageSizePanel.setLayout (new GridLayout (1, 1, 0, 0));
    
    printDevicePageSizePanel.add (JCenter.XY (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.PrintPageSize.class,
      "print page size",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getPrintDevicePageSize (),
      tek2440::setPrintPageSize,
      true)));
    
    //
    // pageLayoutPanel
    //
    
    pageLayoutPanel.setLayout (new GridLayout (2, 4));
    
    pageLayoutPanel.add (new JLabel ("Graticule"));
    pageLayoutPanel.add (new JBoolean_JBoolean (
      "print graticule",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isPrintGraticule (),
      tek2440::setPrintGraticule,
      Color.green,
      true));
    
    pageLayoutPanel.add (new JLabel ("Settings"));
    pageLayoutPanel.add (new JBoolean_JBoolean (
      "print settings",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isPrintSettings (),
      tek2440::setPrintSettings,
      Color.green,
      true));
    
    pageLayoutPanel.add (new JLabel ("Text"));
    pageLayoutPanel.add (new JBoolean_JBoolean (
      "print text",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isPrintText (),
      tek2440::setPrintText,
      Color.green,
      true));
    
    pageLayoutPanel.add (new JLabel ("Waveforms"));
    pageLayoutPanel.add (new JBoolean_JBoolean (
      "print waveforms",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isPrintWaveforms (),
      tek2440::setPrintWaveforms,
      Color.green,
      true));
    
    //
    // commandPanel
    //
    
    commandPanel.setLayout (new GridLayout (1, 1));
    commandPanel.add (JCenter.XY (new JVoid_JColorCheckBox ("print", () -> { /* XXX */ }, Color.blue)));
    
  }

  public JTek2440_GPIB_PrintDevice (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_PrintDevice (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Print Device Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_PrintDevice ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_PrintDevice.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
