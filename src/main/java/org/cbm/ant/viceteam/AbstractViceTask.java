package org.cbm.ant.viceteam;

import java.io.File;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public abstract class AbstractViceTask extends Task
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

        return getProject().getBaseDir();
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
     * Returns the executable. The default depends on the OS.
     *
     * @return the executable
     * @throws BuildException on occasion
     */
    public File getExecutable() throws BuildException
    {
        if (executable != null)
        {
            File result = new File(executable);

            if (!result.isAbsolute())
            {
                result = new File(getViceHome(), executable);

                if (!result.exists())
                {
                    File binResult = new File(new File(getViceHome(), "bin"), executable);

                    if (binResult.exists())
                    {
                        result = binResult;
                    }
                }
            }

            if (!result.exists())
            {
                throw new BuildException("Executable invalid: " + result.getAbsolutePath());
            }

            return result;
        }

        String os = System.getProperty("os.name");
        Map<String, String> executables = getExecutables();

        for (Map.Entry<String, String> entry : executables.entrySet())
        {
            if (os.matches(entry.getKey()))
            {
                File result = new File(getViceHome(), entry.getValue());

                if (!result.exists())
                {
                    File binResult = new File(new File(getViceHome(), "bin"), entry.getValue());

                    if (binResult.exists())
                    {
                        result = binResult;
                    }
                }

                if (!result.exists())
                {
                    throw new BuildException("Executable invalid: " + result.getAbsolutePath());
                }

                return result;
            }
        }

        throw new BuildException("No executable defined for " + os);
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

}
