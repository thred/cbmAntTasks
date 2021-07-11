package org.cbm.ant;

import java.io.File;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.OS;
import org.cbm.ant.util.ProcessConsumer;
import org.cbm.ant.util.ProcessHandler;

public abstract class AbstractCBMProcessHandlerTask extends AbstractCBMTask implements ProcessConsumer
{

    private File home;
    private String executable;

    public AbstractCBMProcessHandlerTask()
    {
        super();
    }

    protected abstract String getHomePropertyKeyPrefix();

    protected abstract String getExecutablePropertyKey();

    protected abstract Map<OS, String> getExecutables();

    /**
     * @return the home path of the executable
     */
    public File getHome()
    {
        if (home != null)
        {
            return home;
        }

        String projectProperty = getProject().getProperty(getHomePropertyKeyPrefix() + ".home");

        if (projectProperty != null)
        {
            return new File(projectProperty);
        }

        String systemProperty = System.getProperty(getHomePropertyKeyPrefix() + ".home");

        if (systemProperty != null)
        {
            return new File(systemProperty);
        }

        String environmentSetting = System.getenv(getHomePropertyKeyPrefix().toUpperCase() + "_HOME");

        if (environmentSetting != null)
        {
            return new File(environmentSetting);
        }

        return null;
    }

    /**
     * Sets the home path of the executable, overwriting the environment setting and/or the system properties. The
     * following determination for the path is used:
     * <ul>
     * <li>Task parameter "home"</li>
     * <li>Project property "*.home"</li>
     * <li>System property "*.home"</li>
     * <li>System environment "*_HOME"</li>
     * <li>The base directory</li>
     * </ul>
     * The '*' is the name of the executable
     *
     * @param home the home path
     */
    public void setHome(File home)
    {
        this.home = home;
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

        return handler.executable(getHome(), getExecutable(OS.current()));
    }

    public String getExecutable(OS os)
    {
        if (executable != null)
        {
            return executable;
        }

        String projectProperty = getProject().getProperty(getExecutablePropertyKey() + ".bin");

        if (projectProperty != null)
        {
            return projectProperty;
        }

        String systemProperty = System.getProperty(getExecutablePropertyKey() + ".bin");

        if (systemProperty != null)
        {
            return systemProperty;
        }

        String environmentSetting = System.getenv(getExecutablePropertyKey().toUpperCase() + "_BIN");

        if (environmentSetting != null)
        {
            return environmentSetting;
        }

        Map<OS, String> executables = getExecutables();

        return executables.get(os);
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
