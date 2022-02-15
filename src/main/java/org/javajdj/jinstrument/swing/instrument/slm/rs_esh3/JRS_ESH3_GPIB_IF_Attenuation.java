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
package org.javajdj.jinstrument.swing.instrument.slm.rs_esh3;

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

/** A Swing panel for the IF Attenuation settings of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_IF_Attenuation
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_IF_Attenuation.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_IF_Attenuation (
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
    
    this.jAtt = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), false, 4, 1);
    this.jAtt.addMouseListener (this.jAttMouseListener);
    add (this.jAtt);
    this.jAttSlider_dB = new JSlider (0, 40, 40);
    this.jAttSlider_dB.setToolTipText ("-");
    this.jAttSlider_dB.setMajorTickSpacing (10);
    this.jAttSlider_dB.setMinorTickSpacing (1);
    this.jAttSlider_dB.setPaintTicks (true);
    this.jAttSlider_dB.setPaintLabels (true);
    this.jAttSlider_dB.addChangeListener (this.jAttSlider_ChangeListener);
    setPanelBorder (this.jAttSlider_dB,
      getLevel () + 2,
      getGuiPreferencesAmplitudeColor (),
      "[dB]");
    add (this.jAttSlider_dB);
    
    getSelectiveLevelMeter ().addInstrumentListener (this.instrumentListener);

  }

  public JRS_ESH3_GPIB_IF_Attenuation (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_IF_Attenuation (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
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
      return "R&S ESH-3 [GPIB] IF Attenuation Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_IF_Attenuation ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_IF_Attenuation.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JSevenSegmentNumber jAtt;
  
  private final MouseListener jAttMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newAttenuation_dB = JInstrumentDialog.getPowerRatioFromDialog_dB ("Enter IF Attenuation [dB]", null);
      if (newAttenuation_dB != null)
        try
        {
          ((RS_ESH3_GPIB_Instrument) JRS_ESH3_GPIB_IF_Attenuation.this.getSelectiveLevelMeter ()).setIfAttenuation_dB
            (newAttenuation_dB);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting IF attenuation on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting IF attenuation on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final JSlider jAttSlider_dB;
  
  private final ChangeListener jAttSlider_ChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JRS_ESH3_GPIB_IF_Attenuation.this.inhibitInstrumentControl)
        try
        {
          ((RS_ESH3_GPIB_Instrument) JRS_ESH3_GPIB_IF_Attenuation.this.getSelectiveLevelMeter ()).setIfAttenuation_dB (
            source.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting IF Attenuation from slider to {0} dB: {1}.",
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
      if (instrument != JRS_ESH3_GPIB_IF_Attenuation.this.getSelectiveLevelMeter ()|| instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof RS_ESH3_GPIB_Settings))
        throw new IllegalArgumentException ();
      final RS_ESH3_GPIB_Settings settings = (RS_ESH3_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JRS_ESH3_GPIB_IF_Attenuation.this.inhibitInstrumentControl = true;
        //
        try
        {
          final RS_ESH3_GPIB_Settings.AttenuationMode attenuationMode = settings.getAttenuationMode ();
          final double ifAttenuation_dB = settings.getIfAttenuation_dB ();
          switch (attenuationMode)
          {
            case AutoRangingLowNoise:
            case AutoRangingLowDistortion:
              JRS_ESH3_GPIB_IF_Attenuation.this.jAtt.setBlank ();
              JRS_ESH3_GPIB_IF_Attenuation.this.jAttSlider_dB.setValue (40);
              JRS_ESH3_GPIB_IF_Attenuation.this.jAttSlider_dB.setToolTipText ("-");
              break;
            case Manual:
              JRS_ESH3_GPIB_IF_Attenuation.this.jAtt.setNumber (ifAttenuation_dB);
              JRS_ESH3_GPIB_IF_Attenuation.this.jAttSlider_dB.setValue ((int) Math.round (ifAttenuation_dB));
              JRS_ESH3_GPIB_IF_Attenuation.this.jAttSlider_dB.setToolTipText (
                Integer.toString ((int) Math.round (ifAttenuation_dB)));
              break;
            default:
              throw new RuntimeException ();
          }
        }
        finally
        {
          JRS_ESH3_GPIB_IF_Attenuation.this.inhibitInstrumentControl = false;
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
