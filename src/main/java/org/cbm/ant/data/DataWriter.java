package org.cbm.ant.data;

import java.io.IOException;
import java.io.OutputStream;

public interface DataWriter
{
    default void writeByte(int b) throws IOException
    {
        writeBytes(new byte[]{(byte) b});
    }

    default void writeBytes(byte[] bytes) throws IOException
    {
        writeBytes(bytes, 0, bytes.length);
    }

    void writeBytes(byte[] b, int offset, int length) throws IOException;

    void writeText(String text) throws IOException;

    void writeComment(String comment) throws IOException;

    default OutputStream createByteStream()
    {
        return new OutputStream()
        {
            @Override
            public void write(byte[] b, int off, int len) throws IOException
            {
                writeBytes(b, off, len);
            }

            @Override
            public void write(int b) throws IOException
            {
                writeByte(b);
            }
        };
    }
}
