# jinstrument
Java Swing application for (GPIB) reading and controlling (old-school) lab instruments like spectrum analyzers, signal generator, digital multimeters, etc.

## Build Type
Maven, but you'll need to manually install dependency artefacts (all available from Maven Central or from my github).

## Installation
Either through Maven, or by simply picking up a release's jinstrument-X.Y-jar-with-dependencies.jar file. A working example ~/.jinstrument/instrumentRegistry.json (holding persistent definitions of devices, instruments and views) is there as well.

## License
Apache v2.0.

## Release 0.1 [20191226]
Very basis support for
- Prologix GPIB-Ethernet
- HP3478A DMM [well, no real support actually]
- HP8566B SA [basic settings & trace display]
- HP8663A RF SigGen [basic settings; still lots of trouble reading settings from instrument]
- A basic GPIB-device console [works OK; very useful]

Reasonable framework for buses, controllers, devices, instruments and instrument views.

## Release 0.2 [20191228]
Most important changes:
- Revamped ProLogix controller implementation.
- Added GPIB Controller logging + Swing component.

## Release 0.3 [20191231]
Most important changes:
- Selecting and removing Instrument Views from the Registry.
- Full-blown HP3478A support (only lacking a few trigger options).

More detailed documentation will follow once the framework has stabilized (notably, the support for channels is still lacking).

## Status [20200103 - 0.4-SNAPSHOT]
Beta:
- ProLogix GPIB Ethernet
- HP-3478A Digital MultiMeter

Alpha:
- HP-8566B Spectrum Analyzer
- HP-8663A Synthesized Signal Generator

Work in Progress [target for V1.0 Release]:
- HP-3586A Selective Level Meter
- HP-5316A Frequency Counter
- HP-5328A [Option 011] Frequency Counter
- HP-8116A Programmable Pulse/Function Generator
- HP-8350 Sweep Oscillator
- HP-54502A Digitizing Oscilloscope
- HP-70000 Spectrum Analyzer
- Rohde&Schwarz ESH-3 (unit broken though...).
- Tek-2440 Digital Storage Oscilloscope
- Wiltron 560A Network Analyzer;
