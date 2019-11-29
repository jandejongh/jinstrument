package org.javajdj.jinstrument.r1.instrument.dso;

import java.util.logging.Logger;

/**
 *
 */
public class DefaultOscilloscopeTraceSettings
implements OscilloscopeTraceSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultOscilloscopeTraceSettings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DefaultOscilloscopeTraceSettings
  (
//   final double centerFrequency_MHz,
//   final double span_MHz,
//   final double resolutionBandwidth_Hz,
//   final boolean autoResolutionBandwidth,
//   final double videoBandwidth_Hz,
//   final boolean autoVideoBandwidth,
//   final double sweepTime_s,
//   final boolean autoSweepTime,
     final int traceLength
//   final double referenceLevel_dBm,
//   final double attenuation_dB,
//   final SpectrumAnalyzerDetector detector
  )
  {
//    if (detector == null)
//      throw new IllegalArgumentException ();
//    // XXX Argument checking!!
//    this.centerFrequency_MHz = centerFrequency_MHz;
//    this.span_MHz = span_MHz;
//    this.resolutionBandwidth_Hz = resolutionBandwidth_Hz;
//    this.autoResolutionBandwidth = autoResolutionBandwidth;
//    this.videoBandwidth_Hz = videoBandwidth_Hz;
//    this.autoVideoBandwidth = autoVideoBandwidth;
      this.traceLength = traceLength;
//    this.sweepTime_s = sweepTime_s;
//    this.autoSweepTime = autoSweepTime;
//    this.referenceLevel_dBm = referenceLevel_dBm;
//    this.attenuation_dB = attenuation_dB;
//    this.detector = detector;
  }

  @Override
  public DefaultOscilloscopeTraceSettings clone () throws CloneNotSupportedException
  {
    return (DefaultOscilloscopeTraceSettings) super.clone ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String toString ()
  {
//    return "[fc_MHz=" + this.centerFrequency_MHz + ", sp_MHz=" + this.span_MHz + "]";
    return "[FIXME]";
  }
  
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // CENTER FREQUENCY
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  
//  private final double centerFrequency_MHz;
//  
//  @Override
//  public final double getCenterFrequency_MHz ()
//  {
//    return this.centerFrequency_MHz;
//  }
//
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // SPAN
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  
//  private final double span_MHz;
//  
//  @Override
//  public final double getSpan_MHz ()
//  {
//    return this.span_MHz;
//  }
//
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // RESOLUTION BANDWIDTH
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  
//  private final double resolutionBandwidth_Hz;
//
//  @Override
//  public final double getResolutionBandwidth_Hz ()
//  {
//    return this.resolutionBandwidth_Hz;
//  }
//  
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // AUTO RESOLUTION BANDWIDTH
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  
//  private final boolean autoResolutionBandwidth;
//
//  @Override
//  public final boolean isAutoResolutionBandwidth ()
//  {
//    return this.autoResolutionBandwidth;
//  }
//
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // VIDEO BANDWIDTH
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  
//  private final double videoBandwidth_Hz;
//
//  @Override
//  public final double getVideoBandwidth_Hz ()
//  {
//    return this.videoBandwidth_Hz;
//  }
//  
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // AUTO VIDEO BANDWIDTH
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  
//  private final boolean autoVideoBandwidth;
//
//  @Override
//  public final boolean isAutoVideoBandwidth ()
//  {
//    return this.autoResolutionBandwidth;
//  }
//
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // SWEEP TIME
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  
//  private final double sweepTime_s;
//
//  @Override
//  public final double getSweepTime_s ()
//  {
//    return this.sweepTime_s;
//  }
//  
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // AUTO SWEEP TIME
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  
//  private final boolean autoSweepTime;
//
//  @Override
//  public final boolean isAutoSweepTime ()
//  {
//    return this.autoSweepTime;
//  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE LENGTH
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final int traceLength;

  @Override
  public final int getTraceLength ()
  {
    return this.traceLength;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // REFERENCE LEVEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
//  private final double referenceLevel_dBm;
//
//  @Override
//  public final double getReferenceLevel_dBm ()
//  {
//    return this.referenceLevel_dBm;
//  }
//
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // ATTENUATION
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  
//  private final double attenuation_dB;
//
//  @Override
//  public final double getAttenuation_dB ()
//  {
//    return this.attenuation_dB;
//  }
//
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // DETECTOR
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  
//  private final SpectrumAnalyzerDetector detector;
//
//  @Override
//  public final SpectrumAnalyzerDetector getDetector ()
//  {
//    return this.detector;
//  }
//  
//  @Override
//  public double sampleIndexToFrequency_MHz (final double sampleIndex)
//  {
//    // This formula has been verified for/with an operational hp70000 system (pa3gyf),
//    // using low values for the trace length.
//    // The first sample (index 0) is always taken at the center frequency minus half the span.
//    // The step size is always the span divided by the trace length.
//    // The two statements above are true, irrespective of whether the trace length is odd or even.
//    // Note that this implies that we never "reach" the upper frequency, viz., the center frequency plus half the span.
//    // I'm not sure if this is true "in general" for spectrum analyzers, but I highly doubt it.
//    final double fStart_MHz = getCenterFrequency_MHz () - 0.5 * getSpan_MHz ();
//    final double fStep_MHz = getSpan_MHz () / getTraceLength ();
//    return fStart_MHz + sampleIndex * fStep_MHz;
//  }
//
//  @Override
//  public double frequency_MHzToSampleIndex (final double frequency_MHz)
//  {
//    final double fStart_MHz = getCenterFrequency_MHz () - 0.5 * getSpan_MHz ();
//    final double fStep_MHz = getSpan_MHz () / getTraceLength ();
//    return (frequency_MHz - fStart_MHz) / fStep_MHz;
//  }
//
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  //
//  // HASH CODE / EQUALS [AUTOGENERATED]
//  //
//  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  @Override
//  public int hashCode ()
//  {
//    int hash = 7;
//    hash = 43 * hash + (int) (Double.doubleToLongBits (this.centerFrequency_MHz) ^ (Double.doubleToLongBits (this.centerFrequency_MHz) >>> 32));
//    hash = 43 * hash + (int) (Double.doubleToLongBits (this.span_MHz) ^ (Double.doubleToLongBits (this.span_MHz) >>> 32));
//    hash = 43 * hash + (int) (Double.doubleToLongBits (this.resolutionBandwidth_Hz) ^ (Double.doubleToLongBits (this.resolutionBandwidth_Hz) >>> 32));
//    hash = 43 * hash + (this.autoResolutionBandwidth ? 1 : 0);
//    hash = 43 * hash + (int) (Double.doubleToLongBits (this.videoBandwidth_Hz) ^ (Double.doubleToLongBits (this.videoBandwidth_Hz) >>> 32));
//    hash = 43 * hash + (this.autoVideoBandwidth ? 1 : 0);
//    hash = 43 * hash + (int) (Double.doubleToLongBits (this.sweepTime_s) ^ (Double.doubleToLongBits (this.sweepTime_s) >>> 32));
//    hash = 43 * hash + (this.autoSweepTime ? 1 : 0);
//    hash = 43 * hash + this.traceLength;
//    hash = 43 * hash + (int) (Double.doubleToLongBits (this.referenceLevel_dBm) ^ (Double.doubleToLongBits (this.referenceLevel_dBm) >>> 32));
//    hash = 43 * hash + (int) (Double.doubleToLongBits (this.attenuation_dB) ^ (Double.doubleToLongBits (this.attenuation_dB) >>> 32));
//    hash = 43 * hash + Objects.hashCode (this.detector);
//    return hash;
//  }
//
//  @Override
//  public boolean equals (Object obj)
//  {
//    if (this == obj)
//    {
//      return true;
//    }
//    if (obj == null)
//    {
//      return false;
//    }
//    if (getClass () != obj.getClass ())
//    {
//      return false;
//    }
//    final DefaultOscilloscopeTraceSettings other = (DefaultOscilloscopeTraceSettings) obj;
//    if (Double.doubleToLongBits (this.centerFrequency_MHz) != Double.doubleToLongBits (other.centerFrequency_MHz))
//    {
//      return false;
//    }
//    if (Double.doubleToLongBits (this.span_MHz) != Double.doubleToLongBits (other.span_MHz))
//    {
//      return false;
//    }
//    if (Double.doubleToLongBits (this.resolutionBandwidth_Hz) != Double.doubleToLongBits (other.resolutionBandwidth_Hz))
//    {
//      return false;
//    }
//    if (this.autoResolutionBandwidth != other.autoResolutionBandwidth)
//    {
//      return false;
//    }
//    if (Double.doubleToLongBits (this.videoBandwidth_Hz) != Double.doubleToLongBits (other.videoBandwidth_Hz))
//    {
//      return false;
//    }
//    if (this.autoVideoBandwidth != other.autoVideoBandwidth)
//    {
//      return false;
//    }
//    if (Double.doubleToLongBits (this.sweepTime_s) != Double.doubleToLongBits (other.sweepTime_s))
//    {
//      return false;
//    }
//    if (this.autoSweepTime != other.autoSweepTime)
//    {
//      return false;
//    }
//    if (this.traceLength != other.traceLength)
//    {
//      return false;
//    }
//    if (Double.doubleToLongBits (this.referenceLevel_dBm) != Double.doubleToLongBits (other.referenceLevel_dBm))
//    {
//      return false;
//    }
//    if (Double.doubleToLongBits (this.attenuation_dB) != Double.doubleToLongBits (other.attenuation_dB))
//    {
//      return false;
//    }
//    if (!Objects.equals (this.detector, other.detector))
//    {
//      return false;
//    }
//    return true;
//  }
  
}
