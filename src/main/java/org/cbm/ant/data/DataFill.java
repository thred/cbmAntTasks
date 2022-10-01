package org.cbm.ant.data;

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.Util;

public class DataFill extends AbstractDataCommand
{
    private int length;
    private String value;

    public DataFill()
    {
        super();
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
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
     * @see org.cbm.ant.data.AbstractDataCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return !exists;
    }

    /**
     * @see org.cbm.ant.data.AbstractDataCommand#execute(Data, DataWriter)
     */
    @Override
    public void execute(Data task, DataWriter writer) throws BuildException, IOException
    {
        int v = Util.parseHex(value);

        for (int i = 0; i < length; i += 1)
        {
            writer.writeByte(v);
        }
    }
}
