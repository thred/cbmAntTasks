package org.cbm.ant.data;

import java.io.IOException;

import org.apache.tools.ant.BuildException;

public class DataText extends AbstractDataCommand
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
        writer.writeText(builder.toString());
    }
}
