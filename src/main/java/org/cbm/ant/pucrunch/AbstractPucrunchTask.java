package org.cbm.ant.pucrunch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public abstract class AbstractPucrunchTask extends Task
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

        return getProject().getBaseDir();
    }

    public void setPucrunchHome(File pucrunchHome)
    {
        this.pucrunchHome = pucrunchHome;
    }

    public File getExecutable()
    {
        if (executable != null)
        {
            File result = new File(executable);

            if (!result.isAbsolute())
            {
                result = new File(getPucrunchHome(), executable);
            }

            if (!result.exists())
            {
                throw new BuildException("Executable invalid: " + result.getAbsolutePath());
            }

            return result;
        }

        String os = System.getProperty("os.name");

        for (Map.Entry<String, String> entry : EXECUTABLES.entrySet())
        {
            if (os.matches(entry.getKey()))
            {
                File result = new File(getPucrunchHome(), entry.getValue());

                if (!result.exists())
                {
                    throw new BuildException("Executable invalid: " + result.getAbsolutePath());
                }

                return result;
            }
        }

        throw new BuildException("No executable defined for " + os);
    }

    public void setExecutable(String executable)
    {
        this.executable = executable;
    }

}
