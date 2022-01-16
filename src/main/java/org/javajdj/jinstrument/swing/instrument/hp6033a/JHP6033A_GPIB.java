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
package org.javajdj.jinstrument.swing.instrument.hp6033a;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.psu.hp6033a.HP6033A_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.psu.hp6033a.HP6033A_GPIB_Settings;
import org.javajdj.jinstrument.gpib.psu.hp6033a.HP6033A_GPIB_Status;
import org.javajdj.jinstrument.swing.default_view.JDefaultPowerSupplyUnitView;
import org.javajdj.jswing.jbyte.JBitsLong;
import org.javajdj.jswing.jbyte.JByte;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A Swing panel for (complete) control and status of a {@link HP6033A_GPIB_Instrument} Power Supply Unit.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JHP6033A_GPIB
  extends JDefaultPowerSupplyUnitView
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JHP6033A_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JHP6033A_GPIB (final HP6033A_GPIB_Instrument powerSupplyUnit, final int level)
  {
    //
    super (powerSupplyUnit, level);
    //
    this.jInstrumentSpecificPanel1.removeAll ();
    setPanelBorder (
      this.jInstrumentSpecificPanel1,
      level + 1,
      getGuiPreferencesManagementColor (),
      "HP-6033A Specific");
    this.jInstrumentSpecificPanel1.setLayout (new GridLayout (4, 1));
    //
    this.jSerialPollStatus = new JByte (
      Color.red,
      Arrays.asList (new String[]{"  -", "RQS", "ERR", "RDY", "  -", "  -", "PON", "FAU"}));
    setPanelBorder (
      this.jSerialPollStatus, level + 2, getGuiPreferencesManagementColor (), "Serial Poll Status [Read-Only]");
    this.jInstrumentSpecificPanel1.add (this.jSerialPollStatus);
    //
    this.jStatusRegister = new JBitsLong (
      Color.red,
      9,
      Arrays.asList (new String[]{"RI", "ERR", "FOLD", "AC", "OT", "OV", "OR", "CC", "CV"}));
    setPanelBorder (
      this.jStatusRegister,
      level + 2,
      getGuiPreferencesManagementColor (),
      "Status Register [Read-Only]");
    this.jInstrumentSpecificPanel1.add (this.jStatusRegister);
    //
    this.jErrorMessage = new JTextField ();
    this.jErrorMessage.setOpaque (false);
    this.jErrorMessage.setEditable (false);
    setPanelBorder (
      this.jErrorMessage,
      level + 2,
      getGuiPreferencesManagementColor (),
      "Error Message [Read-Only]");
    this.jInstrumentSpecificPanel1.add (this.jErrorMessage);
    //
    final JPanel buttonPanel = new JPanel ();
    setPanelBorder (
      buttonPanel,
      level + 2,
      getGuiPreferencesManagementColor (),
      null);
    buttonPanel.setLayout (new GridLayout (1, 3));
    final JPanel jResetPanel = new JPanel ();
    setPanelBorder (
      jResetPanel,
      level + 3,
      getGuiPreferencesManagementColor (),
      "Reset Instrument");
    jResetPanel.setLayout (new GridLayout (1,1));
    this.jReset = new JColorCheckBox.JBoolean ((b) -> getGuiPreferencesManagementColor ());
    this.jReset.setAlignmentX (Component.CENTER_ALIGNMENT);
    this.jReset.setHorizontalAlignment (SwingConstants.CENTER);
    this.jReset.addMouseListener (this.jResetMouseListener);
    jResetPanel.add (this.jReset);
    buttonPanel.add (jResetPanel);
    final JPanel jClearPanel = new JPanel ();
    setPanelBorder (
      jClearPanel,
      level + 3,
      getGuiPreferencesManagementColor (),
      "Clear Instrument");
    jClearPanel.setLayout (new GridLayout (1,1));
    this.jClear = new JColorCheckBox.JBoolean ((b) -> getGuiPreferencesManagementColor ());
    this.jClear.setAlignmentX (Component.CENTER_ALIGNMENT);
    this.jClear.setHorizontalAlignment (SwingConstants.CENTER);
    this.jClear.addMouseListener (this.jClearMouseListener);
    jClearPanel.add (this.jClear);
    buttonPanel.add (jClearPanel);
    this.jFoldbackMode = new JComboBox<> (HP6033A_GPIB_Instrument.FoldbackMode.values ());
    setPanelBorder (
      this.jFoldbackMode,
      level + 3,
      getGuiPreferencesManagementColor (),
      "Foldback");
    this.jFoldbackMode.setSelectedItem (null);
    this.jFoldbackMode.addItemListener (this.jFoldbackModeListener);
    buttonPanel.add (this.jFoldbackMode);
    this.jInstrumentSpecificPanel1.add (buttonPanel);
    //
    this.jInstrumentSpecificPanel2.removeAll ();
    setPanelBorder (
      this.jInstrumentSpecificPanel2,
      level + 1,
      getGuiPreferencesManagementColor (),
      "HP-6033A Specific");
    this.jInstrumentSpecificPanel2.setLayout (new GridLayout (2, 1));
    //
    this.jOvp = new JSevenSegmentNumber (getGuiPreferencesVoltageColor (), 0, 999, 0.1);
    setPanelBorder (
      this.jOvp,
      level + 2,
      getGuiPreferencesVoltageColor (),
      "Over Voltage Protection [V] [Read-Only]");
    this.jInstrumentSpecificPanel2.add (this.jOvp);
    //
    final JPanel delayPanel = new JPanel ();
    setPanelBorder (
      delayPanel,
      level + 2,
      getGuiPreferencesTimeColor (),
      "Delay [s]");
    delayPanel.setLayout (new GridLayout (1, 1));
    this.jDelay = new JSevenSegmentNumber (getGuiPreferencesTimeColor (), 0, 32, 0.001);
    this.jDelay.addMouseListener (this.jDelayListener);
    delayPanel.add (this.jDelay);
    this.jInstrumentSpecificPanel2.add (delayPanel);
    //
    getPowerSupplyUnit ().addInstrumentListener (this.instrumentListener);
    //
  }
  
  public JHP6033A_GPIB (final HP6033A_GPIB_Instrument powerSupplyUnit)
  {
    this (powerSupplyUnit, 0);
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
      return "HP-6033A [GPIB] PSU View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof HP6033A_GPIB_Instrument))
        return new JHP6033A_GPIB ((HP6033A_GPIB_Instrument) instrument);
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
    return JHP6033A_GPIB.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // SWING - COMPONENTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JByte jSerialPollStatus;
  
  private final JBitsLong jStatusRegister;
  
  private final JTextField jErrorMessage;
  
  private final JSevenSegmentNumber jOvp;
  
  private final JSevenSegmentNumber jDelay;
  
  private final JColorCheckBox.JBoolean jReset;
  
  private final JColorCheckBox.JBoolean jClear;
  
  private final JComboBox<HP6033A_GPIB_Instrument.FoldbackMode> jFoldbackMode;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING - LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final MouseListener jDelayListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newDelay_s = getPeriodFromDialog_s ("Enter Delay [s]", null);
      if (newDelay_s != null)
        try
        {
          ((HP6033A_GPIB_Instrument) JHP6033A_GPIB.this.getPowerSupplyUnit ()).setDelay_s (newDelay_s);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting delay on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting delay on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final MouseListener jResetMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      super.mouseClicked (me);
      try
      {
        ((HP6033A_GPIB_Instrument) getPowerSupplyUnit ()).resetInstrument ();
      }
      catch (IOException | InterruptedException e)
      {
        LOG.log (Level.INFO, "Caught exception while resetting instrument.");
      }
    }
  };
  
  private final MouseListener jClearMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      super.mouseClicked (me);
      try
      {
        ((HP6033A_GPIB_Instrument) getPowerSupplyUnit ()).clearInstrument ();
      }
      catch (IOException | InterruptedException e)
      {
        LOG.log (Level.INFO, "Caught exception while clearing instrument.");
      }
    }
  };
  
  private final ItemListener jFoldbackModeListener = (ItemEvent ie) ->
  {
    if (ie.getStateChange () == ItemEvent.SELECTED)
    {
      final HP6033A_GPIB_Instrument.FoldbackMode newValue = (HP6033A_GPIB_Instrument.FoldbackMode) ie.getItem ();
      if (! JHP6033A_GPIB.this.inhibitInstrumentControl)
      {
        try
        {
          ((HP6033A_GPIB_Instrument) getPowerSupplyUnit ()).setFoldbackMode (newValue);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting foldback mode on instrument from combo box to {0}.",
            new Object[]{newValue, e});
        }
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
      if (instrument != JHP6033A_GPIB.this.getPowerSupplyUnit () || instrumentStatus == null)
        throw new IllegalArgumentException ();
      if (! (instrumentStatus instanceof HP6033A_GPIB_Status))
        throw new IllegalArgumentException ();
      final HP6033A_GPIB_Status status = (HP6033A_GPIB_Status) instrumentStatus;
      SwingUtilities.invokeLater (() ->
      {
        JHP6033A_GPIB.this.jSerialPollStatus.setDisplayedValue (status.getSerialPollStatusByte ());
        JHP6033A_GPIB.this.jStatusRegister.setDisplayedValue (status.getStatusRegister () & 0x1ff);
        JHP6033A_GPIB.this.jErrorMessage.setForeground (status.getErrorNumber () == 0 ? Color.black : Color.red);
        JHP6033A_GPIB.this.jErrorMessage.setText (status.getErrorString ());
      });
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JHP6033A_GPIB.this.getPowerSupplyUnit ()|| instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof HP6033A_GPIB_Settings))
        throw new IllegalArgumentException ();
      final HP6033A_GPIB_Settings settings = (HP6033A_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JHP6033A_GPIB.this.inhibitInstrumentControl = true;
        //
        try
        {
          JHP6033A_GPIB.this.jOvp.setNumber (settings.getOverVoltageProtection_V ());
          JHP6033A_GPIB.this.jDelay.setNumber (settings.getDelay_s ());
          JHP6033A_GPIB.this.jFoldbackMode.setSelectedItem (settings.getFoldbackMode ());
        }
        finally
        {
          JHP6033A_GPIB.this.inhibitInstrumentControl = false;
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
