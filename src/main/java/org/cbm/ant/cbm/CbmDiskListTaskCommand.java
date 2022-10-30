package org.cbm.ant.cbm;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CbmDisk;

public class CbmDiskListTaskCommand extends AbstractCbmDiskTaskCommand
{
    private boolean listDeleted;

    public CbmDiskListTaskCommand()
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

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#execute(org.cbm.ant.cbm.CbmDiskTask, CbmDisk)
     */
    @Override
    public Long execute(CbmDiskTask task, CbmDisk disk) throws BuildException
    {
        StringBuilder bob = new StringBuilder();

        disk.printDirectory(bob, listDeleted);

        System.out.println(bob.toString());

        return null;
    }
}
