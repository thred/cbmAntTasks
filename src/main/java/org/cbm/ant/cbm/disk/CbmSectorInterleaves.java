package org.cbm.ant.cbm.disk;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines, how files get written on a disk. Files, on a standard 1541, are stored using an interleave of 10. Assuming a
 * starting track/sector of 17/0, the chain would run 17/0, 17/10, 17/20, 17/8, 17/18, etc. The interleave depends on
 * the track. <br>
 * <br>
 * The parser uses the followin format:
 *
 * <pre>
 * config = definition { "," definition }.
 * definition = default | overrde.
 * default = INTERLEAVE.
 * override = TRACK [ "-" TRACK ] ":" INTERLEAVE.
 * </pre>
 *
 * @author Manfred Hantschel
 */
public class CbmSectorInterleaves
{
    public static CbmSectorInterleaves parse(String s)
    {
        return parse(null, s);
    }

    public static CbmSectorInterleaves parse(CbmSectorInterleaves base, String s)
    {
        Integer defaultInterleave = null;
        Map<Integer, Integer> interleaveByTrack = new HashMap<>();

        if (s != null)
        {
            String[] values = s.split(",");

            try
            {
                for (String value : values)
                {
                    if (value.contains(":"))
                    {
                        String trackNr = value.substring(0, value.indexOf(":")).trim();
                        String endTrackNr = trackNr;
                        String interleave = value.substring(value.indexOf(":") + 1).trim();

                        if (trackNr.contains("-"))
                        {
                            endTrackNr = trackNr.substring(trackNr.indexOf("-") + 1).trim();
                            trackNr = trackNr.substring(0, trackNr.indexOf("-")).trim();
                        }

                        int fromTrackNr = Integer.decode(trackNr);
                        int toTrackNr = Integer.decode(endTrackNr);
                        Integer intl = Integer.decode(interleave);

                        for (int i = fromTrackNr; i <= toTrackNr; ++i)
                        {
                            interleaveByTrack.put(i, intl);
                        }
                    }
                    else
                    {
                        if (defaultInterleave != null)
                        {
                            throw new IllegalArgumentException("Multiple default values");
                        }

                        defaultInterleave = Integer.decode(value.trim());
                    }
                }
            }
            catch (IllegalArgumentException e)
            {
                throw new IllegalArgumentException(String
                    .format(
                        "Failed to parse sector interleaves \"%s\". Expected a comma-separated list of: [ <TRACK> [ \"-\" <TRACK> ] \":\" ] <INTERLEAVE>",
                        s),
                    e);
            }
        }

        return base != null ? base.with(defaultInterleave, interleaveByTrack)
            : new CbmSectorInterleaves(defaultInterleave, interleaveByTrack);
    }

    private final Integer defaultInterleave;
    private final Map<Integer, Integer> interleaveByTrack;

    private CbmSectorInterleaves(Integer defaultInterleave, Map<Integer, Integer> interleaveByTrack)
    {
        super();

        this.defaultInterleave = defaultInterleave;
        this.interleaveByTrack = Collections.unmodifiableMap(interleaveByTrack);
    }

    public CbmSectorInterleaves withDefaultInterleave(Integer defaultInterleave)
    {
        return new CbmSectorInterleaves(defaultInterleave, interleaveByTrack);
    }

    public CbmSectorInterleaves withInterleave(int trackNr, int interleave)
    {
        Map<Integer, Integer> interleaveByTrack = new HashMap<>(this.interleaveByTrack);

        interleaveByTrack.put(trackNr, interleave);

        return new CbmSectorInterleaves(defaultInterleave, interleaveByTrack);
    }

    public CbmSectorInterleaves withInterleaves(int fromTrackNr, int toTrackNr, int interleave)
    {
        Map<Integer, Integer> interleaveByTrack = new HashMap<>(this.interleaveByTrack);

        for (int trackNr = fromTrackNr; trackNr <= toTrackNr; ++trackNr)
        {
            interleaveByTrack.put(trackNr, interleave);
        }

        return new CbmSectorInterleaves(defaultInterleave, interleaveByTrack);
    }

    public CbmSectorInterleaves withInterleaves(Map<Integer, Integer> interleaveByTrack)
    {
        Map<Integer, Integer> combinedInterleaveByTrack = new HashMap<>(this.interleaveByTrack);

        combinedInterleaveByTrack.putAll(interleaveByTrack);

        return new CbmSectorInterleaves(defaultInterleave, combinedInterleaveByTrack);
    }

    public CbmSectorInterleaves with(Integer defaultInterleave, Map<Integer, Integer> interleaveByTrack)
    {
        Map<Integer, Integer> combinedInterleaveByTrack = new HashMap<>(this.interleaveByTrack);

        combinedInterleaveByTrack.putAll(interleaveByTrack);

        return new CbmSectorInterleaves(defaultInterleave != null ? defaultInterleave : this.defaultInterleave,
            combinedInterleaveByTrack);
    }

    public CbmSectorInterleaves with(CbmSectorInterleaves sectorInterleaves)
    {
        return with(sectorInterleaves.defaultInterleave, sectorInterleaves.interleaveByTrack);
    }

    public int getDefaultInterleave()
    {
        return defaultInterleave != null ? defaultInterleave : 1;
    }

    public int getInterleave(int trackNr)
    {
        Integer interleave = interleaveByTrack.get(trackNr);

        return interleave != null ? interleave : defaultInterleave;
    }
}
