package org.cbm.ant.cbm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.cbm.ant.cbm.disk.CbmAllocateSectorStrategies;
import org.cbm.ant.cbm.disk.CbmDisk;
import org.cbm.ant.cbm.disk.CbmImageType;
import org.cbm.ant.cbm.disk.CbmImageTypes;
import org.cbm.ant.cbm.disk.CbmSectorInterleaves;

public class CbmDiskTask extends AbstractDiskTask
{
    private final List<CbmDiskTaskCommand> commands;

    private File image;
    private CbmImageType<?> type;
    private boolean failOnError = true;
    private boolean ignoreIfNotExists = false;
    private String allocateSectorStrategy;
    private String sectorInterleaves;

    public CbmDiskTask()
    {
        super();

        commands = new ArrayList<>();
    }

    public void addFormat(CbmDiskFormatTaskCommand command)
    {
        addCommand(command);
    }

    public void addDump(CbmDiskDumpTaskCommand command)
    {
        addCommand(command);
    }

    public void addList(CbmDiskListTaskCommand command)
    {
        addCommand(command);
    }

    public void addRead(CbmDiskReadTaskCommand command)
    {
        addCommand(command);
    }

    public void addDelete(CbmDiskDeleteTaskCommand command)
    {
        addCommand(command);
    }

    public void addWrite(CbmDiskWriteTaskCommand command)
    {
        addCommand(command);
    }

    private void addCommand(CbmDiskTaskCommand command)
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

    public CbmImageType<?> getType()
    {
        if (type == null)
        {
            String fileName = getImage().getName().toLowerCase();

            if (fileName.endsWith(".d64"))
            {
                type = CbmImageTypes.D64;
            }
            else if (fileName.endsWith(".d71"))
            {
                type = CbmImageTypes.D71;
            }
            else if (fileName.endsWith(".d81"))
            {
                type = CbmImageTypes.D81;
            }
            else
            {
                throw new BuildException("The type is missing");
            }
        }

        return type;
    }

    /**
     * Sets the type of image
     *
     * @param type the image type
     */
    public void setType(String type)
    {
        this.type = CbmImageTypes.getByName(type).orElseThrow(() -> new BuildException("Unknown image type: " + type));
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

    public String getAllocateSectorStrategy()
    {
        return allocateSectorStrategy;
    }

    public void setAllocateSectorStrategy(String allocateSectorStrategy)
    {
        this.allocateSectorStrategy = allocateSectorStrategy;
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
        File fileName = getImage();

        if (ignoreIfNotExists && !fileName.exists())
        {
            return;
        }

        if (!isExecutionNecessary(fileName))
        {
            return;
        }

        CbmImageType<?> type = getType();
        CbmDisk disk = type.createEmptyDisk();

        if (fileName.exists())
        {
            log(String.format("Loading existing image \"%s\"...", fileName));

            try
            {
                disk.load(fileName);
            }
            catch (IOException e)
            {
                if (isFailOnError())
                {
                    throw new BuildException(String.format("Failed to load image \"%s\"", fileName), e);
                }

                log(String.format("Failed to load image \"%s\" due to %s. Ignoring...", fileName, e), e,
                    Project.MSG_INFO);
            }
        }

        if (allocateSectorStrategy != null)
        {
            disk = disk.withAllocateSectorStrategy(CbmAllocateSectorStrategies.parse(allocateSectorStrategy));
        }

        if (sectorInterleaves != null)
        {
            disk = disk.withSectorInterleaves(CbmSectorInterleaves.parse(sectorInterleaves));
        }

        Long modification = null;

        for (CbmDiskTaskCommand command : commands)
        {
            try
            {
                Long result = command.execute(this, disk);

                if (result != null && (modification == null || result.longValue() > modification.longValue()))
                {
                    modification = result;
                }
            }
            catch (BuildException e)
            {
                if (isFailOnError() && command.isFailOnError())
                {
                    throw e;
                }
                log("Ignoring error: " + e.getMessage());
            }
            catch (Exception e)
            {
                if (isFailOnError() && command.isFailOnError())
                {
                    throw new BuildException("Unhandled Exception: " + e, e);
                }
                log("Ignoring error: " + e.getMessage());
            }
        }

        if (modification != null)
        {
            try
            {
                disk.save(fileName);

                fileName.setLastModified(modification);
            }
            catch (IOException e)
            {
                if (isFailOnError())
                {
                    throw new BuildException(String.format("Failed to save image \"%s\"", fileName), e);
                }
                log(String.format("Failed to save image \"%s\" due to %s. Ignoring...", fileName, e), e,
                    Project.MSG_INFO);
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

        for (CbmDiskTaskCommand command : commands)
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
                if (command.isFailOnError())
                {
                    throw e;
                }
                log("Ignoring error: " + e.getMessage(), Project.MSG_WARN);
            }
        }

        return false;
    }
}
