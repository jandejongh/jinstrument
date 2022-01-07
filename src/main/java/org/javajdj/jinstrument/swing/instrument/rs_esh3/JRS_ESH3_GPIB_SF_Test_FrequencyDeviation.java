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
package org.javajdj.jinstrument.swing.instrument.rs_esh3;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentListener;
import org.javajdj.jinstrument.InstrumentReading;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.InstrumentStatus;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SelectiveLevelMeter;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Reading;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Settings;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;
import org.javajdj.jswing.jsevensegment.JSevenSegmentNumber;

/** A Swing panel for Test Frequency Deviation setting and readings
 *    of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_SF_Test_FrequencyDeviation
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_SF_Test_FrequencyDeviation (
    final SelectiveLevelMeter selectiveLevelMeter,
    final String title,
    final int level,
    final Color panelColor)
  {

    super (selectiveLevelMeter, title, level, panelColor);
    if (! (selectiveLevelMeter instanceof RS_ESH3_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final RS_ESH3_GPIB_Instrument rs_esh3 = (RS_ESH3_GPIB_Instrument) selectiveLevelMeter;

    removeAll ();
    setLayout (new GridLayout (1, 6));
    
    final JPanel enableStatusPanel1 = new JPanel ();
    enableStatusPanel1.setLayout (new GridLayout (2,2));
    add (enableStatusPanel1);
    
    enableStatusPanel1.add (JCenter.XY (new JLabel ("E")));
    enableStatusPanel1.add (JCenter.XY (JCenter.Y (new JBoolean_JBoolean (
      "[kHz]",
      (settings) -> ((RS_ESH3_GPIB_Settings) settings).isTestFrequencyDeviation (),
      rs_esh3::setTestFrequencyDeviation,
      Color.green,
      true))));
    
    enableStatusPanel1.add (JCenter.XY (new JLabel ("V")));
    this.jValid1 = new JColorCheckBox.JBoolean ();
    this.jValid1.setDisplayedValue (false);
    this.jValid1.setToolTipText ("Que?");
    enableStatusPanel1.add (JCenter.XY (JCenter.Y (this.jValid1)));
    
    this.jFrequencyDeviation = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 3, 0);
    add (this.jFrequencyDeviation);
    
    final JPanel enableStatusPanel2 = new JPanel ();
    enableStatusPanel2.setLayout (new GridLayout (2,2));
    add (enableStatusPanel2);
    
    enableStatusPanel2.add (JCenter.XY (new JLabel ("E")));
    enableStatusPanel2.add (JCenter.XY (JCenter.Y (new JBoolean_JBoolean (
      "+[kHz]",
      (settings) -> ((RS_ESH3_GPIB_Settings) settings).isTestFrequencyDeviationPositivePeak (),
      rs_esh3::setTestFrequencyDeviationPositivePeak,
      Color.green,
      true))));
    
    enableStatusPanel2.add (JCenter.XY (new JLabel ("V")));
    this.jValid2 = new JColorCheckBox.JBoolean ();
    this.jValid2.setDisplayedValue (false);
    this.jValid2.setToolTipText ("Que?");
    enableStatusPanel2.add (JCenter.XY (JCenter.Y (this.jValid2)));
        
    this.jFrequencyDeviationPositivePeak =
      new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 3, 0);
    add (this.jFrequencyDeviationPositivePeak);
    
    final JPanel enableStatusPanel3 = new JPanel ();
    enableStatusPanel3.setLayout (new GridLayout (2,2));
    add (enableStatusPanel3);
    
    enableStatusPanel3.add (JCenter.XY (new JLabel ("E")));
    enableStatusPanel3.add (JCenter.XY (JCenter.Y (new JBoolean_JBoolean (
      "-[kHz]",
      (settings) -> ((RS_ESH3_GPIB_Settings) settings).isTestFrequencyDeviationNegativePeak (),
      rs_esh3::setTestFrequencyDeviationNegativePeak,
      Color.green,
      true))));
    
    enableStatusPanel3.add (JCenter.XY (new JLabel ("V")));
    this.jValid3 = new JColorCheckBox.JBoolean ();
    this.jValid3.setDisplayedValue (false);
    this.jValid3.setToolTipText ("Que?");
    enableStatusPanel3.add (JCenter.XY (JCenter.Y (this.jValid3)));
            
    this.jFrequencyDeviationNegativePeak =
      new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), false, 3, 0);
    add (this.jFrequencyDeviationNegativePeak);
    
    getSelectiveLevelMeter ().addInstrumentListener (this.instrumentListener);

  }

  public JRS_ESH3_GPIB_SF_Test_FrequencyDeviation (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_SF_Test_FrequencyDeviation (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
  {
    this (selectiveLevelMeter, 0);
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
      return "R&S ESH-3 [GPIB] Test/Frequency Deviation Settings/Readings";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_SF_Test_ModulationDepth ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_SF_Test_ModulationDepth.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JColorCheckBox.JBoolean jValid1;
  private final JColorCheckBox.JBoolean jValid2;
  private final JColorCheckBox.JBoolean jValid3;
  
  private final JSevenSegmentNumber jFrequencyDeviation;
  private final JSevenSegmentNumber jFrequencyDeviationPositivePeak;
  private final JSevenSegmentNumber jFrequencyDeviationNegativePeak;
  
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
      if (instrument != JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.this.getSelectiveLevelMeter () || instrumentReading == null)
        throw new IllegalArgumentException ();
      if (! (instrumentReading instanceof RS_ESH3_GPIB_Reading))
        throw new IllegalArgumentException ();
      final RS_ESH3_GPIB_Reading reading = (RS_ESH3_GPIB_Reading) instrumentReading;
      final RS_ESH3_GPIB_Reading.ReadingType readingType = reading.getReadingType ();
      final double frequencyOffset_kHz;
      final boolean valid = ! reading.isError ();
      final String errorString = (valid ? "OK" : reading.getErrorMessage ());
      switch (readingType)
      {
        case FrequencyDeviation_kHz:
          frequencyOffset_kHz = reading.getFrequencyDeviation_kHz ();
          break;
        case FrequencyDeviationPositivePeak_kHz:
          frequencyOffset_kHz = reading.getFrequencyDeviationPositivePeak_kHz ();
          break;
        case FrequencyDeviationNegativePeak_kHz:
          frequencyOffset_kHz = reading.getFrequencyDeviationNegativePeak_kHz ();
          break;
        default:
          return;
      }
      SwingUtilities.invokeLater (() ->
      {
        setInhibitInstrumentControl ();
        //
        try
        {
          switch (readingType)
          {
            case FrequencyDeviation_kHz:
              JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.this.jValid1.setDisplayedValue (valid);
              JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.this.jValid1.setToolTipText (errorString);
              JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.this.jFrequencyDeviation.setNumber (frequencyOffset_kHz);
              break;
            case FrequencyDeviationPositivePeak_kHz:
              JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.this.jValid2.setDisplayedValue (valid);
              JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.this.jValid2.setToolTipText (errorString);
              JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.this.jFrequencyDeviationPositivePeak.setNumber (frequencyOffset_kHz);
              break;
            case FrequencyDeviationNegativePeak_kHz:
              JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.this.jValid3.setDisplayedValue (valid);
              JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.this.jValid3.setToolTipText (errorString);
              JRS_ESH3_GPIB_SF_Test_FrequencyDeviation.this.jFrequencyDeviationNegativePeak.setNumber (frequencyOffset_kHz);
              break;
            default:
              throw new RuntimeException ();
          }
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
  // CLEAR READING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected final void clearReading ()
  {
    this.jValid1.setDisplayedValue (false);
    this.jValid1.setToolTipText ("Que?");
    this.jFrequencyDeviation.setBlank ();
    this.jValid2.setDisplayedValue (false);
    this.jValid2.setToolTipText ("Que?");
    this.jFrequencyDeviationPositivePeak.setBlank ();
    this.jValid3.setDisplayedValue (false);
    this.jValid3.setToolTipText ("Que?");
    this.jFrequencyDeviationNegativePeak.setBlank ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
