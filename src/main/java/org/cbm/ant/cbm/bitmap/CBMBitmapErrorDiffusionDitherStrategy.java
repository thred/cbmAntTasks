package org.cbm.ant.cbm.bitmap;

public class CBMBitmapErrorDiffusionDitherStrategy extends AbstractCBMBitmapDitherStrategy
{

	private final int centerX;
	private final int centerY;
	private final int width;
	private final int height;
	private final Integer[][] matrix;
	private final int divisor;

	public CBMBitmapErrorDiffusionDitherStrategy(Integer[][] matrix)
	{
		super();

		height = matrix.length;
		width = matrix[0].length;

		this.matrix = matrix;

		int centerX = 0;
		int centerY = 0;
		int divisor = 0;

		for (int y = 0; y < height; y++)
		{
			Integer[] row = matrix[y];

			for (int x = 0; x < width; x++)
			{
				Integer value = row[x];
				if (value != null)
				{
					int v = value.intValue();

					if (v == Integer.MAX_VALUE)
					{
						centerX = x;
						centerY = y;
					}
					else
					{
						divisor += v;
					}
				}
			}
		}

		this.centerX = centerX;
		this.centerY = centerY;
		this.divisor = divisor;
	}

	@Override
	public CBMColor execute(CBMImage image, CBMPalette palette, CBMColor[] allowedColors, int x, int y, float strength)
	{
		float[] sourceValue = image.get(x, y);
		CBMColor targetColor = palette.estimateCBMColor(allowedColors, image.getColorSpace(), sourceValue);
		float[] targetValue = palette.get(targetColor, image.getColorSpace());

		float[] error = {
				sourceValue[0] - targetValue[0], sourceValue[1] - targetValue[1], sourceValue[2] - targetValue[2]
		};

		error[0] *= strength;
		error[1] *= strength;
		error[2] *= strength;

		for (int checkY = 0; checkY < height; checkY += 1)
		{
			for (int checkX = 0; checkX < width; checkX += 1)
			{
				Integer value = matrix[checkY][checkX];

				if (value == null)
				{
					continue;
				}

				int v = value.intValue();
				int targetX = (x - centerX) + checkX;
				int targetY = (y - centerY) + checkY;

				if (v == Integer.MAX_VALUE)
				{
					image.set(targetX, targetY, targetValue);
				
					continue;
				}

				if ((targetX >= 0) && (targetY >= 0) && (targetX < image.getWidth()) && (targetY < image.getHeight()))
				{
					apply(image.get(targetX, targetY), error, (float) v / divisor);
				}
			}
		}

		return targetColor;
	}

	private static void apply(float[] value, float[] error, float coefficient)
	{
		value[0] += coefficient * error[0];
		value[1] += coefficient * error[1];
		value[2] += coefficient * error[2];
	}

};
