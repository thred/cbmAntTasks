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
     * @see org.cbm.ant.data.DataCommand#execute(org.cbm.ant.data.Data, java.io.OutputStream)
     */
    @Override
    public void execute(Data task, OutputStream out) throws BuildException, IOException
    {
        task.log("Extracting sprite data from: " + image);

        bitmap.image(ImageIO.read(getImage()));

        if (sample != null)
        {
            ImageIO.write(bitmap.getSampleImage(), "PNG", sample);
        }

        bitmap.writeSpriteData(out);
        //bitmap.writeCharacterData(out);
        //bitmap.writeColorData(out);
        out.write(0);
    }

    //	/**
    //	 * @see org.cbm.ant.data.DataCommand#execute(Data, java.io.OutputStream)
    //	 */
    //	@Override
    //	public void execute(Data task, OutputStream out) throws BuildException, IOException {
    //		task.log("Extracting sprite data from: " + image);
    //
    //		BufferedImage image;
    //
    //		try {
    //			image = ImageIO.read(imageFile);
    //		} catch (IOException e) {
    //			throw new BuildException("Failed to read image: " + imageFile, e);
    //		}
    //
    //		for (int y = 0; y < 21; y += 1) {
    //			for (int x = 0; x < 24; x += 8) {
    //				int value = (getBit(image.getRGB(this.x + x, this.y + y)) << 7) + (getBit(image
    //						.getRGB(this.x + x + 1, this.y + y)) << 6) + (getBit(image
    //						.getRGB(this.x + x + 2, this.y + y)) << 5) + (getBit(image
    //						.getRGB(this.x + x + 3, this.y + y)) << 4) + (getBit(image
    //						.getRGB(this.x + x + 4, this.y + y)) << 3) + (getBit(image
    //						.getRGB(this.x + x + 5, this.y + y)) << 2) + (getBit(image
    //						.getRGB(this.x + x + 6, this.y + y)) << 1) + getBit(image
    //						.getRGB(this.x + x + 7, this.y + y));
    //				out.write(value);
    //			}
    //		}
    //		out.write(0);
    //	}
    //
    //	private int getBit(int color) {
    //		int[] colorRGB = toRGB(color);
    //		int backgroundDelta = getDelta(colorRGB, background);
    //		int foregroundDelta = getDelta(colorRGB, foreground);
    //
    //
    //		if (backgroundDelta < foregroundDelta) {
    //			return 0;
    //		}
    //
    //		return 1;
    //	}
    //
    //	private static int getDelta(int[] color, int[] otherColor) {
    //		return (Math.abs(color[0] - otherColor[0])
    //				+ Math.abs(color[1] - otherColor[1]) + Math.abs(color[2]
    //				- otherColor[2]));
    //	}
    //
    //	private static int parseColor(String color) {
    //		if (color.startsWith("#")) {
    //			color = color.substring(1);
    //		} else if (color.startsWith("0x")) {
    //			color = color.substring(2);
    //		}
    //
    //		return Integer.parseInt(color, 16);
    //	}
    //
    //	private static int[] toRGB(int color) {
    //		color &= 0x00ffffff;
    //
    //		return new int[] { (color >> 16) % 256, (color >> 8) % 256,
    //				color % 256 };
    //	}
}
