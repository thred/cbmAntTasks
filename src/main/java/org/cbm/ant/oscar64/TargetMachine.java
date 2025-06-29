package org.cbm.ant.oscar64;

public enum TargetMachine
{
    c64, // Commodore C64, (0x0800..0xa000)
    c128, // Commodore C128, memory range (0x1c00..0xfc00)
    c128b, // Commodore C128, first 16KB only (0x1c00..0x4000)
    plus4, // Commodore PLUS4, (0x1000..0xfc00)
    vic20, // Commodore VIC20, no extra memory (0x1000..0x1e00)
    vic20_3, // Commodore VIC20, 3K RAM expansion (0x0400..0x1e00)
    vic20_8, // Commodore VIC20, 8K RAM expansion (0x1200..0x4000)
    vic20_16, // Commodore VIC20, 16K RAM expansion (0x1200..0x6000)
    vic20_24, // Commodore VIC20, 24K RAM expansion (0x1200..0x8000)
    pet, // Commodore PET, 8K RAM (0x0400..0x2000)
    pet16, // Commodore PET, 16K RAM (0x0400..0x4000)
    pet32, // Commodore PET, 32K RAM (0x0400..0x8000)
    nes, // Nintendo entertainment system, NROM 32 K ROM, no mirror
    nes_nrom_h, // Nintendo entertainment system, NROM 32 K ROM, h mirror
    nes_nrom_v, // Nintendo entertainment system, NROM 32 K ROM, v mirror
    nes_mmc1, // Nintendo entertainment system, MMC1, 256K PROM, 128K CROM
    nes_mmc3, // Nintendo entertainment system, MMC3, 512K PROM, 256K CROM
    atari, // Atari 8bit systems, (0x2000..0xbc00)
    x16, // Commander X16, (0x0800..0x9f00)
    mega65 // Mega 65, (0x2000..0xc000)
}
