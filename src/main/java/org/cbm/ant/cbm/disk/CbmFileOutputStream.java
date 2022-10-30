package org.cbm.ant.cbm.disk;

import java.util.Optional;

import org.cbm.ant.cbm.disk.CbmIOException.Type;

public class CbmFileOutputStream extends CbmOutputStream
{
    private final CbmFile file;

    public CbmFileOutputStream(CbmFile file, Optional<CbmSectorLocation> location) throws CbmIOException
    {
        super(file.getImage(), location);

        this.file = file;

        if (!file.isDeleted())
        {
            if (file.getLocation().isPresent())
            {
                throw new CbmIOException(Type.FILE_NOT_EMPTY);
            }

            if (file.getSize() > 0)
            {
                throw new CbmIOException(Type.FILE_NOT_EMPTY);
            }

            if (file.isLocked())
            {
                throw new CbmIOException(Type.FILE_NOT_EMPTY);
            }

            if (!file.isClosed())
            {
                throw new CbmIOException(Type.FILE_ALREADY_OPEN);
            }
        }

        file.setClosed(false);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close()
    {
        file.setLocation(getStartLocation());
        file.setSize(getBlockSize());
        file.setClosed(true);

        super.close();
    }
}
