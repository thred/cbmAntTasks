package org.cbm.ant.util;

import java.awt.image.BufferedImage;

/**
 * Thanks to http://bisqwit.iki.fi/story/howto/dither/jy/
 * 
 * @author thred
 */
public class CBMBitmapOrderedDitherStrategy extends AbstractCBMBitmapDitherStrategy
{

	private final float[] matrix;
	private final int size;

	public CBMBitmapOrderedDitherStrategy(int[] matrix, int divisor)
	{
		super();

		this.matrix = new float[matrix.length];

		for (int i = 0; i < matrix.length; i += 1)
		{
			this.matrix[i] = (float) matrix[i] / divisor;
		}

		size = (int) Math.sqrt(matrix.length);
	}

	@Override
	public CBMColor execute(BufferedImage image, int x, int y, CBMPalette palette, ColorSpace colorSpace,
			CBMColor[] allowedColors, float strength)
	{
		int sourceValue = image.getRGB(x, y);
		float m = matrix[(x % size) + ((y % size) * size)];
		int[] thresholds = palette.getThresholds(colorSpace);

		int a = CBMPalette.range(0, (int) (((sourceValue >> 16) & 0xff) + (m * thresholds[0])) - (thresholds[0] / 2),
				255);
		int b = CBMPalette.range(0, (int) (((sourceValue >> 8) & 0xff) + (m * thresholds[1])) - (thresholds[1] / 2),
				255);
		int c = CBMPalette.range(0, (int) ((sourceValue & 0xff) + (m * thresholds[2])) - (thresholds[2] / 2), 255);
		int abc = (a << 16) | (b << 8) | c;

		CBMColor targetColor = palette.estimateCBMColor(allowedColors, colorSpace, abc);
		int targetValue = palette.get(targetColor, colorSpace);

		image.setRGB(x, y, targetValue);

		return targetColor;
	}

}
