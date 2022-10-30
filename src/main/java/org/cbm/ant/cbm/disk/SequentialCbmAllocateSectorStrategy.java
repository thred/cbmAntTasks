package org.cbm.ant.cbm.disk;

import java.util.Iterator;
import java.util.Optional;

import org.cbm.ant.cbm.disk.CbmIOException.Type;

public class SequentialCbmAllocateSectorStrategy implements CbmAllocateSectorStrategy
{
    @Override
    public CbmSector allocateDirectorySector(CbmDisk disk) throws CbmIOException
    {
        CbmSector sector = null;
        Iterator<CbmSector> iterator = disk.iterateSectorChainAt(disk.getDirectoryLocation());

        while (iterator.hasNext())
        {
            sector = iterator.next();
        }

        CbmSectorInterleaves sectorInterleaves = disk.getSectorInterleaves();
        CbmSectorLocation location = findFreeSectorOfTrack(disk, sector.getLocation(),
            sectorInterleaves.getInterleave(sector.getLocation().getTrackNr()))
                .orElseThrow(() -> new CbmIOException(Type.DIRECTORY_FULL));

        sector.setNextLocation(Optional.of(location));
        disk.setSectorUsed(location, true);

        sector = disk.sectorAt(location);

        new CbmDirectorySector(disk, sector).format();

        return sector;
    }

    @Override
    public CbmSector allocateFileSector(CbmDisk disk, Optional<CbmSectorLocation> optionalLocation, boolean subsequent)
        throws CbmIOException
    {
        CbmSectorMap sectorMap = disk.getSectorMap();
        CbmSectorLocation directoryLocation = disk.getDirectoryLocation();
        CbmSectorLocation location = optionalLocation.orElse(CbmSectorLocation.of(1, 0));
        CbmSectorInterleaves sectorInterleaves = disk.getSectorInterleaves();
        int directoryTrackNr = directoryLocation.getTrackNr();
        int trackNr = location.getTrackNr();
        int sectorNr = location.getSectorNr();
        int count = 0;
        int interleave = subsequent ? sectorInterleaves.getInterleave(trackNr) : 1;

        while (trackNr == directoryTrackNr || disk.getFreeSectorCountOfTrack(trackNr, true) == 0)
        {
            sectorNr = 0;
            interleave = 1;
            ++trackNr;
            ++count;

            if (trackNr > sectorMap.getMaxTrackNr())
            {
                trackNr = 1;

                continue;
            }

            if (count > sectorMap.getMaxTrackNr())
            {
                throw new CbmIOException(Type.DISK_FULL);
            }
        }

        location = findFreeSectorOfTrack(disk, CbmSectorLocation.of(trackNr, sectorNr), interleave)
            .orElseThrow(() -> new CbmIOException(Type.DISK_FULL));

        CbmSector sector = disk.sectorAt(location);

        disk.setSectorUsed(location, true);
        sector.setNextLocation(Optional.empty());

        return sector;
    }

    /**
     * Searches for a free sector in the specified track. Starts at the specified location and uses the specified
     * interleave to advance the sector number.
     *
     * @param location the starting location
     * @param interleave the interleave
     * @return the location, empty if not found
     */
    protected Optional<CbmSectorLocation> findFreeSectorOfTrack(CbmDisk disk, CbmSectorLocation location,
        int interleave)
    {
        if (interleave < 0)
        {
            throw new IllegalArgumentException("Invalid interleave (interleave >= 0): " + interleave);
        }

        if (!disk.isSectorUsed(location))
        {
            return Optional.of(location);
        }

        int trackNr = location.getTrackNr();

        if (disk.getFreeSectorCountOfTrack(trackNr, true) == 0)
        {
            return Optional.empty();
        }

        CbmSectorMap sectorMap = disk.getSectorMap();
        int maxSectorNr = sectorMap.getMaxSectorNrByTrackNr(trackNr);
        int sectorNr = location.getSectorNr();

        sectorNr = computeNextSector(sectorNr, maxSectorNr, interleave);

        location = location.withSectorNr(sectorNr);

        int count = 0;

        //        while (!isSectorAvailable(location) || isSectorUsed(location))
        while (disk.isSectorUsed(location))
        {
            location = location.withSectorNr((location.getSectorNr() + 1) % (maxSectorNr + 1));
            count += 1;

            if (count > maxSectorNr + 1)
            {
                return Optional.empty();
            }
        }

        return Optional.of(location);
    }

    protected int computeNextSector(int sectorNr, int maxSectorNr, int interleave)
    {
        sectorNr += interleave;

        while (sectorNr > maxSectorNr)
        {
            sectorNr -= maxSectorNr + 1;

            if (sectorNr > 0)
            {
                --sectorNr;
            }
        }

        return sectorNr;
    }
}
