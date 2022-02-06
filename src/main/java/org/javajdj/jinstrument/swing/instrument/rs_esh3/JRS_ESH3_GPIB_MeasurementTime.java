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
package org.javajdj.jinstrument.swing.instrument.rs_esh3;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SelectiveLevelMeter;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.setPanelBorder;
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;
import org.javajdj.jinstrument.swing.dialog.JInstrumentDialog;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A Swing panel for the Measurement Time setting of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_MeasurementTime
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_MeasurementTime.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_MeasurementTime (
    final SelectiveLevelMeter selectiveLevelMeter,
    final String title,
    final int level,
    final Color panelColor)
  {

    super (selectiveLevelMeter, title, level, panelColor);
    if (! (selectiveLevelMeter instanceof RS_ESH3_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final RS_ESH3_GPIB_Instrument rs_esh3 = (RS_ESH3_GPIB_Instrument) selectiveLevelMeter;

    removeAll ();
    setLayout (new GridLayout (3, 1));
    
    this.jTime = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesTimeColor (), false, 7, 3);
    this.jTime.addMouseListener (this.jTimeMouseListener);
    add (this.jTime);
    this.jTimeSlider_s = new JSlider (0, 1000, 0);
    this.jTimeSlider_s.setToolTipText ("-");
    this.jTimeSlider_s.setMajorTickSpacing (1000);
    this.jTimeSlider_s.setMinorTickSpacing (100);
    this.jTimeSlider_s.setPaintTicks (true);
    this.jTimeSlider_s.setPaintLabels (true);
    this.jTimeSlider_s.addChangeListener (this.jTimeSlider_sChangeListener);
    setPanelBorder (this.jTimeSlider_s,
      getLevel () + 2,
      getGuiPreferencesTimeColor (),
      "[s]");
    this.jTimeSlider_ms = new JSlider (0, 1000, 0);
    add (this.jTimeSlider_s);
    this.jTimeSlider_ms.setToolTipText ("-");
    this.jTimeSlider_ms.setMajorTickSpacing (1000);
    this.jTimeSlider_ms.setMinorTickSpacing (100);
    this.jTimeSlider_ms.setPaintTicks (true);
    this.jTimeSlider_ms.setPaintLabels (true);
    this.jTimeSlider_ms.addChangeListener (this.jTimeSlider_msChangeListener);
    setPanelBorder (this.jTimeSlider_ms,
      getLevel () + 2,
      getGuiPreferencesTimeColor (),
      "[ms]");
    add (this.jTimeSlider_ms);
    
    getSelectiveLevelMeter ().addInstrumentListener (this.instrumentListener);

  }

  public JRS_ESH3_GPIB_MeasurementTime (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_MeasurementTime (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
  {
    this (selectiveLevelMeter, 0);
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
      return "R&S ESH-3 [GPIB] Measurement Time Setting";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_MeasurementTime ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_MeasurementTime.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSevenSegmentNumber jTime;
  
  private final MouseListener jTimeMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newTime_s = JInstrumentDialog.getPeriodFromDialog_s ("Enter Measurement Time [s]", null);
      if (newTime_s != null)
        try
        {
          ((RS_ESH3_GPIB_Instrument) JRS_ESH3_GPIB_MeasurementTime.this.getSelectiveLevelMeter ()).setMeasurementTime_s (
            newTime_s);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting measurement time on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting measurement time on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final JSlider jTimeSlider_s;
  
  private final ChangeListener jTimeSlider_sChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JRS_ESH3_GPIB_MeasurementTime.this.inhibitInstrumentControl)
        try
        {
          ((RS_ESH3_GPIB_Instrument) JRS_ESH3_GPIB_MeasurementTime.this.getSelectiveLevelMeter ()).setMeasurementTime_s (
            source.getValue ()
            + 1.0e-3 * Math.min (JRS_ESH3_GPIB_MeasurementTime.this.jTimeSlider_ms.getValue (), 999));
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting meaqsurement time from slider to {0} s: {1}.",
            new Object[]{source.getValue (), e});          
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
    
  private final JSlider jTimeSlider_ms;
  
  private final ChangeListener jTimeSlider_msChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JRS_ESH3_GPIB_MeasurementTime.this.inhibitInstrumentControl)
        try
        {
          ((RS_ESH3_GPIB_Instrument) JRS_ESH3_GPIB_MeasurementTime.this.getSelectiveLevelMeter ()).setMeasurementTime_s (
              1.0 * JRS_ESH3_GPIB_MeasurementTime.this.jTimeSlider_s.getValue ()
              + 1.0e-3 * Math.min (source.getValue (), 999));
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting measurement time from slider to {0} ms: {1}.",
            new Object[]{source.getValue (), e});          
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
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
      if (instrument != JRS_ESH3_GPIB_MeasurementTime.this.getSelectiveLevelMeter ()|| instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof RS_ESH3_GPIB_Settings))
        throw new IllegalArgumentException ();
      final RS_ESH3_GPIB_Settings settings = (RS_ESH3_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JRS_ESH3_GPIB_MeasurementTime.this.inhibitInstrumentControl = true;
        //
        try
        {
          final double rem_s = settings.getMeasurementTime_s ();
          final int reading_s = (int) Math.floor (rem_s);
          final double rem_ms = rem_s - reading_s;
          final int reading_ms = (int) Math.floor (rem_ms * 1.0e3);
          JRS_ESH3_GPIB_MeasurementTime.this.jTime.setNumber (reading_s + 1.0e-3 * reading_ms);
          JRS_ESH3_GPIB_MeasurementTime.this.jTimeSlider_s.setValue (reading_s);
          JRS_ESH3_GPIB_MeasurementTime.this.jTimeSlider_s.setToolTipText (Integer.toString (reading_s));
          JRS_ESH3_GPIB_MeasurementTime.this.jTimeSlider_ms.setValue (Math.min (reading_ms, 999));
          JRS_ESH3_GPIB_MeasurementTime.this.jTimeSlider_ms.setToolTipText (Integer.toString (Math.min (reading_ms, 999)));
        }
        finally
        {
          JRS_ESH3_GPIB_MeasurementTime.this.inhibitInstrumentControl = false;
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
