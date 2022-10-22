package org.cbm.ant.cbm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CBMDiskException;
import org.cbm.ant.cbm.disk.CBMDiskLocation;
import org.cbm.ant.cbm.disk.CBMDiskOperator;
import org.cbm.ant.cbm.disk.CBMDiskOutputStream;
import org.cbm.ant.cbm.disk.CBMFileType;
import org.cbm.ant.cbm.disk.CBMSectorInterleaves;
import org.cbm.ant.util.IOUtils;

public class CBMDiskWriteTaskCommand extends AbstractCBMDiskTaskCommand
{

    private File source;
    private String destination;
    private CBMFileType fileType = CBMFileType.PRG;
    private Integer track;
    private Integer sector;
    private String sectorInterleaves;
    private boolean overwrite;

    public CBMDiskWriteTaskCommand()
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

    public CBMFileType getFileType()
    {
        return fileType;
    }

    public void setFileType(CBMFileType fileType)
    {
        this.fileType = fileType;
    }

    public Integer getTrack()
    {
        return track;
    }

    public void setTrack(Integer track)
    {
        this.track = track;
    }

    public Integer getSector()
    {
        return sector;
    }

    public void setSector(Integer sector)
    {
        this.sector = sector;
    }

    /**
     * @return the sector interleaves
     */
    public String getSectorInterleaves()
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
        this.sectorInterleaves = sectorInterleaves;
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
     * @see org.cbm.ant.viceteam.C1541Command#isExecutionNecessary(long, boolean)
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
     * @see org.cbm.ant.cbm.CBMDiskTaskCommand#execute(org.cbm.ant.cbm.CBMDiskTask,
     *      org.cbm.ant.cbm.disk.CBMDiskOperator)
     */
    @Override
    public Long execute(CBMDiskTask task, CBMDiskOperator operator) throws BuildException
    {
        task.log(String.format("Writing \"%s\" to disk image...", source));

        CBMSectorInterleaves currentSectorInterleaves = operator.getDisk().getSectorInterleaves();

        if (sectorInterleaves != null)
        {
            currentSectorInterleaves = CBMSectorInterleaves.parse(currentSectorInterleaves, sectorInterleaves);
        }

        CBMDiskLocation location = null;

        if (track != null || sector != null)
        {
            if (track == null)
            {
                throw new BuildException("Sector is set, but track is missing");
            }

            if (sector == null)
            {
                throw new BuildException("Track is set, but sector is missing");
            }

            location = new CBMDiskLocation(track, sector);
        }

        CBMDiskOutputStream out;

        String destination = getDestination();

        if (operator.exists(destination))
        {
            if (!overwrite)
            {
                throw new BuildException(String.format("File \"%s\" already exists", destination));
            }

            try
            {
                operator.delete(destination);
            }
            catch (FileNotFoundException e)
            {
                throw new BuildException(String.format("Failed to delete file \"%s\"", destination), e);
            }
        }

        try
        {
            out = operator.create(location, destination, getFileType(), currentSectorInterleaves);
        }
        catch (IOException e)
        {
            throw new BuildException(String.format("Failed to create file \"%s\"", destination), e);
        }
        catch (CBMDiskException e)
        {
            throw new BuildException(String.format("Failed to create file \"%s\": %s", destination, e.getMessage()), e);
        }

        try
        {
            try
            {
                try (InputStream in = new FileInputStream(getSource()))
                {
                    IOUtils.copy(in, out);
                }
            }
            catch (IOException e)
            {
                throw new BuildException(
                    String.format("Failed to copy data from file \"%s\" to file \"%s\"", getSource(), destination), e);
            }
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                throw new BuildException(String.format("Failed to close file \"%s\"", destination), e);
            }
        }

        return getSource().lastModified();
    }
}
