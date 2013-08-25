package org.cbm.ant.viceteam;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessConsumer;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractViceLaunchTask extends AbstractViceTask implements ProcessConsumer
{

	private File autostart;
	private File tape;
	private File drive8;
	private File drive9;
	private File drive10;
	private File drive11;

	private File labelFile;

	public AbstractViceLaunchTask()
	{
		super();
	}

	public File getAutostart()
	{
		return autostart;
	}

	public void setAutostart(File autostart)
	{
		this.autostart = autostart;
	}

	public File getTape()
	{
		return tape;
	}

	public void setTape(File tape)
	{
		this.tape = tape;
	}

	public File getDrive8()
	{
		return drive8;
	}

	public void setDrive8(File drive8)
	{
		this.drive8 = drive8;
	}

	public File getDrive9()
	{
		return drive9;
	}

	public void setDrive9(File drive9)
	{
		this.drive9 = drive9;
	}

	public File getDrive10()
	{
		return drive10;
	}

	public void setDrive10(File drive10)
	{
		this.drive10 = drive10;
	}

	public File getDrive11()
	{
		return drive11;
	}

	public void setDrive11(File drive11)
	{
		this.drive11 = drive11;
	}

	public File getLabelFile()
	{
		return labelFile;
	}

	public void setLabelFile(File labelFile)
	{
		this.labelFile = labelFile;
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException
	{
		File executable = getExecutable();
		ProcessHandler handler = new ProcessHandler(this, executable).directory(executable.getParentFile());

		if (getTape() != null)
		{
			handler.parameter("-1").parameter(getTape());
		}

		if (getDrive8() != null)
		{
			handler.parameter("-8").parameter(getDrive8());
		}

		if (getDrive9() != null)
		{
			handler.parameter("-9").parameter(getDrive9());
		}

		if (getDrive10() != null)
		{
			handler.parameter("-10").parameter(getDrive10());
		}

		if (getDrive11() != null)
		{
			handler.parameter("-11").parameter(getDrive11());
		}

		if (getLabelFile() != null)
		{
			/* VICE does not handle that right - it does not load all the labels
			try
			{
				File viceLaunchFile = File.createTempFile("cbmAntTasks", "viceLaunch");
				Writer writer = new FileWriter(viceLaunchFile);

				try
				{
					writer.write("ll \"");
					writer.write(labelFile.getAbsolutePath());
					writer.write("\"\n");
				}
				finally
				{
					writer.close();
				}

				handler.parameter("-moncommand").parameter(viceLaunchFile);
				
				viceLaunchFile.deleteOnExit();
			}
			catch (IOException e)
			{
				throw new BuildException("Error creating vice launch file", e);
			}
			*/
			handler.parameter("-moncommand").parameter(labelFile.getAbsoluteFile());
		}

		if (getAutostart() != null)
		{
			handler.parameter("-autostart").parameter(getAutostart());
		}

		log("Executing: " + handler);

		int exitValue = handler.consume();

		if (exitValue != 0)
		{
			throw new BuildException("Failed with exit value " + exitValue);
		}
	}

	/**
	 * @see org.cbm.ant.util.ProcessConsumer#processOutput(java.lang.String, boolean)
	 */
	@Override
	public void processOutput(String output, boolean isError)
	{
		log(output);
	}

}
