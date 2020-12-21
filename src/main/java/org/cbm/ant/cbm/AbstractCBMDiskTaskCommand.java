package org.cbm.ant.cbm;

public abstract class AbstractCBMDiskTaskCommand implements CBMDiskTaskCommand
{

    private Boolean failOnError = null;

    public AbstractCBMDiskTaskCommand()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CBMDiskTaskCommand#isFailOnError()
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
