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
package org.javajdj.jinstrument.swing.instrument.sa.hp70000;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SpectrumAnalyzer;
import org.javajdj.jinstrument.gpib.sa.hp70000.HP70000_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.sa.hp70000.HP70000_GPIB_Status;
import org.javajdj.jinstrument.swing.base.JSpectrumAnalyzerPanel;
import org.javajdj.jswing.jbyte.JByte;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for the status of a {@link HP70000_GPIB_Instrument} Spectrum Analyzer.
 *
 * <p>
 * XXX This class is a candidate for GPIB-wide definition.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see HP70000_GPIB_Status
 * 
 */
public class JHP70000_GPIB_Status
  extends JSpectrumAnalyzerPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP70000_GPIB_Status.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP70000_GPIB_Status (
    final SpectrumAnalyzer spectrumAnalyzer,
    final String title,
    final int level,
    final Color panelColor)
  {

    super (spectrumAnalyzer, title, level, panelColor);
    if (! (spectrumAnalyzer instanceof HP70000_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final HP70000_GPIB_Instrument hp70000 = (HP70000_GPIB_Instrument) spectrumAnalyzer;

    removeAll ();
    setLayout (new GridLayout (2, 1));
    
    this.jSerialPollStatus = new JByte (Color.red,
      Arrays.asList (new String[]{" X ", "RQS", "ERR", "RDY", " X ", "EOS", "MSG", "ARM"}));
    add (this.jSerialPollStatus);
    
    final JPanel messageUncalUncorPanel = new JPanel ();
    add (messageUncalUncorPanel);
    
    messageUncalUncorPanel.setLayout (new GridLayout (1, 2));
    
    this.jMessage = new JTextArea (3, 1);
    this.jMessage.setEditable (false);
    messageUncalUncorPanel.add (new JScrollPane (this.jMessage));

    final JPanel uncalUncorPanel = new JPanel ();
    messageUncalUncorPanel.add (uncalUncorPanel);
    
    // XXX The layout of this panel could use some more work...
    uncalUncorPanel.setLayout (new GridLayout (2,5));
    
    // uncalUncorPanel top row...
    
    uncalUncorPanel.add (new JLabel ());
    
    this.jUncalibrated = new JColorCheckBox.JBoolean (Color.red);
    uncalUncorPanel.add (JCenter.XY (this.jUncalibrated));
    
    uncalUncorPanel.add (new JLabel ());
    
    this.jUncorrected = new JColorCheckBox.JBoolean (Color.red);
    uncalUncorPanel.add (JCenter.XY (this.jUncorrected));
    
    uncalUncorPanel.add (new JLabel ());
    
    // uncalUncorPanel bottom row...
    
    uncalUncorPanel.add (new JLabel ());
    
    uncalUncorPanel.add (JCenter.X (new JLabel ("UNCAL")));
    
    uncalUncorPanel.add (new JLabel ());
    
    uncalUncorPanel.add (JCenter.X (new JLabel ("UNCOR")));
    
    uncalUncorPanel.add (new JLabel ());
    
    // InstrumentListener
    
    getSpectrumAnalyzer ().addInstrumentListener (this.instrumentListener);
    
  }

  public JHP70000_GPIB_Status (final HP70000_GPIB_Instrument spectrumAnalyzer, final int level)
  {
    this (spectrumAnalyzer, null, level, null);
  }
  
  public JHP70000_GPIB_Status (final HP70000_GPIB_Instrument spectrumAnalyzer)
  {
    this (spectrumAnalyzer, 0);
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
      return "HP-70000 [GPIB] Status";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP70000_GPIB_Instrument))
        return new JHP70000_GPIB_Status ((HP70000_GPIB_Instrument) instrument);
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
    return JHP70000_GPIB_Status.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JTextArea jMessage;
  
  private final JColorCheckBox.JBoolean jUncalibrated;
  
  private final JColorCheckBox.JBoolean jUncorrected;
    
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
      if (instrument != JHP70000_GPIB_Status.this.getSpectrumAnalyzer () || instrumentStatus == null)
        throw new IllegalArgumentException ();
      if (! (instrumentStatus instanceof HP70000_GPIB_Status))
        throw new IllegalArgumentException ();
      final HP70000_GPIB_Status hp70000_status = (HP70000_GPIB_Status) instrumentStatus;
      SwingUtilities.invokeLater (() ->
      {
        JHP70000_GPIB_Status.this.jSerialPollStatus.setDisplayedValue (hp70000_status.getSerialPollStatusByte ());
        if (hp70000_status.isErrorPresent ())
        {
          final String errorsString = hp70000_status.getErrorString ();
          if (errorsString != null)
            JHP70000_GPIB_Status.this.jMessage.append ("Error: " + errorsString + "\n");
        }
        JHP70000_GPIB_Status.this.jUncalibrated.setDisplayedValue (hp70000_status.isUncalibrated ());
        JHP70000_GPIB_Status.this.jUncorrected.setDisplayedValue (hp70000_status.isUncorrected ());
        if (hp70000_status.isMessagePresent ())
        {
          // Note: This does not show 'UNCAL/UNCOR' notifications.
          final String messageStringCooked = hp70000_status.getMessageStringCooked ();
          if (messageStringCooked != null)
            JHP70000_GPIB_Status.this.jMessage.append (messageStringCooked + "\n");
        }
      });
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      // EMPTY
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
