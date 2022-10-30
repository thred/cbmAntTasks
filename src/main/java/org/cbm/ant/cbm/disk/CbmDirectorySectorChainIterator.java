package org.cbm.ant.cbm.disk;

import java.util.Iterator;

public class CbmDirectorySectorChainIterator implements Iterator<CbmDirectorySector>
{
    private final CbmDisk disk;
    private final Iterator<CbmSector> iterator;

    public CbmDirectorySectorChainIterator(CbmDisk disk, Iterator<CbmSector> iterator)
    {
        super();

        this.disk = disk;
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext()
    {
        return iterator.hasNext();
    }

    @Override
    public CbmDirectorySector next()
    {
        return new CbmDirectorySector(disk, iterator.next());
    }
}
