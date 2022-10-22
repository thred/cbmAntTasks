package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a CBM disk image, like the data of a d64 image with some additional data
 *
 * @author thred
 */
public class CBMDisk
{

    private final List<CBMDiskTrack> tracks = new ArrayList<>();

    private CBMDiskFormat format;
    private int bamTrackNr;
    private CBMSectorInterleaves sectorInterleaves;

    public CBMDisk()
    {
        super();
    }

    /**
     * Clears all temporary marks from all tracks
     */
    public void clearMarks()
    {
        for (CBMDiskTrack track : tracks)
        {
            track.clearMarks();
        }
    }

    /**
     * Sets the specified mark to the specified sector
     *
     * @param trackNr the number of the track (starts with 1)
     * @param sectorNr the number of the sector (starts with 0)
     * @param mark the mark
     * @throws IllegalArgumentException if the number of the track and/or sector is invalid
     */
    public void mark(int trackNr, int sectorNr, int mark) throws IllegalArgumentException
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

    /**
     * Initializes the disk using the specified format. This will wipe all data and invalidate the {@link CBMDiskDir}
     * and {@link CBMDiskBAM}.
     *
     * @param format the type
     */
    public void init(CBMDiskFormat format)
    {
        setFormat(format);

        tracks.clear();

        for (int trackNr = 1; trackNr <= format.getNumberOfTracks(); trackNr += 1)
        {
            CBMDiskTrack track = new CBMDiskTrack(trackNr);

            track.init(format.getNumberOfSectors(trackNr));

            tracks.add(track);
        }
    }

    /**
     * Loads the data from the specified file. Tries to automatically determine the format of the disk image.
     *
     * @param file the file
     * @throws IOException on occasion
     */
    public void load(File file) throws IOException
    {
        CBMDiskFormat format = CBMDiskFormat.determineDiskType(file);

        if (format == null)
        {
            throw new IOException("Unsupported file format");
        }

        try (InputStream in = new FileInputStream(file))
        {
            read(in, format);
        }
    }

    /**
     * Reads the data from the specified stream. Calls {@link #init(CBMDiskFormat)} using the specified type.
     *
     * @param in the steam
     * @param format the format
     * @throws IOException on occasion
     */
    protected void read(InputStream in, CBMDiskFormat format) throws IOException
    {
        init(format);

        tracks.clear();

        for (int trackNr = 1; trackNr <= format.getNumberOfTracks(); trackNr += 1)
        {
            CBMDiskTrack track = new CBMDiskTrack(trackNr);

            track.read(in, format.getNumberOfSectors(trackNr));

            tracks.add(track);
        }
    }

    /**
     * Saves the data to the specified file.
     *
     * @param file the file
     * @throws IOException on occasion
     */
    public void save(File file) throws IOException
    {
        try (OutputStream out = new FileOutputStream(file))
        {
            write(out);
        }
    }

    /**
     * Writes the data to the specified stream
     *
     * @param out the stream
     * @throws IOException on occasion
     */
    public void write(OutputStream out) throws IOException
    {
        for (CBMDiskTrack track : tracks)
        {
            track.write(out);
        }
    }

    /**
     * Returns the format of the disk
     *
     * @return the format
     */
    public CBMDiskFormat getFormat()
    {
        return format;
    }

    /**
     * Sets the format of the disk.
     *
     * @param format the format
     */
    public void setFormat(CBMDiskFormat format)
    {
        this.format = format;

        bamTrackNr = format.getBAMTrackNr();
        sectorInterleaves = format.getSectorInterleaves();
    }

    /**
     * Returns the number of the BAM track
     *
     * @return the number of the BAM track
     */
    public int getBAMTrackNr()
    {
        return bamTrackNr;
    }

    /**
     * Sets the number of the BAM track
     *
     * @param bamTrackNr the number of the BAM track
     */
    public void setBAMTrackNr(int bamTrackNr)
    {
        this.bamTrackNr = bamTrackNr;
    }

    /**
     * @return the sector interleaves
     */
    public CBMSectorInterleaves getSectorInterleaves()
    {
        return sectorInterleaves;
    }

    /**
     * Sets the sector interleaves.
     *
     * @param sectorInterleaves the interleaves
     */
    public void setSectorInterleaves(CBMSectorInterleaves sectorInterleaves)
    {
        this.sectorInterleaves = sectorInterleaves;
    }

    /**
     * Sets the sector interleaves from a comma-separated list: <code>[ &lt;TRACK&gt; [ \"-\" &lt;TRACK&gt; ] \":\" ]
     * &lt;INTERLEAVE&gt;</code>
     *
     * @param sectorInterleaves the interleaves
     */
    public void setSectorInterleaves(String sectorInterleaves)
    {
        this.sectorInterleaves =
            CBMSectorInterleaves.parse(format != null ? format.getSectorInterleaves() : null, sectorInterleaves);
    }

    /**
     * Returns the track
     *
     * @param trackNr the number of the track
     * @return the track
     * @throws IllegalArgumentException if the number of the track is invalid
     */
    public CBMDiskTrack getTrack(int trackNr) throws IllegalArgumentException
    {
        try
        {
            return tracks.get(trackNr - 1);
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException(
                String.format("Invalid track number %d (disk has %d tracks)", trackNr, tracks.size()));
        }
    }

    /**
     * Returns the specified sector
     *
     * @param location the location
     * @return the sector
     * @throws IllegalArgumentException if the number of the track and/or sector is invalid
     */
    public CBMDiskSector getSector(CBMDiskLocation location) throws IllegalArgumentException
    {
        return getSector(location.getTrackNr(), location.getSectorNr());
    }

    /**
     * Returns the specified sector
     *
     * @param trackNr the number of the track
     * @param sectorNr the number of the sector
     * @return the sector
     * @throws IllegalArgumentException if the number of the track and/or sector is invalid
     */
    public CBMDiskSector getSector(int trackNr, int sectorNr) throws IllegalArgumentException
    {
        return getTrack(trackNr).getSector(sectorNr);
    }

    /**
     * Returns the number of tracks
     *
     * @return the number of tracks
     */
    public int getNumberOfTracks()
    {
        return tracks.size();
    }

    /**
     * Returns the number of sectors of the specified track
     *
     * @param trackNr the number of the track
     * @return the number of sectors
     * @throws IllegalArgumentException if the number of the track is invalid
     */
    public int getNumberOfSectors(int trackNr) throws IllegalArgumentException
    {
        return getTrack(trackNr).getNumberOfSectors();
    }

}
