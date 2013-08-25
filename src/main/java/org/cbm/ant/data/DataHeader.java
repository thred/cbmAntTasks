package org.cbm.ant.data;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;

public class DataHeader implements DataCommand {

	private byte[] header;

	public DataHeader() {
		super();
	}

	public byte[] getHeader() {
		return header;
	}

	public void setHeader(String header) {
		int value = Integer.decode(header).intValue();

		if ((value < 0x0000) || (value > 0xffff))
		{
			throw new BuildException("Invalid header: " + header);
		}
		
		this.header = new byte[]{
			(byte) (value % 0x0100), (byte) (value / 0x0100)
		};
	}
	
	/**
	 * @see org.cbm.ant.data.DataCommand#isExecutionNecessary(long, boolean)
	 */
	@Override
	public boolean isExecutionNecessary(long lastModified, boolean exists) {
		return !exists;
	}

	/**
	 * @see org.cbm.ant.data.DataCommand#execute(Data, java.io.OutputStream)
	 */
	@Override
	public void execute(Data task, OutputStream out) throws BuildException, IOException {
		task.log("Adding header");

		out.write(header);
	}
	
}
