package org.cbm.ant.cbm.bitmap;

public enum CBMBitmapEmboss
{

	NONE("None", new float[] {
			0, 0, 0, 0, 0, 0, 0, 0, 0
	}),

	TOP_LEFT("Top Left", new float[] {
			1, 0, 0, 0, 0, 0, 0, 0, -1
	}),

	TOP("Top", new float[] {
			0, 1, 0, 0, 0, 0, 0, -1, 0
	}),

	TOP_RIGHT("Top Right", new float[] {
			0, 0, 1, 0, 0, 0, -1, 0, 0
	}),

	RIGHT("Right", new float[] {
			0, 0, 0, -1, 0, 1, 0, 0, 0
	}),

	BOTTOM_RIGHT("Bottom Right", new float[] {
			-1, 0, 0, 0, 0, 0, 0, 0, 1
	}),

	BOTTOM("Bottom", new float[] {
			0, -1, 0, 0, 0, 0, 0, 1, 0
	}),

	BOTTOM_LEFT("Bottom Left", new float[] {
			0, 0, -1, 0, 0, 0, 1, 0, 0
	}),

	LEFT("Left", new float[] {
			0, 0, 0, 1, 0, -1, 0, 0, 0
	});

	private final String name;
	private final float[] mask;

	private CBMBitmapEmboss(String name, float[] mask)
	{
		this.name = name;
		this.mask = mask;
	}

	public String getName()
	{
		return name;
	}

	public float[] getMask(float factor)
	{
		float[] result = new float[mask.length];

		for (int i = 0; i < result.length; i += 1)
		{
			result[i] = mask[i] * factor;
		}

		result[4] = 1;

		return result;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
