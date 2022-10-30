package org.cbm.ant.cbm.disk;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class CbmSectorChainIterator implements Iterator<CbmSector>
{
    private final LinkedList<CbmSectorLocation> visitedLocations = new LinkedList<>();

    private final CbmImage disk;

    private CbmSectorLocation lastLocation;
    private CbmSectorLocation nextLocation;

    public CbmSectorChainIterator(CbmImage disk, CbmSectorLocation location)
    {
        super();

        this.disk = disk;

        nextLocation = location;
    }

    @Override
    public boolean hasNext()
    {
        return nextLocation != null;
    }

    @Override
    public CbmSector next()
    {
        if (!hasNext())
        {
            throw new NoSuchElementException(
                lastLocation == null ? "Iterator is empty" : "Last sector reached: " + lastLocation);
        }

        CbmSector sector = disk.sectorAt(nextLocation);

        lastLocation = sector.getLocation();

        int visitedIndex = visitedLocations.indexOf(lastLocation);

        if (visitedIndex >= 0)
        {
            throw new IllegalStateException(
                "Loop detected: " + visitedLocations.subList(visitedIndex, visitedLocations.size()));
        }

        visitedLocations.add(lastLocation);
        nextLocation = sector.getNextLocation().orElse(null);

        return sector;
    }
}
