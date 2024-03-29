package org.cbm.ant.viceteam;

import java.io.File;

import org.cbm.ant.disk.AbstractDiskWriteCommand;
import org.cbm.ant.util.ProcessHandler;
import org.cbm.ant.util.Util;

public class C1541Write extends AbstractDiskWriteCommand<C1541Write>
{
    public C1541Write()
    {
        super();
    }

    @Override
    protected void prepareHandler(ProcessHandler handler, File image)
    {
        handler.parameter(Util.escape(image.getAbsolutePath()));
        handler.parameter("-write");
        handler.parameter(Util.escape(getSource().getAbsolutePath()));
        handler.parameter(Util.escape(getDestination()));
    }
}
