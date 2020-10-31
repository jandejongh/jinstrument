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
import java.util.HashMap;
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
    final VModeSettings vModeSettings,
    final BandwidthLimitSettings bandwidthLimitSettings,
    final AcquisitionSettings acquisitionSettings,
    final ATriggerSettings aTriggerSettings,
    final BTriggerSettings bTriggerSettings,
    final RunSettings runSettings,
    final DelayTimeSettings delayTimeSettings,
    final DelayEventsSettings delayEventsSettings)
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
    if (bandwidthLimitSettings == null)
      throw new IllegalArgumentException ();
    this.bandwidthLimitSettings = bandwidthLimitSettings;
    if (acquisitionSettings == null)
      throw new IllegalArgumentException ();
    this.acquisitionSettings = acquisitionSettings;
    if (aTriggerSettings == null)
      throw new IllegalArgumentException ();
    this.aTriggerSettings = aTriggerSettings;
    if (bTriggerSettings == null)
      throw new IllegalArgumentException ();
    this.bTriggerSettings = bTriggerSettings;
    if (runSettings == null)
      throw new IllegalArgumentException ();
    this.runSettings = runSettings;
    if (delayTimeSettings == null)
      throw new IllegalArgumentException ();
    this.delayTimeSettings = delayTimeSettings;
    if (delayEventsSettings == null)
      throw new IllegalArgumentException ();
    this.delayEventsSettings = delayEventsSettings;
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
    BandwidthLimitSettings bandwidthLimitSettings = null;
    AcquisitionSettings acquisitionSettings = null;
    ATriggerSettings aTriggerSettings = null;
    BTriggerSettings bTriggerSettings = null;
    RunSettings runSettings = null;
    DelayTimeSettings delayTimeSettings = null;
    DelayEventsSettings delayEventsSettings = null;
    for (final String part : parts)
    {
      final String[] partParts = part.trim ().split (" ", 2);
      final String keyString = partParts[0].trim ().toLowerCase ();
      final String argString = partParts[1].trim ().toLowerCase ();
      switch (keyString)
      {
        case "autos":
        case "autosetup":
          autoSetupSettings = parseAutoSetupSettings (argString);
          break;
        case "hor":
        case "horizontal":
          horizontalSettings = parseHorizontalSettings (argString);
          break;
        case "ch1":
          ch1Settings = parseChannelSettings (argString, Tek2440_GPIB_Instrument.Tek2440Channel.Channel1);
          break;
        case "ch2":
          ch2Settings = parseChannelSettings (argString, Tek2440_GPIB_Instrument.Tek2440Channel.Channel2);
          break;
        case "dat":
        case "data":
          dataSettings = parseDataSettings (argString);
          break;
        case "vmo":
        case "vmode":
          vModeSettings = parseVModeSettings (argString);
          break;
        case "bwl":
        case "bwlimit":
          bandwidthLimitSettings = parseBandwidthLimitSettings (argString);
          break;
        case "acq":
        case "acquire":
          acquisitionSettings = parseAcquisitionSettings (argString);
          break;
        case "atr":
        case "atrigger":
          aTriggerSettings = parseATriggerSettings (argString);
          break;
        case "btr":
        case "btrigger":
          bTriggerSettings = parseBTriggerSettings (argString);
          break;
        case "run":
          runSettings = parseRunSettings (argString);
          break;
        case "dlyt":
        case "dlytime":
          delayTimeSettings = parseDelayTimeSettings (argString);
          break;
        case "dlye":
        case "dlyevts":
          delayEventsSettings = parseDelayEventsSettings (argString);
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
      vModeSettings,
      bandwidthLimitSettings,
      acquisitionSettings,
      aTriggerSettings,
      bTriggerSettings,
      runSettings,
      delayTimeSettings,
      delayEventsSettings);
  }
  
  private static boolean parseOnOff (final String argString)
  {
    switch (argString)
    {
      case "on" : return true;
      case "off": return false;
      default: throw new IllegalArgumentException ();
    }
  }
  
  private static <E extends Enum<E>> E parseEnum (
    final String argString,
    final Map<String, E> argMap)
  {
    for (final String s : argMap.keySet ())
      if (argString.equals (s))
        return argMap.get (s);
    return null;
  }
  
  private static <E extends Enum<E>> E parseEnumFromOrdinal (
    final String argString,
    final Class<E> enumClass,
    final int firstIndex)
  {
    try
    {
      return enumClass.getEnumConstants ()[Integer.parseInt (argString) - firstIndex];
    }
    catch (NumberFormatException | ArrayIndexOutOfBoundsException | NullPointerException e)
    {
      throw new IllegalArgumentException ();
    }
  }
  
  private static int parseNr1 (final String argString)
  {
    try
    {
      return Integer.parseInt (argString);
    }
    catch (NumberFormatException nfe)
    {
      throw new IllegalArgumentException ();
    }
  }
  
  private static int parseNr1 (final String argString, final int min, final int max)
  {
    try
    {
      final int iRead = Integer.parseInt (argString);
      if (iRead < min)
        throw new IllegalArgumentException ();
      if (iRead > max)
        throw new IllegalArgumentException ();
      return iRead;
    }
    catch (NumberFormatException nfe)
    {
      throw new IllegalArgumentException ();
    }
  }
  
  private static double parseNr3 (final String argString)
  {
    try
    {
      return Double.parseDouble (argString);
    }
    catch (NumberFormatException nfe)
    {
      throw new IllegalArgumentException ();
    }
  }
  
  private static double parseNr3 (final String argString, final double min, final double max)
  {
    try
    {
      final double dRead = Double.parseDouble (argString);
      if (dRead < min)
        throw new IllegalArgumentException ();
      if (dRead > max)
        throw new IllegalArgumentException ();
      return dRead;
    }
    catch (NumberFormatException nfe)
    {
      throw new IllegalArgumentException ();
    }
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
        throw new IllegalArgumentException ();
      final String argKey = argArgParts[0].trim ();
      switch (argKey)
      {
        case "mod":
        case "mode":
        {
          switch (argArgParts[1].trim ())
          {
            case "fal":
            case "fall":
              mode = AutoSetupMode.FALL;
              break;
            case "peri":
            case "period":
              mode = AutoSetupMode.PERIOD;
              break;
            case "pul":
            case "pulse":
              mode = AutoSetupMode.PULSE;
              break;
            case "ris":
            case "rise":
              mode = AutoSetupMode.RISE;
              break;
            case "vie":
            case "view":
              mode = AutoSetupMode.VIEW;
              break;
            default:
              throw new IllegalArgumentException ();
          }
          break;
        }
        case "res":
        case "resolution":
        {
          switch (argArgParts[1].trim ())
          {
            case "hi": resolution = AutoSetupResolution.HIGH; break;
            case "lo": resolution = AutoSetupResolution.LOW;  break;
            default: throw new IllegalArgumentException ();
          }
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
    
    HorExtExp_1     (1),
    HorExtExp_2     (2),
    HorExtExp_5     (5),
    HorExtExp_10   (10),
    HorExtExp_20   (20),
    HorExtExp_50   (50),
    HorExtExp_100 (100);

    private final int heef;
    
    private HorizontalExternalExpansionFactor (final int heef)
    {
      this.heef = heef;
    }
    
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

    @Override
    public final String toString ()
    {
      return Integer.toString (this.heef);
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
        case "ase":
        case "asecdiv":
          isASecDiv = true;
          // Intended fallthrough.
        case "bse":
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
          break;
        }
        case "exte":
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
          break;
        }
        case "mod":
        case "mode":
        {
          switch (argArgParts[1].trim ())
          {
            case "ain":
            case "aintb":
              mode = HorizontalMode.AInTb;
              break;
            case "asw":
            case "asweep":
              mode = HorizontalMode.ASweep;
              break;
            case "bsw":
            case "bsweep":
              mode = HorizontalMode.BSweep;
              break;
            default:
              throw new IllegalArgumentException ();
          }
          break;
        }
        case "pos":
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
  
  public final double getVariableVoltsPerDivision (final Tek2440_GPIB_Instrument.Tek2440Channel channel)
  {
    if (channel == null)
      throw new IllegalArgumentException ();
    switch (channel)
    {
      case Channel1: return getCh1VariableVoltsPerDivision ();
      case Channel2: return getCh2VariableVoltsPerDivision ();
      default:
        throw new IllegalArgumentException ();
    }
  }
  
  public final double getCh1VariableVoltsPerDivision ()
  {
    return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel1).variable;
  }
  
  public final double getCh2VariableVoltsPerDivision ()
  {
    return getChannelSettings (Tek2440_GPIB_Instrument.Tek2440Channel.Channel2).variable;
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
        throw new IllegalArgumentException ();
      final String argKey = argArgParts[0].trim ();
      switch (argKey)
      {
        case "cou":
        case "coupling":
        {
          switch (argArgParts[1].trim ())
          {
            case "ac":  channelCoupling = ChannelCoupling.AC;  break;
            case "dc":  channelCoupling = ChannelCoupling.DC;  break;
            case "gnd": channelCoupling = ChannelCoupling.GND; break;
            default: throw new IllegalArgumentException ();
          }
          break;
        }
        case "fif":
        case "fifty":
        {
          fifty = parseOnOff (argArgParts[1].trim ());
          break;
        }
        case "inv":
        case "invert":
        {
          invert = parseOnOff (argArgParts[1].trim ());
          break;
        }
        case "pos":
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
          break;
        }
        case "var":
        case "variable":
        {
          try
          {
            variable = Double.parseDouble (argArgParts[1].trim ());
          }
          catch (NumberFormatException nfe)
          {
            throw new IllegalArgumentException ();              
          }
          break;
        }
        case "vol":
        case "volts":
        {
          try
          {
            volts = Double.parseDouble (argArgParts[1].trim ());
          }
          catch (NumberFormatException nfe)
          {
            throw new IllegalArgumentException ();              
          }
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
        throw new IllegalArgumentException ();
      final String argKey = argArgParts[0].trim ();
      boolean isDSource = false;
      switch (argKey)
      {
        case "dsou":
        case "dsource":
          isDSource = true;
          // Intended fallthrough.
        case "sou":
        case "source":
        {
          final DataSource thisSource;
          switch (argArgParts[1].trim ())
          {
            case "ch1":     thisSource = DataSource.Ch1;     break;
            case "ch2":     thisSource = DataSource.Ch2;     break;
            case "add":     thisSource = DataSource.Add;     break;
            case "mul":
            case "mult":    thisSource = DataSource.Mult;    break;
            case "ref1":    thisSource = DataSource.Ref1;    break;
            case "ref2":    thisSource = DataSource.Ref2;    break;
            case "ref3":    thisSource = DataSource.Ref3;    break;
            case "ref4":    thisSource = DataSource.Ref4;    break;
            case "ch1d":
            case "ch1del":  thisSource = DataSource.Ch1Del;  break;
            case "ch2d":
            case "ch2del":  thisSource = DataSource.Ch2Del;  break;
            case "addd":
            case "adddel":  thisSource = DataSource.AddDel;  break;
            case "multd":
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
        case "enc":
        case "encdg":
        {
          switch (argArgParts[1].trim ())
          {
            case "asc":
            case "ascii":
              encoding = DataEncoding.ASCII;
              break;
            case "rpb":
            case "rpbinary":
              encoding = DataEncoding.RPBinary;
              break;
            case "rib":
            case "ribinary":
              encoding = DataEncoding.RIBinary;
              break;
            case "rpp":
            case "rppartial":
              encoding = DataEncoding.RPPartial;
              break;
            case "rip":
            case "ripartial":
              encoding = DataEncoding.RIPartial;
              break;
            default:
              throw new IllegalArgumentException ();
          }
          break;
        }
        case "tar":
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
  // BANDWIDTH LIMIT SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum BandwidthLimit
  {
    
    BWL_20_MHz  ("20 MHz"),
    BWL_100_MHz ("100 MHz"),
    BWL_FULL    ("Full");
    
    public final String bwlString;

    private BandwidthLimit (final String bwlString)
    {
      this.bwlString = bwlString;
    }

    @Override
    public final String toString ()
    {
      return this.bwlString;
    }
    
  }
  
  public static class BandwidthLimitSettings
  {
    
    private final BandwidthLimit bandwidthLimit;
    
    public BandwidthLimitSettings (final BandwidthLimit bandwidthLimit)
    {
      if (bandwidthLimit == null)
        throw new IllegalArgumentException ();
      this.bandwidthLimit = bandwidthLimit;
    }
    
  }
  
  private final BandwidthLimitSettings bandwidthLimitSettings;
  
  public final BandwidthLimitSettings getBandwidthLimitSettings ()
  {
    return this.bandwidthLimitSettings;
  }

  public final BandwidthLimit getBandwidthLimit ()
  {
    return this.bandwidthLimitSettings.bandwidthLimit;
  }
  
  private static BandwidthLimitSettings parseBandwidthLimitSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    final BandwidthLimit bandwidthLimit;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    if (argParts == null || argParts.length != 1 || argParts[0] == null)
      throw new IllegalArgumentException ();
    switch (argParts[0].trim ())
    {
      case "twe":
      case "twenty":
        bandwidthLimit = BandwidthLimit.BWL_20_MHz;
        break;
      case "hun":
      case "hundred":
        bandwidthLimit = BandwidthLimit.BWL_100_MHz;
        break;
      case "ful":
      case "full":
        bandwidthLimit = BandwidthLimit.BWL_FULL;
        break;
      default:
        throw new IllegalArgumentException ();
    }
    return new BandwidthLimitSettings (bandwidthLimit);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACQUISITION SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum AcquisitionMode
  {
    AVERAGE,
    ENVELOPE,
    NORMAL;
  }
  
  public enum NumberOfAcquisitionsAveraged
  {
    
    ACQ_AVG_2,
    ACQ_AVG_4,
    ACQ_AVG_8,
    ACQ_AVG_16,
    ACQ_AVG_32,
    ACQ_AVG_64,
    ACQ_AVG_128,
    ACQ_AVG_256;
    
  }
  
  public enum NumberOfEnvelopeSweeps
  {
    
    ENV_SWEEPS_1, // XXX Should not get this value; is this the encoding for ENV_SWEEPS_CONTROL??
    ENV_SWEEPS_2,
    ENV_SWEEPS_4,
    ENV_SWEEPS_8,
    ENV_SWEEPS_16,
    ENV_SWEEPS_32,
    ENV_SWEEPS_64,
    ENV_SWEEPS_128,
    ENV_SWEEPS_256,
    ENV_SWEEPS_CONTROL;
    
  }
  
  public static class AcquisitionSettings
  {
    
    private final AcquisitionMode mode;
    
    private final boolean repetitive;
    
    private final NumberOfAcquisitionsAveraged acquisitionsAveraged;
    
    private final NumberOfEnvelopeSweeps envelopeSweeps;
    
    private final boolean saveOnDelta;

    public AcquisitionSettings (
      final AcquisitionMode mode,
      final Boolean repetitive,
      final NumberOfAcquisitionsAveraged acquisitionsAveraged,
      final NumberOfEnvelopeSweeps envelopeSweeps,
      final Boolean saveOnDelta)
    {
      if (mode == null || repetitive == null || acquisitionsAveraged == null || envelopeSweeps == null || saveOnDelta == null)
        throw new IllegalArgumentException ();
      this.mode = mode;
      this.repetitive = repetitive;
      this.acquisitionsAveraged = acquisitionsAveraged;
      this.envelopeSweeps = envelopeSweeps;
      this.saveOnDelta = saveOnDelta;
    }
    
  }
  
  private final AcquisitionSettings acquisitionSettings;
  
  public final AcquisitionSettings getAcquisitionSettings ()
  {
    return this.acquisitionSettings;
  }
  
  private static AcquisitionSettings parseAcquisitionSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    AcquisitionMode acquisitionMode = null;
    Boolean repetitive = null;
    NumberOfAcquisitionsAveraged acquisitionsAveraged = null;
    NumberOfEnvelopeSweeps envelopeSweeps = null;
    Boolean saveOnDelta = null;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    for (final String argPart: argParts)
    {
      final String[] argArgParts = argPart.trim ().split (":");
      if (argArgParts == null || argArgParts.length != 2)
        throw new IllegalArgumentException ();
      final String argKey = argArgParts[0].trim ();
      switch (argKey)
      {
        case "mod":
        case "mode":
        {
          switch (argArgParts[1].trim ())
          {
            case "avg":
              acquisitionMode = AcquisitionMode.AVERAGE;
              break;
            case "env":
              acquisitionMode = AcquisitionMode.ENVELOPE;
              break;
            case "nor":
            case "normal":
              acquisitionMode = AcquisitionMode.NORMAL;
              break;
            default:
              throw new IllegalArgumentException ();
          }
          break;
        }
        case "rep":
        case "repet":
        {
          switch (argArgParts[1].trim ())
          {
            case "on":  repetitive = true;  break;
            case "off": repetitive = false; break;
            default: throw new IllegalArgumentException ();
          }
          break;
        }
        case "numav":
        case "numavg":
        {
          switch (argArgParts[1].trim ())
          {
            case   "2": acquisitionsAveraged = NumberOfAcquisitionsAveraged.ACQ_AVG_2;   break;
            case   "4": acquisitionsAveraged = NumberOfAcquisitionsAveraged.ACQ_AVG_4;   break;
            case   "8": acquisitionsAveraged = NumberOfAcquisitionsAveraged.ACQ_AVG_8;   break;
            case  "16": acquisitionsAveraged = NumberOfAcquisitionsAveraged.ACQ_AVG_16;  break;
            case  "32": acquisitionsAveraged = NumberOfAcquisitionsAveraged.ACQ_AVG_32;  break;
            case  "64": acquisitionsAveraged = NumberOfAcquisitionsAveraged.ACQ_AVG_64;  break;
            case "128": acquisitionsAveraged = NumberOfAcquisitionsAveraged.ACQ_AVG_128; break;
            case "256": acquisitionsAveraged = NumberOfAcquisitionsAveraged.ACQ_AVG_256; break;
            default: throw new IllegalArgumentException ();
          }
          break;
        }
        case "nume":
        case "numenv":
        {
          switch (argArgParts[1].trim ())
          {
            // Value "1" should not happen; but it does... Encoding for ENV_SWEEPS_CONTROL??
            case   "1":  envelopeSweeps = NumberOfEnvelopeSweeps.ENV_SWEEPS_1;   break;
            case   "2":  envelopeSweeps = NumberOfEnvelopeSweeps.ENV_SWEEPS_2;   break;
            case   "4":  envelopeSweeps = NumberOfEnvelopeSweeps.ENV_SWEEPS_4;   break;
            case   "8":  envelopeSweeps = NumberOfEnvelopeSweeps.ENV_SWEEPS_8;   break;
            case  "16":  envelopeSweeps = NumberOfEnvelopeSweeps.ENV_SWEEPS_16;  break;
            case  "32":  envelopeSweeps = NumberOfEnvelopeSweeps.ENV_SWEEPS_32;  break;
            case  "64":  envelopeSweeps = NumberOfEnvelopeSweeps.ENV_SWEEPS_64;  break;
            case "128":  envelopeSweeps = NumberOfEnvelopeSweeps.ENV_SWEEPS_128; break;
            case "256":  envelopeSweeps = NumberOfEnvelopeSweeps.ENV_SWEEPS_256; break;
            case "con":
            case "cont": envelopeSweeps = NumberOfEnvelopeSweeps.ENV_SWEEPS_CONTROL; break;
            default: throw new IllegalArgumentException ();
          }
          break;
        }
        case "savd":
        case "savdel":
        {
          switch (argArgParts[1].trim ())
          {
            case "on":  saveOnDelta = true;  break;
            case "off": saveOnDelta = false; break;
            default: throw new IllegalArgumentException ();
          }
          break;
        }
        default:
          throw new IllegalArgumentException ();
      }
    }
    return new AcquisitionSettings (acquisitionMode, repetitive, acquisitionsAveraged, envelopeSweeps, saveOnDelta);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // A-TRIGGER SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum ATriggerMode
  {
    Auto,
    AutoLevel,
    Normal,
    SingleSequence;
  }
  
  public enum ATriggerSource
  {
    Ch1,
    Ch2,
    Ext1,
    Ext2,
    Line,
    Vertical;
  }
  
  public enum ATriggerCoupling
  {
    AC,
    DC,
    LFReject,
    HFReject,
    NoiseReject,
    TV;
  }
  
  public enum ATriggerLogSource
  {
    Off,
    A_and_B,
    Word;
  }
  
  public enum Slope
  {
    Plus,
    Minus; 
  }
  
  public enum ATriggerPosition
  {
    ATriggerPosition_01,
    ATriggerPosition_02,
    ATriggerPosition_03,
    ATriggerPosition_04,
    ATriggerPosition_05,
    ATriggerPosition_06,
    ATriggerPosition_07,
    ATriggerPosition_08,
    ATriggerPosition_09,
    ATriggerPosition_10,
    ATriggerPosition_11,
    ATriggerPosition_12,
    ATriggerPosition_13,
    ATriggerPosition_14,
    ATriggerPosition_15,
    ATriggerPosition_16,
    ATriggerPosition_17,
    ATriggerPosition_18,
    ATriggerPosition_19,
    ATriggerPosition_20,
    ATriggerPosition_21,
    ATriggerPosition_22,
    ATriggerPosition_23,
    ATriggerPosition_24,
    ATriggerPosition_25,
    ATriggerPosition_26,
    ATriggerPosition_27,
    ATriggerPosition_28,
    ATriggerPosition_29,
    ATriggerPosition_30;
  }
  
  public enum ABSelect
  {
    A,
    B;
  }
  
  public static class ATriggerSettings
  {
    
    private final ATriggerMode mode;
    
    private final ATriggerSource source;
    
    private final ATriggerCoupling coupling;
    
    private final ATriggerLogSource logSource;
    
    private final double level;
    
    private final Slope slope;
    
    private final ATriggerPosition position;
    
    private final double holdoff;
    
    private final ABSelect abSelect;

    public ATriggerSettings (
      final ATriggerMode mode,
      final ATriggerSource source,
      final ATriggerCoupling coupling,
      final ATriggerLogSource logSource,
      final Double level,
      final Slope slope,
      final ATriggerPosition position,
      final Double holdoff,
      final ABSelect abSelect)
    {
      if (mode == null || source == null || coupling == null || logSource == null)
        throw new IllegalArgumentException ();
      if (level == null || slope == null || position == null || holdoff == null || abSelect == null)
        throw new IllegalArgumentException ();
      if (holdoff < 0 || holdoff > 100)
        throw new IllegalArgumentException ();
      this.mode = mode;
      this.source = source;
      this.coupling = coupling;
      this.logSource = logSource;
      this.level = level;
      this.slope = slope;
      this.position = position;
      this.holdoff = holdoff;
      this.abSelect = abSelect;
    }
  }
    
  private final ATriggerSettings aTriggerSettings;
  
  public final ATriggerSettings getATriggerSettings ()
  {
    return this.aTriggerSettings;
  }
  
  private static ATriggerSettings parseATriggerSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    ATriggerMode mode = null;
    ATriggerSource source = null;
    ATriggerCoupling coupling = null;
    ATriggerLogSource logSource = null;
    Double level = null;
    Slope slope = null;
    ATriggerPosition position = null;
    Double holdoff = null;
    ABSelect abSelect = null;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    for (final String argPart: argParts)
    {
      final String[] argArgParts = argPart.trim ().split (":");
      if (argArgParts == null || argArgParts.length != 2)
        throw new IllegalArgumentException ();
      final String argKey = argArgParts[0].trim ();
      switch (argKey)
      {
        case "mod":
        case "mode":
        {
          switch (argArgParts[1].trim ())
          {
            case   "auto":
              mode = ATriggerMode.Auto;
              break;
            case   "autol":
            case   "autolevel":
              mode = ATriggerMode.AutoLevel;
              break;
            case   "nor":
            case   "normal":
              mode = ATriggerMode.Normal;
              break;
            case   "sgl":
            case   "sglseq":
              mode = ATriggerMode.SingleSequence;
              break;
            default:
              throw new IllegalArgumentException ();
          }
          break;
        }
        case "sou":
        case "source":
        {
          switch (argArgParts[1].trim ())
          {
            case   "ch1":
              source = ATriggerSource.Ch1;
              break;
            case   "ch2":
              source = ATriggerSource.Ch2;
              break;
            case   "ext1":
              source = ATriggerSource.Ext1;
              break;
            case   "ext2":
              source = ATriggerSource.Ext2;
              break;
            case   "lin":
            case   "line":
              source = ATriggerSource.Line;
              break;
            case   "ver":
            case   "vertical":
              source = ATriggerSource.Vertical;
              break;
            default:
              throw new IllegalArgumentException ();
          }
          break;
        }
        case "cou":
        case "coupling":
        {
          coupling = parseEnum (argArgParts[1].trim (),
            new HashMap<String, ATriggerCoupling> ()
            {{
              put ("ac",       ATriggerCoupling.AC);
              put ("dc",       ATriggerCoupling.DC);
              put ("lfr",      ATriggerCoupling.LFReject);
              put ("lfrej",    ATriggerCoupling.LFReject);
              put ("hfr",      ATriggerCoupling.HFReject);
              put ("hfrej",    ATriggerCoupling.HFReject);
              put ("noi",      ATriggerCoupling.NoiseReject);
              put ("noiserej", ATriggerCoupling.NoiseReject);
              put ("tv",       ATriggerCoupling.TV);
            }});
          break;
        }
        case "log":
        case "logsrc":
        {
          logSource = parseEnum (argArgParts[1].trim (),
            new HashMap<String, ATriggerLogSource> ()
            {{
              put ("off",  ATriggerLogSource.Off);
              put ("a.b",  ATriggerLogSource.A_and_B);
              put ("wor",  ATriggerLogSource.Word);
              put ("word", ATriggerLogSource.Word);
            }});
          break;
        }
        case "lev":
        case "level":
        {
          level = parseNr3 (argArgParts[1].trim ());
          break;
        }
        case "slo":
        case "slope":
        {
          slope = parseEnum (argArgParts[1].trim (),
            new HashMap<String, Slope> ()
            {{
              put ("plu",   Slope.Plus);
              put ("plus",  Slope.Plus);
              put ("minu",  Slope.Minus);
              put ("minus", Slope.Minus);
            }});
          break;
        }
        case "pos":
        case "position":
        {
          position = parseEnumFromOrdinal (argArgParts[1].trim (), ATriggerPosition.class, 1);
          break;
        }
        case "hol":
        case "holdoff":
        {
          holdoff = parseNr3 (argArgParts[1].trim (), 0, 100);
          break;
        }
        case "abse":
        case "abselect":
        {
          abSelect = parseEnum (argArgParts[1].trim (),
            new HashMap<String, ABSelect> ()
            {{
              put ("a", ABSelect.A);
              put ("b", ABSelect.B);
            }});
          break;
        }
        default:
          throw new IllegalArgumentException ();
      }
    }
    return new ATriggerSettings (
      mode,
      source,
      coupling,
      logSource,
      level,
      slope,
      position,
      holdoff,
      abSelect);
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // B-TRIGGER SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum BTriggerMode
  {
    RunsAft,
    TrigAft;
  }
  
  public enum BTriggerSource
  {
    Ch1,
    Ch2,
    Ext1,
    Ext2,
    Word,
    Vertical;
  }
  
  public enum BTriggerCoupling
  {
    AC,
    DC,
    LFReject,
    HFReject,
    NoiseReject;
  }
  
  // Undocumented; assumed to be identical in semantics to A Trigger Position.
  // XXX Keep in place until we are sure.
  public enum BTriggerPosition
  {
    BTriggerPosition_01,
    BTriggerPosition_02,
    BTriggerPosition_03,
    BTriggerPosition_04,
    BTriggerPosition_05,
    BTriggerPosition_06,
    BTriggerPosition_07,
    BTriggerPosition_08,
    BTriggerPosition_09,
    BTriggerPosition_10,
    BTriggerPosition_11,
    BTriggerPosition_12,
    BTriggerPosition_13,
    BTriggerPosition_14,
    BTriggerPosition_15,
    BTriggerPosition_16,
    BTriggerPosition_17,
    BTriggerPosition_18,
    BTriggerPosition_19,
    BTriggerPosition_20,
    BTriggerPosition_21,
    BTriggerPosition_22,
    BTriggerPosition_23,
    BTriggerPosition_24,
    BTriggerPosition_25,
    BTriggerPosition_26,
    BTriggerPosition_27,
    BTriggerPosition_28,
    BTriggerPosition_29,
    BTriggerPosition_30;
  }
  
  public static class BTriggerSettings
  {
    
    private final BTriggerMode mode;
    
    private final boolean extClk;
    
    private final BTriggerSource source;
    
    private final BTriggerCoupling coupling;
    
    private final double level;
    
    private final Slope slope;
    
    private final BTriggerPosition position;

    public BTriggerSettings (
      final BTriggerMode mode,
      final Boolean extClk,
      final BTriggerSource source,
      final BTriggerCoupling coupling,
      final Double level,
      final Slope slope,
      final BTriggerPosition position)
    {
      if (mode == null || extClk == null || source == null || coupling == null)
        throw new IllegalArgumentException ();
      if (level == null || slope == null || position == null)
        throw new IllegalArgumentException ();
      this.mode = mode;
      this.extClk = extClk;
      this.source = source;
      this.coupling = coupling;
      this.level = level;
      this.slope = slope;
      this.position = position;
    }
  }
    
  private final BTriggerSettings bTriggerSettings;
  
  public final BTriggerSettings getBTriggerSettings ()
  {
    return this.bTriggerSettings;
  }
  
  private static BTriggerSettings parseBTriggerSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    BTriggerMode mode = null;
    Boolean extClk = null;
    BTriggerSource source = null;
    BTriggerCoupling coupling = null;
    Double level = null;
    Slope slope = null;
    BTriggerPosition position = null;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    for (final String argPart: argParts)
    {
      final String[] argArgParts = argPart.trim ().split (":");
      if (argArgParts == null || argArgParts.length != 2)
        throw new IllegalArgumentException ();
      final String argKey = argArgParts[0].trim ();
      switch (argKey)
      {
        case "mod":
        case "mode":
        {
          mode = parseEnum (argArgParts[1].trim (),
            new HashMap<String, BTriggerMode> ()
            {{
              put ("runs",    BTriggerMode.RunsAft);
              put ("runsaft", BTriggerMode.RunsAft);
              put ("tri",     BTriggerMode.TrigAft);
              put ("trigaft", BTriggerMode.TrigAft);
            }});
          break;
        }
        case "extcl":
        case "extclk":
        {
          extClk = parseOnOff (argArgParts[1].trim ());
          break;
        }
        case "sou":
        case "source":
        {
          source = parseEnum (argArgParts[1].trim (),
            new HashMap<String, BTriggerSource> ()
            {{
              put ("ch1",      BTriggerSource.Ch1);
              put ("ch2",      BTriggerSource.Ch2);
              put ("ext1",     BTriggerSource.Ext1);
              put ("ext2",     BTriggerSource.Ext2);
              put ("wor",      BTriggerSource.Word);
              put ("word",     BTriggerSource.Word);
              put ("vert",     BTriggerSource.Vertical);
              put ("vertical", BTriggerSource.Vertical);
            }});
          break;
        }
        case "cou":
        case "coupling":
        {
          coupling = parseEnum (argArgParts[1].trim (),
            new HashMap<String, BTriggerCoupling> ()
            {{
              put ("ac",       BTriggerCoupling.AC);
              put ("dc",       BTriggerCoupling.DC);
              put ("lfr",      BTriggerCoupling.LFReject);
              put ("lfrej",    BTriggerCoupling.LFReject);
              put ("hfr",      BTriggerCoupling.HFReject);
              put ("hfrej",    BTriggerCoupling.HFReject);
              put ("noi",      BTriggerCoupling.NoiseReject);
              put ("noiserej", BTriggerCoupling.NoiseReject);
            }});
          break;
        }
        case "lev":
        case "level":
        {
          level = parseNr3 (argArgParts[1].trim ());
          break;
        }
        case "slo":
        case "slope":
        {
          slope = parseEnum (argArgParts[1].trim (),
            new HashMap<String, Slope> ()
            {{
              put ("plu",   Slope.Plus);
              put ("plus",  Slope.Plus);
              put ("minu",  Slope.Minus);
              put ("minus", Slope.Minus);
            }});
          break;
        }
        case "pos":
        case "position":
        {
          position = parseEnumFromOrdinal (argArgParts[1].trim (), BTriggerPosition.class, 1);
          break;
        }
        default:
          throw new IllegalArgumentException ();
      }
    }
    return new BTriggerSettings (
      mode,
      extClk,
      source,
      coupling,
      level,
      slope,
      position);
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RUN SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum RunMode
  {
    Acquire,
    Save;
  }
  
  public final static class RunSettings
  {
    
    private final RunMode mode;

    public RunSettings (final RunMode mode)
    {
      if (mode == null)
        throw new IllegalArgumentException ();
      this.mode = mode;
    }
    
  }
  
  private final RunSettings runSettings;
  
  public final RunSettings getRunSettings ()
  {
    return this.runSettings;
  }
  
  private static RunSettings parseRunSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    final RunMode mode = parseEnum (argString.trim ().toLowerCase (),
      new HashMap<String, RunMode> ()
      {{
        put ("acq",     RunMode.Acquire);
        put ("acquire", RunMode.Acquire);
        put ("sav",     RunMode.Save);
        put ("save",    RunMode.Save);
      }});
    return new RunSettings (mode);
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DELAY TIME SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static class DelayTimeSettings
  {
    
    private final boolean delta;
    
    private final double delay1;
    
    private final double delay2;
    
    public DelayTimeSettings (
      final Boolean delta,
      final Double delay1,
      final Double delay2)
    {
      if (delta == null)
        throw new IllegalArgumentException ();
      this.delta = delta;
      if (delay1 == null)
        throw new IllegalArgumentException ();
      this.delay1 = delay1;
      if (delay2 == null)
        throw new IllegalArgumentException ();
      this.delay2 = delay2;
    }
    
  }
    
  private final DelayTimeSettings delayTimeSettings;
  
  public final DelayTimeSettings getDelayTimeSettings ()
  {
    return this.delayTimeSettings;
  }
  
  private static DelayTimeSettings parseDelayTimeSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    Boolean delta = null;
    Double delay1 = null;
    Double delay2 = null;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    for (final String argPart: argParts)
    {
      final String[] argArgParts = argPart.trim ().split (":");
      if (argArgParts == null || argArgParts.length != 2)
        throw new IllegalArgumentException ();
      final String argKey = argArgParts[0].trim ();
      switch (argKey)
      {
        case "delt":
        case "delta":
        {
          delta = parseOnOff (argArgParts[1].trim ());
          break;
        }
        case "dly1":
        {
          delay1 = parseNr3 (argArgParts[1].trim ());
          break;
        }
        case "dly2":
        {
          delay2 = parseNr3 (argArgParts[1].trim ());
          break;
        }
        default:
          throw new IllegalArgumentException ();
      }
    }
    return new DelayTimeSettings (delta, delay1, delay2);
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DELAY EVENTS SETTINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static class DelayEventsSettings
  {
    
    private final boolean mode;
    
    private final int value;
    
    public DelayEventsSettings (
      final Boolean mode,
      final Integer value)
    {
      if (mode == null)
        throw new IllegalArgumentException ();
      this.mode = mode;
      if (value == null)
        throw new IllegalArgumentException ();
      this.value = value;
    }
    
  }
    
  private final DelayEventsSettings delayEventsSettings;
  
  public final DelayEventsSettings getDelayEventsSettings ()
  {
    return this.delayEventsSettings;
  }
  
  private static DelayEventsSettings parseDelayEventsSettings (final String argString)
  {
    if (argString == null)
      throw new IllegalArgumentException ();
    Boolean mode = null;
    Integer value = null;
    final String[] argParts = argString.trim ().toLowerCase ().split (",");
    for (final String argPart: argParts)
    {
      final String[] argArgParts = argPart.trim ().split (":");
      if (argArgParts == null || argArgParts.length != 2)
        throw new IllegalArgumentException ();
      final String argKey = argArgParts[0].trim ();
      switch (argKey)
      {
        case "mod":
        case "mode":
        {
          mode = parseOnOff (argArgParts[1].trim ());
          break;
        }
        case "val":
        case "value":
        {
          value = parseNr1 (argArgParts[1].trim (), 1, 65536);
          break;
        }
        default:
          throw new IllegalArgumentException ();
      }
    }
    return new DelayEventsSettings (mode, value);
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
