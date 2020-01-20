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
package org.javajdj.jinstrument.gpib.fg;

import org.javajdj.jinstrument.FunctionGenerator;
import java.util.List;
import org.javajdj.jinstrument.gpib.AbstractGpibInstrument;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jservice.Service;

/** Abstract base implementation of {@link FunctionGenerator} for GPIB instruments.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public abstract class AbstractGpibFunctionGenerator
  extends AbstractGpibInstrument
  implements FunctionGenerator
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected AbstractGpibFunctionGenerator (
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
