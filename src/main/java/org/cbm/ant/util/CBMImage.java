package org.cbm.ant.util;

import java.awt.image.BufferedImage;
import java.util.Set;

public class CBMImage
{

	private final int width;
	private final int height;
	private final CBMColorSpace colorSpace;
	private final float[][] data;

	public CBMImage(BufferedImage image, CBMColorSpace colorSpace)
	{
		this(image.getWidth(), image.getHeight(), colorSpace);

		for (int y = 0; y < image.getHeight(); y += 1)
		{
			for (int x = 0; x < image.getWidth(); x += 1)
			{
				colorSpace.convertTo(image.getRGB(x, y), get(x, y));
			}
		}
	}

	public CBMImage(int width, int height, CBMColorSpace colorSpace)
	{
		super();

		this.width = width;
		this.height = height;
		this.colorSpace = colorSpace;

		data = new float[width * height][3];
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public CBMColorSpace getColorSpace()
	{
		return colorSpace;
	}

	public float[] get(int x, int y)
	{
		return data[x + (y * width)];
	}

	public void set(int x, int y, float[] value)
	{
		float[] d = data[x + (y * width)];
		
		d[0] = value[0];
		d[1] = value[1];
		d[2] = value[2];
	}

	public BufferedImage toImage(CBMPalette palette, Set<CBMColor> usedColors)
	{
		BufferedImage result = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		CBMColor[] allowedColors = CBMColor.values();

		for (int y = 0; y < getHeight(); y += 1)
		{
			for (int x = 0; x < getWidth(); x += 1)
			{
				CBMColor color = palette.estimateCBMColor(allowedColors, colorSpace, get(x, y));

				if (usedColors != null)
				{
					usedColors.add(color);
				}

				result.setRGB(x, y, palette.getRGB(color));
			}
		}

		return result;
	}

}
