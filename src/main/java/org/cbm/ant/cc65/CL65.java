package org.cbm.ant.cc65;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.cbm.ant.util.AntFile;
import org.cbm.ant.util.ProcessConsumer;
import org.cbm.ant.util.ProcessHandler;

public class CL65 extends AbstractCC65Task implements ProcessConsumer
{

	private static final Map<String, String> EXECUTABLES = new HashMap<String, String>();

	static
	{
		EXECUTABLES.put("Linux.*", "bin/cl65");
		EXECUTABLES.put("Windows.*", "bin/cl65.exe");
	}

	private final List<FileSet> files;
	private final List<FileSet> includes;

	private File outputFile;
	private File configFile;

	public CL65()
	{
		super();

		files = new ArrayList<FileSet>();
		includes = new ArrayList<FileSet>();
	}

	/**
	 * @see org.cbm.ant.cc65.AbstractCC65Task#getExecutables()
	 */
	@Override
	public Map<String, String> getExecutables()
	{
		return EXECUTABLES;
	}

	public File getOutputFile()
	{
		return outputFile;
	}

	public void setOutputFile(File outputFile)
	{
		this.outputFile = outputFile;
	}

	public File getConfigFile()
	{
		return configFile;
	}

	public void setConfigFile(File configFile)
	{
		this.configFile = configFile;
	}

	public void addFiles(FileSet files)
	{
		this.files.add(files);
	}

	public void addIncludes(FileSet includes)
	{
		this.includes.add(includes);
	}

	private long lastModified()
	{
		return lastModified(files, getConfigFile());
	}

	private boolean isExecutionNecessary()
	{
		File outputFile = getOutputFile();

		if (!outputFile.exists())
		{
			return true;
		}

		long lastModified = lastModified(files, getConfigFile());

		if (outputFile.lastModified() > lastModified())
		{
			log("\"" + outputFile.getAbsolutePath() + "\" got modified in the future");

			return false;
		}

		return outputFile.lastModified() < lastModified;
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException
	{
		if (!isExecutionNecessary())
		{
			return;
		}

		Collection<AntFile> inputFiles = collect(files);
		ProcessHandler handler = new ProcessHandler(this, getExecutable());

		handler.directory(getProject().getBaseDir());

		if (getTarget() != null)
		{
			handler.parameter("-t").parameter(getTarget().getName());
		}

		if (configFile != null)
		{
			handler.parameter("--config").parameter(configFile);
		}

		if (outputFile != null)
		{
			handler.parameter("-o").parameter(outputFile);
		}

		for (File inputFile : inputFiles)
		{
			handler.parameter(inputFile);
		}

		log("Executing: " + handler);
		log("");

		int exitValue = handler.consume();

		log("");

		if (exitValue != 0)
		{
			outputFile.delete();
			
			throw new BuildException("Failed with exit value " + exitValue);
		}
		
		outputFile.setLastModified(lastModified());
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
