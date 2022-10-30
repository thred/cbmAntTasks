package org.cbm.ant.cc1541;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.disk.AbstractDiskFormatCommand;
import org.cbm.ant.util.ProcessHandler;
import org.cbm.ant.util.Util;

public class CC1541Format extends AbstractDiskFormatCommand<CC1541Format>
{
    public CC1541Format()
    {
        super();
    }

    @Override
    protected void prepareHandler(ProcessHandler handler, File image)
    {
        if (image.exists())
        {
            image.delete();
        }

        handler.parameter("-n").parameter(Util.escape(getDiskname()));

        String id = getId();

        if (id.length() == 2)
        {
            id += "\u00a02a";
        }
        else if (id.length() != 5)
        {
            throw new BuildException("Invalid id: " + id);
        }

        handler.parameter("-i").parameter(Util.escape(id));

        handler.parameter(Util.escape(image.getAbsolutePath()));
    }
}
