package org.cbm.ant.cc65;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.cbm.ant.util.AntFile;
import org.cbm.ant.util.ProcessConsumer;
import org.cbm.ant.util.ProcessHandler;

public class CC65 extends AbstractCC65Task implements ProcessConsumer
{

	private static final Map<String, String> EXECUTABLES = new HashMap<String, String>();

	static
	{
		EXECUTABLES.put("Linux.*", "bin/cc65");
		EXECUTABLES.put("Windows.*", "bin/cc65.exe");
	}

	private final List<FileSet> files;
	private final List<FileSet> includes;

	private File file;
	private File outputDir;
	private boolean annotate = false;
	private boolean debug = false;
	private boolean optimize = false;
	private boolean inlining = false;
	private boolean registerVariables = false;
	private boolean staticLocals = false;

	public CC65()
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

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public File getOutputDir()
	{
		return outputDir;
	}

	public void setOutputDir(File outputDir)
	{
		this.outputDir = outputDir;
	}

	public void addFiles(FileSet files)
	{
		this.files.add(files);
	}

	public void addIncludes(FileSet includes)
	{
		this.includes.add(includes);
	}

	public boolean isAnnotate()
	{
		return annotate;
	}

	public void setAnnotate(boolean annotate)
	{
		this.annotate = annotate;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public boolean isOptimize()
	{
		return optimize;
	}

	public void setOptimize(boolean optimize)
	{
		this.optimize = optimize;
	}

	public boolean isInlining()
	{
		return inlining;
	}

	public void setInlining(boolean inlining)
	{
		this.inlining = inlining;
	}

	public boolean isRegisterVariables()
	{
		return registerVariables;
	}

	public void setRegisterVariables(boolean registerVariables)
	{
		this.registerVariables = registerVariables;
	}

	public boolean isStaticLocals()
	{
		return staticLocals;
	}

	public void setStaticLocals(boolean staticLocals)
	{
		this.staticLocals = staticLocals;
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException
	{
		for (AntFile inputFile : collect(files, getFile()))
		{
			AntFile outputFile = null;

			if (outputDir != null)
			{
				outputFile = AntFile.create(getProject().getBaseDir(), outputDir, inputFile.getFilename())
						.withExtension("s");
			}
			else
			{
				outputFile = inputFile.withExtension("s");
			}

			AntFile dependencyFile = outputFile.withExtension("d");

			if (isExecutionNecessary(inputFile, outputFile, dependencyFile))
			{
				ProcessHandler handler = new ProcessHandler(this, getExecutable());

				handler.directory(getProject().getBaseDir());

				if (getTarget() != null)
				{
					handler.parameter("-t").parameter(getTarget().getName());
				}

				if (isAnnotate())
				{
					handler.parameter("--add-source");
				}

				if (isDebug())
				{
					handler.parameter("-g");
				}

				if ((isOptimize()) || (isInlining()) || (isRegisterVariables())) {
					StringBuilder builder = new StringBuilder("-O");
					
					if (isInlining()) {
						builder.append("i");
					}
					
					if (isRegisterVariables()) {
						builder.append("r");
					}
					
					handler.parameter(builder.toString());
				}
				
				if (isStaticLocals())
				{
					handler.parameter("-Cl");
				}

				handler.parameter("--create-dep").parameter(outputFile.withExtension("d").ensureDirectory());

				handler.parameter("-o").parameter(outputFile.ensureDirectory());
				handler.parameter(inputFile);

				log("Executing: " + handler);
				log("");

				int exitValue = handler.consume();

				log("");

				if (exitValue != 0)
				{
					outputFile.delete();

					throw new BuildException("Failed with exit value " + exitValue);
				}

				outputFile.setLastModified(Dependencies.load(getProject().getBaseDir(), dependencyFile)
						.getLastModified(outputFile));
			}
		}
	}

	private boolean isExecutionNecessary(File inputFile, File outputFile, File dependencyFile)
	{
		if (!outputFile.exists())
		{
			return true;
		}

		if (!dependencyFile.exists())
		{
			return true;
		}

		long lastModified = Dependencies.load(getProject().getBaseDir(), dependencyFile).getLastModified(outputFile);

		if (outputFile.lastModified() > lastModified)
		{
			log("\"" + outputFile.getAbsolutePath() + "\" got modified in the future");
		}
		else
		{
			return outputFile.lastModified() < lastModified;
		}

		return false;
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
