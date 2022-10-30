package org.cbm.ant.disk;

public abstract class AbstractDiskCommand implements DiskCommand
{
    private boolean failOnError = true;

    public AbstractDiskCommand()
    {
        super();
    }

    @Override
    public boolean isFailOnError()
    {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError)
    {
        this.failOnError = failOnError;
    }
}
