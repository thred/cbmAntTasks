package org.cbm.ant.tinycrunch;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.AbstractCBMPyhtonTask;
import org.cbm.ant.util.ProcessHandler;
import org.cbm.ant.util.Util;

/**
 * Task for tinycrunch by Christopher Jam. Task was developed and tested by using the version 1.2 found in Krill's
 * Loaders
 *
 * @author ham
 */
public class TinyCrunch extends AbstractCBMPyhtonTask
{

    private File tinyCrunchScript;

    /** The source file */
    private File source;

    /** The target file */
    private File target;

    /** The start address */
    private String startAddress;

    /** The end address */
    private String endAddress;

    /** compress to end of destination area */
    private Boolean inPlace;

    private Boolean selfExtracting;

    /** read/write .bin files, no header. cf readme.txt */
    private Boolean raw;

    /** execution address for self extracting .prg (requires selfExtracting set to true) */
    private String execAccress;

    /** generated asm include file containing a define for the output start address */
    private File paramFile;

    private Boolean verbose;

    /** faster (greedy) compression (default is optimal size) */
    private Boolean fast;

    public File getTinyCrunchScript()
    {
        File tinyCrunchScript = this.tinyCrunchScript;

        if (tinyCrunchScript == null)
        {
            String projectProperty = getProject().getProperty("tinyCrunchScript");

            if (projectProperty != null)
            {
                tinyCrunchScript = new File(projectProperty);
            }
        }

        if (tinyCrunchScript == null)
        {
            String systemProperty = System.getProperty("tinyCrunchScript");

            if (systemProperty != null)
            {
                tinyCrunchScript = new File(systemProperty);
            }
        }

        if (tinyCrunchScript == null)
        {
            String environmentSetting = System.getenv("TINY_CRUNCH_SCRIPT");

            if (environmentSetting != null)
            {
                tinyCrunchScript = new File(environmentSetting);
            }
        }

        if (tinyCrunchScript == null)
        {
            tinyCrunchScript = getProject().getBaseDir();
        }

        if (!tinyCrunchScript.isFile())
        {
            tinyCrunchScript = new File(tinyCrunchScript, "tc_encode.py");
        }

        if (!tinyCrunchScript.canRead())
        {
            throw new BuildException("No TinyCrunchScript found at: " + tinyCrunchScript);
        }

        return tinyCrunchScript;
    }

    public void setTinyCrunchScript(File tinyCrunchScript)
    {
        this.tinyCrunchScript = tinyCrunchScript;
    }

    public File getSource()
    {
        if (target == null)
        {
            throw new BuildException("Source is missing");
        }

        return source;
    }

    public void setSource(File source)
    {
        this.source = source;
    }

    public File getTarget()
    {
        if (target == null)
        {
            throw new BuildException("Target is missing");
        }

        return target;
    }

    public void setTarget(File target)
    {
        this.target = target;
    }

    public String getStartAddress()
    {
        if (Util.isEmpty(startAddress))
        {
            return null;
        }

        try
        {
            int value = Util.parseHex(startAddress);

            if (value < 0x0000 || value > 0xffff)
            {
                throw new BuildException("Invalid start address: " + startAddress);
            }

            return "$" + Integer.toHexString(value);
        }
        catch (NumberFormatException e)
        {
            throw new BuildException("Invalid start address: " + startAddress, e);
        }
    }

    public void setStartAddress(String startAddress)
    {
        this.startAddress = startAddress;
    }

    public String getEndAddress()
    {
        if (Util.isEmpty(endAddress))
        {
            return null;
        }

        try
        {
            int value = Util.parseHex(endAddress);

            if (value < 0x0000 || value > 0xffff)
            {
                throw new BuildException("Invalid end address: " + endAddress);
            }

            return "$" + Integer.toHexString(value);
        }
        catch (NumberFormatException e)
        {
            throw new BuildException("Invalid end address: " + endAddress, e);
        }
    }

    public void setEndAddress(String endAddress)
    {
        this.endAddress = endAddress;
    }

    public Boolean getInPlace()
    {
        if (inPlace == null)
        {
            return Util.isEmpty(startAddress) && Util.isEmpty(endAddress);
        }

        return inPlace;
    }

    public void setInPlace(Boolean inPlace)
    {
        this.inPlace = inPlace;
    }

    public Boolean getSelfExtracting()
    {
        return selfExtracting;
    }

    public void setSelfExtracting(Boolean selfExtracting)
    {
        this.selfExtracting = selfExtracting;
    }

    public Boolean getRaw()
    {
        return raw;
    }

    public void setRaw(Boolean raw)
    {
        this.raw = raw;
    }

    public String getExecAccress()
    {
        return execAccress;
    }

    public void setExecAccress(String execAccress)
    {
        this.execAccress = execAccress;
    }

    public File getParamFile()
    {
        return paramFile;
    }

    public void setParamFile(File paramFile)
    {
        this.paramFile = paramFile;
    }

    public Boolean getVerbose()
    {
        return verbose;
    }

    public void setVerbose(Boolean verbose)
    {
        this.verbose = verbose;
    }

    public Boolean getFast()
    {
        return fast;
    }

    public void setFast(Boolean fast)
    {
        this.fast = fast;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException
    {
        File source = getSource();

        if (!source.exists())
        {
            throw new BuildException("Source not available: " + source.getAbsolutePath());
        }

        File target = getTarget();

        if (target.exists() && !target.equals(source) && source.lastModified() == target.lastModified())
        {
            // not update necessary
            return;
        }

        ProcessHandler handler = createProcessHandler();

        handler.parameter(getTinyCrunchScript());

        if (getStartAddress() != null)
        {
            handler.parameter("--startAddress").parameter(getStartAddress());
        }

        if (getEndAddress() != null)
        {
            handler.parameter("--endAddress").parameter(getEndAddress());
        }

        if (Boolean.TRUE.equals(getInPlace()))
        {
            handler.parameter("--inPlace");
        }

        if (Boolean.TRUE.equals(getSelfExtracting()))
        {
            handler.parameter("--selfExtracting");
        }

        if (Boolean.TRUE.equals(getRaw()))
        {
            handler.parameter("--raw");
        }

        if (getExecAccress() != null)
        {
            handler.parameter("--jmp").parameter(getExecAccress());
        }

        if (getParamFile() != null)
        {
            handler.parameter("--paramFile").parameter(getParamFile());
        }

        if (Boolean.TRUE.equals(getVerbose()))
        {
            handler.parameter("--verbose");
        }

        if (Boolean.TRUE.equals(getFast()))
        {
            handler.parameter("--fast");
        }

        handler.parameter(getSource().getPath());
        handler.parameter(getTarget().getPath());

        log("Executing: " + handler);

        int exitValue = handler.consume();

        if (exitValue != 0)
        {
            throw new BuildException("Failed with exit value " + exitValue);
        }

        if (!source.equals(target))
        {
            target.setLastModified(source.lastModified());
        }
    }
}
