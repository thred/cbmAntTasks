package org.cbm.ant.cbm.disk;

public enum CbmFileType
{
    DEL("del", 0),
    SEQ("seq", 1),
    PRG("prg", 2),
    USR("usr", 3),
    REL("rel", 4),
    CBM("cbm", 5);

    private final String name;
    private final int type;

    CbmFileType(String name, int type)
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

    public static CbmFileType toCbmFileType(int fileType)
    {
        fileType &= 0x07;

        for (CbmFileType type : values())
        {
            if (type.getType() == fileType)
            {
                return type;
            }
        }

        return null;
    }
}
