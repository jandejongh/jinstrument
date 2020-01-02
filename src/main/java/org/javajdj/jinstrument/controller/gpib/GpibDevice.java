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
import java.util.function.Function;
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
  // Device
  // BUS ADDRESS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public GpibAddress getBusAddress ();
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COMMAND
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  void doControllerCommandAsync (
    GpibControllerCommand command,
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
    throws UnsupportedOperationException;
  
  void doControllerCommandSync (GpibControllerCommand command, final long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // readEOI
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateReadEOICommand ()
    throws UnsupportedOperationException;
  
  void readEOIAsync (
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
    throws UnsupportedOperationException;
  
  byte[] readEOISync (long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // readln
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateReadlnCommand (ReadlineTerminationMode readlineTerminationMode)
    throws UnsupportedOperationException;
  
  void readlnAsync (
    ReadlineTerminationMode readlineTerminationMode,
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
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
  
  void readNAsync (
    int N,
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
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
  
  void writeAsync (
    byte[] bytes,
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
    throws UnsupportedOperationException;
  
  void writeSync (byte[] bytes, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // writeAndReadEOI
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateWriteAndReadEOICommand (byte[] bytes)
    throws UnsupportedOperationException;
  
  void writeAndReadEOIAsync (
    byte[] bytes,
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
    throws UnsupportedOperationException;
  
  byte[] writeAndReadEOISync (byte[] bytes, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // writeAndReadln
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateWriteAndReadlnCommand (byte[] bytes, ReadlineTerminationMode readlineTerminationMode)
    throws UnsupportedOperationException;
  
  void writeAndReadlnAsync (
    byte[] bytes,
    ReadlineTerminationMode readlineTerminationMode,
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
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
  
  void writeAndReadNAsync (
    byte[] bytes,
    int N,
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
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
  
  void writeAndReadlnNAsync (
    byte[] bytes,
    ReadlineTerminationMode readlineTerminationMode,
    int N,
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
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
  
  void atomicSequenceAsync (
    GpibControllerCommand[] sequence,
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
    throws UnsupportedOperationException;
  
  void atomicSequenceSync (GpibControllerCommand[] sequence, long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // serialPoll
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateSerialPollCommand ()
    throws UnsupportedOperationException;
  
  void serialPollAsync (
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
    throws UnsupportedOperationException;
  
  byte serialPollSync (long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // atomicRepeatUntil
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  GpibControllerCommand generateAtomicRepeatUntilCommand (
    GpibControllerCommand commandToRepeat,
    Function<GpibControllerCommand, Boolean> condition);
  
  void atomicRepeatUntilAsync (
    GpibControllerCommand commandToRepeat,
    Function<GpibControllerCommand, Boolean> condition,
    Long queueingTimeout_ms,
    Long processingTimeout_ms,
    Long sojournTimeout_ms)
    throws UnsupportedOperationException;
  
  void atomicRepeatUntilSync (
    GpibControllerCommand commandToRepeat,
    Function<GpibControllerCommand, Boolean> condition,
    long timeout_ms)
    throws InterruptedException, IOException, TimeoutException, UnsupportedOperationException;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
