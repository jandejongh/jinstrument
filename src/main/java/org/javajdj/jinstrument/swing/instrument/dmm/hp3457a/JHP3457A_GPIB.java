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
package org.javajdj.jinstrument.swing.instrument.dmm.hp3457a;

import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Settings;
import org.javajdj.jinstrument.gpib.dmm.hp3457a.HP3457A_GPIB_Status;
import org.javajdj.jinstrument.swing.default_view.JDefaultDigitalMultiMeterView;
import org.javajdj.jswing.jbyte.JByte;

/** A Swing panel for (complete) control and status of a {@link HP3457A_GPIB_Instrument} Digital MultiMeter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3457A_GPIB
  extends JDefaultDigitalMultiMeterView
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3457A_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3457A_GPIB (final HP3457A_GPIB_Instrument digitalMultiMeter, final int level)
  {
    super (digitalMultiMeter, level);
    this.jInstrumentSpecificPanel.removeAll ();
    setPanelBorder (this.jInstrumentSpecificPanel, level + 1, DEFAULT_MANAGEMENT_COLOR, "HP-3457A Specific");
    this.jInstrumentSpecificPanel.setLayout (new GridLayout (1, 1));
    //
    final JPanel jSerialPollStatusPanel = new JPanel ();
    setPanelBorder (jSerialPollStatusPanel, level + 2, DEFAULT_MANAGEMENT_COLOR, "Serial Poll Status [Read-Only]");
    jSerialPollStatusPanel.setLayout (new GridLayout (2, 1));
    this.jSerialPollStatus = new JByte ();
    jSerialPollStatusPanel.add (this.jSerialPollStatus);
    final JPanel jSerialPollTags = new JPanel ();
    jSerialPollTags.setLayout (new GridLayout (1, 8));
    jSerialPollTags.add (new JLabel ("XXX"));
    jSerialPollTags.add (new JLabel ("XXX"));
    jSerialPollTags.add (new JLabel ("XXX"));
    jSerialPollTags.add (new JLabel ("XXX"));
    jSerialPollTags.add (new JLabel ("XXX"));
    jSerialPollTags.add (new JLabel ("XXX"));
    jSerialPollTags.add (new JLabel ("XXX"));
    jSerialPollTags.add (new JLabel ("XXX"));
    jSerialPollStatusPanel.add (jSerialPollTags);
    this.jInstrumentSpecificPanel.add (jSerialPollStatusPanel);
    
    getDigitalMultiMeter ().addInstrumentListener (this.instrumentListener);
    
  }
  
  public JHP3457A_GPIB (final HP3457A_GPIB_Instrument digitalMultiMeter)
  {
    this (digitalMultiMeter, 0);
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
      return "HP-3457A [GPIB] DMM View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3457A_GPIB_Instrument))
        return new JHP3457A_GPIB ((HP3457A_GPIB_Instrument) instrument);
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
    return JHP3457A_GPIB.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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

  private final JByte jSerialPollStatus;
    
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
      if (instrument != JHP3457A_GPIB.this.getDigitalMultiMeter () || instrumentStatus == null)
        throw new IllegalArgumentException ();
      if (! (instrumentStatus instanceof HP3457A_GPIB_Status))
        throw new IllegalArgumentException ();
      SwingUtilities.invokeLater (() ->
      {
        JHP3457A_GPIB.this.jSerialPollStatus.setDisplayedValue (
          ((HP3457A_GPIB_Status) instrumentStatus).getSerialPollStatusByte ());
      });
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JHP3457A_GPIB.this.getDigitalMultiMeter () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof HP3457A_GPIB_Settings))
        throw new IllegalArgumentException ();
      final HP3457A_GPIB_Settings settings = (HP3457A_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JHP3457A_GPIB.this.inhibitInstrumentControl = true;
        //
        try
        {
          // JHP3457A_GPIB.this.jXXX.setYYY (settings.XXX);
        }
        finally
        {
          JHP3457A_GPIB.this.inhibitInstrumentControl = false;
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
