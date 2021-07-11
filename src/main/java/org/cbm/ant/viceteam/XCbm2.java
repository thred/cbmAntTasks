package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

import org.cbm.ant.util.OS;

public class XCbm2 extends AbstractViceLaunchTask
{

    private static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "xcbm2");
        EXECUTABLES.put(OS.WINDOWS, "xcbm2.exe");
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "xcbm2";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }
}
