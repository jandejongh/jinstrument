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

import java.io.IOException;
import java.util.List;

/** Extension of {@link Instrument} for (typically LF) single-channel function generators.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface FunctionGenerator
  extends Instrument
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // WAVEFORM
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  enum Waveform
  {
    DC,
    SINE,
    TRIANGLE,
    RAMP_UP,
    RAMP_DOWN,
    SQUARE,
    PULSE;
  }

  List<Waveform> getSupportedWaveforms ();
  
  default boolean isSupportedWaveform (final Waveform waveform)
  {
    final List<Waveform> supportedWaveforms = getSupportedWaveforms ();
    return supportedWaveforms != null && supportedWaveforms.contains (waveform);
  }
  
  void setWaveform (Waveform Waveform)
    throws IOException, InterruptedException;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  double getMinFrequency_Hz ();
  
  double getMaxFrequency_Hz ();
  
  void setFrequency_Hz (double centerFrequency_Hz)
    throws IOException, InterruptedException;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
