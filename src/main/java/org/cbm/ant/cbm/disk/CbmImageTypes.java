package org.cbm.ant.cbm.disk;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CbmImageTypes
{
    public static final CbmImageType<Cbm1541Disk> D64 =
        new SimpleCbmImageType<>("D64", CbmSectorMap.of(17, 21).and(7, 19).and(6, 18).and(5, 17),
            CbmSectorMap.of(17, 21).and(7, 19).and(6, 18).and(10, 17),
            disk -> new Cbm1541Disk(disk, CbmSectorLocation.parse("18/0"), CbmSectorRange.parse("18/1-18"),
                new DefaultCbmAllocateSectorStrategy(), CbmSectorInterleaves.parse("10, 18: 3")));

    public static final CbmImageType<Cbm1571Disk> D71 = new SimpleCbmImageType<>("D71",
        CbmSectorMap.of(17, 21).and(7, 19).and(6, 18).and(5, 17).and(17, 21).and(7, 19).and(6, 18).and(5, 17), null,
        disk -> new Cbm1571Disk(disk, CbmSectorLocation.parse("18/0"), CbmSectorLocation.parse("53/0"),
            CbmSectorRange.parse("18/1-18"), new DefaultCbmAllocateSectorStrategy(),
            CbmSectorInterleaves.parse("6, 18: 3, 53: 3")));

    public static final CbmImageType<Cbm1581Disk> D81 = new SimpleCbmImageType<>("D81", CbmSectorMap.of(80, 40), null,
        disk -> new Cbm1581Disk(disk, CbmSectorLocation.parse("40/0"), CbmSectorLocation.parse("40/1"),
            CbmSectorLocation.parse("40/2"), CbmSectorRange.parse("40/3-39"), new DefaultCbmAllocateSectorStrategy(),
            CbmSectorInterleaves.parse("1")));

    private static final List<CbmImageType<? extends AbstractCbmDisk>> KNWON_TYPES = Arrays.asList(D64, D71, D81);

    public static Optional<CbmImageType<? extends AbstractCbmDisk>> getByName(String name)
    {
        return KNWON_TYPES.stream().filter(type -> type.getName().equalsIgnoreCase(name)).findFirst();
    }
}
