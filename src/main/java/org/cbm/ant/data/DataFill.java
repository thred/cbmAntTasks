package org.cbm.ant.data;

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.Util;

public class DataFill implements DataCommand
{
    private String length;
    private String value;

    public DataFill()
    {
        super();
    }

    public int getLength()
    {
        return Util.parseHex(length);
    }

    public void setLength(String length)
    {
        this.length = length;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * @see org.cbm.ant.data.DataCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return !exists;
    }

    /**
     * @see org.cbm.ant.data.DataCommand#execute(DataWriter)
     */
    @Override
    public void execute(DataWriter writer) throws BuildException, IOException
    {
        int v = Util.parseHex(value);

        for (int i = 0; i < getLength(); i += 1)
        {
            writer.writeByte(v);
        }
    }
}
