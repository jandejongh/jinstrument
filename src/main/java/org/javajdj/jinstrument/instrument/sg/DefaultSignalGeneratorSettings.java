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

import java.util.Arrays;
import java.util.logging.Logger;

/** Default implementation of {@link SignalGeneratorSettings}.
 * 
 * <p>
 * The object is immutable.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class DefaultSignalGeneratorSettings
  implements SignalGeneratorSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultSignalGeneratorSettings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultSignalGeneratorSettings (
    final byte[] bytes,
    final double centerFrequency_MHz,
    final double span_MHz,
    final double startFrequency_MHz,
    final double stopFrequency_MHz,
    final double S_dBm,
    final boolean outputEnable,
    final double modulationSourceInternalFrequency_kHz,
    final boolean amEnable,
    final double amDepth_percent,
    final boolean fmEnable,
    final double fmDeviation_kHz)
  {
    this.bytes = bytes;
    this.centerFrequency_MHz = centerFrequency_MHz;
    this.span_MHz = span_MHz;
    this.startFrequency_MHz = startFrequency_MHz;
    this.stopFrequency_MHz = stopFrequency_MHz;
    this.S_dBm = S_dBm;
    this.outputEnable = outputEnable;
    this.modulationSourceInternalFrequency_kHz = modulationSourceInternalFrequency_kHz;
    this.amEnable = amEnable;
    this.amDepth_percent = amDepth_percent;
    this.fmEnable = fmEnable;
    this.fmDeviation_kHz = fmDeviation_kHz;
  }

  @Override
  public SignalGeneratorSettings clone () throws CloneNotSupportedException
  {
    return (SignalGeneratorSettings) super.clone ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // BYTES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final byte[] bytes;
  
  @Override
  public final byte[] getBytes ()
  {
    return this.bytes;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // CENTER FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double centerFrequency_MHz;
  
  @Override
  public final double getCenterFrequency_MHz ()
  {
    return this.centerFrequency_MHz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // SPAN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double span_MHz;
  
  @Override
  public final double getSpan_MHz ()
  {
    return this.span_MHz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // START FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double startFrequency_MHz;
  
  @Override
  public final double getStartFrequency_MHz ()
  {
    return this.startFrequency_MHz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // STOP FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double stopFrequency_MHz;
  
  @Override
  public final double getStopFrequency_MHz ()
  {
    return this.stopFrequency_MHz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // SIGNAL POWER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double S_dBm;
  
  @Override
  public final double getS_dBm ()
  {
    return this.S_dBm;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // OUTPUT ENABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean outputEnable;
  
  @Override
  public final boolean getOutputEnable ()
  {
    return this.outputEnable;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // AM ENABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean amEnable;
  
  @Override
  public final boolean getAmEnable ()
  {
    return this.amEnable;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // AM DEPTH
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double amDepth_percent;

  @Override
  public final double getAmDepth_percent ()
  {
    return this.amDepth_percent;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // FM ENABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean fmEnable;
  
  @Override
  public final boolean getFmEnable ()
  {
    return this.fmEnable;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // FM DEVIATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double fmDeviation_kHz;

  @Override
  public final double getFmDeviation_kHz ()
  {
    return this.fmDeviation_kHz;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SignalGeneratorSettings
  // INTERNAL MODULATION SOURCE FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double modulationSourceInternalFrequency_kHz;
  
  @Override
  public double getModulationSourceInternalFrequency_kHz ()
  {
    return modulationSourceInternalFrequency_kHz;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 53 * hash + Arrays.hashCode (this.bytes);
    return hash;
  }

  @Override
  public boolean equals (Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass () != obj.getClass ())
    {
      return false;
    }
    final DefaultSignalGeneratorSettings other = (DefaultSignalGeneratorSettings) obj;
    if (!Arrays.equals (this.bytes, other.bytes))
    {
      return false;
    }
    return true;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
