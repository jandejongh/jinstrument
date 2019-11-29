package org.javajdj.jinstrument.r1.instrument.sa;

/** Default implementation of a {@link SpectrumAnalyzerTrace}.
 *
 */
public class DefaultSpectrumAnalyzerTrace
implements SpectrumAnalyzerTrace
{

  public DefaultSpectrumAnalyzerTrace (final SpectrumAnalyzerTraceSettings settings, final double[] samples,
    final boolean error, final String errorMessage, final boolean uncalibrated, final boolean uncorrected)
  {
    if (settings == null || samples == null)
      throw new IllegalArgumentException ();
    this.settings = settings;
    this.samples = samples;
    this.error = error;
    this.errorMessage = errorMessage;
    this.uncalibrated = uncalibrated;
    this.uncorrected = uncorrected;
  }
  
  private final SpectrumAnalyzerTraceSettings settings;

  @Override
  public final SpectrumAnalyzerTraceSettings getSettings ()
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
