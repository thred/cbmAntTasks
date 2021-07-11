package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

import org.cbm.ant.util.OS;

public class XVic extends AbstractViceLaunchTask
{

    private static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "xvic");
        EXECUTABLES.put(OS.WINDOWS, "xvic.exe");
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "xvic";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }
}
