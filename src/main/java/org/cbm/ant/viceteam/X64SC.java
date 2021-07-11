package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

import org.cbm.ant.util.OS;

public class X64SC extends AbstractViceLaunchTask
{

    private static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "x64sc");
        EXECUTABLES.put(OS.WINDOWS, "x64sc.exe");
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "x64sc";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }
}
