package org.cbm.ant.util;

/**
 * Thanks to: http://www.tannerhelland.com/4660/dithering-eleven-algorithms-source-code/
 * 
 * @author thred
 */
public enum CBMBitmapDither
{

	NONE("none", new CBMBitmapNoneDitherStrategy()),

	FLOYD_STEINBERG("Floyd-Steinberg", new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][] {
			{
					null, Integer.MAX_VALUE, 7
			}, {
					3, 5, 1
			}
	})),

	FALSE_FLOYD_STEINBERG("False Floyd-Steinberg", new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][] {
			{
					Integer.MAX_VALUE, 3
			}, {
					3, 2
			}
	})),

	JARVIS_JUDICE_NINKE("Jarvis-Judice-Ninke", new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][] {
			{
					null, null, Integer.MAX_VALUE, 7, 5
			}, {
					3, 5, 7, 5, 3
			}, {
					1, 3, 5, 3, 1
			}
	})),

	STUCKI("Stucki", new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][] {
			{
					null, null, Integer.MAX_VALUE, 8, 4
			}, {
					2, 4, 8, 4, 2
			}, {
					1, 2, 4, 2, 1
			}
	})),

	ATKINSON("Atkinson", new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][] {
			{
					null, Integer.MAX_VALUE, 1, 1
			}, {
					1, 1, 1, null
			}, {
					null, 1, null, null
			}
	})),

	BURKES("Burkes", new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][] {
			{
					null, null, Integer.MAX_VALUE, 8, 4
			}, {
					2, 4, 8, 4, 2
			}
	})),

	SIERRA("Sierra", new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][] {
			{
					null, null, Integer.MAX_VALUE, 5, 3
			}, {
					2, 4, 5, 4, 2
			}, {
					null, 2, 3, 2, null
			}
	})),

	TWO_ROW_SIERRA("Two-Row Sierra", new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][] {
			{
					null, null, Integer.MAX_VALUE, 4, 3
			}, {
					1, 2, 3, 2, 1
			}
	})),

	SIERRA_LIGHT("Sierra Light", new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][] {
			{
					null, Integer.MAX_VALUE, 2
			}, {
					1, 1, null
			}
	}));

	private final String name;
	private final CBMBitmapDitherStrategy strategy;

	private CBMBitmapDither(String name, CBMBitmapDitherStrategy strategy)
	{
		this.name = name;
		this.strategy = strategy;
	}

	public String getName()
	{
		return name;
	}

	public CBMBitmapDitherStrategy getStrategy()
	{
		return strategy;
	}

	@Override
	public String toString()
	{
		return getName();
	}

}
