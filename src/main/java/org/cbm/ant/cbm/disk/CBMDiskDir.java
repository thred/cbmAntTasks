package org.cbm.ant.cbm.disk;

import java.io.PrintStream;

public class CBMDiskDir
{

    private final CBMDiskOperator operator;

    private CBMDiskDirSector firstDirSector;

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

    public void scan()
    {
        CBMDiskBAM bam = operator.getBAM();
        CBMDiskLocation location = bam.getDirLocation();

        firstDirSector = new CBMDiskDirSector(this, location, 0);

        firstDirSector.scan();
    }

    public void format()
    {
        CBMDiskBAM bam = operator.getBAM();
        CBMDiskLocation location = bam.getDirLocation();

        firstDirSector = new CBMDiskDirSector(this, location, 0);
        bam.setSectorUsed(location, true);

        firstDirSector.format();

        scan();
    }

    public void list(PrintStream out)
    {
        CBMDiskBAM bam = operator.getBAM();

        scan();

        out.printf("%d \"%-16s\" %-2s %-2s\n", 0, bam.getDiskName(), bam.getDiskID(), bam.getDOSType());
        firstDirSector.list(out);
        out.printf("%d blocks free.\n", bam.getFreeSectors());
    }

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

    public CBMDiskDirEntry find(String fileName)
    {
        scan();

        return firstDirSector.find(fileName);
    }

    public CBMDiskDirEntry allocate()
    {
        scan();

        return firstDirSector.allocate();
    }

    public CBMDiskDirSector getFirstDirSector()
    {
        return firstDirSector;
    }

}
