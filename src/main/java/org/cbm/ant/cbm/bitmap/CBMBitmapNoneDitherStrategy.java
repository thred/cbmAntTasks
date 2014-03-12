package org.cbm.ant.cbm.bitmap;

public class CBMBitmapNoneDitherStrategy extends AbstractCBMBitmapDitherStrategy
{

	public CBMBitmapNoneDitherStrategy()
	{
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.cbm.ant.cbm.bitmap.CBMBitmapDitherStrategy#execute(CBMImage, org.cbm.ant.cbm.bitmap.CBMPalette, org.cbm.ant.cbm.bitmap.CBMColor[], int,
	 *      int, float)
	 */
	@Override
	public CBMColor execute(CBMImage image, CBMPalette palette, CBMColor[] allowedColors, int x, int y,
			float strength)
	{
		float[] sample = image.get(x, y);
		CBMColor color = palette.estimateCBMColor(allowedColors, image.getColorSpace(), sample);
		
		palette.put(color, image.getColorSpace(), sample);
		
		return color;
	}

}
