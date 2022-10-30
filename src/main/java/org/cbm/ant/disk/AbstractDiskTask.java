package org.cbm.ant.disk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.AbstractCBMProcessHandlerTask;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractDiskTask<SELF extends AbstractDiskTask<?>> extends AbstractCBMProcessHandlerTask
{
    protected final List<DiskCommand> commands = new ArrayList<>();

    private File image;
    private boolean failOnError = true;

    public AbstractDiskTask()
    {
        super();
    }

    @SuppressWarnings("unchecked")
    public SELF self()
    {
        return (SELF) this;
    }

    protected void addFormat(AbstractDiskFormatCommand<?> format)
    {
        commands.add(format);
    }

    protected void addRead(AbstractDiskReadCommand<?> read)
    {
        commands.add(read);
    }

    protected void addDelete(AbstractDiskDeleteCommand<?> delete)
    {
        commands.add(delete);
    }

    protected void addWrite(AbstractDiskWriteCommand<?> write)
    {
        commands.add(write);
    }

    protected void addList(AbstractDiskListCommand list)
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
        if (image == null)
        {
            throw new BuildException("Image is missing");
        }

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

    public SELF image(File image)
    {
        setImage(image);

        return self();
    }

    public boolean isFailOnError()
    {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError)
    {
        this.failOnError = failOnError;
    }

    public SELF failOnError(boolean failOnError)
    {
        setFailOnError(failOnError);

        return self();
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

        for (DiskCommand command : commands)
        {
            ProcessHandler handler = createProcessHandler();

            try
            {
                log("Result: " + command.execute(this, handler, image));
            }
            catch (BuildException e)
            {
                if (command.isFailOnError())
                {
                    throw e;
                }
                log("Ignoring error: " + e.getMessage());
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

        for (DiskCommand command : commands)
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
                if (command.isFailOnError())
                {
                    throw e;
                }
                log("Ignoring error: " + e.getMessage());
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