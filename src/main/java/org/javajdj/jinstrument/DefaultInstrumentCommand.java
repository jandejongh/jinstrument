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

import java.util.LinkedHashMap;

/** Default implementation of an {@link InstrumentCommand}.
 * 
 * <p>
 * The main purpose of this class is to provide custom constructors with (some) additional error checking.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultInstrumentCommand
  extends LinkedHashMap<String, Object>
  implements InstrumentCommand
{

  public DefaultInstrumentCommand (
    final String command,
    final String arg1, final Object val1,
    final String arg2, final Object val2,
    final String arg3, final Object val3)
  {
    super ();
    if (command == null || command.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    if (arg1 != null && arg1.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    if (arg2 != null && arg2.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    if (arg3 != null && arg3.trim ().isEmpty ())
      throw new IllegalArgumentException ();
    put (InstrumentCommand.IC_COMMAND_KEY, command);
    if (arg1 != null)
      put (arg1, val1);
    if (arg2 != null)
      put (arg2, val2);
    if (arg3 != null)
      put (arg3, val3);
  }
  
  public DefaultInstrumentCommand (
    final String command,
    final String arg1, final Object val1,
    final String arg2, final Object val2)
  {
    this (command, arg1, val1, arg2, val2, null, null);
  }
  
  public DefaultInstrumentCommand (
    final String command,
    final String arg1, final Object val1)
  {
    this (command, arg1, val1, null, null, null, null);
  }
  
  public DefaultInstrumentCommand (final String command)
  {
    this (command, null, null, null, null, null, null);
  }
  
}
