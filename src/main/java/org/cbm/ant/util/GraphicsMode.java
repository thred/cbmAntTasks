package org.cbm.ant.util;

public enum GraphicsMode
{

	HIRES(1, 8, 8, 24, 21, 2),

	LORES(2, 4, 8, 12, 21, 4);

	private final int divider;
	private final int widthPerChar;
	private final int heightPerChar;
	private final int widthPerSprite;
	private final int heightPerSprite;
	private final int numberOfColors;

	private GraphicsMode(int divider, int widthPerChar, int heightPerChar, int widthPerSprite, int heightPerSprite,
			int numberOfColors)
	{
		this.divider = divider;
		this.widthPerChar = widthPerChar;
		this.heightPerChar = heightPerChar;
		this.widthPerSprite = widthPerSprite;
		this.heightPerSprite = heightPerSprite;
		this.numberOfColors = numberOfColors;
	}

	public int getBitPerColor()
	{
		return divider;
	}

	public int getWidthPerChar()
	{
		return widthPerChar;
	}

	public int getHeightPerChar()
	{
		return heightPerChar;
	}

	public int getWidthPerSprite()
	{
		return widthPerSprite;
	}

	public int getHeightPerSprite()
	{
		return heightPerSprite;
	}

	public int getNumberOfColors()
	{
		return numberOfColors;
	}

}
