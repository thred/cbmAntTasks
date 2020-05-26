package org.cbm.ant.cc65;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.AbstractCBMTask;

public abstract class AbstractCC65Task extends AbstractCBMTask
{

    private File cc65Home;
    private String executable;
    private Target target;

    public abstract Map<String, String> getExecutables();

    public AbstractCC65Task()
    {
        super();
    }

    /**
     * Returns the cc65 home.
     * 
     * @return the cc65 home
     */
    public File getCC65Home()
    {
        if (cc65Home != null)
        {
            return cc65Home;
        }

        String projectProperty = getProject().getProperty("cc65Home");

        if (projectProperty != null)
        {
            return new File(projectProperty);
        }

        String systemProperty = System.getProperty("cc65Home");

        if (systemProperty != null)
        {
            return new File(systemProperty);
        }

        String environmentSetting = System.getenv("CC65_HOME");

        if (environmentSetting != null)
        {
            return new File(environmentSetting);
        }

        return getProject().getBaseDir();
    }

    /**
     * Sets the CC65 home path, overwriting the environment setting and/or the system properties. The following
     * determination for the path is used:
     * <ul>
     * <li>Task parameter "cc65Home"</li>
     * <li>Project property "cc65Home"</li>
     * <li>System property "cc65Home"</li>
     * <li>System environment "CC65_HOME"</li>
     * <li>The base directory</li>
     * </ul>
     * 
     * @param cc65Home the cc65 home
     */
    public void setCC65Home(File cc65Home)
    {
        this.cc65Home = cc65Home;
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
                result = new File(getCC65Home(), executable);
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
                File result = new File(getCC65Home(), entry.getValue());

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

    public Target getTarget()
    {
        if (target == null)
        {
            return Target.C64;
        }

        return target;
    }

    public void setTarget(String target)
    {
        this.target = Target.valueOf(target.toUpperCase(Locale.getDefault()));
    }

}
