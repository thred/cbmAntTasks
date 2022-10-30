package org.cbm.ant.cbm.disk;

import java.util.Objects;
import java.util.Optional;

public class Cbm1581Disk extends AbstractCbmDisk
{
    private static final int DOS_VERSION = 0x44;
    private static final String DOS_TYPE = "3d";

    private final CbmSectorLocation lowerBamLocation;
    private final CbmSectorLocation upperBamLocation;

    protected Cbm1581Disk(CbmImage image, CbmSectorLocation headerLocation, CbmSectorLocation lowerBamLocation,
        CbmSectorLocation upperBamLocation, CbmSectorRange directoryRange,
        CbmAllocateSectorStrategy allocateSectorStrategy, CbmSectorInterleaves sectorInterleaves)
    {
        super(image, headerLocation, directoryRange, allocateSectorStrategy, sectorInterleaves);

        this.lowerBamLocation = lowerBamLocation;
        this.upperBamLocation = upperBamLocation;
    }

    @Override
    public Cbm1581Disk withAllocateSectorStrategy(CbmAllocateSectorStrategy allocateSectorStrategy)
    {
        return new Cbm1581Disk(image, headerLocation, lowerBamLocation, upperBamLocation, directoryRange,
            allocateSectorStrategy, sectorInterleaves);
    }

    @Override
    public Cbm1581Disk withSectorInterleaves(CbmSectorInterleaves sectorInterleaves)
    {
        return new Cbm1581Disk(image, headerLocation, lowerBamLocation, upperBamLocation, directoryRange,
            allocateSectorStrategy, this.sectorInterleaves.with(sectorInterleaves));
    }

    @Override
    public void format(String diskName, String diskId)
    {
        CbmSectorMap sectorMap = getSectorMap();
        CbmSector header = headerSector();
        CbmSector lowerBam = lowerBamSector();
        CbmSector upperBam = upperBamSector();

        // clear

        header.erase();
        lowerBam.erase();
        upperBam.erase();

        for (CbmSectorLocation location : directoryRange)
        {
            if (image.isSectorSupported(location))
            {
                sectorAt(location).erase();
            }
        }

        // header

        header.setNextLocation(Optional.of(directoryRange.getFromLocation()));

        setDosVersion(DOS_VERSION);
        setDiskName(diskName);

        header.setByte(0x14, 0xa0);
        header.setByte(0x15, 0xa0);

        setDiskId(diskId);

        header.setByte(0x18, 0xa0);

        setDosType(DOS_TYPE);

        header.fill(0x1b, 0xa0, 0x02);

        // lower bam

        lowerBam.setNextLocation(Optional.of(upperBam.getLocation()));

        setVerifyOn(true);
        setCheckHeaderCrc(true);
        setAutoBootLoader(false);

        // upper bam

        upperBam.setNextLocation(Optional.empty());

        // prepare bam

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
        setSectorUsed(lowerBamLocation, true);
        setSectorUsed(upperBamLocation, true);

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

        if (!image.isSectorSupported(lowerBamLocation))
        {
            return "The lower BAM sector is not supported";
        }

        if (!isSectorUsed(lowerBamLocation))
        {
            return "The lower BAM sector is not marked as in-use";
        }

        if (!image.isSectorSupported(upperBamLocation))
        {
            return "The upper BAM sector is not supported";
        }

        if (!isSectorUsed(upperBamLocation))
        {
            return "The upper BAM sector is not marked as in-use";
        }

        if (!directoryRange.stream().allMatch(image::isSectorSupported))
        {
            return "Not all directory sectors are supported";
        }

        CbmSector headerSector = headerSector();
        Optional<CbmSectorLocation> headerNextLocation = headerSector.getNextLocation();

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

    protected CbmSector lowerBamSector()
    {
        return sectorAt(lowerBamLocation);
    }

    protected CbmSector upperBamSector()
    {
        return sectorAt(upperBamLocation);
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

        CbmSector lowerBamSector = lowerBamSector();

        lowerBamSector.setByte(0x02, version);
        lowerBamSector.setByte(0x03, version ^ 0xff);

        CbmSector upperBamSector = upperBamSector();

        upperBamSector.setByte(0x02, version);
        upperBamSector.setByte(0x03, version ^ 0xff);

    }

    @Override
    public String getDiskName()
    {
        return headerSector().getString(0x04, 16);
    }

    protected void setDiskName(String diskName)
    {
        headerSector().setString(0x04, 16, diskName);
    }

    @Override
    public String getDiskId()
    {
        return headerSector().getString(0x16, 2);
    }

    protected void setDiskId(String diskId)
    {
        headerSector().setString(0x16, 2, diskId);
        lowerBamSector().setString(0x04, 2, diskId);
        upperBamSector().setString(0x04, 2, diskId);
    }

    @Override
    public String getDosType()
    {
        return headerSector().getString(0x19, 2);
    }

    protected void setDosType(String dosType)
    {
        headerSector().setString(0x19, 2, dosType);
    }

    public boolean isVerifyOn()
    {
        return lowerBamSector().isBit(0x06, 7);
    }

    public void setVerifyOn(boolean verifyOn)
    {
        lowerBamSector().setBit(0x06, 7, verifyOn);
        upperBamSector().setBit(0x06, 7, verifyOn);
    }

    public boolean isCheckHeaderCrc()
    {
        return lowerBamSector().isBit(0x06, 6);
    }

    public void setCheckHeaderCrc(boolean checkHeaderCrc)
    {
        lowerBamSector().setBit(0x06, 6, checkHeaderCrc);
        upperBamSector().setBit(0x06, 6, checkHeaderCrc);
    }

    public boolean isAutoBootLoader()
    {
        return lowerBamSector().getByte(0x07) != 0;
    }

    public void setAutoBootLoader(boolean autoBootLoader)
    {
        lowerBamSector().setByte(0x07, autoBootLoader ? 1 : 0);
        upperBamSector().setByte(0x07, autoBootLoader ? 1 : 0);
    }

    @Override
    public boolean isSectorUsed(CbmSectorLocation location)
    {
        if (!image.isSectorSupported(location))
        {
            throw new IllegalArgumentException("Invalid location: " + location);
        }

        int trackNr = location.getTrackNr();
        int sectorNr = location.getSectorNr();
        CbmSector sector = getBamSector(trackNr);
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

        int trackNr = location.getTrackNr();
        int sectorNr = location.getSectorNr();
        CbmSector sector = getBamSector(trackNr);
        int offset = getBamSectorOffset(trackNr);
        int value = sector.getByte(offset + 1 + sectorNr / 8);

        if (used)
        {
            value &= ~(1 << sectorNr % 8);
        }
        else
        {
            value |= 1 << sectorNr % 8;
        }

        sector.setByte(offset + 1 + sectorNr / 8, value);

        int usedSectors = 0;

        for (int i = 1; i < 6; ++i)
        {
            usedSectors += Integer.bitCount(sector.getByte(offset + i));
        }

        sector.setByte(offset, usedSectors);
    }

    @Override
    public int getFreeSectorCountOfTrack(int trackNr, boolean includeReserved)
    {
        if (!includeReserved && trackNr == directoryRange.getFromLocation().getTrackNr())
        {
            return 0;
        }

        CbmSector sector = getBamSector(trackNr);
        int offset = getBamSectorOffset(trackNr);

        return sector.getByte(offset);
    }

    protected CbmSector getBamSector(int trackNr)
    {
        if (trackNr <= 0 || trackNr > 80)
        {
            throw new IllegalArgumentException("Invalid track number (1 - 80): " + trackNr);
        }

        return sectorAt(trackNr <= 40 ? lowerBamLocation : upperBamLocation);
    }

    protected int getBamSectorOffset(int trackNr)
    {
        if (trackNr <= 0 || trackNr > 80)
        {
            throw new IllegalArgumentException("Invalid track number (1 - 80): " + trackNr);
        }

        return 0x10 + (trackNr - 1) % 40 * 6;
    }
}
