package org.javajdj.jinstrument.r1.instrument.sa.math;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import org.javajdj.jinstrument.r1.instrument.sa.SpectrumAnalyzerTrace;

/**
 *
 */
public class SpectrumAnalyzerTraceMath
{
  
  public static double getHighestPeakIndex (final SpectrumAnalyzerTrace trace)
  {
    final double[] highestPeaks = getHighestPeaksIndices (trace, 1);
    if (highestPeaks != null)
      return highestPeaks[0];
    else
      // XXX Are frequencies always positive???
      return -1;
  }
  
  public static double[] getHighestPeaksIndices (final SpectrumAnalyzerTrace trace, final int maxNumberOfPeaks)
  {
    if (trace == null || maxNumberOfPeaks < 1)
      return null;
    final double[] samples = trace.getSamples ();
    if (samples == null || samples.length == 0)
      return null;
    if (samples.length == 1)
      return new double[] { 0 };
    // We have an array of size at least two...
    // Obtain all peaks regions.
    // A peak is defined as a point at which the value is not strictly smaller
    // than that of its neighbor(s).
    // A peak region is a (maximal) set points with consecutive indices for this the
    // value is constant (and locally maximal).
    final NavigableMap<Double, NavigableMap<Integer, Integer>> peaksMapToIndices = new TreeMap<> ();
    for (int s = 0; s < samples.length; s++)
      if ((s == 0 || samples[s] >= samples[s-1]) && (s == samples.length - 1 || samples[s] >= samples[s+1]))
      {
        if (! peaksMapToIndices.containsKey (-samples[s]))
        {
          peaksMapToIndices.put (-samples[s], new TreeMap<> ());
          peaksMapToIndices.get (-samples[s]).put (s, s);
        }
        else if (peaksMapToIndices.get (-samples[s]).lastEntry ().getValue () == s - 1)
          peaksMapToIndices.get (-samples[s]).put (peaksMapToIndices.get (-samples[s]).lastKey (), s);
        else
          peaksMapToIndices.get (-samples[s]).put (s, s);
      }
    final Set<Double> peakRegions = new LinkedHashSet<>  ();
    for (final NavigableMap<Integer, Integer> regions : peaksMapToIndices.values ())
      for (final Map.Entry<Integer, Integer> region : regions.entrySet ())
        peakRegions.add (0.5 * (region.getKey () + region.getValue ()));
    final double[] peakRegionsArray = new double[Math.min (peakRegions.size (), maxNumberOfPeaks)];
    // System.err.println ("peaksMapToIndices: " + peaksMapToIndices + ".");
    int index = 0;
    for (final double peak : peakRegions)
    {
      peakRegionsArray[index++] = peak;
      if (index == maxNumberOfPeaks)
        break;
    }
    // System.err.println ("peaksRegionsArray: " + Arrays.toString (peakRegionsArray) + ".");
    return peakRegionsArray;
  }
  
}
