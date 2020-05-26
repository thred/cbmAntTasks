package org.cbm.ant.cbm.disk;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.cbm.ant.util.IOUtils;
import org.junit.Test;

public class CBMDiskIntegrationTest
{

    @Test
    public void testFormat() throws IOException
    {
        CBMDiskOperator operator = createOperator();

        CBMDiskBAM bam = operator.getBAM();
        CBMDiskSector bamSector = bam.getBAMSector();

        assertEquals(18, bamSector.getTrackNr());
        assertEquals(0, bamSector.getSectorNr());

        assertEquals(18, bam.getDirTrackNr());
        assertEquals(1, bam.getDirSectorNr());
        assertTrue(bam.isSectorUsed(18, 0));

        assertEquals("01", bam.getDiskID());
        assertEquals("a2", bam.getDOSType());
        assertEquals(0x41, bam.getDOSVersion());
        assertEquals("test\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0", bam.getDiskName());
        assertEquals(664, bam.getFreeSectors());

        CBMDiskDir dir = operator.getDir();
        CBMDiskDirSector firstDirSector = dir.getFirstDirSector();

        assertTrue(bam.isSectorUsed(firstDirSector.getLocation()));
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

        assertArrayEquals(sampleA, readFromDisk(operator, "sample 0"));
        assertArrayEquals(sampleA, readFromDisk(operator, "sample a"));
        assertArrayEquals(sampleB, readFromDisk(operator, "sample b"));
        assertArrayEquals(sampleC, readFromDisk(operator, "sample c"));
        assertArrayEquals(sampleD, readFromDisk(operator, "sample d"));
        assertArrayEquals(sampleE, readFromDisk(operator, "sample e"));
        assertArrayEquals(sampleF, readFromDisk(operator, "sample f"));
        assertArrayEquals(sampleG, readFromDisk(operator, "sample g"));
        assertArrayEquals(sampleH, readFromDisk(operator, "sample h"));
        assertArrayEquals(sampleI, readFromDisk(operator, "sample i"));

        //operator.getDisk().save(new File("D:/test.d64"));
    }

    private void writeToDisk(CBMDiskOperator operator, String fileName, byte[] sampleA)
        throws IOException, CBMDiskException
    {
        CBMDiskOutputStream out = operator.create(fileName, CBMFileType.PRG);

        try
        {
            ByteArrayInputStream in = new ByteArrayInputStream(sampleA);

            try
            {
                IOUtils.copy(in, out);
            }
            finally
            {
                in.close();
            }
        }
        finally
        {
            out.close();
        }
    }

    public byte[] readFromDisk(CBMDiskOperator operator, String fileName) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try
        {
            InputStream in = operator.open(fileName);

            try
            {
                IOUtils.copy(in, out);
            }
            finally
            {
                in.close();
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
