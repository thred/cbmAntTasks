package org.cbm.ant.cbm.disk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CBMDiskTrack
{

    private final List<CBMDiskSector> sectors = new ArrayList<>();

    private final int trackNr;

    public CBMDiskTrack(int trackNr)
    {
        super();

        this.trackNr = trackNr;
    }

    public void init(int numberOfSectors)
    {
        sectors.clear();

        for (int sectorNr = 0; sectorNr < numberOfSectors; sectorNr += 1)
        {
            CBMDiskSector sector = new CBMDiskSector(trackNr, sectorNr);

            sectors.add(sector);
        }
    }

    public void clearMarks()
    {
        for (CBMDiskSector sector : sectors)
        {
            sector.setMark(-1);
        }
    }

    public void read(InputStream in, int numberOfSectors) throws IOException
    {
        sectors.clear();

        for (int sectorNr = 0; sectorNr < numberOfSectors; sectorNr += 1)
        {
            CBMDiskSector sector = new CBMDiskSector(trackNr, sectorNr);

            sector.read(in);

            sectors.add(sector);
        }
    }

    public void write(OutputStream out) throws IOException
    {
        for (CBMDiskSector sector : sectors)
        {
            sector.write(out);
        }
    }

    public CBMDiskSector getSector(int sector)
    {
        try
        {
            return sectors.get(sector);
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException(
                String.format("Invalid sector number %d (track %d has %d sectors)", sector, trackNr, sectors.size()));
        }
    }

    public int getNumberOfSectors()
    {
        return sectors.size();
    }

}
