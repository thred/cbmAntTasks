package org.cbm.ant.util;

import java.awt.image.BufferedImage;

public interface CBMBitmapDitherStrategy
{

	void execute(int x, int y, int sourceRGB, int targetRGB, BufferedImage source);
	
}
