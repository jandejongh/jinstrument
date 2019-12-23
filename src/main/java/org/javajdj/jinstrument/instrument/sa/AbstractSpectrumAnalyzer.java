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
package org.javajdj.jinstrument.instrument.sa;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.javajdj.jinstrument.Device;
import org.javajdj.jinstrument.AbstractInstrument;
import org.javajdj.jinstrument.DefaultInstrumentCommand;
import org.javajdj.jinstrument.InstrumentCommand;
import org.javajdj.jservice.Service;

/** Abstract base implementation of a {@link SpectrumAnalyzer}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public abstract class AbstractSpectrumAnalyzer
extends AbstractInstrument
implements SpectrumAnalyzer
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (AbstractSpectrumAnalyzer.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected AbstractSpectrumAnalyzer (final String name,
    final Device device,
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
  // SpectrumAnalyzer
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public void setCenterFrequency_MHz (final double centerFrequency_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_FREQUENCY,
      InstrumentCommand.ICARG_RF_FREQUENCY_MHZ,
      centerFrequency_MHz));
  }

  @Override
  public void setSpan_MHz (final double span_MHz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_SPAN,
      InstrumentCommand.ICARG_RF_SPAN_MHZ,
      span_MHz));
  }

  @Override
  public void setResolutionBandwidth_Hz (final double resolutionBandwidth_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RESOLUTION_BANDWIDTH,
      InstrumentCommand.ICARG_RESOLUTION_BANDWIDTH_HZ,
      resolutionBandwidth_Hz));
  }

  @Override
  public void setResolutionBandwidthCoupled ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SET_RESOLUTION_BANDWIDTH_COUPLED));
  }
  
  @Override
  public void setVideoBandwidth_Hz (final double videoBandwidth_Hz)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_VIDEO_BANDWIDTH,
      InstrumentCommand.ICARG_VIDEO_BANDWIDTH_HZ,
      videoBandwidth_Hz));
  }

  @Override
  public void setVideoBandwidthCoupled ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SET_VIDEO_BANDWIDTH_COUPLED));
  }
  
  @Override
  public void setSweepTime_s (final double sweepTime_s)
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SWEEP_TIME,
      InstrumentCommand.ICARG_SWEEP_TIME_S,
      sweepTime_s));
  }
  
  @Override
  public void setSweepTimeCoupled ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SET_SWEEP_TIME_COUPLED));
  }
  
  @Override
  public void setReferenceLevel_dBm (final double referenceLevel_dBm)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_REFERENCE_LEVEL,
      InstrumentCommand.ICARG_REFERENCE_LEVEL_DBM,
      referenceLevel_dBm));
  }

  @Override
  public void setRfAttenuation_dB (final double rfAttenuation_dB)
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_RF_ATTENUATION,
      InstrumentCommand.ICARG_RF_ATTENUATION_DB,
      rfAttenuation_dB));
  }
  
  @Override
  public void setRfAttenuationCoupled ()
    throws IOException, InterruptedException
  {
    addCommand (new DefaultInstrumentCommand (
      InstrumentCommand.IC_SET_RF_ATTENUATION_COUPLED));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
