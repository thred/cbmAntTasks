package org.cbm.ant.util;

import java.awt.image.BufferedImage;

public interface CBMBitmapDitherStrategy
{

	CBMColor execute(BufferedImage image, int x, int y, CBMPalette palette, ColorSpace colorSpace, CBMColor[] allowedColors, float strength);
	
}
