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
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
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
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for the Delay settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Delay
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Delay.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Delay (
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
    setLayout (new GridLayout (2, 1, 0, 4));
        
    final JPanel delayEventsPanel = new JPanel ();
    delayEventsPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Delay Events"));
    add (delayEventsPanel);

    final JPanel delayTimesPanel = new JPanel ();
    delayTimesPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Delay Times"));
    add (delayTimesPanel);
    
    delayEventsPanel.setLayout (new GridLayout (2, 2));
    delayEventsPanel.add (new JLabel ("Mode/Enable"));
    this.jDelayEventsMode = new JColorCheckBox.JBoolean (Color.green);
    this.jDelayEventsMode.addActionListener (new JInstrumentActionListener_1Boolean (
      "events delay mode",
      this.jDelayEventsMode::getDisplayedValue,
      tek2440::setDelayEventsMode,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    delayEventsPanel.add (this.jDelayEventsMode);
    delayEventsPanel.add (new JLabel ("Delay [events]"));
    this.jDelayEvents = new JSlider (1, 65536);
    this.jDelayEvents.setMinorTickSpacing (1024);
    this.jDelayEvents.setMajorTickSpacing (65535);
    this.jDelayEvents.setPaintTicks (true);
    this.jDelayEvents.setPaintLabels (true);
    this.jDelayEvents.setToolTipText ("-");
    this.jDelayEvents.addChangeListener (new JInstrumentSliderChangeListener_1Integer (
      "delay [events]",
      tek2440::setDelayEvents,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    delayEventsPanel.add (this.jDelayEvents);
    
    delayTimesPanel.setLayout (new GridLayout (6, 4));
    delayTimesPanel.add (new JLabel ("Delta"));
    this.jDelta = new JColorCheckBox.JBoolean (Color.green);
    this.jDelta.addActionListener (new JInstrumentActionListener_1Boolean (
      "delay time delta",
      this.jDelta::getDisplayedValue,
      tek2440::setDelayTimesDelta,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    delayTimesPanel.add (this.jDelta);
    delayTimesPanel.add (new JLabel ());
    delayTimesPanel.add (new JLabel ());
    
    this.jDelay1_s = new JSlider (0, 100);
    this.jDelay1_s.setMajorTickSpacing (100);
    this.jDelay1_s.setMinorTickSpacing (10);
    this.jDelay1_s.setPaintTicks (true);
    this.jDelay1_s.setPaintLabels (true);
    this.jDelay1_s.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "delay 1 [s]",
      this::setDelay1,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    this.jDelay1_ms = new JSlider (0, 999);
    this.jDelay1_ms.setMajorTickSpacing (999);
    this.jDelay1_ms.setMinorTickSpacing (100);
    this.jDelay1_ms.setPaintTicks (true);
    this.jDelay1_ms.setPaintLabels (true);
    this.jDelay1_ms.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "delay 1 [ms]",
      this::setDelay1,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    this.jDelay1_mus = new JSlider (0, 999);
    this.jDelay1_mus.setMajorTickSpacing (999);
    this.jDelay1_mus.setMinorTickSpacing (100);
    this.jDelay1_mus.setPaintTicks (true);
    this.jDelay1_mus.setPaintLabels (true);
    this.jDelay1_mus.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "delay 1 [\u03BCs]",
      this::setDelay1,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    this.jDelay1_ns = new JSlider (0, 999);
    this.jDelay1_ns.setMajorTickSpacing (999);
    this.jDelay1_ns.setMinorTickSpacing (100);
    this.jDelay1_ns.setPaintTicks (true);
    this.jDelay1_ns.setPaintLabels (true);
    this.jDelay1_ns.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "delay 1 [ns]",
      this::setDelay1,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    
    this.jDelay2_s = new JSlider (0, 100);
    this.jDelay2_s.setMajorTickSpacing (100);
    this.jDelay2_s.setMinorTickSpacing (10);
    this.jDelay2_s.setPaintTicks (true);
    this.jDelay2_s.setPaintLabels (true);
    this.jDelay2_s.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "delay 2 [s]",
      this::setDelay2,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    this.jDelay2_ms = new JSlider (0, 999);
    this.jDelay2_ms.setMajorTickSpacing (999);
    this.jDelay2_ms.setMinorTickSpacing (100);
    this.jDelay2_ms.setPaintTicks (true);
    this.jDelay2_ms.setPaintLabels (true);
    this.jDelay2_ms.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "delay 2 [ms]",
      this::setDelay2,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    this.jDelay2_mus = new JSlider (0, 999);
    this.jDelay2_mus.setMajorTickSpacing (999);
    this.jDelay2_mus.setMinorTickSpacing (100);
    this.jDelay2_mus.setPaintTicks (true);
    this.jDelay2_mus.setPaintLabels (true);
    this.jDelay2_mus.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "delay 2 [\u03BCs]",
      this::setDelay2,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    this.jDelay2_ns = new JSlider (0, 999);
    this.jDelay2_ns.setMajorTickSpacing (999);
    this.jDelay2_ns.setMinorTickSpacing (100);
    this.jDelay2_ns.setPaintTicks (true);
    this.jDelay2_ns.setPaintLabels (true);
    this.jDelay2_ns.addChangeListener (new JInstrumentSliderChangeListener_1Double (
      "delay 2 [ns]",
      this::setDelay2,
      JTek2440_GPIB_Delay.this::isInhibitInstrumentControl));
    
    delayTimesPanel.add (new JLabel ("Delay 1"));
    delayTimesPanel.add (new JLabel ());
    delayTimesPanel.add (new JLabel ("Delay 2"));
    delayTimesPanel.add (new JLabel ());
    
    delayTimesPanel.add (this.jDelay1_s);
    delayTimesPanel.add (new JLabel ("s"));
    delayTimesPanel.add (this.jDelay2_s);
    delayTimesPanel.add (new JLabel ("s"));
    
    delayTimesPanel.add (this.jDelay1_ms);
    delayTimesPanel.add (new JLabel ("ms"));
    delayTimesPanel.add (this.jDelay2_ms);
    delayTimesPanel.add (new JLabel ("ms"));
    
    delayTimesPanel.add (this.jDelay1_mus);
    delayTimesPanel.add (new JLabel ("\u03BCs"));
    delayTimesPanel.add (this.jDelay2_mus);
    delayTimesPanel.add (new JLabel ("\u03BCs"));
    
    delayTimesPanel.add (this.jDelay1_ns);
    delayTimesPanel.add (new JLabel ("ns"));
    delayTimesPanel.add (this.jDelay2_ns);
    delayTimesPanel.add (new JLabel ("ns"));
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }

  public JTek2440_GPIB_Delay (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Delay (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Delay Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Delay ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Delay.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JColorCheckBox.JBoolean jDelayEventsMode;
  
  private final JSlider jDelayEvents;
  
  private final JColorCheckBox.JBoolean jDelta;
  
  private final JSlider jDelay1_s;
  
  private final JSlider jDelay1_ms;
  
  private final JSlider jDelay1_mus;
  
  private final JSlider jDelay1_ns;
  
  private final JSlider jDelay2_s;
  
  private final JSlider jDelay2_ms;
  
  private final JSlider jDelay2_mus;
  
  private final JSlider jDelay2_ns;
  
  private void setDelay1 (final double dummyReading)
    throws InterruptedException, IOException
  {
    final double delay1_s = this.jDelay1_s.getValue () +
      1e-3 * this.jDelay1_ms.getValue () +
      1e-6 * this.jDelay1_mus.getValue () +
      1e-9 * this.jDelay1_ns.getValue ();
    ((Tek2440_GPIB_Instrument) getInstrument ()).setDelayTime1 (delay1_s);
  }
  
  private void setDelay2 (final double dummyReading)
    throws InterruptedException, IOException
  {
    final double delay2_s = this.jDelay2_s.getValue () +
      1e-3 * this.jDelay2_ms.getValue () +
      1e-6 * this.jDelay2_mus.getValue () +
      1e-9 * this.jDelay2_ns.getValue ();
    ((Tek2440_GPIB_Instrument) getInstrument ()).setDelayTime2 (delay2_s);
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
      if (instrument != JTek2440_GPIB_Delay.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_Delay.this.setInhibitInstrumentControl ();
        try
        {
          JTek2440_GPIB_Delay.this.jDelayEventsMode.setDisplayedValue (settings.isDelayEventsEnabled ());
          JTek2440_GPIB_Delay.this.jDelayEvents.setValue (settings.getDelayEventsValue ());
          JTek2440_GPIB_Delay.this.jDelayEvents.setToolTipText (Integer.toString (settings.getDelayEventsValue ()));
          JTek2440_GPIB_Delay.this.jDelta.setDisplayedValue (settings.isDelayTimeDelta ());
          
          final double delayTime1_double_s = settings.getDelayTime1_s ();
          final int delayTime1_int_s = (int) Math.floor (delayTime1_double_s);
          JTek2440_GPIB_Delay.this.jDelay1_s.setValue (delayTime1_int_s);
          JTek2440_GPIB_Delay.this.jDelay1_s.setToolTipText (Double.toString (delayTime1_double_s) + "s");
          
          final double delayTime1_double_rem_s = delayTime1_double_s - delayTime1_int_s;
          final double delayTime1_double_rem_s_ms = 1000 * delayTime1_double_rem_s;
          final int delayTime1_int_ms = (int) Math.floor (delayTime1_double_rem_s_ms);
          JTek2440_GPIB_Delay.this.jDelay1_ms.setValue (delayTime1_int_ms);
          JTek2440_GPIB_Delay.this.jDelay1_ms.setToolTipText (Double.toString (delayTime1_double_rem_s_ms) + "ms");
          
          final double delayTime1_double_rem_ms = delayTime1_double_rem_s_ms - delayTime1_int_ms;
          final double delayTime1_double_rem_ms_mus = 1000 * delayTime1_double_rem_ms;
          final int delayTime1_int_mus = (int) Math.floor (delayTime1_double_rem_ms_mus);
          JTek2440_GPIB_Delay.this.jDelay1_mus.setValue (delayTime1_int_mus);
          JTek2440_GPIB_Delay.this.jDelay1_mus.setToolTipText (Double.toString (delayTime1_double_rem_ms_mus) + "\u03BCs");
          
          final double delayTime1_double_rem_mus = delayTime1_double_rem_ms_mus - delayTime1_int_mus;
          final double delayTime1_double_rem_mus_ns = 1000 * delayTime1_double_rem_mus;
          final int delayTime1_int_ns = (int) Math.floor (delayTime1_double_rem_mus_ns);
          JTek2440_GPIB_Delay.this.jDelay1_ns.setValue (delayTime1_int_ns);
          JTek2440_GPIB_Delay.this.jDelay1_ns.setToolTipText (Double.toString (delayTime1_double_rem_mus_ns) + "ns");
          
          final double delayTime2_double_s = settings.getDelayTime2_s ();
          final int delayTime2_int_s = (int) Math.floor (delayTime2_double_s);
          JTek2440_GPIB_Delay.this.jDelay2_s.setValue (delayTime2_int_s);
          JTek2440_GPIB_Delay.this.jDelay2_s.setToolTipText (Double.toString (delayTime2_double_s) + "s");
          
          final double delayTime2_double_rem_s = delayTime2_double_s - delayTime2_int_s;
          final double delayTime2_double_rem_s_ms = 1000 * delayTime2_double_rem_s;
          final int delayTime2_int_ms = (int) Math.floor (delayTime2_double_rem_s_ms);
          JTek2440_GPIB_Delay.this.jDelay2_ms.setValue (delayTime2_int_ms);
          JTek2440_GPIB_Delay.this.jDelay2_ms.setToolTipText (Double.toString (delayTime2_double_rem_s_ms) + "ms");
          
          final double delayTime2_double_rem_ms = delayTime2_double_rem_s_ms - delayTime2_int_ms;
          final double delayTime2_double_rem_ms_mus = 1000 * delayTime2_double_rem_ms;
          final int delayTime2_int_mus = (int) Math.floor (delayTime2_double_rem_ms_mus);
          JTek2440_GPIB_Delay.this.jDelay2_mus.setValue (delayTime2_int_mus);
          JTek2440_GPIB_Delay.this.jDelay2_mus.setToolTipText (Double.toString (delayTime2_double_rem_ms_mus) + "\u03BCs");
          
          final double delayTime2_double_rem_mus = delayTime2_double_rem_ms_mus - delayTime2_int_mus;
          final double delayTime2_double_rem_mus_ns = 1000 * delayTime2_double_rem_mus;
          final int delayTime2_int_ns = (int) Math.floor (delayTime2_double_rem_mus_ns);
          JTek2440_GPIB_Delay.this.jDelay2_ns.setValue (delayTime2_int_ns);
          JTek2440_GPIB_Delay.this.jDelay2_ns.setToolTipText (Double.toString (delayTime2_double_rem_mus_ns) + "ns");
          
        }
        finally
        {
          JTek2440_GPIB_Delay.this.resetInhibitInstrumentControl ();
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
