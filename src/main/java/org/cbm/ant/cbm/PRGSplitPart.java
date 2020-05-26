package org.cbm.ant.cbm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.Util;

public class PRGSplitPart
{

    private File target;
    private String header;
    private String offset;
    private String length;
    private boolean includeHeader = true;

    public PRGSplitPart()
    {
        super();
    }

    public File getTarget()
    {
        if (target == null)
        {
            throw new BuildException("Target missing");
        }

        return target;
    }

    /**
     * Sets the target file. Mandatory.
     * 
     * @param target the target file
     */
    public void setTarget(File target)
    {
        this.target = target;
    }

    public byte[] getHeader(int defaultValue)
    {
        if (Util.isEmpty(header))
        {
            return new byte[]{(byte) (defaultValue % 0x0100), (byte) (defaultValue / 0x0100)};
        }

        try
        {
            int value = Integer.decode(header).intValue();

            if (value < 0x0000 || value > 0xffff)
            {
                throw new BuildException("Invalid header: " + header);
            }

            return new byte[]{(byte) (value % 0x0100), (byte) (value / 0x0100)};
        }
        catch (NumberFormatException e)
        {
            throw new BuildException("Invalid header: " + header, e);
        }
    }

    /**
     * Sets the header address. The default is the header of the source file plus the offset
     * 
     * @param header the header
     */
    public void setHeader(String header)
    {
        this.header = header;
    }

    public int getOffset(int defaultValue)
    {
        if (Util.isEmpty(offset))
        {
            return defaultValue;
        }

        try
        {
            int value = Integer.decode(offset).intValue();

            if (value < 0x0000)
            {
                throw new BuildException("Invalid offset: " + offset);
            }

            return value;
        }
        catch (NumberFormatException e)
        {
            throw new BuildException("Invalid offset: " + offset, e);
        }
    }

    /**
     * Sets the offset. The default is the the end of the last part or the beginning of the file
     * 
     * @param offset the offset
     */
    public void setOffset(String offset)
    {
        this.offset = offset;
    }

    public int getLength()
    {
        if (Util.isEmpty(length))
        {
            throw new BuildException("Length missing");
        }

        try
        {
            int value = Integer.decode(length).intValue();

            if (value < 0x0000)
            {
                throw new BuildException("Invalid length: " + length);
            }

            return value;
        }
        catch (NumberFormatException e)
        {
            throw new BuildException("Invalid length: " + length, e);
        }
    }

    /**
     * Sets the length in bytes (decimal or hex value). Mandatory.
     * 
     * @param length the length
     */
    public void setLength(String length)
    {
        this.length = length;
    }

    public boolean isIncludeHeader()
    {
        return includeHeader;
    }

    /**
     * Set to false to skip the header. Default is "true".
     * 
     * @param includeHeader true to include header
     */
    public void setIncludeHeader(boolean includeHeader)
    {
        this.includeHeader = includeHeader;
    }

    public boolean isExecutionNecessary(long lastModified)
    {
        File target = getTarget();

        if (!target.exists())
        {
            return true;
        }

        return lastModified > target.lastModified();
    }

    public int execute(int header, byte[] bytes, int offset, long lastModified)
    {
        File target = getTarget();
        int length = getLength();

        offset = getOffset(offset);

        try
        {
            FileOutputStream out = new FileOutputStream(target);

            try
            {
                if (isIncludeHeader())
                {
                    out.write(getHeader(header + offset));
                }

                out.write(bytes, offset, length);
            }
            finally
            {
                out.close();
            }
        }
        catch (IOException e)
        {
            throw new BuildException("Error writing: " + target.getAbsolutePath());
        }

        target.setLastModified(lastModified);

        return offset + length;
    }

}
