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
package org.javajdj.jinstrument;

/** A reading from an {@link Instrument}.
 * 
 * <p>
 * This class applies mostly to <i>measurement</i> {@link Instrument}s.
 * Reading may be as simple as a single value or as complex as multiple related traces.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface InstrumentReading
{
  
  InstrumentSettings getInstrumentSettings ();

  boolean isError ();
  
  String getErrorMessage ();
  
  boolean isUncalibrated ();
  
  boolean isUncorrected ();
  
}
