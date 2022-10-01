package org.cbm.ant.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.cbm.ant.util.Util;

public class DataBinaryWriter implements DataWriter
{
    private final OutputStream out;
    private final Charset charset;

    public DataBinaryWriter(OutputStream out, Charset charset)
    {
        super();

        this.out = out;
        this.charset = charset;
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) throws IOException
    {
        out.write(b, offset, length);
    }

    @Override
    public void writeText(String text) throws IOException
    {
        List<String> lines = Util.splitAndSanitizeLines(text);

        for (int i = 0; i < lines.size(); i++)
        {
            if (i > 0)
            {
                writeByte('\n');
            }

            String line = lines.get(i);
            byte[] bytes = line.getBytes(charset);

            for (int j = 0; j < bytes.length; ++j)
            {
                bytes[j] = (byte) Util.unsignedByteToInt(bytes[j]);
            }
        }
    }

    @Override
    public void writeComment(String comment) throws IOException
    {
        // ignore
    }
}
