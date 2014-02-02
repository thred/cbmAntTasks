package org.cbm.ant.verify;

import java.io.File;

import org.apache.tools.ant.BuildException;

public class VerifySizeTask implements VerifyTask
{

	private File file;
	private String size;

	public VerifySizeTask()
	{
		super();
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public String getSize()
	{
		return size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	@Override
	public void execute(Verify task) throws BuildException
	{
		if (!file.exists())
		{
			throw new BuildException(String.format("\"%s\" is missing", file));
		}

		long length = file.length();
		int size = Integer.decode(this.size);
		double percent = (double) length / size;

		task.log(String.format("\t%-20s %8d bytes (%4.1f %%)", file.getName(), length, percent * 100));

		if (percent > 1)
		{
			throw new BuildException(String.format("\"%s\" exceeds specified size by %d bytes", file, length - size));
		}
	}

}
