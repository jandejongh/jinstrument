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
package org.javajdj.jinstrument.gpib.fc;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultInstrumentCommand;
import org.javajdj.jinstrument.FrequencyCounter;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.gpib.AbstractGpibInstrument;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jservice.Service;

/** Abstract base implementation of a {@link FrequencyCounter} for GPIB instruments.
 *
 * @param <M> The type of modes (functions, measurement functions, etc.) on the instrument.
 * @param <T> The type of trigger modes.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public abstract class AbstractGpibFrequencyCounter<M, T>
  extends AbstractGpibInstrument
  implements FrequencyCounter<M, T>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (AbstractGpibFrequencyCounter.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected AbstractGpibFrequencyCounter (
    final String name,
    final GpibDevice device,
    final List<Runnable> runnables,
    final List<Service> targetServices,
    final boolean addInitializationServices,
    final boolean addStatusServices,
    final boolean addSettingsServices,
    final boolean addCommandProcessorServices,
    final boolean addAcquisitionServices,
    final boolean addHousekeepingServices,
    final boolean addServiceRequestPollingServices)
  {
    super (name,
      device,
      runnables,
      targetServices,
      addInitializationServices,
      addStatusServices,
      addSettingsServices,
      addCommandProcessorServices,
      addAcquisitionServices,
      addHousekeepingServices,
      addServiceRequestPollingServices);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FrequencyCounter
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public void setInstrumentMode (final M instrumentMode)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_INSTRUMENT_MODE,
      InstrumentCommand.ICARG_INSTRUMENT_MODE, instrumentMode));
  }
  
  @Override
  public void setGateTime_s (final double gateTime_s)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_GATE_TIME,
      InstrumentCommand.ICARG_GATE_TIME_S, gateTime_s));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
