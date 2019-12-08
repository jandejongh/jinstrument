package org.javajdj.jinstrument.r1.instrument.sg;

import java.util.logging.Logger;

/**
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

  private final byte[] bytes;
  
  @Override
  public final byte[] getBytes ()
  {
    return this.bytes;
  }
  
  private final double modulationSourceInternalFrequency_kHz;
  
  @Override
  public double getModulationSourceInternalFrequency_kHz ()
  {
    return modulationSourceInternalFrequency_kHz;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
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
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode ()
  {
    int hash = 3;
    hash = 31 * hash + (int) (Double.doubleToLongBits (this.modulationSourceInternalFrequency_kHz) ^ (Double.doubleToLongBits (this.modulationSourceInternalFrequency_kHz) >>> 32));
    hash = 31 * hash + (int) (Double.doubleToLongBits (this.centerFrequency_MHz) ^ (Double.doubleToLongBits (this.centerFrequency_MHz) >>> 32));
    hash = 31 * hash + (int) (Double.doubleToLongBits (this.span_MHz) ^ (Double.doubleToLongBits (this.span_MHz) >>> 32));
    hash = 31 * hash + (int) (Double.doubleToLongBits (this.startFrequency_MHz) ^ (Double.doubleToLongBits (this.startFrequency_MHz) >>> 32));
    hash = 31 * hash + (int) (Double.doubleToLongBits (this.stopFrequency_MHz) ^ (Double.doubleToLongBits (this.stopFrequency_MHz) >>> 32));
    hash = 31 * hash + (int) (Double.doubleToLongBits (this.S_dBm) ^ (Double.doubleToLongBits (this.S_dBm) >>> 32));
    hash = 31 * hash + (this.outputEnable ? 1 : 0);
    hash = 31 * hash + (this.amEnable ? 1 : 0);
    hash = 31 * hash + (int) (Double.doubleToLongBits (this.amDepth_percent) ^ (Double.doubleToLongBits (this.amDepth_percent) >>> 32));
    hash = 31 * hash + (this.fmEnable ? 1 : 0);
    hash = 31 * hash + (int) (Double.doubleToLongBits (this.fmDeviation_kHz) ^ (Double.doubleToLongBits (this.fmDeviation_kHz) >>> 32));
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
    if (Double.doubleToLongBits (this.modulationSourceInternalFrequency_kHz) != Double.doubleToLongBits (other.modulationSourceInternalFrequency_kHz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.centerFrequency_MHz) != Double.doubleToLongBits (other.centerFrequency_MHz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.span_MHz) != Double.doubleToLongBits (other.span_MHz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.startFrequency_MHz) != Double.doubleToLongBits (other.startFrequency_MHz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.stopFrequency_MHz) != Double.doubleToLongBits (other.stopFrequency_MHz))
    {
      return false;
    }
    if (Double.doubleToLongBits (this.S_dBm) != Double.doubleToLongBits (other.S_dBm))
    {
      return false;
    }
    if (this.outputEnable != other.outputEnable)
    {
      return false;
    }
    if (this.amEnable != other.amEnable)
    {
      return false;
    }
    if (Double.doubleToLongBits (this.amDepth_percent) != Double.doubleToLongBits (other.amDepth_percent))
    {
      return false;
    }
    if (this.fmEnable != other.fmEnable)
    {
      return false;
    }
    if (Double.doubleToLongBits (this.fmDeviation_kHz) != Double.doubleToLongBits (other.fmDeviation_kHz))
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
