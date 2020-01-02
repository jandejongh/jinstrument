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
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SpectrumAnalyzer;
import org.javajdj.jinstrument.SpectrumAnalyzerSettings;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A one-size-fits-all Swing panel for controlling the {@link InstrumentSettings} of a generic {@link SpectrumAnalyzer}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class JSpectrumAnalyzerSettingsPanel
  extends JSpectrumAnalyzerPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JSpectrumAnalyzerSettingsPanel.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JSpectrumAnalyzerSettingsPanel (
    final SpectrumAnalyzer spectrumAnalyzer,
    final String title,
    final int level,
    final Color panelColor)
  {
    super (spectrumAnalyzer, title, level, panelColor);
    setOpaque (true);
    setMaximumSize (new Dimension (200, 600));
    setLayout (new GridLayout (7, 1));
    final JInstrumentPanel cfPanel = new JInstrumentPanel (
      spectrumAnalyzer,
      "Center Frequency [MHz]",
      level + 1,
      JInstrumentPanel.DEFAULT_FREQUENCY_COLOR);
    cfPanel.setLayout (new GridLayout (1, 1));
    this.jCenterFrequency_MHz = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), 0, 26500, 0.0000001);
    cfPanel.add (this.jCenterFrequency_MHz);
    add (cfPanel);
    final JInstrumentPanel spPanel = new JInstrumentPanel (
      spectrumAnalyzer,
      "Span [MHz]",
      level + 1,
      JInstrumentPanel.DEFAULT_FREQUENCY_COLOR);
    spPanel.setLayout (new GridLayout (1, 1));
    this.jSpan_MHz = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), 0, 26500, 0.0000001);
    spPanel.add (this.jSpan_MHz);
    add (spPanel);
    final JInstrumentPanel rbPanel = new JInstrumentPanel (
      spectrumAnalyzer,
      "Resolution Bandwidth [Hz]",
      level + 1,
      JInstrumentPanel.DEFAULT_FREQUENCY_COLOR);
    rbPanel.setLayout (new GridLayout (1, 2));
    this.jResolutionBandwidth_Hz = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), 1, 1e8, 1);
    rbPanel.add (this.jResolutionBandwidth_Hz);
    this.jResolutionBandwidthCoupled = new JColorCheckBox (
      (Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? JInstrumentPanel.getGuiPreferencesFrequencyColor () : null);
    final JInstrumentPanel jResolutionBandwidthCoupledPanel = new JInstrumentPanel ("Coupled", level + 2, Color.black);
    jResolutionBandwidthCoupledPanel.setLayout (new GridLayout (1, 1));
    jResolutionBandwidthCoupledPanel.add (this.jResolutionBandwidthCoupled);
    rbPanel.add (jResolutionBandwidthCoupledPanel);
    add (rbPanel);
    final JInstrumentPanel vbPanel = new JInstrumentPanel (
      spectrumAnalyzer,
      "Video Bandwidth [Hz]",
      level + 1,
      JInstrumentPanel.DEFAULT_FREQUENCY_COLOR);
    vbPanel.setLayout (new GridLayout (1, 2));
    this.jVideoBandwidth_Hz = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), 1, 1e8, 1);
    vbPanel.add (this.jVideoBandwidth_Hz);
    this.jVideoBandwidthCoupled = new JColorCheckBox (
      (Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? JInstrumentPanel.getGuiPreferencesFrequencyColor () : null);
    final JInstrumentPanel jVideoBandwidthCoupledPanel = new JInstrumentPanel ("Coupled", level + 2, Color.black);
    jVideoBandwidthCoupledPanel.setLayout (new GridLayout (1, 1));
    jVideoBandwidthCoupledPanel.add (this.jVideoBandwidthCoupled);
    vbPanel.add (jVideoBandwidthCoupledPanel);
    add (vbPanel);
    final JInstrumentPanel stPanel = new JInstrumentPanel (
      spectrumAnalyzer,
      "Sweep Time [s]",
      level + 1,
      JInstrumentPanel.DEFAULT_FREQUENCY_COLOR);
    stPanel.setLayout (new GridLayout (1, 2));
    this.jSweepTime_s = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), 1e-6, 1e3, 1e-6);
    stPanel.add (this.jSweepTime_s);
    this.jSweepTimeCoupled = new JColorCheckBox (
      (Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? JInstrumentPanel.getGuiPreferencesFrequencyColor () : null);
    final JInstrumentPanel jSweepTimeCoupledPanel = new JInstrumentPanel ("Coupled", level + 2, Color.black);
    jSweepTimeCoupledPanel.setLayout (new GridLayout (1, 1));
    jSweepTimeCoupledPanel.add (this.jSweepTimeCoupled);
    stPanel.add (jSweepTimeCoupledPanel);
    add (stPanel);
    final JInstrumentPanel rlPanel = new JInstrumentPanel (
      spectrumAnalyzer,
      "Reference Level [dBm]",
      level + 1,
      JInstrumentPanel.DEFAULT_AMPLITUDE_COLOR);
    rlPanel.setLayout (new GridLayout (1, 2));
    this.jReferenceLevel_dBm = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), -130, 60, 0.1);
    rlPanel.add (this.jReferenceLevel_dBm);
    rlPanel.add (new JLabel ());
    add (rlPanel);
    final JInstrumentPanel atPanel = new JInstrumentPanel (
      spectrumAnalyzer,
      "RF Attenuation [dB]",
      level + 1,
      JInstrumentPanel.DEFAULT_AMPLITUDE_COLOR);
    atPanel.setLayout (new GridLayout (1, 2));
    this.jRfAttenuation_dB = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesAmplitudeColor (), 0, /* 100 */ 1e5, 1);
    atPanel.add (this.jRfAttenuation_dB);
    this.jRfAttenuationCoupled = new JColorCheckBox (
      (Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? JInstrumentPanel.getGuiPreferencesAmplitudeColor () : null);
    final JInstrumentPanel jRfAttenuationCoupledPanel = new JInstrumentPanel ("Coupled", level + 2, Color.black);
    jRfAttenuationCoupledPanel.setLayout (new GridLayout (1, 1));
    jRfAttenuationCoupledPanel.add (this.jRfAttenuationCoupled);
    atPanel.add (jRfAttenuationCoupledPanel);
    add (atPanel);
    addSwingListeners ();
    // XXX Beautiful critical race follows below...
    final InstrumentSettings settings = getInstrument ().getCurrentInstrumentSettings ();
    if (settings != null)
      this.instrumentListener.newInstrumentSettings (spectrumAnalyzer, settings);
    getInstrument ().addInstrumentListener (this.instrumentListener);
  }

  public JSpectrumAnalyzerSettingsPanel (final SpectrumAnalyzer spectrumAnalyzer)
  {
    this (spectrumAnalyzer, null, 0, null);
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
      return "Default Spectrum Analyzer Settings View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof SpectrumAnalyzer))
        return new JSpectrumAnalyzerSettingsPanel ((SpectrumAnalyzer) instrument);
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
    return JSpectrumAnalyzerSettingsPanel.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JSevenSegmentNumber jCenterFrequency_MHz;
  private final JSevenSegmentNumber jSpan_MHz;
  private final JSevenSegmentNumber jResolutionBandwidth_Hz;
  private final JColorCheckBox<Boolean> jResolutionBandwidthCoupled;
  private final JSevenSegmentNumber jVideoBandwidth_Hz;
  private final JColorCheckBox<Boolean> jVideoBandwidthCoupled;
  private final JSevenSegmentNumber jSweepTime_s;
  private final JColorCheckBox<Boolean> jSweepTimeCoupled;
  private final JSevenSegmentNumber jReferenceLevel_dBm;
  private final JSevenSegmentNumber jRfAttenuation_dB;
  private final JColorCheckBox<Boolean> jRfAttenuationCoupled;
  
  private void addSwingListeners ()
  {
    this.jCenterFrequency_MHz.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newCenterFrequency_MHz = getFrequencyFromDialog_MHz ("Enter Center Frequency [MHz]",
          ((SpectrumAnalyzerSettings) (getSpectrumAnalyzer ().getCurrentInstrumentSettings ())).getCenterFrequency_MHz ());
        if (newCenterFrequency_MHz != null)
          try
          {
            getSpectrumAnalyzer ().setCenterFrequency_MHz (newCenterFrequency_MHz);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting Center Frequency on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting Center Frequency on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    this.jSpan_MHz.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newSpan_MHz = getFrequencyFromDialog_MHz ("Enter Span [MHz]",
          ((SpectrumAnalyzerSettings) (getSpectrumAnalyzer ().getCurrentInstrumentSettings ())).getSpan_MHz ());
        if (newSpan_MHz != null)
          try
          {
            getSpectrumAnalyzer ().setSpan_MHz (newSpan_MHz);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting Span on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting Span on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    this.jResolutionBandwidth_Hz.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newResolutionBandwidth_Hz = getFrequencyFromDialog_Hz ("Enter Resolution Bandwidth [Hz]",
          ((SpectrumAnalyzerSettings) (getSpectrumAnalyzer ().getCurrentInstrumentSettings ())).getResolutionBandwidth_Hz ());
        if (newResolutionBandwidth_Hz != null)
          try
          {
            getSpectrumAnalyzer ().setResolutionBandwidth_Hz (newResolutionBandwidth_Hz);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting Resolution Bandwidth on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting Resolution Bandwidth on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    this.jResolutionBandwidthCoupled.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        super.mouseClicked (me);
        final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
        try
        {
          final boolean newValue = ! source.getDisplayedValue ();
          source.setDisplayedValue (newValue);
          if (newValue)
            getSpectrumAnalyzer ().setResolutionBandwidthCoupled ();
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting Resolution Bandwidth Coupled on instrument: {0}.",
            e.getStackTrace ());
        }
      }
    });
    this.jVideoBandwidth_Hz.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newVideoBandwidth_Hz = getFrequencyFromDialog_Hz ("Enter Video Bandwidth [Hz]",
          ((SpectrumAnalyzerSettings) (getSpectrumAnalyzer ().getCurrentInstrumentSettings ())).getVideoBandwidth_Hz ());
        if (newVideoBandwidth_Hz != null)
          try
          {
            getSpectrumAnalyzer ().setVideoBandwidth_Hz (newVideoBandwidth_Hz);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting Video Bandwidth on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting Video Bandwidth on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    this.jVideoBandwidthCoupled.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        super.mouseClicked (me);
        final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
        try
        {
          final boolean newValue = ! source.getDisplayedValue ();
          source.setDisplayedValue (newValue);
          if (newValue)
            getSpectrumAnalyzer ().setVideoBandwidthCoupled ();
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting Video Bandwidth Coupled on instrument: {0}.",
            e.getStackTrace ());
        }
      }
    });
    this.jSweepTime_s.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newSweepTime_s = getPeriodFromDialog_s ("Enter Sweep Time [s]",
          ((SpectrumAnalyzerSettings) (getSpectrumAnalyzer ().getCurrentInstrumentSettings ())).getSweepTime_s ());
        if (newSweepTime_s != null)
          try
          {
            getSpectrumAnalyzer ().setSweepTime_s (newSweepTime_s);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting Sweep Time on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting Sweep Time on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    this.jSweepTimeCoupled.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        super.mouseClicked (me);
        final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
        try
        {
          final boolean newValue = ! source.getDisplayedValue ();
          source.setDisplayedValue (newValue);
          if (newValue)
            getSpectrumAnalyzer ().setSweepTimeCoupled ();
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting Sweep Time Coupled on instrument: {0}.",
            e.getStackTrace ());
        }
      }
    });
    this.jReferenceLevel_dBm.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newReferenceLevel_dBm = getPowerFromDialog_dBm ("Enter Reference Level [dBm]",
          ((SpectrumAnalyzerSettings) (getSpectrumAnalyzer ().getCurrentInstrumentSettings ())).getReferenceLevel_dBm ());
        if (newReferenceLevel_dBm != null)
          try
          {
            getSpectrumAnalyzer ().setReferenceLevel_dBm (newReferenceLevel_dBm);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting Reference Level on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting Reference Level on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    this.jRfAttenuation_dB.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        final Double newRfAttenuation_dB = getPowerRatioFromDialog_dB ("Enter RF Attenuation [dB]",
          ((SpectrumAnalyzerSettings) (getSpectrumAnalyzer ().getCurrentInstrumentSettings ())).getRfAttenuation_dB ());
        if (newRfAttenuation_dB != null)
          try
          {
            getSpectrumAnalyzer ().setRfAttenuation_dB (newRfAttenuation_dB);
          }
          catch (InterruptedException ie)
          {
             LOG.log (Level.INFO, "Caught InterruptedException while setting RF Attenuation on instrument: {0}.",
               ie.getStackTrace ());
          }
          catch (IOException ioe)
          {
             LOG.log (Level.INFO, "Caught IOException while setting RF Attenuation on instrument: {0}.",
               ioe.getStackTrace ());
          }
      }
    });
    this.jRfAttenuationCoupled.addMouseListener (new MouseAdapter ()
    {
      @Override
      public void mouseClicked (final MouseEvent me)
      {
        super.mouseClicked (me);
        final JColorCheckBox<Boolean> source = (JColorCheckBox<Boolean>) me.getSource ();
        try
        {
          final boolean newValue = ! source.getDisplayedValue ();
          source.setDisplayedValue (newValue);
          if (newValue)
            getSpectrumAnalyzer ().setRfAttenuationCoupled ();
        }
        catch (IOException | InterruptedException e)
        {
          LOG.log (Level.INFO, "Caught exception while setting RF Attenuation Coupled on instrument: {0}.",
            e.getStackTrace ());
        }
      }
    });
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
    }
    
    @Override
    public void newInstrumentSettings (final Instrument instrument, final InstrumentSettings instrumentSettings)
    {
      LOG.log (Level.WARNING, "PRE-SETTINGS!!! PRE-SETTINGS!!!");
      if (instrument != JSpectrumAnalyzerSettingsPanel.this.getSpectrumAnalyzer ())
        return;
      SwingUtilities.invokeLater (() ->
      {
        JSpectrumAnalyzerSettingsPanel.this.inhibitInstrumentControl = true;
        //
        try
        {
          if (instrumentSettings == null)
          {
            JSpectrumAnalyzerSettingsPanel.this.jCenterFrequency_MHz.setBlank ();        
            JSpectrumAnalyzerSettingsPanel.this.jSpan_MHz.setBlank ();
            JSpectrumAnalyzerSettingsPanel.this.jResolutionBandwidth_Hz.setBlank ();
            JSpectrumAnalyzerSettingsPanel.this.jResolutionBandwidthCoupled.setDisplayedValue (null);
            JSpectrumAnalyzerSettingsPanel.this.jVideoBandwidth_Hz.setBlank ();
            JSpectrumAnalyzerSettingsPanel.this.jVideoBandwidthCoupled.setDisplayedValue (null);
            JSpectrumAnalyzerSettingsPanel.this.jSweepTime_s.setBlank ();
            JSpectrumAnalyzerSettingsPanel.this.jSweepTimeCoupled.setDisplayedValue (null);
            JSpectrumAnalyzerSettingsPanel.this.jReferenceLevel_dBm.setBlank ();
            JSpectrumAnalyzerSettingsPanel.this.jRfAttenuation_dB.setBlank ();
            JSpectrumAnalyzerSettingsPanel.this.jRfAttenuationCoupled.setDisplayedValue (null);
          }
          else
          {
            LOG.log (Level.WARNING, "SETTINGS!!! SETTINGS!!!");
            final SpectrumAnalyzerSettings settings = (SpectrumAnalyzerSettings) instrumentSettings;
            JSpectrumAnalyzerSettingsPanel.this.jCenterFrequency_MHz.setNumber (
              settings.getCenterFrequency_MHz ());
            JSpectrumAnalyzerSettingsPanel.this.jSpan_MHz.setNumber (
              settings.getSpan_MHz ());
            JSpectrumAnalyzerSettingsPanel.this.jResolutionBandwidth_Hz.setNumber (
              settings.getResolutionBandwidth_Hz ());
            JSpectrumAnalyzerSettingsPanel.this.jResolutionBandwidthCoupled.setDisplayedValue (
              settings.isResolutionBandwidthCoupled ());
            JSpectrumAnalyzerSettingsPanel.this.jVideoBandwidth_Hz.setNumber (
              settings.getVideoBandwidth_Hz ());
            JSpectrumAnalyzerSettingsPanel.this.jVideoBandwidthCoupled.setDisplayedValue (
              settings.isVideoBandwidthCoupled ());
            JSpectrumAnalyzerSettingsPanel.this.jSweepTime_s.setNumber (
              settings.getSweepTime_s ());
            JSpectrumAnalyzerSettingsPanel.this.jSweepTimeCoupled.setDisplayedValue (
              settings.isSweepTimeCoupled ());
            JSpectrumAnalyzerSettingsPanel.this.jReferenceLevel_dBm.setNumber (
              settings.getReferenceLevel_dBm ());
            JSpectrumAnalyzerSettingsPanel.this.jRfAttenuation_dB.setNumber (
              settings.getRfAttenuation_dB ());
            JSpectrumAnalyzerSettingsPanel.this.jRfAttenuationCoupled.setDisplayedValue (
              settings.isRfAttenuationCoupled ());
          }
        }
        finally
        {
          JSpectrumAnalyzerSettingsPanel.this.inhibitInstrumentControl = false;
        }
      });
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
