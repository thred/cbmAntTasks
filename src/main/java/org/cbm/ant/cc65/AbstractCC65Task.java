package org.cbm.ant.cc65;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.cbm.ant.AbstractCBMProcessHandlerTask;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractCC65Task extends AbstractCBMProcessHandlerTask
{
    private Target target;

    private final List<Define> defines = new ArrayList<>();

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

    public void addDefine(Define define)
    {
        defines.add(define);
    }

    public List<Define> getDefines()
    {
        return defines;
    }

    protected void populateHandler(ProcessHandler handler)
    {
        if (getTarget() != null)
        {
            handler.parameter("-t").parameter(getTarget().getName());
        }

        for (Define define : getDefines())
        {
            StringBuilder assignment = new StringBuilder().append(define.getSymbol());

            if (define.getValue() != null)
            {
                assignment.append("=").append(define.getValue());
            }

            handler.parameter("-D").parameter(assignment.toString());
        }
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
