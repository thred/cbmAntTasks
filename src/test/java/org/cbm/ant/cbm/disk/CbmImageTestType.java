package org.cbm.ant.cbm.disk;

import java.util.function.Function;

public enum CbmImageTestType
{
    D64(CbmImageTypes.D64, CbmImageType::createEmptyDisk),
    D64_KRILL(CbmImageTypes.D64,
        type -> type
            .createEmptyDisk()
            .withAllocateSectorStrategy(new SequentialCbmAllocateSectorStrategy())
            .withSectorInterleaves(CbmSectorInterleaves.parse("4, 18:3"))),
    D71(CbmImageTypes.D71, CbmImageType::createEmptyDisk),
    D81(CbmImageTypes.D81, CbmImageType::createEmptyDisk);

    private final CbmImageType<?> type;
    private final Function<CbmImageType<?>, CbmDisk> factory;

    CbmImageTestType(CbmImageType<?> type, Function<CbmImageType<?>, CbmDisk> factory)
    {
        this.type = type;
        this.factory = factory;
    }

    public CbmImageType<?> getType()
    {
        return type;
    }

    CbmDisk createEmptyDisk()
    {
        return factory.apply(type);
    }
}
