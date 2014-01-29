package org.cbm.ant.util;

import java.awt.image.BufferedImage;

public enum ColorSpace
{
	RGB("RGB", ColorConversion.RGB2RGB),

	YCbCr("YCbCr", ColorConversion.RGB2YCbCr),

	YUV("YUV", ColorConversion.RGB2YUV),

	HSL("HSL", ColorConversion.RGB2HSL),

	HSV("HSV", ColorConversion.RGB2HSV),

	xyY("xyY", ColorConversion.RGB2xyY),

	XYZ("XYZ", ColorConversion.RGB2XYZ),

	LAB("LAB", ColorConversion.RGB2LAB),

	LUV("LUV", ColorConversion.RGB2LUV);

	private final String name;
	private final ColorConversion conversion;

	private ColorSpace(String name, ColorConversion conversion)
	{
		this.name = name;
		this.conversion = conversion;
	}

	public String getName()
	{
		return name;
	}

	public ColorConversion getConversion()
	{
		return conversion;
	}

	public int convertTo(int rgb)
	{
		return conversion.convert(rgb);
	}

	public BufferedImage convertTo(BufferedImage rgbImage)
	{
		return conversion.convert(rgbImage);
	}
	
	public String toString() {
		return name;
	}

}
