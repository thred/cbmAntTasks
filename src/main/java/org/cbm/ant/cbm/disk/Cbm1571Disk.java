package org.cbm.ant.cbm.disk;

import java.util.Objects;
import java.util.Optional;

public class Cbm1571Disk extends AbstractCbmDisk
{
    private static final int DOS_VERSION = 0x41;
    private static final String DOS_TYPE = "2a";

    private final CbmSectorLocation secondBamLocation;

    protected Cbm1571Disk(CbmImage image, CbmSectorLocation headerLocation, CbmSectorLocation secondBamLocation,
        CbmSectorRange directoryRange, CbmAllocateSectorStrategy allocateSectorStrategy,
        CbmSectorInterleaves sectorInterleaves)
    {
        super(image, headerLocation, directoryRange, allocateSectorStrategy, sectorInterleaves);

        this.secondBamLocation = secondBamLocation;
    }

    @Override
    public Cbm1571Disk withAllocateSectorStrategy(CbmAllocateSectorStrategy allocateSectorStrategy)
    {
        return new Cbm1571Disk(image, headerLocation, secondBamLocation, directoryRange, allocateSectorStrategy,
            sectorInterleaves);
    }

    @Override
    public Cbm1571Disk withSectorInterleaves(CbmSectorInterleaves sectorInterleaves)
    {
        return new Cbm1571Disk(image, headerLocation, secondBamLocation, directoryRange, allocateSectorStrategy,
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
        setDoubleSided(true);

        clearSectorsUsed();

        for (int trackNr = 1; trackNr <= sectorMap.getMaxTrackNr(); ++trackNr)
        {
            for (int sectorNr = 0; sectorNr <= sectorMap.getMaxSectorNrByTrackNr(trackNr); ++sectorNr)
            {
                CbmSectorLocation location = CbmSectorLocation.of(trackNr, sectorNr);

                if (sectorMap.contains(location))
                {
                    setSectorUsed(location, false);
                }
            }
        }

        setSectorUsed(headerLocation, true);
        setSectorUsed(secondBamLocation, true);

        for (int sectorNr = 0; sectorNr <= sectorMap
            .getMaxSectorNrByTrackNr(secondBamLocation.getTrackNr()); ++sectorNr)
        {
            setSectorUsed(secondBamLocation.withSectorNr(sectorNr), true);
        }

        setDiskName(diskName);

        headerSector.setByte(0xa0, 0xa0);
        headerSector.setByte(0xa1, 0xa0);

        setDiskId(diskId);

        headerSector.setByte(0xa4, 0xa0);

        setDosType(DOS_TYPE);

        headerSector.fill(0xa7, 0xa0, 0xab - 0xa7);
        headerSector.fill(0xab, 0x00, 0xdd - 0xab);

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

        boolean doubleSided = isDoubleSided();

        if (!doubleSided)
        {
            return "Single sided 1571 images are not supported, currently";
        }

        if (!image.isSectorSupported(secondBamLocation))
        {
            return "The second BAM sector is not supported";
        }

        //        if (!isSectorUsed(secondBamLocation))
        //        {
        //            return "The second BAM sector is not marked as in-use";
        //        }

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

        //        for (int sectorNr = 0; sectorNr < sectorMap.getMaxSectorNrByTrackNr(secondBamLocation.getTrackNr()); ++sectorNr)
        //        {
        //            if (!isSectorUsed(secondBamLocation.withSectorNr(sectorNr)))
        //            {
        //                return "The sector "
        //                    + secondBamLocation.withSectorNr(sectorNr)
        //                    + " of the track holding the second BAM is not marked as in-use";
        //            }
        //        }

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

    public boolean isDoubleSided()
    {
        return headerSector().getByte(0x03) == 0x80;
    }

    public void setDoubleSided(boolean doubleSided)
    {
        headerSector().setByte(0x03, doubleSided ? 0x80 : 0x00);
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

        int trackNr = location.getTrackNr();
        int sectorNr = location.getSectorNr();
        CbmSector sector = getBamSector(trackNr);
        int offset = getBamSectorUseOffset(trackNr) + sectorNr / 8;
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
        int offset = getBamSectorUseOffset(trackNr);
        int value = sector.getByte(offset) | sector.getByte(offset + 1) << 8 | sector.getByte(offset + 2) << 16;

        if (used)
        {
            value &= ~(1 << sectorNr);
        }
        else
        {
            value |= 1 << sectorNr;
        }

        headerSector().setByte(getBamSectorCountOffset(trackNr), Integer.bitCount(value));
        sector.setByte(offset, value & 0xff);
        sector.setByte(offset + 1, value >> 8 & 0xff);
        sector.setByte(offset + 2, value >> 16 & 0xff);
    }

    @Override
    public int getFreeSectorCountOfTrack(int trackNr, boolean includeReserved)
    {
        if (trackNr <= 0 || trackNr > 70)
        {
            throw new IllegalArgumentException("Invalid track number (1 - 70): " + trackNr);
        }

        if (!includeReserved
            && (trackNr == directoryRange.getFromLocation().getTrackNr() || trackNr == secondBamLocation.getTrackNr()))
        {
            return 0;
        }

        return headerSector().getByte(getBamSectorCountOffset(trackNr));
    }

    protected CbmSector getBamSector(int trackNr)
    {
        if (trackNr <= 0 || trackNr > 70)
        {
            throw new IllegalArgumentException("Invalid track number (1 - 70): " + trackNr);
        }

        return sectorAt(trackNr <= 35 ? headerLocation : secondBamLocation);
    }

    protected int getBamSectorCountOffset(int trackNr)
    {
        if (trackNr <= 0 || trackNr > 70)
        {
            throw new IllegalArgumentException("Invalid track number (1 - 70): " + trackNr);
        }

        return trackNr <= 35 ? trackNr * 4 : 0xdd + trackNr - 36;
    }

    protected int getBamSectorUseOffset(int trackNr)
    {
        if (trackNr <= 0 || trackNr > 70)
        {
            throw new IllegalArgumentException("Invalid track number (1 - 70): " + trackNr);
        }

        if (trackNr > 35)
        {
            return (trackNr - 36) * 3;
        }

        return trackNr * 4 + 1;
    }
}
