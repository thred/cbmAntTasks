package org.cbm.ant.cbm;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CBMDiskOperator;

public class CBMDiskFormatTaskCommand extends AbstractCBMDiskTaskCommand
{

    private String diskName;
    private String id;
    private CBMDiskType type;

    public CBMDiskFormatTaskCommand()
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

    public CBMDiskType getType()
    {
        if (type == null)
        {
            return CBMDiskType.D64;
        }

        return type;
    }

    public void setType(CBMDiskType type)
    {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CBMDiskTaskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return false;
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
        task.log(String.format("Formatting disk image..."));

        operator.format(getType().getFormat(), getDiskName(), getId());

        return System.currentTimeMillis();
    }

}
