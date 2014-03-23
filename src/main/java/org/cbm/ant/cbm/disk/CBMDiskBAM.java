package org.cbm.ant.cbm.disk;

/**
 * Service for accessing the BAM of a CBM disk
 * 
 * @author thred
 */
public class CBMDiskBAM
{

	private static final int DEFAULT_DOS_VERSION = 0x41;
	private static final String DEFAULT_DOS_TYPE = "a2";

	private final CBMDiskOperator operator;

	/**
	 * Creates the service for accessing the BAM using the speicifed operator
	 * 
	 * @param operator the operator
	 */
	public CBMDiskBAM(CBMDiskOperator operator)
	{
		super();

		this.operator = operator;
	}

	/**
	 * Returns the disk
	 * 
	 * @return the disk
	 */
	private CBMDisk getDisk()
	{
		return operator.getDisk();
	}

	/**
	 * Returns the BAM sector
	 * 
	 * @return the BAM sector
	 */
	public CBMDiskSector getBAMSector()
	{
		CBMDisk disk = getDisk();

		return disk.getSector(disk.getBAMTrackNr(), 0x00);
	}

	/**
	 * Formats the BAM.
	 * 
	 * @param diskName the name of the disk
	 * @param diskId the id for the disk
	 */
	public void format(String diskName, String diskId)
	{
		CBMDisk disk = getDisk();
		CBMDiskSector bam = getBAMSector();

		bam.clear();

		setDirTrackNr(disk.getBAMTrackNr());
		setDirSectorNr(1);
		setDOSVersion(DEFAULT_DOS_VERSION);

		for (int trackNr = 1; trackNr <= disk.getFormat().getNumberOfTracks(); trackNr += 1)
		{
			for (int sectorNr = 0; sectorNr < disk.getFormat().getNumberOfSectors(trackNr); sectorNr += 1)
			{
				setSectorUsed(trackNr, sectorNr, false);
			}
		}

		setSectorUsed(bam.getTrackNr(), bam.getSectorNr(), true);
		setDiskName(diskName);

		bam.setByte(0xa0, 0xa0);
		bam.setByte(0xa1, 0xa0);

		setDiskID(diskId);

		bam.setByte(0xa4, 0xa0);

		setDOSType(DEFAULT_DOS_TYPE);

		bam.fill(0xa7, 9, 0xa0);
	}

	/**
	 * Searches for a free sector for program data, starting at the BAM track
	 * 
	 * @return a free sector
	 * @throws CBMDiskException on occasion (e.g. disk is full)
	 */
	public CBMDiskLocation findFreeSector() throws IllegalStateException, CBMDiskException
	{
		int offset = 1;
		boolean allowBAMTrack = false;

		return findFreeSector(offset, allowBAMTrack);
	}

	/**
	 * Searches for a free sector for directory data, starting at the BAM track (in theory, it is possible that the
	 * directory exceeds the track)
	 * 
	 * @return a free sector
	 * @throws CBMDiskException on occasion (e.g. disk is full)
	 */
	public CBMDiskLocation findFreeDirSector() throws IllegalStateException, CBMDiskException
	{
		int offset = 0;
		boolean allowBAMTrack = true;

		return findFreeSector(offset, allowBAMTrack);
	}

	/**
	 * Searches for a free sector for program or directory data, starting at the BAM track
	 * 
	 * @return a free sector
	 * @throws CBMDiskException on occasion (e.g. disk is full)
	 */
	protected CBMDiskLocation findFreeSector(int offset, boolean allowBAMTrack) throws IllegalStateException,
			CBMDiskException
	{
		if (isFull())
		{
			throw new CBMDiskException(CBMDiskException.Type.DISK_FULL);
		}

		int dirTrackNr = operator.getDisk().getBAMTrackNr();

		while (offset < 256)
		{
			if (((dirTrackNr - offset) > 0) && (getFreeSectorsOfTrack(dirTrackNr - offset) > 0))
			{
				return findFreeSector(dirTrackNr - offset, 0, 0, allowBAMTrack);
			}

			if (((dirTrackNr + offset) <= operator.getDisk().getFormat().getNumberOfTracks())
					&& (getFreeSectorsOfTrack(dirTrackNr + offset) > 0))
			{
				return findFreeSector(dirTrackNr + offset, 0, 0, allowBAMTrack);
			}

			offset += 1;
		}

		throw new CBMDiskException(CBMDiskException.Type.NO_FREE_SECTOR);
	}

	/**
	 * Searches for a free sector for program data, starting at the specified location plus the disks sector interleave.
	 * This is the only method that respects the sector interleave of the disk.
	 * 
	 * @param location the location the location
	 * @return the free sector
	 * @throws CBMDiskException on occasion (e.g. disk is full)
	 */
	public CBMDiskLocation findNextFreeSector(CBMDiskLocation location) throws CBMDiskException
	{
		int trackNr = location.getTrackNr();
		int sectorNr = (location.getSectorNr() + getDisk().getSectorNrInterleave())
				% operator.getDisk().getFormat().getNumberOfSectors(trackNr);
		int trackNrIncrement = (trackNr < getDisk().getBAMTrackNr()) ? -1 : 1;

		return findFreeSector(trackNr, sectorNr, trackNrIncrement, false);
	}

	/**
	 * Searches for a free sector for directory data, starting at the specified location plus the disks sector
	 * interleave for directories. This is the only method that respects the directory sector interleave of the disk.
	 * 
	 * @param location the location the location
	 * @return the free sector
	 * @throws CBMDiskException on occasion (e.g. disk is full)
	 */
	public CBMDiskLocation findNextFreeDirSector(CBMDiskLocation location) throws CBMDiskException
	{
		int trackNr = location.getTrackNr();
		int sectorNr = (location.getSectorNr() + getDisk().getDirSectorNrInterleave())
				% operator.getDisk().getFormat().getNumberOfSectors(trackNr);
		int trackNrIncrement = (trackNr < getDisk().getBAMTrackNr()) ? -1 : 1;

		return findFreeSector(trackNr, sectorNr, trackNrIncrement, true);
	}

	/**
	 * Searches for a free sector for program and/or directory data. Expected that the specified sector is free, if not
	 * keeps adding one to the sector and the track increment to the track.
	 * 
	 * @param trackNr the number of the track
	 * @param sectorNr the number of the sector
	 * @param trackNrIncrement the increment of the track number (should be -1 or 1)
	 * @param allowBAMTrack true to allow returning a sector of the track that usually houses the BAM (and the
	 *            directory)
	 * @return the free sector
	 * @throws CBMDiskException on occasion (e.g. disk is full)
	 */
	protected CBMDiskLocation findFreeSector(int trackNr, int sectorNr, int trackNrIncrement, boolean allowBAMTrack)
			throws CBMDiskException
	{
		if (isFull())
		{
			throw new CBMDiskException(CBMDiskException.Type.DISK_FULL);
		}

		int count = 0;
		int bamTrackNr = getDisk().getBAMTrackNr();
		int numberOfTracks = getDisk().getFormat().getNumberOfTracks();

		while (((!allowBAMTrack) && (bamTrackNr == trackNr)) || (getFreeSectorsOfTrack(trackNr) <= 0))
		{
			if (trackNrIncrement == 0)
			{
				throw new IllegalArgumentException("No track number increment specified");
			}

			trackNr += trackNrIncrement;

			if ((trackNr < 1) || (trackNr > numberOfTracks))
			{
				trackNrIncrement = -trackNrIncrement;
				trackNr = bamTrackNr + trackNrIncrement;
			}

			count += 1;

			if (count > 255)
			{
				throw new CBMDiskException(CBMDiskException.Type.NO_FREE_SECTOR);
			}
		}

		int numberOfSectors = getDisk().getFormat().getNumberOfSectors(trackNr);

		count = 0;

		while (isSectorUsed(trackNr, sectorNr))
		{
			sectorNr = (((sectorNr + 1) - 1) % numberOfSectors) + 1;

			count += 1;

			if (count > 255)
			{
				throw new CBMDiskException(CBMDiskException.Type.NO_FREE_SECTOR);
			}
		}

		return new CBMDiskLocation(trackNr, sectorNr);
	}

	/**
	 * Returns the track number of the directory as specified in the BAM
	 * 
	 * @return the track number of the directory as specified in the BAM
	 */
	public int getDirTrackNr()
	{
		return getBAMSector().getByte(0x00);
	}

	/**
	 * Sets the track number of the directory to the BAM
	 * 
	 * @param trackNr the track number
	 */
	public void setDirTrackNr(int trackNr)
	{
		getBAMSector().setByte(0x00, trackNr);
	}

	public int getDirSectorNr()
	{
		return getBAMSector().getByte(0x01);
	}

	public void setDirSectorNr(int sectorNr)
	{
		getBAMSector().setByte(0x01, sectorNr);
	}

	public CBMDiskLocation getDirLocation()
	{
		return new CBMDiskLocation(getDirTrackNr(), getDirSectorNr());
	}

	public int getDOSVersion()
	{
		return getBAMSector().getByte(0x02);
	}

	public void setDOSVersion(int dosVersion)
	{
		getBAMSector().setByte(0x02, dosVersion);
	}

	public boolean isSectorUsed(CBMDiskLocation location)
	{
		return isSectorUsed(location.getTrackNr(), location.getSectorNr());
	}

	public boolean isSectorUsed(int trackNr, int sectorNr)
	{
		int position = getBAMSectorPosition(trackNr) + 1 + (sectorNr / 8);
		int value = getBAMSector().getByte(position);

		return (value & (1 << (sectorNr % 8))) == 0;
	}

	public int getFreeSectorsOfTrack(int trackNr)
	{
		int position = getBAMSectorPosition(trackNr);

		return getBAMSector().getByte(position);
	}

	public int getFreeSectors()
	{
		int result = 0;
		CBMDisk disk = getDisk();
		CBMDiskFormat format = disk.getFormat();
		int dirTrack = disk.getBAMTrackNr();

		for (int trackNr = 1; trackNr <= format.getNumberOfTracks(); trackNr += 1)
		{
			if (trackNr != dirTrack)
			{
				result += getFreeSectorsOfTrack(trackNr);
			}
		}

		return result;
	}

	public boolean isFull()
	{
		return getFreeSectors() == 0;
	}

	public void setSectorUsed(CBMDiskLocation location, boolean used)
	{
		setSectorUsed(location.getTrackNr(), location.getSectorNr(), used);
	}

	public void setSectorUsed(int trackNr, int sectorNr, boolean used)
	{
		int position = getBAMSectorPosition(trackNr);
		CBMDiskSector bam = getBAMSector();
		int value = bam.getByte(position + 1) | (bam.getByte(position + 2) << 8) | (bam.getByte(position + 3) << 16);

		if (used)
		{
			value &= ~(1 << sectorNr);
		}
		else
		{
			value |= (1 << sectorNr);
		}

		bam.setByte(position, Integer.bitCount(value));
		bam.setByte(position + 1, value & 0xff);
		bam.setByte(position + 2, (value >> 8) & 0xff);
		bam.setByte(position + 3, (value >> 16) & 0xff);
	}

	protected int getBAMSectorPosition(int trackNr)
	{
		if (trackNr <= 0)
		{
			throw new IllegalArgumentException("Tracks start with index 1");
		}
		if (trackNr <= 35)
		{
			return 0x04 + ((trackNr - 1) * 4);
		}
		else
		{
			return 0xab + ((trackNr - 36) * 4);
		}
	}

	public String getDiskName()
	{
		return getBAMSector().getString(0x90, 0x10);
	}

	public void setDiskName(String diskName)
	{
		getBAMSector().setString(0x90, 0x10, diskName);
	}

	public String getDiskID()
	{
		return CBMDiskUtil.fromCBMDOSName(getBAMSector().getBytes(0xa2, 0x02));
	}

	public void setDiskID(String diskID)
	{
		getBAMSector().setString(0xa2, 0x02, diskID);
	}

	public String getDOSType()
	{
		return CBMDiskUtil.fromCBMDOSName(getBAMSector().getBytes(0xa5, 0x02));
	}

	public void setDOSType(String dosType)
	{
		getBAMSector().setBytes(0xa5, CBMDiskUtil.toCBMDOSName(dosType, 0x02));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		CBMDisk disk = getDisk();

		operator.getDir().mark();

		StringBuilder builder = new StringBuilder();

		builder.append(String.format("BAM of Disk %s\n", getDiskName()));
		builder.append("----------------------------------------\n");
		builder.append(String.format("Disk Type: %s (%d tracks)\n", disk.getFormat(), disk.getFormat()
				.getNumberOfTracks()));
		builder.append(String.format("Directory track / sector: %d / %d\n", getDirTrackNr(), getDirSectorNr()));
		builder.append(String.format("Disk DOS Version: $%2x\n", getDOSVersion()));
		builder.append(String.format("Disk ID / DOS Type: %s / %s\n", getDiskID(), getDOSType()));
		builder.append("----------------------------------------\n");

		for (int i = 1; i <= disk.getFormat().getNumberOfTracks(); i += 1)
		{
			builder.append(String.format("Track %2d ", i));

			for (int j = 0; j < disk.getNumberOfSectors(i); j += 1)
			{
				if (isSectorUsed(i, j))
				{
					builder.append(CBMDiskUtil.id2Key(disk.getSector(i, j).getMark()));
				}
				else
				{
					builder.append("-");
				}
			}

			builder.append(String.format(" (%d sectors free)\n", getFreeSectorsOfTrack(i)));
		}

		return builder.toString();
	}

}
