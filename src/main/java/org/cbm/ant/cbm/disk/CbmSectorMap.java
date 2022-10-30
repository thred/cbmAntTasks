package org.cbm.ant.cbm.disk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CbmSectorMap implements Iterable<CbmSectorLocation>
{
    public static CbmSectorMap empty()
    {
        return new CbmSectorMap(Collections.emptyList());
    }

    public static CbmSectorMap of(int trackCount, int sectorCount)
    {
        return empty().and(trackCount, sectorCount);
    }

    private final List<Integer> sectorCounts;
    private final Map<CbmSectorLocation, Integer> sectorIndices;

    private CbmSectorMap(List<Integer> sectorCounts)
    {
        super();

        this.sectorCounts = Collections.unmodifiableList(sectorCounts);

        Map<CbmSectorLocation, Integer> sectorIndices = new HashMap<>();

        int index = 0;

        for (int trackIndex = 0; trackIndex < sectorCounts.size(); trackIndex++)
        {
            int trackNr = trackIndex + 1;
            Integer sectorCount = sectorCounts.get(trackIndex);

            for (int sectorNr = 0; sectorNr < sectorCount; ++sectorNr)
            {
                sectorIndices.put(CbmSectorLocation.of(trackNr, sectorNr), index++);
            }
        }

        this.sectorIndices = Collections.unmodifiableMap(sectorIndices);
    }

    public CbmSectorMap and(int trackCount, int sectorCount)
    {
        List<Integer> sectorCounts = new ArrayList<>(this.sectorCounts);

        for (int i = 0; i < trackCount; ++i)
        {
            sectorCounts.add(sectorCount);
        }

        return new CbmSectorMap(sectorCounts);
    }

    public int size()
    {
        return sectorCounts.stream().reduce(0, Integer::sum);
    }

    public int getMaxTrackNr()
    {
        return sectorCounts.size();
    }

    public int getMaxSectorNrByTrackNr(int trackNr)
    {
        if (trackNr < 1 || trackNr > sectorCounts.size())
        {
            throw new IllegalArgumentException("Invalid trackNr (1 <= trackNr <= " + getMaxTrackNr() + "): " + trackNr);
        }

        return sectorCounts.get(trackNr - 1) - 1;

    }

    public int indexOf(CbmSectorLocation location)
    {
        return sectorIndices.getOrDefault(location, -1);
    }

    public boolean contains(CbmSectorLocation location)
    {
        return sectorIndices.containsKey(location);
    }

    @Override
    public Iterator<CbmSectorLocation> iterator()
    {
        return new Iterator<CbmSectorLocation>()
        {
            private final Iterator<Integer> trackIterator = sectorCounts.iterator();

            private Integer sectorCount = null;
            private CbmSectorLocation location = null;
            private int trackNr = 0;
            private int sectorNr = 0;

            @Override
            public boolean hasNext()
            {
                if (location != null)
                {
                    return true;
                }

                while (true)
                {
                    if (sectorCount != null && sectorNr >= sectorCount)
                    {
                        sectorCount = null;
                    }

                    if (sectorCount == null)
                    {
                        if (!trackIterator.hasNext())
                        {
                            return false;
                        }

                        sectorCount = trackIterator.next();
                        ++trackNr;
                        sectorNr = 0;

                        continue;
                    }

                    location = CbmSectorLocation.of(trackNr, sectorNr++);

                    return true;
                }
            }

            @Override
            public CbmSectorLocation next()
            {
                if (!hasNext())
                {
                    throw new NoSuchElementException();
                }

                try
                {
                    return location;
                }
                finally
                {
                    location = null;
                }
            }
        };
    }

    public Stream<CbmSectorLocation> stream()
    {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }
}
