package org.cbm.ant.data;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;

public class DataFill implements DataCommand
{

	private int length;
	private String value;

	public DataFill()
	{
		super();
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @see org.cbm.ant.data.DataCommand#isExecutionNecessary(long, boolean)
	 */
	@Override
	public boolean isExecutionNecessary(long lastModified, boolean exists)
	{
		return !exists;
	}

	/**
	 * @see org.cbm.ant.data.DataCommand#execute(Data, java.io.OutputStream)
	 */
	@Override
	public void execute(Data task, OutputStream out) throws BuildException, IOException
	{
		task.log("Adding " + length + " time " + value);

		int v = Integer.decode(value);

		for (int i = 0; i < length; i += 1)
		{
			out.write(v);
		}
	}

}
