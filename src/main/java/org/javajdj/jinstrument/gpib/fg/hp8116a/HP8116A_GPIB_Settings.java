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
    final FunctionGenerator.Waveform waveform,
    final double frequency_Hz,
    final String cstString)
  {
    super (bytes, waveform, frequency_Hz);
    this.cstString = cstString;
  }

  /** The length of the CST string returned WITHOUT Option 001 installed.
   * 
   */
  private final static int CST_LENGTH_PLAIN = 88; // XXX VALUE GUESSED!! I APPEAR TO HAVE OPTION 001 INSTALLED! SEE BELOW XXX!
  
  /** The length of the CST string returned with Option 001 installed.
   * 
   * <p>
   * With option 001 installed, the documented maximum length of the CST string returned is 161
   * (HP Part Number 08116-90003, d.d. Aug 1990).
   * Examination on my unit (with Option 001 installed) reveals that the array is actually 162 bytes in length,
   * including the two terminating bytes {@code 0x0a} and {@code 0x0d}.
   * It makes (some) sense that HP reported 161 bytes in case their BASIC implementation 
   * 
   * <p>
   * After Java creation of a {@code String} from the byte array, the resulting {@code String} size is 160.
   * The creation apparently removes both termination bytes.
   * 
   * <p>
   * At this time, I do not know whether the array can vary in size, but we'll soon find out.
   * If it varies, we're in some trouble, and we better remove the length check.
   * 
   * <p>
   * Update: Well, it can change! Just found out while working in the mHz range.
   * 
   */
  private final static int CST_LENGTH_OPTION001 = 160;
  
  public final static HP8116A_GPIB_Settings fromCstString (final String cstString)
  {
    if (cstString == null)
      throw new IllegalArgumentException ();
//    if (cstString.length () != CST_LENGTH_OPTION001 /* XXX FIX ME for 001 NOT installed! */)
//      throw new IllegalArgumentException ();
    // Assumption: The CST[001] String consists of 20 comma-separated parts (0-19).
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
        outputEnable = false;
        break;
      case "D1":
        outputEnable = true;
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
    LOG.log (Level.WARNING, "Waveform={0}, Frequency={1}Hz.", new Object[]{waveform, frequency_Hz});
    return new HP8116A_GPIB_Settings (
      cstString.getBytes (Charset.forName ("US-ASCII")),
      waveform,
      frequency_Hz,
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
