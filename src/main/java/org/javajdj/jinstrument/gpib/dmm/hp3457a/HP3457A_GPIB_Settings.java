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

import java.util.logging.Logger;
import org.javajdj.jinstrument.InstrumentSettings;
import org.javajdj.junits.Unit;
import org.javajdj.jinstrument.DefaultDigitalMultiMeterSettings;
import org.javajdj.jinstrument.DigitalMultiMeter;
import org.javajdj.jinstrument.DigitalMultiMeterSettings;
import org.javajdj.junits.Resolution;

/** Implementation of {@link InstrumentSettings} and {@link DigitalMultiMeterSettings} for the HP-3457A.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class HP3457A_GPIB_Settings
  extends DefaultDigitalMultiMeterSettings
  implements DigitalMultiMeterSettings
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (HP3457A_GPIB_Settings.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static Unit getReadingUnit (final DigitalMultiMeter.MeasurementMode measurementMode)
  {
    if (measurementMode == null)
      throw new IllegalArgumentException ();
    switch (measurementMode)
    {
      case DC_VOLTAGE:
      case AC_VOLTAGE:
      case AC_DC_VOLTAGE:
        return Unit.UNIT_V;
      case RESISTANCE_2W:
      case RESISTANCE_4W:
        return Unit.UNIT_Ohm;
      case DC_CURRENT:
      case AC_CURRENT:
      case AC_DC_CURRENT:
        return Unit.UNIT_A;
      case FREQUENCY:
        return Unit.UNIT_Hz;
      case PERIOD:
        return Unit.UNIT_s;
      default:
        throw new IllegalArgumentException ();
    }
  }
  
  private HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      final byte[] bytes,
      final Resolution resolution,
      final DigitalMultiMeter.MeasurementMode measurementMode,
      final boolean autoRange,
      final DigitalMultiMeter.Range range,
      // HP3457A_GBIB_Settings
      final double resolution_percent,
      final AutoZeroMode autoZeroMode,
      final String id,
      final TriggerEvent triggerArmEvent,
      final TriggerEvent triggerEvent,
      final TriggerEvent sampleEvent,
      final InstalledOption installedOption,
      final ACBandwidth acBandwidth,
      final MeasurementTerminals measurementTerminals,
      final boolean fixedImpedance,
      final boolean offsetCompensation,
      final boolean beepEnabled,
      final Byte gpibAddress,
      final DigitalMultiMeter.MeasurementMode frequencyPeriodInputSource,
      final int numberOfTriggerArms,
      final int numberOfReadings,
      final double delay_s,
      final double timer_s,
      final boolean triggerBuffering,
      final int calibrationNumber,
      final ReadingFormat readingFormat,
      final boolean locked,
      final double numberOfPowerLineCycles,
      final double integerScale,
      final ReadingMemoryMode readingMemoryMode,
      final double lineFrequency_Hz,
      final double lineFrequencyReference_Hz,
      final boolean gpibInputBuffering,
      final byte serviceRequestMask,
      final short errorMask,
      final boolean eoi,
      final InputChannel inputChannel,
      final ActuatorChannelDefinition actuatorChannelDefinition,
      final MemorySizesDefinition memorySizesDefinition)
  {
    // DefaultDigitalMultiMeterSettings
    super (bytes, resolution, measurementMode, autoRange, range, getReadingUnit (measurementMode));
    // HP3457A_GBIB_Settings
    this.resolution_percent = resolution_percent;
    this.autoZeroMode = autoZeroMode;
    this.id = id;
    this.triggerArmEvent = triggerArmEvent;
    this.triggerEvent = triggerEvent;
    this.sampleEvent = sampleEvent;
    this.installedOption = installedOption;
    this.acBandwidth = acBandwidth;
    this.measurementTerminals = measurementTerminals;
    this.fixedImpedance = fixedImpedance;
    this.offsetCompensation = offsetCompensation;
    this.beepEnabled = beepEnabled;
    this.gpibAddress = gpibAddress;
    this.frequencyPeriodInputSource = frequencyPeriodInputSource;
    this.numberOfTriggerArms = numberOfTriggerArms;
    this.numberOfReadings = numberOfReadings;
    this.delay_s = delay_s;
    this.timer_s = timer_s;
    this.triggerBuffering = triggerBuffering;
    this.calibrationNumber = calibrationNumber;
    this.readingFormat = readingFormat;
    this.locked = locked;
    this.numberOfPowerLineCycles = numberOfPowerLineCycles;
    this.integerScale = integerScale;
    this.readingMemoryMode = readingMemoryMode;
    this.lineFrequency_Hz = lineFrequency_Hz;
    this.lineFrequencyReference_Hz = lineFrequencyReference_Hz;
    this.gpibInputBuffering = gpibInputBuffering;
    this.serviceRequestMask = serviceRequestMask;
    this.errorMask = errorMask;
    this.eoi = eoi;
    this.inputChannel = inputChannel;
    this.actuatorChannelDefinition = actuatorChannelDefinition;
    this.memorySizesDefinition = memorySizesDefinition;
  }
  
  public static HP3457A_GPIB_Settings fromReset ()
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      Resolution.DIGITS_5_5,
      DigitalMultiMeter.MeasurementMode.DC_VOLTAGE,
      true,
      null,
      // HP3457A_GBIB_Settings
      0.00033, // resolution_percent; 5.5 DIGITS
      AutoZeroMode.Always,
      null,
      TriggerEvent.AUTO,
      TriggerEvent.AUTO,
      TriggerEvent.AUTO,
      null,
      ACBandwidth.SLOW,
      MeasurementTerminals.FRONT,
      false,
      false,
      true, // For PRESET actually last value...
      null, // Unknown...
      DigitalMultiMeter.MeasurementMode.AC_VOLTAGE,
      0,
      1,
      Double.NaN, // XXX delay_s
      1.0,
      false,
      -1, // calibrationNumber: Unknown
      ReadingFormat.ASCII,
      false,
      10,
      Double.NaN, // integerScale [ISCALE?]: Unknown
      ReadingMemoryMode.OFF,
      Double.NaN, // lineFrequency_Hz: Unknown
      Double.NaN, // lineFrequencyReference_Hz: Unknown
      false,
      (byte) 0,     // Service Request Mask (actually, PON is unchanged)
      (short) 2047, // Error Mask: all enabled
      false,        // EOI
      InputChannel.None,
      ActuatorChannelDefinition.fromReset (),
      null); // MemorySizesDefinition
  }
  
  public static HP3457A_GPIB_Settings fromPreset ()
  {
    return fromReset ().withTriggerEvent (TriggerEvent.SYN).withNumberOfPowerLineCycles (1);
  }
  
  public final HP3457A_GPIB_Settings withResolution (final Resolution resolution)
  {
    if (resolution == null || ! HP3457A_GPIB_Instrument.isSupportedResolution (resolution))
      throw new IllegalArgumentException ();
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      resolution,
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withMeasurementMode (final DigitalMultiMeter.MeasurementMode measurementMode)
  {
    if (measurementMode == null || ! HP3457A_GPIB_Instrument.isSupportedMeasurementMode (measurementMode))
      throw new IllegalArgumentException ();
    if (measurementMode == getMeasurementMode ())
      return this;
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      measurementMode,
      true, // AutoRange
      null, // Range
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withAutoRange (final boolean autoRange)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      autoRange,
      null, // Range
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withRange (final DigitalMultiMeter.Range range)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      range == null, // autoRange
      range,         // range
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withResolution_percent (final double resolution_percent)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles, // XXX MUST CHANGE AS WELL??
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withAutoZeroMode (final AutoZeroMode autoZeroMode)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withId (final String id)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (), // Implies AutoRange
      getRange (), // XXX Should be null == AutoRange?
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withTriggerArmEvent (final TriggerEvent triggerArmEvent)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (), // Implies AutoRange
      getRange (), // XXX Should be null == AutoRange?
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withTriggerEvent (final TriggerEvent triggerEvent)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (), // Implies AutoRange
      getRange (), // XXX Should be null == AutoRange?
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withSampleEvent (final TriggerEvent sampleEvent)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (), // Implies AutoRange
      getRange (), // XXX Should be null == AutoRange?
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withInstalledOption (final InstalledOption installedOption)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (), // Implies AutoRange
      getRange (), // XXX Should be null == AutoRange?
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withACBandwidth (final ACBandwidth acBandwidth)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (), // Implies AutoRange
      getRange (), // XXX Should be null == AutoRange?
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withMeasurementTerminals (final MeasurementTerminals measurementTerminals)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (), // Implies AutoRange
      getRange (), // XXX Should be null == AutoRange?
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withFixedImpedance (final boolean fixedImpedance)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withOffsetCompensation (final boolean offsetCompensation)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withBeepEnabled (final boolean beepEnabled)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withGpibAddress (final Byte gpibAddress)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withFrequencyPeriodInputSource (
    final DigitalMultiMeter.MeasurementMode frequencyPeriodInputSource)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withNumberOfTriggerArms (final int numberOfTriggerArms)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withNumberOfReadings (final int numberOfReadings)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withDelay (final double delay_s)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withTimer (final double timer_s)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withTriggerBuffering (final boolean triggerBuffering)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withCalibrationNumber (final int calibrationNumber)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withReadingFormat (final ReadingFormat readingFormat)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withLocked (final boolean locked)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withNumberOfPowerLineCycles (final double numberOfPowerLineCycles)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withIntegerScale (final double integerScale)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withReadingMemoryMode (final ReadingMemoryMode readingMemoryMode)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withLineFrequency_Hz (final double lineFrequency_Hz)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withLineFrequencyReference_Hz (final double lineFrequencyReference_Hz)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withGpibInputBuffering (final boolean gpibInputBuffering)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withServiceRequestMask (final byte serviceRequestMask)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withErrorMask (final short errorMask)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withEoi (final boolean eoi)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withInputChannel (final InputChannel inputChannel)
  {
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      inputChannel,
      this.actuatorChannelDefinition,
      this.memorySizesDefinition);
  }
  
  public final HP3457A_GPIB_Settings withActuatorChannelDefinition (final ActuatorChannelDefinition actuatorChannelDefinition)
  {
    if (actuatorChannelDefinition == null)
      throw new IllegalArgumentException ();
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      actuatorChannelDefinition,
      this.memorySizesDefinition);
  
  }
  
  public final HP3457A_GPIB_Settings withMemorySizesDefinition (final MemorySizesDefinition memorySizesDefinition)
  {
    if (actuatorChannelDefinition == null)
      throw new IllegalArgumentException ();
    return new HP3457A_GPIB_Settings (
      // DefaultDigitalMultiMeterSettings
      null,
      getResolution (),
      getMeasurementMode (),
      isAutoRange (),
      getRange (),
      // HP3457A_GBIB_Settings
      this.resolution_percent,
      this.autoZeroMode,
      this.id,
      this.triggerArmEvent,
      this.triggerEvent,
      this.sampleEvent,
      this.installedOption,
      this.acBandwidth,
      this.measurementTerminals,
      this.fixedImpedance,
      this.offsetCompensation,
      this.beepEnabled,
      this.gpibAddress,
      this.frequencyPeriodInputSource,
      this.numberOfTriggerArms,
      this.numberOfReadings,
      this.delay_s,
      this.timer_s,
      this.triggerBuffering,
      this.calibrationNumber,
      this.readingFormat,
      this.locked,
      this.numberOfPowerLineCycles,
      this.integerScale,
      this.readingMemoryMode,
      this.lineFrequency_Hz,
      this.lineFrequencyReference_Hz,
      this.gpibInputBuffering,
      this.serviceRequestMask,
      this.errorMask,
      this.eoi,
      this.inputChannel,
      this.actuatorChannelDefinition,
      memorySizesDefinition);
  
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EQUALS / HASHCODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  // XXX
  @Override
  public final boolean equals (final Object obj)
  {
    return super.equals (obj);
  }

  // XXX
  @Override
  public final int hashCode ()
  {
    return super.hashCode ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AUTO CALIBRATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum AutoCalibrationType
  {
    All,
    AC,
    Ohms;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AC BANDWIDTH
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum ACBandwidth
  {
    SLOW,
    FAST;
  }
  
  private final ACBandwidth acBandwidth;
  
  public final ACBandwidth getACBandwidth ()
  {
    return this.acBandwidth;  
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GPIB ADDRESS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Byte gpibAddress;

  public final Byte getGpibAddress ()
  {
    return this.gpibAddress;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // AUTOZERO MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum AutoZeroMode
  {
    FunctionOrRangeChanges,
    Always;
  }
  
  private final AutoZeroMode autoZeroMode;

  public final AutoZeroMode getAutoZeroMode ()
  {
    return this.autoZeroMode;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ID
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final String id;
  
  public final String getId ()
  {
    return this.id;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BEEP MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean beepEnabled;
  
  public final boolean isBeepEnabled ()
  {
    return this.beepEnabled;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERVICE REQUEST MASK
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final byte serviceRequestMask;
  
  public final byte getServiceRequestMask ()
  {
    return this.serviceRequestMask;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ERROR MASK
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final short errorMask;
  
  public final short getErrorMask ()
  {
    return this.errorMask;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INPUT CHANNEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum InputChannel
  {
    
    None      (-1),
    Channel0  ( 0),
    Channel1  ( 1),
    Channel2  ( 2),
    Channel3  ( 3),
    Channel4  ( 4),
    Channel5  ( 5),
    Channel6  ( 6),
    Channel7  ( 7),
    Channel8  ( 8),
    Channel9  ( 9),
    Channel10 (10),
    Channel11 (11),
    Channel12 (12),
    Channel13 (13);
    
    private InputChannel (final int code)
    {
      this.code = code;
    }
    
    private final int code;
    
    public final int getCode ()
    {
      return this.code;
    }
    
    public static final InputChannel fromCode (final int code)
    {
      switch (code)
      {
        case -1: return None;
        case  0: return Channel0;
        case  1: return Channel1;
        case  2: return Channel2;
        case  3: return Channel3;
        case  4: return Channel4;
        case  5: return Channel5;
        case  6: return Channel6;
        case  7: return Channel7;
        case  8: return Channel8;
        case  9: return Channel9;
        case 10: return Channel10;
        case 11: return Channel11;
        case 12: return Channel12;
        case 13: return Channel13;
        default: throw new IllegalArgumentException ();
      }
    }
    
  }
  
  private final InputChannel inputChannel;
  
  public final InputChannel getInputChannel ()
  {
    return this.inputChannel;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ACTUATOR CHANNELS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static class ActuatorChannelDefinition
  {
    
    private ActuatorChannelDefinition (
      final boolean openChannel8,
      final boolean switchDelayChannel8,
      final boolean openChannel9,
      final boolean switchDelayChannel9)
    {
      this.openChannel8 = openChannel8;
      this.switchDelayChannel8 = switchDelayChannel8;
      this.openChannel9 = openChannel9;
      this.switchDelayChannel9 = switchDelayChannel9;
    }
    
    private final static ActuatorChannelDefinition FROM_RESET = new ActuatorChannelDefinition (true, false, true, false);
    
    public final static ActuatorChannelDefinition fromReset ()
    {
      return ActuatorChannelDefinition.FROM_RESET;
    }
    
    private final boolean openChannel8;

    public final boolean isOpenChannel8 ()
    {
      return this.openChannel8;
    }

    private final boolean switchDelayChannel8;
    
    public final boolean isSwitchDelayChannel8 ()
    {
      return this.switchDelayChannel8;
    }

    private final boolean openChannel9;
    
    public final boolean isOpenChannel9 ()
    {
      return this.openChannel9;
    }

    private final boolean switchDelayChannel9;
    
    public final boolean isSwitchDelayChannel9 ()
    {
      return this.switchDelayChannel9;
    }
    
  }
  
  private final ActuatorChannelDefinition actuatorChannelDefinition;
  
  public final ActuatorChannelDefinition getActuatorChannelDefinition ()
  {
    return this.actuatorChannelDefinition;
  }

  public final boolean isOpenChannel8 ()
  {
    return this.actuatorChannelDefinition.openChannel8;
  }
  
  public final HP3457A_GPIB_Settings withOpenChannel8 (final boolean openChannel8)
  {
    if (this.actuatorChannelDefinition.openChannel8 != openChannel8)
      return withActuatorChannelDefinition (new ActuatorChannelDefinition (
        openChannel8,
        this.actuatorChannelDefinition.switchDelayChannel8,
        this.actuatorChannelDefinition.openChannel9,
        this.actuatorChannelDefinition.switchDelayChannel9));
    else
      return this;
  }
  
  public final boolean isSwitchDelayChannel8 ()
  {
    return this.actuatorChannelDefinition.switchDelayChannel8;
  }
  
  public final HP3457A_GPIB_Settings withSwitchDelayChannel8 (final boolean switchDelayChannel8)
  {
    if (this.actuatorChannelDefinition.switchDelayChannel8 != switchDelayChannel8)
      return withActuatorChannelDefinition (new ActuatorChannelDefinition (
        this.actuatorChannelDefinition.openChannel8,
        switchDelayChannel8,
        this.actuatorChannelDefinition.openChannel9,
        this.actuatorChannelDefinition.switchDelayChannel9));
    else
      return this;
  }
  
  public final boolean isOpenChannel9 ()
  {
    return this.actuatorChannelDefinition.openChannel9;
  }
  
  public final HP3457A_GPIB_Settings withOpenChannel9 (final boolean openChannel9)
  {
    if (this.actuatorChannelDefinition.openChannel8 != openChannel9)
      return withActuatorChannelDefinition (new ActuatorChannelDefinition (
        this.actuatorChannelDefinition.openChannel8,
        this.actuatorChannelDefinition.switchDelayChannel8,
        openChannel9,
        this.actuatorChannelDefinition.switchDelayChannel9));
    else
      return this;
  }
  
  public final boolean isSwitchDelayChannel9 ()
  {
    return this.actuatorChannelDefinition.switchDelayChannel9;
  }
  
  public final HP3457A_GPIB_Settings withSwitchDelayChannel9 (final boolean switchDelayChannel9)
  {
    if (this.actuatorChannelDefinition.switchDelayChannel9 != switchDelayChannel9)
      return withActuatorChannelDefinition (new ActuatorChannelDefinition (
        this.actuatorChannelDefinition.openChannel8,
        this.actuatorChannelDefinition.switchDelayChannel8,
        this.actuatorChannelDefinition.openChannel9,
        switchDelayChannel9));
    else
      return this;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DELAY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double delay_s;
  
  public final double getDelay_s ()
  {
    return this.delay_s;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum DisplayControl
  {
    Off,
    On,
    Message;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EOI
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean eoi;
  
  public final boolean isEoi ()
  {
    return this.eoi;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FIXED IMPEDANCE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean fixedImpedance;
  
  public final boolean isFixedImpedance ()
  {
    return this.fixedImpedance;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // GPIB INPUT BUFFERING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean gpibInputBuffering;
  
  public final boolean isGpibInputBuffering ()
  {
    return this.gpibInputBuffering;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // OFFSET COMPENSATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final boolean offsetCompensation;
  
  public final boolean isOffsetCompensation ()
  {
    return this.offsetCompensation;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // [MEASURED] LINE FREQUENCY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double lineFrequency_Hz;
  
  public final double getLineFrequency_Hz ()
  {
    return this.lineFrequency_Hz;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LINE FREQUENCY REFERENCE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final double lineFrequencyReference_Hz;
  
  public final double getLineFrequencyReference_Hz ()
  {
    return this.lineFrequencyReference_Hz;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRIGGERING AND SAMPLING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum TriggerEvent
  {
    
    AUTO  (1),
    EXT   (2),
    SGL   (3),
    HOLD  (4),
    SYN   (5),
    TIMER (6);
    
    private TriggerEvent (final int code)
    {
      this.code = code;
    }
    
    private final int code;
    
    public final int getCode ()
    {
      return this.code;
    }
    
    public static final TriggerEvent fromCode (final int code)
    {
      switch (code)
      {
        case 1:  return AUTO;
        case 2:  return EXT;
        case 3:  return SGL;
        case 4:  return HOLD;
        case 5:  return SYN;
        case 6:  return TIMER;
        default: throw new IllegalArgumentException ();
      }
    }
    
  }
  
  
  public final static class TriggerDefinition
  {
    
    public TriggerDefinition (final int count, final TriggerEvent triggerEvent)
    {
      this.count = count;
      this.triggerEvent = triggerEvent;
    }
    
    private final int count;
    
    public final int getCount ()
    {
      return this.count;
    }
    
    private final TriggerEvent triggerEvent;
    
    public final TriggerEvent getTriggerEvent ()
    {
      return this.triggerEvent;
    }
    
  }
  
  private final int numberOfTriggerArms;
  
  public final int getNumberOfTriggerArms ()
  {
    return this.numberOfTriggerArms;
  }
  
  private final int numberOfReadings;
  
  public final int getNumberOfReadings ()
  {
    return this.numberOfReadings;
  }
  
  private final double timer_s;
  
  public final double getTimer_s ()
  {
    return this.timer_s;
  }
  
  final TriggerEvent triggerArmEvent;
  
  public final TriggerEvent getTriggerArmEvent ()
  {
    return this.triggerArmEvent;
  }
  
  final TriggerEvent triggerEvent;
  
  public final TriggerEvent getTriggerEvent ()
  {
    return this.triggerEvent;
  }
  
  final TriggerEvent sampleEvent;
  
  public final TriggerEvent getSampleEvent ()
  {
    return this.sampleEvent;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTALLED OPTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum InstalledOption
  {
    NONE,     // 0
    HP_44491, // 44491
    HP_44492; // 44492
  }
  
  private final InstalledOption installedOption;
  
  public final InstalledOption getInstalledOption ()
  {
    return this.installedOption;
  }
  
  public final boolean isInstalledOption44491 ()
  {
    return this.installedOption == InstalledOption.HP_44491;
  }
  
  public final boolean isInstalledOption44492 ()
  {
    return this.installedOption == InstalledOption.HP_44492;    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MATH
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public enum MathOperation
  {
    
    OFF    (0),
    CONT   (1),
    CTHRM  (3),
    DB     (4),
    DBM    (5),
    FILTER (6),
    FTHRM  (8),
    NULL   (9),
    PERC  (10),
    PFAIL (11),
    RMS   (12),
    SCALE (13),
    STAT  (14);
    
    private MathOperation (final int code)
    {
      this.code = code;
    }
    
    public static MathOperation fromCode (final int code)
    {
      switch (code)
      {
        case  0: return OFF;
        case  1: return CONT;
        case  3: return CTHRM;
        case  4: return DB;
        case  5: return DBM;
        case  6: return FILTER;
        case  8: return FTHRM;
        case  9: return NULL;
        case 10: return PERC;
        case 11: return PFAIL;
        case 12: return RMS;
        case 13: return SCALE;
        case 14: return STAT;
        default: throw new IllegalArgumentException ();
      }
    }
    
    private final int code;
    
    public final int getCode ()
    {
      return this.code;
    }
        
  }

  public final static class MathOperationDefinition
  {

    public MathOperationDefinition (final MathOperation operationA, final MathOperation operationB)
    {
      this.operationA = operationA;
      this.operationB = operationB;
    }
        
    private final MathOperation operationA;

    public final MathOperation getOperationA ()
    {
      return this.operationA;
    }

    private final MathOperation operationB;
    
    public final MathOperation getOperationB ()
    {
      return this.operationB;
    }
    
  }
  
  public enum MathRegister
  {
    
    DEGREE (1),
    LOWER  (2),
    MAX    (3),
    MEAN   (4),
    MIN    (5),
    NSAMP  (6),
    OFFSET (7),
    PERC   (8),
    REF    (9),
    RES   (10),
    SCALE (11),
    SDEV  (12),
    UPPER (13),
    HIRES (14);
    
    private MathRegister (final int code)
    {
      this.code = code;
    }
    
    private final int code;
    
    public final int getCode ()
    {
      return this.code;
    }
    
    public static final MathRegister fromCode (final int code)
    {
      switch (code)
      {
        case 1:  return DEGREE;
        case 2:  return LOWER;
        case 3:  return MAX;
        case 4:  return MEAN;
        case 5:  return MIN;
        case 6:  return NSAMP;
        case 7:  return OFFSET;
        case 8:  return PERC;
        case 9:  return REF;
        case 10: return RES;
        case 11: return SCALE;
        case 12: return SDEV;
        case 13: return UPPER;
        case 14: return HIRES;
        default: throw new IllegalArgumentException ();
      }
    }

  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READING MEMORY MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum ReadingMemoryMode
  {
    
    OFF   (0),
    LIFO  (1),
    FIFO  (2),
    CONT  (3);
    
    private ReadingMemoryMode (final int code)
    {
      this.code = code;
    }
    
    private final int code;
    
    public final int getCode ()
    {
      return this.code;
    }
    
    public static final ReadingMemoryMode fromCode (final int code)
    {
      switch (code)
      {
        case 0:  return OFF;
        case 1:  return LIFO;
        case 2:  return FIFO;
        case 3:  return CONT;
        default: throw new IllegalArgumentException ();
      }
    }
    
  }
  
  private final ReadingMemoryMode readingMemoryMode;
  
  public final ReadingMemoryMode getReadingMemoryMode ()
  {
    return this.readingMemoryMode;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MEMORY SIZES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static class MemorySizesDefinition
  {

    /** The total memory size (readings, subprograms, states) of the HP-3457.
     * 
     * <p>
     * HP 3457 Operating Manual, pp 4-83...4-85.
     * 
     */
    public final static int TOTAL_MEMORY_SIZE_BYTES = 2208;
    
    /** The memory size of a state slot of the HP-3457.
     * 
     * <p>
     * HP 3457 Operating Manual, pp 4-84.
     * 
     */
    public final static int STATE_MEMORY_SIZE_BYTES = 69;
    
    public MemorySizesDefinition (final int readings_bytes, final int subprograms_bytes)
    {
      // Argument checking; result is not used.
      MemorySizesDefinition.getMaxStatesMemorySize_bytes (readings_bytes, subprograms_bytes);
      this.readings_bytes = readings_bytes;
      this.subprograms_bytes = subprograms_bytes;
    }
    
    public final static int getTotalMemorySize_bytes ()
    {
      return MemorySizesDefinition.TOTAL_MEMORY_SIZE_BYTES;
    }
    
    private final int readings_bytes;
    
    public final int getMaxReadingsMemorySize_bytes ()
    {
      return this.readings_bytes;
    }
    
    private final int subprograms_bytes;
    
    public final int getMaxSubprogramsMemorySize_bytes ()
    {
      return this.subprograms_bytes;
    }

    public final static int getMaxStatesMemorySize_bytes (final int readings_bytes, final int subprograms_bytes)
    {
      // HP 3457 Operating Manual, pp 4-83.
      if (readings_bytes < 32
        || subprograms_bytes < 32
        || MemorySizesDefinition.TOTAL_MEMORY_SIZE_BYTES - readings_bytes - subprograms_bytes
             < MemorySizesDefinition.STATE_MEMORY_SIZE_BYTES)
        throw new IllegalArgumentException ();
      return MemorySizesDefinition.TOTAL_MEMORY_SIZE_BYTES - readings_bytes - subprograms_bytes;
    }
    
    public final int getMaxStatesMemorySize_bytes ()
    {
      return MemorySizesDefinition.getMaxStatesMemorySize_bytes (this.readings_bytes, this.subprograms_bytes);
    }
    
    @Override
    public final int hashCode ()
    {
      int hash = 7;
      hash = 23 * hash + this.readings_bytes;
      hash = 23 * hash + this.subprograms_bytes;
      return hash;
    }

    @Override
    public final boolean equals (Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null)
      {
        return false;
      }
      if (getClass () != obj.getClass ())
      {
        return false;
      }
      final MemorySizesDefinition other = (MemorySizesDefinition) obj;
      if (this.readings_bytes != other.readings_bytes)
      {
        return false;
      }
      return this.subprograms_bytes == other.subprograms_bytes;
    }
    
  }
  
  private final MemorySizesDefinition memorySizesDefinition;
  
  public final MemorySizesDefinition getMemorySizesDefinition ()
  {
    return this.memorySizesDefinition;
  }
  
  public final int getMaxReadings_ASCII ()
  {
    return this.memorySizesDefinition != null
      ? (int) Math.floor (((double) getMaxReadingsMemorySize_bytes ()) / 16 /* XXX Make a constant */)
      : -1;        
  }
  
  public final int getMaxReadings_SINT ()
  {
    return this.memorySizesDefinition != null
      ? (int) Math.floor (((double) getMaxReadingsMemorySize_bytes ()) / 2 /* XXX Make a constant */)
      : -1;        
  }
  
  public final int getMaxReadings_DINT ()
  {
    return this.memorySizesDefinition != null
      ? (int) Math.floor (((double) getMaxReadingsMemorySize_bytes ()) / 4 /* XXX Make a constant */)
      : -1;        
  }
  
  public final int getMaxReadings_SREAL ()
  {
    return this.memorySizesDefinition != null
      ? (int) Math.floor (((double) getMaxReadingsMemorySize_bytes ()) / 4 /* XXX Make a constant */)
      : -1;        
  }
  
  public final int getMaxReadingsMemorySize_bytes ()
  {
    return this.memorySizesDefinition != null ? this.memorySizesDefinition.getMaxReadingsMemorySize_bytes () : -1;
  }
  
  public final int getMaxSubprogramsMemorySize_bytes ()
  {
    return this.memorySizesDefinition != null ? this.memorySizesDefinition.getMaxSubprogramsMemorySize_bytes () : -1;    
  }
  
  public final int getMaxStatesMemorySize_bytes ()
  {
    return this.memorySizesDefinition != null ? this.memorySizesDefinition.getMaxStatesMemorySize_bytes () : -1;        
  }
  
  public final int getMaxStates ()
  {
    return this.memorySizesDefinition != null
      ? (int) Math.floor (((double) getMaxStatesMemorySize_bytes ()) / MemorySizesDefinition.STATE_MEMORY_SIZE_BYTES)
      : -1;        
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SCAN ADVANCE MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum ScanAdvanceMode
  {
    
    HOLD (0),
    SGL  (1),
    AUTO (2);
    
    private ScanAdvanceMode (final int code)
    {
      this.code = code;
    }
    
    private final int code;
    
    public final int getCode ()
    {
      return this.code;
    }
    
    public static final ScanAdvanceMode fromCode (final int code)
    {
      switch (code)
      {
        case 0:  return HOLD;
        case 1:  return SGL;
        case 2:  return AUTO;
        default: throw new IllegalArgumentException ();
      }
    }

  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NUMBER OF POWER LINE CYCLES
  // SAMPLE TIME
  // RESOLUTION [PERCENT]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double numberOfPowerLineCycles;
  
  public final double getNumberOfPowerLineCycles ()
  {
    return this.numberOfPowerLineCycles;
  }
  
  public final double getSampleTime_s ()
  {
    return this.numberOfPowerLineCycles / this.lineFrequencyReference_Hz;
  }
  
  public final double getLineRejection_dB ()
  {
    if (this.numberOfPowerLineCycles < 1)   return 0;
    if (this.numberOfPowerLineCycles < 10)  return 60;
    if (this.numberOfPowerLineCycles < 100) return 80;
                                            return 90;
  }
  
  private final double resolution_percent;
  
  public final double getResolution_percent ()
  {
    return this.resolution_percent;
  }
  
  public final double getResolution_ppm ()
  {
    return this.resolution_percent * 1e4;
  }
  
  /** Calculates the resolution in percent, given the number of power-line cycles.
   * 
   * <p>
   * From HP 3457A Operation Manual, page 3-5.
   * 
   * @param numberOfPowerLineCycles The number of power-line cycles.
   * 
   * @return The resolution in percent.
   * 
   * @throws IllegalArgumentException If the argument is zero or negative or {@code > 100}.
   * 
   */
  public static double numberOfPowerLineCycles2Resolution_percent (final double numberOfPowerLineCycles)
  {
    if (numberOfPowerLineCycles <= 0 || numberOfPowerLineCycles > 100)
      throw new IllegalArgumentException ();
    if (numberOfPowerLineCycles <  0.005) return 0.033;
    if (numberOfPowerLineCycles <  0.1  ) return 0.0033;
    if (numberOfPowerLineCycles <  1    ) return 0.00033;
    if (numberOfPowerLineCycles <  10   ) return 0.000033;
    if (numberOfPowerLineCycles <= 100  ) return 0.000033; // XXX not sure here...
    // Not needed, but satisfies compiler...
    throw new IllegalArgumentException ();
  }
  
  /** Calculates the resolution in digits, given the number of power-line cycles.
   * 
   * <p>
   * From HP 3457A Operation Manual, page 3-5.
   * 
   * @param numberOfPowerLineCycles The number of power-line cycles.
   * 
   * @return The resolution in digits.
   * 
   * @throws IllegalArgumentException If the argument is zero or negative or {@code > 100}.
   * 
   */
  public static Resolution numberOfPowerLineCycles2Resolution (final double numberOfPowerLineCycles)
  {
    if (numberOfPowerLineCycles <= 0 || numberOfPowerLineCycles > 100)
      throw new IllegalArgumentException ();
    if (numberOfPowerLineCycles <  0.005) return Resolution.DIGITS_3_5;
    if (numberOfPowerLineCycles <  0.1  ) return Resolution.DIGITS_4_5;
    if (numberOfPowerLineCycles <  1    ) return Resolution.DIGITS_5_5;
    if (numberOfPowerLineCycles <  10   ) return Resolution.DIGITS_6_5;
    if (numberOfPowerLineCycles <= 100  ) return Resolution.DIGITS_6_5; // XXX not sure here...
    // Not needed, but satisfies compiler...
    throw new IllegalArgumentException ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOCKED / LOCAL LOCKOUT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean locked;
  
  public final boolean isLocked ()
  {
    return this.locked;
  }
  
  public final boolean isInstrumentKeyboardLocked ()
  {
    return isLocked ();
  }
  
  public final boolean isLocalLockout ()
  {
    return isLocked ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // READING FORMAT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum ReadingFormat
  {
    
    ASCII (1),
    SINT  (2),
    DINT  (3),
    SREAL (4);
    
    private ReadingFormat (final int code)
    {
      this.code = code;
    }
    
    private final int code;
    
    public final int getCode ()
    {
      return this.code;
    }
    
    public static final ReadingFormat fromCode (final int code)
    {
      switch (code)
      {
        case 1:  return ASCII;
        case 2:  return SINT;
        case 3:  return DINT;
        case 4:  return SREAL;
        default: throw new IllegalArgumentException ();
      }
    }
    
  }
  
  private final ReadingFormat readingFormat;
  
  public final ReadingFormat getReadingFormat ()
  {
    return this.readingFormat;
  }
  
  public final ReadingFormat getOutputFormat ()
  {
    return getReadingFormat ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INTEGER SCALE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double integerScale;
  
  public final double getIntegerScale ()
  {
    return this.integerScale;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CALIBRATION NUMBER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final int calibrationNumber;
  
  public final int getCalibrationNumber ()
  {
    return this.calibrationNumber;
  }
  
  public final String getCalibrationNumberString ()
  {
    return Integer.toString (this.calibrationNumber);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRIGGER BUFFERING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean triggerBuffering;
  
  public final boolean isTriggerBuffering ()
  {
    return this.triggerBuffering;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // FREQUENCY/PERIOD INPUT SOURCE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final DigitalMultiMeter.MeasurementMode frequencyPeriodInputSource;

  public final DigitalMultiMeter.MeasurementMode getFrequencyPeriodInputSource ()
  {
    return this.frequencyPeriodInputSource;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MEASUREMENT TERMINALS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum MeasurementTerminals
  {
    
    OPEN    (0),
    FRONT   (1),
    REAR    (2),
    SCANNER (2); // == REAR!!
    
    private MeasurementTerminals (final int code)
    {
      this.code = code;
    }
    
    private final int code;
    
    public final int getCode ()
    {
      return this.code;
    }
    
    public static final MeasurementTerminals fromCode (final int code)
    {
      switch (code)
      {
        case 0:  return OPEN;
        case 1:  return FRONT;
        case 2:  return REAR;
        default: throw new IllegalArgumentException ();
      }
    }

  }
  
  private final MeasurementTerminals measurementTerminals;

  public final MeasurementTerminals getMeasurementTerminals ()
  {
    return this.measurementTerminals;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
