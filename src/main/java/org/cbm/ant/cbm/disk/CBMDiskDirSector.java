package org.cbm.ant.cbm.disk;

import java.io.PrintStream;

public class CBMDiskDirSector
{

	private final CBMDiskDir dir;
	private final CBMDiskLocation location;
	private final int id;
	private final CBMDiskDirEntry[] entries = new CBMDiskDirEntry[8];

	private CBMDiskDirSector nextDirSector;

	public CBMDiskDirSector(CBMDiskDir dir, CBMDiskLocation location, int id)
	{
		super();

		this.dir = dir;
		this.location = location;
		this.id = id;

		for (int i = 0; i < 8; i += 1)
		{
			entries[i] = new CBMDiskDirEntry(this, i, id + i);
		}
	}

	public void scan()
	{
		CBMDiskLocation location = getNextDirectoryLocation();

		if ((location.getTrackNr() != 0x00))
		{
			nextDirSector = new CBMDiskDirSector(dir, location, id + 8);

			nextDirSector.scan();
		}
		else
		{
			nextDirSector = null;
		}
	}

	public void format()
	{
		for (CBMDiskDirEntry entry : entries)
		{
			entry.format();
		}

		scan();
	}

	public void list(PrintStream out, boolean listKeys, boolean listDeleted)
	{
		for (CBMDiskDirEntry entry : entries)
		{
			entry.list(out, listKeys, listDeleted);
		}

		if (nextDirSector != null)
		{
			nextDirSector.list(out, listKeys, listDeleted);
		}
	}

	public void mark()
	{
		for (CBMDiskDirEntry entry : entries)
		{
			entry.mark();
		}

		if (nextDirSector != null)
		{
			nextDirSector.mark();
		}
	}

	public CBMDiskDirEntry find(String fileName)
	{
		for (CBMDiskDirEntry entry : entries)
		{
			if (entry.matches(fileName))
			{
				return entry;
			}
		}

		if (nextDirSector != null)
		{
			return nextDirSector.find(fileName);
		}

		return null;
	}

	public CBMDiskDirEntry allocate() throws CBMDiskException
	{
		for (CBMDiskDirEntry entry : entries)
		{
			if (entry.isFree())
			{
				return entry;
			}
		}

		if (nextDirSector == null)
		{
			CBMDiskBAM bam = getDir().getOperator().getBAM();

			CBMDiskLocation location = bam.findNextFreeDirSector(getLocation());

			bam.setSectorUsed(location, true);

			nextDirSector = new CBMDiskDirSector(dir, location, id + 8);
			nextDirSector.format();

			setNextDirectoryLocation(location);
		}

		return nextDirSector.allocate();
	}

	public CBMDiskDir getDir()
	{
		return dir;
	}

	public int getTrackNr()
	{
		return location.getTrackNr();
	}

	public int getSectorNr()
	{
		return location.getSectorNr();
	}

	public CBMDiskLocation getLocation()
	{
		return location;
	}

	public int getId()
	{
		return id;
	}

	/**
	 * Returns the sector of this directory
	 * 
	 * @return the sector of this directory
	 */
	public CBMDiskSector getSector()
	{
		return dir.getDisk().getSector(location);
	}

	public CBMDiskDirEntry[] getEntries()
	{
		return entries;
	}

	public CBMDiskDirSector getNextBlock()
	{
		return nextDirSector;
	}

	public void setNextBlock(CBMDiskDirSector nextBlock)
	{
		nextDirSector = nextBlock;
	}

	public int getNextDirectoryTrackNr()
	{
		return getSector().getNextTrackNr();
	}

	public void setNextDirectoryTrackNr(int nextDirectoryTrackNr)
	{
		getSector().setNextTrackNr(nextDirectoryTrackNr);
	}

	public int getNextDirectorySectorNr()
	{
		return getSector().getNextSectorNr();
	}

	public void setNextDirectorySectorNr(int nextDirectorySectorNr)
	{
		getSector().setNextSectorNr(nextDirectorySectorNr);
	}

	public void setNextDirectoryLocation(CBMDiskLocation nextDirectoryLocation)
	{
		getSector().setNextLocation(nextDirectoryLocation);
	}

	public CBMDiskLocation getNextDirectoryLocation()
	{
		return getSector().getNextLocation();
	}

}
