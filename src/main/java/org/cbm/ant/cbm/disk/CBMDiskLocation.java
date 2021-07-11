package org.cbm.ant.cbm.disk;

import java.util.Objects;

public class CBMDiskLocation
{

    private final int trackNr;
    private final int sectorNr;

    public CBMDiskLocation(int trackNr, int sectorNr)
    {
        super();
        this.trackNr = trackNr;
        this.sectorNr = sectorNr;
    }

    public int getTrackNr()
    {
        return trackNr;
    }

    public int getSectorNr()
    {
        return sectorNr;
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

        CBMDiskLocation other = (CBMDiskLocation) obj;

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
