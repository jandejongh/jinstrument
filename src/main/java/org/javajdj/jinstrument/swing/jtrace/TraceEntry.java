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
package org.javajdj.jinstrument.swing.jtrace;

import java.awt.Color;
import java.util.logging.Logger;

/** A trace (array of doubles) augmented with boundary and graphics information.
 * 
 * <p>
 * This class is designed for internal use in {@link JTrace}, but is exposed
 * as it may be useful in other use cases as well.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class TraceEntry
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (TraceEntry.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public TraceEntry (
    final double[] trace,
    final double minX,
    final double maxX,
    final double minY,
    final double maxY,
    final Color color)
  {
    this.trace = trace;
    this.minX = minX;
    this.maxX = maxX;
    this.minY = minY;
    this.maxY = maxY;
    this.color = color;
  }

  public static TraceEntry EMPTY = new TraceEntry (
      null,
      Double.NaN, Double.NaN, Double.NaN, Double.NaN,
      null);
    
  public final TraceEntry withTrace (final double[] trace)
  {
    return new TraceEntry (
      trace,
      this.minX, this.maxX, this.minY, this.maxY,
      this.color);
  }
  
  public final TraceEntry withMinX (final double minX)
  {
    return new TraceEntry (
      this.trace,
      minX, this.maxX, this.minY, this.maxY,
      this.color);
  }
  
  public final TraceEntry withMaxX (final double maxX)
  {
    return new TraceEntry (
      this.trace,
      this.minX, maxX, this.minY, this.maxY,
      this.color);
  }
  
  public final TraceEntry withMinY (final double minY)
  {
    return new TraceEntry (
      this.trace,
      this.minX, this.maxX, minY, this.maxY,
      this.color);
  }
  
  public final TraceEntry withMaxY (final double maxY)
  {
    return new TraceEntry (
      this.trace,
      this.minX, this.maxX, this.minY, maxY,
      this.color);
  }
  
  public final TraceEntry withBoundaries (
    final double minX,
    final double maxX,
    final double minY,
    final double maxY)
  {
    return new TraceEntry (
      this.trace,
      minX, maxX, minY, maxY,
      this.color);
  }
  
  public final TraceEntry withColor (final Color color)
  {
    return new TraceEntry (
      this.trace,
      this.minX, this.maxX, this.minY, this.maxY,
      color);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double[] trace;
  
  public final double[] getTrace ()
  {
    return this.trace;
  }
  
  public final int getTraceLength ()
  {
    return this.trace != null ? this.trace.length : 0;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BOUNDARIES HINTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double minX;
  
  public final double getMinX ()
  {
    return this.minX;
  }

  private final double maxX;
  
  public final double getMaxX ()
  {
    return this.maxX;
  }

  private final double minY;
  
  public final double getMinY ()
  {
    return this.minY;
  }

  private final double maxY;

  public final double getMaxY ()
  {
    return this.maxY;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COLOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Color color;
  
  public final Color getColor ()
  {
    return this.color;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
