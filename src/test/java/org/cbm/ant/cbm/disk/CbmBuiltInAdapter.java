package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.IOException;

public class CbmBuiltInAdapter implements CbmDiskUtilityAdapter
{
    private final File imageFile;
    private final CbmImageType<?> imageType;
    private final CbmAllocateSectorStrategy allocateSectorStrategy;
    private final CbmSectorInterleaves sectorInterleaves;
    private final int fillSector;
    private final boolean eraseOnDelete;

    public CbmBuiltInAdapter(File imageFile, CbmImageType<?> imageType)
    {
        this(imageFile, imageType, null, null, CbmOutputStream.FILL_SECTOR_WITH_GARBADGE, false);
    }

    private CbmBuiltInAdapter(File imageFile, CbmImageType<?> imageType,
        CbmAllocateSectorStrategy allocateSectorStrategy, CbmSectorInterleaves sectorInterleaves, int fillSector,
        boolean eraseOnDelete)
    {
        super();
        this.imageFile = imageFile;
        this.imageType = imageType;
        this.allocateSectorStrategy = allocateSectorStrategy;
        this.sectorInterleaves = sectorInterleaves;
        this.fillSector = fillSector;
        this.eraseOnDelete = eraseOnDelete;
    }

    public CbmBuiltInAdapter withAllocateSectorStrategy(CbmAllocateSectorStrategy allocateSectorStrategy)
    {
        return new CbmBuiltInAdapter(imageFile, imageType, allocateSectorStrategy, sectorInterleaves, fillSector,
            eraseOnDelete);
    }

    public CbmBuiltInAdapter withSectorInterleaves(CbmSectorInterleaves sectorInterleaves)
    {
        return new CbmBuiltInAdapter(imageFile, imageType, allocateSectorStrategy, sectorInterleaves, fillSector,
            eraseOnDelete);
    }

    public CbmBuiltInAdapter fillSectorWithGarbage()
    {
        return new CbmBuiltInAdapter(imageFile, imageType, allocateSectorStrategy, sectorInterleaves,
            CbmOutputStream.FILL_SECTOR_WITH_GARBADGE, eraseOnDelete);
    }

    public CbmBuiltInAdapter doNotFillSector()
    {
        return new CbmBuiltInAdapter(imageFile, imageType, allocateSectorStrategy, sectorInterleaves,
            CbmOutputStream.DO_NOT_FILL_SECTOR, eraseOnDelete);
    }

    public CbmBuiltInAdapter fillSectorWith(int b)
    {
        return new CbmBuiltInAdapter(imageFile, imageType, allocateSectorStrategy, sectorInterleaves, b, eraseOnDelete);
    }

    public CbmBuiltInAdapter withEraseOnDelete(boolean eraseOnDelete)
    {
        return new CbmBuiltInAdapter(imageFile, imageType, allocateSectorStrategy, sectorInterleaves, fillSector,
            eraseOnDelete);
    }

    @Override
    public File getImageFile()
    {
        return imageFile;
    }

    @Override
    public CbmImageType<?> getImageType()
    {
        return imageType;
    }

    protected CbmDisk createDisk()
    {
        CbmDisk disk = imageType.createEmptyDisk();

        if (allocateSectorStrategy != null)
        {
            disk = disk.withAllocateSectorStrategy(allocateSectorStrategy);
        }

        if (sectorInterleaves != null)
        {
            disk = disk.withSectorInterleaves(sectorInterleaves);
        }

        return disk;
    }

    @Override
    public CbmBuiltInAdapter format(String diskName, String diskId) throws IOException
    {
        CbmDisk disk = createDisk();

        disk.format(diskName, diskId);
        disk.save(imageFile);

        return this;
    }

    @Override
    public CbmBuiltInAdapter writeFile(String name, byte[] bytes) throws IOException
    {
        CbmDisk disk = createDisk();

        disk.load(imageFile);

        CbmFile file = disk.allocateFile(name);

        try (CbmFileOutputStream out = file.write())
        {
            out.fillSectorWith(fillSector);

            out.write(bytes);
        }

        disk.save(imageFile);

        return this;
    }

    @Override
    public CbmBuiltInAdapter deleteFile(String name) throws IOException
    {
        CbmDisk disk = createDisk();

        disk.load(imageFile);
        disk.deleteFile(name, eraseOnDelete);
        disk.save(imageFile);

        return this;
    }
}
