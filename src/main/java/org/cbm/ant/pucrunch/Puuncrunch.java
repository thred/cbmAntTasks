package org.cbm.ant.pucrunch;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

/**
 * Task for pucrunch by Pasi Ojala. Task was developed and tested by using the version found at
 * https://github.com/mist64/pucrunch
 *
 * @author ham
 */
public class Puuncrunch extends AbstractPucrunchTask
{

    /** The source file */
    private File source;

    /** THe target file */
    private File target;

    public Puuncrunch()
    {
        super();
    }

    public File getSource()
    {
        if (target == null)
        {
            throw new BuildException("Source is missing");
        }

        return source;
    }

    public void setSource(File source)
    {
        this.source = source;
    }

    public File getTarget()
    {
        if (target == null)
        {
            throw new BuildException("Target is missing");
        }

        return target;
    }

    public void setTarget(File target)
    {
        this.target = target;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException
    {
        File source = getSource();

        if (!source.exists())
        {
            throw new BuildException("Source not available: " + source.getAbsolutePath());
        }

        File target = getTarget();

        if (target.exists() && !target.equals(source) && source.lastModified() == target.lastModified())
        {
            // not update necessary
            return;
        }

        ProcessHandler handler = createProcessHandler();

        handler.parameter("-u");

        if (getSource() != null)
        {
            handler.parameter(getSource().getPath());

            if (getTarget() != null)
            {
                handler.parameter(getTarget().getPath());
            }
        }

        log("Executing: " + handler);

        int exitValue = handler.consume();

        if (exitValue != 0)
        {
            throw new BuildException("Failed with exit value " + exitValue);
        }

        if (!source.equals(target))
        {
            target.setLastModified(source.lastModified());
        }
    }
}
