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
package org.javajdj.jinstrument.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.Resolution;
import org.javajdj.jinstrument.Unit;
import org.javajdj.jswing.jbyte.JBooleans;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A Swing (base) component for showing a {@code double} {@link InstrumentReading}.
 * 
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class JDefaultInstrumentReadingPanel_Double
  extends JInstrumentPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected JDefaultInstrumentReadingPanel_Double (
    final Instrument instrument,
    final String title,
    final int level,
    final Color panelBorderColor,
    final boolean orientationHorizontal,
    final Color displayColor,
    final double minValue,
    final double maxValue,
    final double resolution,
    final boolean printUnit,
    final List<Unit> units,
    final boolean allowConversions,
    final boolean showReadingStatus)
  {
    super (instrument, title, level, panelBorderColor);
    this.jDisplay = new JSevenSegmentNumber (displayColor, minValue, maxValue, resolution);
    this.jUnitAndReadingStatus = new JUnitAndReadingStatus (
      ! orientationHorizontal,
      displayColor,
      printUnit,
      units,
      allowConversions,
      showReadingStatus);
    if (orientationHorizontal)
    {
      setLayout (new GridBagLayout ());
      final GridBagConstraints gbc = new GridBagConstraints ();
      gbc.fill = GridBagConstraints.BOTH;
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 0.75;
      gbc.weighty = 1;
      gbc.anchor = GridBagConstraints.LINE_START;
      add (this.jDisplay, gbc);
      gbc.gridx = 1;
      gbc.gridy = 0;
      gbc.weightx = 0.25;
      gbc.weighty = 1;
      gbc.anchor = GridBagConstraints.LINE_END;
      add (this.jUnitAndReadingStatus, gbc);
    }
    else
    {
      setLayout (new GridBagLayout ());
      final GridBagConstraints gbc = new GridBagConstraints ();
      gbc.fill = GridBagConstraints.BOTH;
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.weightx = 1;
      gbc.weighty = 0.75;
      gbc.anchor = GridBagConstraints.PAGE_START;
      add (this.jDisplay, gbc);
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.weightx = 1;
      gbc.weighty = 0.25;
      gbc.anchor = GridBagConstraints.PAGE_END;
      add (this.jUnitAndReadingStatus, gbc);
    }
    getInstrument ().addInstrumentListener (this.instrumentListener);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JSevenSegmentNumber jDisplay;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UNIT
  // READING STATUS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private static enum UnitMode
  {
    INSTRUMENT,
    AUTO,
    MANUAL;
  }
    
  private class JUnitAndReadingStatus
    extends JPanel
  {

    public JUnitAndReadingStatus (
      final boolean orientationHorizontal,
      final Color displayColor,
      final boolean printUnit,
      final List<Unit> units,
      final boolean allowConversions,
      final boolean showReadingStatus)
    {
      this.printUnit = printUnit;
      this.units = units;
      this.allowConversions = allowConversions;
      this.showReadingStatus= showReadingStatus;
      if (orientationHorizontal)
        setLayout (new GridLayout (1, (this.printUnit ? 1 : 0) + (this.units != null ? 1: 0) + (this.showReadingStatus ? 1 : 0)));
      else
        setLayout (new GridLayout ((this.printUnit ? 1 : 0) + (this.units != null ? 1: 0) + (this.showReadingStatus ? 1 : 0), 1));
      if (this.printUnit)
      {
        this.jUnit = new JLabel ();
        this.jUnit.setOpaque (true);
        this.jUnit.setBackground (Color.black);
        this.jUnit.setForeground (displayColor);
        this.jUnit.setFont (new Font ("Serif", Font.BOLD, 64));
        this.jUnit.setHorizontalAlignment (SwingConstants.CENTER);
        add (this.jUnit);
      }
      else
        this.jUnit = null;
      if (this.units != null)
      {      
        final JPanel jUnitControlPanel = new JPanel ();
        jUnitControlPanel.setLayout (new GridLayout (1, 2));
        final JPanel jUnitsModePanel = new JPanel ();
        setPanelBorder (jUnitsModePanel, getLevel () + 2, displayColor, "Mode");
        jUnitsModePanel.setLayout (new GridBagLayout ());
        this.jUnitMode = new JComboBox (UnitMode.values ());
        this.jUnitMode.setEditable (false);
        this.jUnitMode.setAlignmentY (Component.CENTER_ALIGNMENT);
        if (allowConversions)
        {
          this.jUnitMode.setSelectedItem (UnitMode.AUTO);
          this.jUnitMode.addActionListener (this.jUnitModeListener);
        }
        else
        {
          this.jUnitMode.setSelectedItem (UnitMode.INSTRUMENT);
          this.jUnitMode.setEnabled (false);
        }
        jUnitsModePanel.add (this.jUnitMode, new GridBagConstraints ());
        jUnitControlPanel.add (jUnitsModePanel);
        final JPanel jUnitsPanel = new JPanel ();
        setPanelBorder (jUnitsPanel, getLevel () + 2, displayColor, "Unit");
        jUnitsPanel.setLayout (new GridBagLayout ());
        this.jUnits = new JComboBox (this.units.toArray ());
        this.jUnits.setEditable (false);
        this.jUnits.setAlignmentY (Component.CENTER_ALIGNMENT);
        if (this.allowConversions)
          this.jUnits.addActionListener (this.jUnitsListener);
        else
          this.jUnits.setEnabled (false);
        jUnitsPanel.add (this.jUnits, new GridBagConstraints ());
        jUnitControlPanel.add (jUnitsPanel);
        add (jUnitControlPanel);
      }
      else
      {
        this.jUnitMode = null;
        this.jUnits = null;
      }
      if (this.showReadingStatus)
      {
        this.jStatus = new JBooleans ((b) -> b != null && b ? displayColor : null,
          new LinkedHashSet (Arrays.asList (new String[]{"ERR", "OVFL", "UNCOR", "UNCAL"})),
          ! orientationHorizontal);
        setPanelBorder (this.jStatus, getLevel () + 2, displayColor, "Status");
        add (this.jStatus);
      }
      else
      {
        this.jStatus = null;
      }
    }
    
    private final boolean printUnit;
    
    private final JLabel jUnit;

    private final JComboBox jUnitMode;
    
    private final List<Unit> units;
    
    private final JComboBox<Unit> jUnits;
    
    private final boolean allowConversions;
    
    private final boolean showReadingStatus;
    
    private volatile InstrumentReading instrumentReading;
    
    private final JBooleans jStatus;
    
     // XXX Could be user-settable...
    private final Unit.AutoRangePolicy autoRangePolicy = Unit.AutoRangePolicy.PREFER_1_1000;
    
    public final void newInstrumentReading (final InstrumentReading instrumentReading)
    {
      this.instrumentReading = instrumentReading;
      // XXX At this stage, the cast should not be necessary!
      double value = (double) instrumentReading.getReadingValue ();
      Unit unit = instrumentReading.getUnit ();
      int decimalPointIndex = JDefaultInstrumentReadingPanel_Double.this.jDisplay.getDefaultDecimalPointIndex ();
      boolean allowSetUnit = false;
      switch ((UnitMode) this.jUnitMode.getSelectedItem ())
      {
        case INSTRUMENT:
          // EMPTY
          break;
        case AUTO:
        {
          final Unit newUnit = Unit.autoRange (this.autoRangePolicy,
            value,
            unit,
            instrumentReading.getResolution (),
            this.units.toArray (new Unit[]{}),
            Resolution.fromNumberOfDigits (JDefaultInstrumentReadingPanel_Double.this.jDisplay.getNumberOfDigits ()),
            true,
            false);
          if (unit != newUnit)
          {
            value = Unit.convertToUnit (value, unit, newUnit);
            unit = newUnit;
            decimalPointIndex = this.autoRangePolicy.getPreferredDecimalPointIndex ();
          }
          break;
        }
        case MANUAL:
          if (this.allowConversions && unit != this.jUnits.getSelectedItem ())
          {
            value = Unit.convertToUnit (value, unit, (Unit) this.jUnits.getSelectedItem ());
            unit = (Unit) this.jUnits.getSelectedItem ();
            // XXX What if the conversion fails??
          }
          allowSetUnit = this.allowConversions;
          break;
        default:
          throw new RuntimeException ();
      }
      JDefaultInstrumentReadingPanel_Double.this.jDisplay.setDecimalPointIndex (decimalPointIndex);
      JDefaultInstrumentReadingPanel_Double.this.jDisplay.setNumber (value);
      if (this.jUnit != null)
        this.jUnit.setText (unit != null ? unit.toString () : "NULL");
      if (this.jUnits != null)
      {
        this.jUnits.setSelectedItem (this.units.contains (unit) ? unit : null);
        this.jUnits.setEnabled (allowSetUnit);
      }
      if (this.showReadingStatus)
      {
        this.jStatus.setDisplayedValue ("ERR", this.instrumentReading.isError ());
        this.jStatus.setDisplayedValue ("OVFL", this.instrumentReading.isOverflow ());
        this.jStatus.setDisplayedValue ("UNCOR", this.instrumentReading.isUncorrected ());
        this.jStatus.setDisplayedValue ("UNCAL", this.instrumentReading.isUncalibrated ());
      }
    }
    
    private final ActionListener jUnitModeListener = (final ActionEvent ae) ->
    {
      if (this.instrumentReading != null)
        newInstrumentReading (this.instrumentReading);
    };
      
    private final ActionListener jUnitsListener = (final ActionEvent ae) ->
    {
      if (this.instrumentReading != null)
        newInstrumentReading (this.instrumentReading);
    };
  
  }
  
  private final JUnitAndReadingStatus jUnitAndReadingStatus;
  
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
      if (instrument != getInstrument ())
        throw new RuntimeException ();
      SwingUtilities.invokeLater (() ->
      {
        JDefaultInstrumentReadingPanel_Double.this.jUnitAndReadingStatus.newInstrumentReading (instrumentReading);
      });
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
