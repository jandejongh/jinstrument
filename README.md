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

More detailed documentation will follow once the framework has stabilized (notably, the support for channels is still lacking).

## Status [20191129]
Very alpha... Currently working on:
- ProLogix Ethernet-GPIB support (works);
- HP54502A Digitizing Oscilloscope (does something but lot more effort needed here);
- HP70000 Spectrum Analyzer (hp70900b lo and 70300a tg) -> works but much to be desired;
- HP8566B Spectrum Analyzer -> same;
- HP8663A Synthesized Signal Generator -> Very immature, but capable of reading/setting frequency/output power.

And pending:
- HP8550 Sweep Oscillator;
- HP3478A Digital Multimeter;
- Various HP Power Supply Units (a.o. hp6205c);
- Wiltron 560A Network Analyzer;
- Rohde&Schwarz ESH-3 (unit broken though...).

