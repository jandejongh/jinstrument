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
import javax.swing.JPanel;
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
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Reading;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesFrequencyColor;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.setPanelBorder;
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A Swing panel for the (Center) Frequency settings of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_Frequency
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_Frequency.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_Frequency (
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
    setLayout (new GridLayout (5, 1));
    
    this.jFreq = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 6, 1);
    this.jFreq.addMouseListener (this.jFreqMouseListener);
    add (this.jFreq);
    this.jFreqSlider_MHz = new JSlider (0, (int) Math.ceil (getSelectiveLevelMeter ().getMaxFrequency_Hz () / 1.0e6), 0);
    this.jFreqSlider_kHz = new JSlider (0, 1000, 0);
    this.jFreqSlider_Hz = new JSlider (0, 1000, 0);
    this.jFreqSlider_MHz.setToolTipText ("-");
    this.jFreqSlider_MHz.setMajorTickSpacing (10);
    this.jFreqSlider_MHz.setMinorTickSpacing (1);
    this.jFreqSlider_MHz.setPaintTicks (true);
    this.jFreqSlider_MHz.setPaintLabels (true);
    this.jFreqSlider_MHz.addChangeListener (this.jFreqSlider_MHzChangeListener);
    setPanelBorder (
      this.jFreqSlider_MHz,
      getLevel () + 2,
      getGuiPreferencesFrequencyColor (),
      "[MHz]");
    add (this.jFreqSlider_MHz);
    this.jFreqSlider_kHz.setToolTipText ("-");
    this.jFreqSlider_kHz.setMajorTickSpacing (100);
    // this.jFreqSlider_kHz.setMinorTickSpacing (10);
    this.jFreqSlider_kHz.setPaintTicks (true);
    this.jFreqSlider_kHz.addChangeListener (this.jFreqSlider_kHzChangeListener);
    setPanelBorder (
      this.jFreqSlider_kHz,
      getLevel () + 2,
      getGuiPreferencesFrequencyColor (),
      "[kHz]");
    add (this.jFreqSlider_kHz);
    this.jFreqSlider_Hz.setToolTipText ("-");
    this.jFreqSlider_Hz.setMajorTickSpacing (100);
    // this.jFreqSlider_Hz.setMinorTickSpacing (10);
    this.jFreqSlider_Hz.setPaintTicks (true);
    // this.jFreqSlider_Hz.setPaintLabels (true);    
    this.jFreqSlider_Hz.addChangeListener (this.jFreqSlider_HzChangeListener);
    setPanelBorder (
      this.jFreqSlider_Hz,
      getLevel () + 2,
      getGuiPreferencesFrequencyColor (),
      "[Hz]");
    add (this.jFreqSlider_Hz);
    
    final JPanel freqStartStopPanel = new JInstrumentPanel (rs_esh3, null, level + 1, null);
    add (freqStartStopPanel);
    freqStartStopPanel.setLayout (new GridLayout (1, 2));
    freqStartStopPanel.add (
      new JRS_ESH3_GPIB_Frequency_Start (selectiveLevelMeter, "Start [MHz]", level + 2, getGuiPreferencesFrequencyColor ()));
    freqStartStopPanel.add (
      new JRS_ESH3_GPIB_Frequency_Stop (selectiveLevelMeter, "Stop [MHz]", level + 2, getGuiPreferencesFrequencyColor ()));
    
    getSelectiveLevelMeter ().addInstrumentListener (this.instrumentListener);

  }

  public JRS_ESH3_GPIB_Frequency (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_Frequency (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
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
      return "R&S ESH-3 [GPIB] Frequency Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_Frequency ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_Frequency.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JSevenSegmentNumber jFreq;
  
  private final MouseListener jFreqMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newFrequency_Hz = getFrequencyFromDialog_Hz ("Enter Frequency [Hz]", null);
      if (newFrequency_Hz != null)
        try
        {
          JRS_ESH3_GPIB_Frequency.this.getSelectiveLevelMeter ().setFrequency_Hz (newFrequency_Hz);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting frequency on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting frequency on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final JSlider jFreqSlider_MHz;
  
  private final ChangeListener jFreqSlider_MHzChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JRS_ESH3_GPIB_Frequency.this.inhibitInstrumentControl)
        try
        {
          JRS_ESH3_GPIB_Frequency.this.getSelectiveLevelMeter ().setFrequency_Hz (
              1.0e6 * source.getValue ()
            + 1.0e3 * JRS_ESH3_GPIB_Frequency.this.jFreqSlider_kHz.getValue ()
            + 1.0 * JRS_ESH3_GPIB_Frequency.this.jFreqSlider_Hz.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} MHz: {1}.",
            new Object[]{source.getValue (), e});          
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
    
  private final JSlider jFreqSlider_kHz;
  
  private final ChangeListener jFreqSlider_kHzChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JRS_ESH3_GPIB_Frequency.this.inhibitInstrumentControl)
        try
        {
          JRS_ESH3_GPIB_Frequency.this.getSelectiveLevelMeter ().setFrequency_Hz (
              + 1.0e6 * JRS_ESH3_GPIB_Frequency.this.jFreqSlider_MHz.getValue ()
              + 1.0e3 * Math.min (source.getValue (), 999)
              + 1.0 * Math.min (JRS_ESH3_GPIB_Frequency.this.jFreqSlider_Hz.getValue (), 999));
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} kHz: {1}.",
            new Object[]{source.getValue (), e});          
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
  
  private final JSlider jFreqSlider_Hz;
  
  private final ChangeListener jFreqSlider_HzChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JRS_ESH3_GPIB_Frequency.this.inhibitInstrumentControl)
        try
        {
          JRS_ESH3_GPIB_Frequency.this.getSelectiveLevelMeter ().setFrequency_Hz (
                1.0e6 * JRS_ESH3_GPIB_Frequency.this.jFreqSlider_MHz.getValue ()
              + 1.0e3 * Math.min (JRS_ESH3_GPIB_Frequency.this.jFreqSlider_kHz.getValue (), 999)
              + 1.0 * Math.min (source.getValue (), 999));
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} Hz: {1}.",
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
      if (instrument != JRS_ESH3_GPIB_Frequency.this.getSelectiveLevelMeter () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof RS_ESH3_GPIB_Settings))
        throw new IllegalArgumentException ();
      final RS_ESH3_GPIB_Settings settings = (RS_ESH3_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        setInhibitInstrumentControl ();
        //
        try
        {
          final double rem_MHz = settings.getFrequency_Hz ();
          final int reading_MHz = (int) Math.floor (rem_MHz / 1.0e6);
          final double rem_kHz = rem_MHz - (reading_MHz * 1.0e6);
          final int reading_kHz = (int) Math.floor (rem_kHz / 1.0e3);
          final double rem_Hz = rem_kHz - (reading_kHz * 1.0e3);
          final int reading_Hz = 100 * (int) Math.floor (rem_Hz / 100.0);
          JRS_ESH3_GPIB_Frequency.this.jFreq.setNumber (reading_MHz + 1.0e-3 * reading_kHz + 1.0e-6 * reading_Hz);
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_MHz.setValue (reading_MHz);
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_MHz.setToolTipText (Integer.toString (reading_MHz));
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_kHz.setValue (reading_kHz);
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_kHz.setToolTipText (Integer.toString (reading_kHz));
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_Hz.setValue (reading_Hz);
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_Hz.setToolTipText (Integer.toString (reading_Hz));
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
      if (instrument != JRS_ESH3_GPIB_Frequency.this.getSelectiveLevelMeter () || instrumentReading == null)
        throw new IllegalArgumentException ();
      if (! (instrumentReading instanceof RS_ESH3_GPIB_Reading))
        throw new IllegalArgumentException ();
      final RS_ESH3_GPIB_Reading reading = (RS_ESH3_GPIB_Reading) instrumentReading;
      if (reading.getReadingType () != RS_ESH3_GPIB_Reading.ReadingType.Frequency_MHz)
        return;
      // We need the frequency in Hz below; we are mimicking the newInstrumentSettings implementation above.
      final double frequency_Hz = 1.0e6 * reading.getFrequency_MHz ();
      SwingUtilities.invokeLater (() ->
      {
        setInhibitInstrumentControl ();
        //
        try
        {
          final double rem_MHz = frequency_Hz;
          final int reading_MHz = (int) Math.floor (rem_MHz / 1.0e6);
          final double rem_kHz = rem_MHz - (reading_MHz * 1.0e6);
          final int reading_kHz = (int) Math.floor (rem_kHz / 1.0e3);
          final double rem_Hz = rem_kHz - (reading_kHz * 1.0e3);
          final int reading_Hz = 100 * (int) Math.floor (rem_Hz / 100.0);
          JRS_ESH3_GPIB_Frequency.this.jFreq.setNumber (reading_MHz + 1.0e-3 * reading_kHz + 1.0e-6 * reading_Hz);
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_MHz.setValue (reading_MHz);
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_MHz.setToolTipText (Integer.toString (reading_MHz));
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_kHz.setValue (reading_kHz);
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_kHz.setToolTipText (Integer.toString (reading_kHz));
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_Hz.setValue (reading_Hz);
          JRS_ESH3_GPIB_Frequency.this.jFreqSlider_Hz.setToolTipText (Integer.toString (reading_Hz));
        }
        finally
        {
          resetInhibitInstrumentControl ();
        }
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
