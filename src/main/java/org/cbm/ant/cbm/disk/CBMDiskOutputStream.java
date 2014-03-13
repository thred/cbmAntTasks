package org.cbm.ant.cbm.disk;

import java.io.IOException;
import java.io.OutputStream;

public class CBMDiskOutputStream extends OutputStream
{

	private final CBMDiskOperator operator;
	private final CBMDiskDirEntry dirEntry;

	private CBMDiskLocation location = null;
	private CBMDiskSector sector = null;
	private int position;
	private int size = 0;

	public CBMDiskOutputStream(CBMDiskOperator operator, CBMDiskDirEntry dirEntry, CBMDiskLocation location)
	{
		super();

		this.operator = operator;
		this.dirEntry = dirEntry;

		this.location = location;
	}

	public CBMDiskOperator getOperator()
	{
		return operator;
	}

	public CBMDiskDirEntry getDirEntry()
	{
		return dirEntry;
	}

	public CBMDiskLocation getLocation()
	{
		return location;
	}

	public int getSize()
	{
		return size;
	}

	protected void ensure() throws IOException
	{
		if (location == null)
		{
			location = operator.getBAM().findFreeSector();

			grab(location);
		}
		else if (position > 255)
		{
			grab(operator.getBAM().findNextFreeSector(location));
		}
	}

	protected void grab(CBMDiskLocation location) throws IOException
	{
		if (sector != null)
		{
			sector.setNextLocation(location);
		}
		else if (dirEntry != null)
		{
			dirEntry.setFileLocation(location);
			dirEntry.setFileTypeClosed(false);
		}

		if (operator.getBAM().isSectorUsed(location))
		{
			throw new IOException(String.format("Track/sector %s already used", location));
		}

		operator.getBAM().setSectorUsed(location, true);
		sector = operator.getDisk().getSector(location);
		position = 2;
		size += 1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException
	{
		ensure();

		sector.setByte(position, b);
		position += 1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException
	{
		sector.setNextTrackNr(0);
		sector.setNextSectorNr(position - 1);

		if (dirEntry != null)
		{
			dirEntry.setFileSize(size);
			dirEntry.setFileTypeClosed(true);
		}
	}

}
