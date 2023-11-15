package org.cbm.ant.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class DataBinaryWriterTest
{
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Test
    public void testSimple() throws IOException
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            try (DataWriter writer = new DataBinaryWriter(out, CHARSET, 2))
            {
                writer.writeText("Test");
            }

            String result = new String(out.toByteArray(), CHARSET);

            MatcherAssert.assertThat(result, Matchers.equalTo("Test"));
        }
    }

    @Test
    public void testRandomAccess() throws IOException
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            try (DataWriter writer = new DataBinaryWriter(out, CHARSET, 2))
            {
                writer.writeText("T  t");
                writer.at(1);
                writer.writeText("es");
            }

            String result = new String(out.toByteArray(), CHARSET);

            MatcherAssert.assertThat(result, Matchers.equalTo("Test"));
        }
    }

    @Test
    public void testRandomAccessAtBeginning() throws IOException
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            try (DataWriter writer = new DataBinaryWriter(out, CHARSET, 2))
            {
                writer.writeText("  st");
                writer.at(0);
                writer.writeText("Te");
            }

            String result = new String(out.toByteArray(), CHARSET);

            MatcherAssert.assertThat(result, Matchers.equalTo("Test"));
        }
    }

    @Test
    public void testRandomAccessAfterEnd() throws IOException
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            try (DataWriter writer = new DataBinaryWriter(out, CHARSET, 2))
            {
                writer.writeText("T");
                writer.at(3);
                writer.writeText("t");
                writer.at(1);
                writer.writeText("es");
            }

            String result = new String(out.toByteArray(), CHARSET);

            MatcherAssert.assertThat(result, Matchers.equalTo("Test"));
        }
    }
}
