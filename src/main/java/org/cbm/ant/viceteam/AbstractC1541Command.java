package org.cbm.ant.viceteam;

public abstract class AbstractC1541Command implements C1541Command
{

    private boolean failOnError = true;

    public AbstractC1541Command()
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
