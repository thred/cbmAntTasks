package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

public class X64 extends AbstractViceLaunchTask
{

    private static final Map<String, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put("Linux.*", "x64");
        EXECUTABLES.put("Windows.*", "x64.exe");
    }

    /**
     * @see org.cbm.ant.viceteam.AbstractViceTask#getExecutables()
     */
    @Override
    public Map<String, String> getExecutables()
    {
        return EXECUTABLES;
    }

}
