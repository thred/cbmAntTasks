package org.cbm.ant.viceteam;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public class C1541List extends AbstractC1541Command
{

    public C1541List()
    {
        super();
    }

    /**
     * @see org.cbm.ant.viceteam.C1541Command#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return true;
    }

    /**
     * @see org.cbm.ant.viceteam.C1541Command#execute(C1541, org.cbm.ant.util.ProcessHandler, java.io.File)
     */
    @Override
    public int execute(C1541 task, ProcessHandler handler, File image) throws BuildException
    {
        int result;

        if (!image.exists())
        {
            throw new BuildException("Image does not exist: " + image.getAbsolutePath());
        }

        handler.parameter(ViceUtil.escape(image.getAbsolutePath()));
        handler.parameter("-list");

        task.log("Executing: " + handler.toString());

        result = handler.consume();

        if (result != 0)
        {
            throw new BuildException("Failed with exit value " + result);
        }

        return result;
    }

}
