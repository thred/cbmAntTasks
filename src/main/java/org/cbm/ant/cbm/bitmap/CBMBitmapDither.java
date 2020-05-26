package org.cbm.ant.cbm.bitmap;

/**
 * Thanks to: http://www.tannerhelland.com/4660/dithering-eleven-algorithms-source-code/
 *
 * @author thred
 */
public enum CBMBitmapDither
{

    NONE("none", new CBMBitmapNoneDitherStrategy()),

    ORDERED_2("Ordered 2x2", new CBMBitmapOrderedDitherStrategy(new int[]{1, 3, 4, 2}, 5)),

    ORDERED_3("Ordered 3x3", new CBMBitmapOrderedDitherStrategy(new int[]{3, 7, 4, 6, 1, 9, 2, 8, 5}, 10)),

    ORDERED_4("Ordered 4x4",
        new CBMBitmapOrderedDitherStrategy(new int[]{1, 9, 3, 11, 13, 5, 15, 7, 4, 12, 2, 10, 16, 8, 14, 6}, 17)),

    ORDERED_8("Ordered 8x8",
        new CBMBitmapOrderedDitherStrategy(new int[]{
            1,
            49,
            13,
            61,
            4,
            52,
            16,
            64,
            33,
            17,
            45,
            29,
            36,
            20,
            48,
            32,
            9,
            57,
            5,
            53,
            12,
            60,
            8,
            56,
            41,
            25,
            37,
            21,
            44,
            28,
            40,
            24,
            3,
            51,
            15,
            63,
            2,
            50,
            14,
            62,
            35,
            19,
            47,
            31,
            34,
            18,
            46,
            30,
            11,
            59,
            7,
            55,
            10,
            58,
            6,
            54,
            43,
            27,
            39,
            23,
            42,
            26,
            38,
            22}, 65)),

    FLOYD_STEINBERG("Floyd-Steinberg",
        new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][]{{null, Integer.MAX_VALUE, 7}, {3, 5, 1}})),

    FALSE_FLOYD_STEINBERG("False Floyd-Steinberg",
        new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][]{{Integer.MAX_VALUE, 3}, {3, 2}})),

    JARVIS_JUDICE_NINKE("Jarvis-Judice-Ninke",
        new CBMBitmapErrorDiffusionDitherStrategy(
            new Integer[][]{{null, null, Integer.MAX_VALUE, 7, 5}, {3, 5, 7, 5, 3}, {1, 3, 5, 3, 1}})),

    STUCKI("Stucki",
        new CBMBitmapErrorDiffusionDitherStrategy(
            new Integer[][]{{null, null, Integer.MAX_VALUE, 8, 4}, {2, 4, 8, 4, 2}, {1, 2, 4, 2, 1}})),

    ATKINSON("Atkinson",
        new CBMBitmapErrorDiffusionDitherStrategy(
            new Integer[][]{{null, Integer.MAX_VALUE, 1, 1}, {1, 1, 1, null}, {null, 1, null, null}})),

    BURKES("Burkes",
        new CBMBitmapErrorDiffusionDitherStrategy(
            new Integer[][]{{null, null, Integer.MAX_VALUE, 8, 4}, {2, 4, 8, 4, 2}})),

    SIERRA("Sierra",
        new CBMBitmapErrorDiffusionDitherStrategy(
            new Integer[][]{{null, null, Integer.MAX_VALUE, 5, 3}, {2, 4, 5, 4, 2}, {null, 2, 3, 2, null}})),

    TWO_ROW_SIERRA("Two-Row Sierra",
        new CBMBitmapErrorDiffusionDitherStrategy(
            new Integer[][]{{null, null, Integer.MAX_VALUE, 4, 3}, {1, 2, 3, 2, 1}})),

    SIERRA_LIGHT("Sierra Light",
        new CBMBitmapErrorDiffusionDitherStrategy(new Integer[][]{{null, Integer.MAX_VALUE, 2}, {1, 1, null}}));

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
