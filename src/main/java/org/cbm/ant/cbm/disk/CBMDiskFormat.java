package org.cbm.ant.cbm.disk;

import java.io.File;

public enum CBMDiskFormat
{

    CBM_154x(new int[]{ //
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21, // Track 1-17 
        19,
        19,
        19,
        19,
        19,
        19,
        19, // Track 18-24
        18,
        18,
        18,
        18,
        18,
        18, // Track 25-30
        17,
        17,
        17,
        17,
        17
    // Track 31-35
    }, false, 18, 3, 10),

    CBM_154x_EXTENDED(new int[]{ //
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21,
        21, // Track 1-17 
        19,
        19,
        19,
        19,
        19,
        19,
        19, // Track 18-24
        18,
        18,
        18,
        18,
        18,
        18, // Track 25-30
        17,
        17,
        17,
        17,
        17, // Track 31-35
        17,
        17,
        17,
        17,
        17
    // Track 36-40
    }, false, 18, 3, 10);

    private final int numberOfTracks;
    private final int[] numberOfSectors;
    private final boolean errorInformationAvailable;
    private final int fileSize;
    private final int bamTrackNr;
    private final int dirSectorNrInterleave;
    private final int sectorNrInterleave;

    private CBMDiskFormat(int[] numberOfSectors, boolean errorInformationAvailable, int bamTrackNr,
        int dirSectorNrInterleave, int sectorNrInterleave)
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
        this.dirSectorNrInterleave = dirSectorNrInterleave;
        this.sectorNrInterleave = sectorNrInterleave;
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

    public int getDirSectorNrInterleave()
    {
        return dirSectorNrInterleave;
    }

    public int getSectorNrInterleave()
    {
        return sectorNrInterleave;
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
