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
import org.javajdj.junits.Unit;
import org.javajdj.jinstrument.DefaultDigitalMultiMeterReading;
import org.javajdj.jinstrument.DigitalMultiMeter;
import org.javajdj.jinstrument.DigitalMultiMeterReading;
import org.javajdj.jinstrument.DigitalMultiMeterSettings;
import org.javajdj.junits.Resolution;
import org.javajdj.jinstrument.swing.base.JDigitalMultiMeterPanel;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A one-size-fits-all Swing panel for control and status of a generic {@link DigitalMultiMeter}.
 * 
 * <p>
 * The sole objective of this class is to represent the settings and the readings of a {@link DigitalMultiMeter}.
 * To that extent, the component creates four (2x2) panels:
 * <ul>
 * <li>An instrument management panel for controlling and monitoring status of
 *       the controller, device, and instrument service.</li>
 * <li>An instrument specific panel allowing sub-classes to bring in
 *       status and control of instrument-specific settings.</li>
 * <li>A panel showing measurement mode, range, and resolution of the instrument (settings).</li>
 * <li>An reading panel showing the most recent reading from the instrument,
 *       as well as the reading's unit.</li>
 * </ul>
 * 
 * <p>
 * Sub-classes may use this {@code class} as a base for providing an instrument-specific view.
 * Each of the four panels can be controlled from sub-classes to <i>not</i> be pre-populated,
 * allowing sub-classes to provide a custom implementation.
 * 
 * <p>
 * Note that sub-classes can also remove these component(s) and add custom
 * panels using standard Swing layout and child-component features.
 * However, certain panels are notified as an {@link InstrumentListener}
 * when populated. Therefore, the preferred method is to disable population
 * of panels upon construction.
 * 
 * @see #getInstrumentManagementPanel
 * @see #getInstrumentSpecificPanel
 * @see #getModeRangeResolutionPanel
 * @see #getReadingPanel
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
  
  protected JDefaultDigitalMultiMeterView (
    final DigitalMultiMeter digitalMultiMeter,
    final int level,
    final boolean populateInstrumentManagementPanel,
    final boolean populateInstrumentSpecificPanel,
    final boolean populateModeRangeResolutionPanel,
    final boolean populateReadingPanel)
  {
    
    super (digitalMultiMeter, level);
    
    setOpaque (true);
    setLayout (new GridLayout (2, 2));
    
    this.jInstrumentManagementPanel = new JPanel ();
    add (this.jInstrumentManagementPanel);
    if (populateInstrumentManagementPanel)
    {
      this.jInstrumentManagementPanel.setLayout (new GridLayout (1, 1));
      this.jInstrumentManagementPanel.add (new JDefaultInstrumentManagementView (
        JDefaultDigitalMultiMeterView.this.getInstrument (),
        JDefaultDigitalMultiMeterView.this.getLevel () + 1));
    }
    setPanelBorder (this.jInstrumentManagementPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Management");
    
    this.jInstrumentSpecificPanel = new JPanel ();
    add (this.jInstrumentSpecificPanel);
    if (populateInstrumentSpecificPanel)
    {
      this.jInstrumentSpecificPanel.setLayout (new GridLayout (1,1));
      final JLabel jInstrumentSpecificLabel = new JLabel ("Not Supported in this View");
      jInstrumentSpecificLabel.setHorizontalAlignment (SwingConstants.CENTER);
      this.jInstrumentSpecificPanel.add (jInstrumentSpecificLabel);  
    }
    setPanelBorder (
      this.jInstrumentSpecificPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesManagementColor (),
      "Instrument Specific");

    this.jModeRangeResolutionPanel = new JPanel ();
    add (this.jModeRangeResolutionPanel);
    if (populateModeRangeResolutionPanel)
    {
      this.jModeRangeResolutionPanel.setLayout (new GridLayout (1, 3));
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
      this.jModeRangeResolutionPanel.add (modePanel);
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
      this.jModeRangeResolutionPanel.add (rangePanel);
      this.jResolution = new JResolutionPanel ();
      setPanelBorder (
        this.jResolution,
        getLevel () + 2,
        JInstrumentPanel.getGuiPreferencesAmplitudeColor (),
        "Resolution");
      this.jModeRangeResolutionPanel.add (this.jResolution);      
    }
    else
    {
      this.modeButtonGroup = null;
      this.modeButtonMap = null;
      this.jAutoRange = null;
      this.jRangePanel = null;
      this.jResolution = null;
    }
    setPanelBorder (this.jModeRangeResolutionPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor (),
      "Mode/Range/Resolution");

    this.jReadingPanel = new JPanel ();
    add (this.jReadingPanel);
    if (populateReadingPanel)
    {
      this.jReadingPanel.setLayout (new GridLayout (2, 1));
       // XXX Arbitrary values!! Should be parameterized in constructor!
      this.jReadingValue7 = new JSevenSegmentNumber (getGuiPreferencesAmplitudeColor (), -1000, 1000, 1e-6);
      this.jReadingPanel.add (this.jReadingValue7);
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
      this.jReadingPanel.add (jUnitPanel);      
    }
    else
    {
      this.jReadingValue7 = null;
      this.jUnit = null;
    }
    setPanelBorder (this.jReadingPanel,
      getLevel () + 1,
      JInstrumentPanel.getGuiPreferencesAmplitudeColor (),
      "Reading");
    
    if (populateModeRangeResolutionPanel || populateReadingPanel)
      getDigitalMultiMeter ().addInstrumentListener (this.instrumentListener);
    
  }
  
  public JDefaultDigitalMultiMeterView (final DigitalMultiMeter digitalMultiMeter, final int level)
  {
    this (digitalMultiMeter, level, true, true, true, true);
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
  // INSTRUMENT MANAGEMENT PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JPanel jInstrumentManagementPanel;
  
  protected final JPanel getInstrumentManagementPanel ()
  {
    return this.jInstrumentManagementPanel;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // INSTRUMENT SPECIFIC PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JPanel jInstrumentSpecificPanel;
  
  protected final JPanel getInstrumentSpecificPanel ()
  {
    return this.jInstrumentSpecificPanel;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // MODE/RANGE/RESOLUTION PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JPanel jModeRangeResolutionPanel;
  
  protected final JPanel getModeRangeResolutionPanel ()
  {
    return this.jModeRangeResolutionPanel;    
  }
  
  //
  // MODE
  //
  
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
  
  //
  // RANGE
  //
  
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
        add (new JLabel (Double.toString (range.getMaxAbsValue ())));
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
  
  //
  // RESOLUTION
  //
  
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // READING PANEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JPanel jReadingPanel;
  
  public final JPanel getReadingPanel ()
  { 
    return this.jReadingPanel;
  }
  
  private final JSevenSegmentNumber jReadingValue7;
  
  private final JLabel jUnit;
  
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
      if (JDefaultDigitalMultiMeterView.this.modeButtonMap == null)
        return;
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
      if (JDefaultDigitalMultiMeterView.this.jReadingValue7 == null)
        return;
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
        JDefaultDigitalMultiMeterView.this.jUnit.setText (unit != null ? unit.toString () : "Null [error]");
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
