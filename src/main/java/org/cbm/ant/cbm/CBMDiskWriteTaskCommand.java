package org.cbm.ant.cbm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CBMDiskException;
import org.cbm.ant.cbm.disk.CBMDiskLocation;
import org.cbm.ant.cbm.disk.CBMDiskOperator;
import org.cbm.ant.cbm.disk.CBMDiskOutputStream;
import org.cbm.ant.cbm.disk.CBMFileType;
import org.cbm.ant.util.IOUtils;

public class CBMDiskWriteTaskCommand extends AbstractCBMDiskTaskCommand
{

    private File source;
    private String destination;
    private CBMFileType fileType = CBMFileType.PRG;
    private Integer track;
    private Integer sector;
    private Integer sectorInterleave;

    public CBMDiskWriteTaskCommand()
    {
        super();
    }

    public File getSource() throws BuildException
    {
        if (source == null)
        {
            throw new BuildException("Missing source");
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

    public Integer getSectorInterleave()
    {
        return sectorInterleave;
    }

    public void setSectorInterleave(Integer sectorInterleave)
    {
        this.sectorInterleave = sectorInterleave;
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

        int sectorInterleaveBackup = operator.getDisk().getSectorNrInterleave();

        if (sectorInterleave != null)
        {
            operator.getDisk().setSectorNrInterleave(sectorInterleave);
        }

        try
        {
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

            try
            {
                out = operator.create(location, getDestination(), getFileType());
            }
            catch (IOException e)
            {
                throw new BuildException(String.format("Failed to create file \"%s\"", getDestination()), e);
            }
            catch (CBMDiskException e)
            {
                throw new BuildException(
                    String.format("Failed to create file \"%s\": %s", getDestination(), e.getMessage()), e);
            }

            try
            {
                try
                {
                    InputStream in = new FileInputStream(getSource());

                    try
                    {
                        IOUtils.copy(in, out);
                    }
                    finally
                    {
                        in.close();
                    }
                }
                catch (IOException e)
                {
                    throw new BuildException(String.format("Failed to copy data from file \"%s\" to file \"%s\"",
                        getSource(), getDestination()), e);
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
                    throw new BuildException(String.format("Failed to close file \"%s\"", getDestination()), e);
                }
            }
        }
        finally
        {
            operator.getDisk().setSectorNrInterleave(sectorInterleaveBackup);
        }

        return Long.valueOf(getSource().lastModified());
    }
}
