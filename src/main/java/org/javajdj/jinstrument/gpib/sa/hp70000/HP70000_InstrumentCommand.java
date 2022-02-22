/*
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>, TNO.
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
package org.javajdj.jinstrument.gpib.sa.hp70000;

import org.javajdj.jinstrument.InstrumentCommand;

/** A (specific) command for a {@link HP70000_GPIB_Instrument} Spectrum Analyzer.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface HP70000_InstrumentCommand
  extends InstrumentCommand
{
  
  // ABORT
  public final static String IC_HP70000_ABORT = "commandHp70000Abort";
  
  // CONFIG?
  public final static String IC_HP70000_GET_CONFIGURATION_STRING = "commandHp70000GetConfigurationString";
  
  // ID?
  public final static String IC_HP70000_GET_ID = "commandHp70000GetId";
  
  // IDN?
  public final static String IC_HP70000_GET_IDENTIFICATION_NUMBER = "commandHp70000GetIdentificationNumber";
  
  // MEASURE?
  public final static String IC_HP70000_GET_MEASURE_MODE = "commandHp70000GetMeasureMode";
  
  // MEASURE
  public final static String IC_HP70000_SET_MEASURE_MODE = "commandHp70000SetMeasureMode";
  public final static String ICARG_HP70000_SET_MEASURE_MODE = "hp70000SetMeasureMode";
  
  // SRCPWR?
  public final static String IC_HP70000_GET_SOURCE_POWER = "commandHp70000GetSourcePower";
  
  // SRCPWR
  public final static String IC_HP70000_SET_SOURCE_POWER = "commandHp70000SetSourcePower";
  public final static String ICARG_HP70000_SET_SOURCE_POWER = "hp70000SetSourcePower";
  public final static String IC_HP70000_SET_SOURCE_ACTIVE = "commandHp70000SetSourceActive";
  public final static String ICARG_HP70000_SET_SOURCE_ACTIVE = "hp70000SetSourceActive";
  
  // SRCALC?
  public final static String IC_HP70000_GET_SOURCE_ALC_MODE = "commandHp70000GetSourceAlcMode";
  
  // SRCALC
  public final static String IC_HP70000_SET_SOURCE_ALC_MODE = "commandHp70000SetSourceAlcMode";
  public final static String ICARG_HP70000_SET_SOURCE_ALC_MODE = "hp70000SetSourceAlcMode";
  
}
