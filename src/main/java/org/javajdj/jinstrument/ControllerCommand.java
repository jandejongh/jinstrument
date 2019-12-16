/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>, TNO.
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

import java.util.Map;

/** A command for a {@link Controller}.
 * 
 * Controller commands are {@code Map}s from {@link String}s to {@link Object}s.
 * The interface provides {@code String} definitions for the most common commands and
 * their arguments.
 * 
 * @see Controller#addCommand
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface ControllerCommand
  extends Map<String, Object>
{
  
  public final static String CC_COMMAND_KEY = "command";
  
  public final static String CC_EXCEPTION_KEY = "exception";
  
  public final static String CC_RETURN_STATUS_KEY = "returnStatus";
  
  public final static String CC_RETURN_VALUE_KEY = "returnValue";
  
  public final static String CC_NOP_KEY = "commandNop";
  
  public final static String CC_PROPE_KEY = "commandProbe";
  
  public final static String CC_GET_SETTINGS_KEY = "commandGetSettings";
  
}
