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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
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
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for the Acquisition settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Acquisition
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Acquisition.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Acquisition (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final String title,
    final int level,
    final Color panelColor)
  {
    //
    super (digitalStorageOscilloscope, title, level, panelColor);
    //
    removeAll ();
    setLayout (new GridLayout (2, 1, 0, 0));
    //
    final JPanel northPanel = new JPanel ();
    northPanel.setLayout (new GridLayout (5, 2, 0, 10));
    northPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Acquisition"));
    add (northPanel);    
    final JPanel southPanel = new JPanel ();
    southPanel.setLayout (new GridLayout (1, 2, 0, 0));
    add (southPanel);
    final JPanel southWestPanel = new JPanel ();
    southWestPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Run"));
    southWestPanel.setLayout (new BoxLayout (southWestPanel, BoxLayout.X_AXIS));
    southPanel.add (southWestPanel);
    final JPanel southEastPanel = new JPanel ();
    southEastPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Smooth"));
    southEastPanel.setLayout (new BorderLayout ());
    southPanel.add (southEastPanel);
    //
    northPanel.add (new JLabel ("Acquisition Mode"));
    this.jAcquisitionMode = new JComboBox<>  (Tek2440_GPIB_Settings.AcquisitionMode.values ());
    this.jAcquisitionMode.setSelectedItem (null);
    this.jAcquisitionMode.setEditable (false);
    this.jAcquisitionMode.setEnabled (false);
    northPanel.add (this.jAcquisitionMode);
    //
    northPanel.add (new JLabel ("Repetitive"));
    this.jAcquisitionRepetitive = new JColorCheckBox.JBoolean (Color.green);
    this.jAcquisitionRepetitive.setEnabled (false);
    northPanel.add (this.jAcquisitionRepetitive);
    //
    northPanel.add (new JLabel ("Number of Acquisitions Averaged"));
    this.jNumberOfAcquisitionsAveraged = new JComboBox<>  (Tek2440_GPIB_Settings.NumberOfAcquisitionsAveraged.values ());
    this.jNumberOfAcquisitionsAveraged.setSelectedItem (null);
    this.jNumberOfAcquisitionsAveraged.setEditable (false);
    this.jNumberOfAcquisitionsAveraged.setEnabled (false);
    northPanel.add (this.jNumberOfAcquisitionsAveraged);
    //
    northPanel.add (new JLabel ("Number of Envelope Sweeps"));
    this.jNumberOfEnvelopeSweeps = new JComboBox<>  (Tek2440_GPIB_Settings.NumberOfEnvelopeSweeps.values ());
    this.jNumberOfEnvelopeSweeps.setSelectedItem (null);
    this.jNumberOfEnvelopeSweeps.setEditable (false);
    this.jNumberOfEnvelopeSweeps.setEnabled (false);
    northPanel.add (this.jNumberOfEnvelopeSweeps);
    //
    northPanel.add (new JLabel ("Save on Delta"));
    this.jAcquisitionSaveOnDelta = new JColorCheckBox.JBoolean (Color.green);
    this.jAcquisitionSaveOnDelta.setEnabled (false);
    northPanel.add (this.jAcquisitionSaveOnDelta);
    //
    this.jRunMode = new JComboBox<> (Tek2440_GPIB_Settings.RunMode.values ());
    this.jRunMode.setSelectedItem (null);
    this.jRunMode.setEditable (false);
    this.jRunMode.setEnabled (false);
    this.jRunMode.setMaximumSize (new Dimension (120, 20));
    southWestPanel.add (Box.createGlue ());
    southWestPanel.add (this.jRunMode);
    southWestPanel.add (Box.createGlue ());
    //
    this.jSmooth = new JColorCheckBox.JBoolean (Color.green);
    this.jSmooth.setEnabled (false);
    this.jSmooth.setHorizontalAlignment (SwingConstants.CENTER);
    southEastPanel.add (this.jSmooth, BorderLayout.CENTER);
    //
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    //
  }

  public JTek2440_GPIB_Acquisition (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Acquisition (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] ATrigger Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Acquisition ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Acquisition.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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

  private final JComboBox<Tek2440_GPIB_Settings.AcquisitionMode> jAcquisitionMode;
  
  private final JColorCheckBox.JBoolean jAcquisitionRepetitive;
  
  private final JComboBox<Tek2440_GPIB_Settings.NumberOfAcquisitionsAveraged> jNumberOfAcquisitionsAveraged;
  
  private final JComboBox<Tek2440_GPIB_Settings.NumberOfEnvelopeSweeps> jNumberOfEnvelopeSweeps;
  
  private final JColorCheckBox.JBoolean jAcquisitionSaveOnDelta;
  
  private final JComboBox<Tek2440_GPIB_Settings.RunMode> jRunMode;
  
  private final JColorCheckBox.JBoolean jSmooth;
  
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
      if (instrument != JTek2440_GPIB_Acquisition.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_Acquisition.this.inhibitInstrumentControl = true;
        try
        {
          JTek2440_GPIB_Acquisition.this.jAcquisitionMode.setSelectedItem (
            settings.getAcquisitionMode ());
          JTek2440_GPIB_Acquisition.this.jAcquisitionRepetitive.setDisplayedValue (
            settings.isAcquisitionRepetitive ());
          JTek2440_GPIB_Acquisition.this.jNumberOfAcquisitionsAveraged.setSelectedItem (
            settings.getNumberOfAcquisitionsAveraged ());
          JTek2440_GPIB_Acquisition.this.jNumberOfEnvelopeSweeps.setSelectedItem (
            settings.getNumberOfEnvelopeSweeps ());
          JTek2440_GPIB_Acquisition.this.jAcquisitionSaveOnDelta.setDisplayedValue (
            settings.isAcquisitionSaveOnDelta ());
          JTek2440_GPIB_Acquisition.this.jAcquisitionSaveOnDelta.setDisplayedValue (
            settings.isAcquisitionSaveOnDelta ());
          JTek2440_GPIB_Acquisition.this.jRunMode.setSelectedItem (
            settings.getRunMode ());
          JTek2440_GPIB_Acquisition.this.jSmooth.setDisplayedValue (
            settings.isSmoothing ());
        }
        finally
        {
          JTek2440_GPIB_Acquisition.this.inhibitInstrumentControl = false;
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
