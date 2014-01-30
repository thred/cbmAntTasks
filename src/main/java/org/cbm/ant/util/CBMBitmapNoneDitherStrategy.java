package org.cbm.ant.util;

import java.awt.image.BufferedImage;

public class CBMBitmapNoneDitherStrategy extends AbstractCBMBitmapDitherStrategy
{

	public CBMBitmapNoneDitherStrategy()
	{
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.cbm.ant.util.CBMBitmapDitherStrategy#execute(java.awt.image.BufferedImage, int, int, org.cbm.ant.util.CBMPalette,
	 *      org.cbm.ant.util.ColorSpace, org.cbm.ant.util.CBMColor[], float)
	 */
	@Override
	public CBMColor execute(BufferedImage image, int x, int y, CBMPalette palette, ColorSpace colorSpace,
			CBMColor[] allowedColors, float strength)
	{
		int sourceValue = image.getRGB(x, y);
		CBMColor targetColor = palette.estimateCBMColor(allowedColors, colorSpace, sourceValue);
		int targetValue = palette.get(targetColor, colorSpace);

		image.setRGB(x, y, targetValue);
		
		return targetColor;
	}

}
