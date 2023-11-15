package org.cbm.ant.data;

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.Util;

public class DataAt implements DataCommand
{
    private int offset;

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(String offset)
    {
        this.offset = Util.parseHex(offset);
    }

    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return false;
    }

    @Override
    public void execute(DataWriter writer) throws BuildException, IOException
    {
        if (!writer.isRandomAccessSupported())
        {
            throw new BuildException("Current writes does not support random access");
        }

        writer.at(offset);
    }
}
