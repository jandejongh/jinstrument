package org.javajdj.jinstrument.r1.instrument.sa;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.r1.Device;
import org.javajdj.jinstrument.r1.instrument.AbstractInstrument;

/**
 *
 */
public abstract class AbstractSpectrumAnalyzer
extends AbstractInstrument
implements SignalGenerator
{

  private static final Logger LOG = Logger.getLogger (AbstractSpectrumAnalyzer.class.getName ());

  protected AbstractSpectrumAnalyzer (final Device device)
  {
    super (device);
  }

  private Set<SpectrumAnalyzerClient> clients = new LinkedHashSet<>  ();
  
  public final synchronized void registerClient (final SpectrumAnalyzerClient client)
  {
    if (client == null)
      throw new IllegalArgumentException ();
    if (! this.clients.contains (client))
      this.clients.add (client);
  }
  
  public final synchronized void unregisterClient (final SpectrumAnalyzerClient client)
  {
    if (client == null)
      throw new IllegalArgumentException ();
    this.clients.remove (client);
  }
  
  private volatile SpectrumAnalyzerTraceSettings settings = null;
  
  public final synchronized SpectrumAnalyzerTraceSettings getCurrentTraceSettings ()
  {
    return this.settings;
  }
  
  protected final synchronized void setCurrentTraceSettings (final SpectrumAnalyzerTraceSettings settings)
  {
    LOG.log (Level.FINE, "Setting settings to {0}.", settings);
    this.settings = settings;
  }
  
}
