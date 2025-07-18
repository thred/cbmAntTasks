package org.cbm.ant.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;

public class ProcessHandler
{

    private final ProcessConsumer consumer;
    private final List<String> parameters;

    private File directory;
    private String executable;

    public ProcessHandler(ProcessConsumer consumer)
    {
        super();

        this.consumer = consumer;

        parameters = new ArrayList<>();
    }

    public ProcessHandler directory(File directory)
    {
        this.directory = directory;

        return this;
    }

    public ProcessHandler executable(String executable)
    {
        this.executable = executable;

        return this;
    }

    public ProcessHandler executable(File home, String executable)
    {
        if (home != null)
        {
            File file = new File(home, executable);

            if (!file.canExecute())
            {
                File binFile = new File(new File(home, "bin"), executable);

                if (!binFile.canExecute())
                {
                    throw new BuildException("Invalid executable, nether \""
                        + file.getAbsolutePath()
                        + "\" nor \""
                        + binFile.getAbsolutePath()
                        + "\" can be found");
                }

                file = binFile;
            }

            return executable(file.getAbsolutePath());
        }

        return executable(executable);
    }

    public ProcessHandler parameter(String parameter, String value)
    {
        parameters.add(parameter + "=" + value);

        return this;
    }

    public ProcessHandler parameter(String parameter, File file)
    {
        parameters.add(parameter + "=" + file.getPath().replace('\\', '/'));

        return this;
    }

    public ProcessHandler parameter(String parameter)
    {
        parameters.add(parameter);

        return this;
    }

    public ProcessHandler parameter(File parameter)
    {
        return parameter(parameter.getPath().replace('\\', '/'));
    }

    public int consume() throws BuildException
    {
        List<String> parameters = new ArrayList<>();

        if (executable != null)
        {
            executable = executable.trim();
            if (executable.startsWith("\"") && executable.endsWith("\""))
            {
                parameters.add(executable);
            }
            else if (executable.startsWith("\'") && executable.endsWith("\'"))
            {
                parameters.add(executable);
            }
            else
            {
                Collections.addAll(parameters, executable.split(" +"));
            }
        }

        parameters.addAll(this.parameters);

        ProcessBuilder processBuilder = new ProcessBuilder(parameters);

        if (directory != null)
        {
            processBuilder.directory(directory);
        }

        Process process;

        try
        {
            process = processBuilder.start();
        }
        catch (IOException e)
        {
            throw new BuildException("Error starting process", e);
        }

        ProcessInputHandler inputHandler = new ProcessInputHandler(consumer, false, process.getInputStream());
        ProcessInputHandler errorHandler = new ProcessInputHandler(consumer, true, process.getErrorStream());

        inputHandler.start();
        errorHandler.start();

        try
        {
            process.waitFor();

            inputHandler.waitFor();
            errorHandler.waitFor();
        }
        catch (InterruptedException e)
        {
            throw new BuildException("Got interrupted", e);
        }

        return process.exitValue();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        Iterator<String> it = parameters.iterator();

        if (executable != null)
        {
            builder.append(executable);

            if (it.hasNext())
            {
                builder.append(" ");
            }
        }

        while (it.hasNext())
        {
            builder.append(it.next());

            if (it.hasNext())
            {
                builder.append(" ");
            }
        }

        return builder.toString();
    }

    //	private static String[] getEnvironment()
    //	{
    //		List<String> result = new ArrayList<String>();
    //
    //		for (Map.Entry<String, String> entry : System.getenv().entrySet())
    //		{
    //			result.add(entry.getKey() + "=" + entry.getValue());
    //		}
    //
    //		return result.toArray(new String[result.size()]);
    //	}
}
