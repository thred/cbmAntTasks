package org.cbm.ant.data;

import java.io.IOException;

import org.apache.tools.ant.BuildException;

public class DataText implements DataCommand
{
    private final StringBuilder builder = new StringBuilder();

    public DataText()
    {
        super();
    }

    public void addText(String text)
    {
        builder.append(text);
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
        writer.writeText(builder.toString());
    }
}
