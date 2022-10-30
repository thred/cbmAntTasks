package org.cbm.ant.cbm.disk;

import java.util.Comparator;
import java.util.Objects;

import org.cbm.ant.util.Util;

/**
 * A custom location on a disk defined by track number and sector number. Watch out: the first track has number 1, the
 * first sector has number 0.
 *
 * @author Manfred Hantschel
 */
public class CbmSectorLocation implements Comparable<CbmSectorLocation>
{
    private static final Comparator<CbmSectorLocation> COMPARATOR = Comparator
        .comparingInt(CbmSectorLocation::getTrackNr)
        .thenComparing(Comparator.comparingInt(CbmSectorLocation::getSectorNr));

    public static CbmSectorLocation of(int trackNr, int sectorNr)
    {
        return new CbmSectorLocation(trackNr, sectorNr);
    }

    public static CbmSectorLocation parse(String parsableLocation)
    {
        int index = parsableLocation.indexOf("/");

        if (index < 0)
        {
            throw new IllegalArgumentException("Invalid format (expected \"track/sector\"): " + parsableLocation);
        }

        try
        {
            return CbmSectorLocation
                .of(Util.parseHex(parsableLocation.substring(0, index)),
                    Util.parseHex(parsableLocation.substring(index + 1)));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid format (expected \"track/sector\"): " + parsableLocation);
        }
    }

    private final int trackNr;
    private final int sectorNr;

    public CbmSectorLocation(int trackNr, int sectorNr)
    {
        super();

        if (trackNr < 0)
        {
            throw new IllegalArgumentException("Invalid trackNr: " + trackNr);
        }

        if (sectorNr < 0)
        {
            throw new IllegalArgumentException("Invalid sectorNr: " + trackNr);
        }

        this.trackNr = trackNr;
        this.sectorNr = sectorNr;
    }

    public CbmSectorLocation withTrackNr(int trackNr)
    {
        return new CbmSectorLocation(trackNr, sectorNr);
    }

    public int getTrackNr()
    {
        return trackNr;
    }

    public CbmSectorLocation withSectorNr(int sectorNr)
    {
        return new CbmSectorLocation(trackNr, sectorNr);
    }

    public int getSectorNr()
    {
        return sectorNr;
    }

    @Override
    public int compareTo(CbmSectorLocation other)
    {
        return COMPARATOR.compare(this, other);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(sectorNr, trackNr);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        CbmSectorLocation other = (CbmSectorLocation) obj;

        if (sectorNr != other.sectorNr)
        {
            return false;
        }

        if (trackNr != other.trackNr)
        {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%02d/%02d", trackNr, sectorNr);
    }
}
