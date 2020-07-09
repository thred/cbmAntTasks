package org.cbm.ant.pucrunch;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessConsumer;
import org.cbm.ant.util.ProcessHandler;
import org.cbm.ant.util.Util;

/**
 * Task for pucrunch by Pasi Ojala. Task was developed and tested by using the version found at
 * https://github.com/mist64/pucrunch
 *
 * @author ham
 */
public class Pucrunch extends AbstractPucrunchTask implements ProcessConsumer
{

    /** The source file */
    private File source;

    /** THe target file */
    private File target;

    /**
     * Selects the machine. Possible values are C128, C64, VIC20, C16, Plus4 or None. The default is 64, i.e. Commodore
     * 64. If you use None, a packet without the embedded decompression code is produced. This can be decompressed with
     * a standalone routine and of course with pucrunch itself. The 128-mode is not fully developed yet. Currently it
     * overwrites memory locations $f7-$f9 (Text mode lockout, Scrolling, and Bell settings) without restoring them
     * later.
     */
    private Decompressor decompressor;

    /** If set to true, pucrunch tries to avoid video matrix (only for VIC20) */
    private Boolean avoidVideoMatrix;

    /**
     * If set to true, the input file does not contain a loading address. In this case a {@link #loadAddress} has to be
     * specified.
     */
    private Boolean noLoadingAddress;

    /** Sets/overrides the load address */
    private String loadAddress;

    /**
     * Sets the execution address or overrides automatically detected execution address. Pucrunch checks whether a
     * SYS-line is present and tries to decode the address. Plain decimal addresses and addresses in parenthesis are
     * read correctly, otherwise you need to override any incorrect value with this option.
     */
    private String executionAddress;

    /** Disables 2MHz mode for C128 and 2X mode in C16/+4. */
    private Boolean disableFastMode;

    /**
     * Selects the decompressor for basic programs. This version performs the RUN function and enters the basic
     * interpreter automatically. Currently only C64 and VIC20 are supported.
     */
    private Boolean basicDecompressor;

    /**
     * Selects the faster, but longer decompressor version, if such version is available for the selected machine and
     * selected options. Without this option the medium-speed and medium-size decompressor is used.
     */
    private Boolean fastDecompressor;

    /**
     * Selects the shorter, but slower decompressor version, if such version is available for the selected machine and
     * selected options. Without this option the medium-speed and medium-size decompressor is used.
     */
    private Boolean shortDecompressor;

    /**
     * Allows delta matching. In this mode only the waveforms in the data matter, any offset is allowed and added in the
     * decompression. Note that the decompressor becomes 22 bytes longer if delta matching is used and the short
     * decompressor can't be used (24 bytes more). This means that delta matching must get more than 46 bytes of total
     * gain to get any net savings. So, always compare the result size to a version compressed without -fdelta. <br>
     * Also, the compression time increases because delta matching is more complicated. The increase is not 256-fold
     * though, somewhere around 6-7 times is more typical. So, use this option with care and do not be surprised if it
     * doesn't help on your files.
     */
    private Boolean deltaMatching;

    /**
     * Display full statistics instead of a compression summary.
     */
    private Boolean statistics;

    /**
     * Fixes the number of extra LZ77 position bits used for the low part. If pucrunch tells you to to use this option,
     * see if the new setting gives better compression.
     */
    private Integer extraPositionBits;

    /**
     * Sets the maximum length value. The value should be 5, 6, or 7. The lengths are 64, 128 and 256, respectively. If
     * pucrunch tells you to to use this option, see if the new setting gives better compression. The default value is
     * 7.
     */
    private Integer maxLength;

    /**
     * Defines the interrupt enable state to be used after decompression. Value 0 disables interrupts, other values
     * enable interrupts. The default is to enable interrupts after decompression.
     */
    private Integer interruptEnableState;

    /**
     * Defines the memory configuration to be used after decompression. Only used for C64 mode (-c64). The default value
     * is $37.
     */
    private String memoryConfiguration;

    public Pucrunch()
    {
        super();
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

    public Decompressor getDecompressor()
    {
        return decompressor;
    }

    public void setDecompressor(Decompressor decompressor)
    {
        this.decompressor = decompressor;
    }

    public Boolean getAvoidVideoMatrix()
    {
        return avoidVideoMatrix;
    }

    public void setAvoidVideoMatrix(Boolean avoidVideoMatrix)
    {
        this.avoidVideoMatrix = avoidVideoMatrix;
    }

    public Boolean getNoLoadingAddress()
    {
        return noLoadingAddress;
    }

    public void setNoLoadingAddress(Boolean noLoadingAddress)
    {
        this.noLoadingAddress = noLoadingAddress;
    }

    public String getLoadAddress()
    {
        if (Util.isEmpty(loadAddress))
        {
            return null;
        }

        try
        {
            int value = Util.parseHex(loadAddress);

            if (value < 0x0000 || value > 0xffff)
            {
                throw new BuildException("Invalid load address: " + loadAddress);
            }

            return "0x" + Integer.toHexString(value);
        }
        catch (NumberFormatException e)
        {
            throw new BuildException("Invalid load address: " + loadAddress, e);
        }
    }

    public void setLoadAddress(String loadAddress)
    {
        this.loadAddress = loadAddress;
    }

    public String getExecutionAddress()
    {
        if (Util.isEmpty(executionAddress))
        {
            return null;
        }

        try
        {
            int value = Util.parseHex(executionAddress);

            if (value < 0x0000 || value > 0xffff)
            {
                throw new BuildException("Invalid execution address: " + executionAddress);
            }

            return "0x" + Integer.toHexString(value);
        }
        catch (NumberFormatException e)
        {
            throw new BuildException("Invalid execution address: " + executionAddress, e);
        }
    }

    public void setExecutionAddress(String executionAddress)
    {
        this.executionAddress = executionAddress;
    }

    public Boolean getDisableFastMode()
    {
        return disableFastMode;
    }

    public void setDisableFastMode(Boolean disableFastMode)
    {
        this.disableFastMode = disableFastMode;
    }

    public Boolean getBasicDecompressor()
    {
        return basicDecompressor;
    }

    public void setBasicDecompressor(Boolean basicDecompressor)
    {
        this.basicDecompressor = basicDecompressor;
    }

    public Boolean getFastDecompressor()
    {
        return fastDecompressor;
    }

    public void setFastDecompressor(Boolean fastDecompressor)
    {
        this.fastDecompressor = fastDecompressor;
    }

    public Boolean getShortDecompressor()
    {
        return shortDecompressor;
    }

    public void setShortDecompressor(Boolean shortDecompressor)
    {
        this.shortDecompressor = shortDecompressor;
    }

    public Boolean getDeltaMatching()
    {
        return deltaMatching;
    }

    public void setDeltaMatching(Boolean deltaMatching)
    {
        this.deltaMatching = deltaMatching;
    }

    public Boolean getStatistics()
    {
        return statistics;
    }

    public void setStatistics(Boolean statistics)
    {
        this.statistics = statistics;
    }

    public Integer getExtraPositionBits()
    {
        return extraPositionBits;
    }

    public void setExtraPositionBits(Integer extraPositionBits)
    {
        this.extraPositionBits = extraPositionBits;
    }

    public Integer getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength)
    {
        this.maxLength = maxLength;
    }

    public Integer getInterruptEnableState()
    {
        return interruptEnableState;
    }

    public void setInterruptEnableState(Integer interruptEnableState)
    {
        this.interruptEnableState = interruptEnableState;
    }

    public String getMemoryConfiguration()
    {
        if (Util.isEmpty(memoryConfiguration))
        {
            return null;
        }

        try
        {
            int value = Util.parseHex(memoryConfiguration);

            if (value < 0x00 || value > 0xff)
            {
                throw new BuildException("Invalid memory configuration: " + memoryConfiguration);
            }

            return "0x" + Integer.toHexString(value);
        }
        catch (NumberFormatException e)
        {
            throw new BuildException("Invalid memory configuration: " + memoryConfiguration, e);
        }
    }

    public void setMemoryConfiguration(String memoryConfiguration)
    {
        this.memoryConfiguration = memoryConfiguration;
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

        File executable = getExecutable();
        ProcessHandler handler = new ProcessHandler(this, executable).directory(executable.getParentFile());

        if (getDecompressor() != null)
        {
            handler.parameter("-c" + getDecompressor().getKey());
        }

        if (Boolean.TRUE.equals(getAvoidVideoMatrix()))
        {
            handler.parameter("-a");
        }

        if (Boolean.TRUE.equals(getNoLoadingAddress()))
        {
            handler.parameter("-d");
        }

        if (getLoadAddress() != null)
        {
            handler.parameter("-l" + getLoadAddress());
        }

        if (getExecutionAddress() != null)
        {
            handler.parameter("-x" + getExecutionAddress());
        }

        if (Boolean.TRUE.equals(getDisableFastMode()))
        {
            handler.parameter("+f");
        }

        if (Boolean.TRUE.equals(getBasicDecompressor()))
        {
            handler.parameter("-fbasic");
        }

        if (Boolean.TRUE.equals(getFastDecompressor()))
        {
            handler.parameter("-ffast");
        }

        if (Boolean.TRUE.equals(getShortDecompressor()))
        {
            handler.parameter("-fshort");
        }

        if (Boolean.TRUE.equals(getDeltaMatching()))
        {
            handler.parameter("-fdelta");
        }

        if (Boolean.TRUE.equals(getStatistics()))
        {
            handler.parameter("-s");
        }

        if (getExtraPositionBits() != null)
        {
            handler.parameter("-p" + getExtraPositionBits());
        }

        if (getMaxLength() != null)
        {
            handler.parameter("-m" + getMaxLength());
        }

        if (getInterruptEnableState() != null)
        {
            handler.parameter("-i" + getInterruptEnableState());
        }

        if (getMemoryConfiguration() != null)
        {
            handler.parameter("-g" + getMemoryConfiguration());
        }

        if (getSource() != null)
        {
            handler.parameter(getSource().getPath());

            if (getTarget() != null)
            {
                handler.parameter(getTarget().getPath());
            }
        }

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

    /**
     * @see org.cbm.ant.util.ProcessConsumer#processOutput(java.lang.String, boolean)
     */
    @Override
    public void processOutput(String output, boolean isError)
    {
        log(output);
    }
}
