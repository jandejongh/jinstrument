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
package org.javajdj.jinstrument;

import java.io.IOException;
import org.javajdj.jservice.Service;

/** A software representation of a (or more) physical instrument.
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
  
  void addCommand (InstrumentCommand instrumentCommand);

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
