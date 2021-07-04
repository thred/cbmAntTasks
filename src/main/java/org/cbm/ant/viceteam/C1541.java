package org.cbm.ant.viceteam;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessConsumer;
import org.cbm.ant.util.ProcessHandler;

public class C1541 extends AbstractViceTask implements ProcessConsumer
{
    private static final Map<String, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put("Linux.*", "c1541");
        EXECUTABLES.put("Windows.*", "c1541.exe");
    }

    private final List<C1541Command> commands;

    private File image;
    private boolean failOnError = true;

    public C1541()
    {
        super();

        commands = new ArrayList<>();
    }

    /**
     * @see org.cbm.ant.viceteam.AbstractViceTask#getExecutables()
     */
    @Override
    public Map<String, String> getExecutables()
    {
        return EXECUTABLES;
    }

    /**
     * Adds an format command
     *
     * @param format the command
     */
    public void addFormat(C1541Format format)
    {
        commands.add(format);
    }

    /**
     * Adds a read command
     *
     * @param read the command
     */
    public void addRead(C1541Read read)
    {
        commands.add(read);
    }

    /**
     * Adds a write command
     *
     * @param write the command
     */
    public void addWrite(C1541Write write)
    {
        commands.add(write);
    }

    /**
     * Adds a list command
     *
     * @param list the list
     */
    public void addList(C1541List list)
    {
        commands.add(list);
    }

    /**
     * Returns the image file
     *
     * @return the image file
     */
    public File getImage()
    {
        return image;
    }

    /**
     * Sets the image file
     *
     * @param image the image file
     */
    public void setImage(File image)
    {
        this.image = image;
    }

    public boolean isFailOnError()
    {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError)
    {
        this.failOnError = failOnError;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException
    {
        File image = getImage();

        if (!isExecutionNecessary(image))
        {
            return;
        }

        for (C1541Command command : commands)
        {
            ProcessHandler handler = createProcessHandler();

            try
            {
                log("Result: " + command.execute(this, handler, image));
            }
            catch (BuildException e)
            {
                if (!command.isFailOnError())
                {
                    log("Ignoring error: " + e.getMessage());
                }
                else
                {
                    throw e;
                }
            }
        }
    }

    private boolean isExecutionNecessary(File image)
    {
        long lastModified = -1;
        boolean exists = image.exists();

        if (!exists)
        {
            return true;
        }

        if (exists)
        {
            lastModified = image.lastModified();
        }

        for (C1541Command command : commands)
        {
            try
            {
                if (command.isExecutionNecessary(lastModified, exists))
                {
                    return true;
                }
            }
            catch (BuildException e)
            {
                if (!command.isFailOnError())
                {
                    log("Ignoring error: " + e.getMessage());
                }
                else
                {
                    throw e;
                }
            }
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
