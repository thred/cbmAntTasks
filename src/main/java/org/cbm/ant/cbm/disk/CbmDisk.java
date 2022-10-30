package org.cbm.ant.cbm.disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public interface CbmDisk
{
    CbmDisk withAllocateSectorStrategy(CbmAllocateSectorStrategy allocateSectorStrategy);

    CbmDisk withSectorInterleaves(CbmSectorInterleaves sectorInterleaves);

    default void load(File file) throws FileNotFoundException, IOException
    {
        try (FileInputStream in = new FileInputStream(file))
        {
            load(in);
        }
    }

    default void load(InputStream in) throws IOException
    {
        load(in.readAllBytes());
    }

    void load(byte[] bytes) throws CbmIOException;

    default void save(File file) throws IOException
    {
        try (FileOutputStream out = new FileOutputStream(file))
        {
            save(out);
        }
    }

    /**
     * Saves the disk to the specitified stream
     *
     * @param out the stream
     * @throws IOException on occasion
     */
    void save(OutputStream out) throws IOException;

    /**
     * Formats this disk
     *
     * @param diskName the name of the disk
     * @param diskId the id
     */
    void format(String diskName, String diskId);

    default boolean isFormatted()
    {
        return checkFormatted() == null;
    }

    /**
     * @return null, if the file system appears to be formatted by the {@link #format(String, String)} method, a text
     *         describing the problem otherwise
     */
    String checkFormatted();

    boolean existsFile(String fileName);

    CbmFile getFile(String fileName) throws CbmIOException;

    Stream<CbmFile> findFiles(String fileNamePattern);

    default void deleteFile(String fileName, boolean erase) throws CbmIOException
    {
        getFile(fileName).delete(erase);
    }

    default void deleteFiles(String fileNamePattern, boolean erase) throws CbmIOException
    {
        findFiles(fileNamePattern).forEach(file -> file.delete(erase));
    }

    default InputStream readFile(String fileName) throws CbmIOException
    {
        return getFile(fileName).read();
    }

    default byte[] readFileFully(String fileName) throws IOException
    {
        return getFile(fileName).readFully();
    }

    default CbmFile writeFile(String fileName, File file) throws CbmIOException, IOException
    {
        CbmFile cbmFile = allocateFile(fileName);

        cbmFile.write(file);

        return cbmFile;
    }

    default CbmFile writeFile(String fileName, InputStream in) throws CbmIOException, IOException
    {
        CbmFile cbmFile = allocateFile(fileName);

        cbmFile.write(in);

        return cbmFile;
    }

    default CbmFile writeFile(String fileName, byte[] bytes) throws CbmIOException, IOException
    {
        CbmFile cbmFile = allocateFile(fileName);

        cbmFile.write(bytes);

        return cbmFile;
    }

    default OutputStream writeFile(String fileName) throws CbmIOException
    {
        CbmFile cbmFile = allocateFile(fileName);

        return cbmFile.write();
    }

    /**
     * Creates a new directory entry with an empty PRG file of the specified name.
     *
     * @param fileName the name of the file
     * @return the directory entry
     * @throws CbmIOException if the file exists already or the directory is full.
     */
    CbmFile allocateFile(String fileName) throws CbmIOException;

    CbmSector allocateFileSector(Optional<CbmSectorLocation> optionalLocation, boolean subsequent)
        throws CbmIOException;

    void disposeSectorChain(CbmSectorLocation location, boolean erase);

    CbmImage getImage();

    default CbmSectorMap getSectorMap()
    {
        return getImage().getSectorMap();
    }

    CbmSectorLocation getDirectoryLocation();

    CbmAllocateSectorStrategy getAllocateSectorStrategy();

    CbmSectorInterleaves getSectorInterleaves();

    int getDosVersion();

    String getDiskName();

    String getDiskId();

    String getDosType();

    CbmSector sectorAt(CbmSectorLocation location);

    Iterator<CbmSector> iterateSectorChainAt(CbmSectorLocation location);

    Stream<CbmSector> streamSectorChainAt(CbmSectorLocation location);

    /**
     * Marks all sectors as unused, if the file system is supporting this feature.
     */
    void clearSectorsUsed();

    /**
     * Returns true if the sector is used, usually by checking the BAM.
     *
     * @param location the location
     * @return true if used.
     */
    boolean isSectorUsed(CbmSectorLocation location);

    /**
     * Sets the sector to be used, it the file system is supporting this feature.
     *
     * @param location the location
     * @param used true to mark as used
     */
    void setSectorUsed(CbmSectorLocation location, boolean used);

    /**
     * Returns the number of free sectors of the track.
     *
     * @param includeReserved if true, sectors, that are reserved for the file system (header, BAM, directory) will be
     *            included
     * @return the number of free sectors
     */
    int getFreeSectorCountOfTrack(int trackNr, boolean includeReserved);

    /**
     * Returns the total number of free sectors of the file system
     *
     * @param includeReserved if true, sectors, that are reserved for the file system (header, BAM, directory) will be
     *            included
     * @return the number of free sectors
     */
    int getFreeSectorCount(boolean includeReserved);

    default int getBlocksFree()
    {
        return getFreeSectorCount(false);
    }

    default boolean isErrorInformationAvailable()
    {
        return getImage().isErrorInformationAvailable();
    }

    default void setErrorInformationAvailable(boolean errorInformationAvailable)
    {
        getImage().setErrorInformationAvailable(errorInformationAvailable);
    }

    default int getError(CbmSectorLocation location)
    {
        return getImage().getErrorInformation(location);
    }

    default void setError(CbmSectorLocation location, int error)
    {
        getImage().setErrorInformation(location, error);
    }

    /**
     * Prints the directory to the specified stream.
     *
     * @param bob the builder
     * @param includeDeleted true to include deleted entries
     */
    void printDirectory(StringBuilder bob, boolean includeDeleted);

    /**
     * Prints the header information and the BAM to the specified stream.
     *
     * @param bob the builder
     */
    void printHeader(StringBuilder bob);

    /**
     * Prints all sectors to the specified stream.
     *
     * @param bob the builder
     * @param sectorMap the sectors to print
     */
    void printSectors(StringBuilder bob);

    /**
     * Prints the sectors referenced in the map to the specified stream.
     *
     * @param bob the builder
     * @param sectorMap the sectors to print
     */
    void printSectors(StringBuilder bob, CbmSectorMap sectorMap);

    /**
     * Prints the specified sectors to the specified steam and follows the chain of sectors.
     *
     * @param bob the builder
     * @param location the location of the sector to start with
     */
    void printSectorChain(StringBuilder bob, CbmSectorLocation location);
}
