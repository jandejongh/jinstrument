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
package org.javajdj.jinstrument.swing.instrument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
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
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Status;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.default_view.JDefaultDigitalStorageOscilloscopeView;
import org.javajdj.jswing.jbyte.JByte;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jtrace.JTrace;

/** A Swing panel for (complete) control and status of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB
  extends JDefaultDigitalStorageOscilloscopeView
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB (final Tek2440_GPIB_Instrument digitalStorageOscilloscope, final int level)
  {
    //
    super (digitalStorageOscilloscope, level);
    //
    removeAll ();
    setLayout (new BorderLayout ());
    //
    this.xPanel = new JTek2440XPanel (digitalStorageOscilloscope);
    this.triggerPanel = new JTek2440TriggerPanel (digitalStorageOscilloscope);
    this.jCh1Panel = new JTek2440ChannelPanel (digitalStorageOscilloscope, Tek2440_GPIB_Instrument.Tek2440Channel.Channel1);
    this.jCh2Panel = new JTek2440ChannelPanel (digitalStorageOscilloscope, Tek2440_GPIB_Instrument.Tek2440Channel.Channel2);
    //
    getTracePanel ().setBorder (null);
    getTracePanel ().getJTrace ().setXDivisions (Tek2440_GPIB_Instrument.TEK2440_X_DIVISIONS);
    getTracePanel ().getJTrace ().setYDivisions (Tek2440_GPIB_Instrument.TEK2440_Y_DIVISIONS);
    // Next lines ensure that color scheme is consistent across the channels.
    getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, null);
    getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, null);
    add (getTracePanel (), BorderLayout.CENTER);
    //
    this.northPanel = new JPanel ();
    this.northPanel.setLayout (new FlowLayout (FlowLayout.LEFT));
    final JPanel jSimp = getSmallInstrumentManagementPanel ();
    jSimp.setPreferredSize (new Dimension (60, 80));
    jSimp.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Inst"));
    this.northPanel.add (jSimp);
    this.jSerialPollStatus = new JByte (Color.red,
      Arrays.asList (new String[]{"  ?", "RQS", "  ?", "  ?", "  ?", "  ?", "  ?", "  ?"}));
    this.jSerialPollStatus.setPreferredSize (new Dimension (240, 80));
    this.jSerialPollStatus.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (DEFAULT_MANAGEMENT_COLOR),
        "Serial Poll"));
    this.northPanel.add (this.jSerialPollStatus);
    add (northPanel, BorderLayout.NORTH);
    //
    this.eastPanel = new JPanel ();
    this.eastPanel.setLayout (new GridLayout (1, 2));
    this.eastPanel.add (this.jCh1Panel);
    this.eastPanel.add (this.jCh2Panel);
    add (this.eastPanel, BorderLayout.EAST);
    //
    this.southPanel = new JPanel ();
    this.southPanel.setLayout (new GridLayout (1, 2));
    this.southPanel.add (this.xPanel);
    this.southPanel.add (this.triggerPanel);
    add (this.southPanel, BorderLayout.SOUTH);
    //
    this.westPanel = new JPanel ();
    // this.westPanel.add (new JLabel ("west"));
    add (this.westPanel, BorderLayout.WEST);
    //
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    //
  }
  
  public JTek2440_GPIB (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
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
      return "Tektronix 2440 [GPIB] DSO View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return new JTek2440_GPIB ((Tek2440_GPIB_Instrument) instrument);
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
    return JTek2440_GPIB.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // NORTH/EAST/SOUTH/WEST PANELS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JPanel northPanel;
  
  private final JPanel eastPanel;
  
  private final JPanel southPanel;
  
  private final JPanel westPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // NORTH PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JByte jSerialPollStatus;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TEK 2440 PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static abstract class JTek2440Panel
    extends JDigitalStorageOscilloscopePanel
  {

    public JTek2440Panel (
      final Tek2440_GPIB_Instrument digitalStorageOscilloscope,
      final String title,
      final int level,
      final Color panelColor)
    {
      super (digitalStorageOscilloscope, title, level, panelColor);
    }

    public JTek2440Panel (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
    {
      this (digitalStorageOscilloscope, null, 1, null);
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TEK 2440 X PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTek2440XPanel xPanel;
  
  public class JTek2440XPanel
    extends JTek2440Panel
  {

    public JTek2440XPanel (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
    {
      super (digitalStorageOscilloscope, "X", 1, JInstrumentPanel.getGuiPreferencesTimeColor ());
      setLayout (new GridLayout (2, 2));
      add (new JLabel ("A Timebase"));
      this.jChATimebase = new JComboBox<> (Tek2440_GPIB_Settings.SecondsPerDivision.values ());
      this.jChATimebase.addItemListener (JTek2440_GPIB.this.jChTimebaseListener);
      add (this.jChATimebase);
      add (new JLabel ("B Timebase"));
      this.jChBTimebase = new JComboBox<> (Tek2440_GPIB_Settings.SecondsPerDivision.values ());
      this.jChBTimebase.addItemListener (JTek2440_GPIB.this.jChTimebaseListener);
      add (this.jChBTimebase);
    }
    
    private final JComboBox<Tek2440_GPIB_Settings.SecondsPerDivision> jChATimebase;
    
    private final JComboBox<Tek2440_GPIB_Settings.SecondsPerDivision> jChBTimebase;  
    
  }
  
  private final ItemListener jChTimebaseListener = (ItemEvent ie) ->
  {
    if (ie.getStateChange () == ItemEvent.SELECTED)
    {
      final Tek2440_GPIB_Settings.SecondsPerDivision newValue = (Tek2440_GPIB_Settings.SecondsPerDivision) ie.getItem ();
      final Tek2440_GPIB_Instrument tek2440 = ((Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ());
      if (! JTek2440_GPIB.this.inhibitInstrumentControl)
        try
        {
          if (ie.getSource () == JTek2440_GPIB.this.xPanel.jChATimebase)
            tek2440.setChannelTimebase (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, newValue);
          else if (ie.getSource () == JTek2440_GPIB.this.xPanel.jChBTimebase)
            tek2440.setChannelTimebase (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, newValue);
          else
            LOG.log (Level.SEVERE, "Unexpected event source!");            
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting Secs/Div on instrument"
            + " from combo box to {0}: {1}.",
            new Object[]{newValue, e});
        }
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TEK 2440 TRIGGER PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTek2440TriggerPanel triggerPanel;
  
  public static class JTek2440TriggerPanel
    extends JTek2440Panel
  {

    public JTek2440TriggerPanel (final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
    {
      super (digitalStorageOscilloscope, "Trigger", 1, JInstrumentPanel.getGuiPreferencesTriggerColor ());
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TEK 2440 CHANNEL PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTek2440ChannelPanel jCh1Panel;
  
  private final JTek2440ChannelPanel jCh2Panel;
  
  public class JTek2440ChannelPanel
    extends JTek2440Panel
  {

    private final Tek2440_GPIB_Instrument.Tek2440Channel channel;
    
    public JTek2440ChannelPanel (
      final Tek2440_GPIB_Instrument digitalStorageOscilloscope,
      final Tek2440_GPIB_Instrument.Tek2440Channel channel)
    {
      super (digitalStorageOscilloscope, "Channel " + channel.toString (), 1, JInstrumentPanel.getGuiPreferencesAmplitudeColor ());
      if (channel == null)
        throw new IllegalArgumentException ();
      this.channel = channel;
      setLayout (new GridLayout (20,2));
      add (new JLabel("Enable"));
      switch (channel)
      {
        case Channel1:
          this.jEnabled = new JColorCheckBox.JBoolean (JTrace.DEFAULT_COLORS[0]);
          break;
        case Channel2:
          this.jEnabled = new JColorCheckBox.JBoolean (JTrace.DEFAULT_COLORS[1]);
          break;
        default:
          throw new IllegalArgumentException ();
      }
      this.jEnabled.setHorizontalAlignment (SwingConstants.CENTER);
      this.jEnabled.addActionListener (this.jEnabledListener);
      add (this.jEnabled);
      add (new JLabel());
      add (new JLabel());
      add (new JLabel ("V/Div"));
      this.jVoltsPerDiv = new JComboBox<> (Tek2440_GPIB_Settings.VoltsPerDivision.values ());
      this.jVoltsPerDiv.addItemListener (this.jVoltsPerDivListener);
      add (this.jVoltsPerDiv);
      add (new JLabel());
      add (new JLabel());
      add (new JLabel("Coupling"));
      this.jCoupling = new JComboBox<> (Tek2440_GPIB_Settings.ChannelCoupling.values ());
      this.jCoupling.addItemListener (this.jCouplingListener);
      add (this.jCoupling);
      add (new JLabel("50 \u03A9"));
      this.jFifty = new JColorCheckBox.JBoolean (Color.red);
      this.jFifty.setHorizontalAlignment (SwingConstants.CENTER);
      this.jFifty.addActionListener (this.jFiftyListener);
      add (this.jFifty);
      add (new JLabel("Invert"));
      this.jInvert = new JColorCheckBox.JBoolean (Color.red);
      this.jInvert.setHorizontalAlignment (SwingConstants.CENTER);
      this.jInvert.addActionListener (this.jInvertListener);
      add (this.jInvert);
      add (new JLabel());
      add (new JLabel());
      add (new JLabel("Pos [DIV]"));
      this.jPosition = new JSlider (-10, 10);
      this.jPosition.setPreferredSize (new Dimension (100, 40));
      this.jPosition.setMajorTickSpacing (10);
      this.jPosition.setMinorTickSpacing (1);
      this.jPosition.setPaintTicks (true);
      this.jPosition.setToolTipText ("-");
      this.jPosition.addChangeListener (this.jPositionChangeListener);
      add (this.jPosition);
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
      add (new JLabel());
    }
    
    private final JColorCheckBox.JBoolean jEnabled;

    private final ActionListener jEnabledListener = (final ActionEvent ae) ->
    {
      final Boolean currentValue = JTek2440ChannelPanel.this.jEnabled.getDisplayedValue ();
      final boolean newValue = currentValue == null || (! currentValue);
      try
      {
        final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
        tek.setChannelEnable (JTek2440ChannelPanel.this.channel, newValue);
        if (! newValue)
          getTracePanel ().getJTrace ().setTrace (JTek2440ChannelPanel.this.channel, null);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while setting channel enable {0} on instrument {1}: {2}.",        
          new Object[]
            {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    };
  
    private final JComboBox<Tek2440_GPIB_Settings.VoltsPerDivision> jVoltsPerDiv;
    
    private final ItemListener jVoltsPerDivListener = (ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final Tek2440_GPIB_Settings.VoltsPerDivision newValue = (Tek2440_GPIB_Settings.VoltsPerDivision) ie.getItem ();
        final Tek2440_GPIB_Instrument tek2440 = ((Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ());
        if (! JTek2440_GPIB.this.inhibitInstrumentControl)
          try
          {
            tek2440.setVoltsPerDiv (JTek2440ChannelPanel.this.channel, newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.WARNING, "Caught exception while setting V/Div on instrument"
              + " from combo box to {0}: {1}.",
              new Object[]{newValue, Arrays.toString (e.getStackTrace ())});
          }
      }
    };
    
    private final JComboBox<Tek2440_GPIB_Settings.ChannelCoupling> jCoupling;
  
    private final ItemListener jCouplingListener = (ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        final Tek2440_GPIB_Settings.ChannelCoupling newValue = (Tek2440_GPIB_Settings.ChannelCoupling) ie.getItem ();
        final Tek2440_GPIB_Instrument tek2440 = ((Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ());
        if (! JTek2440_GPIB.this.inhibitInstrumentControl)
          try
          {
            tek2440.setChannelCoupling (JTek2440ChannelPanel.this.channel, newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.WARNING, "Caught exception while setting channel coupling on instrument"
              + " from combo box to {0}: {1}.",
              new Object[]{newValue, Arrays.toString (e.getStackTrace ())});
          }
      }
    };
    
    private final JColorCheckBox.JBoolean jFifty;
    
    private final ActionListener jFiftyListener = (final ActionEvent ae) ->
    {
      final Boolean currentValue = JTek2440ChannelPanel.this.jFifty.getDisplayedValue ();
      final boolean newValue = currentValue == null || (! currentValue);
      try
      {
        final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
        tek.setChannelFiftyOhms (JTek2440ChannelPanel.this.channel, newValue);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while setting channel 50 Ohms {0} on instrument {1}: {2}.",        
          new Object[]
            {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    };
  
    private final JColorCheckBox.JBoolean jInvert;
    
    private final ActionListener jInvertListener = (final ActionEvent ae) ->
    {
      final Boolean currentValue = JTek2440ChannelPanel.this.jInvert.getDisplayedValue ();
      final boolean newValue = currentValue == null || (! currentValue);
      try
      {
        final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
        tek.setChannelInvert (JTek2440ChannelPanel.this.channel, newValue);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while setting channel invert {0} on instrument {1}: {2}.",        
          new Object[]
            {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    };
  
    private final JSlider jPosition;
    
    private final ChangeListener jPositionChangeListener = (final ChangeEvent ce) ->
    {
      final JSlider source = (JSlider) ce.getSource ();
      source.setToolTipText (Integer.toString (source.getValue ()));
      if (! source.getValueIsAdjusting ())
      {
        if (! JTek2440_GPIB.this.inhibitInstrumentControl)
          try
          {
            final double newValue = source.getValue ();
            final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
            tek.setChannelPosition (JTek2440ChannelPanel.this.channel, newValue);
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting channel position from slider to {0}: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
    };
  
  }
    
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
      if (instrument != JTek2440_GPIB.this.getDigitalStorageOscilloscope () || instrumentStatus == null)
        throw new IllegalArgumentException ();
      if (! (instrumentStatus instanceof Tek2440_GPIB_Status))
        throw new IllegalArgumentException ();
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB.this.jSerialPollStatus.setDisplayedValue (
          ((Tek2440_GPIB_Status) instrumentStatus).getSerialPollStatusByte ());
      });
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      if (instrument != JTek2440_GPIB.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB.this.inhibitInstrumentControl = true;
        try
        {
          JTek2440_GPIB.this.xPanel.jChATimebase.setSelectedItem (settings.getASecondsPerDivision ());
          JTek2440_GPIB.this.xPanel.jChBTimebase.setSelectedItem (settings.getBSecondsPerDivision ());
          JTek2440_GPIB.this.jCh1Panel.jEnabled.setDisplayedValue (settings.isVModeChannel1 ());
          JTek2440_GPIB.this.jCh2Panel.jEnabled.setDisplayedValue (settings.isVModeChannel2 ());
          if (! settings.isVModeChannel1 ())
            getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, null);
          if (! settings.isVModeChannel2 ())
            getTracePanel ().getJTrace ().setTrace (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, null);
          JTek2440_GPIB.this.jCh1Panel.jVoltsPerDiv.setSelectedItem (settings.getAVoltsPerDivision ());
          JTek2440_GPIB.this.jCh2Panel.jVoltsPerDiv.setSelectedItem (settings.getBVoltsPerDivision ());
          JTek2440_GPIB.this.jCh1Panel.jCoupling.setSelectedItem
            (settings.getChannelCoupling (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1));
          JTek2440_GPIB.this.jCh2Panel.jCoupling.setSelectedItem
            (settings.getChannelCoupling (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2));
          JTek2440_GPIB.this.jCh1Panel.jFifty.setDisplayedValue
            (settings.isFiftyOhms (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1));
          JTek2440_GPIB.this.jCh2Panel.jFifty.setDisplayedValue
            (settings.isFiftyOhms (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2));
          JTek2440_GPIB.this.jCh1Panel.jInvert.setDisplayedValue
            (settings.isInvert (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1));
          JTek2440_GPIB.this.jCh2Panel.jInvert.setDisplayedValue
            (settings.isInvert (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2));
          final int ch1PositionDiv = (int) Math.round (settings.getPosition (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1));
          final int ch2PositionDiv = (int) Math.round (settings.getPosition (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2));
          JTek2440_GPIB.this.jCh1Panel.jPosition.setValue (ch1PositionDiv);
          JTek2440_GPIB.this.jCh1Panel.jPosition.setToolTipText (Integer.toString (ch1PositionDiv));
          JTek2440_GPIB.this.jCh2Panel.jPosition.setValue (ch2PositionDiv);
          JTek2440_GPIB.this.jCh2Panel.jPosition.setToolTipText (Integer.toString (ch2PositionDiv));
        }
        finally
        {
          JTek2440_GPIB.this.inhibitInstrumentControl = false;
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
