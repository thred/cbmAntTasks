package org.cbm.ant.data;

import java.io.IOException;

import org.apache.tools.ant.BuildException;

public abstract class AbstractDataCommand
{
    private DataFormat format;

    public DataFormat getFormat()
    {
        return format;
    }

    public void setFormat(DataFormat format)
    {
        this.format = format;
    }

    abstract boolean isExecutionNecessary(long lastModified, boolean exists);

    abstract void execute(Data task, DataWriter writer) throws BuildException, IOException;
}
