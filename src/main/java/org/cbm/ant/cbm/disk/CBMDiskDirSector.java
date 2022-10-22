package org.cbm.ant.cbm.disk;

import java.io.PrintStream;
import java.util.Optional;

public class CBMDiskDirSector
{

    private final CBMDiskDir dir;
    private final CBMDiskLocation location;
    private final int id;
    private final CBMDiskDirEntry[] entries = new CBMDiskDirEntry[8];

    private Optional<CBMDiskDirSector> nextDirSector;

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
        Optional<CBMDiskLocation> optionalLocation = getNextDirectoryLocation();

        nextDirSector = optionalLocation.map(location -> {
            CBMDiskDirSector dirSector = new CBMDiskDirSector(dir, location, id + 8);

            dirSector.scan();

            return dirSector;
        });
    }

    public void format()
    {
        for (CBMDiskDirEntry entry : entries)
        {
            entry.format();
        }

        setNextDirectoryLocation(new CBMDiskLocation(0x00, 0xff));

        scan();
    }

    public void list(PrintStream out, boolean listKeys, boolean listDeleted)
    {
        for (CBMDiskDirEntry entry : entries)
        {
            entry.list(out, listKeys, listDeleted);
        }

        nextDirSector.ifPresent(dirSector -> dirSector.list(out, listKeys, listDeleted));
    }

    public void mark()
    {
        for (CBMDiskDirEntry entry : entries)
        {
            entry.mark();
        }

        nextDirSector.ifPresent(CBMDiskDirSector::mark);
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

        return nextDirSector.map(dirSector -> dirSector.find(fileName)).orElse(null);
    }

    public CBMDiskDirEntry allocate(CBMSectorInterleaves sectorInterleaves) throws CBMDiskException
    {
        for (CBMDiskDirEntry entry : entries)
        {
            if (entry.isFree())
            {
                return entry;
            }
        }

        if (nextDirSector.isEmpty())
        {
            CBMDiskBAM bam = getDir().getOperator().getBAM();
            CBMDiskLocation location = bam.findNextFreeDirSector(getLocation(), sectorInterleaves);

            bam.setSectorUsed(location, true);

            CBMDiskDirSector dirSector = new CBMDiskDirSector(dir, location, id + 8);

            dirSector.format();

            setNextDirectoryLocation(location);

            nextDirSector = Optional.of(dirSector);
        }

        return nextDirSector.get().allocate(sectorInterleaves);
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

    public Optional<CBMDiskDirSector> getNextBlock()
    {
        return nextDirSector;
    }

    public void setNextBlock(CBMDiskDirSector nextBlock)
    {
        nextDirSector = Optional.ofNullable(nextBlock);
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

    public Optional<CBMDiskLocation> getNextDirectoryLocation()
    {
        return getSector().getNextLocation();
    }

}
