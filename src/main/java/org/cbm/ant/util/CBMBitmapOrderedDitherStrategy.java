package org.cbm.ant.util;

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
	public CBMColor execute(CBMImage image, CBMPalette palette, CBMColor[] allowedColors, int x, int y, float strength)
	{
		float[] value = image.get(x, y);
		float m = matrix[(x % size) + ((y % size) * size)];
		float[] thresholds = palette.getThresholds(image.getColorSpace());

		value[0] += (m * thresholds[0]) - (thresholds[0] * (1-strength));
		value[1] += (m * thresholds[1]) - (thresholds[1] * (1-strength));
		value[2] += (m * thresholds[2]) - (thresholds[2] * (1-strength));

		CBMColor targetColor = palette.estimateCBMColor(allowedColors, image.getColorSpace(), value);

		palette.put(targetColor, image.getColorSpace(), value);

		return targetColor;
	}

}
