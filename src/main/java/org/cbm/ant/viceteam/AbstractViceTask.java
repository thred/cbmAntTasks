package org.cbm.ant.viceteam;

import java.io.File;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.cbm.ant.util.ProcessConsumer;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractViceTask extends Task implements ProcessConsumer
{

    private File viceHome;
    private String executable;

    public abstract Map<String, String> getExecutables();

    public AbstractViceTask()
    {
        super();
    }

    /**
     * Returns the vice home.
     *
     * @return the vice home
     */
    public File getViceHome()
    {
        if (viceHome != null)
        {
            return viceHome;
        }

        String projectProperty = getProject().getProperty("viceHome");

        if (projectProperty != null)
        {
            return new File(projectProperty);
        }

        String systemProperty = System.getProperty("viceHome");

        if (systemProperty != null)
        {
            return new File(systemProperty);
        }

        String environmentSetting = System.getenv("VICE_HOME");

        if (environmentSetting != null)
        {
            return new File(environmentSetting);
        }

        return null;
    }

    /**
     * Sets the VICE home path, overwriting the environment setting and/or the system properties. The following
     * determination for the path is used:
     * <ul>
     * <li>Task parameter "viceHome"</li>
     * <li>Project property "viceHome"</li>
     * <li>System property "viceHome"</li>
     * <li>System environment "VICE_HOME"</li>
     * <li>The base directory</li>
     * </ul>
     *
     * @param viceHome the vice home
     */
    public void setViceHome(File viceHome)
    {
        this.viceHome = viceHome;
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

        return handler.executable(getViceHome(), getExecutables());
    }

    public String getExecutable()
    {
        return executable;
    }

    /**
     * Sets the executable overwriting the default for the operating system.
     *
     * @param executable the executable
     */
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
