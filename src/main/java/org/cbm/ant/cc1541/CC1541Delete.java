package org.cbm.ant.cc1541;

import java.io.File;

import org.cbm.ant.disk.AbstractDiskDeleteCommand;
import org.cbm.ant.util.ProcessHandler;
import org.cbm.ant.util.Util;

public class CC1541Delete extends AbstractDiskDeleteCommand<CC1541Delete>
{
    public CC1541Delete()
    {
        super();
    }

    @Override
    protected void prepareHandler(ProcessHandler handler, File image)
    {
        handler
            .parameter("-T")
            .parameter("DEL")
            .parameter("-O")
            .parameter("-f")
            .parameter(Util.escape(getFile()))
            .parameter("-L");

        handler.parameter(Util.escape(image.getAbsolutePath()));
    }
}
