package org.cbm.ant.cbm;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CbmDisk;

public class CbmDiskFormatTaskCommand extends AbstractCbmDiskTaskCommand
{

    private String diskName;
    private String id;

    public CbmDiskFormatTaskCommand()
    {
        super();
    }

    public String getDiskName() throws BuildException
    {
        if (diskName == null || diskName.trim().length() == 0)
        {
            throw new BuildException("Diskname is missing");
        }

        return diskName;
    }

    public void setDiskName(String diskName)
    {
        this.diskName = diskName;
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

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#execute(org.cbm.ant.cbm.CbmDiskTask, CbmDisk)
     */
    @Override
    public Long execute(CbmDiskTask task, CbmDisk disk) throws BuildException
    {
        task.log(String.format("Formatting disk ..."));

        disk.format(getDiskName(), getId());

        return System.currentTimeMillis();
    }

}
