package org.cbm.ant.cbm.disk;

import java.util.function.Function;

public class SimpleCbmImageType<AnyCbmDisk extends CbmDisk> implements CbmImageType<AnyCbmDisk>
{
    private final String name;
    private final CbmSectorMap sectorMap;
    private final CbmSectorMap extendedSectorMap;
    private final Function<CbmImage, AnyCbmDisk> factory;

    public SimpleCbmImageType(String name, CbmSectorMap sectorMap, CbmSectorMap extendedSectorMap,
        Function<CbmImage, AnyCbmDisk> factory)
    {
        super();
        this.name = name;
        this.sectorMap = sectorMap;
        this.extendedSectorMap = extendedSectorMap;
        this.factory = factory;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public CbmSectorMap getSectorMap()
    {
        return sectorMap;
    }

    public CbmSectorMap getExtendedSectorMap()
    {
        return extendedSectorMap;
    }

    @Override
    public boolean isCompatible(byte[] bytes)
    {
        int length = bytes.length;
        int sectorCount = sectorMap.size();
        int dataSize = sectorCount * 256;

        if (length == dataSize || length == dataSize + sectorCount)
        {
            return true;
        }

        if (extendedSectorMap != null)
        {
            int extendedSectorCount = extendedSectorMap.size();
            int extendedDataSize = extendedSectorCount * 256;

            return length == extendedDataSize || length == extendedDataSize + extendedSectorCount;
        }

        return false;
    }

    @Override
    public AnyCbmDisk createEmptyDisk()
    {
        return factory.apply(new SimpleCbmImage(sectorMap, extendedSectorMap, false, false));
    }
}
