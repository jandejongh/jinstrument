package org.javajdj.jinstrument.r1.instrument.dso;

/** Default implementation of a {@link OscilloscopeTrace}.
 *
 */
public class DefaultOscilloscopeTrace
implements OscilloscopeTrace
{

  public DefaultOscilloscopeTrace (final OscilloscopeTraceSettings settings, final double[] samples)
  {
    if (settings == null || samples == null)
      throw new IllegalArgumentException ();
    this.settings = settings;
    this.samples = samples;
//    this.error = error;
//    this.errorMessage = errorMessage;
//    this.uncalibrated = uncalibrated;
//    this.uncorrected = uncorrected;
    this.error = false;
    this.errorMessage = "";
    this.uncalibrated = false;
    this.uncorrected = false;
  }
  
  private final OscilloscopeTraceSettings settings;

  @Override
  public final OscilloscopeTraceSettings getSettings ()
  {
    return this.settings;
  }

  private final double [] samples;
  
  @Override
  public final double[] getSamples ()
  {
    return this.samples;
  }
  
  private final boolean error;
  
  @Override
  public final boolean isError ()
  {
    return this.error;
  }
  
  private final String errorMessage;

  @Override
  public final String getErrorMessage ()
  {
    return this.errorMessage != null ? this.errorMessage : "";
  }
  
  private final boolean uncalibrated;
  
  @Override
  public final boolean isUncalibrated ()
  {
    return this.uncalibrated;
  }
  
  private final boolean uncorrected;
  
  @Override
  public final boolean isUncorrected ()
  {
    return this.uncorrected;
  }
  
}
