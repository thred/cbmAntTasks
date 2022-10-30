package org.cbm.ant.cbm.disk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import org.cbm.ant.cbm.disk.CbmIOException.Type;

public class CbmOutputStream extends OutputStream
{
    public static final int FILL_SECTOR_WITH_GARBADGE = -256;
    public static final int DO_NOT_FILL_SECTOR = -257;

    private final CbmDisk disk;
    private final byte[] buffer = new byte[256];

    private int fillSector = FILL_SECTOR_WITH_GARBADGE;

    private Optional<CbmSectorLocation> startLocation = null;
    private CbmSector sector = null;
    private int position = 2;
    private int blockSize = 0;
    private boolean closed = false;

    public CbmOutputStream(CbmDisk disk, Optional<CbmSectorLocation> startLocation)
    {
        super();

        this.disk = disk;
        this.startLocation = startLocation;
    }

    public Optional<CbmSectorLocation> getStartLocation()
    {
        return startLocation;
    }

    public int getBlockSize()
    {
        return blockSize;
    }

    /**
     * The original CBM drive is not capable of writing a portion of a sector and if the data of the last sector is
     * shorter than 254 bytes, it fills it with garbage (usually the last sector it has written). This is the default
     * behavior.
     */
    public void fillSectorWithGarbage()
    {
        fillSector = FILL_SECTOR_WITH_GARBADGE;
    }

    public void doNotFillSector()
    {
        fillSector = DO_NOT_FILL_SECTOR;
    }

    public void fillSectorWith(int b)
    {
        fillSector = b;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException
    {
        if (closed)
        {
            throw new CbmIOException(Type.FILE_CLOSED);
        }

        if (sector == null || position > 255)
        {
            CbmSector previousSector = sector;

            if (previousSector != null)
            {
                sector = disk.allocateFileSector(Optional.of(previousSector.getLocation()), true);

                buffer[0] = (byte) sector.getTrackNr();
                buffer[1] = (byte) sector.getSectorNr();

                previousSector.setBytes(0, buffer);
            }
            else
            {
                sector = disk.allocateFileSector(startLocation, false);

                startLocation = Optional.of(sector.getLocation());
            }

            position = 2;
            ++blockSize;
        }

        buffer[position] = (byte) b;
        ++position;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close()
    {
        if (position > 2)
        {
            buffer[0] = 0;
            buffer[1] = (byte) (position - 1);

            if (fillSector == FILL_SECTOR_WITH_GARBADGE)
            {
                sector.setBytes(0, buffer);
            }
            else
            {
                sector.setBytes(0, buffer, 0, position);

                if (fillSector != DO_NOT_FILL_SECTOR)
                {
                    sector.fill(position, fillSector, 256 - position);
                }
            }
        }

        closed = true;
    }
}
