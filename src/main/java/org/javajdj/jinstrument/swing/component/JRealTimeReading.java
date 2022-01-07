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
package org.javajdj.jinstrument.swing.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.util.DynamicTimeSeriesChart;

/** A Swing (base) component showing in real-time the readings (if one-dimensional) from an {@link Instrument}.
 * 
 *
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class JRealTimeReading
  extends JInstrumentPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRealTimeReading.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRealTimeReading (
    final Instrument instrument,
    final String title,
    final int level,
    final Color panelColor,
    final LinkedHashSet<Enum<?>> readingTypes)
  {
    
    super (instrument, title, level, panelColor);
    
    this.readingTypes = readingTypes;
    if (this.readingTypes == null)
      this.chartMap.put (null, new DynamicTimeSeriesChart (title));
    else for (final Enum<?> readingType : this.readingTypes)
      this.chartMap.put (readingType, new DynamicTimeSeriesChart (readingType.toString ()));
    
    setLayout (new BorderLayout ());
    
    this.chartPanel = new JPanel ();
    add (this.chartPanel, BorderLayout.CENTER);
    
    this.chartPanel.setLayout (new GridLayout (2, 2));
    
    for (final DynamicTimeSeriesChart chart : this.chartMap.values ())
      this.chartPanel.add (chart);
    
    getInstrument ().addInstrumentListener (this.instrumentListener);

  }

  public JRealTimeReading (
    final String title,
    final int level,
    final Color panelColor,
    final LinkedHashSet<Enum<?>> readingTypes)
  {
    this (null, title, level, panelColor, readingTypes);
  }
  
  public JRealTimeReading (
    final Instrument instrument,
    final int level,
    final LinkedHashSet<Enum<?>> readingTypes)
  {
    this (instrument, null, level, null, readingTypes);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READING TYPES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final LinkedHashSet<Enum<?>> readingTypes;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JPanel chartPanel;
  
  private final LinkedHashMap<Enum<?>, DynamicTimeSeriesChart> chartMap = new LinkedHashMap<> ();
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CLEAR READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final void clearReading ()
  {
    // XXX Clearing the charts is not (yet) supported.
    // for (final DynamicTimeSeriesChart chart : this.chartMap.values ())
    //   chart.clear ();
    // XXX Therefore, the hard way...
    if (this.readingTypes == null)
      this.chartMap.put (null, new DynamicTimeSeriesChart (getTitle ()));
    else for (final Enum<?> readingType : this.readingTypes)
      this.chartMap.put (readingType, new DynamicTimeSeriesChart (readingType.toString ()));
    this.chartPanel.removeAll ();
    for (final DynamicTimeSeriesChart chart : this.chartMap.values ())
      this.chartPanel.add (chart);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final InstrumentListener instrumentListener = new InstrumentListener ()
  {
    
    @Override
    public void newInstrumentStatus (final Instrument instrument, final InstrumentStatus instrumentStatus)
    {
      // EMPTY
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      // EMPTY
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      if (instrument != JRealTimeReading.this.getInstrument () || instrumentReading == null)
        throw new IllegalArgumentException ();
      final Enum<?> readingType = instrumentReading.getReadingType ();
      if (JRealTimeReading.this.readingTypes != null && ! JRealTimeReading.this.readingTypes.contains (readingType))
        return;
      final Object readingValue = instrumentReading.getReadingValue ();
      if (readingValue == null || ! (readingValue instanceof Double))
        return;
      final double reading = (double) readingValue;
      final DynamicTimeSeriesChart chart = (JRealTimeReading.this.readingTypes == null
        ? JRealTimeReading.this.chartMap.get (null)
        : JRealTimeReading.this.chartMap.get (readingType));
      SwingUtilities.invokeLater (() ->
      {
        setInhibitInstrumentControl ();
        //
        try
        {
          // XXX Why the cast to float??
          chart.update ((float) reading);
        }
        finally
        {
          resetInhibitInstrumentControl ();
        }
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}