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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

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
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Print Settings</b>" +
//                          "<ul>" +
//                            "<li> the data format for transferring settings, status, and traces;</li>" +
//                            "<li>Features manual data transfers to and from the instrument;</li>" +
//                            "<li>Values marked in red are used internally in the software and therefore read-only.</li>" +
//                          "</ul>" +
                          "</html>");
    add (jDescription);
    
    final JPanel printDeviceTypePanel = new JPanel ();
    printDeviceTypePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "[Print] Device Type"));
    printDeviceTypePanel.setLayout (new GridLayout (1, 1, 0, 0));
    this.jPrintDeviceType = new JComboBox<> (Tek2440_GPIB_Settings.PrintDeviceType.values ());
    this.jPrintDeviceType.setSelectedItem (null);
    this.jPrintDeviceType.setEditable (false);
    this.jPrintDeviceType.setBackground (Color.red);
    this.jPrintDeviceType.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "print device type",
      Tek2440_GPIB_Settings.PrintDeviceType.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.PrintDeviceType>) tek2440::setPrintDeviceType,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    printDeviceTypePanel.add (JCenter.XY (this.jPrintDeviceType));
    add (printDeviceTypePanel);

    final JPanel printDevicePageSizePanel = new JPanel ();
    printDevicePageSizePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "[Print Device] Page Size"));
    printDevicePageSizePanel.setLayout (new GridLayout (1, 1, 0, 0));
    this.jPrintDevicePageSize = new JComboBox<> (Tek2440_GPIB_Settings.PrintPageSize.values ());
    this.jPrintDevicePageSize.setSelectedItem (null);
    this.jPrintDevicePageSize.setEditable (false);
    this.jPrintDevicePageSize.setBackground (Color.red);
    this.jPrintDevicePageSize.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "print page size",
      Tek2440_GPIB_Settings.PrintPageSize.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.PrintPageSize>) tek2440::setPrintPageSize,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    printDevicePageSizePanel.add (JCenter.XY (this.jPrintDevicePageSize));
    add (printDevicePageSizePanel);
    
    final JPanel pageLayoutPanel = new JPanel ();
    pageLayoutPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Page Layout"));
    pageLayoutPanel.setLayout (new GridLayout (2, 4));
    pageLayoutPanel.add (new JLabel ("Graticule"));
    this.jPrintGraticule = new JColorCheckBox.JBoolean (Color.green);
    this.jPrintGraticule.addActionListener (new JInstrumentActionListener_1Boolean (
      "print graticule",
      this.jPrintGraticule::getDisplayedValue,
      tek2440::setPrintGraticule,
      this::isInhibitInstrumentControl));
    pageLayoutPanel.add (this.jPrintGraticule);
    pageLayoutPanel.add (new JLabel ("Settings"));
    this.jPrintSettings = new JColorCheckBox.JBoolean (Color.green);
    this.jPrintSettings.addActionListener (new JInstrumentActionListener_1Boolean (
      "print settings",
      this.jPrintSettings::getDisplayedValue,
      tek2440::setPrintSettings,
      this::isInhibitInstrumentControl));
    pageLayoutPanel.add (this.jPrintSettings);
    pageLayoutPanel.add (new JLabel ("Text"));
    this.jPrintText = new JColorCheckBox.JBoolean (Color.green);
    this.jPrintText.addActionListener (new JInstrumentActionListener_1Boolean (
      "print text",
      this.jPrintText::getDisplayedValue,
      tek2440::setPrintText,
      this::isInhibitInstrumentControl));
    pageLayoutPanel.add (this.jPrintText);
    pageLayoutPanel.add (new JLabel ("Waveforms"));
    this.jPrintWaveforms = new JColorCheckBox.JBoolean (Color.green);
    this.jPrintWaveforms.addActionListener (new JInstrumentActionListener_1Boolean (
      "print waveforms",
      this.jPrintWaveforms::getDisplayedValue,
      tek2440::setPrintWaveforms,
      this::isInhibitInstrumentControl));
    pageLayoutPanel.add (this.jPrintWaveforms);
    add (pageLayoutPanel);
    
    final JPanel commandPanel = new JPanel ();
    commandPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Print Command"));
    commandPanel.setLayout (new BorderLayout ());
    this.jPrintCommand = new JButton ("Print");
    commandPanel.add (JCenter.XY (this.jPrintCommand), BorderLayout.CENTER);
    add (commandPanel);
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
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
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JComboBox<Tek2440_GPIB_Settings.PrintDeviceType> jPrintDeviceType;
  
  private final JComboBox<Tek2440_GPIB_Settings.PrintPageSize> jPrintDevicePageSize;
  
  private final JColorCheckBox.JBoolean jPrintGraticule;
  
  private final JColorCheckBox.JBoolean jPrintSettings;
    
  private final JColorCheckBox.JBoolean jPrintText;
  
  private final JColorCheckBox.JBoolean jPrintWaveforms;
  
  private final JButton jPrintCommand;
  
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
      // EMPTY
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JTek2440_GPIB_PrintDevice.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_PrintDevice.this.setInhibitInstrumentControl ();
        try
        {
          JTek2440_GPIB_PrintDevice.this.jPrintDeviceType.setSelectedItem (
            settings.getPrintDeviceType ());
          JTek2440_GPIB_PrintDevice.this.jPrintDeviceType.setBackground (
            JTek2440_GPIB_PrintDevice.this.getBackground ());
          JTek2440_GPIB_PrintDevice.this.jPrintDevicePageSize.setSelectedItem (
            settings.getPrintDevicePageSize ());
          JTek2440_GPIB_PrintDevice.this.jPrintDevicePageSize.setBackground (
            JTek2440_GPIB_PrintDevice.this.getBackground ());
          JTek2440_GPIB_PrintDevice.this.jPrintGraticule.setDisplayedValue (
            settings.isPrintGraticule ());
          JTek2440_GPIB_PrintDevice.this.jPrintSettings.setDisplayedValue (
            settings.isPrintSettings ());
          JTek2440_GPIB_PrintDevice.this.jPrintText.setDisplayedValue (
            settings.isPrintText ());
          JTek2440_GPIB_PrintDevice.this.jPrintWaveforms.setDisplayedValue (
            settings.isPrintWaveforms ());
        }
        finally
        {
          JTek2440_GPIB_PrintDevice.this.resetInhibitInstrumentControl ();
        }
      });
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      // EMPTY
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
