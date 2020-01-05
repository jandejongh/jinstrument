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
import java.util.logging.Level;
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
    final String cstString)
  {
    super (bytes, outputEnable, waveform, frequency_Hz, amplitude_Vpp, dcOffset_V);
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
    // Part 1: Control Modes: CT0-CT4
    // Part 2: Trigger Control: T0-T2
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
    // Part 5: Auto Vernier: A0-A1
    // Part 6: Output Limits: L0-L1
    // Part 7: Complement Output: C0-C1
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
    // Part 10: Repeat Interval: RPT <int/double?> <time unit>
    // Part 11: Start Frequency: STA <double> <freq unit>
    // Part 12: Stop Frequency: STP <double> <Freq unit>
    // Part 13: Sweep Time: SWT <double> <time unit>
    // Part 14: Marker Frequency: MRK <double> <freq unit>
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
    // Part 17: Pulse Width: WID <double> <time unit>
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
    LOG.log (Level.WARNING, "OutputEnable={0}, Waveform={1}, Frequency={2}Hz, Amplitude={3}Vpp, DC Offset={4}V.",
      new Object[]{outputEnable, waveform, frequency_Hz, amplitude_Vpp, dcOffset_V});
    return new HP8116A_GPIB_Settings (
      cstString.getBytes (Charset.forName ("US-ASCII")),
      outputEnable,
      waveform,
      frequency_Hz,
      amplitude_Vpp,
      dcOffset_V,
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
