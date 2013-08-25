package org.cbm.ant.cbm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.cbm.ant.util.Util;

/**
 * Splits a CBM PRG file into multiple parts.
 * 
 * @author Manfred Hantschel
 */
public class PRGSplit extends Task
{

	private final List<PRGSplitPart> parts;

	private File source;
	private String header;
	private boolean hasHeader = true;

	public PRGSplit()
	{
		super();

		parts = new ArrayList<PRGSplitPart>();
	}

	public File getSource()
	{
		if (source == null)
		{
			throw new BuildException("Source missing");
		}

		return source;
	}

	/**
	 * Sets the source file. Mandatory.
	 * 
	 * @param source the source file
	 */
	public void setSource(File source)
	{
		this.source = source;
	}

	public int getHeader(int defaultHeader)
	{
		if (Util.isEmpty(header))
		{
			return defaultHeader;
		}

		try
		{
			int value = Integer.decode(header).intValue();

			if ((value < 0x0000) || (value > 0xffff))
			{
				throw new BuildException("Invalid header: " + header);
			}

			return value;
		}
		catch (NumberFormatException e)
		{
			throw new BuildException("Invalid header: " + header, e);
		}
	}

	/**
	 * Sets the base header as decimal or hex value for the parts. The parts will get header plus their offset. Default
	 * is specified by the header of the source file.
	 * 
	 * @param header the header value
	 */
	public void setHeader(String header)
	{
		this.header = header;
	}

	public boolean hasHeader()
	{
		return hasHeader;
	}

	/**
	 * Set to false, if the source file does not contain header information (first two bytes). Default is "true".
	 * 
	 * @param hasHeader true if file contains CBM PRG header
	 */
	public void setHasHeader(boolean hasHeader)
	{
		this.hasHeader = hasHeader;
	}

	/**
	 * Adds a part
	 * 
	 * @param part the part
	 */
	public void addPart(PRGSplitPart part)
	{
		parts.add(part);
	}

	private boolean isExecutionNecessary(File source)
	{
		long lastModified = source.lastModified();

		for (PRGSplitPart part : parts)
		{
			if (part.isExecutionNecessary(lastModified))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException
	{
		File source = getSource();

		if (!source.exists())
		{
			throw new BuildException("Invalid source: " + source.getAbsolutePath());
		}

		if (!isExecutionNecessary(source))
		{
			return;
		}

		long lastModified = source.lastModified();
		int header = 0;
		byte[] bytes;

		try
		{
			log("Reading " + source);
			bytes = Util.read(source);
		}
		catch (IOException e)
		{
			throw new BuildException("Error reading: " + source.getAbsolutePath());
		}

		if (hasHeader())
		{
			header = (bytes[0] & 0xff) + ((bytes[1] & 0xff) << 8);

			log("Header is " + Util.toHex(header));

			bytes = Arrays.copyOfRange(bytes, 2, bytes.length);
		}

		header = getHeader(header);

		int offset = 0;

		for (PRGSplitPart part : parts)
		{
			log("Writing " + part.getTarget());

			offset = part.execute(header, bytes, offset, lastModified);
		}
	}

}
