package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

import org.cbm.ant.util.OS;

public class X64DTV extends AbstractViceLaunchTask
{

    private static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "x64dtv");
        EXECUTABLES.put(OS.WINDOWS, "x64dtv.exe");
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "x64dtv";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }
}
