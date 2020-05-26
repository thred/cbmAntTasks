package org.cbm.ant.viceteam;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public class C1541Format extends AbstractC1541Command
{

    private String diskname;
    private String id;
    private String type;

    public C1541Format()
    {
        super();
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

    public String getId() throws BuildException
    {
        if (id == null || id.trim().length() == 0)
        {
            throw new BuildException("Id is missing");
        }

        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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

    /**
     * @see org.cbm.ant.viceteam.C1541Command#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return !exists;
    }

    /**
     * @see org.cbm.ant.viceteam.C1541Command#execute(C1541, org.cbm.ant.util.ProcessHandler, java.io.File)
     */
    @Override
    public int execute(C1541 task, ProcessHandler handler, File image) throws BuildException
    {
        int result;

        handler.parameter("-format");
        handler.parameter(ViceUtil.escape(getDiskname() + "," + getId()));
        handler.parameter(getType());
        handler.parameter(ViceUtil.escape(image.getAbsolutePath()));

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
}
