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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
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
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for the BTrigger settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_BTrigger
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_BTrigger.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_BTrigger (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final String title,
    final int level,
    final Color panelColor)
  {
    //
    super (digitalStorageOscilloscope, title, level, panelColor);
    //
    removeAll ();
    setLayout (new GridLayout (1, 2, 10, 0));
    //
    final JPanel leftPanel = new JPanel ();
    leftPanel.setLayout (new GridLayout (5, 1, 0, 10));
    add (leftPanel);
    final JPanel rightPanel = new JPanel ();
    rightPanel.setLayout (new GridLayout (5, 1, 0, 10));
    add (rightPanel);
    //
    leftPanel.add (new JLabel ("Mode"));
    this.jMode = new JComboBox<>  (Tek2440_GPIB_Settings.BTriggerMode.values ());
    this.jMode.setSelectedItem (null);
    this.jMode.setEditable (false);
    this.jMode.setEnabled (false);
    leftPanel.add (this.jMode);
    //
    leftPanel.add (new JLabel ("Source"));
    this.jSource = new JComboBox<> (Tek2440_GPIB_Settings.BTriggerSource.values ());
    this.jSource.setSelectedItem (null);
    this.jSource.setEditable (false);
    this.jSource.setEnabled (false);
    leftPanel.add (this.jSource);
    //
    leftPanel.add (new JLabel ("Coupling"));
    this.jCoupling = new JComboBox<> (Tek2440_GPIB_Settings.BTriggerCoupling.values ());
    this.jCoupling.setSelectedItem (null);
    this.jCoupling.setEditable (false);
    this.jCoupling.setEnabled (false);
    leftPanel.add (this.jCoupling);
    //
    leftPanel.add (new JLabel ("Slope"));
    this.jSlope = new JComboBox<> (Tek2440_GPIB_Settings.Slope.values ());
    this.jSlope.setSelectedItem (null);
    this.jSlope.setEditable (false);
    this.jSlope.setEnabled (false);
    leftPanel.add (this.jSlope);
    //
    leftPanel.add (new JLabel ("Level"));
    this.jLevel = new JSlider ();
    this.jLevel.setEnabled (false);
    leftPanel.add (this.jLevel);
    //
    rightPanel.add (new JLabel ("Position"));
    this.jPosition = new JComboBox<> (Tek2440_GPIB_Settings.BTriggerPosition.values ());
    this.jPosition.setSelectedItem (null);
    this.jPosition.setEditable (false);
    this.jPosition.setEnabled (false);
    rightPanel.add (this.jPosition);
    //
    rightPanel.add (new JLabel ("Ext Clk"));
    this.jExtClk = new JColorCheckBox.JBoolean (Color.green);
    this.jExtClk.setEnabled (false);
    rightPanel.add (this.jExtClk);
    //
    rightPanel.add (new JLabel ());
    rightPanel.add (new JLabel ());
    rightPanel.add (new JLabel ());
    rightPanel.add (new JLabel ());
    rightPanel.add (new JLabel ());
    rightPanel.add (new JLabel ());
    //
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    //
  }

  public JTek2440_GPIB_BTrigger (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_BTrigger (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] BTrigger Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_BTrigger ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_BTrigger.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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

  private final JComboBox<Tek2440_GPIB_Settings.BTriggerMode> jMode;
  
  private final JComboBox<Tek2440_GPIB_Settings.BTriggerSource> jSource;
  
  private final JComboBox<Tek2440_GPIB_Settings.BTriggerCoupling> jCoupling;
  
  private final JComboBox<Tek2440_GPIB_Settings.Slope> jSlope;
  
  private final JSlider jLevel;
  
  private final JComboBox<Tek2440_GPIB_Settings.BTriggerPosition> jPosition;
  
  private final JColorCheckBox.JBoolean jExtClk;
  
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
      if (instrument != JTek2440_GPIB_BTrigger.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_BTrigger.this.inhibitInstrumentControl = true;
        try
        {
          JTek2440_GPIB_BTrigger.this.jMode.setSelectedItem (settings.getBTriggerMode ());
          JTek2440_GPIB_BTrigger.this.jSource.setSelectedItem (settings.getBTriggerSource ());
          JTek2440_GPIB_BTrigger.this.jCoupling.setSelectedItem (settings.getBTriggerCoupling ());
          JTek2440_GPIB_BTrigger.this.jSlope.setSelectedItem (settings.getBTriggerSlope ());
          // JTek2440_GPIB_BTrigger.this.jLevel.setValue (XXX);
          JTek2440_GPIB_BTrigger.this.jLevel.setToolTipText (Double.toString (settings.getBTriggerLevel ()));
          JTek2440_GPIB_BTrigger.this.jPosition.setSelectedItem (settings.getBTriggerPosition ());
          JTek2440_GPIB_BTrigger.this.jExtClk.setDisplayedValue (settings.getBTriggerExtClock ());
        }
        finally
        {
          JTek2440_GPIB_BTrigger.this.inhibitInstrumentControl = false;
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