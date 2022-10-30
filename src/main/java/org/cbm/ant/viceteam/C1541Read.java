package org.cbm.ant.viceteam;

import java.io.File;

import org.cbm.ant.disk.AbstractDiskReadCommand;
import org.cbm.ant.util.ProcessHandler;
import org.cbm.ant.util.Util;

public class C1541Read extends AbstractDiskReadCommand<C1541Read>
{
    public C1541Read()
    {
        super();
    }

    @Override
    protected void prepareHandler(ProcessHandler handler, File image)
    {
        handler.parameter(Util.escape(image.getAbsolutePath()));
        handler.parameter("-read");
        handler.parameter(Util.escape(getSource()));
        handler.parameter(Util.escape(getDestination().getAbsolutePath()));
    }
}
