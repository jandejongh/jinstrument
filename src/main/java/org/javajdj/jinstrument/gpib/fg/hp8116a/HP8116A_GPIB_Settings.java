/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.gpib.fg.hp8116a;

import java.nio.charset.Charset;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.javajdj.jinstrument.DefaultFunctionGeneratorSettings;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.FunctionGeneratorSettings;
import org.javajdj.jinstrument.InstrumentStatus;

/** Implementation of {@link InstrumentStatus} for the HP-8116A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP8116A_GPIB_Settings
  extends DefaultFunctionGeneratorSettings
  implements FunctionGeneratorSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP8116A_GPIB_Settings.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private HP8116A_GPIB_Settings (
    final byte[] bytes,
    final boolean outputEnable,
    final FunctionGenerator.Waveform waveform,
    final double frequency_Hz,
    final double amplitude_Vpp,
    final double dcOffset_V,
    final HP8116A_GPIB_Instrument.HP8116ATriggerMode triggerMode,
    final HP8116A_GPIB_Instrument.HP8116ATriggerControl triggerControl,
    final HP8116A_GPIB_Instrument.HP8116AControlMode controlMode,
    final HP8116A_GPIB_Instrument.HP8116AStartPhase startPhase,
    final boolean autoVernier,
    final boolean outputLimits,
    final boolean complementaryOutput,
    final int burstLength,
    final double repeatInterval_s,
    final double startFrequency_Hz,
    final double stopFrequency_Hz,
    final double sweepTime_s,
    final double markerFrequency_Hz,
    final double dutyCycle_percent,
    final double pulseWidth_s,
    final String cstString)
  {
    super (bytes, outputEnable, waveform, frequency_Hz, amplitude_Vpp, dcOffset_V);
    if (triggerMode == null)
      throw new IllegalArgumentException ();
    this.triggerMode = triggerMode;
    if (triggerControl == null)
      throw new IllegalArgumentException ();
    this.triggerControl = triggerControl;
    if (controlMode == null)
      throw new IllegalArgumentException ();
    this.controlMode = controlMode;
    if (startPhase == null)
      throw new IllegalArgumentException ();
    this.startPhase = startPhase;
    this.autoVernier = autoVernier;
    this.outputLimits = outputLimits;
    this.complementaryOutput = complementaryOutput;
    this.burstLength = burstLength;               // [OPTION001]
    this.repeatInterval_s = repeatInterval_s;     // [OPTION001]
    this.startFrequency_Hz = startFrequency_Hz;   // [OPTION001]
    this.stopFrequency_Hz = stopFrequency_Hz;     // [OPTION001]
    this.sweepTime_s = sweepTime_s;               // [OPTION001]
    this.markerFrequency_Hz = markerFrequency_Hz; // [OPTION001]
    this.dutyCycle_percent = dutyCycle_percent;
    this.pulseWidth_s = pulseWidth_s;
    this.cstString = cstString;
  }

  /** The maximum length of the CST string returned WITHOUT Option 001 installed.
   * 
   */
  private final static int CST_LENGTH_PLAIN = 89;
  
  /** The maximum length of the CST string returned with Option 001 installed.
   * 
   * <p>
   * With option 001 installed, the documented maximum length of the CST string returned is 161
   * (HP Part Number 08116-90003, d.d. Aug 1990).
   * 
   * <p>
   * Examination on my unit (with Option 001 installed) reveals that the array is sometimes actually 162 bytes in length,
   * including the two terminating bytes {@code 0x0a} and {@code 0x0d}.
   * 
   * <p>
   * After Java creation of a {@code String} from the byte array, the resulting {@code String} size is 160.
   * The creation apparently removes both termination bytes.
   * 
   * <p>
   * Note that the size of the returned CST array, either in bytes or as a {@code String} is
   * <i>not</i> always of the same length.
   * 
   */
  private final static int CST_MAX_LENGTH_OPTION001 = 161;
  
  public final static HP8116A_GPIB_Settings fromCstString (final String cstString)
  {
    if (cstString == null)
      throw new IllegalArgumentException ();
    // Assumption: The CST[Option001] String consists of 20 comma-separated parts (0-19).
    final String[] cstParts = cstString.split (Pattern.quote (","));
    if (cstParts == null || cstParts.length != 20)
      throw new IllegalArgumentException ();
    // Part 0: Trigger Modes: M1-M8 [M1-M4 without OPTION001]
    final HP8116A_GPIB_Instrument.HP8116ATriggerMode triggerMode;
    switch (cstParts[0].trim ())
    {
      case "M1":
        triggerMode = HP8116A_GPIB_Instrument.HP8116ATriggerMode.M1_NORM;
        break;
      case "M2":
        triggerMode = HP8116A_GPIB_Instrument.HP8116ATriggerMode.M2_TRIG;
        break;
      case "M3":
        triggerMode = HP8116A_GPIB_Instrument.HP8116ATriggerMode.M3_GATE;
        break;
      case "M4":
        triggerMode = HP8116A_GPIB_Instrument.HP8116ATriggerMode.M4_E_WID;
        break;
      case "M5":
        triggerMode = HP8116A_GPIB_Instrument.HP8116ATriggerMode.M5_I_SWP;
        break;
      case "M6":
        triggerMode = HP8116A_GPIB_Instrument.HP8116ATriggerMode.M6_E_SWP;
        break;
      case "M7":
        triggerMode = HP8116A_GPIB_Instrument.HP8116ATriggerMode.M7_I_BUR;
        break;
      case "M8":
        triggerMode = HP8116A_GPIB_Instrument.HP8116ATriggerMode.M8_E_BUR;
        break;
      default:
        throw new IllegalArgumentException ();
    }
    // Part 1: Control Modes: CT0-CT4
    final HP8116A_GPIB_Instrument.HP8116AControlMode controlMode;
    switch (cstParts[1].trim ())
    {
      case "CT0":
        controlMode = HP8116A_GPIB_Instrument.HP8116AControlMode.CT0_OFF;
        break;
      case "CT1":
        controlMode = HP8116A_GPIB_Instrument.HP8116AControlMode.CT1_FM;
        break;
      case "CT2":
        controlMode = HP8116A_GPIB_Instrument.HP8116AControlMode.CT2_AM;
        break;
      case "CT3":
        controlMode = HP8116A_GPIB_Instrument.HP8116AControlMode.CT3_PWM;
        break;
      case "CT4":
        controlMode = HP8116A_GPIB_Instrument.HP8116AControlMode.CT4_VCO;
        break;
      default:
        throw new IllegalArgumentException ();
    }    
    // Part 2: Trigger Control: T0-T2
    final HP8116A_GPIB_Instrument.HP8116ATriggerControl triggerControl;
    switch (cstParts[2].trim ())
    {
      case "T0":
        triggerControl = HP8116A_GPIB_Instrument.HP8116ATriggerControl.T0_TRIGGER_OFF;
        break;
      case "T1":
        triggerControl = HP8116A_GPIB_Instrument.HP8116ATriggerControl.T1_POSITIVE_TRIGGER_SLOPE;
        break;
      case "T2":
        triggerControl = HP8116A_GPIB_Instrument.HP8116ATriggerControl.T2_NEGATIVE_TRIGGER_SLOPE;
        break;
      default:
        throw new IllegalArgumentException ();
    }    
    // Part 3: Output Waveform: W0-W4
    final FunctionGenerator.Waveform waveform;
    switch (cstParts[3].trim ())
    {
      case "W0":
        waveform = FunctionGenerator.Waveform.DC;
        break;
      case "W1":
        waveform = FunctionGenerator.Waveform.SINE;
        break;
      case "W2":
        waveform = FunctionGenerator.Waveform.TRIANGLE;
        break;
      case "W3":
        waveform = FunctionGenerator.Waveform.SQUARE;
        break;
      case "W4":
        waveform = FunctionGenerator.Waveform.PULSE;
        break;
      default:
        throw new IllegalArgumentException ();
    }
    // Part 4: Start Phase: H0-H1
    final HP8116A_GPIB_Instrument.HP8116AStartPhase startPhase;
    switch (cstParts[4].trim ())
    {
      case "H0":
        startPhase = HP8116A_GPIB_Instrument.HP8116AStartPhase.H0_NORMAL;
        break;
      case "H1":
        startPhase = HP8116A_GPIB_Instrument.HP8116AStartPhase.H1_MINUS_90_DEGREES;
        break;
      default:
        throw new IllegalArgumentException ();
    }    
    // Part 5: Auto Vernier: A0-A1
    final boolean autoVernier;
    switch (cstParts[5].trim ())
    {
      case "A0":
        autoVernier = false;
        break;
      case "A1":
        autoVernier = true;
        break;
      default:
        throw new IllegalArgumentException ();
    }
    // Part 6: Output Limits: L0-L1
    final boolean outputLimits;
    switch (cstParts[6].trim ())
    {
      case "L0": outputLimits = false; break;
      case "L1": outputLimits = true; break;
      default: throw new IllegalArgumentException ();
    }
    // Part 7: Complement Output: C0-C1
    final boolean complementaryOutput;
    switch (cstParts[7].trim ())
    {
      case "C0": complementaryOutput = false; break;
      case "C1": complementaryOutput = true; break;
      default: throw new IllegalArgumentException ();      
    }    
    // Part 8: Enable/Disable Output: D0-D1
    final boolean outputEnable;
    switch (cstParts[8].trim ())
    {
      case "D0":
        outputEnable = true;
        break;
      case "D1":
        outputEnable = false;
        break;
      default:
        throw new IllegalArgumentException ();
    }
    // Part 9: Burst Number: BUR <int> '#'
    final int burstLength;
    if (! cstParts[9].trim ().startsWith ("BUR") && cstParts[9].trim ().endsWith ("#"))
      throw new IllegalArgumentException ();
    try
    {
      burstLength = Integer.parseInt (cstParts[9].trim ().substring (3, cstParts[9].trim ().length () - 1).trim ());
    }
    catch (NumberFormatException nfe)
    {
      throw new IllegalArgumentException (nfe);
    }
    // Part 10: Repeat Interval: RPT <int/double?> <time unit>
    final double repeatInterval_s;
    if (! cstParts[10].trim ().startsWith ("RPT"))
      throw new IllegalArgumentException ();
    else
    {
      final String valueString;
      final double conversionFactor;
      if (cstParts[10].trim ().endsWith ("NS"))
      {
        valueString = cstParts[10].trim ().substring (3, cstParts[10].trim ().length () - 2);
        conversionFactor = 1e-9;
      }
      else if (cstParts[10].trim ().endsWith ("US"))
      {
        valueString = cstParts[10].trim ().substring (3, cstParts[10].trim ().length () - 2);
        conversionFactor = 1e-6;
      }
      else if (cstParts[10].trim ().endsWith ("MS"))
      {
        valueString = cstParts[10].trim ().substring (3, cstParts[10].trim ().length () - 2);
        conversionFactor = 1e-3;
      }
      else
        throw new IllegalArgumentException ();
      try
      {
        repeatInterval_s = conversionFactor * Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException (nfe);
      }      
    }
    // Part 11: Start Frequency: STA <double> <freq unit>
    final double startFrequency_Hz;
    if (! cstParts[11].trim ().startsWith ("STA"))
      throw new IllegalArgumentException ();
    else
    {
      final String valueString;
      final double conversionFactor;
      if (cstParts[11].trim ().endsWith ("MHZ"))
      {
        valueString = cstParts[11].trim ().substring (3, cstParts[11].trim ().length () - 3);
        conversionFactor = 1e6;
      }
      else if (cstParts[11].trim ().endsWith ("KHZ"))
      {
        valueString = cstParts[11].trim ().substring (3, cstParts[11].trim ().length () - 3);
        conversionFactor = 1e3;
      }
      else if (cstParts[11].trim ().endsWith ("HZ"))
      {
        valueString = cstParts[11].trim ().substring (3, cstParts[11].trim ().length () - 2);
        conversionFactor = 1;
      }
      else if (cstParts[11].trim ().endsWith ("MZ"))
      {
        valueString = cstParts[11].trim ().substring (3, cstParts[11].trim ().length () - 2);
        conversionFactor = 1e-3;
      }
      else
        throw new IllegalArgumentException ();
      try
      {
        startFrequency_Hz = conversionFactor * Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException (nfe);
      }
    }
    // Part 12: Stop Frequency: STP <double> <Freq unit>
    final double stopFrequency_Hz;
    if (! cstParts[12].trim ().startsWith ("STP"))
      throw new IllegalArgumentException ();
    else
    {
      final String valueString;
      final double conversionFactor;
      if (cstParts[12].trim ().endsWith ("MHZ"))
      {
        valueString = cstParts[12].trim ().substring (3, cstParts[12].trim ().length () - 3);
        conversionFactor = 1e6;
      }
      else if (cstParts[12].trim ().endsWith ("KHZ"))
      {
        valueString = cstParts[12].trim ().substring (3, cstParts[12].trim ().length () - 3);
        conversionFactor = 1e3;
      }
      else if (cstParts[12].trim ().endsWith ("HZ"))
      {
        valueString = cstParts[12].trim ().substring (3, cstParts[12].trim ().length () - 2);
        conversionFactor = 1;
      }
      else if (cstParts[12].trim ().endsWith ("MZ"))
      {
        valueString = cstParts[12].trim ().substring (3, cstParts[12].trim ().length () - 2);
        conversionFactor = 1e-3;
      }
      else
        throw new IllegalArgumentException ();
      try
      {
        stopFrequency_Hz = conversionFactor * Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException (nfe);
      }
    }
    // Part 13: Sweep Time: SWT <double> <time unit>
    final double sweepTime_s;
    if (! cstParts[13].trim ().startsWith ("SWT"))
      throw new IllegalArgumentException ();
    else
    {
      final String valueString;
      final double conversionFactor;
      if (cstParts[13].trim ().endsWith ("MS"))
      {
        valueString = cstParts[13].trim ().substring (3, cstParts[13].trim ().length () - 2);
        conversionFactor = 1e-3;
      }
      else if (cstParts[13].trim ().endsWith ("S"))
      {
        valueString = cstParts[13].trim ().substring (3, cstParts[13].trim ().length () - 1);
        conversionFactor = 1;
      }
      else
        throw new IllegalArgumentException ();
      try
      {
        sweepTime_s = conversionFactor * Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException (nfe);
      }      
    }
    // Part 14: Marker Frequency: MRK <double> <freq unit>
    final double markerFrequency_Hz;
    if (! cstParts[14].trim ().startsWith ("MRK"))
      throw new IllegalArgumentException ();
    else
    {
      final String valueString;
      final double conversionFactor;
      if (cstParts[14].trim ().endsWith ("MHZ"))
      {
        valueString = cstParts[14].trim ().substring (3, cstParts[14].trim ().length () - 3);
        conversionFactor = 1e6;
      }
      else if (cstParts[14].trim ().endsWith ("KHZ"))
      {
        valueString = cstParts[14].trim ().substring (3, cstParts[14].trim ().length () - 3);
        conversionFactor = 1e3;
      }
      else if (cstParts[14].trim ().endsWith ("HZ"))
      {
        valueString = cstParts[14].trim ().substring (3, cstParts[14].trim ().length () - 2);
        conversionFactor = 1;
      }
      else if (cstParts[14].trim ().endsWith ("MZ"))
      {
        valueString = cstParts[14].trim ().substring (3, cstParts[14].trim ().length () - 2);
        conversionFactor = 1e-3;
      }
      else
        throw new IllegalArgumentException ();
      try
      {
        markerFrequency_Hz = conversionFactor * Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException (nfe);
      }
    }
    // Part 15: Frequency: FRQ <double> <freq unit>
    final double frequency_Hz;
    if (! cstParts[15].trim ().startsWith ("FRQ"))
      throw new IllegalArgumentException ();
    else
    {
      final String valueString;
      final double conversionFactor;
      // Note ordering of checks below :-)... Yes, it caught me by surprise...
      if (cstParts[15].trim ().endsWith ("MHZ"))
      {
        valueString = cstParts[15].trim ().substring (3, cstParts[15].trim ().length () - 3);
        conversionFactor = 1e6;
      }
      else if (cstParts[15].trim ().endsWith ("KHZ"))
      {
        valueString = cstParts[15].trim ().substring (3, cstParts[15].trim ().length () - 3);
        conversionFactor = 1e3;
      }
      else if (cstParts[15].trim ().endsWith ("HZ"))
      {
        valueString = cstParts[15].trim ().substring (3, cstParts[15].trim ().length () - 2);
        conversionFactor = 1;
      }
      else if (cstParts[15].trim ().endsWith ("MZ"))
      {
        valueString = cstParts[15].trim ().substring (3, cstParts[15].trim ().length () - 2);
        conversionFactor = 1e-3;
      }
      else
        throw new IllegalArgumentException ();
      try
      {
        frequency_Hz = conversionFactor * Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException (nfe);
      }
    }
    // Part 16: Duty Cycle: DTY <int/double?> '%'
    final double dutyCycle_percent;
    if (! cstParts[16].trim ().startsWith ("DTY") && cstParts[16].trim ().endsWith ("%"))
      throw new IllegalArgumentException ();
    try
    {
      dutyCycle_percent = Double.parseDouble (cstParts[16].trim ().substring (3, cstParts[16].trim ().length () - 1));
    }
    catch (NumberFormatException nfe)
    {
      throw new IllegalArgumentException (nfe);
    }
    // Part 17: Pulse Width: WID <double> <time unit>
    final double pulseWidth_s;
    if (! cstParts[17].trim ().startsWith ("WID"))
      throw new IllegalArgumentException ();
    else
    {
      final String valueString;
      final double conversionFactor;
      if (cstParts[17].trim ().endsWith ("NS"))
      {
        valueString = cstParts[17].trim ().substring (3, cstParts[17].trim ().length () - 2);
        conversionFactor = 1e-9;
      }
      else if (cstParts[17].trim ().endsWith ("US"))
      {
        valueString = cstParts[17].trim ().substring (3, cstParts[17].trim ().length () - 2);
        conversionFactor = 1e-6;
      }
      else if (cstParts[17].trim ().endsWith ("MS"))
      {
        valueString = cstParts[17].trim ().substring (3, cstParts[17].trim ().length () - 2);
        conversionFactor = 1e-3;
      }
      else
        throw new IllegalArgumentException ();
      try
      {
        pulseWidth_s = conversionFactor * Double.parseDouble (valueString);
      }
      catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException (nfe);
      }      
    }
    // Part 18: Amplitude OR High Level: AMP <int/double?> <voltage units> OR HIL <double> <voltage unit>
    // Part 19: DC Offset OR Low Level: OFS <int/double?> <voltage units> OR LOL <double> <voltage unit>
    final double amplitude_Vpp;
    final double dcOffset_V;
    if (cstParts[18].trim ().startsWith ("AMP") && cstParts[19].trim ().startsWith ("OFS"))
    {
      final String amplitudeValueString;
      final double amplitudeConversionFactor;
      if (cstParts[18].trim ().endsWith ("MV"))
      {
        amplitudeValueString = cstParts[18].trim ().substring (3, cstParts[18].trim ().length () - 2);
        amplitudeConversionFactor = 1e-3;        
      }
      else if (cstParts[18].trim ().endsWith ("V"))
      {
        amplitudeValueString = cstParts[18].trim ().substring (3, cstParts[18].trim ().length () - 1);
        amplitudeConversionFactor = 1;        
      }
      else
        throw new IllegalArgumentException ();
      try
      {
        amplitude_Vpp = amplitudeConversionFactor * Double.parseDouble (amplitudeValueString);
      }
      catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException (nfe);
      }
      final String offsetValueString;
      final double offsetConversionFactor;
      if (cstParts[19].trim ().endsWith ("MV"))
      {
        offsetValueString = cstParts[19].trim ().substring (3, cstParts[19].trim ().length () - 2);
        offsetConversionFactor = 1e-3;        
      }
      else if (cstParts[19].trim ().endsWith ("V"))
      {
        offsetValueString = cstParts[19].trim ().substring (3, cstParts[19].trim ().length () - 1);
        offsetConversionFactor = 1;
      }
      else
        throw new IllegalArgumentException ();
      try
      {
        dcOffset_V = offsetConversionFactor * Double.parseDouble (offsetValueString);
      }
      catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException (nfe);
      }
    }
    else if (cstParts[18].trim ().startsWith ("HIL") && cstParts[19].trim ().startsWith ("LOL"))
    {
      final double hil_V;
      final double lol_V;
      if (! cstParts[18].trim ().endsWith ("V"))
        throw new IllegalArgumentException ();
      if (! cstParts[19].trim ().endsWith ("V"))
        throw new IllegalArgumentException ();
      try
      {
        hil_V = Double.parseDouble (cstParts[18].trim ().substring (3, cstParts[18].trim ().length () - 1));
        lol_V = Double.parseDouble (cstParts[19].trim ().substring (3, cstParts[19].trim ().length () - 1));
        // Assumption is that hil_V ALWAYS >= lol_V.
        if (hil_V < lol_V)
          throw new UnsupportedOperationException ();
        // Assumption is peak-to-peak value!
        amplitude_Vpp = hil_V - lol_V;
        dcOffset_V = 0.5 * (hil_V + lol_V); // XXX Is this always correct (pulse)?
      }
      catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException (nfe);
      }
    }
    else
      throw new IllegalArgumentException ();
    // LOG.log (Level.WARNING, "OutputEnable={0}, Waveform={1}, Frequency={2}Hz, Amplitude={3}Vpp, DC Offset={4}V.",
    //  new Object[]{outputEnable, waveform, frequency_Hz, amplitude_Vpp, dcOffset_V});
    return new HP8116A_GPIB_Settings (
      cstString.getBytes (Charset.forName ("US-ASCII")),
      outputEnable,
      waveform,
      frequency_Hz,
      amplitude_Vpp,
      dcOffset_V,
      triggerMode,
      triggerControl,
      controlMode,
      startPhase,
      autoVernier,
      outputLimits,
      complementaryOutput,
      burstLength,
      repeatInterval_s,
      startFrequency_Hz,
      stopFrequency_Hz,
      sweepTime_s,
      markerFrequency_Hz,
      dutyCycle_percent,
      pulseWidth_s,
      cstString);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final String cstString;
  
  @Override
  public String toString ()
  {
    return this.cstString;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRIGGER MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final HP8116A_GPIB_Instrument.HP8116ATriggerMode triggerMode;
  
  public final HP8116A_GPIB_Instrument.HP8116ATriggerMode getTriggerMode ()
  {
    return this.triggerMode;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRIGGER CONTROL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final HP8116A_GPIB_Instrument.HP8116ATriggerControl triggerControl;
  
  public final HP8116A_GPIB_Instrument.HP8116ATriggerControl getTriggerControl ()
  {
    return this.triggerControl;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROL MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final HP8116A_GPIB_Instrument.HP8116AControlMode controlMode;
  
  public final HP8116A_GPIB_Instrument.HP8116AControlMode getControlMode ()
  {
    return this.controlMode;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // START PHASE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final HP8116A_GPIB_Instrument.HP8116AStartPhase startPhase;
  
  public final HP8116A_GPIB_Instrument.HP8116AStartPhase getStartPhase ()
  {
    return this.startPhase;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AUTO VERNIER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean autoVernier;
  
  public final boolean isAutoVernier ()
  {
    return this.autoVernier;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // OUTPUT LIMITS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean outputLimits;
  
  public final boolean isOutputLimits ()
  {
    return this.outputLimits;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COMPLEMENTARY OUTPUT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean complementaryOutput;
  
  public final boolean isComplementaryOutput ()
  {
    return this.complementaryOutput;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BURST LENGTH
  // [OPTION001]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final int burstLength;
  
  public final int getBurstLength ()
  {
    return this.burstLength;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // REPEAT INTERVAL
  // [OPTION001]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double repeatInterval_s;
  
  public final double getRepeatInterval_s ()
  {
    return this.repeatInterval_s;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // START FREQUENCY
  // STOP FREQUENCY
  // [OPTION001]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double startFrequency_Hz;

  public final double getStartFrequency_Hz ()
  {
    return this.startFrequency_Hz;
  }

  private final double stopFrequency_Hz;
  
  public final double getStopFrequency_Hz ()
  {
    return this.stopFrequency_Hz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWEEP TIME
  // [OPTION001]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private double sweepTime_s;

  public final double getSweepTime_s ()
  {
    return this.sweepTime_s;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MARKER FREQUENCY
  // [OPTION001]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private double markerFrequency_Hz;
  
  public final double getMarkerFrequency_Hz ()
  {
    return this.markerFrequency_Hz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DUTY CYCLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private double dutyCycle_percent;
  
  public final double getDutyCycle_percent ()
  {
    return this.dutyCycle_percent;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PULSE WIDTH
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double pulseWidth_s;
  
  public final double getPulseWidth_s ()
  {
    return this.pulseWidth_s;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
