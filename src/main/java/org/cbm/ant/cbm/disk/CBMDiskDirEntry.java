package org.cbm.ant.cbm.disk;

import java.io.PrintStream;

import org.cbm.ant.util.WildcardUtils;

public class CBMDiskDirEntry
{

	private static final String LIST_ENTRY_WITH_KEY = "%-3d  %-18s %s (%c)\n";
	private static final String LIST_ENTRY_WITHOUT_KEY = "%-3d  %-18s %s\n";

	private final CBMDiskDirSector block;
	private final int index;
	private final int id;

	public CBMDiskDirEntry(CBMDiskDirSector block, int index, int id)
	{
		super();

		this.block = block;
		this.index = index;
		this.id = id;
	}

	public void format()
	{
		setNextDirectoryTrack(0x00);
		setNextDirectorySector(0x00);
		getSector().setByte(getPosition(0x02), 0x00);
		setFileTrackNr(0x00);
		setFileSectorNr(0x00);
		getSector().fill(getPosition(0x05), 0x0f, 0xa0);
		getSector().fill(getPosition(0x15), 0x0b, 0x00);
	}

	public void list(PrintStream out, boolean listKeys, boolean listDeleted)
	{
		if ((isFree()) && (!listDeleted))
		{
			return;
		}

		String type = getFileType().getName();

		if (isFileTypeLocked())
		{
			type = ">" + type;
		}

		if (!isFileTypeClosed())
		{
			type = "*" + type;
		}

		out.printf((listKeys) ? LIST_ENTRY_WITH_KEY : LIST_ENTRY_WITHOUT_KEY, getFileSize(),
				CBMDiskUtil.apostrophes(getFileName()), type, CBMDiskUtil.id2Key(id));
	}

	public void mark()
	{
		int fileSize = getFileSize();

		if (fileSize <= 0)
		{
			return;
		}

		int track = getFileTrackNr();
		int sector = getFileSectorNr();

		if (track <= 0)
		{
			return;
		}

		block.getDir().getDisk().mark(track, sector, id);
	}

	public boolean matches(String fileName)
	{
		String currentFileName = getFileName().replace('\u00a0', ' ').trim();

		return WildcardUtils.match(currentFileName, fileName);
	}

	public boolean isFree()
	{
		return (getFileType() == CBMFileType.DEL);
	}

	public int getIndex()
	{
		return index;
	}

	public int getId()
	{
		return id;
	}

	protected CBMDiskSector getSector()
	{
		return block.getSector();
	}

	protected int getPosition(int pos)
	{
		return (index * 0x20) + pos;
	}

	public int getNextDirectoryTrack()
	{
		return getSector().getByte(getPosition(0x00));
	}

	public void setNextDirectoryTrack(int nextDirectoryTrack)
	{
		getSector().setByte(getPosition(0x00), nextDirectoryTrack);
	}

	public int getNextDirectorySector()
	{
		return getSector().getByte(getPosition(0x01));
	}

	public void setNextDirectorySector(int nextDirectorySector)
	{
		getSector().setByte(getPosition(0x01), nextDirectorySector);
	}

	public CBMFileType getFileType()
	{
		return CBMFileType.toCBMFileType(getSector().getByte(getPosition(0x02)));
	}

	public void setFileType(CBMFileType fileType)
	{
		getSector().setByte(getPosition(0x02), (getSector().getByte(getPosition(0x02)) & 0xf8) | fileType.getType());
	}

	public boolean isFileTypeLocked()
	{
		return getSector().isBit(getPosition(0x02), 6);
	}

	public void setFileTypeLocked(boolean locked)
	{
		getSector().setBit(getPosition(0x02), 6, locked);
	}

	public boolean isFileTypeClosed()
	{
		return getSector().isBit(getPosition(0x02), 7);
	}

	public void setFileTypeClosed(boolean closed)
	{
		getSector().setBit(getPosition(0x02), 7, closed);
	}

	public int getFileTrackNr()
	{
		return getSector().getByte(getPosition(0x03));
	}

	public void setFileTrackNr(int nextDirectoryTrackNr)
	{
		getSector().setByte(getPosition(0x03), nextDirectoryTrackNr);
	}

	public int getFileSectorNr()
	{
		return getSector().getByte(getPosition(0x04));
	}

	public void setFileSectorNr(int nextDirectorySectorNr)
	{
		getSector().setByte(getPosition(0x04), nextDirectorySectorNr);
	}

	public CBMDiskLocation getFileLocation()
	{
		return new CBMDiskLocation(getFileTrackNr(), getFileSectorNr());
	}

	public void setFileLocation(CBMDiskLocation location)
	{
		setFileTrackNr(location.getTrackNr());
		setFileSectorNr(location.getSectorNr());
	}

	public String getFileName()
	{
		return CBMDiskUtil.fromCBMDOSName(getSector().getBytes(getPosition(0x05), 16));
	}

	public void setFileName(String fileName)
	{
		getSector().setBytes(getPosition(0x05), CBMDiskUtil.toCBMDOSName(fileName, 16));
	}

	public int getRELFileTrackNr()
	{
		return getSector().getByte(getPosition(0x15));
	}

	public void setRELFileTrackNr(int nextDirectoryTrackNr)
	{
		getSector().setByte(getPosition(0x15), nextDirectoryTrackNr);
	}

	public int getRELFileSectorNr()
	{
		return getSector().getByte(getPosition(0x16));
	}

	public void setRELFileSectorNr(int nextDirectorySectorNr)
	{
		getSector().setByte(getPosition(0x16), nextDirectorySectorNr);
	}

	public CBMDiskLocation getRELFileLocation()
	{
		return new CBMDiskLocation(getRELFileTrackNr(), getRELFileSectorNr());
	}

	public void setRELFileLocation(CBMDiskLocation location)
	{
		setRELFileTrackNr(location.getTrackNr());
		setRELFileSectorNr(location.getSectorNr());
	}

	public int getFileSize()
	{
		return getSector().getWord(getPosition(0x1e));
	}

	public void setFileSize(int fileSize)
	{
		getSector().setWord(getPosition(0x1e), fileSize);
	}

}
