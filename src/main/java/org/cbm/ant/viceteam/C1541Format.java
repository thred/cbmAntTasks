package org.cbm.ant.viceteam;

import java.io.File;

import org.cbm.ant.disk.AbstractDiskFormatCommand;
import org.cbm.ant.util.ProcessHandler;
import org.cbm.ant.util.Util;

public class C1541Format extends AbstractDiskFormatCommand<C1541Format>
{
    public C1541Format()
    {
        super();
    }

    @Override
    protected void prepareHandler(ProcessHandler handler, File image)
    {
        handler.parameter("-format");
        handler.parameter(Util.escape(getDiskname() + "," + getId()));
        handler.parameter(getType());
        handler.parameter(Util.escape(image.getAbsolutePath()));
    }
}
