package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CBMDisk
{

	private final List<CBMDiskTrack> tracks = new ArrayList<CBMDiskTrack>();

	private CBMDiskType type;
	private int dirTrackNr;
	private int dirSectorNrInterleave;
	private int trackNrInterleave;
	private int sectorNrInterleave;

	public CBMDisk()
	{
		super();
	}

	public void clearMarks()
	{
		for (CBMDiskTrack track : tracks)
		{
			track.clearMarks();
		}
	}

	public void mark(int trackNr, int sectorNr, int mark)
	{
		CBMDiskSector sector = getSector(trackNr, sectorNr);

		if (sector.getMark() == mark)
		{
			return;
		}

		sector.setMark(mark);

		trackNr = sector.getNextTrackNr();

		if (trackNr > 0)
		{
			sectorNr = sector.getNextSectorNr();

			mark(trackNr, sectorNr, mark);
		}
	}

	public void init(CBMDiskType type)
	{
		setType(type);

		tracks.clear();

		for (int trackNr = 1; trackNr <= type.getNumberOfTracks(); trackNr += 1)
		{
			CBMDiskTrack track = new CBMDiskTrack(trackNr);

			track.init(type.getNumberOfSectors(trackNr));

			tracks.add(track);
		}
	}

	public void load(File file) throws IOException
	{
		CBMDiskType type = CBMDiskType.determineDiskType(file);

		if (type == null)
		{
			throw new IOException("Unsupported file type");
		}

		InputStream in = new FileInputStream(file);

		try
		{
			read(in, type);
		}
		finally
		{
			in.close();
		}
	}

	public void read(InputStream in, CBMDiskType type) throws IOException
	{
		setType(type);

		tracks.clear();

		for (int trackNr = 1; trackNr <= type.getNumberOfTracks(); trackNr += 1)
		{
			CBMDiskTrack track = new CBMDiskTrack(trackNr);

			track.read(in, type.getNumberOfSectors(trackNr));

			tracks.add(track);
		}
	}

	public void save(File file) throws IOException
	{
		OutputStream out = new FileOutputStream(file);

		try
		{
			write(out);
		}
		finally
		{
			out.close();
		}
	}

	public void write(OutputStream out) throws IOException
	{
		for (CBMDiskTrack track : tracks)
		{
			track.write(out);
		}
	}

	public CBMDiskType getType()
	{
		return type;
	}

	public void setType(CBMDiskType type)
	{
		this.type = type;

		dirTrackNr = type.getDirTrackNr();
		dirSectorNrInterleave = type.getDirSectorNrInterleave();
		trackNrInterleave = type.getTrackNrInterleave();
		sectorNrInterleave = type.getSectorNrInterleave();
	}

	public int getDirTrackNr()
	{
		return dirTrackNr;
	}

	public void setDirTrackNr(int dirTrackNr)
	{
		this.dirTrackNr = dirTrackNr;
	}

	public int getDirSectorNrInterleave()
	{
		return dirSectorNrInterleave;
	}

	public void setDirSectorNrInterleave(int dirSectorNrInterleave)
	{
		this.dirSectorNrInterleave = dirSectorNrInterleave;
	}

	public int getTrackNrInterleave()
	{
		return trackNrInterleave;
	}

	public void setTrackNrInterleave(int trackNrInterleave)
	{
		this.trackNrInterleave = trackNrInterleave;
	}

	public int getSectorNrInterleave()
	{
		return sectorNrInterleave;
	}

	public void setSectorNrInterleave(int sectorNrInterleave)
	{
		this.sectorNrInterleave = sectorNrInterleave;
	}

	public CBMDiskTrack getTrack(int trackNr)
	{
		try
		{
			return tracks.get(trackNr - 1);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new IllegalArgumentException(String.format("Invalid track number %d (disk has %d tracks)", trackNr,
					tracks.size()));
		}
	}

	public CBMDiskSector getSector(CBMDiskLocation location)
	{
		return getSector(location.getTrackNr(), location.getSectorNr());
	}

	public CBMDiskSector getSector(int trackNr, int sectorNr)
	{
		return getTrack(trackNr).getSector(sectorNr);
	}

	public int getNumberOfTracks()
	{
		return tracks.size();
	}

	public int getNumberOfSectors(int trackNr)
	{
		return getTrack(trackNr).getNumberOfSectors();
	}

}
