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
package org.javajdj.jinstrument.util;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;

/** A {@code JPanel} showing dynamic XT data in a graph in real-time.
 * 
 * <p>
 * This class is experimental; its interface and implementation will change in the future.
 * 
 * <p>
 * Stolen and adapted from {@literal
 * https://stackoverflow.com/questions/21299096/jfreechart-how-to-show-real-time-on-the-x-axis-of-a-timeseries-chart}.
 * 
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class DynamicTimeSeriesChart extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DynamicTimeSeriesChart.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public DynamicTimeSeriesChart (final String title)
  {

    this.dataset = new DynamicTimeSeriesCollection (1, 2000, new Second ());
    this.dataset.setTimeBase (new Second (0, 0, 0, 1, 1, 1990)); // date 1st jan 0 mins 0 secs

    this.dataset.addSeries (new float[1], 0, title);
    
    this.chart = ChartFactory.createTimeSeriesChart (
      title,
      "Time",
      title,
      this.dataset,
      true,
      true,
      false);
    
    final XYPlot plot = this.chart.getXYPlot ();

    final ValueAxis axis = plot.getDomainAxis ();
    axis.setAutoRange (true);
    axis.setFixedAutoRange (200000); // proportional to scroll speed
    
    plot.getRangeAxis ().setAutoRange (true);

    final ChartPanel chartPanel = new ChartPanel (this.chart);
    
    setLayout (new BoxLayout (this, BoxLayout.PAGE_AXIS));
    add (chartPanel);
    
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING/JFREECHART
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final DynamicTimeSeriesCollection dataset;

  private final JFreeChart chart;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UPDATE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public void update (final float value)
  {
    final float[] newData = new float[1];
    newData[0] = value;
    this.dataset.advanceTime ();
    this.dataset.appendData (newData);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CLEAR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public void clear ()
  {
    // XXX
    throw new UnsupportedOperationException ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MAIN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Demo program for {@link DynamicTimeSeriesChart}.
   * 
   * <p>
   * Shows a real-time graph of randomly generated numbers versus time.
   * 
   * @param args Command-line argument(s); not used.
   * 
   */
  public static void main (String[] args)
  {
    final JFrame frame = new JFrame ("Testing DynamicTimeSeriesChart");
    frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    final DynamicTimeSeriesChart chart = new DynamicTimeSeriesChart ("random numbers");
    frame.add (chart);
    frame.pack ();
    frame.setVisible (true);
    final Timer timer = new Timer (100, (ActionEvent e) ->
    {
      EventQueue.invokeLater (() ->
      {
        chart.update ((float) (Math.random () * 10));
      });
    });
    timer.start ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
