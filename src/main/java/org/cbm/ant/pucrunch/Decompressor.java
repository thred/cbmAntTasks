package org.cbm.ant.pucrunch;

public enum Decompressor
{

    C128("128"),

    C64("64"),

    VIC20("20"),

    C16("16"),

    PLUS4("16"),

    NONE("0");

    private final String key;

    Decompressor(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

}
