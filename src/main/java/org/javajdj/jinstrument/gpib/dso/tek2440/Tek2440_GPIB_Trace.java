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
import java.util.Arrays;
import org.javajdj.jinstrument.DefaultDigitalStorageOscilloscopeTrace;
import org.javajdj.jinstrument.DigitalStorageOscilloscopeTrace;
import org.javajdj.jinstrument.Resolution;
import org.javajdj.jinstrument.Unit;
import org.javajdj.jinstrument.util.Util;

/** Implementation of {@link DigitalStorageOscilloscopeTrace} for the Tektronix-2440.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class Tek2440_GPIB_Trace
  extends DefaultDigitalStorageOscilloscopeTrace
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static int TEK_2440_SAMPLE_LENGTH = 1024;
  
  public Tek2440_GPIB_Trace (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Instrument.Tek2440Channel channel,
    final byte[] bytes)
  {
    super (settings,
      channel,
      toSamples (settings, bytes),
      toMinXHint (settings, bytes),
      toMaxXHint (settings, bytes),
      toMinYHint (settings, bytes),
      toMaxYHint (settings, bytes),
      toUnit (settings, bytes),
      toResolution (settings, bytes), 
      toError (settings, bytes),
      toErrorMessage (settings, bytes),
      toOverflow (settings, bytes),
      toUncalibrated (settings, bytes),
      toUncorrected (settings, bytes));
    this.bytes = bytes;
  }
  
  public Tek2440_GPIB_Trace (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    this (settings, toChannel (settings, bytes), bytes);
  }
  
  private static Tek2440_GPIB_Instrument.Tek2440Channel toChannel (
    final Tek2440_GPIB_Settings settings,
    final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Instrument.Tek2440Channel channel;
    switch (settings.getDataSource ())
    {
      case Ch1:
        channel = Tek2440_GPIB_Instrument.Tek2440Channel.Channel1;
        break;
      case Ch2:
        channel = Tek2440_GPIB_Instrument.Tek2440Channel.Channel2;
        break;
      case Add:
      case Mult:
      case Ch1Del:
      case Ch2Del:
      case AddDel:
      case MultDel:
      case Ref1:
      case Ref2:
      case Ref3:
      case Ref4:
        throw new UnsupportedOperationException ();
      default:
        throw new RuntimeException ();
    }
    return channel;    
  }
  
  private static double[] toSamples (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    if (bytes.length < TEK_2440_SAMPLE_LENGTH + "CURVE %".length () + 2)
      throw new IllegalArgumentException ();
    final double[] samples = new double[TEK_2440_SAMPLE_LENGTH];
    final String headerSample = new String (Arrays.copyOf (bytes, "CURVE %".length () + 10), Charset.forName ("US-ASCII"));
    if (! headerSample.startsWith ("CURVE %"))
      throw new IllegalArgumentException ();
    // Check for proper length encoding of the trace (0x0401)...
    // Note: The length field includes the checksum.
    if (! (bytes["CURVE %".length ()] == 0x04 && bytes["CURVE %".length () + 1] == 0x01))
      throw new IllegalArgumentException ();
    final int header = "CURVE %".length () + 2;
    final Tek2440_GPIB_Instrument.Tek2440Channel channel;
    switch (settings.getDataSource ())
    {
      case Ch1:
        channel = Tek2440_GPIB_Instrument.Tek2440Channel.Channel1;
        break;
      case Ch2:
        channel = Tek2440_GPIB_Instrument.Tek2440Channel.Channel2;
        break;
      case Add:
      case Mult:
      case Ch1Del:
      case Ch2Del:
      case AddDel:
      case MultDel:
      case Ref1:
      case Ref2:
      case Ref3:
      case Ref4:
        throw new UnsupportedOperationException ();
      default:
        throw new RuntimeException ();
    }
    final Tek2440_GPIB_Settings.VoltsPerDivision voltsPerDiv = settings.getVoltsPerDivision (channel);
    final double voltsPerDiv_V = voltsPerDiv.getVoltsPerDivision_V ();
    for (int i=0; i < TEK_2440_SAMPLE_LENGTH; i++)
      switch (settings.getDataEncoding ())
      {
        case ASCII:
          // XXX
          throw new UnsupportedOperationException ();
        case RPBinary:
          // Resolution: 1/25th of a division.
          samples[i] = voltsPerDiv_V * ((0xff & bytes[header + i]) - 128) / 25.0;
          break;
        case RIBinary:
          // Resolution: 1/25th of a division.
          samples[i] = voltsPerDiv_V * bytes[header + i] / 25.0;
          break;
        case RPPartial:
          // XXX
          throw new UnsupportedOperationException ();
        case RIPartial:
          // XXX
          throw new UnsupportedOperationException ();
        default:
          throw new RuntimeException ();
      }
    return samples;
  }

  private static double toMinXHint (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    return 0;
  }
  
  private static double toMaxXHint (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    switch (toChannel (settings, bytes))
    {
      case Channel1:
        return settings.getASecondsPerDivision ().getSecondsPerDivision_s () * Tek2440_GPIB_Instrument.TEK2440_X_DIVISIONS;
      case Channel2:
        return settings.getBSecondsPerDivision ().getSecondsPerDivision_s () * Tek2440_GPIB_Instrument.TEK2440_X_DIVISIONS;
      default:
        throw new UnsupportedOperationException ();
    }
  }
  
  private static double toMinYHint (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Settings.VoltsPerDivision voltsPerDiv = settings.getVoltsPerDivision (toChannel (settings, bytes));
    final double voltsPerDiv_V = voltsPerDiv.getVoltsPerDivision_V ();
    return - voltsPerDiv_V * Tek2440_GPIB_Instrument.TEK2440_Y_DIVISIONS / 2.0;
    
  }
  
  private static double toMaxYHint (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    final Tek2440_GPIB_Settings.VoltsPerDivision voltsPerDiv = settings.getVoltsPerDivision (toChannel (settings, bytes));
    final double voltsPerDiv_V = voltsPerDiv.getVoltsPerDivision_V ();
    return voltsPerDiv_V * Tek2440_GPIB_Instrument.TEK2440_Y_DIVISIONS / 2.0;    
  }
  
  private static Unit toUnit (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX Always???
    return Unit.UNIT_V;
  }
  
  private static Resolution toResolution (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX Check??
    return Resolution.DIGITS_2;
  }
  
  private static boolean toError (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX
    return true;
  }
  
  private static String toErrorMessage (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX
    return "Unsupported";
  }
  
  private static boolean toOverflow (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX
    return true;
  }
  
  private static boolean toUncalibrated (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX
    return true;    
  }
  
  private static boolean toUncorrected (final Tek2440_GPIB_Settings settings, final byte[] bytes)
  {
    if (settings == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX
    return true;    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BYTES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final byte[] bytes;

  public final byte[] getBytes ()
  {
    return this.bytes;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String toString ()
  {
    return "0x" + Util.bytesToHex (this.bytes);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
