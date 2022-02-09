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

## Status [20220209 - v0.8-SNAPSHOT]

Just to let you know that a V0.8 release is in the making.
New instruments will be the HP-3457A Digital MultiMeter and
  the HP-3325B Function Generator, more or less completing
  support for my personally-owned DC/LF instruments
  (except for the HP-54502A Digitizing Oscilloscope
  and the HP-6050A DC Load Mainframe).

What I'm currently working on is GUI configuration and documentation.
I really want to accomplish a much easier interface
  to opening supported instruments through (their) default views.
I also intend to complete first-order documentation for V0.8.

I'm currently aiming at a release data of April 1st, 2022.
After that, I will or intend to make sure the project
  gets more interest, through announcements on forums
  and releases (including dependencies) on Maven Central.

I will then also think about and decide upon
  the (modified) target for a V1.0 Release.

### Beta [Target met for V1.0 Release]:
- HP-3325B Function Generator [almost complete]
- HP-3457A Digital MultiMeter [fairly complete]
- HP-3478A Digital MultiMeter [complete]
- HP-5316A Frequency Counter [almost complete; could use GUI rework]
- HP-6033A Power Supply Unit / System Power Supply [almost complete]
- HP-8116A 50MHz Programmable Pulse/Function Generator [complete - OPTION001 ONLY!]
- ProLogix GPIB Ethernet [complete]
- R&S ESH-3 Selective Level Meter [almost complete]
- Tek-2440 Digital Storage Oscilloscope [almost complete]

### Alpha [Target: -> Beta for V1.0 Release]:
- HP-8566B Spectrum Analyzer
- HP-8663A Synthesized Signal Generator
- HP-70000 Spectrum Analyzer [MMS]

### Pre-Alpha [Target: -> Beta for V0.8 Release]:
- Easy Configuration (Framework)

### Pre-Alpha [Target: -> Beta for V1.0 Release; Alpha for V0.8 Release]:
- User Manual
- Plugin Framework
- Instrument Tuning Framework

## Status/Log [20220209 - v0.8-SNAPSHOT]

### [20220209] Alpha -> Beta:
- HP-3457A Digital Multimeter [All commands implemented, except GUI for state memory, MATH, SUBPROGRMs and a few minor functions;
                               acquisition and settings work; fine-tuning required; requires jswing 0.8-SNAPSHOT]

### [20220130] Added Alpha:
- HP-3457A Digital Multimeter [All commands implemented;
                               acquisition and settings need some more work; requires jswing 0.8-SNAPSHOT]

## Status/Log [20220115 - v0.8-SNAPSHOT]

### [20220115] Added Beta:
- HP-3325B Function Generator [complete except arbitrary waveform and memory functions]

## Status [20220107 - v0.7]

### Beta [Target met for V1.0 Release]:
- HP-3478A Digital MultiMeter [complete]
- HP-5316A Frequency Counter [almost complete; could use GUI rework]
- HP-6033A Power Supply Unit / System Power Supply [almost complete]
- HP-8116A 50MHz Programmable Pulse/Function Generator [complete - OPTION001 ONLY!]
- ProLogix GPIB Ethernet
- R&S ESH-3 Selective Level Meter [almost complete]
- Tek-2440 Digital Storage Oscilloscope [almost complete]

### Alpha [Target: -> Beta for V1.0 Release]:
- HP-8566B Spectrum Analyzer
- HP-8663A Synthesized Signal Generator
- HP-70000 Spectrum Analyzer [MMS]

### Work in Progress [Target: -> Beta for V1.0 Release]:
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
