package org.cbm.ant.cbm.bitmap;

import org.cbm.ant.util.Util;

public class CBMPreferredColorIndex
{
    public static CBMPreferredColorIndex of(String preferredColorIndex)
    {
        String[] split = preferredColorIndex.split(":");

        if (split.length != 2)
        {
            throw new IllegalArgumentException("Semicolon is missing");
        }

        CBMColor color;
        int index;

        try
        {
            color = CBMColor.valueOf(split[0].trim());
        }
        catch (IllegalArgumentException e)
        {
            throw new IllegalArgumentException("Invalid color: " + split[0].trim(), e);
        }

        try
        {
            index = Util.parseHex(split[1].trim());
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid index: " + split[1].trim(), e);
        }

        return new CBMPreferredColorIndex(color, index);
    }

    CBMColor color;
    int index;

    public CBMPreferredColorIndex(CBMColor color, int index)
    {
        super();
        this.color = color;
        this.index = index;
    }

    public CBMColor getColor()
    {
        return color;
    }

    public int getIndex()
    {
        return index;
    }

}
