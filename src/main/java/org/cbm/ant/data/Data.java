package org.cbm.ant.data;

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
public class Data extends Task
{
    private File target;
    private DataFormat format;
    private Charset charset;
    private final List<AbstractDataCommand> commands;

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

    private void addCommand(AbstractDataCommand command)
    {
        commands.add(command);
    }

    public void addCharImage(DataCharImage image)
    {
        addCommand(image);
    }

    public void addComment(DataComment comment)
    {
        addCommand(comment);
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
    public void execute() throws BuildException
    {
        boolean executionNecessary = false;
        boolean exists = target.exists();
        long lastModified = exists ? target.lastModified() : -1;

        for (AbstractDataCommand command : commands)
        {
            if (command.isExecutionNecessary(lastModified, exists))
            {
                executionNecessary = true;
                break;
            }
        }

        if (!executionNecessary)
        {
            return;
        }

        log("Writing " + target);

        try
        {
            try (FileOutputStream out = new FileOutputStream(target))
            {
                Charset charset = getCharset();
                DataWriter writer = null;
                DataFormat format = null;

                for (AbstractDataCommand command : commands)
                {
                    DataFormat currentFormat = command.getFormat();

                    if (currentFormat == null)
                    {
                        currentFormat = getFormat();
                    }

                    if (format != currentFormat)
                    {
                        format = currentFormat;
                        writer = format.createWriter(out, charset);
                    }

                    command.execute(this, writer);
                }
            }
        }
        catch (IOException e)
        {
            throw new BuildException("Failed to write file: " + target, e);
        }
    }
}
