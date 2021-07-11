package org.cbm.ant.pucrunch;

import java.util.HashMap;
import java.util.Map;

import org.cbm.ant.AbstractCBMProcessHandlerTask;
import org.cbm.ant.util.OS;

public abstract class AbstractPucrunchTask extends AbstractCBMProcessHandlerTask
{
    private static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "pucrunch");
        EXECUTABLES.put(OS.WINDOWS, "pucrunch.exe");
    }

    @Override
    protected String getHomePropertyKeyPrefix()
    {
        return "pucrunch";
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "pucrunch";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }
}
