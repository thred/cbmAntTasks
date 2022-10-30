package org.cbm.ant.cbm.disk;

public interface CbmImageType<AnyCbmDisk extends CbmDisk>
{
    String getName();

    boolean isCompatible(byte[] bytes);

    AnyCbmDisk createEmptyDisk();
}