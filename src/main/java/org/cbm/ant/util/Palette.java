package org.cbm.ant.util;

/**
 * The VIC II Color Palette<br/>
 * <br/>
 * The true colors are based on the analysis of Philip "Pepto" Timmermann<br/>
 * <a href="http://unusedino.de/ec64/technical/misc/vic656x/colors/index.html"
 * >http://unusedino.de/ec64/technical/misc/vic656x/colors/index.html</a>
 * 
 * @author Manfred HANTSCHEL
 */
public enum Palette
{

	BLACK("Black", 0x00, 0.0f, 0.0f, 0.0f),
	WHITE("White", 0x01, 255.0f, 0.0f, 0.0f),
	RED("Red", 0x02, 79.6875f, -13.01434923670814368f, +31.41941843272073796f),
	CYAN("Cyan", 0x03, 159.375f, +13.01434923670814368f, -31.41941843272073796f),
	PURPLE("Purple", 0x04, 95.625f, +24.04738177749708281f, +24.04738177749708281f),
	GREEN("Green", 0x05, 127.5f, -24.04738177749708281f, -24.04738177749708281f),
	BLUE("Blue", 0x06, 63.75f, +34.081334493f, 0f),
	YELLOW("Yellow", 0x07, 191.25f, -34.0081334493f, 0f),

	ORANGE("Orange", 0x08, 95.625f, -24.04738177749708281f, +24.04738177749708281f),
	BROWN("Brown", 0x09, 63.75f, -31.41941843272073796f, +13.01434923670814368f),
	LIGHT_RED("Light Red", 0x0a, 127.5f, -13.01434923670814368f, +31.41941843272073796f),
	DARK_GRAY("Dark Gray", 0x0b, 79.6875f, 0f, 0f),
	GRAY("Gray", 0x0c, 119.53125f, 0f, 0f),
	LIGHT_GREEN("Light Green", 0x0d, 191.25f, -24.04738177749708281f, -24.04738177749708281f),
	LIGHT_BLUE("Light Blue", 0x0e, 119.53125f, +34.0081334493f, 0f),
	LIGHT_GRAY("Light Gray", 0x0f, 159.375f, 0f, 0f);

	private final String name;
	private final int index;
	private final int yuv;
	private final int rgb;

	Palette(final String name, final int index, final float l, final float u, final float v)
	{
		this.name = name;
		this.index = index;

		rgb = yuv2rgb(l, u, v);
		yuv = rgb2yuv(rgb);
	}

	public String getName()
	{
		return name + " (0x0" + Integer.toHexString(index) + ")";
	}

	public int getIndex()
	{
		return index;
	}

	public int yuv()
	{
		return yuv;
	}

	public int rgb()
	{
		return rgb;
	}

	public double delta(int rgb, boolean useYUV, double channelAMult, double channelBMult, double channelCMult)
	{
		if (useYUV)
		{
			return delta(yuv, rgb2yuv(rgb), channelAMult, channelBMult, channelCMult);
		}

		return delta(this.rgb, rgb, channelAMult, channelBMult, channelCMult);
	}

	public static Palette toPalette(int rgb, boolean useYUV, double channelA, double channelB, double channelC)
	{
		return toPalette(Palette.values(), rgb, useYUV, channelA, channelB, channelC);
	}

	public static Palette toPalette(Palette[] palette, int rgb, boolean useYUV, double channelA, double channelB,
			double channelC)
	{
		return palette[indexOf(palette, rgb, useYUV, channelA, channelB, channelC)];
	}

	public static int indexOf(Palette[] palette, int rgb, boolean useYUV, double channelA, double channelB,
			double channelC)
	{
		int index = -1;
		double minDelta = Double.MAX_VALUE;

		for (int i = 0; i < palette.length; i += 1)
		{
			double delta = palette[i].delta(rgb, useYUV, channelA, channelB, channelC);

			if (delta < minDelta)
			{
				index = i;
				minDelta = delta;
			}
		}

		return index;
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

	public static int yuv2rgb(final float l, final float u, final float v)
	{
		return (range(0, (int) (l + (1.140f * v)), 255) << 16)
				+ (range(0, (int) (l - (0.396f * u) - (0.581f * v)), 255) << 8)
				+ (range(0, (int) (l + (2.029f * u)), 255));
	}

	public static double delta(int left, int right, double channelAMult, double channelBMult, double channelCMult)
	{
		int leftChannelA = (left >> 16) & 0xff;
		int leftChannelB = (left >> 8) & 0xff;
		int leftChannelC = left & 0xff;
		int rightChannelA = (right >> 16) & 0xff;
		int rightChannelB = (right >> 8) & 0xff;
		int rightChannelC = right & 0xff;

		return (((double) Math.abs(rightChannelA - leftChannelA) / 256) * channelAMult)
				+ (((double) Math.abs(rightChannelB - leftChannelB) / 256) * channelBMult)
				+ (((double) Math.abs(rightChannelC - leftChannelC) / 256) * channelCMult);
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
