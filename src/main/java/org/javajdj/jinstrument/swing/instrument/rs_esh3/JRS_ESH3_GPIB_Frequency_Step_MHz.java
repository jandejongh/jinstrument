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
import javax.swing.SwingUtilities;
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
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;
import org.javajdj.jinstrument.swing.dialog.JInstrumentDialog;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A Swing panel for the Frequency Step [MHz] setting of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_Frequency_Step_MHz
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_Frequency_Step_MHz.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_Frequency_Step_MHz (
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
    setLayout (new GridLayout (1, 1));
    
    this.jFreqStep_MHz = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 6, 1);
    this.jFreqStep_MHz.addMouseListener (this.jFreqStep_MHzMouseListener);
    add (this.jFreqStep_MHz);
    
    getSelectiveLevelMeter ().addInstrumentListener (this.instrumentListener);

  }

  public JRS_ESH3_GPIB_Frequency_Step_MHz (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_Frequency_Step_MHz (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
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
      return "R&S ESH-3 [GPIB] Frequency Step [MHz] Setting";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_Frequency_Step_MHz ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_Frequency_Step_MHz.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JSevenSegmentNumber jFreqStep_MHz;
  
  private final MouseListener jFreqStep_MHzMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newFrequencyStep_MHz = JInstrumentDialog.getFrequencyFromDialog_MHz ("Enter Frequency Step [MHz]", null);
      if (newFrequencyStep_MHz != null)
        try
        {
          // XXX Round to 100 Hz here?
          ((RS_ESH3_GPIB_Instrument) JRS_ESH3_GPIB_Frequency_Step_MHz.this.getSelectiveLevelMeter ()).setStepSize_MHz
            (newFrequencyStep_MHz);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting frequency step [MHz] on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting frequency step [MHz] on instrument: {0}.",
             ioe.getStackTrace ());
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
      if (instrument != JRS_ESH3_GPIB_Frequency_Step_MHz.this.getSelectiveLevelMeter ()|| instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof RS_ESH3_GPIB_Settings))
        throw new IllegalArgumentException ();
      final RS_ESH3_GPIB_Settings settings = (RS_ESH3_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JRS_ESH3_GPIB_Frequency_Step_MHz.this.inhibitInstrumentControl = true;
        //
        try
        {
          if (settings.getStepSizeMode () == RS_ESH3_GPIB_Settings.StepSizeMode.Linear)
          {
            final double rem_MHz = settings.getStepSize_MHz ();
            final int reading_MHz = (int) Math.floor (rem_MHz);
            final double rem_kHz = rem_MHz - reading_MHz;
            final int reading_kHz = (int) Math.floor (rem_kHz * 1.0e3);
            final double rem_Hz = rem_kHz - (reading_kHz / 1.0e3);
            // XXX Round to 100 Hz here?
            final int reading_Hz = (int) Math.floor (rem_Hz * 1.0e6);
            JRS_ESH3_GPIB_Frequency_Step_MHz.this.jFreqStep_MHz.setNumber (
              reading_MHz
                + 1.0e-3 * reading_kHz
                + 1.0e-6 * reading_Hz);
          }
          else
            JRS_ESH3_GPIB_Frequency_Step_MHz.this.jFreqStep_MHz.setBlank ();
        }
        finally
        {
          JRS_ESH3_GPIB_Frequency_Step_MHz.this.inhibitInstrumentControl = false;
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
