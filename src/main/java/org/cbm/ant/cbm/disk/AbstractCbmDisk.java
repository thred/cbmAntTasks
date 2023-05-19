package org.cbm.ant.cbm.disk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.cbm.ant.cbm.disk.CbmIOException.Type;

public abstract class AbstractCbmDisk implements CbmDisk
{
    protected final CbmImage image;

    protected final CbmSectorLocation headerLocation;
    protected final CbmSectorRange directoryRange;
    protected final CbmAllocateSectorStrategy allocateSectorStrategy;
    protected final CbmSectorInterleaves sectorInterleaves;

    protected AbstractCbmDisk(CbmImage image, CbmSectorLocation headerLocation, CbmSectorRange directoryRange,
        CbmAllocateSectorStrategy allocateSectorStrategy, CbmSectorInterleaves sectorInterleaves)
    {
        super();

        this.image = Objects.requireNonNull(image, "Image is null");

        this.headerLocation = Objects.requireNonNull(headerLocation, "HeaderLocation is null");
        this.directoryRange = Objects.requireNonNull(directoryRange, "DirectoryRange is null");
        this.allocateSectorStrategy =
            Objects.requireNonNull(allocateSectorStrategy, "AllocateFileSectorStrategy is null");
        this.sectorInterleaves = sectorInterleaves;

        CbmSectorMap sectorMap = image.getSectorMap();

        for (CbmSectorLocation location : sectorMap)
        {
            if (!image.isSectorSupported(location))
            {
                throw new IllegalArgumentException("Image does not support sector: " + location);
            }
        }

        if (!sectorMap.contains(headerLocation))
        {
            throw new IllegalArgumentException("Sector of header is not available: " + headerLocation);
        }

        if (!sectorMap.contains(directoryRange.getFromLocation()))
        {
            throw new IllegalArgumentException(
                "First sector of directory is not available: " + directoryRange.getFromLocation());
        }
    }

    @Override
    public void load(byte[] bytes) throws CbmIOException
    {
        image.load(bytes);
    }

    @Override
    public void save(OutputStream out) throws IOException
    {
        image.save(out);
    }

    protected String checkDirectoryFormatted()
    {
        Set<CbmSectorLocation> visitedLocations = new HashSet<>();
        CbmDirectorySector directorySector = firstDirectorySector();

        while (directorySector != null)
        {
            if (!visitedLocations.add(directorySector.getLocation()))
            {
                return "The directory chain is looping";
            }

            if (!directorySector.isFormatted())
            {
                return "The directory sector " + directorySector.getLocation() + " is not formatted";
            }

            if (!isSectorUsed(directorySector.getLocation()))
            {
                return "The directory sector " + directorySector.getLocation() + " is not marked as in-use";
            }

            directorySector = directorySector.getNextLocation().map(this::directorySectorAt).orElse(null);
        }

        return null;
    }

    @Override
    public boolean existsFile(String fileName)
    {
        return streamFiles(CbmFile::exists).anyMatch(file -> file.nameEquals(fileName));
    }

    @Override
    public CbmFile getFile(String fileName) throws CbmIOException
    {
        return streamFiles(file -> file.exists() && file.nameEquals(fileName))
            .findFirst()
            .orElseThrow(() -> new CbmIOException(Type.FILE_NOT_FOUND, fileName));
    }

    @Override
    public Stream<CbmFile> findFiles(String fileNamePattern)
    {
        return streamFiles(file -> file.exists() && file.nameMatches(fileNamePattern));
    }

    @Override
    public CbmFile allocateFile(String fileName) throws CbmIOException
    {
        if (existsFile(fileName))
        {
            throw new CbmIOException(Type.FILE_EXISTS, fileName);
        }

        CbmDirectorySector sector = firstDirectorySector();
        CbmFile file = sector.findDeletedFile().orElse(null);

        while (file == null)
        {
            sector = sector.getNextLocation().map(this::directorySectorAt).orElse(null);

            if (sector == null)
            {
                sector = allocateDirectorySector();
            }

            file = sector.findDeletedFile().orElse(null);
        }

        file.format();
        file.setName(fileName);
        file.setType(CbmFileType.PRG);
        file.setClosed(true);

        return file;
    }

    protected CbmDirectorySector allocateDirectorySector() throws CbmIOException
    {
        return new CbmDirectorySector(this, allocateSectorStrategy.allocateDirectorySector(this));
    }

    @Override
    public CbmSector allocateFileSector(Optional<CbmSectorLocation> optionalLocation, boolean subsequent)
        throws CbmIOException
    {
        return allocateSectorStrategy.allocateFileSector(this, optionalLocation, subsequent);
    }

    /**
     * Searches for a free sector in the specified track. Starts at the specified location and uses the specified
     * interleave to advance the sector number.
     *
     * @param location the starting location
     * @param interleave the interleave
     * @return the location, empty if not found
     */
    protected Optional<CbmSectorLocation> findFreeSectorOfTrack(CbmSectorLocation location, int interleave)
    {
        if (interleave < 0)
        {
            throw new IllegalArgumentException("Invalid interleave (interleave >= 0): " + interleave);
        }

        if (!isSectorUsed(location))
        {
            return Optional.of(location);
        }

        int trackNr = location.getTrackNr();

        if (getFreeSectorCountOfTrack(trackNr, true) == 0)
        {
            return Optional.empty();
        }

        CbmSectorMap sectorMap = getSectorMap();
        int maxSectorNr = sectorMap.getMaxSectorNrByTrackNr(trackNr);
        int sectorNr = location.getSectorNr();

        sectorNr = computeNextSector(sectorNr, maxSectorNr, interleave);

        location = location.withSectorNr(sectorNr);

        int count = 0;

        //        while (!isSectorAvailable(location) || isSectorUsed(location))
        while (isSectorUsed(location))
        {
            location = location.withSectorNr((location.getSectorNr() + 1) % (maxSectorNr + 1));
            count += 1;

            if (count > maxSectorNr + 1)
            {
                return Optional.empty();
            }
        }

        return Optional.of(location);
    }

    protected int computeNextSector(int sectorNr, int maxSectorNr, int interleave)
    {
        sectorNr += interleave;

        while (sectorNr > maxSectorNr)
        {
            sectorNr -= maxSectorNr + 1;

            if (sectorNr > 0)
            {
                --sectorNr;
            }
        }

        return sectorNr;
    }

    @Override
    public void disposeSectorChain(CbmSectorLocation location, boolean erase)
    {
        streamSectorChainAt(location).forEach(sector -> {
            setSectorUsed(sector.getLocation(), false);

            if (erase)
            {
                sector.erase();
            }
        });
    }

    @Override
    public CbmImage getImage()
    {
        return image;
    }

    @Override
    public CbmSectorLocation getDirectoryLocation()
    {
        return directoryRange.getFromLocation();
    }

    @Override
    public CbmAllocateSectorStrategy getAllocateSectorStrategy()
    {
        return allocateSectorStrategy;
    }

    @Override
    public CbmSectorInterleaves getSectorInterleaves()
    {
        return sectorInterleaves;
    }

    @Override
    public CbmSector sectorAt(CbmSectorLocation location)
    {
        return image.sectorAt(location);
    }

    @Override
    public Iterator<CbmSector> iterateSectorChainAt(CbmSectorLocation location)
    {
        return image.iterateSectorChainAt(location);
    }

    @Override
    public Stream<CbmSector> streamSectorChainAt(CbmSectorLocation location)
    {
        return image.streamSectorChainAt(location);
    }

    protected CbmSector headerSector()
    {
        return sectorAt(headerLocation);
    }

    protected CbmDirectorySector firstDirectorySector()
    {
        return directorySectorAt(directoryRange.getFromLocation());
    }

    protected CbmDirectorySector directorySectorAt(CbmSectorLocation location)
    {
        return new CbmDirectorySector(this, sectorAt(location));
    }

    protected Iterator<CbmDirectorySector> iterateDirectorySectorChain()
    {
        return new CbmDirectorySectorChainIterator(this, iterateSectorChainAt(directoryRange.getFromLocation()));
    }

    protected Stream<CbmDirectorySector> streamDirectorySectorChain()
    {
        return StreamSupport
            .stream(Spliterators.spliteratorUnknownSize(iterateDirectorySectorChain(), Spliterator.ORDERED), false);
    }

    protected Iterator<CbmFile> iterateFiles(Predicate<CbmFile> predicate)
    {
        return new CbmFileIterator(iterateDirectorySectorChain(), predicate);
    }

    protected Stream<CbmFile> streamFiles(Predicate<CbmFile> predicate)
    {
        return StreamSupport
            .stream(Spliterators.spliteratorUnknownSize(iterateFiles(predicate), Spliterator.ORDERED), false);
    }

    @Override
    public int getDosVersion()
    {
        return headerSector().getByte(0x02);
    }

    protected void setDosVersion(int version)
    {
        headerSector().setByte(0x02, version);
    }

    @Override
    public void clearSectorsUsed()
    {
        getSectorMap().forEach(location -> setSectorUsed(location, false));
    }

    @Override
    public int getFreeSectorCount(boolean includeReserved)
    {
        return getSectorMap()
            .stream()
            .map(CbmSectorLocation::getTrackNr)
            .distinct()
            .map(trackNr -> getFreeSectorCountOfTrack(trackNr, includeReserved))
            .reduce(0, Integer::sum);
    }

    @Override
    public void printDirectory(StringBuilder bob, boolean includeDeleted)
    {
        String check = checkFormatted();

        if (check != null)
        {
            bob.append(String.format("Not formatted: %s", check));
            return;
        }

        bob
            .append(String
                .format("%d \"%-16s\" %-2s %-2s\n", 0, getDiskName()/*.replace("\u00a0", " ")*/, getDiskId(),
                    getDosType()));

        streamFiles(file -> includeDeleted || file.exists())
            .map(CbmFile::describe)
            .forEach(line -> bob.append(line).append("\n"));

        bob.append(String.format("%d blocks free.", getFreeSectorCount(false)));
    }

    @Override
    public void printHeader(StringBuilder bob)
    {
        CbmSectorMap sectorMap = getSectorMap();
        String check = checkFormatted();

        if (check != null)
        {
            bob.append(String.format("Not formatted: %s", check));
            return;
        }

        bob.append(String.format("Disk %s\n", getDiskName()));
        bob.append("------------------------------------------------------------\n");
        bob.append(String.format("Header track/sector: %s\n", headerLocation));
        bob.append(String.format("Directory track/sector: %s\n", directoryRange));
        bob.append(String.format("Disk DOS Version: $%2x\n", getDosVersion()));
        bob.append(String.format("Disk ID / DOS Type: %s / %s\n", getDiskId(), getDosType()));
        bob.append("------------------------------------------------------------------------");

        for (int trackNr = 1; trackNr <= sectorMap.getMaxTrackNr(); trackNr += 1)
        {
            bob.append(String.format("\nTrack %2d ", trackNr));

            for (int sectorNr = 0; sectorNr <= sectorMap.getMaxSectorNrByTrackNr(trackNr); sectorNr += 1)
            {
                CbmSectorLocation location = CbmSectorLocation.of(trackNr, sectorNr);

                if (!image.isSectorSupported(location))
                {
                    bob.append(" ");
                }
                else if (isSectorUsed(location))
                {
                    bob.append("*");
                }
                else
                {
                    bob.append("-");
                }
            }

            bob.append(String.format(" (%d sectors free)", getFreeSectorCountOfTrack(trackNr, true)));
        }
    }

    @Override
    public void printSectors(StringBuilder bob)
    {
        printSectors(bob, getSectorMap());
    }

    @Override
    public void printSectors(StringBuilder bob, CbmSectorMap sectorMap)
    {
        boolean first = true;

        for (CbmSectorLocation location : sectorMap)
        {
            if (!image.isSectorSupported(location) || image.isSectorEmpty(location))
            {
                continue;
            }

            if (!first)
            {
                bob.append("\n\n");
            }
            else
            {
                first = false;
            }

            CbmSector sector = sectorAt(location);

            sector.print(bob);
        }
    }

    @Override
    public void printSectorChain(StringBuilder bob, CbmSectorLocation location)
    {
        int size = 0;
        boolean first = true;

        Iterator<CbmSector> iterator = iterateSectorChainAt(location);

        while (iterator.hasNext())
        {
            if (!first)
            {
                bob.append("\n");
            }
            else
            {
                first = false;
            }

            CbmSector sector = iterator.next();

            sector.print(bob);

            if (iterator.hasNext())
            {
                size += 254;
            }
            else
            {
                size += sector.getNextSectorNr() - 1;
            }
        }

        bob.append("\n------------------------------------------------------------------------\n");
        bob.append(String.format("Size: %s bytes\n", size));
        bob.append("------------------------------------------------------------------------");
    }

    @Override
    public void printSector(StringBuilder bob, CbmSectorLocation location)
    {
        CbmSector sector = image.sectorAt(location);

        sector.print(bob);
    }
}
