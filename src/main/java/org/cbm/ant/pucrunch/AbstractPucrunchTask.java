package org.cbm.ant.pucrunch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.cbm.ant.util.ProcessConsumer;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractPucrunchTask extends Task implements ProcessConsumer
{

    private static final Map<String, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put("Linux.*", "pucrunch");
        EXECUTABLES.put("Windows.*", "pucrunch.exe");
    }

    private File pucrunchHome;
    private String executable;

    public File getPucrunchHome()
    {
        if (pucrunchHome != null)
        {
            return pucrunchHome;
        }

        String projectProperty = getProject().getProperty("pucrunchHome");

        if (projectProperty != null)
        {
            return new File(projectProperty);
        }

        String systemProperty = System.getProperty("pucrunchHome");

        if (systemProperty != null)
        {
            return new File(systemProperty);
        }

        String environmentSetting = System.getenv("PUCRUNCH_HOME");

        if (environmentSetting != null)
        {
            return new File(environmentSetting);
        }

        return null;
    }

    public void setPucrunchHome(File pucrunchHome)
    {
        this.pucrunchHome = pucrunchHome;
    }

    /**
     * Creates a process handler.
     *
     * @return the executable
     * @throws BuildException on occasion
     */
    public ProcessHandler createProcessHandler() throws BuildException
    {
        ProcessHandler handler = new ProcessHandler(this).directory(getProject().getBaseDir());

        if (executable != null)
        {
            return handler.executable(executable);
        }

        return handler.executable(getPucrunchHome(), EXECUTABLES);
    }

    public String getExecutable()
    {
        return executable;
    }

    public void setExecutable(String executable)
    {
        this.executable = executable;
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
