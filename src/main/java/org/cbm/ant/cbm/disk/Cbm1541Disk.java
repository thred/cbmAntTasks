package org.cbm.ant.cbm.disk;

import java.util.Objects;
import java.util.Optional;

public class Cbm1541Disk extends AbstractCbmDisk
{
    protected static final int DOS_VERSION = 0x41;
    protected static final String DOS_TYPE = "2a";

    protected Cbm1541Disk(CbmImage image, CbmSectorLocation headerLocation, CbmSectorRange directoryRange,
        CbmAllocateSectorStrategy allocateSectorStrategy, CbmSectorInterleaves sectorInterleaves)
    {
        super(image, headerLocation, directoryRange, allocateSectorStrategy, sectorInterleaves);
    }

    @Override
    public Cbm1541Disk withAllocateSectorStrategy(CbmAllocateSectorStrategy allocateSectorStrategy)
    {
        return new Cbm1541Disk(image, headerLocation, directoryRange, allocateSectorStrategy, sectorInterleaves);
    }

    @Override
    public Cbm1541Disk withSectorInterleaves(CbmSectorInterleaves sectorInterleaves)
    {
        return new Cbm1541Disk(image, headerLocation, directoryRange, allocateSectorStrategy,
            this.sectorInterleaves.with(sectorInterleaves));
    }

    @Override
    public void format(String diskName, String diskId)
    {
        CbmSectorMap sectorMap = getSectorMap();
        CbmSector headerSector = headerSector();

        headerSector.erase();

        for (CbmSectorLocation location : directoryRange)
        {
            image.sectorAt(location).erase();
        }

        headerSector.setNextLocation(Optional.of(directoryRange.getFromLocation()));

        setDosVersion(DOS_VERSION);

        clearSectorsUsed();

        for (int trackNr = 1; trackNr <= sectorMap.getMaxTrackNr(); trackNr += 1)
        {
            for (int sectorNr = 0; sectorNr <= sectorMap.getMaxSectorNrByTrackNr(trackNr); sectorNr += 1)
            {
                CbmSectorLocation location = CbmSectorLocation.of(trackNr, sectorNr);

                if (sectorMap.contains(location))
                {
                    setSectorUsed(location, false);
                }
            }
        }

        setSectorUsed(headerLocation, true);

        setDiskName(diskName);

        headerSector.setByte(0xa0, 0xa0);
        headerSector.setByte(0xa1, 0xa0);

        setDiskId(diskId);

        headerSector.setByte(0xa4, 0xa0);

        setDosType(DOS_TYPE);

        headerSector.fill(0xa7, 0xa0, 0xab - 0xa7);
        headerSector.fill(0xab, 0x00, 0x100 - 0xab);

        // directory

        CbmDirectorySector directorySector = firstDirectorySector();

        setSectorUsed(directorySector.getLocation(), true);

        directorySector.format();
    }

    @Override
    public String checkFormatted()
    {
        if (!image.isSectorSupported(headerLocation))
        {
            return "The header sector is not supported";
        }

        if (!isSectorUsed(headerLocation))
        {
            return "The header sector is not marked as in-use";
        }

        CbmSector headerSector = headerSector();
        Optional<CbmSectorLocation> headerNextLocation = headerSector.getNextLocation();

        if (!directoryRange.stream().allMatch(image::isSectorSupported))
        {
            return "Not all directory sectors are supported";
        }

        if (headerNextLocation.isEmpty() || !Objects.equals(headerNextLocation.get(), directoryRange.getFromLocation()))
        {
            return "The header does not point to directory sector";
        }

        if (getDosVersion() != DOS_VERSION)
        {
            return String
                .format("The DOS version is invalid: %s != %s", CbmUtils.toHex("$", getDosVersion(), 2),
                    CbmUtils.toHex("$", DOS_VERSION, 2));
        }

        return checkDirectoryFormatted();
    }

    @Override
    public int getDosVersion()
    {
        return headerSector().getByte(0x02);
    }

    @Override
    protected void setDosVersion(int version)
    {
        headerSector().setByte(0x02, version);
    }

    @Override
    public String getDiskName()
    {
        return headerSector().getString(0x90, 16);
    }

    protected void setDiskName(String diskName)
    {
        headerSector().setString(0x90, 16, diskName);
    }

    @Override
    public String getDiskId()
    {
        return headerSector().getString(0xa2, 2);
    }

    protected void setDiskId(String diskId)
    {
        headerSector().setString(0xa2, 2, diskId);
    }

    @Override
    public String getDosType()
    {
        return headerSector().getString(0xa5, 2);
    }

    protected void setDosType(String dosType)
    {
        headerSector().setString(0xa5, 2, dosType);
    }

    @Override
    public boolean isSectorUsed(CbmSectorLocation location)
    {
        if (!image.isSectorSupported(location))
        {
            throw new IllegalArgumentException("Invalid location: " + location);
        }

        CbmSector sector = headerSector();
        int trackNr = location.getTrackNr();
        int sectorNr = location.getSectorNr();
        int offset = getBamSectorOffset(trackNr) + 1 + sectorNr / 8;
        int value = sector.getByte(offset);

        return (value & 1 << sectorNr % 8) == 0;
    }

    @Override
    public void setSectorUsed(CbmSectorLocation location, boolean used)
    {
        if (!image.isSectorSupported(location))
        {
            throw new IllegalArgumentException("Invalid location: " + location);
        }

        CbmSector sector = headerSector();
        int trackNr = location.getTrackNr();
        int sectorNr = location.getSectorNr();
        int offset = getBamSectorOffset(trackNr);
        int value = sector.getByte(offset + 1) | sector.getByte(offset + 2) << 8 | sector.getByte(offset + 3) << 16;

        if (used)
        {
            value &= ~(1 << sectorNr);
        }
        else
        {
            value |= 1 << sectorNr;
        }

        sector.setByte(offset, Integer.bitCount(value));
        sector.setByte(offset + 1, value & 0xff);
        sector.setByte(offset + 2, value >> 8 & 0xff);
        sector.setByte(offset + 3, value >> 16 & 0xff);
    }

    @Override
    public int getFreeSectorCountOfTrack(int trackNr, boolean includeReserved)
    {
        if (!includeReserved && trackNr == directoryRange.getFromLocation().getTrackNr())
        {
            return 0;
        }

        int offset = getBamSectorOffset(trackNr);

        return headerSector().getByte(offset);
    }

    protected int getBamSectorOffset(int trackNr)
    {
        if (trackNr <= 0 || trackNr > 35)
        {
            throw new IllegalArgumentException("Invalid track number (1 - 35): " + trackNr);
        }

        return trackNr * 4;
    }
}
