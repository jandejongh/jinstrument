# jinstrument
Java Swing application for (GPIB) reading and controlling (old-school) lab instruments like spectrum analyzers, signal generator, digital multimeters, etc.

## Build Type
Maven, but you'll need to manually install dependency artefacts (all available from Maven Central or from my github).

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

