package org.cbm.ant.cbm.bitmap;

public enum CBMColorSpace
{
    RGB("RGB", CBMColorConversion.RGB2RGB),

    YCbCr("YCbCr", CBMColorConversion.RGB2YCbCr),

    YUV("YUV", CBMColorConversion.RGB2YUV),

    HSL("HSL", CBMColorConversion.RGB2HSL),

    HSV("HSV", CBMColorConversion.RGB2HSV),

    xyY("xyY", CBMColorConversion.RGB2xyY),

    XYZ("XYZ", CBMColorConversion.RGB2XYZ),

    LAB("LAB", CBMColorConversion.RGB2LAB),

    LUV("LUV", CBMColorConversion.RGB2LUV);

    private final String name;
    private final CBMColorConversion conversion;

    CBMColorSpace(String name, CBMColorConversion conversion)
    {
        this.name = name;
        this.conversion = conversion;
    }

    public String getName()
    {
        return name;
    }

    public CBMColorConversion getConversion()
    {
        return conversion;
    }

    public float[] convertTo(int rgb, float[] to)
    {
        return conversion.convert(rgb, to);
    }

    @Override
    public String toString()
    {
        return name;
    }

}
