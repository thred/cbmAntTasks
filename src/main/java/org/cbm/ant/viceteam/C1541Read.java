package org.cbm.ant.viceteam;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public class C1541Read extends AbstractC1541Command
{

    private String source;
    private File destination;

    public C1541Read()
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
     * @see org.cbm.ant.viceteam.C1541Command#isExecutionNecessary(long, boolean)
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
     * @see org.cbm.ant.viceteam.C1541Command#execute(C1541, org.cbm.ant.util.ProcessHandler, java.io.File)
     */
    @Override
    public int execute(C1541 task, ProcessHandler handler, File image) throws BuildException
    {
        int result;

        if (!image.exists())
        {
            throw new BuildException("Image does not exist: " + image.getAbsolutePath());
        }

        handler.parameter(ViceUtil.escape(image.getAbsolutePath()));
        handler.parameter("-read");
        handler.parameter(ViceUtil.escape(getSource()));
        handler.parameter(ViceUtil.escape(getDestination().getAbsolutePath()));

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

}
