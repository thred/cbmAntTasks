package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface CbmImage
{
    default void load(File file) throws FileNotFoundException, IOException
    {
        try (FileInputStream in = new FileInputStream(file))
        {
            load(in);
        }
    }

    default void load(InputStream in) throws IOException
    {
        load(in.readAllBytes());
    }

    void load(byte[] bytes) throws CbmIOException;

    default void save(File file) throws IOException
    {
        try (FileOutputStream out = new FileOutputStream(file))
        {
            save(out);
        }
    }

    void save(OutputStream out) throws IOException;

    CbmSectorMap getSectorMap();

    CbmSector sectorAt(CbmSectorLocation location);

    default CbmSectorChainIterator iterateSectorChainAt(CbmSectorLocation location)
    {
        return new CbmSectorChainIterator(this, location);
    }

    default Stream<CbmSector> streamSectorChainAt(CbmSectorLocation location)
    {
        return StreamSupport
            .stream(Spliterators.spliteratorUnknownSize(iterateSectorChainAt(location), Spliterator.ORDERED), false);
    }

    boolean isSectorSupported(CbmSectorLocation location);

    default boolean isSectorEmpty(CbmSectorLocation location)
    {
        byte[] bytes = sectorAt(location).getBytes(0, 256);

        if (bytes[0] != 0x00)
        {
            return false;
        }

        if (bytes[1] != 0x00 && bytes[1] != 0xff)
        {
            return false;
        }

        for (int i = 2; i < bytes.length; ++i)
        {
            if (bytes[i] != 0)
            {
                return false;
            }
        }

        return true;
    }

    boolean isErrorInformationAvailable();

    void setErrorInformationAvailable(boolean errorInformationAvailable);

    int getErrorInformation(CbmSectorLocation location);

    void setErrorInformation(CbmSectorLocation location, int error);

}
