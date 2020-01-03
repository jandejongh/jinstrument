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
package org.javajdj.jinstrument.gpib.fc;

import java.util.List;
import java.util.logging.Logger;
import org.javajdj.jinstrument.FrequencyCounter;
import org.javajdj.jinstrument.gpib.AbstractGpibInstrument;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jservice.Service;

/** Abstract base implementation of a {@link FrequencyCounter} for GPIB instruments.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public abstract class AbstractGpibFrequencyCounter
  extends AbstractGpibInstrument
  implements FrequencyCounter
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
    final boolean addStatusServices,
    final boolean addSettingsServices,
    final boolean addCommandProcessorServices,
    final boolean addAcquisitionServices)

  {
    super (name, device, runnables, targetServices,
      addStatusServices, addSettingsServices, addCommandProcessorServices, addAcquisitionServices);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FrequencyCounter
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}