package org.cbm.ant.cbm.disk;

public enum CBMFileType
{

    DEL("del", 0), //
    SEQ("seq", 1), //
    PRG("prg", 2), //
    USR("usr", 3), //
    REL("rel", 4);

    private final String name;
    private final int type;

    private CBMFileType(String name, int type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public int getType()
    {
        return type;
    }

    public static CBMFileType toCBMFileType(int fileType)
    {
        fileType &= 0x07;

        for (CBMFileType type : values())
        {
            if (type.getType() == fileType)
            {
                return type;
            }
        }

        return null;
    }
}
