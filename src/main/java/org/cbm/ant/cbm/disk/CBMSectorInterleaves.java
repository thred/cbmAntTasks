package org.cbm.ant.cbm.disk;

import java.util.HashMap;
import java.util.Map;

public class CBMSectorInterleaves
{

    public static CBMSectorInterleaves parse(String s)
    {
        return parse(null, s);
    }

    public static CBMSectorInterleaves parse(CBMSectorInterleaves base, String s)
    {
        CBMSectorInterleaves interleaves = new CBMSectorInterleaves(base);

        if (s != null)
        {
            String[] values = s.split(",");
            boolean defaultSet = false;

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

                        interleaves
                            .setInterleave(Integer.decode(trackNr), Integer.decode(endTrackNr),
                                Integer.decode(interleave));
                    }
                    else
                    {
                        if (defaultSet)
                        {
                            throw new IllegalArgumentException("Multiple default values");
                        }

                        interleaves.setDefaultInterleave(Integer.decode(value.trim()));
                        defaultSet = true;
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

        return interleaves;
    }

    private final Map<Integer, Integer> interleaveByTrack;

    private int defaultInterleave;

    public CBMSectorInterleaves(int defaultInterleave)
    {
        this(defaultInterleave, null);
    }

    public CBMSectorInterleaves(CBMSectorInterleaves base)
    {
        this(base != null ? base.defaultInterleave : 10, base != null ? base.interleaveByTrack : null);
    }

    public CBMSectorInterleaves(int defaultInterleave, Map<Integer, Integer> interleaveByTrackNr)
    {
        super();

        this.defaultInterleave = defaultInterleave;
        interleaveByTrack = interleaveByTrackNr != null ? new HashMap<>(interleaveByTrackNr) : new HashMap<>();
    }

    public int getDefaultInterleave()
    {
        return defaultInterleave;
    }

    public void setDefaultInterleave(int defaultInterleave)
    {
        this.defaultInterleave = defaultInterleave;
    }

    public void setInterleave(int trackNr, int interleave)
    {
        interleaveByTrack.put(trackNr, interleave);
    }

    public void setInterleave(int fromTrackNr, int toTrackNr, int interleave)
    {
        for (int trackNr = fromTrackNr; trackNr <= toTrackNr; ++trackNr)
        {
            setInterleave(trackNr, interleave);
        }
    }

    public int getInterleave(int trackNr)
    {
        Integer interleave = interleaveByTrack.get(trackNr);

        return interleave != null ? interleave : defaultInterleave;
    }
}
