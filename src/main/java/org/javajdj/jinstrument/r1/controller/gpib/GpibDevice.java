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
package org.javajdj.jinstrument.r1.controller.gpib;

import org.javajdj.jinstrument.instrument.controller.gpib.GpibAddress;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.javajdj.jinstrument.Device;

/** A device on a GPIB (General Purpose Interface Bus).
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface GpibDevice
  extends Device
{
  
  GpibAddress getAddress ();
  
  enum ReadlineTerminationMode
  {
    CR,
    LF,
    CR_LF,
    LF_CR;
  }
  
  void readAsync ()
    throws UnsupportedOperationException;
  
  byte[] readSync (long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
    
  void readlnAsync (ReadlineTerminationMode readlineTerminationMode)
    throws UnsupportedOperationException;
  
  byte[] readlnSync (ReadlineTerminationMode readlineTerminationMode, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
    
  void readNAsync (final int N)
    throws UnsupportedOperationException;
  
  byte[] readNSync (final int N, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  void writeAsync (byte[] bytes)
    throws UnsupportedOperationException;
  
  void writeSync (byte[] bytes, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  void writeAndReadAsync (byte[] bytes)
    throws UnsupportedOperationException;
  
  byte[] writeAndReadSync (byte[] bytes, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  void writeAndReadlnAsync (byte[] bytes, ReadlineTerminationMode readlineTerminationMode)
    throws UnsupportedOperationException;
  
  byte[] writeAndReadlnSync (byte[] bytes, ReadlineTerminationMode readlineTerminationMode, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  void writeAndReadNAsync (byte[] bytes, int N)
    throws UnsupportedOperationException;
  
  byte[] writeAndReadNSync (byte[] bytes, int N, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
}
