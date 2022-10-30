package org.cbm.ant.disk;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractDiskDeleteCommand<SELF extends AbstractDiskDeleteCommand<?>> extends AbstractDiskCommand
{

    private String file;

    public AbstractDiskDeleteCommand()
    {
        super();
    }

    @SuppressWarnings("unchecked")
    public SELF self()
    {
        return (SELF) this;
    }

    public String getFile() throws BuildException
    {
        if (file == null || file.trim().length() == 0)
        {
            throw new BuildException("File missing");
        }

        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    public SELF file(String file)
    {
        setFile(file);

        return self();
    }

    /**
     * @see org.cbm.ant.disk.DiskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return exists;
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