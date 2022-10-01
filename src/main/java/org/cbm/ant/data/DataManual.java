package org.cbm.ant.data;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;

public class DataManual extends AbstractDataCommand
{
    private String values;

    public DataManual()
    {
        super();
    }

    public String getValues()
    {
        return values;
    }

    public void setValues(String values)
    {
        this.values = values;
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
        StringTokenizer tokenizer = new StringTokenizer(values);

        while (tokenizer.hasMoreTokens())
        {
            writer.writeByte(Integer.parseInt(tokenizer.nextToken()));
        }
    }
}
