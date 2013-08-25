cbmAntTasks
===========

A collection of Ant tasks for the development with cc65 (http://www.cc65.org) and Vice (http://www.viceteam.org/).

Tasks
=====

CBM
---

Utilities for working with CBM files.

* PRGHeader - Adds or modifies the two byte header of a CBM PRG file.
* PRGSplit - Splits a CBM PRG file into multiple parts.

CC65
----

Launchers for cc65 (http://www.cc65.org).

* CA65 - Calls the ca65 executable of cc65.
* CC65 - Calls the cc65 executable of cc65.
* CL65 - Calls the cl65 executable of cc65.
* LD65 - Calls the ld65 executable of cc65.

Data
----

Builds binary data files.

* Data - The main task for Ant.
  * DataFill - Fills bytes with the same value.
  * DataFont - Extracts a font from the cbmFontEditor projects and stores it as binary.
  * DataHeader - Sets the two byte header of a CBM PRG file.
  * DataImage - Converts an image to binary (including dithering).
  * DataManual - Adds manual data.
  * DataSprite - Converts an image to binary sprite data (including dithering).
  * DataText - Converts ASCII to PETSCII text

Viceteam
--------

Tasks for the Vice emulator (http://www.viceteam.org/).

* C1541 - Main task for executing the C1541 executable of Vice
  * C1541Format - Calls format of the C1541 executable of Vice
  * C1541List - Calls list of the C1541 executable of Vice
  * C1541Read - Calls read of the C1541 executable of Vice
  * C1541Write - Calls write of the C1541 executable of Vice
* X128 - Starts the Commodore 128 emulator of Vice
* X64 - Starts the Commodore 64 emulator of Vice
* X64DTV - Starts the Commodore 64 DTV emulator of Vice
* X64 - Starts the Commodore 64 emulator of Vice
* XCbm2 - Starts the CBM 2 emulator of Vice
* XPet - Starts the Commodore PET emulator of Vice
* XPlus4 - Starts the Commodore Plus 4 emulator of Vice
* XVic - Starts the Commodore VIC emulator of Vice
