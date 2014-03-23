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

	/**
	 * Formats the disk. Reinitialized the bam and the directory structure. Does not touch any other sectors.
	 * 
	 * @param format the format
	 * @param diskName the name of the disk
	 * @param diskId the id of the disk
	 */
	public void format(CBMDiskFormat format, String diskName, String diskId)
	{
		getDisk().init(format);
		getBAM().format(diskName, diskId);
		getDir().format();
	}

	public InputStream open(String fileName) throws IOException
	{
		CBMDiskDirEntry entry = getDir().find(fileName);

		if (entry == null)
		{
			throw new FileNotFoundException(String.format("File not found: %s", fileName));
		}

		return new CBMDiskInputStream(this, entry.getFileTrackNr(), entry.getFileSectorNr());
	}

	public CBMDiskOutputStream create(String fileName, CBMFileType fileType) throws IOException, CBMDiskException
	{
		return create(null, fileName, fileType);
	}

	public CBMDiskOutputStream create(CBMDiskLocation location, String fileName, CBMFileType fileType)
			throws IOException, CBMDiskException
	{
		CBMDiskDirEntry dirEntry = getDir().allocate();

		dirEntry.setFileTrackNr(0);
		dirEntry.setFileSectorNr(0);
		dirEntry.setFileType(fileType);
		dirEntry.setFileTypeLocked(false);
		dirEntry.setFileTypeClosed(true);
		dirEntry.setFileName(fileName);
		dirEntry.setRELFileTrackNr(0);
		dirEntry.setRELFileSectorNr(0);
		dirEntry.setFileSize(0);

		return create(location, dirEntry);
	}

	public CBMDiskOutputStream create(CBMDiskDirEntry dirEntry)
	{
		return null;
	}

	public CBMDiskOutputStream create()
	{
		return create((CBMDiskLocation) null, (CBMDiskDirEntry) null);
	}

	public CBMDiskOutputStream create(CBMDiskLocation location)
	{
		return create(location, null);
	}

	public CBMDiskOutputStream create(CBMDiskLocation location, CBMDiskDirEntry dirEntry)
	{
		return new CBMDiskOutputStream(this, dirEntry, location);
	}

}
