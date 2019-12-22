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
package org.javajdj.jinstrument.controller.gpib;

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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ADDRESS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibAddress getAddress ();
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READLINE TERMINATION MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  enum ReadlineTerminationMode
  {
    CR,
    LF,
    CR_LF,
    OPTCR_LF,
    LF_CR;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COMMAND
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  void addCommand (final GpibControllerCommand command)
    throws UnsupportedOperationException;
  
  void doControllerCommandSync (GpibControllerCommand command, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // read
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateReadCommand ()
    throws UnsupportedOperationException;
  
  void readAsync ()
    throws UnsupportedOperationException;
  
  byte[] readSync (long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // readln
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateReadlnCommand (ReadlineTerminationMode readlineTerminationMode)
    throws UnsupportedOperationException;
  
  void readlnAsync (ReadlineTerminationMode readlineTerminationMode)
    throws UnsupportedOperationException;
  
  byte[] readlnSync (ReadlineTerminationMode readlineTerminationMode, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // readN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateReadNCommand (int N)
    throws UnsupportedOperationException;
  
  void readNAsync (int N)
    throws UnsupportedOperationException;
  
  byte[] readNSync (int N, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // write
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateWriteCommand (byte[] bytes)
    throws UnsupportedOperationException;
  
  void writeAsync (byte[] bytes)
    throws UnsupportedOperationException;
  
  void writeSync (byte[] bytes, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // writeAndRead
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateWriteAndReadCommand (byte[] bytes)
    throws UnsupportedOperationException;
  
  void writeAndReadAsync (byte[] bytes)
    throws UnsupportedOperationException;
  
  byte[] writeAndReadSync (byte[] bytes, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // writeAndReadln
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateWriteAndReadlnCommand (byte[] bytes, ReadlineTerminationMode readlineTerminationMode)
    throws UnsupportedOperationException;
  
  void writeAndReadlnAsync (byte[] bytes, ReadlineTerminationMode readlineTerminationMode)
    throws UnsupportedOperationException;
  
  byte[] writeAndReadlnSync (byte[] bytes, ReadlineTerminationMode readlineTerminationMode, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // writeAndReadN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateWriteAndReadNCommand (byte[] bytes, int N)
    throws UnsupportedOperationException;
  
  void writeAndReadNAsync (byte[] bytes, int N)
    throws UnsupportedOperationException;
  
  byte[] writeAndReadNSync (byte[] bytes, int N, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // writeAndReadN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateWriteAndReadlnNCommand (byte[] bytes, ReadlineTerminationMode readlineTerminationMode, int N)
    throws UnsupportedOperationException;
  
  void writeAndReadlnNAsync (byte[] bytes, ReadlineTerminationMode readlineTerminationMode, int N)
    throws UnsupportedOperationException;
  
  byte[][] writeAndReadlnNSync (byte[] bytes, ReadlineTerminationMode readlineTerminationMode, int N, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // atomicSequence
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateAtomicSequenceCommand (GpibControllerCommand[] sequence)
    throws UnsupportedOperationException;
  
  void atomicSequenceAsync (GpibControllerCommand[] sequence)
    throws UnsupportedOperationException;
  
  void atomicSequenceSync (GpibControllerCommand[] sequence, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}