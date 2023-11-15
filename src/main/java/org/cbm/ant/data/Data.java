package org.cbm.ant.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Creates a file from various sources
 *
 * @author thred
 */
public class Data extends Task implements DataCommand
{
    private File target;
    private DataFormat format;
    private Charset charset;

    private final List<DataCommand> commands;

    public Data()
    {
        super();

        commands = new ArrayList<>();
    }

    public File getTarget()
    {
        return target;
    }

    public void setTarget(File target)
    {
        this.target = target;
    }

    public DataFormat getFormat()
    {
        if (format == null)
        {
            return DataFormat.BINARY;
        }

        return format;
    }

    public void setFormat(DataFormat format)
    {
        this.format = format;
    }

    public Charset getCharset()
    {
        if (charset == null)
        {
            charset = StandardCharsets.UTF_8;
        }

        return charset;
    }

    public void setCharset(Charset charset)
    {
        this.charset = charset;
    }

    private void addCommand(DataCommand command)
    {
        commands.add(command);
    }

    public void addAt(DataAt at)
    {
        addCommand(at);
    }

    public void addAtEnd(DataAtEnd atEnd)
    {
        addCommand(atEnd);
    }

    public void addCharImage(DataCharImage image)
    {
        addCommand(image);
    }

    public void addComment(DataComment comment)
    {
        addCommand(comment);
    }

    public void addData(Data data)
    {
        addCommand(data);
    }

    public void addFill(DataFill fill)
    {
        addCommand(fill);
    }

    public void addHeader(DataHeader header)
    {
        addCommand(header);
    }

    public void addImage(DataImage image)
    {
        addCommand(image);
    }

    public void addManual(DataManual manual)
    {
        addCommand(manual);
    }

    public void addRaw(DataRaw raw)
    {
        addCommand(raw);
    }

    public void addSprite(DataSprite sprite)
    {
        addCommand(sprite);
    }

    public void addFont(DataFont font)
    {
        addCommand(font);
    }

    public void addText(DataText text)
    {
        addCommand(text);
    }

    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        boolean executionNecessary = false;

        for (DataCommand command : commands)
        {
            if (command.isExecutionNecessary(lastModified, exists))
            {
                executionNecessary = true;
                break;
            }
        }
        return executionNecessary;
    }

    @Override
    public void execute() throws BuildException
    {
        boolean exists = target.exists();
        long lastModified = exists ? target.lastModified() : -1;
        boolean executionNecessary = isExecutionNecessary(lastModified, exists);

        if (!executionNecessary)
        {
            return;
        }

        log("Writing " + target);

        try
        {
            try (FileOutputStream out = new FileOutputStream(target))
            {
                DataFormat format = getFormat();
                Charset charset = getCharset();
                DataWriter writer = format.createWriter(out, charset);

                executeCommands(writer);
            }
        }
        catch (IOException e)
        {
            throw new BuildException("Failed to write file: " + target, e);
        }
    }

    @Override
    public void execute(DataWriter writer) throws BuildException, IOException
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            DataFormat format = getFormat();
            Charset charset = getCharset();

            executeCommands(format.createWriter(out, charset));

            out.close();

            writer.writeBytes(out.toByteArray());
        }
    }

    protected void executeCommands(DataWriter writer) throws IOException
    {
        for (DataCommand command : commands)
        {
            command.execute(writer);
        }

        writer.flush();
    }
}
