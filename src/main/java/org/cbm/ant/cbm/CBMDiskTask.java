package org.cbm.ant.cbm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.cbm.ant.cbm.disk.CBMDisk;
import org.cbm.ant.cbm.disk.CBMDiskOperator;

public class CBMDiskTask extends AbstractDiskTask
{

    private final List<CBMDiskTaskCommand> commands;

    private File image;
    private boolean failOnError = true;
    private boolean ignoreIfNotExists = false;
    private String sectorInterleaves;

    public CBMDiskTask()
    {
        super();

        commands = new ArrayList<>();
    }

    public void addFormat(CBMDiskFormatTaskCommand command)
    {
        addCommand(command);
    }

    public void addList(CBMDiskListTaskCommand command)
    {
        addCommand(command);
    }

    public void addRead(CBMDiskReadTaskCommand command)
    {
        addCommand(command);
    }

    public void addWrite(CBMDiskWriteTaskCommand command)
    {
        addCommand(command);
    }

    private void addCommand(CBMDiskTaskCommand command)
    {
        commands.add(command);
    }

    /**
     * Returns the image file
     *
     * @return the image file
     */
    public File getImage()
    {
        return image;
    }

    /**
     * Sets the image file
     *
     * @param image the image file
     */
    public void setImage(File image)
    {
        this.image = image;
    }

    /**
     * Returns true if the default value for the commands is set to fail on error
     *
     * @return true if the default value for the commands is set to fail on error
     */
    public boolean isFailOnError()
    {
        return failOnError;
    }

    /**
     * Set to true if the default value for the commands is set to fail on error
     *
     * @param failOnError true if the default value for the commands is set to fail on error
     */
    public void setFailOnError(boolean failOnError)
    {
        this.failOnError = failOnError;
    }

    public boolean isIgnoreIfNotExists()
    {
        return ignoreIfNotExists;
    }

    public void setIgnoreIfNotExists(boolean ignoreIfNotExists)
    {
        this.ignoreIfNotExists = ignoreIfNotExists;
    }

    public String getSectorInterleaves()
    {
        return sectorInterleaves;
    }

    public void setSectorInterleaves(String sectorInterleaves)
    {
        this.sectorInterleaves = sectorInterleaves;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException
    {
        File image = getImage();

        if (ignoreIfNotExists && !image.exists())
        {
            return;
        }

        if (!isExecutionNecessary(image))
        {
            return;
        }

        CBMDisk disk = new CBMDisk();
        CBMDiskOperator operator = new CBMDiskOperator(disk);

        if (image.exists())
        {
            log(String.format("Loading existing image \"%s\"...", image));

            try
            {
                operator.getDisk().load(image);
            }
            catch (IOException e)
            {
                if (!isFailOnError())
                {
                    log(String.format("Failed to load image \"%s\" due to %s. Ignoring...", image, e), e,
                        Project.MSG_INFO);
                }
                else
                {
                    throw new BuildException(String.format("Failed to load image \"%s\"", image), e);
                }
            }
        }

        if (sectorInterleaves != null)
        {
            disk.setSectorInterleaves(sectorInterleaves);
        }

        Long modification = null;

        for (CBMDiskTaskCommand command : commands)
        {
            try
            {
                Long result = command.execute(this, operator);

                if (result != null && (modification == null || result.longValue() > modification.longValue()))
                {
                    modification = result;
                }
            }
            catch (BuildException e)
            {
                if (!isFailOnError() || !command.isFailOnError())
                {
                    log("Ignoring error: " + e.getMessage());
                }
                else
                {
                    throw e;
                }
            }
            catch (Exception e)
            {
                if (!isFailOnError() || !command.isFailOnError())
                {
                    log("Ignoring error: " + e.getMessage());
                }
                else
                {
                    throw new BuildException("Unhandled Exception: " + e, e);
                }
            }
        }

        if (modification != null)
        {
            try
            {
                operator.getDisk().save(image);

                image.setLastModified(modification.longValue());
            }
            catch (IOException e)
            {
                if (!isFailOnError())
                {
                    log(String.format("Failed to save image \"%s\" due to %s. Ignoring...", image, e), e,
                        Project.MSG_INFO);
                }
                else
                {
                    throw new BuildException(String.format("Failed to save image \"%s\"", image), e);
                }
            }
        }
    }

    private boolean isExecutionNecessary(File image)
    {
        long lastModified = -1;
        boolean exists = image.exists();

        if (!exists)
        {
            return true;
        }

        if (exists)
        {
            lastModified = image.lastModified();
        }

        for (CBMDiskTaskCommand command : commands)
        {
            try
            {
                if (command.isExecutionNecessary(lastModified, exists))
                {
                    return true;
                }
            }
            catch (BuildException e)
            {
                if (!command.isFailOnError())
                {
                    log("Ignoring error: " + e.getMessage(), Project.MSG_WARN);
                }
                else
                {
                    throw e;
                }
            }
        }

        return false;
    }

}
