/*
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.swing.instrument.tek2440;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
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
import org.javajdj.jswing.jbyte.JBitsLong;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for the Sequencer settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * <p>
 * Currently empty...
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Sequencer
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Sequencer.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Sequencer (
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
    setLayout (new GridLayout (4, 1, 10, 0));
    
    final JTextPane jDescription = new JTextPane ();
    jDescription.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Description"));
    jDescription.setContentType ("text/html");
    jDescription.setBackground (getBackground ());
    jDescription.setEditable (false);
    jDescription.setText ("<html><b>Sequencer Settings and Commands</b>" +
//                          "<ul>" +
//                            "<li> the data format for transferring settings, status, and traces;</li>" +
//                            "<li>Features manual data transfers to and from the instrument;</li>" +
//                            "<li>Values marked in red are used internally in the software and therefore read-only.</li>" +
//                          "</ul>" +
                          "</html>");
    add (jDescription);
    
    final JPanel formatPanel = new JPanel ();
    formatPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Format"));
    formatPanel.setLayout (new GridLayout (1, 4));
    formatPanel.add (new JLabel ("Add Formatting [PRGm?]"));
    this.jSequencerFormat = new JColorCheckBox.JBoolean (Color.green);
    this.jSequencerFormat.addActionListener (new JInstrumentActionListener_1Boolean (
      "[sequencer] format [PRGm?]",
      this.jSequencerFormat::getDisplayedValue,
      tek2440::setSequencerEnableFormatChars,
      this::isInhibitInstrumentControl));
    formatPanel.add (this.jSequencerFormat);
    formatPanel.add (new JLabel ()); // Aesthetics, really...
    formatPanel.add (new JLabel ());
    add (formatPanel);

    final JPanel actionPanel = new JPanel ();
    actionPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Action(s)"));
    actionPanel.setLayout (new GridLayout (1, 1));
    this.jSequencerActions = new JBitsLong (
      Color.green,
      9,
      Arrays.asList (
        "Protect      ",
        "Pause        ",
        "SRQ          ",
        "Bell         ",
        "Print        ",
        "AutoSetup    ",
        "SelfTest     ",
        "SelfCal      ",
        "Repeat       "),
      this.jSeqeuncerActionsListener,
      false);
    actionPanel.add (JCenter.XY (this.jSequencerActions));
    add (actionPanel);
    
    final JPanel maintenancePanel = new JPanel ();
    maintenancePanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR, 2),
        "Sequencer Maintenance"));
    maintenancePanel.setLayout (new GridLayout (1, 4));
    maintenancePanel.add (new JLabel ("Force"));
    this.jSequencerForce = new JColorCheckBox.JBoolean (Color.green);
    this.jSequencerForce.addActionListener (new JInstrumentActionListener_1Boolean (
      "[sequencer] force",
      this.jSequencerForce::getDisplayedValue,
      tek2440::setSequencerForce,
      this::isInhibitInstrumentControl));
    maintenancePanel.add (this.jSequencerForce);
    maintenancePanel.add (new JLabel ()); // Aesthetics, really...
    maintenancePanel.add (new JLabel ());
    add (maintenancePanel);
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }

  public JTek2440_GPIB_Sequencer (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    this (digitalStorageOscilloscope, null, level, null);
  }
  
  public JTek2440_GPIB_Sequencer (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] Sequencer Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB_Sequencer ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB_Sequencer.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JColorCheckBox.JBoolean jSequencerFormat;
  
  private final JBitsLong jSequencerActions;
  
  private final JColorCheckBox.JBoolean jSequencerForce;
  
  private final Consumer<Long> jSeqeuncerActionsListener = (final Long newValue) ->
  {
    final Tek2440_GPIB_Instrument tek2440 = ((Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ());
    try
    {
      tek2440.setSequencerActionsFromInt ((int) (long) newValue);
    }
    catch (IOException | InterruptedException e)    
    {
      LOG.log (Level.INFO, "Caught exception while setting sequencer actions on instrument {1}"
        + " from JBitsLong to {0}: {2}.",
        new Object[]{newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
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
      if (instrument != JTek2440_GPIB_Sequencer.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_Sequencer.this.setInhibitInstrumentControl ();
        try
        {
          JTek2440_GPIB_Sequencer.this.jSequencerFormat.setDisplayedValue (
            settings.isSequencerFormatCharsEnabled ());
          JTek2440_GPIB_Sequencer.this.jSequencerActions.setDisplayedValue (
            settings.getSequencerActionsAsInt ());
          JTek2440_GPIB_Sequencer.this.jSequencerForce.setDisplayedValue (
            settings.isSequencerForce ());
        }
        finally
        {
          JTek2440_GPIB_Sequencer.this.resetInhibitInstrumentControl ();
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
