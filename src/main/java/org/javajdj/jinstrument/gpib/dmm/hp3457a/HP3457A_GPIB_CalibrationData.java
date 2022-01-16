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
package org.javajdj.jinstrument.gpib.dmm.hp3457a;

import org.javajdj.jinstrument.AbstractInstrumentCalibrationData;
import org.javajdj.jinstrument.InstrumentCalibrationData;

/** Calibration Data for a {@link HP3457A_GPIB_Instrument} Digital MultiMeter.
 * 
 * <p>
 * Source credits: {@literal https://www.eevblog.com/forum/metrology/dumping-cal-ram-of-a-hp3457a/}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class HP3457A_GPIB_CalibrationData
  extends AbstractInstrumentCalibrationData
  implements InstrumentCalibrationData
{

  public final static int SIZE = 448; // 0x040 - 0x1ff
  
  private static byte[] checkBytes (final byte[] bytes)
  {
    if (bytes == null || bytes.length != HP3457A_GPIB_CalibrationData.SIZE)
      throw new IllegalArgumentException ();
    return bytes;
  }
  
  public HP3457A_GPIB_CalibrationData (final byte[] bytes)
  {
    super (HP3457A_GPIB_CalibrationData.checkBytes (bytes));
  }
  
}
