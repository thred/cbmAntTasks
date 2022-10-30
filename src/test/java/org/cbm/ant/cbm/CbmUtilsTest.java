package org.cbm.ant.cbm;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.cbm.ant.cbm.disk.CbmUtils;
import org.junit.jupiter.api.Test;

public class CbmUtilsTest
{
    @Test
    public void ascii2petsciiTest()
    {
        assertThat(CbmUtils.ascii2petscii('!'), equalTo((byte) 0x21));
        assertThat(CbmUtils.ascii2petscii('1'), equalTo((byte) 0x31));
        assertThat(CbmUtils.ascii2petscii('a'), equalTo((byte) 0x41));
        assertThat(CbmUtils.ascii2petscii('q'), equalTo((byte) 0x51));
        assertThat(CbmUtils.ascii2petscii('A'), equalTo((byte) 0xc1));
        assertThat(CbmUtils.ascii2petscii('Q'), equalTo((byte) 0xd1));
    }

    @Test
    public void petscii2asciiTest()
    {
        assertThat(CbmUtils.petscii2ascii((byte) 0x21), equalTo('!'));
        assertThat(CbmUtils.petscii2ascii((byte) 0x31), equalTo('1'));
        assertThat(CbmUtils.petscii2ascii((byte) 0x41), equalTo('a'));
        assertThat(CbmUtils.petscii2ascii((byte) 0x51), equalTo('q'));
        assertThat(CbmUtils.petscii2ascii((byte) 0xc1), equalTo('A'));
        assertThat(CbmUtils.petscii2ascii((byte) 0xd1), equalTo('Q'));
    }
}
