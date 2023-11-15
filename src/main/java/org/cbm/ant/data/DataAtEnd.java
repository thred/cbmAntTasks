package org.cbm.ant.data;

import java.io.IOException;

import org.apache.tools.ant.BuildException;

public class DataAtEnd implements DataCommand
{
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return false;
    }

    @Override
    public void execute(DataWriter writer) throws BuildException, IOException
    {
        writer.atEnd();
    }
}
