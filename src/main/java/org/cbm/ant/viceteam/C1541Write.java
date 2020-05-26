package org.cbm.ant.viceteam;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public class C1541Write extends AbstractC1541Command
{

    private File source;
    private String destination;

    public C1541Write()
    {
        super();
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

    /**
     * @see org.cbm.ant.viceteam.C1541Command#isExecutionNecessary(long, boolean)
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
        handler.parameter("-write");
        handler.parameter(ViceUtil.escape(getSource().getAbsolutePath()));
        handler.parameter(ViceUtil.escape(getDestination()));

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

}
