package org.cbm.ant.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.Util;

public class DataRaw implements DataCommand
{
    private File file;
    private Integer offset;
    private Integer length;

    public File getFile()
    {
        if (file == null)
        {
            throw new BuildException("Source is missing");
        }

        if (!file.exists())
        {
            throw new BuildException("Error reading source: " + file.getAbsolutePath());
        }

        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public Integer getOffset()
    {
        return offset;
    }

    public void setOffset(String offset)
    {
        this.offset = Util.parseHex(offset);
    }

    public Integer getLength()
    {
        return length;
    }

    public void setLength(String length)
    {
        this.length = Util.parseHex(length);
    }

    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        File file = getFile();

        if (exists && file.exists())
        {
            return file.lastModified() > lastModified;
        }

        return true;
    }

    @Override
    public void execute(DataWriter writer) throws BuildException, IOException
    {
        byte[] bytes;

        try
        {
            bytes = Util.read(getFile());
        }
        catch (IOException e)
        {
            throw new BuildException("Failed to read file: " + getFile(), e);
        }

        int offset = this.offset != null ? this.offset : 0;
        int length = this.length != null ? this.length : bytes.length - offset;

        try (OutputStream stream = writer.createByteStream())
        {
            stream.write(bytes, offset, length);
        }
    }
}
