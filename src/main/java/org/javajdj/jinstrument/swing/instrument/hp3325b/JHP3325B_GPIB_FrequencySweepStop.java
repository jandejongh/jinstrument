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
package org.javajdj.jinstrument.swing.instrument.hp3325b;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JFunctionGeneratorPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.dialog.JInstrumentDialog;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A Swing panel for the Stop Frequency setting of a {@link HP3325B_GPIB_Instrument} Function Generator.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3325B_GPIB_FrequencySweepStop
  extends JFunctionGeneratorPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3325B_GPIB_FrequencySweepStop.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3325B_GPIB_FrequencySweepStop (
    final FunctionGenerator functionGenerator,
    final String title,
    final int level,
    final Color panelColor)
  {

    super (functionGenerator, title, level, panelColor);
    if (! (functionGenerator instanceof HP3325B_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final HP3325B_GPIB_Instrument hp3325b = (HP3325B_GPIB_Instrument) functionGenerator;

    removeAll ();
    setLayout (new GridLayout (1, 1));
    
    this.jFreqStop = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 11, 7);
    this.jFreqStop.addMouseListener (this.jFreqStopMouseListener);
    add (this.jFreqStop);
    
    getFunctionGenerator ().addInstrumentListener (this.instrumentListener);

  }

  public JHP3325B_GPIB_FrequencySweepStop (final FunctionGenerator functionGenerator, final int level)
  {
    this (functionGenerator, null, level, null);
  }
  
  public JHP3325B_GPIB_FrequencySweepStop (final FunctionGenerator functionGenerator)
  {
    this (functionGenerator, 0);
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
      return "HP-3325B [GPIB] Stop Frequency Setting";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3325B_GPIB_Instrument))
        return new JHP3325B_GPIB_FrequencySweepStop ((HP3325B_GPIB_Instrument) instrument);
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
    return JHP3325B_GPIB_FrequencySweepStop.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JSevenSegmentNumber jFreqStop;
  
  private final MouseListener jFreqStopMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newFrequency_Hz = JInstrumentDialog.getFrequencyFromDialog_Hz ("Enter Stop Frequency [Hz]", null);
      if (newFrequency_Hz != null)
        try
        {
          ((HP3325B_GPIB_Instrument) JHP3325B_GPIB_FrequencySweepStop.this.getFunctionGenerator ()).setSweepStopFrequency_Hz (
            newFrequency_Hz);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting stop frequency on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting stop frequency on instrument: {0}.",
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
      if (instrument != JHP3325B_GPIB_FrequencySweepStop.this.getFunctionGenerator () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof HP3325B_GPIB_Settings))
        throw new IllegalArgumentException ();
      final HP3325B_GPIB_Settings settings = (HP3325B_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JHP3325B_GPIB_FrequencySweepStop.this.setInhibitInstrumentControl ();
        //
        try
        {
          final double reading_Hz = settings.getSweepStopFrequency_Hz ();
          JHP3325B_GPIB_FrequencySweepStop.this.jFreqStop.setNumber (reading_Hz);
        }
        finally
        {
          JHP3325B_GPIB_FrequencySweepStop.this.resetInhibitInstrumentControl ();
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
