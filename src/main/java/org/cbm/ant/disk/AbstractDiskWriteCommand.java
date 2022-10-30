package org.cbm.ant.disk;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractDiskWriteCommand<SELF extends AbstractDiskWriteCommand<?>> extends AbstractDiskCommand
{
    private File source;
    private String destination;
    private boolean overwrite;

    public AbstractDiskWriteCommand()
    {
        super();
    }

    @SuppressWarnings("unchecked")
    public SELF self()
    {
        return (SELF) this;
    }

    public File getSource() throws BuildException
    {
        if (source == null)
        {
            throw new BuildException("Missing source");
        }

        if (!source.exists())
        {
            throw new BuildException("Error reading source: " + source.getAbsolutePath());
        }

        return source;
    }

    public void setSource(File source)
    {
        this.source = source;
    }

    public SELF source(File source)
    {
        setSource(source);

        return self();
    }

    public String getDestination() throws BuildException
    {
        if (destination == null || destination.trim().length() == 0)
        {
            return getSource().getName();
        }

        return destination;
    }

    /**
     * Sets the destination file name. If not specified, the name of the source file will be used
     *
     * @param destination the destination file name
     */
    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }

    public SELF destination(String destination)
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
        File source = getSource();

        if (exists && source.exists())
        {
            return source.lastModified() > lastModified;
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

        task.log("Executing: " + handler.toString() + " (" + getSource().length() + " bytes)");

        result = handler.consume();

        if (result != 0)
        {
            throw new BuildException("Failed with exit value " + result);
        }

        long lastModified = image.lastModified();
        long millis = getSource().lastModified();

        if (millis > lastModified)
        {
            image.setLastModified(millis);
        }

        return result;
    }

    protected abstract void prepareHandler(ProcessHandler handler, File image);
}