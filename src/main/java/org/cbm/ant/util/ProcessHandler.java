package org.cbm.ant.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;

public class ProcessHandler
{

    private final ProcessConsumer consumer;
    private final List<String> parameters;

    private File directory;

    private ProcessHandler(ProcessConsumer consumer)
    {
        super();

        this.consumer = consumer;

        parameters = new ArrayList<>();
    }

    public ProcessHandler(ProcessConsumer consumer, String command)
    {
        this(consumer);

        parameter(command);
    }

    public ProcessHandler(ProcessConsumer consumer, File command)
    {
        this(consumer);

        parameter(command);
    }

    public ProcessHandler directory(File directory)
    {
        this.directory = directory;

        return this;
    }

    public ProcessHandler parameter(String parameter)
    {
        parameters.add(parameter);

        return this;
    }

    public ProcessHandler parameter(File parameter)
    {
        return parameter("\"" + parameter.getPath().replace('\\', '/') + "\"");
    }

    public int consume() throws BuildException
    {
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
