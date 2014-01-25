package org.cbm.ant.util;

import java.awt.image.BufferedImage;

public class CBMBitmapNoneDitherStrategy extends AbstractCBMBitmapDitherStrategy
{

	public CBMBitmapNoneDitherStrategy()
	{
		super();
	}

	@Override
	public void execute(int x, int y, int sourceRGB, int targetRGB, float strength, BufferedImage source)
	{
		// nothing to do
	}

}
