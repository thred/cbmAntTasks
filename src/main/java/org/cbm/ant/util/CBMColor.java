package org.cbm.ant.util;

/**
 * The VIC II Color Palette<br/>
 * <br/>
 * The true colors are based on the analysis of Philip "Pepto" Timmermann<br/>
 * <a href="http://unusedino.de/ec64/technical/misc/vic656x/colors/index.html"
 * >http://unusedino.de/ec64/technical/misc/vic656x/colors/index.html</a>
 * 
 * @author Manfred HANTSCHEL
 */
public enum CBMColor
{

	BLACK("Black", 0x00),
	WHITE("White", 0x01),
	RED("Red", 0x02),
	CYAN("Cyan", 0x03),
	PURPLE("Purple", 0x04),
	GREEN("Green", 0x05),
	BLUE("Blue", 0x06),
	YELLOW("Yellow", 0x07),

	ORANGE("Orange", 0x08),
	BROWN("Brown", 0x09),
	LIGHT_RED("Light Red", 0x0a),
	DARK_GRAY("Dark Gray", 0x0b),
	GRAY("Gray", 0x0c),
	LIGHT_GREEN("Light Green", 0x0d),
	LIGHT_BLUE("Light Blue", 0x0e),
	LIGHT_GRAY("Light Gray", 0x0f);

	public static final int LENGTH = 16;

	private static CBMColor[] colors = null;

	private final String name;
	private final int index;

	CBMColor(final String name, final int index)
	{
		this.name = name;
		this.index = index;
	}

	public String getName()
	{
		return name + " (0x0" + Integer.toHexString(index) + ")";
	}

	public int index()
	{
		return index;
	}

	public static CBMColor toCBMColor(int index)
	{
		if (colors == null)
		{
			colors = new CBMColor[LENGTH];

			for (CBMColor color : values())
			{
				colors[color.index()] = color;
			}
		}

		return colors[index];
	}
}
