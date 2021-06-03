/* 
 * Copyright 2010-2021 Jan de Jongh <jfcmdejongh@gmail.com>.
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

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.javajdj.jservice.Service;

/** A software representation of a physical instrument.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface Instrument
  extends Service
{
  
  String getInstrumentUrl ();
  
  Device getDevice ();
  
  void addInstrumentListener (InstrumentListener l);
  
  void removeInstrumentListener (InstrumentListener l);
  
  String getInstrumentId ();
  
  InstrumentStatus getCurrentInstrumentStatus ();
  
  InstrumentSettings getCurrentInstrumentSettings ();
  
  boolean isPoweredOn ();
  
  void setPoweredOn (boolean poweredOn);
  
  /** Adds (queues) an {@code InstrumentCommand} for asynchronous execution on this instrument.
   * 
   * <p>
   * The asynchronous execution of (appropriate) {@link InstrumentCommand}s is one of the
   * {@link Service} tasks of an {@link Instrument}.
   * Unless specified otherwise, {@link InstrumentCommand}s added to an {@link Instrument}
   * are processed in First-Come First-Served order.
   * 
   * <p>
   * Implementations of this method must <i>never</i> block.
   * 
   * <p>
   * The behavior of this method when the {@link Service} is not <i>active</i> is implementation dependent.
   * (This includes the option of dropping the command.)
   * 
   * <p>
   * In case of finite (command) buffer size, a buffer-full condition upon invoking this method leads to
   * dropping the command.
   * 
   * @param instrumentCommand The command, non-{@code null}.
   * 
   * @throws IllegalArgumentException If the command is {@code null}.
   * 
   * @see InstrumentCommand
   * @see Service
   * 
   */
  void addCommand (InstrumentCommand instrumentCommand);
  
  /** Adds (queues) an {@code InstrumentCommand} for execution on this instrument and awaits its completion.
   * 
   * <p>
   * This method essentially wraps invocation of {@link #addCommand} with synchronization means.
   * See the description of that method for some details on the execution of {@link InstrumentCommand}s.
   * 
   * @param instrumentCommand The command, non-{@code null}.
   * @param timeout           The timeout (in units given as third argument).
   * @param unit              The time unit in which the timeout is to be interpreted.
   * 
   * @throws IllegalArgumentException If the command is {@code null}.
   * @throws IOException          If communication with the controller, device or instrument resulted in an error.
   * @throws InterruptedException If the current thread was interrupted while awaiting command completion.
   * @throws TimeoutException     If a timeout occurred (this may be an earlier timeout than the one supplied).
   * 
   * @see #addCommand
   * @see InstrumentCommand
   * @see Service
   * 
   */
  void addAndProcessCommandSync (InstrumentCommand instrumentCommand, long timeout, TimeUnit unit)
    throws IOException, InterruptedException, TimeoutException;

  @FunctionalInterface
  interface InstrumentAction
  {
    
    void action () throws IOException, InterruptedException;
    
  }
  
  @FunctionalInterface
  interface InstrumentSetter_1Boolean
  {
    
    void set (boolean arg) throws IOException, InterruptedException;
    
  }
  
  @FunctionalInterface
  interface InstrumentSetter_1Number<N extends Number>
  {
    
    void set (N arg) throws IOException, InterruptedException;
    
  }
  
  @FunctionalInterface
  interface InstrumentSetter_1Integer
    extends InstrumentSetter_1Number<Integer>
  {
    
    @Override
    void set (Integer arg) throws IOException, InterruptedException;
    
    default int byteToArg   (final byte   b) { return (int) b; }
    default int shortToArg  (final short  s) { return (int) s; }
    default int intToArg    (final int    i) { return (int) i; }
    default int longToArg   (final long   l) { return (int) l; }
    default int floatToArg  (final float  f) { return (int) Math.round (f); }
    default int doubleToArg (final double d) { return (int) Math.round (d); }
    
  }
  
  @FunctionalInterface
  interface InstrumentSetter_1int
    extends InstrumentSetter_1Number<Integer>
  {
    
    void set (int arg) throws IOException, InterruptedException;
    
    @Override
    default void set (final Integer arg)  throws IOException, InterruptedException { set ((int) arg); }
    
    default int byteToArg   (final byte   b) { return (int) b; }
    default int shortToArg  (final short  s) { return (int) s; }
    default int intToArg    (final int    i) { return (int) i; }
    default int longToArg   (final long   l) { return (int) l; }
    default int floatToArg  (final float  f) { return (int) Math.round (f); }
    default int doubleToArg (final double d) { return (int) Math.round (d); }
    
  }
  
  @FunctionalInterface
  interface InstrumentSetter_1Long
    extends InstrumentSetter_1Number<Long>
  {
    
    @Override
    void set (Long arg) throws IOException, InterruptedException;
    
    default long byteToArg   (final byte   b) { return (long) b; }
    default long shortToArg  (final short  s) { return (long) s; }
    default long intToArg    (final int    i) { return (long) i; }
    default long longToArg   (final long   l) { return (long) l; }
    default long floatToArg  (final float  f) { return (long) Math.round (f); }
    default long doubleToArg (final double d) { return (long) Math.round (d); }
    
  }
  
  @FunctionalInterface
  interface InstrumentSetter_1long
    extends InstrumentSetter_1Number<Long>
  {
    
    void set (long arg) throws IOException, InterruptedException;
    
    @Override
    default void set (final Long arg)  throws IOException, InterruptedException { set ((long) arg); }
    
    default long byteToArg   (final byte   b) { return (long) b; }
    default long shortToArg  (final short  s) { return (long) s; }
    default long intToArg    (final int    i) { return (long) i; }
    default long longToArg   (final long   l) { return (long) l; }
    default long floatToArg  (final float  f) { return (long) Math.round (f); }
    default long doubleToArg (final double d) { return (long) Math.round (d); }
    
  }
  
  @FunctionalInterface
  interface InstrumentSetter_1Double
    extends InstrumentSetter_1Number<Double>
  {
    
    void set (Double arg) throws IOException, InterruptedException;
    
    default double byteToArg   (final byte   b) { return (double) b; }
    default double shortToArg  (final short  s) { return (double) s; }
    default double intToArg    (final int    i) { return (double) i; }
    default double longToArg   (final long   l) { return (double) l; }
    default double floatToArg  (final float  f) { return (double) f; }
    default double doubleToArg (final double d) { return (double) d; }
    
  }
  
  @FunctionalInterface
  interface InstrumentSetter_1double
    extends InstrumentSetter_1Number<Double>
  {
    
    void set (double arg) throws IOException, InterruptedException;
    
    @Override
    default void set (final Double arg)  throws IOException, InterruptedException { set ((double) arg); }
    
    default double byteToArg   (final byte   b) { return (double) b; }
    default double shortToArg  (final short  s) { return (double) s; }
    default double intToArg    (final int    i) { return (double) i; }
    default double longToArg   (final long   l) { return (double) l; }
    default double floatToArg  (final float  f) { return (double) f; }
    default double doubleToArg (final double d) { return (double) d; }
    
  }
  
  @FunctionalInterface
  interface InstrumentSetter_1Enum<E extends Enum<E>>
  {
    
    void set (E arg) throws IOException, InterruptedException;
    
  }
  
  @FunctionalInterface
  interface InstrumentSetter_1String
  {
    
    void set (String arg) throws IOException, InterruptedException;
    
  }
  
}
