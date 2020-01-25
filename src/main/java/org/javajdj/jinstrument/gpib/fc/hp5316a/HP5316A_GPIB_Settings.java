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
package org.javajdj.jinstrument.gpib.fc.hp5316a;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.Unit;
import org.javajdj.jinstrument.DefaultFrequencyCounterSettings;
import org.javajdj.jinstrument.FrequencyCounterSettings;

/** Implementation of {@link InstrumentSettings} and {@link FrequencyCounterSettings} for the HP-5316A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class HP5316A_GPIB_Settings
  extends DefaultFrequencyCounterSettings
  implements FrequencyCounterSettings
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP5316A_GPIB_Settings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static String getCanonicalStringForInstrument (
    final MeasurementFunction measurementFunction,
    final TriggerSlope channelATriggerSlope,
    final TriggerSlope channelBTriggerSlope,
    final GatingMode gatingMode,
    final boolean endOfMeasurementSrq,
    final GateTimeControl gateTimeControl,
    final TriggerLevelControl triggerLevelControl,
    final double aChannelTriggerLevel_V,
    final double bChannelTriggerLevel_V)
  {
    final StringBuffer sb = new StringBuffer ();
    if (measurementFunction == null)
      throw new IllegalArgumentException ();
    sb.append (measurementFunction.toCanonicalString ());
    if (channelATriggerSlope == null)
      throw new IllegalArgumentException ();
    switch (channelATriggerSlope)
    {
      case POSITIVE:
        sb.append ("AS0");
        break;
      case NEGATIVE:
        sb.append ("AS1");
        break;
      default:
        throw new RuntimeException ();
    }
    if (channelBTriggerSlope == null)
      throw new IllegalArgumentException ();
    switch (channelBTriggerSlope)
    {
      case POSITIVE:
        sb.append ("BS0");
        break;
      case NEGATIVE:
        sb.append ("BS1");
        break;
      default:
        throw new RuntimeException ();
    }
    if (gatingMode == null)
      throw new IllegalArgumentException ();
    sb.append (gatingMode.toString ());
    sb.append (endOfMeasurementSrq ? "SR1" : "SR0");
    if (gateTimeControl == null)
      throw new IllegalArgumentException ();
    sb.append (gateTimeControl.toString ());
    if (triggerLevelControl == null)
      throw new IllegalArgumentException ();
    sb.append (triggerLevelControl.toString ());
    final DecimalFormat df = new DecimalFormat ("#.##");
    sb.append ("AT").append (df.format (aChannelTriggerLevel_V));
    sb.append ("BT").append (df.format (bChannelTriggerLevel_V));
    return sb.toString ();
  }
  
  private static String getCanonicalString (
    final double gateTime_s,
    final MeasurementFunction measurementFunction,
    final TriggerSlope channelATriggerSlope,
    final TriggerSlope channelBTriggerSlope,
    final GatingMode gatingMode,
    final boolean endOfMeasurementSrq,
    final GateTimeControl gateTimeControl,
    final TriggerLevelControl triggerLevelControl,
    final double aChannelTriggerLevel_V,
    final double bChannelTriggerLevel_V)
  {
    return
      "GT"
      + Double.toString (gateTime_s)
      + getCanonicalStringForInstrument (
        measurementFunction,
        channelATriggerSlope,
        channelBTriggerSlope,
        gatingMode,
        endOfMeasurementSrq,
        gateTimeControl,
        triggerLevelControl,
        aChannelTriggerLevel_V,
        bChannelTriggerLevel_V);
  }
  
  private static byte[] getBytes (
    final double gateTime_s,
    final MeasurementFunction measurementFunction,
    final TriggerSlope channelATriggerSlope,
    final TriggerSlope channelBTriggerSlope,
    final GatingMode gatingMode,
    final boolean endOfMeasurementSrq,
    final GateTimeControl gateTimeControl,
    final TriggerLevelControl triggerLevelControl,
    final double aChannelTriggerLevel_V,
    final double bChannelTriggerLevel_V)
  {
    return getCanonicalString (
      gateTime_s,
      measurementFunction,
      channelATriggerSlope,
      channelBTriggerSlope,
      gatingMode,
      endOfMeasurementSrq,
      gateTimeControl,
      triggerLevelControl,
      aChannelTriggerLevel_V,
      bChannelTriggerLevel_V).getBytes (Charset.forName ("US-ASCII"));
  }
  
  private static Unit getReadingUnit (final MeasurementFunction measurementFunction)
  {
    if (measurementFunction == null)
      throw new IllegalArgumentException ();
    switch (measurementFunction)
    {
      case FN00_ROLLING_DISPLAY_TEST:
        return null;
      case FN01_FREQUENCY_A:
        return Unit.UNIT_Hz;
      case FN02_TIME_INTERVAL_A_TO_B:
      case FN03_TIME_INTERVAL_DELAY:
        return Unit.UNIT_s;
      case FN04_RATIO_A_OVER_B:
        return null;
      case FN05_FREQUENCY_C:
        return Unit.UNIT_Hz;
      case FN06_TOTALIZE_STOP:
        return null;
      case FN07_PERIOD_A:
        return Unit.UNIT_s;
      case FN08_TIME_INTERVAL_AVERAGE_A_TO_B:
        return Unit.UNIT_s;
      case FN09_CHECK_10MHZ:
        return Unit.UNIT_Hz;
      case FN10_A_GATED_BY_B:
        return null;
      case FN11_GATE_TIME:
        return Unit.UNIT_s;
      case FN12_TOTALIZE_START:
        return null;
      case FN13_FREQUENCY_A_AVG_ARMED_BY_B_POS_SLOPE:
        return Unit.UNIT_Hz;
      default:
        throw new IllegalArgumentException ();
    }
  }

  public HP5316A_GPIB_Settings (
    final double gateTime_s,
    final MeasurementFunction measurementFunction,
    final TriggerSlope channelATriggerSlope,
    final TriggerSlope channelBTriggerSlope,
    final GatingMode gatingMode,
    final boolean endOfMeasurementSrq,
    final GateTimeControl gateTimeControl,
    final TriggerLevelControl triggerLevelControl,
    final double aChannelTriggerLevel_V,
    final double bChannelTriggerLevel_V)
  {
    super (
      getBytes (
        gateTime_s,
        measurementFunction,
        channelATriggerSlope,
        channelBTriggerSlope,
        gatingMode,
        endOfMeasurementSrq,
        gateTimeControl,
        triggerLevelControl,
        aChannelTriggerLevel_V,
        bChannelTriggerLevel_V),
      gateTime_s,
      getReadingUnit (measurementFunction));
    this.measurementFunction = measurementFunction;
    this.channelATriggerSlope = channelATriggerSlope;
    this.channelBTriggerSlope = channelBTriggerSlope;
    this.gatingMode = gatingMode;
    this.endOfMeasurementSrq = endOfMeasurementSrq;
    this.gateTimeControl = gateTimeControl;
    this.triggerLevelControl = triggerLevelControl;
    this.aChannelTriggerLevel_V = aChannelTriggerLevel_V;
    this.bChannelTriggerLevel_V = bChannelTriggerLevel_V;
  }

  public final static HP5316A_GPIB_Settings INSTRUMENT_INIT_SETTINGS = new HP5316A_GPIB_Settings (
    1.0,
    MeasurementFunction.FN01_FREQUENCY_A,
    TriggerSlope.POSITIVE,
    TriggerSlope.POSITIVE,
    GatingMode.CONTINUOUS,
    false,
    GateTimeControl.GA0_LONG_FRONT,
    TriggerLevelControl.FRONT_PANEL,
    0,
    0);
  
  public final static HP5316A_GPIB_Settings DEFAULT_SETTINGS = new HP5316A_GPIB_Settings (
    1.0,
    MeasurementFunction.FN01_FREQUENCY_A,
    TriggerSlope.POSITIVE,
    TriggerSlope.POSITIVE,
    GatingMode.ONCE,
    true,
    GateTimeControl.GA0_LONG_FRONT,
    TriggerLevelControl.REMOTE_HP_IB,
    0,
    0);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  protected final String getCanonicalStringForInstrument ()
  {
    return HP5316A_GPIB_Settings.getCanonicalStringForInstrument (
      this.measurementFunction,
      this.channelATriggerSlope,
      this.channelBTriggerSlope,
      this.gatingMode,
      this.endOfMeasurementSrq,
      this.gateTimeControl,
      this.triggerLevelControl,
      this.aChannelTriggerLevel_V,
      this.bChannelTriggerLevel_V);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MEASUREMENT FUNCTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum MeasurementFunction
  {
    
    FN00_ROLLING_DISPLAY_TEST ("FN0"),
    FN01_FREQUENCY_A ("FN1"),
    FN02_TIME_INTERVAL_A_TO_B ("FN2"),
    FN03_TIME_INTERVAL_DELAY ("FN3"),
    FN04_RATIO_A_OVER_B ("FN4"),
    FN05_FREQUENCY_C ("FN5"),
    FN06_TOTALIZE_STOP ("FN6"),
    FN07_PERIOD_A ("FN7"),
    FN08_TIME_INTERVAL_AVERAGE_A_TO_B ("FN8"),
    FN09_CHECK_10MHZ ("FN9"),
    FN10_A_GATED_BY_B ("FN10"),
    FN11_GATE_TIME ("FN11"),
    FN12_TOTALIZE_START ("FN12"),
    FN13_FREQUENCY_A_AVG_ARMED_BY_B_POS_SLOPE ("FN13"),
    FN14_FREQUENCY_A_AVG_ARMED_BY_B_NEG_SLOPE ("FN14"),
    FN16_HPIB_INTERFACE_TEST ("FN16");

    final String canonicalString;
    
    private MeasurementFunction (final String canonicalString)
    {
      this.canonicalString = canonicalString;
    }
    
    public final String toCanonicalString ()
    {
      return this.canonicalString;
    }
    
    @Override
    public final String toString ()
    {
      return super.toString ();
    }
    
  }
  
  private final MeasurementFunction measurementFunction;
  
  public final MeasurementFunction getMeasurementFunction ()
  {
    return this.measurementFunction;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRIGGER SLOPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum TriggerSlope
  {
    POSITIVE,
    NEGATIVE;
  }
  
  private final TriggerSlope channelATriggerSlope;
  
  public final TriggerSlope getChannelATriggerSlope ()
  {
    return this.channelATriggerSlope;
  }
  
  private final TriggerSlope channelBTriggerSlope;

  public final TriggerSlope getChannelBTriggerSlope ()
  {
    return this.channelBTriggerSlope;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GATING MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum GatingMode
  {
    
    CONTINUOUS ("WA0"),
    ONCE ("WA1");

    private final String string;
    
    private GatingMode (final String string)
    {
      this.string = string;
    }
    
    @Override
    public final String toString ()
    {
      return this.string;
    }

  }
  
  private final GatingMode gatingMode;

  public final GatingMode getGatingMode ()
  {
    return this.gatingMode;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END-OF-MEASUREMENT SRQ
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean endOfMeasurementSrq;

  public final boolean isEndOfMeasurementSrq ()
  {
    return this.endOfMeasurementSrq;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GATE TIME CONTROL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum GateTimeControl
  {
    
    GA0_LONG_FRONT ("GA0"),
    GA1_SHORT_FRONT ("GA1"),
    GA2_LONG_REAR ("GA2"),
    GA3_SHORT_REAR ("GA3");
    
    final String string;

    private GateTimeControl (final String string)
    {
      this.string = string;
    }
    
    @Override
    public final String toString ()
    {
      return this.string;
    }
    
  }
  
  private final GateTimeControl gateTimeControl;

  public final GateTimeControl getGateTimeControl ()
  {
    return this.gateTimeControl;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRIGGER LEVEL CONTROL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum TriggerLevelControl
  {
    
    FRONT_PANEL ("TR0"),
    REMOTE_HP_IB ("TR1");

    private final String string;
    
    private TriggerLevelControl (final String string)
    {
      this.string = string;
    }
    
    @Override
    public final String toString ()
    {
      return this.string;
    }
    
  }
  
  private final TriggerLevelControl triggerLevelControl;

  public final TriggerLevelControl getTriggerLevelControl ()
  {
    return this.triggerLevelControl;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRIGGER LEVELS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static double MINIMUM_TRIGGER_LEVEL_V = -2.5;
  
  public final static double MAXIMUM_TRIGGER_LEVEL_V = 2.5;
  
  private static double boundedTriggerLevel (final double triggerLevel_V)
  {
    return Math.max (MINIMUM_TRIGGER_LEVEL_V, Math.min (MAXIMUM_TRIGGER_LEVEL_V, triggerLevel_V));
  }
  
  private final double aChannelTriggerLevel_V;

  public final double getAChannelTriggerLevel_V ()
  {
    return this.aChannelTriggerLevel_V;
  }

  private final double bChannelTriggerLevel_V;

  public final double getBChannelTriggerLevel_V ()
  {
    return this.bChannelTriggerLevel_V;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final boolean equals (final Object obj)
  {
    return super.equals (obj);
  }

  @Override
  public final int hashCode ()
  {
    return super.hashCode ();
  }
  
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
