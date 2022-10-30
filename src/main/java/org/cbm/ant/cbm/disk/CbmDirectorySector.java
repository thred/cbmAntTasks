package org.cbm.ant.cbm.disk;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CbmDirectorySector implements CbmSector, Iterable<CbmFile>
{
    private static final int MAX_NUMBER_OF_ENTRIES = 8;

    private final CbmDisk disk;
    private final CbmSector sector;

    CbmDirectorySector(CbmDisk disk, CbmSector sector)
    {
        super();

        this.disk = disk;
        this.sector = sector;
    }

    public CbmDisk getDisk()
    {
        return disk;
    }

    @Override
    public CbmSectorLocation getLocation()
    {
        return sector.getLocation();
    }

    @Override
    public byte[] getBytes(int pos, int length)
    {
        return sector.getBytes(pos, length);
    }

    @Override
    public void setBytes(int pos, byte[] bytes, int offset, int length)
    {
        sector.setBytes(pos, bytes, offset, length);
    }

    @Override
    public int getByte(int pos)
    {
        return sector.getByte(pos);
    }

    @Override
    public void setByte(int pos, int value)
    {
        sector.setByte(pos, value);
    }

    @Override
    public boolean isDamaged()
    {
        return sector.isDamaged();
    }

    @Override
    public Iterator<CbmFile> iterator()
    {
        return new CbmFileIterator(this, entry -> true);
    }

    public Stream<CbmFile> stream()
    {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }

    public CbmFile getFile(int index)
    {
        if (index < 0 || index >= MAX_NUMBER_OF_ENTRIES)
        {
            throw new IllegalArgumentException("Invalid index (0 <= index < " + MAX_NUMBER_OF_ENTRIES + "): " + index);
        }

        return new CbmFile(this, index);
    }

    public Optional<CbmFile> getFile(String fileName)
    {
        return stream().filter(CbmFile::exists).filter(entry -> entry.nameEquals(fileName)).findFirst();
    }

    public Stream<CbmFile> findFiles(String fileNamePattern)
    {
        return stream().filter(CbmFile::exists).filter(entry -> entry.nameMatches(fileNamePattern));
    }

    public Optional<CbmFile> findDeletedFile()
    {
        return stream().filter(CbmFile::isDeleted).findFirst();
    }

    /**
     * Formats this block and terminates the directory.
     */
    public void format()
    {
        erase();

        stream().forEach(CbmFile::format);
        setNextLocation(Optional.empty());
    }

    public boolean isFormatted()
    {
        return stream().allMatch(CbmFile::isFormatted);
    }

    public void printDirectory(StringBuilder bob, boolean includeDeleted)
    {
        bob
            .append(stream()
                .filter(entry -> includeDeleted || entry.exists())
                .map(CbmFile::describe)
                .collect(Collectors.joining("\n")));
    }
}
