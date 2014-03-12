package org.cbm.ant.cbm.disk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class CBMDiskSector
{

    private final byte[] data = new byte[256];

    private final int trackNr;
    private final int sectorNr;

    private int mark;

    public CBMDiskSector(int trackNr, int sectorNr)
    {
        super();

        this.trackNr = trackNr;
        this.sectorNr = sectorNr;
    }

    public byte[] getData()
    {
        return data;
    }

    public int getTrackNr()
    {
        return trackNr;
    }

    public int getSectorNr()
    {
        return sectorNr;
    }

    public int getNextTrackNr()
    {
        return getByte(0x00);
    }

    public void setNextTrackNr(int trackNr)
    {
        setByte(0x00, trackNr);
    }

    public int getNextSectorNr()
    {
        return getByte(0x01);
    }

    public void setNextSectorNr(int sectorNr)
    {
        setByte(0x01, sectorNr);
    }

    public void read(InputStream in) throws IOException
    {
        int remaining = 256;

        while (remaining > 0)
        {
            int length = in.read(data, 256 - remaining, remaining);

            if (length < 0)
            {
                return;
            }

            remaining -= length;
        }
    }

    public void write(OutputStream out) throws IOException
    {
        out.write(data);
    }

    public void clear()
    {
        Arrays.fill(data, (byte) 0x00);
    }

    public boolean isBit(int position, int bit)
    {
        int b = 1 << bit;

        return (getByte(position) & b) == b;
    }

    public int getByte(int position)
    {
        return (data[position] & 0xff);
    }

    public byte[] getBytes(int position, int length)
    {
        byte[] result = new byte[length];

        System.arraycopy(data, position, result, 0, length);

        return result;
    }

    public int getWord(int position)
    {
        return getByte(position) + (getByte(position + 1) * 256);
    }

    public String getString(int position, int length)
    {
        return CBMDiskUtil.fromCBMDOSName(getBytes(position, length));
    }

    public void setBit(int position, int bit, boolean value)
    {
        int b = 1 << bit;

        if (value)
        {
            data[position] |= b;
        }
        else
        {
            data[position] &= ~b;
        }
    }

    public void setByte(int position, int value)
    {
        data[position] = (byte) (0xff & value);
    }

    public void setBytes(int position, byte[] values)
    {
        System.arraycopy(values, 0, data, position, values.length);
    }

    public void setWord(int position, int value)
    {
        setByte(position, value % 256);
        setByte(position + 1, value / 256);
    }

    public void setString(int position, int length, String value)
    {
        setBytes(position, CBMDiskUtil.toCBMDOSName(value, length));
    }

    public void fill(int position, int length, int value)
    {
        Arrays.fill(data, position, position + length, (byte) (0xff & value));
    }

    public int getMark()
    {
        return mark;
    }

    public void setMark(int mark)
    {
        this.mark = mark;
    }

}
