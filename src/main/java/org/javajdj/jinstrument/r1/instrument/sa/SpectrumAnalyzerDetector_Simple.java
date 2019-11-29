package org.javajdj.jinstrument.r1.instrument.sa;

/**
 *
 */
public enum SpectrumAnalyzerDetector_Simple
implements SpectrumAnalyzerDetector
{
  
  UNKNOWN   ("UKN", "UNKN"),
  GROUND    ("GND", "GRND"),
  SAMPLE    ("SMP", "SMPL"),
  PEAK_MAX  ("MAX", "PMAX"),
  PEAK_MIN  ("MIN", "PMIN"),
  ROSENFELL ("R&F", "ROFE");
  
  SpectrumAnalyzerDetector_Simple (final String threeCharString, final String fourCharString)
  {
    if (threeCharString == null || threeCharString.length () != 3)
      throw new IllegalArgumentException ();
    if (fourCharString == null || fourCharString.length () != 4)
      throw new IllegalArgumentException ();
    this.threeCharString = threeCharString;
    this.fourCharString = fourCharString;
  }
  
  final String threeCharString;
  
  @Override
  public final String getThreeCharString ()
  {
    return this.threeCharString;
  }
  
  final String fourCharString;
  
  @Override
  public final String getFourCharString ()
  {
    return this.fourCharString;
  }
  
  @Override
  public final SpectrumAnalyzerDetector_Simple fromString (final String detectorString)
  {
    if (detectorString == null || detectorString.trim ().isEmpty ())
      return null;
    final String trimmedLowerCaseString = detectorString.trim ().toLowerCase ();
    for (SpectrumAnalyzerDetector_Simple d : SpectrumAnalyzerDetector_Simple.values ())
      if (d.getThreeCharString ().equalsIgnoreCase (trimmedLowerCaseString)
       || d.getFourCharString ().equalsIgnoreCase (trimmedLowerCaseString))
        return d;
    return null;
  }
  
}
