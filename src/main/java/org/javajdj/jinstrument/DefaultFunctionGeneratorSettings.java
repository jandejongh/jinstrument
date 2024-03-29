/* 
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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

import java.util.Arrays;
import java.util.Objects;
import org.javajdj.junits.Unit;
import java.util.logging.Logger;

/** Default implementation of {@link FunctionGeneratorSettings}.
 * 
 * <p>
 * The object is immutable.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class DefaultFunctionGeneratorSettings
  implements FunctionGeneratorSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultFunctionGeneratorSettings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultFunctionGeneratorSettings (
    final byte[] bytes,
    final boolean outputEnable,
    final FunctionGenerator.Waveform waveform,
    final double frequency_Hz,
    final double amplitude_Vpp,
    final double dcOffset_V)
  {
    this.bytes = bytes;
    this.outputEnable = outputEnable;
    this.waveform = waveform;
    this.frequency_Hz = frequency_Hz;
    this.amplitude_Vpp = amplitude_Vpp;
    this.dcOffset_V = dcOffset_V;
  }

  @Override
  public FunctionGeneratorSettings clone () throws CloneNotSupportedException
  {
    return (FunctionGeneratorSettings) super.clone ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentSettings
  // READING UNIT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final Unit getReadingUnit ()
  {
    return null;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentSettings
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
  // FunctionGeneratorSettings
  // OUTPUT ENABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean outputEnable;

  @Override
  public final boolean isOuputEnable ()
  {
    return this.outputEnable;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FunctionGeneratorSettings
  // WAVEFORM
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final FunctionGenerator.Waveform waveform;
  
  @Override
  public final FunctionGenerator.Waveform getWaveform ()
  {
    return this.waveform;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FunctionGeneratorSettings
  // FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double frequency_Hz;
  
  @Override
  public final double getFrequency_Hz ()
  {
    return this.frequency_Hz;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FunctionGeneratorSettings
  // AMPLITUDE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double amplitude_Vpp;
  
  @Override
  public final double getAmplitude_Vpp ()
  {
    return this.amplitude_Vpp;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FunctionGeneratorSettings
  // DC OFFSET
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double dcOffset_V;
  
  @Override
  public final double getDCOffset_V ()
  {
    return this.dcOffset_V;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    return this.bytes == null ? hashCode_NonBytes () : hashCode_Bytes ();
  }

  @Override
  public boolean equals (Object obj)
  {
    return this.bytes == null ? equals_NonBytes (obj) : equals_Bytes (obj);    
  }

  public int hashCode_Bytes ()
  {
    int hash = 7;
    hash = 53 * hash + Arrays.hashCode (this.bytes);
    return hash;
  }

  public boolean equals_Bytes (Object obj)
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
    final DefaultFunctionGeneratorSettings other = (DefaultFunctionGeneratorSettings) obj;
    if (! Arrays.equals (this.bytes, other.bytes))
    {
      return false;
    }
    return true;
  }

  public int hashCode_NonBytes ()
  {
    int hash = 5;
    hash = 37 * hash + (this.outputEnable ? 1 : 0);
    hash = 37 * hash + Objects.hashCode (this.waveform);
    hash = 37 * hash + (int) (Double.doubleToLongBits (this.frequency_Hz) ^ (Double.doubleToLongBits (this.frequency_Hz) >>> 32));
    hash = 37 * hash + (int) (Double.doubleToLongBits (this.amplitude_Vpp) ^ (Double.doubleToLongBits (this.amplitude_Vpp) >>> 32));
    hash = 37 * hash + (int) (Double.doubleToLongBits (this.dcOffset_V) ^ (Double.doubleToLongBits (this.dcOffset_V) >>> 32));
    return hash;
  }

  public boolean equals_NonBytes (Object obj)
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
    final DefaultFunctionGeneratorSettings other = (DefaultFunctionGeneratorSettings) obj;
    if (this.outputEnable != other.outputEnable)
    {
      return false;
    }
    if (Double.doubleToLongBits (this.frequency_Hz) != Double.doubleToLongBits (other.frequency_Hz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.amplitude_Vpp) != Double.doubleToLongBits (other.amplitude_Vpp))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.dcOffset_V) != Double.doubleToLongBits (other.dcOffset_V))
    {
      return false;
    }
    if (this.waveform != other.waveform)
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
