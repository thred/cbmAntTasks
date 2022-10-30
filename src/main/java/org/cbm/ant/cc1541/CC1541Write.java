package org.cbm.ant.cc1541;

import java.io.File;

import org.cbm.ant.disk.AbstractDiskWriteCommand;
import org.cbm.ant.util.ProcessHandler;
import org.cbm.ant.util.Util;

public class CC1541Write extends AbstractDiskWriteCommand<CC1541Write>
{
    public CC1541Write()
    {
        super();
    }

    @Override
    protected void prepareHandler(ProcessHandler handler, File image)
    {
        handler.parameter("-f").parameter(Util.escape(getDestination()));
        handler.parameter("-w").parameter(Util.escape(getSource().getAbsolutePath()));

        handler.parameter(Util.escape(image.getAbsolutePath()));
    }
}
