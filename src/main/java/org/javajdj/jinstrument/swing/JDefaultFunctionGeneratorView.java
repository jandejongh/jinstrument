/*
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.FunctionGeneratorSettings;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A one-size-fits-all Swing panel for control and status of a generic {@link FunctionGenerator}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class JDefaultFunctionGeneratorView
  extends JFunctionGeneratorPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultFunctionGeneratorView.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultFunctionGeneratorView (final FunctionGenerator functionGenerator)
  {
    
    super (functionGenerator, null, 0, null);
    
    setOpaque (true);
    setMinimumSize (new Dimension (1024, 600));
    setPreferredSize (new Dimension (1024, 600));
    
    setLayout (new GridLayout (2, 2));
    
    final JInstrumentPanel managementPanel = new JDefaultInstrumentManagementView (getFunctionGenerator (), getLevel () + 1);
    managementPanel.setPanelBorder (JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR, "Management");
    add (managementPanel);
    
    final JPanel waveformPanel = new JPanel ();
    setPanelBorder (waveformPanel, 1, "Waveform");
    waveformPanel.setLayout (new FlowLayout (FlowLayout.CENTER));
    this.jWaveform = new JComboBox (getFunctionGenerator ().getSupportedWaveforms ().toArray ());
    this.jWaveform.setSelectedItem (null);
    this.jWaveform.addActionListener (this.jWaveformListener);
    waveformPanel.add (this.jWaveform);
    add (waveformPanel);
    
    final JPanel frequencyPanel = new JPanel ();
    frequencyPanel.setOpaque (true);
    frequencyPanel.setLayout (new GridLayout (5, 1));
    //
    this.jFreq = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 11, 7);
    this.jFreq.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newFrequency_Hz = getFrequencyFromDialog_Hz ("Enter Frequency [Hz]", null);
        if (newFrequency_Hz != null)
          try
          {
            JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setFrequency_Hz (newFrequency_Hz);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting frequency on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting frequency on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    frequencyPanel.add (this.jFreq);
    //
    this.jFreqSlider_MHz = new JSlider (0, (int) Math.ceil (getFunctionGenerator ().getMaxFrequency_Hz () / 1.0e6), 0);
    this.jFreqSlider_kHz = new JSlider (0, 999, 0);
    this.jFreqSlider_Hz = new JSlider (0, 999, 0);
    this.jFreqSlider_mHz = new JSlider (0, 999, 0);
    this.jFreqSlider_MHz.setToolTipText ("-");
    this.jFreqSlider_MHz.setMajorTickSpacing (10);
    this.jFreqSlider_MHz.setMinorTickSpacing (1);
    this.jFreqSlider_MHz.setPaintTicks (true);
    this.jFreqSlider_MHz.setPaintLabels (true);
    this.jFreqSlider_MHz.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ())
      {
        if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
          try
          {
            LOG.log (Level.INFO, "Setting frequency on instrument from slider to {0} MHz.", source.getValue ());
            JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setFrequency_Hz (
              + 1.0e6 * source.getValue ()
              + 1.0e3 * this.jFreqSlider_kHz.getValue ()
              + 1.0 * this.jFreqSlider_Hz.getValue ()
              + 1.0e-3 * this.jFreqSlider_mHz.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} MHz: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
      else
        source.setToolTipText (Integer.toString (source.getValue ()));
    });
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), this.jFreqSlider_MHz, "[MHz]");
    frequencyPanel.add (this.jFreqSlider_MHz);
    //
    this.jFreqSlider_kHz.setToolTipText ("-");
    this.jFreqSlider_kHz.setMajorTickSpacing (100);
    this.jFreqSlider_kHz.setMinorTickSpacing (10);
    this.jFreqSlider_kHz.setPaintTicks (true);
    this.jFreqSlider_kHz.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ()) {
        if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
          try
          {
            JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setFrequency_Hz (
                1.0e6 * this.jFreqSlider_MHz.getValue ()
              + 1.0e3 * source.getValue ()
              + 1.0 * this.jFreqSlider_Hz.getValue ()
              + 1.0e-3 * this.jFreqSlider_mHz.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} kHz: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
    });
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), this.jFreqSlider_kHz, "[kHz]");
    frequencyPanel.add (this.jFreqSlider_kHz);
    //
    this.jFreqSlider_Hz.setToolTipText ("-");
    this.jFreqSlider_Hz.setMajorTickSpacing (100);
    this.jFreqSlider_Hz.setMinorTickSpacing (10);
    this.jFreqSlider_Hz.setPaintTicks (true);
    this.jFreqSlider_Hz.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ()) {
        if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
          try
          {
            JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setFrequency_Hz (
                1.0e6 * this.jFreqSlider_MHz.getValue ()
              + 1.0e3 * this.jFreqSlider_kHz.getValue ()
              + 1.0 * source.getValue ()
              + 1.0e-3 * this.jFreqSlider_mHz.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} Hz: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
    });
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), this.jFreqSlider_Hz, "[Hz]");
    frequencyPanel.add (this.jFreqSlider_Hz);
    //
    this.jFreqSlider_mHz.setToolTipText ("-");
    this.jFreqSlider_mHz.setMajorTickSpacing (100);
    this.jFreqSlider_mHz.setMinorTickSpacing (10);
    this.jFreqSlider_mHz.setPaintTicks (true);
    this.jFreqSlider_mHz.addChangeListener ((final ChangeEvent ce) ->
    {
      JSlider source = (JSlider) ce.getSource ();
      if (! source.getValueIsAdjusting ()) {
        if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
          try
          {
            // LOG.log (Level.INFO, "Setting frequency on instrument from slider to {0} mHz.", source.getValue ());
            JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setFrequency_Hz (
                1.0e6 * this.jFreqSlider_MHz.getValue ()
              + 1.0e3 * this.jFreqSlider_kHz.getValue ()
              + 1.0 * this.jFreqSlider_Hz.getValue ()
              + 1.0e-3 * source.getValue ());
          }
          catch (IOException | InterruptedException e)
          {
            LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} mHz: {1}.",
              new Object[]{source.getValue (), e});          
          }
      }
    });
    setSubPanelBorder (JInstrumentPanel.getGuiPreferencesFrequencyColor (), this.jFreqSlider_mHz, "[mHz]");
    frequencyPanel.add (this.jFreqSlider_mHz);
    //
    JInstrumentPanel.setPanelBorder (frequencyPanel, getLevel () + 1, "Frequency [Hz]");
    add (frequencyPanel);
    
    add (new JLabel ());
    
    getFunctionGenerator ().addInstrumentListener (this.instrumentListener);
    
  }
  
  private void setSubPanelBorder (final Color color, final JComponent panel, final String title)
  {
    panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (color, 2, true), title));    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT VIEW TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static InstrumentViewType INSTRUMENT_VIEW_TYPE = new InstrumentViewType ()
  {
    
    @Override
    public final String getInstrumentViewTypeUrl ()
    {
      return "Default [LF] Function Generator View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof FunctionGenerator))
        return new JDefaultFunctionGeneratorView ((FunctionGenerator) instrument);
      else
        return null;
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Instrument
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getInstrumentViewUrl ()
  {
    return JDefaultFunctionGeneratorView.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
      + "<>"
      + getInstrument ().getInstrumentUrl ();
  }
  
  @Override
  public String toString ()
  {
    return getInstrumentViewUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSevenSegmentNumber jFreq;
  
  private final JSlider jFreqSlider_MHz;
  
  private final JSlider jFreqSlider_kHz;
  
  private final JSlider jFreqSlider_Hz;
  
  private final JSlider jFreqSlider_mHz;
  
  private final JComboBox<FunctionGenerator.Waveform> jWaveform;
  
  private final ActionListener jWaveformListener = (final ActionEvent ae) ->
  {
    final FunctionGenerator.Waveform waveform =
      (FunctionGenerator.Waveform) JDefaultFunctionGeneratorView.this.jWaveform.getSelectedItem ();
    try
    {
      JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setWaveform (waveform);
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while setting waveform {0} on instrument {1}: {2}.",        
        new Object[]
          {waveform, getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
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
      if (instrument != JDefaultFunctionGeneratorView.this.getFunctionGenerator () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof FunctionGeneratorSettings))
        throw new IllegalArgumentException ();
      final FunctionGeneratorSettings settings = (FunctionGeneratorSettings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JDefaultFunctionGeneratorView.this.inhibitInstrumentControl = true;
        //
        try
        {
          //
          final FunctionGenerator.Waveform waveform = settings.getWaveform ();
          final boolean isSupportedWaveform =
            JDefaultFunctionGeneratorView.this.getFunctionGenerator ().isSupportedWaveform (waveform);
          JDefaultFunctionGeneratorView.this.jWaveform.setSelectedItem (isSupportedWaveform ? waveform : null);
          //
          final double f_Hz = settings.getFrequency_Hz ();
          final long f_mHz_long = Math.round (f_Hz * 1e3);
          JDefaultFunctionGeneratorView.this.jFreq.setNumber (f_Hz);
          final int f_int_MHz = (int) (f_mHz_long / 1000000000L);
          JDefaultFunctionGeneratorView.this.jFreqSlider_MHz.setValue (f_int_MHz);
          JDefaultFunctionGeneratorView.this.jFreqSlider_MHz.setToolTipText (Integer.toString (f_int_MHz));
          final int f_rem_int_kHz = (int) ((f_mHz_long % 1000000000L) / 1000000L);
          JDefaultFunctionGeneratorView.this.jFreqSlider_kHz.setValue (f_rem_int_kHz);
          JDefaultFunctionGeneratorView.this.jFreqSlider_kHz.setToolTipText (Integer.toString (f_rem_int_kHz));
          final int f_rem_int_Hz = (int) ((f_mHz_long % 1000000L) / 1000L);
          JDefaultFunctionGeneratorView.this.jFreqSlider_Hz.setValue (f_rem_int_Hz);
          JDefaultFunctionGeneratorView.this.jFreqSlider_Hz.setToolTipText (Integer.toString (f_rem_int_Hz));
          final int f_rem_int_mHz = (int) (f_mHz_long % 1000L);
          JDefaultFunctionGeneratorView.this.jFreqSlider_mHz.setValue (f_rem_int_mHz);
          JDefaultFunctionGeneratorView.this.jFreqSlider_mHz.setToolTipText (Integer.toString (f_rem_int_mHz));
          //
        }
        finally
        {
          JDefaultFunctionGeneratorView.this.inhibitInstrumentControl = false;
        }
      });
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      LOG.log (Level.WARNING, "Unexpected InstrumentReading: {0}.", instrumentReading);
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
