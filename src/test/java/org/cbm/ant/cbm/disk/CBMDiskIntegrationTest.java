package org.cbm.ant.cbm.disk;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.cbm.ant.util.IOUtils;
import org.junit.jupiter.api.Test;

public class CBMDiskIntegrationTest
{

    @Test
    public void testFormat() throws IOException
    {
        CBMDiskOperator operator = createOperator();

        CBMDiskBAM bam = operator.getBAM();
        CBMDiskSector bamSector = bam.getBAMSector();

        assertThat(bamSector.getTrackNr(), equalTo(18));
        assertThat(bamSector.getSectorNr(), equalTo(0));

        assertThat(bam.getDirTrackNr(), equalTo(18));
        assertThat(bam.getDirSectorNr(), equalTo(1));
        assertThat(bam.isSectorUsed(18, 0), equalTo(true));

        assertThat(bam.getDiskID(), equalTo("01"));
        assertThat(bam.getDOSType(), equalTo("a2"));
        assertThat(bam.getDOSVersion(), equalTo(0x41));
        assertThat(bam.getDiskName(),
            equalTo("test\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0"));
        assertThat(bam.getFreeSectors(), equalTo(664));

        CBMDiskDir dir = operator.getDir();
        CBMDiskDirSector firstDirSector = dir.getFirstDirSector();

        assertThat(bam.isSectorUsed(firstDirSector.getLocation()), equalTo(true));
    }

    @Test
    public void testReadWrite() throws IOException, CBMDiskException
    {
        CBMDiskOperator operator = createOperator();

        byte[] sample0 = createSample(256 * 0);
        byte[] sampleA = createSample(256 * 1);
        byte[] sampleB = createSample(256 * 2);
        byte[] sampleC = createSample(256 * 4);
        byte[] sampleD = createSample(256 * 8);
        byte[] sampleE = createSample(256 * 16);
        byte[] sampleF = createSample(256 * 32);
        byte[] sampleG = createSample(256 * 64);
        byte[] sampleH = createSample(256 * 128);
        byte[] sampleI = createSample(256 * 256);

        writeToDisk(operator, "sample 0", sample0);
        writeToDisk(operator, "sample a", sampleA);
        writeToDisk(operator, "sample b", sampleB);
        writeToDisk(operator, "sample c", sampleC);
        writeToDisk(operator, "sample d", sampleD);
        writeToDisk(operator, "sample e", sampleE);
        writeToDisk(operator, "sample f", sampleF);
        writeToDisk(operator, "sample g", sampleG);
        writeToDisk(operator, "sample h", sampleH);
        writeToDisk(operator, "sample i", sampleI);

        System.out.println(operator.getBAM());

        operator.getDir().list(System.out, false, false);

        assertThat(readFromDisk(operator, "sample 0"), equalTo(sample0));
        assertThat(readFromDisk(operator, "sample a"), equalTo(sampleA));
        assertThat(readFromDisk(operator, "sample b"), equalTo(sampleB));
        assertThat(readFromDisk(operator, "sample c"), equalTo(sampleC));
        assertThat(readFromDisk(operator, "sample d"), equalTo(sampleD));
        assertThat(readFromDisk(operator, "sample e"), equalTo(sampleE));
        assertThat(readFromDisk(operator, "sample f"), equalTo(sampleF));
        assertThat(readFromDisk(operator, "sample g"), equalTo(sampleG));
        assertThat(readFromDisk(operator, "sample h"), equalTo(sampleH));
        assertThat(readFromDisk(operator, "sample i"), equalTo(sampleI));
    }

    private void writeToDisk(CBMDiskOperator operator, String fileName, byte[] sampleA)
        throws IOException, CBMDiskException
    {
        try (CBMDiskOutputStream out = operator.create(fileName, CBMFileType.PRG, null))
        {
            try (ByteArrayInputStream in = new ByteArrayInputStream(sampleA))
            {
                IOUtils.copy(in, out);
            }
        }
    }

    public byte[] readFromDisk(CBMDiskOperator operator, String fileName) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try
        {
            try (InputStream in = operator.open(fileName))
            {
                IOUtils.copy(in, out);
            }
        }
        finally
        {
            out.close();
        }

        return out.toByteArray();
    }

    private CBMDiskOperator createOperator()
    {
        CBMDisk disk = new CBMDisk();

        disk.init(CBMDiskFormat.CBM_154x);

        CBMDiskOperator operator = new CBMDiskOperator(disk);

        operator.format(CBMDiskFormat.CBM_154x, "test", "01");

        return operator;
    }

    private byte[] createSample(int length)
    {
        byte[] result = new byte[length];

        for (int i = 0; i < length; i += 1)
        {
            result[i] = (byte) (Math.random() * 0xff);
        }

        return result;
    }

}
