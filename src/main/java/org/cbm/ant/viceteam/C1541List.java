package org.cbm.ant.viceteam;

import java.io.File;

import org.cbm.ant.disk.AbstractDiskListCommand;
import org.cbm.ant.util.ProcessHandler;
import org.cbm.ant.util.Util;

public class C1541List extends AbstractDiskListCommand
{
    public C1541List()
    {
        super();
    }

    protected void prepareHandler(ProcessHandler handler, File image)
    {
        handler.parameter(Util.escape(image.getAbsolutePath()));
        handler.parameter("-list");
    }
}
