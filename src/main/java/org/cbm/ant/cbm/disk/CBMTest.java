package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.IOException;

import org.cbm.ant.util.IOUtils;

public class CBMTest
{
    public static void main(String[] args) throws IOException, CBMDiskException
    {
        CBMDisk disk = new CBMDisk();
        CBMDiskOperator operator = new CBMDiskOperator(disk);

        //        disk.load(new File("Pieces_Of_8-Bit-GENESIS-PROJECT.d64"));
        disk.load(new File("../Locators Synd.d64"));

        System.out.println(operator.getBAM().toString());

        operator.getDir().list(System.out, true, true);

        IOUtils
            .write(new File("C:/home/dev/c64/projects/LocatorsSynd/bin/locators synd(2).prg"),
                operator.open("locators synd"));
        IOUtils.write(new File("C:/home/dev/c64/projects/LocatorsSynd/bin/lib(2)"), operator.open("lib"));

        CBMDiskOutputStream out = operator.create("tst", CBMFileType.PRG);

        try
        {
            IOUtils.read(new File("C:/home/dev/c64/projects/LocatorsSynd/bin/lib(2)"), out);
        }
        finally
        {
            out.close();
        }

        disk.save(new File("test.d64"));

        System.out.println(operator.getBAM().toString());

        operator.getDir().list(System.out, true, true);
    }
}
