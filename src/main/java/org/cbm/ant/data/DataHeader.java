package org.cbm.ant.data;

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.Util;

public class DataHeader extends AbstractDataCommand
{
    private byte[] header;

    public DataHeader()
    {
        super();
    }

    public byte[] getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        int value = Util.parseHex(header);

        if (value < 0x0000 || value > 0xffff)
        {
            throw new BuildException("Invalid header: " + header);
        }

        this.header = new byte[]{(byte) (value % 0x0100), (byte) (value / 0x0100)};
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
        writer.writeBytes(header);
    }
}
