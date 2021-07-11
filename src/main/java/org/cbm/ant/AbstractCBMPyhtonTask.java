package org.cbm.ant;

import java.util.HashMap;
import java.util.Map;

import org.cbm.ant.util.OS;

public abstract class AbstractCBMPyhtonTask extends AbstractCBMProcessHandlerTask
{

    private static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "python");
        EXECUTABLES.put(OS.WINDOWS, "python.exe");
    }

    @Override
    protected String getHomePropertyKeyPrefix()
    {
        return "python";
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "python";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }
}
