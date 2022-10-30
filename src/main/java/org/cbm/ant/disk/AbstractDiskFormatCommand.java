package org.cbm.ant.disk;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractDiskFormatCommand<SELF extends AbstractDiskFormatCommand<?>> extends AbstractDiskCommand
{

    private String diskname;
    private String id;
    private String type;

    public AbstractDiskFormatCommand()
    {
        super();
    }

    @SuppressWarnings("unchecked")
    public SELF self()
    {
        return (SELF) this;
    }

    public String getDiskname() throws BuildException
    {
        if (diskname == null || diskname.trim().length() == 0)
        {
            throw new BuildException("Diskname is missing");
        }

        return diskname;
    }

    public void setDiskname(String diskname)
    {
        this.diskname = diskname;
    }

    public SELF diskname(String diskname)
    {
        setDiskname(diskname);

        return self();
    }

    public String getId() throws BuildException
    {
        if (id == null || id.trim().length() == 0)
        {
            return "00";
        }

        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public SELF id(String id)
    {
        setId(id);

        return self();
    }

    public String getType()
    {
        if (type == null || type.trim().length() == 0)
        {
            return "d64";
        }

        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public SELF type(String type)
    {
        setType(type);

        return self();
    }

    /**
     * @see org.cbm.ant.disk.DiskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return !exists;
    }

    /**
     * @see org.cbm.ant.disk.DiskCommand#execute(AbstractDiskTask, org.cbm.ant.util.ProcessHandler, java.io.File)
     */
    @Override
    public int execute(AbstractDiskTask<?> task, ProcessHandler handler, File image) throws BuildException
    {
        int result;

        prepareHandler(handler, image);

        task.log("Executing: " + handler.toString());

        result = handler.consume();

        if (result != 0)
        {
            throw new BuildException("Failed with exit value " + result);
        }

        if (image.exists())
        {
            long lastModified = image.lastModified();
            long millis = System.currentTimeMillis();

            if (millis > lastModified)
            {
                image.setLastModified(millis);
            }
        }

        return result;
    }

    protected abstract void prepareHandler(ProcessHandler handler, File image);
}