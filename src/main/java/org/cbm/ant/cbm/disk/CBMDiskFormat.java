package org.cbm.ant.cbm.disk;

import java.io.File;

public enum CBMDiskFormat
{

    CBM_154x(new int[]{
        21, // 1
        21, // 2
        21, // 3
        21, // 4
        21, // 5
        21, // 6
        21, // 7
        21, // 8
        21, // 9
        21, // 10
        21, // 11
        21, // 12
        21, // 13
        21, // 14
        21, // 15
        21, // 16
        21, // 17
        19, // 18
        19, // 19
        19, // 20
        19, // 21
        19, // 22
        19, // 23
        19, // 24
        18, // 25
        18, // 26
        18, // 27
        18, // 28
        18, // 29
        18, // 30
        17, // 31
        17, // 32
        17, // 33
        17, // 34
        17 //  35
    }, false, 18, CBMSectorInterleaves.parse("10, 18: 3")),

    CBM_154x_EXTENDED(new int[]{ //
        21, // 1
        21, // 2
        21, // 3
        21, // 4
        21, // 5
        21, // 6
        21, // 7
        21, // 8
        21, // 9
        21, // 10
        21, // 11
        21, // 12
        21, // 13
        21, // 14
        21, // 15
        21, // 16
        21, // 17
        19, // 18
        19, // 19
        19, // 20
        19, // 21
        19, // 22
        19, // 23
        19, // 24
        18, // 25
        18, // 26
        18, // 27
        18, // 28
        18, // 29
        18, // 30
        17, // 31
        17, // 32
        17, // 33
        17, // 34
        17, // 35
        17, // 36
        17, // 37
        17, // 38
        17, // 39
        17 //  40
    }, false, 18, CBMSectorInterleaves.parse("10, 18: 3"));

    private final int numberOfTracks;
    private final int[] numberOfSectors;
    private final boolean errorInformationAvailable;
    private final int fileSize;
    private final int bamTrackNr;
    private final CBMSectorInterleaves sectorInterleaves;

    CBMDiskFormat(int[] numberOfSectors, boolean errorInformationAvailable, int bamTrackNr,
        CBMSectorInterleaves interleaves)
    {
        numberOfTracks = numberOfSectors.length;

        this.numberOfSectors = numberOfSectors;
        this.errorInformationAvailable = errorInformationAvailable;

        int fileSize = 0;

        for (int numberOfSector : numberOfSectors)
        {
            fileSize += numberOfSector * 256;
        }

        this.fileSize = fileSize;
        this.bamTrackNr = bamTrackNr;
        sectorInterleaves = interleaves;
    }

    public int getNumberOfTracks()
    {
        return numberOfTracks;
    }

    public int[] getNumberOfSectors()
    {
        return numberOfSectors;
    }

    public int getNumberOfSectors(int trackNr)
    {
        if (trackNr == 0 || trackNr > numberOfTracks)
        {
            throw new IllegalArgumentException(
                String.format("Invalid track: %d (must be between 1 and %d)", trackNr, numberOfTracks));
        }

        return numberOfSectors[trackNr - 1];
    }

    public boolean isErrorInformationAvailable()
    {
        return errorInformationAvailable;
    }

    public int getFileSize()
    {
        return fileSize;
    }

    public int getBAMTrackNr()
    {
        return bamTrackNr;
    }

    public CBMSectorInterleaves getSectorInterleaves()
    {
        return sectorInterleaves;
    }

    public static CBMDiskFormat determineDiskType(File file)
    {
        long size = file.length();

        for (CBMDiskFormat format : values())
        {
            if (format.getFileSize() == size)
            {
                return format;
            }
        }

        return null;
    }
}
