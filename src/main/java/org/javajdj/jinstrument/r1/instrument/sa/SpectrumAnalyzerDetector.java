package org.javajdj.jinstrument.r1.instrument.sa;

/**
 *
 */
public interface SpectrumAnalyzerDetector
{
  String getThreeCharString ();
  
  String getFourCharString ();
  
  SpectrumAnalyzerDetector fromString (final String detectorString);
  
}
