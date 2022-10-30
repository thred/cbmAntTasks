package org.cbm.ant.disk;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractDiskReadCommand<SELF extends AbstractDiskReadCommand<?>> extends AbstractDiskCommand
{
    private String source;
    private File destination;

    public AbstractDiskReadCommand()
    {
        super();
    }

    @SuppressWarnings("unchecked")
    public SELF self()
    {
        return (SELF) this;
    }

    public String getSource() throws BuildException
    {
        if (source == null || source.trim().length() == 0)
        {
            throw new BuildException("Source is missing");
        }

        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public SELF source(String source)
    {
        setSource(source);

        return self();
    }

    public File getDestination()
    {
        if (destination.isDirectory())
        {
            return new File(destination, getSource());
        }

        return destination;
    }

    public void setDestination(File destination)
    {
        this.destination = destination;
    }

    public SELF destination(File destination)
    {
        setDestination(destination);

        return self();
    }

    /**
     * @see org.cbm.ant.disk.DiskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        File destination = getDestination();

        if (exists && destination.exists())
        {
            return lastModified > destination.lastModified();
        }

        return true;
    }

    /**
     * @see org.cbm.ant.disk.DiskCommand#execute(AbstractDiskTask, org.cbm.ant.util.ProcessHandler, java.io.File)
     */
    @Override
    public int execute(AbstractDiskTask<?> task, ProcessHandler handler, File image) throws BuildException
    {
        int result;

        if (!image.exists())
        {
            throw new BuildException("Image does not exist: " + image.getAbsolutePath());
        }

        prepareHandler(handler, image);

        task.log("Executing: " + handler.toString());

        result = handler.consume();

        if (result != 0)
        {
            throw new BuildException("Failed with exit value " + result);
        }

        long lastModified = getDestination().lastModified();
        long millis = image.lastModified();

        if (millis > lastModified)
        {
            image.setLastModified(millis);
        }

        return result;
    }

    protected abstract void prepareHandler(ProcessHandler handler, File image);
}