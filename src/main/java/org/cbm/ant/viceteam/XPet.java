package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

import org.cbm.ant.util.OS;

public class XPet extends AbstractViceLaunchTask
{

    private static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "xpet");
        EXECUTABLES.put(OS.WINDOWS, "xpet.exe");
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "xpet";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }
}
