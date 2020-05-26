package org.cbm.ant.cc65;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;
import org.cbm.ant.util.AntFile;
import org.cbm.ant.util.ProcessConsumer;
import org.cbm.ant.util.ProcessHandler;

public class CA65 extends AbstractCC65Task implements ProcessConsumer
{

    private static final Map<String, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put("Linux.*", "bin/ca65");
        EXECUTABLES.put("Windows.*", "bin/ca65.exe");
    }

    private final Collection<FileSet> files;
    private final Collection<DirSet> includes;

    private File outputDir;
    private boolean debug = false;
    private boolean listing = false;

    public CA65()
    {
        super();

        files = new ArrayList<>();
        includes = new ArrayList<>();
    }

    /**
     * @see org.cbm.ant.cc65.AbstractCC65Task#getExecutables()
     */
    @Override
    public Map<String, String> getExecutables()
    {
        return EXECUTABLES;
    }

    public File getOutputDir()
    {
        return outputDir;
    }

    public void setOutputDir(File outputDir)
    {
        this.outputDir = outputDir;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public boolean isListing()
    {
        return listing;
    }

    public void setListing(boolean listing)
    {
        this.listing = listing;
    }

    public void addFiles(FileSet files)
    {
        this.files.add(files);
    }

    public void addIncludes(DirSet includes)
    {
        this.includes.add(includes);
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException
    {
        for (AntFile inputFile : collect(files))
        {
            AntFile outputFile = null;

            if (outputDir != null)
            {
                outputFile =
                    AntFile.create(getProject().getBaseDir(), outputDir, inputFile.getFilename()).withExtension("o");
            }
            else
            {
                outputFile = inputFile.withExtension("o");
            }

            if (isExecutionNecessary(inputFile, outputFile))
            {
                ProcessHandler handler = new ProcessHandler(this, getExecutable());

                handler.directory(getProject().getBaseDir());

                if (getTarget() != null)
                {
                    handler.parameter("-t").parameter(getTarget().getName());
                }

                if (isDebug())
                {
                    handler.parameter("-g");
                }

                if (isListing())
                {
                    handler.parameter("-l").parameter(outputFile.withExtension("l"));
                }

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

                outputFile.setLastModified(inputFile.lastModified());
            }
        }
    }

    private boolean isExecutionNecessary(File inputFile, File outputFile)
    {
        boolean result = !outputFile.exists();

        if (result)
        {
            return result;
        }

        if (outputFile.lastModified() > inputFile.lastModified())
        {
            log("\"" + outputFile.getAbsolutePath() + "\" got modified in the future");
        }
        else
        {
            return outputFile.lastModified() < inputFile.lastModified();
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
