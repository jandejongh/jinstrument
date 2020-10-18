/*
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.Unit;
import org.javajdj.jinstrument.DefaultDigitalMultiMeterReading;
import org.javajdj.jinstrument.DigitalMultiMeter;
import org.javajdj.jinstrument.DigitalMultiMeterReading;
import org.javajdj.jinstrument.DigitalMultiMeterSettings;
import org.javajdj.jinstrument.Resolution;
import org.javajdj.jinstrument.swing.base.JDigitalMultiMeterPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A one-size-fits-all Swing panel for control and status of a generic {@link DigitalMultiMeter}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JDefaultDigitalMultiMeterView
  extends JDigitalMultiMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JDefaultDigitalMultiMeterView.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JDefaultDigitalMultiMeterView (final DigitalMultiMeter digitalMultiMeter, final int level)
  {
    //
    super (digitalMultiMeter, level);
    //
    setOpaque (true);
    setLayout (new GridLayout (2, 2));
    //
    final JDefaultInstrumentManagementView instrumentManagementPanel= new JDefaultInstrumentManagementView (
      JDefaultDigitalMultiMeterView.this.getInstrument (),
      JDefaultDigitalMultiMeterView.this.getLevel () + 1);
    setPanelBorder (
      instrumentManagementPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Management");
    add (instrumentManagementPanel);
    //
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
    //
    final JPanel controlPanel = new JPanel ();
    setPanelBorder (
      controlPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor (),
      "Resolution/Mode/Range");
    controlPanel.setLayout (new GridLayout (1, 3));
    this.jResolution = new JResolutionPanel ();
    setPanelBorder (
      this.jResolution,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor (),
      "Resolution");
    controlPanel.add (this.jResolution);
    final JPanel modePanel = new JPanel ();
    setPanelBorder (
      modePanel,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor (),
      "Mode");
    modePanel.setLayout (new GridLayout (digitalMultiMeter.getSupportedMeasurementModes ().size (), 1));
    this.modeButtonGroup = new ButtonGroup ();
    this.modeButtonMap = new HashMap<> ();
    for (final DigitalMultiMeter.MeasurementMode measurementMode : digitalMultiMeter.getSupportedMeasurementModes ())
    {
      final JRadioButton modeButton = new JRadioButton (measurementMode.toString ());
      this.modeButtonMap.put (modeButton, measurementMode);
      this.modeButtonGroup.add (modeButton);
      modeButton.addActionListener (this.modeButtonActionListener);
      modePanel.add (modeButton);
    }
    controlPanel.add (modePanel);
    final JPanel rangePanel = new JPanel ();
    setPanelBorder (
      rangePanel,
      getLevel () + 2,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor (),
      "Range");
    rangePanel.setLayout (new GridLayout (2,1));
    final JPanel jAutoRangePanel = new JPanel ();
    jAutoRangePanel.setLayout (new GridLayout (1, 1));
    this.jAutoRange = new JColorCheckBox<> ((t) -> t != null && t ? Color.red : null);
    this.jAutoRange.setHorizontalAlignment (SwingConstants.CENTER);
    this.jAutoRange.setVerticalAlignment (SwingConstants.CENTER);
    jAutoRangePanel.add (this.jAutoRange);
    this.jAutoRange.addActionListener (this.autoRangeActionListener);
    setPanelBorder (
      jAutoRangePanel,
      getLevel () + 3,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor (),
      "Auto");
    rangePanel.add (jAutoRangePanel);
    this.jRangePanel = new JRangePanel ();
    this.jRangePanel.setAlignmentX (Component.CENTER_ALIGNMENT);
    rangePanel.add (this.jRangePanel);
    controlPanel.add (rangePanel);
    add (controlPanel);
    //
    final JPanel readingPanel = new JPanel ();
    setPanelBorder (
      readingPanel, getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor (),
      "Reading");
    readingPanel.setLayout (new GridLayout (2, 1));
    // XXX Should use JDefaultInstrumentReadingPanel_Double here?
    this.jReadingValue7 = new JSevenSegmentNumber (getGuiPreferencesAmplitudeColor (), -1000, 1000, 1e-6); // XXX Arbitrary values!!
    readingPanel.add (this.jReadingValue7);
    final JPanel jUnitPanel = new JPanel ();
    jUnitPanel.setLayout (new GridLayout (1, 1));
    setPanelBorder (
      jUnitPanel,
      getLevel () + 2,
      getGuiPreferencesAmplitudeColor (),
      "Unit");
    this.jUnit = new JLabel ("Unknown");
    this.jUnit.setHorizontalAlignment (SwingConstants.CENTER);
    this.jUnit.setForeground (getGuiPreferencesAmplitudeColor ());
    this.jUnit.setFont (new Font ("Serif", Font.BOLD, 64));
    jUnitPanel.add (this.jUnit);
    readingPanel.add (jUnitPanel);
    add (readingPanel);
    //
    getDigitalMultiMeter ().addInstrumentListener (this.instrumentListener);
    //
  }
  
  public JDefaultDigitalMultiMeterView (final DigitalMultiMeter digitalMultiMeter)
  {
    this (digitalMultiMeter, 0);
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
      return "Default Digital MultiMeter View";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof DigitalMultiMeter))
        return new JDefaultDigitalMultiMeterView ((DigitalMultiMeter) instrument);
      else
        return null;
    }
    
  };

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentView
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getInstrumentViewUrl ()
  {
    return JDefaultDigitalMultiMeterView.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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

  private class JResolutionPanel
    extends JPanel
  {
    
    public JResolutionPanel ()
    {
      final List<Resolution> resolutions = getDigitalMultiMeter ().getSupportedResolutions ();
      setLayout (new GridLayout (resolutions.size (), 2));
      this.resolutionsMap = new LinkedHashMap<> ();
      for (final Resolution resolution : resolutions)
      {
        final JColorCheckBox<Boolean> jccb = new JColorCheckBox<> ((t) -> (t != null && t) ? Color.red : null);
        this.resolutionsMap.put (resolution, jccb);
        add (jccb);
        add (new JLabel (resolution.toString ()));
        jccb.addActionListener ((ActionEvent ae) ->
        {
          try
          {
            getDigitalMultiMeter ().setResolution (resolution);
          }
          catch (Exception e)
          {
            LOG.log (Level.INFO, "Caught exception while setting resolution {0} on instrument {1}: {2}.",        
              new Object[]
                {resolution, getDigitalMultiMeter (), Arrays.toString (e.getStackTrace ())});
          }
        });
      }
    }
    
    private final Map<Resolution, JColorCheckBox> resolutionsMap;
    
    public void setResolution (Resolution newResolution)
    {
      for (final Resolution resolution : this.resolutionsMap.keySet ())
      {
        this.resolutionsMap.get (resolution).setDisplayedValue (resolution == newResolution);
        this.resolutionsMap.get (resolution).repaint ();
      }
    }
    
  }
  
  private final JResolutionPanel jResolution;
  
  private final ButtonGroup modeButtonGroup;  
  
  private final Map<JRadioButton, DigitalMultiMeter.MeasurementMode> modeButtonMap;
  
  private DigitalMultiMeter.MeasurementMode getSelectedMeasurementModeFromSwing ()
  {
    for (final JRadioButton modeButton : this.modeButtonMap.keySet ())
      if (modeButton.isSelected ())
        return this.modeButtonMap.get (modeButton);
    return null;
  }
  
  private void setMeasurementMode (final DigitalMultiMeter.MeasurementMode measurementMode)
  {
    this.modeButtonGroup.clearSelection ();
    for (final JRadioButton modeButton : this.modeButtonMap.keySet ())
    {
      modeButton.setSelected (this.modeButtonMap.get (modeButton) == measurementMode);
      this.modeButtonGroup.setSelected (modeButton.getModel (), this.modeButtonMap.get (modeButton) == measurementMode);
      modeButton.repaint ();
    }
  }
  
  private final ActionListener modeButtonActionListener = (final ActionEvent ae) ->
  {
    final JRadioButton modeButton = (JRadioButton) ae.getSource ();
    final DigitalMultiMeter.MeasurementMode newMeasurementMode = JDefaultDigitalMultiMeterView.this.modeButtonMap.get (modeButton);
    try
    {
      getDigitalMultiMeter ().setMeasurementMode (newMeasurementMode);
    }
    catch (Exception e)
    {
      LOG.log (Level.INFO, "Caught exception while setting measurement mode {0} on instrument {1}: {2}.",        
        new Object[]
          {newMeasurementMode, getDigitalMultiMeter (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
  private final ActionListener autoRangeActionListener = (final ActionEvent ae) ->
  {
    try
    {
      getDigitalMultiMeter ().setAutoRange ();
    }
    catch (Exception e)
    {
      LOG.log (Level.INFO, "Caught exception while setting Auto Range on instrument {0}: {1}.",        
        new Object[]
          {getDigitalMultiMeter (), Arrays.toString (e.getStackTrace ())});
    }
  };
  
  private JColorCheckBox<Boolean> jAutoRange;
  
  private class JRangePanelSingleMeasurementMode
    extends JPanel
  {
    

    public JRangePanelSingleMeasurementMode (DigitalMultiMeter.MeasurementMode measurementMode)
    {
      if (measurementMode == null)
        throw new IllegalArgumentException ();
      this.measurementMode = measurementMode;
      this.rangesMap = new LinkedHashMap<> ();
      final List<DigitalMultiMeter.Range> ranges = getDigitalMultiMeter ().getSupportedRanges (measurementMode);
      setLayout (new GridLayout (ranges.size (), 3));
      for (final DigitalMultiMeter.Range range : ranges)
      {
        final JColorCheckBox<Boolean> jccb = new JColorCheckBox<> ((t) -> (t != null && t) ? Color.red : null);
        this.rangesMap.put (range, jccb);
        add (jccb);
        add (new JLabel (Double.toString (range.getValue ())));
        add (new JLabel (range.getUnit ().toString ()));
        jccb.addActionListener ((ActionEvent ae) ->
        {
          try
          {
            getDigitalMultiMeter ().setRange (range);
          }
          catch (Exception e)
          {
            LOG.log (Level.INFO, "Caught exception while setting range {0} on instrument {1}: {2}.",        
              new Object[]
                {range, getDigitalMultiMeter (), Arrays.toString (e.getStackTrace ())});
          }
        });
      }
      this.currentRange = null;
    }
    
    private final DigitalMultiMeter.MeasurementMode measurementMode;
    
    private final Map<DigitalMultiMeter.Range, JColorCheckBox> rangesMap;
    
    private DigitalMultiMeter.Range currentRange;
    
    public void setRange (final DigitalMultiMeter.Range range)
    {
      if (range != this.currentRange)
      {
        if (this.currentRange != null)
        {
          this.rangesMap.get (this.currentRange).setDisplayedValue (false);
          this.rangesMap.get (this.currentRange).repaint ();
        }
        if (range != null && this.rangesMap.containsKey (range))
        {
          this.rangesMap.get (range).setDisplayedValue (true);
          this.rangesMap.get (range).repaint ();
        }
        this.currentRange = range;
        repaint ();
      }
    }
    
  }

  private class JRangePanel
    extends JPanel
  {
    
    public JRangePanel ()
    {
      setLayout (new GridLayout (1, 1));
      add (new JLabel ("Unknown"));
      this.modePanels = new LinkedHashMap<> ();
      final List<DigitalMultiMeter.MeasurementMode> modes =
        JDefaultDigitalMultiMeterView.this.getDigitalMultiMeter ().getSupportedMeasurementModes ();
      for (final DigitalMultiMeter.MeasurementMode mode : modes)
      {
        this.modePanels.put (mode, new JRangePanelSingleMeasurementMode (mode));
      }
    }
    
    private DigitalMultiMeterSettings settings;
    
    private final Map<DigitalMultiMeter.MeasurementMode, JRangePanelSingleMeasurementMode> modePanels;
    
    public void setDigitalMultiMeterSettings (DigitalMultiMeterSettings settings)
    {
      if (settings != this.settings)
      {
        if (settings == null)
        {
          removeAll ();
          add (new JLabel ("Unknown"));
          this.settings = settings;
          revalidate ();
          repaint ();
        }
        else if (this.settings != null && settings.getMeasurementMode () == this.settings.getMeasurementMode ())
        {
          final JRangePanelSingleMeasurementMode subPanel = this.modePanels.get (settings.getMeasurementMode ());
          subPanel.setRange (settings.getRange ());
          this.settings = settings;
          repaint ();
        }
        else if (this.modePanels.containsKey (settings.getMeasurementMode ()))
        {
          final JRangePanelSingleMeasurementMode subPanel = this.modePanels.get (settings.getMeasurementMode ());
          removeAll ();
          add (subPanel);
          subPanel.setRange (settings.getRange ());
          this.settings = settings;
          revalidate ();
          repaint ();
        }
        else
        {
          removeAll ();
          add (new JLabel ("Unsupported"));
          revalidate ();
          repaint ();
        }
      }
    }
    
  }
  
  private JRangePanel jRangePanel;
  
  protected final JPanel jInstrumentSpecificPanel;
  
  private JSevenSegmentNumber jReadingValue7;
  
  private JLabel jUnit;
  
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
      if (instrument != JDefaultDigitalMultiMeterView.this.getDigitalMultiMeter () || instrumentSettings == null)
        throw new IllegalArgumentException ();
      if (! (instrumentSettings instanceof DigitalMultiMeterSettings))
        throw new IllegalArgumentException ();
      final DigitalMultiMeterSettings settings = (DigitalMultiMeterSettings) instrumentSettings;
      SwingUtilities.invokeLater (() ->
      {
        JDefaultDigitalMultiMeterView.this.inhibitInstrumentControl = true;
        //
        try
        {
          JDefaultDigitalMultiMeterView.this.jResolution.setResolution (settings.getResolution ());
          JDefaultDigitalMultiMeterView.this.setMeasurementMode (settings.getMeasurementMode ());
          JDefaultDigitalMultiMeterView.this.jAutoRange.setDisplayedValue (settings.isAutoRange ());
          JDefaultDigitalMultiMeterView.this.jRangePanel.setDigitalMultiMeterSettings (settings);
        }
        finally
        {
          JDefaultDigitalMultiMeterView.this.inhibitInstrumentControl = false;
        }
      });
    }

    @Override
    public void newInstrumentReading (final Instrument instrument, final InstrumentReading instrumentReading)
    {
      if (instrument != JDefaultDigitalMultiMeterView.this.getDigitalMultiMeter () || instrumentReading == null)
        throw new IllegalArgumentException ();
      if (! (instrumentReading instanceof DigitalMultiMeterReading))
        throw new IllegalArgumentException ();
      final DigitalMultiMeterReading digitalMultiMeterReading = (DefaultDigitalMultiMeterReading) instrumentReading;
      SwingUtilities.invokeLater (() ->
      {
        final double readingValue = digitalMultiMeterReading.getMultiMeterReading ();
        JDefaultDigitalMultiMeterView.this.jReadingValue7.setNumber (readingValue);
        final Unit unit = digitalMultiMeterReading.getUnit ();
        JDefaultDigitalMultiMeterView.this.jUnit.setText (unit != null ? unit.toString () : "No Unit Provided [error]");
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
