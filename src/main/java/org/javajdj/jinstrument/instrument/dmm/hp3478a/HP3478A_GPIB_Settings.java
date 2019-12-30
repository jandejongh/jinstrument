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
package org.javajdj.jinstrument.instrument.dmm.hp3478a;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.instrument.dmm.DefaultDigitalMultiMeterSettings;
import org.javajdj.jinstrument.instrument.dmm.DigitalMultiMeter;
import org.javajdj.jinstrument.instrument.dmm.DigitalMultiMeterSettings;

/** Implementation of {@link InstrumentSettings} and {@link DigitalMultiMeterSettings} for the HP-3478A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class HP3478A_GPIB_Settings
  extends DefaultDigitalMultiMeterSettings
  implements DigitalMultiMeterSettings
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP3478A_GPIB_Settings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private HP3478A_GPIB_Settings (
    final byte[] bytes,
    final DigitalMultiMeter.NumberOfDigits resolution,
    final DigitalMultiMeter.MeasurementMode measurementMode,
    final boolean autoRange,
    final DigitalMultiMeter.Range range)
  {
    super (bytes, resolution, measurementMode, autoRange, range);
  }

  public final static int B_LENGTH = 5;
  
  public final static HP3478A_GPIB_Settings fromB (final byte[] b)
  {
    if (b == null || b.length != HP3478A_GPIB_Settings.B_LENGTH)
    {
      LOG.log (Level.WARNING, "Null B object or unexpected number of bytes read for Settings.");
      throw new IllegalArgumentException ();
    }
    // LOG.log (Level.WARNING, "B bytes = {0}", Util.bytesToHex (b));
    final int resolutionInt = (b[0] & 0x03);
    final DigitalMultiMeter.NumberOfDigits resolution;
    switch (resolutionInt)
    {
      case 0:
        throw new IllegalArgumentException ();
      case 1:
        resolution = DigitalMultiMeter.NumberOfDigits.DIGITS_5_5;
        break;
      case 2:
        resolution = DigitalMultiMeter.NumberOfDigits.DIGITS_4_5;
        break;
      case 3:
        resolution = DigitalMultiMeter.NumberOfDigits.DIGITS_3_5;
        break;
      default:
        throw new RuntimeException ();
    }
    final int measurentModeInt = ((b[0] & 0xe0) >>> 5) & 0x07;
    final DigitalMultiMeter.MeasurementMode measurementMode;
    switch (measurentModeInt)
    {
      case 0: throw new IllegalArgumentException ();
      case 1: measurementMode = DigitalMultiMeter.MeasurementMode.DC_VOLTAGE;    break;
      case 2: measurementMode = DigitalMultiMeter.MeasurementMode.AC_VOLTAGE;    break;
      case 3: measurementMode = DigitalMultiMeter.MeasurementMode.RESISTANCE_2W; break;
      case 4: measurementMode = DigitalMultiMeter.MeasurementMode.RESISTANCE_4W; break;
      case 5: measurementMode = DigitalMultiMeter.MeasurementMode.DC_CURRENT;    break;
      case 6: measurementMode = DigitalMultiMeter.MeasurementMode.AC_CURRENT;    break;
      case 7:
      {
        LOG.log (Level.WARNING, "The EXTENDED OHMS mode on the HP3478A is not supported on this Instrument!");
        measurementMode = DigitalMultiMeter.MeasurementMode.UNSUPPORTED;
        break;
      }
      default:
      {
        LOG.log (Level.WARNING, "The Measurement Mode numbered {0} is not supported (and unexpected) on this Instrument!",
          measurentModeInt);
        throw new IllegalArgumentException ();
      }
    }
    final int rangeInt = ((b[0] & 0x1c) >>> 2) & 0x07;
    if (rangeInt == 0)
    {
      LOG.log (Level.WARNING, "The Range numbered {0} is not supported (and unexpected) on this Instrument!",
        rangeInt);
      throw new IllegalArgumentException ();        
    }
    final List<DigitalMultiMeter.Range> ranges = HP3478A_GPIB_Instrument.SUPPORTED_RANGES_MAP.get (measurementMode);
    final DigitalMultiMeter.Range range;
    switch (measurementMode)
    {
      case DC_VOLTAGE:
        if (rangeInt >= 6)
          throw new IllegalArgumentException ();
        range = ranges.get (rangeInt - 1);
        break;
      case AC_VOLTAGE:
        if (rangeInt >= 5)
          throw new IllegalArgumentException ();
        range = ranges.get (rangeInt - 1);
        break;
      case RESISTANCE_2W:
      case RESISTANCE_4W:
        range = ranges.get (rangeInt - 1);
        break;
      case DC_CURRENT:
      case AC_CURRENT:
        if (rangeInt >= 3)
          throw new IllegalArgumentException ();
        range = ranges.get (rangeInt - 1);
        break;
      default:
        throw new RuntimeException ();
    }
    final boolean autoRange = (b[1] & 0x02) != 0;
    return new HP3478A_GPIB_Settings (
      b,
      resolution,
      measurementMode,
      autoRange,
      range);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
