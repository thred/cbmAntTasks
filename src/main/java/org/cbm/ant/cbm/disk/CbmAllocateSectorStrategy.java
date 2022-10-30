package org.cbm.ant.cbm.disk;

import java.util.Optional;

public interface CbmAllocateSectorStrategy
{
    CbmSector allocateDirectorySector(CbmDisk disk) throws CbmIOException;

    CbmSector allocateFileSector(CbmDisk disk, Optional<CbmSectorLocation> optionalLocation, boolean subsequent)
        throws CbmIOException;
}