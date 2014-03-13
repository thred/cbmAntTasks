package org.cbm.ant.cbm.disk;

import java.io.PrintStream;

public class CBMDiskDirBlock
{

	private final CBMDiskDir dir;
	private final int track;
	private final int sector;
	private final int id;
	private final CBMDiskDirEntry[] entries = new CBMDiskDirEntry[8];

	private CBMDiskDirBlock nextBlock;

	public CBMDiskDirBlock(CBMDiskDir dir, int track, int sector, int id)
	{
		super();

		this.dir = dir;
		this.track = track;
		this.sector = sector;
		this.id = id;

		for (int i = 0; i < 8; i += 1)
		{
			entries[i] = new CBMDiskDirEntry(this, i, id + i);
		}
	}

	public void scan()
	{
		int track = getNextDirectoryTrack();
		int sector = getNextDirectorySector();

		if ((track != 0x00))
		{
			nextBlock = new CBMDiskDirBlock(dir, track, sector, id + 8);

			nextBlock.scan();
		}
		else
		{
			nextBlock = null;
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

	public void list(PrintStream out)
	{
		for (CBMDiskDirEntry entry : entries)
		{
			entry.list(out);
		}

		if (nextBlock != null)
		{
			nextBlock.list(out);
		}
	}

	public void mark()
	{
		for (CBMDiskDirEntry entry : entries)
		{
			entry.mark();
		}

		if (nextBlock != null)
		{
			nextBlock.mark();
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

		if (nextBlock != null)
		{
			return nextBlock.find(fileName);
		}

		return null;
	}

	public CBMDiskDirEntry allocate()
	{
		for (CBMDiskDirEntry entry : entries)
		{
			if (entry.isFree())
			{
				return entry;
			}
		}

		if (nextBlock == null)
		{
			// TODO puh!
		}

		return nextBlock.allocate();
	}

	public CBMDiskDir getDir()
	{
		return dir;
	}

	public int getTrack()
	{
		return track;
	}

	public int getId()
	{
		return id;
	}

	public CBMDiskDirEntry[] getEntries()
	{
		return entries;
	}

	public CBMDiskSector getSector()
	{
		return dir.getDisk().getSector(track, sector);
	}

	public CBMDiskDirBlock getNextBlock()
	{
		return nextBlock;
	}

	public void setNextBlock(CBMDiskDirBlock nextBlock)
	{
		this.nextBlock = nextBlock;
	}

	public int getNextDirectoryTrack()
	{
		return getSector().getByte(0x00);
	}

	public void setNextDirectoryTrack(int nextDirectoryTrack)
	{
		getSector().setByte(0x00, nextDirectoryTrack);
	}

	public int getNextDirectorySector()
	{
		return getSector().getByte(0x01);
	}

	public void setNextDirectorySector(int nextDirectorySector)
	{
		getSector().setByte(0x01, nextDirectorySector);
	}

}
