package org.cbm.ant.cbm;

public abstract class AbstractCbmDiskTaskCommand implements CbmDiskTaskCommand
{
    private Boolean failOnError = null;

    public AbstractCbmDiskTaskCommand()
    {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#isFailOnError()
     */
    @Override
    public boolean isFailOnError()
    {
        return failOnError == null || failOnError;
    }

    public void setFailOnError(boolean failOnError)
    {
        this.failOnError = failOnError;
    }
}
