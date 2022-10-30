package org.cbm.ant.cbm.disk;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class CbmFileIterator implements Iterator<CbmFile>
{
    private final Iterator<CbmDirectorySector> directoryIterator;
    private final Predicate<CbmFile> predicate;

    private CbmDirectorySector directorySector;
    private CbmFile file;
    private int index = 0;

    public CbmFileIterator(CbmDirectorySector directorySector, Predicate<CbmFile> predicate)
    {
        this(Arrays.asList(directorySector).iterator(), predicate);
    }

    public CbmFileIterator(Iterator<CbmDirectorySector> directoryIterator, Predicate<CbmFile> predicate)
    {
        super();
        this.directoryIterator = directoryIterator;
        this.predicate = predicate;
    }

    @Override
    public boolean hasNext()
    {
        if (file != null)
        {
            return true;
        }

        while (true)
        {
            if (index >= 8)
            {
                directorySector = null;
            }

            if (directorySector == null)
            {
                if (!directoryIterator.hasNext())
                {
                    return false;
                }

                directorySector = directoryIterator.next();
                index = 0;
            }

            file = directorySector.getFile(index++);

            if (predicate.test(file))
            {
                return true;
            }

            file = null;
        }
    }

    @Override
    public CbmFile next()
    {
        if (!hasNext())
        {
            throw new NoSuchElementException();
        }

        try
        {
            return file;
        }
        finally
        {
            file = null;
        }
    }
}
