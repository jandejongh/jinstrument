# jinstrument
Java Swing application for (GPIB) reading and controlling (old-school) lab instruments
like spectrum analyzers, signal generator, digital multimeters, etc.

## Build Type
Maven, but you'll need to manually install dependency artefacts (all available from Maven Central or from my github).

## Installation
Either through Maven, or by simply picking up a release's jinstrument-X.Y-jar-with-dependencies.jar file.
A working example ~/.jinstrument/instrumentRegistry.json (holding persistent definitions of devices, instruments and views)
is there as well.

## License
Apache v2.0.

## Status/Log [20220228 - v0.9-SNAPSHOT]

Most important changes:
- HP-70000 Spectrum Analyzer [progress alpha -> beta].
  - Implemented all SRCxxx [tracking generator] commands.

## Status/Log [20220213 - v0.8]

Most important changes:
- HP-3325B Function Generator -> beta.
- HP-3457A Digital MultiMeter -> beta.
- Configuration Framework.

New instruments are the HP-3457A Digital MultiMeter and
  the HP-3325B Function Generator, more or less completing
  support for my personally-owned DC/LF instruments
  (except for the HP-54502A Digitizing Oscilloscope
  and the HP-6050A DC Load Mainframe).
Work on these instruments is more or less finished by now.

### Beta [Target met for V1.0 Release]:
- HP-3325B Function Generator [complete except arbitrary waveform and memory functions]
- HP-3457A Digital Multimeter [almost complete]
- HP-3478A Digital MultiMeter [complete]
- HP-5316A Frequency Counter [almost complete; could use GUI rework]
- HP-6033A Power Supply Unit / System Power Supply [almost complete]
- HP-8116A 50MHz Programmable Pulse/Function Generator [complete - OPTION001 ONLY!]
- ProLogix GPIB Ethernet [complete]
- R&S ESH-3 Selective Level Meter [almost complete]
- Tek-2440 Digital Storage Oscilloscope [almost complete]
- Configuration Framework

### Alpha [Target: -> Beta for V1.0 Release]:
- HP-8566B Spectrum Analyzer
- HP-8663A Synthesized Signal Generator
- HP-70000 Spectrum Analyzer [MMS]

### Pre-Alpha [Target: -> Beta for V1.0 Release]:
- User Manual
- Instrument Tuning Framework
- Plugin Framework
- HP-3586A Selective Level Meter
- HP-5328A [Option 011] Frequency Counter
- HP-6050A Electronic Load Mainframe [DC Load]
- HP-8156A Optical Attenuator
- HP-8350 Sweep Oscillator
- HP-54502A Digitizing Oscilloscope
- Wiltron 560A Network Analyzer

## Release History

### Release 0.7 [20220107]
Most important changes:
- HP-5316A Frequency Counter -> beta.
- R&S ESH-3 Selective Level Meter -> beta.
- Tek-2440 Digital Storage Oscilloscope -> beta.

Many other changes (almost two years since previous release)
with respect to the jinstrument framework, instrument implementations and GUI components;
see the git log for details.

### Release 0.6 [20200112]
Most important change:
- Reinstated HP-70000 [SpectrumAnalyzer ONLY!] -> alpha.
- Completed (almost) Instrument and InstrumentView implementation for HP-8663-A -> beta.

More detailed documentation will follow once the framework has stabilized (notably, the support for channels is still lacking).

### Release 0.5 [20200106]
Most important change:
- Completed Instrument and InstrumentView implementation for HP-8116A.

### Release 0.4 [20200104]
Most important changes:
- Timeout handling.
- Added many instrument classes and concrete (placeholder) classes.
- HP-8116A [partial] enters beta.
- Improvements to (notably) Controller Debugger.

### Release 0.3 [20191231]
Most important changes:
- Selecting and removing Instrument Views from the Registry.
- Full-blown HP3478A support (only lacking a few trigger options).

### Release 0.2 [20191228]
Most important changes:
- Revamped ProLogix controller implementation.
- Added GPIB Controller logging + Swing component.

### Release 0.1 [20191226]
Very basis support for
- Prologix GPIB-Ethernet
- HP-3478A DMM [well, no real support actually]
- HP-8566B SA [basic settings & trace display]
- HP-8663A RF SigGen [basic settings; still lots of trouble reading settings from instrument]
- A basic GPIB-device console [works OK; very useful]

Reasonable framework for buses, controllers, devices, instruments and instrument views.
