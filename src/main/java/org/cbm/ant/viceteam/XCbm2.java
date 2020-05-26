package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

public class XCbm2 extends AbstractViceLaunchTask
{

    private static final Map<String, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put("Linux.*", "xcbm2");
        EXECUTABLES.put("Windows.*", "xcbm2.exe");
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
