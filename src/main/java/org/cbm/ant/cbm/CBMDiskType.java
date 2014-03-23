package org.cbm.ant.cbm;

import org.cbm.ant.cbm.disk.CBMDiskFormat;

public enum CBMDiskType
{

	D64(CBMDiskFormat.CBM_154x),

	D64E(CBMDiskFormat.CBM_154x_EXTENDED);

	private final CBMDiskFormat format;

	private CBMDiskType(CBMDiskFormat format)
	{
		this.format = format;
	}

	public CBMDiskFormat getFormat()
	{
		return format;
	}

}
