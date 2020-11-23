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
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import org.javajdj.jinstrument.DigitalStorageOscilloscope;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.dso.tek2440.Tek2440_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JDigitalStorageOscilloscopePanel;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jtrace.legacy.JTraceLegacy;

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
    
    removeAll ();
    setLayout (new GridLayout (7,2));
      
    add (new JLabel("Enable"));
    add (JCenter.Y (new JBoolean_JBoolean (
      "Enable on channel " + channel,
      (settings) -> ((Tek2440_GPIB_Settings) settings).isVModeChannel (channel),
      (enable) -> tek2440.setChannelEnable (channel, enable),
      channelColor,
      channelEnableDisableListener,
      true)));
    
    add (new JLabel ("V/Div"));
    add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.VoltsPerDivision.class,
      "V/Div on channel " + channel,
      (settings) -> ((Tek2440_GPIB_Settings) settings).getVoltsPerDivision (channel),
      (voltsPerDivision) -> tek2440.setVoltsPerDiv (channel, voltsPerDivision),
      true)));

    add (new JLabel ("Var"));
    final JSlider jVar = new JDouble_JSlider (
      SwingConstants.HORIZONTAL,
      0.0,
      100.0,
      0.0,
      "var [Y/Div] on channel " + channel,
      (settings) -> ((Tek2440_GPIB_Settings) settings).getVariableVoltsPerDivision (channel),
      (var) -> tek2440.setChannelVariableY (channel, var),
      true);
    jVar.setPreferredSize (new Dimension (100, 40));
    add (JCenter.Y (jVar));

    add (new JLabel ("Coupling"));
    add (JCenter.Y (new JEnum_JComboBox<> (
      Tek2440_GPIB_Settings.ChannelCoupling.class,
      "coupling on channel" + channel,
      (settings) -> ((Tek2440_GPIB_Settings) settings).getChannelCoupling (channel),
      (coupling) -> tek2440.setChannelCoupling (channel, coupling),
      true)));

    add (new JLabel ("50 \u03A9"));
    add (JCenter.Y (new JBoolean_JBoolean (
      "50 \u03A9 on channel " + channel,
      (settings) -> ((Tek2440_GPIB_Settings) settings).isFiftyOhms (channel),
      (enable) -> tek2440.setChannelFiftyOhms (channel, enable),
      Color.green,
      true)));

    add (new JLabel ("Invert"));
    add (JCenter.Y (new JBoolean_JBoolean (
      "invert on channel " + channel,
      (settings) -> ((Tek2440_GPIB_Settings) settings).isInvert (channel),
      (enable) -> tek2440.setChannelInvert (channel, enable),
      Color.green,
      true)));

    add (new JLabel ("Y Pos [DIV]"));
    final JSlider jPosition = new JDouble_JSlider (
      SwingConstants.HORIZONTAL,
      -10.0,
      +10.0,
      0.0,
      "Y position for channel " + channel,
      (settings) -> ((Tek2440_GPIB_Settings) settings).getPosition (channel),
      (position) -> tek2440.setChannelPosition (channel, position),
      true);
    jPosition.setPreferredSize (new Dimension (100, 40));
    jPosition.setMajorTickSpacing (10);
    jPosition.setMinorTickSpacing (1);
    jPosition.setPaintTicks (true);
    add (JCenter.Y (jPosition));
    
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
  
  public final static Color DEFAULT_COLOR_CHANNEL_1 = JTraceLegacy.DEFAULT_COLORS[0];
  
  public final static Color DEFAULT_COLOR_CHANNEL_2 = JTraceLegacy.DEFAULT_COLORS[1];
  
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
