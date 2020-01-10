# jinstrument
Java Swing application for (GPIB) reading and controlling (old-school) lab instruments like spectrum analyzers, signal generator, digital multimeters, etc.

## Build Type
Maven, but you'll need to manually install dependency artefacts (all available from Maven Central or from my github).

## Installation
Either through Maven, or by simply picking up a release's jinstrument-X.Y-jar-with-dependencies.jar file. A working example ~/.jinstrument/instrumentRegistry.json (holding persistent definitions of devices, instruments and views) is there as well.

## License
Apache v2.0.

## Status [20200110 - v0.6-SNAPSHOT]
- Requires jswing v06-SNAPSHOT!
- HP-70000 [SpectrumAnalyzer ONLY!] -> alpha.

## Status [20200106 - v0.5]

### Beta:
- ProLogix GPIB Ethernet
- HP-3478A Digital MultiMeter [complete]
- HP-8116A 50MHz Programmable Pulse/Function Generator [complete - OPTION001 ONLY!]

### Alpha:
- HP-8566B Spectrum Analyzer
- HP-8663A Synthesized Signal Generator

### Work in Progress [target for V1.0 Release]:
- HP-3586A Selective Level Meter
- HP-5316A Frequency Counter
- HP-5328A [Option 011] Frequency Counter
- HP-6033A DC Power Supply Unit [System Power Supply]
- HP-6050A Electronic Load Mainframe [DC Load]
- HP-8156A Optical Attenuator
- HP-8350 Sweep Oscillator
- HP-54502A Digitizing Oscilloscope
- HP-70000 Spectrum Analyzer
- Rohde&Schwarz ESH-3 (unit broken though...)
- Tek-2440 Digital Storage Oscilloscope
- Wiltron 560A Network Analyzer

## Release History

### Release 0.1 [20191226]
Very basis support for
- Prologix GPIB-Ethernet
- HP3478A DMM [well, no real support actually]
- HP8566B SA [basic settings & trace display]
- HP8663A RF SigGen [basic settings; still lots of trouble reading settings from instrument]
- A basic GPIB-device console [works OK; very useful]

Reasonable framework for buses, controllers, devices, instruments and instrument views.

### Release 0.2 [20191228]
Most important changes:
- Revamped ProLogix controller implementation.
- Added GPIB Controller logging + Swing component.

### Release 0.3 [20191231]
Most important changes:
- Selecting and removing Instrument Views from the Registry.
- Full-blown HP3478A support (only lacking a few trigger options).

### Release 0.4 [20190104]
Most important changes:
- Timeout handling.
- Added many instrument classes and concrete (placeholder) classes.
- HP-8116A [partial] enters beta.
- Improvements to (notably) Controller Debugger.

### Release 0.5 [20190106]
Most important change:
- Completed Instrument and InstrumentView implementation for HP-8116A.

More detailed documentation will follow once the framework has stabilized (notably, the support for channels is still lacking).
