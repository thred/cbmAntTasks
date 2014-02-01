package org.cbm.ant.util;


public interface CBMBitmapDitherStrategy
{

	CBMColor execute(CBMImage image, CBMPalette palette, CBMColor[] allowedColors, int x, int y, float strength);
	
}
