package org.cbm.ant.cbm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CbmAllocateSectorStrategies;
import org.cbm.ant.cbm.disk.CbmAllocateSectorStrategy;
import org.cbm.ant.cbm.disk.CbmDisk;
import org.cbm.ant.cbm.disk.CbmFile;
import org.cbm.ant.cbm.disk.CbmFileOutputStream;
import org.cbm.ant.cbm.disk.CbmFileType;
import org.cbm.ant.cbm.disk.CbmIOException;
import org.cbm.ant.cbm.disk.CbmSectorInterleaves;
import org.cbm.ant.cbm.disk.CbmSectorLocation;
import org.cbm.ant.util.IOUtils;

public class CbmDiskWriteTaskCommand extends AbstractCbmDiskTaskCommand
{

    private File source;
    private String destination;
    private CbmFileType fileType = CbmFileType.PRG;
    private CbmSectorLocation location;
    private CbmAllocateSectorStrategy allocateSectorStrategy;
    private CbmSectorInterleaves sectorInterleaves;
    private boolean overwrite;

    public CbmDiskWriteTaskCommand()
    {
        super();
    }

    public File getSource() throws BuildException
    {
        if (source == null)
        {
            throw new BuildException("Source is missing");
        }

        if (!source.exists())
        {
            throw new BuildException("Error reading source: " + source.getAbsolutePath());
        }

        return source;
    }

    public void setSource(File source)
    {
        this.source = source;
    }

    public String getDestination() throws BuildException
    {
        if (destination == null || destination.trim().length() == 0)
        {
            return getSource().getName();
        }

        return destination;
    }

    /**
     * Sets the destination file name. If not specified, the name of the source file will be used
     *
     * @param destination the destination file name
     */
    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    public CbmFileType getFileType()
    {
        return fileType;
    }

    public void setFileType(CbmFileType fileType)
    {
        this.fileType = fileType;
    }

    public CbmSectorLocation getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        try
        {
            this.location = CbmSectorLocation.parse(location);
        }
        catch (IllegalArgumentException e)
        {
            throw new BuildException("Invalid location: " + location, e);
        }
    }

    public CbmAllocateSectorStrategy getAllocateSectorStrategy()
    {
        return allocateSectorStrategy;
    }

    public void setAllocateSectorStrategy(String allocateSectorStrategy)
    {
        try
        {
            this.allocateSectorStrategy = CbmAllocateSectorStrategies.parse(allocateSectorStrategy);
        }
        catch (IllegalArgumentException e)
        {
            throw new BuildException("Invalid allocate sector strategy: " + allocateSectorStrategy, e);
        }
    }

    /**
     * @return the sector interleaves
     */
    public CbmSectorInterleaves getSectorInterleaves()
    {
        return sectorInterleaves;
    }

    /**
     * Sets the sector interleaves from a comma-separated list: <code>[ &lt;TRACK&gt; [ \"-\" &lt;TRACK&gt; ] \":\" ]
     * &lt;INTERLEAVE&gt;</code>
     *
     * @param sectorInterleaves the interleaves
     */
    public void setSectorInterleaves(String sectorInterleaves)
    {
        try
        {
            this.sectorInterleaves = CbmSectorInterleaves.parse(sectorInterleaves);
        }
        catch (IllegalArgumentException e)
        {
            throw new BuildException("Invalid sector interleaves: " + sectorInterleaves, e);
        }
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }

    /**
     * @see org.cbm.ant.disk.DiskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        File source = getSource();

        if (exists && source.exists())
        {
            return source.lastModified() > lastModified;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#execute(org.cbm.ant.cbm.CbmDiskTask, CbmDisk)
     */
    @Override
    public Long execute(CbmDiskTask task, CbmDisk disk) throws BuildException
    {
        File source = getSource();
        
        task.log(String.format("Writing \"%s\" to disk image...", source));

        if (allocateSectorStrategy != null)
        {
            disk = disk.withAllocateSectorStrategy(allocateSectorStrategy);
        }

        if (sectorInterleaves != null)
        {
            disk = disk.withSectorInterleaves(sectorInterleaves);
        }

        String destination = getDestination();

        if (disk.existsFile(destination))
        {
            if (!overwrite)
            {
                throw new BuildException(String.format("File \"%s\" already exists", destination));
            }

            try
            {
                disk.deleteFile(destination, false);
            }
            catch (CbmIOException e)
            {
                throw new BuildException(String.format("Failed to delete file \"%s\"", destination), e);
            }
        }

        CbmFile file;

        try
        {
            file = disk.allocateFile(destination);
        }
        catch (CbmIOException e1)
        {
            throw new BuildException("Failed to allocate file: " + destination);
        }

        if (location != null)
        {
            file.setLocation(Optional.of(location));
        }

        if (fileType != null)
        {
            file.setType(fileType);
        }

        try (CbmFileOutputStream out = file.write())
        {
            try (InputStream in = new FileInputStream(source))
            {
                IOUtils.copy(in, out);
            }
        }
        catch (IOException e)
        {
            throw new BuildException(String
                .format("Failed to copy data from file \"%s\" to file \"%s\": %s", source, destination,
                    e.getMessage()),
                e);
        }

        return source.lastModified();
    }
}
