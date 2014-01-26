package org.cbm.ant.util;

import java.awt.image.BufferedImage;

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
	public void execute(int x, int y, int sourceRGB, int targetRGB, float strength, BufferedImage source)
	{
		int[] error = {
				((sourceRGB >> 16) & 0xff) - ((targetRGB >> 16) & 0xff),
				((sourceRGB >> 8) & 0xff) - ((targetRGB >> 8) & 0xff), (sourceRGB & 0xff) - (targetRGB & 0xff)
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

				if (v == Integer.MAX_VALUE)
				{
					continue;
				}

				int targetX = (x - centerX) + checkX;
				int targetY = (y - centerY) + checkY;

				if ((targetX >= 0) && (targetY >= 0) && (targetX < source.getWidth()) && (targetY < source.getHeight()))
				{
					source.setRGB(targetX, targetY, apply(source.getRGB(targetX, targetY), error, (double) v / divisor));
				}
			}
		}
	}

	private static int apply(int rgb, int[] error, double coefficient)
	{
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;

		r = CBMPalette.range(0, (int) (r + (coefficient * error[0])), 255);
		g = CBMPalette.range(0, (int) (g + (coefficient * error[1])), 255);
		b = CBMPalette.range(0, (int) (b + (coefficient * error[2])), 255);

		return (r << 16) + (g << 8) + b;
	}

}
