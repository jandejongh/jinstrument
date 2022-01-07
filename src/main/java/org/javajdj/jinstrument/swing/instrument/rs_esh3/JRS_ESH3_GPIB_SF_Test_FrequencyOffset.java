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

/** A Swing panel for Test Frequency Offset setting and readings
 *    of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_SF_Test_FrequencyOffset
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_SF_Test_FrequencyOffset.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_SF_Test_FrequencyOffset (
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
    setLayout (new GridLayout (1, 3));
    
    final JPanel enableStatusPanel = new JPanel ();
    enableStatusPanel.setLayout (new GridLayout (2,1));
    add (enableStatusPanel);
    
    enableStatusPanel.add (JCenter.XY (JCenter.Y (new JBoolean_JBoolean (
      "Frequency Offset",
      (settings) -> ((RS_ESH3_GPIB_Settings) settings).isTestFrequencyOffset (),
      rs_esh3::setTestFrequencyOffset,
      Color.green,
      true))));

    this.jValid = new JColorCheckBox.JBoolean ();
    this.jValid.setDisplayedValue (false);
    this.jValid.setToolTipText ("Que?");
    enableStatusPanel.add (JCenter.XY (JCenter.Y (this.jValid)));

    this.jFrequencyOffset = new JSevenSegmentNumber (JInstrumentPanel.getGuiPreferencesFrequencyColor (), true, 3, 0);
    add (this.jFrequencyOffset);

    add (JCenter.XY (new JLabel ("kHz")));
    
    getSelectiveLevelMeter ().addInstrumentListener (this.instrumentListener);

  }

  public JRS_ESH3_GPIB_SF_Test_FrequencyOffset (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_SF_Test_FrequencyOffset (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
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
      return "R&S ESH-3 [GPIB] Test/Frequency Offset Setting/Reading";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_SF_Test_FrequencyOffset ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_SF_Test_FrequencyOffset.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  
  private final JColorCheckBox.JBoolean jValid;
  
  private final JSevenSegmentNumber jFrequencyOffset;
  
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
      if (instrument != JRS_ESH3_GPIB_SF_Test_FrequencyOffset.this.getSelectiveLevelMeter () || instrumentReading == null)
        throw new IllegalArgumentException ();
      if (! (instrumentReading instanceof RS_ESH3_GPIB_Reading))
        throw new IllegalArgumentException ();
      final RS_ESH3_GPIB_Reading reading = (RS_ESH3_GPIB_Reading) instrumentReading;
      if (reading.getReadingType () != RS_ESH3_GPIB_Reading.ReadingType.FrequencyOffset_kHz)
        return;
      final double frequencyOffset_kHz = reading.getFrequencyOffset_kHz ();
      final boolean valid = ! reading.isError ();
      final String errorString = (valid ? "OK" : reading.getErrorMessage ());
      SwingUtilities.invokeLater (() ->
      {
        setInhibitInstrumentControl ();
        //
        try
        {
          JRS_ESH3_GPIB_SF_Test_FrequencyOffset.this.jValid.setDisplayedValue (valid);
          JRS_ESH3_GPIB_SF_Test_FrequencyOffset.this.jValid.setToolTipText (errorString);
          JRS_ESH3_GPIB_SF_Test_FrequencyOffset.this.jFrequencyOffset.setNumber (frequencyOffset_kHz);
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
    this.jValid.setDisplayedValue (false);
    this.jValid.setToolTipText ("Que?");
    this.jFrequencyOffset.setBlank ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
