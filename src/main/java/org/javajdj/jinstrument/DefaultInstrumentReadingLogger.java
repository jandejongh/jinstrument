/*
 * Copyright 2010-2021 Jan de Jongh <jfcmdejongh@gmail.com>.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jservice.AbstractService;

/** Default implementation of {@link InstrumentReadingLogger}.
 *
 * @param <R> The type of the actual reading.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultInstrumentReadingLogger<R>
  extends AbstractService
  implements InstrumentReadingLogger<R>
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultInstrumentReadingLogger.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public DefaultInstrumentReadingLogger (final String name, final Instrument instrument)
  {
    super (name);
    if (instrument == null)
      throw new IllegalArgumentException ();
    this.instrument = instrument;
    this.instrument.addInstrumentListener (this.instrumentListener);
  }
  
  public DefaultInstrumentReadingLogger (final Instrument instrument)
  {
    this ("DefaultInstrumentReadingLogger", instrument);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Instrument instrument;
  
  @Override
  public final Instrument getInstrument ()
  {
    return this.instrument;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Service
  // AbstractService
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public final synchronized void startService ()
  {
    this.instrument.addInstrumentListener (this.instrumentListener);
  }

  @Override
  public final synchronized void stopService ()
  {
    this.instrument.removeInstrumentListener (this.instrumentListener);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERIES
  // READINGS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final List<List<InstrumentReading<R>>> readings = new ArrayList<> ();
  
  private final Object readingsLock = new Object ();
  
  @Override
  public final void clear ()
  {
    synchronized (this.readingsLock)
    {
      this.readings.clear ();
    }
  }

  @Override
  public final void emptySeries (final int s)
  {
    synchronized (this.readingsLock)
    {
      if (s < 0 || s >= this.readings.size ())
        throw new IllegalArgumentException ();
      this.readings.get (s).clear ();
    }
  }
  
  @Override
  public final int getNumberOfSeries ()
  {
    synchronized (this.readingsLock)
    {
      return this.readings.size ();
    }    
  }
  
  @Override
  public final int getSeriesLength (final int s)
  {
    synchronized (this.readingsLock)
    {
      if (s < 0 || s >= this.readings.size ())
        throw new IllegalArgumentException ();
      return this.readings.get (s).size ();
    }    
  }

  @Override
  public final int nextSeries ()
  {
    synchronized (this.readingsLock)
    {
      this.readings.add (new ArrayList<> ());
      return this.readings.size () - 1;
    }    
  }

  @Override
  public final List<List<InstrumentReading<R>>> getReadings ()
  {
    synchronized (this.readingsLock)
    {
      final List<List<InstrumentReading<R>>> readings = new ArrayList<> ();
      for (int s = 0; s <= this.readings.size () - 1; s++)
      {
        final List<InstrumentReading<R>> series = this.readings.get (s);
        if (series == null)
          throw new RuntimeException ();
        if (s == this.readings.size () - 1)
          readings.add (Collections.unmodifiableList (new ArrayList<> (series)));
        else
          readings.add (Collections.unmodifiableList (series));
      }
      return Collections.unmodifiableList (readings);
    }    
  }

  @Override
  public final List<InstrumentReading<R>> getReadingsForSeries (final int s)
  {
    synchronized (this.readingsLock)
    {
      if (s < 0 || s >= this.readings.size ())
        throw new IllegalArgumentException ();
      final List<InstrumentReading<R>> series = this.readings.get (s);
      if (series == null)
        throw new RuntimeException ();
      if (s == this.readings.size () - 1)
        return Collections.unmodifiableList (new ArrayList<> (series));
      else
        return Collections.unmodifiableList (series);
    }    
  }

  @Override
  public final InstrumentReading<R> getReading (final int s, final int n)
  {
    synchronized (this.readingsLock)
    {
      if (s < 0 || s >= this.readings.size ())
        throw new IllegalArgumentException ();
      final List<InstrumentReading<R>> series = this.readings.get (s);
      if (series == null)
        throw new RuntimeException ();
      if (n < 0 || n >= series.size ())
        throw new IllegalArgumentException ();
      return series.get (n);
    }    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ADD READING [FROM INSTRUMENT LISTENER]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
  protected final void addReading (final InstrumentReading<R> reading)
  {
    if (getStatus () != Status.ACTIVE)
      return;
    if (reading == null)
    {
      LOG.log (Level.WARNING, "Null reading from instrument {0}; entering ERROR state on {1}!",
        new Object[]{getInstrument (), this});
      error ();
    }
    else
      synchronized (this.readingsLock)
      {
        if (this.readings.isEmpty ())
          nextSeries ();
        this.readings.get (this.readings.size () - 1).add (reading);
      }    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
  private final InstrumentListener instrumentListener = new InstrumentListener ()
  {
    
    @Override
    public final void newInstrumentStatus (final Instrument instrument, final InstrumentStatus instrumentStatus)
    {
      // EMPTY
    }
    
    @Override
    public final void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      // EMPTY
    }

    @Override
    public final void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      if (instrument != DefaultInstrumentReadingLogger.this.getInstrument () || instrumentReading == null)
        throw new IllegalArgumentException ();
      DefaultInstrumentReadingLogger.this.addReading (instrumentReading);
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
