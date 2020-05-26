package org.cbm.ant.cc65;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.cbm.ant.util.AntFile;
import org.cbm.ant.util.ProcessConsumer;
import org.cbm.ant.util.ProcessHandler;

public class LD65 extends AbstractCC65Task implements ProcessConsumer
{

    private static final Map<String, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put("Linux.*", "bin/ld65");
        EXECUTABLES.put("Windows.*", "bin/ld65.exe");
    }

    private final Collection<FileSet> files;
    private final Collection<Library> libraries;

    private File outputFile;
    private File configFile;
    private File labelFile;
    private File mapFile;

    public LD65()
    {
        super();

        files = new ArrayList<>();
        libraries = new ArrayList<>();
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

    public File getLabelFile()
    {
        return labelFile;
    }

    public void setLabelFile(File labelFile)
    {
        this.labelFile = labelFile;
    }

    public File getMapFile()
    {
        return mapFile;
    }

    public void setMapFile(File mapFile)
    {
        this.mapFile = mapFile;
    }

    public void addFiles(FileSet files)
    {
        this.files.add(files);
    }

    public void addLibrary(Library library)
    {
        libraries.add(library);
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

        long lastModified = lastModified();

        if (outputFile.lastModified() > lastModified)
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

        if (outputFile != null)
        {
            handler.parameter("-o").parameter(outputFile);
        }

        if (getTarget() != null)
        {
            if (configFile == null)
            {
                handler.parameter("-t").parameter(getTarget().getName());
            }

            libraries.addAll(Arrays.asList(getTarget().getLibraries()));
        }

        if (labelFile != null)
        {
            handler.parameter("-Ln").parameter(labelFile);
        }

        if (mapFile != null)
        {
            handler.parameter("-m").parameter(mapFile);
        }

        if (configFile != null)
        {
            handler.parameter("-C").parameter(configFile);
        }

        for (File inputFile : inputFiles)
        {
            handler.parameter(inputFile);
        }

        for (Library library : libraries)
        {
            handler.parameter(library.getName());
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
