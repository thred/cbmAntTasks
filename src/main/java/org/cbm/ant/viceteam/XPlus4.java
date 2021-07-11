package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

import org.cbm.ant.util.OS;

public class XPlus4 extends AbstractViceLaunchTask
{

    private static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "xplus4");
        EXECUTABLES.put(OS.WINDOWS, "xplus4.exe");
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "xplus4";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }
}
