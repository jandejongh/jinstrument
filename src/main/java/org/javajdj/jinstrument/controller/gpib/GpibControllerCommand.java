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

import org.javajdj.jinstrument.ControllerCommand;

/** Extension of {@link ControllerCommand} for GPIB controllers.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface GpibControllerCommand
  extends ControllerCommand
{
  
  public final static String CC_GPIB_READ_EOI = "gpibCommandReadEOI";
  public final static String CC_GPIB_READLN = "gpibCommandReadln";
  public final static String CC_GPIB_READ_N = "gpibCommandReadN";
  public final static String CC_GPIB_WRITE = "gpibCommandWrite";
  public final static String CC_GPIB_WRITE_AND_READ_EOI = "gpibCommandWriteAndReadEOI";
  public final static String CC_GPIB_WRITE_AND_READLN = "gpibCommandWriteAndReadln";
  public final static String CC_GPIB_WRITE_AND_READ_N = "gpibCommandWriteAndReadN";
  public final static String CC_GPIB_WRITE_AND_READLN_N = "gpibCommandWriteAndReadlnN";
  public final static String CC_GPIB_ATOMIC_SEQUENCE = "gpibCommandAtomicSequence";
  public final static String CC_GPIB_SERIAL_POLL = "gpibCommandSerialPoll";
  
  public final static String CCARG_GPIB_ADDRESS = "gpibArgAddress";
  public final static String CCARG_GPIB_READLN_TERMINATION_MODE = "gpibArgReadlnTerminationMode";
  public final static String CCARG_GPIB_READ_BYTES = "gpibArgReadBytes";
  public final static String CCARG_GPIB_READ_N = "gpibArgReadN";
  public final static String CCARG_GPIB_WRITE_BYTES = "gpibArgWriteBytes";
  public final static String CCARG_GPIB_ATOMIC_SEQUENCE = "gpibArgAtomicSequence";
  
  public final static String CCARG_GPIB_TIMEOUT_MS = "gpibArgTimeout_ms";
    
}
