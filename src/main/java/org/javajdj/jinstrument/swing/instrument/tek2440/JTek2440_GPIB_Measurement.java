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

/** A Swing panel for the Measurement settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Measurement
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Measurement.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Measurement (
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
    setLayout (new GridLayout (4, 1, 10, 10));
    
    final JTextPane jDescription = new JTextPane ();
    jDescription.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Description"));
    add (jDescription);
    
    final JPanel mainPanel = new JPanel ();
    mainPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Main"));
    add (mainPanel);
    
    final JPanel channelsPanel = new JPanel ();
    add (channelsPanel);
    
    channelsPanel.setLayout (new GridLayout (1, 4));
    
    final JPanel jCh1Panel = new JPanel ();
    jCh1Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Channel 1"));
    channelsPanel.add (jCh1Panel);
    
    final JPanel jCh2Panel = new JPanel ();
    jCh2Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Channel 2"));
    channelsPanel.add (jCh2Panel);
    
    final JPanel jCh3Panel = new JPanel ();
    jCh3Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Channel 3"));
    channelsPanel.add (jCh3Panel);
    
    final JPanel jCh4Panel = new JPanel ();
    jCh4Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Channel 4"));
    channelsPanel.add (jCh4Panel);
    
    final JPanel methodPanel = new JPanel ();
    add (methodPanel);

    methodPanel.setLayout (new GridLayout (1, 4));
    
    final JPanel jProximalPanel = new JPanel ();
    jProximalPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Proximal"));
    methodPanel.add (jProximalPanel);
    
    final JPanel jMesialPanel = new JPanel ();
    jMesialPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Mesial"));
    methodPanel.add (jMesialPanel);
    
    final JPanel jDMesialPanel = new JPanel ();
    jDMesialPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "DMesial"));
    methodPanel.add (jDMesialPanel);
    
    final JPanel jDistalPanel = new JPanel ();
    jDistalPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 1),
        "Distal"));
    methodPanel.add (jDistalPanel);
    
    //
    // jDescription
    //
    
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Measurement Settings</b>" +
                          "<ul>" +
                            "<li>Defines up to four measurement channels (and their display settings);</li>" +
                            "<li>Defines settings for four different measurement methods.</li>" +
                          "</ul>" +
                          "</html>");
    
    //
    // mainPanel
    //
    
    mainPanel.setLayout (new GridLayout (2, 5));
    
    mainPanel.add (new JLabel ("Method"));
    mainPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementMethod.class,
      "measurement method",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementMethod (),
      tek2440::setMeasurementMethod,
      true)));

    mainPanel.add (new JLabel ());
    
    mainPanel.add (new JLabel ("Display"));
    mainPanel.add (new JBoolean_JBoolean (
      "display measurements",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isMeasurementDisplay (),
      tek2440::setMeasurementDisplay,
      Color.green,
      true));
    
    mainPanel.add (new JLabel ("Windowing"));
    mainPanel.add (new JBoolean_JBoolean (
      "[measurements] windowing",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isMeasurementWindowing (),
      tek2440::setMeasurementWindowing,
      Color.green,
      true));
    
    mainPanel.add (new JLabel ());
    
    mainPanel.add (new JLabel ("Display Markers"));
    mainPanel.add (new JBoolean_JBoolean (
      "display measurement threshold markers",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isMeasurementDisplayThresholdCrossingMarks (),
      tek2440::setMeasurementDisplayMarkers,
      Color.green,
      true));
    
    //
    // jCh1Panel
    //
    
    jCh1Panel.setLayout (new GridLayout (3, 2));
    
    jCh1Panel.add (new JLabel ("Type"));
    jCh1Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementType.class,
      "measurement channel 1 type",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel1Type (),
      tek2440::setMeasurementChannel1Type,
      true)));
    
    jCh1Panel.add (new JLabel ("Source"));
    jCh1Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementSource.class,
      "measurement channel 1 source",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel1Source (),
      tek2440::setMeasurementChannel1Source,
      true)));
    
    jCh1Panel.add (new JLabel ("DSource"));
    jCh1Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementSource.class,
      "measurement channel 1 delayed source",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel1DelayedSource (),
      tek2440::setMeasurementChannel1DSource,
      true)));
        
    //
    // jCh2Panel
    //
    
    jCh2Panel.setLayout (new GridLayout (3, 2));
    
    jCh2Panel.add (new JLabel ("Type"));
    jCh2Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementType.class,
      "measurement channel 2 type",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel2Type (),
      tek2440::setMeasurementChannel2Type,
      true)));
    
    jCh2Panel.add (new JLabel ("Source"));
    jCh2Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementSource.class,
      "measurement channel 2 source",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel2Source (),
      tek2440::setMeasurementChannel2Source,
      true)));
    
    jCh2Panel.add (new JLabel ("DSource"));
    jCh2Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementSource.class,
      "measurement channel 2 delayed source",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel2DelayedSource (),
      tek2440::setMeasurementChannel2DSource,
      true)));
    
    //
    // jCh3Panel
    //
    
    jCh3Panel.setLayout (new GridLayout (3, 2));
    
    jCh3Panel.add (new JLabel ("Type"));
    jCh3Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementType.class,
      "measurement channel 3 type",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel3Type (),
      tek2440::setMeasurementChannel3Type,
      true)));
    
    jCh3Panel.add (new JLabel ("Source"));
    jCh3Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementSource.class,
      "measurement channel 3 source",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel3Source (),
      tek2440::setMeasurementChannel3Source,
      true)));
    
    jCh3Panel.add (new JLabel ("DSource"));
    jCh3Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementSource.class,
      "measurement channel 3 delayed source",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel3DelayedSource (),
      tek2440::setMeasurementChannel3DSource,
      true)));
    
    //
    // jCh4Panel
    //
    
    jCh4Panel.setLayout (new GridLayout (3, 2));
    
    jCh4Panel.add (new JLabel ("Type"));
    jCh4Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementType.class,
      "measurement channel 4 type",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel4Type (),
      tek2440::setMeasurementChannel4Type,
      true)));
    
    jCh4Panel.add (new JLabel ("Source"));
    jCh4Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementSource.class,
      "measurement channel 4 source",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel4Source (),
      tek2440::setMeasurementChannel4Source,
      true)));
    
    jCh4Panel.add (new JLabel ("DSource"));
    jCh4Panel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MeasurementSource.class,
      "measurement channel 4 delayed source",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementChannel4DelayedSource (),
      tek2440::setMeasurementChannel4DSource,
      true)));
    
    //
    // jProximalPanel
    //
    
    jProximalPanel.setLayout (new GridLayout (3, 2));
    
    jProximalPanel.add (new JLabel ("Unit"));
    jProximalPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.ProximalUnit.class,
      "[measurement] proximal unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementProximalUnit (),
      tek2440::setMeasurementProximalUnit,
      true)));
    
    jProximalPanel.add (new JLabel ("Volts"));
    jProximalPanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "[measurement] proximal volts level",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementProximalVoltsLevel (),
      tek2440::setMeasurementProximalVoltsLevel,
      true)));
    
    jProximalPanel.add (new JLabel ("Percents"));
    jProximalPanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "[measurement] proximal percents level",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementProximalPercentsLevel (),
      tek2440::setMeasurementProximalPercentsLevel,
      true)));
    
    //
    // jMesialPanel
    //
    
    jMesialPanel.setLayout (new GridLayout (3, 2));
    
    jMesialPanel.add (new JLabel ("Unit"));
    jMesialPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.MesialUnit.class,
      "[measurement] mesial unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementMesialUnit (),
      tek2440::setMeasurementMesialUnit,
      true)));
    
    jMesialPanel.add (new JLabel ("Volts"));
    jMesialPanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "[measurement] mesial volts level",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementMesialVoltsLevel (),
      tek2440::setMeasurementMesialVoltsLevel,
      true)));
    
    jMesialPanel.add (new JLabel ("Percents"));
    jMesialPanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "[measurement] mesial percents level",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementMesialPercentsLevel (),
      tek2440::setMeasurementMesialPercentsLevel,
      true)));
    
    //
    // jDMesialPanel
    //
    
    jDMesialPanel.setLayout (new GridLayout (3, 2));
    
    jDMesialPanel.add (new JLabel ("Unit"));
    jDMesialPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.DMesialUnit.class,
      "[measurement] dmesial unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementDMesialUnit (),
      tek2440::setMeasurementDMesialUnit,
      true)));
    
    jDMesialPanel.add (new JLabel ("Volts"));
    jDMesialPanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "[measurement] dmesial volts level",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementDMesialVoltsLevel (),
      tek2440::setMeasurementDMesialVoltsLevel,
      true)));
    
    jDMesialPanel.add (new JLabel ("Percents"));
    jDMesialPanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "[measurement] dmesial percents level",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementDMesialPercentsLevel (),
      tek2440::setMeasurementDMesialPercentsLevel,
      true)));
    
    //
    // jDistalPanel
    //
    
    jDistalPanel.setLayout (new GridLayout (3, 2));
    
    jDistalPanel.add (new JLabel ("Unit"));
    jDistalPanel.add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.DistalUnit.class,
      "[measurement] distal unit",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementDistalUnit (),
      tek2440::setMeasurementDistalUnit,
      true)));
    
    jDistalPanel.add (new JLabel ("Volts"));
    jDistalPanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "[measurement] distal volts level",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementDistalVoltsLevel (),
      tek2440::setMeasurementDistalVoltsLevel,
      true)));
    
    jDistalPanel.add (new JLabel ("Percents"));
    jDistalPanel.add (JCenter.Y (new Jdouble_JTextField (
      null,
      12,
      "[measurement] distal percents level",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getMeasurementDistalPercentsLevel (),
      tek2440::setMeasurementDistalPercentsLevel,
      true)));
    
  }

  public JTek2440_GPIB_Measurement (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Measurement (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Measurement Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Measurement ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Measurement.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
