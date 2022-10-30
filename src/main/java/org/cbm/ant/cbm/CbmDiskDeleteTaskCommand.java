package org.cbm.ant.cbm;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CbmDisk;
import org.cbm.ant.cbm.disk.CbmIOException;

public class CbmDiskDeleteTaskCommand extends AbstractCbmDiskTaskCommand
{
    private String file;

    public CbmDiskDeleteTaskCommand()
    {
        super();
    }

    public String getFile()
    {
        if (file == null || file.trim().length() == 0)
        {
            throw new BuildException("File is missing");
        }

        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return exists;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#execute(org.cbm.ant.cbm.CbmDiskTask, CbmDisk)
     */
    @Override
    public Long execute(CbmDiskTask task, CbmDisk disk) throws BuildException
    {
        task.log(String.format("Deleting \"%s\" from disk ...", file));

        try
        {
            disk.deleteFiles(file, false);
        }
        catch (CbmIOException e)
        {
            throw new BuildException("Failed to delete files", e);
        }

        return null;
    }
}
