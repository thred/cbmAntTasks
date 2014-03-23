package org.cbm.ant.cbm.disk;

public class CBMDiskBAM
{

    private static final int DEFAULT_DOS_VERSION = 0x41;
    private static final String DEFAULT_DISK_ID = "01";
    private static final String DEFAULT_DOS_TYPE = "a2";

    private final CBMDiskOperator operator;

    public CBMDiskBAM(CBMDiskOperator operator)
    {
        super();

        this.operator = operator;
    }

    private CBMDisk getDisk()
    {
        return operator.getDisk();
    }

    public CBMDiskSector getBAMSector()
    {
        CBMDisk disk = getDisk();

        return disk.getSector(disk.getDirTrackNr(), 0x00);
    }

    /**
     * Formats the BAM
     * 
     * @param diskName the name of the disk
     */
    public void format(String diskName)
    {
        CBMDisk disk = getDisk();
        CBMDiskSector bam = getBAMSector();

        bam.clear();

        setDirTrackNr(disk.getDirTrackNr());
        setDirSectorNr(1);
        setDOSVersion(DEFAULT_DOS_VERSION);

        for (int trackNr = 1; trackNr <= disk.getType().getNumberOfTracks(); trackNr += 1)
        {
            for (int sectorNr = 0; sectorNr < disk.getType().getNumberOfSectors(trackNr); sectorNr += 1)
            {
                setSectorUsed(trackNr, sectorNr, false);
            }
        }

        setSectorUsed(bam.getTrackNr(), bam.getSectorNr(), true);
        setDiskName(diskName);

        bam.setByte(0xa0, 0xa0);
        bam.setByte(0xa1, 0xa0);

        setDiskID(DEFAULT_DISK_ID);

        bam.setByte(0xa4, 0xa0);

        setDOSType(DEFAULT_DOS_TYPE);

        bam.fill(0xa7, 9, 0xa0);
    }

    public CBMDiskLocation findFreeSector()
    {
        if (isFull())
        {
            throw new IllegalStateException("Disk is full");
        }

        int offset = 1;
        int dirTrackNr = operator.getDisk().getDirTrackNr();

        while (offset < 256)
        {
            if (((dirTrackNr - offset) > 0) && (getFreeSectorsOfTrack(dirTrackNr - offset) > 0))
            {
                return findFreeSector(dirTrackNr - offset, 0, 0);
            }

            if (((dirTrackNr + offset) <= operator.getDisk().getType().getNumberOfTracks())
                && (getFreeSectorsOfTrack(dirTrackNr + offset) > 0))
            {
                return findFreeSector(dirTrackNr + offset, 0, 0);
            }

            offset += 1;
        }

        throw new IllegalStateException("Did not find the free sector");
    }

    public CBMDiskLocation findNextFreeSector(CBMDiskLocation location)
    {
        int trackNr = location.getTrackNr();
        int sectorNr =
            (location.getSectorNr() + getDisk().getSectorNrInterleave())
                % operator.getDisk().getType().getNumberOfSectors(trackNr);
        int trackNrIncrement = (trackNr < getDisk().getDirTrackNr()) ? -1 : 1;

        return findFreeSector(trackNr, sectorNr, trackNrIncrement);
    }

    protected CBMDiskLocation findFreeSector(int trackNr, int sectorNr, int trackNrIncrement)
    {
        if (isFull())
        {
            throw new IllegalStateException("Disk is full");
        }

        int numberOfTracks = operator.getDisk().getType().getNumberOfTracks();

        while ((getDisk().getDirTrackNr() == trackNr) || (getFreeSectorsOfTrack(trackNr) <= 0))
        {
            if (trackNrIncrement == 0)
            {
                throw new IllegalArgumentException("No track number increment specified");
            }

            trackNr = ((((trackNr + trackNrIncrement) - 1) + numberOfTracks) % numberOfTracks) + 1;
        }

        int numberOfSectors = operator.getDisk().getType().getNumberOfSectors(trackNr);

        while (isSectorUsed(trackNr, sectorNr))
        {
            sectorNr = (((sectorNr + 1) - 1) % numberOfSectors) + 1;
        }

        return new CBMDiskLocation(trackNr, sectorNr);
    }

    public int getDirTrackNr()
    {
        return getBAMSector().getByte(0x00);
    }

    public void setDirTrackNr(int trackNr)
    {
        getBAMSector().setByte(0x00, trackNr);
    }

    public int getDirSectorNr()
    {
        return getBAMSector().getByte(0x01);
    }

    public void setDirSectorNr(int sectorNr)
    {
        getBAMSector().setByte(0x01, sectorNr);
    }

    public CBMDiskLocation getDirLocation()
    {
        return new CBMDiskLocation(getDirTrackNr(), getDirSectorNr());
    }

    public int getDOSVersion()
    {
        return getBAMSector().getByte(0x02);
    }

    public void setDOSVersion(int dosVersion)
    {
        getBAMSector().setByte(0x02, dosVersion);
    }

    public boolean isSectorUsed(CBMDiskLocation location)
    {
        return isSectorUsed(location.getTrackNr(), location.getSectorNr());
    }

    public boolean isSectorUsed(int trackNr, int sectorNr)
    {
        int position = getBAMSectorPosition(trackNr) + 1 + (sectorNr / 8);
        int value = getBAMSector().getByte(position);

        return (value & (1 << (sectorNr % 8))) == 0;
    }

    public int getFreeSectorsOfTrack(int trackNr)
    {
        int position = getBAMSectorPosition(trackNr);

        return getBAMSector().getByte(position);
    }

    public int getFreeSectors()
    {
        int result = 0;
        CBMDisk disk = getDisk();
        CBMDiskType type = disk.getType();
        int dirTrack = disk.getDirTrackNr();

        for (int trackNr = 1; trackNr <= type.getNumberOfTracks(); trackNr += 1)
        {
            if (trackNr != dirTrack)
            {
                result += getFreeSectorsOfTrack(trackNr);
            }
        }

        return result;
    }

    public boolean isFull()
    {
        return getFreeSectors() == 0;
    }

    public void setSectorUsed(CBMDiskLocation location, boolean used)
    {
        setSectorUsed(location.getTrackNr(), location.getSectorNr(), used);
    }

    public void setSectorUsed(int trackNr, int sectorNr, boolean used)
    {
        int position = getBAMSectorPosition(trackNr);
        CBMDiskSector bam = getBAMSector();
        int value = bam.getByte(position + 1) | (bam.getByte(position + 2) << 8) | (bam.getByte(position + 3) << 16);

        if (used)
        {
            value &= ~(1 << sectorNr);
        }
        else
        {
            value |= (1 << sectorNr);
        }

        bam.setByte(position, Integer.bitCount(value));
        bam.setByte(position + 1, value & 0xff);
        bam.setByte(position + 2, (value >> 8) & 0xff);
        bam.setByte(position + 3, (value >> 16) & 0xff);
    }

    protected int getBAMSectorPosition(int trackNr)
    {
        if (trackNr <= 0)
        {
            throw new IllegalArgumentException("Tracks start with index 1");
        }
        if (trackNr <= 35)
        {
            return 0x04 + ((trackNr - 1) * 4);
        }
        else
        {
            return 0xab + ((trackNr - 36) * 4);
        }
    }

    public String getDiskName()
    {
        return getBAMSector().getString(0x90, 0x10);
    }

    public void setDiskName(String diskName)
    {
        getBAMSector().setString(0x90, 0x10, diskName);
    }

    public String getDiskID()
    {
        return CBMDiskUtil.fromCBMDOSName(getBAMSector().getBytes(0xa2, 0x02));
    }

    public void setDiskID(String diskID)
    {
        getBAMSector().setString(0xa2, 0x02, diskID);
    }

    public String getDOSType()
    {
        return CBMDiskUtil.fromCBMDOSName(getBAMSector().getBytes(0xa5, 0x02));
    }

    public void setDOSType(String dosType)
    {
        getBAMSector().setBytes(0xa5, CBMDiskUtil.toCBMDOSName(dosType, 0x02));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        CBMDisk disk = getDisk();

        operator.getDir().mark();

        StringBuilder builder = new StringBuilder();

        builder.append(String.format("BAM of Disk %s\n", getDiskName()));
        builder.append("----------------------------------------\n");
        builder
            .append(String.format("Disk Type: %s (%d tracks)\n", disk.getType(), disk.getType().getNumberOfTracks()));
        builder.append(String.format("Directory track / sector: %d / %d\n", getDirTrackNr(), getDirSectorNr()));
        builder.append(String.format("Disk DOS Version: $%2x\n", getDOSVersion()));
        builder.append(String.format("Disk ID / DOS Type: %s / %s\n", getDiskID(), getDOSType()));
        builder.append("----------------------------------------\n");

        for (int i = 1; i <= disk.getType().getNumberOfTracks(); i += 1)
        {
            builder.append(String.format("Track %2d ", i));

            for (int j = 0; j < disk.getNumberOfSectors(i); j += 1)
            {
                if (isSectorUsed(i, j))
                {
                    builder.append(CBMDiskUtil.id2Key(disk.getSector(i, j).getMark()));
                }
                else
                {
                    builder.append("-");
                }
            }

            builder.append(String.format(" (%d sectors free)\n", getFreeSectorsOfTrack(i)));
        }

        return builder.toString();
    }

}
