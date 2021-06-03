/*
 * Copyright 2010-2021 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.gpib.dmm.hp3478a;

import org.javajdj.jinstrument.*;

/** A (specific) command for a {@link HP3478A_GPIB_Instrument} Digital MultiMeter.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface HP3478A_InstrumentCommand
  extends InstrumentCommand
{
  
  public final static String ICARG_HP3478A_CALIBRATION_NIBBLE_ADDRESS = "hp3478aCalibrationNibbleAddress";
  
  public final static String IC_HP3478A_READ_CALIBRATION_NIBBLE = "commandHp3478aReadCalibrationNibble";
  public final static String ICARG_HP3478A_READ_CALIBRATION_NIBBLE_ADDRESS = ICARG_HP3478A_CALIBRATION_NIBBLE_ADDRESS;
  
  public final static String IC_HP3478A_WRITE_CALIBRATION_NIBBLE = "commandHp3478aWriteCalibrationNibble";
  public final static String ICARG_HP3478A_WRITE_CALIBRATION_NIBBLE_ADDRESS = ICARG_HP3478A_CALIBRATION_NIBBLE_ADDRESS;
  public final static String ICARG_HP3478A_WRITE_CALIBRATION_NIBBLE_DATA = "hp3478aWriteCalibrationNibbleData";
  
  public final static String IC_HP3478A_READ_CALIBRATION_DATA = "commandHp3478aReadCalibrationData";
  
  public final static String IC_HP3478A_WRITE_CALIBRATION_DATA = "commandHp3478aWriteCalibrationData";
  public final static String ICARG_HP3478A_WRITE_CALIBRATION_DATA = "hp3478aWriteCalibrationData";
  
}
