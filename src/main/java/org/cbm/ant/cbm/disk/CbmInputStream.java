package org.cbm.ant.cbm.disk;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.cbm.ant.cbm.disk.CbmIOException.Type;

public class CbmInputStream extends InputStream
{
    private final Set<CbmSectorLocation> visitedLocations = new HashSet<>();

    private final CbmDisk disk;

    private CbmSector sector;
    private int position;
    private int end;

    public CbmInputStream(CbmDisk disk, Optional<CbmSectorLocation> location) throws CbmIOException
    {
        super();

        this.disk = disk;

        initSector(location.orElse(null));
    }

    private boolean initSector(CbmSectorLocation location) throws CbmIOException
    {
        if (location == null)
        {
            sector = null;
            position = 2;
            end = 0;

            return false;
        }

        if (!visitedLocations.add(location))
        {
            throw new CbmIOException(Type.LOOP_DETECTED, location);
        }

        sector = disk.sectorAt(location);
        position = 2;

        if (sector.getNextTrackNr() == 0)
        {
            end = sector.getNextSectorNr();
        }
        else
        {
            end = 255;
        }

        return true;
    }

    private boolean nextSector() throws CbmIOException
    {
        return initSector(sector.getNextLocation().orElse(null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws CbmIOException
    {
        if (sector == null)
        {
            return -1;
        }

        if (position > end && !nextSector())
        {
            return -1;
        }

        return sector.getByte(position++);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int offset, int length) throws CbmIOException
    {
        if (sector == null)
        {
            return -1;
        }

        if (position > end && !nextSector())
        {
            return -1;
        }

        int available = Math.min(end + 1 - position, length);

        System.arraycopy(sector.getBytes(position, available), 0, b, offset, available);

        position += available;

        return available;
    }
}
