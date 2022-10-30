package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

import org.cbm.ant.cbm.disk.CbmIOException.Type;
import org.cbm.ant.util.IOUtils;
import org.cbm.ant.util.WildcardUtils;

public class CbmFile
{
    private static final int FILE_TYPE_POS = 0x02;
    private static final int TRACK_NR_POS = 0x03;
    private static final int SECTOR_NR_POS = 0x04;
    private static final int FILE_NAME_POS = 0x05;
    private static final int FILE_NAME_LENGTH = 16;
    private static final int REL_TRACK_NR_POS = 0x15;
    private static final int REL_SECTOR_NR_POS = 0x16;
    private static final int REL_FILE_RECORD_LENGTH_POS = 0x17;
    private static final int FILE_SIZE_POS = 0x1e;

    private static final int ENTRY_LENGTH = 0x20;

    private final CbmDirectorySector sector;
    private final int index;

    public CbmFile(CbmDirectorySector sector, int index)
    {
        super();
        this.sector = sector;
        this.index = index;
    }

    public CbmDisk getImage()
    {
        return sector.getDisk();
    }

    protected CbmDirectorySector getSector()
    {
        return sector;
    }

    public int getIndex()
    {
        return index;
    }

    protected int offset(int pos)
    {
        return index * ENTRY_LENGTH + pos;
    }

    public CbmFileType getType()
    {
        return CbmFileType.toCbmFileType(getSector().getByte(offset(FILE_TYPE_POS)));
    }

    public void setType(CbmFileType fileType)
    {
        CbmSector sector = getSector();

        sector.setByte(offset(FILE_TYPE_POS), sector.getByte(offset(FILE_TYPE_POS)) & 0xf8 | fileType.getType());
    }

    public boolean isLocked()
    {
        return getSector().isBit(offset(FILE_TYPE_POS), 6);
    }

    public void setLocked(boolean locked)
    {
        getSector().setBit(offset(FILE_TYPE_POS), 6, locked);
    }

    public boolean isClosed()
    {
        return getSector().isBit(offset(FILE_TYPE_POS), 7);
    }

    public void setClosed(boolean closed)
    {
        getSector().setBit(offset(FILE_TYPE_POS), 7, closed);
    }

    public boolean exists()
    {
        return getType() != CbmFileType.DEL;
    }

    public boolean isDeleted()
    {
        return getType() == CbmFileType.DEL;
    }

    private String getTypeDescription()
    {
        String type = getType().getName();

        if (isLocked())
        {
            type = ">" + type;
        }

        if (!isClosed())
        {
            type = "*" + type;
        }

        return type;
    }

    protected int getLocationSectorNr()
    {
        return getSector().getByte(offset(SECTOR_NR_POS));
    }

    protected int getLocationTrackNr()
    {
        return getSector().getByte(offset(TRACK_NR_POS));
    }

    public Optional<CbmSectorLocation> getLocation()
    {
        int trackNr = getLocationTrackNr();

        if (trackNr <= 0)
        {
            return Optional.empty();
        }

        int sectorNr = getLocationSectorNr();

        return Optional.of(CbmSectorLocation.of(trackNr, sectorNr));
    }

    public void setLocation(Optional<CbmSectorLocation> location)
    {
        CbmSector sector = getSector();

        location.ifPresentOrElse($ -> {
            sector.setByte(offset(TRACK_NR_POS), $.getTrackNr());
            sector.setByte(offset(SECTOR_NR_POS), $.getSectorNr());
        }, () -> {
            sector.setByte(offset(TRACK_NR_POS), 0x00);
            sector.setByte(offset(SECTOR_NR_POS), 0x00);
        });
    }

    public String getName()
    {
        return CbmUtils.trimCbmDosName(getSector().getString(offset(FILE_NAME_POS), FILE_NAME_LENGTH));
    }

    public void setName(String fileName)
    {
        getSector().setString(offset(FILE_NAME_POS), FILE_NAME_LENGTH, fileName);
    }

    public boolean nameEquals(String fileName)
    {
        return Objects.equals(getName(), CbmUtils.trimCbmDosName(fileName));
    }

    public boolean nameMatches(String fileNamePattern)
    {
        return WildcardUtils.match(getName(), CbmUtils.trimCbmDosName(fileNamePattern));
    }

    public Optional<CbmSectorLocation> getRelLocation()
    {
        CbmSector sector = getSector();
        int trackNr = sector.getByte(offset(REL_TRACK_NR_POS));
        int sectorNr = sector.getByte(offset(REL_SECTOR_NR_POS));

        if (trackNr == 0x00 || sectorNr == 0xff)
        {
            return Optional.empty();
        }

        return Optional.of(CbmSectorLocation.of(trackNr, sectorNr));
    }

    public void setRelLocation(Optional<CbmSectorLocation> location)
    {
        CbmSector sector = getSector();

        location.ifPresentOrElse($ -> {
            sector.setByte(offset(REL_TRACK_NR_POS), $.getTrackNr());
            sector.setByte(offset(REL_SECTOR_NR_POS), $.getSectorNr());
        }, () -> {
            sector.setByte(offset(REL_TRACK_NR_POS), 0x00);
            sector.setByte(offset(REL_SECTOR_NR_POS), 0x00);
        });
    }

    public int getRelRecordLength()
    {
        return getSector().getByte(offset(REL_FILE_RECORD_LENGTH_POS));
    }

    public void setRelRecordLength(int length)
    {
        getSector().setByte(offset(REL_FILE_RECORD_LENGTH_POS), length);
    }

    public int getSize()
    {
        return getSector().getWord(offset(FILE_SIZE_POS));
    }

    public void setSize(int size)
    {
        if (size < 0 || size >= 65536)
        {
            throw new IllegalArgumentException("Invalid size (0 <= size < 65536): " + size);
        }

        getSector().setWord(offset(FILE_SIZE_POS), size);
    }

    public void format()
    {
        CbmSector sector = getSector();

        if (index > 0)
        {
            sector.fill(offset(0), 0x00, 2);
        }

        sector.fill(offset(2), 0x00, 30);

    }

    public boolean isFormatted()
    {
        return true;
    }

    public void delete(boolean erase)
    {
        setType(CbmFileType.DEL);
        setClosed(false);

        getLocation().ifPresent(location -> getImage().disposeSectorChain(location, erase));

        if (erase)
        {
            setSize(0);
            setLocation(Optional.empty());
        }
    }

    public CbmInputStream read() throws CbmIOException
    {
        if (!exists())
        {
            throw new CbmIOException(Type.FILE_NOT_FOUND, getName());
        }

        return new CbmInputStream(getImage(), getLocation());
    }

    public byte[] readFully() throws IOException
    {
        try (CbmInputStream in = read())
        {
            return in.readAllBytes();
        }
    }

    public void write(File file) throws IOException
    {
        try (FileInputStream in = new FileInputStream(file))
        {
            write(in);
        }
    }

    public void write(InputStream in) throws IOException
    {
        try (CbmFileOutputStream out = write())
        {
            IOUtils.copy(in, out);
        }
    }

    public void write(byte[] bytes) throws IOException
    {
        try (CbmFileOutputStream out = write())
        {
            out.write(bytes);
        }
    }

    public CbmFileOutputStream write() throws CbmIOException
    {
        return new CbmFileOutputStream(this, getLocation());
    }

    public String describe()
    {
        return String
            .format("%-3d  %-18s %s [%s]", getSize(), CbmUtils.apostrophes(getName()), getTypeDescription(),
                CbmSectorLocation.of(getLocationTrackNr(), getLocationSectorNr()));
    }

    public void printSectorChain(StringBuilder bob)
    {
        Optional<CbmSectorLocation> location = getLocation();

        bob.append("------------------------------------------------------------------------\n");
        bob.append(describe());
        bob.append("\n");

        if (location.isPresent())
        {
            sector.getDisk().printSectorChain(bob, location.get());
        }
        else
        {
            bob.append("------------------------------------------------------------------------\n");
            bob.append("File is empty.\n");
            bob.append("------------------------------------------------------------------------");
        }
    }
}
