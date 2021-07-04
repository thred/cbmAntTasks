package org.cbm.ant.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public ProcessHandler executable(File home, Map<String, String> executablesByOs)
    {
        String os = System.getProperty("os.name");

        for (Map.Entry<String, String> entry : executablesByOs.entrySet())
        {
            if (os.matches(entry.getKey()))
            {
                if (home != null)
                {
                    File file = new File(home, entry.getValue());

                    if (!file.canExecute())
                    {
                        File binFile = new File(new File(home, "bin"), entry.getValue());

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

                return executable(entry.getValue());
            }
        }

        throw new BuildException("No executable defined for OS: " + os);
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
            parameters.add(executable);
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
