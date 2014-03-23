package org.cbm.ant.cbm.disk;

import java.io.IOException;
import java.io.InputStream;

public class CBMDiskInputStream extends InputStream
{

	private final CBMDiskOperator operator;

	private CBMDiskSector sector;
	private int position;
	private int end;

	/**
	 * Creates an input stream using the specified disk operator and the specified sector
	 * 
	 * @param operator the operator
	 * @param trackNr the number of the track
	 * @param sectorNr the number of the sector
	 * @throws IllegalArgumentException if the number of the track and/or sector is invalid
	 */
	public CBMDiskInputStream(CBMDiskOperator operator, int trackNr, int sectorNr) throws IllegalArgumentException
	{
		super();

		this.operator = operator;

		initSector(trackNr, sectorNr);
	}

	private boolean hasNextSector()
	{
		return (sector.getNextTrackNr() > 0);
	}

	private void nextSector()
	{
		if (!hasNextSector())
		{
			throw new IllegalStateException("Last sector reached");
		}

		initSector(sector.getNextTrackNr(), sector.getNextSectorNr());
	}

	/**
	 * Initializes the specified sector
	 * 
	 * @param trackNr the number of the track
	 * @param sectorNr the number of the sector
	 * @throws IllegalArgumentException if the number of the track and/or sector is invalid
	 */
	private void initSector(int trackNr, int sectorNr) throws IllegalArgumentException
	{
		sector = operator.getDisk().getSector(trackNr, sectorNr);
		position = 2;

		if (!hasNextSector())
		{
			end = sector.getNextSectorNr();
		}
		else
		{
			end = 255;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read() throws IOException
	{
		if (position > end)
		{
			if (!hasNextSector())
			{
				return -1;
			}

			nextSector();
		}

		return sector.getByte(position++);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(byte[] b, int offset, int length) throws IOException
	{
		if (position > end)
		{
			if (!hasNextSector())
			{
				return -1;
			}

			nextSector();
		}

		int available = Math.min((end + 1) - position, length);

		System.arraycopy(sector.getData(), position, b, offset, available);

		position += available;

		return available;
	}

}
