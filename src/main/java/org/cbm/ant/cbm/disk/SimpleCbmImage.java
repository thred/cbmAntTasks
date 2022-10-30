package org.cbm.ant.cbm.disk;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.cbm.ant.cbm.disk.CbmIOException.Type;

public class SimpleCbmImage implements CbmImage
{
    public static SimpleCbmImage of(CbmSectorMap sectorMap, CbmSectorMap extendedSectorMap, boolean extended,
        boolean errorInformationAvailable)
    {
        return new SimpleCbmImage(sectorMap, extendedSectorMap, extended, errorInformationAvailable);
    }

    private final ByteBuffer data;
    private final byte[] errors;

    private final CbmSectorMap sectorMap;
    private final CbmSectorMap extendedSectorMap;

    private boolean extended;
    private boolean errorInformationAvailable;

    protected SimpleCbmImage(CbmSectorMap sectorMap, CbmSectorMap extendedSectorMap, boolean extended,
        boolean errorInformationAvailable)
    {
        super();

        int sectorCount = sectorMap.size();

        if (extendedSectorMap != null)
        {
            sectorCount = Math.max(sectorCount, extendedSectorMap.size());
        }

        data = ByteBuffer.allocate(sectorCount * 256);
        errors = new byte[sectorCount];

        this.sectorMap = sectorMap;
        this.extendedSectorMap = extendedSectorMap;
        this.extended = extended;
        this.errorInformationAvailable = errorInformationAvailable;
    }

    @Override
    public CbmSectorMap getSectorMap()
    {
        return extended ? extendedSectorMap : sectorMap;
    }

    public boolean isExtended()
    {
        return extended;
    }

    public void setExtended(boolean extended)
    {
        if (extended && extendedSectorMap == null)
        {
            throw new IllegalArgumentException("Extended mode not supported");
        }

        this.extended = extended;
    }

    @Override
    public boolean isErrorInformationAvailable()
    {
        return errorInformationAvailable;
    }

    @Override
    public void setErrorInformationAvailable(boolean errorInformationAvailable)
    {
        this.errorInformationAvailable = errorInformationAvailable;
    }

    @Override
    public int getErrorInformation(CbmSectorLocation location)
    {
        return errorInformationAvailable ? errors[getSectorMap().indexOf(location)] & 0xff : 0;
    }

    @Override
    public void setErrorInformation(CbmSectorLocation location, int error)
    {
        if (!errorInformationAvailable)
        {
            throw new IllegalArgumentException("Error information not available");
        }

        errors[getSectorMap().indexOf(location)] = (byte) error;
    }

    @Override
    public void load(byte[] bytes) throws CbmIOException
    {
        int length = bytes.length;
        int sectorCount = sectorMap.size();
        int dataSize = sectorCount * 256;

        if (length == dataSize)
        {
            setExtended(false);
            setErrorInformationAvailable(false);

            data.clear();
            data.put(bytes);

            Arrays.fill(errors, (byte) 0);
        }
        else if (length == dataSize + sectorCount)
        {
            setExtended(false);
            setErrorInformationAvailable(true);

            data.clear();
            data.put(bytes, 0, dataSize);

            Arrays.fill(errors, (byte) 0);
            System.arraycopy(bytes, dataSize, errors, 0, sectorCount);
        }
        else
        {
            int extendedSectorCount = extendedSectorMap.size();
            int extendedDataSize = extendedSectorCount * 256;

            if (extendedSectorMap != null && length == extendedDataSize)
            {
                setExtended(true);
                setErrorInformationAvailable(false);

                data.clear();
                data.put(bytes);

                Arrays.fill(errors, (byte) 0);
            }
            else if (extendedSectorMap != null && length == extendedDataSize + extendedSectorCount)
            {
                setExtended(true);
                setErrorInformationAvailable(true);

                data.clear();
                data.put(bytes, 0, extendedDataSize);

                System.arraycopy(bytes, extendedDataSize, errors, 0, extendedSectorCount);
            }
            else
            {
                throw new CbmIOException(Type.INVALID_DATA_SIZE, bytes.length);
            }
        }
    }

    @Override
    public void save(OutputStream out) throws IOException
    {
        if (extended)
        {
            int sectorCount = extendedSectorMap.size();
            int dataSize = sectorCount * 256;

            out.write(data.array(), 0, dataSize);

            if (errorInformationAvailable)
            {
                out.write(errors, 0, sectorCount);
            }
        }
        else
        {
            int sectorCount = sectorMap.size();
            int dataSize = sectorCount * 256;

            out.write(data.array(), 0, dataSize);

            if (errorInformationAvailable)
            {
                out.write(errors, 0, sectorCount);
            }
        }
    }

    @Override
    public CbmSector sectorAt(CbmSectorLocation location)
    {
        int sectorOffset = computeOffset(location);

        return new CbmSector()
        {
            @Override
            public CbmSectorLocation getLocation()
            {
                return location;
            }

            @Override
            public byte[] getBytes(int pos, int length)
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
                    throw new IllegalArgumentException("Invalid range (pos + length > 256): " + (pos + length));
                }

                byte[] bytes = new byte[length];

                data.get(sectorOffset + pos, bytes);

                return bytes;
            }

            @Override
            public void setBytes(int pos, byte[] bytes, int offset, int length)
            {
                if (pos < 0)
                {
                    throw new IllegalArgumentException("Invalid pos (pos < 0): " + pos);
                }

                if (pos + bytes.length > 256)
                {
                    throw new IllegalArgumentException("Invalid range (pos + length > 256): " + (pos + length));
                }

                if (offset < 0)
                {
                    throw new IllegalArgumentException("Invalid offset (offset < 0): " + offset);
                }

                if (length < 0)
                {
                    throw new IllegalArgumentException("Invalid length (length < 0): " + length);
                }

                if (offset + length > bytes.length)
                {
                    throw new IllegalArgumentException(
                        "Invalid range (offset + length > " + bytes.length + "): " + (offset + length));
                }

                data.put(sectorOffset + pos, bytes, offset, length);
            }

            @Override
            public int getByte(int pos)
            {
                if (pos < 0)
                {
                    throw new IllegalArgumentException("Invalid pos (pos < 0): " + pos);
                }

                if (pos >= 256)
                {
                    throw new IllegalArgumentException("Invalid range (pos >= 256): " + pos);
                }

                return data.get(sectorOffset + pos) & 0xff;
            }

            @Override
            public void setByte(int pos, int value)
            {
                if (pos < 0)
                {
                    throw new IllegalArgumentException("Invalid pos (pos < 0): " + pos);
                }

                if (pos >= 256)
                {
                    throw new IllegalArgumentException("Invalid range (pos >= 256): " + pos);
                }

                data.put(sectorOffset + pos, (byte) value);
            }

            @Override
            public boolean isDamaged()
            {
                return false;
            }

            @Override
            public String toString()
            {
                StringBuilder bob = new StringBuilder();

                print(bob);

                return bob.toString();
            }
        };
    }

    protected int computeOffset(CbmSectorLocation location)
    {
        return getSectorMap().indexOf(location) * 256;
    }

    @Override
    public boolean isSectorSupported(CbmSectorLocation location)
    {
        return getSectorMap().contains(location);
    }
}
