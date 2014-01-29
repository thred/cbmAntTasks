package org.cbm.ant.util;

import java.awt.Color;
import java.awt.image.BufferedImage;

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

	private final int[][] values = new int[CBMColor.LENGTH][ColorSpace.values().length];
	private final Color[] colors = new Color[CBMColor.LENGTH];

	public CBMPalette()
	{
		super();
	}

	public int get(CBMColor cbmColor, ColorSpace type)
	{
		return values[cbmColor.index()][type.ordinal()];
	}

	public Color color(CBMColor cbmColor)
	{
		return colors[cbmColor.index()];
	}

	public void setFromRGB(CBMColor cbmColor, int rgb)
	{
		int index = cbmColor.index();

		for (ColorSpace type : ColorSpace.values())
		{
			values[index][type.ordinal()] = type.convertTo(rgb);
		}

		colors[index] = new Color(values[index][ColorSpace.RGB.ordinal()], false);
	}

	public void setFromYUV(CBMColor cbmColor, float l, float u, float v)
	{
		setFromRGB(cbmColor, yuv2rgb(l, u, v));
	}

	public void setFromColor(CBMColor cbmColor, Color color)
	{
		setFromRGB(cbmColor, color.getRGB());
	}

	public int estimateIndex(CBMColor[] colors, ColorSpace colorSpace, int value)
	{
		int result = -1;
		double minDelta = Double.MAX_VALUE;

		for (int i = 0; i < colors.length; i++)
		{
			double delta = delta(value, values[colors[i].index()][colorSpace.ordinal()]);

			if (delta < minDelta)
			{
				result = i;
				minDelta = delta;
			}
		}

		return result;
	}

	public CBMColor estimateCBMColor(CBMColor[] allowedColors, ColorSpace colorSpace, int value)
	{
		CBMColor color = null;
		double minDelta = Double.MAX_VALUE;

		for (CBMColor allowedColor : allowedColors)
		{
			double delta = delta(value, values[allowedColor.index()][colorSpace.ordinal()]);

			if (delta < minDelta)
			{
				color = allowedColor;
				minDelta = delta;
			}
		}

		return color;
	}

	public static int rgb2yuv(int rgb)
	{
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;

		int y = (int) ((0.257 * r) + (0.504 * g) + (0.098 * b) + 16);
		int u = (int) ((-(0.148 * r) - (0.291 * g)) + (0.439 * b) + 128);
		int v = (int) (((0.439 * r) - (0.368 * g) - (0.071 * b)) + 128);

		return (y << 16) + (u << 8) + v;
	}

	public static int yuv2rgb(int yuv)
	{
		int y = (yuv >> 16) & 0xff;
		int u = (yuv >> 8) & 0xff;
		int v = yuv & 0xff;

		int r = range(0, (int) ((1.164 * (y - 16)) + (1.596 * (v - 128))), 255);
		int g = range(0, (int) ((1.164 * (y - 16)) - (0.813 * (v - 128)) - (0.391 * (u - 128))), 255);
		int b = range(0, (int) ((1.164 * (y - 16)) + (2.018 * (u - 128))), 255);

		return (r << 16) + (g << 8) + b;
	}

	public static int yuv2rgb(float l, float u, float v)
	{
		return (range(0, (int) (l + (1.140f * v)), 255) << 16)
				+ (range(0, (int) (l - (0.396f * u) - (0.581f * v)), 255) << 8)
				+ (range(0, (int) (l + (2.029f * u)), 255));
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

				int l = (yuv >> 16) & 0xff;
				int u = (yuv >> 8) & 0xff;
				int v = yuv & 0xff;

				lImage.setRGB(x, y, (l << 16) + (l << 8) + l);
				uImage.setRGB(x, y, (u << 16) + (u << 8) + u);
				vImage.setRGB(x, y, (v << 16) + (v << 8) + v);
			}
		}

		return new BufferedImage[] {
				lImage, uImage, vImage
		};
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
				int yuv = ((yuvImages[0].getRGB(x, y) & 0xff) << 16) + ((yuvImages[1].getRGB(x, y) & 0xff) << 8)
						+ (yuvImages[2].getRGB(x, y) & 0xff);
				int rgb = yuv2rgb(yuv);

				image.setRGB(x, y, rgb);
			}
		}

		return image;
	}

	public static double delta(int leftRGB, int rightRGB)
	{
		int a = ((rightRGB >> 16) & 0xff) - ((leftRGB >> 16) & 0xff);
		int b = ((rightRGB >> 8) & 0xff) - ((leftRGB >> 8) & 0xff);
		int c = (rightRGB & 0xff) - (leftRGB & 0xff);

		return Math.sqrt((a * a) + (b * b) + (c * c));
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
