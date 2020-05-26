package org.cbm.ant.cbm.disk;

import static org.junit.Assert.*;

import org.junit.Test;

public class CBMDiskUtilTest
{

    @Test
    public void ascii2petsciiTest()
    {
        assertEquals(0x21, CBMDiskUtil.ascii2petscii('!'));
        assertEquals(0x31, CBMDiskUtil.ascii2petscii('1'));
        assertEquals(0x41, CBMDiskUtil.ascii2petscii('a'));
        assertEquals(0x51, CBMDiskUtil.ascii2petscii('q'));
        assertEquals(0x61, CBMDiskUtil.ascii2petscii('A'));
        assertEquals(0x71, CBMDiskUtil.ascii2petscii('Q'));
    }

    @Test
    public void petscii2asciiTest()
    {
        assertEquals('!', CBMDiskUtil.petscii2ascii((byte) 0x21));
        assertEquals('1', CBMDiskUtil.petscii2ascii((byte) 0x31));
        assertEquals('a', CBMDiskUtil.petscii2ascii((byte) 0x41));
        assertEquals('q', CBMDiskUtil.petscii2ascii((byte) 0x51));
        assertEquals('A', CBMDiskUtil.petscii2ascii((byte) 0x61));
        assertEquals('Q', CBMDiskUtil.petscii2ascii((byte) 0x71));
    }

}
