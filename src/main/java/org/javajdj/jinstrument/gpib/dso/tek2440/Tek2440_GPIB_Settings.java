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
package org.javajdj.jinstrument.gpib.dso.tek2440;

import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultDigitalStorageOscilloscopeSettings;
import org.javajdj.jinstrument.DigitalStorageOscilloscopeSettings;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.Unit;

/** Implementation of {@link InstrumentSettings}
 *  and {@link DigitalStorageOscilloscopeSettings} for the Tektronix-2440.
 *
 * <p>
 * Implementation is <i>not</i> complete.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class Tek2440_GPIB_Settings
  extends DefaultDigitalStorageOscilloscopeSettings
  implements DigitalStorageOscilloscopeSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (Tek2440_GPIB_Settings.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected Tek2440_GPIB_Settings (
    final byte[] bytes,
    final Unit unit,
    final AutoSetupSettings autoSetup,
    final HorizontalSettings horizontalSettings,
    final ChannelSettings ch1Settings,
    final ChannelSettings ch2Settings,
    final DataSettings dataSettings,
    final VModeSettings vModeSettings)
  {
    super (
      bytes,
      unit);
    if (autoSetup == null)
      throw new IllegalArgumentException ();
    this.autoSetupSettings = autoSetup;
    if (horizontalSettings == null)
      throw new IllegalArgumentException ();
    this.horizontalSettings = horizontalSettings;
    if (ch1Settings == null)
      throw new IllegalArgumentException ();
    this.channelSettings.put (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1, ch1Settings);
    if (ch2Settings == null)
      throw new IllegalArgumentException ();
    this.channelSettings.put (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2, ch2Settings);
    if (dataSettings == null)
      throw new IllegalArgumentException ();
    this.dataSettings = dataSettings;
    if (vModeSettings == null)
      throw new IllegalArgumentException ();
    this.vModeSettings = vModeSettings;
  }

  public static Tek2440_GPIB_Settings fromSetData (final byte[] bytes)
  {
    return parserFromSetData (bytes);
  }
  
  public static Tek2440_GPIB_Settings fromLlSetData (final byte[] bytes)
  {
    // XXX
    throw new UnsupportedOperationException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PARSER ['SET?']
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  private static Tek2440_GPIB_Settings parserFromSetData (final byte[] bytes)
  {
    final String[] parts = new String (bytes, Charset.forName ("US-ASCII")).split (";");
    AutoSetupSettings autoSetupSettings = null;
    HorizontalSettings horizontalSettings = null;
    ChannelSettings ch1Settings = null;
    ChannelSettings ch2Settings = null;
    DataSettings dataSettings = null;
    VModeSettings vModeSettings = null;
    for (final String part : parts)
    {
      final String[] partParts = part.trim ().split (" ", 2);
      final String keyString = partParts[0].trim ().toLowerCase ();
      final String argString = partParts[1].trim ().toLowerCase ();
      switch (keyString)
      {
        case "autosetup":
          autoSetupSettings = parseAutoSetupSettings (argString);
          break;
        case "horizontal":
          horizontalSettings = parseHorizontalSettings (argString);
          break;
        case "ch1":
          ch1Settings = parseChannelSettings (argString, Tek2440_GPIB_Instrument.Tek2440Channel.Channel1);
          break;
        case "ch2":
          ch2Settings = parseChannelSettings (argString, Tek2440_GPIB_Instrument.Tek2440Channel.Channel2);
          break;
        case "data":
          dataSettings = parseDataSettings (argString);
          break;
        case "vmo":
        case "vmode":
          vModeSettings = parseVModeSettings (argString);
          break;
        // XXX ParseException of IllegalArgumentException later...
        default:
          // System.err.println ("UNKNOWN key=" + keyString + ", arg=" + argString + ".");      
      }
    }
    return new Tek2440_GPIB_Settings (
      bytes,
      Unit.UNIT_V,
      autoSetupSettings,
      horizontalSettings,
      ch1Settings,
      ch2Settings,
      dataSettings,
      vModeSettings);    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AUTOSETUP SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum AutoSetupMode
  {
    FALL,
    PERIOD,
    PULSE,
    RISE,
    VIEW
  }
  
  public static enum AutoSetupResolution
  {
    HIGH,
    LOW
  }
  
  public static final class AutoSetupSettings
  {
    
    private final AutoSetupMode mode;
    
    private final AutoSetupResolution resolution;

    public final AutoSetupMode getMode ()
    {
      return this.mode;
    }

    public final AutoSetupResolution getResolution ()
    {
      return this.resolution;
    }

    public AutoSetupSettings (final AutoSetupMode mode, final AutoSetupResolution resolution)
    {
      if (mode == null || resolution == null)
        throw new IllegalArgumentException ();
      this.mode = mode;
      this.resolution = resolution;
    }
    
  }
  
  private final AutoSetupSettings autoSetupSettings;
  
  public final AutoSetupSettings getAutoSetupSettings ()
  {
    return this.autoSetupSettings;
  }
  
  public final AutoSetupMode getAutoSetupMode ()
  {
    return getAutoSetupSettings ().getMode ();
  }
  
  public final AutoSetupResolution getAutoSetupResolution ()
  {
    return getAutoSetupSettings ().getResolution ();
  }
  
  private static AutoSetupSettings parseAutoSetupSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    AutoSetupMode mode = null;
    AutoSetupResolution resolution = null;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    for (final String argPart: argParts)
    {
      final String[] argArgParts = argPart.trim ().split (":");
      if (argArgParts == null || argArgParts.length != 2)
        throw new IllegalArgumentException ("autosetup");
      final String argKey = argArgParts[0].trim ();
      switch (argKey)
      {
        case "mode":
        {
          switch (argArgParts[1].trim ())
          {
            case "fall":   mode = AutoSetupMode.FALL; break;
            case "period": mode = AutoSetupMode.PERIOD; break;
            case "pulse":  mode = AutoSetupMode.PULSE; break;
            case "rise":   mode = AutoSetupMode.RISE; break;
            case "view": mode = AutoSetupMode.VIEW; break;
            default: throw new IllegalArgumentException ("autosetup - mode");
          }
          // System.err.println ("  Found autosetup/mode definition: " + argPart + ".");
          break;
        }
        case "resolution":
        {
          switch (argArgParts[1].trim ())
          {
            case "hi": resolution = AutoSetupResolution.HIGH; break;
            case "lo": resolution = AutoSetupResolution.LOW; break;
            default: throw new IllegalArgumentException ("autosetup - resolution");
          }
          // System.err.println ("  Found autosetup/resolution definition: " + argPart + ".");
          break;
        }
        default:
          throw new IllegalArgumentException ();
      }
    }
    return new AutoSetupSettings (mode, resolution);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // HORIZONTAL SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum SecondsPerDivision
  {
    
    SecondsPerDivision_2ns    (2E-9,   2, Unit.UNIT_ns),
    SecondsPerDivision_5ns    (5E-9,   5, Unit.UNIT_ns),
    SecondsPerDivision_10ns   (1E-8,  10, Unit.UNIT_ns),
    SecondsPerDivision_20ns   (2E-8,  20, Unit.UNIT_ns),
    SecondsPerDivision_50ns   (5E-8,  50, Unit.UNIT_ns),
    SecondsPerDivision_100ns  (1E-7, 100, Unit.UNIT_ns),
    SecondsPerDivision_200ns  (2E-7, 200, Unit.UNIT_ns),
    SecondsPerDivision_500ns  (5E-7, 500, Unit.UNIT_ns),
    SecondsPerDivision_1mus   (1E-6,   1, Unit.UNIT_mus),
    SecondsPerDivision_2mus   (2E-6,   2, Unit.UNIT_mus),
    SecondsPerDivision_5mus   (5E-6,   5, Unit.UNIT_mus),
    SecondsPerDivision_10mus  (1E-5,  10, Unit.UNIT_mus),
    SecondsPerDivision_20mus  (2E-5,  20, Unit.UNIT_mus),
    SecondsPerDivision_50mus  (5E-5,  50, Unit.UNIT_mus),
    SecondsPerDivision_100mus (1E-4, 100, Unit.UNIT_mus),
    SecondsPerDivision_200mus (2E-4, 200, Unit.UNIT_mus),
    SecondsPerDivision_500mus (5E-4, 500, Unit.UNIT_mus),
    SecondsPerDivision_1ms    (1E-3,   1, Unit.UNIT_ms),
    SecondsPerDivision_2ms    (2E-3,   2, Unit.UNIT_ms),
    SecondsPerDivision_5ms    (5E-3,   5, Unit.UNIT_ms),
    SecondsPerDivision_10ms   (1E-2,  10, Unit.UNIT_ms),
    SecondsPerDivision_20ms   (2E-2,  20, Unit.UNIT_ms),
    SecondsPerDivision_50ms   (5E-2,  50, Unit.UNIT_ms),
    SecondsPerDivision_100ms  (1E-1, 100, Unit.UNIT_ms),
    SecondsPerDivision_200ms  (2E-1, 200, Unit.UNIT_ms),
    SecondsPerDivision_500ms  (5E-1, 500, Unit.UNIT_ms),
    SecondsPerDivision_1s     (1E0,    1, Unit.UNIT_s),
    SecondsPerDivision_2s     (2E0,    2, Unit.UNIT_s),
    SecondsPerDivision_5s     (5E0,    5, Unit.UNIT_s);

    private SecondsPerDivision (
      final double secondsPerDivision_s,
      final int value,
      final Unit unit)
    {
      this.secondsPerDivision_s = secondsPerDivision_s;
      this.value = value;
      this.unit = unit;
    }
    
    public static SecondsPerDivision fromDouble (final double secDivDouble_s)
    {
      if (! Double.isFinite (secDivDouble_s))
        throw new IllegalArgumentException ();
      if (secDivDouble_s <= 0)
        throw new IllegalArgumentException ();
      for (final SecondsPerDivision spd : SecondsPerDivision.values ())
        if (Math.abs (spd.getSecondsPerDivision_s () - secDivDouble_s) / spd.getSecondsPerDivision_s () < 1.0E-4)
          return spd;
      throw new IllegalArgumentException ();
    }
    
    private final double secondsPerDivision_s;
    
    public final double getSecondsPerDivision_s ()
    {
      return this.secondsPerDivision_s;
    }
    
    private final int value;
    
    private final Unit unit;

    @Override
    public String toString ()
    {
      return Integer.toString (this.value) + " " + this.unit.toString ();
    }
    
  }
  
  public static enum HorizontalExternalExpansionFactor
  {
    
    HorExtExp_1,
    HorExtExp_2,
    HorExtExp_5,
    HorExtExp_10,
    HorExtExp_20,
    HorExtExp_50,
    HorExtExp_100;
    
    public static HorizontalExternalExpansionFactor fromInteger (final int heefInt)
    {
      switch (heefInt)
      {
        case   1: return HorExtExp_1;
        case   2: return HorExtExp_2;
        case   5: return HorExtExp_5;
        case  10: return HorExtExp_10;
        case  20: return HorExtExp_20;
        case  50: return HorExtExp_50;
        case 100: return HorExtExp_100;
        default: throw new IllegalArgumentException ();
      }
    }
    
  }
  
  public static enum HorizontalMode
  {
    AInTb,
    ASweep,
    BSweep;
  }
  
  public static class HorizontalSettings
  {
    private final SecondsPerDivision aSecDiv;
    private final SecondsPerDivision bSecDiv;
    private final HorizontalExternalExpansionFactor extExp;
    private final HorizontalMode mode;
    private final Double position;

    public HorizontalSettings (
      final SecondsPerDivision aSecDiv,
      final SecondsPerDivision bSecDiv,
      final HorizontalExternalExpansionFactor extExp,
      final HorizontalMode mode,
      final Double position)
    {
      if (aSecDiv == null)
        throw new IllegalArgumentException ();
      this.aSecDiv = aSecDiv;
      if (bSecDiv == null)
        throw new IllegalArgumentException ();
      this.bSecDiv = bSecDiv;
      if (extExp == null)
        throw new IllegalArgumentException ();
      this.extExp = extExp;
      if (mode == null)
        throw new IllegalArgumentException ();
      this.mode = mode;
      if (position == null || position < 0 || position > 1023)
        throw new IllegalArgumentException ();
      this.position = position;
    }
    
  }
  
  private final HorizontalSettings horizontalSettings;
  
  public final HorizontalSettings getHorizontalSettings ()
  {
    return this.horizontalSettings;
  }
      
  public final SecondsPerDivision getASecondsPerDivision ()
  {
    return getHorizontalSettings ().aSecDiv;
  }
  
  public final SecondsPerDivision getBSecondsPerDivision ()
  {
    return getHorizontalSettings ().bSecDiv;
  }
  
  public final HorizontalExternalExpansionFactor getHorizontalExternalExpansionFactor ()
  {
    return getHorizontalSettings ().extExp;
  }
  
  public final HorizontalMode getHorizontalMode ()
  {
    return getHorizontalSettings ().mode;
  }
  
  public final double getHorizontalPosition ()
  {
    return getHorizontalSettings ().position;
  }
  
  private static HorizontalSettings parseHorizontalSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    SecondsPerDivision aSecDiv = null;
    SecondsPerDivision bSecDiv = null;
    HorizontalExternalExpansionFactor extExp = null;
    HorizontalMode mode = null;
    Double position = null;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    for (final String argPart: argParts)
    {
      final String[] argArgParts = argPart.trim ().split (":");
      if (argArgParts == null || argArgParts.length != 2)
        throw new IllegalArgumentException ();
      final String argKey = argArgParts[0].trim ();
      boolean isASecDiv = false;
      switch (argKey)
      {
        case "asecdiv":
          isASecDiv = true;
          // Intended fallthrough.
        case "bsecdiv":
        {
          try
          {
            final double secDivDouble_s = Double.parseDouble (argArgParts[1].trim ());
            if (isASecDiv)
              aSecDiv = SecondsPerDivision.fromDouble (secDivDouble_s);
            else
              bSecDiv = SecondsPerDivision.fromDouble (secDivDouble_s);
          }
          catch (NumberFormatException nfe)
          {
            throw new IllegalArgumentException ();                          
          }
          // System.err.println ("  Found horizontal/" + argKey + " definition: " + argPart + ".");
          break;
        }
        case "extexp":
        {
          try
          {
            extExp = HorizontalExternalExpansionFactor.fromInteger (Integer.parseInt (argArgParts[1].trim ()));
          }
          catch (NumberFormatException nfe)
          {
            throw new IllegalArgumentException ();
          }
          // System.err.println ("  Found horizontal/extexp definition: " + argPart + ".");
          break;
        }
        case "mode":
        {
          switch (argArgParts[1].trim ())
          {
            case "aintb":  mode = HorizontalMode.AInTb;  break;
            case "asweep": mode = HorizontalMode.ASweep; break;
            case "bsweep": mode = HorizontalMode.BSweep; break;
            default: throw new IllegalArgumentException ();
          }
          // System.err.println ("  Found horizontal/mode definition: " + argPart + ".");
          break;
        }
        case "position":
        {
          try
          {
            position = Double.parseDouble (argArgParts[1].trim ());
          }
          catch (NumberFormatException nfe)
          {
            throw new IllegalArgumentException ();
          }
          if (position < 0 || position > 1023)
            throw new IllegalArgumentException ();
          // System.err.println ("  Found horizontal/position definition: " + argPart + ".");
          break;
        }
        default:
          throw new IllegalArgumentException ();
      }
    }
    return new HorizontalSettings (aSecDiv, bSecDiv, extExp, mode, position);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CHANNEL SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum VoltsPerDivision
  {
    
    VoltsPerDivision_2nV     (2E-9,   2, Unit.UNIT_nV),
    VoltsPerDivision_5nV     (5E-9,   5, Unit.UNIT_nV),
    VoltsPerDivision_10nV    (1E-8,  10, Unit.UNIT_nV),
    VoltsPerDivision_20nV    (2E-8,  20, Unit.UNIT_nV),
    VoltsPerDivision_50nV    (5E-8,  50, Unit.UNIT_nV),
    VoltsPerDivision_100nV   (1E-7, 100, Unit.UNIT_nV),
    VoltsPerDivision_200nV   (2E-7, 200, Unit.UNIT_nV),
    VoltsPerDivision_500nV   (5E-7, 500, Unit.UNIT_nV),
    VoltsPerDivision_1muV    (1E-6,   1, Unit.UNIT_muV),
    VoltsPerDivision_2muV    (2E-6,   2, Unit.UNIT_muV),
    VoltsPerDivision_5muV    (5E-6,   5, Unit.UNIT_muV),
    VoltsPerDivision_10muV   (1E-5,  10, Unit.UNIT_muV),
    VoltsPerDivision_20muV   (2E-5,  20, Unit.UNIT_muV),
    VoltsPerDivision_50muV   (5E-5,  50, Unit.UNIT_muV),
    VoltsPerDivision_100muV  (1E-4, 100, Unit.UNIT_muV),
    VoltsPerDivision_200muV  (2E-4, 200, Unit.UNIT_muV),
    VoltsPerDivision_500muV  (5E-4, 500, Unit.UNIT_muV),
    VoltsPerDivision_1mV     (1E-3,   1, Unit.UNIT_mV),
    VoltsPerDivision_2mV     (2E-3,   2, Unit.UNIT_mV),
    VoltsPerDivision_5mV     (5E-3,   5, Unit.UNIT_mV),
    VoltsPerDivision_10mV    (1E-2,  10, Unit.UNIT_mV),
    VoltsPerDivision_20mV    (2E-2,  20, Unit.UNIT_mV),
    VoltsPerDivision_50mV    (5E-2,  50, Unit.UNIT_mV),
    VoltsPerDivision_100mV   (1E-1, 100, Unit.UNIT_mV),
    VoltsPerDivision_200mV   (2E-1, 200, Unit.UNIT_mV),
    VoltsPerDivision_500mV   (5E-1, 500, Unit.UNIT_mV),
    VoltsPerDivision_1V      (1E0,    1, Unit.UNIT_V),
    VoltsPerDivision_2V      (2E0,    2, Unit.UNIT_V),
    VoltsPerDivision_5V      (5E0,    5, Unit.UNIT_V),
    VoltsPerDivision_10V     (1E1,   10, Unit.UNIT_V),
    VoltsPerDivision_20V     (2E1,   20, Unit.UNIT_V),
    VoltsPerDivision_50V     (5E1,   50, Unit.UNIT_V),
    VoltsPerDivision_100V    (1E2,  100, Unit.UNIT_V);

    private VoltsPerDivision (
      final double voltsPerDivision_V,
      final int value,
      final Unit unit)
    {
      this.voltsPerDivision_V = voltsPerDivision_V;
      this.value = value;
      this.unit = unit;
    }
    
    public static VoltsPerDivision fromDouble (final double secDivDouble_V)
    {
      if (! Double.isFinite (secDivDouble_V))
        throw new IllegalArgumentException ();
      if (secDivDouble_V <= 0)
        throw new IllegalArgumentException ();
      for (final VoltsPerDivision spd : VoltsPerDivision.values ())
        if (Math.abs (spd.getVoltsPerDivision_V () - secDivDouble_V) / spd.getVoltsPerDivision_V () < 1.0E-4)
          return spd;
      throw new IllegalArgumentException ();
    }
    
    private final double voltsPerDivision_V;
    
    public final double getVoltsPerDivision_V ()
    {
      return this.voltsPerDivision_V;
    }
    
    private final int value;
    
    private final Unit unit;

    @Override
    public String toString ()
    {
      return Integer.toString (this.value) + " " + this.unit.toString ();
    }
    
  }
  
  public static enum ChannelCoupling
  {
    AC,
    DC,
    GND;
  }
  
  public static class ChannelSettings
  {
    
    private final ChannelCoupling channelCoupling;

    private final boolean fifty;
    
    private final boolean invert;
    
    private final double position;
    
    private final double variable;
    
    private final double volts;
    
    private final VoltsPerDivision voltsPerDivision;
    
    public ChannelSettings (
      final ChannelCoupling channelCoupling,
      final Boolean fifty,
      final Boolean invert,
      final Double position,
      final Double variable,
      final Double volts)
    {
      if (channelCoupling == null)
        throw new IllegalArgumentException ();
      this.channelCoupling = channelCoupling;
      if (fifty == null)
        throw new IllegalArgumentException ();
      this.fifty = fifty;
      if (invert == null)
        throw new IllegalArgumentException ();
      this.invert = invert;
      if (position == null || position < -10 || position > 10)
        throw new IllegalArgumentException ();
      this.position = position;
      if (variable == null || variable < 0 || variable > 100)
        throw new IllegalArgumentException ();
      this.variable = variable;
      if (volts == null || volts <= 0)
        throw new IllegalArgumentException ();
      this.volts = volts;
      this.voltsPerDivision = VoltsPerDivision.fromDouble (this.volts);
    }
    
  }
  
  public final Map<Tek2440_GPIB_Instrument.Tek2440Channel, ChannelSettings> channelSettings =
    new EnumMap<> (Tek2440_GPIB_Instrument.Tek2440Channel.class);
  
  public final ChannelSettings getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel channel)
  {
    if (channel == null)
      throw new IllegalArgumentException ();
    return this.channelSettings.get (channel);
  }
  
  public final ChannelCoupling getChannelCoupling (final Tek2440_GPIB_Instrument.Tek2440Channel channel)
  {
    if (channel == null)
      throw new IllegalArgumentException ();
    switch (channel)
    {
      case Channel1: return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1).channelCoupling;
      case Channel2: return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2).channelCoupling;
      default:
        throw new IllegalArgumentException ();
    }    
  }
  
  public final VoltsPerDivision getVoltsPerDivision (final Tek2440_GPIB_Instrument.Tek2440Channel channel)
  {
    if (channel == null)
      throw new IllegalArgumentException ();
    switch (channel)
    {
      case Channel1: return getAVoltsPerDivision ();
      case Channel2: return getBVoltsPerDivision ();
      default:
        throw new IllegalArgumentException ();
    }
  }
  
  public final VoltsPerDivision getAVoltsPerDivision ()
  {
    return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1).voltsPerDivision;
  }
  
  public final VoltsPerDivision getBVoltsPerDivision ()
  {
    return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2).voltsPerDivision;
  }
  
  public final boolean isFiftyOhms (final Tek2440_GPIB_Instrument.Tek2440Channel channel)
  {
    if (channel == null)
      throw new IllegalArgumentException ();
    switch (channel)
    {
      case Channel1: return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1).fifty;
      case Channel2: return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2).fifty;
      default:
        throw new IllegalArgumentException ();
    }    
  }
  
  public final boolean isInvert (final Tek2440_GPIB_Instrument.Tek2440Channel channel)
  {
    if (channel == null)
      throw new IllegalArgumentException ();
    switch (channel)
    {
      case Channel1: return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1).invert;
      case Channel2: return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2).invert;
      default:
        throw new IllegalArgumentException ();
    }    
  }
  
  public final double getPosition (final Tek2440_GPIB_Instrument.Tek2440Channel channel)
  {
    if (channel == null)
      throw new IllegalArgumentException ();
    switch (channel)
    {
      case Channel1: return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1).position;
      case Channel2: return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2).position;
      default:
        throw new IllegalArgumentException ();
    }    
  }
  
  private static ChannelSettings parseChannelSettings (final String argString, final Tek2440_GPIB_Instrument.Tek2440Channel channel)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    if (channel == null)
      throw new IllegalArgumentException ();
    ChannelCoupling channelCoupling = null;
    Boolean fifty = null;
    Boolean invert = null;
    Double position = null;
    Double variable = null;
    Double volts = null;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    for (final String argPart: argParts)
    {
      final String[] argArgParts = argPart.trim ().split (":");
      if (argArgParts == null || argArgParts.length != 2)
        throw new IllegalArgumentException ("ch");
      final String argKey = argArgParts[0].trim ();
      switch (argKey)
      {
        case "coupling":
        {
          switch (argArgParts[1].trim ())
          {
            case "ac":  channelCoupling = ChannelCoupling.AC;  break;
            case "dc":  channelCoupling = ChannelCoupling.DC;  break;
            case "gnd": channelCoupling = ChannelCoupling.GND; break;
            default: throw new IllegalArgumentException ("ch - " + channel + " - coupling");
          }
          // System.err.println ("  Found coupling definition: " + argPart + ", channel=" + channel + ".");
          break;
        }
        case "fifty":
        {
          switch (argArgParts[1].trim ())
          {
            case "on":  fifty = true;  break;
            case "off": fifty = false; break;
            default: throw new IllegalArgumentException ("ch - " + channel + " - fifty");
          }
          // System.err.println ("  Found fifty definition: " + argPart + ", channel=" + channel + ".");
          break;
        }
        case "invert":
        {
          switch (argArgParts[1].trim ())
          {
            case "on":  invert = true;  break;
            case "off": invert = false; break;
            default: throw new IllegalArgumentException ("ch - " + channel + " - invert");
          }
          // System.err.println ("  Found invert definition: " + argPart + ", channel=" + channel + ".");
          break;
        }
        case "position":
        {
          try
          {
            position = Double.parseDouble (argArgParts[1].trim ());
          }
          catch (NumberFormatException nfe)
          {
            throw new IllegalArgumentException ("ch - " + channel + " - position");              
          }
          // System.err.println ("  Found position definition: " + argPart + ", channel=" + channel + ".");
          break;
        }
        case "variable":
        {
          try
          {
            variable = Double.parseDouble (argArgParts[1].trim ());
          }
          catch (NumberFormatException nfe)
          {
            throw new IllegalArgumentException ("ch - " + channel + " - variable");              
          }
          // System.err.println ("  Found variable definition: " + argPart + ", channel=" + channel + ".");
          break;
        }
        case "volts":
        {
          try
          {
            volts = Double.parseDouble (argArgParts[1].trim ());
          }
          catch (NumberFormatException nfe)
          {
            throw new IllegalArgumentException ("ch - " + channel + " - volts");              
          }
          // System.err.println ("  Found volts definition: " + argPart + ", channel=" + channel + ".");
          break;
        }
        default:
          throw new IllegalArgumentException ();
      }
    }
    return new ChannelSettings (channelCoupling, fifty, invert, position, variable, volts);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DATA SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum DataSource
  {
    Ch1,
    Ch2,
    Add,
    Mult,
    Ref1,
    Ref2,
    Ref3,
    Ref4,
    Ch1Del,
    Ch2Del,
    AddDel,
    MultDel;
  }
  
  public static enum DataEncoding
  {
    ASCII,
    RPBinary,
    RIBinary,
    RIPartial,
    RPPartial;
  }
  
  public static enum DataTarget
  {
    Ref1,
    Ref2,
    Ref3,
    Ref4;
  }
  
  public static class DataSettings
  {
    
    private final DataSource source;
    
    private final DataSource dSource;
    
    private final DataEncoding encoding;
    
    private final DataTarget target;

    public DataSettings (
      final DataSource source,
      final DataSource dSource,
      final DataEncoding encoding,
      final DataTarget target)
    {
      if (source == null || dSource == null || encoding == null || target == null)
        throw new IllegalArgumentException ();
      this.source = source;
      this.dSource = dSource;
      this.encoding = encoding;
      this.target = target;
    }
    
  }
  
  private final DataSettings dataSettings;
  
  public final DataSettings getDataSettings ()
  {
    return this.dataSettings;
  }
  
  public final DataSource getDataSource ()
  {
    return getDataSettings ().source;
  }
  
  public final DataSource getDataDSource ()
  {
    return getDataSettings ().dSource;
  }
  
  public final DataEncoding getDataEncoding ()
  {
    return getDataSettings ().encoding;
  }
  
  public final DataTarget getDataTarget ()
  {
    return this.getDataSettings ().target;
  }
  
  private static DataSettings parseDataSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    DataSource source = null;
    DataSource dSource = null;
    DataEncoding encoding = null;
    DataTarget target = null;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    for (final String argPart: argParts)
    {
      final String[] argArgParts = argPart.trim ().split (":");
      if (argArgParts == null || argArgParts.length != 2)
        throw new IllegalArgumentException ("data");
      final String argKey = argArgParts[0].trim ();
      boolean isDSource = false;
      switch (argKey)
      {
        case "dsource":
          isDSource = true;
          // Intended fallthrough.
        case "source":
        {
          final DataSource thisSource;
          switch (argArgParts[1].trim ())
          {
            case "ch1":     thisSource = DataSource.Ch1;     break;
            case "ch2":     thisSource = DataSource.Ch2;     break;
            case "add":     thisSource = DataSource.Add;     break;
            case "mult":    thisSource = DataSource.Mult;    break;
            case "ref1":    thisSource = DataSource.Ref1;    break;
            case "ref2":    thisSource = DataSource.Ref2;    break;
            case "ref3":    thisSource = DataSource.Ref3;    break;
            case "ref4":    thisSource = DataSource.Ref4;    break;
            case "ch1del":  thisSource = DataSource.Ch1Del;  break;
            case "ch2del":  thisSource = DataSource.Ch2Del;  break;
            case "adddel":  thisSource = DataSource.AddDel;  break;
            case "multdel": thisSource = DataSource.MultDel; break;
            default: throw new IllegalArgumentException ();
          }
          if (isDSource)
            dSource = thisSource;
          else
            source = thisSource;
          // System.err.println ("  Found data/" + argKey + " definition: " + argPart + ".");
          break;
        }
        case "encdg":
        {
          switch (argArgParts[1].trim ())
          {
            case "ascii":     encoding = DataEncoding.ASCII;     break;
            case "rpbinary":  encoding = DataEncoding.RPBinary;  break;
            case "ribinary":  encoding = DataEncoding.RIBinary;  break;
            case "rppartial": encoding = DataEncoding.RPPartial; break;
            case "ripartial": encoding = DataEncoding.RIPartial; break;
            default: throw new IllegalArgumentException ();
          }
          // System.err.println ("  Found data/" + argKey + " definition: " + argPart + ".");
          break;
        }
        case "target":
        {
          switch (argArgParts[1].trim ())
          {
            case "ref1": target = DataTarget.Ref1; break;
            case "ref2": target = DataTarget.Ref2; break;
            case "ref3": target = DataTarget.Ref3; break;
            case "ref4": target = DataTarget.Ref4; break;
            default:
              throw new IllegalArgumentException ();
          }
          break;
        }
        default:
          throw new IllegalArgumentException ();
      }
    }
    return new DataSettings (source, dSource, encoding, target);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // VERTICAL MODE SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum VModeDisplay
  {
    YT,
    XY;
  }
  
  public static class VModeSettings
  {
    
    private final boolean channel1;
    
    private final boolean channel2;
    
    private final boolean add;
    
    private final boolean mult;
    
    private final VModeDisplay display;
    
    public VModeSettings (
      final Boolean channel1,
      final Boolean channel2,
      final Boolean add,
      final Boolean mult,
      final VModeDisplay display)
    {
      if (channel1 == null || channel2 == null || add == null || mult == null || display == null)
        throw new IllegalArgumentException ();
      this.channel1 = channel1;
      this.channel2 = channel2;
      this.add = add;
      this.mult = mult;
      this.display = display;
    }
    
  }
  
  private final VModeSettings vModeSettings;
  
  public final VModeSettings getVModeSettings ()
  {
    return this.vModeSettings;
  }
  
  public final boolean isVModeChannel1 ()
  {
    return this.vModeSettings.channel1;
  }
  
  public final boolean isVModeChannel2 ()
  {
    return this.vModeSettings.channel2;
  }
  
  public final boolean isVModeAdd ()
  {
    return this.vModeSettings.add;
  }
  
  public final boolean isVModeMult ()
  {
    return this.vModeSettings.mult;
  }
  
  public final VModeDisplay getVModeDisplay ()
  {
    return this.vModeSettings.display;
  }
  
  private static VModeSettings parseVModeSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    Boolean channel1 = null;
    Boolean channel2 = null;
    Boolean add = null;
    Boolean mult = null;
    VModeDisplay display = null;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    for (final String argPart: argParts)
    {
      final String[] argArgParts = argPart.trim ().split (":");
      if (argArgParts == null || argArgParts.length != 2)
        throw new IllegalArgumentException ();
      final String argKey = argArgParts[0].trim ();
      switch (argKey)
      {
        case "ch1":
        {
          switch (argArgParts[1].trim ())
          {
            case "on":  channel1 = true;  break;
            case "off": channel1 = false; break;
            default: throw new IllegalArgumentException ();
          }
          break;
        }
        case "ch2":
        {
          switch (argArgParts[1].trim ())
          {
            case "on":  channel2 = true;  break;
            case "off": channel2 = false; break;
            default: throw new IllegalArgumentException ();
          }
          break;
        }
        case ("add"):
        {
          switch (argArgParts[1].trim ())
          {
            case "on":  add = true;  break;
            case "off": add = false; break;
            default: throw new IllegalArgumentException ();
          }
          break;
        }
        case ("mul"):
        case ("mult"):
        {
          switch (argArgParts[1].trim ())
          {
            case "on":  mult = true;  break;
            case "off": mult = false; break;
            default: throw new IllegalArgumentException ();
          }
          break;
        }
        case ("disp"):
        case ("display"):
        {
          switch (argArgParts[1].trim ())
          {
            case "yt": display = VModeDisplay.YT; break;
            case "xy": display = VModeDisplay.XY; break;
            default: throw new IllegalArgumentException ();
          }
          break;
        }
        default:
          throw new IllegalArgumentException ();
      }
    }
    return new VModeSettings (channel1, channel2, add, mult, display);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
