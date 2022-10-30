package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.IOException;

import org.cbm.ant.AntTestUtils;
import org.cbm.ant.viceteam.C1541;
import org.cbm.ant.viceteam.C1541Delete;
import org.cbm.ant.viceteam.C1541Format;
import org.cbm.ant.viceteam.C1541Write;

public class CbmC1541Adapter implements CbmDiskUtilityAdapter
{
    private final File imageFile;
    private final CbmImageType<?> imageType;
    private final String type;

    public CbmC1541Adapter(File imageFile, CbmImageType<?> imageType, String type)
    {
        super();

        this.imageFile = imageFile;
        this.imageType = imageType;
        this.type = type;
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
    public CbmC1541Adapter format(String diskName, String diskId) throws IOException
    {
        AntTestUtils
            .prepare(new C1541())
            .image(imageFile)
            .format(new C1541Format().diskname(diskName).id(diskId).type(type))
            .execute();

        return this;
    }

    @Override
    public CbmC1541Adapter writeFile(String name, byte[] bytes) throws IOException
    {
        File file = AntTestUtils.randomTmpFile();

        try
        {
            AntTestUtils.write(file, bytes);

            AntTestUtils
                .prepare(new C1541())
                .image(imageFile)
                .write(new C1541Write().source(file).destination(name))
                .execute();
        }
        finally
        {
            file.delete();
        }

        return this;
    }

    @Override
    public CbmC1541Adapter deleteFile(String name)
    {
        AntTestUtils.prepare(new C1541()).image(imageFile).delete(new C1541Delete().file(name)).execute();

        return this;
    }
}
