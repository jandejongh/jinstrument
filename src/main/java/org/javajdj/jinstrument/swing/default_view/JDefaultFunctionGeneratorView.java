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
package org.javajdj.jinstrument.swing.default_view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.javajdj.jinstrument.FunctionGenerator;
import org.javajdj.jinstrument.FunctionGeneratorSettings;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.swing.base.JFunctionGeneratorPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import static org.javajdj.jinstrument.swing.base.JInstrumentPanel.setPanelBorder;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
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
  
  public JDefaultFunctionGeneratorView (final FunctionGenerator functionGenerator, final int level)
  {
    
    super (functionGenerator, level);
    
    setOpaque (true);
    setMinimumSize (new Dimension (1024, 600));
    setPreferredSize (new Dimension (1024, 600));
    
    setLayout (new GridLayout (3, 2));
    
    final JInstrumentPanel managementPanel = new JDefaultInstrumentManagementView (getFunctionGenerator (), getLevel () + 1);
    setPanelBorder (
      managementPanel, getLevel () + 1,
      getGuiPreferencesManagementColor (),
      "Management");
    add (managementPanel);
    
    this.jInstrumentSpecificPanel = new JPanel ();
    setPanelBorder (
      this.jInstrumentSpecificPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Instrument Specific");
    this.jInstrumentSpecificPanel.setLayout (new GridLayout (1,1));
    final JLabel jInstrumentSpecificLabel = new JLabel ("Not Supported in this View");
    jInstrumentSpecificLabel.setHorizontalAlignment (SwingConstants.CENTER);
    this.jInstrumentSpecificPanel.add (jInstrumentSpecificLabel);
    add (this.jInstrumentSpecificPanel);
    
    final JPanel waveformControlPanel = new JPanel ();
    setPanelBorder (waveformControlPanel,
      getLevel () + 1,
      getGuiPreferencesAmplitudeColor (),
      "Waveform Control");
    waveformControlPanel.setLayout (new GridLayout (3, 3));
    final JPanel jOutputEnablePanel = new JPanel ();
    setPanelBorder (jOutputEnablePanel,
      getLevel () + 2,
      getGuiPreferencesAmplitudeColor (),
      "Output Enable");
    this.jOutputEnable = new JColorCheckBox.JBoolean (Color.red);
    this.jOutputEnable.addActionListener (this.jOutputEnableListener);
    jOutputEnablePanel.add (this.jOutputEnable);
    if (functionGenerator.supportsOutputEnable ())
      waveformControlPanel.add (jOutputEnablePanel);
    final JPanel jWaveformPanel = new JPanel ();
    setPanelBorder (
      jWaveformPanel,
      getLevel () + 2,
      getGuiPreferencesAmplitudeColor (),
      "Waveform");
    jWaveformPanel.setLayout (new FlowLayout ());
    jWaveformPanel.setAlignmentY (Component.CENTER_ALIGNMENT);
    this.jWaveform = new JComboBox (getFunctionGenerator ().getSupportedWaveforms ().toArray ());
    this.jWaveform.setSelectedItem (null);
    this.jWaveform.addActionListener (this.jWaveformListener);
    jWaveformPanel.add (this.jWaveform);
    waveformControlPanel.add (jWaveformPanel);
    waveformControlPanel.add (new JLabel ());
    waveformControlPanel.add (new JLabel ());
    waveformControlPanel.add (new JLabel ());
    waveformControlPanel.add (new JLabel ());
    waveformControlPanel.add (new JLabel ());
    waveformControlPanel.add (new JLabel ());
    waveformControlPanel.add (new JLabel ());
    add (waveformControlPanel);
    
    final JPanel frequencyPanel = new JPanel ();
    setPanelBorder (
      frequencyPanel,
      getLevel () + 1,
      getGuiPreferencesFrequencyColor (),
      "Frequency [Hz]");
    frequencyPanel.setOpaque (true);
    frequencyPanel.setLayout (new GridLayout (5, 1));
    this.jFreq = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 11, 7);
    this.jFreq.addMouseListener (this.jFreqMouseListener);
    frequencyPanel.add (this.jFreq);
    this.jFreqSlider_MHz = new JSlider (0, (int) Math.ceil (getFunctionGenerator ().getMaxFrequency_Hz () / 1.0e6), 0);
    this.jFreqSlider_kHz = new JSlider (0, 999, 0);
    this.jFreqSlider_Hz = new JSlider (0, 999, 0);
    this.jFreqSlider_mHz = new JSlider (0, 999, 0);
    this.jFreqSlider_MHz.setToolTipText ("-");
    this.jFreqSlider_MHz.setMajorTickSpacing (10);
    this.jFreqSlider_MHz.setMinorTickSpacing (1);
    this.jFreqSlider_MHz.setPaintTicks (true);
    this.jFreqSlider_MHz.setPaintLabels (true);
    this.jFreqSlider_MHz.addChangeListener (this.jFreqSlider_MHzChangeListener);
    setPanelBorder (
      this.jFreqSlider_MHz,
      getLevel () + 2,
      getGuiPreferencesFrequencyColor (),
      "[MHz]");
    frequencyPanel.add (this.jFreqSlider_MHz);
    this.jFreqSlider_kHz.setToolTipText ("-");
    this.jFreqSlider_kHz.setMajorTickSpacing (100);
    this.jFreqSlider_kHz.setMinorTickSpacing (10);
    this.jFreqSlider_kHz.setPaintTicks (true);
    this.jFreqSlider_kHz.addChangeListener (this.jFreqSlider_kHzChangeListener);
    setPanelBorder (
      this.jFreqSlider_kHz,
      getLevel () + 2,
      getGuiPreferencesFrequencyColor (),
      "[kHz]");
    frequencyPanel.add (this.jFreqSlider_kHz);
    this.jFreqSlider_Hz.setToolTipText ("-");
    this.jFreqSlider_Hz.setMajorTickSpacing (100);
    this.jFreqSlider_Hz.setMinorTickSpacing (10);
    this.jFreqSlider_Hz.setPaintTicks (true);
    this.jFreqSlider_Hz.addChangeListener (this.jFreqSlider_HzChangeListener);
    setPanelBorder (
      this.jFreqSlider_Hz,
      getLevel () + 2,
      getGuiPreferencesFrequencyColor (),
      "[Hz]");
    frequencyPanel.add (this.jFreqSlider_Hz);
    this.jFreqSlider_mHz.setToolTipText ("-");
    this.jFreqSlider_mHz.setMajorTickSpacing (100);
    this.jFreqSlider_mHz.setMinorTickSpacing (10);
    this.jFreqSlider_mHz.setPaintTicks (true);
    this.jFreqSlider_mHz.addChangeListener (this.jFreqSlider_mHzChangeListener);
    setPanelBorder (
      this.jFreqSlider_mHz,
      getLevel () + 2,
      getGuiPreferencesFrequencyColor (),
      "[mHz]");
    frequencyPanel.add (this.jFreqSlider_mHz);
    add (frequencyPanel);
    
    final JPanel amplitudePanel = new JPanel ();
    setPanelBorder (
      amplitudePanel,
      getLevel () + 1,
      getGuiPreferencesAmplitudeColor (),
      "Amplitude [V] (pp)");
    amplitudePanel.setLayout (new GridLayout (3, 1));
    this.jAmp = new JSevenSegmentNumber (getGuiPreferencesAmplitudeColor (), false, 5, 1);
    this.jAmp.addMouseListener (this.jAmpMouseListener);
    amplitudePanel.add (this.jAmp);
    this.jAmpSlider_V = new JSlider (0, (int) Math.ceil (getFunctionGenerator ().getMaxAmplitude_Vpp ()), 0);
    setPanelBorder (
      this.jAmpSlider_V,
      getLevel () + 2,
      getGuiPreferencesAmplitudeColor (),
      "[V]");
    this.jAmpSlider_V.setToolTipText ("-");
    this.jAmpSlider_V.setMajorTickSpacing (1);
    this.jAmpSlider_V.setPaintTicks (true);
    this.jAmpSlider_V.setPaintLabels (true);
    this.jAmpSlider_V.addChangeListener (this.jAmpSlider_VChangeListener);
    amplitudePanel.add (this.jAmpSlider_V);
    this.jAmpSlider_mV = new JSlider (0, 999, 0);
    setPanelBorder (
      this.jAmpSlider_mV,
      getLevel () + 2,
      getGuiPreferencesAmplitudeColor (),
      "[mV]");
    this.jAmpSlider_mV.setToolTipText ("-");
    this.jAmpSlider_mV.setMajorTickSpacing (100);
    this.jAmpSlider_mV.setMinorTickSpacing (10);
    this.jAmpSlider_mV.setPaintTicks (true);
    this.jAmpSlider_mV.setPaintLabels (false);
    this.jAmpSlider_mV.addChangeListener (this.jAmpSlider_mVChangeListener);
    amplitudePanel.add (this.jAmpSlider_mV);
    add (amplitudePanel);

    final JPanel offsetPanel = new JPanel ();
    setPanelBorder (
      offsetPanel,
      getLevel () + 1,
      getGuiPreferencesDCColor (),
      "DC Offset [V]");
    offsetPanel.setLayout (new GridLayout (3, 1));
    this.jOffset = new JSevenSegmentNumber (getGuiPreferencesDCColor (), true, 5, 1); // XXX Arbitrary values!
    this.jOffset.addMouseListener (this.jOffsetMouseListener);
    offsetPanel.add (this.jOffset);
    this.jOffsetSlider_V = new JSlider (
      (int) Math.floor (getFunctionGenerator ().getMinDCOffset_V ()),
      (int) Math.ceil (getFunctionGenerator ().getMaxDCOffset_V ()),
      0);
    setPanelBorder (
      this.jOffsetSlider_V,
      getLevel () + 2,
      getGuiPreferencesDCColor (),
      "[V]");
    this.jOffsetSlider_V.setToolTipText ("-");
    this.jOffsetSlider_V.setMajorTickSpacing (1);
    this.jOffsetSlider_V.setPaintTicks (true);
    this.jOffsetSlider_V.setPaintLabels (true);
    this.jOffsetSlider_V.addChangeListener (this.jOffsetSlider_VChangeListener);
    offsetPanel.add (this.jOffsetSlider_V);
    this.jOffsetSlider_mV = new JSlider (0, 999, 0);
    setPanelBorder (
      this.jOffsetSlider_mV,
      getLevel () + 2,
      getGuiPreferencesDCColor (),
      "[mV]");
    this.jOffsetSlider_mV.setToolTipText ("-");
    this.jOffsetSlider_mV.setMajorTickSpacing (100);
    this.jOffsetSlider_mV.setMinorTickSpacing (10);
    this.jOffsetSlider_mV.setPaintTicks (true);
    this.jOffsetSlider_mV.setPaintLabels (false);
    this.jOffsetSlider_mV.addChangeListener (this.jOffsetSlider_mVChangeListener);
    offsetPanel.add (this.jOffsetSlider_mV);
    add (offsetPanel);
    
    getFunctionGenerator ().addInstrumentListener (this.instrumentListener);
    
  }
  
  public JDefaultFunctionGeneratorView (final FunctionGenerator functionGenerator)
  {
    this (functionGenerator, 0);
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
  // OUTPUT ENABLE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JColorCheckBox.JBoolean jOutputEnable;
  
  private final ActionListener jOutputEnableListener = (final ActionEvent ae) ->
  {
    final Boolean currentValue = JDefaultFunctionGeneratorView.this.jOutputEnable.getDisplayedValue ();
    final boolean newValue = currentValue == null || (! currentValue);
    try
    {
      getFunctionGenerator ().setOutputEnable (newValue);
    }
    catch (Exception e)
    {
      LOG.log (Level.WARNING, "Caught exception while setting output enable {0} on instrument {1}: {2}.",        
        new Object[]
          {newValue, getInstrument (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // WAVEFORM
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JComboBox<FunctionGenerator.Waveform> jWaveform;
  
  private final ActionListener jWaveformListener = (final ActionEvent ae) ->
  {
    if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
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
    }
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSevenSegmentNumber jFreq;
  
  private final MouseListener jFreqMouseListener = new MouseAdapter ()
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
  };
    
  private final JSlider jFreqSlider_MHz;
  
  private final ChangeListener jFreqSlider_MHzChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
        try
        {
          JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setFrequency_Hz (
            + 1.0e6 * source.getValue ()
            + 1.0e3 * JDefaultFunctionGeneratorView.this.jFreqSlider_kHz.getValue ()
            + 1.0 * JDefaultFunctionGeneratorView.this.jFreqSlider_Hz.getValue ()
            + 1.0e-3 * JDefaultFunctionGeneratorView.this.jFreqSlider_mHz.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} MHz: {1}.",
            new Object[]{source.getValue (), e});          
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
    
  private final JSlider jFreqSlider_kHz;
  
  private final ChangeListener jFreqSlider_kHzChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
        try
        {
          JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setFrequency_Hz (
                1.0e6 * JDefaultFunctionGeneratorView.this.jFreqSlider_MHz.getValue ()
              + 1.0e3 * source.getValue ()
              + 1.0 * JDefaultFunctionGeneratorView.this.jFreqSlider_Hz.getValue ()
              + 1.0e-3 * JDefaultFunctionGeneratorView.this.jFreqSlider_mHz.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} kHz: {1}.",
            new Object[]{source.getValue (), e});          
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
  
  private final JSlider jFreqSlider_Hz;
  
  private final ChangeListener jFreqSlider_HzChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
        try
        {
          JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setFrequency_Hz (
                1.0e6 * JDefaultFunctionGeneratorView.this.jFreqSlider_MHz.getValue ()
              + 1.0e3 * JDefaultFunctionGeneratorView.this.jFreqSlider_kHz.getValue ()
              + 1.0 * source.getValue ()
              + 1.0e-3 * JDefaultFunctionGeneratorView.this.jFreqSlider_mHz.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} Hz: {1}.",
            new Object[]{source.getValue (), e});          
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
  
  private final JSlider jFreqSlider_mHz;
  
  private final ChangeListener jFreqSlider_mHzChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
        try
        {
          JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setFrequency_Hz (
                1.0e6 * JDefaultFunctionGeneratorView.this.jFreqSlider_MHz.getValue ()
              + 1.0e3 * JDefaultFunctionGeneratorView.this.jFreqSlider_kHz.getValue ()
              + 1.0 * JDefaultFunctionGeneratorView.this.jFreqSlider_Hz.getValue ()
              + 1.0e-3 * source.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting frequency from slider to {0} mHz: {1}.",
            new Object[]{source.getValue (), e});          
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // AMPLITUDE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSevenSegmentNumber jAmp;
  
  private final MouseListener jAmpMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newAmplitude_Vpp = getVoltageFromDialog_V ("Enter Amplitude [V] (pp)", null);
      if (newAmplitude_Vpp != null)
        try
        {
          JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setAmplitude_Vpp (newAmplitude_Vpp);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting amplitude on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting amplitude on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };
    
  private final JSlider jAmpSlider_V;
  
  private final ChangeListener jAmpSlider_VChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
        try
        {
          JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setAmplitude_Vpp (
            + 1.0 * source.getValue ()
            + 1.0e-3 * JDefaultFunctionGeneratorView.this.jAmpSlider_mV.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting amplitude from slider to {0} V: {1}.",
            new Object[]{source.getValue (), e});
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
    
  private final JSlider jAmpSlider_mV;

  private final ChangeListener jAmpSlider_mVChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
        try
        {
          JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setAmplitude_Vpp (
            + 1.0 * JDefaultFunctionGeneratorView.this.jAmpSlider_V.getValue ()
            + 1.0e-3 * source.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting amplitude from slider to {0} mV: {1}.",
            new Object[]{source.getValue (), e});
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // DC OFFSET
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSevenSegmentNumber jOffset;
  
  private final MouseListener jOffsetMouseListener = new MouseAdapter ()
  {
    @Override
    public void mouseClicked (final MouseEvent me)
    {
      final Double newOffset_V = getVoltageFromDialog_V ("Enter DC Offset [V]", null);
      if (newOffset_V != null)
        try
        {
          JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setDCOffset_V (newOffset_V);
        }
        catch (InterruptedException ie)
        {
           LOG.log (Level.INFO, "Caught InterruptedException while setting DC offset on instrument: {0}.",
             ie.getStackTrace ());
        }
        catch (IOException ioe)
        {
           LOG.log (Level.INFO, "Caught IOException while setting DC offset on instrument: {0}.",
             ioe.getStackTrace ());
        }
    }
  };

  private final JSlider jOffsetSlider_V;
  
  private final ChangeListener jOffsetSlider_VChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
        try
        {
          JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setDCOffset_V (
            + 1.0 * source.getValue ()
            + 1.0e-3 * JDefaultFunctionGeneratorView.this.jOffsetSlider_mV.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting DC offset from slider to {0} V: {1}.",
            new Object[]{source.getValue (), e});
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
    
  private final JSlider jOffsetSlider_mV;
  
  private final ChangeListener jOffsetSlider_mVChangeListener = (final ChangeEvent ce) ->
  {
    JSlider source = (JSlider) ce.getSource ();
    if (! source.getValueIsAdjusting ())
    {
      if (! JDefaultFunctionGeneratorView.this.inhibitInstrumentControl)
        try
        {
          JDefaultFunctionGeneratorView.this.getFunctionGenerator ().setDCOffset_V (
            + 1.0 * JDefaultFunctionGeneratorView.this.jOffsetSlider_V.getValue ()
            + 1.0e-3 * source.getValue ());
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting DC offset from slider to {0} mV: {1}.",
            new Object[]{source.getValue (), e});
        }
    }
    else
      source.setToolTipText (Integer.toString (source.getValue ()));
  };
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // INSTRUMENT SPECIFIC
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected final JPanel jInstrumentSpecificPanel;

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
          final boolean isOutputEnable = settings.isOuputEnable ();
          JDefaultFunctionGeneratorView.this.jOutputEnable.setDisplayedValue (isOutputEnable);
          //
          final FunctionGenerator.Waveform waveform = settings.getWaveform ();
          final boolean isSupportedWaveform =
            JDefaultFunctionGeneratorView.this.getFunctionGenerator ().isSupportedWaveform (waveform);
          JDefaultFunctionGeneratorView.this.jWaveform.setSelectedItem (isSupportedWaveform ? waveform : null);
          //
          final double amplitude_Vpp = settings.getAmplitude_Vpp ();
          JDefaultFunctionGeneratorView.this.jAmp.setNumber (amplitude_Vpp);
          final long amplitude_mV_long = Math.round (amplitude_Vpp * 1e3);
          final int amplitude_int_V = (int) (amplitude_mV_long / 1000L);
          JDefaultFunctionGeneratorView.this.jAmpSlider_V.setValue (amplitude_int_V);
          JDefaultFunctionGeneratorView.this.jAmpSlider_V.setToolTipText (Integer.toString (amplitude_int_V));
          final int amplitude_rem_int_mV = (int) (amplitude_mV_long % 1000L);
          JDefaultFunctionGeneratorView.this.jAmpSlider_mV.setValue (amplitude_rem_int_mV);
          JDefaultFunctionGeneratorView.this.jAmpSlider_mV.setToolTipText (Integer.toString (amplitude_rem_int_mV));
          //
          final double offset_V = settings.getDCOffset_V ();
          JDefaultFunctionGeneratorView.this.jOffset.setNumber (offset_V);
          final long offset_mV_long = Math.round (offset_V * 1e3);
          final int offset_int_V = (int) Math.floorDiv (offset_mV_long, 1000L);
          final int offset_rem_int_mV = (int) Math.floorMod (offset_mV_long, 1000L);
          JDefaultFunctionGeneratorView.this.jOffsetSlider_V.setValue (offset_int_V);
          JDefaultFunctionGeneratorView.this.jOffsetSlider_V.setToolTipText (Integer.toString (offset_int_V));
          JDefaultFunctionGeneratorView.this.jOffsetSlider_mV.setValue (offset_rem_int_mV);
          JDefaultFunctionGeneratorView.this.jOffsetSlider_mV.setToolTipText (Integer.toString (offset_rem_int_mV));
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
