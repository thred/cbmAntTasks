package org.cbm.ant.cbm.disk;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

public class CBMDiskUtilTest
{
    @Test
    public void ascii2petsciiTest()
    {
        assertThat(CBMDiskUtil.ascii2petscii('!'), equalTo((byte) 0x21));
        assertThat(CBMDiskUtil.ascii2petscii('1'), equalTo((byte) 0x31));
        assertThat(CBMDiskUtil.ascii2petscii('a'), equalTo((byte) 0x41));
        assertThat(CBMDiskUtil.ascii2petscii('q'), equalTo((byte) 0x51));
        assertThat(CBMDiskUtil.ascii2petscii('A'), equalTo((byte) 0x61));
        assertThat(CBMDiskUtil.ascii2petscii('Q'), equalTo((byte) 0x71));
    }

    @Test
    public void petscii2asciiTest()
    {
        assertThat(CBMDiskUtil.petscii2ascii((byte) 0x21), equalTo('!'));
        assertThat(CBMDiskUtil.petscii2ascii((byte) 0x31), equalTo('1'));
        assertThat(CBMDiskUtil.petscii2ascii((byte) 0x41), equalTo('a'));
        assertThat(CBMDiskUtil.petscii2ascii((byte) 0x51), equalTo('q'));
        assertThat(CBMDiskUtil.petscii2ascii((byte) 0x61), equalTo('A'));
        assertThat(CBMDiskUtil.petscii2ascii((byte) 0x71), equalTo('Q'));
    }
}
