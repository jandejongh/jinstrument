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
package org.javajdj.jinstrument.gpib.psu;

import java.io.IOException;
import java.util.List;
import org.javajdj.jinstrument.DefaultInstrumentCommand;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jinstrument.PowerSupplyUnit;
import org.javajdj.jinstrument.gpib.AbstractGpibInstrument;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jservice.Service;

/** Abstract base implementation of {@link PowerSupplyUnit} for GPIB instruments.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public abstract class AbstractGpibPowerSupplyUnit
  extends AbstractGpibInstrument
  implements PowerSupplyUnit
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected AbstractGpibPowerSupplyUnit (
    final String name,
    final GpibDevice device,
    final List<Runnable> runnables,
    final List<Service> targetServices,
    final boolean addStatusServices,
    final boolean addSettingsServices,
    final boolean addCommandProcessorServices,
    final boolean addAcquisitionServices,
    final boolean addServiceRequestPollingServices)

  {
    super (
      name,
      device,
      runnables,
      targetServices,
      addStatusServices,
      addSettingsServices,
      addCommandProcessorServices,
      addAcquisitionServices,
      addServiceRequestPollingServices);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PowerSupplyUnit
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public void setSoftLimitVoltage_V (final double softLimitVoltage_V)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SOFT_LIMIT_VOLTAGE,
      InstrumentCommand.ICARG_SOFT_LIMIT_VOLTAGE_V, softLimitVoltage_V));
  }

  @Override
  public void setSoftLimitCurrent_A (final double softLimitCurrent_A)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SOFT_LIMIT_CURRENT,
      InstrumentCommand.ICARG_SOFT_LIMIT_CURRENT_A, softLimitCurrent_A));
  }

  @Override
  public void setSoftLimitPower_W (final double softLimitPower_W)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SOFT_LIMIT_POWER,
      InstrumentCommand.ICARG_SOFT_LIMIT_POWER_W, softLimitPower_W));
  }  
  
  @Override
  public void setSetVoltage_V (final double setVoltage_V)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SET_VOLTAGE,
      InstrumentCommand.ICARG_SET_VOLTAGE_V, setVoltage_V));
  }

  @Override
  public void setSetCurrent_A (final double setCurrent_A)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SET_CURRENT,
      InstrumentCommand.ICARG_SET_CURRENT_A, setCurrent_A));
  }

  @Override
  public void setSetPower_W (final double setPower_W)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SET_POWER,
      InstrumentCommand.ICARG_SET_POWER_W, setPower_W));
  }
  
  @Override
  public void setOutputEnable (final boolean outputEnable)
    throws IOException, InterruptedException, UnsupportedOperationException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_OUTPUT_ENABLE,
      InstrumentCommand.ICARG_OUTPUT_ENABLE, outputEnable));
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
