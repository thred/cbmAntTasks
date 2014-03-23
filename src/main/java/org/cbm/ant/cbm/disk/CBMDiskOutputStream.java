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
			try
			{
				location = operator.getBAM().findFreeSector();
			}
			catch (CBMDiskException e)
			{
				throw new IOException(e.getMessage(), e);
			}

			grab(location);
		}
		else if (position > 255)
		{
			CBMDiskLocation currentLocation = location;

			if (sector != null)
			{
				currentLocation = sector.getLocation();
			}

			try
			{
				grab(operator.getBAM().findNextFreeSector(currentLocation));
			}
			catch (CBMDiskException e)
			{
				throw new IOException(e.getMessage(), e);
			}
		}
	}

	protected void grab(CBMDiskLocation nextLocation) throws IOException
	{
		if (sector != null)
		{
			sector.setNextLocation(nextLocation);
		}
		else if (dirEntry != null)
		{
			dirEntry.setFileLocation(nextLocation);
			dirEntry.setFileTypeClosed(false);
		}

		if (operator.getBAM().isSectorUsed(nextLocation))
		{
			throw new IOException(String.format("Track/sector %s already used", nextLocation));
		}

		operator.getBAM().setSectorUsed(nextLocation, true);
		sector = operator.getDisk().getSector(nextLocation);
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
