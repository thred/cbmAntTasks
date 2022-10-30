package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.IOException;

import org.cbm.ant.AntTestUtils;
import org.cbm.ant.cc1541.CC1541;
import org.cbm.ant.cc1541.CC1541Delete;
import org.cbm.ant.cc1541.CC1541Format;
import org.cbm.ant.cc1541.CC1541Write;

public class CbmCC1541Adapter implements CbmDiskUtilityAdapter
{
    private final File imageFile;
    private final CbmImageType<?> imageType;

    public CbmCC1541Adapter(File imageFile, CbmImageType<?> imageType)
    {
        super();

        this.imageFile = imageFile;
        this.imageType = imageType;
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

    @Override
    public CbmCC1541Adapter format(String diskName, String diskId) throws IOException
    {
        AntTestUtils
            .prepare(new CC1541())
            .image(imageFile)
            .format(new CC1541Format().diskname(diskName).id(diskId))
            .execute();

        return this;
    }

    @Override
    public CbmCC1541Adapter writeFile(String name, byte[] bytes) throws IOException
    {
        File file = AntTestUtils.randomTmpFile();

        try
        {
            AntTestUtils.write(file, bytes);

            AntTestUtils
                .prepare(new CC1541())
                .image(imageFile)
                .write(new CC1541Write().source(file).destination(name))
                .execute();
        }
        finally
        {
            file.delete();
        }

        return this;
    }

    @Override
    public CbmCC1541Adapter deleteFile(String name)
    {
        AntTestUtils.prepare(new CC1541()).image(imageFile).delete(new CC1541Delete().file(name)).execute();

        return this;
    }
}
