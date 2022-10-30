package org.cbm.ant.cbm.disk;

public class CbmAllocateSectorStrategies
{
    public static final CbmAllocateSectorStrategy DEFAULT = new DefaultCbmAllocateSectorStrategy();

    public static final CbmAllocateSectorStrategy SEQUENTIAL = new SequentialCbmAllocateSectorStrategy();

    public static CbmAllocateSectorStrategy parse(String allocateSectorStrategy)
    {
        switch (allocateSectorStrategy)
        {
            case "DEFAULT":
                return DEFAULT;

            case "SEQUENTIAL":
                return SEQUENTIAL;

            default:
                throw new IllegalArgumentException("Unknown strategy: " + allocateSectorStrategy);
        }
    }
}
