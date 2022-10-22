package org.cbm.ant.cbm;

import java.io.FileNotFoundException;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CBMDiskOperator;

public class CBMDiskDeleteTaskCommand extends AbstractCBMDiskTaskCommand
{
    private String file;

    public CBMDiskDeleteTaskCommand()
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
        task.log(String.format("Deleting \"%s\" from disk image...", file));

        try
        {
            operator.delete(file);
        }
        catch (FileNotFoundException e)
        {
            throw new BuildException(String.format("Failed to delete file \"%s\"", file), e);
        }

        return null;
    }
}
