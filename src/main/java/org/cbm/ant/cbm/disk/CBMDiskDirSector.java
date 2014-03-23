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

    public void list(PrintStream out)
    {
        for (CBMDiskDirEntry entry : entries)
        {
            entry.list(out);
        }

        if (nextDirSector != null)
        {
            nextDirSector.list(out);
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

    public CBMDiskDirEntry allocate()
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
            // TODO puh!
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
        return getSector().getByte(0x00);
    }

    public void setNextDirectoryTrack(int nextDirectoryTrack)
    {
        getSector().setByte(0x00, nextDirectoryTrack);
    }

    public int getNextDirectorySectorNr()
    {
        return getSector().getByte(0x01);
    }

    public void setNextDirectorySector(int nextDirectorySector)
    {
        getSector().setByte(0x01, nextDirectorySector);
    }

    public CBMDiskLocation getNextDirectoryLocation()
    {
        return new CBMDiskLocation(getNextDirectoryTrackNr(), getNextDirectorySectorNr());
    }

}
