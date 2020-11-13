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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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

/** A Swing panel for the Horizontal settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Horizontal
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Horizontal.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Horizontal (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final String title,
    final int level,
    final Color panelColor)
  {
    
    super (digitalStorageOscilloscope, title, level, panelColor);
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;
    
    setLayout (new GridLayout (5, 2, 0, 10));
    
    add (new JLabel ("Mode"));
    this.jMode = new JComboBox<> (Tek2440_GPIB_Settings.HorizontalMode.values ());
    this.jMode.setSelectedItem (null);
    this.jMode.setEditable (false);
    this.jMode.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "horizontal mode",
      Tek2440_GPIB_Settings.HorizontalMode.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.HorizontalMode>) tek2440::setHorizontalMode,
      this::isInhibitInstrumentControl));
    add (this.jMode);
    
    add (new JLabel ("A Timebase"));
    this.jChATimebase = new JComboBox<> (Tek2440_GPIB_Settings.SecondsPerDivision.values ());
    this.jChATimebase.setSelectedItem (null);
    this.jChATimebase.setEditable (false);
    this.jChATimebase.addItemListener (this.jChTimebaseListener);
    add (this.jChATimebase);
    
    add (new JLabel ("B Timebase"));
    this.jChBTimebase = new JComboBox<> (Tek2440_GPIB_Settings.SecondsPerDivision.values ());
    this.jChBTimebase.setSelectedItem (null);
    this.jChBTimebase.setEditable (false);
    this.jChBTimebase.addItemListener (this.jChTimebaseListener);
    add (this.jChBTimebase);
    
    add (new JLabel ("Position"));
    this.jPosition = new JSlider (0, 1023);
    this.jPosition.setMajorTickSpacing (1023);
    this.jPosition.setMinorTickSpacing (128);
    this.jPosition.setPaintTicks (true);
    this.jPosition.setToolTipText ("-");
    this.jPosition.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "horizontal position",
      tek2440::setHorizontalPosition,
      this::isInhibitInstrumentControl));
    add (this.jPosition);
    
    add (new JLabel ("Hor Ext Expansion"));
    this.jExtExp = new JComboBox (Tek2440_GPIB_Settings.HorizontalExternalExpansionFactor.values ());
    this.jExtExp.setSelectedItem (null);
    this.jExtExp.setEditable (false);
    this.jExtExp.addItemListener (new JInstrumentComboBoxItemListener_Enum<> (
      "horizontal external expansion factor",
      Tek2440_GPIB_Settings.HorizontalExternalExpansionFactor.class,
      (Instrument.InstrumentSetter_1Enum<Tek2440_GPIB_Settings.HorizontalExternalExpansionFactor>)
        tek2440::setHorizontalExternalExpansion,
      this::isInhibitInstrumentControl));
    add (this.jExtExp);
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }

  public JTek2440_GPIB_Horizontal (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Horizontal (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Horizontal Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Horizontal ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Horizontal.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JComboBox<Tek2440_GPIB_Settings.HorizontalMode> jMode;

  private final JComboBox<Tek2440_GPIB_Settings.SecondsPerDivision> jChATimebase;

  private final JComboBox<Tek2440_GPIB_Settings.SecondsPerDivision> jChBTimebase;

  private final JSlider jPosition;

  private final JComboBox<Tek2440_GPIB_Settings.HorizontalExternalExpansionFactor> jExtExp;
    
  private final ItemListener jChTimebaseListener = (ItemEvent ie) ->
  {
    if (ie.getStateChange () == ItemEvent.SELECTED)
    {
      final Tek2440_GPIB_Settings.SecondsPerDivision newValue = (Tek2440_GPIB_Settings.SecondsPerDivision) ie.getItem ();
      final Tek2440_GPIB_Instrument tek2440 = ((Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ());
      if (! JTek2440_GPIB_Horizontal.this.isInhibitInstrumentControl ())
        try
        {
          if (ie.getSource () == JTek2440_GPIB_Horizontal.this.jChATimebase)
            tek2440.setChannelTimebase (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, newValue);
          else if (ie.getSource () == JTek2440_GPIB_Horizontal.this.jChBTimebase)
            tek2440.setChannelTimebase (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, newValue);
          else
            LOG.log (Level.SEVERE, "Unexpected event source!");            
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting Secs/Div on instrument"
            + " from combo box to {0}: {1}.",
            new Object[]{newValue, Arrays.toString (e.getStackTrace ())});
        }
    }
  };
  
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
      if (instrument != JTek2440_GPIB_Horizontal.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_Horizontal.this.setInhibitInstrumentControl ();
        try
        {
          JTek2440_GPIB_Horizontal.this.jMode.setSelectedItem (settings.getHorizontalMode ());
          JTek2440_GPIB_Horizontal.this.jChATimebase.setSelectedItem (settings.getASecondsPerDivision ());
          JTek2440_GPIB_Horizontal.this.jChBTimebase.setSelectedItem (settings.getBSecondsPerDivision ());
          final int roundedXPosition = (int) Math.round (settings.getHorizontalPosition ());
          JTek2440_GPIB_Horizontal.this.jPosition.setValue (roundedXPosition);
          JTek2440_GPIB_Horizontal.this.jPosition.setToolTipText (Integer.toString (roundedXPosition));
          JTek2440_GPIB_Horizontal.this.jExtExp.setSelectedItem (settings.getHorizontalExternalExpansionFactor ());
        }
        finally
        {
          JTek2440_GPIB_Horizontal.this.resetInhibitInstrumentControl ();
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
