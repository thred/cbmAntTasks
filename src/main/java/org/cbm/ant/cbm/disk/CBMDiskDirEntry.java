package org.cbm.ant.cbm.disk;

import java.io.PrintStream;

import org.cbm.ant.util.WildcardUtils;

public class CBMDiskDirEntry
{

    private static final int FILE_TYPE_POS = 0x02;
    private static final int TRACK_NR_POS = 0x03;
    private static final int SECTOR_NR_POS = 0x04;
    private static final int FILE_NAME_POS = 0x05;
    private static final int FILE_NAME_LENGTH = 16;
    private static final int REL_TRACK_NR_POS = 0x15;
    private static final int REL_SECTOR_NR_POS = 0x16;
    private static final int REL_FILE_RECORD_LENGTH_POS = 0x17;
    private static final int FILE_SIZE_POS = 0x1e;

    private static final int ENTRY_LENGTH = 0x20;

    private static final String LIST_ENTRY_WITH_KEY = "%-3d  %-18s %s [%s] (%c)\n";
    private static final String LIST_ENTRY_WITHOUT_KEY = "%-3d  %-18s %s [%s]\n";

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
        if (index > 0)
        {
            getSector().fill(getPosition(0), 2, 0x00);
        }

        getSector().setByte(getPosition(FILE_TYPE_POS), CBMFileType.DEL.getType());
        setFileTrackNr(0x00);
        setFileSectorNr(0x00);
        getSector().fill(getPosition(FILE_NAME_POS), FILE_NAME_LENGTH, 0xa0);
        getSector().fill(getPosition(REL_TRACK_NR_POS), ENTRY_LENGTH - REL_TRACK_NR_POS, 0x00);
    }

    public void list(PrintStream out, boolean listKeys, boolean listDeleted)
    {
        if (isFree() && !listDeleted)
        {
            return;
        }

        out
            .printf(listKeys ? LIST_ENTRY_WITH_KEY : LIST_ENTRY_WITHOUT_KEY, getFileSize(),
                CBMDiskUtil.apostrophes(getFileName()), getFileTypeDescription(), getFileLocation(),
                CBMDiskUtil.id2Key(id));
    }

    private String getFileTypeDescription()
    {
        String type = getFileType().getName();

        if (isFileTypeLocked())
        {
            type = ">" + type;
        }

        if (!isFileTypeClosed())
        {
            type = "*" + type;
        }
        return type;
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
        return getFileType() == CBMFileType.DEL;
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
        return index * ENTRY_LENGTH + pos;
    }

    public CBMFileType getFileType()
    {
        return CBMFileType.toCBMFileType(getSector().getByte(getPosition(FILE_TYPE_POS)));
    }

    public void setFileType(CBMFileType fileType)
    {
        getSector()
            .setByte(getPosition(FILE_TYPE_POS),
                getSector().getByte(getPosition(FILE_TYPE_POS)) & 0xf8 | fileType.getType());
    }

    public boolean isFileTypeLocked()
    {
        return getSector().isBit(getPosition(FILE_TYPE_POS), 6);
    }

    public void setFileTypeLocked(boolean locked)
    {
        getSector().setBit(getPosition(FILE_TYPE_POS), 6, locked);
    }

    public boolean isFileTypeClosed()
    {
        return getSector().isBit(getPosition(FILE_TYPE_POS), 7);
    }

    public void setFileTypeClosed(boolean closed)
    {
        getSector().setBit(getPosition(FILE_TYPE_POS), 7, closed);
    }

    public int getFileTrackNr()
    {
        return getSector().getByte(getPosition(TRACK_NR_POS));
    }

    public void setFileTrackNr(int nextDirectoryTrackNr)
    {
        getSector().setByte(getPosition(TRACK_NR_POS), nextDirectoryTrackNr);
    }

    public int getFileSectorNr()
    {
        return getSector().getByte(getPosition(SECTOR_NR_POS));
    }

    public void setFileSectorNr(int nextDirectorySectorNr)
    {
        getSector().setByte(getPosition(SECTOR_NR_POS), nextDirectorySectorNr);
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
        return CBMDiskUtil.fromCBMDOSName(getSector().getBytes(getPosition(FILE_NAME_POS), FILE_NAME_LENGTH));
    }

    public void setFileName(String fileName)
    {
        getSector().setBytes(getPosition(FILE_NAME_POS), CBMDiskUtil.toCBMDOSName(fileName, FILE_NAME_LENGTH));
    }

    public int getRELFileTrackNr()
    {
        return getSector().getByte(getPosition(REL_TRACK_NR_POS));
    }

    public void setRELFileTrackNr(int nextDirectoryTrackNr)
    {
        getSector().setByte(getPosition(REL_TRACK_NR_POS), nextDirectoryTrackNr);
    }

    public int getRELFileSectorNr()
    {
        return getSector().getByte(getPosition(REL_SECTOR_NR_POS));
    }

    public void setRELFileSectorNr(int nextDirectorySectorNr)
    {
        getSector().setByte(getPosition(REL_SECTOR_NR_POS), nextDirectorySectorNr);
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

    public int getRELFileRecordLength()
    {
        return getSector().getByte(getPosition(REL_FILE_RECORD_LENGTH_POS));
    }

    public void setRELFileRectorLength(int nextDirectorySectorNr)
    {
        getSector().setByte(getPosition(REL_FILE_RECORD_LENGTH_POS), nextDirectorySectorNr);
    }

    public int getFileSize()
    {
        return getSector().getWord(getPosition(FILE_SIZE_POS));
    }

    public void setFileSize(int fileSize)
    {
        getSector().setWord(getPosition(FILE_SIZE_POS), fileSize);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName() + " {\n");

        builder.append(String.format("\tname: %s\n", getFileName()));
        builder.append(String.format("\ttype: %s\n", getFileTypeDescription()));
        builder.append(String.format("\tsize: %d blocks\n", getFileSize()));
        builder.append(String.format("\tlocation: %s\n", getFileLocation()));
        builder.append("}");

        return builder.toString();
    }

}
