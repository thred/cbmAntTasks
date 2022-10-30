package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.cbm.ant.AntTestUtils;

public interface CbmDiskUtilityAdapter
{
    File getImageFile();

    CbmImageType<?> getImageType();

    CbmDiskUtilityAdapter format(String diskName, String diskId) throws IOException;

    CbmDiskUtilityAdapter writeFile(String name, byte[] bytes) throws IOException;

    CbmDiskUtilityAdapter deleteFile(String name) throws IOException;

    default String dumpImage() throws IOException
    {
        File imageFile = getImageFile();
        File dumpFile = new File(imageFile.getParentFile(), imageFile.getName() + ".dump");

        CbmDisk disk = getImageType().createEmptyDisk();

        disk.load(imageFile);

        StringBuilder bob = new StringBuilder();

        disk.printHeader(bob);
        bob.append("\n\n");
        disk.printDirectory(bob, true);
        bob.append("\n\n");
        disk.getFile("sample H").printSectorChain(bob);
        bob.append("\n\n");
        disk.printSectors(bob);

        String dump = bob.toString();

        try (FileWriter out = new FileWriter(dumpFile))
        {
            out.append(dump);
        }

        return AntTestUtils.crc32(dump.getBytes());
    }

    default void printDirectory() throws IOException
    {
        File imageFile = getImageFile();

        CbmDisk disk = getImageType().createEmptyDisk();

        disk.load(imageFile);

        StringBuilder bob = new StringBuilder();

        disk.printDirectory(bob, true);

        System.out.println(bob);
    }
}
