package org.cbm.ant.cbm.disk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.cbm.ant.util.Util;

public class CbmSectorRange implements Iterable<CbmSectorLocation>
{
    public static CbmSectorRange of(CbmSectorLocation fromLocation, CbmSectorLocation toLocation)
    {
        return new CbmSectorRange(fromLocation, toLocation);
    }

    public static CbmSectorRange parse(String parsableRange)
    {
        try
        {
            String[] parts = parsableRange.split("[\\s]*/[\\s]*");

            if (parts.length != 2)
            {
                throw new IllegalArgumentException("Invalid definition: " + parsableRange);
            }

            int fromTrackNr;
            int toTrackNr;

            if (parts[0].contains("-"))
            {
                String[] span = parts[0].split("[\\s]*-[\\s]*");

                if (span.length != 2)
                {
                    throw new IllegalArgumentException("Invalid range: " + parsableRange);
                }

                fromTrackNr = Util.parseHex(span[0]);
                toTrackNr = Util.parseHex(span[1]);

                if (toTrackNr < fromTrackNr)
                {
                    throw new IllegalArgumentException("Invalid range: " + parsableRange);
                }
            }
            else
            {
                fromTrackNr = toTrackNr = Util.parseHex(parts[0]);
            }

            int fromSectorNr;
            int toSectorNr;

            if (parts[1].contains("-"))
            {
                String[] span = parts[1].split("[\\s]*-[\\s]*");

                if (span.length != 2)
                {
                    throw new IllegalArgumentException("Invalid range: " + parsableRange);
                }

                fromSectorNr = Util.parseHex(span[0]);
                toSectorNr = Util.parseHex(span[1]);

                if (toSectorNr < fromSectorNr)
                {
                    throw new IllegalArgumentException("Invalid range: " + parsableRange);
                }
            }
            else
            {
                fromSectorNr = toSectorNr = Util.parseHex(parts[1]);
            }

            return new CbmSectorRange(CbmSectorLocation.of(fromTrackNr, fromSectorNr),
                CbmSectorLocation.of(toTrackNr, toSectorNr));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid definition: " + parsableRange, e);
        }
    }

    private final CbmSectorLocation fromLocation;
    private final CbmSectorLocation toLocation;

    private CbmSectorRange(CbmSectorLocation fromLocation, CbmSectorLocation toLocation)
    {
        super();

        this.fromLocation = fromLocation;
        this.toLocation = toLocation;

        if (toLocation.getTrackNr() < fromLocation.getTrackNr())
        {
            throw new IllegalArgumentException("Invalid range (from trackNr <= to trackNr): "
                + fromLocation.getTrackNr()
                + "-"
                + toLocation.getTrackNr());
        }

        if (toLocation.getSectorNr() < fromLocation.getSectorNr())
        {
            throw new IllegalArgumentException("Invalid range (from sectorNr <= to sectorNr): "
                + fromLocation.getSectorNr()
                + "-"
                + toLocation.getSectorNr());
        }
    }

    public CbmSectorLocation getFromLocation()
    {
        return fromLocation;
    }

    public CbmSectorLocation getToLocation()
    {
        return toLocation;
    }

    @Override
    public Iterator<CbmSectorLocation> iterator()
    {
        // TODO optimize by creating a range iterator

        List<CbmSectorLocation> locations = new ArrayList<>();

        for (int trackNr = fromLocation.getTrackNr(); trackNr <= toLocation.getTrackNr(); ++trackNr)
        {
            for (int sectorNr = fromLocation.getSectorNr(); sectorNr <= toLocation.getSectorNr(); ++sectorNr)
            {
                locations.add(CbmSectorLocation.of(trackNr, sectorNr));
            }
        }

        return Collections.unmodifiableCollection(locations).iterator();
    }

    public Stream<CbmSectorLocation> stream()
    {
        // TODO optimize by creating a spliterator

        List<CbmSectorLocation> locations = new ArrayList<>();

        for (int trackNr = fromLocation.getTrackNr(); trackNr <= toLocation.getTrackNr(); ++trackNr)
        {
            for (int sectorNr = fromLocation.getSectorNr(); sectorNr <= toLocation.getSectorNr(); ++sectorNr)
            {
                locations.add(CbmSectorLocation.of(trackNr, sectorNr));
            }
        }

        return locations.stream();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(fromLocation, toLocation);
    }

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
        CbmSectorRange other = (CbmSectorRange) obj;
        return Objects.equals(fromLocation, other.fromLocation) && Objects.equals(toLocation, other.toLocation);
    }

    @Override
    public String toString()
    {
        StringBuilder trackNr = new StringBuilder().append(fromLocation.getTrackNr());

        if (toLocation.getTrackNr() != fromLocation.getTrackNr())
        {
            trackNr.append("-").append(toLocation.getTrackNr());
        }

        StringBuilder sectorNr = new StringBuilder().append(fromLocation.getSectorNr());

        if (toLocation.getSectorNr() != fromLocation.getSectorNr())
        {
            sectorNr.append("-").append(toLocation.getSectorNr());
        }

        return String.format("%s/%s", trackNr.toString(), sectorNr.toString());
    }
}
