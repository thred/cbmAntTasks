package org.cbm.ant.viceteam;

import java.io.File;

import org.cbm.ant.disk.AbstractDiskDeleteCommand;
import org.cbm.ant.util.ProcessHandler;
import org.cbm.ant.util.Util;

public class C1541Delete extends AbstractDiskDeleteCommand<C1541Delete>
{
    public C1541Delete()
    {
        super();
    }

    @Override
    protected void prepareHandler(ProcessHandler handler, File image)
    {
        handler.parameter(Util.escape(image.getAbsolutePath()));
        handler.parameter("-delete");
        handler.parameter(Util.escape(getFile()));
    }
}
