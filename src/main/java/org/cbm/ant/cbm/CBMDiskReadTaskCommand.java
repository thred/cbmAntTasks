package org.cbm.ant.cbm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CBMDiskOperator;
import org.cbm.ant.util.IOUtils;

public class CBMDiskReadTaskCommand extends AbstractCBMDiskTaskCommand
{

    private String source;
    private File destination;

    public CBMDiskReadTaskCommand()
    {
        super();
    }

    public String getSource() throws BuildException
    {
        if (source == null || source.trim().length() == 0)
        {
            throw new BuildException("Destination missing");
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
     * @see org.cbm.ant.cbm.CBMDiskTaskCommand#isExecutionNecessary(long, boolean)
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
     * @see org.cbm.ant.cbm.CBMDiskTaskCommand#execute(org.cbm.ant.cbm.CBMDiskTask,
     *      org.cbm.ant.cbm.disk.CBMDiskOperator)
     */
    @Override
    public Long execute(CBMDiskTask task, CBMDiskOperator operator) throws BuildException
    {
        task.log(String.format("Reading \"%s\" from disk image...", source));

        byte[] bytes;

        try (InputStream in = operator.open(getSource()))
        {
            bytes = IOUtils.readFully(in);
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
