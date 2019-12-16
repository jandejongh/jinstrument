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
package org.javajdj.jinstrument.instrument.sg;

import java.io.IOException;
import org.javajdj.jinstrument.Instrument;

/** Extension of {@link Instrument} for (typically RF) signal generators.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface SignalGenerator
  extends Instrument
{

  enum RfSweepMode
  {
    OFF,
    AUTO,
    MANUAL,
    SINGLE;
  }
  
  enum ModulationSource
  {
    INT,
    INT_400,
    INT_1K,
    EXT_AC,
    EXT_DC;
  }
  
  void setSettingsOnInstrument (SignalGeneratorSettings signalGeneratorSettings) throws IOException, InterruptedException;
  
  void setCenterFrequency_MHz (double centerFrequency_MHz) throws IOException, InterruptedException;

  void setRfSweepMode (RfSweepMode rfSweepMode) throws IOException, InterruptedException;
  
  void setSpan_MHz (double span_MHz) throws IOException, InterruptedException;
  
  void setStartFrequency_MHz (double startFrequency_MHz) throws IOException, InterruptedException;
  
  void setStopFrequency_MHz (double stopFrequency_MHz) throws IOException, InterruptedException;
  
  void setOutputEnable (boolean outputEnable) throws IOException, InterruptedException;
  
  void setAmplitude_dBm (double amplitude_dBm) throws IOException, InterruptedException;
  
  void setModulationSourceInternalFrequency_kHz (double ModulationSourceInternalFrequency_kHz)
    throws IOException, InterruptedException;

  void setEnableAm (boolean enableAm, double depth_percent, ModulationSource modulationSource)
    throws IOException, InterruptedException;
  
  void setEnableFm (boolean enableFm, double deviation_kHz, ModulationSource modulationSource)
    throws IOException, InterruptedException;
  
  void setEnablePm (boolean enablePm, double deviation_degrees, ModulationSource modulationSource)
    throws IOException, InterruptedException;
  
  void setEnablePulseM (boolean enablePulseM, ModulationSource modulationSource)
    throws IOException, InterruptedException;
  
  void setEnableBpsk (boolean enableBpsk, ModulationSource modulationSource)
    throws IOException, InterruptedException;
  
}
