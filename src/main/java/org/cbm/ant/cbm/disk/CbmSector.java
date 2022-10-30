package org.cbm.ant.cbm.disk;

import java.util.Arrays;
import java.util.Optional;

import org.cbm.ant.util.Util;

public interface CbmSector
{
    int NEXT_TRACK_NR_POS = 0x00;
    int NEXT_SECTOR_NR_POS = 0x01;

    default int getTrackNr()
    {
        return getLocation().getTrackNr();
    }

    default int getSectorNr()
    {
        return getLocation().getSectorNr();
    }

    CbmSectorLocation getLocation();

    default boolean isBit(int pos, int bit)
    {
        int b = 1 << bit;

        return (getByte(pos) & b) == b;
    }

    default void setBit(int pos, int bit, boolean value)
    {
        int b = 1 << bit;

        if (value)
        {
            setByte(pos, getByte(pos) | b);
        }
        else
        {
            setByte(pos, getByte(pos) & ~b);
        }
    }

    byte[] getBytes(int pos, int length);

    default void setBytes(int pos, byte[] bytes)
    {
        setBytes(pos, bytes, 0, bytes.length);
    }

    void setBytes(int pos, byte[] bytes, int offset, int length);

    default void fill(int pos, int value, int length)
    {
        if (pos < 0)
        {
            throw new IllegalArgumentException("Invalid pos (pos < 0): " + pos);
        }

        if (length < 0)
        {
            throw new IllegalArgumentException("Invalid length (length < 0): " + length);
        }

        if (pos + length > 256)
        {
            throw new IllegalArgumentException("Invalid range (pos + length > " + 256 + "): " + (pos + length));
        }

        byte[] bytes = new byte[length];

        Arrays.fill(bytes, (byte) value);

        setBytes(pos, bytes);
    }

    int getByte(int pos);

    void setByte(int pos, int value);

    default int getWord(int position)
    {
        return getByte(position) + getByte(position + 1) * 256;
    }

    default void setWord(int position, int value)
    {
        setByte(position, value % 256);
        setByte(position + 1, value / 256);
    }

    default String getString(int position, int length)
    {
        return CbmUtils.fromCbmDosName(getBytes(position, length));
    }

    default void setString(int position, int length, String value)
    {
        setBytes(position, CbmUtils.toCbmDosName(value, length));
    }

    boolean isDamaged();

    default int getNextTrackNr()
    {
        return getByte(NEXT_TRACK_NR_POS);
    }

    default void setNextTrackNr(int trackNr)
    {
        setByte(NEXT_TRACK_NR_POS, trackNr);
    }

    default int getNextSectorNr()
    {
        return getByte(NEXT_SECTOR_NR_POS);
    }

    default void setNextSectorNr(int sectorNr)
    {
        setByte(NEXT_SECTOR_NR_POS, sectorNr);
    }

    default Optional<CbmSectorLocation> getNextLocation()
    {
        int nextTrackNr = getNextTrackNr();
        int nextSectorNr = getNextSectorNr();

        if (nextTrackNr == 0 || nextSectorNr == 0xff)
        {
            return Optional.empty();
        }

        return Optional.of(new CbmSectorLocation(nextTrackNr, nextSectorNr));
    }

    default void setNextLocation(Optional<CbmSectorLocation> location)
    {
        location.ifPresentOrElse($ -> {
            setNextTrackNr($.getTrackNr());
            setNextSectorNr($.getSectorNr());
        }, () -> {
            setNextTrackNr(0x00);
            setNextSectorNr(0xff);
        });
    }

    default void erase()
    {
        setBytes(0, Util.byteArrayOf((byte) 0, 256));
    }

    default void print(StringBuilder bob)
    {
        bob.append("------------------------------------------------------------------------\n");
        bob.append(String.format("Track/Sector: %s\n", getLocation()));
        bob.append("------------------------------------------------------------------------");

        for (int j = 0; j < 256; j += 16)
        {
            bob.append("\n").append(CbmUtils.toHex("$", j, 2)).append(" | ");

            for (int i = 0; i < 16; ++i)
            {
                bob.append(CbmUtils.toHex("", getByte(j + i), 2));
                bob.append(" ");
            }

            bob.append("| ");

            for (int i = 0; i < 16; ++i)
            {
                bob.append(CbmUtils.petscii2ascii((byte) getByte(j + i)));
            }
        }
    }
}
