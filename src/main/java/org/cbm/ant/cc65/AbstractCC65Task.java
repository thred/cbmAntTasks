package org.cbm.ant.cc65;

import java.util.Locale;

import org.cbm.ant.AbstractCBMProcessHandlerTask;

public abstract class AbstractCC65Task extends AbstractCBMProcessHandlerTask
{
    private Target target;

    public AbstractCC65Task()
    {
        super();
    }

    @Override
    protected String getHomePropertyKeyPrefix()
    {
        return "cc65";
    }

    public Target getTarget()
    {
        if (target == null)
        {
            return Target.C64;
        }

        return target;
    }

    public void setTarget(String target)
    {
        this.target = Target.valueOf(target.toUpperCase(Locale.getDefault()));
    }

    /**
     * @see org.cbm.ant.util.ProcessConsumer#processOutput(java.lang.String, boolean)
     */
    @Override
    public void processOutput(String output, boolean isError)
    {
        log(output);
    }
}
