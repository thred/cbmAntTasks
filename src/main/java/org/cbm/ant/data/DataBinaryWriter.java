package org.cbm.ant.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.cbm.ant.util.Util;

public class DataBinaryWriter implements DataWriter
{
    private final OutputStream out;
    private final Charset charset;
    private final int chunkSize;

    private byte[] buffer;
    private int endOffset;
    private int writeOffset;

    public DataBinaryWriter(OutputStream out, Charset charset)
    {
        this(out, charset, 4096);
    }

    public DataBinaryWriter(OutputStream out, Charset charset, int chunkSize)
    {
        super();

        this.out = out;
        this.charset = charset;
        this.chunkSize = chunkSize;

        buffer = new byte[chunkSize];
        endOffset = 0;
        writeOffset = 0;
    }

    @Override
    public boolean isRandomAccessSupported()
    {
        return true;
    }

    @Override
    public void at(int writeOffset)
    {
        this.writeOffset = writeOffset;
    }

    @Override
    public void atEnd()
    {
        writeOffset = endOffset;
    }

    @Override
    public void flush() throws IOException
    {
        out.write(buffer, 0, endOffset);

        Arrays.fill(buffer, (byte) 0);
        endOffset = 0;
        writeOffset = 0;
    }

    @Override
    public void close() throws IOException
    {
        DataWriter.super.close();

        out.close();
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length)
    {
        if (writeOffset + length > buffer.length)
        {
            int newLength = (writeOffset + length + chunkSize) / chunkSize * chunkSize;

            buffer = Arrays.copyOf(buffer, newLength);
        }

        System.arraycopy(b, offset, buffer, writeOffset, length);

        endOffset = Math.max(endOffset, writeOffset + length);
        writeOffset += length;
    }

    @Override
    public void writeText(String text) throws IOException
    {
        byte[] bytes = text.getBytes(charset);

        for (int j = 0; j < bytes.length; ++j)
        {
            bytes[j] = (byte) Util.unsignedByteToInt(bytes[j]);
        }

        writeBytes(bytes);
    }

    @Override
    public void writeComment(String comment) throws IOException
    {
        // ignore
    }
}
