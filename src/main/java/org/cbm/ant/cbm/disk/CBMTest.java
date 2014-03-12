package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.IOException;

import org.cbm.ant.util.IOUtils;

public class CBMTest
{
	public static void main(String[] args) throws IOException
	{
		CBMDisk disk = new CBMDisk();
		CBMDiskOperator operator = new CBMDiskOperator(disk);

		//        disk.load(new File("Pieces_Of_8-Bit-GENESIS-PROJECT.d64"));
		disk.load(new File("Locators Synd.d64"));

		System.out.println(operator.getBAM().toString());

		operator.getDir().list(System.out);

		IOUtils.write(new File("C:/home/dev/c64/projects/LocatorsSynd/bin/locators synd(2).prg"),
				operator.getInputStream("locators synd"));
	}
}
