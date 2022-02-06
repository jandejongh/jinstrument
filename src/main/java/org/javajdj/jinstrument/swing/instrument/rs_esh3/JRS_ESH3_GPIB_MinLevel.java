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

/** A Swing panel for the Minimum Level settings of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_MinLevel
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_MinLevel.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_MinLevel (
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
    setLayout (new GridLayout (2, 1));
    
    this.jLevel = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), true, 5, 2);
    this.jLevel.addMouseListener (this.jLevelMouseListener);
    add (this.jLevel);
    this.jLevelSlider_dB = new JSlider (-140, 200, 0);
    this.jLevelSlider_dB.setToolTipText ("-");
    this.jLevelSlider_dB.setMajorTickSpacing (340);
    this.jLevelSlider_dB.setMinorTickSpacing (140);
    this.jLevelSlider_dB.setPaintTicks (true);
    this.jLevelSlider_dB.setPaintLabels (true);
    this.jLevelSlider_dB.setValue (-140);
    this.jLevelSlider_dB.addChangeListener (this.jLevelSlider_ChangeListener);
    setPanelBorder (this.jLevelSlider_dB,
      getLevel () + 2,
      getGuiPreferencesAmplitudeColor (),
      "[dB]");
    add (this.jLevelSlider_dB);
    
    getSelectiveLevelMeter ().addInstrumentListener (this.instrumentListener);

  }

  public JRS_ESH3_GPIB_MinLevel (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_MinLevel (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
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
      return "R&S ESH-3 [GPIB] Minimum Level Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_MinLevel ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_MinLevel.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // ATTENUATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSevenSegmentNumber jLevel;
  
  private final MouseListener jLevelMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newLevel_dB = JInstrumentDialog.getPowerRatioFromDialog_dB ("Enter Minimum Level [dB]", null);
      if (newLevel_dB != null)
        try
        {
          ((RS_ESH3_GPIB_Instrument) JRS_ESH3_GPIB_MinLevel.this.getSelectiveLevelMeter ()).setMinimumLevel_dB (newLevel_dB);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting minimum level on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting minimum level on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final JSlider jLevelSlider_dB;
  
  private final ChangeListener jLevelSlider_ChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JRS_ESH3_GPIB_MinLevel.this.inhibitInstrumentControl)
        try
        {
          ((RS_ESH3_GPIB_Instrument) JRS_ESH3_GPIB_MinLevel.this.getSelectiveLevelMeter ()).setMinimumLevel_dB (
            source.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting minimum level from slider to {0} dB: {1}.",
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
      if (instrument != JRS_ESH3_GPIB_MinLevel.this.getSelectiveLevelMeter ()|| instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof RS_ESH3_GPIB_Settings))
        throw new IllegalArgumentException ();
      final RS_ESH3_GPIB_Settings settings = (RS_ESH3_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JRS_ESH3_GPIB_MinLevel.this.inhibitInstrumentControl = true;
        //
        try
        {
          final boolean maxMinMode = settings.getMaxMinMode ();
          final double minimumLevel_dB = settings.getMinimumLevel_dB ();
          if (maxMinMode)
          {
            JRS_ESH3_GPIB_MinLevel.this.jLevel.setNumber (minimumLevel_dB);
            JRS_ESH3_GPIB_MinLevel.this.jLevelSlider_dB.setValue ((int) Math.round (minimumLevel_dB));
            JRS_ESH3_GPIB_MinLevel.this.jLevelSlider_dB.setToolTipText (Integer.toString ((int) Math.round (minimumLevel_dB)));
          }
          else
          {
            JRS_ESH3_GPIB_MinLevel.this.jLevel.setBlank ();
            JRS_ESH3_GPIB_MinLevel.this.jLevelSlider_dB.setValue (-140);
            JRS_ESH3_GPIB_MinLevel.this.jLevelSlider_dB.setToolTipText ("-");
          }
        }
        finally
        {
          JRS_ESH3_GPIB_MinLevel.this.inhibitInstrumentControl = false;
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
