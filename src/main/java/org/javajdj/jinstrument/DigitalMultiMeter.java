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
package org.javajdj.jinstrument;

import org.javajdj.junits.Resolution;
import org.javajdj.junits.Unit;
import java.io.IOException;
import java.util.List;

/** Extension of {@link Instrument} for single-channel digital multi-meters.
 * 
 * <p>
 * A {@link DigitalMultiMeter} supports the measurement
 * of a temporary single selection
 * of a diverse set of physical (electro-technical)
 * quantities from its single set of main input terminals.
 * Typically, the input terminals required depend on what is being measured.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface DigitalMultiMeter
  extends Instrument
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MEASUREMENT MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  enum MeasurementMode
  {
    NONE,
    UNKNOWN,
    UNSUPPORTED,
    DC_VOLTAGE,
    AC_VOLTAGE,
    AC_DC_VOLTAGE,
    RESISTANCE_2W,
    RESISTANCE_4W,
    DC_CURRENT,
    AC_CURRENT,
    AC_DC_CURRENT,
    FREQUENCY,
    PERIOD,
    CAPACITANCE,
    INDUCTANCE,
    IMPEDANCE,
    TEMPERATURE;
  }
  
  List<MeasurementMode> getSupportedMeasurementModes ();
  
  boolean supportsMeasurementMode (MeasurementMode measurementMode);
  
  void setMeasurementMode (MeasurementMode measurementMode)
    throws IOException, InterruptedException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RANGE
  //
  // XXX THIS IS A CANDIDATE FOR TRANSFERRAL TO THE junits REPO! XXX
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** A contiguous range (interval) of (real) values of some one-dimensional physical quantity.
   * 
   * <p>
   * Implementations must be immutable.
   * 
   * Refer to {@link Range.Type} for instructions on which of the {@code default} methods to override.
   * 
   */
  interface Range
  {
    
    /** The type of {@link Range}.
     * 
     * <p>
     * The type property of {@link Range} allows for quick construction of often-used intervals
     * (symmetric around the origin, one-side, etc.).
     * 
     * <p>
     * Note that a range may be singleton, i.e., consisting of a single point, but that it <i>cannot</i> be empty.
     * 
     */
    enum Type
    {
      
      /** An arbitrary contiguous interval from a given minimum value to a given maximum value.
       * 
       * <p>
       * Implementations returning an arbitrary interval type through overriding {@link #getType}
       * must provide the minimum and maximum boundaries of the interval through
       * overriding {@link #getMinValue} and {@link #getMaxValue}, respectively.
       * 
       * <p>
       * In addition, they must provide the physical {@link Unit} of the range
       * through implementing {@link #getUnit}.
       * 
       */
      Arbitary,
      
      /** A symmetric interval around the origin (default).
       * 
       * <p>
       * Since this is the default interval type,
       * implementations of this range type must only override {@link #getMaxAbsValue}.
       * 
       * <p>
       * In addition, they must provide the physical {@link Unit} of the range
       * through implementing {@link #getUnit}.
       * 
       */
      SymmetricAroundZero,
      
      /** An interval from a fixed negative number to the origin.
       * 
       * <p>
       * Implementations returning a negative to zero interval type through overriding {@link #getType}
       * must only (also) override {@link #getMaxAbsValue}.
       * 
       * <p>
       * In addition, they must provide the physical {@link Unit} of the range
       * through implementing {@link #getUnit}.
       * 
       */
      NegativeTowardsZero,
      
      /** An interval from the origin to a fixed positive number.
       * 
       * <p>
       * Implementations returning a zero to positive interval type through overriding {@link #getType}
       * must only (also) override {@link #getMaxAbsValue}.
       * 
       * <p>
       * In addition, they must provide the physical {@link Unit} of the range
       * through implementing {@link #getUnit}.
       * 
       */
      PositiveFromZero;
      
    }

    /** Returns the type of the range.
     * 
     * <p>
     * The default implementation returns range type {@link Type#SymmetricAroundZero};
     * it must be overridden for any other {@link Type}.
     * 
     * @return The type of the range non-{@code null}.
     * 
     */
    default Type getType ()
    {
      return Type.SymmetricAroundZero;
    }
    
    /** Returns the minimum (most negative) value of the range.
     * 
     * <p>
     * It must be overridden for range type {@link Type#Arbitary}.
     * 
     * @return The minimum (most negative) value of the range.
     * 
     */
    default double getMinValue ()
    {
      switch (getType ())
      {
        case Arbitary:
          // Must be overriden!
          throw new UnsupportedOperationException ();
        case SymmetricAroundZero:
        case NegativeTowardsZero:
          return -getMaxAbsValue ();
        case PositiveFromZero:
          return 0;
        default:
          throw new RuntimeException ();
      }
    }
    
    /** Returns the maximum (most positive) value of the range.
     * 
     * <p>
     * It must be overridden for range type {@link Type#Arbitary}.
     * 
     * @return The maximum (most positive) value of the range.
     * 
     */
    default double getMaxValue ()
    {
      switch (getType ())
      {
        case Arbitary:
          // Must be overriden!
          throw new UnsupportedOperationException ();
        case SymmetricAroundZero:
        case PositiveFromZero:
          return getMaxAbsValue ();
        case NegativeTowardsZero:
          return 0;
        default:
          throw new RuntimeException ();
      }
    }
    
    /** Returns whether this range is a symmetric interval around zero.
     * 
     * @return Whether this range is a symmetric interval around zero.
     * 
     */
    default boolean isSymmetricAroundZero ()
    {
      switch (getType ())
      {
        case Arbitary:
          return getMinValue () < 0 && getMaxValue () > 0 && getMinValue () == -getMaxValue ();
        case SymmetricAroundZero:
          return true;
        case NegativeTowardsZero:
        case PositiveFromZero:
          return false;
        default:
          throw new RuntimeException ();
      }
    }
    
    /** Return the maximum absolute value of the endpoints of the range.
     * 
     * <p>
     * It must be overridden for range types
     * {@link Type#SymmetricAroundZero},
     * {@link Type#NegativeTowardsZero},
     * and {@link Type#PositiveFromZero}.
     * 
     * @return The maximum absolute value of the endpoints of the range, {@code >= 0}.
     * 
     */
    default double getMaxAbsValue ()
    {
      switch (getType ())
      {
        case Arbitary:
          return Math.max (Math.abs (getMinValue ()), Math.abs (getMaxValue ()));
        case SymmetricAroundZero:
        case NegativeTowardsZero:
        case PositiveFromZero:
          // Must be overriden!
          throw new UnsupportedOperationException ();
        default:
          throw new RuntimeException ();
      }
    }
    
    /** Returns the {@link Unit} to which this range applies.
     * 
     * <p>
     * The unit may be zero for those few cases in which the range applies to quantities other than
     * (regular) physical ones. This, however should be rare...
     * 
     * @return The {@link Unit} to which this range applies, may be {@code null}.
     * 
     */
    Unit getUnit ();
    
    /** Creates an arbitrary {@link Range}.
     * 
     * <p>
     * Convenience factory method.
     * 
     * <p>
     * Note that this method always returns a {@link Type#Arbitary} range,
     * even if the range is symmetric around or one-sided towards or from the origin.
     *             
     * 
     * @param minValue The minimum value of the range.
     * @param maxValue The maximum value of the range.
     * @param unit     The unit.
     * 
     * @return The range.
     * 
     * @throws IllegalArgumentException If {@code minValue > maxValue} or if the unit is {@code null}.
     * 
     * @see Type#Arbitary
     * 
     */
    public static Range arbitrary (final double minValue, final double maxValue, final Unit unit)
    {
      if (minValue > maxValue || unit == null)
        throw new IllegalArgumentException ();
      return new Range ()
      {
        @Override public final Type   getType ()     { return Range.Type.Arbitary; }
        @Override public final double getMinValue () { return minValue; }
        @Override public final double getMaxValue () { return maxValue; }
        @Override public Unit         getUnit ()     { return unit;}
      }; 
    }
    
    /** Creates a symmetric {@link Range} around the origin.
     * 
     * <p>
     * Convenience factory method.
     * 
     * <p>
     * Note that you can supply any number; the method will interpret the number, depending on its sign,
     * either as the negative minimum or the positive maximum of the range.
     * 
     * @param minOrMaxValue The minimum (negative or zero) or maximum (zero or positive) value of the range.
     * @param unit          The unit.
     * 
     * @return The range.
     * 
     * @throws IllegalArgumentException If the unit is {@code null}.
     * 
     * @see Type#SymmetricAroundZero
     * 
     */
    public static Range symmetricAroundZero (final double minOrMaxValue, final Unit unit)
    {
      if (unit == null)
        throw new IllegalArgumentException ();
      return new Range ()
      {
        @Override public final Type   getType ()        { return Range.Type.SymmetricAroundZero; }
        @Override public final double getMaxAbsValue () { return Math.abs (minOrMaxValue); }
        @Override public Unit         getUnit ()        { return unit;}
      }; 
    }
    
    /** Creates a one-sided negative {@link Range} towards the origin.
     * 
     * <p>
     * Convenience factory method.
     * 
     * <p>
     * Note that you can supply any number; the method will interpret the number, depending on its sign,
     * either as the negative minimum or positive length of the range.
     * 
     * @param minOrAbsMinValue The minimum (negative or zero) or length (zero or positive) value of the range.
     * @param unit             The unit.
     * 
     * @return The range.
     * 
     * @throws IllegalArgumentException If the unit is {@code null}.
     * 
     * @see Type#NegativeTowardsZero
     * 
     */
    public static Range negativeTowardsZero (final double minOrAbsMinValue, final Unit unit)
    {
      if (unit == null)
        throw new IllegalArgumentException ();
      return new Range ()
      {
        @Override public final Type getType ()          { return Range.Type.NegativeTowardsZero; }
        @Override public final double getMaxAbsValue () { return Math.abs (minOrAbsMinValue); }
        @Override public Unit getUnit ()                { return unit;}
      }; 
    }

    /** Creates a one-sided positive {@link Range} starting at the origin.
     * 
     * <p>
     * Convenience factory method.
     * 
     * @param maxValue The maximum value of the range ({@code >= 0}).
     * @param unit     The unit.
     * 
     * @return The range.
     * 
     * @throws IllegalArgumentException If {@code maxValue < 0} or the unit is {@code null}.
     * 
     * @see Type#PositiveFromZero
     * 
     */
    public static Range positiveFromZero (final double maxValue, final Unit unit)
    {
      if (maxValue < 0 || unit == null)
        throw new IllegalArgumentException ();
      return new Range ()
      {
        @Override public final Type getType ()          { return Range.Type.PositiveFromZero; }
        @Override public final double getMaxAbsValue () { return maxValue; }
        @Override public Unit getUnit ()                { return unit;}
      }; 
    }

  }
  
  /** Returns a list of supported range for a given measurement mode.
   * 
   * <p>
   * Implementations must return {@code null} if the mode is {@code null} or unsupported (instead of throwing an exception).
   * 
   * @param measurementMode The measurement mode.
   * 
   * @return The list holding the supported ranges for given measurement mode (may be {@code null}).
   * 
   */
  List<Range> getSupportedRanges (MeasurementMode measurementMode);

  /** Sets auto-ranging on the instrument.
   * 
   * <p>
   * The {@link MeasurementMode} remains unaffected.
   * 
   * @throws IOException                   If an I/O error occurred while sending the command to the instrument.
   * @throws InterruptedException          If processing the command was interrupted.
   * @throws UnsupportedOperationException If auto-ranging is not supported on the instrument.
   * 
   */
  void setAutoRange ()
    throws IOException, InterruptedException, UnsupportedOperationException;
  
  /** Sets the range on the instrument.
   * 
   * <p>
   * The {@link MeasurementMode} remains unaffected.
   * 
   * @param range The new range.
   * 
   * @throws IllegalArgumentException      If the argument is {@code null}.
   * @throws IOException                   If an I/O error occurred while sending the command to the instrument.
   * @throws InterruptedException          If processing the command was interrupted.
   * @throws UnsupportedOperationException If the range is not supported on the instrument.
   * 
   */
  void setRange (Range range)
    throws IOException, InterruptedException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RESOLUTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  List<Resolution> getSupportedResolutions ();
  
  void setResolution (Resolution resolution)
    throws IOException, InterruptedException;
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
