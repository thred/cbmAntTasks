package org.cbm.ant.cbm;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CbmDisk;
import org.cbm.ant.cbm.disk.CbmSectorLocation;
import org.cbm.ant.util.IOUtils;

public class CbmDiskDumpTaskCommand extends AbstractCbmDiskTaskCommand
{
    private CbmSectorLocation location;
    private boolean chain = true;

    private boolean header = false;
    private boolean directory = false;
    private boolean sectors = false;

    private File target;

    public CbmDiskDumpTaskCommand()
    {
        super();
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

    public boolean isChain()
    {
        return chain;
    }

    public void setChain(boolean chain)
    {
        this.chain = chain;
    }

    public boolean isHeader()
    {
        return header;
    }

    public void setHeader(boolean header)
    {
        this.header = header;
    }

    public boolean isDirectory()
    {
        return directory;
    }

    public void setDirectory(boolean directory)
    {
        this.directory = directory;
    }

    public boolean isSectors()
    {
        return sectors;
    }

    public void setSectors(boolean sectors)
    {
        this.sectors = sectors;
    }

    public File getTarget()
    {
        return target;
    }

    public void setTarget(File target)
    {
        this.target = target;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        if (target == null)
        {
            return true;
        }

        File target = getTarget();

        if (exists && target.exists())
        {
            return target.lastModified() < lastModified;
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
        StringBuilder bob = new StringBuilder();

        if (header)
        {
            disk.printHeader(bob);
        }

        if (directory)
        {
            if (bob.length() > 0)
            {
                bob.append("\n\n");
            }

            disk.printDirectory(bob, true);
        }

        if (location != null)
        {
            if (bob.length() > 0)
            {
                bob.append("\n\n");
            }

            if (chain)
            {
                disk.printSectorChain(bob, location);
            }
            else
            {
                disk.printSector(bob, location);
            }
        }

        if (sectors || !header && !directory && !sectors && location == null)
        {
            if (bob.length() > 0)
            {
                bob.append("\n\n");
            }

            disk.printSectors(bob);
        }

        if (target != null)
        {
            try
            {
                IOUtils.write(target, bob.toString());
            }
            catch (IOException e)
            {
                throw new BuildException("Failed to write file: " + target, e);
            }
        }
        else
        {
            System.out.println(bob.toString());
        }

        return null;
    }
}
