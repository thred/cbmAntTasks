package org.cbm.ant.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.Util;

public class DataCBMWriter implements DataWriter
{
    private final OutputStream out;

    public DataCBMWriter(OutputStream out, Charset charset)
    {
        super();

        this.out = out;
    }

    @Override
    public boolean isRandomAccessSupported()
    {
        return false;
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
                writeByte(convert('\n'));
            }

            char[] data = lines.get(i).toCharArray();
            int index = 0;

            while (index < data.length)
            {
                char ch = data[index++];

                if (ch == '\\')
                {
                    if (index >= data.length)
                    {
                        throw new BuildException("Invalid escape sequence at position " + index);
                    }

                    ch = data[index++];

                    switch (ch)
                    {
                        case '\\':
                            writeByte(convert('\\'));
                            continue;

                        case '0':
                            writeByte(convert('\0'));
                            continue;

                        case 'n':
                            writeByte(convert('\n'));
                            continue;

                        case 'r':
                            writeByte(convert('\r'));
                            continue;

                        case 't':
                            writeByte(convert('\t'));
                            continue;

                        case 'u':
                            if (index >= data.length)
                            {
                                throw new BuildException("Invalid escape sequence at position " + index);
                            }

                            ch = Character.toLowerCase(data[index++]);

                            if (!(ch >= '0' && ch <= '9') && !(ch >= 'a' && ch <= 'f'))
                            {
                                throw new BuildException("Invalid escape sequence at position " + index);
                            }

                            int value = ch >= '0' && ch <= '9' ? ch - '0' : ch - 'a' + 10;

                            if (index >= data.length)
                            {
                                throw new BuildException("Invalid escape sequence at position " + index);
                            }

                            ch = data[index++];

                            if (!Character.isDigit(ch))
                            {
                                throw new BuildException("Invalid escape sequence at position " + index);
                            }

                            value <<= 4;
                            value += ch >= '0' && ch <= '9' ? ch - '0' : ch - 'a' + 10;
                            writeByte(convert((char) value));
                            continue;
                    }

                    throw new BuildException("Invalid escape sequence at position " + index);
                }

                switch (ch)
                {
                    case '\u00c4':
                        ch = 0x5b;
                        break;

                    case '\u00d6':
                        ch = 0x5c;
                        break;

                    case '\u00dc':
                        ch = 0x5d;
                        break;

                    case '\u00e4':
                        ch = 0x7b;
                        break;

                    case '\u00f6':
                        ch = 0x7c;
                        break;

                    case '\u00fc':
                        ch = 0x7d;
                        break;

                    case '\u00df':
                        ch = 0x7e;
                        break;
                }

                writeByte(convert(ch));
            }
        }
    }

    protected int convert(int ch)
    {
        if (ch == '\n')
        {
            ch = '\r';
        }
        else if (ch == '\\')
        {
            ch = 0xbf;
        }
        else if (ch >= 0x40 && ch < 0x60)
        {
            ch += 0x80;
        }
        else if (ch >= 0x60 && ch < 0x80)
        {
            ch -= 0x20;
        }

        return ch;
    }

    @Override
    public void writeComment(String comment) throws IOException
    {
        // ignore
    }

    @Override
    public void flush() throws IOException
    {
        out.flush();
    }

    @Override
    public void close() throws IOException
    {
        DataWriter.super.close();

        out.close();
    }
}
