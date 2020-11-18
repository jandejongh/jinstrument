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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jtrace.JTrace;

/** A Swing panel for the Horizontal settings of a {@link Tek2440_GPIB_Instrument} Digital Storage Oscilloscope.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTek2440_GPIB_Channel
  extends JDigitalStorageOscilloscopePanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTek2440_GPIB_Channel.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JTek2440_GPIB_Channel (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final Tek2440_GPIB_Instrument.Tek2440Channel channel,
    final Color channelColor,
    final String title,
    final int level,
    final Color panelColor,
    final Consumer<Boolean> channelEnableDisableListener)
  {
    
    super (digitalStorageOscilloscope, title, level, panelColor);
    if (! (digitalStorageOscilloscope instanceof Tek2440_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument tek2440 = (Tek2440_GPIB_Instrument) digitalStorageOscilloscope;

    if (channel == null)
      throw new IllegalArgumentException ();
    this.channel = channel;
    
    if (channelColor == null)
      throw new IllegalArgumentException ();
    
    this.channelEnableDisableListener = channelEnableDisableListener;
    
    removeAll ();
    setLayout (new GridLayout (6,2));
      
    add (new JLabel("Enable"));
    switch (channel)
    {
      case Channel1:
      case Channel2:
        this.jEnabled = new JColorCheckBox.JBoolean (channelColor);
        break;
      default:
        throw new IllegalArgumentException ();
    }
    this.jEnabled.setHorizontalAlignment (SwingConstants.CENTER);
    this.jEnabled.addActionListener (this.jEnabledListener);
    add (this.jEnabled);
    
    add (new JLabel ("V/Div"));
    this.jVoltsPerDiv = new JComboBox<> (Tek2440_GPIB_Settings.VoltsPerDivision.values ());
    this.jVoltsPerDiv.setSelectedItem (null);
    this.jVoltsPerDiv.addItemListener (this.jVoltsPerDivListener);
    add (this.jVoltsPerDiv);

    add (new JLabel ("Var"));
    this.jVar = new JSlider (0, 100);
    this.jVar.setToolTipText ("-");
    this.jVar.setPreferredSize (new Dimension (100, 40));
    this.jVar.addChangeListener (this.jVarChangeListener);
    add (this.jVar);

    add (new JLabel ("Coupling"));
    this.jCoupling = new JComboBox<> (Tek2440_GPIB_Settings.ChannelCoupling.values ());
    this.jCoupling.setSelectedItem (null);
    this.jCoupling.addItemListener (this.jCouplingListener);
    add (this.jCoupling);

    final JPanel j50Combi = new JPanel ();
    j50Combi.setLayout (new GridLayout (1, 2));
    j50Combi.add (new JLabel ("50 \u03A9"));
    this.jFifty = new JColorCheckBox.JBoolean (Color.green);
    this.jFifty.setHorizontalAlignment (SwingConstants.CENTER);
    this.jFifty.addActionListener (this.jFiftyListener);
    j50Combi.add (this.jFifty);
    add (j50Combi);

    final JPanel jInvertCombi = new JPanel ();
    jInvertCombi.setLayout (new GridLayout (1, 2));
    jInvertCombi.add (new JLabel ("Invert"));
    this.jInvert = new JColorCheckBox.JBoolean (Color.green);
    this.jInvert.setHorizontalAlignment (SwingConstants.CENTER);
    this.jInvert.addActionListener (this.jInvertListener);
    jInvertCombi.add (this.jInvert);
    add (jInvertCombi);

    add (new JLabel ("Y Pos [DIV]"));
    this.jPosition = new JSlider (-10, 10);
    this.jPosition.setPreferredSize (new Dimension (100, 40));
    this.jPosition.setMajorTickSpacing (10);
    this.jPosition.setMinorTickSpacing (1);
    this.jPosition.setPaintTicks (true);
    this.jPosition.setToolTipText ("-");
    this.jPosition.addChangeListener (this.jPositionChangeListener);
    add (this.jPosition);
    
    getDigitalStorageOscilloscope ().addInstrumentListener (this.instrumentListener);
    
  }

  public JTek2440_GPIB_Channel (
    final DigitalStorageOscilloscope digitalStorageOscilloscope,
    final Tek2440_GPIB_Instrument.Tek2440Channel channel,
    final Color channelColor,
    final String title,
    final int level,
    final Color panelColor)
  {
    this (digitalStorageOscilloscope, channel, channelColor, title, level, panelColor, null);
  }
  
  public JTek2440_GPIB_Channel (
    final Tek2440_GPIB_Instrument digitalStorageOscilloscope,
    final Tek2440_GPIB_Instrument.Tek2440Channel channel,
    final Color channelColor,
    final int level)
  {
    this (digitalStorageOscilloscope, channel, channelColor, null, level, null, null);
  }
  
  public JTek2440_GPIB_Channel (
    final Tek2440_GPIB_Instrument digitalStorageOscilloscope,
    final Tek2440_GPIB_Instrument.Tek2440Channel channel,
    final Color channelColor)
  {
    this (digitalStorageOscilloscope, channel, channelColor, null, 0, null, null);
  }
  
  public JTek2440_GPIB_Channel (
    final Tek2440_GPIB_Instrument digitalStorageOscilloscope,
    final Tek2440_GPIB_Instrument.Tek2440Channel channel)
  {
    this (digitalStorageOscilloscope, channel, defaultColorForChannel (channel), null, 0, null, null);
  }
  
  public static JTek2440_GPIB_Channel defaultForChannel1 (
    final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
  {
    return new JTek2440_GPIB_Channel (
      digitalStorageOscilloscope,
      Tek2440_GPIB_Instrument.Tek2440Channel.Channel1);
  }
  
  public static JTek2440_GPIB_Channel defaultForChannel2 (
    final Tek2440_GPIB_Instrument digitalStorageOscilloscope)
  {
    return new JTek2440_GPIB_Channel (
      digitalStorageOscilloscope,
      Tek2440_GPIB_Instrument.Tek2440Channel.Channel2);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CHANNEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Tek2440_GPIB_Instrument.Tek2440Channel channel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT CHANNEL COLORS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static Color DEFAULT_COLOR_CHANNEL_1 = JTrace.DEFAULT_COLORS[0];
  
  public final static Color DEFAULT_COLOR_CHANNEL_2 = JTrace.DEFAULT_COLORS[1];
  
  public static Color defaultColorForChannel (final Tek2440_GPIB_Instrument.Tek2440Channel channel)
  {
    switch (channel)
    {
      case Channel1: return DEFAULT_COLOR_CHANNEL_1;
      case Channel2: return DEFAULT_COLOR_CHANNEL_2;
      default: throw new IllegalArgumentException ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CHANNEL ENABLE/DISABLE STATUS LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Consumer<Boolean> channelEnableDisableListener;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT VIEW TYPES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static InstrumentViewType INSTRUMENT_CHANNEL_1_VIEW_TYPE = new InstrumentViewType ()
  {
    
    @Override
    public final String getInstrumentViewTypeUrl ()
    {
      return "Tektronix 2440 [GPIB] Channel 1 Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return defaultForChannel1 ((Tek2440_GPIB_Instrument) instrument);
      else
        return null;
    }
    
  };

  public final static InstrumentViewType INSTRUMENT_CHANNEL_2_VIEW_TYPE = new InstrumentViewType ()
  {
    
    @Override
    public final String getInstrumentViewTypeUrl ()
    {
      return "Tektronix 2440 [GPIB] Channel 2 Settings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof Tek2440_GPIB_Instrument))
        return defaultForChannel2 ((Tek2440_GPIB_Instrument) instrument);
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
    switch (this.channel)
    {
      case Channel1:
        return JTek2440_GPIB_Channel.INSTRUMENT_CHANNEL_1_VIEW_TYPE.getInstrumentViewTypeUrl ()
          + "<>"
          + getInstrument ().getInstrumentUrl ();
      case Channel2:
        return JTek2440_GPIB_Channel.INSTRUMENT_CHANNEL_2_VIEW_TYPE.getInstrumentViewTypeUrl ()
          + "<>"
          + getInstrument ().getInstrumentUrl ();
      default:
        throw new RuntimeException ();
    }
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
  
  private final JColorCheckBox.JBoolean jEnabled;

  private final ActionListener jEnabledListener = (final ActionEvent ae) ->
  {
    final Boolean currentValue = JTek2440_GPIB_Channel.this.jEnabled.getDisplayedValue ();
    final boolean newValue = currentValue == null || (! currentValue);
    if (JTek2440_GPIB_Channel.this.channelEnableDisableListener != null)
      JTek2440_GPIB_Channel.this.channelEnableDisableListener.accept (newValue);
    try
    {
      final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
      tek.setChannelEnable (JTek2440_GPIB_Channel.this.channel, newValue);
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
      if (! JTek2440_GPIB_Channel.this.isInhibitInstrumentControl ())
        try
        {
          tek2440.setVoltsPerDiv (JTek2440_GPIB_Channel.this.channel, newValue);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.WARNING, "Caught exception while setting V/Div on instrument"
            + " from combo box to {0}: {1}.",
            new Object[]{newValue, Arrays.toString (e.getStackTrace ())});
        }
    }
  };
    
  private final JSlider jVar;
       
  private final ChangeListener jVarChangeListener = (final ChangeEvent ce) ->
  {
    final JSlider source = (JSlider) ce.getSource ();
    source.setToolTipText (Integer.toString (source.getValue ()));
    if (! source.getValueIsAdjusting ())
    {
      if (! JTek2440_GPIB_Channel.this.isInhibitInstrumentControl ())
        try
        {
          final double newValue = source.getValue ();
          final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
          tek.setChannelVariableY (JTek2440_GPIB_Channel.this.channel, newValue);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting channel variable-Y from slider to {0}: {1}.",
            new Object[]{source.getValue (), e});          
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
      if (! JTek2440_GPIB_Channel.this.isInhibitInstrumentControl ())
        try
        {
          tek2440.setChannelCoupling (JTek2440_GPIB_Channel.this.channel, newValue);
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
    final Boolean currentValue = JTek2440_GPIB_Channel.this.jFifty.getDisplayedValue ();
    final boolean newValue = currentValue == null || (! currentValue);
    try
    {
      final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
      tek.setChannelFiftyOhms (JTek2440_GPIB_Channel.this.channel, newValue);
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
    final Boolean currentValue = JTek2440_GPIB_Channel.this.jInvert.getDisplayedValue ();
    final boolean newValue = currentValue == null || (! currentValue);
    try
    {
      final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
      tek.setChannelInvert (JTek2440_GPIB_Channel.this.channel, newValue);
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
      if (! JTek2440_GPIB_Channel.this.isInhibitInstrumentControl ())
        try
        {
          final double newValue = source.getValue ();
          final Tek2440_GPIB_Instrument tek = (Tek2440_GPIB_Instrument) getDigitalStorageOscilloscope ();
          tek.setChannelPosition (JTek2440_GPIB_Channel.this.channel, newValue);
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting channel position from slider to {0}: {1}.",
            new Object[]{source.getValue (), e});
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
      if (instrument != JTek2440_GPIB_Channel.this.getDigitalStorageOscilloscope () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof Tek2440_GPIB_Settings))
        throw new IllegalArgumentException ();
      final Tek2440_GPIB_Instrument.Tek2440Channel channel = JTek2440_GPIB_Channel.this.channel;
      final Tek2440_GPIB_Settings settings = (Tek2440_GPIB_Settings) instrumentSettings;
      switch (channel)
      {
        case Channel1:
        case Channel2:
          break;
        default:
          throw new IllegalArgumentException ();
      }
      SwingUtilities.invokeLater (() ->
      {
        JTek2440_GPIB_Channel.this.setInhibitInstrumentControl ();
        try
        {
          JTek2440_GPIB_Channel.this.jEnabled.setDisplayedValue (
            settings.isVModeChannel (channel));
          JTek2440_GPIB_Channel.this.jVoltsPerDiv.setSelectedItem (
            settings.getVoltsPerDivision (channel));
          JTek2440_GPIB_Channel.this.jVar.setValue (
            (int) Math.round (settings.getVariableVoltsPerDivision (channel)));
          JTek2440_GPIB_Channel.this.jVar.setToolTipText (
            Integer.toString ((int) Math.round (settings.getVariableVoltsPerDivision (channel))));
          JTek2440_GPIB_Channel.this.jCoupling.setSelectedItem (
            settings.getChannelCoupling (channel));
          JTek2440_GPIB_Channel.this.jFifty.setDisplayedValue (
            settings.isFiftyOhms (channel));
          JTek2440_GPIB_Channel.this.jInvert.setDisplayedValue (
            settings.isInvert (channel));
          final int chPositionDiv = (int) Math.round (
            settings.getPosition (channel));
          JTek2440_GPIB_Channel.this.jPosition.setValue (
            chPositionDiv);
          JTek2440_GPIB_Channel.this.jPosition.setToolTipText (
            Integer.toString (chPositionDiv));
        }
        finally
        {
          JTek2440_GPIB_Channel.this.resetInhibitInstrumentControl ();
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
