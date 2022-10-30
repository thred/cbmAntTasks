package org.cbm.ant.disk;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractDiskListCommand extends AbstractDiskCommand
{
    public AbstractDiskListCommand()
    {
        super();
    }

    /**
     * @see org.cbm.ant.disk.DiskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
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

        return result;
    }

    protected abstract void prepareHandler(ProcessHandler handler, File image);
}