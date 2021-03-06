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
import org.javajdj.junits.Resolution;
import org.javajdj.junits.Unit;
import org.javajdj.jinstrument.util.Util;

/** Implementation of {@link DigitalStorageOscilloscopeTrace} for the Tektronix-2440.
 *
 * <p>
 * XXX Note: This really only cover like the Yn (Yt) cases like for Ch1, Ch2, Add, Mult.
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
  
  /** Creates the trace from data received in response to a "CURVE?" command.
   * 
   * @param settings The current instrument settings.
   * @param channel  The channel to which the curve data applies; overrides the applicable data in {@code settings}.
   * @param bytes    The (raw) data received from the instrument.
   * 
   * @throws IllegalArgumentException If any of the arguments is {@code null} or the {@code bytes} array has an illegal
   *                                  (or unrecognized) structure.
   * 
   */
  public Tek2440_GPIB_Trace (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    super (settings,
      channel,
      toSamples (settings, channel, bytes),
      toMinNHint (settings, channel, bytes),
      toMaxNHint (settings, channel, bytes),
      toMinXHint (settings, channel, bytes),
      toMaxXHint (settings, channel, bytes),
      toMinYHint (settings, channel, bytes),
      toMaxYHint (settings, channel, bytes),
      toUnit (settings, channel, bytes),
      toResolution (settings, channel, bytes), 
      toError (settings, channel, bytes),
      toErrorMessage (settings, channel, bytes),
      toOverflow (settings, channel, bytes),
      toUncalibrated (settings, channel, bytes),
      toUncorrected (settings, channel, bytes));
    this.bytes = bytes;
  }
  
  private final static int HEADER_SAMPLE_LENGTH = 10;
  
  private static double[] toSamples (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    if (bytes.length < HEADER_SAMPLE_LENGTH) // For now; we need a header sample to be more accurate.
      throw new IllegalArgumentException ();
    final String headerSample =
      new String (Arrays.copyOf (bytes, HEADER_SAMPLE_LENGTH), Charset.forName ("US-ASCII")).toLowerCase ();
    final int pastPercentSign;
    if (headerSample.startsWith ("curve %"))
      pastPercentSign = 7; // "curve %".length ();
    else if (headerSample.startsWith ("curv %"))
      pastPercentSign = 6; // "curv %".length ();
    else if (headerSample.startsWith ("%"))
      pastPercentSign = 1; // "%".length ();
    else
      throw new IllegalArgumentException ();
    if (bytes.length < TEK_2440_SAMPLE_LENGTH + pastPercentSign + 2 /* length encoding */ + 1 /* checksum */)
      throw new IllegalArgumentException ();
    if (! (bytes[pastPercentSign] == 0x04 && bytes[pastPercentSign + 1] == 0x01))
      throw new IllegalArgumentException ();
    final int header = pastPercentSign + 2;
    return toSamples (settings, channel, bytes, header);
  }

  private static double[] toSamples (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes,
    final int header)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    final double[] samples = new double[TEK_2440_SAMPLE_LENGTH];
    final Tek2440_GPIB_Settings.VoltsPerDivision voltsPerDiv = settings.getVoltsPerDivision (channel);
    final double voltsPerDiv_V = voltsPerDiv.getVoltsPerDivision_V ();
    for (int i=0; i < TEK_2440_SAMPLE_LENGTH; i++)
      switch (settings.getDataEncoding ())
      {
        case ASCII:
          // XXX
          throw new UnsupportedOperationException ();
        case RPBinary:
        {
          // Resolution: 1/25th of a division.
          // Implementation tested and proved correctly 20201215.
          final double YLevelsPerDiv = Tek2440_GPIB_Instrument.TEK2440_DIGITIZING_LEVELS_PER_DIVISION;
          samples[i] = voltsPerDiv_V * ((0xff & bytes[header + i]) - 128) / YLevelsPerDiv;
          break;
        }
        case RIBinary:
        {
          // Resolution: 1/25th of a division.
          // Implementation tested and proved correctly 20201215.
          final double YLevelsPerDiv = Tek2440_GPIB_Instrument.TEK2440_DIGITIZING_LEVELS_PER_DIVISION;
          samples[i] = voltsPerDiv_V * bytes[header + i] / YLevelsPerDiv;
          break;
        }
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

  private static double toMinNHint (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // The number of samples in the waveform; index of 0 inclusive to N exclusive.
    final double N = (double) Tek2440_GPIB_Instrument.TEK2440_SAMPLES_PER_WAVEFORM;
    // The "index" in the trace data that is centered on the display of the Tek-2440 (and, by default, on our own display).
    // This index is made to correspond to zero in the (returned) X range.
    final double n_c = settings.getHorizontalPosition ();
    // The number of samples per display (as a double).
    final double Nd = Tek2440_GPIB_Instrument.TEK2440_SAMPLES_PER_DISPLAY;
    final double n_min = n_c - Nd / 2;
    return n_min;
  }
  
  private static double toMaxNHint (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // The number of samples in the waveform; index of 0 inclusive to N exclusive.
    final double N = (double) Tek2440_GPIB_Instrument.TEK2440_SAMPLES_PER_WAVEFORM;
    // The "index" in the trace data that is centered on the display of the Tek-2440 (and, by default, on our own display).
    // This index is made to correspond to zero in the (returned) X range.
    final double n_c = settings.getHorizontalPosition ();
    // The number of samples per display (as a double).
    final double Nd = Tek2440_GPIB_Instrument.TEK2440_SAMPLES_PER_DISPLAY;
    final double n_max = n_c + Nd / 2;
    return n_max;
  }
  
  private static double toMinXHint (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // The total number of X divisions the waveform data stretches.
    final double divX = Tek2440_GPIB_Instrument.TEK2440_X_DIVISIONS_PER_WAVEFORM;
    // The number of samples in the waveform; index of 0 inclusive to N exclusive.
    final double N = (double) Tek2440_GPIB_Instrument.TEK2440_SAMPLES_PER_WAVEFORM;
    // The "index" in the trace data that is centered on the display of the Tek-2440 (and, by default, on our own display).
    // This index is made to correspond to zero in the (returned) X range.
    final double n_c = settings.getHorizontalPosition ();
    // The relative ([-1, 1]) X position of the minimum (first) index value of the trace data.
    final double minX_rel = - n_c / N;
    // The relative ([-1, 1]) X position of the maximum (last) index value of the trace data.
    final double maxX_rel = (N - n_c) / N;
    switch (channel)
    {
      case Ch1:
      {
        // XXX Ch1 always on A?
        // The X span in seconds.
        final double Xs_s = settings.getASecondsPerDivision ().getSecondsPerDivision_s () * divX;
        final double minX = minX_rel * Xs_s;
        return minX;
      }
      case Ch2:
      {
        // XXX Ch2 always on B?
        // The X span in seconds.
        final double Xs_s = settings.getBSecondsPerDivision ().getSecondsPerDivision_s () * divX;
        final double minX = minX_rel * Xs_s;
        return minX;
      }
      case Add:
      case Mult:
        // Actually, unsupported...
        return 0;
      case Ch1Del:
      case Ch2Del:
      case AddDel:
      case MultDel:
        throw new UnsupportedOperationException ();
      case Ref1:
      case Ref2:
      case Ref3:
      case Ref4:
        throw new UnsupportedOperationException ();
      default:
        throw new UnsupportedOperationException ();
    }
  }
  
  private static double toMaxXHint (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // The total number of X divisions the waveform data stretches.
    final double divX = Tek2440_GPIB_Instrument.TEK2440_X_DIVISIONS_PER_WAVEFORM;
    // The number of samples in the waveform; index of 0 inclusive to N exclusive.
    final double N = (double) Tek2440_GPIB_Instrument.TEK2440_SAMPLES_PER_WAVEFORM;
    // The "index" in the trace data that is centered on the display of the Tek-2440 (and, by default, on our own display).
    // This index is made to correspond to zero in the (returned) X range.
    final double n_c = settings.getHorizontalPosition ();
    // The relative ([-1, 1]) X position of the minimum (first) index value of the trace data.
    final double minX_rel = - n_c / N;
    // The relative ([-1, 1]) X position of the maximum (last) index value of the trace data.
    final double maxX_rel = (N - n_c) / N;
    switch (channel)
    {
      case Ch1:
      {
        // XXX Ch1 always on A?
        // The X span in seconds.
        final double Xs_s = settings.getASecondsPerDivision ().getSecondsPerDivision_s () * divX;
        final double maxX = maxX_rel * Xs_s;
        return maxX;
      }
      case Ch2:
      {
        // XXX Ch2 always on B?
        // The X span in seconds.
        final double Xs_s = settings.getBSecondsPerDivision ().getSecondsPerDivision_s () * divX;
        final double maxX = maxX_rel * Xs_s;
        return maxX;
      }
      case Add:
      case Mult:
        // Actually, unsupported...
        return settings.getASecondsPerDivision ().getSecondsPerDivision_s () * Tek2440_GPIB_Instrument.TEK2440_X_DIVISIONS;
      case Ch1Del:
      case Ch2Del:
      case AddDel:
      case MultDel:
        throw new UnsupportedOperationException ();
      case Ref1:
      case Ref2:
      case Ref3:
      case Ref4:
        throw new UnsupportedOperationException ();
      default:
        throw new UnsupportedOperationException ();
    }
  }
  
  private static double toMinYHint (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // Implementation tested and proved correctly 20201215.
    final Tek2440_GPIB_Settings.VoltsPerDivision voltsPerDiv = settings.getVoltsPerDivision (channel);
    final double voltsPerDiv_V = voltsPerDiv.getVoltsPerDivision_V ();
    return - voltsPerDiv_V * Tek2440_GPIB_Instrument.TEK2440_Y_DIVISIONS / 2.0;    
  }
  
  private static double toMaxYHint (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // Implementation tested and proved correctly 20201215.
    final Tek2440_GPIB_Settings.VoltsPerDivision voltsPerDiv = settings.getVoltsPerDivision (channel);
    final double voltsPerDiv_V = voltsPerDiv.getVoltsPerDivision_V ();
    return voltsPerDiv_V * Tek2440_GPIB_Instrument.TEK2440_Y_DIVISIONS / 2.0;
  }
  
  private static Unit toUnit (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX Always???
    return Unit.UNIT_V;
  }
  
  private static Resolution toResolution (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX Check??
    return Resolution.DIGITS_2;
  }
  
  private static boolean toError (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX
    return true;
  }
  
  private static String toErrorMessage (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX
    return "Unsupported";
  }
  
  private static boolean toOverflow (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX
    return true;
  }
  
  private static boolean toUncalibrated (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
      throw new IllegalArgumentException ();
    // XXX
    return true;    
  }
  
  private static boolean toUncorrected (
    final Tek2440_GPIB_Settings settings,
    final Tek2440_GPIB_Settings.DataSource channel,
    final byte[] bytes)
  {
    if (settings == null || channel == null || bytes == null)
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
