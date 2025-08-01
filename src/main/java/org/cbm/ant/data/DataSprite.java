package org.cbm.ant.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.bitmap.CBMBitmap;
import org.cbm.ant.cbm.bitmap.CBMBitmapDither;
import org.cbm.ant.cbm.bitmap.GraphicsMode;

public class DataSprite implements DataCommand
{
    private final CBMBitmap bitmap;

    private File image;
    private File sample;

    public DataSprite()
    {
        super();

        bitmap = new CBMBitmap().area(0, 0, 24, 21).blockSize(24, 21).hires();
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
        bitmap.setX(x);
    }

    public void setY(int y)
    {
        bitmap.setY(y);
    }

    public void setWidth(int width)
    {
        bitmap.setWidth(width);
    }

    public void setHeight(int height)
    {
        bitmap.setHeight(height);
    }

    public void setTargetWidth(int targetWidth)
    {
        bitmap.setTargetWidth(targetWidth);
    }

    public void setTargetHeight(int targetHeight)
    {
        bitmap.setTargetHeight(targetHeight);
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

    public File getSample()
    {
        return sample;
    }

    public void setSample(File sample)
    {
        this.sample = sample;
    }

    public void setPalette(String palette)
    {
        bitmap.setAllowedColors(palette);
    }

    public void setMandatoryPalette(String mandatoryPalette)
    {
        bitmap.setMandatoryColors(mandatoryPalette);
    }

    /**
     * @see org.cbm.ant.data.DataCommand#isExecutionNecessary(long, boolean)
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
     * @see org.cbm.ant.data.DataCommand#execute(DataWriter)
     */
    @Override
    public void execute(DataWriter writer) throws BuildException, IOException
    {
        bitmap.image(ImageIO.read(getImage()));

        if (sample != null)
        {
            ImageIO.write(bitmap.getSampleImage(), "PNG", sample);
        }

        try (OutputStream stream = writer.createByteStream())
        {
            bitmap.writeSpriteData(stream);
            
            stream.write(0);
        }
    }
}
