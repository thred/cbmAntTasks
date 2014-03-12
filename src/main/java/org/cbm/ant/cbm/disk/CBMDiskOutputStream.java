package org.cbm.ant.cbm.disk;

import java.io.IOException;
import java.io.OutputStream;

public class CBMDiskOutputStream extends OutputStream
{

	private final CBMDisk disk;

	private CBMDiskLocation location = null;

	public CBMDiskOutputStream(CBMDisk disk)
	{
		super();

		this.disk = disk;
	}

	public CBMDiskLocation getLocation()
	{
		return location;
	}

	public void setLocation(CBMDiskLocation location)
	{
		this.location = location;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException
	{
		
	}

}
