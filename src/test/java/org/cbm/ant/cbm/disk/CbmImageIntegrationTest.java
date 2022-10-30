package org.cbm.ant.cbm.disk;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.cbm.ant.AntTestUtils;
import org.cbm.ant.cbm.disk.CbmIOException.Type;
import org.cbm.ant.util.Util;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class CbmImageIntegrationTest
{
    private static final String FILENAME_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @ParameterizedTest
    @EnumSource(CbmImageTestType.class)
    public void testFormat(CbmImageTestType type) throws IOException
    {
        CbmDisk disk = type.createEmptyDisk();

        disk.format("test", "01");

        assertThat(disk.checkFormatted(), nullValue());

        assertThat(disk.getDiskName(),
            equalTo("test\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0"));
        assertThat(disk.getDiskId(), equalTo("01"));

        CbmSector header;
        CbmSector directory;

        switch (type)
        {
            case D64:
            case D64_KRILL:
                header = disk.sectorAt(CbmSectorLocation.of(18, 0));
                directory = disk.sectorAt(CbmSectorLocation.of(18, 1));

                assertThat(disk.getDosVersion(), equalTo(0x41));
                assertThat(disk.getDosType(), equalTo("2a"));

                assertThat(header.getByte(0x02), equalTo(0x41));
                assertThat(header.getString(0x90, 0x10),
                    equalTo("test\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0"));
                assertThat(header.getString(0xa2, 0x02), equalTo("01"));
                assertThat(header.getString(0xa5, 0x02), equalTo("2a"));

                assertThat(disk.getBlocksFree(), equalTo(664));
                break;

            case D71:
                header = disk.sectorAt(CbmSectorLocation.of(18, 0));
                directory = disk.sectorAt(CbmSectorLocation.of(18, 1));

                assertThat(disk.getDosVersion(), equalTo(0x41));
                assertThat(disk.getDosType(), equalTo("2a"));

                assertThat(header.getByte(0x02), equalTo(0x41));
                assertThat(header.getString(0x90, 0x10),
                    equalTo("test\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0"));
                assertThat(header.getString(0xa2, 0x02), equalTo("01"));
                assertThat(header.getString(0xa5, 0x02), equalTo("2a"));

                assertThat(disk.getBlocksFree(), equalTo(1328));
                break;

            case D81:
                header = disk.sectorAt(CbmSectorLocation.of(40, 0));
                directory = disk.sectorAt(CbmSectorLocation.of(40, 3));

                assertThat(disk.isSectorUsed(CbmSectorLocation.of(40, 1)), equalTo(true)); // lower BAM
                assertThat(disk.isSectorUsed(CbmSectorLocation.of(40, 2)), equalTo(true)); // upper BAM

                assertThat(disk.getDosVersion(), equalTo(0x44));
                assertThat(disk.getDosType(), equalTo("3d"));

                assertThat(header.getByte(0x02), equalTo(0x44));
                assertThat(header.getString(0x04, 0x10),
                    equalTo("test\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0\u00a0"));
                assertThat(header.getString(0x16, 0x02), equalTo("01"));
                assertThat(header.getString(0x19, 0x02), equalTo("3d"));

                assertThat(disk.getBlocksFree(), equalTo(3160));
                break;

            default:
                throw new UnsupportedOperationException("Type not supported: " + type);
        }

        assertThat(disk.isSectorUsed(header.getLocation()), equalTo(true));
        assertThat(disk.isSectorUsed(directory.getLocation()), equalTo(true));

        assertThat(header.getNextTrackNr(), equalTo(directory.getLocation().getTrackNr()));
        assertThat(header.getNextSectorNr(), equalTo(directory.getLocation().getSectorNr()));

        assertThat(directory.getNextTrackNr(), equalTo(0x00));
        assertThat(directory.getNextSectorNr(), equalTo(0xff));

        for (int i = 0; i < 8; ++i)
        {
            int pos = i * 32;

            assertThat(directory.getByte(pos + 0x02), equalTo(0x00));
            assertThat(directory.getByte(pos + 0x03), equalTo(0x00));
            assertThat(directory.getByte(pos + 0x04), equalTo(0x00));
            assertThat(directory.getBytes(pos + 0x05, 16), equalTo(Util.byteArrayOf((byte) 0, 16)));
            assertThat(directory.getByte(pos + 0x17), equalTo(0x00));
            assertThat(directory.getByte(pos + 0x1e), equalTo(0x00));
            assertThat(directory.getByte(pos + 0x1f), equalTo(0x00));
        }
    }

    @ParameterizedTest
    @EnumSource(CbmImageTestType.class)
    public void testReadWrite(CbmImageTestType type) throws IOException
    {
        byte[] source = AntTestUtils.createSample(65536);
        CbmDisk disk = type.createEmptyDisk();

        disk.format("test", "01");
        disk.writeFile("file", source);

        byte[] target = disk.readFileFully("file");

        assertThat(source, equalTo(target));
    }

    @ParameterizedTest
    @EnumSource(CbmImageTestType.class)
    public void testMultiReadWrite(CbmImageTestType type) throws IOException
    {
        byte[] sample1 = AntTestUtils.createSample(128);
        byte[] sample2 = AntTestUtils.createSample(1544);
        byte[] sample3 = AntTestUtils.createSample(16890);
        byte[] sample4 = AntTestUtils.createSample(1);
        byte[] sample5 = AntTestUtils.createSample(0);
        byte[] sample6 = AntTestUtils.createSample(254);
        byte[] sample7 = AntTestUtils.createSample(255);
        byte[] sample8 = AntTestUtils.createSample(8000);
        byte[] sample9 = AntTestUtils.createSample(128);

        CbmDisk disk = type.createEmptyDisk();

        disk.format("test", "01");
        disk.writeFile("sample1", sample1);
        disk.writeFile("sample2", sample2);
        disk.writeFile("sample3", sample3);
        disk.writeFile("sample4", sample4);
        disk.writeFile("sample5", sample5);
        disk.writeFile("sample6", sample6);
        disk.writeFile("sample7", sample7);
        disk.writeFile("sample8", sample8);
        disk.writeFile("sample9", sample9);

        assertThat(sample1, equalTo(disk.readFileFully("sample1")));
        assertThat(sample2, equalTo(disk.readFileFully("sample2")));
        assertThat(sample3, equalTo(disk.readFileFully("sample3")));
        assertThat(sample4, equalTo(disk.readFileFully("sample4")));
        assertThat(sample5, equalTo(disk.readFileFully("sample5")));
        assertThat(sample6, equalTo(disk.readFileFully("sample6")));
        assertThat(sample7, equalTo(disk.readFileFully("sample7")));
        assertThat(sample8, equalTo(disk.readFileFully("sample8")));
        assertThat(sample9, equalTo(disk.readFileFully("sample9")));
    }

    @ParameterizedTest
    @EnumSource(CbmImageTestType.class)
    public void testDirectory(CbmImageTestType type) throws IOException
    {
        Random random = new Random(1);
        Map<String, byte[]> cachedData = new HashMap<>();
        CbmDisk disk = type.createEmptyDisk();

        disk.format("test", "01");

        while (disk.getBlocksFree() > 0)
        {
            String filename = "";

            for (int i = random.nextInt(15) + 1; i >= 0; --i)
            {
                filename += FILENAME_CHARACTERS.charAt(random.nextInt(FILENAME_CHARACTERS.length()));
            }

            int size = random.nextInt(100 * 254) % (disk.getBlocksFree() * 254);
            byte[] source = AntTestUtils.createSample(size);

            cachedData.put(filename, source);
            disk.writeFile(filename, source);
        }

        File file = AntTestUtils.randomTmpFile();

        disk.save(file);

        disk = type.createEmptyDisk();
        disk.load(file);

        assertThat(disk.getBlocksFree(), equalTo(0));

        disk.findFiles("*").forEach(f -> {
            byte[] bytes = cachedData.remove(f.getName());

            assertThat("Known file: " + f.getName(), bytes, notNullValue());

            try
            {
                assertThat(f.readFully(), equalTo(bytes));
            }
            catch (IOException e)
            {
                throw new AssertionError("Failed to read file: " + f.getName());
            }
        });

        assertThat(cachedData.isEmpty(), equalTo(true));
    }

    @ParameterizedTest
    @EnumSource(CbmImageTestType.class)
    public void testDuplicateFile(CbmImageTestType type) throws IOException
    {
        byte[] bytes = AntTestUtils.createSample(256);
        CbmDisk disk = type.createEmptyDisk();

        disk.format("test", "01");
        disk.writeFile("file a", bytes);

        CbmIOException exception = assertThrows(CbmIOException.class, () -> disk.writeFile("file a", bytes));

        assertThat(exception.getType(), equalTo(Type.FILE_EXISTS));
    }
}
