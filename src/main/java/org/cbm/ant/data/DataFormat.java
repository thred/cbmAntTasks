package org.cbm.ant.data;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.BiFunction;

public enum DataFormat
{
    BINARY(DataBinaryWriter::new),

    CA65(DataCA65Writer::new),

    CBM(DataCBMWriter::new);

    private final BiFunction<OutputStream, Charset, DataWriter> factory;

    DataFormat(BiFunction<OutputStream, Charset, DataWriter> factory)
    {
        this.factory = factory;
    }

    public DataWriter createWriter(OutputStream out, Charset charset)
    {
        return factory.apply(out, charset);
    }
}
