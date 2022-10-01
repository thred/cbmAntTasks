package org.cbm.ant.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.cbm.ant.util.Util;

public class DataCA65Writer implements DataWriter
{
    private final OutputStream out;
    private final Charset charset;

    private int writtenBytes = 0;

    public DataCA65Writer(OutputStream out, Charset charset)
    {
        super();

        this.out = out;
        this.charset = charset;
    }

    protected void print(String s) throws IOException
    {
        out.write(s.getBytes(charset));
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) throws IOException
    {
        for (int i = 0; i < length; ++i)
        {
            if (writtenBytes < 0 || writtenBytes >= 16)
            {
                print(System.getProperty("line.separator"));
                writtenBytes = 0;
            }

            if (writtenBytes == 0)
            {
                print("  .byte ");
            }
            else
            {
                print(", ");
            }

            print(Util.toHex("$", b[offset + i]));
            ++writtenBytes;
        }
    }

    @Override
    public void writeText(String text) throws IOException
    {
        writtenBytes = -1;

        for (String line : Util.splitAndSanitizeLines(text))
        {
            print(System.getProperty("line.separator") + line);
        }
    }

    @Override
    public void writeComment(String comment) throws IOException
    {
        writtenBytes = -1;

        for (String line : Util.splitAndSanitizeLines(comment))
        {
            print(System.getProperty("line.separator") + "; " + line);
        }
    }
}
