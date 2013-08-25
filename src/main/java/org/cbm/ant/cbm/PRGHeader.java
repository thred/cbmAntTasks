package org.cbm.ant.cbm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.cbm.ant.util.Util;

/**
 * Adds or modified the two byte header of a CBM PRG file.
 * 
 * @author Manfred Hantschel
 */
public class PRGHeader extends Task
{

	public enum Method
	{
		AUTO,

		ADD,

		MODIFY
	}

	private Method method;
	private File source;
	private File target;
	private String header;

	public PRGHeader()
	{
		super();
	}

	public Method getMethod() throws BuildException
	{
		if (method == null)
		{
			return Method.AUTO;
		}

		return method;
	}

	/**
	 * Sets the method: "add", "modify" or "auto". "auto" is default.
	 * <ul>
	 * <li>add: adds the header, the file will get two bytes more</li>
	 * <li>modify: modified the first two bytes, the fill will keep its size</li>
	 * <li>auto: if the first two bytes do not match the header, it will add the header</li>
	 * <ul>
	 * 
	 * @param method the method
	 */
	public void setMethod(String method)
	{
		try
		{
			this.method = Method.valueOf(method.toUpperCase(Locale.getDefault()));
		}
		catch (IllegalArgumentException e)
		{
			throw new BuildException("Invalid method: " + method, e);
		}
	}

	public File getSource()
	{
		if (source == null)
		{
			return getTarget();
		}

		return source;
	}

	/**
	 * Sets the source file. Default is the target file.
	 * 
	 * @param source the source file
	 */
	public void setSource(File source)
	{
		this.source = source;
	}

	public File getTarget() throws BuildException
	{
		if (target == null)
		{
			throw new BuildException("Target is missing");
		}

		return target;
	}

	/**
	 * Sets the target file. Mandatory.
	 * 
	 * @param target the target
	 */
	public void setTarget(File target)
	{
		this.target = target;
	}

	public byte[] getHeader()
	{
		if (Util.isEmpty(header))
		{
			throw new BuildException("Header is missing");
		}

		try
		{
			int value = Integer.decode(header).intValue();

			if ((value < 0x0000) || (value > 0xffff))
			{
				throw new BuildException("Invalid header: " + header);
			}

			return new byte[]{
				(byte) (value % 0x0100), (byte) (value / 0x0100)
			};
		}
		catch (NumberFormatException e)
		{
			throw new BuildException("Invalid header: " + header, e);
		}
	}

	/**
	 * Sets the header as decimal or hex string. Mandatory.
	 * 
	 * @param header the header
	 */
	public void setHeader(String header)
	{
		this.header = header;
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
			throw new BuildException("Source not available: " + source.getAbsolutePath());
		}

		File target = getTarget();

		if ((target.exists()) && (!target.equals(source)) && (source.lastModified() == target.lastModified()))
		{
			// not update necessary
			return;
		}

		byte[] header = getHeader();
		byte[] bytes;

		try
		{
			log("Reading " + source);
			bytes = Util.read(source);
		}
		catch (IOException e)
		{
			throw new BuildException("Error reading source", e);
		}

		try
		{
			switch (getMethod())
			{
				case AUTO:
					if ((bytes[0] == header[0]) && (bytes[1] == header[1]))
					{
						// already contains header
						if (!source.equals(target))
						{
							log("Keeping header");
							log("Writing " + target.getAbsolutePath());

							write(target, null, bytes);
						}
						else
						{
							log("No change necessary");
						}
						break;
					}
					//$FALL-THROUGH$
				case ADD:
					log("Adding header: " + Util.toHex(header[0]) + " " + Util.toHex(header[1]));
					log("Writing " + target.getAbsolutePath());

					write(target, header, bytes);
					break;

				case MODIFY:
					log("Modifying header: " + Util.toHex(header[0]) + " " + Util.toHex(header[1]));
					log("Writing " + target.getAbsolutePath());

					bytes[0] = header[0];
					bytes[1] = header[1];

					write(target, null, bytes);
					break;
			}
		}
		catch (IOException e)
		{
			throw new BuildException("Error writing target: " + target.getAbsolutePath());
		}

		if (!source.equals(target))
		{
			target.setLastModified(source.lastModified());
		}
	}

	private static void write(File file, byte[] header, byte[] bytes) throws IOException
	{
		OutputStream out = new FileOutputStream(file);

		try
		{
			if (header != null)
			{
				out.write(header);
			}

			out.write(bytes);
		}
		finally
		{
			out.close();
		}
	}

}
