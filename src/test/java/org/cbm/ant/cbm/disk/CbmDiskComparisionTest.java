package org.cbm.ant.cbm.disk;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import org.cbm.ant.AntTestUtils;
import org.junit.jupiter.api.Test;

/**
 * In order to run these tests, it is necessary, that the c1541 and the cc1541 executables are accessible, either by
 * *_HOME variable or via the path.
 *
 * @author Manfred Hantschel
 */
public class CbmDiskComparisionTest
{
    private static final File PATH = new File("target");

    @Test
    public void compareC1541() throws FileNotFoundException, IOException
    {
        File defaultImage = new File(PATH, "cbm.d64");
        File c1541Image = new File(PATH, "c1541.d64");

        defaultImage.delete();
        c1541Image.delete();

        CbmBuiltInAdapter defaultAdapter = new CbmBuiltInAdapter(defaultImage, CbmImageTypes.D64);
        CbmC1541Adapter c1541Adapter = new CbmC1541Adapter(c1541Image, CbmImageTypes.D64, "d64");

        write1541TestData(defaultAdapter);
        write1541TestData(c1541Adapter);

        String defaultDumpCrc = defaultAdapter.dumpImage();
        String c1541DumpCrc = c1541Adapter.dumpImage();

        assertThat(defaultDumpCrc, equalTo(c1541DumpCrc));
    }

    @Test
    public void compareCC1541() throws FileNotFoundException, IOException
    {
        File defaultImage = new File(PATH, "ccbm.d64");
        File cc1541Image = new File(PATH, "cc1541.d64");

        defaultImage.delete();
        cc1541Image.delete();

        CbmBuiltInAdapter defaultAdapter = new CbmBuiltInAdapter(defaultImage, CbmImageTypes.D64)
            .withAllocateSectorStrategy(new SequentialCbmAllocateSectorStrategy())
            .fillSectorWith(0x00)
            .withEraseOnDelete(true);

        CbmCC1541Adapter cc1541Adapter = new CbmCC1541Adapter(cc1541Image, CbmImageTypes.D64);

        write1541TestData(defaultAdapter);
        write1541TestData(cc1541Adapter);

        String defaultDumpCrc = defaultAdapter.dumpImage();
        String c1541DumpCrc = cc1541Adapter.dumpImage();

        assertThat(defaultDumpCrc, equalTo(c1541DumpCrc));
    }

    private void write1541TestData(CbmDiskUtilityAdapter adapter) throws IOException
    {
        adapter.format("test", "01");

        adapter.writeFile("sample A", AntTestUtils.createSample(254 * 2));
        adapter.writeFile("sample B", AntTestUtils.createSample(254 * 5 - 42));
        adapter.deleteFile("sample A");
        adapter.writeFile("sample A", AntTestUtils.createSample(254 * 1 + 126));
        adapter.writeFile("sample C", AntTestUtils.createSample(254 * 7));
        adapter.writeFile("sample D", AntTestUtils.createSample(254 * 9 - 253));
        adapter.writeFile("sample E", AntTestUtils.createSample(254 * 24));
        adapter.writeFile("sample F", AntTestUtils.createSample(254 * 334 + 1));
        adapter.deleteFile("sample B");
        adapter.writeFile("sample G", AntTestUtils.createSample(254 * 2));
        adapter.writeFile("sample H", AntTestUtils.createSample(254 * 5));
        adapter.writeFile("sample I", AntTestUtils.createSample(254 * 21));
        adapter.deleteFile("sample E");
        adapter.writeFile("sample J", AntTestUtils.createSample(254 * 21));

        writeRandomTestData(adapter, 262);

        adapter.printDirectory();
        adapter.deleteFile("sample G");
    }

    @Test
    public void compareC1571() throws FileNotFoundException, IOException
    {
        File defaultImage = new File(PATH, "cbm.d71");
        File c1541Image = new File(PATH, "c1541.d71");

        defaultImage.delete();
        c1541Image.delete();

        CbmBuiltInAdapter defaultAdapter = new CbmBuiltInAdapter(defaultImage, CbmImageTypes.D71);
        CbmC1541Adapter c1541Adapter = new CbmC1541Adapter(c1541Image, CbmImageTypes.D71, "d71");

        write1571TestData(defaultAdapter);
        write1571TestData(c1541Adapter);

        String defaultDumpCrc = defaultAdapter.dumpImage();
        String c1541DumpCrc = c1541Adapter.dumpImage();

        assertThat(defaultDumpCrc, equalTo(c1541DumpCrc));
    }

    private void write1571TestData(CbmDiskUtilityAdapter adapter) throws IOException
    {
        adapter.format("test", "01");
        adapter.writeFile("sample A", AntTestUtils.createSample(254 * 2));
        adapter.writeFile("sample B", AntTestUtils.createSample(254 * 5 - 42));
        adapter.deleteFile("sample A");
        adapter.writeFile("sample A", AntTestUtils.createSample(254 * 1 + 126));
        adapter.writeFile("sample C", AntTestUtils.createSample(254 * 7));
        adapter.writeFile("sample D", AntTestUtils.createSample(254 * 9 - 253));
        adapter.writeFile("sample E", AntTestUtils.createSample(254 * 24));
        adapter.writeFile("sample F", AntTestUtils.createSample(254 * 334 + 1));
        adapter.deleteFile("sample B");
        adapter.writeFile("sample G", AntTestUtils.createSample(254 * 2));
        adapter.writeFile("sample H", AntTestUtils.createSample(254 * 5));
        adapter.writeFile("sample I", AntTestUtils.createSample(254 * 21));
        adapter.deleteFile("sample E");
        adapter.writeFile("sample J", AntTestUtils.createSample(254 * 21));

        writeRandomTestData(adapter, 926);

        adapter.deleteFile("sample G");
    }

    @Test
    public void compareC1581() throws FileNotFoundException, IOException
    {
        File defaultImage = new File(PATH, "cbm.d81");
        File c1541Image = new File(PATH, "c1541.d81");

        defaultImage.delete();
        c1541Image.delete();

        CbmBuiltInAdapter defaultAdapter = new CbmBuiltInAdapter(defaultImage, CbmImageTypes.D81);
        CbmC1541Adapter c1541Adapter = new CbmC1541Adapter(c1541Image, CbmImageTypes.D81, "d81");

        write1581TestData(defaultAdapter);
        write1581TestData(c1541Adapter);

        String defaultDumpCrc = defaultAdapter.dumpImage();
        String c1541DumpCrc = c1541Adapter.dumpImage();

        assertThat(defaultDumpCrc, equalTo(c1541DumpCrc));
    }

    private void write1581TestData(CbmDiskUtilityAdapter adapter) throws IOException
    {
        adapter.format("test", "01");
        adapter.writeFile("sample A", AntTestUtils.createSample(254 * 2));
        adapter.writeFile("sample B", AntTestUtils.createSample(254 * 5 - 42));
        adapter.deleteFile("sample A");
        adapter.writeFile("sample A", AntTestUtils.createSample(254 * 1 + 126));
        adapter.writeFile("sample C", AntTestUtils.createSample(254 * 7));
        adapter.writeFile("sample D", AntTestUtils.createSample(254 * 9 - 253));
        adapter.writeFile("sample E", AntTestUtils.createSample(254 * 24));
        adapter.writeFile("sample F", AntTestUtils.createSample(254 * 334 + 1));
        adapter.deleteFile("sample B");
        adapter.writeFile("sample G", AntTestUtils.createSample(254 * 2));
        adapter.writeFile("sample H", AntTestUtils.createSample(254 * 5));
        adapter.writeFile("sample I", AntTestUtils.createSample(254 * 21));
        adapter.deleteFile("sample E");
        adapter.writeFile("sample J", AntTestUtils.createSample(254 * 21));

        writeRandomTestData(adapter, 2758);

        adapter.deleteFile("sample G");
    }

    private void writeRandomTestData(CbmDiskUtilityAdapter adapter, int blocks) throws IOException
    {
        Random random = new Random(blocks);

        while (blocks > 0)
        {
            int size = Math.min(random.nextInt(63 * 254) + 1, blocks * 254);

            adapter.writeFile("sample " + blocks, AntTestUtils.createSample(size));

            blocks -= (size + 254) / 254;
        }
    }

}
