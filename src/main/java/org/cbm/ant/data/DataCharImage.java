package org.cbm.ant.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.bitmap.CBMBitmap;
import org.cbm.ant.cbm.bitmap.CBMBitmapDither;
import org.cbm.ant.cbm.bitmap.GraphicsMode;

public class DataCharImage extends AbstractDataCommand
{
    private final CBMBitmap bitmap;

    private File image;

    private int x = 0;
    private int y = 0;
    private int width = 1;
    private int height = 1;

    public DataCharImage()
    {
        super();

        bitmap = new CBMBitmap().blockSize(8, 8);

        bitmap.setWidth(8);
        bitmap.setHeight(8);
    }

    public File getImage()
    {
        return image;
    }

    public void setImage(File image)
    {
        this.image = image;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setMode(GraphicsMode mode)
    {
        bitmap.setMode(mode);
    }

    public void setDither(CBMBitmapDither dither)
    {
        bitmap.setDither(dither);
    }

    public void setOverscan(int overscan)
    {
        bitmap.setOverscan(overscan);
    }

    public void setPalette(String palette)
    {
        bitmap.setAllowedColors(palette);
    }

    public void setMandatoryPalette(String mandatoryPalette)
    {
        bitmap.setMandatoryColors(mandatoryPalette);
    }

    public void setPreferredColorIndices(String preferredColorIndices)
    {
        bitmap.setPreferredColorIndices(preferredColorIndices);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.data.AbstractDataCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        File file = getImage();

        if (exists && file.exists())
        {
            return file.lastModified() > lastModified;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.data.AbstractDataCommand#execute(org.cbm.ant.data.Data, DataWriter)
     */
    @Override
    public void execute(Data task, DataWriter writer) throws BuildException, IOException
    {
        bitmap.image(ImageIO.read(getImage()));

        for (int dy = 0; dy < height; ++dy)
        {
            for (int dx = 0; dx < width; ++dx)
            {
                bitmap.area((x + dx) * 8, (y + dy) * 8, 8, 8);

                try (OutputStream stream = writer.createByteStream())
                {
                    bitmap.writeBitmapData(stream);
                    bitmap.writeCharacterData(stream);
                    bitmap.writeColorData(stream);
                }
            }
        }
    }
}
