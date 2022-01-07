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
package org.javajdj.jinstrument.gpib.slm.rs_esh3;

import org.javajdj.jinstrument.*;
import org.javajdj.junits.Resolution;
import org.javajdj.junits.Unit;

/** Implementation of {@link InstrumentReading} for the Rohde{@code &}Schwarz ESH-3.
 *
 * <p>
 * Note that a reading over GPIB from the Rohde{@code &}Schwarz ESH-3
 * represents one of several physical quantities (level, modulation depth, etc.).
 * The distinction in this implementation is made through the {@link ReadingStatusType} {@code enum}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class RS_ESH3_GPIB_Reading
  extends AbstractInstrumentReading<Double>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public RS_ESH3_GPIB_Reading (
    final RS_ESH3_GPIB_Settings settings,
    final ReadingStatusType readingStatusType,
    final ReadingType readingType,
    final double reading)
  {
    super (settings,
      RS_ESH3_GPIB_Channel_RF_Input.getInstance (),
      reading,
      toUnit (readingType),
      toResolution (readingType),
      toError (readingStatusType),
      toErrorMessage (readingStatusType),
      toOverflow (readingStatusType),
      false,  // No way of knowing this...
      false); // No way of knowing this...
    if (readingType == null)
      throw new IllegalArgumentException ();
    this.readingStatusType = readingStatusType;
    this.readingType = readingType;
  }

  private static Unit toUnit (final ReadingType readingType)
  {
    switch (readingType)
    {
      case Frequency_MHz:
        return Unit.UNIT_MHz;
      case Level_dB:
        return Unit.UNIT_dB;
      case Power_dBm:
        return Unit.UNIT_dBm;
      case Voltage_muV:
        return Unit.UNIT_muV;
      case Current_dBmuA:
        // XXX
        return Unit.UNIT_NONE;
      case Current_muA:
        return Unit.UNIT_muA;
      case EFieldStrength_dBmuVpm:
        // XXX
        return Unit.UNIT_NONE;
      case EFieldStrength_muVpm:
        return Unit.UNIT_muVpm;
      case HFieldStrength_dBmuApm:
        // XXX
        return Unit.UNIT_NONE;
      case HFieldStrength_muApm:
        return Unit.UNIT_muApm;
      case ModulationDepth_Percent:
      case ModulationDepthPositivePeak_Percent:
      case ModulationDepthNegativePeak_Percent:
        return Unit.UNIT_Percent;
      case FrequencyOffset_kHz:
      case FrequencyDeviation_kHz:
      case FrequencyDeviationPositivePeak_kHz:
      case FrequencyDeviationNegativePeak_kHz:
        return Unit.UNIT_kHz;
      default:
        throw new UnsupportedOperationException ();
    }
  }
  
  private static Resolution toResolution (final ReadingType readingType)
  {
    switch (readingType)
    {
      case Frequency_MHz:
        // Specifications: 6 digits; resolution 100 Hz.
        return Resolution.DIGITS_6;
      case Level_dB:
        // Specifications: 4 digits; resolution 0.1 dB.
        return Resolution.DIGITS_4;
      case Power_dBm:
        // Specifications: 4 digits; resolution 0.1 dB.
        return Resolution.DIGITS_4;
      case Voltage_muV:
        // Specifications: 3 digits.
        return Resolution.DIGITS_3;
      case Current_dBmuA:
        // XXX Not in Specifications: Best Guess.
        return Resolution.DIGITS_4;
      case Current_muA:
        // XXX Not in Specifications: Best Guess.
        return Resolution.DIGITS_3;
      case EFieldStrength_dBmuVpm:
        // XXX Not in Specifications: Best Guess.
        return Resolution.DIGITS_4;
      case EFieldStrength_muVpm:
        // XXX Not in Specifications: Best Guess.
        return Resolution.DIGITS_3;
      case HFieldStrength_dBmuApm:
        // XXX Not in Specifications: Best Guess.
        return Resolution.DIGITS_4;
      case HFieldStrength_muApm:
        // XXX Not in Specifications: Best Guess.
        return Resolution.DIGITS_3;
      case ModulationDepth_Percent:
      case ModulationDepthPositivePeak_Percent:
      case ModulationDepthNegativePeak_Percent:
        // Specifications: 2 digits; range 2%...99%; resolution 1%.
        return Resolution.DIGITS_2;
      case FrequencyOffset_kHz:
        // Specifications: 3 digits; range +/- 5 kHz; resolution 0.01 kHz.
        return Resolution.DIGITS_3;
      case FrequencyDeviation_kHz:
      case FrequencyDeviationPositivePeak_kHz:
      case FrequencyDeviationNegativePeak_kHz:
        // Specifications: 3 digits; range 0.05-5 kHz (XXX no sign??); resolution 0.01 kHz.
        return Resolution.DIGITS_3;
      default:
        throw new UnsupportedOperationException ();
    }
  }
  
  private static boolean toError (final ReadingStatusType readingStatusType)
  {
    switch (readingStatusType)
    {
      case Valid:
        return false;
      case Overflow:
      case Underflow:
      case Overload:
        return true;
      default:
        throw new RuntimeException ();
    }
  }
  
  private static String toErrorMessage (final ReadingStatusType readingStatusType)
  {
    switch (readingStatusType)
    {
      case Valid:
        return null;
      case Overflow:
      case Underflow:
      case Overload:
        return readingStatusType.toString ();
      default:
        throw new RuntimeException ();
    }
  }
  
  private static boolean toOverflow (final ReadingStatusType readingStatusType)
  {
    switch (readingStatusType)
    {
      case Valid:
        return false;
      case Overflow:
      case Underflow:
      case Overload:
        return true;
      default:
        throw new RuntimeException ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READING STATUS TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum ReadingStatusType
  {
    
    Valid     (' ', "Valid"),
    Overflow  ('H', "Overflow"),
    Underflow ('O', "Underflow"),
    Overload  ('X', "Overload");
    
    private ReadingStatusType (final char definingChar, final String string)
    {
      this.definingChar = definingChar;
      this.string = string;
    }
    
    public static ReadingStatusType fromDefiningChar (final char definingChar)
    {
      switch (definingChar)
      {
        case ' ': return Valid;
        case 'H': return Overflow;
        case 'U': return Underflow;
        case 'X': return Overload;
        default:  throw new IllegalArgumentException ();
      }
    }
    
    public static boolean isValidDefiningChar (final char definingChar)
    {
      switch (definingChar)
      {
        case ' ':
        case 'H':
        case 'U':
        case 'X':
          return true;
        default:
          return false;
      }
    }
    
    private final char definingChar;
    
    private final String string;
    
    @Override
    public final String toString ()
    {
      return this.string;
    }
    
  };
  
  private final ReadingStatusType readingStatusType;
  
  public final ReadingStatusType getReadingStatusType ()
  {
    return this.readingStatusType;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READING TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum ReadingType
  {
    Frequency_MHz,
    Level_dB,
    Power_dBm,
    Voltage_muV,
    Current_dBmuA,
    Current_muA,
    EFieldStrength_dBmuVpm,
    EFieldStrength_muVpm,
    HFieldStrength_dBmuApm,
    HFieldStrength_muApm,
    ModulationDepth_Percent,
    ModulationDepthPositivePeak_Percent,
    ModulationDepthNegativePeak_Percent,
    FrequencyOffset_kHz,
    FrequencyDeviation_kHz,
    FrequencyDeviationPositivePeak_kHz,
    FrequencyDeviationNegativePeak_kHz;
  };
  
  private final ReadingType readingType;
  
  public final ReadingType getReadingType ()
  {
    return this.readingType;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getFrequency_MHz ()
  {
    if (this.readingType != ReadingType.Frequency_MHz)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LEVEL [dB]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getLevel_dB ()
  {
    if (this.readingType != ReadingType.Level_dB)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // POWER [dBm]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getPower_dBm ()
  {
    if (this.readingType != ReadingType.Power_dBm)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // VOLTAGE [muV]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getVoltage_muV ()
  {
    if (this.readingType != ReadingType.Voltage_muV)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CURRENT [dBmuA]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getCurrent_dBmuA ()
  {
    if (this.readingType != ReadingType.Current_dBmuA)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CURRENT [muA]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getCurrent_uA ()
  {
    if (this.readingType != ReadingType.Current_muA)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ELECTRIC FIELD STRENGTH [dBmuV/m]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getElectricFieldStrength_dBmuVpm ()
  {
    if (this.readingType != ReadingType.EFieldStrength_dBmuVpm)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ELECTRIC FIELD STRENGTH [muV/m]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getElectricFieldStrength_muVpm ()
  {
    if (this.readingType != ReadingType.EFieldStrength_muVpm)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MAGNETIC FIELD STRENGTH [dBmuA/m]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getMagneticFieldStrength_dBmuApm ()
  {
    if (this.readingType != ReadingType.HFieldStrength_dBmuApm)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MAGNETIC FIELD STRENGTH [muA/m]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getMagneticFieldStrength_muApm ()
  {
    if (this.readingType != ReadingType.HFieldStrength_muApm)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MODULATION DEPTH [%]
  // MODULATION DEPTH POSITIVE PEAK [%]
  // MODULATION DEPTH NEGATIVE PEAK [%]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getModulationDepth_Percent ()
  {
    if (this.readingType != ReadingType.ModulationDepth_Percent)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  public final double getModulationDepthPositivePeak_Percent ()
  {
    if (this.readingType != ReadingType.ModulationDepthPositivePeak_Percent)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  public final double getModulationDepthNegativePeak_Percent ()
  {
    if (this.readingType != ReadingType.ModulationDepthNegativePeak_Percent)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FREQUENCY OFFSET [kHz]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getFrequencyOffset_kHz ()
  {
    if (this.readingType != ReadingType.FrequencyOffset_kHz)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FREQUENCY DEVIATION [%]
  // FREQUENCY DEVIATION POSITIVE PEAK [%]
  // FREQUENCY DEVIATION NEGATIVE PEAK [%]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final double getFrequencyDeviation_kHz ()
  {
    if (this.readingType != ReadingType.FrequencyDeviation_kHz)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  public final double getFrequencyDeviationPositivePeak_kHz ()
  {
    if (this.readingType != ReadingType.FrequencyDeviationPositivePeak_kHz)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  public final double getFrequencyDeviationNegativePeak_kHz ()
  {
    if (this.readingType != ReadingType.FrequencyDeviationNegativePeak_kHz)
      throw new IllegalStateException ();
    return getReadingValue ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
