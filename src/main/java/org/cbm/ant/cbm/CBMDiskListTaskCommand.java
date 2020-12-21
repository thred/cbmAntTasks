package org.cbm.ant.cbm;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CBMDiskOperator;

public class CBMDiskListTaskCommand extends AbstractCBMDiskTaskCommand
{

    private boolean listDeleted;
    private boolean listKeys;

    public CBMDiskListTaskCommand()
    {
        super();
    }

    public boolean isListDeleted()
    {
        return listDeleted;
    }

    public void setListDeleted(boolean listDeleted)
    {
        this.listDeleted = listDeleted;
    }

    public boolean isListKeys()
    {
        return listKeys;
    }

    public void setListKeys(boolean listKeys)
    {
        this.listKeys = listKeys;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CBMDiskTaskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
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
        operator.getDir().list(System.out, listKeys, listDeleted);

        return null;
    }
}
