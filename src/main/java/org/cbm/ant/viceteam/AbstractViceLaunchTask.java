package org.cbm.ant.viceteam;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractViceLaunchTask extends AbstractViceTask
{
    private File autostart;
    private File tape;
    private File drive8;
    private File drive9;
    private File drive10;
    private File drive11;
    private Boolean drive8Readonly;
    private Boolean drive9Readonly;
    private Boolean drive10Readonly;
    private Boolean drive11Readonly;
    private Boolean verbose;
    private Boolean silent;

    private File labelFile;

    public AbstractViceLaunchTask()
    {
        super();
    }

    public File getAutostart()
    {
        return autostart;
    }

    public void setAutostart(File autostart)
    {
        this.autostart = autostart;
    }

    public File getTape()
    {
        return tape;
    }

    public void setTape(File tape)
    {
        this.tape = tape;
    }

    public File getDrive8()
    {
        return drive8;
    }

    public void setDrive8(File drive8)
    {
        this.drive8 = drive8;
    }

    public File getDrive9()
    {
        return drive9;
    }

    public void setDrive9(File drive9)
    {
        this.drive9 = drive9;
    }

    public File getDrive10()
    {
        return drive10;
    }

    public void setDrive10(File drive10)
    {
        this.drive10 = drive10;
    }

    public File getDrive11()
    {
        return drive11;
    }

    public void setDrive11(File drive11)
    {
        this.drive11 = drive11;
    }

    public Boolean getDrive8Readonly()
    {
        return drive8Readonly;
    }

    public void setDrive8Readonly(Boolean drive8Readonly)
    {
        this.drive8Readonly = drive8Readonly;
    }

    public Boolean getDrive9Readonly()
    {
        return drive9Readonly;
    }

    public void setDrive9Readonly(Boolean drive9Readonly)
    {
        this.drive9Readonly = drive9Readonly;
    }

    public Boolean getDrive10Readonly()
    {
        return drive10Readonly;
    }

    public void setDrive10Readonly(Boolean drive10Readonly)
    {
        this.drive10Readonly = drive10Readonly;
    }

    public Boolean getDrive11Readonly()
    {
        return drive11Readonly;
    }

    public void setDrive11Readonly(Boolean drive11Readonly)
    {
        this.drive11Readonly = drive11Readonly;
    }

    public Boolean getVerbose()
    {
        return verbose;
    }

    public void setVerbose(Boolean verbose)
    {
        this.verbose = verbose;
    }

    public Boolean getSilent()
    {
        return silent;
    }

    public void setSilent(Boolean silent)
    {
        this.silent = silent;
    }

    public File getLabelFile()
    {
        return labelFile;
    }

    public void setLabelFile(File labelFile)
    {
        this.labelFile = labelFile;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException
    {
        ProcessHandler handler = createProcessHandler();

        if (getTape() != null)
        {
            handler.parameter("-1").parameter(getTape());
        }

        if (getDrive8() != null)
        {
            handler.parameter("-8").parameter(getDrive8());
        }

        if (getDrive9() != null)
        {
            handler.parameter("-9").parameter(getDrive9());
        }

        if (getDrive10() != null)
        {
            handler.parameter("-10").parameter(getDrive10());
        }

        if (getDrive11() != null)
        {
            handler.parameter("-11").parameter(getDrive11());
        }

        if (getDrive8Readonly() != null)
        {
            handler.parameter("-AttachDrive8Readonly=" + (getDrive8Readonly() ? "1" : "0"));
        }

        if (getDrive9Readonly() != null)
        {
            handler.parameter("-AttachDrive9Readonly=" + (getDrive9Readonly() ? "1" : "0"));
        }

        if (getDrive10Readonly() != null)
        {
            handler.parameter("-AttachDrive10Readonly=" + (getDrive10Readonly() ? "1" : "0"));
        }

        if (getDrive11Readonly() != null)
        {
            handler.parameter("-AttachDrive11Readonly=" + (getDrive11Readonly() ? "1" : "0"));
        }

        if (getVerbose() != null)
        {
            handler.parameter("-verbose");
        }

        if (getSilent() != null)
        {
            handler.parameter("-silent");
        }

        if (getLabelFile() != null)
        {
            /* VICE does not handle that right - it does not load all the labels
            try
            {
            	File viceLaunchFile = File.createTempFile("cbmAntTasks", "viceLaunch");
            	Writer writer = new FileWriter(viceLaunchFile);

            	try
            	{
            		writer.write("ll \"");
            		writer.write(labelFile.getAbsolutePath());
            		writer.write("\"\n");
            	}
            	finally
            	{
            		writer.close();
            	}

            	handler.parameter("-moncommand").parameter(viceLaunchFile);

            	viceLaunchFile.deleteOnExit();
            }
            catch (IOException e)
            {
            	throw new BuildException("Error creating vice launch file", e);
            }
            */
            handler.parameter("-moncommand").parameter(labelFile.getAbsoluteFile());
        }

        if (getAutostart() != null)
        {
            handler.parameter("-autostart").parameter(getAutostart());
        }

        log("Executing: " + handler);

        int exitValue = handler.consume();

        if (exitValue != 0)
        {
            throw new BuildException("Failed with exit value " + exitValue);
        }
    }
}
