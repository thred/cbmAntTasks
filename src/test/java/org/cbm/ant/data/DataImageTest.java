package org.cbm.ant.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.bitmap.CBMBitmap;

public class DataImageTest
{
    public static void main(String[] args) throws BuildException, IOException
    {
        DataImage d = new DataImage();

        save("testImageA.cbm.lores.dither.png",
            new CBMBitmap().image(load("testImageA.png")).targetSize(320, 320).lores());

        /*
        d.setTargetWidth(320);
        d.setTargetHeight(320);
        d.setAntiAlias(true);

        d.setGraphicsMode(GraphicsMode.HIRES);
        d.setDither(false);
        d.setSample(new File("testImageA.cbm.hires.no-dither.png"));
        d.execute(null, null);

        d.setDither(true);
        d.setSample(new File("testImageA.cbm.hires.dither.png"));
        d.execute(null, null);

        d.setGraphicsMode(GraphicsMode.LORES);
        d.setMandatoryPalette(Palette.BLACK);
        d.setDither(false);
        d.setSample(new File("testImageA.cbm.lores.no-dither.png"));
        d.execute(null, null);

        d.setDither(true);
        d.setSample(new File("testImageA.cbm.lores.dither.png"));
        d.execute(null, null);

        d.setDither(true);
        d.setGraphicsMode(GraphicsMode.HIRES);
        d.setPalette(Palette.WHITE, Palette.BLACK);
        d.setSample(new File("testImageA.cbm.hires.bw.png"));
        d.execute(null, null);

        d.setDither(true);
        d.setGraphicsMode(GraphicsMode.LORES);
        d.setPalette(Palette.WHITE, Palette.LIGHT_GRAY, Palette.GRAY, Palette.DARK_GRAY, Palette.BLACK);
        d.setSample(new File("testImageA.cbm.hires.gray.png"));
        d.execute(null, null);

        d = new DataImage();

        d.setImage(new File(DataImageTest.class.getResource("testImageB.jpg").getPath()));
        d.setTargetWidth(200);
        d.setTargetHeight(320);
        d.setAntiAlias(true);

        d.setGraphicsMode(GraphicsMode.HIRES);
        d.setDither(false);
        d.setSample(new File("testImageB.cbm.hires.no-dither.png"));
        d.execute(null, null);

        d.setDither(true);
        d.setSample(new File("testImageB.cbm.hires.dither.png"));
        d.execute(null, null);

        d.setGraphicsMode(GraphicsMode.LORES);
        d.setMandatoryPalette(Palette.BLACK);
        d.setDither(false);
        d.setSample(new File("testImageB.cbm.lores.no-dither.png"));
        d.execute(null, null);

        d.setDither(true);
        d.setSample(new File("testImageB.cbm.lores.dither.png"));
        d.execute(null, null);

        d = new DataImage();

        d.setImage(new File(DataImageTest.class.getResource("testImageC.png").getPath()));
        d.setTargetWidth(320);
        d.setTargetHeight(200);
        d.setAntiAlias(true);

        d.setGraphicsMode(GraphicsMode.HIRES);
        d.setDither(false);
        d.setSample(new File("testImageC.cbm.hires.no-dither.png"));
        d.execute(null, null);

        d.setDither(true);
        d.setSample(new File("testImageC.cbm.hires.dither.png"));
        d.execute(null, null);

        d.setGraphicsMode(GraphicsMode.LORES);
        d.setMandatoryPalette(Palette.BLACK);
        d.setDither(false);
        d.setSample(new File("testImageC.cbm.lores.no-dither.png"));
        d.execute(null, null);

        d.setDither(true);
        d.setSample(new File("testImageC.cbm.lores.dither.png"));
        d.execute(null, null);
        */
    }

    public static BufferedImage load(String name) throws IOException
    {
        return ImageIO.read(new File(DataImageTest.class.getResource("testImageA.png").getPath()));
    }

    public static void save(String name, CBMBitmap bitmap) throws IOException
    {
        ImageIO.write(bitmap.getSampleImage(), "PNG", new File(name));
    }
}
