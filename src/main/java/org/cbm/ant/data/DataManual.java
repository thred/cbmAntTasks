package org.cbm.ant.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;

public class DataManual implements DataCommand
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
     * @see org.cbm.ant.data.DataCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return !exists;
    }

    /**
     * @see org.cbm.ant.data.DataCommand#execute(Data, java.io.OutputStream)
     */
    @Override
    public void execute(Data task, OutputStream out) throws BuildException, IOException
    {
        task.log("Adding manual data");

        StringTokenizer tokenizer = new StringTokenizer(values);

        while (tokenizer.hasMoreTokens())
        {
            out.write(Integer.parseInt(tokenizer.nextToken()));
        }
    }

}
