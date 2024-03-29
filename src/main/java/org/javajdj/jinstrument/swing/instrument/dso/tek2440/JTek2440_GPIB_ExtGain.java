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
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for the Ext (Inputs) Gain settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_ExtGain
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_ExtGain.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_ExtGain (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final String title,
    final int level,
    final Color panelColor,
    final Integer input)
  {

    super (digitalStorageOscilloscope, title, level, panelColor);
    
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;

    if (input != null && input != 1 && input != 2)
      throw new IllegalArgumentException ();
    this.input = input;
    
    if (this.input == null || this.input == 1)
    {
      this.jExtGain1 = new JComboBox<>  (Tek2440_GPIB_Settings.ExtGain.values ());
      this.jExtGain1.setSelectedItem (null);
      this.jExtGain1.setEditable (false);
      this.jExtGain1.setBackground (getGuiPreferencesUpdatePendingColor ());
      this.jExtGain1.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
        "ext 1 [input] gain",
        Tek2440_GPIB_Settings.ExtGain.class,
        (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.ExtGain>) tek2440::setExtGain1,
        JTek2440_GPIB_ExtGain.this::isInhibitInstrumentControl,
        getGuiPreferencesUpdatePendingColor ()));
    }
    else
      this.jExtGain1 = null;
    
    if (this.input == null || this.input == 2)
    {
      this.jExtGain2 = new JComboBox<> (Tek2440_GPIB_Settings.ExtGain.values ());
      this.jExtGain2.setSelectedItem (null);
      this.jExtGain2.setEditable (false);
      this.jExtGain2.setBackground (getGuiPreferencesUpdatePendingColor ());
      this.jExtGain2.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
        "ext 2 [input] gain",
        Tek2440_GPIB_Settings.ExtGain.class,
        (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.ExtGain>) tek2440::setExtGain2,
        JTek2440_GPIB_ExtGain.this::isInhibitInstrumentControl,
        getGuiPreferencesUpdatePendingColor ()));
    }
    else
      this.jExtGain2 = null;
    
    removeAll ();
    
    if (this.input == null)
    {
      setLayout (new GridLayout (2, 2, 0, 1));
      add (new JLabel ("Ext 1"));
      add (JCenter.Y (this.jExtGain1));
      add (new JLabel ("Ext 2"));
      add (JCenter.Y (this.jExtGain2));
    }
    else
    {
      setLayout (new GridLayout (1, 1));
      switch (this.input)
      {
        case 1:
          add (JCenter.XY (this.jExtGain1));
          break;
        case 2:
          add (JCenter.XY (this.jExtGain2));
          break;
        default:
          throw new RuntimeException ();
      }
    }

    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);

  }

  public JTek2440_GPIB_ExtGain (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final String title,
    final int level,
    final Color panelColor)
  {
    this (digitalStorageOscilloscope, title, level, panelColor, null);
  }
  
  public JTek2440_GPIB_ExtGain (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_ExtGain (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Ext Gain Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_ExtGain ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_ExtGain.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // INPUT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  final Integer input;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JComboBox<Tek2440_GPIB_Settings.ExtGain> jExtGain1;
  
  private final JComboBox<Tek2440_GPIB_Settings.ExtGain> jExtGain2;
  
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
      if (instrument != JTek2440_GPIB_ExtGain.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_ExtGain.this.setInhibitInstrumentControl ();
        try
        {
          if (JTek2440_GPIB_ExtGain.this.jExtGain1 != null)
          {
            JTek2440_GPIB_ExtGain.this.jExtGain1.setSelectedItem (settings.getExtGain1 ());
            JTek2440_GPIB_ExtGain.this.jExtGain1.setBackground (JTek2440_GPIB_ExtGain.this.getBackground ());
          } 
          if (JTek2440_GPIB_ExtGain.this.jExtGain2 != null)
          {
            JTek2440_GPIB_ExtGain.this.jExtGain2.setSelectedItem (settings.getExtGain2 ());
            JTek2440_GPIB_ExtGain.this.jExtGain2.setBackground (JTek2440_GPIB_ExtGain.this.getBackground ());
          }
        }
        finally
        {
          JTek2440_GPIB_ExtGain.this.resetInhibitInstrumentControl ();
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
