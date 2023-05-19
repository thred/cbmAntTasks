package org.cbm.ant.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

public class IOUtils
{

    public static void read(File file, OutputStream out) throws IOException
    {
        try (InputStream in = new FileInputStream(file))
        {
            copy(in, out);
        }
    }

    public static void write(File file, InputStream in) throws IOException
    {
        try (OutputStream out = new FileOutputStream(file))
        {
            copy(in, out);
        }
    }

    public static void write(File target, String s) throws IOException
    {
        try (FileWriter write = new FileWriter(target))
        {
            write.write(s);
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        int length;
        byte buffer[] = new byte[256];

        while ((length = in.read(buffer)) >= 0)
        {
            out.write(buffer, 0, length);
        }
    }

    public static byte[] readFully(InputStream in) throws IOException
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            copy(in, out);

            return out.toByteArray();
        }
    }

    public static String printToStream(Consumer<PrintStream> consumer)
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            try (PrintStream stream = new PrintStream(out, true))
            {
                consumer.accept(stream);
            }

            return out.toString();
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to print to stream", e);
        }
    }
}
