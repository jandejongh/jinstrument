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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jswing.jcenter.JCenter;

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
    
    super (digitalStorageOscilloscope, title, level, panelColor);
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;
    
    removeAll ();
    setLayout (new GridLayout (1, 2, 10, 0));
    
    final JPanel leftPanel = new JPanel ();
    leftPanel.setLayout (new GridLayout (5, 1, 0, 10));
    add (leftPanel);
    final JPanel rightPanel = new JPanel ();
    rightPanel.setLayout (new GridLayout (5, 1, 0, 10));
    add (rightPanel);
    
    leftPanel.add (new JLabel ("Mode"));
    leftPanel.add (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.BTriggerMode.class,
      "B trigger mode",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getBTriggerMode (),
      tek2440::setBTriggerMode,
      true));
    
    leftPanel.add (new JLabel ("Source"));
    leftPanel.add (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.BTriggerSource.class,
      "B trigger source",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getBTriggerSource (),
      tek2440::setBTriggerSource,
      true));
    
    leftPanel.add (new JLabel ("Coupling"));
    leftPanel.add (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.BTriggerCoupling.class,
      "B trigger coupling",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getBTriggerCoupling (),
      tek2440::setBTriggerCoupling,
      true));
    
    leftPanel.add (new JLabel ("Slope"));
    leftPanel.add (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.Slope.class,
      "B trigger slope",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getBTriggerSlope (),
      tek2440::setBTriggerSlope,
      true));
    
    leftPanel.add (new JLabel ("Level [V]"));
    leftPanel.add (new JDouble_JSlider (
      SwingConstants.HORIZONTAL,
      -200.0, // XXX Arbitrary!
      200.0,  // XXX Arbitrary!
      0.0,    // XXX Arbitrary!
      "B trigger level",
      (settings) -> ((Tek2440_GPIB_Settings) settings).getBTriggerLevel (),
      tek2440::setBTriggerLevel,
      true));
    
    rightPanel.add (new JLabel ("Position"));
    rightPanel.add (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.BTriggerPosition.class,
      "B trigger position",
      (final InstrumentSettings settings) -> ((Tek2440_GPIB_Settings) settings).getBTriggerPosition (),
      tek2440::setBTriggerPosition,
      true));
    
    rightPanel.add (new JLabel ("Ext Clk"));
    rightPanel.add (JCenter.Y (new JBoolean_JBoolean (
      "B trigger external clock",
      (settings) -> ((Tek2440_GPIB_Settings) settings).isBTriggerExtClock (),
      tek2440::setBTriggerExternalClock,
      Color.green,
      true)));
    
    rightPanel.add (new JLabel ());
    rightPanel.add (new JLabel ());
    rightPanel.add (new JLabel ());
    rightPanel.add (new JLabel ());
    rightPanel.add (new JLabel ());
    rightPanel.add (new JLabel ());
    
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
