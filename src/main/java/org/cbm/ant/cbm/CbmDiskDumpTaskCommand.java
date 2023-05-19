package org.cbm.ant.cbm;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CbmDisk;
import org.cbm.ant.cbm.disk.CbmSectorLocation;

public class CbmDiskDumpTaskCommand extends AbstractCbmDiskTaskCommand
{
    private CbmSectorLocation location;
    private boolean chain = true;
    private final boolean header = false;

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

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
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

        if (location != null)
        {
            if (header)
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
        else if (!header)
        {
            disk.printSectors(bob);
        }

        System.out.println(bob.toString());

        return null;
    }
}
