package org.cbm.ant.cbm.disk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CBMDiskOperator
{

	private final CBMDisk disk;
	private final CBMDiskBAM bam;
	private final CBMDiskDir dir;

	public CBMDiskOperator(CBMDisk disk)
	{
		super();

		this.disk = disk;

		bam = new CBMDiskBAM(this);
		dir = new CBMDiskDir(this);
	}

	public CBMDisk getDisk()
	{
		return disk;
	}

	public CBMDiskBAM getBAM()
	{
		return bam;
	}

	public CBMDiskDir getDir()
	{
		return dir;
	}

	public InputStream getInputStream(String fileName) throws IOException
	{
		CBMDiskDirEntry entry = getDir().find(fileName);

		if (entry == null)
		{
			throw new FileNotFoundException(String.format("File not found: %s", fileName));
		}

		return new CBMDiskInputStream(disk, entry.getFileTrackNr(), entry.getFileSectorNr());
	}
	
	
}
