package org.cbm.ant.cbm.disk;

import java.io.PrintStream;

public class CBMDiskDir
{

	private final CBMDiskOperator operator;

	private CBMDiskDirBlock firstBlock;

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
		int track = bam.getDirTrackNr();
		int sector = bam.getDirSectorNr();

		firstBlock = new CBMDiskDirBlock(this, track, sector, 0);

		firstBlock.scan();
	}

	public void format()
	{
		scan();

		firstBlock.format();
	}

	public void list(PrintStream out)
	{
		CBMDiskBAM bam = operator.getBAM();

		scan();

		out.printf("%d \"%-16s\" %-2s %-2s\n", 0, bam.getDiskName(), bam.getDiskID(), bam.getDOSType());
		firstBlock.list(out);
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

		firstBlock.mark();
	}

	public CBMDiskDirEntry find(String fileName)
	{
		scan();

		return firstBlock.find(fileName);
	}
	
	public CBMDiskDirEntry allocate() {
		scan();
		
		return firstBlock.allocate();
	}

}
