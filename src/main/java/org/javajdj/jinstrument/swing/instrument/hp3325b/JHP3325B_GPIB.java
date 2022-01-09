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
import java.util.Arrays;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Settings;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Status;
import org.javajdj.jinstrument.swing.default_view.JDefaultFunctionGeneratorView;
import org.javajdj.jswing.jbyte.JByte;

/** A Swing panel for a {@link HP3325B_GPIB_Instrument} Function Generator.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3325B_GPIB
  extends JDefaultFunctionGeneratorView
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3325B_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3325B_GPIB (final HP3325B_GPIB_Instrument functionGenerator, final int level)
  {

    super (functionGenerator, level);

    this.jInstrumentSpecificPanel.removeAll ();
    setPanelBorder (
      this.jInstrumentSpecificPanel,
      getLevel () + 1,
      getGuiPreferencesManagementColor (),
      "HP-3325B Specific");
    this.jInstrumentSpecificPanel.setLayout (new GridLayout (5, 2));

    this.jSerialPollStatus = new JByte (Color.red,
      Arrays.asList (new String[]{"BSY", "SRQ", "SWP", "ZERO", "FAIL", "STA", "STO", "UERR"}));
    setPanelBorder (
      this.jSerialPollStatus,
      level + 2,
      getGuiPreferencesManagementColor (),
      "Serial Poll Status [Read-Only]");
    this.jInstrumentSpecificPanel.add (this.jSerialPollStatus);
    
    this.jInstrumentSpecificPanel.add (new JLabel ());
    this.jInstrumentSpecificPanel.add (new JLabel ());
    this.jInstrumentSpecificPanel.add (new JLabel ());
    this.jInstrumentSpecificPanel.add (new JLabel ());
    this.jInstrumentSpecificPanel.add (new JLabel ());
    this.jInstrumentSpecificPanel.add (new JLabel ());
    this.jInstrumentSpecificPanel.add (new JLabel ());
    this.jInstrumentSpecificPanel.add (new JLabel ());
    this.jInstrumentSpecificPanel.add (new JLabel ());

    getFunctionGenerator ().addInstrumentListener (this.instrumentListener);
    
  }
  
  public JHP3325B_GPIB (final HP3325B_GPIB_Instrument functionGenerator)
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
      return "HP-3325B [GPIB] FG View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3325B_GPIB_Instrument))
        return new JHP3325B_GPIB ((HP3325B_GPIB_Instrument) instrument);
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
    return JHP3325B_GPIB.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
      if (instrument != JHP3325B_GPIB.this.getFunctionGenerator ()|| instrumentStatus == null)
        throw new IllegalArgumentException ();
      if (! (instrumentStatus instanceof HP3325B_GPIB_Status))
        throw new IllegalArgumentException ();
      SwingUtilities.invokeLater (() ->
      {
        JHP3325B_GPIB.this.jSerialPollStatus.setDisplayedValue (
          ((HP3325B_GPIB_Status) instrumentStatus).getSerialPollStatusByte ());
      });
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JHP3325B_GPIB.this.getFunctionGenerator ()|| instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof HP3325B_GPIB_Settings))
        throw new IllegalArgumentException ();
      final HP3325B_GPIB_Settings settings = (HP3325B_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JHP3325B_GPIB.this.inhibitInstrumentControl = true;
        try
        {
        }
        finally
        {
          JHP3325B_GPIB.this.inhibitInstrumentControl = false;
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
