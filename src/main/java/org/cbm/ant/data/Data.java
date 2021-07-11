package org.cbm.ant.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private void addCommand(DataCommand command)
    {
        commands.add(command);
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

        for (DataCommand command : commands)
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
                for (DataCommand command : commands)
                {
                    command.execute(this, out);
                }
            }
        }
        catch (IOException e)
        {
            throw new BuildException("Failed to write file: " + target, e);
        }
    }

}
