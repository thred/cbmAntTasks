package org.cbm.ant.cbm.disk;

import java.io.PrintStream;

/**
 * Service for accessing the directory of a CBM disk image
 *
 * @author thred
 */
public class CBMDiskDir
{

    private final CBMDiskOperator operator;

    private CBMDiskDirSector firstDirSector;

    /**
     * Creates the service for accessing the directory of a CBM disk image
     * 
     * @param operator the operator
     */
    public CBMDiskDir(CBMDiskOperator operator)
    {
        super();

        this.operator = operator;
    }

    public CBMDiskOperator getOperator()
    {
        return operator;
    }

    public CBMDisk getDisk()
    {
        return operator.getDisk();
    }

    /**
     * (Re)scans the directory to create a internal representation. This should be done after disk changes.
     */
    public void scan()
    {
        CBMDiskBAM bam = operator.getBAM();
        CBMDiskLocation location = bam.getDirLocation();

        firstDirSector = new CBMDiskDirSector(this, location, 0);

        firstDirSector.scan();
    }

    /**
     * Formats the directory. Expects that the BAM already has been initialized.
     */
    public void format()
    {
        CBMDiskBAM bam = operator.getBAM();
        CBMDiskLocation location = bam.getDirLocation();

        firstDirSector = new CBMDiskDirSector(this, location, 0);

        bam.setSectorUsed(location, true);

        firstDirSector.format();

        scan();
    }

    /**
     * Lists the directory to the specified stream
     * 
     * @param out the stream
     * @param listKeys if true adds a key to the listing that matches the key of the BAM listing
     * @param listDeleted if true even lists deleted files
     */
    public void list(PrintStream out, boolean listKeys, boolean listDeleted)
    {
        CBMDiskBAM bam = operator.getBAM();

        scan();

        out.printf("%d \"%-16s\" %-2s %-2s\n", 0, bam.getDiskName(), bam.getDiskID(), bam.getDOSType());
        firstDirSector.list(out, listKeys, listDeleted);
        out.printf("%d blocks free.\n", bam.getFreeSectors());
    }

    /**
     * Marks the sectors of the disk, that belong to a directory entry.
     */
    public void mark()
    {
        scan();

        CBMDisk disk = getDisk();

        disk.clearMarks();

        CBMDiskBAM bam = operator.getBAM();

        bam.getBAMSector().setMark(CBMDiskUtil.MARK_BAM);

        int track = bam.getDirTrackNr();
        int sector = bam.getDirSectorNr();

        disk.mark(track, sector, CBMDiskUtil.MARK_DIR);

        firstDirSector.mark();
    }

    /**
     * Searches for a file name. The file name supports wildcards.
     * 
     * @param fileName the file name
     * @return the entry
     */
    public CBMDiskDirEntry find(String fileName)
    {
        scan();

        return firstDirSector.find(fileName);
    }

    /**
     * Allocates a free directory entry.
     * 
     * @return a free directory entry
     * @throws CBMDiskException on occasion
     */
    public CBMDiskDirEntry allocate() throws CBMDiskException
    {
        scan();

        return firstDirSector.allocate();
    }

    /**
     * Returns the first directory sector
     * 
     * @return the first directory sector
     */
    public CBMDiskDirSector getFirstDirSector()
    {
        return firstDirSector;
    }

}
