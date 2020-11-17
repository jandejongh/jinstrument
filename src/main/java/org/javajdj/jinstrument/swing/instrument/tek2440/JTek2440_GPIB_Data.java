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
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

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
    add (jDescription);
    
    final JPanel dataFormatPanel = new JPanel ();
    dataFormatPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Data [Waveforms] Formats"));
    
    dataFormatPanel.setLayout (new GridLayout (3, 2));
    add (dataFormatPanel);
    
    dataFormatPanel.add (new JLabel ("Encoding"));
    this.jDataEncoding = new JComboBox<> (Tek2440_GPIB_Settings.DataEncoding.values ());
    this.jDataEncoding.setSelectedItem (null);
    this.jDataEncoding.setEditable (false);
    this.jDataEncoding.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jDataEncoding.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "data encoding",
      Tek2440_GPIB_Settings.DataEncoding.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.DataEncoding>) tek2440::setDataEncoding,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    dataFormatPanel.add (new ComboBoxPanel (this.jDataEncoding));

    dataFormatPanel.add (new JLabel ("Use Path [in Query Responses]"));
    this.jPath = new JColorCheckBox.JBoolean (Color.green);
    this.jPath.addActionListener (new JInstrumentActionListener_1Boolean (
      "use path [query responses]",
      this.jPath::getDisplayedValue,
      tek2440::setUsePath,
      JTek2440_GPIB_Data.this::isInhibitInstrumentControl));
    dataFormatPanel.add (this.jPath);
    
    dataFormatPanel.add (new JLabel ("Long Response [Symbols]"));
    this.jLongResponse = new JColorCheckBox.JBoolean (Color.green);
    this.jLongResponse.addActionListener (new JInstrumentActionListener_1Boolean (
      "use long response [symbols]",
      this.jLongResponse::getDisplayedValue,
      tek2440::setUseLongResponse,
      JTek2440_GPIB_Data.this::isInhibitInstrumentControl));
    dataFormatPanel.add (this.jLongResponse);
    
    final JPanel dataTransferPanel = new JPanel ();
    dataTransferPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Data [Waveforms] Transfer"));
    
    dataTransferPanel.setLayout (new GridLayout (3, 2));
    add (dataTransferPanel);
    
    dataTransferPanel.add (new JLabel ("Source"));
    this.jDataSource = new JComboBox<> (Tek2440_GPIB_Settings.DataSource.values ());
    this.jDataSource.setSelectedItem (null);
    this.jDataSource.setEditable (false);
    this.jDataSource.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jDataSource.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "data source",
      Tek2440_GPIB_Settings.DataSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.DataSource>) tek2440::setDataSource,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    dataTransferPanel.add (new ComboBoxPanel (this.jDataSource));
    
    dataTransferPanel.add (new JLabel ("DSource"));
    this.jDataDSource = new JComboBox<> (Tek2440_GPIB_Settings.DataSource.values ());
    this.jDataDSource.setSelectedItem (null);
    this.jDataDSource.setEditable (false);
    this.jDataDSource.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jDataDSource.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "data dSource",
      Tek2440_GPIB_Settings.DataSource.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.DataSource>) tek2440::setDataDSource,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    dataTransferPanel.add (new ComboBoxPanel (this.jDataDSource));
    
    dataTransferPanel.add (new JLabel ("Target"));
    this.jDataTarget = new JComboBox<> (Tek2440_GPIB_Settings.DataTarget.values ());
    this.jDataTarget.setSelectedItem (null);
    this.jDataTarget.setEditable (false);
    this.jDataTarget.setBackground (JInstrumentPanel.getGuiPreferencesUpdatePendingColor ());
    this.jDataTarget.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "data target",
      Tek2440_GPIB_Settings.DataTarget.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.DataTarget>) tek2440::setDataTarget,
      this::isInhibitInstrumentControl,
      JInstrumentPanel.getGuiPreferencesUpdatePendingColor ()));
    dataTransferPanel.add (new ComboBoxPanel (this.jDataTarget));
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
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
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JComboBox<Tek2440_GPIB_Settings.DataEncoding> jDataEncoding;
  
  private final JColorCheckBox.JBoolean jPath;
  
  private final JColorCheckBox.JBoolean jLongResponse;
  
  private final JComboBox<Tek2440_GPIB_Settings.DataSource> jDataSource;
  
  private final JComboBox<Tek2440_GPIB_Settings.DataSource> jDataDSource;
  
  private final JComboBox<Tek2440_GPIB_Settings.DataTarget> jDataTarget;
  
  private final static class ComboBoxPanel
    extends JPanel
  {
    
    private final JComboBox jComboBox;

    public ComboBoxPanel (final JComboBox jComboBox)
    {
      if (jComboBox == null)
        throw new IllegalArgumentException ();
      this.jComboBox = jComboBox;
      this.jComboBox.setEditable (false);
      setLayout (new BoxLayout (this, BoxLayout.X_AXIS));
      final JPanel jColumn = new JPanel ();
      jColumn.setLayout (new BoxLayout (jColumn, BoxLayout.Y_AXIS));
      jColumn.add (Box.createGlue ());
      jColumn.add (this.jComboBox);
      jColumn.add (Box.createGlue ());
      add (jColumn);
      add (Box.createGlue ());
    }
    
  }
  
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
      if (instrument != JTek2440_GPIB_Data.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        setInhibitInstrumentControl ();
        try
        {
          
          JTek2440_GPIB_Data.this.jDataEncoding.setSelectedItem (settings.getDataEncoding ());
          JTek2440_GPIB_Data.this.jDataEncoding.setBackground (JTek2440_GPIB_Data.this.getBackground ());
          
          JTek2440_GPIB_Data.this.jPath.setDisplayedValue (settings.getUsePath ());
          
          JTek2440_GPIB_Data.this.jLongResponse.setDisplayedValue (settings.getUseLongResponse ());
          
          JTek2440_GPIB_Data.this.jDataSource.setSelectedItem (settings.getDataSource ());
          JTek2440_GPIB_Data.this.jDataSource.setBackground (JTek2440_GPIB_Data.this.getBackground ());
          
          JTek2440_GPIB_Data.this.jDataDSource.setSelectedItem (settings.getDataDSource ());
          JTek2440_GPIB_Data.this.jDataDSource.setBackground (JTek2440_GPIB_Data.this.getBackground ());
          
          JTek2440_GPIB_Data.this.jDataTarget.setSelectedItem (settings.getDataTarget ());
          JTek2440_GPIB_Data.this.jDataTarget.setBackground (JTek2440_GPIB_Data.this.getBackground ());
          
        }
        finally
        {
          resetInhibitInstrumentControl ();
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
