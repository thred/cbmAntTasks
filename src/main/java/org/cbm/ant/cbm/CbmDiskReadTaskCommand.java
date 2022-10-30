package org.cbm.ant.cbm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CbmDisk;

public class CbmDiskReadTaskCommand extends AbstractCbmDiskTaskCommand
{
    private String source;
    private File destination;

    public CbmDiskReadTaskCommand()
    {
        super();
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

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#isExecutionNecessary(long, boolean)
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
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#execute(org.cbm.ant.cbm.CbmDiskTask, CbmDisk)
     */
    @Override
    public Long execute(CbmDiskTask task, CbmDisk disk) throws BuildException
    {
        task.log(String.format("Reading \"%s\" from disk ...", source));

        byte[] bytes;

        try
        {
            bytes = disk.readFileFully(getSource());
        }
        catch (IOException e)
        {
            throw new BuildException(String.format("Failed to read file \"%s\"", getSource()), e);
        }

        try (FileOutputStream out = new FileOutputStream(getDestination()))
        {
            out.write(bytes);
        }
        catch (IOException e)
        {
            throw new BuildException(String.format("Failed to create file \"%s\"", getDestination()), e);
        }

        long lastModified = getDestination().lastModified();
        long millis = task.getImage().lastModified();

        if (millis > lastModified)
        {
            task.getImage().setLastModified(millis);
        }

        return null;
    }
}
