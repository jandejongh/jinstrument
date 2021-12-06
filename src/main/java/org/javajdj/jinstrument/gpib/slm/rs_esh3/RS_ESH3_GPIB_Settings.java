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
package org.javajdj.jinstrument.gpib.slm.rs_esh3;

import java.util.logging.Logger;
import org.javajdj.jinstrument.DefaultSelectiveLevelMeterSettings;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.jinstrument.SelectiveLevelMeterSettings;
import org.javajdj.junits.Unit;

/** Implementation of {@link InstrumentSettings}
 *  and {@link SelectiveLevelMeterSettings} for the Rohde{@code &}Schwarz ESH-3.
 *
 * <p>
 * Implementation is <i>not</i> (even close to) complete.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class RS_ESH3_GPIB_Settings
  extends DefaultSelectiveLevelMeterSettings
  implements SelectiveLevelMeterSettings
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (RS_ESH3_GPIB_Settings.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected RS_ESH3_GPIB_Settings (
    final byte[] bytes,
    final Unit readingUnit)
  {
    super (bytes, readingUnit);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEMODULATION SETTINGS
  // [INCOMPLETE]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static enum DemodulationMode
  {
    OFF,
    A0,
    A1,
    A3,
    A3J_LSB,
    A3J_USB,
    F3
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
