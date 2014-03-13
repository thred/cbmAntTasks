package org.cbm.ant.cbm.disk;

import java.io.File;

public enum CBMDiskType
{

	CBM_154x(new int[] { //
			21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, // Track 1-17 
			19, 19, 19, 19, 19, 19, 19, // Track 18-24
			18, 18, 18, 18, 18, 18, // Track 25-30
			17, 17, 17, 17, 17
	// Track 31-35
	}, false, 18, 3, -1, 10),

	CBM_154x_EXTENDED(new int[] { //
			21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, // Track 1-17 
			19, 19, 19, 19, 19, 19, 19, // Track 18-24
			18, 18, 18, 18, 18, 18, // Track 25-30
			17, 17, 17, 17, 17, // Track 31-35
			17, 17, 17, 17, 17
	// Track 36-40
	}, false, 18, 3, -1, 10);

	private final int numberOfTracks;
	private final int[] numberOfSectors;
	private final boolean errorInformationAvailable;
	private final int fileSize;
	private final int dirTrackNr;
	private final int dirSectorNrInterleave;
	private final int trackNrInterleave;
	private final int sectorNrInterleave;

	private CBMDiskType(int[] numberOfSectors, boolean errorInformationAvailable, int dirTrackNr,
			int dirSectorNrInterleave, int trackNrInterleave, int sectorNrInterleave)
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
		this.dirTrackNr = dirTrackNr;
		this.dirSectorNrInterleave = dirSectorNrInterleave;
		this.trackNrInterleave = trackNrInterleave;
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
		if ((trackNr == 0) || (trackNr > numberOfTracks))
		{
			throw new IllegalArgumentException(String.format("Invalid track: %d (must be between 1 and %d)", trackNr,
					numberOfTracks));
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

	public int getDirTrackNr()
	{
		return dirTrackNr;
	}

	public int getDirSectorNrInterleave()
	{
		return dirSectorNrInterleave;
	}

	public int getTrackNrInterleave()
	{
		return trackNrInterleave;
	}

	public int getSectorNrInterleave()
	{
		return sectorNrInterleave;
	}

	public static CBMDiskType determineDiskType(File file)
	{
		long size = file.length();

		for (CBMDiskType type : values())
		{
			if (type.getFileSize() == size)
			{
				return type;
			}
		}

		return null;
	}
}
