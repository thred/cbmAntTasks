package org.cbm.ant.viceteam;

import org.cbm.ant.AbstractCBMProcessHandlerTask;

public abstract class AbstractViceTask extends AbstractCBMProcessHandlerTask
{
    public AbstractViceTask()
    {
        super();
    }

    @Override
    protected String getHomePropertyKeyPrefix()
    {
        return "vice";
    }
}
