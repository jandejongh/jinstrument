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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.fg.hp3325b.HP3325B_GPIB_Instrument;
import org.javajdj.jinstrument.swing.base.JFunctionGeneratorPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.getGuiPreferencesManagementColor;
import org.javajdj.jswing.jcenter.JCenter;

/** A Swing panel for Display settings of a {@link HP3325B_GPIB_Instrument} Function Generator.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP3325B_GPIB_Display
  extends JFunctionGeneratorPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP3325B_GPIB_Display.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP3325B_GPIB_Display (
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
    setLayout (new GridLayout (2, 2));
        
    add (new JInstrumentPanel (
      hp3325b,
      "Display Off",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JVoid_JColorCheckBox (
        "Display Off",
        hp3325b::displayOff,
        Color.blue)));
    
    add (new JInstrumentPanel (
      hp3325b,
      "Display On",
      level + 1,
      getGuiPreferencesManagementColor (),
      new JVoid_JColorCheckBox (
        "Display On",
        hp3325b::displayOn,
        Color.blue)));
    
    final JPanel displayStringPanel = new JInstrumentPanel (
      hp3325b,
      "Display String",
      level + 1,
      getGuiPreferencesManagementColor ());
    add (displayStringPanel);
    
    displayStringPanel.setLayout (new GridLayout (2, 1));
    
    this.jDisplayString = new JTextField ("", 32);
    displayStringPanel.add (JCenter.XY (this.jDisplayString));
    
    final JButton displayWriteButton = new JButton ("Write String");
    displayStringPanel.add (JCenter.XY (displayWriteButton));
    displayWriteButton.addActionListener (this.jDisplayWriteButtonListener);
    
    add (new JLabel ());
    
  }

  public JHP3325B_GPIB_Display (final HP3325B_GPIB_Instrument functionGenerator, final int level)
  {
    this (functionGenerator, null, level, null);
  }
  
  public JHP3325B_GPIB_Display (final HP3325B_GPIB_Instrument functionGenerator)
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
      return "HP-3325B [GPIB] Display Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP3325B_GPIB_Instrument))
        return new JHP3325B_GPIB_Display ((HP3325B_GPIB_Instrument) instrument);
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
    return JHP3325B_GPIB_Display.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JTextField jDisplayString;
  
  private final ActionListener jDisplayWriteButtonListener = (final ActionEvent ae) ->
  {
    final String newDisplayString = JHP3325B_GPIB_Display.this.jDisplayString.getText ();
    try
    {
      ((HP3325B_GPIB_Instrument) JHP3325B_GPIB_Display.this.getFunctionGenerator ())
        .setDisplayString (newDisplayString);
    }
    catch (InterruptedException ie)
    {
      LOG.log (Level.INFO, "Caught InterruptedException while setting display string on instrument: {0}.",
        ie.getStackTrace ());
    }
    catch (IOException ioe)
    {
      LOG.log (Level.INFO, "Caught IOException while setting display string on instrument: {0}.",
        ioe.getStackTrace ());
    }
  };
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
