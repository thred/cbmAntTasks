package org.cbm.ant.cbm.bitmap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * The VIC II Color Palette<br/>
 * <br/>
 * The true colors are based on the analysis of Philip "Pepto" Timmermann<br/>
 * <a href="http://unusedino.de/ec64/technical/misc/vic656x/colors/index.html"
 * >http://unusedino.de/ec64/technical/misc/vic656x/colors/index.html</a>
 *
 * @author Manfred HANTSCHEL
 */
public class CBMPalette
{

    public static final CBMPalette DEFAULT;

    static
    {
        DEFAULT = new CBMPalette();

        //		DEFAULT.setFromYUV(CBMColor.BLACK, 0.0f, 0.0f, 0.0f);
        //		DEFAULT.setFromYUV(CBMColor.WHITE, 255.0f, 0.0f, 0.0f);
        //		DEFAULT.setFromYUV(CBMColor.RED, 79.6875f, -13.01434923670814368f, +31.41941843272073796f);
        //		DEFAULT.setFromYUV(CBMColor.CYAN, 159.375f, +13.01434923670814368f, -31.41941843272073796f);
        //		DEFAULT.setFromYUV(CBMColor.PURPLE, 95.625f, +24.04738177749708281f, +24.04738177749708281f);
        //		DEFAULT.setFromYUV(CBMColor.GREEN, 127.5f, -24.04738177749708281f, -24.04738177749708281f);
        //		DEFAULT.setFromYUV(CBMColor.BLUE, 63.75f, +34.081334493f, 0f);
        //		DEFAULT.setFromYUV(CBMColor.YELLOW, 191.25f, -34.0081334493f, 0f);
        //
        //		DEFAULT.setFromYUV(CBMColor.ORANGE, 95.625f, -24.04738177749708281f, +24.04738177749708281f);
        //		DEFAULT.setFromYUV(CBMColor.BROWN, 63.75f, -31.41941843272073796f, +13.01434923670814368f);
        //		DEFAULT.setFromYUV(CBMColor.LIGHT_RED, 127.5f, -13.01434923670814368f, +31.41941843272073796f);
        //		DEFAULT.setFromYUV(CBMColor.DARK_GRAY, 79.6875f, 0f, 0f);
        //		DEFAULT.setFromYUV(CBMColor.GRAY, 119.53125f, 0f, 0f);
        //		DEFAULT.setFromYUV(CBMColor.LIGHT_GREEN, 191.25f, -24.04738177749708281f, -24.04738177749708281f);
        //		DEFAULT.setFromYUV(CBMColor.LIGHT_BLUE, 119.53125f, +34.0081334493f, 0f);
        //		DEFAULT.setFromYUV(CBMColor.LIGHT_GRAY, 159.375f, 0f, 0f);

        DEFAULT.setFromRGB(CBMColor.BLACK, 0x000000);
        DEFAULT.setFromRGB(CBMColor.WHITE, 0xffffff);
        DEFAULT.setFromRGB(CBMColor.RED, 0x68372b);
        DEFAULT.setFromRGB(CBMColor.CYAN, 0x70a4b2);
        DEFAULT.setFromRGB(CBMColor.PURPLE, 0x6f3d86);
        DEFAULT.setFromRGB(CBMColor.GREEN, 0x588d43);
        DEFAULT.setFromRGB(CBMColor.BLUE, 0x352879);
        DEFAULT.setFromRGB(CBMColor.YELLOW, 0xb8c76f);

        DEFAULT.setFromRGB(CBMColor.ORANGE, 0x6f4f25);
        DEFAULT.setFromRGB(CBMColor.BROWN, 0x433900);
        DEFAULT.setFromRGB(CBMColor.LIGHT_RED, 0x9a6759);
        DEFAULT.setFromRGB(CBMColor.DARK_GRAY, 0x444444);
        DEFAULT.setFromRGB(CBMColor.GRAY, 0x6c6c6c);
        DEFAULT.setFromRGB(CBMColor.LIGHT_GREEN, 0x9ad284);
        DEFAULT.setFromRGB(CBMColor.LIGHT_BLUE, 0x6c5eb5);
        DEFAULT.setFromRGB(CBMColor.LIGHT_GRAY, 0x959595);
    }

    private final int[] rgbs;
    private final float[][][] values;
    private final float[][] thresholds;
    private final Color[] colors;

    private boolean updateThresholds = true;

    private CBMPalette()
    {
        this(new int[CBMColor.LENGTH], new float[CBMColorSpace.values().length][CBMColor.LENGTH][3],
            new float[CBMColorSpace.values().length][3], new Color[CBMColor.LENGTH]);
    }

    private CBMPalette(int[] rgbs, float[][][] values, float[][] thresholds, Color[] colors)
    {
        super();

        this.rgbs = rgbs;
        this.values = values;
        this.thresholds = thresholds;
        this.colors = colors;
    }

    public CBMPalette copy()
    {
        int[] rgbs = Arrays.copyOf(this.rgbs, CBMColor.LENGTH);
        float[][][] values = new float[CBMColorSpace.values().length][CBMColor.LENGTH][3];
        float[][] thresholds = new float[CBMColorSpace.values().length][3];

        for (CBMColorSpace colorSpace : CBMColorSpace.values())
        {
            for (CBMColor color : CBMColor.values())
            {
                values[colorSpace.ordinal()][color.index()] =
                    Arrays.copyOf(this.values[colorSpace.ordinal()][color.index()], 3);
            }

            thresholds[colorSpace.ordinal()] = Arrays.copyOf(this.thresholds[colorSpace.ordinal()], 3);
        }

        Color[] colors = Arrays.copyOf(this.colors, CBMColor.LENGTH);

        return new CBMPalette(rgbs, values, thresholds, colors);
    }

    public int getRGB(CBMColor cbmColor)
    {
        return rgbs[cbmColor.index()];
    }

    public float[] get(CBMColor cbmColor, CBMColorSpace colorSpace)
    {
        return values[colorSpace.ordinal()][cbmColor.index()];
    }

    public void put(CBMColor cbmColor, CBMColorSpace colorSpace, float[] to)
    {
        float[] value = values[colorSpace.ordinal()][cbmColor.index()];

        to[0] = value[0];
        to[1] = value[1];
        to[2] = value[2];
    }

    public float[] getThresholds(CBMColorSpace colorSpace)
    {
        if (updateThresholds)
        {
            for (CBMColorSpace currentColorSpace : CBMColorSpace.values())
            {
                thresholds[currentColorSpace.ordinal()] = computeThresholds(values[currentColorSpace.ordinal()]);
            }

            updateThresholds = false;
        }

        return thresholds[colorSpace.ordinal()];
    }

    public Color color(CBMColor cbmColor)
    {
        return colors[cbmColor.index()];
    }

    public void setFromRGB(CBMColor cbmColor, int rgb)
    {
        int index = cbmColor.index();

        rgbs[index] = rgb;

        for (CBMColorSpace colorSpace : CBMColorSpace.values())
        {
            float[] value = colorSpace.convertTo(rgb, null);

            values[colorSpace.ordinal()][index] = value;
        }

        colors[index] = new Color(rgb, false);

        updateThresholds = true;
    }

    public void setFromYUV(CBMColor cbmColor, float l, float u, float v)
    {
        setFromRGB(cbmColor, yuv2rgb(l, u, v));
    }

    public void setFromColor(CBMColor cbmColor, Color color)
    {
        setFromRGB(cbmColor, color.getRGB());
    }

    protected float[] computeThresholds(float[][] values)
    {
        float[] result = new float[3];
        float[] v = new float[values.length];

        for (int i = 0; i < values.length; i += 1)
        {
            v[i] = values[i][0];
        }

        result[0] = computeThreshold(v);

        for (int i = 0; i < values.length; i += 1)
        {
            v[i] = values[i][1];
        }

        result[1] = computeThreshold(v);

        for (int i = 0; i < values.length; i += 1)
        {
            v[i] = values[i][2];
        }

        result[2] = computeThreshold(v);

        return result;
    }

    protected float computeThreshold(float[] values)
    {
        Arrays.sort(values);
        float threshold = 0;

        for (int i = 1; i < values.length; i += 1)
        {
            float diff = values[i] - values[i - 1];

            if (diff > threshold)
            {
                threshold = diff;
            }
        }

        return threshold;
    }

    public int estimateIndex(CBMColor[] colors, CBMColorSpace colorSpace, float[] value)
    {
        int result = -1;
        double minDelta = Double.MAX_VALUE;

        for (int i = 0; i < colors.length; i++)
        {
            double delta = delta(value, values[colorSpace.ordinal()][colors[i].index()]);

            if (delta < minDelta)
            {
                result = i;
                minDelta = delta;
            }
        }

        return result;
    }

    public CBMColor estimateCBMColor(CBMColor[] allowedColors, CBMColorSpace colorSpace, float[] value)
    {
        CBMColor color = null;
        float minDelta = Float.NaN;

        for (CBMColor allowedColor : allowedColors)
        {
            float delta = delta(value, values[colorSpace.ordinal()][allowedColor.index()]);

            if (Float.isNaN(minDelta) || delta < minDelta)
            {
                color = allowedColor;
                minDelta = delta;
            }
        }

        return color;
    }

    public static int rgb2yuv(int rgb)
    {
        int r = rgb >> 16 & 0xff;
        int g = rgb >> 8 & 0xff;
        int b = rgb & 0xff;

        int y = (int) (0.257 * r + 0.504 * g + 0.098 * b + 16);
        int u = (int) (-(0.148 * r) - 0.291 * g + 0.439 * b + 128);
        int v = (int) (0.439 * r - 0.368 * g - 0.071 * b + 128);

        return (y << 16) + (u << 8) + v;
    }

    public static int yuv2rgb(int yuv)
    {
        int y = yuv >> 16 & 0xff;
        int u = yuv >> 8 & 0xff;
        int v = yuv & 0xff;

        int r = range(0, (int) (1.164 * (y - 16) + 1.596 * (v - 128)), 255);
        int g = range(0, (int) (1.164 * (y - 16) - 0.813 * (v - 128) - 0.391 * (u - 128)), 255);
        int b = range(0, (int) (1.164 * (y - 16) + 2.018 * (u - 128)), 255);

        return (r << 16) + (g << 8) + b;
    }

    public static int yuv2rgb(float l, float u, float v)
    {
        return (range(0, (int) (l + 1.140f * v), 255) << 16)
            + (range(0, (int) (l - 0.396f * u - 0.581f * v), 255) << 8)
            + range(0, (int) (l + 2.029f * u), 255);
    }

    public static BufferedImage[] rgb2yuv(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage lImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage uImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage vImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y += 1)
        {
            for (int x = 0; x < width; x += 1)
            {
                int rgb = image.getRGB(x, y);
                int yuv = rgb2yuv(rgb);

                int l = yuv >> 16 & 0xff;
                int u = yuv >> 8 & 0xff;
                int v = yuv & 0xff;

                lImage.setRGB(x, y, (l << 16) + (l << 8) + l);
                uImage.setRGB(x, y, (u << 16) + (u << 8) + u);
                vImage.setRGB(x, y, (v << 16) + (v << 8) + v);
            }
        }

        return new BufferedImage[]{lImage, uImage, vImage};
    }

    public static BufferedImage yuv2rgb(BufferedImage[] yuvImages)
    {
        int width = yuvImages[0].getWidth();
        int height = yuvImages[0].getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y += 1)
        {
            for (int x = 0; x < width; x += 1)
            {
                int yuv = ((yuvImages[0].getRGB(x, y) & 0xff) << 16)
                    + ((yuvImages[1].getRGB(x, y) & 0xff) << 8)
                    + (yuvImages[2].getRGB(x, y) & 0xff);
                int rgb = yuv2rgb(yuv);

                image.setRGB(x, y, rgb);
            }
        }

        return image;
    }

    public static float delta(float[] left, float[] right)
    {
        float a = right[0] - left[0];
        float b = right[1] - left[1];
        float c = right[2] - left[2];

        return a * a + b * b + c * c;
    }

    public static int range(final int min, final int value, final int max)
    {
        if (value > max)
        {
            return max;
        }

        if (value < min)
        {
            return min;
        }

        return value;
    }

}
